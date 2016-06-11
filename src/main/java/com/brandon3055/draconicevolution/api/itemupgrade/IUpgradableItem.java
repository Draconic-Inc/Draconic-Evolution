package com.brandon3055.draconicevolution.api.itemupgrade;

import net.minecraft.item.ItemStack;

/**
 * Created by brandon3055 on 31/05/2016.
 */
public interface IUpgradableItem {

    /**
     * Add valid upgrades to the given list as well as their maximum level.
     * Or -1 if the upgrade has no cap.
     * @return upgrades
     */
    ItemUpgradeRegistry getValidUpgrades(ItemStack stack, ItemUpgradeRegistry upgradeRegistry);

    /**
     * @return the maximum number of upgrades for this item
     */
    int getUpgradeCapacity(ItemStack stack);
}
