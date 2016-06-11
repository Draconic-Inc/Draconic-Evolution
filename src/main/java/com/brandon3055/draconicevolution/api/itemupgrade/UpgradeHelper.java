package com.brandon3055.draconicevolution.api.itemupgrade;

import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by brandon3055 on 31/05/2016.
 * This class contains useful helper methods for use with the upgrade system.
 */
public class UpgradeHelper {
    //region Name References
    public static final String RF_CAPACITY = "RFCap";
    public static final String DIG_SPEED = "DigSpeed";
    public static final String DIG_AOE = "DigAOE";
    public static final String DIG_DEPTH = "DigDepth";
    public static final String ATTACK_DAMAGE = "AttackDmg";
    public static final String ATTACK_AOE = "AttackAOE";
    public static final String ARROW_DAMAGE = "ArrowDmg";
    public static final String DRAW_SPEED = "DrawSpeed";
    public static final String ARROW_SPEED = "ArrowSpeed";
    public static final String SHIELD_CAPACITY = "ShieldCap";
    public static final String SHIELD_RECOVERY = "ShieldRec";
    public static final String MOVE_SPEED = "MoveSpeed";
    public static final String JUMP_BOOST = "JumpBoost";
//    public static final SimpleUpgrade RF_CAPACITY = new SimpleUpgrade("RFCap");
//    public static final SimpleUpgrade DIG_SPEED = new SimpleUpgrade("DigSpeed");
//    public static final SimpleUpgrade DIG_AOE = new SimpleUpgrade("DigAOE").setMaxLevel(5);
//    public static final SimpleUpgrade DIG_DEPTH = new SimpleUpgrade("DigDepth").setMaxLevel(5);
//    public static final SimpleUpgrade ATTACK_DAMAGE = new SimpleUpgrade("AttackDmg");
//    public static final SimpleUpgrade ATTACK_AOE = new SimpleUpgrade("AttackAOE").setMaxLevel(5);
//    public static final SimpleUpgrade ARROW_DAMAGE = new SimpleUpgrade("ArrowDmg");
//    public static final SimpleUpgrade DRAW_SPEED = new SimpleUpgrade("DrawSpeed");
//    public static final SimpleUpgrade ARROW_SPEED = new SimpleUpgrade("ArrowSpeed");
//    public static final SimpleUpgrade SHIELD_CAPACITY = new SimpleUpgrade("ShieldCap");
//    public static final SimpleUpgrade SHIELD_RECOVERY = new SimpleUpgrade("ShieldRec");
//    public static final SimpleUpgrade MOVE_SPEED = new SimpleUpgrade("MoveSpeed");//todo Set max
//    public static final SimpleUpgrade JUMP_BOOST = new SimpleUpgrade("JumpBoost");//todo Set max
    //endregion

    public static int getUpgradeLevel(ItemStack stack, String upgradeName){
        if (stack == null || !(stack.getItem() instanceof IUpgradableItem)){
            return 0;
        }

        IUpgradableItem item = (IUpgradableItem) stack.getItem();
        IUpgrade upgrade = item.getValidUpgrades(stack, new ItemUpgradeRegistry()).getUpgrade(upgradeName);

        if (upgrade != null){
            return upgrade.getLevel();
        }

        return 0;
    }


//    public static void addUpgrade(IUpgrade upgrade, int level, ItemStack stack) {
//        if (stack.getTagCompound() == null) {
//            stack.setTagCompound(new NBTTagCompound());
//        }
//
//        NBTTagCompound itemCompound = stack.getTagCompound();
//
//        if (!itemCompound.hasKey("Upgrades")) {
//            itemCompound.setTag("Upgrades", new NBTTagList());
//        }
//
//        NBTTagList upgradeList = itemCompound.getTagList("Upgrades", 10);
//        NBTTagCompound newTag = new NBTTagCompound();
//        newTag.setString("Name", upgrade.getName());
//        newTag.setByte("Level", (byte) level);
//        upgradeList.appendTag(newTag);
//    }
//
//    public static int getTotalSlotsUsed(ItemStack stack) {
//        if (stack.getTagCompound() == null) {
//            stack.setTagCompound(new NBTTagCompound());
//        }
//
//        NBTTagCompound itemCompound = stack.getTagCompound();
//
//        if (!itemCompound.hasKey("Upgrades")) {
//            return 0;
//        }
//
//        return itemCompound.getTagList("Upgrades", 10).tagCount();
//    }
//
//    public static Map<IUpgrade, Integer> getUpgrades(ItemStack stack) {
//        Map<IUpgrade, Integer> map = new HashMap<IUpgrade, Integer>();
//
//        for (IUpgrade upgrade : upgradeRegistry.values()) {
//            int level = getUpgradeLevel(upgrade, stack);
//            if (level > 0) {
//                map.put(upgrade, level);
//            }
//        }
//
//        return map;
//    }

    public static List<String> getUpgradeStats(ItemStack stack) {
        ArrayList<String> list = new ArrayList<String>();


        return new ArrayList<String>();
    }
}
