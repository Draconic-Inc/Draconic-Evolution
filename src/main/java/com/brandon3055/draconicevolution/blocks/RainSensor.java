package com.brandon3055.draconicevolution.blocks;

import com.brandon3055.brandonscore.blocks.BlockBCore;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.Random;

/**
 * Created by brandon3055 on 25/09/2016.
 */
public class RainSensor extends BlockBCore {

    protected static final VoxelShape SHAPE = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0, 1.0, 16.0);
    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");

    public RainSensor(Block.Properties properties) {
        super(properties);
        setDefaultState(stateContainer.getBaseState().with(ACTIVE, false));
        canProvidePower = true;
    }

    @Override
    public boolean isBlockFullCube() {
        return false;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
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
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return SHAPE;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        context.getWorld().getPendingBlockTicks().scheduleTick(context.getPos(), this, 10);
        return super.getStateForPlacement(context);
    }

    @Override
    public int getStrongPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
        return blockState.get(ACTIVE) ? 15 : 0;
    }

    @Override
    public int getWeakPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
        return blockState.get(ACTIVE) ? 15 : 0;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        if (stateIn.get(ACTIVE)) {
            worldIn.addParticle(RedstoneParticleData.REDSTONE_DUST, pos.getX() + 0.1875 + (rand.nextBoolean() ? 0.625 : 0), pos.getY(), pos.getZ() + 0.1875 + (rand.nextBoolean() ? 0.625 : 0), 0, 0.0625, 0);
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
