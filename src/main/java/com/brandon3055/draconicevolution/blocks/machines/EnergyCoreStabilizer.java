package com.brandon3055.draconicevolution.blocks.machines;

import com.brandon3055.brandonscore.blocks.BlockBCore;
import com.brandon3055.draconicevolution.blocks.tileentity.TileEnergyCore;
import com.brandon3055.draconicevolution.blocks.tileentity.TileEnergyCoreStabilizer;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.*;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

/**
 * Created by brandon3055 on 30/3/2016.
 */
public class EnergyCoreStabilizer extends BlockBCore {

    public static final BooleanProperty LARGE = BooleanProperty.create("large");
    public static VoxelShape SHAPE = makeCuboidShape(0.98, 0.98, 0.98, 15.02, 15.02, 15.02);
    public static VoxelShape SHAPE_X = VoxelShapes.create(0, -1, -1, 1, 2, 2);
    public static VoxelShape SHAPE_Y = VoxelShapes.create(-1, 0, -1, 2, 1, 2);
    public static VoxelShape SHAPE_Z = VoxelShapes.create(-1, -1, 0, 2, 2, 1);

    public EnergyCoreStabilizer(Properties properties) {
        super(properties);
        this.setDefaultState(stateContainer.getBaseState().with(LARGE, false));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(LARGE);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return state.get(LARGE) ? BlockRenderType.INVISIBLE : BlockRenderType.MODEL;
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    //endregion

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new TileEnergyCoreStabilizer();
    }


    @Override
    public int getLightValue(BlockState state) {
        return 10;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return VoxelShapes.fullCube();//VoxelShapes.empty();//super.getCollisionShape(state, worldIn, pos, context);
    }

    @Override
    public VoxelShape getRaytraceShape(BlockState state, IBlockReader worldIn, BlockPos pos) {
        return VoxelShapes.empty();//super.getRaytraceShape(state, worldIn, pos);
    }

    @Override
    public VoxelShape getRenderShape(BlockState state, IBlockReader worldIn, BlockPos pos) {
        return VoxelShapes.empty();//super.getRenderShape(state, worldIn, pos);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
        double px = 1 / 16D;

//        SHAPE = makeCuboidShape(0.98, 0.98, 0.98, 15.02, 15.02, 15.02);
//        SHAPE_X = VoxelShapes.create(0, -1, -1, 1, 2, 2);
//        SHAPE_Y = VoxelShapes.create(-1, 0, -1, 2, 1, 2);
//        SHAPE_Z = VoxelShapes.create(-1, -1, 0, 2, 2, 1);

        TileEntity tile = world.getTileEntity(pos);
//
        if (tile instanceof TileEnergyCoreStabilizer) {
            if (((TileEnergyCoreStabilizer) tile).isValidMultiBlock.get()) {
                switch (((TileEnergyCoreStabilizer) tile).multiBlockAxis.get()) {
                    case X:
                        return SHAPE_X;
                    case Y:
                        return SHAPE_Y;
                    case Z:
                        return SHAPE_Z;
                }
            }
        }

        return SHAPE;//VoxelShapes.empty();//super.getShape(state, worldIn, pos, context);
    }

    //endregion

    //region Place/Break stuff

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        TileEntity tile = world.getTileEntity(pos);

        if (tile instanceof TileEnergyCoreStabilizer) {
            ((TileEnergyCoreStabilizer) tile).onPlaced();
        } else {
            super.onBlockPlacedBy(world, pos, state, placer, stack);
        }
    }

    @Override
    public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {
        TileEntity tile = world.getTileEntity(pos);

        if (tile instanceof TileEnergyCoreStabilizer) {
            if (((TileEnergyCoreStabilizer) tile).isValidMultiBlock.get()) {
                ((TileEnergyCoreStabilizer) tile).deFormStructure();
            }
            TileEnergyCore core = ((TileEnergyCoreStabilizer) tile).getCore();

            if (core != null) {
                world.removeTileEntity(pos);
                ((TileEnergyCoreStabilizer) tile).validateStructure();
            }

        }
        super.onReplaced(state, world, pos, newState, isMoving);
    }


    //endregion

}
