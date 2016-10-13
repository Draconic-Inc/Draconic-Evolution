package com.brandon3055.draconicevolution.api.itemupgrade;

import com.brandon3055.brandonscore.utils.InfoHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by brandon3055 on 31/05/2016.
 * This class contains useful helper methods for use with the upgrade system.
 */
public class UpgradeHelper {
    public static final String UPGRADE_TAG = "DEUpgrades";

    /**
     * @param stack the stack.
     * @param upgrade the upgrade string.
     * @return the upgrade level.
     */
    public static int getUpgradeLevel(ItemStack stack, String upgrade) {
        if (!stack.hasTagCompound() || !stack.getTagCompound().hasKey(UPGRADE_TAG)) {
            return 0;
        }

        NBTTagCompound upgradeTag = stack.getSubCompound(UPGRADE_TAG, true);
        return upgradeTag.getByte(upgrade);
    }

    /**
     * Note: This method assumes that you have already done the required checks to make sure the upgrade and level are
     * valid for this item.
     * @param stack the stack
     * @param upgrade the upgrade
     * @param level the level
     */
    public static void setUpgradeLevel(ItemStack stack, String upgrade, int level) {
        NBTTagCompound upgradeTag = stack.getSubCompound(UPGRADE_TAG, true);
        upgradeTag.setByte(upgrade, (byte)level);
    }

    @SideOnly(Side.CLIENT)
    public static List<String> getUpgradeStats(ItemStack stack) {
        ArrayList<String> list = new ArrayList<String>();

        if (stack != null && stack.getItem() instanceof IUpgradableItem){
            for (String upgrade : ((IUpgradableItem) stack.getItem()).getValidUpgrades(stack)){
                list.add(InfoHelper.ITC() + I18n.format("upgrade.de." + upgrade + ".name") + " " + InfoHelper.HITC() + I18n.format("upgrade.level." + getUpgradeLevel(stack, upgrade)));
            }
        }

        return list;
    }

}
