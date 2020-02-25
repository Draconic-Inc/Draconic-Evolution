package com.brandon3055.draconicevolution.blocks.reactor;

import com.brandon3055.brandonscore.blocks.BlockBCore;
import com.brandon3055.draconicevolution.blocks.reactor.tileentity.TileReactorCore;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * Created by brandon3055 on 6/11/2016.
 */
public class ReactorCore extends BlockBCore /*implements ITileEntityProvider, IRenderOverride*/ {

    private static final VoxelShape NO_AABB = VoxelShapes.create(0.5, 0.5, 0.5, 0.5, 0.5, 0.5);

    public ReactorCore(Properties properties) {
        super(properties);
    }

    @Override
    public boolean uberIsBlockFullCube() {
        return false;
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return false;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new TileReactorCore();
    }

    @Override
    public float getBlockHardness(BlockState blockState, IBlockReader world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileReactorCore) {
            return ((TileReactorCore) tile).reactorState.get().isShieldActive() ? -1F : super.getBlockHardness(blockState, world, pos);
        }

        return super.getBlockHardness(blockState, world, pos);
    }

    @Override
    public float getExplosionResistance(BlockState state, IWorldReader world, BlockPos pos, @Nullable Entity exploder, Explosion explosion) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileReactorCore) {
            return ((TileReactorCore) tile).reactorState.get().isShieldActive() ? 6000000.0F : super.getExplosionResistance(state, world, pos, exploder, explosion);
        }

        return super.getExplosionResistance(state, world, pos, exploder, explosion);
    }

    //region Rendering


    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.INVISIBLE;
    }

//    @OnlyIn(Dist.CLIENT)
//    @Override
//    public void registerRenderer(Feature feature) {
//        ClientRegistry.bindTileEntitySpecialRenderer(TileReactorCore.class, new RenderTileReactorCore());
//        ModelRegistryHelper.registerItemRenderer(Item.getItemFromBlock(this), new RenderItemReactorComponent());
//    }
//
//    @Override
//    public boolean registerNormal(Feature feature) {
//        return false;
//    }


//    @Override
//    public VoxelShape getRaytraceShape(BlockState state, IBlockReader worldIn, BlockPos pos) {
//        return super.getRaytraceShape(state, worldIn, pos);
//    }
//
//    @Override
//    public AxisAlignedBB getSelectedBoundingBox(BlockState state, World worldIn, BlockPos pos) {
//        return NO_AABB;
//    }
//
//    //endregion
//
//    @Nullable
//    public AxisAlignedBB getCollisionBoundingBox(BlockState blockState, World worldIn, BlockPos pos) {
//        TileEntity tile = worldIn.getTileEntity(pos);
//
//        if (tile instanceof TileReactorCore && ((TileReactorCore) tile).reactorState.get().isShieldActive()) {
//            return NULL_AABB;
//        }
//
//        return super.getCollisionBoundingBox(blockState, worldIn, pos);
//    }


    @Override
    public void onBlockExploded(BlockState state, World world, BlockPos pos, Explosion explosion) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileReactorCore && ((TileReactorCore) tile).reactorState.get().isShieldActive()) {
            return;
        }
        super.onBlockExploded(state, world, pos, explosion);
    }

//    @Nullable
//    @Override
//    public RayTraceResult collisionRayTrace(BlockState blockState, World worldIn, BlockPos pos, Vec3d start, Vec3d end) {
//        RayTraceResult result = super.collisionRayTrace(blockState, worldIn, pos, start, end);
//
//        if (result != null && result.typeOfHit == RayTraceResult.Type.BLOCK) {
//            TileEntity tile = worldIn.getTileEntity(pos);
//            if (tile instanceof TileReactorCore && ((TileReactorCore) tile).reactorState.get().isShieldActive()) {
//                result = new RayTraceResult(RayTraceResult.Type.MISS, result.hitVec, result.sideHit, pos);
//            }
//        }
//        return result;
//    }

}
