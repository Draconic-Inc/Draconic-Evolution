package com.brandon3055.draconicevolution.common.blocks.multiblock;

import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.common.blocks.DraconicEvolutionBlock;
import com.brandon3055.draconicevolution.common.blocks.ModBlocks;
import com.brandon3055.draconicevolution.common.core.utills.LogHelper;
import com.brandon3055.draconicevolution.common.core.utills.Utills;
import com.brandon3055.draconicevolution.common.lib.References;
import com.brandon3055.draconicevolution.common.lib.Strings;
import com.brandon3055.draconicevolution.common.tileentities.multiblocktiles.TileEnergyStorageCore;
import com.brandon3055.draconicevolution.common.tileentities.multiblocktiles.TileInvisibleMultiblock;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Created by Brandon on 25/07/2014.
 */
public class EnergyStorageCore extends DraconicEvolutionBlock {

	public EnergyStorageCore() {
		super(Material.iron);
		this.setHardness(10F);
		this.setResistance(100F);
		this.setCreativeTab(DraconicEvolution.getCreativeTab(2));
		this.setBlockName(Strings.energyStorageCoreName);
		ModBlocks.register(this);
	}


	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconRegister) {
		blockIcon = iconRegister.registerIcon(References.RESOURCESPREFIX + "energy_storage_core");
	}

	@Override
	public boolean hasTileEntity(int metadata) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(World world, int metadata) {
		return new TileEnergyStorageCore();
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int p_149727_6_, float p_149727_7_, float p_149727_8_, float p_149727_9_) {
		TileEnergyStorageCore tile = (world.getTileEntity(x, y, z) != null && world.getTileEntity(x, y, z) instanceof TileEnergyStorageCore) ? (TileEnergyStorageCore) world.getTileEntity(x, y, z) : null;
		if (tile == null)
		{
			LogHelper.error("Missing Tile Entity (EnergyStorageCore)");
			return false;
		}

		if (!world.isRemote) {
			player.addChatComponentMessage(new ChatComponentText("Tier:" + tile.getTier()));
			player.addChatComponentMessage(new ChatComponentText("" + Utills.formatNumber(tile.getEnergyStored()) + " / " + Utills.formatNumber(tile.getMaxEnergyStored())));
		}
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int side) {
		TileEnergyStorageCore tile = (world.getTileEntity(x - ForgeDirection.getOrientation(side).offsetX, y - ForgeDirection.getOrientation(side).offsetY, z - ForgeDirection.getOrientation(side).offsetZ) != null && world.getTileEntity(x - ForgeDirection.getOrientation(side).offsetX, y - ForgeDirection.getOrientation(side).offsetY, z - ForgeDirection.getOrientation(side).offsetZ) instanceof TileEnergyStorageCore) ? (TileEnergyStorageCore) world.getTileEntity(x - ForgeDirection.getOrientation(side).offsetX, y - ForgeDirection.getOrientation(side).offsetY, z - ForgeDirection.getOrientation(side).offsetZ) : null;
		//LogHelper.error(world.getTileEntity(x - ForgeDirection.getOrientation(side).offsetX, y - ForgeDirection.getOrientation(side).offsetY, z - ForgeDirection.getOrientation(side).offsetZ));
		if (tile == null)
		{
			LogHelper.error("Missing Tile Entity (EnergyStorageCore)(shouldSideBeRendered)");
			return true;
		}
		if (tile.isOnline())
			return false;
		else
			return super.shouldSideBeRendered(world, x, y, z, side);
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block p_149695_5_) {
		TileEnergyStorageCore thisTile = (world.getTileEntity(x, y, z) != null && world.getTileEntity(x, y, z) instanceof TileEnergyStorageCore) ? (TileEnergyStorageCore) world.getTileEntity(x, y, z) : null;
		if (thisTile != null && thisTile.isOnline() && thisTile.getTier() == 0)
		{
			thisTile.isStructureStillValid(false);
		}
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block p_149749_5_, int p_149749_6_) {
		TileEnergyStorageCore thisTile = (world.getTileEntity(x, y, z) != null && world.getTileEntity(x, y, z) instanceof TileEnergyStorageCore) ? (TileEnergyStorageCore) world.getTileEntity(x, y, z) : null;
		if (thisTile != null && thisTile.isOnline() && thisTile.getTier() == 0)
		{
			thisTile.deactivateStabilizers();
		}
		super.breakBlock(world, x, y, z, p_149749_5_, p_149749_6_);
	}

	@Override
	public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
		TileEnergyStorageCore thisTile = (world.getTileEntity(x, y, z) != null && world.getTileEntity(x, y, z) instanceof TileEnergyStorageCore) ? (TileEnergyStorageCore) world.getTileEntity(x, y, z) : null;
		if (thisTile != null && thisTile.isOnline())
		{
			return AxisAlignedBB.getBoundingBox(thisTile.xCoord + 0.5, thisTile.yCoord + 0.5, thisTile.zCoord + 0.5, thisTile.xCoord + 0.5, thisTile.yCoord + 0.5, thisTile.zCoord + 0.5);
		}
		return super.getSelectedBoundingBoxFromPool(world, x, y, z);
	}
}
