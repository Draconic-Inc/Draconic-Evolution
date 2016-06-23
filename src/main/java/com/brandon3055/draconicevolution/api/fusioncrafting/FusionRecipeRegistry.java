package com.brandon3055.draconicevolution.api.fusioncrafting;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by brandon3055 on 11/06/2016.
 */
public class FusionRecipeRegistry {
    public static List<IFusionRecipe> recipeRegistry = new ArrayList<IFusionRecipe>();//todo make private

    public static void registerRecipe(IFusionRecipe recipe) {
        recipeRegistry.add(recipe);
    }

    public static IFusionRecipe findRecipeForCatalyst(ItemStack catalyst) {
        if (catalyst == null){
            return null;
        }

        for (IFusionRecipe recipe : recipeRegistry) {
            if (recipe.isRecipeCatalyst(catalyst)) {
                return recipe;
            }
        }

        return null;
    }

    public static IFusionRecipe findRecipe(IFusionCraftingInventory inventory, World world, BlockPos corePos) {
        if (inventory == null || inventory.getStackInCore(0) == null){
            return null;
        }

        for (IFusionRecipe recipe : recipeRegistry) {
            if (recipe.matches(inventory, world, corePos)) {
                return recipe;
            }
        }

        return null;
    }
}
