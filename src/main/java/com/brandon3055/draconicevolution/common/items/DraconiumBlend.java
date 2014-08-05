package com.brandon3055.draconicevolution.common.items;

import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.common.lib.Strings;

public class DraconiumBlend extends ItemDE {
	public DraconiumBlend() {
		this.setUnlocalizedName(Strings.draconiumBlendName);
		this.setCreativeTab(DraconicEvolution.getCreativeTab(2));
		ModItems.register(this);
	}

}