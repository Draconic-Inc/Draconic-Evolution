package com.brandon3055.draconicevolution.blocks;

import com.brandon3055.brandonscore.blocks.BlockBCore;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.state.EnumProperty;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import java.util.Random;

/**
 * Created by brandon3055 on 18/3/2016.
 */
public class DraconiumOre extends BlockBCore {

    public DraconiumOre(Properties properties) {
        super(properties);
    }

//    public static EnumProperty<EnumType> ORE_TYPE = EnumProperty.create("type", EnumType.class);
//
//    public DraconiumOre(Block.Properties properties) {
//        super(properties);
//        this.setHarvestLevel("pickaxe", 3);
//        this.setDefaultState(blockState.getBaseState().withProperty(ORE_TYPE, EnumType.NORMAL));
//        this.addName(0, "draconium_ore_normal");
//        this.addName(1, "draconium_ore_nether");
//        this.addName(2, "draconium_ore_end");
//    }
//
//    @Override
//    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
//        for (EnumType enumType : EnumType.values()) {
//            list.add(new ItemStack(this, 1, enumType.getMeta()));
//        }
//    }
//
//    //region BlockState
//    @Override
//    protected BlockStateContainer createBlockState() {
//        return new BlockStateContainer(this, ORE_TYPE);
//    }
//
//    @Override
//    public int getMetaFromState(BlockState state) {
//        return state.getValue(ORE_TYPE).getMeta();
//    }
//
//    @Override
//    public BlockState getStateFromMeta(int meta) {
//        return getDefaultState().withProperty(ORE_TYPE, EnumType.byMetadata(meta));
//    }
//
//    @Override
//    public BlockState getActualState(BlockState state, IBlockAccess worldIn, BlockPos pos) {
//        return super.getActualState(state, worldIn, pos);
//    }
//
//    @Override
//    public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
//        world.setBlockState(pos, state.withProperty(ORE_TYPE, EnumType.byMetadata(stack.getItemDamage())));
//    }
//
//    @Override
//    public ItemStack getPickBlock(BlockState state, RayTraceResult target, World world, BlockPos pos, PlayerEntity player) {
//        return new ItemStack(this, 1, world.getBlockState(pos).getValue(ORE_TYPE).getMeta());
//    }
//
//    @Override
//    public ItemStack getItem(World world, BlockPos pos, BlockState state) {
//        return new ItemStack(this, 1, world.getBlockState(pos).getValue(ORE_TYPE).getMeta());
//    }
//
//    @Override
//    public int getExpDrop(BlockState state, net.minecraft.world.IBlockAccess world, BlockPos pos, int fortune) {
//        Random rand = world instanceof World ? ((World)world).rand : new Random();
//        return MathHelper.getInt(rand, 3, 7) * fortune;
//    }
//
//    //endregion
//
//    //region Drops
//
//    @Override
//    public Item getItemDropped(BlockState state, Random rand, int fortune) {
//        return DEFeatures.draconiumDust;
//    }
//
//    @Override
//    public int quantityDropped(BlockState state, int fortune, Random random) {
//        return 2 + random.nextInt(2 + (fortune * 2));
//    }
//
//    //endregion
//
//    public static BlockState getEnd() {
//        if (ModFeatureParser.isEnabled(DEFeatures.draconiumOre)) {
//            return DEFeatures.draconiumOre.getDefaultState().withProperty(ORE_TYPE, EnumType.END);
//        }
//        else {
//            return Blocks.END_STONE.getDefaultState();
//        }
//    }
//
//    public static BlockState getNether() {
//        if (ModFeatureParser.isEnabled(DEFeatures.draconiumOre)) {
//            return DEFeatures.draconiumOre.getDefaultState().withProperty(ORE_TYPE, EnumType.NETHER);
//        }
//        else {
//            return Blocks.NETHERRACK.getDefaultState();
//        }
//    }

//    public enum EnumType implements IStringSerializable {
//        NORMAL(0, "normal"), NETHER(1, "nether"), END(2, "end");
//
//        private static final EnumType[] META_LOOKUP = new EnumType[values().length];
//        private final int meta;
//        private final String name;
//
//        EnumType(int meta, String name) {
//            this.meta = meta;
//            this.name = name;
//        }
//
//        public int getMeta() {
//            return meta;
//        }
//
//        public static EnumType byMetadata(int meta) {
//            if (meta < 0 || meta >= META_LOOKUP.length) {
//                meta = 0;
//            }
//
//            return META_LOOKUP[meta];
//        }
//
//        @Override
//        public String getName() {
//            return this.name;
//        }
//
//        static {
//            for (EnumType type : values()) {
//                META_LOOKUP[type.getMeta()] = type;
//            }
//        }
//    }
}
