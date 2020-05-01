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
    protected static final VoxelShape AABB_DOWN = VoxelShapes.create(0.0625D, 0.9375D, 0.0625D, 0.9375D, 1.0D, 0.9375D);
    protected static final VoxelShape AABB_UP = VoxelShapes.create(0.0625D, 0.0D, 0.0625D, 0.9375D, 0.0625D, 0.9375D);
    protected static final VoxelShape AABB_NORTH = VoxelShapes.create(0.0625D, 0.0625D, 0.9375D, 0.9375D, 0.9375D, 1.0D);
    protected static final VoxelShape AABB_SOUTH = VoxelShapes.create(0.0625D, 0.0625D, 0.0D, 0.9375D, 0.9375D, 0.0625D);
    protected static final VoxelShape AABB_WEST = VoxelShapes.create(0.9375D, 0.0625D, 0.0625D, 1.0D, 0.9375D, 0.9375D);
    protected static final VoxelShape AABB_EAST = VoxelShapes.create(0.0D, 0.0625D, 0.0625D, 0.0625D, 0.9375D, 0.9375D);

    public Potentiometer(Block.Properties properties) {
        super(properties);
        setDefaultState(stateContainer.getBaseState().with(FACING, Direction.UP));
        this.canProvidePower = true;
//        this.setHardness(2F);
    }

    @Override
    public boolean isBlockFullCube() {
        return false;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    //region Blockstate

//    public BlockState getStateFromMeta(int meta) {
//        Direction enumfacing;
//
//        switch (meta) {
//            case 0:
//                enumfacing = Direction.DOWN;
//                break;
//            case 1:
//                enumfacing = Direction.EAST;
//                break;
//            case 2:
//                enumfacing = Direction.WEST;
//                break;
//            case 3:
//                enumfacing = Direction.SOUTH;
//                break;
//            case 4:
//                enumfacing = Direction.NORTH;
//                break;
//            case 5:
//            default:
//                enumfacing = Direction.UP;
//        }
//
//        return this.getDefaultState().withProperty(FACING, enumfacing);
//    }
//
//    public int getMetaFromState(BlockState state) {
//        int i;
//
//        switch (state.getValue(FACING)) {
//            case EAST:
//                i = 1;
//                break;
//            case WEST:
//                i = 2;
//                break;
//            case SOUTH:
//                i = 3;
//                break;
//            case NORTH:
//                i = 4;
//                break;
//            case UP:
//            default:
//                i = 5;
//                break;
//            case DOWN:
//                i = 0;
//        }
//
//        return i;
//    }
//
//    @Override
//    protected BlockStateContainer createBlockState() {
//        return new BlockStateContainer(this, FACING);
//    }
//
//    @Override
//    public BlockState getActualState(BlockState state, IBlockAccess worldIn, BlockPos pos) {
//        return super.getActualState(state, worldIn, pos);
//    }

    //endregion

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new TilePotentiometer();
    }

    //region place

    protected static boolean canPlaceBlock(World worldIn, BlockPos pos, Direction direction) {
        BlockPos blockpos = pos.offset(direction);
        return worldIn.getBlockState(blockpos).isSolidSide(worldIn, blockpos, direction.getOpposite());
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        super.onBlockPlacedBy(world, pos, state, placer, stack);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return canPlaceBlock(context.getWorld(), context.getPos(), context.getFace().getOpposite()) ? this.getDefaultState().with(FACING, context.getFace()) : this.getDefaultState().with(FACING, Direction.DOWN);
    }

    @Override //TODO make sure this logic is not backwards
    public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
        return hasEnoughSolidSide(worldIn, pos.offset(state.get(FACING).getOpposite()), state.get(FACING));
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

    //endregion

    //region render

//    @OnlyIn(Dist.CLIENT)
//    @Override
//    public void registerRenderer(Feature feature) {
//        ClientRegistry.bindTileEntitySpecialRenderer(TilePotentiometer.class, new RenderTilePotentiometer());
//    }
//
//    @Override
//    public boolean registerNormal(Feature feature) {
//        return true;
//    }


    @Override
    public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return VoxelShapes.empty();
    }

//    @Nullable
//    public AxisAlignedBB getCollisionBoundingBox(BlockState blockState, World worldIn, BlockPos pos) {
//        return NULL_AABB;
//    }


    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        Direction enumfacing = state.get(FACING);

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
