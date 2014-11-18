package com.brandon3055.draconicevolution.common.items;

import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.common.ModItems;
import com.brandon3055.draconicevolution.common.lib.Strings;

public class DragonHeart extends ItemDE {
	public DragonHeart() {
		this.setUnlocalizedName(Strings.dragonHeartName);
		this.setCreativeTab(DraconicEvolution.tolkienTabBlocksItems);
		ModItems.register(this);
	}

}