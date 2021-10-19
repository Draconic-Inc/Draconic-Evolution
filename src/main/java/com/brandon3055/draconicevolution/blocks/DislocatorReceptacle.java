package com.brandon3055.draconicevolution.blocks;

import com.brandon3055.brandonscore.blocks.BlockBCore;
import com.brandon3055.draconicevolution.blocks.tileentity.TileDislocatorReceptacle;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * Created by brandon3055 on 16/07/2016.
 */
public class DislocatorReceptacle extends BlockBCore {

    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");
    public static final BooleanProperty CAMO = BooleanProperty.create("camo");

    public DislocatorReceptacle(Block.Properties properties) {
        super(properties);
        this.registerDefaultState(stateDefinition.any().setValue(ACTIVE, false).setValue(CAMO, false));
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(ACTIVE, CAMO);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new TileDislocatorReceptacle();
    }

    @Override
    public void onRemove(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        TileEntity tile = worldIn.getBlockEntity(pos);

        if (tile instanceof TileDislocatorReceptacle && newState.getBlock() != state.getBlock()) {
            ((TileDislocatorReceptacle) tile).deactivate();
        }

        super.onRemove(state, worldIn, pos, newState, isMoving);
    }

    @Override
    public void onPlace(BlockState state, World worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
        TileEntity tile = worldIn.getBlockEntity(pos);

        if (tile instanceof TileDislocatorReceptacle && ((TileDislocatorReceptacle) tile).isActive()) {
            ((TileDislocatorReceptacle) tile).attemptActivation();
        }
    }

    //endregion
}
