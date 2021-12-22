package com.brandon3055.draconicevolution.api.crafting;

import com.brandon3055.brandonscore.api.TechLevel;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Created by brandon3055 on 26/11/20
 * This is simplified for 1.16+
 * Its now simply and way to access and consume the crafting ingredients (including power) and nothing more.
 * So only what is needed by IFusionRecipe
 */
public interface IFusionInventory extends IInventory {

    /**
     * Gets the stack in the catalyst slot of the core<br>
     */
    @Nonnull
    ItemStack getCatalystStack();

    /**
     * Gets the stack in the output slot of the core<br>
     */
    @Nonnull
    ItemStack getOutputStack();

    /**
     * Sets the stack in the catalyst slot of the core<br>
     */
    void setCatalystStack(@Nonnull ItemStack stack);

    /**
     * Sets the stack in the output slot of the core<br>
     */
    void setOutputStack(@Nonnull ItemStack stack);

    /**
     * Returns a list of all valid crafting injectors.
     */
    List<IFusionInjector> getInjectors();

    /**
     * @return the {@link TechLevel} of the lowest tier injector that is currently holding an item.
     */
    TechLevel getMinimumTier();

    //@formatter:off
    @Override default int getContainerSize() { return 0; }
    @Override default boolean isEmpty() { return false; }
    @Override default ItemStack getItem(int index) { return ItemStack.EMPTY; }
    @Override default ItemStack removeItem(int index, int count) { return ItemStack.EMPTY; }
    @Override default ItemStack removeItemNoUpdate(int index) { return ItemStack.EMPTY; }
    @Override default void setItem(int index, ItemStack stack) { }
    @Override default void setChanged() { }
    @Override default boolean stillValid(PlayerEntity player) { return false; }
    @Override default void clearContent() { }
    //@formatter:on
}
