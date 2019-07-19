package com.brandon3055.draconicevolution.handlers;

import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
import com.brandon3055.brandonscore.BrandonsCore;
import com.brandon3055.brandonscore.utils.ItemNBTHelper;
import com.brandon3055.draconicevolution.DEConfig;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.integration.ModHelper;
import com.brandon3055.draconicevolution.items.armor.DraconicArmor;
import com.brandon3055.draconicevolution.items.armor.ICustomArmor;
import com.brandon3055.draconicevolution.items.tools.ToolStats;
import com.brandon3055.draconicevolution.network.PacketShieldHit;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import java.util.*;

/**
 * Created by Brandon on 13/11/2014.
 */
public class CustomArmorHandler {
    public static final UUID WALK_SPEED_UUID = UUID.fromString("0ea6ce8e-d2e8-11e5-ab30-625662870761");
    private static final DamageSource ADMIN_KILL = new DamageSource("administrative.kill").setDamageAllowedInCreativeMode().setDamageBypassesArmor().setDamageIsAbsolute();
    public static Map<EntityPlayer, Boolean> playersWithFlight = new WeakHashMap<EntityPlayer, Boolean>();
    public static List<String> playersWithUphillStep = new ArrayList<String>();  //TODO Switch to UUID

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onPlayerAttacked(LivingAttackEvent event) {
        if (!(event.getEntityLiving() instanceof EntityPlayer) || event.isCanceled() || event.getAmount() <= 0) {
            return;
        }

        EntityPlayer player = (EntityPlayer) event.getEntityLiving();
        if (player.world.isRemote) {
            return;
        }

        ArmorSummery summery = new ArmorSummery().getSummery(player);

        float hitAmount = ModHelper.applyModDamageAdjustments(summery, event);

        if (applyArmorDamageBlocking(event, summery)) {
            return;
        }
        if (summery == null || summery.protectionPoints <= 0 || event.getSource() == ADMIN_KILL) {
            return;
        }
        event.setCanceled(true);
        //Ensure that the /kill command can still kill the player
        if (hitAmount == Float.MAX_VALUE && !event.getSource().damageType.equals(ADMIN_KILL.damageType)) {
            player.attackEntityFrom(ADMIN_KILL, Float.MAX_VALUE);
            return;
        }
        if ((float) player.hurtResistantTime > (float) player.maxHurtResistantTime - 4) return;

        float newEntropy = Math.min(summery.entropy + 1 + (hitAmount / 20), 100F);

        //Divide the damage between the armor pieces based on how many of the protection points each piece has
        float totalAbsorbed = 0;
        int remainingPoints = 0;
        for (int i = 0; i < summery.allocation.length; i++) {
            if (summery.allocation[i] == 0) continue;
            ItemStack armorPiece = summery.armorStacks.get(i);

            float dmgShear = summery.allocation[i] / summery.protectionPoints;
            float dmg = dmgShear * hitAmount;

            float absorbed = Math.min(dmg, summery.allocation[i]);
            totalAbsorbed += absorbed;
            summery.allocation[i] -= absorbed;
            remainingPoints += summery.allocation[i];
            ItemNBTHelper.setFloat(armorPiece, "ProtectionPoints", summery.allocation[i]);
            ItemNBTHelper.setFloat(armorPiece, "ShieldEntropy", newEntropy);
        }

        summery.saveStacks(player);

        DraconicEvolution.network.sendToAllAround(new PacketShieldHit(player, remainingPoints / summery.maxProtectionPoints), new NetworkRegistry.TargetPoint(player.dimension, player.posX, player.posY, player.posZ, 64));

        if (remainingPoints > 0) {
            player.hurtResistantTime = 20;
        }
        else if (hitAmount - totalAbsorbed > 0) {
            player.attackEntityFrom(event.getSource(), hitAmount - totalAbsorbed);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlayerDeath(LivingDeathEvent event) {
        if (!(event.getEntityLiving() instanceof EntityPlayer) || event.isCanceled()) {
            return;
        }

        EntityPlayer player = (EntityPlayer) event.getEntityLiving();
        if (!player.isServerWorld()) {
            return;
        }

        ArmorSummery summery = new ArmorSummery().getSummery(player);

        if (summery == null || event.getSource() == ADMIN_KILL) {
            return;
        }

        if (summery.protectionPoints > 500) {
            event.setCanceled(true);
            event.getEntityLiving().setHealth(10);
            return;
        }

        if (!summery.hasDraconic) {
            return;
        }

        int[] charge = new int[summery.armorStacks.size()];
        long totalCharge = 0;
        for (int i = 0; i < summery.armorStacks.size(); i++) {
            if (!summery.armorStacks.get(i).isEmpty()) {
                charge[i] = ((ICustomArmor) summery.armorStacks.get(i).getItem()).getEnergyStored(summery.armorStacks.get(i));
                totalCharge += charge[i];
            }
        }

        if (totalCharge < ToolStats.LAST_STAND_ENERGY) {
            return;
        }

        for (int i = 0; i < summery.armorStacks.size(); i++) {
            if (!summery.armorStacks.get(i).isEmpty()) {
                ((ICustomArmor) summery.armorStacks.get(i).getItem()).modifyEnergy(summery.armorStacks.get(i), -(int) ((charge[i] / (double) totalCharge) * 10000000L));
            }
        }

        summery.saveStacks(player);

        player.sendMessage(new TextComponentTranslation("msg.de.shieldDepleted.txt").setStyle(new Style().setColor(TextFormatting.DARK_RED)));
        event.setCanceled(true);
        player.setHealth(1);
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;

        EntityPlayer player = event.player;
        ArmorSummery summery = new ArmorSummery().getSummery(player);

        tickShield(summery, player);
        tickArmorEffects(summery, player);
    }

    @SubscribeEvent
    public void onLivingJumpEvent(LivingEvent.LivingJumpEvent event) {
        if (!(event.getEntity() instanceof EntityPlayer)) {
            return;
        }

        EntityPlayer player = (EntityPlayer) event.getEntity();
        CustomArmorHandler.ArmorSummery summery = new CustomArmorHandler.ArmorSummery().getSummery(player);

        if (summery != null && summery.jumpModifier > 0) {
            player.motionY += (double) (summery.jumpModifier * 0.135F);
        }
    }

    public static void tickShield(ArmorSummery summery, EntityPlayer player) {
        if (summery == null || (summery.maxProtectionPoints - summery.protectionPoints < 0.01 && summery.entropy == 0) || player.world.isRemote) {
            return;
        }

        float totalPointsToAdd = Math.min(summery.maxProtectionPoints - summery.protectionPoints, summery.maxProtectionPoints / 60F);
        totalPointsToAdd *= (1F - (summery.entropy / 100F));
        totalPointsToAdd = Math.min(totalPointsToAdd, summery.totalEnergyStored / 1000);

        if (totalPointsToAdd < 0F) {
            totalPointsToAdd = 0F;
        }

        summery.entropy -= (summery.meanRecoveryPoints / 100F);
        if (summery.entropy < 0) {
            summery.entropy = 0;
        }

        for (int i = 0; i < summery.armorStacks.size(); i++) {
            ItemStack stack = summery.armorStacks.get(i);

            if (stack.isEmpty() || summery.totalEnergyStored <= 0) {
                continue;
            }

            float maxForPiece = ((ICustomArmor) stack.getItem()).getProtectionPoints(stack);
            int energyAmount = ((ICustomArmor) summery.armorStacks.get(i).getItem()).getEnergyPerProtectionPoint();
            ((ICustomArmor) stack.getItem()).modifyEnergy(stack, -(int) (((double) summery.energyAllocation[i] / (double) summery.totalEnergyStored) * (totalPointsToAdd * energyAmount)));
            float pointsForPiece = (summery.pointsDown[i] / Math.max(1, summery.maxProtectionPoints - summery.protectionPoints)) * totalPointsToAdd;
            summery.allocation[i] += pointsForPiece;

            if (summery.allocation[i] > maxForPiece || maxForPiece - summery.allocation[i] < 0.1F) {
                summery.allocation[i] = maxForPiece;
            }

            ItemNBTHelper.setFloat(stack, "ProtectionPoints", summery.allocation[i]);

            if (player.hurtResistantTime <= 0) {//TODO Increase this delay (Store the delay in forge entity nbt)
                ItemNBTHelper.setFloat(stack, "ShieldEntropy", summery.entropy);
            }
        }

        summery.saveStacks(player);
    }

    @SuppressWarnings("ConstantConditions")
    public static void tickArmorEffects(ArmorSummery summery, EntityPlayer player) {

        //region/*----------------- Flight ------------------*/
        if (DEConfig.enableFlight) {
            if (summery != null && summery.flight[0]) {
                playersWithFlight.put(player, true);
                player.capabilities.allowFlying = true;
                if (summery.flight[1]) player.capabilities.isFlying = true;

                if (player.world.isRemote) {
                    setPlayerFlySpeed(player, 0.05F + (0.05F * summery.flightSpeedModifier * (float) ToolStats.FLIGHT_SPEED_MODIFIER));
                }

                if ((!player.onGround && player.capabilities.isFlying) && player.motionY != 0 && summery.flightVModifier > 0) {
//				float percentIncrease = summery.flightVModifier;

                    if (BrandonsCore.proxy.isJumpKeyDown() && !BrandonsCore.proxy.isSneakKeyDown()) {
                        //LogHelper.info(player.motionY);
                        player.motionY = 0.225F * summery.flightVModifier;
                    }

                    if (BrandonsCore.proxy.isSneakKeyDown() && !BrandonsCore.proxy.isJumpKeyDown()) {
                        player.motionY = -0.225F * summery.flightVModifier;
                    }
                }

                if (summery.flight[2] && player.moveForward == 0 && player.moveStrafing == 0 && player.capabilities.isFlying) {
                    player.motionX *= 0.5;
                    player.motionZ *= 0.5;
                }

            }
            else {
                if (!playersWithFlight.containsKey(player)) {
                    playersWithFlight.put(player, false);
                }

                if (playersWithFlight.get(player) && !player.world.isRemote) {
                    playersWithFlight.put(player, false);

                    if (!player.capabilities.isCreativeMode) {
                        player.capabilities.allowFlying = false;
                        player.capabilities.isFlying = false;
                        player.sendPlayerAbilities();
                    }
                }

                if (player.world.isRemote && playersWithFlight.get(player)) {
                    playersWithFlight.put(player, false);
                    if (!player.capabilities.isCreativeMode) {
                        player.capabilities.allowFlying = false;
                        player.capabilities.isFlying = false;
                    }
                    setPlayerFlySpeed(player, 0.05F);
                }
            }
        }
        //endregion

        //region/*---------------- Swiftness ----------------*/

        IAttribute speedAttr = SharedMonsterAttributes.MOVEMENT_SPEED;
        if (summery != null && summery.speedModifier > 0) {
            double value = summery.speedModifier;
            if (player.getEntityAttribute(speedAttr).getModifier(WALK_SPEED_UUID) == null) {
                player.getEntityAttribute(speedAttr).applyModifier(new AttributeModifier(WALK_SPEED_UUID, speedAttr.getName(), value, 1));
            }
            else if (player.getEntityAttribute(speedAttr).getModifier(WALK_SPEED_UUID).getAmount() != value) {
                player.getEntityAttribute(speedAttr).removeModifier(player.getEntityAttribute(speedAttr).getModifier(WALK_SPEED_UUID));
                player.getEntityAttribute(speedAttr).applyModifier(new AttributeModifier(WALK_SPEED_UUID, speedAttr.getName(), value, 1));
            }

            if (!player.onGround && player.getRidingEntity() == null) {
                player.jumpMovementFactor = 0.02F + (0.02F * summery.speedModifier);
            }
        }
        else if (player.getEntityAttribute(speedAttr).getModifier(WALK_SPEED_UUID) != null) {
            player.getEntityAttribute(speedAttr).removeModifier(player.getEntityAttribute(speedAttr).getModifier(WALK_SPEED_UUID));
        }

        //endregion

        //region/*---------------- HillStep -----------------*/
        if (summery != null && player.world.isRemote) {
            boolean highStepListed = playersWithUphillStep.contains(player.getDisplayNameString()) && player.stepHeight >= 1f;
            boolean hasHighStep = summery.hasHillStep;

            if (hasHighStep && !highStepListed) {
                playersWithUphillStep.add(player.getDisplayNameString());
                player.stepHeight = 1.0625f;
            }

            if (!hasHighStep && highStepListed) {
                playersWithUphillStep.remove(player.getDisplayNameString());
                player.stepHeight = 0.6F;
            }
        }
        //endregion
    }

    private static void setPlayerFlySpeed(EntityPlayer player, float speed) {
        player.capabilities.setFlySpeed(speed);
    }

    /**
     * @return true if the damage was blocked
     */
    private static boolean applyArmorDamageBlocking(LivingAttackEvent event, ArmorSummery summery) {
        if (summery == null) return false;

        if (event.getSource().isFireDamage() && summery.fireResistance >= 1F) {
            event.setCanceled(true);
            event.getEntityLiving().extinguish();
            return true;
        }

        if (event.getSource().damageType.equals("fall") && summery.jumpModifier > 0F) {
            if (event.getAmount() < summery.jumpModifier * 5F) {
                event.setCanceled(true);
                return true;
            }
        }

        if ((event.getSource().damageType.equals("inWall") || event.getSource().damageType.equals("drown")) && !summery.armorStacks.get(3).isEmpty()) {
            if (event.getAmount() <= 2f) {
                event.setCanceled(true);
            }
            return true;
        }

        return false;
    }

    //TODO (1.13) Overhaul the entire custom armor system.
    public static class ArmorSummery {
        /*---- Shield ----*/
        /**
         * Max protection points from all equipped armor pieces
         */
        public float maxProtectionPoints = 0F;
        /**
         * Total protection points from all equipped armor pieces
         */
        public float protectionPoints = 0F;
        /**
         * Number of quipped armor pieces
         */
        public int pieces = 0;
        /**
         * Point  Allocation, The number of points on each piece
         */
        public float[] allocation;
        /**
         * How many points have been drained from each armor piece
         */
        public float[] pointsDown;
        /**
         * The armor pieces (Index will contain EMPTY if piece is not present)
         */
        public NonNullList<ItemStack> armorStacks;

        public NonNullList<ItemStack> baublesStacks = null;
        /**
         * Mean Fatigue
         */
        public float entropy = 0F;
        /**
         * Mean Recovery Points
         */
        public float meanRecoveryPoints = 0;
        /**
         * Total RF stored in the armor
         */
        public long totalEnergyStored = 0;
        /**
         * Total Max RF storage for the armor
         */
        public long maxTotalEnergyStorage = 0;
        /**
         * RF stored in each armor piece
         */
        public int[] energyAllocation;
        /*---- Effects ----*/
        public boolean[] flight = new boolean[]{false, false, false};
        public float flightVModifier = 0F;
        public float speedModifier = 0F;
        public float jumpModifier = 0F;
        public float fireResistance = 0F;
        public float flightSpeedModifier = 0;
        public boolean hasHillStep = false;
        public boolean hasDraconic = false;

        public ArmorSummery getSummery(EntityPlayer player) {
            List<ItemStack> armorStacks = new ArrayList<>(player.inventory.armorInventory);
            float totalEntropy = 0;
            float totalRecoveryPoints = 0;

            if (ModHelper.isBaublesInstalled) {
                getBaubles(player, armorStacks);
            }

            allocation = new float[armorStacks.size()];
            this.armorStacks = NonNullList.withSize(armorStacks.size(), ItemStack.EMPTY);
            pointsDown = new float[armorStacks.size()];
            energyAllocation = new int[armorStacks.size()];

            for (int i = 0; i < armorStacks.size(); i++) {
                ItemStack stack = armorStacks.get(i);
                if (stack.isEmpty() || !(stack.getItem() instanceof ICustomArmor)) continue;
                ICustomArmor armor = (ICustomArmor) stack.getItem();
                pieces++;
                allocation[i] = ItemNBTHelper.getFloat(stack, "ProtectionPoints", 0);
                protectionPoints += allocation[i];
                totalEntropy += ItemNBTHelper.getFloat(stack, "ShieldEntropy", 0);
                this.armorStacks.set(i, stack);
                totalRecoveryPoints += armor.getRecoveryRate(stack);//UpgradeHelper.getUpgradeLevel(stack, ToolUpgrade.SHIELD_RECOVERY);
                float maxPoints = armor.getProtectionPoints(stack);
                pointsDown[i] = maxPoints - allocation[i];
                maxProtectionPoints += maxPoints;
                energyAllocation[i] = armor.getEnergyStored(stack);
                totalEnergyStored += energyAllocation[i];
                maxTotalEnergyStorage += armor.getMaxEnergyStored(stack);
                if (stack.getItem() instanceof DraconicArmor) hasDraconic = true;

                fireResistance += armor.getFireResistance(stack);

                //TODO in 1.13 the armor pieces should have more control over what effects they can supply. That would make this switch unnecessary.
                switch (i) {
                    case 3: //Head
                        break;
                    case 2: //Chest
                        boolean[] fa = armor.hasFlight(stack);
                        flight[0] = flight[0] || fa[0];
                        flight[1] = flight[1] || fa[1];
                        flight[2] = flight[2] || fa[2];

                        if (flight[0]) {
                            flightVModifier = Math.max(flightVModifier, armor.getFlightVModifier(stack, player));
                            flightSpeedModifier = Math.max(flightVModifier, armor.getFlightSpeedModifier(stack, player));
                        }
                        break;
                    case 1: //Legs
                        speedModifier = Math.max(speedModifier, armor.getSpeedModifier(stack, player));
                        break;
                    case 0: //Feet
                        hasHillStep = hasHillStep || armor.hasHillStep(stack, player);
                        jumpModifier = Math.max(jumpModifier, armor.getJumpModifier(stack, player));
                        break;
                    default: //Baubles
                        fa = armor.hasFlight(stack);
                        flight[0] = flight[0] || fa[0];
                        flight[1] = flight[1] || fa[1];
                        flight[2] = flight[2] || fa[2];

                        if (flight[0]) {
                            flightVModifier = Math.max(flightVModifier, armor.getFlightVModifier(stack, player));
                            flightSpeedModifier = Math.max(flightSpeedModifier, armor.getFlightSpeedModifier(stack, player));
                        }
                        speedModifier = Math.max(speedModifier, armor.getSpeedModifier(stack, player));
                        hasHillStep = hasHillStep || armor.hasHillStep(stack, player);
                        jumpModifier = Math.max(jumpModifier, armor.getJumpModifier(stack, player));
                        break;
                }
            }

            if (pieces == 0) {
                return null;
            }

            entropy = totalEntropy / pieces;
            meanRecoveryPoints = totalRecoveryPoints / pieces;

            return this;
        }

        private void getBaubles(EntityPlayer player, List<ItemStack> stacks) {
            IBaublesItemHandler baubles = BaublesApi.getBaublesHandler(player);
            baublesStacks = NonNullList.withSize(baubles.getSlots(), ItemStack.EMPTY);
            for (int i = 0; i < baubles.getSlots(); i++) {
                //Not allowed to directly modify a stack returned by IItemHandler.getStackInSlot so we copy the stack and replace it with the new stack later.
                baublesStacks.set(i, baubles.getStackInSlot(i).copy());
            }
            stacks.addAll(baublesStacks);
        }

        public void saveStacks(EntityPlayer player) {
            if (ModHelper.isBaublesInstalled) {
                saveBaubles(player);
            }
        }

        private void saveBaubles(EntityPlayer player) {
            if (baublesStacks != null) {
                IBaublesItemHandler baubles = BaublesApi.getBaublesHandler(player);
                for (int i = 0; i < baubles.getSlots(); i++) {
                    baubles.setStackInSlot(i, baublesStacks.get(i));
                }
            }
        }
    }
}
