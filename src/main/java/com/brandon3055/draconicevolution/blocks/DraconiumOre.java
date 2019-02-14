package com.brandon3055.draconicevolution.blocks;

import com.brandon3055.brandonscore.blocks.BlockBCore;
import com.brandon3055.brandonscore.registry.ModFeatureParser;
import com.brandon3055.draconicevolution.DEFeatures;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.Random;

/**
 * Created by brandon3055 on 18/3/2016.
 */
public class DraconiumOre extends BlockBCore {
    public static PropertyEnum<EnumType> ORE_TYPE = PropertyEnum.create("type", EnumType.class);

    public DraconiumOre() {
        super(Material.ROCK);
        this.setHarvestLevel("pickaxe", 3);
        this.setDefaultState(blockState.getBaseState().withProperty(ORE_TYPE, EnumType.NORMAL));
        this.addName(0, "draconium_ore_normal");
        this.addName(1, "draconium_ore_nether");
        this.addName(2, "draconium_ore_end");
    }

    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
        for (EnumType enumType : EnumType.values()) {
            list.add(new ItemStack(this, 1, enumType.getMeta()));
        }
    }

    //region BlockState
    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, ORE_TYPE);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(ORE_TYPE).getMeta();
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(ORE_TYPE, EnumType.byMetadata(meta));
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        return super.getActualState(state, worldIn, pos);
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        world.setBlockState(pos, state.withProperty(ORE_TYPE, EnumType.byMetadata(stack.getItemDamage())));
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        return new ItemStack(this, 1, world.getBlockState(pos).getValue(ORE_TYPE).getMeta());
    }

    @Override
    public ItemStack getItem(World world, BlockPos pos, IBlockState state) {
        return new ItemStack(this, 1, world.getBlockState(pos).getValue(ORE_TYPE).getMeta());
    }

    @Override
    public int getExpDrop(IBlockState state, net.minecraft.world.IBlockAccess world, BlockPos pos, int fortune) {
        Random rand = world instanceof World ? ((World)world).rand : new Random();
        return MathHelper.getInt(rand, 3, 7) * fortune;
    }

    //endregion

    //region Drops

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return DEFeatures.draconiumDust;
    }

    @Override
    public int quantityDropped(IBlockState state, int fortune, Random random) {
        return 2 + random.nextInt(2 + (fortune * 2));
    }

    //endregion

    public static IBlockState getEnd() {
        if (ModFeatureParser.isEnabled(DEFeatures.draconiumOre)) {
            return DEFeatures.draconiumOre.getDefaultState().withProperty(ORE_TYPE, EnumType.END);
        }
        else {
            return Blocks.END_STONE.getDefaultState();
        }
    }

    public static IBlockState getNether() {
        if (ModFeatureParser.isEnabled(DEFeatures.draconiumOre)) {
            return DEFeatures.draconiumOre.getDefaultState().withProperty(ORE_TYPE, EnumType.NETHER);
        }
        else {
            return Blocks.NETHERRACK.getDefaultState();
        }
    }

    public enum EnumType implements IStringSerializable {
        NORMAL(0, "normal"), NETHER(1, "nether"), END(2, "end");

        private static final EnumType[] META_LOOKUP = new EnumType[values().length];
        private final int meta;
        private final String name;

        EnumType(int meta, String name) {
            this.meta = meta;
            this.name = name;
        }

        public int getMeta() {
            return meta;
        }

        public static EnumType byMetadata(int meta) {
            if (meta < 0 || meta >= META_LOOKUP.length) {
                meta = 0;
            }

            return META_LOOKUP[meta];
        }

        @Override
        public String getName() {
            return this.name;
        }

        static {
            for (EnumType type : values()) {
                META_LOOKUP[type.getMeta()] = type;
            }
        }
    }
}
