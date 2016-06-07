package com.brandon3055.draconicevolution.api.itemupgrade;

import net.minecraft.item.ItemStack;

import java.util.Map;

/**
 * Created by brandon3055 on 31/05/2016.
 */
public interface IUpgradableItem {

    /**
     * Add valid upgrades to the given list as well as their maximum level.
     * Or -1 if the upgrade has no cap.
     * @return upgrades
     */
    Map<IUpgrade, Integer> getValidUpgrades(ItemStack stack, Map<IUpgrade, Integer> upgrades);

    /**
     * @return the maximum number of upgrades for this item
     */
    int getUpgradeSlots(ItemStack stack);

    /**
     * @return the max upgrade tier allowed for this item
     * 0 = Basic
     * 1 = Wyvern
     * 2 = Awakened
     * 3 = Chaotic
     */
    int getMaxUpgradeTier(ItemStack stack);

}
