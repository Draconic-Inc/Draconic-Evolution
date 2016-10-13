package com.brandon3055.draconicevolution.blocks.machines;

import com.brandon3055.brandonscore.blocks.BlockBCore;
import com.brandon3055.draconicevolution.blocks.tileentity.TileStabilizedSpawner;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by brandon3055 on 25/09/2016.
 */
public class StabilizedSpawner extends BlockBCore implements ITileEntityProvider {

    public StabilizedSpawner() {
        setIsFullCube(false);
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileStabilizedSpawner();
    }

    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT;
    }
}
