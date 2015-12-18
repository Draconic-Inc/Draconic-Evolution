package com.brandon3055.draconicevolution.common.items.tools.baseclasses;

import cofh.api.energy.IEnergyContainerItem;
import com.brandon3055.brandonscore.common.utills.ItemNBTHelper;
import com.brandon3055.draconicevolution.common.ModItems;
import com.brandon3055.draconicevolution.common.lib.References;
import com.brandon3055.draconicevolution.common.utills.LogHelper;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityDragonPart;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.server.S23PacketBlockChange;
import net.minecraft.util.*;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class ToolHandler {
	public static Block[] destroyList = {Blocks.cobblestone, Blocks.stone, Blocks.dirt, Blocks.gravel, Blocks.sand, Blocks.grass, Blocks.netherrack};

	public static boolean isRightMaterial(final Material material, final Material[] materialsListing) {
		for (final Material mat : materialsListing) {
			if (material == mat) {
				return true;
			}
		}
		return false;
	}

	public static boolean checkDestroyList(Block curBlock) {
		for (Block block : destroyList) {
			if (curBlock == block) {
				return true;
			}
		}
		return false;
	}

	public static boolean disSquare(int x, int y, int z, final EntityPlayer player, final World world, final boolean silk, final int fortune, Material[] materialsListing, ItemStack stack) {
		int size = stack.getItem().equals(ModItems.draconicAxe) ? 2 : ItemNBTHelper.getShort(stack, "size", (short) 0);
		MovingObjectPosition mop = raytraceFromEntity(world, player, 4.5D);
		if (mop == null) {
			updateGhostBlocks(player, world);
			return false;
		}

		int sizeX = size;
		int sizeY = size;
		int sizeZ = size;
		int yOff = (size * -1);
		Block targetBlock = world.getBlock(x, y, z);
		if (size > 0) yOff++;
		//if (size == 0) return false;
		int side = (stack.getItem().equals(ModItems.draconicAxe)) ? 6 : mop.sideHit;
		switch (side) {
			case 0:
			case 1:
				sizeY = 0;
				yOff = 0;
				break;
			case 2:
			case 3:
				sizeZ = 0;
				break;
			case 4:
			case 5:
				sizeX = 0;
				break;
		}

		for (int x1 = x - sizeX; x1 <= x + sizeX; x1++) {
			for (int y1 = y - (sizeY + yOff); y1 <= y + (sizeY - yOff); y1++) {
				for (int z1 = z - sizeZ; z1 <= z + sizeZ; z1++) {
					mineBlock(x1, y1, z1, player, world, silk, fortune, materialsListing, stack);
					//player.worldObj.scheduleBlockUpdate(x1, y1, z1, Blocks.stone, 100);
				}
			}
		}
		world.playSoundEffect(x + 0.5F, y + 0.5F, z + 0.5F, targetBlock.stepSound.getStepResourcePath(), (targetBlock.stepSound.getVolume() + 1.0F) / 2.0F, targetBlock.stepSound.getPitch() * 0.8F);
		return true;
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	public static void mineBlock(final int x, final int y, final int z, final EntityPlayer player, final World world, final boolean silk, final int fortune, Material[] materialsListing, ItemStack stack) {
		Block block = world.getBlock(x, y, z);
		int meta = world.getBlockMetadata(x, y, z);
		Material mat = block.getMaterial();
		if ((block != null) && (!block.isAir(world, x, y, z)) && (block.getPlayerRelativeBlockHardness(player, world, x, y, z) != 0.0F)) {
			List<ItemStack> items = new ArrayList();

			if ((!block.canHarvestBlock(player, meta)) || (!isRightMaterial(mat, materialsListing))) {
				return;
			}

			if (!(stack.getItem() instanceof IEnergyContainerItem) || ((IEnergyContainerItem) stack.getItem()).getEnergyStored(stack) < References.ENERGYPERBLOCK) {
				if (!player.capabilities.isCreativeMode) return;
			} else {
				if (!player.capabilities.isCreativeMode)
					((IEnergyContainerItem) stack.getItem()).extractEnergy(stack, References.ENERGYPERBLOCK, false);
			}

			if (checkDestroyList(block) && (ItemNBTHelper.getBoolean(stack, "obliterate", false))) {
				world.setBlockToAir(x, y, z);
				return;
			}

			if ((stack.getItem().equals(ModItems.draconicAxe) ? 2 : ItemNBTHelper.getShort(stack, "size", (short) 0)) == 0) return;

			if ((silk) && (block.canSilkHarvest(world, player, x, y, z, meta))) {
				if (block == Blocks.lit_redstone_ore)
					items.add(new ItemStack(Item.getItemFromBlock(Blocks.redstone_ore)));
				else items.add(new ItemStack(block, 1, meta));
			} else {
				items.addAll(block.getDrops(world, x, y, z, meta, fortune));
				//block.dropXpOnBlockBreak(world, (int)player.posX, (int)player.posY, (int)player.posZ, block.getExpDrop(world, meta, fortune));
				int xp = block.getExpDrop(world, meta, fortune);
				player.addExperience(xp);
			}

			world.setBlockToAir(x, y, z);

			if (!world.isRemote && !player.capabilities.isCreativeMode && world.getGameRules().getGameRuleBooleanValue("doTileDrops")) {
				for (final ItemStack item : items) {
					world.spawnEntityInWorld(new EntityItem(world, player.posX, player.posY, player.posZ, item));
				}
			}
		}
	}

	public static void damageEntityBasedOnHealth(Entity entity, EntityPlayer player, float dmgMult) {
		ItemStack stack = player.getCurrentEquippedItem();
		float baseAttack = getDamageAgainstEntity(stack, entity);

		if (entity instanceof EntityLivingBase)
		{
			float entHealth = ((EntityLivingBase) entity).getHealth();
			baseAttack += (entHealth * dmgMult);
		}

		if (entity instanceof EntityDragonPart)
		{
			List <EntityDragon> list = player.worldObj.getEntitiesWithinAABB(EntityDragon.class, entity.boundingBox.expand(10, 10, 10));
			if (!list.isEmpty() && list.get(0) instanceof EntityDragon){
				EntityDragon dragon = list.get(0);
				float entHealth = dragon.getHealth();
				baseAttack += (entHealth * dmgMult);
				LogHelper.info(baseAttack);
			}
		}

		if (!player.capabilities.isCreativeMode && ((IEnergyContainerItem) stack.getItem()).getEnergyStored(stack) < (int)(baseAttack / 2) * References.ENERGYPERATTACK) return;

		entity.attackEntityFrom(DamageSource.causePlayerDamage(player), baseAttack);
		if (EnchantmentHelper.getEnchantmentLevel(Enchantment.fireAspect.effectId, stack) > 0) entity.setFire(EnchantmentHelper.getEnchantmentLevel(Enchantment.fireAspect.effectId, stack) * 15);


		if (!player.capabilities.isCreativeMode) ((IEnergyContainerItem) stack.getItem()).extractEnergy(stack, (int)(baseAttack / 2) * References.ENERGYPERATTACK, false);


		if (entity instanceof EntityLivingBase) {
			EntityLivingBase entityLivingBase = (EntityLivingBase)entity;
			double d1 = player.posX - entityLivingBase.posX;
			double d0;

			for (d0 = player.posZ - entityLivingBase.posZ; d1 * d1 + d0 * d0 < 1.0E-4D; d0 = (Math.random() - Math.random()) * 0.01D)
			{
				d1 = (Math.random() - Math.random()) * 0.01D;
			}
			entityLivingBase.attackedAtYaw = (float)(Math.atan2(d0, d1) * 180.0D / Math.PI) - entityLivingBase.rotationYaw;

			if (entityLivingBase.worldObj.rand.nextDouble() >= entityLivingBase.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).getAttributeValue())
			{
				entityLivingBase.isAirBorne = true;
				float f1 = MathHelper.sqrt_double(d1 * d1 + d0 * d0);
				float f2 = 0.1F + EnchantmentHelper.getKnockbackModifier(player, entityLivingBase) * 0.4F;
				entityLivingBase.motionX /= 2.0D;
				entityLivingBase.motionY /= 2.0D;
				entityLivingBase.motionZ /= 2.0D;
				entityLivingBase.motionX -= d1 / (double)f1 * (double)f2;
				entityLivingBase.motionY += (double)f2;
				entityLivingBase.motionZ -= d0 / (double)f1 * (double)f2;

				if (entityLivingBase.motionY > 0.4000000059604645D)
				{
					entityLivingBase.motionY = 0.4000000059604645D;
				}
			}
		}
	}

	@SuppressWarnings({"rawtypes", "unused"})
	public static void AOEAttack(EntityPlayer player, Entity entity, ItemStack stack, int range) {
		World world = player.worldObj;
		AxisAlignedBB box = AxisAlignedBB.getBoundingBox(entity.posX - range, entity.posY - range, entity.posZ - range, entity.posX + range, entity.posY + range, entity.posZ + range).expand(1.0D, 1.0D, 1.0D);
		List list = world.getEntitiesWithinAABBExcludingEntity(player, box);
		if (range == 0) return;
		IEnergyContainerItem item = (IEnergyContainerItem)stack.getItem();

		for (Object entityObject : list) {
			if (item.getEnergyStored(stack) < References.ENERGYPERATTACK) break;
			if (entityObject instanceof EntityLivingBase) {
				EntityLivingBase entityLivingBase = (EntityLivingBase) entityObject;
				if (entityLivingBase.getEntityId() == entity.getEntityId()) continue;

				entityLivingBase.attackEntityFrom(DamageSource.causePlayerDamage(player), getDamageAgainstEntity(stack, entityLivingBase));
				item.extractEnergy(stack, References.ENERGYPERATTACK, false);
				if (EnchantmentHelper.getEnchantmentLevel(Enchantment.fireAspect.effectId, stack) > 0) entityLivingBase.setFire(EnchantmentHelper.getEnchantmentLevel(Enchantment.fireAspect.effectId, stack) * 15);


				double d1 = player.posX - entityLivingBase.posX;
				double d0;

				for (d0 = player.posZ - entityLivingBase.posZ; d1 * d1 + d0 * d0 < 1.0E-4D; d0 = (Math.random() - Math.random()) * 0.01D) {
					d1 = (Math.random() - Math.random()) * 0.01D;
				}

				if (entityLivingBase.worldObj.rand.nextDouble() >= entityLivingBase.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).getAttributeValue()) {
					entityLivingBase.isAirBorne = true;
					float f1 = MathHelper.sqrt_double(d1 * d1 + d0 * d0);
					float f2 = 0.1F + (EnchantmentHelper.getKnockbackModifier(player, entityLivingBase) * 0.4F);
					entityLivingBase.motionX /= 2.0D;
					entityLivingBase.motionY /= 2.0D;
					entityLivingBase.motionZ /= 2.0D;
					entityLivingBase.motionX -= d1 / (double) f1 * (double) f2;
					entityLivingBase.motionY += (double) f2;
					entityLivingBase.motionZ -= d0 / (double) f1 * (double) f2;

					if (entityLivingBase.motionY > 0.4000000059604645D) {
						entityLivingBase.motionY = 0.4000000059604645D;
					}
				}
				entityLivingBase.attackTime = 0;
			}
		}

	}

	public static MovingObjectPosition raytraceFromEntity(World world, Entity player, double range) {//todo move to core and make the range work as expected
		float f = 1.0F;
		float f1 = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * f;
		float f2 = player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * f;
		double d0 = player.prevPosX + (player.posX - player.prevPosX) * (double) f;
		double d1 = player.prevPosY + (player.posY - player.prevPosY) * (double) f;
		if (!world.isRemote && player instanceof EntityPlayer) d1 += 1.62D;
		double d2 = player.prevPosZ + (player.posZ - player.prevPosZ) * (double) f;
		Vec3 vec3 = Vec3.createVectorHelper(d0, d1, d2);
		float f3 = MathHelper.cos(-f2 * 0.017453292F - (float) Math.PI);
		float f4 = MathHelper.sin(-f2 * 0.017453292F - (float) Math.PI);
		float f5 = -MathHelper.cos(-f1 * 0.017453292F);
		float f6 = MathHelper.sin(-f1 * 0.017453292F);
		float f7 = f4 * f5;
		float f8 = f3 * f5;
		double d3 = range;
		if (player instanceof EntityPlayerMP && range < 10) {
			d3 = ((EntityPlayerMP) player).theItemInWorldManager.getBlockReachDistance();
		}
		Vec3 vec31 = vec3.addVector((double) f7 * d3, (double) f6 * d3, (double) f8 * d3);
		return world.rayTraceBlocks(vec3, vec31);
	}

	public static void updateGhostBlocks(EntityPlayer player, World world) {
		if (world.isRemote) return;
		int xPos = (int) player.posX;
		int yPos = (int) player.posY;
		int zPos = (int) player.posZ;

		for (int x = xPos - 6; x < xPos + 6; x++) {
			for (int y = yPos - 6; y < yPos + 6; y++) {
				for (int z = zPos - 6; z < zPos + 6; z++) {
					((EntityPlayerMP)player).playerNetServerHandler.sendPacket(new S23PacketBlockChange(x, y, z, world));
					//world.markBlockForUpdate(x, y, z);
				}
			}
		}
	}

	public static float getBaseAttackDamage(ItemStack stack)
	{
		if (stack == null) return 0;
		float sharpMod = (float)EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, stack) * 4F;
		if (stack.getItem() == ModItems.draconicDestructionStaff) return (ModItems.DRACONIUM_T3.getDamageVsEntity()) + sharpMod;
		else if (stack.getItem() instanceof ItemSword) return (((ItemSword)stack.getItem()).func_150931_i()) + sharpMod;
		return 0;
	}

	public static float getDamageAgainstEntity(ItemStack stack, Entity entity)
	{
		float baseAttack = getBaseAttackDamage(stack);
		float smiteMod = EnchantmentHelper.getEnchantmentLevel(Enchantment.smite.effectId ,stack) * 6F;
		float athropodsMod = EnchantmentHelper.getEnchantmentLevel(Enchantment.baneOfArthropods.effectId ,stack) * 6F;

		if (entity instanceof EntityLivingBase && ((EntityLivingBase)entity).isEntityUndead()) baseAttack += smiteMod;
		if (entity instanceof EntitySpider) baseAttack += athropodsMod;

		return baseAttack;
	}
}
