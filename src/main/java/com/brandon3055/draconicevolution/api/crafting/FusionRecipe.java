package com.brandon3055.draconicevolution.api.crafting;

import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.draconicevolution.api.DraconicAPI;
import com.brandon3055.draconicevolution.api.OreDictHelper;
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
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
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
        List<IFusionInjector> injectors = new ArrayList<>(inv.getInjectors());

        //Check the catalyst for this recipe
        if (inv.getCatalystStack().isEmpty() || !getCatalyst().test(inv.getCatalystStack())) {// || inv.getStackInCore(0).getCount() < catalyst.getCount()) {
            return false;
        }

//        Check for catalyst NBT data
//        if (catalyst.hasTag() && !ItemStack.areItemStackTagsEqual(getCatalyst(), inv.getStackInCore(0))) {
//            return false;
//        }

        //Check that all of the ingredients are available.
        //TODO when i re write i want to abstract this out
        //I should not be iterating over injectors. I just need a list off something like IFusionIngredient
        //That just that an ingredient and a pedestal tier.
        //In fact i dont even need that. The FusionInventory can just have a method that returns the lowest tier
        //pedestal that is holding an item.
        for (Ingredient ingredient : getIngredients()) {
            boolean foundIngredient = false;

            for (IFusionInjector injector : injectors) {
                if (!injector.getInjectorStack().isEmpty() && ingredient.test(injector.getInjectorStack())) {
                    ItemStack i = OreDictHelper.resolveObject(ingredient);
                    if (i.hasTag() && !ItemStack.tagMatches(i, injector.getInjectorStack())) {
                        continue;
                    }

                    foundIngredient = true;
                    injectors.remove(injector);
                    break;
                }
            }

            if (!foundIngredient) {
                return false;
            }
        }

        //Check that there are no extra items that are not part of the recipe.
        for (IFusionInjector pedestal : injectors) {
            if (!pedestal.getInjectorStack().isEmpty()) {
                return false;
            }
        }

        return canCraft_(inv, worldIn);
    }

    //Temporary Hack
    private boolean canCraft_(IFusionInventory inventory, World world) {
        if (!inventory.getOutputStack().isEmpty()) {
            return false;
        }

        List<IFusionInjector> pedestals = new ArrayList<>(inventory.getInjectors());

        for (IFusionInjector pedestal : pedestals) {
            if (!pedestal.getInjectorStack().isEmpty() && pedestal.getInjectorTier().index < getRecipeTier().index) {
                return false;
            }
        }

        return true;
    }

    @Override
    public ItemStack assemble(IFusionInventory inv) {
        //TODO Apply any required data transfer
        return result.copy();
    }


    @Override
    public ItemStack getResultItem() {
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
            ingredient.toNetwork(buffer);
        }

        protected static FusionIngredient read(PacketBuffer buffer) {
            boolean consume = buffer.readBoolean();
            Ingredient ingredient = Ingredient.fromNetwork(buffer);
            return new FusionIngredient(ingredient, consume);
        }
    }

    public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<FusionRecipe> {
        @Override
        public FusionRecipe fromJson(ResourceLocation id, JsonObject json) {
            ItemStack result = CraftingHelper.getItemStack(JSONUtils.getAsJsonObject(json, "result"), true);
            Ingredient catalyst = CraftingHelper.getIngredient(JSONUtils.getAsJsonObject(json, "catalyst"));

            List<FusionIngredient> fusionIngredients = new ArrayList<>();
            JsonArray ingredients = JSONUtils.getAsJsonArray(json, "ingredients");
            for (JsonElement element : ingredients) {
                Ingredient ingredient;
                if (element.isJsonObject() && element.getAsJsonObject().has("ingredient")) {
                    ingredient = CraftingHelper.getIngredient(element.getAsJsonObject().get("ingredient"));
                } else {
                    ingredient = CraftingHelper.getIngredient(element);
                }
                boolean isConsumed = !element.isJsonObject() || JSONUtils.getAsBoolean(element.getAsJsonObject(), "consume", true);
                fusionIngredients.add(new FusionIngredient(ingredient, isConsumed));
            }

            long totalEnergy = JSONUtils.getAsLong(json, "total_energy");
            TechLevel techLevel = TechLevel.valueOf(JSONUtils.getAsString(json, "tier", TechLevel.DRACONIUM.name()));

            return new FusionRecipe(id, result, catalyst, totalEnergy, techLevel, fusionIngredients);
        }

        @Override
        public FusionRecipe fromNetwork(ResourceLocation id, PacketBuffer buffer) {
            ItemStack result = buffer.readItem();
            Ingredient catalyst = Ingredient.fromNetwork(buffer);

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
        public void toNetwork(PacketBuffer buffer, FusionRecipe recipe) {
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
