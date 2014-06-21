package com.brandon3055.draconicevolution.common.items;

import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.common.lib.Strings;

public class DraconicCore extends TolkienItem {
	public DraconicCore() {
		this.setUnlocalizedName(Strings.draconicCoreName);
		this.setCreativeTab(DraconicEvolution.getCreativeTab(2));
		ModItems.register(this);
	}

}
