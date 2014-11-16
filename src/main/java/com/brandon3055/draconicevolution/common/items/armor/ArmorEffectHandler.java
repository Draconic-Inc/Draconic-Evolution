package com.brandon3055.draconicevolution.common.items.armor;

import cofh.api.energy.IEnergyContainerItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/**
 * Created by Brandon on 13/11/2014.
 */
public class ArmorEffectHandler {

//	slotHelm = 4;
//	slotChest = 3;
//	slotLeggs = 2;
//	slotBoots = 1;

	public static boolean getHasFlight(EntityPlayer player){
		return isDraconicArmor(player, 3) && ((IEnergyContainerItem)player.getEquipmentInSlot(3).getItem()).extractEnergy(player.getEquipmentInSlot(3), 80, !player.capabilities.isFlying) == 80;
	}

	public static boolean getHasSwiftness(EntityPlayer player){
		return isDraconicArmor(player, 2) || isWyvernArmor(player, 2);
	}

	public static int getSwiftnessLevel(EntityPlayer player){
		int i = isWyvernArmor(player, 2) ? 1 : isDraconicArmor(player, 2) ? 2 : 0;
		return player.isSprinting() ? i * 2 : i;
	}

	public static boolean getHasJumpBoost(EntityPlayer player){
		return (isDraconicArmor(player, 1) || isWyvernArmor(player, 1)) && extractEnergy(player, (getJumpLevel(player)+1)*1000, 1);
	}

	public static int getJumpLevel(EntityPlayer player){
		int i = isWyvernArmor(player, 1) ? 1 : isDraconicArmor(player, 1) ? 2 : 0;
		return player.isSprinting() ? i * 2 : i;
	}

	public static boolean getFireImunity(EntityPlayer player){
		if (isDraconicArmor(player, 3) && extractEnergy(player, 80, 3)) return true;
		return (isWyvernArmor(player, 1) && isWyvernArmor(player, 2) && isWyvernArmor(player, 3) && isWyvernArmor(player, 4));
	}

	public static boolean getHasHighStep(EntityPlayer player){
		return isDraconicArmor(player, 1);
	}

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
}
