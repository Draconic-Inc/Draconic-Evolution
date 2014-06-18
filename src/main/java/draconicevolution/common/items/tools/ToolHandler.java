package draconicevolution.common.items.tools;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import draconicevolution.common.core.helper.ItemNBTHelper;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings.GameType;

public class ToolHandler {
	public static Material[] materialsPick = { Material.anvil, Material.circuits, Material.coral, Material.glass, Material.ice, Material.iron, Material.rock };
	public static Material[] materialsShovel = { Material.clay, Material.ground, Material.grass, Material.sand, Material.snow, Material.craftedSnow };
	public static Material[] materialsAxe = { Material.cactus, Material.leaves, Material.wood, Material.plants };
	public static Material[] materialsDStaff = { Material.anvil, Material.circuits, Material.coral, Material.glass, Material.ice, Material.iron, Material.rock, Material.clay, Material.ground, Material.grass, Material.sand, Material.snow, Material.craftedSnow, Material.cactus, Material.leaves, Material.wood, Material.plants };
	public static Block[] destroyList = { Blocks.cobblestone, Blocks.stone, Blocks.dirt, Blocks.gravel, Blocks.sand, Blocks.grass, Blocks.netherrack };

	public static boolean isRightMaterial(final Material material, final Material[] materialsListing)
	{
		for (final Material mat : materialsListing) {
			if (material == mat) {
				return true;
			}
		}
		return false;
	}

	public static boolean checkDestroyList(Block curBlock)
	{
		for (Block block : destroyList) {
			if (curBlock == block) {
				return true;
			}
		}
		return false;
	}

	public static void disSquare(int x, int y, int z, final EntityPlayer player, final World world, final boolean silk, final int fortune, Material[] materialsListing, ItemStack stack)
	{
		int size = ItemNBTHelper.getShort(stack, "size", (short)0);
		int dyrection = getBlockFace(player, x, y, z);
		int sizeX = size;
		int sizeY = size;
		int sizeZ = size;
		int yOff = (size * -1);
		if (size > 0)
			yOff++;
		if (ItemNBTHelper.getShort(stack, "size", (short)0) == 0)
			return;
		switch (dyrection) {
		case 1:
			sizeX = 0;
			break;
		case 2:
			sizeY = 0;
			yOff = 0;
			break;
		case 3:
			sizeZ = 0;
			break;
		}
		for (int x1 = x - sizeX; x1 <= x + sizeX; x1++) {
			for (int y1 = y - (sizeY + yOff); y1 <= y + (sizeY - yOff); y1++) {
				for (int z1 = z - sizeZ; z1 <= z + sizeZ; z1++) {
					if ((x1 != x) || (y1 != y) || (z1 != z))
						mineBlock(x1, y1, z1, player, world, silk, fortune, materialsListing, stack);
				}
			}
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void mineBlock(final int x, final int y, final int z, final EntityPlayer player, final World world, final boolean silk, final int fortune, Material[] materialsListing, ItemStack stack)
	{
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
				else
					items.add(new ItemStack(block.getItem(world, x, y, z), 1, meta));
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

	public static int getBlockFace(EntityPlayer player, int blockX, int blockY, int blockZ)
	{
		double playerX = player.posX;
		double playerY = player.posY;
		if (!player.worldObj.isRemote)
			playerY += 1.62D;
		if (player.isSneaking())
			playerY += -1.62;
		double playerZ = player.posZ;
		double xx = Math.abs(playerX - (blockX + 0.5));
		double yy = Math.abs(playerY - (blockY + 0.5));
		double zz = Math.abs(playerZ - (blockZ + 0.5));
		if ((xx > yy) && (xx > zz))
			return 1;
		else if ((yy > xx) && (yy > zz))
			return 2;
		else
			return 3;
	}

	public static ItemStack changeMode(ItemStack stack, EntityPlayer player, boolean hasOblit, int maxSize)
	{
		if (player.isSneaking()) {
			if (ItemNBTHelper.getShort(stack, "size", (short)0) < maxSize)
				ItemNBTHelper.setShort(stack, "size", (short) (ItemNBTHelper.getShort(stack, "size", (short) 0) + 1));
			else
				ItemNBTHelper.setShort(stack, "size", (short) 0);
			if (!player.worldObj.isRemote)
				player.addChatMessage(new ChatComponentTranslation("msg.size" + ItemNBTHelper.getShort(stack, "size", (short)0) + ".txt"));

		} else {
			if (hasOblit) {
				ItemNBTHelper.setBoolean(stack, "obliterate", !ItemNBTHelper.getBoolean(stack, "obliterate", false));
				if (player.worldObj.isRemote)
					player.addChatMessage(new ChatComponentTranslation("msg.oblit" + ItemNBTHelper.getBoolean(stack, "obliterate", false) + ".txt"));
			}
		}
		return stack;
	}

	public static void demageEntytyBasedOnHealth(Entity entity, EntityPlayer player, float dmg)
	{
		World world = player.worldObj;
		if (entity.getEyeHeight() > 0) {
			float entHealth = ((EntityLivingBase) entity).getHealth();
			if (!world.isRemote) {
				if (entHealth > 20) {
					entity.attackEntityFrom(DamageSource.causePlayerDamage(player), (entHealth) * dmg);
				}
			}
		}
	}

	@SuppressWarnings({ "rawtypes", "unused" })
	public static void AOEAttack(EntityPlayer player, Entity entity, ItemStack stack, int dmg, int range)
	{
		Map enchants = EnchantmentHelper.getEnchantments(stack);
		int sharp = 0;
		int loot = 0;
		if (enchants.get(16) != null)
			sharp = (Integer) enchants.get(16);
		if (enchants.get(21) != null)
			loot = (Integer) enchants.get(21);
		World world = player.worldObj;
		AxisAlignedBB box = AxisAlignedBB.getAABBPool().getAABB(entity.posX - range, entity.posY - range, entity.posZ - range, entity.posX + range, entity.posY + range, entity.posZ + range).expand(1.0D, 1.0D, 1.0D);
		List list = world.getEntitiesWithinAABBExcludingEntity(player, box);
		for (Object o : list) {
			if (((Entity) o) instanceof EntityLivingBase)
				((Entity) o).attackEntityFrom(DamageSource.causePlayerDamage(player), dmg + sharp);

		}

	}
}
