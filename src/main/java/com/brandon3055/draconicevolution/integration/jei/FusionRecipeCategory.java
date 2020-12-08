package com.brandon3055.draconicevolution.integration.jei;

import com.brandon3055.draconicevolution.api.crafting.IFusionRecipe;
import com.brandon3055.draconicevolution.api.fusioncrafting.IFusionRecipeOld;
import com.brandon3055.draconicevolution.init.DEContent;
import com.brandon3055.draconicevolution.utils.LogHelper;
import com.brandon3055.draconicevolution.utils.ResourceHelperDE;
import com.brandon3055.draconicevolution.client.DETextures;


import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.IIngredients;

import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.util.Translator;
import net.minecraft.block.Blocks;
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
    public void setIngredients(IFusionRecipe recipe, IIngredients ingredients) {
        List<Ingredient> recipeIngredients = new ArrayList<>();
        recipeIngredients.add(recipe.getCatalyst());
        recipeIngredients.addAll(recipe.getIngredients());
        ingredients.setInputIngredients(recipeIngredients);
        ingredients.setOutput(VanillaTypes.ITEM, recipe.getRecipeOutput());
        LogHelper.dev("Set Ingredients");
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
        int rows = ingreds.size() / nColumns;

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
        LogHelper.dev("Set Recipe");

    }

    //    @Override
//    public void setRecipe(IRecipeLayout recipeLayout, IRecipeWrapper recipeWrapper, IIngredients ingredients) { //TODO JEI Update
//        try {
//
//            IFusionRecipe recipe = ((FusionRecipeWrapper) recipeWrapper).recipe;
//
//            IGuiItemStackGroup stackGroup = recipeLayout.getItemStacks();
//            stackGroup.init(0, true, xSize / 2 - 9, ySize / 2 - 9 - 23);
//            stackGroup.init(1, false, xSize / 2 - 9, ySize / 2 - 9 + 23);
//
//            //region Add Ingredients
//
//
//            List ingreds = recipe.getRecipeIngredients();
//            int nColumns = ingreds.size() > 16 ? 4 : 2;             //The number of ingredient columns.
//            LinkedList<MGuiList> iColumns = new LinkedList<>();         //The list of ingredient columns.
//
//            for (int i = 0; i < nColumns; i++) {
//                int x = (nColumns == 2 ? 15 + i * 128 : 7 + ((i % 2) * 17) + ((i / 2) * 126));
//                MGuiList list = new MGuiList(null, x, 8, 20, 98).setScrollingEnabled(false);
//                list.addChild(new MGuiBorderedRect(null, list.xPos, list.yPos - 1, list.xSize, list.ySize + 2).setBorderColour(0xFFAA00FF));
//                list.topPadding = list.bottomPadding = 0;
//                iColumns.add(list);
//            }
//
//            int i = 0;
//            for (Object ingredient : ingreds) {
//                ItemStack ingredStack = OreDictHelper.resolveObject(ingredient);
//                MGuiList column = iColumns.get(iColumns.size() == 4 ? i % 4 : i % 2);
//                column.addEntry((MGuiListEntryWrapper) new MGuiListEntryWrapper(null, new MGuiStackIcon(null, 0, 0, 16, 16, new StackReference(ingredStack)).setDrawHoverHighlight(true)).setLinkedObject(i));
//                column.sortEvenSpacing(true);
//                i++;
//            }
//
//            for (MGuiList list : iColumns) {
//                for (MGuiListEntry entry : list.listEntries) {
//                    stackGroup.init((Integer) entry.linkedObject + 2, true, entry.xPos - 8, entry.yPos);
//                }
//            }
//
//            stackGroup.set(ingredients);
//            //endregion
//
//            if (recipe instanceof FusionUpgradeRecipe) {
//                FusionUpgradeRecipe fRecipe = (FusionUpgradeRecipe) recipe;
//                List<ItemStack> inputs = new LinkedList<>();
//                List<ItemStack> outputs = new LinkedList<>();
//
//                for (ItemStack stack : DEJEIPlugin.iUpgradables) {
//                    if (stack.getItem() instanceof IUpgradableItem) {
//                        IUpgradableItem item = (IUpgradableItem) stack.getItem();
//                        if (item.getValidUpgrades(stack).contains(fRecipe.upgrade) && item.getMaxUpgradeLevel(stack, fRecipe.upgrade) >= fRecipe.upgradeLevel) {
//                            ItemStack input = stack.copy();
//                            ItemStack output = stack.copy();
//                            UpgradeHelper.setUpgradeLevel(input, fRecipe.upgrade, fRecipe.upgradeLevel - 1);
//                            UpgradeHelper.setUpgradeLevel(output, fRecipe.upgrade, fRecipe.upgradeLevel);
//                            inputs.add(input);
//                            outputs.add(output);
//                        }
//                    }
//                }
//
//                stackGroup.set(0, inputs);
//                stackGroup.set(1, outputs);
//            }
//
//        }
//        catch (Throwable e) {
//            e.printStackTrace();
//        }
//    }

//    @Override
//    public void drawExtras(@Nonnull Minecraft minecraft) {
//
//    }
}
