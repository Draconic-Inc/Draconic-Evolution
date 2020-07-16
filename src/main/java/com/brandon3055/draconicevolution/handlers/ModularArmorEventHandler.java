package com.brandon3055.draconicevolution.handlers;

import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.api.capability.DECapabilities;
import com.brandon3055.draconicevolution.api.capability.ModuleHost;
import com.brandon3055.draconicevolution.api.modules.ModuleTypes;
import com.brandon3055.draconicevolution.api.modules.entities.ShieldControlEntity;
import com.brandon3055.draconicevolution.items.equipment.IModularItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.*;

/**
 * Created by Brandon on 13/11/2014.
 */
@Mod.EventBusSubscriber(modid = DraconicEvolution.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ModularArmorEventHandler {
    private static final EquipmentSlotType[] ARMOR_SLOTS = new EquipmentSlotType[] {EquipmentSlotType.FEET, EquipmentSlotType.LEGS, EquipmentSlotType.CHEST, EquipmentSlotType.HEAD};

    public static final UUID WALK_SPEED_UUID = UUID.fromString("0ea6ce8e-d2e8-11e5-ab30-625662870761");
    private static final DamageSource ADMIN_KILL = new DamageSource("administrative.kill").setDamageAllowedInCreativeMode().setDamageBypassesArmor().setDamageIsAbsolute();
    public static Map<PlayerEntity, Boolean> playersWithFlight = new WeakHashMap<>();
    public static List<UUID> playersWithUphillStep = new ArrayList<>();

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onPlayerAttacked(LivingAttackEvent event) {
        if (event.isCanceled() || event.getAmount() <= 0 || event.getEntityLiving().world.isRemote || event.getSource() == ADMIN_KILL) {
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
    public static void onPlayerDamaged(LivingDamageEvent event) {
        if (event.isCanceled() || event.getAmount() <= 0 || event.getEntityLiving().world.isRemote || event.getSource() == ADMIN_KILL) {
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

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (event.isCanceled() || event.getEntityLiving().world.isRemote) {
            return;
        }

        LivingEntity entity = event.getEntityLiving();

//        ArmorSummery summery = new ArmorSummery().getSummery(player);
//
//        if (summery == null || event.getSource() == ADMIN_KILL) {
//            return;
//        }
//
//        if (summery.protectionPoints > 500) {
//            event.setCanceled(true);
//            event.getEntityLiving().setHealth(10);
//            return;
//        }
//
//        if (!summery.hasDraconic) {
//            return;
//        }
//
//        int[] charge = new int[summery.armorStacks.size()];
//        long totalCharge = 0;
//        for (int i = 0; i < summery.armorStacks.size(); i++) {
//            if (!summery.armorStacks.get(i).isEmpty()) {
//                charge[i] = ((ICustomArmor) summery.armorStacks.get(i).getItem()).getEnergyStored(summery.armorStacks.get(i));
//                totalCharge += charge[i];
//            }
//        }
//
//        if (totalCharge < ToolStats.LAST_STAND_ENERGY) {
//            return;
//        }
//
//        for (int i = 0; i < summery.armorStacks.size(); i++) {
//            if (!summery.armorStacks.get(i).isEmpty()) {
//                ((ICustomArmor) summery.armorStacks.get(i).getItem()).modifyEnergy(summery.armorStacks.get(i), -(int) ((charge[i] / (double) totalCharge) * 10000000L));
//            }
//        }
//
//        summery.saveStacks(player);
//
//        player.sendMessage(new TranslationTextComponent("msg.de.shieldDepleted.txt").setStyle(new Style().setColor(TextFormatting.DARK_RED)));
//        event.setCanceled(true);
//        player.setHealth(1);
    }

    @SubscribeEvent
    public static void livingTick(LivingEvent.LivingUpdateEvent event) {
        LivingEntity entity = event.getEntityLiving();
        if (entity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) entity;
            NonNullList<ItemStack> stacks = player.inventory.mainInventory;
            for (int i = 0; i < stacks.size(); ++i) {
                tryTickStack(stacks.get(i), player, player.inventory.currentItem == i ? EquipmentSlotType.MAINHAND : null);
            }
            for (EquipmentSlotType slot : ARMOR_SLOTS) {
                tryTickStack(player.inventory.armorInventory.get(slot.getIndex()), player, slot);
            }
            for (ItemStack stack : player.inventory.offHandInventory) {
                tryTickStack(stack, player, EquipmentSlotType.OFFHAND);
            }
//        tickArmorEffects(summery, player);
        }else {
            for (EquipmentSlotType slot : EquipmentSlotType.values()) {
                tryTickStack(entity.getItemStackFromSlot(slot), entity, slot);
            }
        }
    }

    private static void tryTickStack(ItemStack stack, LivingEntity entity, EquipmentSlotType slot) {
        if (!stack.isEmpty() && stack.getItem() instanceof IModularItem) {
            ((IModularItem) stack.getItem()).handleTick(stack, entity, slot);
        }
    }

    @SubscribeEvent
    public static void onLivingJumpEvent(LivingEvent.LivingJumpEvent event) {
//        if (!(event.getEntity() instanceof PlayerEntity)) {
//            return;
//        }
//
//        PlayerEntity player = (PlayerEntity) event.getEntity();
//        ModularArmorEventHandler.ArmorSummery summery = new ModularArmorEventHandler.ArmorSummery().getSummery(player);
//
//        if (summery != null && summery.jumpModifier > 0) {
//            player.addVelocity(0, summery.jumpModifier * 0.135F, 0);
//        }
    }

//    public static void tickShield(ArmorSummery summery, PlayerEntity player) {
//        if (summery == null || (summery.maxProtectionPoints - summery.protectionPoints < 0.01 && summery.entropy == 0) || player.world.isRemote) {
//            return;
//        }
//
//        float totalPointsToAdd = Math.min(summery.maxProtectionPoints - summery.protectionPoints, summery.maxProtectionPoints / 60F);
//        totalPointsToAdd *= (1F - (summery.entropy / 100F));
//        totalPointsToAdd = Math.min(totalPointsToAdd, summery.totalEnergyStored / 1000F);
//
//        if (totalPointsToAdd < 0F) {
//            totalPointsToAdd = 0F;
//        }
//
//        summery.entropy -= (summery.meanRecoveryPoints / 100F);
//        if (summery.entropy < 0) {
//            summery.entropy = 0;
//        }
//
//        for (int i = 0; i < summery.armorStacks.size(); i++) {
//            ItemStack stack = summery.armorStacks.get(i);
//
//            if (stack.isEmpty() || summery.totalEnergyStored <= 0) {
//                continue;
//            }
//
//            float maxForPiece = ((ICustomArmor) stack.getItem()).getProtectionPoints(stack);
//            int energyAmount = ((ICustomArmor) summery.armorStacks.get(i).getItem()).getEnergyPerProtectionPoint();
//            ((ICustomArmor) stack.getItem()).modifyEnergy(stack, -(int) (((double) summery.energyAllocation[i] / (double) summery.totalEnergyStored) * (totalPointsToAdd * energyAmount)));
//            float pointsForPiece = (summery.pointsDown[i] / Math.max(1, summery.maxProtectionPoints - summery.protectionPoints)) * totalPointsToAdd;
//            summery.allocation[i] += pointsForPiece;
//
//            if (summery.allocation[i] > maxForPiece || maxForPiece - summery.allocation[i] < 0.1F) {
//                summery.allocation[i] = maxForPiece;
//            }
//
//            ItemNBTHelper.setFloat(stack, "ProtectionPoints", summery.allocation[i]);
//
//            if (player.hurtResistantTime <= 0) {//TODO Increase this delay (Store the delay in forge entity nbt)
//                ItemNBTHelper.setFloat(stack, "ShieldEntropy", summery.entropy);
//            }
//        }
//
//        summery.saveStacks(player);
//    }

//    @SuppressWarnings("ConstantConditions")
//    public static void tickArmorEffects(ArmorSummery summery, PlayerEntity player) {
//
//        //region/*----------------- Flight ------------------*/
//        if (DEOldConfig.enableFlight) {
//            if (summery != null && summery.flight[0]) {
//                playersWithFlight.put(player, true);
//                player.abilities.allowFlying = true;
//                if (summery.flight[1]) player.abilities.isFlying = true;
//
//                if (player.world.isRemote) {
//                    setPlayerFlySpeed(player, 0.05F + (0.05F * summery.flightSpeedModifier * (float) ToolStats.FLIGHT_SPEED_MODIFIER));
//                }
//
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
//
//            } else {
//                if (!playersWithFlight.containsKey(player)) {
//                    playersWithFlight.put(player, false);
//                }
//
//                if (playersWithFlight.get(player) && !player.world.isRemote) {
//                    playersWithFlight.put(player, false);
//
//                    if (!player.abilities.isCreativeMode) {
//                        player.abilities.allowFlying = false;
//                        player.abilities.isFlying = false;
//                        player.sendPlayerAbilities();
//                    }
//                }
//
//                if (player.world.isRemote && playersWithFlight.get(player)) {
//                    playersWithFlight.put(player, false);
//                    if (!player.abilities.isCreativeMode) {
//                        player.abilities.allowFlying = false;
//                        player.abilities.isFlying = false;
//                    }
//                    setPlayerFlySpeed(player, 0.05F);
//                }
//            }
//        }
//        //endregion
//
//        //region/*---------------- Swiftness ----------------*/
//
//        IAttribute speedAttr = SharedMonsterAttributes.MOVEMENT_SPEED;
//        if (summery != null && summery.speedModifier > 0) {
//            double value = summery.speedModifier;
//            if (player.getAttribute(speedAttr).getModifier(WALK_SPEED_UUID) == null) {
//                player.getAttribute(speedAttr).applyModifier(new AttributeModifier(WALK_SPEED_UUID, speedAttr.getName(), value, AttributeModifier.Operation.MULTIPLY_BASE));
//            } else if (player.getAttribute(speedAttr).getModifier(WALK_SPEED_UUID).getAmount() != value) {
//                player.getAttribute(speedAttr).removeModifier(player.getAttribute(speedAttr).getModifier(WALK_SPEED_UUID));
//                player.getAttribute(speedAttr).applyModifier(new AttributeModifier(WALK_SPEED_UUID, speedAttr.getName(), value, AttributeModifier.Operation.MULTIPLY_BASE));
//            }
//
//            if (!player.onGround && player.getRidingEntity() == null) {
//                player.jumpMovementFactor = 0.02F + (0.02F * summery.speedModifier);
//            }
//        } else if (player.getAttribute(speedAttr).getModifier(WALK_SPEED_UUID) != null) {
//            player.getAttribute(speedAttr).removeModifier(player.getAttribute(speedAttr).getModifier(WALK_SPEED_UUID));
//        }
//
//        //endregion
//
//        //region/*---------------- HillStep -----------------*/
//        if (summery != null && player.world.isRemote) {
//            boolean highStepListed = playersWithUphillStep.contains(player.getUniqueID()) && player.stepHeight >= 1f;
//            boolean hasHighStep = summery.hasHillStep;
//
//            if (hasHighStep && !highStepListed) {
//                playersWithUphillStep.add(player.getUniqueID());
//                player.stepHeight = 1.0625f;
//            }
//
//            if (!hasHighStep && highStepListed) {
//                playersWithUphillStep.remove(player.getUniqueID());
//                player.stepHeight = 0.6F;
//            }
//        }
//        //endregion
//    }

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
//        if (event.getSource().damageType.equals("fall") && summery.jumpModifier > 0F) {
//            if (event.getAmount() < summery.jumpModifier * 5F) {
//                event.setCanceled(true);
//                return true;
//            }
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
