package com.brandon3055.draconicevolution.common.items;

import com.brandon3055.draconicevolution.common.ModItems;
import com.brandon3055.draconicevolution.common.lib.Strings;

public class SunFocus extends ItemDE {

    public SunFocus() {
        this.setUnlocalizedName(Strings.sunFocusName);
        // this.setCreativeTab(DraconicEvolution.tabBlocksItems);
        ModItems.register(this);
    }
}
