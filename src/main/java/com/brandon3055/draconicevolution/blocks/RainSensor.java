package com.brandon3055.draconicevolution.blocks;

import com.brandon3055.brandonscore.blocks.BlockBCore;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.Random;

/**
 * Created by brandon3055 on 25/09/2016.
 */
public class RainSensor extends BlockBCore {

    protected static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0, 1.0, 16.0);
    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");

    public RainSensor(Block.Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(ACTIVE, false));
        canProvidePower = true;
    }


    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ACTIVE);
    }

    //region BlockState

//    @Override
//    public BlockState getStateFromMeta(int meta) {
//        return getDefaultState().withProperty(ACTIVE, meta == 1);
//    }
//
//    @Override
//    public int getMetaFromState(BlockState state) {
//        return state.getValue(ACTIVE) ? 1 : 0;
//    }
//
//    @Override
//    protected BlockStateContainer createBlockState() {
//        return new BlockStateContainer(this, ACTIVE);
//    }

    //endregion

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

    @Override
    public int getDirectSignal(BlockState blockState, BlockGetter blockAccess, BlockPos pos, Direction side) {
        return blockState.getValue(ACTIVE) ? 15 : 0;
    }

    @Override
    public int getSignal(BlockState blockState, BlockGetter blockAccess, BlockPos pos, Direction side) {
        return blockState.getValue(ACTIVE) ? 15 : 0;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void animateTick(BlockState stateIn, Level worldIn, BlockPos pos, Random rand) {
        if (stateIn.getValue(ACTIVE)) {
            worldIn.addParticle(DustParticleOptions.REDSTONE, pos.getX() + 0.1875 + (rand.nextBoolean() ? 0.625 : 0), pos.getY(), pos.getZ() + 0.1875 + (rand.nextBoolean() ? 0.625 : 0), 0, 0.0625, 0);
        }
    }

//    @Override
//    public boolean ticksRandomly(BlockState state) {
//        return super.ticksRandomly(state);
//    }
//
//    @Override
//    public void tick(BlockState state, World worldIn, BlockPos pos, Random random) {
//        super.tick(state, worldIn, pos, random);
//    }


    //    @OnlyIn(Dist.CLIENT)
//    @Override
//    public void randomDisplayTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {

//        super.randomDisplayTick(stateIn, worldIn, pos, rand);
//    }
//
//    @Override
//    public void updateTick(World worldIn, BlockPos pos, BlockState state, Random rand) {
//        worldIn.scheduleUpdate(pos, this, 20);
//
//        boolean raining = worldIn.isRaining() && worldIn.canSeeSky(pos);
//
//        if (state.getValue(ACTIVE) != raining) {
//            worldIn.setBlockState(pos, state.withProperty(ACTIVE, raining));
//        }
//
//        super.updateTick(worldIn, pos, state, rand);
//    }
}
