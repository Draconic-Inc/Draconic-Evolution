package com.brandon3055.draconicevolution.common.items.armor;

import com.brandon3055.draconicevolution.common.core.utills.LogHelper;
import com.brandon3055.draconicevolution.common.items.ModItems;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.ISpecialArmor;

/**
 * Created by Brandon on 3/07/2014.
 */
public class ArmorPropertiesHandler {

	public static ISpecialArmor.ArmorProperties draconicHelm(EntityLivingBase player, ItemStack armor, DamageSource source){
		if (source == DamageSource.fallingBlock || source == DamageSource.anvil)
			return new ISpecialArmor.ArmorProperties(1, 1, Integer.MAX_VALUE);
		else if (isFireDamage(source))
			return new ISpecialArmor.ArmorProperties(1, 0.15, Integer.MAX_VALUE);
		else
			return new ISpecialArmor.ArmorProperties(1, 0.14, Integer.MAX_VALUE);

	}

	public static ISpecialArmor.ArmorProperties draconicChest(EntityLivingBase player, ItemStack armor, DamageSource source){
		if (hasFullSetDraconic(player) && source == DamageSource.wither || source == DamageSource.magic)
			return new ISpecialArmor.ArmorProperties(1, 1, Integer.MAX_VALUE);
		else if (isFireDamage(source))
			return new ISpecialArmor.ArmorProperties(1, 0.50, Integer.MAX_VALUE);
		else
			return new ISpecialArmor.ArmorProperties(1, 0.50, Integer.MAX_VALUE);
	}

	public static ISpecialArmor.ArmorProperties draconicLeggs(EntityLivingBase player, ItemStack armor, DamageSource source){
		if (isFireDamage(source))
			return new ISpecialArmor.ArmorProperties(1, 0.20, Integer.MAX_VALUE);
		else
			return new ISpecialArmor.ArmorProperties(1, 0.18, Integer.MAX_VALUE);
	}

	public static ISpecialArmor.ArmorProperties draconicBoots(EntityLivingBase player, ItemStack armor, DamageSource source){
		if (isFireDamage(source))
			return new ISpecialArmor.ArmorProperties(1, 0.15, Integer.MAX_VALUE);
		else if (source == DamageSource.fall)
			return new ISpecialArmor.ArmorProperties(1, 1, Integer.MAX_VALUE);
		else
			return new ISpecialArmor.ArmorProperties(1, 0.14, Integer.MAX_VALUE);
	}

	public static boolean isFireDamage(DamageSource damage){
		return damage == DamageSource.inFire || damage == DamageSource.onFire || damage == DamageSource.lava;
	}

	public static boolean hasFullSetDraconic(EntityLivingBase player){
		ItemStack slotBoots = player.getEquipmentInSlot(1);
		ItemStack slotLeggs = player.getEquipmentInSlot(2);
		ItemStack slotChest = player.getEquipmentInSlot(3);
		ItemStack slotHelmet = player.getEquipmentInSlot(4);
		if (slotBoots == null || slotHelmet == null || slotChest == null || slotHelmet == null)
			return false;
		if(slotBoots.getItem() == ModItems.draconicBoots && slotLeggs.getItem() == ModItems.draconicLeggs && slotChest.getItem() == ModItems.draconicChest && slotHelmet.getItem() == ModItems.draconicHelm)
			return true;
		return false;
	}


	public static ISpecialArmor.ArmorProperties wyvernHelm(EntityLivingBase player, ItemStack armor, DamageSource source){
			return new ISpecialArmor.ArmorProperties(1, 0.12, Integer.MAX_VALUE);
	}

	public static ISpecialArmor.ArmorProperties wyvernChest(EntityLivingBase player, ItemStack armor, DamageSource source){
		if (hasFullSetWyvern(player) && source == DamageSource.magic)
			return new ISpecialArmor.ArmorProperties(1, 0.9, Integer.MAX_VALUE);
		else if (isFireDamage(source))
			return new ISpecialArmor.ArmorProperties(1, 0.49, Integer.MAX_VALUE);
		else
			return new ISpecialArmor.ArmorProperties(1, 0.48, Integer.MAX_VALUE);
	}

	public static ISpecialArmor.ArmorProperties wyvernLeggs(EntityLivingBase player, ItemStack armor, DamageSource source){
		if (isFireDamage(source))
			return new ISpecialArmor.ArmorProperties(1, 0.19, Integer.MAX_VALUE);
		else
			return new ISpecialArmor.ArmorProperties(1, 0.16, Integer.MAX_VALUE);
	}

	public static ISpecialArmor.ArmorProperties wyvernBoots(EntityLivingBase player, ItemStack armor, DamageSource source){
		if (isFireDamage(source))
			return new ISpecialArmor.ArmorProperties(1, 0.14, Integer.MAX_VALUE);
		else
			return new ISpecialArmor.ArmorProperties(1, 0.12, Integer.MAX_VALUE);
	}

	public static boolean hasFullSetWyvern(EntityLivingBase player){
		ItemStack slotBoots = player.getEquipmentInSlot(1);
		ItemStack slotLeggs = player.getEquipmentInSlot(2);
		ItemStack slotChest = player.getEquipmentInSlot(3);
		ItemStack slotHelmet = player.getEquipmentInSlot(4);
		if (slotBoots == null || slotHelmet == null || slotChest == null || slotHelmet == null)
			return false;
		if(slotBoots.getItem() == ModItems.wyvernBoots && slotLeggs.getItem() == ModItems.wyvernLeggs && slotChest.getItem() == ModItems.wyvernChest && slotHelmet.getItem() == ModItems.wyvernHelm)
			return true;
		return false;
	}

}
