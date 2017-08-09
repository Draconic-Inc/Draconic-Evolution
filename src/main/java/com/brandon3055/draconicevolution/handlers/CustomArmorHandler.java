package com.brandon3055.draconicevolution.handlers;

import com.brandon3055.brandonscore.BrandonsCore;
import com.brandon3055.brandonscore.utils.ItemNBTHelper;
import com.brandon3055.draconicevolution.DEConfig;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.integration.ModHelper;
import com.brandon3055.draconicevolution.items.armor.DraconicArmor;
import com.brandon3055.draconicevolution.items.armor.ICustomArmor;
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
import net.minecraftforge.event.entity.living.LivingHurtEvent;
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

    @SubscribeEvent
    public void onPlayerHurt(LivingHurtEvent event) {
//		EntityPlayer player = (EntityPlayer) event.getEntityLiving();
//		ArmorSummery summery = new ArmorSummery().getSummery(player);
//		if (summery == null || summery.protectionPoints <= 0) return;
//		float newEntropy = Math.min(summery.entropy + Math.min(3, event.ammount/5) + player.world.rand.nextFloat(), 100F);
//
//		//Divide the damage between the armor peaces based on how many of the protection points each peace has
//		float totalAbsorbed = 0;
//		for (int i = 0; i < summery.allocation.length; i++){
//			if (summery.allocation[i] == 0) continue;
//			ItemStack armorPeace = summery.armorStacks[i];
//
//			float dmgShear = summery.allocation[i] / summery.protectionPoints;
//			float dmg = dmgShear * event.ammount;
//
//			float absorbed = Math.min(dmg, summery.allocation[i]);
//			dmg -= absorbed;
//			totalAbsorbed += absorbed;
//			summery.allocation[i]-=absorbed;
//			ItemNBTHelper.setFloat(armorPeace, "ProtectionPoints", summery.allocation[i]);
//			ItemNBTHelper.setInteger(armorPeace, "ShieldHitTimer", 20);
//			ItemNBTHelper.setFloat(armorPeace, "ShieldEntropy", newEntropy);
//
////			if (dmg > 0 && absorbed >= dmgShear*20F){
////				int energyCost = (int)(dmg * OVER_DRAIN_COST);
////				int extracted = ((IEnergyContainerItem)armorPeace.getItem()).extractEnergy(armorPeace, energyCost, false);
////				dmg = (energyCost-extracted) / OVER_DRAIN_COST;
////				totalAbsorbed += extracted / OVER_DRAIN_COST;
////				ItemNBTHelper.setFloat(armorPeace, "ShieldEntropy", 100);
////			}
//
////			LogHelper.info(dmg);
//		}
//
//		event.ammount-=totalAbsorbed;
//		if (event.ammount <= 0) event.setCanceled(true);
//		player.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).removeModifier(new AttributeModifier(KB_ATTRIB_UUID, SharedMonsterAttributes.knockbackResistance.getAttributeUnlocalizedName(), 100, 0));
//		LogHelper.info("hurt");
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onPlayerAttacked(LivingAttackEvent event) {
        if (!(event.getEntityLiving() instanceof EntityPlayer) || event.isCanceled() || event.getAmount() <= 0) {
            return;
        }

        EntityPlayer player = (EntityPlayer) event.getEntityLiving();
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
//        if ((float) player.hurtResistantTime > (float) player.maxHurtResistantTime / 2.0F) return;

        float newEntropy = Math.min(summery.entropy + 1 + (hitAmount / 20), 100F);

        //Divide the damage between the armor peaces based on how many of the protection points each peace has
        float totalAbsorbed = 0;
        int remainingPoints = 0;
        for (int i = 0; i < summery.allocation.length; i++) {
            if (summery.allocation[i] == 0) continue;
            ItemStack armorPeace = summery.armorStacks.get(i);

            float dmgShear = summery.allocation[i] / summery.protectionPoints;
            float dmg = dmgShear * hitAmount;

            float absorbed = Math.min(dmg, summery.allocation[i]);
            totalAbsorbed += absorbed;
            summery.allocation[i] -= absorbed;
            remainingPoints += summery.allocation[i];
            ItemNBTHelper.setFloat(armorPeace, "ProtectionPoints", summery.allocation[i]);
            ItemNBTHelper.setFloat(armorPeace, "ShieldEntropy", newEntropy);
        }

        DraconicEvolution.network.sendToAllAround(new PacketShieldHit(player, remainingPoints / summery.maxProtectionPoints), new NetworkRegistry.TargetPoint(player.dimension, player.posX, player.posY, player.posZ, 64));

        if (remainingPoints > 0) {
            player.hurtResistantTime = 20;
        }
        else if (hitAmount - totalAbsorbed > 0) {
            player.attackEntityFrom(event.getSource(), hitAmount - totalAbsorbed);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onPlayerDeath(LivingDeathEvent event) {
        if (!(event.getEntityLiving() instanceof EntityPlayer) || event.isCanceled()) {
            return;
        }

        EntityPlayer player = (EntityPlayer) event.getEntityLiving();
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

        if (totalCharge < 10000000) {
            return;
        }

        for (int i = 0; i < summery.armorStacks.size(); i++) {
            if (!summery.armorStacks.get(i).isEmpty()) {
                ((ICustomArmor) summery.armorStacks.get(i).getItem()).modifyEnergy(summery.armorStacks.get(i), -(int) ((charge[i] / (double) totalCharge) * 10000000L));
            }
        }

        player.sendMessage(new TextComponentTranslation("msg.de.shieldDepleted.txt").setStyle(new Style().setColor(TextFormatting.DARK_RED)));
        event.setCanceled(true);
        player.setHealth(1);
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {

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

            float maxForPeace = ((ICustomArmor) stack.getItem()).getProtectionPoints(stack);
            int energyAmount = ((ICustomArmor) summery.armorStacks.get(i).getItem()).getEnergyPerProtectionPoint();
            ((ICustomArmor) stack.getItem()).modifyEnergy(stack, -(int) (((double) summery.energyAllocation[i] / (double) summery.totalEnergyStored) * (totalPointsToAdd * energyAmount)));
            float pointsForPeace = (summery.pointsDown[i] / Math.max(1, summery.maxProtectionPoints - summery.protectionPoints)) * totalPointsToAdd;
            summery.allocation[i] += pointsForPeace;

            if (summery.allocation[i] > maxForPeace || maxForPeace - summery.allocation[i] < 0.1F) {
                summery.allocation[i] = maxForPeace;
            }

            ItemNBTHelper.setFloat(stack, "ProtectionPoints", summery.allocation[i]);

            if (player.hurtResistantTime <= 0) {//TODO Increase this delay (Store the delay in forge entity nbt)
                ItemNBTHelper.setFloat(stack, "ShieldEntropy", summery.entropy);
            }
        }
    }

    public static void tickArmorEffects(ArmorSummery summery, EntityPlayer player) {

        //region/*----------------- Flight ------------------*/
        if (DEConfig.enableFlight) {
            if (summery != null && summery.flight[0]) {
                playersWithFlight.put(player, true);
                player.capabilities.allowFlying = true;
                if (summery.flight[1]) player.capabilities.isFlying = true;

                if (player.world.isRemote) {
                    setPlayerFlySpeed(player, 0.05F + (0.05F * summery.flightSpeedModifier));
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

            if (!player.onGround && player.getRidingEntity() == null) player.jumpMovementFactor = 0.02F + (0.02F * summery.speedModifier);
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
                player.stepHeight = 0.5F;
            }
        }
        //endregion
    }

    private static void setPlayerFlySpeed(EntityPlayer player, float speed) {
        player.capabilities.setFlySpeed(speed);
    }

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
            }
            return true;
        }

        if ((event.getSource().damageType.equals("inWall") || event.getSource().damageType.equals("drown")) && !summery.armorStacks.get(3).isEmpty()) {
            if (event.getAmount() <= 2f) event.setCanceled(true);
            return true;
        }

        return false;
    }

    public static class ArmorSummery {
        /*---- Shield ----*/
        /**
         * Max protection points from all equipped armor peaces
         */
        public float maxProtectionPoints = 0F;
        /**
         * Total protection points from all equipped armor peaces
         */
        public float protectionPoints = 0F;
        /**
         * Number of quipped armor peaces
         */
        public int peaces = 0;
        /**
         * Point  Allocation, The number of points on each peace
         */
        public float[] allocation;
        /**
         * How many points have been drained from each armor peace
         */
        public float[] pointsDown;
        /**
         * The armor peaces (Index will contain null if peace is not present)
         */
        public NonNullList<ItemStack> armorStacks;
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
         * RF stored in each armor peace
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
            NonNullList<ItemStack> armorSlots = player.inventory.armorInventory;
            float totalEntropy = 0;
            float totalRecoveryPoints = 0;

            allocation = new float[armorSlots.size()];
            armorStacks = NonNullList.withSize(armorSlots.size(), ItemStack.EMPTY);
            pointsDown = new float[armorSlots.size()];
            energyAllocation = new int[armorSlots.size()];

            for (int i = 0; i < armorSlots.size(); i++) {
                ItemStack stack = armorSlots.get(i);
                if (stack.isEmpty() || !(stack.getItem() instanceof ICustomArmor)) continue;
                ICustomArmor armor = (ICustomArmor) stack.getItem();
                peaces++;
                allocation[i] = ItemNBTHelper.getFloat(stack, "ProtectionPoints", 0);
                protectionPoints += allocation[i];
                totalEntropy += ItemNBTHelper.getFloat(stack, "ShieldEntropy", 0);
                armorStacks.set(i, stack);
                totalRecoveryPoints += armor.getRecoveryRate(stack);//UpgradeHelper.getUpgradeLevel(stack, ToolUpgrade.SHIELD_RECOVERY);
                float maxPoints = armor.getProtectionPoints(stack);
                pointsDown[i] = maxPoints - allocation[i];
                maxProtectionPoints += maxPoints;
                energyAllocation[i] = armor.getEnergyStored(stack);
                totalEnergyStored += energyAllocation[i];
                maxTotalEnergyStorage += armor.getMaxEnergyStored(stack);
                if (stack.getItem() instanceof DraconicArmor) hasDraconic = true;

                fireResistance += armor.getFireResistance(stack);

                switch (i) {
                    case 2:
                        flight = armor.hasFlight(stack);
                        if (flight[0]) {
                            flightVModifier = armor.getFlightVModifier(stack, player);
                            flightSpeedModifier = armor.getFlightSpeedModifier(stack, player);
                        }
                        break;
                    case 1:
                        speedModifier = armor.getSpeedModifier(stack, player);
                        break;
                    case 0:
                        hasHillStep = armor.hasHillStep(stack, player);
                        jumpModifier = armor.getJumpModifier(stack, player);
                        break;
                }
            }

            if (peaces == 0) {
                return null;
            }

            entropy = totalEntropy / peaces;
            meanRecoveryPoints = totalRecoveryPoints / peaces;

            return this;
        }
    }
}
