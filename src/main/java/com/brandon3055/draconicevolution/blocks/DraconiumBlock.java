package com.brandon3055.draconicevolution.blocks;

import com.brandon3055.brandonscore.blocks.BlockMobSafe;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

/**
 * Created by brandon3055 on 18/3/2016.
 */
public class DraconiumBlock extends BlockMobSafe {
    public static PropertyBool CHARGED = PropertyBool.create("charged");

    public DraconiumBlock() {
        super(Material.IRON);
        this.setHarvestLevel("pickaxe", 3);
        this.setDefaultState(blockState.getBaseState().withProperty(CHARGED, false));
        this.addName(0, "draconium_block");
        this.addName(1, "draconium_block_charged");
    }

    @Override
    public void getSubBlocks(Item item, CreativeTabs tab, NonNullList<ItemStack> list) {
        list.add(new ItemStack(item, 1, 0));
        list.add(new ItemStack(item, 1, 1));
    }

    //region BlockState
    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, CHARGED);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(CHARGED) ? 1 : 0;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(CHARGED, meta == 1);
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        return super.getActualState(state, worldIn, pos);
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        world.setBlockState(pos, state.withProperty(CHARGED, stack.getItemDamage() == 1));
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        return new ItemStack(this, 1, world.getBlockState(pos).getValue(CHARGED) ? 1 : 0);
    }
    //endregion


    @Override
    public float getEnchantPowerBonus(World world, BlockPos pos) {
        if (world.getBlockState(pos).getValue(CHARGED)) {
            return 12f;
        }

        return 4f;
    }
}
