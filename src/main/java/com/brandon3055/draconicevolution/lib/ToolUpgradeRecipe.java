package com.brandon3055.draconicevolution.lib;

import cofh.redstoneflux.api.IEnergyContainerItem;
import com.brandon3055.brandonscore.utils.ItemNBTHelper;
import com.brandon3055.draconicevolution.api.fusioncrafting.SimpleFusionRecipe;
import com.brandon3055.draconicevolution.api.itemupgrade_dep.IUpgradableItem;
import com.brandon3055.draconicevolution.api.itemupgrade_dep.UpgradeHelper;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

/**
 * Created by brandon3055 on 17/11/2016.
 */
@Deprecated
public class ToolUpgradeRecipe extends SimpleFusionRecipe {

    /**
     * tier 0 = basic, tier 1 = wyvern, tier 2 = awakened, tier 3 = chaotic
     */
    public ToolUpgradeRecipe(ItemStack result, ItemStack catalyst, int energyCost, int craftingTier, Object... ingredients) {
        super(result, catalyst, energyCost, craftingTier, ingredients);
    }

    @Override
    public ItemStack getRecipeOutput(@Nonnull ItemStack catalyst) {
        ItemStack stack = super.getRecipeOutput(catalyst);

        if (catalyst.getItem() instanceof IEnergyContainerItem && catalyst.getItem() instanceof IEnergyContainerItem) {
            ItemNBTHelper.setInteger(stack, "Energy", ((IEnergyContainerItem) catalyst.getItem()).getEnergyStored(catalyst));
        }

        if (catalyst.getItem() instanceof IUpgradableItem && catalyst.getItem() instanceof IUpgradableItem) {
            UpgradeHelper.setUpgrades(stack, UpgradeHelper.getUpgrades(catalyst));
        }

        EnchantmentHelper.setEnchantments(EnchantmentHelper.getEnchantments(catalyst), stack);

        return stack;
    }

    @Override
    public String toString() {
        return String.format("ToolUpgradeRecipe: {Result: %s, Catalyst: %s}", result, catalyst);
    }
}
