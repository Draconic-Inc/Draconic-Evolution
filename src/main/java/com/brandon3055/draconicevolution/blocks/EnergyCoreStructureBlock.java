package com.brandon3055.draconicevolution.blocks;

import com.brandon3055.brandonscore.blocks.BlockBCore;
import com.brandon3055.draconicevolution.blocks.tileentity.IMultiBlockPart;
import com.brandon3055.draconicevolution.blocks.tileentity.TileCoreStructure;
import com.brandon3055.draconicevolution.world.EnergyCoreStructure;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;

/**
 * Created by brandon3055 on 13/4/2016.
 */
public class EnergyCoreStructureBlock extends BlockBCore/* implements IRenderOverride, ITileEntityProvider*/ {

    public EnergyCoreStructureBlock(Block.Properties properties) {
        super(properties);
//        this.setHardness(10F);
//        this.setLightLevel(1F);
    }

    @Override
    public boolean uberIsBlockFullCube() {
        return false;
    }

    @Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {}

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
    public void neighborChanged(BlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        if (com.brandon3055.draconicevolution.world.EnergyCoreStructure.coreForming) {
            return;
        }

        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileCoreStructure && ((TileCoreStructure) tile).getController() == null) {
            ((TileCoreStructure) tile).revert();
        }
    }

    @Override
    public boolean onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        TileEntity tile = world.getTileEntity(pos);

        if (tile instanceof TileCoreStructure) {
            return ((TileCoreStructure) tile).onTileClicked(player, state);
        }
        return super.onBlockActivated(state, world, pos, player, handIn, hit);
    }

    //region Drops

//    @Override
//    public Item getItemDropped(BlockState state, Random rand, int fortune) {
//        return null;
//    }

    @Override
    public void onBlockHarvested(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        TileEntity tile = world.getTileEntity(pos);

        if (tile instanceof TileCoreStructure) {

            if (!((TileCoreStructure) tile).blockName.isEmpty() && !player.abilities.isCreativeMode) {
                Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(((TileCoreStructure) tile).blockName));

                if (block != Blocks.AIR) {
                    if (((TileCoreStructure) tile).blockName.equals("draconicevolution:particle_generator")) {
//                        spawnAsEntity(world, pos, new ItemStack(block, 1));TODO
                    } else {
//                        spawnAsEntity(world, pos, new ItemStack(block));
                    }
                }
            }

            IMultiBlockPart master = ((TileCoreStructure) tile).getController();
            if (master != null) {
                world.setBlockState(pos, Blocks.AIR.getDefaultState());
                master.validateStructure();
            }
        }
    }

//    @Override
//    public ItemStack getPickBlock(BlockState state, RayTraceResult target, World world, BlockPos pos, PlayerEntity player) {
//        TileEntity tile = world.getTileEntity(pos);
//
//        if (tile instanceof TileInvisECoreBlock) {
//            if (((TileInvisECoreBlock) tile).blockName.equals("draconicevolution:draconium_block")) {
//                return new ItemStack(DEFeatures.draconiumBlock);
//            } else if (((TileInvisECoreBlock) tile).blockName.equals("draconicevolution:draconic_block")) {
//                return new ItemStack(DEFeatures.draconicBlock);
//            } else if (((TileInvisECoreBlock) tile).blockName.equals("draconicevolution:particle_generator")) {
//                return new ItemStack(DEFeatures.particleGenerator, 1, 2);
//            } else if (((TileInvisECoreBlock) tile).blockName.equals("minecraft:glass")) {
//                return new ItemStack(Blocks.GLASS);
//            } else if (((TileInvisECoreBlock) tile).blockName.equals("minecraft:redstone_block")) {
//                return new ItemStack(Blocks.REDSTONE_BLOCK);
//            }
//        }
//
//        return ItemStack.EMPTY;
//    }

    //endregion

    //region Rendering

    public boolean isFullCube(BlockState state) {
        return false;
    }

//    @Override
//    public AxisAlignedBB getSelectedBoundingBox(BlockState blockState, World world, BlockPos pos) {
//        TileEntity tile = world.getTileEntity(pos);
//
//        if (tile instanceof TileInvisECoreBlock && ((TileInvisECoreBlock) tile).blockName.equals("draconicevolution:particle_generator")) {
//            IMultiBlockPart controller = ((TileInvisECoreBlock) tile).getController();
//
//            if (controller instanceof TileEnergyCoreStabilizer) {
//                TileEnergyCoreStabilizer stabilizer = (TileEnergyCoreStabilizer) controller;
//                if (stabilizer.isValidMultiBlock.get()) {
//                    AxisAlignedBB bb = new AxisAlignedBB(stabilizer.getPos());
//
//                    if (stabilizer.multiBlockAxis.getPlane() == Direction.Plane.HORIZONTAL) {
//                        if (stabilizer.multiBlockAxis == Direction.Axis.X) {
//                            bb = bb.grow(0, 1, 1);
//                        } else {
//                            bb = bb.grow(1, 1, 0);
//                        }
//                    } else {
//                        bb = bb.grow(1, 0, 1);
//                    }
//                    return bb;
//                }
//            }
//
//            return super.getSelectedBoundingBox(blockState, world, pos);
//        }
//        return new AxisAlignedBB(0, 0, 0, 0, 0, 0);
//    }
//
//    @Override
//    public AxisAlignedBB getCollisionBoundingBox(BlockState state, IBlockAccess world, BlockPos pos) {
//        TileEntity tile = world.getTileEntity(pos);
//
//        if (tile instanceof TileInvisECoreBlock && ((TileInvisECoreBlock) tile).blockName.equals("minecraft:glass")) {
//            return NULL_AABB;
//        }
//
//        return super.getCollisionBoundingBox(state, world, pos);
//    }


    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.INVISIBLE;
    }

//    @OnlyIn(Dist.CLIENT)
//    @Override
//    public void registerRenderer(Feature feature) {
//
//    }
//
//    @Override
//    public boolean registerNormal(Feature feature) {
//        return false;
//    }

    //endregion
}
