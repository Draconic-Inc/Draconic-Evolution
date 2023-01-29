package com.brandon3055.draconicevolution.common.utills;

import net.minecraft.enchantment.Enchantment;

/**
 * Created by Brandon on 17/01/2015.
 */
public interface IInventoryTool extends IConfigurableItem {

    public abstract String getInventoryName();

    public abstract int getInventorySlots();

    public abstract boolean isEnchantValid(Enchantment enchant);
}
