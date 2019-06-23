package com.brandon3055.draconicevolution.api.fusioncrafting;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by brandon3055 on 11/06/2016.
 */
public interface IFusionRecipe {

    /**
     * @param catalyst Nullable recipe catalyst used when finding a recipe that matches a specific catalyst.
     * @return if catalyst == null return the recipe output. Otherwise return the recipe output for the given catalyst (can return null)
     */
    ItemStack getRecipeOutput(@Nullable ItemStack catalyst);

    /**
     * @return true of the catalyst provided is valid for this recipe.
     */
    boolean isRecipeCatalyst(ItemStack catalyst);

    /**
     * @return the ingredients required for this recipe.
     * These can be Items, Blocks, ItemStacks or ore dictionary strings. (Or a mix of any)
     */
    List getRecipeIngredients();

    /**
     * Currently only used for JEI integration.
     *
     * @return The pedestal tier required for this recipe.
     */
    int getRecipeTier();

    /**
     * This is currently only used for JEI integration.
     *
     * @return the recipe catalyst
     */
    ItemStack getRecipeCatalyst();

    /**
     * @param inventory The crafting inventory. Note this does not extend IInventory because it should not need to
     * @param world     The world
     * @param pos       The position of the Fusion Crafting Core
     * @return true if the items in the IFusionCraftingInventory inventory match this recipe.
     * <p>
     * Note: You must also make sure there are no extra stacks that are not part of the recipe.
     * Do not check the pedestal tier here. Thats handles by getCraftStatus.
     */
    boolean matches(IFusionCraftingInventory inventory, World world, BlockPos pos);

    /**
     * Called at the end of the crafting process to actually craft the item. This is where you should consume the required
     * items and add the result to the output slot in the inventory. Remember to respect item containers when consuming items.
     *
     * @param inventory The crafting inventory. Note this does not extend IInventory because it should not need to
     * @param world     The world
     * @param pos       The position of the Fusion Crafting Core
     */
    void craft(IFusionCraftingInventory inventory, World world, BlockPos pos);

    /**
     * This is the energy cost for each item to be fused (the items in the pedestals). So this will be multiplied
     * by the number of crafting ingredients (not including the item in the crafting core)
     */
    @Deprecated //Use the long method getIngredientEnergyCost()
    default int getEnergyCost() {
        return 0;
    }

    /**
     * This is the energy cost for each item to be fused (the items in the pedestals). So this will be multiplied
     * by the number of crafting ingredients (not including the item in the crafting core)
     */
    default long getIngredientEnergyCost() {
        return getEnergyCost();
    }

    /**
     * This method is called every tick during the crafting process.
     *
     * @param inventory The crafting inventory. Note this does not extend IInventory because it should not need to
     * @param world     The world
     * @param pos       The position of the Fusion Crafting Core
     */
    void onCraftingTick(IFusionCraftingInventory inventory, World world, BlockPos pos);

    /**
     * Check if you can craft (Assume matches() has already been checked and returned true)
     * Use this to check the pedestal tiers and do anything else you wish to do.
     * <p>
     * <pre>
     * Return "true" if can craft.
     * Return "tierLow" if the pedestal tier is to low.
     * Return "some.unlocalized.message" This can be any short custom message. It will be translated with I18n
     * Message will be shown in crafting gui.
     * </pre>
     */
    String canCraft(IFusionCraftingInventory inventory, World world, BlockPos pos);
}
