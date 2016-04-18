package com.brandon3055.draconicevolution.blocks;

import com.brandon3055.brandonscore.blocks.BlockBCore;
import com.brandon3055.brandonscore.blocks.TileBCBase;
import com.brandon3055.brandonscore.config.Feature;
import com.brandon3055.brandonscore.config.ICustomRender;
import com.brandon3055.draconicevolution.blocks.tileentity.TileEnergyStorageCore;
import com.brandon3055.draconicevolution.blocks.tileentity.TileInvisECoreBlock;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

/**
 * Created by brandon3055 on 13/4/2016.
 */
public class InvisECoreBlock extends BlockBCore implements ICustomRender, ITileEntityProvider {

    public InvisECoreBlock() {
        this.setHardness(10F);
    }

    @Override
    public void getSubBlocks(Item item, CreativeTabs tab, List<ItemStack> list) {
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileInvisECoreBlock();
    }


    //region Drops

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return null;
    }

    @Override
    public void onBlockHarvested(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
        TileInvisECoreBlock tile = TileBCBase.getCastTileAt(world, pos, TileInvisECoreBlock.class);

        if (tile != null) {

            if (!tile.blockName.isEmpty() && !player.capabilities.isCreativeMode) {
                Block block = Block.blockRegistry.getObject(new ResourceLocation(tile.blockName));

                if (block != null) {
                    spawnAsEntity(world, pos, new ItemStack(block));
                }
            }

            TileEnergyStorageCore core = tile.getCore();
            if (core != null) {
                world.setBlockToAir(pos);
                core.validateStructure();
            }
        }
    }

    //endregion

    //region Rendering

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return new AxisAlignedBB(0, 0, 0, 0, 0, 0);
    }

    @Override
    public AxisAlignedBB getSelectedBoundingBox(IBlockState blockState, World worldIn, BlockPos pos) {
        return NULL_AABB;
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.INVISIBLE;
    }

    @Override
    public boolean isBlockNormalCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isVisuallyOpaque() {
        return false;
    }

    @Override
    public void registerRenderer(Feature feature) {

    }

    @Override
    public boolean registerNormal(Feature feature) {
        return false;
    }

    //endregion
}
