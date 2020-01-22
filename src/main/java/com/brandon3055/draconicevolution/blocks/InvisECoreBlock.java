package com.brandon3055.draconicevolution.blocks;

import com.brandon3055.brandonscore.blocks.BlockBCore;
import com.brandon3055.brandonscore.registry.Feature;
import com.brandon3055.brandonscore.registry.IRenderOverride;
import com.brandon3055.draconicevolution.DEFeatures;
import com.brandon3055.draconicevolution.blocks.tileentity.IMultiBlockPart;
import com.brandon3055.draconicevolution.blocks.tileentity.TileEnergyCoreStabilizer;
import com.brandon3055.draconicevolution.blocks.tileentity.TileInvisECoreBlock;
import com.brandon3055.draconicevolution.world.EnergyCoreStructure;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

/**
 * Created by brandon3055 on 13/4/2016.
 */
public class InvisECoreBlock extends BlockBCore implements IRenderOverride, ITileEntityProvider {

    public InvisECoreBlock() {
        this.setHardness(10F);
        this.setLightLevel(1F);
    }

    @Override
    public boolean uberIsBlockFullCube() {
        return false;
    }

    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileInvisECoreBlock();
    }

    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos) {
        if (EnergyCoreStructure.coreForming) {
            return;
        }

        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileInvisECoreBlock && ((TileInvisECoreBlock) tile).getController() == null) {
            ((TileInvisECoreBlock) tile).revert();
        }
    }


    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        TileEntity tile = world.getTileEntity(pos);

        if (tile instanceof TileInvisECoreBlock) {
            return ((TileInvisECoreBlock) tile).onTileClicked(playerIn, state);
        }

        return super.onBlockActivated(world, pos, state, playerIn, hand, side, hitX, hitY, hitZ);
    }

    //region Drops

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return null;
    }

    @Override
    public void onBlockHarvested(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
        TileEntity tile = world.getTileEntity(pos);

        if (tile instanceof TileInvisECoreBlock) {

            if (!((TileInvisECoreBlock) tile).blockName.isEmpty() && !player.capabilities.isCreativeMode) {
                Block block = Block.REGISTRY.getObject(new ResourceLocation(((TileInvisECoreBlock) tile).blockName));

                if (block != Blocks.AIR) {
                    if (((TileInvisECoreBlock) tile).blockName.equals("draconicevolution:particle_generator")) {
                        spawnAsEntity(world, pos, new ItemStack(block, 1, 2));
                    }
                    else {
                        spawnAsEntity(world, pos, new ItemStack(block));
                    }
                }
            }

            IMultiBlockPart master = ((TileInvisECoreBlock) tile).getController();
            if (master != null) {
                world.setBlockToAir(pos);
                master.validateStructure();
            }
        }
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        TileEntity tile = world.getTileEntity(pos);

        if (tile instanceof TileInvisECoreBlock) {
            if (((TileInvisECoreBlock) tile).blockName.equals("draconicevolution:draconium_block")) {
                return new ItemStack(DEFeatures.draconiumBlock);
            }
            else if (((TileInvisECoreBlock) tile).blockName.equals("draconicevolution:draconic_block")) {
                return new ItemStack(DEFeatures.draconicBlock);
            }
            else if (((TileInvisECoreBlock) tile).blockName.equals("draconicevolution:particle_generator")) {
                return new ItemStack(DEFeatures.particleGenerator, 1, 2);
            }
            else if (((TileInvisECoreBlock) tile).blockName.equals("minecraft:glass")) {
                return new ItemStack(Blocks.GLASS);
            }
            else if (((TileInvisECoreBlock) tile).blockName.equals("minecraft:redstone_block")) {
                return new ItemStack(Blocks.REDSTONE_BLOCK);
            }
        }

        return ItemStack.EMPTY;
    }

    //endregion

    //region Rendering

    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return super.getBoundingBox(state, source, pos);//new AxisAlignedBB(0, 0, 0, 0, 0, 0);
    }

    @Override
    public AxisAlignedBB getSelectedBoundingBox(IBlockState blockState, World world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);

        if (tile instanceof TileInvisECoreBlock && ((TileInvisECoreBlock) tile).blockName.equals("draconicevolution:particle_generator")) {
            IMultiBlockPart controller = ((TileInvisECoreBlock) tile).getController();

            if (controller instanceof TileEnergyCoreStabilizer) {
                TileEnergyCoreStabilizer stabilizer = (TileEnergyCoreStabilizer) controller;
                if (stabilizer.isValidMultiBlock.value) {
                    AxisAlignedBB bb = new AxisAlignedBB(stabilizer.getPos());

                    if (stabilizer.multiBlockAxis.getPlane() == EnumFacing.Plane.HORIZONTAL) {
                        if (stabilizer.multiBlockAxis == EnumFacing.Axis.X) {
                            bb = bb.grow(0, 1, 1);
                        }
                        else {
                            bb = bb.grow(1, 1, 0);
                        }
                    }
                    else {
                        bb = bb.grow(1, 0, 1);
                    }
                    return bb;
                }
            }

            return super.getSelectedBoundingBox(blockState, world, pos);
        }
        return new AxisAlignedBB(0, 0, 0, 0, 0, 0);
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);

        if (tile instanceof TileInvisECoreBlock && ((TileInvisECoreBlock) tile).blockName.equals("minecraft:glass")) {
            return NULL_AABB;
        }

        return super.getCollisionBoundingBox(state, world, pos);
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.INVISIBLE;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerRenderer(Feature feature) {

    }

    @Override
    public boolean registerNormal(Feature feature) {
        return false;
    }

    //endregion
}
