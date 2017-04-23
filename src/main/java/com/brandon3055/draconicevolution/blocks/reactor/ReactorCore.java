package com.brandon3055.draconicevolution.blocks.reactor;

import codechicken.lib.model.ModelRegistryHelper;
import com.brandon3055.brandonscore.blocks.BlockBCore;
import com.brandon3055.brandonscore.config.Feature;
import com.brandon3055.brandonscore.config.ICustomRender;
import com.brandon3055.draconicevolution.blocks.reactor.tileentity.TileReactorCore;
import com.brandon3055.draconicevolution.client.render.item.RenderItemReactorComponent;
import com.brandon3055.draconicevolution.client.render.tile.RenderTileReactorCore;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

/**
 * Created by brandon3055 on 6/11/2016.
 */
public class ReactorCore extends BlockBCore implements ITileEntityProvider, ICustomRender {

    private static final AxisAlignedBB NO_AABB = new AxisAlignedBB(0.5, 0.5, 0.5, 0.5, 0.5, 0.5);

    public ReactorCore() {
        setIsFullCube(false);
    }


    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileReactorCore();
    }

    @Override
    public float getBlockHardness(IBlockState blockState, World world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileReactorCore) {
            return ((TileReactorCore) tile).reactorState.value.isShieldActive() ? -1 : super.getBlockHardness(blockState, world, pos);
        }

        return super.getBlockHardness(blockState, world, pos);
    }

    @Override
    public float getExplosionResistance(World world, BlockPos pos, Entity exploder, Explosion explosion) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileReactorCore) {
            return ((TileReactorCore) tile).reactorState.value.isShieldActive() ? 6000000.0F : super.getExplosionResistance(world, pos, exploder, explosion);
        }

        return super.getExplosionResistance(world, pos, exploder, explosion);
    }

    //region Rendering

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.INVISIBLE;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerRenderer(Feature feature) {
        ClientRegistry.bindTileEntitySpecialRenderer(TileReactorCore.class, new RenderTileReactorCore());
        ModelRegistryHelper.registerItemRenderer(Item.getItemFromBlock(this), new RenderItemReactorComponent());
    }

    @Override
    public boolean registerNormal(Feature feature) {
        return false;
    }

    @Override
    public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World worldIn, BlockPos pos) {
        return NO_AABB;
    }

    //endregion

    @Nullable
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, World worldIn, BlockPos pos) {
        TileEntity tile = worldIn.getTileEntity(pos);

        if (tile instanceof TileReactorCore && ((TileReactorCore) tile).reactorState.value.isShieldActive()) {
            return NULL_AABB;
        }

        return super.getCollisionBoundingBox(blockState, worldIn, pos);
    }

    @Override
    public void onBlockExploded(World world, BlockPos pos, Explosion explosion) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileReactorCore && ((TileReactorCore) tile).reactorState.value.isShieldActive()) {
            return;
        }

        super.onBlockExploded(world, pos, explosion);
    }

    @Nullable
    @Override
    public RayTraceResult collisionRayTrace(IBlockState blockState, World worldIn, BlockPos pos, Vec3d start, Vec3d end) {
        RayTraceResult result = super.collisionRayTrace(blockState, worldIn, pos, start, end);

        if (result != null && result.typeOfHit == RayTraceResult.Type.BLOCK) {
            TileEntity tile = worldIn.getTileEntity(pos);
            if (tile instanceof TileReactorCore && ((TileReactorCore) tile).reactorState.value.isShieldActive()) {
                result = new RayTraceResult(RayTraceResult.Type.MISS, result.hitVec, result.sideHit, pos);
            }
        }
        return result;
    }

}
