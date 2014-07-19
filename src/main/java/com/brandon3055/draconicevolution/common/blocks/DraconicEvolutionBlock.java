package com.brandon3055.draconicevolution.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import com.brandon3055.draconicevolution.common.lib.References;

public class DraconicEvolutionBlock extends Block {

	public DraconicEvolutionBlock(final Material material) {
		super(material);

	}

	public DraconicEvolutionBlock() {
		super(Material.rock);
	}

	@Override
	public String getUnlocalizedName()
	{
		
		return String.format("tile.%s", getUnwrappedUnlocalizedName(super.getUnlocalizedName()));
	}

	public String getUnwrappedUnlocalizedName(final String unlocalizedName)
	{
		
		return unlocalizedName.substring(unlocalizedName.indexOf(".") + 1);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(final IIconRegister iconRegister)
	{
		this.blockIcon = iconRegister.registerIcon(References.RESOURCESPREFIX + getUnwrappedUnlocalizedName(super.getUnlocalizedName()));
	}

}
