package com.brandon3055.draconicevolution.blocks;

import com.brandon3055.brandonscore.blocks.BlockBCore;
import com.brandon3055.draconicevolution.blocks.tileentity.TileDislocatorReceptacle;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * Created by brandon3055 on 16/07/2016.
 */
public class DislocatorReceptacle extends BlockBCore implements ITileEntityProvider {

    public static final PropertyBool ACTIVE = PropertyBool.create("active");

    public DislocatorReceptacle() {
        this.setDefaultState(blockState.getBaseState().withProperty(ACTIVE, false));
    }

    //region Block state and tile creation

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

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileDislocatorReceptacle();
    }

    //endregion

    //region Fun stuff

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        TileEntity tile = worldIn.getTileEntity(pos);

        if (tile instanceof TileDislocatorReceptacle){
            return ((TileDislocatorReceptacle) tile).onBlockActivated(playerIn);
        }

        return false;
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        TileEntity tile = worldIn.getTileEntity(pos);

        if (tile instanceof TileDislocatorReceptacle){
            ((TileDislocatorReceptacle) tile).deactivate();
        }

        super.breakBlock(worldIn, pos, state);
    }

    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
        TileEntity tile = worldIn.getTileEntity(pos);

        if (tile instanceof TileDislocatorReceptacle){
            ((TileDislocatorReceptacle) tile).attemptIgnition();
        }
    }

    //endregion
}
