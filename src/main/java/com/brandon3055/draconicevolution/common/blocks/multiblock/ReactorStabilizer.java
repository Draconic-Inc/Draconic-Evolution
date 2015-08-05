package com.brandon3055.draconicevolution.common.blocks.multiblock;

import com.brandon3055.brandonscore.common.utills.Utills;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.common.ModBlocks;
import com.brandon3055.draconicevolution.common.blocks.BlockDE;
import com.brandon3055.draconicevolution.common.lib.References;
import com.brandon3055.draconicevolution.common.tileentities.multiblocktiles.reactor.TileReactorCore;
import com.brandon3055.draconicevolution.common.tileentities.multiblocktiles.reactor.TileReactorStabilizer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Created by Brandon on 5/7/2015.
 */
public class ReactorStabilizer extends BlockDE {
	public ReactorStabilizer() {
		this.setCreativeTab(DraconicEvolution.tabBlocksItems);
		this.setBlockName("reactorStabilizer");

		ModBlocks.register(this);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister iconRegister) {
		blockIcon = iconRegister.registerIcon(References.RESOURCESPREFIX + "transparency");
	}

	@Override
	public int getRenderType() {
		return -1;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean hasTileEntity(int metadata) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(World world, int metadata) {
		return new TileReactorStabilizer();
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack stack) {
		int d = Utills.determineOrientation(x, y, z, entity);
		TileReactorStabilizer tile = world.getTileEntity(x, y, z) instanceof TileReactorStabilizer ? (TileReactorStabilizer)world.getTileEntity(x, y, z) : null;
		if (tile != null){
			tile.facingDirection = ForgeDirection.getOrientation(d).getOpposite().ordinal();
			tile.onPlaced();
		}
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block p_149749_5_, int p_149749_6_) {
		TileReactorStabilizer tile = world.getTileEntity(x, y, z) instanceof TileReactorStabilizer ? (TileReactorStabilizer)world.getTileEntity(x, y, z) : null;
		TileEntity core = null;
		if (tile != null) core = tile.getMaster().getTileEntity(world);
		super.breakBlock(world, x, y, z, p_149749_5_, p_149749_6_);
		if (core instanceof TileReactorCore) ((TileReactorCore) core).validateStructure();
	}
}
