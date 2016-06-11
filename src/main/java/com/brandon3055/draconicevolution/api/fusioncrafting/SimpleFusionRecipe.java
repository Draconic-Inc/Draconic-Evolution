package com.brandon3055.draconicevolution.api.fusioncrafting;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by brandon3055 on 11/06/2016.
 * A simple implementation if IFusionRecipe
 */
public class SimpleFusionRecipe implements IFusionRecipe {

    protected ItemStack result;
    protected ItemStack catalyst;
    protected ItemStack[] ingredients;
    protected int energyCost;
    protected int craftingTier;

    public SimpleFusionRecipe(ItemStack result, ItemStack catalyst, ItemStack[] ingredients, int energyCost, int craftingTier){
        this.result = result;
        this.catalyst = catalyst;
        this.ingredients = ingredients;
        this.energyCost = energyCost;
        this.craftingTier = craftingTier;
    }

    @Override
    public boolean matches(IFusionCraftingInventory inventory, World world, BlockPos pos) {
        List<ICraftingPedestal> pedestals = new ArrayList<ICraftingPedestal>();
        pedestals.addAll(inventory.getPedestals());

        //Check the catalyst for this recipe
        if (inventory.getStackInCore() == null || !inventory.getStackInCore().isItemEqual(catalyst)){
            return false;
        }

        //Check that all of the ingredients are available.
        for (ItemStack ingredient : ingredients){
            boolean foundIngredient = false;

            for (ICraftingPedestal pedestal : pedestals){
                if (pedestal.getStackInPedestal() != null && pedestal.getStackInPedestal().isItemEqual(ingredient) && pedestal.getPedestalTier() >= craftingTier){
                    foundIngredient = true;
                    pedestals.remove(pedestal);
                    break;
                }
            }

            if (!foundIngredient){
                return false;
            }
        }

        //Check that there are no extra items that are not part of the recipe.
        for (ICraftingPedestal pedestal : pedestals){
            if (pedestal.getStackInPedestal() != null){
                return false;
            }
        }

        return true;
    }

    @Override
    public void craft(IFusionCraftingInventory inventory, World world, BlockPos pos) {
        //This shouldn't be needed but cant hurt.
        if (!matches(inventory, world, pos)){
            return;
        }

        List<ICraftingPedestal> pedestals = new ArrayList<ICraftingPedestal>();
        pedestals.addAll(inventory.getPedestals());

        //Use Ingredients
        for (ItemStack ingredient : ingredients){
            for (ICraftingPedestal pedestal : pedestals){
                if (pedestal.getStackInPedestal() != null && pedestal.getStackInPedestal().isItemEqual(ingredient) && pedestal.getPedestalTier() >= craftingTier){

                    ItemStack stack = pedestal.getStackInPedestal();
                    if (stack.getItem().hasContainerItem(stack)){
                        stack = stack.getItem().getContainerItem(stack);
                    }
                    else {
                        stack.stackSize--;
                        if (stack.stackSize <= 0){
                            stack = null;
                        }
                    }

                    pedestal.setStackInPedestal(stack);
                    pedestals.remove(pedestal);
                    break;
                }
            }
        }

        inventory.setStackInCore(result.copy());
    }

    @Override
    public int getEnergyCost() {
        return energyCost;
    }

    @Override
    public void onCraftingTick(IFusionCraftingInventory inventory, World world, BlockPos pos) {}
}
