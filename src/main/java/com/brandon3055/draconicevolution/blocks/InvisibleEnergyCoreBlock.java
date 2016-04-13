package com.brandon3055.draconicevolution.blocks;

import com.brandon3055.brandonscore.blocks.BlockBCore;
import com.brandon3055.brandonscore.blocks.properties.PropertyString;
import com.brandon3055.brandonscore.config.Feature;
import com.brandon3055.brandonscore.config.ICustomRender;
import com.brandon3055.draconicevolution.blocks.tileentity.TileInvisibleEnergyCoreBlock;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

/**
 * Created by brandon3055 on 13/4/2016.
 *
 */
public class InvisibleEnergyCoreBlock extends BlockBCore implements ICustomRender, ITileEntityProvider {

    public static final PropertyString BLOCK_TYPE = new PropertyString("block", "draconicevolution:draconiumBlock", "draconicevolution:draconicBlock", "minecraft:redstone_block", "minecraft:glass");

    @Override
    public void getSubBlocks(Item item, CreativeTabs tab, List<ItemStack> list) {
        this.setDefaultState(blockState.getBaseState().withProperty(BLOCK_TYPE, "draconicevolution:draconiumBlock"));
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileInvisibleEnergyCoreBlock();
    }

    //region BlockStates

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, BLOCK_TYPE);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return BLOCK_TYPE.toMeta(state.getValue(BLOCK_TYPE));
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(BLOCK_TYPE, BLOCK_TYPE.fromMeta(meta));
    }

    //endregionm

    //region Drops

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        String blockName = state.getValue(BLOCK_TYPE);
        Block block = Block.blockRegistry.getObject(new ResourceLocation(blockName));

        if (block != null) {
            return Item.getItemFromBlock(block);
        }

        return super.getItemDropped(state, rand, fortune);
    }

    //endregion

    //region Rendering

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
