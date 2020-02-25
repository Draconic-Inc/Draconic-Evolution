package com.brandon3055.draconicevolution.blocks;

import com.brandon3055.brandonscore.blocks.BlockBCore;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;

/**
 * Created by brandon3055 on 18/3/2016.
 */
public class DraconiumBlock extends BlockBCore {
//    public static PropertyBool CHARGED = PropertyBool.create("charged");

    public DraconiumBlock(Block.Properties properties) {
        super(properties);
//        this.setHarvestLevel("pickaxe", 3);
//        this.setDefaultState(stateContainer.getBaseState().withProperty(CHARGED, false));
//        this.addName(0, "draconium_block");
//        this.addName(1, "draconium_block_charged");
    }

//    @Override
//    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
//        list.add(new ItemStack(this, 1, 0));
//        list.add(new ItemStack(this, 1, 1));
//    }

//    //region BlockState
//    @Override
//    protected BlockStateContainer createBlockState() {
//        return new BlockStateContainer(this, CHARGED);
//    }
//
//    @Override
//    public int getMetaFromState(BlockState state) {
//        return state.getValue(CHARGED) ? 1 : 0;
//    }
//
//    @Override
//    public BlockState getStateFromMeta(int meta) {
//        return getDefaultState().withProperty(CHARGED, meta == 1);
//    }
//
//    @Override
//    public BlockState getActualState(BlockState state, IBlockAccess worldIn, BlockPos pos) {
//        return super.getActualState(state, worldIn, pos);
//    }
//
//    @Override
//    public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
//        world.setBlockState(pos, state.withProperty(CHARGED, stack.getItemDamage() == 1));
//    }
//
//    @Override
//    public ItemStack getPickBlock(BlockState state, RayTraceResult target, World world, BlockPos pos, PlayerEntity player) {
//        return new ItemStack(this, 1, world.getBlockState(pos).getValue(CHARGED) ? 1 : 0);
//    }
//    //endregion


    @Override
    public float getEnchantPowerBonus(BlockState state, IWorldReader world, BlockPos pos) {
        return 4f;
    }
}
