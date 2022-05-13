package com.brandon3055.draconicevolution.api.crafting;

import com.brandon3055.draconicevolution.api.DraconicAPI;
import com.google.common.collect.Streams;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.crafting.IIngredientSerializer;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.stream.Stream;

/**
 * Created by brandon3055 on 9/12/20
 */
public class IngredientStack extends Ingredient {
    public static final Serializer SERIALIZER = new Serializer();
    public static final IngredientStack EMPTY = new IngredientStack(Stream.empty(), 0);

    private final int count;

    protected IngredientStack(Stream<? extends Value> itemLists, int count) {
        super(itemLists);
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
            this.dissolve();
            if (this.itemStacks.length == 0) {
                return stack.isEmpty();
            } else {
                for (ItemStack itemstack : this.itemStacks) {
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
            this.dissolve();
            if (this.itemStacks.length == 0) {
                return stack.isEmpty();
            } else {
                for(ItemStack itemstack : this.itemStacks) {
                    if (itemstack.getItem() == stack.getItem()) {
                        return true;
                    }
                }
                return false;
            }
        }
    }

    @Override
    protected void dissolve() {
        if (this.itemStacks == null) {
            this.itemStacks = Arrays.stream(this.values)
                    .flatMap((itemList) -> itemList.getItems().stream())
                    .peek(stack -> stack.setCount(count))
                    .distinct()
                    .toArray(ItemStack[]::new);
        }
    }

    @Override
    public IIngredientSerializer<? extends IngredientStack> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public JsonElement toJson() {
        JsonObject obj = new JsonObject();
        obj.addProperty("count", count);
        JsonArray jsonarray = new JsonArray();
        for (Ingredient.Value ingredient$iitemlist : this.values) {
            jsonarray.add(ingredient$iitemlist.serialize());
        }
        obj.add("items", jsonarray);
        obj.addProperty("type", DraconicAPI.INGREDIENT_STACK_TYPE.toString());
        return obj;
    }


    public static IngredientStack fromItemListStream(Stream<? extends Ingredient.Value> stream, int count) {
        IngredientStack ingredient = new IngredientStack(stream, count);
        return ingredient.values.length == 0 ? EMPTY : ingredient;
    }

    public static IngredientStack fromItems(int count, ItemLike... itemsIn) {
        return fromStacks(Arrays.stream(itemsIn).map(ItemStack::new), count);
    }

    public static IngredientStack fromStacks(int count, ItemStack... stacks) {
        return fromStacks(Arrays.stream(stacks), count);
    }

    public static IngredientStack fromStacks(Stream<ItemStack> stacks, int count) {
        return fromItemListStream(stacks.filter((stack) -> !stack.isEmpty()).map(ItemValue::new), count);
    }

    public static IngredientStack fromTag(TagKey<Item> tagIn, int count) {
        return fromItemListStream(Stream.of(new Ingredient.TagValue(tagIn)), count);
    }

    public static class Serializer implements IIngredientSerializer<IngredientStack> {

        @Override
        public IngredientStack parse(FriendlyByteBuf buffer) {
            int count = buffer.readShort();
            return IngredientStack.fromItemListStream(Stream.generate(() -> new Ingredient.ItemValue(buffer.readItem())).limit(buffer.readVarInt()), count);
        }

        @Override
        public IngredientStack parse(JsonObject json) {
            int count = json.get("count").getAsShort();
            JsonArray stacks = json.get("items").getAsJsonArray();
            return IngredientStack.fromItemListStream(Streams.stream(stacks).map(e -> Ingredient.valueFromJson(e.getAsJsonObject())), count);
        }

        @Override
        public void write(FriendlyByteBuf buffer, IngredientStack ingredient) {
            buffer.writeShort(ingredient.getCount());
            ItemStack[] items = ingredient.getItems();
            buffer.writeVarInt(items.length);
            for (ItemStack stack : items) {
                buffer.writeItem(stack);
            }
        }
    }

}
