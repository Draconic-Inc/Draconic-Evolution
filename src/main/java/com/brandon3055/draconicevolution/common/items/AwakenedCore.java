package com.brandon3055.draconicevolution.common.items;

import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.common.ModItems;
import com.brandon3055.draconicevolution.common.lib.Strings;
import net.minecraft.item.ItemStack;

public class AwakenedCore extends ItemDE {
    public AwakenedCore() {
        this.setUnlocalizedName(Strings.draconicCompoundName);
        this.setCreativeTab(DraconicEvolution.tabBlocksItems);
        ModItems.register(this);
    }

    @Override
    public boolean hasEffect(ItemStack par1ItemStack, int pass) {
        return true;
    }
}
