package com.brandon3055.draconicevolution.api;

import net.minecraft.item.ItemStack;

/**
 * Created by brandon3055 on 8/02/19.
 *
 * This interface will eventually be expanded and extended to add additional functionality as needed.
 */
public interface ITechItem {

    TechLevel getTechLevel(ItemStack stack);
}
