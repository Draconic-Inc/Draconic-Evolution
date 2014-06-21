package com.brandon3055.draconicevolution.common.items.weapons;

import java.util.Random;

import com.brandon3055.draconicevolution.common.entity.EntityDraconicArrow;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;

public class BowHandler {
	public static void standerdShot(ItemStack stack, World world, EntityPlayer player, int count, Random itemRand, float pullSpeedModifier, float speedModifier, boolean ignorSpeedWhenCalculatingDamage, double ignorSpeedCompensation, float soundPitchModifier, boolean isExplosive, int minRelease)
	{
		int j = 72000 - count;
		ArrowLooseEvent event = new ArrowLooseEvent(player, stack, j);
		MinecraftForge.EVENT_BUS.post(event);
		if (event.isCanceled()) {
			return;
		}
		j = event.charge;

		boolean flag = player.capabilities.isCreativeMode || EnchantmentHelper.getEnchantmentLevel(Enchantment.infinity.effectId, stack) > 0;

		if (flag || player.inventory.hasItem(Items.arrow)) {
			float f = j / pullSpeedModifier;
			f = (f * f + f * 2.0F) / 3.0F;

			if ((j < minRelease) || f < 0.1D)
				return;

			if (f > 1.0F)
				f = 1.0F;

			f *= speedModifier;

			EntityDraconicArrow entityarrow = new EntityDraconicArrow(stack, world, player, f * 2.0F);//.setShooter(player);

			if (f >= 1.0F)
				entityarrow.setIsCritical(true);

			int k = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, stack);

			if (k > 0) 											//
				entityarrow.setDamage(entityarrow.getDamage() + k + 0.5D);

			int l = EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, stack);

			if (l > 0)
				entityarrow.setKnockbackStrength(l * 2);

			if (EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, stack) > 0)
				entityarrow.setFire(200);

			if (ignorSpeedWhenCalculatingDamage) {
				entityarrow.ignorSpeed = true;
				entityarrow.setDamage(entityarrow.getDamage() + ignorSpeedCompensation);
			}
			if (isExplosive) {
				if ((player.inventory.hasItemStack(new ItemStack(Items.gunpowder, 4)) || player.capabilities.isCreativeMode)) {
					entityarrow.explosive = isExplosive;
					for (int i = 0; i < 4; ++i)
						player.inventory.consumeInventoryItem(Items.gunpowder);
				} else {
					if (player.worldObj.isRemote)
						player.addChatMessage(new ChatComponentTranslation("msg.bowoutofgunpowder.txt"));
				}
			}

			stack.damageItem(1, player);																			//
			world.playSoundAtEntity(player, "random.bow", 1.0F, soundPitchModifier * (1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + f * 0.3F));

			if (flag)
				entityarrow.canBePickedUp = 2;
			else {
				entityarrow.canBePickedUp = 1;
				player.inventory.consumeInventoryItem(Items.arrow);
			}

			if (!world.isRemote)
				world.spawnEntityInWorld(entityarrow);
		}
	}

	public static void rapidFire(EntityPlayer player, int count, int speed)
	{
		int ticks = 72000 - count;
		if (ticks > speed) {
			//if (!player.isSneaking())
			player.stopUsingItem();
		}
	}
}
