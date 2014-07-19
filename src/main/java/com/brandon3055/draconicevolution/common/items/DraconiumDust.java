package com.brandon3055.draconicevolution.common.items;

import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.common.lib.Strings;

public class DraconiumDust extends DraconicEvolutionItem {
	public DraconiumDust() {
		this.setUnlocalizedName(Strings.draconiumDustName);
		this.setCreativeTab(DraconicEvolution.getCreativeTab(2));
		ModItems.register(this);
	}

}
