package com.brandon3055.draconicevolution.common.blocks;

import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.common.core.utills.ItemNBTHelper;
import com.brandon3055.draconicevolution.common.core.utills.LogHelper;
import com.brandon3055.draconicevolution.common.items.ModItems;
import com.brandon3055.draconicevolution.common.lib.Strings;
import com.brandon3055.draconicevolution.common.tileentities.TileCustomSpawner;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.Random;

/**
 * Created by Brandon on 5/07/2014.
 */
public class CustomSpawner extends BlockDE{
	public CustomSpawner()
	{
		this.setBlockName(Strings.customSpawnerName);
		this.setCreativeTab(DraconicEvolution.tolkienTabBlocksItems);
		this.setHardness(10F);
		this.setResistance(2000F);
		this.setHarvestLevel("pickaxe", 3);
		ModBlocks.register(this);
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int p_149727_6_, float p_149727_7_, float p_149727_8_, float p_149727_9_) {
		TileCustomSpawner spawner = (world.getTileEntity(x, y, z) != null && world.getTileEntity(x, y, z) instanceof TileCustomSpawner) ? (TileCustomSpawner)world.getTileEntity(x, y, z) : null;
		if (spawner != null)
		{
			ItemStack item = player.getHeldItem();
			if (item != null && item.getItem().equals(ModItems.mobSoul))
			{
				String name = ItemNBTHelper.getString(item, "Name", "Pig");
				if (name.equals(spawner.getBaseLogic().entityName)){return false;}
				spawner.getBaseLogic().entityName = name;
				spawner.isSetToSpawn = true;
				world.markBlockForUpdate(x, y, z);
				item.splitStack(1);
				return true;
			}else if (item != null && item.getItem().equals(Items.nether_star) && spawner.getBaseLogic().requiresPlayer){
				spawner.getBaseLogic().requiresPlayer = false;
				world.markBlockForUpdate(x, y, z);
				item.splitStack(1);
				return true;
			}else if (item != null && item.getItem().equals(ModItems.infusedCompound) && spawner.getBaseLogic().spawnSpeed == 0){
				spawner.getBaseLogic().setSpawnRate(1);
				world.markBlockForUpdate(x, y, z);
				item.splitStack(1);
				return true;
			}else if (item != null && item.getItem().equals(ModItems.draconicCompound) && spawner.getBaseLogic().spawnSpeed == 1){
				spawner.getBaseLogic().setSpawnRate(2);
				world.markBlockForUpdate(x, y, z);
				item.splitStack(1);
				return true;
			}else if (item != null && item.getItem().equals(Item.getItemFromBlock(Blocks.dragon_egg)) && spawner.getBaseLogic().spawnSpeed == 2){
				spawner.getBaseLogic().setSpawnRate(3);
				world.markBlockForUpdate(x, y, z);
				item.splitStack(1);
				return true;
			}else if (item != null && item.getItem().equals(Items.golden_apple) && item.getItemDamage() == 1 && !spawner.getBaseLogic().ignoreSpawnRequirements){
				spawner.getBaseLogic().ignoreSpawnRequirements = true;
				world.markBlockForUpdate(x, y, z);
				item.splitStack(1);
				return true;
			}else
			{
				if (world.isRemote && !player.isSneaking()) {
					player.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "#################################"));
					player.addChatMessage(new ChatComponentTranslation("msg.spawnerInfo1.txt").appendText(": " + EnumChatFormatting.DARK_AQUA + spawner.getBaseLogic().entityName));
					player.addChatMessage(new ChatComponentTranslation("msg.spawnerInfo2.txt").appendText(": " + EnumChatFormatting.DARK_AQUA + spawner.getBaseLogic().requiresPlayer));
					player.addChatMessage(new ChatComponentTranslation("msg.spawnerInfo3.txt").appendText(": " + EnumChatFormatting.DARK_AQUA + spawner.getBaseLogic().ignoreSpawnRequirements));
					player.addChatMessage(new ChatComponentTranslation("msg.spawnerInfo4.txt").appendText(": " + EnumChatFormatting.DARK_AQUA + spawner.getBaseLogic().spawnSpeed));
					player.addChatMessage(new ChatComponentTranslation("msg.spawnerInfo5.txt"));
					player.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "#################################"));
				}else if (world.isRemote && player.isSneaking())
				{
					player.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "#################################"));
					player.addChatMessage(new ChatComponentTranslation("msg.spawnerInfo6.txt"));
					player.addChatMessage(new ChatComponentTranslation("msg.spawnerInfo7.txt"));
					player.addChatMessage(new ChatComponentTranslation("msg.spawnerInfo8.txt"));
					player.addChatMessage(new ChatComponentTranslation("msg.spawnerInfo9.txt"));
					player.addChatMessage(new ChatComponentTranslation("msg.spawnerInfo10.txt"));
					player.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "#################################"));
				}
				return true;
			}
		}
		LogHelper.error("Invalid or nonexistent TileEntity");
		return false;
	}

	@Override
	public boolean hasTileEntity(int metadata) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(World world, int metadata) {
		return new TileCustomSpawner();
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public int getHarvestLevel(int metadata) {
		return 4;
	}

	@Override
	public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
		return true;
	}

	@Override
	public Item getItemDropped(int p_149650_1_, Random p_149650_2_, int p_149650_3_) {
		return Item.getItemFromBlock(ModBlocks.customSpawner);
	}

	@Override
	public int quantityDropped(Random p_149745_1_) {
		return 1;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister iconRegister) {
		blockIcon = iconRegister.registerIcon("mob_spawner");
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block p_149695_5_) {
		TileCustomSpawner spawner = (world.getTileEntity(x, y, z) != null && world.getTileEntity(x, y, z) instanceof TileCustomSpawner) ? (TileCustomSpawner)world.getTileEntity(x, y, z) : null;
		if (spawner != null)
		{
			spawner.getBaseLogic().powered = world.isBlockIndirectlyGettingPowered(x, y, z);
			world.markBlockForUpdate(x, y, z);
			spawner.getBaseLogic().setSpawnRate(spawner.getBaseLogic().spawnSpeed);
		}
	}

	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z) {
		return new ItemStack(ModBlocks.customSpawner);
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block p_149749_5_, int p_149749_6_) {
		TileCustomSpawner spawner = (world.getTileEntity(x, y, z) != null && world.getTileEntity(x, y, z) instanceof TileCustomSpawner) ? (TileCustomSpawner)world.getTileEntity(x, y, z) : null;
		if (spawner != null && !world.isRemote)
		{
			float multiplyer = 0.05F;

			if (spawner.getBaseLogic().ignoreSpawnRequirements)
			{
				EntityItem item = new EntityItem(world, x + 0.5, y + 0.5, z + 0.5, new ItemStack(Items.golden_apple, 1, 1));
				item.motionX = (-0.5F + world.rand.nextFloat()) * multiplyer;
				item.motionY = (4 + world.rand.nextFloat()) * multiplyer;
				item.motionZ = (-0.5F + world.rand.nextFloat()) * multiplyer;
				world.spawnEntityInWorld(item);
			}
			if (spawner.getBaseLogic().spawnSpeed > 0)
			{
				EntityItem item = new EntityItem(world, x + 0.5, y + 0.5, z + 0.5, new ItemStack(ModItems.infusedCompound));
				item.motionX = (-0.5F + world.rand.nextFloat()) * multiplyer;
				item.motionY = (4 + world.rand.nextFloat()) * multiplyer;
				item.motionZ = (-0.5F + world.rand.nextFloat()) * multiplyer;
				world.spawnEntityInWorld(item);
			}
			if (spawner.getBaseLogic().spawnSpeed > 1)
			{
				EntityItem item = new EntityItem(world, x + 0.5, y + 0.5, z + 0.5, new ItemStack(ModItems.draconicCompound));
				item.motionX = (-0.5F + world.rand.nextFloat()) * multiplyer;
				item.motionY = (4 + world.rand.nextFloat()) * multiplyer;
				item.motionZ = (-0.5F + world.rand.nextFloat()) * multiplyer;
				world.spawnEntityInWorld(item);
			}
			if (spawner.getBaseLogic().spawnSpeed > 2)
			{
				EntityItem item = new EntityItem(world, x + 0.5, y + 0.5, z + 0.5, new ItemStack(Item.getItemFromBlock(Blocks.dragon_egg)));
				item.motionX = (-0.5F + world.rand.nextFloat()) * multiplyer;
				item.motionY = (4 + world.rand.nextFloat()) * multiplyer;
				item.motionZ = (-0.5F + world.rand.nextFloat()) * multiplyer;
				world.spawnEntityInWorld(item);
			}
			if (!spawner.getBaseLogic().requiresPlayer)
			{
				EntityItem item = new EntityItem(world, x + 0.5, y + 0.5, z + 0.5, new ItemStack(Items.nether_star));
				item.motionX = (-0.5F + world.rand.nextFloat()) * multiplyer;
				item.motionY = (4 + world.rand.nextFloat()) * multiplyer;
				item.motionZ = (-0.5F + world.rand.nextFloat()) * multiplyer;
				world.spawnEntityInWorld(item);
			}
		}
		super.breakBlock(world, x, y, z, p_149749_5_, p_149749_6_);
	}
}
