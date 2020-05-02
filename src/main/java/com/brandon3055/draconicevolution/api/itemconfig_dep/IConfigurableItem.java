package com.brandon3055.draconicevolution.api.itemconfig_dep;

import net.minecraft.item.ItemStack;

/**
 * Created by brandon3055 on 1/06/2016.
 */
@Deprecated
public interface IConfigurableItem {

    /**
     * Register your fields with the given ItemConfigFieldRegistry then return the registry.
     */
    ItemConfigFieldRegistry getFields(ItemStack stack, ItemConfigFieldRegistry registry);

    default void onFieldChanged(ItemStack stack, IItemConfigField field) {}

    int getProfileCount(ItemStack stack);
}
