package com.brandon3055.draconicevolution.blocks;

import com.brandon3055.brandonscore.blocks.BlockBCore;
import com.brandon3055.brandonscore.blocks.EntityBlockBCore;
import com.brandon3055.brandonscore.blocks.TileBCore;
import com.brandon3055.brandonscore.utils.FacingUtils;
import com.brandon3055.draconicevolution.blocks.tileentity.TileDislocatorReceptacle;
import com.brandon3055.draconicevolution.blocks.tileentity.TilePortal;
import com.brandon3055.draconicevolution.init.DEContent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

/**
 * Created by brandon3055 on 16/07/2016.
 */
public class Portal extends EntityBlockBCore {

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
        setBlockEntity(() -> DEContent.tile_portal, false);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> entityType) {
        if (level.isClientSide() && entityType == DEContent.tile_portal) {
            return (e, e2, e3, tile) -> ((TileBCore) tile).tick();
        }
        return null;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AXIS, DRAW_UP, DRAW_DOWN, DRAW_EAST, DRAW_WEST, VISIBLE);
    }

    @Override
    public VoxelShape getShape(BlockState p_220053_1_, BlockGetter p_220053_2_, BlockPos p_220053_3_, CollisionContext p_220053_4_) {
        return Shapes.empty();
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        BlockEntity tile = world.getBlockEntity(pos);
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
    public void tick(BlockState state, ServerLevel world, BlockPos pos, Random rand) {
        if (!state.canSurvive(world, pos)) {
            world.removeBlock(pos, false);
        }
    }

    @Override
    public void neighborChanged(BlockState state, Level world, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        if (world.isClientSide) return;

        if (!canSurvive(state, world, pos)) {
            BlockEntity tile = world.getBlockEntity(pos);
            if (tile instanceof TileDislocatorReceptacle) {
                ((TileDislocatorReceptacle) tile).deactivate();
            }
            world.scheduleTick(pos, this, 1);
        }

        world.setBlockAndUpdate(pos, getPlacementState(state, world, pos));
    }

    @Override
    public void entityInside(BlockState state, Level world, BlockPos pos, Entity entity) {
        BlockEntity tile = world.getBlockEntity(pos);
        if (tile instanceof TilePortal && ((TilePortal) tile).getController() != null) {
            ((TilePortal) tile).getController().handleEntityTeleport(entity);
        }
    }

    public static BlockState getPlacementState(BlockState state, Level world, BlockPos pos) {
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

    private static boolean isFrame(LevelReader world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        return state.getBlock() == DEContent.infused_obsidian || state.getBlock() == DEContent.dislocator_receptacle;
    }

    private static boolean isPortal(LevelReader world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        return state.getBlock() == DEContent.portal;
    }
}
