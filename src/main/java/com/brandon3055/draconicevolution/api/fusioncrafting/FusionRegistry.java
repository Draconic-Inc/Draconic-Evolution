package com.brandon3055.draconicevolution.api.fusioncrafting;

import java.util.List;

/**
 * Created by brandon3055 on 23/07/2016.
 * This interface is for internal use by Draconic Evolution and should not be implemented by other mods.
 */
@Deprecated
public interface FusionRegistry {

    void add(IFusionRecipeOld recipe);

    void remove(IFusionRecipeOld recipe);

    List<IFusionRecipeOld> getRecipes();
}
