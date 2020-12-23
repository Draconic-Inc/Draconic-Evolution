package com.brandon3055.draconicevolution.api.crafting;

import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.draconicevolution.api.DraconicAPI;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

import java.util.List;

/**
 * Created by brandon3055 on 26/11/20
 */
public interface IFusionRecipe extends IRecipe<IFusionInventory> {

    @Override
    default ItemStack getIcon() {
        if (DraconicAPI.CRAFTING_CORE != null) {
            return new ItemStack(DraconicAPI.CRAFTING_CORE);
        } else {
            return new ItemStack(Blocks.CRAFTING_TABLE);
        }
    }

    @Override
    default IRecipeType<?> getType() {
        return DraconicAPI.FUSION_RECIPE_TYPE;
    }

    TechLevel getRecipeTier();

    long getEnergyCost();

    /**
     * @return A list of recipe ingredients NOT including the catalyst.
     */
    @Override
    NonNullList<Ingredient> getIngredients();

    List<IFusionIngredient> fusionIngredients();

    Ingredient getCatalyst();

    /**
     * Returns true if the ingredients in the inventory match this recipe.
     *
     * @param inv   The fusion crafting inventory.
     * @param world the world
     */
    @Override
    boolean matches(IFusionInventory inv, World world);

    /**
     * Used to apply secondary checks such as crafting tier and any "special" crafting requirements.
     *
     * @param inv   The fusion crafting inventory.
     * @param world the world
     */
    default boolean canCraft(IFusionInventory inv, World world){
        return matches(inv, world);//TODO
    }

    //TODO Not sure if i will need both of these
    default void onCraftingTick(IFusionInventory inv, World world) {}

    default void onCraftingComplete(IFusionInventory inv, World world) {}


    @Override
    default boolean canFit(int width, int height) {
        return true;
    }

    interface IFusionIngredient {
        Ingredient get();
        boolean consume();
    }
}
