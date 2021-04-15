package com.brandon3055.draconicevolution.blocks;

import com.brandon3055.brandonscore.blocks.BlockBCore;
import com.brandon3055.draconicevolution.blocks.tileentity.TilePotentiometer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * Created by brandon3055 on 25/09/2016.
 */
public class Potentiometer extends BlockBCore /*implements ITileEntityProvider, IRenderOverride*/ {

    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    protected static final VoxelShape AABB_DOWN = VoxelShapes.box(0.0625D, 0.9375D, 0.0625D, 0.9375D, 1.0D, 0.9375D);
    protected static final VoxelShape AABB_UP = VoxelShapes.box(0.0625D, 0.0D, 0.0625D, 0.9375D, 0.0625D, 0.9375D);
    protected static final VoxelShape AABB_NORTH = VoxelShapes.box(0.0625D, 0.0625D, 0.9375D, 0.9375D, 0.9375D, 1.0D);
    protected static final VoxelShape AABB_SOUTH = VoxelShapes.box(0.0625D, 0.0625D, 0.0D, 0.9375D, 0.9375D, 0.0625D);
    protected static final VoxelShape AABB_WEST = VoxelShapes.box(0.9375D, 0.0625D, 0.0625D, 1.0D, 0.9375D, 0.9375D);
    protected static final VoxelShape AABB_EAST = VoxelShapes.box(0.0D, 0.0625D, 0.0625D, 0.0625D, 0.9375D, 0.9375D);

    public Potentiometer(Block.Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.UP));
        this.canProvidePower = true;
    }

    @Override
    public boolean isBlockFullCube() {
        return false;
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new TilePotentiometer();
    }

    protected static boolean canPlaceBlock(World worldIn, BlockPos pos, Direction direction) {
        BlockPos blockpos = pos.relative(direction);
        return worldIn.getBlockState(blockpos).isFaceSturdy(worldIn, blockpos, direction.getOpposite());
    }

    @Override
    public void setPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(world, pos, state, placer, stack);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return canPlaceBlock(context.getLevel(), context.getClickedPos(), context.getClickedFace().getOpposite()) ? this.defaultBlockState().setValue(FACING, context.getClickedFace()) : this.defaultBlockState().setValue(FACING, Direction.DOWN);
    }

    @Override //TODO make sure this logic is not backwards
    public boolean canSurvive(BlockState state, IWorldReader worldIn, BlockPos pos) {
        return canSupportCenter(worldIn, pos.relative(state.getValue(FACING).getOpposite()), state.getValue(FACING));
    }

    @Override
    public void onRemove(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!isMoving && !state.is(newState.getBlock())) {
            TileEntity tile = worldIn.getBlockEntity(pos);
            if (tile instanceof TilePotentiometer && ((TilePotentiometer) tile).power.get() > 0) {
                this.updateNeighbors(state, worldIn, pos, (TilePotentiometer)tile);
            }

            super.onRemove(state, worldIn, pos, newState, isMoving);
        }
    }

    private void updateNeighbors(BlockState state, World world, BlockPos pos, TilePotentiometer tile) {
        world.updateNeighborsAt(pos, this);
        world.updateNeighborsAt(pos.relative(state.getValue(FACING).getOpposite()), this);
    }

    //    @Override
//    public void neighborChanged(BlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
//        if (this.checkForDrop(world, pos, state) && !canPlaceBlock(world, pos, ((Direction) state.get(FACING)).getOpposite())) {
//
//            this.dropBlockAsItem(world, pos, state, 0);
//            world.setBlockToAir(pos);
//        }
//    }

//    private boolean checkForDrop(World worldIn, BlockPos pos, BlockState state) {
//        if (this.canPlaceBlockAt(worldIn, pos)) {
//            return true;
//        }
//        else {
//            this.dropBlockAsItem(worldIn, pos, state, 0);
//            worldIn.setBlockToAir(pos);
//            return false;
//        }
//    }

//    @Override
//    public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, Direction side) {
//        return canPlaceBlock(worldIn, pos, side.getOpposite());
//    }
//
//    @Override
//    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
//        for (Direction enumfacing : Direction.values()) {
//            if (canPlaceBlock(worldIn, pos, enumfacing)) {
//                return true;
//            }
//        }
//
//        return false;
//    }


    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        Direction enumfacing = state.getValue(FACING);

        switch (enumfacing) {
            case EAST:
                return AABB_EAST;
            case WEST:
                return AABB_WEST;
            case SOUTH:
                return AABB_SOUTH;
            case UP:
                return AABB_UP;
            case DOWN:
                return AABB_DOWN;
            case NORTH:
            default:
                return AABB_NORTH;
        }
    }

    //endregion
}
