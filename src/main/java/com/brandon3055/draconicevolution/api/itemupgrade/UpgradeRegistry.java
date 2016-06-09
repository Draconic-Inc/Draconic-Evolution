package com.brandon3055.draconicevolution.api.itemupgrade;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by brandon3055 on 31/05/2016.
 */
public class UpgradeRegistry
{
    public static Map<String, IUpgrade> upgradeRegistry = new HashMap<String, IUpgrade>();

    //todo on all. Set sprites and recipe
    public static final SimpleUpgrade RF_CAPACITY = new SimpleUpgrade("RFCap");
    public static final SimpleUpgrade DIG_SPEED = new SimpleUpgrade("DigSpeed");
    public static final SimpleUpgrade DIG_AOE = new SimpleUpgrade("DigAOE").setMaxLevel(5);
    public static final SimpleUpgrade DIG_DEPTH = new SimpleUpgrade("DigDepth").setMaxLevel(5);
    public static final SimpleUpgrade ATTACK_DAMAGE = new SimpleUpgrade("AttackDmg");
    public static final SimpleUpgrade ATTACK_AOE = new SimpleUpgrade("AttackAOE").setMaxLevel(5);
    public static final SimpleUpgrade ARROW_DAMAGE = new SimpleUpgrade("ArrowDmg");
    public static final SimpleUpgrade DRAW_SPEED = new SimpleUpgrade("DrawSpeed");
    public static final SimpleUpgrade ARROW_SPEED = new SimpleUpgrade("ArrowSpeed");
    public static final SimpleUpgrade SHIELD_CAPACITY = new SimpleUpgrade("ShieldCap");
    public static final SimpleUpgrade SHIELD_RECOVERY = new SimpleUpgrade("ShieldRec");
    public static final SimpleUpgrade MOVE_SPEED = new SimpleUpgrade("MoveSpeed");//todo Set max
    public static final SimpleUpgrade JUMP_BOOST = new SimpleUpgrade("JumpBoost");//todo Set max

    public static void registerUpgrade(IUpgrade upgrade){
        if (upgradeRegistry.containsKey(upgrade.getName())){
            throw new RuntimeException("Duplicate IUpgrade Detected! There can only be 1 IUpgrade registered for any given upgrade name!");
        }

        upgradeRegistry.put(upgrade.getName(), upgrade);
    }

    public static void addUpgrade(IUpgrade upgrade, int level, ItemStack stack){
        if (stack.getTagCompound() == null){
            stack.setTagCompound(new NBTTagCompound());
        }

        NBTTagCompound itemCompound = stack.getTagCompound();

        if (!itemCompound.hasKey("Upgrades")){
            itemCompound.setTag("Upgrades", new NBTTagList());
        }

        NBTTagList upgradeList = itemCompound.getTagList("Upgrades", 10);
        NBTTagCompound newTag = new NBTTagCompound();
        newTag.setString("Name", upgrade.getName());
        newTag.setByte("Level", (byte)level);
        upgradeList.appendTag(newTag);
    }

    public static int getTotalSlotsUsed(ItemStack stack){
        if (stack.getTagCompound() == null){
            stack.setTagCompound(new NBTTagCompound());
        }

        NBTTagCompound itemCompound = stack.getTagCompound();

        if (!itemCompound.hasKey("Upgrades")){
            return 0;
        }

        return itemCompound.getTagList("Upgrades", 10).tagCount();
    }

    public static Map<IUpgrade, Integer> getUpgrades(ItemStack stack){
        Map<IUpgrade, Integer> map = new HashMap<IUpgrade, Integer>();

        for (IUpgrade upgrade : upgradeRegistry.values()) {
            int level = getUpgradeLevel(upgrade, stack);
            if (level > 0){
                map.put(upgrade, level);
            }
        }

        return map;
    }

    public static int getUpgradeLevel(IUpgrade upgrade, ItemStack stack) {
        if (stack.getTagCompound() == null){
            stack.setTagCompound(new NBTTagCompound());
        }

        NBTTagCompound itemCompound = stack.getTagCompound();

        if (!itemCompound.hasKey("Upgrades")){
            return 0;
        }

        int level = 0;

        NBTTagList upgradeList = itemCompound.getTagList("Upgrades", 10);

        for (int i = 0; i < upgradeList.tagCount(); i++){
            NBTTagCompound tag = upgradeList.getCompoundTagAt(i);
            if (tag.getString("Name").equals(upgrade.getName())){
                level += tag.getByte("Level");
            }
        }

        return level;
    }

    public static List<String> getUpgradeStats(ItemStack stack){
        ArrayList<String> list = new ArrayList<String>();



        return new ArrayList<String>();
    }
}
