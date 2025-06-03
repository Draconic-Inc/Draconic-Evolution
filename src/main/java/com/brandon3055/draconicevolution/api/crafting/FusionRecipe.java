package com.brandon3055.draconicevolution.api.crafting;

import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.draconicevolution.api.DraconicAPI;
import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;

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
    public ItemStack assemble(IFusionInventory inv, HolderLookup.Provider provider) {
        ItemStack stack = result.copy();
        if (stack.getItem() instanceof IFusionDataTransfer) {
            ((IFusionDataTransfer) stack.getItem()).transferIngredientData(stack, inv, provider);
        }
        return stack;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider provider) {
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

        public static final StreamCodec<RegistryFriendlyByteBuf, FusionIngredient> STREAM_CODEC = StreamCodec.composite(
                Ingredient.CONTENTS_STREAM_CODEC, e -> e.ingredient,
                ByteBufCodecs.BOOL, e -> e.consume,
                FusionIngredient::new
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

//        protected void write(FriendlyByteBuf buffer) {
//            buffer.writeBoolean(consume);
//            ingredient.toNetwork(buffer);
//        }
//
//        protected static FusionIngredient read(FriendlyByteBuf buffer) {
//            boolean consume = buffer.readBoolean();
//            Ingredient ingredient = Ingredient.fromNetwork(buffer);
//            return new FusionIngredient(ingredient, consume);
//        }
    }

    public static class Serializer implements RecipeSerializer<FusionRecipe> {
        private static final MapCodec<FusionRecipe> CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
                        ItemStack.CODEC.fieldOf("result").forGetter(e -> e.result),
                        Ingredient.CODEC_NONEMPTY.fieldOf("catalyst").forGetter(e -> e.catalyst),
                        Codec.LONG.fieldOf("totalEnergy").forGetter(e -> e.totalEnergy),
                        TechLevel.CODEC.fieldOf("techLevel").forGetter(e -> e.techLevel),
                        Codec.list(FusionIngredient.CODEC).fieldOf("ingredients").forGetter(e -> e.ingredients)
                ).apply(builder, FusionRecipe::new)
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, FusionRecipe> STREAM_CODEC = StreamCodec.of(
                FusionRecipe.Serializer::toNetwork, FusionRecipe.Serializer::fromNetwork
        );

        @Override
        public MapCodec<FusionRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, FusionRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        private static FusionRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
            ItemStack result =  ItemStack.STREAM_CODEC.decode(buffer);
            Ingredient catalyst = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);

            int count = buffer.readByte();
            List<FusionIngredient> fusionIngredients = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                fusionIngredients.add(FusionIngredient.STREAM_CODEC.decode(buffer));
            }

            long totalEnergy = buffer.readLong();
            TechLevel techLevel = TechLevel.VALUES[Mth.clamp(buffer.readByte(), 0, TechLevel.values().length - 1)];

            return new FusionRecipe(result, catalyst, totalEnergy, techLevel, fusionIngredients);
        }

        private static void toNetwork(RegistryFriendlyByteBuf buffer, FusionRecipe recipe) {
            ItemStack.STREAM_CODEC.encode(buffer, recipe.result);
            Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.catalyst);

            buffer.writeByte(recipe.ingredients.size());
            for (FusionIngredient ingredient : recipe.ingredients) {
                FusionIngredient.STREAM_CODEC.encode(buffer, ingredient);
            }

            buffer.writeLong(recipe.totalEnergy);
            buffer.writeByte(recipe.techLevel.index);
        }

    }
}
