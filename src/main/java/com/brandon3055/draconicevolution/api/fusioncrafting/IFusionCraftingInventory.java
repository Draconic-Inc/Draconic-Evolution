package com.brandon3055.draconicevolution.api.fusioncrafting;

import net.minecraft.item.ItemStack;

import java.util.List;

/**
 * Created by brandon3055 on 11/06/2016.
 * This is a wrapper meant to give IFusionRecipes direct access to the items being used for crafting.
 */
public interface IFusionCraftingInventory {

    /**
     * Gets the stack in the fusion crafting core. Also known as the crafting catalyst
     * */
    ItemStack getStackInCore();

    /**
     * Sets the stack in the fusion crafting core.
     * */
    void setStackInCore(ItemStack stack);

    /**
     * Returns a list of all valid crafting pedestals.
     * */
    List<ICraftingPedestal> getPedestals();

    /**
     * Receive energy from a pedestal. Should return the amount that was accepted.
     * */
    int receiveEnergyFromPedestal(int max, ICraftingPedestal pedestal);

    /**
     * @return true if currently crafting an item.
     */
    boolean craftingInProgress();
}
