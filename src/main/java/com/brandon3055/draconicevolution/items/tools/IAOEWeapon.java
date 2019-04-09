package com.brandon3055.draconicevolution.items.tools;

import net.minecraft.item.ItemStack;

/**
 * Created by brandon3055 on 16/08/2016.
 */
public interface IAOEWeapon {

    double getWeaponAOE(ItemStack stack);

    //This default implementation is broken but without it any mods that are using this interface will break.
    default double getMaxWeaponAOE(ItemStack stack) { return getWeaponAOE(stack); }

    void setWeaponAOE(ItemStack stack, double value);

}
