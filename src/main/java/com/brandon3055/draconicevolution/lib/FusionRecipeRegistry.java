package com.brandon3055.draconicevolution.lib;

import com.brandon3055.draconicevolution.api.fusioncrafting.FusionRegistry;
import com.brandon3055.draconicevolution.api.fusioncrafting.IFusionCraftingInventory;
import com.brandon3055.draconicevolution.api.fusioncrafting.IFusionRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by brandon3055 on 23/07/2016.
 */
public class FusionRecipeRegistry implements FusionRegistry {

    private final List<IFusionRecipe> REGISTRY = new ArrayList<IFusionRecipe>();

    //region API Interface

    @Override
    public void add(IFusionRecipe recipe) {
        REGISTRY.add(recipe);
    }

    @Override
    public void remove(IFusionRecipe recipe) {
        if (REGISTRY.contains(recipe)) {
            REGISTRY.remove(recipe);
        }
    }

    @Override
    public List<IFusionRecipe> getRecipes() {
        return new ArrayList<>(REGISTRY);
    }

    //endregion

    public IFusionRecipe findRecipeForCatalyst(ItemStack catalyst) {
        if (catalyst.isEmpty()) {
            return null;
        }

        for (IFusionRecipe recipe : REGISTRY) {
            if (recipe.isRecipeCatalyst(catalyst)) {
                return recipe;
            }
        }

        return null;
    }

    public IFusionRecipe findRecipe(IFusionCraftingInventory inventory, World world, BlockPos corePos) {
        if (inventory == null || inventory.getStackInCore(0).isEmpty()) {
            return null;
        }

        for (IFusionRecipe recipe : REGISTRY) {
            if (recipe.matches(inventory, world, corePos)) {
                return recipe;
            }
        }

        return null;
    }
}
