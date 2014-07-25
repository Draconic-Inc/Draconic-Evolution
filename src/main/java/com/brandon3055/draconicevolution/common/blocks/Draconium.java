package com.brandon3055.draconicevolution.common.blocks;

import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.common.lib.References;
import com.brandon3055.draconicevolution.common.lib.Strings;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;

/**
 * Created by Brandon on 25/07/2014.
 */
public class Draconium extends DraconicEvolutionBlock {
	public Draconium() {
		super(Material.iron);
		this.setHardness(10F);
		this.setResistance(100F);
		this.setCreativeTab(DraconicEvolution.getCreativeTab(2));
		this.setBlockName(Strings.draconiumName);
		ModBlocks.register(this);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconRegister) {
		blockIcon = iconRegister.registerIcon(References.RESOURCESPREFIX + "draconium_block");
	}
}
