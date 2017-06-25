package com.brandon3055.draconicevolution.api.itemconfig;

import net.minecraft.item.ItemStack;

/**
 * Created by brandon3055 on 1/06/2016.
 */
public interface IConfigurableItem {

    /**
     * Register your fields with the given ItemConfigFieldRegistry then return the registry.
     */
    ItemConfigFieldRegistry getFields(ItemStack stack, ItemConfigFieldRegistry registry);

    int getProfileCount(ItemStack stack);
}
