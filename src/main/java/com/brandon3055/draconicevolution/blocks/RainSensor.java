package com.brandon3055.draconicevolution.blocks;

import java.util.Random;

import javax.annotation.Nullable;

import com.brandon3055.brandonscore.blocks.BlockBCore;
import com.brandon3055.brandonscore.blocks.EntityBlockBCore;
import com.brandon3055.draconicevolution.init.DEContent;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Created by brandon3055 on 25/09/2016.
 */
public class RainSensor extends EntityBlockBCore {

    protected static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0, 1.0, 16.0);
    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");

    public RainSensor(Block.Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(ACTIVE, false));
        this.canProvidePower = true;
        setBlockEntity(() -> DEContent.tile_rain_sensor, true);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ACTIVE);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        context.getLevel().scheduleTick(context.getClickedPos(), this, 10);
        return super.getStateForPlacement(context);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void animateTick(BlockState stateIn, Level worldIn, BlockPos pos, Random rand) {
        if (stateIn.getValue(ACTIVE)) {
            worldIn.addParticle(DustParticleOptions.REDSTONE, pos.getX() + 0.1875 + (rand.nextBoolean() ? 0.625 : 0), pos.getY(), pos.getZ() + 0.1875 + (rand.nextBoolean() ? 0.625 : 0), 0, 0.0625, 0);
        }
    }
    
    @Override
    public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!isMoving && !state.is(newState.getBlock())) {
            BlockEntity tile = worldIn.getBlockEntity(pos);
            if (state.getValue(ACTIVE)) {
                this.updateNeighbors(state, worldIn, pos);
            }
            super.onRemove(state, worldIn, pos, newState, isMoving);
        }
    }

    private void updateNeighbors(BlockState state, Level world, BlockPos pos) {
        world.updateNeighborsAt(pos, this);
        world.updateNeighborsAt(pos.relative(Direction.DOWN), this);
    }
}
