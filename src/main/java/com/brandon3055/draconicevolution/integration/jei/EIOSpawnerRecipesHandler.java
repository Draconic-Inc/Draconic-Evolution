package com.brandon3055.draconicevolution.integration.jei;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;

public class EIOSpawnerRecipesHandler implements IRecipeHandler<EIOSpawnerRecipesWrapper> {
	private final IGuiHelper guiHelper;

	public EIOSpawnerRecipesHandler(IGuiHelper guiHelper) {
		this.guiHelper = guiHelper;
	}

	@Override
	public Class<EIOSpawnerRecipesWrapper> getRecipeClass() {
		return EIOSpawnerRecipesWrapper.class;
	}

	@Override
	public String getRecipeCategoryUid() {
		return VanillaRecipeCategoryUid.CRAFTING;
	}

	@Override
	public String getRecipeCategoryUid(EIOSpawnerRecipesWrapper recipe) {
		return VanillaRecipeCategoryUid.CRAFTING;
	}

	@Override
	public IRecipeWrapper getRecipeWrapper(EIOSpawnerRecipesWrapper wrapper) {
		return wrapper;
	}

	@Override
	public boolean isRecipeValid(EIOSpawnerRecipesWrapper recipe) {
		return true;
	}
}
