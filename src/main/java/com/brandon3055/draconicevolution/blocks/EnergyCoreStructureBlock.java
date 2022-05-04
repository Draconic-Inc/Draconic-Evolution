package com.brandon3055.draconicevolution.blocks;

import com.brandon3055.brandonscore.blocks.BlockBCore;
import com.brandon3055.draconicevolution.blocks.tileentity.IMultiBlockPart;
import com.brandon3055.draconicevolution.blocks.tileentity.TileCoreStructure;
import com.brandon3055.draconicevolution.blocks.tileentity.TileEnergyCoreStabilizer;
import com.brandon3055.draconicevolution.init.DEContent;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;

/**
 * Created by brandon3055 on 13/4/2016.
 */
public class EnergyCoreStructureBlock extends BlockBCore {

    
    public EnergyCoreStructureBlock(Block.Properties properties) {
        super(properties);
    }

    @Override
    public boolean canCreatureSpawn(BlockState state, IBlockReader world, BlockPos pos, EntitySpawnPlacementRegistry.PlacementType type, @Nullable EntityType<?> entityType) {
        return false;
    }

    @Override
    public BlockRenderType getRenderShape(BlockState state) {
        return BlockRenderType.INVISIBLE;
    }

    @Override
    public void fillItemCategory(ItemGroup group, NonNullList<ItemStack> items) {}

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new TileCoreStructure();
    }

    @Override
    public void observedNeighborChange(BlockState observerState, World world, BlockPos observerPos, Block changedBlock, BlockPos changedBlockPos) {

    }

    @Override
    public void onNeighborChange(BlockState state, IWorldReader world, BlockPos pos, BlockPos neighbor) {
        if (com.brandon3055.draconicevolution.world.EnergyCoreStructure.coreForming) {
            return;
        }

        TileEntity tile = world.getBlockEntity(pos);
        if (tile instanceof TileCoreStructure && ((TileCoreStructure) tile).getController() == null) {
            ((TileCoreStructure) tile).revert();
        }
    }

    @Override
    public void neighborChanged(BlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        if (com.brandon3055.draconicevolution.world.EnergyCoreStructure.coreForming) {
            return;
        }

        TileEntity tile = world.getBlockEntity(pos);
        if (tile instanceof TileCoreStructure && ((TileCoreStructure) tile).getController() == null) {
            ((TileCoreStructure) tile).revert();
        }
    }

    @Override
    public boolean removedByPlayer(BlockState state, World world, BlockPos pos, PlayerEntity player, boolean willHarvest, FluidState fluid) {
        TileEntity tile = world.getBlockEntity(pos);

        if (tile instanceof TileCoreStructure) {
            Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(((TileCoreStructure) tile).blockName.get()));

            IMultiBlockPart master = ((TileCoreStructure) tile).getController();
            if (master != null) {
                world.removeBlock(pos, false);
                master.validateStructure();
                if (block != Blocks.AIR && !player.abilities.instabuild) {
                    popResource(world, pos, new ItemStack(block));
                }
            }
        }
        return true;
    }

    @Override
    public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player) {
        TileEntity tile = world.getBlockEntity(pos);
        if (tile instanceof TileCoreStructure) {
            if (((TileCoreStructure) tile).blockName.get().equals("draconicevolution:block_draconium")) {
                return new ItemStack(DEContent.block_draconium);
            } else if (((TileCoreStructure) tile).blockName.get().equals("draconicevolution:block_draconium_awakened")) {
                return new ItemStack(DEContent.block_draconium_awakened);
            } else if (((TileCoreStructure) tile).blockName.get().equals("draconicevolution:energy_core_stabilizer")) {
                return new ItemStack(DEContent.energy_core_stabilizer, 1);
            } else if (((TileCoreStructure) tile).blockName.get().equals("minecraft:glass")) {
                return new ItemStack(Blocks.GLASS);
            } else if (((TileCoreStructure) tile).blockName.get().equals("minecraft:redstone_block")) {
                return new ItemStack(Blocks.REDSTONE_BLOCK);
            }
        }

        return ItemStack.EMPTY;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
        TileEntity tile = world.getBlockEntity(pos);

        if (tile instanceof TileCoreStructure && ((TileCoreStructure) tile).blockName.get().equals("draconicevolution:energy_core_stabilizer")) {
            IMultiBlockPart controller = ((TileCoreStructure) tile).getController();

            if (controller instanceof TileEnergyCoreStabilizer) {
                TileEnergyCoreStabilizer stabilizer = (TileEnergyCoreStabilizer) controller;
                if (stabilizer.isValidMultiBlock.get()) {
                    BlockState stabState = world.getBlockState(stabilizer.getBlockPos());
                    BlockPos offset = stabilizer.getBlockPos().subtract(pos);
                    return stabState.getBlock().getShape(stabState, world, stabilizer.getBlockPos(), context).move(offset.getX(), offset.getY(), offset.getZ());
                }
            }
        }

        return VoxelShapes.block();
    }
}
