package com.brandon3055.draconicevolution.blocks;

import com.brandon3055.brandonscore.blocks.BlockBCore;
import com.brandon3055.brandonscore.utils.FacingUtils;
import com.brandon3055.draconicevolution.blocks.tileentity.TileDislocatorReceptacle;
import com.brandon3055.draconicevolution.blocks.tileentity.TilePortal;
import com.brandon3055.draconicevolution.blocks.tileentity.TilePortalClient;
import com.brandon3055.draconicevolution.init.DEContent;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.Random;

/**
 * Created by brandon3055 on 16/07/2016.
 */
public class Portal extends BlockBCore {

    public static final EnumProperty<Direction.Axis> AXIS = EnumProperty.create("axis", Direction.Axis.class);
    public static final BooleanProperty DRAW_UP = BooleanProperty.create("drawup");
    public static final BooleanProperty DRAW_DOWN = BooleanProperty.create("drawdown");
    public static final BooleanProperty DRAW_EAST = BooleanProperty.create("draweast");
    public static final BooleanProperty DRAW_WEST = BooleanProperty.create("drawwest");
    public static final BooleanProperty VISIBLE = BooleanProperty.create("visible");

    public Portal(Block.Properties properties) {
        super(properties);
        this.registerDefaultState(stateDefinition.any()
                .setValue(AXIS, Direction.Axis.X)
                .setValue(DRAW_UP, true)
                .setValue(DRAW_DOWN, true)
                .setValue(DRAW_EAST, true)
                .setValue(DRAW_WEST, true)
                .setValue(VISIBLE, true));
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(AXIS, DRAW_UP, DRAW_DOWN, DRAW_EAST, DRAW_WEST, VISIBLE);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return world instanceof ServerWorld ? new TilePortal() : new TilePortalClient();
    }

    @Override
    public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
        return VoxelShapes.empty();
    }

    @Override
    public boolean canSurvive(BlockState state, IWorldReader world, BlockPos pos) {
        TileEntity tile = world.getBlockEntity(pos);
        if (!(tile instanceof TilePortal)) {
            return false;
        }

        TileDislocatorReceptacle controller = ((TilePortal) tile).getController();
        if (controller != null && controller.ignitionStage.get() == 2) {
            return true;
        }

        if (!((TilePortal) tile).isPortalActive()) {
            return false;
        }

        for (Direction dir : FacingUtils.getFacingsAroundAxis(state.getValue(AXIS))) {
            if (!isFrame(world, pos.relative(dir)) && !isPortal(world, pos.relative(dir))) {
                return false;
            }
        }

        return true;
    }

    @Override
    public void tick(BlockState state, ServerWorld world, BlockPos pos, Random rand) {
        if (!state.canSurvive(world, pos)) {
            world.removeBlock(pos, false);
        }
    }

    @Override
    public void neighborChanged(BlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        if (world.isClientSide) return;

        if (!canSurvive(state, world, pos)) {
            TileEntity tile = world.getBlockEntity(pos);
            if (tile instanceof TileDislocatorReceptacle) {
                ((TileDislocatorReceptacle) tile).deactivate();
            }
            world.getBlockTicks().scheduleTick(pos, this, 1);
        }

        world.setBlockAndUpdate(pos, getPlacementState(state, world, pos));
    }

    @Override
    public void entityInside(BlockState state, World world, BlockPos pos, Entity entity) {
        TileEntity tile = world.getBlockEntity(pos);
        if (tile instanceof TilePortal && ((TilePortal) tile).getController() != null) {
            ((TilePortal) tile).getController().handleEntityTeleport(entity);
        }
    }

    public static BlockState getPlacementState(BlockState state, World world, BlockPos pos) {
        switch (state.getValue(AXIS)) {
            case Z:
                return state.setValue(DRAW_UP, isFrame(world, pos.above()))
                        .setValue(DRAW_DOWN, isFrame(world, pos.below()))
                        .setValue(DRAW_EAST, isFrame(world, pos.east()))
                        .setValue(DRAW_WEST, isFrame(world, pos.west()));
            case Y:
                return state.setValue(DRAW_UP, isFrame(world, pos.north()))
                        .setValue(DRAW_DOWN, isFrame(world, pos.south()))
                        .setValue(DRAW_EAST, isFrame(world, pos.east()))
                        .setValue(DRAW_WEST, isFrame(world, pos.west()));
            case X:
                return state.setValue(DRAW_UP, isFrame(world, pos.above()))
                        .setValue(DRAW_DOWN, isFrame(world, pos.below()))
                        .setValue(DRAW_EAST, isFrame(world, pos.south()))
                        .setValue(DRAW_WEST, isFrame(world, pos.north()));
        }
        return state;
    }

    private static boolean isFrame(IWorldReader world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        return state.getBlock() == DEContent.infused_obsidian || state.getBlock() == DEContent.dislocator_receptacle;
    }

    private static boolean isPortal(IWorldReader world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        return state.getBlock() == DEContent.portal;
    }
}
