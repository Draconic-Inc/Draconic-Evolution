package com.brandon3055.draconicevolution.blocks;

import com.brandon3055.brandonscore.blocks.BlockBCore;
import com.brandon3055.brandonscore.blocks.EntityBlockBCore;
import com.brandon3055.brandonscore.multiblock.StructurePart;
import com.brandon3055.draconicevolution.blocks.tileentity.MultiBlockController;
import com.brandon3055.draconicevolution.blocks.tileentity.TileEnergyCoreStabilizer;
import com.brandon3055.draconicevolution.blocks.tileentity.TileStructureBlock;
import com.brandon3055.draconicevolution.init.DEContent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.DrawSelectionEvent;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * An invisible placeholder block used by multi-block structures.
 * <p>
 * Created by brandon3055 on 16/08/2022
 */
public class StructureBlock extends EntityBlockBCore implements StructurePart {
    public static boolean buildingLock = false;

    public StructureBlock(Block.Properties properties) {
        super(properties);
        setBlockEntity(() -> DEContent.tile_structure_block, false);
        dontSpawnOnMe();
        setLightTransparent();
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Override
    public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {}

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

        BlockEntity tile = world.getBlockEntity(pos);
        if (tile instanceof TileStructureBlock && (((TileStructureBlock) tile).getController() == null || !((TileStructureBlock) tile).getController().isStructureValid())) {
            ((TileStructureBlock) tile).revert();
        }
    }

    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level world, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
        BlockEntity tile = world.getBlockEntity(pos);

        if (tile instanceof TileStructureBlock structureTile) {
            ResourceLocation blockName = structureTile.blockName.get();
            if (blockName != null) {
                Block block = ForgeRegistries.BLOCKS.getValue(blockName);
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
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter world, BlockPos pos, Player player) {
        BlockEntity tile = world.getBlockEntity(pos);
        if (tile instanceof TileStructureBlock structureTile) {
            ResourceLocation blockName = structureTile.blockName.get();
            if (blockName != null) {
                Block block = ForgeRegistries.BLOCKS.getValue(blockName);
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
    public void tick(BlockState state, ServerLevel level, BlockPos pos, Random random) {
        if (level.getBlockEntity(pos) instanceof TileStructureBlock tile) {
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
                return ForgeRegistries.BLOCKS.getValue(name);
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
            int i = net.minecraftforge.common.ForgeHooks.isCorrectToolForDrops(state, player) ? 30 : 100;
            return player.getDigSpeed(state, pos) / f / (float)i;
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean renderSelectionBox(DrawSelectionEvent.HighlightBlock event, Level level) {
        if (level.getBlockEntity(event.getTarget().getBlockPos()) instanceof TileStructureBlock tile) {
            return tile.renderSelectionBox(event);
        }
        return true;
    }
}
