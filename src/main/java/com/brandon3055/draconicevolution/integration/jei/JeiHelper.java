package com.brandon3055.draconicevolution.integration.jei;

import com.brandon3055.draconicevolution.integration.ModHelper;
import mezz.jei.api.IRecipeRegistry;
import mezz.jei.api.gui.IRecipeLayoutDrawable;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StringUtils;
import net.minecraftforge.fml.common.Optional;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by brandon3055 on 21/09/2016.
 */
public class JeiHelper {//TODO This Is Now Borked

    //region JEI Checks

    public static boolean jeiAvailable() {
        if (!ModHelper.isJEIInstalled) {
            return false;
        }

        return checkJEDRuntime();
    }

    @Optional.Method(modid = "jei")
    public static boolean checkJEDRuntime() {
        return DEJEIPlugin.jeiRuntime != null;
    }

    //endregion

    //region Get Renderers

    public static List<IRecipeRenderer> getRecipeRenderers(ItemStack result) {
        if (!jeiAvailable()) {
            return null;
        }

        return getRenderers(result);
    }

    @Optional.Method(modid = "jei")
    private static List<IRecipeRenderer> getRenderers(ItemStack result) {
        List<IRecipeRenderer> renderers = new ArrayList<>();

        IRecipeRegistry registry = DEJEIPlugin.jeiRuntime.getRecipeRegistry();
        List<IRecipeCategory> categories = new LinkedList<>();
//        categories.addAll(registry.getRecipeCategories(registry.createFocus(IFocus.Mode.INPUT, result)));//getRecipeCategoriesWithOutput(result);TODO Verify this is correct.
        categories.addAll(registry.getRecipeCategories(registry.createFocus(IFocus.Mode.OUTPUT, result)));

        for (IRecipeCategory category : categories) {
            List wrappers = registry.getRecipeWrappers(category, registry.createFocus(IFocus.Mode.OUTPUT, result));
            for (Object wrapper : wrappers) {
                try {
                    renderers.add(new RecipeRenderer(category, (IRecipeWrapper) wrapper, result));
                }
                catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        }

        return renderers;
    }

    //endregion

    //region IRecipeRenderer

    private static class RecipeRenderer implements IRecipeRenderer {

        private IRecipeLayoutDrawable recipeLayout;
        private int width;
        private int height;
        private int xPos = 0;
        private int yPos = 0;
        private String title;

        public RecipeRenderer(IRecipeCategory category, IRecipeWrapper wrapper, ItemStack result) {
            IFocus<?> f = DEJEIPlugin.jeiRuntime.getRecipeRegistry().createFocus(IFocus.Mode.OUTPUT, result);//new Focus<Object>(result);
            this.recipeLayout = DEJEIPlugin.jeiRuntime.getRecipeRegistry().createRecipeLayoutDrawable(category, wrapper, f);
            this.width = category.getBackground().getWidth();
            this.height = category.getBackground().getHeight();
            this.title = category.getTitle();
            if (StringUtils.isNullOrEmpty(this.title)) {
                this.title = "[Unknown Crafting Type]";
            }
        }

        @Override
        public int getWidth() {
            return width;
        }

        @Override
        public int getHeight() {
            return height;
        }

        @Override
        public void render(Minecraft mc, int xPos, int yPos, int mouseX, int mouseY) {
            if (this.xPos != xPos || this.yPos != yPos) {
                this.xPos = xPos;
                this.yPos = yPos;
                recipeLayout.setPosition(xPos, yPos);
            }

            recipeLayout.draw(mc, mouseX, mouseY);
        }

        @Override
        public boolean handleClick(Minecraft minecraft, int mouseX, int mouseY, int mouseButton) {
            Object clicked = recipeLayout.getIngredientUnderMouse(mouseX, mouseY);

            if (clicked != null) {
                IFocus f = DEJEIPlugin.jeiRuntime.getRecipeRegistry().createFocus(mouseButton == 0 ? IFocus.Mode.OUTPUT : IFocus.Mode.INPUT, clicked);
                DEJEIPlugin.jeiRuntime.getRecipesGui().show(f);
            }

            return false;//layout.handleClick(minecraft, mouseX, mouseY, mouseButton);
        }

        @Override
        public String getTitle() {
            return title;
        }
    }

    //endregion

}
