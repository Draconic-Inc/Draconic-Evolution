package com.brandon3055.draconicevolution.handlers;

import com.brandon3055.brandonscore.api.TimeKeeper;
import com.brandon3055.brandonscore.api.power.IOPStorage;
import com.brandon3055.brandonscore.capability.CapabilityOP;
import com.brandon3055.draconicevolution.api.capability.DECapabilities;
import com.brandon3055.draconicevolution.api.capability.ModuleHost;
import com.brandon3055.draconicevolution.api.capability.PropertyProvider;
import com.brandon3055.draconicevolution.api.modules.ModuleTypes;
import com.brandon3055.draconicevolution.api.modules.data.FlightData;
import com.brandon3055.draconicevolution.api.modules.data.JumpData;
import com.brandon3055.draconicevolution.api.modules.data.SpeedData;
import com.brandon3055.draconicevolution.api.modules.entities.FlightEntity;
import com.brandon3055.draconicevolution.api.modules.entities.ShieldControlEntity;
import com.brandon3055.draconicevolution.api.modules.entities.UndyingEntity;
import com.brandon3055.draconicevolution.init.DEDamage;
import com.brandon3055.draconicevolution.init.EquipCfg;
import com.brandon3055.draconicevolution.integration.equipment.EquipmentManager;
import com.brandon3055.draconicevolution.items.equipment.IModularArmor;
import com.brandon3055.draconicevolution.items.equipment.IModularItem;
import net.minecraft.core.NonNullList;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.event.entity.living.*;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Created by Brandon on 13/11/2014.
 */
public class ModularArmorEventHandler {
    private static final EquipmentSlot[] ARMOR_SLOTS = new EquipmentSlot[]{EquipmentSlot.FEET, EquipmentSlot.LEGS, EquipmentSlot.CHEST, EquipmentSlot.HEAD};

    public static final UUID WALK_SPEED_UUID = UUID.fromString("0ea6ce8e-d2e8-11e5-ab30-625662870761");
    public static final UUID STEP_HEIGHT_UUID = UUID.fromString("f4ccc2d7-477a-4610-bac0-b4de1a20e12f");
    public static final UUID FLY_SPEED_UUID = UUID.fromString("364320fe-5cba-48db-a28f-7f4f57422bf5");

    public static final EntityAttributeHandler<ArmorAbilities> ATTRIBUTE_HANDLER = new EntityAttributeHandler<>();

    public static Map<Player, Boolean> playersWithFlight = new WeakHashMap<>();


    public static void init() {
        NeoForge.EVENT_BUS.addListener(EventPriority.LOW, ModularArmorEventHandler::onEntityAttacked);
        NeoForge.EVENT_BUS.addListener(EventPriority.LOW, ModularArmorEventHandler::onEntityDamaged);
        NeoForge.EVENT_BUS.addListener(ModularArmorEventHandler::onEntityFall);
        NeoForge.EVENT_BUS.addListener(EventPriority.HIGHEST, ModularArmorEventHandler::onEntityDeath);
        NeoForge.EVENT_BUS.addListener(ModularArmorEventHandler::livingTick);
        NeoForge.EVENT_BUS.addListener(ModularArmorEventHandler::onLivingJumpEvent);
        NeoForge.EVENT_BUS.addListener(EventPriority.LOW, ModularArmorEventHandler::breakSpeed);
        NeoForge.EVENT_BUS.addListener(ModularArmorEventHandler::onPlayerLogin);

        ATTRIBUTE_HANDLER.register(WALK_SPEED_UUID, () -> Attributes.MOVEMENT_SPEED, ModularArmorEventHandler::getWalkSpeedAttribute);
        ATTRIBUTE_HANDLER.register(FLY_SPEED_UUID, () -> Attributes.FLYING_SPEED, ModularArmorEventHandler::getFlightSpeedAttribute);
//        ATTRIBUTE_HANDLER.register(WALK_SPEED_UUID, ForgeMod.STEP_HEIGHT, ModularArmorEventHandler::getStepHeight); //TODO 1.20.2+, 1.20.1 requires STEP_HEIGHT_ADDITION for forge support
        ATTRIBUTE_HANDLER.register(STEP_HEIGHT_UUID, NeoForgeMod.STEP_HEIGHT::value, ModularArmorEventHandler::getStepHeight);
    }

    @Nullable
    private static AttributeModifier getWalkSpeedAttribute(LivingEntity entity, ArmorAbilities abilities) {
        if (abilities.data == null) return null;

        double speedModifier = abilities.data.speedMultiplier();
        if (entity.isSprinting() && abilities.speedSettingRun != -1) {
            speedModifier = Math.min(speedModifier, abilities.speedSettingRun);
        } else if (abilities.speedSetting != -1) {
            speedModifier = Math.min(speedModifier, abilities.speedSetting);
        }
        if (speedModifier > 0) {
            return new AttributeModifier(WALK_SPEED_UUID, Attributes.MOVEMENT_SPEED.getDescriptionId(), speedModifier, AttributeModifier.Operation.MULTIPLY_BASE);
        }
        return null;
    }

    @Nullable
    private static AttributeModifier getFlightSpeedAttribute(LivingEntity entity, ArmorAbilities abilities) {
        if (abilities.data == null) return null;

        double speedModifier = abilities.data.speedMultiplier();
        if (entity.isSprinting() && abilities.speedSettingRun != -1) {
            speedModifier = Math.min(speedModifier, abilities.speedSettingRun);
        } else if (abilities.speedSetting != -1) {
            speedModifier = Math.min(speedModifier, abilities.speedSetting);
        }
        if (speedModifier > 0) {
            //TODO test this modifier
            return new AttributeModifier(FLY_SPEED_UUID, Attributes.FLYING_SPEED.getDescriptionId(), speedModifier / 2, AttributeModifier.Operation.MULTIPLY_BASE);
        }
        return null;
    }

    @Nullable
    private static AttributeModifier getStepHeight(LivingEntity entity, ArmorAbilities abilities) {
        ItemStack chestStack = IModularArmor.getArmor(entity);
        ModuleHost host = chestStack.getCapability(DECapabilities.Host.ITEM);
        boolean hasHost = !chestStack.isEmpty() && host != null;
        boolean hasHighStep = hasHost && host.getEntitiesByType(ModuleTypes.HILL_STEP).findAny().isPresent() && !entity.isShiftKeyDown();
        AttributeInstance instance = entity.getAttribute(NeoForgeMod.STEP_HEIGHT.value());

        if (hasHighStep && instance != null) {
            double stepHeight = instance.getValue();
            //If someone else is already boosting step height then lets not make things dumb.
            if (stepHeight > 1 && instance.getModifier(STEP_HEIGHT_UUID) == null) {
                return null;
            }
            return new AttributeModifier(STEP_HEIGHT_UUID, NeoForgeMod.STEP_HEIGHT.value().getDescriptionId(), 1.0625D - stepHeight, AttributeModifier.Operation.ADDITION);
        }
        return null;
    }


    private static void onEntityAttacked(LivingAttackEvent event) {
        LivingEntity entity = event.getEntity();
        if (event.isCanceled() || event.getAmount() <= 0 || entity.level().isClientSide || event.getSource().is(DEDamage.KILL)) {
            return;
        }

        ItemStack chestStack = IModularArmor.getArmor(entity);
        ModuleHost host = chestStack.getCapability(DECapabilities.Host.ITEM);
        if (chestStack.isEmpty() || host == null) {
            return;
        }

        //Allows /kill to completely bypass all protections
        if (event.getAmount() == Float.MAX_VALUE && event.getSource().is(DamageTypes.FELL_OUT_OF_WORLD)) {
            event.setCanceled(true);
            entity.hurt(DEDamage.killDamage(entity.level()), Float.MAX_VALUE / 5);
            return;
        }

        if (host.getEntitiesByType(ModuleTypes.UNDYING).anyMatch(module -> ((UndyingEntity) module).tryBlockDamage(event))) {
            return;
        }

        ShieldControlEntity shieldControl = host.getEntitiesByType(ModuleTypes.SHIELD_CONTROLLER).map(e -> (ShieldControlEntity) e).findAny().orElse(null);
        if (shieldControl == null) {
            return;
        }

        shieldControl.tryBlockDamage(event);
    }

    private static void onEntityDamaged(LivingDamageEvent event) {
        LivingEntity entity = event.getEntity();
        if (event.isCanceled() || event.getAmount() <= 0 || entity.level().isClientSide || event.getSource().is(DEDamage.KILL)) {
            return;
        }

        ItemStack chestStack = IModularArmor.getArmor(entity);
        ModuleHost host = chestStack.getCapability(DECapabilities.Host.ITEM);

        if (chestStack.isEmpty() || host == null) {
            return;
        }

        if (host.getEntitiesByType(ModuleTypes.UNDYING).anyMatch(module -> ((UndyingEntity) module).tryBlockDamage(event))) {
            return;
        }

        ShieldControlEntity shieldControl = host.getEntitiesByType(ModuleTypes.SHIELD_CONTROLLER).map(e -> (ShieldControlEntity) e).findAny().orElse(null);
        if (shieldControl == null) {
            return;
        }

        shieldControl.tryBlockDamage(event);
    }

    private static void onEntityFall(LivingFallEvent event) {
        LivingEntity entity = event.getEntity();
        float jumpBoost = getJumpBoost(entity, true);
        if (jumpBoost > 0) {
            jumpBoost *= 2;
            event.setDistance(Math.max(0, event.getDistance() - jumpBoost));
        }
    }

    private static void onEntityDeath(LivingDeathEvent event) {
        LivingEntity entity = event.getEntity();
        if (event.isCanceled() || entity.level().isClientSide) {
            return;
        }

        List<UndyingEntity> undyingModules = new ArrayList<>();

        if (entity instanceof Player) {
            Player player = (Player) entity;
            NonNullList<ItemStack> stacks = player.getInventory().items;
            for (int i = 0; i < stacks.size(); ++i) {
                getUndyingEntities(stacks.get(i), undyingModules, player.getInventory().selected == i ? EquipmentSlot.MAINHAND : null, false);
            }
            for (EquipmentSlot slot : ARMOR_SLOTS) {
                getUndyingEntities(player.getInventory().armor.get(slot.getIndex()), undyingModules, slot, false);
            }
            for (ItemStack stack : player.getInventory().offhand) {
                getUndyingEntities(stack, undyingModules, EquipmentSlot.OFFHAND, false);
            }
            for (ItemStack stack : EquipmentManager.getAllItems(entity)) {
                getUndyingEntities(stack, undyingModules, null, true);
            }
        } else {
            if (EquipmentManager.equipModLoaded()) {
                for (EquipmentSlot slot : EquipmentSlot.values()) {
                    getUndyingEntities(entity.getItemBySlot(slot), undyingModules, slot, true);
                }
            }
        }

        if (undyingModules.isEmpty() || event.getSource().is(DEDamage.KILL)) {
            return;
        }

        boolean blocked = undyingModules.stream()
                .sorted(Comparator.comparing(e -> e.getModule().getModuleTechLevel().index))
                .anyMatch(e -> e.tryBlockDeath(event));

        if (blocked) {
            event.setCanceled(true);
        }
    }

    private static void getUndyingEntities(ItemStack stack, List<UndyingEntity> entities, EquipmentSlot slot, boolean inEquipModSlot) {
        ModuleHost host = stack.getCapability(DECapabilities.Host.ITEM);
        if (!stack.isEmpty() && stack.getItem() instanceof IModularItem && ((IModularItem) stack.getItem()).isEquipped(stack, slot, inEquipModSlot)) {
            if (host != null) {
                entities.addAll(host.getModuleEntities()
                        .stream()
                        .filter(e -> e instanceof UndyingEntity)
                        .map(e -> (UndyingEntity) e)
                        .toList()
                );
            }
        }
    }

    private static void livingTick(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();

        ArmorAbilities armorAbilities = new ArmorAbilities();
        if (entity instanceof Player player) {
            NonNullList<ItemStack> stacks = player.getInventory().items;
            for (int i = 0; i < stacks.size(); ++i) {
                tryTickStack(stacks.get(i), player, player.getInventory().selected == i ? EquipmentSlot.MAINHAND : null, armorAbilities, false);
            }
            for (EquipmentSlot slot : ARMOR_SLOTS) {
                tryTickStack(player.getInventory().armor.get(slot.getIndex()), player, slot, armorAbilities, false);
            }
            for (ItemStack stack : player.getInventory().offhand) {
                tryTickStack(stack, player, EquipmentSlot.OFFHAND, armorAbilities, false);
            }
            if (EquipmentManager.equipModLoaded()) {
                EquipmentManager.findItems(e -> e.getItem() instanceof IModularItem, entity).forEach(stack -> {
                    tryTickStack(stack, player, null, armorAbilities, true);
                });
            }
        } else {
            for (EquipmentSlot slot : EquipmentSlot.values()) {
                tryTickStack(entity.getItemBySlot(slot), entity, slot, armorAbilities, false);
            }
        }

        if (!entity.level().isClientSide() && TimeKeeper.getServerTick() % 10 == 0) {
            ATTRIBUTE_HANDLER.updateEntity(entity, armorAbilities);
        }

        //region/*----------------- Flight ------------------*/

        if (entity instanceof Player player) {
            boolean canFly = true;
            boolean noPower = false;
            if (armorAbilities.creativeFlight && armorAbilities.flightPower != null && !player.getAbilities().instabuild && !player.isSpectator()) {
                canFly = armorAbilities.flightPower.getOPStored() >= EquipCfg.creativeFlightEnergy;
                noPower = !canFly;
                if (canFly && player.getAbilities().flying && !entity.level().isClientSide) {
                    armorAbilities.flightPower.modifyEnergyStored(-EquipCfg.creativeFlightEnergy);
                }
            }
            if (armorAbilities.creativeFlight && canFly) {
                player.getAbilities().mayfly = true;
                playersWithFlight.put(player, true);
            } else {
                if (!playersWithFlight.containsKey(player)) {
                    playersWithFlight.put(player, false);
                }

                if (playersWithFlight.get(player) && !entity.level().isClientSide) {
                    playersWithFlight.put(player, false);

                    if (!player.getAbilities().instabuild && !player.isSpectator()) {
                        boolean wasFlying = player.getAbilities().flying;
                        player.getAbilities().mayfly = false;
                        player.getAbilities().flying = false;
                        player.onUpdateAbilities();
                        if (wasFlying && noPower) {
                            player.tryToStartFallFlying();
                        }
                    }
                }

                if (player.level().isClientSide && playersWithFlight.get(player)) {
                    playersWithFlight.put(player, false);
                    if (!player.getAbilities().instabuild) {
                        player.getAbilities().mayfly = false;
                        player.getAbilities().flying = false;
                    }
                }
            }

            // Elytra Flight (PR Testing)
            // If i end up using something like this i think the best option would be to clear the attribute in livingTick then
            // optionally re apply the attribute and my acceleration code in the module entity.
//            if (player instanceof ServerPlayerEntity) {
//                int flightEnchantmentLevel = armorAbilities.elytraFlight ? 1 : 0;
//
//                ModifiableAttributeInstance fallFlyingAttribute = entity.getAttribute(ForgeMod.FALL_FLIGHT.get());
//                if (fallFlyingAttribute != null) {
//                    if (fallFlyingAttribute.getModifier(FlightEntity.FLIGHT_UUID) != null) {
//                        fallFlyingAttribute.removeModifier(FlightEntity.FLIGHT_UUID);
//                    }
//                    if (flightEnchantmentLevel > 0) {
//                        fallFlyingAttribute.addTransientModifier(FlightEntity.FLIGHT_MODIFIER);
//                    }
//                }
//
//                int flightSpeedEnchantmentLevel = 1;//EnchantmentHelper.getEnchantmentLevel(FLIGHT_SPEED_ENCHANTMENT.get(), entity);
//                ModifiableAttributeInstance fallFlyingSpeedAttribute = entity.getAttribute(ForgeMod.FALL_FLYING_SPEED.get());
//                if (fallFlyingSpeedAttribute != null) {
//                    if (fallFlyingSpeedAttribute.getModifier(FlightEntity.FLIGHT_SPEED_UUID) != null) {
//                        fallFlyingSpeedAttribute.removeModifier(FlightEntity.FLIGHT_SPEED_UUID);
//                    }
//                    if (flightSpeedEnchantmentLevel > 0) {
////                        fallFlyingSpeedAttribute.addTransientModifier(new AttributeModifier(FlightEntity.FLIGHT_SPEED_UUID, "Flight Speed Enchantment", 0.5, AttributeModifier.Operation.ADDITION));
//                    }
//                }
//            } else {
//                double flightSpeed = 1; //flightModule == null ? 0 : flightModule.getElytraBoost();
//                if (flightSpeed > 0 && player.isFallFlying() && entity.isSprinting()) {
//                    double speed = 1.5D * flightSpeed;
//                    double accel = 0.01 * flightSpeed;
//                    Vector3d look = entity.getLookAngle();
//                    Vector3d motion = entity.getDeltaMovement();
//                    entity.setDeltaMovement(motion.add(
//                            look.x * accel + (look.x * speed - motion.x) * accel,
//                            look.y * accel + (look.y * speed - motion.y) * accel,
//                            look.z * accel + (look.z * speed - motion.z) * accel
//                    ));
//                }
//            }

        }
        //endregion
    }

    private static float getJumpBoost(LivingEntity entity, boolean max) {
        ItemStack chestStack = IModularArmor.getArmor(entity);
        ModuleHost host = chestStack.getCapability(DECapabilities.Host.ITEM);
        if (host != null) {
            JumpData jumpData = host.getModuleData(ModuleTypes.JUMP_BOOST);
            if (jumpData != null) {
                double jump = jumpData.multiplier();
                if (max) return (float) jump;
                if (entity.isSprinting()) {
                    if (host instanceof PropertyProvider && ((PropertyProvider) host).hasDecimal("jump_boost_run")) {
                        jump = Math.min(jump, ((PropertyProvider) host).getDecimal("jump_boost_run").getValue());
                    }
                } else {
                    if (host instanceof PropertyProvider && ((PropertyProvider) host).hasDecimal("jump_boost")) {
                        jump = Math.min(jump, ((PropertyProvider) host).getDecimal("jump_boost").getValue());
                    }
                }
                return (float) jump;
            }
        }
        return 0;
    }

    private static void tryTickStack(ItemStack stack, LivingEntity entity, EquipmentSlot slot, ArmorAbilities abilities, boolean equipMod) {
        if (stack.getItem() instanceof IModularItem) {
            ((IModularItem) stack.getItem()).handleTick(stack, entity, slot, equipMod);

            if ((slot != null && slot.getType() == EquipmentSlot.Type.ARMOR) || equipMod) {
                ModuleHost host = stack.getCapability(DECapabilities.Host.ITEM);
                if (host != null){
                    gatherArmorProps(stack, host, entity, abilities);
                }
            }
        }
    }

    private static void onLivingJumpEvent(LivingEvent.LivingJumpEvent event) {
        LivingEntity entity = event.getEntity();
        float jumpBoost = getJumpBoost(entity, false);
        if (jumpBoost > 0 && !entity.isShiftKeyDown()) {
            entity.push(0, 0.1F * (jumpBoost + 1), 0);
        }
    }

    private static void breakSpeed(PlayerEvent.BreakSpeed event) {
        Player player = event.getEntity();
        if (player == null) return;

        float newDigSpeed = event.getOriginalSpeed();

        ItemStack chestStack = IModularArmor.getArmor(player);
        ModuleHost host = chestStack.getCapability(DECapabilities.Host.ITEM);
        if (host == null) return;

        if (host.getModuleData(ModuleTypes.AQUA_ADAPT) != null) {
            if (player.isEyeInFluid(FluidTags.WATER) && !EnchantmentHelper.hasAquaAffinity(player)) {
                newDigSpeed *= 5f;
            }
        }

        if (!player.onGround() && host.getModuleData(ModuleTypes.MINING_STABILITY) != null) {
            newDigSpeed *= 5f;
        }

        if (newDigSpeed != event.getOriginalSpeed()) {
            event.setNewSpeed(newDigSpeed);
        }
    }

    private static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        if (player.onGround()) return;

        ItemStack chestStack = IModularArmor.getArmor(player);
        ModuleHost host = chestStack.getCapability(DECapabilities.Host.ITEM);
        if (host != null) {
            FlightData flightData = host.getModuleData(ModuleTypes.FLIGHT);
            if (flightData != null && flightData.creative()) {
                player.getAbilities().flying = true;
                player.onUpdateAbilities();
            }
        }
    }

    private static void gatherArmorProps(ItemStack stack, ModuleHost host, LivingEntity entity, ArmorAbilities abilities) {
        SpeedData speed = host.getModuleData(ModuleTypes.SPEED);
        if (speed != null) {
            abilities.addSpeedData(speed, host);
        }

        FlightEntity flight = host.getEntitiesByType(ModuleTypes.FLIGHT).map(e -> (FlightEntity) e).findAny().orElse(null);
        if (flight != null) {
            abilities.addFlightData(flight, stack.getCapability(CapabilityOP.ITEM));
        }
    }

    public static class ArmorAbilities {
        private double speedSetting = -1;
        private double speedSettingRun = -1;
        private SpeedData data;
        private boolean elytraFlight = false;
        private boolean creativeFlight = false;
        private IOPStorage flightPower = null;

        private void addSpeedData(SpeedData data, ModuleHost host) {
            this.data = this.data == null ? data : this.data.combine(data);
            if (host instanceof PropertyProvider) {
                if (((PropertyProvider) host).hasDecimal("run_speed")) {
                    if (speedSettingRun == -1) speedSettingRun = 0;
                    speedSettingRun += ((PropertyProvider) host).getDecimal("run_speed").getValue();
                }
                if (((PropertyProvider) host).hasDecimal("walk_speed")) {
                    if (speedSetting == -1) speedSetting = 0;
                    speedSetting += ((PropertyProvider) host).getDecimal("walk_speed").getValue();
                }
            }
        }

        private void addFlightData(FlightEntity entity, IOPStorage flightPower) {
            elytraFlight = elytraFlight || entity.getElytraEnabled();
            creativeFlight = creativeFlight || entity.getCreativeEnabled();
            if (flightPower != null && (this.flightPower == null || flightPower.getOPStored() > this.flightPower.getOPStored())) {
                this.flightPower = flightPower;
            }
        }
    }
}
