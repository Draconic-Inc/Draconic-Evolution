package com.brandon3055.draconicevolution.blocks;

import com.brandon3055.brandonscore.blocks.BlockBCore;
import com.brandon3055.brandonscore.blocks.EntityBlockBCore;
import com.brandon3055.draconicevolution.blocks.tileentity.TilePotentiometer;
import com.brandon3055.draconicevolution.init.DEContent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

/**
 * Created by brandon3055 on 25/09/2016.
 */
public class Potentiometer extends EntityBlockBCore {

    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    protected static final VoxelShape AABB_DOWN = Shapes.box(0.0625D, 0.9375D, 0.0625D, 0.9375D, 1.0D, 0.9375D);
    protected static final VoxelShape AABB_UP = Shapes.box(0.0625D, 0.0D, 0.0625D, 0.9375D, 0.0625D, 0.9375D);
    protected static final VoxelShape AABB_NORTH = Shapes.box(0.0625D, 0.0625D, 0.9375D, 0.9375D, 0.9375D, 1.0D);
    protected static final VoxelShape AABB_SOUTH = Shapes.box(0.0625D, 0.0625D, 0.0D, 0.9375D, 0.9375D, 0.0625D);
    protected static final VoxelShape AABB_WEST = Shapes.box(0.9375D, 0.0625D, 0.0625D, 1.0D, 0.9375D, 0.9375D);
    protected static final VoxelShape AABB_EAST = Shapes.box(0.0D, 0.0625D, 0.0625D, 0.0625D, 0.9375D, 0.9375D);

    public Potentiometer(Block.Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.UP));
        this.canProvidePower = true;
        setBlockEntity(() -> DEContent.tile_potentiometer, false);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    protected static boolean canPlaceBlock(Level worldIn, BlockPos pos, Direction direction) {
        BlockPos blockpos = pos.relative(direction);
        return worldIn.getBlockState(blockpos).isFaceSturdy(worldIn, blockpos, direction.getOpposite());
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return canPlaceBlock(context.getLevel(), context.getClickedPos(), context.getClickedFace().getOpposite()) ? this.defaultBlockState().setValue(FACING, context.getClickedFace()) : this.defaultBlockState().setValue(FACING, Direction.DOWN);
    }

    @Override //TODO make sure this logic is not backwards
    public boolean canSurvive(BlockState state, LevelReader worldIn, BlockPos pos) {
        return canSupportCenter(worldIn, pos.relative(state.getValue(FACING).getOpposite()), state.getValue(FACING));
    }

    @Override
    public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!isMoving && !state.is(newState.getBlock())) {
            BlockEntity tile = worldIn.getBlockEntity(pos);
            if (tile instanceof TilePotentiometer && ((TilePotentiometer) tile).power.get() > 0) {
                this.updateNeighbors(state, worldIn, pos);
            }

            super.onRemove(state, worldIn, pos, newState, isMoving);
        }
    }

    private void updateNeighbors(BlockState state, Level world, BlockPos pos) {
        world.updateNeighborsAt(pos, this);
        world.updateNeighborsAt(pos.relative(state.getValue(FACING).getOpposite()), this);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
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
