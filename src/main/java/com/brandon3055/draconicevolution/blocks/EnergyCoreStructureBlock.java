//package com.brandon3055.draconicevolution.blocks;
//
//import com.brandon3055.brandonscore.blocks.BlockBCore;
//import com.brandon3055.draconicevolution.blocks.tileentity.TileCoreStructure;
//import com.brandon3055.draconicevolution.blocks.tileentity.TileEnergyCoreStabilizer;
//import com.brandon3055.draconicevolution.init.DEContent;
//import net.minecraft.core.BlockPos;
//import net.minecraft.core.NonNullList;
//import net.minecraft.resources.ResourceLocation;
//import net.minecraft.world.entity.player.Player;
//import net.minecraft.world.item.CreativeModeTab;
//import net.minecraft.world.item.ItemStack;
//import net.minecraft.world.level.BlockGetter;
//import net.minecraft.world.level.Level;
//import net.minecraft.world.level.LevelReader;
//import net.minecraft.world.level.block.Block;
//import net.minecraft.world.level.block.Blocks;
//import net.minecraft.world.level.block.EntityBlock;
//import net.minecraft.world.level.block.RenderShape;
//import net.minecraft.world.level.block.entity.BlockEntity;
//import net.minecraft.world.level.block.state.BlockState;
//import net.minecraft.world.level.material.FluidState;
//import net.minecraft.world.phys.HitResult;
//import net.minecraft.world.phys.shapes.CollisionContext;
//import net.minecraft.world.phys.shapes.Shapes;
//import net.minecraft.world.phys.shapes.VoxelShape;
//import net.minecraftforge.registries.ForgeRegistries;
//
///**
// * Created by brandon3055 on 13/4/2016.
// */
//public class EnergyCoreStructureBlock extends BlockBCore implements EntityBlock {
//
//    public EnergyCoreStructureBlock(Block.Properties properties) {
//        super(properties);
//        setBlockEntity(() -> DEContent.tile_structure_block, false);
//        dontSpawnOnMe();
//    }
//
//    @Override
//    public RenderShape getRenderShape(BlockState state) {
//        return RenderShape.INVISIBLE;
//    }
//
//    @Override
//    public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {}
//
//    @Override
//    public void onNeighborChange(BlockState state, LevelReader world, BlockPos pos, BlockPos neighbor) {
//        if (com.brandon3055.draconicevolution.world.EnergyCoreStructure.coreForming) {
//            return;
//        }
//
//        BlockEntity tile = world.getBlockEntity(pos);
//        if (tile instanceof TileCoreStructure && ((TileCoreStructure) tile).getController() == null) {
//            ((TileCoreStructure) tile).revert();
//        }
//    }
//
//    @Override
//    public void neighborChanged(BlockState state, Level world, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
//        if (com.brandon3055.draconicevolution.world.EnergyCoreStructure.coreForming) {
//            return;
//        }
//
//        BlockEntity tile = world.getBlockEntity(pos);
//        if (tile instanceof TileCoreStructure && ((TileCoreStructure) tile).getController() == null) {
//            ((TileCoreStructure) tile).revert();
//        }
//    }
//
//    @Override
//    public boolean onDestroyedByPlayer(BlockState state, Level world, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
//        BlockEntity tile = world.getBlockEntity(pos);
//
//        if (tile instanceof TileCoreStructure) {
//            Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(((TileCoreStructure) tile).blockName.get()));
//
//            IMultiBlockPart master = ((TileCoreStructure) tile).getController();
//            if (master != null) {
//                world.removeBlock(pos, false);
//                master.validateStructure();
//                if (block != Blocks.AIR && !player.getAbilities().instabuild) {
//                    popResource(world, pos, new ItemStack(block));
//                }
//            }
//        }
//        return true;
//    }
//
//    @Override
//    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter world, BlockPos pos, Player player) {
//        BlockEntity tile = world.getBlockEntity(pos);
//        if (tile instanceof TileCoreStructure) {
//            if (((TileCoreStructure) tile).blockName.get().equals("draconicevolution:block_draconium")) {
//                return new ItemStack(DEContent.block_draconium);
//            } else if (((TileCoreStructure) tile).blockName.get().equals("draconicevolution:block_draconium_awakened")) {
//                return new ItemStack(DEContent.block_draconium_awakened);
//            } else if (((TileCoreStructure) tile).blockName.get().equals("draconicevolution:energy_core_stabilizer")) {
//                return new ItemStack(DEContent.energy_core_stabilizer, 1);
//            } else if (((TileCoreStructure) tile).blockName.get().equals("minecraft:glass")) {
//                return new ItemStack(Blocks.GLASS);
//            } else if (((TileCoreStructure) tile).blockName.get().equals("minecraft:redstone_block")) {
//                return new ItemStack(Blocks.REDSTONE_BLOCK);
//            }
//        }
//
//        return ItemStack.EMPTY;
//    }
//
//    @Override
//    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
//        BlockEntity tile = world.getBlockEntity(pos);
//
//        if (tile instanceof TileCoreStructure && ((TileCoreStructure) tile).blockName.get().equals("draconicevolution:energy_core_stabilizer")) {
//            IMultiBlockPart controller = ((TileCoreStructure) tile).getController();
//
//            if (controller instanceof TileEnergyCoreStabilizer) {
//                TileEnergyCoreStabilizer stabilizer = (TileEnergyCoreStabilizer) controller;
//                if (stabilizer.isValidMultiBlock.get()) {
//                    BlockState stabState = world.getBlockState(stabilizer.getBlockPos());
//                    BlockPos offset = stabilizer.getBlockPos().subtract(pos);
//                    return stabState.getBlock().getShape(stabState, world, stabilizer.getBlockPos(), context).move(offset.getX(), offset.getY(), offset.getZ());
//                }
//            }
//        }
//
//        return Shapes.block();
//    }
//}
