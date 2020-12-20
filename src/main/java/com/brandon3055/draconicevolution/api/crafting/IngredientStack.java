package com.brandon3055.draconicevolution.api.crafting;

import com.brandon3055.draconicevolution.api.DraconicAPI;
import com.google.common.collect.Streams;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tags.ITag;
import net.minecraft.util.IItemProvider;
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

    protected IngredientStack(Stream<? extends IItemList> itemLists, int count) {
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
            this.determineMatchingStacks();
            if (this.matchingStacks.length == 0) {
                return stack.isEmpty();
            } else {
                for (ItemStack itemstack : this.matchingStacks) {
                    if (itemstack.getItem() == stack.getItem() && itemstack.getCount() >= count) {
                        return true;
                    }
                }
                return false;
            }
        }
    }

    @Override
    public void determineMatchingStacks() {
        if (this.matchingStacks == null) {
            this.matchingStacks = Arrays.stream(this.acceptedItems)
                    .flatMap((itemList) -> itemList.getStacks().stream())
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
    public JsonElement serialize() {
        JsonObject obj = new JsonObject();
        obj.addProperty("count", count);
        JsonArray jsonarray = new JsonArray();
        for (Ingredient.IItemList ingredient$iitemlist : this.acceptedItems) {
            jsonarray.add(ingredient$iitemlist.serialize());
        }
        obj.add("items", jsonarray);
        obj.addProperty("type", DraconicAPI.INGREDIENT_STACK_TYPE.toString());
        return obj;
    }


    public static IngredientStack fromItemListStream(Stream<? extends Ingredient.IItemList> stream, int count) {
        IngredientStack ingredient = new IngredientStack(stream, count);
        return ingredient.acceptedItems.length == 0 ? EMPTY : ingredient;
    }

    public static IngredientStack fromItems(int count, IItemProvider... itemsIn) {
        return fromStacks(Arrays.stream(itemsIn).map(ItemStack::new), count);
    }

    public static IngredientStack fromStacks(int count, ItemStack... stacks) {
        return fromStacks(Arrays.stream(stacks), count);
    }

    public static IngredientStack fromStacks(Stream<ItemStack> stacks, int count) {
        return fromItemListStream(stacks.filter((stack) -> !stack.isEmpty()).map(SingleItemList::new), count);
    }

    public static IngredientStack fromTag(ITag<Item> tagIn, int count) {
        return fromItemListStream(Stream.of(new Ingredient.TagList(tagIn)), count);
    }

    public static class Serializer implements IIngredientSerializer<IngredientStack> {

        @Override
        public IngredientStack parse(PacketBuffer buffer) {
            int count = buffer.readShort();
            return IngredientStack.fromItemListStream(Stream.generate(() -> new Ingredient.SingleItemList(buffer.readItemStack())).limit(buffer.readVarInt()), count);
        }

        @Override
        public IngredientStack parse(JsonObject json) {
            int count = json.get("count").getAsShort();
            JsonArray stacks = json.get("items").getAsJsonArray();
            return IngredientStack.fromItemListStream(Streams.stream(stacks).map(e -> Ingredient.deserializeItemList(e.getAsJsonObject())), count);
        }

        @Override
        public void write(PacketBuffer buffer, IngredientStack ingredient) {
            buffer.writeShort(ingredient.getCount());
            ItemStack[] items = ingredient.getMatchingStacks();
            buffer.writeVarInt(items.length);
            for (ItemStack stack : items) {
                buffer.writeItemStack(stack);
            }
        }
    }

}
