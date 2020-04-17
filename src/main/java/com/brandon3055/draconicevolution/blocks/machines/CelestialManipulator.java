package com.brandon3055.draconicevolution.blocks.machines;

import com.brandon3055.brandonscore.blocks.BlockBCore;
import com.brandon3055.draconicevolution.blocks.tileentity.TileCelestialManipulator;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;

/**
 * Created by brandon3055 on 25/09/2016.
 */
public class CelestialManipulator extends BlockBCore /*implements ITileEntityProvider, IRenderOverride*/ {//Replacement For both the weather controller and sun dial

    private VoxelShape SHAPE = Block.makeCuboidShape(1.0, 0, 1.0, 15.0, 13.0, 15.0);

    public CelestialManipulator(Properties properties) {
        super(properties);
        canProvidePower = true;
    }

    @Override
    public boolean isBlockFullCube() {
        return false;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return SHAPE;
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new TileCelestialManipulator();
    }
//
//    @Override
//    public boolean onBlockActivated(World worldIn, BlockPos pos, BlockState state, PlayerEntity playerIn, Hand hand, Direction side, float hitX, float hitY, float hitZ) {
//        if (!worldIn.isRemote) {
//            FMLNetworkHandler.openGui(playerIn, DraconicEvolution.instance, GuiHandler.GUIID_CELESTIAL, worldIn, pos.getX(), pos.getY(), pos.getZ());
//        }
//        return true;
//    }

//    @OnlyIn(Dist.CLIENT)
//    @Override
//    public void registerRenderer(Feature feature) {
//        ClientRegistry.bindTileEntitySpecialRenderer(TileCelestialManipulator.class, new RenderTileCelestialManipulator());
//    }
//
//    @Override
//    public boolean registerNormal(Feature feature) {
//        return true;
//    }
}
