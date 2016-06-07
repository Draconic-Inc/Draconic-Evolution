package com.brandon3055.draconicevolution.api.itemconfig;

import net.minecraft.item.ItemStack;

import java.util.Map;

/**
 * Created by brandon3055 on 1/06/2016.
 */
public interface IConfigurableItem {

    /**
     * Add your fields to the given map then return the map.
     * */
    Map<String, IItemConfigField> getFields(ItemStack stack, Map<String, IItemConfigField> fields);

    int getProfileCount(ItemStack stack);
}
