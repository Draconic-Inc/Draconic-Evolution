package com.brandon3055.draconicevolution.blocks.machines;

import com.brandon3055.brandonscore.blocks.BlockBCore;
import com.brandon3055.brandonscore.blocks.EntityBlockBCore;
import com.brandon3055.draconicevolution.blocks.tileentity.TileEnergyCore;
import com.brandon3055.draconicevolution.init.DEContent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;

/**
 * Created by brandon3055 on 30/3/2016.
 */
public class EnergyCore extends EntityBlockBCore {

    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");

    public EnergyCore(Properties properties) {
        super(properties);
        this.registerDefaultState(stateDefinition.any().setValue(ACTIVE, false));
        dontSpawnOnMe();
        setBlockEntity(() -> DEContent.tile_storage_core, true);
        setLightTransparent();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ACTIVE);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return state.getValue(ACTIVE) ? RenderShape.INVISIBLE : RenderShape.MODEL;
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (level.getBlockEntity(pos) instanceof TileEnergyCore tile && (!state.is(newState.getBlock()) || !newState.hasBlockEntity())) {
            tile.onRemoved();
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }
}
