package com.brandon3055.draconicevolution.api.crafting;

import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.draconicevolution.api.DraconicAPI;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.common.crafting.CraftingHelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by brandon3055 on 24/11/20
 */
public class FusionRecipe implements IFusionRecipe {

    private final ResourceLocation id;
    private final ItemStack result;
    private final Ingredient catalyst;
    private final long totalEnergy;
    private final TechLevel techLevel;
    private final Collection<FusionIngredient> ingredients;

    public FusionRecipe(ResourceLocation id, ItemStack result, Ingredient catalyst, long totalEnergy, TechLevel techLevel, Collection<FusionIngredient> ingredients) {
        this.id = id;
        this.result = result;
        this.catalyst = catalyst;
        this.totalEnergy = totalEnergy;
        this.techLevel = techLevel;
        this.ingredients = ingredients;
    }

    @Override
    public TechLevel getRecipeTier() {
        return techLevel;
    }

    @Override
    public long getEnergyCost() {
        return totalEnergy;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return ingredients.stream().map(fusionIngredient -> fusionIngredient.ingredient).collect(Collectors.toCollection(NonNullList::create));
    }

    @Override
    public List<IFusionIngredient> fusionIngredients() {
        return ImmutableList.copyOf(ingredients);
    }

    @Override
    public Ingredient getCatalyst() {
        return catalyst;
    }

    @Override
    public ItemStack assemble(IFusionInventory inv, RegistryAccess registryAccess) {
        ItemStack stack = result.copy();
        if (stack.getItem() instanceof IFusionDataTransfer){
            ((IFusionDataTransfer) stack.getItem()).transferIngredientData(stack, inv);
        }
        return stack;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return result;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return DraconicAPI.FUSION_RECIPE_SERIALIZER.get();
    }

    public static class FusionIngredient implements IFusionIngredient {
        private final Ingredient ingredient;
        private final boolean consume;

        public FusionIngredient(Ingredient ingredient, boolean consume) {
            this.ingredient = ingredient;
            this.consume = consume;
        }

        @Override
        public Ingredient get() {
            return ingredient;
        }

        @Override
        public boolean consume() {
            return consume;
        }

        protected void write(FriendlyByteBuf buffer) {
            buffer.writeBoolean(consume);
            ingredient.toNetwork(buffer);
        }

        protected static FusionIngredient read(FriendlyByteBuf buffer) {
            boolean consume = buffer.readBoolean();
            Ingredient ingredient = Ingredient.fromNetwork(buffer);
            return new FusionIngredient(ingredient, consume);
        }
    }

    public static class Serializer implements RecipeSerializer<FusionRecipe> {
        @Override
        public FusionRecipe fromJson(ResourceLocation id, JsonObject json) {
            ItemStack result = CraftingHelper.getItemStack(GsonHelper.getAsJsonObject(json, "result"), true);
            Ingredient catalyst = CraftingHelper.getIngredient(GsonHelper.getAsJsonObject(json, "catalyst"), false);

            List<FusionIngredient> fusionIngredients = new ArrayList<>();
            JsonArray ingredients = GsonHelper.getAsJsonArray(json, "ingredients");
            for (JsonElement element : ingredients) {
                Ingredient ingredient;
                if (element.isJsonObject() && element.getAsJsonObject().has("ingredient")) {
                    ingredient = CraftingHelper.getIngredient(element.getAsJsonObject().get("ingredient"), false);
                } else {
                    ingredient = CraftingHelper.getIngredient(element, false);
                }
                boolean isConsumed = !element.isJsonObject() || GsonHelper.getAsBoolean(element.getAsJsonObject(), "consume", true);
                fusionIngredients.add(new FusionIngredient(ingredient, isConsumed));
            }

            long totalEnergy = GsonHelper.getAsLong(json, "total_energy");
            TechLevel techLevel = TechLevel.valueOf(GsonHelper.getAsString(json, "tier", TechLevel.DRACONIUM.name()));

            return new FusionRecipe(id, result, catalyst, totalEnergy, techLevel, fusionIngredients);
        }

        @Override
        public FusionRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {
            ItemStack result = buffer.readItem();
            Ingredient catalyst = Ingredient.fromNetwork(buffer);

            int count = buffer.readByte();
            List<FusionIngredient> fusionIngredients = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                fusionIngredients.add(FusionIngredient.read(buffer));
            }

            long totalEnergy = buffer.readLong();
            TechLevel techLevel = TechLevel.VALUES[Mth.clamp(buffer.readByte(), 0, TechLevel.values().length - 1)];

            return new FusionRecipe(id, result, catalyst, totalEnergy, techLevel, fusionIngredients);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, FusionRecipe recipe) {
            buffer.writeItemStack(recipe.result, false);
            recipe.catalyst.toNetwork(buffer);

            buffer.writeByte(recipe.ingredients.size());
            for (FusionIngredient ingredient : recipe.ingredients) {
                ingredient.write(buffer);
            }

            buffer.writeLong(recipe.totalEnergy);
            buffer.writeByte(recipe.techLevel.index);
        }
    }
}
