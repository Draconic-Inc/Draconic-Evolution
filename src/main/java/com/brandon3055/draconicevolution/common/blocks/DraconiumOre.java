package com.brandon3055.draconicevolution.common.blocks;

import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.util.IIcon;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.common.items.ModItems;
import com.brandon3055.draconicevolution.common.lib.References;
import com.brandon3055.draconicevolution.common.lib.Strings;
import net.minecraft.world.IBlockAccess;

public class DraconiumOre extends BlockDE {
	public IIcon icon;
	public IIcon iconEnd;
	public IIcon iconNether;

	protected DraconiumOre() {
		super(Material.rock);
		this.setBlockName(Strings.draconiumOreName);
		this.setCreativeTab(DraconicEvolution.tolkienTabBlocksItems);
		this.setHardness(5f);
		this.setResistance(20.0f);
		ModBlocks.register(this);
	}

    @Override
    public boolean canEntityDestroy(IBlockAccess world, int x, int y, int z, Entity entity) {
        return false;
    }

    @Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(final IIconRegister iconRegister)
	{
		icon = iconRegister.registerIcon(References.RESOURCESPREFIX + "animated/draconium_ore");
		iconEnd = iconRegister.registerIcon(References.RESOURCESPREFIX + "animated/draconium_ore_end");
		iconNether = iconRegister.registerIcon(References.RESOURCESPREFIX + "animated/draconium_ore_nether");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta)
	{
		int dim = Minecraft.getMinecraft().theWorld.provider.dimensionId;

		if (dim == -1)
			return iconNether;
		else if (dim == 0)
			return icon;
		else
			return iconEnd;
	}

	@Override
	public int quantityDropped(final Random random)
	{
		return 1 + random.nextInt(2);
	}

	@Override
	public int quantityDroppedWithBonus(final int number, final Random random)
	{
		return this.quantityDropped(random) + random.nextInt(number + 1);
	}

	@Override
	protected boolean canSilkHarvest()
	{
		return true;
	}

	@Override
	public Item getItemDropped(final int p_149650_1_, final Random p_149650_2_, final int p_149650_3_)
	{
		return ModItems.draconiumDust;
	}
}
