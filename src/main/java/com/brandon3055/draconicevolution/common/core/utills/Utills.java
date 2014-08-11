package com.brandon3055.draconicevolution.common.core.utills;

import com.brandon3055.draconicevolution.common.blocks.BlockDE;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import java.util.List;

/**
 * Created by Brandon on 25/07/2014.
 */
public class Utills {

	public static String formatNumber(double value){
		if (value < 1000D)
			return String.valueOf(value);
		else if (value < 1000000D)
			return String.valueOf(Math.round(value)/1000D) + "K";
		else if (value < 1000000000D)
			return String.valueOf(Math.round(value/1000D)/1000D) + "M";
		else if (value < 1000000000000D)
			return String.valueOf(Math.round(value/1000000D)/1000D) + "B";
		else
			return String.valueOf(Math.round(value/1000000000D)/1000D) + "T";
	}

	public static class BlockChanger extends Block{

		public BlockDE block;

		public BlockChanger(String name, BlockDE block) {
			super(Material.rock);
			this.setBlockName(name);
			this.block = block;
			GameRegistry.registerBlock(this, BlockChangerItem.class ,name);
		}

		@Override
		public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer p_149727_5_, int p_149727_6_, float p_149727_7_, float p_149727_8_, float p_149727_9_) {
			LogHelper.info(world.getBlock(x, y, z).getUnlocalizedName());
			return false;
		}

		@Override
		public boolean hasTileEntity(int metadata) {
			return true;
		}

		@Override
		public TileEntity createTileEntity(World world, int metadata) {
			return new TileBlockChanger(block);
		}
	}

	public static class TileBlockChanger extends TileEntity {

		BlockDE block;
		public TileBlockChanger(BlockDE block) {
			this.block = block;
		}

		@Override
		public void updateEntity() {
			if (worldObj.isRemote) {
				worldObj.removeTileEntity(xCoord, yCoord, zCoord);
				return;
			}
			LogHelper.info("Changing block at [X:"+xCoord+" Y:"+yCoord+" Z:"+zCoord+"] from "+worldObj.getBlock(xCoord,yCoord,zCoord).getUnlocalizedName()+" to "+block.getUnlocalizedName());
			worldObj.setBlock(xCoord, yCoord, zCoord, block);
			worldObj.removeTileEntity(xCoord,yCoord,zCoord);
		}
	}

	public static class BlockChangerItem extends ItemBlock {

		private BlockChanger block;
		public BlockChangerItem(Block block) {
			super(block);
			if (block instanceof BlockChanger) this.block = (BlockChanger)block;
		}

		@Override
		public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean p_77663_5_) {
			if (entity instanceof EntityPlayer && block.block != null){
				EntityPlayer player = (EntityPlayer)entity;
				player.inventory.setInventorySlotContents(slot, new ItemStack(Item.getItemFromBlock(block.block), stack.stackSize));
			}
		}

		@SuppressWarnings("unchecked")
		@Override
		public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean p_77624_4_) {
			list.add("This block was broken by the update");
			list.add("Place it in your inventory to fix it");
		}
	}
}
