package com.brandon3055.draconicevolution.integration.jei;

import com.brandon3055.draconicevolution.api.fusioncrafting.IFusionRecipe;
import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;

import javax.annotation.Nonnull;

/**
 * Created by brandon3055 on 24/07/2016.
 */
public class FusionRecipeHandler implements IRecipeHandler<IFusionRecipe> {

    public FusionRecipeHandler() {
    }

    @Nonnull
    @Override
    public Class<IFusionRecipe> getRecipeClass() {
        return IFusionRecipe.class;
    }


    @Nonnull
    @Override
    public String getRecipeCategoryUid(@Nonnull IFusionRecipe recipe) {
        return RecipeCategoryUids.FUSION_CRAFTING;
    }

    @Nonnull
    @Override
    public IRecipeWrapper getRecipeWrapper(@Nonnull IFusionRecipe recipe) {
        return new FusionRecipeWrapper(recipe);
    }

    @Override
    public boolean isRecipeValid(@Nonnull IFusionRecipe recipe) {
        return true;
    }
}
