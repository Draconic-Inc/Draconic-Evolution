package com.brandon3055.draconicevolution.lib;

import com.brandon3055.brandonscore.items.ItemEnergyBase;
import com.brandon3055.draconicevolution.api.fusioncrafting.SimpleFusionRecipe;
import com.brandon3055.draconicevolution.api.itemupgrade.UpgradeHelper;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

/**
 * Created by brandon3055 on 17/11/2016.
 */
public class ToolUpgradeRecipe extends SimpleFusionRecipe {

    /**
     * tier 0 = basic, tier 1 = wyvern, tier 2 = awakened, tier 3 = chaotic
     */
    public ToolUpgradeRecipe(ItemStack result, ItemStack catalyst, int energyCost, int craftingTier, Object... ingredients) {
        super(result, catalyst, energyCost, craftingTier, ingredients);
    }

    @Override
    public ItemStack getRecipeOutput(@Nullable ItemStack catalyst) {
        ItemStack stack = super.getRecipeOutput(catalyst);

        if (catalyst != null && stack != null && catalyst.getItem() instanceof ItemEnergyBase && catalyst.getItem() instanceof ItemEnergyBase) {
            ((ItemEnergyBase) stack.getItem()).setEnergy(stack, ((ItemEnergyBase) catalyst.getItem()).getEnergyStored(catalyst));
            UpgradeHelper.setUpgrades(stack, UpgradeHelper.getUpgrades(catalyst));
        }

        return stack;
    }
}
