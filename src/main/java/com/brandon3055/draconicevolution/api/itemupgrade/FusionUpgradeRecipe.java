package com.brandon3055.draconicevolution.api.itemupgrade;

import com.brandon3055.draconicevolution.api.OreDictHelper;
import com.brandon3055.draconicevolution.api.fusioncrafting.ICraftingPedestal;
import com.brandon3055.draconicevolution.api.fusioncrafting.IFusionCraftingInventory;
import com.brandon3055.draconicevolution.api.fusioncrafting.IFusionRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by brandon3055 on 11/06/2016.
 * This is the recipe class used for upgrading items. If uoi would like to add a custom upgrade recipe please extend
 * this so it can be identified as an upgrade recipe.
 */
public class FusionUpgradeRecipe implements IFusionRecipe {

    protected final int upgradeLevel;
    protected final ItemStack upgradeKey;
    protected final String upgrade;
    protected List<Object> ingredients;
    protected int energyCost;
    protected int craftingTier;

    public FusionUpgradeRecipe(String upgrade, ItemStack upgradeKey, int energyCost, int craftingTier, int upgradeLevel, Object... ingredients) {
        this.upgrade = upgrade;
        this.upgradeLevel = upgradeLevel;
        this.upgradeKey = upgradeKey;
        this.ingredients = new LinkedList<Object>();
        Collections.addAll(this.ingredients, ingredients);
        this.ingredients.add(upgradeKey);
        this.energyCost = energyCost;
        this.craftingTier = craftingTier;
    }


    @Override
    public ItemStack getRecipeOutput(@Nullable ItemStack catalyst) {
        ItemStack stack = catalyst.copy();
        UpgradeHelper.setUpgradeLevel(stack, upgrade, upgradeLevel);
        return stack;
    }

    @Override
    public boolean isRecipeCatalyst(ItemStack catalyst) {
        return catalyst != null && catalyst.getItem() instanceof IUpgradableItem && ((IUpgradableItem) catalyst.getItem()).getValidUpgrades(catalyst).contains(upgrade);
    }

    @Override
    public List<Object> getRecipeIngredients() {
        return ingredients;
    }

    @Override
    public boolean matches(IFusionCraftingInventory inventory, World world, BlockPos pos) {
        List<ICraftingPedestal> pedestals = new ArrayList<ICraftingPedestal>();
        pedestals.addAll(inventory.getPedestals());

        //Check the item is upgradable
        if (!isRecipeCatalyst(inventory.getStackInCore(0))) {
            return false;
        }

        //Check if the upgrade key is present
        boolean flag = false;
        for (ICraftingPedestal pedestal : pedestals) {
            if (pedestal.getStackInPedestal() != null && upgradeKey.isItemEqual(pedestal.getStackInPedestal())) {
                flag = true;
                break;
            }
        }

        if (!flag){
            return false;
        }

        //Check that all of the ingredients are available.
        for (Object ingredient : ingredients) {
            flag = false;

            for (ICraftingPedestal pedestal : pedestals) {
                if (pedestal.getStackInPedestal() != null && OreDictHelper.areStacksEqual(ingredient, pedestal.getStackInPedestal())) {
                    flag = true;
                    pedestals.remove(pedestal);
                    break;
                }
            }

            if (!flag) {
                return false;
            }
        }

        //Check that there are no extra items that are not part of the recipe.
        for (ICraftingPedestal pedestal : pedestals) {
            if (pedestal.getStackInPedestal() != null && !pedestal.getStackInPedestal().isItemEqual(upgradeKey)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public String canCraft(IFusionCraftingInventory inventory, World world, BlockPos pos) {

        if (!isRecipeCatalyst(inventory.getStackInCore(0))) {
            return "upgrade.de.upgradeNA.info";
        }

        if (inventory.getStackInCore(1) != null){
            return "outputObstructed";
        }

        IUpgradableItem item = (IUpgradableItem)inventory.getStackInCore(0).getItem();
        if (item.getMaxUpgradeLevel(inventory.getStackInCore(0), upgrade) < upgradeLevel){
            return "upgrade.de.upgradeLevelToHigh.info";
        }

        if (UpgradeHelper.getUpgradeLevel(inventory.getStackInCore(0), upgrade) >= upgradeLevel) {
            return "upgrade.de.upgradeApplied.info";
        }

        if (UpgradeHelper.getUpgradeLevel(inventory.getStackInCore(0), upgrade) < upgradeLevel - 1) {
            return "upgrade.de.upgradePrevLevelRequired.info";
        }

        List<ICraftingPedestal> pedestals = new ArrayList<ICraftingPedestal>();
        pedestals.addAll(inventory.getPedestals());

        for (ICraftingPedestal pedestal : pedestals) {
            if (pedestal.getStackInPedestal() != null && pedestal.getPedestalTier() < craftingTier) {
                return "tierLow";
            }
        }

        return "true";
    }

    @Override
    public void craft(IFusionCraftingInventory inventory, World world, BlockPos pos) {
        //This shouldn't be needed but cant hurt.
        if (!matches(inventory, world, pos)) {
            return;
        }

        List<ICraftingPedestal> pedestals = new ArrayList<ICraftingPedestal>();
        pedestals.addAll(inventory.getPedestals());

        //Use Ingredients
        for (Object ingredient : ingredients) {
            for (ICraftingPedestal pedestal : pedestals) {
                if (pedestal.getStackInPedestal() != null && OreDictHelper.areStacksEqual(ingredient, pedestal.getStackInPedestal()) && pedestal.getPedestalTier() >= craftingTier && !pedestal.getStackInPedestal().isItemEqual(upgradeKey)) {

                    ItemStack stack = pedestal.getStackInPedestal();
                    if (stack.getItem().hasContainerItem(stack)) {
                        stack = stack.getItem().getContainerItem(stack);
                    } else {
                        stack.stackSize--;
                        if (stack.stackSize <= 0) {
                            stack = null;
                        }
                    }

                    pedestal.setStackInPedestal(stack);
                    pedestals.remove(pedestal);
                    break;
                }
            }
        }

        ItemStack stack = inventory.getStackInCore(0);
        inventory.setStackInCore(0, null);
        UpgradeHelper.setUpgradeLevel(stack, upgrade, upgradeLevel);
        inventory.setStackInCore(1, stack);
    }

    @Override
    public int getEnergyCost() {
        return energyCost;
    }

    @Override
    public void onCraftingTick(IFusionCraftingInventory inventory, World world, BlockPos pos) {
    }

    @Override
    public String toString() {
        return String.format("FusionUpgradeRecipe: [Upgrade: %s, Level: %s]", upgrade, upgradeLevel);
    }
}
