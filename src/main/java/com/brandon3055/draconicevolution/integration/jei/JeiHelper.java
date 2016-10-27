package com.brandon3055.draconicevolution.integration.jei;

import com.brandon3055.draconicevolution.integration.ModHelper;
import mezz.jei.api.IRecipeRegistry;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.gui.Focus;
import mezz.jei.gui.RecipeLayout;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StringUtils;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.Optional;

import java.util.ArrayList;
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

    @Optional.Method(modid = "JEI")
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

    @Optional.Method(modid = "JEI")
    private static List<IRecipeRenderer> getRenderers(ItemStack result) {
        List<IRecipeRenderer> renderers = new ArrayList<>();

        IRecipeRegistry registry = DEJEIPlugin.jeiRuntime.getRecipeRegistry();
        List<IRecipeCategory> categories = registry.getRecipeCategoriesWithOutput(result);

        for (IRecipeCategory category : categories) {
            List<Object> recipes = registry.getRecipesWithOutput(category, result);
            for (Object recipe : recipes) {
                IRecipeHandler handler = registry.getRecipeHandler(recipe.getClass());
                if (handler != null) {
                    try {
                        IRecipeWrapper wrapper = handler.getRecipeWrapper(recipe);
                        renderers.add(new RecipeRenderer(recipes.indexOf(recipe), category, wrapper, registry, result));
                    }
                    catch (Throwable t) { t.printStackTrace(); }
                }
            }
        }

        return renderers;
    }

    //endregion

    //region IRecipeRenderer

    private static class RecipeRenderer implements IRecipeRenderer {

        private final int index;
        private final IRecipeCategory category;
        private final IRecipeWrapper wrapper;
        private final IRecipeRegistry registry;
        private final ItemStack result;
        private RecipeLayout layout;
        private int width;
        private int height;
        private int xPos = 0;
        private int yPos = 0;
        private String title;

        public RecipeRenderer(int index, IRecipeCategory category, IRecipeWrapper wrapper, IRecipeRegistry registry, ItemStack result) {
            this.index = index;
            this.category = category;
            this.wrapper = wrapper;
            this.registry = registry;
            this.result = result;
//            IFocus<?> f = new Focus<Object>(result);
            this.layout = new RecipeLayout(index, 0, 0, category, wrapper, registry.createFocus(IFocus.Mode.OUTPUT, result));
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
                this.layout = new RecipeLayout(index, xPos, yPos, category, wrapper, registry.createFocus(IFocus.Mode.OUTPUT, result));
                layout.setRecipeTransferButton(-1000, -1000);
                this.xPos = xPos;
                this.yPos = yPos;
            }

            layout.draw(mc, mouseX, mouseY);
        }

        @Override
        public boolean handleClick(Minecraft minecraft, int mouseX, int mouseY, int mouseButton) {
            Focus focus = null;//layout.getFocusUnderMouse(mouseX, mouseY);
            if (focus != null) {
                if (focus.getValue() instanceof ItemStack) {
                    if (mouseButton == 0) {
                        DEJEIPlugin.jeiRuntime.getRecipesGui().showRecipes((ItemStack) focus.getValue());
                    }
                    else if (mouseButton == 1) {
                        DEJEIPlugin.jeiRuntime.getRecipesGui().showUses((ItemStack) focus.getValue());
                    }
                }
                else if (focus.getValue() instanceof FluidStack) {
                    if (mouseButton == 0) {
                        DEJEIPlugin.jeiRuntime.getRecipesGui().showRecipes((FluidStack) focus.getValue());
                    }
                    else if (mouseButton == 1) {
                        DEJEIPlugin.jeiRuntime.getRecipesGui().showUses((FluidStack) focus.getValue());
                    }
                }
            }
            return layout.handleClick(minecraft, mouseX, mouseY, mouseButton);
        }

        @Override
        public String getTitle() {
            return title;
        }
    }

    //endregion

}
