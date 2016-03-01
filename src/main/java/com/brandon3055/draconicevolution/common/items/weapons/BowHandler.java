package com.brandon3055.draconicevolution.common.items.weapons;

import cofh.api.energy.IEnergyContainerItem;
import com.brandon3055.draconicevolution.common.ModItems;
import com.brandon3055.draconicevolution.common.entity.EntityCustomArrow;
import com.brandon3055.draconicevolution.common.entity.EntityDraconicArrow;
import com.brandon3055.draconicevolution.common.entity.EntityEnderArrow;
import com.brandon3055.draconicevolution.common.utills.IConfigurableItem;
import com.brandon3055.draconicevolution.common.utills.IUpgradableItem;
import com.brandon3055.draconicevolution.common.utills.LogHelper;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.event.entity.player.ArrowNockEvent;

import java.util.Random;

public class BowHandler {

	public static ItemStack onBowRightClick(Item bow, ItemStack stack, World world, EntityPlayer player){
		BowHandler.BowProperties properties = new BowHandler.BowProperties(stack, player);
		if (properties.canFire()){
			ArrowNockEvent event = new ArrowNockEvent(player, stack);
			MinecraftForge.EVENT_BUS.post(event);
			if (event.isCanceled()) {
				return event.result;
			}

			player.setItemInUse(stack, bow.getMaxItemUseDuration(stack));
		}
		else if (!properties.canFire() && properties.cantFireMessage != null) player.addChatComponentMessage(new ChatComponentTranslation(properties.cantFireMessage));

		return stack;
	}

	public static void onBowUsingTick(ItemStack stack, EntityPlayer player, int count){
		BowHandler.BowProperties properties = new BowHandler.BowProperties(stack, player);
		int j = 72000 - count;
		if (properties.autoFire && j >= properties.getDrawTicks()) player.stopUsingItem();
	}

	public static void onPlayerStoppedUsingBow(ItemStack stack, World world, EntityPlayer player, int count){
		BowHandler.BowProperties properties = new BowHandler.BowProperties(stack, player);
		if (!properties.canFire()) return;

		int j = 72000 - count;
		ArrowLooseEvent event = new ArrowLooseEvent(player, stack, j);
		MinecraftForge.EVENT_BUS.post(event);
		if (event.isCanceled()) {
			return;
		}
		j = event.charge;

		float drawArrowSpeedModifier = Math.min((float) j / (float) properties.getDrawTicks(), 1F);
		if (drawArrowSpeedModifier < 0.1) return;
		float velocity = properties.arrowSpeed * drawArrowSpeedModifier * 2F; //2F is the speed of a vanilla arrow

		LogHelper.info(velocity);

		EntityCustomArrow customArrow = new EntityCustomArrow(properties, world, player, velocity);

		if (properties.consumeArrow()) customArrow.canBePickedUp = 1;
		else customArrow.canBePickedUp = 2;




		if (!world.isRemote) world.spawnEntityInWorld(customArrow);

		world.playSoundAtEntity(player, "random.bow", 1.0F, (1.0F / (world.rand.nextFloat() * 0.4F + 1.2F) + (drawArrowSpeedModifier + (velocity / 40F)) * 0.5F));

//		boolean flag = player.capabilities.isCreativeMode || EnchantmentHelper.getEnchantmentLevel(Enchantment.infinity.effectId, stack) > 0;
//
//		if (flag || player.inventory.hasItem(Items.arrow)) {
//			float f = j / pullSpeedModifier;
//			f = (f * f + f * 2.0F) / 3.0F;
//
//			if ((j < minRelease) || f < 0.1D) return;
//
//			if (f > 1.0F) f = 1.0F;
//
//			f *= speedModifier;
//
//			EntityDraconicArrow entityArrow = new EntityDraconicArrow(world, player, f * 2.0F);//.setShooter(player);
//
//			if (f >= 1.0F) entityArrow.setIsCritical(true);
//
//			int k = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, stack);
//
//			if (k > 0)                                            //
//				entityArrow.setDamage(entityArrow.getDamage() + k + 0.5D);
//
//			int l = EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, stack);
//
//			if (l > 0) entityArrow.setKnockbackStrength(l * 2);
//
//			if (EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, stack) > 0) entityArrow.setFire(200);
//
//			if (ignorSpeedWhenCalculatingDamage) {
//				entityArrow.ignorSpeed = true;
//				entityArrow.setDamage(entityArrow.getDamage() + ignorSpeedCompensation);
//			}
//			if (isExplosive) {
//				if ((player.inventory.hasItemStack(new ItemStack(Items.gunpowder, 4)) || player.capabilities.isCreativeMode)) {
//					entityArrow.explosive = isExplosive;
//					for (int i = 0; i < 4; ++i)
//						player.inventory.consumeInventoryItem(Items.gunpowder);
//				} else {
//					if (player.worldObj.isRemote)
//						player.addChatMessage(new ChatComponentTranslation("msg.bowoutofgunpowder.txt"));
//				}
//			}
//
//			stack.damageItem(1, player);                                                                            //
//			world.playSoundAtEntity(player, "random.bow", 1.0F, soundPitchModifier * (1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + f * 0.3F));
//
//			if (flag) entityArrow.canBePickedUp = 2;
//			else {
//				entityArrow.canBePickedUp = 1;
//				player.inventory.consumeInventoryItem(Items.arrow);
//			}
//
//			if (!world.isRemote) world.spawnEntityInWorld(entityArrow);
//		}
	}

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

			EntityDraconicArrow entityArrow = new EntityDraconicArrow(world, player, f * 2.0F);//.setShooter(player);

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
				player.inventory.consumeInventoryItem(Items.arrow);
			}

			if (!world.isRemote)
				world.spawnEntityInWorld(entityArrow);
		}
	}

	public static void enderShot(ItemStack stack, World world, EntityPlayer player, int count, Random itemRand, float pullSpeedModifier, float speedModifier, float soundPitchModifier, int minRelease)
	{
		int j = 72000 - count;
		ArrowLooseEvent event = new ArrowLooseEvent(player, stack, j);
		MinecraftForge.EVENT_BUS.post(event);
		if (event.isCanceled()) {
			return;
		}
		j = event.charge;

		if (player.inventory.hasItem(ModItems.enderArrow)) {
			float f = j / pullSpeedModifier;
			f = (f * f + f * 2.0F) / 3.0F;

			if ((j < minRelease) || f < 0.1D)
				return;

			if (f > 1.0F)
				f = 1.0F;

			f *= speedModifier;

			EntityEnderArrow entityArrow = new EntityEnderArrow(world, player, f * 2.0F);


			stack.damageItem(1, player);																			//
			world.playSoundAtEntity(player, "random.bow", 1.0F, soundPitchModifier * (1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + f * 0.3F));

			if (player.inventory.hasItem(ModItems.enderArrow)) player.inventory.consumeInventoryItem(ModItems.enderArrow);

			if (!world.isRemote) {
				world.spawnEntityInWorld(entityArrow);
				player.mountEntity(entityArrow);
			}

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

	public static class BowProperties {
		public ItemStack bow;
		public EntityPlayer player;

		public float arrowDamage = 0F;
		public float arrowSpeed = 0F;
		public float explosionPower = 0F;
		public float shockWavePower = 0F;
		public float zoomModifier = 0F;
		private int drawTimeReduction = 0;
		public boolean autoFire = false;
		public boolean energyBolt = false;

		public String cantFireMessage = null;

		public BowProperties(){
			this.bow = new ItemStack(ModItems.wyvernBow);
			this.player = null;
		}

		public BowProperties(ItemStack bow, EntityPlayer player){
			this.bow = bow;
			this.player = player;
			updateValues();
		}

		public int calculateEnergyCost(){
			updateValues();
			double rfCost = 80;

			rfCost *= 1 + arrowDamage;
			rfCost *= 1 + arrowSpeed;
			rfCost *= 1 + explosionPower * 10;
			rfCost *= 1 + shockWavePower * 10;
			if (energyBolt) rfCost *= 30;

			return (int)rfCost;
		}

		public boolean canFire(){
			updateValues();

			if (player == null) return false;
			if (!(bow.getItem() instanceof IEnergyContainerItem)){
				cantFireMessage = "[Error] This bow is not a valid energy container (This is a bug, Please report on the Draconic Evolution github)";
				return false;
			}
			else if (!energyBolt && shockWavePower > 0){
				cantFireMessage = "msg.de.shockWaveForEnergyBoltsOnly.name";
				return false;
			}
			else if (energyBolt && explosionPower > 0){
				cantFireMessage = "msg.de.explosiveNotForEnergyBolts.name";
				return false;
			}
			else if (calculateEnergyCost() > ((IEnergyContainerItem)bow.getItem()).getEnergyStored(bow)){
				cantFireMessage = "msg.de.insufficientPowerToFire.name";
				return false;
			}
			else if (!energyBolt && !player.inventory.hasItem(Items.arrow) && EnchantmentHelper.getEnchantmentLevel(Enchantment.infinity.effectId, bow) == 0 && !player.capabilities.isCreativeMode){
				cantFireMessage = "msg.de.outOfArrows.name";
				return false;
			}


			cantFireMessage = null;
			return true;
		}

		private void updateValues(){
			arrowDamage = (float)IUpgradableItem.EnumUpgrade.ARROW_DAMAGE.getUpgradePoints(bow);
			arrowSpeed = 1F + IConfigurableItem.ProfileHelper.getFloat(bow, "BowArrowSpeedModifier", 0F);
			explosionPower = IConfigurableItem.ProfileHelper.getFloat(bow, "BowExplosionPower", 0F);
			shockWavePower = IConfigurableItem.ProfileHelper.getFloat(bow, "BowShockWavePower", 0F);
			drawTimeReduction = IUpgradableItem.EnumUpgrade.DRAW_SPEED.getUpgradePoints(bow);
			zoomModifier = IConfigurableItem.ProfileHelper.getFloat(bow, "BowZoomModifier", 0F);
			autoFire = IConfigurableItem.ProfileHelper.getBoolean(bow, "BowAutoFire", false);
			energyBolt = IConfigurableItem.ProfileHelper.getBoolean(bow, "BowEnergyBolt", false);
		}

		public int getDrawTicks(){
			return Math.max(62 - (drawTimeReduction * 10), 1);
		}

		public boolean consumeArrow(){
			if (!energyBolt && EnchantmentHelper.getEnchantmentLevel(Enchantment.infinity.effectId, bow) == 0 && !player.capabilities.isCreativeMode) {
				player.inventory.consumeInventoryItem(Items.arrow);
				return true;
			}

			return false;
		}
	}
}
//lot, "BowArrowSpeedModifier").setMinMaxAndIncromente(0F, (float)EnumUpgrade.ARROW_SPEED.getUpgradePoints(stack), 0.01F).readFromItem(stack, 0F)todo dont forget the speed needs to be "Calibrated" for the upgrade system
