package com.brandon3055.draconicevolution.api.crafting;

import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.draconicevolution.api.DraconicAPI;
import com.brandon3055.draconicevolution.api.OreDictHelper;
import com.brandon3055.draconicevolution.api.fusioncrafting.ICraftingInjector;
import com.brandon3055.draconicevolution.api.fusioncrafting.IFusionCraftingInventory;
import com.brandon3055.draconicevolution.init.DEContent;
import com.brandon3055.draconicevolution.utils.LogHelper;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.registries.ForgeRegistryEntry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by brandon3055 on 24/11/20
 */
public class FusionRecipe implements IFusionRecipe {

    private ResourceLocation id;
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
    public boolean canCraft(IFusionInventory inv, World world) {
        return matches(inv, world);
    }

    @Override
    public boolean matches(IFusionInventory inv, World worldIn) {
        List<ICraftingInjector> pedestals = new ArrayList<ICraftingInjector>();
        pedestals.addAll(inv.getInjectors());

        //Check the catalyst for this recipe
        if (inv.getStackInCore(0).isEmpty() || !getCatalyst().test(inv.getStackInCore(0))) {// || inv.getStackInCore(0).getCount() < catalyst.getCount()) {
            return false;
        }

//        Check for catalyst NBT data
//        if (catalyst.hasTag() && !ItemStack.areItemStackTagsEqual(getCatalyst(), inv.getStackInCore(0))) {
//            return false;
//        }

        //Check that all of the ingredients are available.
        //TODO when i re write i want to abstract this out
        //I should not be iterating over pedestals. I just need a list off something like IFusionIngredient
        //That just that an ingredient and a pedestal tier.
        //In fact i dont even need that. The FusionInventory can just have a method that returns the lowest tier
        //pedestal that is holding an item.
        for (Ingredient ingredient : getIngredients()) {
            boolean foundIngredient = false;

            for (ICraftingInjector pedestal : pedestals) {
                if (!pedestal.getStackInPedestal().isEmpty() && ingredient.test(pedestal.getStackInPedestal())) {
                    ItemStack i = OreDictHelper.resolveObject(ingredient);
                    if (i.hasTag() && !ItemStack.areItemStackTagsEqual(i, pedestal.getStackInPedestal())) {
                        continue;
                    }

                    foundIngredient = true;
                    pedestals.remove(pedestal);
                    break;
                }
            }

            if (!foundIngredient) {
                return false;
            }
        }

        //Check that there are no extra items that are not part of the recipe.
        for (ICraftingInjector pedestal : pedestals) {
            if (!pedestal.getStackInPedestal().isEmpty()) {
                return false;
            }
        }

        return canCraft_(inv, worldIn);
    }

    //Temporary Hack
    private boolean canCraft_(IFusionInventory inventory, World world) {
        if (!inventory.getStackInCore(1).isEmpty()) {
            return false;
        }

        List<ICraftingInjector> pedestals = new ArrayList<ICraftingInjector>();
        pedestals.addAll(inventory.getInjectors());

        for (ICraftingInjector pedestal : pedestals) {
            if (!pedestal.getStackInPedestal().isEmpty() && pedestal.getPedestalTier() < getRecipeTier().index) {
                return false;
            }
        }

        return true;
    }

    @Override
    public ItemStack getCraftingResult(IFusionInventory inv) {
        //TODO Apply any required data transfer
        return result.copy();
    }


    @Override
    public ItemStack getRecipeOutput() {
        return result;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return DraconicAPI.FUSION_RECIPE_SERIALIZER;
    }

    public static class FusionIngredient implements IFusionIngredient{
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

        protected void write(PacketBuffer buffer) {
            buffer.writeBoolean(consume);
            ingredient.write(buffer);
        }

        protected static FusionIngredient read(PacketBuffer buffer) {
            boolean consume = buffer.readBoolean();
            Ingredient ingredient = Ingredient.read(buffer);
            return new FusionIngredient(ingredient, consume);
        }
    }

    public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<FusionRecipe> {
        @Override
        public FusionRecipe read(ResourceLocation id, JsonObject json) {
            ItemStack result = CraftingHelper.getItemStack(JSONUtils.getJsonObject(json, "result"), true);
            Ingredient catalyst = CraftingHelper.getIngredient(JSONUtils.getJsonObject(json, "catalyst"));

            List<FusionIngredient> fusionIngredients = new ArrayList<>();
            JsonArray ingredients = JSONUtils.getJsonArray(json, "ingredients");
            for (JsonElement element : ingredients) {
                Ingredient ingredient;
                if (element.isJsonObject() && element.getAsJsonObject().has("ingredient")) {
                    ingredient = CraftingHelper.getIngredient(element.getAsJsonObject().get("ingredient"));
                } else {
                    ingredient = CraftingHelper.getIngredient(element);
                }
                boolean isConsumed = !element.isJsonObject() || JSONUtils.getBoolean(element.getAsJsonObject(), "consume", true);
                fusionIngredients.add(new FusionIngredient(ingredient, isConsumed));
            }

            long totalEnergy = JSONUtils.getLong(json, "total_energy");
            TechLevel techLevel = TechLevel.valueOf(JSONUtils.getString(json, "tier", TechLevel.DRACONIUM.name()));

            return new FusionRecipe(id, result, catalyst, totalEnergy, techLevel, fusionIngredients);
        }

        @Override
        public FusionRecipe read(ResourceLocation id, PacketBuffer buffer) {
            ItemStack result = buffer.readItemStack();
            Ingredient catalyst = Ingredient.read(buffer);

            int count = buffer.readByte();
            List<FusionIngredient> fusionIngredients = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                fusionIngredients.add(FusionIngredient.read(buffer));
            }

            long totalEnergy = buffer.readLong();
            TechLevel techLevel = TechLevel.VALUES[MathHelper.clamp(buffer.readByte(), 0, TechLevel.values().length - 1)];

            return new FusionRecipe(id, result, catalyst, totalEnergy, techLevel, fusionIngredients);
        }

        @Override
        public void write(PacketBuffer buffer, FusionRecipe recipe) {
            buffer.writeItemStack(recipe.result, false);
            recipe.catalyst.write(buffer);

            buffer.writeByte(recipe.ingredients.size());
            for (FusionIngredient ingredient : recipe.ingredients) {
                ingredient.write(buffer);
            }

            buffer.writeLong(recipe.totalEnergy);
            buffer.writeByte(recipe.techLevel.index);
        }
    }
}
