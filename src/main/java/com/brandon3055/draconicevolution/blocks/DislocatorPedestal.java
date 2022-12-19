package com.brandon3055.draconicevolution.blocks;

import com.brandon3055.brandonscore.blocks.BlockBCore;
import com.brandon3055.brandonscore.blocks.EntityBlockBCore;
import com.brandon3055.draconicevolution.blocks.tileentity.TileDislocatorPedestal;
import com.brandon3055.draconicevolution.init.DEContent;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Created by brandon3055 on 25/09/2016.
 */
public class DislocatorPedestal extends EntityBlockBCore {
    protected static final VoxelShape SHAPE = Block.box(5.6f, 0f, 5.6f, 10.4f, 12.8f, 10.4f);

    public DislocatorPedestal(Properties properties) {
        super(properties);
        setBlockEntity(() -> DEContent.tile_dislocator_pedestal, false);
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        BlockEntity tile = world.getBlockEntity(pos);
        if (tile instanceof TileDislocatorPedestal) {
            float f = (float) Mth.floor((Mth.wrapDegrees(placer.getYRot() - 180.0F) + 11.25F) / 22.5F);
            ((TileDislocatorPedestal) tile).rotation.set((int) f);
            if (!world.isClientSide) {
                ((TileDislocatorPedestal) tile).getDataManager().forceSync();
            }
        }
        super.setPlacedBy(world, pos, state, placer, stack);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }
}
