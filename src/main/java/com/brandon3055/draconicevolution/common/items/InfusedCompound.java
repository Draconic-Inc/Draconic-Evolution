package com.brandon3055.draconicevolution.common.items;

import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.common.lib.Strings;

public class InfusedCompound extends TolkienItem {
	public InfusedCompound() {
		this.setUnlocalizedName(Strings.infusedCompoundName);
		this.setCreativeTab(DraconicEvolution.getCreativeTab(2));
		ModItems.register(this);
	}

}