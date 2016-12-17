package com.brandon3055.draconicevolution.blocks;

import com.brandon3055.brandonscore.blocks.BlockBCore;
import com.brandon3055.draconicevolution.DEFeatures;
import com.brandon3055.draconicevolution.DraconicEvolution;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.List;
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
	}

	@Override
	public void getSubBlocks(Item item, CreativeTabs tab, List<ItemStack> list) {
		for (EnumType enumType : EnumType.values())
		{
			list.add(new ItemStack(item, 1, enumType.getMeta()));
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
	//endregion

	//region Drops

	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return DEFeatures.draconiumDust;
	}

	@Override
	public int quantityDropped(IBlockState state, int fortune, Random random) {
		return 4 - state.getValue(ORE_TYPE).getMeta() + random.nextInt(2 + (fortune * 2));
	}

	//endregion

	public static IBlockState getEnd(){
		if (DraconicEvolution.featureParser.isEnabled(DEFeatures.draconiumOre)){
			return DEFeatures.draconiumOre.getDefaultState().withProperty(ORE_TYPE, EnumType.END);
		}else {
			return Blocks.END_STONE.getDefaultState();
		}
	}

	public static IBlockState getNether(){
		if (DraconicEvolution.featureParser.isEnabled(DEFeatures.draconiumOre)){
			return DEFeatures.draconiumOre.getDefaultState().withProperty(ORE_TYPE, EnumType.NETHER);
		}else {
			return Blocks.NETHERRACK.getDefaultState();
		}
	}

	public static enum EnumType implements IStringSerializable {
		NORMAL(0, "normal"),
		NETHER(1, "nether"),
		END(2, "end");

		private static final EnumType[] META_LOOKUP = new EnumType[values().length];
		private final int meta;
		private final String name;

		private EnumType(int meta, String name){
			this.meta = meta;
			this.name = name;
		}

		public int getMeta() {
			return meta;
		}

		public static EnumType byMetadata(int meta)
		{
			if (meta < 0 || meta >= META_LOOKUP.length)
			{
				meta = 0;
			}

			return META_LOOKUP[meta];
		}

		@Override
		public String getName() {
			return this.name;
		}

		static
		{
			for (EnumType type : values())
			{
				META_LOOKUP[type.getMeta()] = type;
			}
		}
	}
}
