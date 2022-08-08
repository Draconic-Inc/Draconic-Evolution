package com.brandon3055.draconicevolution.common.items.armor;

import cofh.api.energy.IEnergyContainerItem;
import com.brandon3055.brandonscore.BrandonsCore;
import com.brandon3055.brandonscore.common.utills.ItemNBTHelper;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.common.handler.BalanceConfigHandler;
import com.brandon3055.draconicevolution.common.handler.ConfigHandler;
import com.brandon3055.draconicevolution.common.network.ShieldHitPacket;
import com.brandon3055.draconicevolution.common.utills.IUpgradableItem;
import com.brandon3055.draconicevolution.integration.ModHelper;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import java.util.*;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

/**
 * Created by Brandon on 13/11/2014.
 */
public class CustomArmorHandler {
    public static final UUID WALK_SPEED_UUID = UUID.fromString("0ea6ce8e-d2e8-11e5-ab30-625662870761");
    private static final DamageSource ADMIN_KILL = new DamageSource("administrative.kill")
            .setDamageAllowedInCreativeMode()
            .setDamageBypassesArmor()
            .setDamageIsAbsolute();
    public static Map<EntityPlayer, Boolean> playersWithFlight = new WeakHashMap<EntityPlayer, Boolean>();
    public static List<String> playersWithUphillStep = new ArrayList<String>();

    public static void onPlayerHurt(LivingHurtEvent event) {
        //		EntityPlayer player = (EntityPlayer) event.entityLiving;
        //		ArmorSummery summery = new ArmorSummery().getSummery(player);
        //		if (summery == null || summery.protectionPoints <= 0) return;
        //		float newEntropy = Math.min(summery.entropy + Math.min(3, event.ammount/5) +
        // player.worldObj.rand.nextFloat(), 100F);
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
        ////				int extracted = ((IEnergyContainerItem)armorPeace.getItem()).extractEnergy(armorPeace, energyCost,
        // false);
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
        //		player.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).removeModifier(new
        // AttributeModifier(KB_ATTRIB_UUID, SharedMonsterAttributes.knockbackResistance.getAttributeUnlocalizedName(),
        // 100, 0));
        //		LogHelper.info("hurt");
    }

    public static void onPlayerAttacked(LivingAttackEvent event) {
        if (event.isCanceled()) {
            return;
        }

        EntityPlayer player = (EntityPlayer) event.entityLiving;
        ArmorSummery summery = new ArmorSummery().getSummery(player);

        float hitAmount = ModHelper.applyModDamageAdjustments(summery, event);

        if (applyArmorDamageBlocking(event, summery)) {
            return;
        }
        if (summery == null || summery.protectionPoints <= 0 || event.source == ADMIN_KILL) {
            return;
        }
        event.setCanceled(true);
        // Ensure that the /kill command can still kill the player
        if (hitAmount == Float.MAX_VALUE && !event.source.damageType.equals(ADMIN_KILL.damageType)) {
            player.attackEntityFrom(ADMIN_KILL, Float.MAX_VALUE);
            return;
        }
        if ((float) player.hurtResistantTime > (float) player.maxHurtResistantTime / 2.0F) return;

        float newEntropy = Math.min(summery.entropy + 1 + (hitAmount / 20), 100F);

        // Divide the damage between the armor peaces based on how many of the protection points each peace has
        float totalAbsorbed = 0;
        int remainingPoints = 0;
        for (int i = 0; i < summery.allocation.length; i++) {
            if (summery.allocation[i] == 0) continue;
            ItemStack armorPeace = summery.armorStacks[i];

            float dmgShear = summery.allocation[i] / summery.protectionPoints;
            float dmg = dmgShear * hitAmount;

            float absorbed = Math.min(dmg, summery.allocation[i]);
            totalAbsorbed += absorbed;
            summery.allocation[i] -= absorbed;
            remainingPoints += summery.allocation[i];
            ItemNBTHelper.setFloat(armorPeace, "ProtectionPoints", summery.allocation[i]);
            ItemNBTHelper.setFloat(armorPeace, "ShieldEntropy", newEntropy);
        }

        if (summery.protectionPoints > 0) {
            DraconicEvolution.network.sendToAllAround(
                    new ShieldHitPacket(player, summery.protectionPoints / summery.maxProtectionPoints),
                    new NetworkRegistry.TargetPoint(player.dimension, player.posX, player.posY, player.posZ, 64));
            player.worldObj.playSoundEffect(
                    player.posX + 0.5D,
                    player.posY + 0.5D,
                    player.posZ + 0.5D,
                    "draconicevolution:shieldStrike",
                    0.9F,
                    player.worldObj.rand.nextFloat() * 0.1F + 1.055F);
        }

        if (remainingPoints > 0) {
            player.hurtResistantTime = 20;
        } else if (hitAmount - totalAbsorbed > 0) {
            player.attackEntityFrom(event.source, hitAmount - totalAbsorbed);
        }
    }

    public static void onPlayerDeath(LivingDeathEvent event) {
        if (event.isCanceled()) {
            return;
        }

        EntityPlayer player = (EntityPlayer) event.entityLiving;
        ArmorSummery summery = new ArmorSummery().getSummery(player);

        if (summery == null || event.source == ADMIN_KILL) return;

        if (summery.protectionPoints > 500) {
            event.setCanceled(true);
            event.entityLiving.setHealth(10);
            //		LogHelper.warn("Something is trying to bypass the draconic shield. [Culprit: {Damage Type=" +
            // event.source.damageType + ", Damage Class=" + event.source.toString() + "]");
            return;
        }

        if (!summery.hasDraconic) return;

        int[] charge = new int[summery.armorStacks.length];
        int totalCharge = 0;
        for (int i = 0; i < summery.armorStacks.length; i++) {
            if (summery.armorStacks[i] != null) {
                charge[i] = ((IEnergyContainerItem) summery.armorStacks[i].getItem())
                        .getEnergyStored(summery.armorStacks[i]);
                totalCharge += charge[i];
            }
        }

        if (totalCharge < BalanceConfigHandler.draconicArmorBaseStorage) return;

        for (int i = 0; i < summery.armorStacks.length; i++) {
            if (summery.armorStacks[i] != null) {
                ((IEnergyContainerItem) summery.armorStacks[i].getItem())
                        .extractEnergy(
                                summery.armorStacks[i],
                                (int) ((charge[i] / (double) totalCharge)
                                        * BalanceConfigHandler.draconicArmorBaseStorage),
                                false);
            }
        }

        player.addChatComponentMessage(new ChatComponentTranslation("msg.de.shieldDepleted.txt")
                .setChatStyle(new ChatStyle().setColor(EnumChatFormatting.DARK_RED)));
        event.setCanceled(true);
        player.setHealth(1);
    }

    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {

        EntityPlayer player = event.player;
        ArmorSummery summery = new ArmorSummery().getSummery(player);

        tickShield(summery, player);
        tickArmorEffects(summery, player);
    }

    public static void tickShield(ArmorSummery summery, EntityPlayer player) {
        if (summery == null
                || (summery.maxProtectionPoints - summery.protectionPoints < 0.01 && summery.entropy == 0)
                || player.worldObj.isRemote) return;

        float totalPointsToAdd =
                Math.min(summery.maxProtectionPoints - summery.protectionPoints, summery.maxProtectionPoints / 60F);
        totalPointsToAdd *= (1F - (summery.entropy / 100F));
        totalPointsToAdd = Math.min(
                totalPointsToAdd,
                summery.totalEnergyStored
                        / (summery.hasDraconic
                                ? BalanceConfigHandler.draconicArmorEnergyPerProtectionPoint
                                : BalanceConfigHandler.wyvernArmorEnergyPerProtectionPoint));
        if (totalPointsToAdd < 0F) totalPointsToAdd = 0F;

        summery.entropy -= (summery.meanRecoveryPoints * 0.01F);
        if (summery.entropy < 0) summery.entropy = 0;

        for (int i = 0; i < summery.armorStacks.length; i++) {
            ItemStack stack = summery.armorStacks[i];
            if (stack == null || summery.totalEnergyStored <= 0) continue;
            float maxForPeace = ((ICustomArmor) stack.getItem()).getProtectionPoints(stack);
            int energyAmount = ((ICustomArmor) summery.armorStacks[i].getItem()).getEnergyPerProtectionPoint();
            ((IEnergyContainerItem) stack.getItem())
                    .extractEnergy(
                            stack,
                            (int) (((double) summery.energyAllocation[i] / (double) summery.totalEnergyStored)
                                    * (totalPointsToAdd * energyAmount)),
                            false);
            float pointsForPeace =
                    (summery.pointsDown[i] / Math.max(1, summery.maxProtectionPoints - summery.protectionPoints))
                            * totalPointsToAdd;
            summery.allocation[i] += pointsForPeace;
            if (summery.allocation[i] > maxForPeace || maxForPeace - summery.allocation[i] < 0.1F)
                summery.allocation[i] = maxForPeace;
            ItemNBTHelper.setFloat(stack, "ProtectionPoints", summery.allocation[i]);
            if (player.hurtResistantTime <= 0) ItemNBTHelper.setFloat(stack, "ShieldEntropy", summery.entropy);
        }
    }

    public static void tickArmorEffects(ArmorSummery summery, EntityPlayer player) {

        // region/*----------------- Flight ------------------*/
        if (ConfigHandler.enableFlight) {
            if (summery != null && summery.flight[0]) {
                playersWithFlight.put(player, true);
                player.capabilities.allowFlying = true;
                if (summery.flight[1]) player.capabilities.isFlying = true;

                if (player.worldObj.isRemote) setPlayerFlySpeed(player, 0.05F + (0.05F * summery.flightSpeedModifier));

                if ((!player.onGround && player.capabilities.isFlying)
                        && player.motionY != 0
                        && summery.flightVModifier > 0) {
                    //				float percentIncrease = summery.flightVModifier;

                    if (BrandonsCore.proxy.isSpaceDown() && !BrandonsCore.proxy.isShiftDown()) {
                        // LogHelper.info(player.motionY);
                        player.motionY = 0.225F * summery.flightVModifier;
                    }

                    if (BrandonsCore.proxy.isShiftDown() && !BrandonsCore.proxy.isSpaceDown()) {
                        player.motionY = -0.225F * summery.flightVModifier;
                    }
                }

                if (summery.flight[2]
                        && player.moveForward == 0
                        && player.moveStrafing == 0
                        && player.capabilities.isFlying) {
                    player.motionX *= 0.5;
                    player.motionZ *= 0.5;
                }

            } else {
                if (!playersWithFlight.containsKey(player)) {
                    playersWithFlight.put(player, false);
                }

                if (playersWithFlight.get(player) && !player.worldObj.isRemote) {
                    playersWithFlight.put(player, false);

                    if (!player.capabilities.isCreativeMode) {
                        player.capabilities.allowFlying = false;
                        player.capabilities.isFlying = false;
                        player.sendPlayerAbilities();
                    }
                }

                if (player.worldObj.isRemote && playersWithFlight.get(player)) {
                    playersWithFlight.put(player, false);
                    if (!player.capabilities.isCreativeMode) {
                        player.capabilities.allowFlying = false;
                        player.capabilities.isFlying = false;
                    }
                    setPlayerFlySpeed(player, 0.05F);
                }
            }
        }
        // endregion

        // region/*---------------- Swiftness ----------------*/

        IAttribute speedAttr = SharedMonsterAttributes.movementSpeed;
        if (summery != null && summery.speedModifier > 0) {
            double value = summery.speedModifier;
            if (player.getEntityAttribute(speedAttr).getModifier(WALK_SPEED_UUID) == null) {
                player.getEntityAttribute(speedAttr)
                        .applyModifier(new AttributeModifier(
                                WALK_SPEED_UUID, speedAttr.getAttributeUnlocalizedName(), value, 1));
            } else if (player.getEntityAttribute(speedAttr)
                            .getModifier(WALK_SPEED_UUID)
                            .getAmount()
                    != value) {
                player.getEntityAttribute(speedAttr)
                        .removeModifier(player.getEntityAttribute(speedAttr).getModifier(WALK_SPEED_UUID));
                player.getEntityAttribute(speedAttr)
                        .applyModifier(new AttributeModifier(
                                WALK_SPEED_UUID, speedAttr.getAttributeUnlocalizedName(), value, 1));
            }

            if (!player.onGround && player.ridingEntity == null)
                player.jumpMovementFactor = 0.02F + (0.02F * summery.speedModifier);
        } else if (player.getEntityAttribute(speedAttr).getModifier(WALK_SPEED_UUID) != null) {
            player.getEntityAttribute(speedAttr)
                    .removeModifier(player.getEntityAttribute(speedAttr).getModifier(WALK_SPEED_UUID));
        }

        // endregion

        // region/*---------------- HillStep -----------------*/
        if (summery != null && player.worldObj.isRemote) {
            boolean highStepListed = playersWithUphillStep.contains(player.getDisplayName()) && player.stepHeight >= 1f;
            boolean hasHighStep = summery.hasHillStep;

            if (hasHighStep && !highStepListed) {
                playersWithUphillStep.add(player.getDisplayName());
                player.stepHeight = 1f;
            }

            if (!hasHighStep && highStepListed) {
                playersWithUphillStep.remove(player.getDisplayName());
                player.stepHeight = 0.5F;
            }
        }
        // endregion
    }

    private static void setPlayerFlySpeed(EntityPlayer player, float speed) {
        player.capabilities.setFlySpeed(speed);
    }

    private static boolean applyArmorDamageBlocking(LivingAttackEvent event, ArmorSummery summery) {
        if (summery == null) return false;

        if (event.source.isFireDamage() && summery.fireResistance >= 1F) {
            event.setCanceled(true);
            event.entityLiving.extinguish();
            return true;
        }

        if (event.source.damageType.equals("fall") && summery.jumpModifier > 0F) {
            if (event.ammount < summery.jumpModifier * 5F) event.setCanceled(true);
            return true;
        }

        if ((event.source.damageType.equals("inWall") || event.source.damageType.equals("drown"))
                && summery.armorStacks[3] != null) {
            if (event.ammount <= 2f) event.setCanceled(true);
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
        public ItemStack[] armorStacks;
        /**
         * Mean Fatigue
         */
        public float entropy = 0F;
        /**
         * Mean Recovery Points
         */
        public int meanRecoveryPoints = 0;
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
        public boolean[] flight = new boolean[] {false, false, false};
        public float flightVModifier = 0F;
        public float speedModifier = 0F;
        public float jumpModifier = 0F;
        public float fireResistance = 0F;
        public float flightSpeedModifier = 0;
        public boolean hasHillStep = false;
        public boolean hasDraconic = false;

        public ArmorSummery getSummery(EntityPlayer player) {
            ItemStack[] armorSlots = player.inventory.armorInventory;
            float totalEntropy = 0;
            int totalRecoveryPoints = 0;

            allocation = new float[armorSlots.length];
            armorStacks = new ItemStack[armorSlots.length];
            pointsDown = new float[armorSlots.length];
            energyAllocation = new int[armorSlots.length];

            for (int i = 0; i < armorSlots.length; i++) {
                ItemStack stack = armorSlots[i];
                if (stack == null || !(stack.getItem() instanceof ICustomArmor)) continue;
                ICustomArmor armor = (ICustomArmor) stack.getItem();
                peaces++;
                allocation[i] = ItemNBTHelper.getFloat(stack, "ProtectionPoints", 0);
                protectionPoints += allocation[i];
                totalEntropy += ItemNBTHelper.getFloat(stack, "ShieldEntropy", 0);
                armorStacks[i] = stack;
                totalRecoveryPoints += IUpgradableItem.EnumUpgrade.SHIELD_RECOVERY.getUpgradePoints(stack);
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

            if (peaces == 0) return null;

            entropy = totalEntropy / peaces;
            meanRecoveryPoints = totalRecoveryPoints / peaces;

            return this;
        }
    }
}
