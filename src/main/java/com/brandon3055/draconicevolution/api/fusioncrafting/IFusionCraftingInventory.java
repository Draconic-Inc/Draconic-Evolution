package com.brandon3055.draconicevolution.api.fusioncrafting;

import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Created by brandon3055 on 11/06/2016.
 * This is a wrapper meant to give IFusionRecipes direct access to the items being used for crafting.
 */
public interface IFusionCraftingInventory {

    /**
     * Gets the stack in the fusion crafting core. Also known as the crafting catalyst.<br>
     * slot 0 = Input Slot<br>
     * slot 1 == output slot
     */
    @Nonnull ItemStack getStackInCore(int slot);

    /**
     * Sets the stack in the specified slot.<br>
     * slot 0 = Input Slot<br>
     * slot 1 == output slot
     */
    void setStackInCore(int slot, @Nonnull ItemStack stack);

    /**
     * Returns a list of all valid crafting injectors.
     */
    List<ICraftingInjector> getInjectors();

    /**
     * @return The charge required for the current crafting recipe or 0 if there is no active recipe.
     */
    int getRequiredCharge();

    /**
     * @return true if currently crafting an item.
     */
    boolean craftingInProgress();

    /**
     * @return The current crafting stage (0 -> 1000 = charging, 1000 -> 2000 = crafting)
     */
    int getCraftingStage();
}
