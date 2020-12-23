package com.brandon3055.draconicevolution.integration.jei;

import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.brandonscore.client.utils.GuiHelper;
import com.brandon3055.brandonscore.utils.Utils;
import com.brandon3055.draconicevolution.api.crafting.IFusionRecipe;
import com.brandon3055.draconicevolution.init.DEContent;
import com.brandon3055.draconicevolution.utils.ResourceHelperDE;
import com.brandon3055.draconicevolution.client.DETextures;


import com.mojang.blaze3d.matrix.MatrixStack;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;

import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by brandon3055 on 24/07/2016.
 */
public class FusionRecipeCategory implements IRecipeCategory<IFusionRecipe> {

    private final IDrawable background;
    private final IDrawable icon;
    //    private final ICraftingGridHelper craftingGridHelper;
    private final String localizedName;
    private int xSize = 164;
    private int ySize = 111;

    public FusionRecipeCategory(IGuiHelper guiHelper) {
        background = guiHelper.createDrawable(ResourceHelperDE.getResource(DETextures.GUI_JEI_FUSION), 0, 0, xSize, ySize);
        localizedName = I18n.format(DEContent.crafting_core.getTranslationKey());

        icon = guiHelper.createDrawableIngredient(new ItemStack(DEContent.crafting_core));
//        craftingGridHelper = guiHelper.createCraftingGridHelper(craftInputSlot1);
    }

    @Nonnull
    @Override
    public ResourceLocation getUid() {
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
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public Class<? extends IFusionRecipe> getRecipeClass() {
        return IFusionRecipe.class;
    }

    @Override
    public void draw(IFusionRecipe recipe, MatrixStack matrixStack, double mouseX, double mouseY) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.fontRenderer != null) {
            TechLevel tier = recipe.getRecipeTier();
            int colour = tier.index == 0 ? 5263615 : (tier.index == 1 ? 8388863 : (tier.index == 2 ? 16737792 : 5263440));

            GuiHelper.drawCenteredString(mc.fontRenderer, matrixStack, I18n.format("gui.de.tier." + recipe.getRecipeTier().name(), new Object[0]), this.xSize / 2, 5, colour, false);
            GuiHelper.drawCenteredString(mc.fontRenderer, matrixStack, I18n.format("gui.de.energyCost.txt"), this.xSize / 2, this.ySize - 20, 4474111, false);
            GuiHelper.drawCenteredString(mc.fontRenderer, matrixStack, Utils.addCommas(recipe.getEnergyCost()) + " OP", this.xSize / 2, this.ySize - 10, 4500223, false);
        }
    }

    @Override
    public void setIngredients(IFusionRecipe recipe, IIngredients ingredients) {
        List<Ingredient> recipeIngredients = new ArrayList<>();
        recipeIngredients.add(recipe.getCatalyst());
        recipeIngredients.addAll(recipe.getIngredients());
        ingredients.setInputIngredients(recipeIngredients);
        ingredients.setOutput(VanillaTypes.ITEM, recipe.getRecipeOutput());
    }

    @Override
    public void setRecipe(IRecipeLayout layout, IFusionRecipe recipe, IIngredients ingredients) {
        IGuiItemStackGroup stackGroup = layout.getItemStacks();

        stackGroup.init(0, true, xSize / 2 - 9, ySize / 2 - 9 - 23);
        stackGroup.init(1, false, xSize / 2 - 9, ySize / 2 - 9 + 23);

        List<Ingredient> ingreds = recipe.getIngredients();
        int nColumns = ingreds.size() > 16 ? 4 : 2;
        int xc = xSize / 2 - 9;
        int yc = ySize / 2 - 9;
        int rows = (int) Math.ceil(ingreds.size() / (double) nColumns);

        for (int i = 0; i < ingreds.size(); i++) {
            int side = (i % nColumns) >= nColumns / 2 ? 1 : -1;
            int offset = nColumns == 2 ? 0 : i % 2 == 0 ? -1 : 1;
            int row = i / nColumns;

            int xPos = xc + (side * (60 + (offset * 10)));
            int yPos = yc;
            if (rows > 1) {
                yPos = (yc - 42) + ((84 / (rows - 1)) * row);
            }

            stackGroup.init(2 + i, true, xPos, yPos);
        }

        stackGroup.set(ingredients);
    }
}
