package com.brandon3055.draconicevolution.blocks.machines;

import com.brandon3055.brandonscore.blocks.BlockBCore;
import com.brandon3055.brandonscore.blocks.EntityBlockBCore;
import com.brandon3055.draconicevolution.blocks.tileentity.TileEnergyCore;
import com.brandon3055.draconicevolution.blocks.tileentity.TileEnergyCoreStabilizer;
import com.brandon3055.draconicevolution.init.DEContent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Created by brandon3055 on 30/3/2016.
 */
public class EnergyCoreStabilizer extends EntityBlockBCore {

    public static final BooleanProperty LARGE = BooleanProperty.create("large");
    public static VoxelShape SHAPE = box(0.98, 0.98, 0.98, 15.02, 15.02, 15.02);
    public static VoxelShape SHAPE_X = Shapes.box(0, -1, -1, 1, 2, 2);
    public static VoxelShape SHAPE_Y = Shapes.box(-1, 0, -1, 2, 1, 2);
    public static VoxelShape SHAPE_Z = Shapes.box(-1, -1, 0, 2, 2, 1);

    public EnergyCoreStabilizer(Properties properties) {
        super(properties);
        this.registerDefaultState(stateDefinition.any().setValue(LARGE, false));
        setBlockEntity(() -> DEContent.tile_core_stabilizer, true);
        dontSpawnOnMe();
        setLightTransparent();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LARGE);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return state.getValue(LARGE) ? RenderShape.INVISIBLE : RenderShape.MODEL;
    }

    @Override
    public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
        return 10;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return SHAPE;//VoxelShapes.empty();//super.getCollisionShape(state, worldIn, pos, context);
    }

    @Override
    public VoxelShape getInteractionShape(BlockState state, BlockGetter worldIn, BlockPos pos) {
        return SHAPE;//super.getRaytraceShape(state, worldIn, pos);
    }

    @Override
    public VoxelShape getOcclusionShape(BlockState state, BlockGetter worldIn, BlockPos pos) {
        return SHAPE;//super.getRenderShape(state, worldIn, pos);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        BlockEntity tile = world.getBlockEntity(pos);

        if (tile instanceof TileEnergyCoreStabilizer) {
            if (((TileEnergyCoreStabilizer) tile).isValidMultiBlock.get()) {
                switch (((TileEnergyCoreStabilizer) tile).multiBlockAxis.get()) {
                    case X:
                        return SHAPE_X;
                    case Y:
                        return SHAPE_Y;
                    case Z:
                        return SHAPE_Z;
                }
            }
        }

        return SHAPE;
    }

    //endregion

    //region Place/Break stuff

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        BlockEntity tile = world.getBlockEntity(pos);

        if (tile instanceof TileEnergyCoreStabilizer) {
            ((TileEnergyCoreStabilizer) tile).onPlaced();
        } else {
            super.setPlacedBy(world, pos, state, placer, stack);
        }
    }

    @Override
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
        BlockEntity tile = world.getBlockEntity(pos);

        if (tile instanceof TileEnergyCoreStabilizer) {
            if (((TileEnergyCoreStabilizer) tile).isValidMultiBlock.get()) {
                ((TileEnergyCoreStabilizer) tile).revertStructure();
            }
            TileEnergyCore core = ((TileEnergyCoreStabilizer) tile).getCore();
            if (core != null) {
                world.removeBlockEntity(pos);
                ((TileEnergyCoreStabilizer) tile).validateStructure();
            }
        }
        super.onRemove(state, world, pos, newState, isMoving);
    }


    //endregion

}
