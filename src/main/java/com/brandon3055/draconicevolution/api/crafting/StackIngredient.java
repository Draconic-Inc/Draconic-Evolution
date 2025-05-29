package com.brandon3055.draconicevolution.api.crafting;

import com.brandon3055.draconicevolution.init.DEContent;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.HolderSetCodec;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import net.neoforged.neoforge.common.crafting.IngredientType;

import java.util.Arrays;
import java.util.stream.Stream;

/**
 * Created by brandon3055 on 9/12/20
 */
public class StackIngredient implements ICustomIngredient {
    public static final StackIngredient EMPTY = new StackIngredient(HolderSet.empty(), 0);

    public static final MapCodec<StackIngredient> CODEC = RecordCodecBuilder.mapCodec(
            builder -> builder
                    .group(
                            HolderSetCodec.create(Registries.ITEM, BuiltInRegistries.ITEM.holderByNameCodec(), false).fieldOf("items").forGetter(StackIngredient::items),
                            Codec.INT.optionalFieldOf("count", 1).forGetter(StackIngredient::getCount))
                    .apply(builder, StackIngredient::new));

    private final int count;
    private final HolderSet<Item> items;
    private final ItemStack[] stacks;

    public StackIngredient(HolderSet<Item> items, int count) {
        this.items = items;
        this.count = count;
        this.stacks = items.stream()
                .map(i -> new ItemStack(i, count))
                .toArray(ItemStack[]::new);
    }

    public int getCount() {
        return count;
    }

    @Override
    public boolean isSimple() {
        return false;
    }

    @Override
    public IngredientType<?> getType() {
        return DEContent.STACK_INGREDIENT_TYPE.get();
    }

    @Override
    public Stream<ItemStack> getItems() {
        return Stream.of(stacks);
    }


    @Override
    public boolean test(ItemStack stack) {
        return this.items.contains(stack.getItemHolder()) && stack.getCount() >= count;
    }

    public HolderSet<Item> items() {
        return items;
    }

    public static Ingredient of(int count, ItemStack stack) {
        return of(count, stack.getItem());
    }

    public static Ingredient of(int count, ItemLike... items) {
        return of(count, HolderSet.direct(Arrays.stream(items).map(ItemLike::asItem).map(Item::builtInRegistryHolder).toList()));
    }

    @SafeVarargs
    public static Ingredient of(int count, Holder<Item>... items) {
        return of(count, HolderSet.direct(items));
    }

    public static Ingredient of(int count, HolderSet<Item> items) {
        return new StackIngredient(items, count).toVanilla();
    }
}
