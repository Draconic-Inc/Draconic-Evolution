package com.brandon3055.draconicevolution.blocks.machines;

import com.brandon3055.brandonscore.blocks.BlockBCore;
import com.brandon3055.draconicevolution.blocks.tileentity.TileEntityDetector;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

/**
 * Created by brandon3055 on 25/09/2016.
 */
public class EntityDetector extends BlockBCore implements ITileEntityProvider {//Both (Use 2 separate tiles)
    public static final PropertyBool ACTIVE = PropertyBool.create("active");

    public EntityDetector() {
        setDefaultState(blockState.getBaseState().withProperty(ACTIVE, false));
    }

    //region BLockState

    @Override
    public int getMetaFromState(IBlockState state) {
        return 0;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, ACTIVE);
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        return super.getActualState(state, worldIn, pos);//todo active
    }

    //endregion

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityDetector();
    }
}
