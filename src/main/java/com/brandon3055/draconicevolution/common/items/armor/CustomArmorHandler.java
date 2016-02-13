package com.brandon3055.draconicevolution.common.items.armor;

import cofh.api.energy.IEnergyContainerItem;
import com.brandon3055.brandonscore.common.utills.ItemNBTHelper;
import com.brandon3055.draconicevolution.common.utills.IUpgradableItem;
import cpw.mods.fml.common.gameevent.TickEvent;
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
	private static final DamageSource ADMIN_KILL = new DamageSource("administrative.kill");

	public static void onPlayerHurt(LivingHurtEvent event) {
//		EntityPlayer player = (EntityPlayer) event.entityLiving;
//		ShieldSummery summery = new ShieldSummery().getSummery(player);
//		if (summery == null || summery.protectionPoints <= 0) return;
//		float newEntropy = Math.min(summery.entropy + Math.min(3, event.ammount/5) + player.worldObj.rand.nextFloat(), 100F);
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

	public static void onPlayerAttacked(LivingAttackEvent event){
		EntityPlayer player = (EntityPlayer)event.entityLiving;
		ShieldSummery summery = new ShieldSummery().getSummery(player);

		if (summery == null || summery.protectionPoints <= 0 || event.source == ADMIN_KILL) return;
		event.setCanceled(true);
		//Ensure that the /kill command can still kill the player
		if (event.ammount == Float.MAX_VALUE && !event.source.damageType.equals(ADMIN_KILL.damageType)){
			player.attackEntityFrom(ADMIN_KILL, Float.MAX_VALUE);
			return;
		}
		if ((float)player.hurtResistantTime > (float)player.maxHurtResistantTime / 2.0F) return;

		float newEntropy = Math.min(summery.entropy + Math.min(2, event.ammount/5) + player.worldObj.rand.nextFloat(), 100F);

		//Divide the damage between the armor peaces based on how many of the protection points each peace has
		float totalAbsorbed = 0;
		int remainingPoints = 0;
		for (int i = 0; i < summery.allocation.length; i++){
			if (summery.allocation[i] == 0) continue;
			ItemStack armorPeace = summery.armorStacks[i];

			float dmgShear = summery.allocation[i] / summery.protectionPoints;
			float dmg = dmgShear * event.ammount;

			float absorbed = Math.min(dmg, summery.allocation[i]);
			totalAbsorbed += absorbed;
			summery.allocation[i]-=absorbed;
			remainingPoints += summery.allocation[i];
			ItemNBTHelper.setFloat(armorPeace, "ProtectionPoints", summery.allocation[i]);
			ItemNBTHelper.setFloat(armorPeace, "ShieldEntropy", newEntropy);
		}

		if (remainingPoints > 0) {
			player.hurtResistantTime = 20;
		}
		else if (event.ammount-totalAbsorbed > 0){
			player.attackEntityFrom(event.source, event.ammount-totalAbsorbed);
		}
	}

	public static void onPlayerDeath(LivingDeathEvent event) {
		EntityPlayer player = (EntityPlayer)event.entityLiving;
		ShieldSummery summery = new ShieldSummery().getSummery(player);

		if (summery == null || event.source == ADMIN_KILL) return;

		int[] charge = new int[summery.armorStacks.length];
		int totalCharge = 0;
		for (int i = 0; i < summery.armorStacks.length; i++){
			if (summery.armorStacks[i] != null) {
				charge[i] = ((IEnergyContainerItem)summery.armorStacks[i].getItem()).getEnergyStored(summery.armorStacks[i]);
				totalCharge += charge[i];
			}
		}

		if (totalCharge < 5000000) return;

		for (int i = 0; i < summery.armorStacks.length; i++){
			if (summery.armorStacks[i] != null) {
				((IEnergyContainerItem)summery.armorStacks[i].getItem()).extractEnergy(summery.armorStacks[i], (int) ((charge[i] / (double) totalCharge) * 5000000D), false);
			}
		}

		player.addChatComponentMessage(new ChatComponentTranslation("msg.de.shieldDepleted.txt").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.DARK_RED)));
		event.setCanceled(true);
		player.setHealth(1);
	}

	public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
		EntityPlayer player = event.player;
		ShieldSummery summery = new ShieldSummery().getSummery(player);
		if (summery == null || (summery.maxProtectionPoints - summery.protectionPoints < 0.01 && summery.entropy == 0) || player.worldObj.isRemote) return;

		float totalPointsToAdd = Math.min(summery.maxProtectionPoints - summery.protectionPoints, summery.maxProtectionPoints / 60F);
		totalPointsToAdd *= (1F - (summery.entropy / 100F));
		totalPointsToAdd = Math.min(totalPointsToAdd, summery.totalEnergyStored / 1000);
		if (totalPointsToAdd < 0F) totalPointsToAdd = 0F;

		summery.entropy -= (summery.meanRecoveryPoints * 0.01F);
		if (summery.entropy < 0) summery.entropy = 0;

		for (int i = 0; i < summery.armorStacks.length; i++){
			ItemStack stack = summery.armorStacks[i];
			if (stack == null || summery.totalEnergyStored <= 0) continue;
			float maxForPeace = ((ICustomArmor)stack.getItem()).getProtectionPoints(stack);
			((IEnergyContainerItem)stack.getItem()).extractEnergy(stack, (int)((summery.energyAllocation[i] / (double)summery.totalEnergyStored) * (totalPointsToAdd * 1000)), false);
			float pointsForPeace = (summery.pointsDown[i] / Math.max(1, summery.maxProtectionPoints - summery.protectionPoints)) * totalPointsToAdd;
			summery.allocation[i] += pointsForPeace;
			if (summery.allocation[i] > maxForPeace || maxForPeace - summery.allocation[i] < 0.1F) summery.allocation[i] = maxForPeace;
			ItemNBTHelper.setFloat(stack, "ProtectionPoints", summery.allocation[i]);
			if (player.hurtResistantTime <= 0)ItemNBTHelper.setFloat(stack, "ShieldEntropy", summery.entropy);
		}
	}




















//Re Add the armor effects!

















//	slotHelm = 4;
//	slotChest = 3;
//	slotLeggs = 2;
//	slotBoots = 1;

	//Flight------------------------------------------------------------------------------------------------------------
	public static boolean getHasFlight(EntityPlayer player){
		return isDraconicArmor(player, 3);// && ((IEnergyContainerItem)player.getEquipmentInSlot(3).getItem()).extractEnergy(player.getEquipmentInSlot(3), 80, !player.capabilities.isFlying) == 80;
	}

	public static float getVAccel(EntityPlayer player){
		return ItemNBTHelper.getFloat(player.getEquipmentInSlot(3), "VerticalAcceleration", 0.3F);
	}

	public static boolean getVAccSprint(EntityPlayer player){
		return ItemNBTHelper.getBoolean(player.getEquipmentInSlot(3), "EffectiveOnSprint", false);
	}

	public static boolean getFlightLock(EntityPlayer player){
		return ItemNBTHelper.getBoolean(player.getEquipmentInSlot(3), "ArmorFlightLock", false);
	}

	//Swiftness---------------------------------------------------------------------------------------------------------
	public static boolean getHasSwiftness(EntityPlayer player){
		return isDraconicArmor(player, 2) || isWyvernArmor(player, 2);
	}

	public static int getSwiftnessLevel(EntityPlayer player){
		int i = isWyvernArmor(player, 2) ? 2 : isDraconicArmor(player, 2) ? 4 : 0;
		return (player.isSprinting() || !ItemNBTHelper.getBoolean(player.getEquipmentInSlot(2), "ArmorSprintOnly", false)) ? i * 2 : i;
	}

	public static float getSwiftnessMultiplier(EntityPlayer player){
		return ItemNBTHelper.getFloat(player.getEquipmentInSlot(2), "ArmorSpeedMult", 1f);
	}

	//Jump Boost--------------------------------------------------------------------------------------------------------
	public static boolean getHasJumpBoost(EntityPlayer player){
		return (isDraconicArmor(player, 1) || isWyvernArmor(player, 1)) && extractEnergy(player, (getJumpLevel(player)+1)*80, 1);
	}

	public static int getJumpLevel(EntityPlayer player){
		int i = isWyvernArmor(player, 1) ? 2 : isDraconicArmor(player, 1) ? 4 : 0;
		return (player.isSprinting() || !ItemNBTHelper.getBoolean(player.getEquipmentInSlot(1), "ArmorSprintOnly", false)) ? i * 2 : i;
	}

	public static float getJumpMultiplier(EntityPlayer player){
		return ItemNBTHelper.getFloat(player.getEquipmentInSlot(1), "ArmorJumpMult", 1f);
	}

	//Fire Immunity-----------------------------------------------------------------------------------------------------
	public static boolean getFireImunity(EntityPlayer player){
		if (isDraconicArmor(player, 3) && extractEnergy(player, 80, 3)) return true;
		return (isWyvernArmor(player, 1) && isWyvernArmor(player, 2) && isWyvernArmor(player, 3) && isWyvernArmor(player, 4));
	}

	//Hill Step---------------------------------------------------------------------------------------------------------
	public static boolean getHasHighStep(EntityPlayer player){
		return isDraconicArmor(player, 1) && ItemNBTHelper.getBoolean(player.getEquipmentInSlot(1), "ArmorHillStep", true);
	}

	//Armor & Energy----------------------------------------------------------------------------------------------------
	public static boolean isWyvernArmor(EntityPlayer player, int slot){
		int slotType = 4 - slot;
		ItemStack armor = player.getEquipmentInSlot(slot);
		return armor != null && armor.getItem() instanceof WyvernArmor && ((WyvernArmor)armor.getItem()).armorType == slotType;
	}

	public static boolean isDraconicArmor(EntityPlayer player, int slot){
		int slotType = 4 - slot;
		ItemStack armor = player.getEquipmentInSlot(slot);
		return armor != null && armor.getItem() instanceof DraconicArmor && ((DraconicArmor)armor.getItem()).armorType == slotType;
	}

	public static boolean extractEnergy(EntityPlayer player, int amount, int slot){
		if (player.getEquipmentInSlot(slot).getItem() instanceof IEnergyContainerItem) return ((IEnergyContainerItem)player.getEquipmentInSlot(slot).getItem()).extractEnergy(player.getEquipmentInSlot(slot), amount, false) == amount;
		return false;
	}


	public static class ShieldSummery {
		/**Max protection points from all equipped armor peaces*/
		public float maxProtectionPoints;
		/**Total protection points from all equipped armor peaces*/
		public float protectionPoints;
		/**Number of quipped armor peaces*/
		public int peaces;
		/**Point  Allocation, The number of points on each peace*/
		public float[] allocation;
		/***/
		public float[] pointsDown;
		/**The armor peaces (Index will contain null if peace is not present)*/
		public ItemStack[] armorStacks;
		/**Mean Fatigue*/
		public float entropy;
		/**Mean Recovery Points*/
		public int meanRecoveryPoints;
		/**Total RF stored in the armor*/
		public int totalEnergyStored;
		/**RF stored in each armor peace*/
		public int[] energyAllocation;

		public ShieldSummery getSummery(EntityPlayer player){
			ItemStack[] armorSlots = player.inventory.armorInventory;
			protectionPoints = 0;
			peaces = 0;
			float totalEntropy = 0;
			int totalRecoveryPoints = 0;
			meanRecoveryPoints = 0;
			allocation = new float[armorSlots.length];
			armorStacks = new ItemStack[armorSlots.length];
			pointsDown = new float[armorSlots.length];
			energyAllocation = new int[armorSlots.length];
			totalEnergyStored = 0;

			for (int i = 0; i < armorSlots.length; i++){
				ItemStack stack = armorSlots[i];
				if (stack == null || !(stack.getItem() instanceof ICustomArmor)) continue;
				peaces++;
				allocation[i] = ItemNBTHelper.getFloat(stack, "ProtectionPoints", 0);
				protectionPoints += allocation[i];
				totalEntropy += ItemNBTHelper.getFloat(stack, "ShieldEntropy", 0);
				armorStacks[i] = stack;
				totalRecoveryPoints += IUpgradableItem.EnumUpgrade.SHIELD_RECOVERY.getUpgradePoints(stack);
				float maxPoints = ((ICustomArmor)stack.getItem()).getProtectionPoints(stack);
				pointsDown[i] = maxPoints - allocation[i];
				maxProtectionPoints += maxPoints;
				energyAllocation[i] = ((IEnergyContainerItem)stack.getItem()).getEnergyStored(stack);
				totalEnergyStored += energyAllocation[i];
			}

			if (peaces == 0) return null;

			entropy = totalEntropy / peaces;
			meanRecoveryPoints = totalRecoveryPoints / peaces;

			return this;
		}
	}
}
