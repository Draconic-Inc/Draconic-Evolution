package com.brandon3055.draconicevolution.api.crafting;

import com.brandon3055.draconicevolution.api.fusioncrafting.ICraftingInjector;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Created by brandon3055 on 26/11/20
 */
//TODO a lot of this will need to be re written
public interface IFusionInventory extends IInventory {

//Will use seperate methods when i re write
//    /**
//     * Gets the stack in the catalyst slot of the core<br>
//     */
//    @Nonnull
//    ItemStack getCatalystStack();
//
//    /**
//     * Gets the stack in the output slot of the core<br>
//     */
//    @Nonnull
//    ItemStack getOutputStack();
//
//
//    /**
//     * Sets the stack in the catalyst slot of the core<br>
//     */
//    void setCatalystStack(@Nonnull ItemStack stack);
//
//    /**
//     * Sets the stack in the output slot of the core<br>
//     */
//    void setOutputStack(@Nonnull ItemStack stack);

    /**
     * Gets the stack in the fusion crafting core. Also known as the crafting catalyst.<br>
     * slot 0 = Input Slot<br>
     * slot 1 == output slot
     */
    @Nonnull
    ItemStack getStackInCore(int slot);

    /**
     * Sets the stack in the specified slot.<br>
     * slot 0 = Input Slot<br>
     * slot 1 == output slot
     */
    void setStackInCore(int slot, @Nonnull ItemStack stack);

    /**
     * @return The required =er ingredient energy for the current crafting recipe or 0 if there is no active recipe.
     */
    long getIngredientEnergyCost();

    /**
     * Returns a list of all valid crafting injectors.
     */
    List<ICraftingInjector> getInjectors();


    /**
     * @return true if currently crafting an item.
     */
    boolean craftingInProgress();

    /**
     * @return The current crafting stage (0 -> 1000 = charging, 1000 -> 2000 = crafting)
     */
    int getCraftingStage();

    BlockPos getPos();

    //@formatter:off
    @Override default int getSizeInventory() { return 0; }
    @Override default boolean isEmpty() { return false; }
    @Override default ItemStack getStackInSlot(int index) { return null; }
    @Override default ItemStack decrStackSize(int index, int count) { return null; }
    @Override default ItemStack removeStackFromSlot(int index) { return null; }
    @Override default void setInventorySlotContents(int index, ItemStack stack) { }
    @Override default void markDirty() { }
    @Override default boolean isUsableByPlayer(PlayerEntity player) { return false; }
    @Override default void clear() { }
    //@formatter:on
}
