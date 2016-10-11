package com.brandon3055.draconicevolution.lib;

import com.brandon3055.brandonscore.config.ModFeatureParser;
import com.brandon3055.draconicevolution.DEConfig;
import com.brandon3055.draconicevolution.api.OreDictHelper;
import com.brandon3055.draconicevolution.api.fusioncrafting.FusionRecipeAPI;
import com.brandon3055.draconicevolution.api.fusioncrafting.IFusionRecipe;
import com.brandon3055.draconicevolution.api.fusioncrafting.SimpleFusionRecipe;
import com.brandon3055.draconicevolution.items.ToolUpgrade;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import java.util.ArrayList;
import java.util.List;

import static com.brandon3055.draconicevolution.lib.RecipeManager.RecipeDifficulty.ALL;

/**
 * Created by brandon3055 on 20/07/2016.
 */
public class RecipeManager {

    public static final FusionRecipeRegistry FUSION_REGISTRY = new FusionRecipeRegistry();

    private static List<IRecipe> activeCrafting = new ArrayList<IRecipe>();
    private static List<IFusionRecipe> activeFusion = new ArrayList<IFusionRecipe>();

    //region Initialization

    /**
     * Creates the FusionRecipeRegistry and Initializes the FusionRecipeAPI
     */
    public static void initialize() {
        FusionRecipeAPI.registry = FUSION_REGISTRY;
        loadRecipes();
    }

    /**
     * Loads all recipes from {@link DERecipes} If recipes have already been loaded
     * it will first remove all currently loaded recipes before reloading.
     */
    public static void loadRecipes() {
        if (!activeCrafting.isEmpty()){
            for (IRecipe recipe : activeCrafting) {
                CraftingManager.getInstance().getRecipeList().remove(recipe);
            }
        }

        activeCrafting.clear();

        if (!activeFusion.isEmpty()){
            for (IFusionRecipe recipe : activeFusion) {
                FUSION_REGISTRY.remove(recipe);
            }
        }

        activeFusion.clear();

        ToolUpgrade.addUpgrades();
        DERecipes.addRecipes();
    }

    //endregion

    //region Registration

    public static void addShaped(RecipeDifficulty difficulty, Block result, Object... recipe) {
        addShaped(difficulty, new ItemStack(result), recipe);
    }

    public static void addShaped(RecipeDifficulty difficulty, Item result, Object... recipe) {
        addShaped(difficulty, new ItemStack(result), recipe);
    }

    public static void addShaped(RecipeDifficulty difficulty, ItemStack result, Object... recipe) {
        if (difficulty != ALL && RecipeDifficulty.getDifficulty() != difficulty) {
            return;
        }

        boolean isOre = false;

        if (!isEnabled(result)){
            return;
        }

        for (int i = 3; i < recipe.length; i++){
            if (recipe[i] instanceof String) {
                isOre = true;
            }

            if (!isEnabled(recipe[i])){
                return;
            }
        }

        if (isOre) {
            IRecipe iRecipe = new ShapedOreRecipe(result, recipe);
            activeCrafting.add(iRecipe);
            GameRegistry.addRecipe(iRecipe);
        }
        else {
            activeCrafting.add(GameRegistry.addShapedRecipe(result, recipe));
        }
    }

    public static void addShapeless(RecipeDifficulty difficulty, Block result, Object... recipe) {
        addShapeless(difficulty, new ItemStack(result), recipe);
    }

    public static void addShapeless(RecipeDifficulty difficulty, Item result, Object... recipe) {
        addShapeless(difficulty, new ItemStack(result), recipe);
    }

    public static void addShapeless(RecipeDifficulty difficulty, ItemStack result, Object... recipe) {
        if (difficulty != ALL && RecipeDifficulty.getDifficulty() != difficulty) {
            return;
        }

        boolean isOre = false;

        if (!isEnabled(result)){
            return;
        }

        for (int i = 0; i < recipe.length; i++){
            if (recipe[i] instanceof String) {
                isOre = true;
            }

            if (!isEnabled(recipe[i])){
                return;
            }
        }

        if (isOre) {
            IRecipe iRecipe = new ShapelessOreRecipe(result, recipe);
            activeCrafting.add(iRecipe);
            GameRegistry.addRecipe(iRecipe);
        }
        else {
            List<ItemStack> list = new ArrayList<ItemStack>();

            for (Object object : recipe) {
                if (object instanceof ItemStack) {
                    list.add(((ItemStack)object).copy());
                }
                else if (object instanceof Item) {
                    list.add(new ItemStack((Item)object));
                }
                else {
                    if (!(object instanceof Block)) {
                        throw new IllegalArgumentException("Invalid shapeless recipe: unknown type " + object.getClass().getName() + "!");
                    }

                    list.add(new ItemStack((Block)object));
                }
            }

            IRecipe iRecipe = new ShapelessRecipes(result, list);
            activeCrafting.add(iRecipe);

            GameRegistry.addRecipe(iRecipe);
        }
    }

    public static void addFusion(RecipeDifficulty difficulty, ItemStack result, ItemStack catalyst, int energyCost, int craftingTier, Object... ingredients) {
        if (difficulty != ALL && RecipeDifficulty.getDifficulty() != difficulty) {
            return;
        }

        if (!isEnabled(result) || !isEnabled(catalyst)) {
            return;
        }

        for (Object ingredient : ingredients) {
            if (!isEnabled(OreDictHelper.resolveObject(ingredient))){
                return;
            }
        }

        IFusionRecipe recipe = new SimpleFusionRecipe(result, catalyst, energyCost, craftingTier, ingredients);
        activeFusion.add(recipe);
        FUSION_REGISTRY.add(recipe);
    }

    public static void addRecipe(IRecipe recipe) {
        activeCrafting.add(recipe);
        GameRegistry.addRecipe(recipe);
    }

    public static boolean isEnabled(Object stack) {
        if (stack instanceof ItemStack) {
            Item item = ((ItemStack) stack).getItem();
            Object o = item instanceof ItemBlock ? ((ItemBlock) item).getBlock() : item;
            return !ModFeatureParser.isFeature(o) || ModFeatureParser.isEnabled(o);
        }
        else if (stack instanceof Item || stack instanceof Block) {
            return !ModFeatureParser.isFeature(stack) || ModFeatureParser.isEnabled(stack);
        }
        return true;
    }

    public static enum RecipeDifficulty {
        ALL,
        NORMAL,
        HARD;

        public static RecipeDifficulty getDifficulty() {
            return DEConfig.hardMode ? HARD : NORMAL;
        }
    }

    //endregion
}
