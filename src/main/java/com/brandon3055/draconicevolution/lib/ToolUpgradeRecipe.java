package com.brandon3055.draconicevolution.lib;

import cofh.api.energy.IEnergyContainerItem;
import com.brandon3055.brandonscore.utils.ItemNBTHelper;
import com.brandon3055.draconicevolution.api.fusioncrafting.SimpleFusionRecipe;
import com.brandon3055.draconicevolution.api.itemupgrade.IUpgradableItem;
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

        if (catalyst != null && stack != null && catalyst.getItem() instanceof IEnergyContainerItem && catalyst.getItem() instanceof IEnergyContainerItem) {
            ItemNBTHelper.setInteger(stack, "Energy", ((IEnergyContainerItem) catalyst.getItem()).getEnergyStored(catalyst));
//            ((ItemEnergyBase) stack.getItem()).setEnergy(stack, ((ItemEnergyBase) catalyst.getItem()).getEnergyStored(catalyst));
        }

        if (catalyst != null && stack != null && catalyst.getItem() instanceof IUpgradableItem && catalyst.getItem() instanceof IUpgradableItem) {
            UpgradeHelper.setUpgrades(stack, UpgradeHelper.getUpgrades(catalyst));
        }

        return stack;
    }
}
