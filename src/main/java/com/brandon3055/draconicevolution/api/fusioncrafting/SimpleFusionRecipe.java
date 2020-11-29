package com.brandon3055.draconicevolution.api.fusioncrafting;

import com.brandon3055.draconicevolution.api.OreDictHelper;
import com.brandon3055.draconicevolution.lib.WTFException;
import com.brandon3055.draconicevolution.utils.LogHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.math.BigInteger;
import java.util.*;

/**
 * Created by brandon3055 on 11/06/2016.
 * A simple implementation if IFusionRecipe
 * Accepts ore dictionary entries.
 */
@Deprecated
public class SimpleFusionRecipe implements IFusionRecipe {

    protected ItemStack result;
    protected ItemStack catalyst;
    protected List<Object> ingredients;
    protected long energyCost;
    protected int craftingTier;

    /**
     * tier 0 = basic, tier 1 = wyvern, tier 2 = awakened, tier 3 = chaotic
     */
    public SimpleFusionRecipe(ItemStack result, ItemStack catalyst, long energyCost, int craftingTier, Object... ingredients) {
        this.result = result;
        this.catalyst = catalyst;
        this.ingredients = new LinkedList<>();
        Collections.addAll(this.ingredients, ingredients);
        this.energyCost = energyCost;
        this.craftingTier = craftingTier;

        long maxCost = Long.MAX_VALUE / ingredients.length;
        if (energyCost > maxCost) {
            String r = "Result: " + result + "\nCatalyst: " + catalyst + "\nTier: " + craftingTier + "\nEnergy: (per ingredient) " + energyCost + ", (total) " + BigInteger.valueOf(energyCost).multiply(BigInteger.valueOf(ingredients.length));
            for (Object i : ingredients) {
                r += "\n" + i;
            }
            LogHelper.error("An error occurred while registering the following recipe. \n" + r);
            throw new WTFException("Invalid Recipe: The combined energy cost of your recipe exceeds Long.MAX_VALUE (" + Long.MAX_VALUE + ") WTF are you doing?");
        }
    }

    @Deprecated
    public SimpleFusionRecipe(ItemStack result, ItemStack catalyst, int energyCost, int craftingTier, Object... ingredients) {
        this(result, catalyst, (long) energyCost, craftingTier, ingredients);
    }

    @Override
    public ItemStack getRecipeOutput(@Nullable ItemStack catalyst) {
        return result;
    }

    @Override
    public boolean isRecipeCatalyst(ItemStack catalyst) {
        return catalyst != null && this.catalyst.isItemEqual(catalyst);
    }

    @Override
    public ItemStack getRecipeCatalyst() {
        return catalyst;
    }

    @Override
    public int getRecipeTier() {
        return craftingTier;
    }

    @Override
    public List<Object> getRecipeIngredients() {
        return ingredients;
    }

    @Override
    public boolean matches(IFusionCraftingInventory inventory, World world, BlockPos pos) {
        List<ICraftingInjector> pedestals = new ArrayList<ICraftingInjector>();
        pedestals.addAll(inventory.getInjectors());

        //Check the catalyst for this recipe
        if (inventory.getStackInCore(0).isEmpty() || !inventory.getStackInCore(0).isItemEqual(catalyst) || inventory.getStackInCore(0).getCount() < catalyst.getCount()) {
            return false;
        }

        //Check for catalyst NBT data
        if (catalyst.hasTag() && !ItemStack.areItemStackTagsEqual(catalyst, inventory.getStackInCore(0))) {
            return false;
        }

        //Check that all of the ingredients are available.
        for (Object ingredient : ingredients) {
            boolean foundIngredient = false;

            for (ICraftingInjector pedestal : pedestals) {
                if (!pedestal.getStackInPedestal().isEmpty() && OreDictHelper.areStacksEqual(ingredient, pedestal.getStackInPedestal())) {
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

        return true;
    }

    @Override
    public void craft(IFusionCraftingInventory inventory, World world, BlockPos pos) {
        //This shouldn't be needed but cant hurt.
        if (!matches(inventory, world, pos)) {
            return;
        }

        List<ICraftingInjector> pedestals = new ArrayList<>();
        pedestals.addAll(inventory.getInjectors());

        //Use Ingredients
        for (Object ingredient : ingredients) {
            for (ICraftingInjector pedestal : pedestals) {
                if (!pedestal.getStackInPedestal().isEmpty() && OreDictHelper.areStacksEqual(ingredient, pedestal.getStackInPedestal()) && pedestal.getPedestalTier() >= craftingTier) {

                    ItemStack stack = pedestal.getStackInPedestal();
                    if (stack.getItem().hasContainerItem(stack)) {
                        stack = stack.getItem().getContainerItem(stack);
                    }
                    else {
                        stack.shrink(1);
                        if (stack.getCount() <= 0) {
                            stack = ItemStack.EMPTY;
                        }
                    }

                    pedestal.setStackInPedestal(stack);
                    pedestals.remove(pedestal);
                    break;
                }
            }
        }

        ItemStack catalyst = inventory.getStackInCore(0);
        ItemStack result = getRecipeOutput(catalyst);
        catalyst.shrink(this.catalyst.getCount());

        if (catalyst.getCount() <= 0) {
            catalyst = ItemStack.EMPTY;
        }

        inventory.setStackInCore(0, catalyst);
        inventory.setStackInCore(1, result.copy());
    }

    @Override
    public long getIngredientEnergyCost() {
        return energyCost;
    }

    @Override
    public void onCraftingTick(IFusionCraftingInventory inventory, World world, BlockPos pos) {
    }

    @Override
    public String canCraft(IFusionCraftingInventory inventory, World world, BlockPos pos) {

        if (!inventory.getStackInCore(1).isEmpty()) {
            return "outputObstructed";
        }

        List<ICraftingInjector> pedestals = new ArrayList<ICraftingInjector>();
        pedestals.addAll(inventory.getInjectors());

        for (ICraftingInjector pedestal : pedestals) {
            if (!pedestal.getStackInPedestal().isEmpty() && pedestal.getPedestalTier() < craftingTier) {
                return "tierLow";
            }
        }

        return "true";
    }

    @Override
    public String toString() {
        return String.format("SimpleFusionRecipe: {Result: %s, Catalyst: %s}", result, catalyst);
    }
}
