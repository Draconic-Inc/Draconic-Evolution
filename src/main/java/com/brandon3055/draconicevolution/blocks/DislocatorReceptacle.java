package com.brandon3055.draconicevolution.blocks;

import com.brandon3055.brandonscore.blocks.EntityBlockBCore;
import com.brandon3055.draconicevolution.blocks.tileentity.TileDislocatorReceptacle;
import com.brandon3055.draconicevolution.init.DEContent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

/**
 * Created by brandon3055 on 16/07/2016.
 */
public class DislocatorReceptacle extends EntityBlockBCore {

    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");
    public static final BooleanProperty CAMO = BooleanProperty.create("camo");

    public DislocatorReceptacle(Block.Properties properties) {
        super(properties);
        this.registerDefaultState(stateDefinition.any().setValue(ACTIVE, false).setValue(CAMO, false));
        setBlockEntity(DEContent.TILE_DISLOCATOR_RECEPTACLE::get, true);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ACTIVE, CAMO);
    }

    @Override
    public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        BlockEntity tile = worldIn.getBlockEntity(pos);

        if (tile instanceof TileDislocatorReceptacle && newState.getBlock() != state.getBlock()) {
            ((TileDislocatorReceptacle) tile).deactivate();
        }

        super.onRemove(state, worldIn, pos, newState, isMoving);
    }

    @Override
    public void onPlace(BlockState state, Level worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
        BlockEntity tile = worldIn.getBlockEntity(pos);

        if (tile instanceof TileDislocatorReceptacle && ((TileDislocatorReceptacle) tile).isActive()) {
            ((TileDislocatorReceptacle) tile).attemptActivation();
        }
    }
}
