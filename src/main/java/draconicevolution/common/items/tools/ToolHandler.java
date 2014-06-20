package draconicevolution.common.items.tools;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import draconicevolution.common.core.helper.ItemNBTHelper;
import draconicevolution.common.items.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragonPart;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings.GameType;

public class ToolHandler {
	public static Material[] materialsPick = {Material.anvil, Material.circuits, Material.coral, Material.glass, Material.ice, Material.iron, Material.rock};
	public static Material[] materialsShovel = {Material.clay, Material.ground, Material.grass, Material.sand, Material.snow, Material.craftedSnow};
	public static Material[] materialsAxe = {Material.cactus, Material.leaves, Material.wood, Material.plants};
	public static Material[] materialsDStaff = {Material.anvil, Material.circuits, Material.coral, Material.glass, Material.ice, Material.iron, Material.rock, Material.clay, Material.ground, Material.grass, Material.sand, Material.snow, Material.craftedSnow, Material.cactus, Material.leaves, Material.wood, Material.plants};
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
			if (player instanceof EntityPlayer)
				updateGhostBlocks(player, world);
			return false;
		}

		int sizeX = size;
		int sizeY = size;
		int sizeZ = size;
		int yOff = (size * -1);
		Block targetBlock = world.getBlock(x, y, z);
		if (size > 0) yOff++;
		if (size == 0) return false;
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
					player.worldObj.scheduleBlockUpdate(x1, y1, z1, Blocks.stone, 100);
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
			if (checkDestroyList(block) && (ItemNBTHelper.getBoolean(stack, "obliterate", false))) {
				world.setBlockToAir(x, y, z);
				return;
			}
			if ((silk) && (block.canSilkHarvest(world, player, x, y, z, meta))) {
				if (block == Blocks.lit_redstone_ore)
					items.add(new ItemStack(Item.getItemFromBlock(Blocks.redstone_ore)));
				else items.add(new ItemStack(block.getItem(world, x, y, z), 1, meta));
			} else {
				items.addAll(block.getDrops(world, x, y, z, meta, fortune));
				//block.dropXpOnBlockBreak(world, (int)player.posX, (int)player.posY, (int)player.posZ, block.getExpDrop(world, meta, fortune));
				int xp = block.getExpDrop(world, meta, fortune);
				player.addExperience(xp);
			}
			world.setBlockToAir(x, y, z);
			if (!world.isRemote && !(((EntityPlayerMP) player).theItemInWorldManager.getGameType() == GameType.CREATIVE)) {
				for (final ItemStack item : items) {
					world.spawnEntityInWorld(new EntityItem(world, player.posX, player.posY, player.posZ, item));
				}
			}
		}
	}

	public static ItemStack changeMode(ItemStack stack, EntityPlayer player, boolean hasOblit, int maxSize) {
		if (player.isSneaking()) {
			if (ItemNBTHelper.getShort(stack, "size", (short) 0) < maxSize)
				ItemNBTHelper.setShort(stack, "size", (short) (ItemNBTHelper.getShort(stack, "size", (short) 0) + 1));
			else ItemNBTHelper.setShort(stack, "size", (short) 0);
			if (!player.worldObj.isRemote)
				player.addChatMessage(new ChatComponentTranslation("msg.size" + ItemNBTHelper.getShort(stack, "size", (short) 0) + ".txt"));

		} else {
			updateGhostBlocks(player, player.worldObj);
			if (hasOblit) {
				ItemNBTHelper.setBoolean(stack, "obliterate", !ItemNBTHelper.getBoolean(stack, "obliterate", false));
				if (player.worldObj.isRemote)
					player.addChatMessage(new ChatComponentTranslation("msg.oblit" + ItemNBTHelper.getBoolean(stack, "obliterate", false) + ".txt"));
			}
		}
		return stack;
	}

	public static void demageEntytyBasedOnHealth(Entity entity, EntityPlayer player, float dmg) {
		System.out.println(dmg);
		World world = player.worldObj;
		if (entity instanceof EntityLivingBase) {//entity.getEyeHeight() > 0) {
			float entHealth = ((EntityLivingBase) entity).getHealth();
			if (!world.isRemote) {
				if (entHealth > 20) {
					entity.attackEntityFrom(DamageSource.causePlayerDamage(player), (entHealth) * dmg);
				}
			}
		} else if (entity instanceof EntityDragonPart) {
			if (!world.isRemote) {
				System.out.println("part");
				entity.attackEntityFrom(DamageSource.causePlayerDamage(player), 200F * dmg);
			}
		} else {
			if (!world.isRemote) {
				System.out.println("Oter");
				entity.attackEntityFrom(DamageSource.causePlayerDamage(player), 100F * dmg);
			}
		}
	}

	@SuppressWarnings({"rawtypes", "unused"})
	public static void AOEAttack(EntityPlayer player, Entity entity, ItemStack stack, int dmg, int range) {
		Map enchants = EnchantmentHelper.getEnchantments(stack);
		int sharp = 0;
		int loot = 0;
		if (enchants.get(16) != null) sharp = (Integer) enchants.get(16);
		if (enchants.get(21) != null) loot = (Integer) enchants.get(21);
		World world = player.worldObj;
		AxisAlignedBB box = AxisAlignedBB.getAABBPool().getAABB(entity.posX - range, entity.posY - range, entity.posZ - range, entity.posX + range, entity.posY + range, entity.posZ + range).expand(1.0D, 1.0D, 1.0D);
		List list = world.getEntitiesWithinAABBExcludingEntity(player, box);
		for (Object o : list) {
			if (((Entity) o) instanceof EntityLivingBase)
				((Entity) o).attackEntityFrom(DamageSource.causePlayerDamage(player), dmg + sharp);

		}

	}

	public static MovingObjectPosition raytraceFromEntity (World world, Entity player, double range)
	{
		float f = 1.0F;
		float f1 = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * f;
		float f2 = player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * f;
		double d0 = player.prevPosX + (player.posX - player.prevPosX) * (double) f;
		double d1 = player.prevPosY + (player.posY - player.prevPosY) * (double) f;
		if (!world.isRemote && player instanceof EntityPlayer)
			d1 += 1.62D;
		double d2 = player.prevPosZ + (player.posZ - player.prevPosZ) * (double) f;
		Vec3 vec3 = world.getWorldVec3Pool().getVecFromPool(d0, d1, d2);
		float f3 = MathHelper.cos(-f2 * 0.017453292F - (float) Math.PI);
		float f4 = MathHelper.sin(-f2 * 0.017453292F - (float) Math.PI);
		float f5 = -MathHelper.cos(-f1 * 0.017453292F);
		float f6 = MathHelper.sin(-f1 * 0.017453292F);
		float f7 = f4 * f5;
		float f8 = f3 * f5;
		double d3 = range;
		if (player instanceof EntityPlayerMP)
		{
			d3 = ((EntityPlayerMP) player).theItemInWorldManager.getBlockReachDistance();
		}
		Vec3 vec31 = vec3.addVector((double) f7 * d3, (double) f6 * d3, (double) f8 * d3);
		return world.rayTraceBlocks(vec3, vec31);
	}

	private static void updateGhostBlocks(EntityPlayer player, World world)
	{
		int xPos = (int) player.posX;
		int yPos = (int) player.posY;
		int zPos = (int) player.posZ;

		for (int x = xPos - 6; x < xPos + 6; x++)
		{
			for (int y = yPos - 6; y < yPos + 6; y++)
			{
				for (int z = zPos - 6; z < zPos + 6; z++)
				{
					world.markBlockForUpdate(x, y, z);
				}
			}
		}
	}
}
