package com.brandon3055.draconicevolution.blocks;

import com.brandon3055.brandonscore.blocks.EntityBlockBCore;
import com.brandon3055.brandonscore.lib.CustomTabHandling;
import com.brandon3055.brandonscore.multiblock.StructurePart;
import com.brandon3055.draconicevolution.blocks.tileentity.MultiBlockController;
import com.brandon3055.draconicevolution.blocks.tileentity.TileStructureBlock;
import com.brandon3055.draconicevolution.init.DEContent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.event.RenderHighlightEvent;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.event.EventHooks;

/**
 * An invisible placeholder block used by multi-block structures.
 * <p>
 * Created by brandon3055 on 16/08/2022
 */
public class StructureBlock extends EntityBlockBCore implements StructurePart, CustomTabHandling {
    public static boolean buildingLock = false;

    public StructureBlock(Block.Properties properties) {
        super(properties);
        setBlockEntity(DEContent.TILE_STRUCTURE_BLOCK::get, false);
        dontSpawnOnMe();
        setLightTransparent();
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Override
    public void onNeighborChange(BlockState state, LevelReader world, BlockPos pos, BlockPos neighbor) {
        neighborChanged(world, pos);
    }

    @Override
    public void neighborChanged(BlockState state, Level world, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        neighborChanged(world, pos);
    }

    private void neighborChanged(LevelReader world, BlockPos pos) {
        if (buildingLock) {
            return;
        }

        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof TileStructureBlock tile && (tile.getController() == null || !tile.getController().isStructureValid())) {
            tile.debug("Structure Block: Reverting from neighborChanged");
//            if (tile.debugEnabled()) LogHelper.bigInfo("Trace");
            tile.revert();
        }
    }

    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level world, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
        BlockEntity tile = world.getBlockEntity(pos);

        if (tile instanceof TileStructureBlock structureTile) {
            ResourceLocation blockName = structureTile.blockName.get();
            if (blockName != null) {
                Block block = BuiltInRegistries.BLOCK.get(blockName);
                MultiBlockController controller = structureTile.getController();
                world.removeBlock(pos, false);
                if (block != Blocks.AIR && !player.getAbilities().instabuild) {
                    popResource(world, pos, new ItemStack(block));
                }
                if (controller != null) {
                    controller.validateStructure();
                }
            }
        }
        playerWillDestroy(world, pos, state, player);
        return true;
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader world, BlockPos pos, Player player) {
        BlockEntity tile = world.getBlockEntity(pos);
        if (tile instanceof TileStructureBlock structureTile) {
            ResourceLocation blockName = structureTile.blockName.get();
            if (blockName != null) {
                Block block = BuiltInRegistries.BLOCK.get(blockName);
                return new ItemStack(block);
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState p_60572_, BlockGetter p_60573_, BlockPos p_60574_, CollisionContext p_60575_) {
        return Shapes.block();
    }

    @Override
    public VoxelShape getOcclusionShape(BlockState p_60578_, BlockGetter p_60579_, BlockPos p_60580_) {
        return Shapes.block();
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
        if (getter.getBlockEntity(pos) instanceof TileStructureBlock tile) {
            return tile.getShape(context);
        }
        return super.getShape(state, getter, pos, context);
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (level.getBlockEntity(pos) instanceof TileStructureBlock tile) {
            tile.debug("Structure Block: Block Tick doRevert");
//            if (tile.debugEnabled()) LogHelper.bigInfo("Trace");
            tile.doRevert();
        }
        super.tick(state, level, pos, random);
    }

    @Override
    public boolean is(Level level, BlockPos pos, TagKey<Block> key) {
        return getBlock(level, pos).defaultBlockState().is(key);
    }

    @Override
    public boolean is(Level level, BlockPos pos, Block block) {
        return getBlock(level, pos).defaultBlockState().is(block);
    }

    public static Block getBlock(Level level, BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof TileStructureBlock tile) {
            ResourceLocation name = tile.blockName.get();
            if (name != null) {
                return BuiltInRegistries.BLOCK.get(name);
            }
        }
        return Blocks.AIR;
    }

    @Override
    public float getDestroyProgress(BlockState state, Player player, BlockGetter getter, BlockPos pos) {
        float f = state.getDestroySpeed(getter, pos);
        if (getter.getBlockEntity(pos) instanceof TileStructureBlock tile) {
            Block block = tile.getOriginalBlock();
            if (block != Blocks.AIR) {
                f = block.defaultBlockState().getDestroySpeed(getter, pos);
            }
        }
        if (f == -1.0F) {
            return 0.0F;
        } else {
            int i = CommonHooks.isCorrectToolForDrops(state, player) ? 30 : 100;
            return player.getDigSpeed(state, pos) / f / (float)i;
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean renderSelectionBox(RenderHighlightEvent.Block event, Level level) {
        if (level.getBlockEntity(event.getTarget().getBlockPos()) instanceof TileStructureBlock tile) {
            return tile.renderSelectionBox(event);
        }
        return true;
    }
}
