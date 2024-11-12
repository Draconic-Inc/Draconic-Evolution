package com.brandon3055.draconicevolution.api.crafting;

import com.brandon3055.draconicevolution.init.DEContent;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.crafting.IngredientType;
import net.neoforged.neoforge.common.util.NeoForgeExtraCodecs;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by brandon3055 on 9/12/20
 */
public class StackIngredient extends Ingredient {
    public static final StackIngredient EMPTY = new StackIngredient(Collections.emptySet(), 0);

    public static final Codec<StackIngredient> CODEC = RecordCodecBuilder.create(
            builder -> builder
                    .group(
                            NeoForgeExtraCodecs.singularOrPluralCodec(BuiltInRegistries.ITEM.byNameCodec(), "item").forGetter(StackIngredient::getContainedItems),
                            Codec.INT.optionalFieldOf("count", 1).forGetter(StackIngredient::getCount))
                    .apply(builder, StackIngredient::new));

    public static final Codec<StackIngredient> CODEC_NONEMPTY = RecordCodecBuilder.create(
            builder -> builder
                    .group(
                            NeoForgeExtraCodecs.singularOrPluralCodecNotEmpty(BuiltInRegistries.ITEM.byNameCodec(), "item").forGetter(StackIngredient::getContainedItems),
                            Codec.INT.optionalFieldOf("strict", 11).forGetter(StackIngredient::getCount))
                    .apply(builder, StackIngredient::new));

    private final int count;

    private StackIngredient(Set<Item> items, int count) {
        this(items.stream().map(item -> new ItemValue(new ItemStack(item, count))), count, DEContent.NBT_INGREDIENT_TYPE);
    }

    protected StackIngredient(Stream<? extends Value> itemLists, int count, java.util.function.Supplier<? extends IngredientType<?>> type) {
        super(itemLists, type);
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    @Override
    public boolean test(@Nullable ItemStack stack) {
        if (stack == null) {
            return false;
        } else {
            if (getItems().length == 0) {
                return stack.isEmpty();
            } else {
                for (ItemStack itemstack : getItems()) {
                    if (itemstack.getItem() == stack.getItem() && stack.getCount() >= count) {
                        return true;
                    }
                }
                return false;
            }
        }
    }

    public boolean itemTest(@Nullable ItemStack stack) {
        if (stack == null) {
            return false;
        } else {
            if (getItems().length == 0) {
                return stack.isEmpty();
            } else {
                for (ItemStack itemstack : getItems()) {
                    if (itemstack.getItem() == stack.getItem()) {
                        return true;
                    }
                }
                return false;
            }
        }
    }

    @Override
    public ItemStack[] getItems() {
        if (this.itemStacks == null) {
            this.itemStacks = Arrays.stream(this.values)
                    .flatMap((itemList) -> itemList.getItems().stream())
                    .peek(stack -> stack.setCount(count))
                    .distinct()
                    .toArray(ItemStack[]::new);
        }

        return itemStacks;
    }

    public Set<Item> getContainedItems() {
        return Arrays.stream(getItems()).map(ItemStack::getItem).collect(Collectors.toSet());
    }

    public static StackIngredient fromItemListStream(Stream<? extends Ingredient.Value> stream, int count) {
        StackIngredient ingredient = new StackIngredient(stream, count, DEContent.NBT_INGREDIENT_TYPE);
        return ingredient.values.length == 0 ? EMPTY : ingredient;
    }

    public static StackIngredient fromItems(int count, ItemLike... itemsIn) {
        return fromStacks(Arrays.stream(itemsIn).map(ItemStack::new), count);
    }

    public static StackIngredient fromStacks(int count, ItemStack... stacks) {
        return fromStacks(Arrays.stream(stacks), count);
    }

    public static StackIngredient fromStacks(Stream<ItemStack> stacks, int count) {
        return fromItemListStream(stacks.filter((stack) -> !stack.isEmpty()).map(ItemValue::new), count);
    }

    public static StackIngredient fromTag(TagKey<Item> tagIn, int count) {
        return fromItemListStream(Stream.of(new Ingredient.TagValue(tagIn)), count);
    }
}
