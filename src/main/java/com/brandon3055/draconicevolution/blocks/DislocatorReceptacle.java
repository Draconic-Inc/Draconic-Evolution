package com.brandon3055.draconicevolution.blocks;

import com.brandon3055.brandonscore.blocks.BlockBCore;
import com.brandon3055.draconicevolution.DEContent;
import com.brandon3055.draconicevolution.blocks.tileentity.TileDislocatorReceptacle;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * Created by brandon3055 on 16/07/2016.
 */
public class DislocatorReceptacle extends BlockBCore/* implements ITileEntityProvider */{

    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");
    public static final BooleanProperty CAMO = BooleanProperty.create("camo");

    public DislocatorReceptacle(Block.Properties properties) {
        super(properties);
        this.setDefaultState(stateContainer.getBaseState().with(ACTIVE, false).with(CAMO, false));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(ACTIVE, CAMO);
    }

    //region Block state and tile creation

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
//        return new BlockStateContainer(this, ACTIVE, CAMO);
//    }


    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new TileDislocatorReceptacle();
    }

//    @Override
//    public BlockState getActualState(BlockState state, IBlockAccess worldIn, BlockPos pos) {
//        TileEntity tile = worldIn.getTileEntity(pos);
//        return state.withProperty(CAMO, tile instanceof TileDislocatorReceptacle && ((TileDislocatorReceptacle) tile).camo.get());
//    }

    //endregion

    //region Fun stuff


    @Override
    public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        TileEntity tile = worldIn.getTileEntity(pos);

        if (tile instanceof TileDislocatorReceptacle) {
            ItemStack stack = player.getHeldItem(hand);
            if (stack.getItem() instanceof BlockItem && ((BlockItem) stack.getItem()).getBlock() == DEContent.infused_obsidian) {
                ((TileDislocatorReceptacle) tile).camo.set(!((TileDislocatorReceptacle) tile).camo.get());
                ((TileDislocatorReceptacle) tile).updateBlock();
                return true;
            }

            return ((TileDislocatorReceptacle) tile).onBlockActivated(player);
        }

        return false;
    }

    @Override
    public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        TileEntity tile = worldIn.getTileEntity(pos);

        if (tile instanceof TileDislocatorReceptacle) {
            ((TileDislocatorReceptacle) tile).deactivate();
        }

        super.onReplaced(state, worldIn, pos, newState, isMoving);
    }

    @Override
    public void onBlockAdded(BlockState state, World worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
        TileEntity tile = worldIn.getTileEntity(pos);

        if (tile instanceof TileDislocatorReceptacle) {
            ((TileDislocatorReceptacle) tile).attemptIgnition();
        }
    }

    @Override
    public void neighborChanged(BlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        TileEntity t = world.getTileEntity(pos);

        if (t instanceof TileDislocatorReceptacle) {
            TileDislocatorReceptacle tile = (TileDislocatorReceptacle) t;

            boolean powered = world.isBlockPowered(pos);

            if (!powered && tile.ltRedstone.get()) {
                tile.ltRedstone.set(false);
                tile.deactivate();
            }
            else if (powered && !tile.ltRedstone.get()) {
                tile.ltRedstone.set(true);
                tile.updateBlock();
                if (!tile.active.get()) {
                    tile.attemptIgnition();
                }
            }
        }
    }

    //endregion
}
