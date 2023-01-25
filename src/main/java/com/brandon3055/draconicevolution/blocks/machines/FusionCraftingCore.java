package com.brandon3055.draconicevolution.blocks.machines;

import com.brandon3055.brandonscore.blocks.BlockBCore;
import com.brandon3055.brandonscore.blocks.EntityBlockBCore;
import com.brandon3055.draconicevolution.blocks.tileentity.TileFusionCraftingCore;
import com.brandon3055.draconicevolution.init.DEContent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Created by brandon3055 on 11/06/2016.
 */
public class FusionCraftingCore extends EntityBlockBCore {

    private static final VoxelShape SHAPE = Shapes.box(0.0625, 0.0625, 0.0625, 0.9375, 0.9375, 0.9375);

    public FusionCraftingCore(Properties properties) {
        super(properties);
        setBlockEntity(() -> DEContent.tile_crafting_core, true);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public void neighborChanged(BlockState state, Level world, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        if (!world.isClientSide()) {
            if (isBlockPowered(world, pos)) {
                BlockEntity tile = world.getBlockEntity(pos);
                if (tile instanceof TileFusionCraftingCore) {
                    ((TileFusionCraftingCore) tile).startCraft();
                }
            }
        }
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState blockState, Level worldIn, BlockPos pos) {
        BlockEntity tile = worldIn.getBlockEntity(pos);
        if (tile instanceof TileFusionCraftingCore) {
            return ((TileFusionCraftingCore) tile).getComparatorOutput();
        }
        return super.getAnalogOutputSignal(blockState, worldIn, pos);
    }
}
