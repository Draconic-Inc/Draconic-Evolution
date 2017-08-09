package com.brandon3055.draconicevolution.blocks;

import com.brandon3055.brandonscore.blocks.BlockBCore;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

/**
 * Created by brandon3055 on 25/09/2016.
 */
public class RainSensor extends BlockBCore {

    protected static final AxisAlignedBB AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.0625D, 1.0D);
    public static final PropertyBool ACTIVE = PropertyBool.create("active");

    public RainSensor() {
        setDefaultState(blockState.getBaseState().withProperty(ACTIVE, false));
    }

    //region BlockState

    @Override
    public boolean uberIsBlockFullCube() {
        return false;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(ACTIVE, meta == 1);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(ACTIVE) ? 1 : 0;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, ACTIVE);
    }

    //endregion


    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return AABB;
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        world.scheduleUpdate(pos, this, 10);
        return super.getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, meta, placer, hand);
    }

    @Override
    public boolean canConnectRedstone(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return true;
    }

    @Override
    public int getStrongPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
        return blockState.getValue(ACTIVE) ? 15 : 0;
    }

    @Override
    public int getWeakPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
        return blockState.getValue(ACTIVE) ? 15 : 0;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        if (stateIn.getValue(ACTIVE)) {
            worldIn.spawnParticle(EnumParticleTypes.REDSTONE, pos.getX() + 0.1875 + (rand.nextBoolean() ? 0.625 : 0), pos.getY(), pos.getZ() + 0.1875 + (rand.nextBoolean() ? 0.625 : 0), 0, 0.0625, 0);
        }
        super.randomDisplayTick(stateIn, worldIn, pos, rand);
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        worldIn.scheduleUpdate(pos, this, 20);

        boolean raining = worldIn.isRaining() && worldIn.canSeeSky(pos);

        if (state.getValue(ACTIVE) != raining) {
            worldIn.setBlockState(pos, state.withProperty(ACTIVE, raining));
        }

        super.updateTick(worldIn, pos, state, rand);
    }
}
