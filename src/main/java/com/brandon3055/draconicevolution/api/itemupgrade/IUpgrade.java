package com.brandon3055.draconicevolution.api.itemupgrade;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

/**
 * Created by brandon3055 on 31/05/2016.
 *
 */
public interface IUpgrade {

    String getName();

    String getLocalizedName();

    ResourceLocation getSprite();

    ItemStack[] getRecipe();

    //public void onTick();Todo Work out a sain way to implement this

    int getMaxLevel(ItemStack stack);

    void onApplied(ItemStack stack);

    void onRemoved(ItemStack stack);
}
