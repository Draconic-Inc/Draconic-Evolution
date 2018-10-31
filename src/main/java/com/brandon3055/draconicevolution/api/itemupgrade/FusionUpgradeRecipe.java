package com.brandon3055.draconicevolution.api.itemupgrade;

import com.brandon3055.draconicevolution.DEFeatures;
import com.brandon3055.draconicevolution.api.OreDictHelper;
import com.brandon3055.draconicevolution.api.fusioncrafting.ICraftingInjector;
import com.brandon3055.draconicevolution.api.fusioncrafting.IFusionCraftingInventory;
import com.brandon3055.draconicevolution.api.fusioncrafting.IFusionRecipe;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.*;

/**
 * Created by brandon3055 on 11/06/2016.
 * This is the recipe class used for upgrading items. If uoi would like to add a custom upgrade recipe please extend
 * this so it can be identified as an upgrade recipe.
 */
public class FusionUpgradeRecipe implements IFusionRecipe {

    public final int upgradeLevel;
    public final ItemStack upgradeKey;
    public final String upgrade;
    protected List<Object> ingredients;
    protected long energyCost;
    protected int craftingTier;
    private static Item[] tools = new Item[]{DEFeatures.wyvernAxe, DEFeatures.draconicAxe, DEFeatures.wyvernShovel, DEFeatures.draconicShovel, DEFeatures.wyvernPick, DEFeatures.draconicPick, DEFeatures.wyvernSword, DEFeatures.draconicSword, DEFeatures.wyvernBow, DEFeatures.draconicBow, DEFeatures.draconicStaffOfPower};
    private static Random rand = new Random();

    public FusionUpgradeRecipe(String upgrade, ItemStack upgradeKey, long energyCost, int craftingTier, int upgradeLevel, Object... ingredients) {
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
    public ItemStack getRecipeOutput(@Nonnull ItemStack catalyst) {
        if (catalyst.isEmpty()) {
            return ItemStack.EMPTY;
        }

        ItemStack stack = catalyst.copy();
        UpgradeHelper.setUpgradeLevel(stack, upgrade, upgradeLevel);
        return stack;
    }

    @Override
    public boolean isRecipeCatalyst(ItemStack catalyst) {
        return !catalyst.isEmpty() && catalyst.getItem() instanceof IUpgradableItem && ((IUpgradableItem) catalyst.getItem()).getValidUpgrades(catalyst).contains(upgrade);
    }

    @Override
    public ItemStack getRecipeCatalyst() {
        return ItemStack.EMPTY;//new ItemStack(tools[rand.nextInt(tools.length)]);
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

        //Check the item is upgradable
        if (!isRecipeCatalyst(inventory.getStackInCore(0))) {
            return false;
        }

        //Check if the upgrade key is present
        boolean flag = false;
        for (ICraftingInjector pedestal : pedestals) {
            if (!pedestal.getStackInPedestal().isEmpty() && upgradeKey.isItemEqual(pedestal.getStackInPedestal())) {
                flag = true;
                break;
            }
        }

        if (!flag) {
            return false;
        }

        //Check that all of the ingredients are available.
        for (Object ingredient : ingredients) {
            flag = false;

            for (ICraftingInjector pedestal : pedestals) {
                if (!pedestal.getStackInPedestal().isEmpty() && OreDictHelper.areStacksEqual(ingredient, pedestal.getStackInPedestal())) {
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
        for (ICraftingInjector pedestal : pedestals) {
            if (!pedestal.getStackInPedestal().isEmpty() && !pedestal.getStackInPedestal().isItemEqual(upgradeKey)) {
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

        if (!inventory.getStackInCore(1).isEmpty()) {
            return "outputObstructed";
        }

        IUpgradableItem item = (IUpgradableItem) inventory.getStackInCore(0).getItem();
        if (item.getMaxUpgradeLevel(inventory.getStackInCore(0), upgrade) < upgradeLevel) {
            return "upgrade.de.upgradeLevelToHigh.info";
        }

        if (UpgradeHelper.getUpgradeLevel(inventory.getStackInCore(0), upgrade) >= upgradeLevel) {
            return "upgrade.de.upgradeApplied.info";
        }

        if (UpgradeHelper.getUpgradeLevel(inventory.getStackInCore(0), upgrade) < upgradeLevel - 1) {
            return "upgrade.de.upgradePrevLevelRequired.info";
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
    public void craft(IFusionCraftingInventory inventory, World world, BlockPos pos) {
        //This shouldn't be needed but cant hurt.
        if (!matches(inventory, world, pos)) {
            return;
        }

        List<ICraftingInjector> pedestals = new ArrayList<ICraftingInjector>();
        pedestals.addAll(inventory.getInjectors());

        //Use Ingredients
        for (Object ingredient : ingredients) {
            for (ICraftingInjector pedestal : pedestals) {
                if (!pedestal.getStackInPedestal().isEmpty() && OreDictHelper.areStacksEqual(ingredient, pedestal.getStackInPedestal()) && pedestal.getPedestalTier() >= craftingTier && !pedestal.getStackInPedestal().isItemEqual(upgradeKey)) {

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

        ItemStack stack = inventory.getStackInCore(0);
        inventory.setStackInCore(0, ItemStack.EMPTY);
        UpgradeHelper.setUpgradeLevel(stack, upgrade, upgradeLevel);
        inventory.setStackInCore(1, stack);
    }

    @Override
    public long getIngredientEnergyCost() {
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
