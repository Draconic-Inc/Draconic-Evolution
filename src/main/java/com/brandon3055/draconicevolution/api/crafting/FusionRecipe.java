package com.brandon3055.draconicevolution.api.crafting;

import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.draconicevolution.api.DraconicAPI;
import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by brandon3055 on 24/11/20
 */
public class FusionRecipe implements IFusionRecipe {

    private final ItemStack result;
    private final Ingredient catalyst;
    private final long totalEnergy;
    private final TechLevel techLevel;
    private final List<FusionIngredient> ingredients;

    public FusionRecipe(ItemStack result, Ingredient catalyst, long totalEnergy, TechLevel techLevel, List<FusionIngredient> ingredients) {
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
        if (stack.getItem() instanceof IFusionDataTransfer) {
            ((IFusionDataTransfer) stack.getItem()).transferIngredientData(stack, inv);
        }
        return stack;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return result;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return DraconicAPI.FUSION_RECIPE_SERIALIZER.get();
    }

    public static class FusionIngredient implements IFusionIngredient {
        private static final Codec<FusionIngredient> CODEC = RecordCodecBuilder.create(builder -> builder.group(
                        Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(e -> e.ingredient),
                        Codec.BOOL.fieldOf("consume").forGetter(e -> e.consume)
                ).apply(builder, FusionIngredient::new)
        );

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
        private static final Codec<FusionRecipe> CODEC = RecordCodecBuilder.create(builder -> builder.group(
                        ItemStack.ITEM_WITH_COUNT_CODEC.fieldOf("result").forGetter(e -> e.result),
                        Ingredient.CODEC_NONEMPTY.fieldOf("catalyst").forGetter(e -> e.catalyst),
                        Codec.LONG.fieldOf("totalEnergy").forGetter(e -> e.totalEnergy),
                        TechLevel.CODEC.fieldOf("techLevel").forGetter(e -> e.techLevel),
                        Codec.list(FusionIngredient.CODEC).fieldOf("ingredients").forGetter(e -> e.ingredients)
                ).apply(builder, FusionRecipe::new)
        );

        @Override
        public Codec<FusionRecipe> codec() {
            return CODEC;
        }

        @Override
        public FusionRecipe fromNetwork(FriendlyByteBuf buffer) {
            ItemStack result = buffer.readItem();
            Ingredient catalyst = Ingredient.fromNetwork(buffer);

            int count = buffer.readByte();
            List<FusionIngredient> fusionIngredients = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                fusionIngredients.add(FusionIngredient.read(buffer));
            }

            long totalEnergy = buffer.readLong();
            TechLevel techLevel = TechLevel.VALUES[Mth.clamp(buffer.readByte(), 0, TechLevel.values().length - 1)];

            return new FusionRecipe(result, catalyst, totalEnergy, techLevel, fusionIngredients);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, FusionRecipe recipe) {
            buffer.writeItem(recipe.result);
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
