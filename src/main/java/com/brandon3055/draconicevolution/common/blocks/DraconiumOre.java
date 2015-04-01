package com.brandon3055.draconicevolution.common.blocks;

import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.common.ModBlocks;
import com.brandon3055.draconicevolution.common.ModItems;
import com.brandon3055.draconicevolution.common.lib.References;
import com.brandon3055.draconicevolution.common.lib.Strings;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

import java.util.Random;

public class DraconiumOre extends BlockDE {
	public IIcon icon;
	public IIcon iconEnd;
	public IIcon iconNether;

	public DraconiumOre() {
		super(Material.rock);
		this.setBlockName(Strings.draconiumOreName);
		this.setCreativeTab(DraconicEvolution.tabBlocksItems);
		this.setHardness(10f);
		this.setResistance(20.0f);
		this.setHarvestLevel("pickaxe", 3);
		ModBlocks.register(this);
	}

    @Override
    public boolean canEntityDestroy(IBlockAccess world, int x, int y, int z, Entity entity) {
        return false;
    }

    @Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconRegister)
	{
		icon = iconRegister.registerIcon(References.RESOURCESPREFIX + "animated/draconium_ore");
		iconEnd = iconRegister.registerIcon(References.RESOURCESPREFIX + "animated/draconium_ore_end");
		iconNether = iconRegister.registerIcon(References.RESOURCESPREFIX + "animated/draconium_ore_nether");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta)
	{
		switch (Minecraft.getMinecraft().theWorld.provider.dimensionId)
		{
			case -1:
				return iconNether;
			case 0:
				return icon;
			case 1:
				return iconEnd;
			default:
				return icon;
		}
	}

	@Override
	public int quantityDropped(Random random)
	{
		return 1 + random.nextInt(2);
	}

	@Override
	public int quantityDroppedWithBonus(int fortune, Random random)
	{
		return fortune == 0 ? this.quantityDropped(random) : this.quantityDropped(random) + fortune + random.nextInt(fortune * 2);
	}

	@Override
	protected boolean canSilkHarvest()
	{
		return true;
	}

	@Override
	public Item getItemDropped(int p_149650_1_, Random p_149650_2_, int p_149650_3_)
	{
		return ModItems.draconiumDust;
	}

}
