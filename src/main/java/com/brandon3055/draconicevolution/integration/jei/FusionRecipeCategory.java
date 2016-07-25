package com.brandon3055.draconicevolution.integration.jei;

import com.brandon3055.brandonscore.client.utils.GuiHelper;
import com.brandon3055.draconicevolution.api.OreDictHelper;
import com.brandon3055.draconicevolution.api.fusioncrafting.IFusionRecipe;
import com.brandon3055.draconicevolution.helpers.ResourceHelperDE;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.recipe.BlankRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;

import javax.annotation.Nonnull;

/**
 * Created by brandon3055 on 24/07/2016.
 */
public class FusionRecipeCategory extends BlankRecipeCategory {

    private final IDrawable background;
    private final String localizedName;
    private int xSize = 164;
    private int ySize = 111;
    private IFusionRecipe recipeCache = null;

    public FusionRecipeCategory(IGuiHelper guiHelper) {
        background = guiHelper.createDrawable(ResourceHelperDE.getResource("textures/gui/jeiFusionBackground.png"), 0, 0, xSize, ySize);
        localizedName = I18n.format("gui.de.fusionCraftingCore.name");
    }

    @Nonnull
    @Override
    public String getUid() {
        return RecipeCategoryUids.FUSION_CRAFTING;
    }

    @Nonnull
    @Override
    public String getTitle() {
        return localizedName;
    }

    @Nonnull
    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public void setRecipe(@Nonnull IRecipeLayout recipeLayout, @Nonnull IRecipeWrapper recipeWrapper) {
        if (!(recipeWrapper instanceof FusionRecipeWrapper)) {
            return;
        }

        FusionRecipeWrapper recipe = (FusionRecipeWrapper) recipeWrapper;
        recipeCache = recipe.recipe;

        IGuiItemStackGroup stackGroup = recipeLayout.getItemStacks();
        stackGroup.init(0, true, xSize / 2 - 9, ySize / 2 - 9 - 23);
        stackGroup.setFromRecipe(0, recipe.recipe.getRecipeCatalyst());
        stackGroup.init(1, false, xSize / 2 - 9, ySize / 2 - 9 + 23);
        stackGroup.setFromRecipe(1, recipe.recipe.getRecipeOutput(null));


        //region Add Ingredients

        int centerX = xSize / 2;
        int centerY = ySize / 2;

        for (int i = 0; i < recipe.recipe.getRecipeIngredients().size(); i++) {
            boolean isLeft = i % 2 == 0;
            boolean isOdd = recipe.recipe.getRecipeIngredients().size() % 2 == 1;
            int sideCount = recipe.recipe.getRecipeIngredients().size() / 2;

            if (isOdd && !isLeft) {
                sideCount--;
            }

            int xPos;
            int yPos;

            if (isLeft) {
                xPos = centerX - 65;
                int ySize = 80 / Math.max(sideCount - (isOdd ? 0 : 1), 1);
                int sideIndex = i / 2;

                if (sideCount <= 1 && (!isOdd || recipe.recipe.getRecipeIngredients().size() == 1)) {
                    sideIndex = 1;
                    ySize = 40;
                }

                yPos = centerY - 40 + (sideIndex * ySize);
            } else {
                xPos = centerX + 63;
                int ySize = 80 / Math.max(sideCount - (isOdd ? 0 : 1), 1);
                int sideIndex = i / 2;

                if (isOdd) {
                    sideCount++;
                }

                if (sideCount <= 1) {
                    sideIndex = 1;
                    ySize = 40;
                }

                yPos = centerY - 40 + (sideIndex * ySize);
            }

            stackGroup.init(i + 2, true, xPos - 8, yPos - 8);
            stackGroup.setFromRecipe(i + 2, OreDictHelper.resolveObject(recipe.recipe.getRecipeIngredients().get(i)));
        }

        //endregion
    }

    @Override
    public void drawExtras(@Nonnull Minecraft minecraft) {
        GuiHelper.drawBorderedRect(8, 6, 20, 100, 1, 0xFF000000, 0xFFAA00FF);
        GuiHelper.drawBorderedRect(xSize - 28, 6, 20, 100, 1, 0xFF000000, 0xFFAA00FF);
        GuiHelper.drawBorderedRect((xSize / 2) - 10, 22, 20, 66, 1, 0xFF000000, 0xFF00FFFF);
    }
}
