package com.brandon3055.draconicevolution.common.items;

import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.common.ModItems;
import com.brandon3055.draconicevolution.common.lib.Strings;

/**
 * Created by Brandon on 21/11/2014.
 */
public class DraconicIngot extends ItemDE {

    public DraconicIngot() {
        this.setUnlocalizedName(Strings.draconicIngotName);
        this.setCreativeTab(DraconicEvolution.tabBlocksItems);
        ModItems.register(this);
    }
}
