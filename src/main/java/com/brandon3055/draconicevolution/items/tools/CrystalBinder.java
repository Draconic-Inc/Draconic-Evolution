package com.brandon3055.draconicevolution.items.tools;

import com.brandon3055.brandonscore.items.ItemBCore;
import com.brandon3055.draconicevolution.api.ICrystalBinder;
import net.minecraft.item.ItemStack;

/**
 * Created by brandon3055 on 25/11/2016.
 */
public class CrystalBinder extends ItemBCore implements ICrystalBinder {

    public CrystalBinder() {
        this.setMaxStackSize(1);
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return true;
    }
}
