package com.brandon3055.draconicevolution.blocks;

import com.brandon3055.brandonscore.blocks.BlockBCore;
import com.brandon3055.draconicevolution.blocks.tileentity.TileDislocatorPedestal;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * Created by brandon3055 on 25/09/2016.
 */
public class DislocatorPedestal extends BlockBCore/* implements ITileEntityProvider, IRenderOverride*/ {
    protected static final VoxelShape SHAPE = Block.makeCuboidShape(5.6f, 0f, 5.6f, 10.4f, 12.8f, 10.4f);

    public DislocatorPedestal(Properties properties) {
        super(properties);
    }

    @Override
    public boolean uberIsBlockFullCube() {
        return false;
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileDislocatorPedestal) {
            float f = (float) MathHelper.floor((MathHelper.wrapDegrees(placer.rotationYaw - 180.0F) + 11.25F) / 22.5F);
            ((TileDislocatorPedestal) tile).rotation.set((int) f);
            if (!world.isRemote) {
                ((TileDislocatorPedestal) tile).getDataManager().forceSync();
            }
        }
        super.onBlockPlacedBy(world, pos, state, placer, stack);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new TileDislocatorPedestal();
    }

    @Override
    public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
//        TileEntity tileEntity = worldIn.getTileEntity(pos);

//        if (tileEntity instanceof TileDislocatorPedestal) {//TODO switch to tile interface
//            return ((TileDislocatorPedestal) tileEntity).onBlockActivated(player);
//        }
        return super.onBlockActivated(state, worldIn, pos, player, handIn, hit);
    }


    //region Render


    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return SHAPE;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.INVISIBLE;
    }
//
//    @OnlyIn(Dist.CLIENT)
//    @Override
//    public void registerRenderer(Feature feature) {
//        ClientRegistry.bindTileEntitySpecialRenderer(TileDislocatorPedestal.class, new RenderTileDislocatorPedestal());
//    }
//
//    @Override
//    public boolean registerNormal(Feature feature) {
//        return true;
//    }

    //endregion
}
