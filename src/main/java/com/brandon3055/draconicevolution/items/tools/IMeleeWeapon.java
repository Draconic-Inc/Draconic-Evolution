package com.brandon3055.draconicevolution.items.tools;

import net.minecraft.item.ItemStack;

/**
 * Created by brandon3055 on 16/08/2016.
 */
public interface IMeleeWeapon {
    
    double getWeaponAOE(ItemStack stack);

    float getWeaponDamage(ItemStack stack);

    float getWeaponSpeed(ItemStack stack);
}
