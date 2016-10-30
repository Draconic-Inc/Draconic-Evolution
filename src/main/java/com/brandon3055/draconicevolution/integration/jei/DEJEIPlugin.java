package com.brandon3055.draconicevolution.integration.jei;

import com.brandon3055.draconicevolution.DEFeatures;
import com.brandon3055.draconicevolution.api.ITellJEIToGetOutOfTheWay;
import com.brandon3055.draconicevolution.client.gui.GuiFusionCraftingCore;
import com.brandon3055.draconicevolution.lib.RecipeManager;
import mezz.jei.api.*;
import mezz.jei.api.gui.IAdvancedGuiHandler;
import mezz.jei.api.ingredients.IModIngredientRegistration;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.ArrayList;

/**
 * Created by brandon3055 on 24/07/2016.
 */
@JEIPlugin
public class DEJEIPlugin implements IModPlugin {

    public static IJeiHelpers jeiHelpers = null;
    public static IJeiRuntime jeiRuntime = null;

    public DEJEIPlugin() {}

    @Override
    public void registerItemSubtypes(ISubtypeRegistry subtypeRegistry) {

    }

    @Override
    public void registerIngredients(IModIngredientRegistration registry) {
    }

    @Override
    public void register(IModRegistry registry) {
        IJeiHelpers jeiHelpers = registry.getJeiHelpers();
        IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

        registry.addRecipeCategories(new FusionRecipeCategory(guiHelper));
        registry.addRecipeHandlers(new FusionRecipeHandler());
        registry.addRecipeCategoryCraftingItem(new ItemStack(DEFeatures.fusionCraftingCore), RecipeCategoryUids.FUSION_CRAFTING);
        registry.addRecipeClickArea(GuiFusionCraftingCore.class, 81, 45, 18, 22, RecipeCategoryUids.FUSION_CRAFTING);

        registry.addRecipes(RecipeManager.FUSION_REGISTRY.getRecipes());

        DEJEIPlugin.jeiHelpers = jeiHelpers;

        registry.addAdvancedGuiHandlers(new IAdvancedGuiHandler() {
            @Override
            public Class getGuiContainerClass() {
                return ITellJEIToGetOutOfTheWay.class;
            }

            @Nullable
            @Override
            public java.util.List<Rectangle> getGuiExtraAreas(GuiContainer guiContainer) {
                if (guiContainer instanceof ITellJEIToGetOutOfTheWay) {
                    return ((ITellJEIToGetOutOfTheWay) guiContainer).getGuiExtraAreas();
                }
                return new ArrayList<>();
            }
        });
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime iJeiRuntime) {
        jeiRuntime = iJeiRuntime;
    }

    public static void reloadJEI() {
        if (jeiHelpers != null) {
            jeiHelpers.reload();
        }
    }
}
