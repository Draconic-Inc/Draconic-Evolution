package com.brandon3055.draconicevolution.api.itemupgrade;

import net.minecraft.item.ItemStack;

import java.util.List;

/**
 * Created by brandon3055 on 31/05/2016.
 */
public interface IUpgradableItem {

//    /**
//     * Add valid upgrades to the given list as well as their maximum level.
//     * Or -1 if the upgrade has no cap.
//     * @return upgrades
//     */
//    ItemUpgradeRegistry getValidUpgrades(ItemStack stack, ItemUpgradeRegistry upgradeRegistry);
//
//    /**
//     * @return the maximum number of upgrades for this item
//     */
//    int getUpgradeCapacity(ItemStack stack);

    /**
     * @param stack The stack
     * @return a list of all upgrades that can be applied to this item
     */
    List<String> getValidUpgrades(ItemStack stack);

    /**
     * @param stack The stack
     * @param upgrade The upgrade
     * @return Returns the max upgrade level this item can accept (1 = basic, 2 = wyvern, 3 = draconic, 4 = chaotic)
     */
    int getMaxUpgradeLevel(ItemStack stack, String upgrade);
}
