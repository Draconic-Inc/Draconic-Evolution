package com.brandon3055.draconicevolution.blocks;

import codechicken.lib.raytracer.RayTracer;
import codechicken.lib.vec.Vector3;
import com.brandon3055.brandonscore.blocks.BlockBCore;
import com.brandon3055.draconicevolution.blocks.tileentity.TilePlacedItem;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * Created by brandon3055 on 25/07/2016.
 */
public class PlacedItem extends BlockBCore /*implements ITileEntityProvider, IRenderOverride*/ {

    public static final DirectionProperty FACING = BlockStateProperties.FACING;

    public PlacedItem(Properties properties) {
        super(properties);
        this.setDefaultState(stateContainer.getBaseState().with(FACING, Direction.UP));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    //region Block state and stuff...

    @Override
    public boolean uberIsBlockFullCube() {
        return false;
    }

    @Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {}


//    @Override
//    protected BlockStateContainer createBlockState() {
//        return new BlockStateContainer(this, FACING);
//    }
//
//    @Override
//    public BlockState getStateFromMeta(int meta) {
//        Direction enumfacing = Direction.getFront(meta);
//        return this.getDefaultState().withProperty(FACING, enumfacing);
//    }
//
//    @Override
//    public int getMetaFromState(BlockState state) {
//        return state.getValue(FACING).getIndex();
//    }
//
//    @Override
//    public BlockState withRotation(BlockState state, Rotation rot) {
//        return state.withProperty(FACING, rot.rotate(state.getValue(FACING)));
//    }
//
//    @Override
//    public BlockState withMirror(BlockState state, Mirror mirrorIn) {
//        return state.withRotation(mirrorIn.toRotation(state.getValue(FACING)));
//    }


    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new TilePlacedItem();
    }

    //endregion

    //region Render


    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.INVISIBLE;
    }

//    @Override
//    public boolean registerNormal(Feature feature) {
//        return false;
//    }
//
//    @OnlyIn(Dist.CLIENT)
//    @Override
//    public void registerRenderer(Feature feature) {
//        ClientRegistry.bindTileEntitySpecialRenderer(TilePlacedItem.class, new RenderTilePlacedItem());
//    }

    //endregion

    //region Interact

    @Override
    public boolean onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hitIIn) {
        if (world.isRemote) {
            return true;
        }

        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TilePlacedItem) {

            RayTraceResult hit = RayTracer.retraceBlock(world, player, pos);
            RayTraceResult subHitResult = null;//TODO RayTracer.rayTraceCuboidsClosest(new Vector3(RayTracer.getStartVec(player)), new Vector3(RayTracer.getEndVec(player)), pos, ((TilePlacedItem) tile).getIndexedCuboids());

            if (subHitResult != null) {
                hit = subHitResult;
            }
            else if (hit == null) {
                return true;
            }

            ((TilePlacedItem) tile).handleClick(hit.subHit, player);
        }
        return true;
    }

//    @Override
//    public AxisAlignedBB getBoundingBox(BlockState state, IBlockAccess source, BlockPos pos) {
//        if (!(source instanceof World)) {
//            return FULL_BLOCK_AABB;
//        }
//
//        TileEntity tile = source.getTileEntity(pos);
//
//        if (tile instanceof ICuboidProvider) {
//            return ((ICuboidProvider) tile).getIndexedCuboids().get(0).aabb();
////            return ((TilePlacedItem) tile).getBlockBounds().aabb();
//        }
//
//        return super.getBoundingBox(state, source, pos);
//    }

//    public RayTraceResult collisionRayTrace(BlockState state, World world, BlockPos pos, Vec3d start, Vec3d end) {
//        TileEntity tile = world.getTileEntity(pos);
//        if (tile instanceof ICuboidProvider) {
//            return RayTracer.rayTraceCuboidsClosest(start, end, pos, ((ICuboidProvider) tile).getIndexedCuboids());
//        }
//        return super.collisionRayTrace(state, world, pos, start, end);
//    }

//    @Override
//    public boolean rotateBlock(World world, BlockPos pos, Direction axis) {
//        return false;
//    }

    //endregion

    //region Harvest


    @Override
    public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TilePlacedItem) {

            RayTraceResult hit = target;
            RayTraceResult subHitResult = null;//TODO RayTracer.rayTraceCuboidsClosest(new Vector3(RayTracer.getStartVec(player)), new Vector3(RayTracer.getEndVec(player)), pos, ((TilePlacedItem) tile).getIndexedCuboids());

            if (subHitResult != null) {
                hit = subHitResult;
            }
            else if (hit == null) {
                return ItemStack.EMPTY;
            }

            if (hit.subHit > 0 && ((TilePlacedItem) tile).inventory.getStackInSlot(hit.subHit - 1) != null) {
                ItemStack stack = ((TilePlacedItem) tile).inventory.getStackInSlot(hit.subHit - 1).copy();
                if (stack.hasTag()) {
                    stack.getTag().remove("BlockEntityTag");
                }
                return stack;
            }
        }

        return ItemStack.EMPTY;
    }

    @Override
    public void harvestBlock(World world, PlayerEntity player, BlockPos pos, BlockState state, TileEntity te, ItemStack heldStack) {
    }

    @Override
    public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {
        TileEntity tile = world.getTileEntity(pos);

        if (tile instanceof TilePlacedItem) {
            ((TilePlacedItem) tile).breakBlock();
        }
        super.onReplaced(state, world, pos, newState, isMoving);
    }

    //endregion
}
