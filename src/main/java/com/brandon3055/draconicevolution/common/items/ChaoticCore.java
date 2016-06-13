package com.brandon3055.draconicevolution.common.items;

import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.common.ModItems;
import net.minecraft.item.ItemStack;

/**
 * Created by brandon3055 on 2/10/2015.
 */
public class ChaoticCore extends ItemDE {
    public ChaoticCore() {
        this.setUnlocalizedName("chaoticCore");
        this.setCreativeTab(DraconicEvolution.tabBlocksItems);
        ModItems.register(this);
    }

    @Override
    public boolean hasEffect(ItemStack par1ItemStack, int pass) {
        return true;
    }
}
