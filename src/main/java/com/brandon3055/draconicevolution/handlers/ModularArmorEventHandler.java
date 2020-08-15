package com.brandon3055.draconicevolution.handlers;

import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.api.capability.DECapabilities;
import com.brandon3055.draconicevolution.api.capability.ModuleHost;
import com.brandon3055.draconicevolution.api.capability.PropertyProvider;
import com.brandon3055.draconicevolution.api.modules.ModuleTypes;
import com.brandon3055.draconicevolution.api.modules.data.JumpData;
import com.brandon3055.draconicevolution.api.modules.data.SpeedData;
import com.brandon3055.draconicevolution.api.modules.entities.FlightEntity;
import com.brandon3055.draconicevolution.api.modules.entities.LastStandEntity;
import com.brandon3055.draconicevolution.api.modules.entities.ShieldControlEntity;
import com.brandon3055.draconicevolution.items.equipment.IModularItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.thread.EffectiveSide;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Brandon on 13/11/2014.
 */
@Mod.EventBusSubscriber(modid = DraconicEvolution.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ModularArmorEventHandler {
    private static final EquipmentSlotType[] ARMOR_SLOTS = new EquipmentSlotType[]{EquipmentSlotType.FEET, EquipmentSlotType.LEGS, EquipmentSlotType.CHEST, EquipmentSlotType.HEAD};

    public static final UUID WALK_SPEED_UUID = UUID.fromString("0ea6ce8e-d2e8-11e5-ab30-625662870761");
    private static final DamageSource KILL_COMMAND = new DamageSource("administrative.kill").setDamageAllowedInCreativeMode().setDamageBypassesArmor().setDamageIsAbsolute();
    public static Map<PlayerEntity, Boolean> playersWithFlight = new WeakHashMap<>();
    public static List<UUID> playersWithUphillStep = new ArrayList<>();

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onEntityAttacked(LivingAttackEvent event) {
        if (event.isCanceled() || event.getAmount() <= 0 || event.getEntityLiving().world.isRemote || event.getSource() == KILL_COMMAND) {
            return;
        }

        //Allows /kill to completely bypass all protections
        if (event.getAmount() == Float.MAX_VALUE && event.getSource() == DamageSource.OUT_OF_WORLD) {
            event.setCanceled(true);
            event.getEntityLiving().attackEntityFrom(KILL_COMMAND, Float.MAX_VALUE);
            return;
        }

        LivingEntity entity = event.getEntityLiving();
        ItemStack chestStack = entity.getItemStackFromSlot(EquipmentSlotType.CHEST);
        LazyOptional<ModuleHost> optionalHost = chestStack.getCapability(DECapabilities.MODULE_HOST_CAPABILITY);

        if (chestStack.isEmpty() || !optionalHost.isPresent()) {
            return;
        }

        ModuleHost host = optionalHost.orElseThrow(IllegalStateException::new);
        if (applyArmorDamageBlocking(event, entity)) {
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
        if (event.isCanceled() || event.getAmount() <= 0 || event.getEntityLiving().world.isRemote || event.getSource() == KILL_COMMAND) {
            return;
        }

        LivingEntity entity = event.getEntityLiving();
        ItemStack chestStack = entity.getItemStackFromSlot(EquipmentSlotType.CHEST);
        LazyOptional<ModuleHost> optionalHost = chestStack.getCapability(DECapabilities.MODULE_HOST_CAPABILITY);

        if (chestStack.isEmpty() || !optionalHost.isPresent()) {
            return;
        }

        ModuleHost host = optionalHost.orElseThrow(IllegalStateException::new);
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
        if (event.isCanceled() || event.getEntityLiving().world.isRemote) {
            return;
        }

        LivingEntity entity = event.getEntityLiving();
        List<LastStandEntity> lastStandModules = new ArrayList<>();

        if (entity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) entity;
            NonNullList<ItemStack> stacks = player.inventory.mainInventory;
            for (int i = 0; i < stacks.size(); ++i) {
                getLastStandEntities(stacks.get(i), lastStandModules, player.inventory.currentItem == i ? EquipmentSlotType.MAINHAND : null);
            }
            for (EquipmentSlotType slot : ARMOR_SLOTS) {
                getLastStandEntities(player.inventory.armorInventory.get(slot.getIndex()), lastStandModules, slot);
            }
            for (ItemStack stack : player.inventory.offHandInventory) {
                getLastStandEntities(stack, lastStandModules, EquipmentSlotType.OFFHAND);
            }
        } else {
            for (EquipmentSlotType slot : EquipmentSlotType.values()) {
                getLastStandEntities(entity.getItemStackFromSlot(slot), lastStandModules, slot);
            }
        }

        if (lastStandModules.isEmpty() || event.getSource() == KILL_COMMAND) {
            return;
        }

        boolean blocked = lastStandModules.stream()
                .sorted(Comparator.comparing(e -> e.getModule().getModuleTechLevel().index))
                .anyMatch(e -> e.tryBlockDeath(event));

        if (blocked){
            event.setCanceled(true);
        }
    }

    private static void getLastStandEntities(ItemStack stack, List<LastStandEntity> entities, EquipmentSlotType slot) {
        LazyOptional<ModuleHost> optional = stack.getCapability(DECapabilities.MODULE_HOST_CAPABILITY);
        if (!stack.isEmpty() && stack.getItem() instanceof IModularItem && ((IModularItem) stack.getItem()).isEquipped(stack, slot, false)) {
            optional.ifPresent(host -> {
                entities.addAll(host.getModuleEntities()
                        .stream()
                        .filter(e -> e instanceof LastStandEntity)
                        .map(e -> (LastStandEntity) e)
                        .collect(Collectors.toList())
                );
            });
        }
    }

    @SubscribeEvent
    public static void livingTick(LivingEvent.LivingUpdateEvent event) {
        LivingEntity entity = event.getEntityLiving();
        ArmorAbilities armorAbilities = new ArmorAbilities();
        if (entity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) entity;
            NonNullList<ItemStack> stacks = player.inventory.mainInventory;
            for (int i = 0; i < stacks.size(); ++i) {
                tryTickStack(stacks.get(i), player, player.inventory.currentItem == i ? EquipmentSlotType.MAINHAND : null, armorAbilities);
            }
            for (EquipmentSlotType slot : ARMOR_SLOTS) {
                tryTickStack(player.inventory.armorInventory.get(slot.getIndex()), player, slot, armorAbilities);
            }
            for (ItemStack stack : player.inventory.offHandInventory) {
                tryTickStack(stack, player, EquipmentSlotType.OFFHAND, armorAbilities);
            }
        } else {
            for (EquipmentSlotType slot : EquipmentSlotType.values()) {
                tryTickStack(entity.getItemStackFromSlot(slot), entity, slot, armorAbilities);
            }
        }

        //region/*---------------- HillStep -----------------*/

        if (entity.world.isRemote) {
            ItemStack chestStack = entity.getItemStackFromSlot(EquipmentSlotType.CHEST);
            LazyOptional<ModuleHost> optional = chestStack.getCapability(DECapabilities.MODULE_HOST_CAPABILITY);
            boolean hasHost = !chestStack.isEmpty() && optional.isPresent();
            boolean highStepListed = playersWithUphillStep.contains(entity.getUniqueID()) && entity.stepHeight >= 1f;
            boolean hasHighStep = hasHost && optional.orElseThrow(IllegalStateException::new).getEntitiesByType(ModuleTypes.HILL_STEP).findAny().isPresent();

            if (hasHighStep && !highStepListed) {
                playersWithUphillStep.add(entity.getUniqueID());
                entity.stepHeight = 1.0625f;
            }

            if (!hasHighStep && highStepListed) {
                playersWithUphillStep.remove(entity.getUniqueID());
                entity.stepHeight = 0.6F;
            }
        }

        //endregion

        //region/*---------------- Movement Speed ----------------*/

        IAttribute speedAttr = SharedMonsterAttributes.MOVEMENT_SPEED;
        double speedModifier = 0;
        if (armorAbilities.data != null) {
            speedModifier = armorAbilities.data.getSpeedMultiplier();
            if (entity.isSprinting() && armorAbilities.speedSettingRun != -1) {
                speedModifier = Math.min(speedModifier, armorAbilities.speedSettingRun);
            } else if (armorAbilities.speedSetting != -1) {
                speedModifier = Math.min(speedModifier, armorAbilities.speedSetting);
            }
        }

        AttributeModifier currentModifier = entity.getAttribute(speedAttr).getModifier(WALK_SPEED_UUID);
        if (speedModifier > 0) {
            if (currentModifier == null) {
                entity.getAttribute(speedAttr).applyModifier(new AttributeModifier(WALK_SPEED_UUID, speedAttr.getName(), speedModifier, AttributeModifier.Operation.MULTIPLY_BASE));
            } else if (currentModifier.getAmount() != speedModifier) {
                entity.getAttribute(speedAttr).removeModifier(currentModifier);
                entity.getAttribute(speedAttr).applyModifier(new AttributeModifier(WALK_SPEED_UUID, speedAttr.getName(), speedModifier, AttributeModifier.Operation.MULTIPLY_BASE));
            }

            if (!entity.onGround && entity.getRidingEntity() == null) {
                entity.jumpMovementFactor = 0.02F + (0.02F * (float) speedModifier);
            }
        } else {
            if (currentModifier != null) {
                entity.getAttribute(speedAttr).removeModifier(currentModifier);
            }
        }

        //endregion

        //region/*----------------- Flight ------------------*/


        if (entity instanceof PlayerEntity && !entity.world.isRemote) {
            PlayerEntity player = (PlayerEntity) entity;
            if (armorAbilities.creativeFlight) {
                if (!player.abilities.allowFlying) {
                    player.abilities.allowFlying = true;
                    player.sendPlayerAbilities();
                }
                playersWithFlight.put(player, true);
                //TODO do i want to do creative flight speed boost? I mean i kinda like the idea of creative as "precise" flight and elytra a "fast" flight.
//                if (summery.flight[1]) player.abilities.isFlying = true;


//                if (player.world.isRemote) {
//                    setPlayerFlySpeed(player, 0.05F + (0.05F * summery.flightSpeedModifier * (float) ToolStats.FLIGHT_SPEED_MODIFIER));
//                }

//                Vec3d motion = player.getMotion();
//                if ((!player.onGround && player.abilities.isFlying) && player.getMotion().y != 0 && summery.flightVModifier > 0) {
////				float percentIncrease = summery.flightVModifier;
//
//                    if (BrandonsCore.proxy.isJumpKeyDown() && !BrandonsCore.proxy.isSneakKeyDown()) {
//                        //LogHelper.info(player.motionY);
//                        player.setVelocity(motion.x, 0.225F * summery.flightVModifier, motion.z);
//                    }
//
//                    if (BrandonsCore.proxy.isSneakKeyDown() && !BrandonsCore.proxy.isJumpKeyDown()) {
//                        player.setVelocity(motion.x, -0.225F * summery.flightVModifier, motion.z);
//                    }
//                }
//
//                if (summery.flight[2] && player.moveForward == 0 && player.moveStrafing == 0 && player.abilities.isFlying) {
//                    player.setVelocity(motion.x * 0.5, motion.y, motion.z * 0.5);
//                }

            } else {
                if (!playersWithFlight.containsKey(player)) {
                    playersWithFlight.put(player, false);
                }

                if (playersWithFlight.get(player) && !player.world.isRemote) {
                    playersWithFlight.put(player, false);

                    if (!player.abilities.isCreativeMode) {
                        player.abilities.allowFlying = false;
                        player.abilities.isFlying = false;
                        player.sendPlayerAbilities();
                    }
                }

//                if (player.world.isRemote && playersWithFlight.get(player)) {
//                    if (!player.abilities.isCreativeMode) {
////                        player.abilities.allowFlying = false;
////                        player.abilities.isFlying = false;
//                    }
////                    setPlayerFlySpeed(player, 0.05F);
//                }
            }
        }
        //endregion
    }

    private static float getJumpBoost(LivingEntity entity, boolean max) {
        ItemStack stack = entity.getItemStackFromSlot(EquipmentSlotType.CHEST);
        LazyOptional<ModuleHost> optional = stack.getCapability(DECapabilities.MODULE_HOST_CAPABILITY);
        if (optional.isPresent()) {
            ModuleHost host = optional.orElseThrow(IllegalStateException::new);
            JumpData jumpData = host.getModuleData(ModuleTypes.JUMP_BOOST);
            if (jumpData != null) {
                double jump = jumpData.getMultiplier();
                if (max) return (float) jump;
                if (entity.isSprinting()) {
                    if (host instanceof PropertyProvider && ((PropertyProvider) host).hasDecimal("de.module.jump_boost_run.prop")) {
                        jump = Math.min(jump, ((PropertyProvider) host).getDecimal("de.module.jump_boost_run.prop").getValue());
                    }
                } else {
                    if (host instanceof PropertyProvider && ((PropertyProvider) host).hasDecimal("de.module.jump_boost.prop")) {
                        jump = Math.min(jump, ((PropertyProvider) host).getDecimal("de.module.jump_boost.prop").getValue());
                    }
                }
                return (float) jump;
            }
        }
        return 0;
    }

    private static void tryTickStack(ItemStack stack, LivingEntity entity, EquipmentSlotType slot, ArmorAbilities abilities) {
        if (!stack.isEmpty() && stack.getItem() instanceof IModularItem) {
            ((IModularItem) stack.getItem()).handleTick(stack, entity, slot);

            if (slot != null && slot.getSlotType() == EquipmentSlotType.Group.ARMOR) {
                LazyOptional<ModuleHost> optional = stack.getCapability(DECapabilities.MODULE_HOST_CAPABILITY);
                optional.ifPresent(host -> {
                    gatherArmorProps(host, entity, abilities);
                });
            }
        }
    }

    @SubscribeEvent
    public static void onLivingJumpEvent(LivingEvent.LivingJumpEvent event) {
        LivingEntity entity = event.getEntityLiving();
        float jumpBoost = getJumpBoost(entity, false);
        if (jumpBoost > 0) {
            entity.addVelocity(0, 0.1F * (jumpBoost + 1), 0);
        }
    }

    //    @SuppressWarnings("ConstantConditions")
    public static void gatherArmorProps(ModuleHost host, LivingEntity entity, ArmorAbilities abilities) {

//        //region/*----------------- Flight ------------------*/
//        if (DEOldConfig.enableFlight) {
//            if (summery != null && summery.flight[0]) {
//                playersWithFlight.put(entity, true);
//                entity.abilities.allowFlying = true;
//                if (summery.flight[1]) entity.abilities.isFlying = true;
//
//                if (entity.world.isRemote) {
//                    setPlayerFlySpeed(entity, 0.05F + (0.05F * summery.flightSpeedModifier * (float) ToolStats.FLIGHT_SPEED_MODIFIER));
//                }
//
//                Vec3d motion = entity.getMotion();
//                if ((!entity.onGround && entity.abilities.isFlying) && entity.getMotion().y != 0 && summery.flightVModifier > 0) {
////				float percentIncrease = summery.flightVModifier;
//
//                    if (BrandonsCore.proxy.isJumpKeyDown() && !BrandonsCore.proxy.isSneakKeyDown()) {
//                        //LogHelper.info(entity.motionY);
//                        entity.setVelocity(motion.x, 0.225F * summery.flightVModifier, motion.z);
//                    }
//
//                    if (BrandonsCore.proxy.isSneakKeyDown() && !BrandonsCore.proxy.isJumpKeyDown()) {
//                        entity.setVelocity(motion.x, -0.225F * summery.flightVModifier, motion.z);
//                    }
//                }
//
//                if (summery.flight[2] && entity.moveForward == 0 && entity.moveStrafing == 0 && entity.abilities.isFlying) {
//                    entity.setVelocity(motion.x * 0.5, motion.y, motion.z * 0.5);
//                }
//
//            } else {
//                if (!playersWithFlight.containsKey(entity)) {
//                    playersWithFlight.put(entity, false);
//                }
//
//                if (playersWithFlight.get(entity) && !entity.world.isRemote) {
//                    playersWithFlight.put(entity, false);
//
//                    if (!entity.abilities.isCreativeMode) {
//                        entity.abilities.allowFlying = false;
//                        entity.abilities.isFlying = false;
//                        entity.sendPlayerAbilities();
//                    }
//                }
//
//                if (entity.world.isRemote && playersWithFlight.get(entity)) {
//                    playersWithFlight.put(entity, false);
//                    if (!entity.abilities.isCreativeMode) {
//                        entity.abilities.allowFlying = false;
//                        entity.abilities.isFlying = false;
//                    }
//                    setPlayerFlySpeed(entity, 0.05F);
//                }
//            }
//        }
//        //endregion


        SpeedData speed = host.getModuleData(ModuleTypes.SPEED);
        if (speed != null) {
            abilities.addSpeedData(speed, host);
        }

        FlightEntity flight = host.getEntitiesByType(ModuleTypes.FLIGHT).map(e -> (FlightEntity) e).findAny().orElse(null);
        if (flight != null) {
            abilities.addFlightData(flight);
        }
    }

    private static void setPlayerFlySpeed(PlayerEntity player, float speed) {
        player.abilities.setFlySpeed(speed);
    }

    /**
     * @return true if the damage was blocked
     */
    private static boolean applyArmorDamageBlocking(LivingAttackEvent event, LivingEntity entity) {
//        if (event.getSource().isFireDamage() && summery.fireResistance >= 1F) {
//            event.setCanceled(true);
//            event.getEntityLiving().extinguish();
//            return true;
//        }
//
//        if ((event.getSource().damageType.equals("inWall") || event.getSource().damageType.equals("drown")) && !summery.armorStacks.get(3).isEmpty()) {
//            if (event.getAmount() <= 2f) {
//                event.setCanceled(true);
//            }
//            return true;
//        }

        return false;
    }

    private static class ArmorAbilities {
        private float speedSetting = -1;
        private float speedSettingRun = -1;
        private SpeedData data;
        private boolean elytraFlight = false;
        private boolean creativeFlight = false;

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

        private void addFlightData(FlightEntity entity) {
            elytraFlight = elytraFlight || entity.getElytraEnabled();
            creativeFlight = creativeFlight || entity.getCreativeEnabled();
        }
    }

//    //TODO (1.13) Overhaul the entire custom armor system.
//    public static class ArmorSummery {
//        /*---- Shield ----*/
//        /**
//         * Max protection points from all equipped armor pieces
//         */
//        public float maxProtectionPoints = 0F;
//        /**
//         * Total protection points from all equipped armor pieces
//         */
//        public float protectionPoints = 0F;
//        /**
//         * Number of quipped armor pieces
//         */
//        public int pieces = 0;
//        /**
//         * Point  Allocation, The number of points on each piece
//         */
//        public float[] allocation;
//        /**
//         * How many points have been drained from each armor piece
//         */
//        public float[] pointsDown;
//        /**
//         * The armor pieces (Index will contain EMPTY if piece is not present)
//         */
//        public NonNullList<ItemStack> armorStacks;
//
//        public NonNullList<ItemStack> baublesStacks = null;
//        /**
//         * Mean Fatigue
//         */
//        public float entropy = 0F;
//        /**
//         * Mean Recovery Points
//         */
//        public float meanRecoveryPoints = 0;
//        /**
//         * Total RF stored in the armor
//         */
//        public long totalEnergyStored = 0;
//        /**
//         * Total Max RF storage for the armor
//         */
//        public long maxTotalEnergyStorage = 0;
//        /**
//         * RF stored in each armor piece
//         */
//        public int[] energyAllocation;
//        /*---- Effects ----*/
//        public boolean[] flight = new boolean[]{false, false, false};
//        public float flightVModifier = 0F;
//        public float speedModifier = 0F;
//        public float jumpModifier = 0F;
//        public float fireResistance = 0F;
//        public float flightSpeedModifier = 0;
//        public boolean hasHillStep = false;
//        public boolean hasDraconic = false;
//
//        public ArmorSummery getSummery(PlayerEntity player) {
//            List<ItemStack> armorStacks = new ArrayList<>(player.inventory.armorInventory);
//            float totalEntropy = 0;
//            float totalRecoveryPoints = 0;
//
//            if (ModHelper.isBaublesInstalled) {
//                getBaubles(player, armorStacks);
//            }
//
//            allocation = new float[armorStacks.size()];
//            this.armorStacks = NonNullList.withSize(armorStacks.size(), ItemStack.EMPTY);
//            pointsDown = new float[armorStacks.size()];
//            energyAllocation = new int[armorStacks.size()];
//
//            for (int i = 0; i < armorStacks.size(); i++) {
//                ItemStack stack = armorStacks.get(i);
//                if (stack.isEmpty() || !(stack.getItem() instanceof ICustomArmor)) continue;
//                ICustomArmor armor = (ICustomArmor) stack.getItem();
//                pieces++;
//                allocation[i] = ItemNBTHelper.getFloat(stack, "ProtectionPoints", 0);
//                protectionPoints += allocation[i];
//                totalEntropy += ItemNBTHelper.getFloat(stack, "ShieldEntropy", 0);
//                this.armorStacks.set(i, stack);
//                totalRecoveryPoints += armor.getRecoveryRate(stack);//UpgradeHelper.getUpgradeLevel(stack, ToolUpgrade.SHIELD_RECOVERY);
//                float maxPoints = armor.getProtectionPoints(stack);
//                pointsDown[i] = maxPoints - allocation[i];
//                maxProtectionPoints += maxPoints;
//                energyAllocation[i] = armor.getEnergyStored(stack);
//                totalEnergyStored += energyAllocation[i];
//                maxTotalEnergyStorage += armor.getMaxEnergyStored(stack);
//                if (stack.getItem() instanceof DraconicArmor) hasDraconic = true;
//
//                fireResistance += armor.getFireResistance(stack);
//
//                //TODO in 1.13 the armor pieces should have more control over what effects they can supply. That would make this switch unnecessary.
//                switch (i) {
//                    case 3: //Head
//                        break;
//                    case 2: //Chest
//                        boolean[] fa = armor.hasFlight(stack);
//                        flight[0] = flight[0] || fa[0];
//                        flight[1] = flight[1] || fa[1];
//                        flight[2] = flight[2] || fa[2];
//
//                        if (flight[0]) {
//                            flightVModifier = Math.max(flightVModifier, armor.getFlightVModifier(stack, player));
//                            flightSpeedModifier = Math.max(flightVModifier, armor.getFlightSpeedModifier(stack, player));
//                        }
//                        break;
//                    case 1: //Legs
//                        speedModifier = Math.max(speedModifier, armor.getSpeedModifier(stack, player));
//                        break;
//                    case 0: //Feet
//                        hasHillStep = hasHillStep || armor.hasHillStep(stack, player);
//                        jumpModifier = Math.max(jumpModifier, armor.getJumpModifier(stack, player));
//                        break;
//                    default: //Baubles
//                        fa = armor.hasFlight(stack);
//                        flight[0] = flight[0] || fa[0];
//                        flight[1] = flight[1] || fa[1];
//                        flight[2] = flight[2] || fa[2];
//
//                        if (flight[0]) {
//                            flightVModifier = Math.max(flightVModifier, armor.getFlightVModifier(stack, player));
//                            flightSpeedModifier = Math.max(flightSpeedModifier, armor.getFlightSpeedModifier(stack, player));
//                        }
//                        speedModifier = Math.max(speedModifier, armor.getSpeedModifier(stack, player));
//                        hasHillStep = hasHillStep || armor.hasHillStep(stack, player);
//                        jumpModifier = Math.max(jumpModifier, armor.getJumpModifier(stack, player));
//                        break;
//                }
//            }
//
//            if (pieces == 0) {
//                return null;
//            }
//
//            entropy = totalEntropy / pieces;
//            meanRecoveryPoints = totalRecoveryPoints / pieces;
//
//            return this;
//        }
//
//        private void getBaubles(PlayerEntity player, List<ItemStack> stacks) {
////            IBaublesItemHandler baubles = BaublesApi.getBaublesHandler(player);
////            baublesStacks = NonNullList.withSize(baubles.getSlots(), ItemStack.EMPTY);
////            for (int i = 0; i < baubles.getSlots(); i++) {
////                //Not allowed to directly modify a stack returned by IItemHandler.getStackInSlot so we copy the stack and replace it with the new stack later.
////                baublesStacks.set(i, baubles.getStackInSlot(i).copy());
////            }
////            stacks.addAll(baublesStacks);
//        }
//
//        public void saveStacks(PlayerEntity player) {
//            if (ModHelper.isBaublesInstalled) {
//                saveBaubles(player);
//            }
//        }
//
//        private void saveBaubles(PlayerEntity player) {
////            if (baublesStacks != null) {
////                IBaublesItemHandler baubles = BaublesApi.getBaublesHandler(player);
////                for (int i = 0; i < baubles.getSlots(); i++) {
////                    baubles.setStackInSlot(i, baublesStacks.get(i));
////                }
////            }
//        }
//    }
}
