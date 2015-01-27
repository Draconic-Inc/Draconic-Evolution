package com.brandon3055.draconicevolution.common.utills;

import net.minecraft.item.ItemStack;

import java.util.List;

/**
 * Created by Brandon on 26/01/2015.
 */
public interface IHudDisplayItem {
	List<String> getDisplayData(ItemStack stack);
}
