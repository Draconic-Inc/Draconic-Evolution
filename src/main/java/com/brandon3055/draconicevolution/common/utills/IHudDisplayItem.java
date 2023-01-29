package com.brandon3055.draconicevolution.common.utills;

import java.util.List;

import net.minecraft.item.ItemStack;

/**
 * Created by Brandon on 26/01/2015.
 */
public interface IHudDisplayItem {

    List<String> getDisplayData(ItemStack stack);
}
