package com.brandon3055.draconicevolution.blocks;

import com.brandon3055.brandonscore.blocks.BlockBCore;
import com.brandon3055.draconicevolution.blocks.tileentity.TilePortal;
import com.brandon3055.draconicevolution.blocks.tileentity.TilePortalClient;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

import static net.minecraft.util.Direction.Axis.X;

/**
 * Created by brandon3055 on 16/07/2016.
 */
public class Portal extends BlockBCore {

    public static final EnumProperty<Direction.Axis> AXIS = EnumProperty.create("axis", Direction.Axis.class);
    public static final BooleanProperty DRAW_UP = BooleanProperty.create("drawup");
    public static final BooleanProperty DRAW_DOWN = BooleanProperty.create("drawdown");
    public static final BooleanProperty DRAW_EAST = BooleanProperty.create("draweast");
    public static final BooleanProperty DRAW_WEST = BooleanProperty.create("drawwest");
    public static final BooleanProperty VISIBLE = BooleanProperty.create("visible");

    public Portal(Block.Properties properties) {
        super(properties);
//        setHardness(Float.MAX_VALUE);
        this.setDefaultState(stateContainer.getBaseState() //
                .with(AXIS, X) //
                .with(DRAW_UP, true) //
                .with(DRAW_DOWN, true) //
                .with(DRAW_EAST, true) //
                .with(DRAW_WEST, true) //
                .with(VISIBLE, true));
    }

    @Override
    public boolean uberIsBlockFullCube() {
        return false;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(AXIS, DRAW_UP, DRAW_DOWN, DRAW_EAST, DRAW_WEST, VISIBLE);
    }

//    //region Block State
//
//    @Override
//    public int getMetaFromState(BlockState state) {
//        return state.getValue(AXIS) == X ? 0 : state.getValue(AXIS) == Y ? 1 : 2;
//    }
//
//    @Override
//    public BlockState getStateFromMeta(int meta) {
//        return getDefaultState().withProperty(AXIS, meta == 0 ? X : meta == 1 ? Y : Z);
//    }
//
//    @Override
//    protected BlockStateContainer createBlockState() {
//        return new BlockStateContainer(this, AXIS, DRAW_UP, DRAW_DOWN, DRAW_EAST, DRAW_WEST, VISIBLE);
//    }
//
//    @Override
//    public BlockState getActualState(BlockState state, IBlockAccess world, BlockPos pos) {
//        Direction.Axis axix = state.getValue(AXIS);
//        BlockState north = world.getBlockState(pos.add(0, 0, -1));
//        BlockState south = world.getBlockState(pos.add(0, 0, 1));
//        BlockState east = world.getBlockState(pos.add(1, 0, 0));
//        BlockState west = world.getBlockState(pos.add(-1, 0, 0));
//        BlockState up = world.getBlockState(pos.add(0, 1, 0));
//        BlockState down = world.getBlockState(pos.add(0, -1, 0));
//
//
//        boolean visible = true;
//        TileEntity tile = world.getTileEntity(pos);
//        if (tile instanceof TilePortal) {
//            TileDislocatorReceptacle receptacle = ((TilePortal) tile).getMaster();
//            visible = receptacle == null || receptacle.hiddenTime == 0;
//            ((TilePortal) tile).disabled = !visible;
//        }
//        state = state.withProperty(VISIBLE, visible);
//
//
//        switch (axix) {
//            case X:
//                return state.withProperty(DRAW_UP, up.getBlock() != this).withProperty(DRAW_DOWN, down.getBlock() != this).withProperty(DRAW_EAST, south.getBlock() != this).withProperty(DRAW_WEST, north.getBlock() != this);
//            case Y:
//                return state.withProperty(DRAW_UP, south.getBlock() != this).withProperty(DRAW_DOWN, north.getBlock() != this).withProperty(DRAW_EAST, east.getBlock() != this).withProperty(DRAW_WEST, west.getBlock() != this);
//            case Z:
//                return state.withProperty(DRAW_UP, up.getBlock() != this).withProperty(DRAW_DOWN, down.getBlock() != this).withProperty(DRAW_EAST, east.getBlock() != this).withProperty(DRAW_WEST, west.getBlock() != this);
//        }
//
//        return state;
//    }
//
//    @Override
//    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, BlockState state, int fortune) {
//    }
//
//    @Override
//    public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, BlockState state, float chance, int fortune) {
//    }
//
//    @Override
//    public void harvestBlock(World world, PlayerEntity player, BlockPos pos, BlockState state, TileEntity te, ItemStack heldStack) {
//    }

    //endregion

    //region Portal Logic


    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return world instanceof ClientWorld ? new TilePortalClient() : new TilePortal();
    }

//    @Override
//    public AxisAlignedBB getSelectedBoundingBox(BlockState state, World worldIn, BlockPos pos) {
//        return new AxisAlignedBB(0.5, 0.5, 0.5, 0.5, 0.5, 0.5);
//    }

//    @Nullable
//    @Override
//    public AxisAlignedBB getCollisionBoundingBox(BlockState blockState, IBlockAccess worldIn, BlockPos pos) {
//        return NULL_AABB;//new AxisAlignedBB(0.5, 0.5, 0.5, 0.5, 0.5, 0.5);
//    }

    @Override
    public void neighborChanged(BlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        if (world.isRemote) {
            return;
        }

        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TilePortal) {
            ((TilePortal) tile).validatePortal();
        } else {
            world.removeBlock(pos, false);
        }
        super.neighborChanged(state, world, pos, blockIn, fromPos, isMoving);
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TilePortal && ((TilePortal) tile).getMaster() != null) {
            ((TilePortal) tile).getMaster().handleEntityTeleport(entity);
        }
    }

    //endregion
}
