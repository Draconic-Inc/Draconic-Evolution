package com.brandon3055.draconicevolution.datagen;

import codechicken.lib.datagen.recipe.AbstractItemStackRecipeBuilder;
import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.draconicevolution.api.crafting.FusionRecipe;
import com.brandon3055.draconicevolution.api.crafting.StackIngredient;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class FusionRecipeBuilder extends AbstractItemStackRecipeBuilder<FusionRecipeBuilder> {
    private final ItemStack result;
    private Ingredient catalyst = null;
    private long energy = -1;
    private TechLevel techLevel = null;
    private List<FusionRecipe.FusionIngredient> ingredients = new ArrayList<>();

    protected FusionRecipeBuilder(ResourceLocation id, ItemStack result) {
        super(id, result);
        this.result = result;
    }

    public static FusionRecipeBuilder builder(ItemLike result) {
        return builder(result, 1);
    }

    public static FusionRecipeBuilder builder(ItemLike result, int count) {
        return builder(new ItemStack(result, count));
    }

    public static FusionRecipeBuilder builder(ItemLike result, int count, ResourceLocation id) {
        return builder(new ItemStack(result, count), id);
    }

    public static FusionRecipeBuilder builder(ItemStack result) {
        return builder(result, BuiltInRegistries.ITEM.getKey(result.getItem()));
    }

    public static FusionRecipeBuilder builder(ItemStack result, ResourceLocation id) {
        return new FusionRecipeBuilder(id, result);
    }

    public FusionRecipeBuilder catalyst(TagKey<Item> catalyst) {
        return catalyst(Ingredient.of(catalyst));
    }

    public FusionRecipeBuilder catalyst(Supplier<? extends ItemLike> catalyst) {
        return catalyst(Ingredient.of(catalyst.get()));
    }

    public FusionRecipeBuilder catalyst(ItemLike... catalyst) {
        return catalyst(Ingredient.of(catalyst));
    }

    public FusionRecipeBuilder catalyst(ItemStack... catalyst) {
        return catalyst(Ingredient.of(catalyst));
    }

    public FusionRecipeBuilder catalyst(int count, TagKey<Item> catalyst) {
        return catalyst(StackIngredient.fromTag(catalyst, count));
    }

    public FusionRecipeBuilder catalyst(int count, ItemLike... catalyst) {
        return catalyst(StackIngredient.fromItems(count, catalyst));
    }

    public FusionRecipeBuilder catalyst(int count, Supplier<? extends ItemLike> catalyst) {
        return catalyst(StackIngredient.fromItems(count, catalyst.get()));
    }

    public FusionRecipeBuilder catalyst(int count, ItemStack... catalyst) {
        return catalyst(StackIngredient.fromStacks(count, catalyst));
    }

    public FusionRecipeBuilder catalyst(Ingredient catalyst) {
        this.catalyst = catalyst;
        return this;
    }

    public FusionRecipeBuilder energy(long totalEnergy) {
        this.energy = totalEnergy;
        return this;
    }

    public FusionRecipeBuilder techLevel(TechLevel techLevel) {
        this.techLevel = techLevel;
        return this;
    }

    public FusionRecipeBuilder ingredient(boolean consume, Ingredient ingredient) {
        ingredients.add(new FusionRecipe.FusionIngredient(ingredient, consume));
        return this;
    }

    public FusionRecipeBuilder ingredient(Ingredient ingredient) {
        return ingredient(true, ingredient);
    }

    public FusionRecipeBuilder ingredient(boolean consume, ItemStack... ingredient) {
        return ingredient(consume, Ingredient.of(ingredient));
    }

    public FusionRecipeBuilder ingredient(ItemStack... ingredient) {
        return ingredient(true, ingredient);
    }

    public FusionRecipeBuilder ingredient(boolean consume, Supplier<? extends ItemLike> ingredient) {
        return ingredient(consume, Ingredient.of(ingredient.get()));
    }

    public FusionRecipeBuilder ingredient(boolean consume, ItemLike... ingredient) {
        return ingredient(consume, Ingredient.of(ingredient));
    }

    public FusionRecipeBuilder ingredient(Supplier<? extends ItemLike> ingredient) {
        return ingredient(true, ingredient.get());
    }

    public FusionRecipeBuilder ingredient(ItemLike... ingredient) {
        return ingredient(true, ingredient);
    }

    public FusionRecipeBuilder ingredient(boolean consume, TagKey<Item> ingredient) {
        return ingredient(consume, Ingredient.of(ingredient));
    }

    public FusionRecipeBuilder ingredient(TagKey<Item> ingredient) {
        return ingredient(true, ingredient);
    }

    @Override
    protected void validate() {
        super.validate();
        if (catalyst == null) {
            throw new IllegalStateException("No catalyst is defined for fusion recipe " + id + "!");
        } else if (energy == -1) {
            throw new IllegalStateException("Energy requirement is not defined for fusion recipe " + id + "!");
        } else if (techLevel == null) {
            throw new IllegalStateException("No TechLevel (crafting tier) is defined for fusion recipe " + id + "!");
        } else if (ingredients.isEmpty()) {
            throw new IllegalStateException("No ingredients are defined for fusion recipe " + id + "!");
        }
    }

    @Override
    public Recipe<?> _build() {
        return new FusionRecipe(result, catalyst, energy, techLevel, ingredients);
    }
}