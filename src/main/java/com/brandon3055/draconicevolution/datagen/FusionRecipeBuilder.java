package com.brandon3055.draconicevolution.datagen;

import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.draconicevolution.api.DraconicAPI;
import com.brandon3055.draconicevolution.api.crafting.FusionRecipe;
import com.brandon3055.draconicevolution.api.crafting.IngredientStack;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.*;
import java.util.function.Consumer;
import javax.annotation.Nullable;

import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ITag;
import net.minecraft.tags.Tag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;

public class FusionRecipeBuilder {
    private final ItemStack result;
    //    private final Advancement.Builder advancementBuilder = Advancement.Builder.builder();
    private Ingredient catalyst = null;
    private long energy = -1;
    private TechLevel techLevel = null;
    private List<FusionRecipe.FusionIngredient> ingredients = new ArrayList<>();

    public FusionRecipeBuilder(ItemStack result) {
        this.result = result;
    }

    public static FusionRecipeBuilder fusionRecipe(IItemProvider resultIn) {
        return fusionRecipe(resultIn, 1);
    }

    public static FusionRecipeBuilder fusionRecipe(IItemProvider resultIn, int countIn) {
        return fusionRecipe(new ItemStack(resultIn, countIn));
    }

    public static FusionRecipeBuilder fusionRecipe(ItemStack result) {
        return new FusionRecipeBuilder(result);
    }

    public FusionRecipeBuilder catalyst(ITag<Item> catalyst) {
        return catalyst(Ingredient.fromTag(catalyst));
    }

    public FusionRecipeBuilder catalyst(IItemProvider... catalyst) {
        return catalyst(Ingredient.fromItems(catalyst));
    }

    public FusionRecipeBuilder catalyst(ItemStack... catalyst) {
        return catalyst(Ingredient.fromStacks(catalyst));
    }

    public FusionRecipeBuilder catalyst(int count, ITag<Item> catalyst) {
        return catalyst(IngredientStack.fromTag(catalyst, count));
    }

    public FusionRecipeBuilder catalyst(int count, IItemProvider... catalyst) {
        return catalyst(IngredientStack.fromItems(count, catalyst));
    }

    public FusionRecipeBuilder catalyst(int count, ItemStack... catalyst) {
        return catalyst(IngredientStack.fromStacks(count, catalyst));
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
        return ingredient(consume, Ingredient.fromStacks(ingredient));
    }

    public FusionRecipeBuilder ingredient(ItemStack... ingredient) {
        return ingredient(true, ingredient);
    }

    public FusionRecipeBuilder ingredient(boolean consume, IItemProvider... ingredient) {
        return ingredient(consume, Ingredient.fromItems(ingredient));
    }

    public FusionRecipeBuilder ingredient(IItemProvider... ingredient) {
        return ingredient(true, ingredient);
    }

    public FusionRecipeBuilder ingredient(boolean consume, ITag<Item> ingredient) {
        return ingredient(consume, Ingredient.fromTag(ingredient));
    }

    public FusionRecipeBuilder ingredient(ITag<Item> ingredient) {
        return ingredient(true, ingredient);
    }

//    public FusionRecipeBuilder addCriterion(String name, ICriterionInstance criterionIn) {
//        advancementBuilder.withCriterion(name, criterionIn);
//        return this;
//    }


    public void build(Consumer<IFinishedRecipe> consumer) {
        build(consumer, result.getItem().getRegistryName());
    }

    public void build(Consumer<IFinishedRecipe> consumer, String name) {
        ResourceLocation resourcelocation = result.getItem().getRegistryName();
        ResourceLocation saveName = new ResourceLocation(resourcelocation.getNamespace(), name);
        if (saveName.equals(resourcelocation)) {
            throw new IllegalStateException("Fusion Recipe " + saveName + " should remove its 'name' argument");
        } else {
            build(consumer, saveName);
        }
    }

    public void build(Consumer<IFinishedRecipe> consumer, ResourceLocation id) {
        if (result.isEmpty()) return;
        validate(id);
//        advancementBuilder.withParentId(new ResourceLocation("recipes/root")).withCriterion("has_the_recipe", new RecipeUnlockedTrigger.Instance(id)).withRewards(AdvancementRewards.Builder.recipe(id)).withRequirementsStrategy(IRequirementsStrategy.OR);
        consumer.accept(new Result(id, result, catalyst, energy, techLevel, ingredients));//, this.advancementBuilder, new ResourceLocation(id.getNamespace(), "recipes/" + this.result.getGroup().getPath() + "/" + id.getPath())));
    }

    private void validate(ResourceLocation id) {
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

    public static JsonObject writeItemStack(ItemStack stack) {
        JsonObject json = new JsonObject();
        json.addProperty("item", stack.getItem().getRegistryName().toString());
        if (stack.getCount() != 1) {
            json.addProperty("count", stack.getCount());
        }

        if (stack.hasTag()) {
            json.addProperty("nbt", stack.getTag().toString());
        }
        return json;
    }

    public static class Result implements IFinishedRecipe {
        private final ResourceLocation id;
        private final ItemStack result;
        private final Ingredient catalyst;
        private final TechLevel techLevel;
        private final Collection<FusionRecipe.FusionIngredient> ingredients;
        private final long energy;
//        private final Advancement.Builder advancementBuilder;
//        private final ResourceLocation advancementId;

        public Result(ResourceLocation id, ItemStack result, Ingredient catalyst, long energy, TechLevel techLevel, Collection<FusionRecipe.FusionIngredient> ingredients) {//, Advancement.Builder advancementBuilderIn, ResourceLocation advancementIdIn) {
            this.id = id;
            this.result = result;
            this.catalyst = catalyst;
            this.energy = energy;
            this.techLevel = techLevel;
            this.ingredients = ingredients;
//            this.advancementBuilder = advancementBuilderIn;
//            this.advancementId = advancementIdIn;
        }

        public void serialize(JsonObject json) {
            json.add("result", writeItemStack(result));
            json.add("catalyst", catalyst.serialize());
            json.addProperty("total_energy", energy);
            json.addProperty("tier", techLevel.name());

            JsonArray ingredientArray = new JsonArray();
            for (FusionRecipe.FusionIngredient ingredient : ingredients) {
                JsonElement element = ingredient.get().serialize();
                if (!ingredient.consume()) {
                    JsonObject object = new JsonObject();
                    object.addProperty("consume", false);
                    object.add("ingredient", element);
                    ingredientArray.add(object);
                } else {
                    ingredientArray.add(element);
                }
            }
            json.add("ingredients", ingredientArray);
        }

        public IRecipeSerializer<?> getSerializer() {
            return DraconicAPI.FUSION_RECIPE_SERIALIZER;
        }

        public ResourceLocation getID() {
            return this.id;
        }

        @Nullable
        public JsonObject getAdvancementJson() {
            return null;//this.advancementBuilder.serialize();
        }

        @Nullable
        public ResourceLocation getAdvancementID() {
            return null;//this.advancementId;
        }
    }
}
