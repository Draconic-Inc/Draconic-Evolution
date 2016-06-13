package com.brandon3055.draconicevolution.api.fusioncrafting;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by brandon3055 on 11/06/2016.
 */
public class FusionRecipeRegistry {
    public static List<IFusionRecipe> recipeRegistry = new ArrayList<IFusionRecipe>();//todo make private

    public static void registerRecipe(IFusionRecipe recipe){
        recipeRegistry.add(recipe);
    }
}
