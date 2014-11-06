package com.brandon3055.draconicevolution.common.blocks;

import com.brandon3055.draconicevolution.common.lib.References;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;

public class BlockDE extends Block {

	public BlockDE(final Material material) {
		super(material);

	}

	public BlockDE() {
		super(Material.rock);
	}
	//todo unify block resistance and hardness
	@Override
	public String getUnlocalizedName()
	{
		return String.format("tile.%s%s", References.MODID.toLowerCase() + ":", getUnwrappedUnlocalizedName(super.getUnlocalizedName()));
	}

	public String getUnwrappedUnlocalizedName(String unlocalizedName)
	{
		return unlocalizedName.substring(unlocalizedName.indexOf(".") + 1);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconRegister)
	{
		this.blockIcon = iconRegister.registerIcon(References.RESOURCESPREFIX + getUnwrappedUnlocalizedName(super.getUnlocalizedName()));
	}

}
