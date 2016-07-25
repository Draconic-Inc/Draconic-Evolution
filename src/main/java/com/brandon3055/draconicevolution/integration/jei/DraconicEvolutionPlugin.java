package com.brandon3055.draconicevolution.integration.jei;

import com.brandon3055.draconicevolution.DEFeatures;
import com.brandon3055.draconicevolution.client.gui.GuiFusionCraftingCore;
import com.brandon3055.draconicevolution.lib.RecipeManager;
import mezz.jei.api.*;
import net.minecraft.item.ItemStack;

/**
 * Created by brandon3055 on 24/07/2016.
 */
@JEIPlugin
public class DraconicEvolutionPlugin implements IModPlugin {

    public static IJeiHelpers jeiHelpers = null;

    public DraconicEvolutionPlugin() {}

    @Override
    public void register(IModRegistry registry) {
        IJeiHelpers jeiHelpers = registry.getJeiHelpers();
        IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

        registry.addRecipeCategories(new FusionRecipeCategory(guiHelper));
        registry.addRecipeHandlers(new FusionRecipeHandler());
        registry.addRecipeCategoryCraftingItem(new ItemStack(DEFeatures.fusionCraftingCore), RecipeCategoryUids.FUSION_CRAFTING);
        registry.addRecipeClickArea(GuiFusionCraftingCore.class, 81, 45, 18, 22, RecipeCategoryUids.FUSION_CRAFTING);

        registry.addRecipes(RecipeManager.FUSION_REGISTRY.getRecipes());

        this.jeiHelpers = jeiHelpers;
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime iJeiRuntime) {

    }

    public static void reloadJEI() {
        if (jeiHelpers != null) {
            jeiHelpers.reload();
        }
    }
}
