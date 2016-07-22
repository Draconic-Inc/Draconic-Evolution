package com.brandon3055.draconicevolution.lib;

import com.brandon3055.brandonscore.config.ModFeatureParser;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by brandon3055 on 20/07/2016.
 */
public class RecipeHandler {

    private static List<IRecipe> recipes = new ArrayList<IRecipe>();

    //region Initialization

    public static void initialize() {
        addRecipes();
    }

    public static void reInitialize() {
//        for ()

    }

    //endregion

    //region Recipes

    private static void addRecipes() {


    }

    //endregion

    //region Registration

    private static void addShaped(Block result, Object... recipe) {
        addShaped(new ItemStack(result), recipe);
    }

    private static void addShaped(Item result, Object... recipe) {
        addShaped(new ItemStack(result), recipe);
    }

    private static void addShaped(ItemStack result, Object... recipe) {
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
            recipes.add(iRecipe);
            GameRegistry.addRecipe(iRecipe);
        }
        else {
            recipes.add(GameRegistry.addShapedRecipe(result, recipe));
        }
    }

    private static void addShapeless(Block result, Object... recipe) {
        addShapeless(new ItemStack(result), recipe);
    }

    private static void addShapeless(Item result, Object... recipe) {
        addShapeless(new ItemStack(result), recipe);
    }

    private static void addShapeless(ItemStack result, Object... recipe) {
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
            recipes.add(iRecipe);
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
            recipes.add(iRecipe);
            GameRegistry.addRecipe(iRecipe);
        }
    }

    private static boolean isEnabled(Object stack) {
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

    //endregion
}
