package com.brandon3055.draconicevolution.api.fusioncrafting;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by brandon3055 on 11/06/2016.
 * This API allows you to register custom IFusionRecipe's
 */
@Deprecated
public class FusionRecipeAPI {

    /**
     * The fusion recipe registry. Assigned by Draconic Evolution during pre-initialization.<br>
     * This field should not be accessed directly. Instead use the methods provided in this class.
     */
    public static FusionRegistry registry = null;

    /**
     * Adds a recipe to the registry.<br>
     * This should only be called during initialization.
     */
    public static void addRecipe(IFusionRecipeOld recipe) {
        if (registry != null) {
            registry.add(recipe);
        }
    }

    /**
     * Can be used to remove a recipe from the registry.<br>
     * If for whatever reason you wish to remove a recipe from another mod do so in during post initialisation.
     */
    public static void removeRecipe(IFusionRecipeOld recipe) {
        if (registry != null) {
            registry.remove(recipe);
        }
    }

    /**
     * @return a list of all currently registered fusion crafting recipes.
     */
    public static List<IFusionRecipeOld> getRecipes() {
        if (registry != null) {
            return registry.getRecipes();
        }
        return new ArrayList<IFusionRecipeOld>();
    }
}
