package com.brandon3055.draconicevolution.blocks.machines;

import com.brandon3055.brandonscore.blocks.BlockBCore;
import com.brandon3055.draconicevolution.blocks.tileentity.TileEnergyCore;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IntegerProperty;
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
public class EnergyCore extends BlockBCore {

    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");

    public EnergyCore(Properties properties) {
        super(properties);
        this.setDefaultState(stateContainer.getBaseState().with(ACTIVE, false));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(ACTIVE);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return state.get(ACTIVE) ? BlockRenderType.INVISIBLE : BlockRenderType.MODEL;
    }

//    @Override
//    public BlockRenderLayer getRenderLayer() {
//        return BlockRenderLayer.CUTOUT;
//    }


    //region Render Stuff


//    @Override
//    public AxisAlignedBB getCollisionBoundingBox(BlockState state, IBlockAccess worldIn, BlockPos pos) {
//        return getActualState(state, worldIn, pos).getValue(ACTIVE) ? new AxisAlignedBB(0, 0, 0, 0, 0, 0) : super.getCollisionBoundingBox(state, worldIn, pos);
//    }
//
//    @OnlyIn(Dist.CLIENT)
//    @Override
//    public boolean shouldSideBeRendered(BlockState state, IBlockAccess blockAccess, BlockPos pos, Direction side) {
//        return !state.getValue(ACTIVE);
//    }

    //endregion


    @Override
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        TileEntity core = world.getTileEntity(pos);

        if (core instanceof TileEnergyCore && !world.isRemote) {
            ((TileEnergyCore) core).onStructureClicked(world, pos, state, player);
        }

        return ActionResultType.PASS;
    }

    //region Interfaces


    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new TileEnergyCore();
    }

    //endregion
}
