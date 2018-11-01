package com.brandon3055.draconicevolution.blocks;

import com.brandon3055.brandonscore.blocks.BlockBCore;
import com.brandon3055.draconicevolution.blocks.tileentity.TileDislocatorReceptacle;
import com.brandon3055.draconicevolution.blocks.tileentity.TilePortal;
import com.brandon3055.draconicevolution.blocks.tileentity.TilePortalClient;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;

import static net.minecraft.util.EnumFacing.Axis.*;

/**
 * Created by brandon3055 on 16/07/2016.
 */
public class Portal extends BlockBCore implements ITileEntityProvider {

    public static final PropertyEnum<EnumFacing.Axis> AXIS = PropertyEnum.create("axis", EnumFacing.Axis.class);
    public static final PropertyBool DRAW_UP = PropertyBool.create("drawup");
    public static final PropertyBool DRAW_DOWN = PropertyBool.create("drawdown");
    public static final PropertyBool DRAW_EAST = PropertyBool.create("draweast");
    public static final PropertyBool DRAW_WEST = PropertyBool.create("drawwest");
    public static final PropertyBool VISIBLE = PropertyBool.create("visible");

    public Portal() {
        setHardness(Float.MAX_VALUE);
        this.setDefaultState(this.blockState.getBaseState() //
                .withProperty(AXIS, X) //
                .withProperty(DRAW_UP, true) //
                .withProperty(DRAW_DOWN, true) //
                .withProperty(DRAW_EAST, true) //
                .withProperty(DRAW_WEST, true) //
                .withProperty(VISIBLE, true));
    }

    @Override
    public boolean uberIsBlockFullCube() {
        return false;
    }

    //region Block State

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(AXIS) == X ? 0 : state.getValue(AXIS) == Y ? 1 : 2;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(AXIS, meta == 0 ? X : meta == 1 ? Y : Z);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, AXIS, DRAW_UP, DRAW_DOWN, DRAW_EAST, DRAW_WEST, VISIBLE);
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
        EnumFacing.Axis axix = state.getValue(AXIS);
        IBlockState north = world.getBlockState(pos.add(0, 0, -1));
        IBlockState south = world.getBlockState(pos.add(0, 0, 1));
        IBlockState east = world.getBlockState(pos.add(1, 0, 0));
        IBlockState west = world.getBlockState(pos.add(-1, 0, 0));
        IBlockState up = world.getBlockState(pos.add(0, 1, 0));
        IBlockState down = world.getBlockState(pos.add(0, -1, 0));


        boolean visible = true;
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TilePortal) {
            TileDislocatorReceptacle receptacle = ((TilePortal) tile).getMaster();
            visible = receptacle == null || receptacle.hiddenTime == 0;
            ((TilePortal) tile).disabled = !visible;
        }
        state = state.withProperty(VISIBLE, visible);


        switch (axix) {
            case X:
                return state.withProperty(DRAW_UP, up.getBlock() != this).withProperty(DRAW_DOWN, down.getBlock() != this).withProperty(DRAW_EAST, south.getBlock() != this).withProperty(DRAW_WEST, north.getBlock() != this);
            case Y:
                return state.withProperty(DRAW_UP, south.getBlock() != this).withProperty(DRAW_DOWN, north.getBlock() != this).withProperty(DRAW_EAST, east.getBlock() != this).withProperty(DRAW_WEST, west.getBlock() != this);
            case Z:
                return state.withProperty(DRAW_UP, up.getBlock() != this).withProperty(DRAW_DOWN, down.getBlock() != this).withProperty(DRAW_EAST, east.getBlock() != this).withProperty(DRAW_WEST, west.getBlock() != this);
        }

        return state;
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
    }

    @Override
    public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune) {
    }

    @Override
    public void harvestBlock(World world, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity te, ItemStack heldStack) {
    }

    //endregion

    //region Portal Logic

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return worldIn.isRemote ? new TilePortalClient() : new TilePortal();
    }

    @Override
    public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World worldIn, BlockPos pos) {
        return new AxisAlignedBB(0.5, 0.5, 0.5, 0.5, 0.5, 0.5);
    }

    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
        return NULL_AABB;//new AxisAlignedBB(0.5, 0.5, 0.5, 0.5, 0.5, 0.5);
    }

    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos) {
        if (world.isRemote) {
            return;
        }

        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TilePortal) {
            ((TilePortal) tile).validatePortal();
        }
        else {
            world.setBlockToAir(pos);
        }
    }

    @Override
    public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entity) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TilePortal && ((TilePortal) tile).getMaster() != null) {
            ((TilePortal) tile).getMaster().handleEntityTeleport(entity);
        }
    }

    //endregion
}
