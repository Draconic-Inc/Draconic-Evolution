package com.brandon3055.draconicevolution.api.fusioncrafting;

import net.minecraft.item.ItemStack;

/**
 * Created by brandon3055 on 11/06/2016.
 * This interface is used to identify and interact with crafting pedestals.
 *
 * This interface should only ever be implemented by tile entities so it should be of to safe cast an ICraftingPedestal
 * to a TileEntity if you need access to the world and position.
 */
public interface ICraftingPedestal {

    /**
     * @return The tier of the pedestal.
     *
     * Default tiers are:
     * 0 - Basic
     * 1 - Wyvern
     * 2 - Draconic
     * 3 - Chaotic
     */
    int getPedestalTier();

    /**
     * @return The stack in this pedestal.
     */
    ItemStack getStackInPedestal();

    /**
     * Sets the stack in the pedestal.
     */
    void setStackInPedestal(ItemStack stack);
}
