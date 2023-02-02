package com.brandon3055.draconicevolution.blocks;

import codechicken.lib.math.MathHelper;
import codechicken.lib.raytracer.IndexedVoxelShape;
import codechicken.lib.raytracer.MultiIndexedVoxelShape;
import codechicken.lib.raytracer.SubHitBlockHitResult;
import codechicken.lib.raytracer.VoxelShapeCache;
import codechicken.lib.vec.Cuboid6;
import codechicken.lib.vec.Rotation;
import codechicken.lib.vec.Vector3;
import com.brandon3055.brandonscore.blocks.BlockBCore;
import com.brandon3055.brandonscore.blocks.EntityBlockBCore;
import com.brandon3055.draconicevolution.blocks.tileentity.TilePlacedItem;
import com.brandon3055.draconicevolution.init.DEContent;
import com.google.common.collect.ImmutableSet;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by brandon3055 on 25/07/2016.
 */
public class PlacedItem extends EntityBlockBCore {
    private static final VoxelShape FALLBACK_SHAPE = Shapes.box(0.1, 0.1, 0.1, 0.9, 0.9, 0.9);
    private static Int2ObjectMap<VoxelShape> SHAPE_CACHE = new Int2ObjectOpenHashMap<>();
    public static final DirectionProperty FACING = BlockStateProperties.FACING;

    public PlacedItem(Properties properties) {
        super(properties);
        this.registerDefaultState(stateDefinition.any().setValue(FACING, Direction.UP));
        setBlockEntity(() -> DEContent.tile_placed_item, true);
    }

    private static VoxelShape computeShape(int stackCount, boolean tool, boolean[] isBlock, Direction facing, boolean getCollisionShape) {
        int shapeConfig = 0b0;
        shapeConfig |= facing.ordinal() & 0b111; // Bits 1,2,3 are rotation
        shapeConfig |= (tool ? 1 : 0) << 3; // Bit 4 is tool mode
        shapeConfig |= (getCollisionShape ? 1 : 0) << 4; // Bit 5 is collision mode
        for (int i = 0; i < stackCount; i++) {
            shapeConfig |= (0b10 | (isBlock[i] ? 0b01 : 0b00)) << (5 + (i * 2));
        }

        return SHAPE_CACHE.computeIfAbsent(shapeConfig, integer -> {
            Cuboid6 baseCuboid = new Cuboid6(8 / 16D, 0, 8 / 16D, 8 / 16D, 0 / 16D, 8 / 16D);
            List<Cuboid6> stackCuboids = new ArrayList<>();
            boolean toolSize = tool && stackCount == 1;

            for (int i = 0; i < stackCount; i++) {
                double xOffset = getXOffset(i, stackCount);
                double zOffset = getZOffset(i, stackCount);
                double xzSize = 7 / 2D;
                double ySize = (1 / 16D) * (7.5 / 16D);
                if (isBlock[i]) {
                    xzSize = 6 / 2D;
                    ySize = 6 / 16D;
                } else if (toolSize) {
                    xzSize = 14 / 2D;
                    ySize = (1 / 16D) * (14.5 / 16D);
                }

                Cuboid6 stackBox = new Cuboid6(((8 - xzSize) / 16D) + xOffset, 0, ((8 - xzSize) / 16D) + zOffset, ((8 + xzSize) / 16D) + xOffset, ySize, ((8 + xzSize) / 16D) + zOffset);
                baseCuboid.enclose(stackBox);
                stackCuboids.add(stackBox);
            }

            if (getCollisionShape) {
                rotateCuboid(baseCuboid, facing);
                return VoxelShapeCache.getShape(baseCuboid);
            }

            baseCuboid.expand(0.25 / 16D);
            baseCuboid.max.y = 0.1 / 16D;
            baseCuboid.min.y = 0;
            rotateCuboid(baseCuboid, facing);

            ImmutableSet.Builder<IndexedVoxelShape> cuboids = ImmutableSet.builder();
            IndexedVoxelShape baseShape = new IndexedVoxelShape(VoxelShapeCache.getShape(baseCuboid), 0);
            cuboids.add(baseShape);
            for (int i = 0; i < stackCuboids.size(); i++) {
                rotateCuboid(stackCuboids.get(i), facing);
                cuboids.add(new IndexedVoxelShape(VoxelShapeCache.getShape(stackCuboids.get(i)), 1 + i));
            }

            return new MultiIndexedVoxelShape(baseShape, cuboids.build());
        });
    }

    private static void rotateCuboid(Cuboid6 cuboid, Direction rotation) {
        switch (rotation) {
            case DOWN:
                cuboid.apply(new Rotation(180 * MathHelper.torad, Vector3.X_POS).at(new Vector3(0.5, 0.5, 0.5)));
                break;
            case NORTH:
                cuboid.apply(new Rotation(-90 * MathHelper.torad, Vector3.X_POS).at(new Vector3(0.5, 0.5, 0.5)));
                break;
            case SOUTH:
                cuboid.apply(new Rotation(-90 * MathHelper.torad, Vector3.X_POS).at(new Vector3(0.5, 0.5, 0.5)));
                cuboid.apply(new Rotation(180 * MathHelper.torad, Vector3.Y_POS).at(new Vector3(0.5, 0.5, 0.5)));
                break;
            case WEST:
                cuboid.apply(new Rotation(-90 * MathHelper.torad, Vector3.X_POS).at(new Vector3(0.5, 0.5, 0.5)));
                cuboid.apply(new Rotation(90 * MathHelper.torad, Vector3.Y_POS).at(new Vector3(0.5, 0.5, 0.5)));
                break;
            case EAST:
                cuboid.apply(new Rotation(-90 * MathHelper.torad, Vector3.X_POS).at(new Vector3(0.5, 0.5, 0.5)));
                cuboid.apply(new Rotation(-90 * MathHelper.torad, Vector3.Y_POS).at(new Vector3(0.5, 0.5, 0.5)));
                break;
        }
    }

    public static double getXOffset(int index, int count) {
        double spacing = 0.25;
        double lowerVal = 3.5 + spacing;
        double upperVal = 7 + (spacing * 2);

        if (count == 1) return 0;
        else if (count == 2) return -(lowerVal / 16D) + (index * (upperVal / 16D));
        else if (count == 3) return index == 2 ? 0 : -(lowerVal / 16D) + (index * (upperVal / 16D));
        return -(lowerVal / 16D) + ((index % 2) * (upperVal / 16D));
    }

    public static double getZOffset(int index, int count) {
        if (count <= 2) return 0;
        double spacing = 0.25;
        double lowerVal = 3.5 + spacing;
        return index <= 1 ? -(lowerVal / 16D) : (lowerVal / 16D);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {}

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
        return state.getFluidState().isEmpty();
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter reader, BlockPos pos, CollisionContext context) {
        BlockEntity te = reader.getBlockEntity(pos);
        if (te instanceof TilePlacedItem) {
            TilePlacedItem tile = (TilePlacedItem) te;
            return computeShape(tile.stackCount.get(), tile.toolMode.get(), tile.getBlockArray(), state.getValue(FACING), false);
        }
        return FALLBACK_SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter reader, BlockPos pos, CollisionContext context) {
        BlockEntity te = reader.getBlockEntity(pos);
        if (te instanceof TilePlacedItem) {
            TilePlacedItem tile = (TilePlacedItem) te;
            return computeShape(tile.stackCount.get(), tile.toolMode.get(), tile.getBlockArray(), state.getValue(FACING), true);
        }
        return FALLBACK_SHAPE;
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player) {
        BlockEntity tile = level.getBlockEntity(pos);
        if (tile instanceof TilePlacedItem && target instanceof SubHitBlockHitResult) {
            List<ItemStack> stacks = ((TilePlacedItem) tile).getStacksInOrder();
            int index = ((SubHitBlockHitResult) target).subHit - 1;

            if (index >= 0 && index < stacks.size()) {
                ItemStack stack = stacks.get(index).copy();
                stack.setCount(1);
                return stack;
            }
        }

        return ItemStack.EMPTY;
    }

    @Override
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
        super.onRemove(state, world, pos, newState, isMoving);
    }

    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
        BlockEntity tile = level.getBlockEntity(pos);
        if (tile instanceof TilePlacedItem) {
            ((TilePlacedItem) tile).onBroken(player, Vector3.fromTileCenter(tile), false);
        }
        return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
    }

    @Override
    protected void spawnDestroyParticles(Level level, Player player, BlockPos pos, BlockState state) {
        level.levelEvent(player, 2001, pos, getId(state));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean addLandingEffects(BlockState state1, ServerLevel worldserver, BlockPos pos, BlockState state2, LivingEntity entity, int numberOfParticles) {
        return true;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean addRunningEffects(BlockState state, Level world, BlockPos pos, Entity entity) {
        return true;
    }
}
