package com.brandon3055.draconicevolution.blocks.machines;

import com.brandon3055.brandonscore.blocks.BlockBCore;
import com.brandon3055.brandonscore.blocks.PropertyString;
import com.brandon3055.draconicevolution.blocks.tileentity.TileEnergyPylon;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * Created by brandon3055 on 30/3/2016.
 */
public class EnergyPylon extends BlockBCore/* implements ITileEntityProvider, IRenderOverride*/ {

    public static final BooleanProperty OUTPUT = BooleanProperty.create("output");
    public static final PropertyString FACING = new PropertyString("facing", "up", "down", "null");

    public EnergyPylon(Properties properties) {
        super(properties);
        this.setDefaultState(stateContainer.getBaseState().with(OUTPUT, false).with(FACING, "null"));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(OUTPUT, FACING);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new TileEnergyPylon();
    }

//    //region BlockState
//    @Override
//    protected BlockStateContainer createBlockState() {
//        return new BlockStateContainer(this, OUTPUT, FACING);
//    }
//
//    @Override
//    public int getMetaFromState(BlockState state) {
//        return state.getValue(FACING).equals("up") ? 1 : state.getValue(FACING).equals("down") ? 2 : 0;
//    }
//
//    @Override
//    public BlockState getStateFromMeta(int meta) {
//        return getDefaultState().withProperty(FACING, meta == 1 ? "up" : meta == 2 ? "down" : "null");
//    }
//
//    @Override
//    public BlockState getActualState(BlockState state, IBlockAccess worldIn, BlockPos pos) {
//        TileEntity tile = worldIn.getTileEntity(pos);
//        return state.withProperty(OUTPUT, tile instanceof TileEnergyPylon && ((TileEnergyPylon) tile).isOutputMode.get());
//    }
    //endregion

    //region Block Stuff


    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        TileEntity tile = worldIn.getTileEntity(pos);
        if (tile instanceof TileEnergyPylon) {
            if (player.isShiftKeyDown()) {
                ((TileEnergyPylon) tile).selectNextCore();
            } else {
                ((TileEnergyPylon) tile).validateStructure();
            }
            return ((TileEnergyPylon) tile).structureValid.get() ? ActionResultType.SUCCESS : ActionResultType.FAIL;
        }

        return ActionResultType.SUCCESS;
    }

    @Override
    public void neighborChanged(BlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        TileEntity tile = world.getTileEntity(pos);

        if (tile instanceof TileEnergyPylon) {
            ((TileEnergyPylon) tile).validateStructure();
        }
    }

    @Override
    public boolean hasComparatorInputOverride(BlockState state) {
        return true;
    }

    @Override
    public int getComparatorInputOverride(BlockState blockState, World world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile != null && tile instanceof TileEnergyPylon && ((TileEnergyPylon) tile).getExtendedCapacity() > 0) {
            return (int) ((double) ((TileEnergyPylon) tile).getExtendedStorage() / ((TileEnergyPylon) tile).getExtendedCapacity() * 15D);
        }
        return 0;
    }

    //endregion
}
