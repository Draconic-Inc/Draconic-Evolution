package com.brandon3055.draconicevolution.common.blocks;

import com.brandon3055.draconicevolution.common.core.utills.InventoryUtils;
import com.brandon3055.draconicevolution.common.lib.References;
import com.google.common.collect.Lists;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class BlockDE extends Block {

	public BlockDE(final Material material) {
		super(material);

	}

	public BlockDE() {
		super(Material.rock);
	}
	//todo unify block resistance and hardness
	@Override
	public String getUnlocalizedName()
	{
		return String.format("tile.%s%s", References.MODID.toLowerCase() + ":", getUnwrappedUnlocalizedName(super.getUnlocalizedName()));
	}

	public String getUnwrappedUnlocalizedName(String unlocalizedName)
	{
		return unlocalizedName.substring(unlocalizedName.indexOf(".") + 1);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconRegister)
	{
		this.blockIcon = iconRegister.registerIcon(References.RESOURCESPREFIX + getUnwrappedUnlocalizedName(super.getUnlocalizedName()));
	}

	public boolean hasTileEntityDrops() {
		return false;
	}

	public boolean dropTileInventory() {return false;}

	@Override
	public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z, boolean willHarvest) {
		if (willHarvest && hasTileEntityDrops() && !player.capabilities.isCreativeMode) {
			final TileEntity te = world.getTileEntity(x, y, z);

			boolean result = super.removedByPlayer(world, player, x, y, z, willHarvest);

			if (result) {
				List<ItemStack> teDrops = Lists.newArrayList();
				getTileEntityDrops(te, teDrops, 0);
				for (ItemStack drop : teDrops)
					dropBlockAsItem(world, x, y, z, drop);
			}

			return result;
		}

		return super.removedByPlayer(world, player, x, y, z, willHarvest);
	}

	public void getTileEntityDrops(TileEntity te, List<ItemStack> result, int fortune) {
		if (te != null) {
			getTileInventoryDrops(te, result);
		}
	}

	public static boolean getTileInventoryDrops(TileEntity tileEntity, List<ItemStack> drops) {
		if (tileEntity == null) return false;

		if (tileEntity instanceof IInventory) {
			drops.addAll(InventoryUtils.getInventoryContents((IInventory)tileEntity));
			return true;
		}

		return false;
	}

	public boolean hasNormalDrops() {return true;}

	@Override
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
		ArrayList<ItemStack> result = Lists.newArrayList();
//		if (hasNormalDrops()) result.addAll(super.getDrops(world, x, y, z, metadata, fortune));
//		if (hasTileEntityDrops()) {
//			final TileEntity te = world.getTileEntity(x, y, z);
//			getTileEntityDrops(te, result, fortune);
//		}

		return result;
	}
}
