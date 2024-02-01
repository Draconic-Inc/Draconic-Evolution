package com.brandon3055.draconicevolution.datagen;

import codechicken.lib.datagen.recipe.AbstractItemStackRecipeBuilder;
import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.draconicevolution.api.DraconicAPI;
import com.brandon3055.draconicevolution.api.crafting.FusionRecipe;
import com.brandon3055.draconicevolution.api.crafting.IngredientStack;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

public class FusionRecipeBuilder extends AbstractItemStackRecipeBuilder<FusionRecipeBuilder> {
    private final ItemStack result;
    private Ingredient catalyst = null;
    private long energy = -1;
    private TechLevel techLevel = null;
    private List<FusionRecipe.FusionIngredient> ingredients = new ArrayList<>();

    protected FusionRecipeBuilder(ResourceLocation id, ItemStack result) {
        super(DraconicAPI.FUSION_RECIPE_SERIALIZER, id, result);
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
        return builder(result, ForgeRegistries.ITEMS.getKey(result.getItem()));
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
        return catalyst(IngredientStack.fromTag(catalyst, count));
    }

    public FusionRecipeBuilder catalyst(int count, ItemLike... catalyst) {
        return catalyst(IngredientStack.fromItems(count, catalyst));
    }

    public FusionRecipeBuilder catalyst(int count, Supplier<? extends ItemLike> catalyst) {
        return catalyst(IngredientStack.fromItems(count, catalyst.get()));
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

//    @Override
//    public void build(Consumer<FinishedRecipe> consumer) {
//        build(consumer, result.getItem().getRegistryName());
//    }
//
//    public void build(Consumer<FinishedRecipe> consumer, String save) {
//        ResourceLocation resourcelocation = result.getItem().getRegistryName();
//        if ((new ResourceLocation(save)).equals(resourcelocation)) {
//            throw new IllegalStateException("Fusion Recipe " + save + " should remove its 'save' argument");
//        } else {
//            this.build(consumer, new ResourceLocation(save));
//        }
//    }


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

    public static JsonObject writeItemStack(ItemStack stack) {
        JsonObject json = new JsonObject();
        json.addProperty("item", ForgeRegistries.ITEMS.getKey(stack.getItem()).toString());
        if (stack.getCount() != 1) {
            json.addProperty("count", stack.getCount());
        }

        if (stack.hasTag()) {
            json.addProperty("nbt", stack.getTag().toString());
        }
        return json;
    }

    @Override
    public AbstractItemStackRecipeBuilder<FusionRecipeBuilder>.AbstractItemStackFinishedRecipe _build() {
        return new Result(id, result, catalyst, energy, techLevel, ingredients);
    }

    public class Result extends AbstractItemStackFinishedRecipe {
        private final ResourceLocation id;
        private final ItemStack result;
        private final Ingredient catalyst;
        private final TechLevel techLevel;
        private final Collection<FusionRecipe.FusionIngredient> ingredients;
        private final long energy;

        public Result(ResourceLocation id, ItemStack result, Ingredient catalyst, long energy, TechLevel techLevel, Collection<FusionRecipe.FusionIngredient> ingredients) {//, Advancement.Builder advancementBuilderIn, ResourceLocation advancementIdIn) {
            this.id = id;
            this.result = result;
            this.catalyst = catalyst;
            this.energy = energy;
            this.techLevel = techLevel;
            this.ingredients = ingredients;
        }

        @Override
        public void serializeRecipeData(JsonObject json) {
            json.add("result", writeItemStack(result));
            json.add("catalyst", catalyst.toJson());
            json.addProperty("total_energy", energy);
            json.addProperty("tier", techLevel.name());

            JsonArray ingredientArray = new JsonArray();
            for (FusionRecipe.FusionIngredient ingredient : ingredients) {
                JsonElement element = ingredient.get().toJson();
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

        @Override
        public RecipeSerializer<?> getType() {
            return DraconicAPI.FUSION_RECIPE_SERIALIZER;
        }

        @Override
        public ResourceLocation getId() {
            return this.id;
        }

        @Nullable
        public JsonObject serializeAdvancement() {
            return null;//this.advancementBuilder.serialize();
        }

        @Nullable
        public ResourceLocation getAdvancementId() {
            return null;//this.advancementId;
        }
    }
}