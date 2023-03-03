package com.brandon3055.draconicevolution.handlers;

import com.brandon3055.brandonscore.api.power.IOPStorage;
import com.brandon3055.brandonscore.api.power.IOPStorageModifiable;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.api.capability.DECapabilities;
import com.brandon3055.draconicevolution.api.capability.ModuleHost;
import com.brandon3055.draconicevolution.api.capability.PropertyProvider;
import com.brandon3055.draconicevolution.api.modules.ModuleTypes;
import com.brandon3055.draconicevolution.api.modules.data.JumpData;
import com.brandon3055.draconicevolution.api.modules.data.SpeedData;
import com.brandon3055.draconicevolution.api.modules.entities.FlightEntity;
import com.brandon3055.draconicevolution.api.modules.entities.ShieldControlEntity;
import com.brandon3055.draconicevolution.api.modules.entities.UndyingEntity;
import com.brandon3055.draconicevolution.init.EquipCfg;
import com.brandon3055.draconicevolution.integration.equipment.EquipmentManager;
import com.brandon3055.draconicevolution.items.equipment.IModularItem;
import com.brandon3055.draconicevolution.items.equipment.ModularChestpiece;
import net.minecraft.core.NonNullList;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Brandon on 13/11/2014.
 */
@Mod.EventBusSubscriber(modid = DraconicEvolution.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ModularArmorEventHandler {
    private static final EquipmentSlot[] ARMOR_SLOTS = new EquipmentSlot[]{EquipmentSlot.FEET, EquipmentSlot.LEGS, EquipmentSlot.CHEST, EquipmentSlot.HEAD};

    public static final UUID WALK_SPEED_UUID = UUID.fromString("0ea6ce8e-d2e8-11e5-ab30-625662870761");
    private static final DamageSource KILL_COMMAND = new DamageSource("administrative.kill").bypassInvul().bypassArmor().bypassMagic();
    public static Map<Player, Boolean> playersWithFlight = new WeakHashMap<>();
    public static List<UUID> playersWithUphillStep = new ArrayList<>();

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onEntityAttacked(LivingAttackEvent event) {
        if (event.isCanceled() || event.getAmount() <= 0 || event.getEntityLiving().level.isClientSide || event.getSource() == KILL_COMMAND) {
            return;
        }

        LivingEntity entity = event.getEntityLiving();
        ItemStack chestStack = ModularChestpiece.getChestpiece(entity);
        LazyOptional<ModuleHost> optionalHost = chestStack.getCapability(DECapabilities.MODULE_HOST_CAPABILITY);

        if (chestStack.isEmpty() || !optionalHost.isPresent()) {
            return;
        }

        //Allows /kill to completely bypass all protections
        if (event.getAmount() == Float.MAX_VALUE && event.getSource() == DamageSource.OUT_OF_WORLD) {
            event.setCanceled(true);
            event.getEntityLiving().hurt(KILL_COMMAND, Float.MAX_VALUE);
            return;
        }

        ModuleHost host = optionalHost.orElseThrow(IllegalStateException::new);

        if (host.getEntitiesByType(ModuleTypes.UNDYING).anyMatch(module -> ((UndyingEntity) module).tryBlockDamage(event))) {
            return;
        }

        ShieldControlEntity shieldControl = host.getEntitiesByType(ModuleTypes.SHIELD_CONTROLLER).map(e -> (ShieldControlEntity) e).findAny().orElse(null);
        if (shieldControl == null) {
            return;
        }

        shieldControl.tryBlockDamage(event);
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onEntityDamaged(LivingDamageEvent event) {
        if (event.isCanceled() || event.getAmount() <= 0 || event.getEntityLiving().level.isClientSide || event.getSource() == KILL_COMMAND) {
            return;
        }

        LivingEntity entity = event.getEntityLiving();
        ItemStack chestStack = ModularChestpiece.getChestpiece(entity);
        LazyOptional<ModuleHost> optionalHost = chestStack.getCapability(DECapabilities.MODULE_HOST_CAPABILITY);

        if (chestStack.isEmpty() || !optionalHost.isPresent()) {
            return;
        }

        ModuleHost host = optionalHost.orElseThrow(IllegalStateException::new);

        if (host.getEntitiesByType(ModuleTypes.UNDYING).anyMatch(module -> ((UndyingEntity) module).tryBlockDamage(event))) {
            return;
        }

        ShieldControlEntity shieldControl = host.getEntitiesByType(ModuleTypes.SHIELD_CONTROLLER).map(e -> (ShieldControlEntity) e).findAny().orElse(null);
        if (shieldControl == null) {
            return;
        }

        shieldControl.tryBlockDamage(event);
    }

    @SubscribeEvent
    public static void onEntityFall(LivingFallEvent event) {
        LivingEntity entity = event.getEntityLiving();
        float jumpBoost = getJumpBoost(entity, true);
        if (jumpBoost > 0) {
            jumpBoost *= 2;
            event.setDistance(Math.max(0, event.getDistance() - jumpBoost));
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onEntityDeath(LivingDeathEvent event) {
        if (event.isCanceled() || event.getEntityLiving().level.isClientSide) {
            return;
        }

        LivingEntity entity = event.getEntityLiving();
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

        if (undyingModules.isEmpty() || event.getSource() == KILL_COMMAND) {
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
        LazyOptional<ModuleHost> optional = stack.getCapability(DECapabilities.MODULE_HOST_CAPABILITY);
        if (!stack.isEmpty() && stack.getItem() instanceof IModularItem && ((IModularItem) stack.getItem()).isEquipped(stack, slot, inEquipModSlot)) {
            optional.ifPresent(host -> {
                entities.addAll(host.getModuleEntities()
                        .stream()
                        .filter(e -> e instanceof UndyingEntity)
                        .map(e -> (UndyingEntity) e)
                        .collect(Collectors.toList())
                );
            });
        }
    }

    @SubscribeEvent
    public static void livingTick(LivingEvent.LivingUpdateEvent event) {
        LivingEntity entity = event.getEntityLiving();
//        if (!(entity instanceof PlayerEntity)) return;

        ArmorAbilities armorAbilities = new ArmorAbilities();
        if (entity instanceof Player) {
            Player player = (Player) entity;
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

        //region/*---------------- HillStep -----------------*/

        if (entity.level.isClientSide) {
            ItemStack chestStack = ModularChestpiece.getChestpiece(entity);
            LazyOptional<ModuleHost> optional = chestStack.getCapability(DECapabilities.MODULE_HOST_CAPABILITY);
            boolean hasHost = !chestStack.isEmpty() && optional.isPresent();
            boolean highStepListed = playersWithUphillStep.contains(entity.getUUID()) && entity.maxUpStep >= 1f;
            boolean hasHighStep = hasHost && optional.orElseThrow(IllegalStateException::new).getEntitiesByType(ModuleTypes.HILL_STEP).findAny().isPresent() && !entity.isShiftKeyDown();

            if (hasHighStep && !highStepListed) {
                playersWithUphillStep.add(entity.getUUID());
                entity.maxUpStep = 1.0625f;
            }

            if (!hasHighStep && highStepListed) {
                playersWithUphillStep.remove(entity.getUUID());
                entity.maxUpStep = 0.6F;
            }
        }

        //endregion

        //region/*---------------- Movement Speed ----------------*/

        Attribute speedAttr = Attributes.MOVEMENT_SPEED;
        double speedModifier = 0;
        if (armorAbilities.data != null) {
            speedModifier = armorAbilities.data.speedMultiplier();
            if (entity.isSprinting() && armorAbilities.speedSettingRun != -1) {
                speedModifier = Math.min(speedModifier, armorAbilities.speedSettingRun);
            } else if (armorAbilities.speedSetting != -1) {
                speedModifier = Math.min(speedModifier, armorAbilities.speedSetting);
            }
        }

        AttributeModifier currentModifier = entity.getAttribute(speedAttr).getModifier(WALK_SPEED_UUID);
        if (speedModifier > 0) {
            if (currentModifier == null) {
                entity.getAttribute(speedAttr).addTransientModifier(new AttributeModifier(WALK_SPEED_UUID, speedAttr.getDescriptionId(), speedModifier, AttributeModifier.Operation.MULTIPLY_BASE));
            } else if (currentModifier.getAmount() != speedModifier) {
                entity.getAttribute(speedAttr).removeModifier(currentModifier);
                entity.getAttribute(speedAttr).addTransientModifier(new AttributeModifier(WALK_SPEED_UUID, speedAttr.getDescriptionId(), speedModifier, AttributeModifier.Operation.MULTIPLY_BASE));
            }

            if (!entity.isOnGround() && entity.getVehicle() == null) {
                entity.flyingSpeed = 0.02F + (0.02F * (float) speedModifier);
            }
        } else {
            if (currentModifier != null) {
                entity.getAttribute(speedAttr).removeModifier(currentModifier);
            }
        }

        //endregion

        //region/*----------------- Flight ------------------*/

        if (entity instanceof Player) {
            Player player = (Player) entity;
            boolean canFly = true;
            boolean noPower = false;
            if (armorAbilities.creativeFlight && armorAbilities.flightPower != null && !player.getAbilities().instabuild && !player.isSpectator()) {
                canFly = armorAbilities.flightPower.getOPStored() >= EquipCfg.creativeFlightEnergy;
                noPower = !canFly;
                if (canFly && player.getAbilities().flying && !entity.level.isClientSide) {
                    if (armorAbilities.flightPower instanceof IOPStorageModifiable) {
                        ((IOPStorageModifiable) armorAbilities.flightPower).modifyEnergyStored(-EquipCfg.creativeFlightEnergy);
                    } else {
                        armorAbilities.flightPower.extractOP(EquipCfg.creativeFlightEnergy, false);
                    }
                }
            }
            if (armorAbilities.creativeFlight && canFly) {
                player.getAbilities().mayfly = true;
                playersWithFlight.put(player, true);
            } else {
                if (!playersWithFlight.containsKey(player)) {
                    playersWithFlight.put(player, false);
                }

                if (playersWithFlight.get(player) && !entity.level.isClientSide) {
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

                if (player.level.isClientSide && playersWithFlight.get(player)) {
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
        ItemStack chestStack = ModularChestpiece.getChestpiece(entity);
        LazyOptional<ModuleHost> optional = chestStack.getCapability(DECapabilities.MODULE_HOST_CAPABILITY);
        if (optional.isPresent()) {
            ModuleHost host = optional.orElseThrow(IllegalStateException::new);
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
                LazyOptional<ModuleHost> optional = stack.getCapability(DECapabilities.MODULE_HOST_CAPABILITY);
                optional.ifPresent(host -> {
                    gatherArmorProps(stack, host, entity, abilities);
                });
            }
        }
    }

    @SubscribeEvent
    public static void onLivingJumpEvent(LivingEvent.LivingJumpEvent event) {
        LivingEntity entity = event.getEntityLiving();
        float jumpBoost = getJumpBoost(entity, false);
        if (jumpBoost > 0 && !entity.isShiftKeyDown()) {
            entity.push(0, 0.1F * (jumpBoost + 1), 0);
        }
    }

    public static void gatherArmorProps(ItemStack stack, ModuleHost host, LivingEntity entity, ArmorAbilities abilities) {
        SpeedData speed = host.getModuleData(ModuleTypes.SPEED);
        if (speed != null) {
            abilities.addSpeedData(speed, host);
        }

        FlightEntity flight = host.getEntitiesByType(ModuleTypes.FLIGHT).map(e -> (FlightEntity) e).findAny().orElse(null);
        if (flight != null) {
            LazyOptional<IOPStorage> optional = stack.getCapability(DECapabilities.OP_STORAGE);
            abilities.addFlightData(flight, optional.isPresent() ? optional.orElseThrow(IllegalStateException::new) : null);
        }
    }

    private static class ArmorAbilities {
        private float speedSetting = -1;
        private float speedSettingRun = -1;
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
