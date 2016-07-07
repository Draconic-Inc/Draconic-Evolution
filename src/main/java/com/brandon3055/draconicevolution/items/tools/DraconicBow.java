package com.brandon3055.draconicevolution.items.tools;

import net.minecraft.item.ItemStack;

/**
 * Created by brandon3055 on 5/06/2016.
 */
public class DraconicBow extends WyvernBow {
    public DraconicBow() {
        setEnergyStats(ToolStats.DRACONIC_BASE_CAPACITY, 8000000, 0);
    }


    @Override
    public int getToolTier(ItemStack stack) {
        return 1;
    }
}
