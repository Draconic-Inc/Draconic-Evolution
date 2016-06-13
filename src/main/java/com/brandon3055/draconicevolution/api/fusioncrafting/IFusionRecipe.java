package com.brandon3055.draconicevolution.api.fusioncrafting;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Created by brandon3055 on 11/06/2016.
 *
 */
public interface IFusionRecipe {

    /**
     * @param inventory The crafting inventory. Note this dose not extend IInventory because it should not need to
     * @param world The world
     * @param pos The position of the Fusion Crafting Core
     * @return true if the items in the IFusionCraftingInventory inventory match this recipe.
     *
     * For the sake of sanity do not just return true if the required items are present. Also make sure there are no extra
     * stacks that are not part of the recipe.
     * Also remember to check that the tier of the crafting pedestals is high enough for your recipe because there
     * are no other checks for that.
     */
    boolean matches(IFusionCraftingInventory inventory, World world, BlockPos pos);

    /**
     * Called at the end of the crafting process to actually craft the item. This is where you should consume the required
     * items and replace the item in the crafting core with the result. Remember to respect item containers when consuming items.
     *
     * @param inventory The crafting inventory. Note this dose not extend IInventory because it should not need to
     * @param world The world
     * @param pos The position of the Fusion Crafting Core
     */
    void craft(IFusionCraftingInventory inventory, World world, BlockPos pos);

    /**
     * This is the energy cost for each item to be fused (the items in the pedestals). So this will be multiplied
     * by the number of crafting ingredients (not including the item in the crafting core)
     * */
    int getEnergyCost();

    /**
     * This method is called every tick during the crafting process.
     *
     * @param inventory The crafting inventory. Note this dose not extend IInventory because it should not need to
     * @param world The world
     * @param pos The position of the Fusion Crafting Core
     * */
    void onCraftingTick(IFusionCraftingInventory inventory, World world, BlockPos pos);
}
