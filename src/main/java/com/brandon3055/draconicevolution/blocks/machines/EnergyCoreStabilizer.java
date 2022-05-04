package com.brandon3055.draconicevolution.blocks.machines;

import com.brandon3055.brandonscore.blocks.BlockBCore;
import com.brandon3055.draconicevolution.blocks.tileentity.TileEnergyCore;
import com.brandon3055.draconicevolution.blocks.tileentity.TileEnergyCoreStabilizer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

/**
 * Created by brandon3055 on 30/3/2016.
 */
public class EnergyCoreStabilizer extends BlockBCore {

    public static final BooleanProperty LARGE = BooleanProperty.create("large");
    public static VoxelShape SHAPE = box(0.98, 0.98, 0.98, 15.02, 15.02, 15.02);
    public static VoxelShape SHAPE_X = VoxelShapes.box(0, -1, -1, 1, 2, 2);
    public static VoxelShape SHAPE_Y = VoxelShapes.box(-1, 0, -1, 2, 1, 2);
    public static VoxelShape SHAPE_Z = VoxelShapes.box(-1, -1, 0, 2, 2, 1);

    public EnergyCoreStabilizer(Properties properties) {
        super(properties);
        this.registerDefaultState(stateDefinition.any().setValue(LARGE, false));
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(LARGE);
    }

    @Override
    public BlockRenderType getRenderShape(BlockState state) {
        return state.getValue(LARGE) ? BlockRenderType.INVISIBLE : BlockRenderType.MODEL;
    }

//    @Override
//    public BlockRenderLayer getRenderLayer() {
//        return BlockRenderLayer.CUTOUT;
//    }

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
    public int getLightValue(BlockState state, IBlockReader world, BlockPos pos) {
        return 10;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return VoxelShapes.block();//VoxelShapes.empty();//super.getCollisionShape(state, worldIn, pos, context);
    }

    @Override
    public VoxelShape getInteractionShape(BlockState state, IBlockReader worldIn, BlockPos pos) {
        return VoxelShapes.empty();//super.getRaytraceShape(state, worldIn, pos);
    }

    @Override
    public VoxelShape getOcclusionShape(BlockState state, IBlockReader worldIn, BlockPos pos) {
        return VoxelShapes.empty();//super.getRenderShape(state, worldIn, pos);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
        double px = 1 / 16D;

//        SHAPE = makeCuboidShape(0.98, 0.98, 0.98, 15.02, 15.02, 15.02);
//        SHAPE_X = VoxelShapes.create(0, -1, -1, 1, 2, 2);
//        SHAPE_Y = VoxelShapes.create(-1, 0, -1, 2, 1, 2);
//        SHAPE_Z = VoxelShapes.create(-1, -1, 0, 2, 2, 1);

        TileEntity tile = world.getBlockEntity(pos);
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
    public void setPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        TileEntity tile = world.getBlockEntity(pos);

        if (tile instanceof TileEnergyCoreStabilizer) {
            ((TileEnergyCoreStabilizer) tile).onPlaced();
        } else {
            super.setPlacedBy(world, pos, state, placer, stack);
        }
    }

    @Override
    public void onRemove(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {
        TileEntity tile = world.getBlockEntity(pos);

        if (tile instanceof TileEnergyCoreStabilizer) {
            if (((TileEnergyCoreStabilizer) tile).isValidMultiBlock.get()) {
                ((TileEnergyCoreStabilizer) tile).deFormStructure();
            }
            TileEnergyCore core = ((TileEnergyCoreStabilizer) tile).getCore();

            if (core != null) {
                world.removeBlockEntity(pos);
                ((TileEnergyCoreStabilizer) tile).validateStructure();
            }

        }
        super.onRemove(state, world, pos, newState, isMoving);
    }


    //endregion

}
