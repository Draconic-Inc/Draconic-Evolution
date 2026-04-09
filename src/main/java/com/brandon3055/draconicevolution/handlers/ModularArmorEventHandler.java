package com.brandon3055.draconicevolution.handlers;

import com.brandon3055.brandonscore.api.TimeKeeper;
import com.brandon3055.brandonscore.api.power.IOPStorage;
import com.brandon3055.brandonscore.capability.CapabilityOP;
import com.brandon3055.draconicevolution.DraconicEvolution;
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
import com.brandon3055.draconicevolution.items.equipment.IModularMiningTool;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.*;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Supplier;

/**
 * Created by Brandon on 13/11/2014.
 */
public class ModularArmorEventHandler {
    private static final EquipmentSlot[] ARMOR_SLOTS = new EquipmentSlot[]{EquipmentSlot.FEET, EquipmentSlot.LEGS, EquipmentSlot.CHEST, EquipmentSlot.HEAD};

    public static final ResourceLocation WALK_SPEED_ID = ResourceLocation.fromNamespaceAndPath(DraconicEvolution.MODID, "walk_speed");
    public static final ResourceLocation STEP_HEIGHT_ID = ResourceLocation.fromNamespaceAndPath(DraconicEvolution.MODID, "step_height");
    public static final ResourceLocation FLY_SPEED_ID = ResourceLocation.fromNamespaceAndPath(DraconicEvolution.MODID, "fly_speed");
    public static final ResourceLocation SUBMERGED_MINE_SPEED_ID = ResourceLocation.fromNamespaceAndPath(DraconicEvolution.MODID, "aqua_speed");

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
        NeoForge.EVENT_BUS.addListener(ModularArmorEventHandler::blockBreakEvent);

        ATTRIBUTE_HANDLER.register(WALK_SPEED_ID, () -> Attributes.MOVEMENT_SPEED, ModularArmorEventHandler::getWalkSpeedAttribute);
        ATTRIBUTE_HANDLER.register(FLY_SPEED_ID, () -> Attributes.FLYING_SPEED, ModularArmorEventHandler::getFlightSpeedAttribute);
        ATTRIBUTE_HANDLER.register(STEP_HEIGHT_ID, () -> Attributes.STEP_HEIGHT, ModularArmorEventHandler::getStepHeight);
        ATTRIBUTE_HANDLER.register(SUBMERGED_MINE_SPEED_ID, () -> Attributes.SUBMERGED_MINING_SPEED, ModularArmorEventHandler::getSubmergedMiningSpeed);
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
            return new AttributeModifier(WALK_SPEED_ID, speedModifier, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
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
            return new AttributeModifier(FLY_SPEED_ID, speedModifier / 2, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        }
        return null;
    }

    @Nullable
    private static AttributeModifier getStepHeight(LivingEntity entity, ArmorAbilities abilities) {
        ItemStack chestStack = IModularArmor.getArmor(entity);
        ModuleHost host = DECapabilities.getHost(chestStack);
        boolean hasHost = !chestStack.isEmpty() && host != null;
        boolean hasHighStep = hasHost && host.getEntitiesByType(ModuleTypes.HILL_STEP).findAny().isPresent() && !entity.isShiftKeyDown();
        AttributeInstance instance = entity.getAttribute(Attributes.STEP_HEIGHT);

        if (hasHighStep && instance != null) {
            double modifier = instance.getValue();
            //If someone else is already boosting step height then lets not make things dumb.
            if (modifier > 1 && instance.getModifier(STEP_HEIGHT_ID) == null) {
                return null;
            }
            if (instance.hasModifier(STEP_HEIGHT_ID)) {
                modifier -= instance.getModifier(STEP_HEIGHT_ID).amount();
            }
            modifier = 1.1625D - modifier;
            if (modifier > 0) {
                return new AttributeModifier(STEP_HEIGHT_ID, modifier, AttributeModifier.Operation.ADD_VALUE);
            }
        }
        return null;
    }

    @Nullable
    private static AttributeModifier getSubmergedMiningSpeed(LivingEntity entity, ArmorAbilities abilities) {
        ItemStack chestStack = IModularArmor.getArmor(entity);
        ModuleHost host = DECapabilities.getHost(chestStack);
        boolean hasHost = !chestStack.isEmpty() && host != null;
        boolean hasAquaAdapt = hasHost && host.getModuleData(ModuleTypes.AQUA_ADEPT) != null && !entity.isShiftKeyDown();

        AttributeInstance instance = entity.getAttribute(Attributes.SUBMERGED_MINING_SPEED);

        if (hasAquaAdapt && instance != null) {
            double modifier = instance.getValue();
            if (modifier >= 1) {
                return null;
            }
            if (instance.hasModifier(SUBMERGED_MINE_SPEED_ID)) {
                modifier -= instance.getModifier(SUBMERGED_MINE_SPEED_ID).amount();
            }
            modifier = 1 - modifier;
            if (modifier > 0) {
                return new AttributeModifier(SUBMERGED_MINE_SPEED_ID, modifier, AttributeModifier.Operation.ADD_VALUE);
            }
        }
        return null;
    }

    private static void breakSpeed(PlayerEvent.BreakSpeed event) {
        Player player = event.getEntity();
        if (player == null) return;

        float newDigSpeed = event.getOriginalSpeed();

        ItemStack chestStack = IModularArmor.getArmor(player);
        ModuleHost host = DECapabilities.getHost(chestStack);
        if (host == null) return;

        if (!player.onGround() && host.getModuleData(ModuleTypes.MINING_STABILITY) != null) {
            newDigSpeed *= 5f;
        }

        if (newDigSpeed != event.getOriginalSpeed()) {
            event.setNewSpeed(newDigSpeed);
        }
    }


    private static void onEntityAttacked(LivingIncomingDamageEvent event) {
        LivingEntity entity = event.getEntity();
        if (event.isCanceled() || event.getAmount() <= 0 || entity.level().isClientSide || event.getSource().is(DEDamage.KILL)) {
            return;
        }

        ItemStack chestStack = IModularArmor.getArmor(entity);
        try (ModuleHost host = DECapabilities.getHost(chestStack)) {
            if (chestStack.isEmpty() || host == null) {
                return;
            }

            //Allows /kill to completely bypass all protections
            if (event.getAmount() == Float.MAX_VALUE && event.getSource().is(DamageTypes.FELL_OUT_OF_WORLD)) {
                event.setCanceled(true);
                entity.hurt(DEDamage.killDamage(entity.level()), Float.MAX_VALUE / 100F);
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
    }

    private static void onEntityDamaged(LivingDamageEvent.Pre event) {
        LivingEntity entity = event.getEntity();
        if (event.getNewDamage() <= 0 || entity.level().isClientSide || event.getSource().is(DEDamage.KILL)) {
            return;
        }

        ItemStack chestStack = IModularArmor.getArmor(entity);
        try (ModuleHost host = DECapabilities.getHost(chestStack)) {

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

        Map<UndyingEntity, ModuleHost> undyingModules = new HashMap<>();

        if (entity instanceof Player) {
            Player player = (Player) entity;
            NonNullList<ItemStack> stacks = player.getInventory().items;
            for (int i = 0; i < stacks.size(); ++i) {
                getUndyingEntities(stacks.get(i), undyingModules, player.getInventory().selected == i ? EquipmentSlot.MAINHAND : null, false, entity.registryAccess());
            }
            for (EquipmentSlot slot : ARMOR_SLOTS) {
                getUndyingEntities(player.getInventory().armor.get(slot.getIndex()), undyingModules, slot, false, entity.registryAccess());
            }
            for (ItemStack stack : player.getInventory().offhand) {
                getUndyingEntities(stack, undyingModules, EquipmentSlot.OFFHAND, false, entity.registryAccess());
            }
            for (ItemStack stack : EquipmentManager.getAllItems(entity)) {
                getUndyingEntities(stack, undyingModules, null, true, entity.registryAccess());
            }
        } else {
            if (EquipmentManager.equipModLoaded()) {
                for (EquipmentSlot slot : EquipmentSlot.values()) {
                    getUndyingEntities(entity.getItemBySlot(slot), undyingModules, slot, true, entity.registryAccess());
                }
            }
        }

        if (undyingModules.isEmpty() || event.getSource().is(DEDamage.KILL)) {
            return;
        }

        for (Map.Entry<UndyingEntity, ModuleHost> entry : undyingModules.entrySet()) {
            if (entry.getKey().tryBlockDeath(event)) {
                entry.getValue().save();
                event.setCanceled(true);
                return;
            }
        }
    }

    private static void getUndyingEntities(ItemStack stack, Map<UndyingEntity, ModuleHost> entities, EquipmentSlot slot, boolean inEquipModSlot, HolderLookup.Provider provider) {
        ModuleHost host = DECapabilities.getHost(stack);
        if (!stack.isEmpty() && stack.getItem() instanceof IModularItem && ((IModularItem) stack.getItem()).isEquipped(stack, slot, inEquipModSlot)) {
            if (host != null) {
                host.getModuleEntities()
                        .stream()
                        .filter(e -> e instanceof UndyingEntity)
                        .map(e -> (UndyingEntity) e)
                        .forEach(undyingEntity -> entities.put(undyingEntity, host));
            }
        }
    }

    private static void livingTick(EntityTickEvent.Pre event) {
        if (!(event.getEntity() instanceof LivingEntity entity)) {
            return;
        }

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
        } else if (!entity.level().isClientSide() && entity instanceof Player) {
            ATTRIBUTE_HANDLER.updateEntity(entity, armorAbilities);
        }

        //region/*----------------- Flight ------------------*/

        if (entity instanceof Player player) {
            boolean canFly = true;
            boolean noPower = false;
            if (armorAbilities.creativeFlight && armorAbilities.flightPower != null && !player.getAbilities().instabuild && !player.isSpectator()) {
                canFly = armorAbilities.flightPower.get().getOPStored() >= EquipCfg.creativeFlightEnergy;
                noPower = !canFly;
                if (canFly && player.getAbilities().flying && !entity.level().isClientSide) {
                    armorAbilities.flightPower.get().modifyEnergyStored(-EquipCfg.creativeFlightEnergy);
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
        try (ModuleHost host = DECapabilities.getHost(chestStack)) {
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
        }
        return 0;
    }

    private static void tryTickStack(ItemStack stack, LivingEntity entity, EquipmentSlot slot, ArmorAbilities abilities, boolean equipMod) {
        if (stack.getItem() instanceof IModularItem modularItem) {
            try (ModuleHost host = DECapabilities.getHost(stack)) {
                if (host == null) {
                    return;
                }

                modularItem.handleTick(host, stack, entity, slot, equipMod);

                if ((slot != null && slot.getType() == EquipmentSlot.Type.HUMANOID_ARMOR) || equipMod) {
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

    private static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        if (player.onGround()) return;

        ItemStack chestStack = IModularArmor.getArmor(player);
        try (ModuleHost host = DECapabilities.getHost(chestStack)) {
            if (host != null) {
                FlightData flightData = host.getModuleData(ModuleTypes.FLIGHT);
                if (flightData != null && flightData.creative()) {
                    player.getAbilities().flying = true;
                    player.onUpdateAbilities();
                }
            }
        }
    }

    private static void blockBreakEvent(BlockEvent.BreakEvent event) {
        ItemStack stack = event.getPlayer().getMainHandItem();
        if (event.isCanceled() || !(stack.getItem() instanceof IModularMiningTool miningTool)) {
            return;
        }
        if (miningTool.onBlockStartBreak(stack, event.getPos(), event.getPlayer())) {
            event.setCanceled(true);
        }
    }

    private static void gatherArmorProps(ItemStack stack, ModuleHost host, LivingEntity entity, ArmorAbilities abilities) {
        SpeedData speed = host.getModuleData(ModuleTypes.SPEED);
        if (speed != null) {
            abilities.addSpeedData(speed, host);
        }

        FlightEntity flight = host.getEntitiesByType(ModuleTypes.FLIGHT).map(e -> (FlightEntity) e).findAny().orElse(null);
        if (flight != null) {
            abilities.addFlightData(flight, () -> stack.getCapability(CapabilityOP.ITEM));
        }
    }

    public static class ArmorAbilities {
        private double speedSetting = -1;
        private double speedSettingRun = -1;
        private SpeedData data;
        private boolean elytraFlight = false;
        private boolean creativeFlight = false;
        private Supplier<IOPStorage> flightPower = null;

        private void addSpeedData(SpeedData data, ModuleHost host) {
            this.data = this.data == null ? data : this.data.combine(data);
            if (host instanceof PropertyProvider) {
                if (host.hasDecimal("run_speed")) {
                    if (speedSettingRun == -1) speedSettingRun = 0;
                    speedSettingRun += host.getDecimal("run_speed").getValue();
                }
                if (host.hasDecimal("walk_speed")) {
                    if (speedSetting == -1) speedSetting = 0;
                    speedSetting += host.getDecimal("walk_speed").getValue();
                }
            }
        }

        private void addFlightData(FlightEntity entity, Supplier<IOPStorage> flightPower) {
            elytraFlight = elytraFlight || entity.getElytraEnabled();
            creativeFlight = creativeFlight || entity.getCreativeEnabled();
            if (flightPower != null && flightPower.get() != null && (this.flightPower == null || flightPower.get().getOPStored() > this.flightPower.get().getOPStored())) {
                this.flightPower = flightPower;
            }
        }
    }
}
