package com.brandon3055.draconicevolution.common.items.weapons;

import com.brandon3055.draconicevolution.common.entity.EntityDraconicArrow;
import com.brandon3055.draconicevolution.common.entity.EntityEnderArrow;
import com.brandon3055.draconicevolution.common.items.ModItems;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;

import java.util.Random;

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

		if (flag || player.inventory.hasItem(Items.arrow) || player.inventory.hasItem(ModItems.enderArrow)) {
			float f = j / pullSpeedModifier;
			f = (f * f + f * 2.0F) / 3.0F;

			if ((j < minRelease) || f < 0.1D)
				return;

			if (f > 1.0F)
				f = 1.0F;

			f *= speedModifier;

			EntityDraconicArrow entityArrow;

			if (player.inventory.hasItem(ModItems.enderArrow))
				/*EntityEnderArrow*/ entityArrow = new EntityEnderArrow(world, player, f * 2.0F);
			else
				/*EntityDraconicArrow*/ entityArrow = new EntityDraconicArrow(world, player, f * 2.0F);


			if (f >= 1.0F)
				entityArrow.setIsCritical(true);

			int k = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, stack);

			if (k > 0) 											//
				entityArrow.setDamage(entityArrow.getDamage() + k + 0.5D);

			int l = EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, stack);

			if (l > 0)
				entityArrow.setKnockbackStrength(l * 2);

			if (EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, stack) > 0)
				entityArrow.setFire(200);

			if (ignorSpeedWhenCalculatingDamage) {
				entityArrow.ignorSpeed = true;
				entityArrow.setDamage(entityArrow.getDamage() + ignorSpeedCompensation);
			}
			if (isExplosive) {
				if ((player.inventory.hasItemStack(new ItemStack(Items.gunpowder, 4)) || player.capabilities.isCreativeMode)) {
					entityArrow.explosive = isExplosive;
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
				entityArrow.canBePickedUp = 2;
			else {
				entityArrow.canBePickedUp = 1;
				if (player.inventory.hasItem(ModItems.enderArrow))
					player.inventory.consumeInventoryItem(ModItems.enderArrow);
				else
					player.inventory.consumeInventoryItem(Items.arrow);
			}

			if (!world.isRemote)
				world.spawnEntityInWorld(entityArrow);
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
