package com.brandon3055.draconicevolution.common.blocks;

import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.common.ModBlocks;
import com.brandon3055.draconicevolution.common.lib.References;
import com.brandon3055.draconicevolution.common.lib.Strings;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;

/**
 * Created by Brandon on 21/11/2014.
 */
public class DraconicBlock extends BlockDE{
	IIcon top;
	public DraconicBlock() {
		this.setHardness(20F);
		this.setResistance(40F);
		this.setCreativeTab(DraconicEvolution.tabBlocksItems);
		this.setBlockName(Strings.draconicBlockName);
		this.setHarvestLevel("pickaxe", 4);
		ModBlocks.register(this);
	}

	@Override
	public void registerBlockIcons(IIconRegister iconRegister) {
		blockIcon = iconRegister.registerIcon(References.RESOURCESPREFIX + "draconic_block");
		top = iconRegister.registerIcon(References.RESOURCESPREFIX + "draconic_block_blank");
	}

	@Override
	public IIcon getIcon(int side, int p_149691_2_) {
		if (side == 0 || side == 1) return top;
		else return blockIcon;
	}
}
