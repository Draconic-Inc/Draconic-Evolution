package com.brandon3055.draconicevolution.api.itemupgrade;

import com.brandon3055.brandonscore.utils.InfoHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by brandon3055 on 31/05/2016.
 * This class contains useful helper methods for use with the upgrade system.
 */
public class UpgradeHelper {
    public static final String UPGRADE_TAG = "DEUpgrades";

    /**
     * @param stack   the stack.
     * @param upgrade the upgrade string.
     * @return the upgrade level.
     */
    public static int getUpgradeLevel(ItemStack stack, String upgrade) {
        if (!stack.hasTagCompound() || !stack.getTagCompound().hasKey(UPGRADE_TAG)) {
            return 0;
        }

        NBTTagCompound upgradeTag = stack.getOrCreateSubCompound(UPGRADE_TAG);
        return upgradeTag.getByte(upgrade);
    }

    /**
     * Note: This method assumes that you have already done the required checks to make sure the upgrade and level are
     * valid for this item.
     *
     * @param stack   the stack
     * @param upgrade the upgrade
     * @param level   the level
     */
    public static void setUpgradeLevel(ItemStack stack, String upgrade, int level) {
        NBTTagCompound upgradeTag = stack.getOrCreateSubCompound(UPGRADE_TAG);
        upgradeTag.setByte(upgrade, (byte) level);
    }

    /**
     * This returns a map of all upgrades and their level applied to a stack.
     *
     * @param stack a stack.
     * @return a map of all upgrades and their levels applied to this stack.
     */
    public static Map<String, Integer> getUpgrades(ItemStack stack) {
        Map<String, Integer> upgrades = new HashMap<>();
        if (!stack.hasTagCompound() || !stack.getTagCompound().hasKey(UPGRADE_TAG)) {
            return upgrades;
        }

        NBTTagCompound upgradeTag = stack.getOrCreateSubCompound(UPGRADE_TAG);

        for (String upgrade : upgradeTag.getKeySet()) {
            if (upgradeTag.hasKey(upgrade, 1)) {
                upgrades.put(upgrade, (int) upgradeTag.getByte(upgrade));
            }
        }

        return upgrades;
    }

    /**
     * Applies a map of upgrades to a stack. If the upgrade already exists on the item its level whil be changed to whatever the new level is.
     * Any upgrades already applied and do not exist in the given map will not be affected.
     * This will not apply an upgrade if the item does not accept it.
     *
     * @param stack    The stack.
     * @param upgrades A map of upgrates and their levels.
     */
    public static void setUpgrades(ItemStack stack, Map<String, Integer> upgrades) {
        if (!(stack.getItem() instanceof IUpgradableItem)) {
            return;
        }
        IUpgradableItem item = (IUpgradableItem) stack.getItem();

        for (String upgrade : upgrades.keySet()) {
            if (item.getValidUpgrades(stack).contains(upgrade)) {
                setUpgradeLevel(stack, upgrade, upgrades.get(upgrade));
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public static List<String> getUpgradeStats(ItemStack stack) {
        ArrayList<String> list = new ArrayList<String>();

        if (stack.getItem() instanceof IUpgradableItem) {
            for (String upgrade : ((IUpgradableItem) stack.getItem()).getValidUpgrades(stack)) {
                list.add(InfoHelper.ITC() + I18n.format("upgrade.de." + upgrade + ".name") + " " + InfoHelper.HITC() + I18n.format("upgrade.level." + getUpgradeLevel(stack, upgrade)));
            }
        }

        return list;
    }

}
