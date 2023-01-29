package com.brandon3055.draconicevolution.common.blocks.multiblock;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.common.ModBlocks;
import com.brandon3055.draconicevolution.common.blocks.BlockDE;
import com.brandon3055.draconicevolution.common.lib.References;
import com.brandon3055.draconicevolution.common.tileentities.multiblocktiles.reactor.TileReactorCore;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Created by Brandon on 16/6/2015.
 */
public class ReactorCore extends BlockDE {

    public ReactorCore() {
        this.setCreativeTab(DraconicEvolution.tabBlocksItems);
        this.setBlockName("reactorCore");
        this.setHardness(100F);

        ModBlocks.register(this);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(IIconRegister iconRegister) {
        blockIcon = iconRegister.registerIcon(References.RESOURCESPREFIX + "transparency");
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
    public int getRenderType() {
        return -1;
    }

    @Override
    public boolean hasTileEntity(int metadata) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, int metadata) {
        return new TileReactorCore();
    }

    @Override
    public void onBlockAdded(World world, int x, int y, int z) {
        TileReactorCore tile = world.getTileEntity(x, y, z) instanceof TileReactorCore
                ? (TileReactorCore) world.getTileEntity(x, y, z)
                : null;
        if (tile != null) tile.onPlaced();
    }

    @Override
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World p_149633_1_, int x, int y, int z) {

        return AxisAlignedBB.getBoundingBox(0, 0, 0, 1, 1, 1); // super.getSelectedBoundingBoxFromPool(p_149633_1_, x,
                                                               // y, z);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int p_149727_6_,
            float p_149727_7_, float p_149727_8_, float p_149727_9_) {
        // TileReactorCore tile = world.getTileEntity(x, y, z) instanceof TileReactorCore ? (TileReactorCore)
        // world.getTileEntity(x, y, z) : null;
        // if (tile != null) return tile.onStructureRightClicked(player);
        return false;
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block p_149749_5_, int p_149749_6_) {
        TileReactorCore tile = world.getTileEntity(x, y, z) instanceof TileReactorCore
                ? (TileReactorCore) world.getTileEntity(x, y, z)
                : null;
        if (tile != null) tile.onBroken();
        super.breakBlock(world, x, y, z, p_149749_5_, p_149749_6_);
    }
}
