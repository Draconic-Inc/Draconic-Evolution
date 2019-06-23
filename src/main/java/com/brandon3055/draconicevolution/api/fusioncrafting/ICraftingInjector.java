package com.brandon3055.draconicevolution.api.fusioncrafting;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

import javax.annotation.Nullable;

/**
 * Created by brandon3055 on 11/06/2016.
 * This interface is used to identify and interact with crafting pedestals.
 * <p>
 * This interface should only ever be implemented by tile entities so it should be of to safe cast an ICraftingPedestal
 * to a TileEntity if you need access to the world and position.
 */
public interface ICraftingInjector {

    /**
     * @return The tier of the pedestal.
     * <p>
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

    /**
     * @param craftingInventory The crafting inventory.
     * @return true if the pedestal accepts the inventory and can craft. false if the pedestal does not accept the inventory.
     * Will return false if the pedestal is already working with a different core.
     */
    boolean setCraftingInventory(@Nullable IFusionCraftingInventory craftingInventory);

    /**
     * Return the direction this pedestal is facing.
     */
    EnumFacing getDirection();

    /**
     * @return the current charge stored in this pedestal.
     */
    @Deprecated // use getInjectorCharge
    default int getCharge() {
        return 0;
    }

    /**
     * @return the current charge stored in this pedestal.
     */
    default long getInjectorCharge() {
        return getCharge();
    }

    /**
     * Called when crafting occurs. Use this event to clear the pedestals energy buffer.
     */
    void onCraft();
}
