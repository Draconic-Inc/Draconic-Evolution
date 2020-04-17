package com.brandon3055.draconicevolution.integration.jei;

import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.utils.ResourceHelperDE;
import com.brandon3055.draconicevolution.utils.DETextures;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;

import javax.annotation.Nonnull;

/**
 * Created by brandon3055 on 24/07/2016.
 */
public class FusionRecipeCategory extends BlankRecipeCategory { //TODO Fix animation in PI

    private final IDrawable background;
    private final String localizedName;
    private int xSize = 164;
    private int ySize = 111;

    public FusionRecipeCategory(IGuiHelper guiHelper) {
        background = guiHelper.createDrawable(ResourceHelperDE.getResource(DETextures.GUI_JEI_FUSION), 0, 0, xSize, ySize);
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

    @Override
    public String getModName() {
        return DraconicEvolution.MODID;
    }

    @Nonnull
    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, IRecipeWrapper recipeWrapper, IIngredients ingredients) { //TODO JEI Update
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
    }

    @Override
    public void drawExtras(@Nonnull Minecraft minecraft) {

    }
}
