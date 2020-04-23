package com.brandon3055.draconicevolution.api.itemupgrade;

/**
 * Created by brandon3055 on 8/06/2016.
 * A simple map wrapper for handling IUpgrade's
 */
@Deprecated
public class ItemUpgradeRegistry {
//    public static final String UPGRADE_TAG = "DEUpgrades";
//
//    protected Map<Integer, IUpgrade> upgradeRegistry = new HashMap<Integer, IUpgrade>();
//    protected Map<String, Integer> nameToIndexMap = new HashMap<String, Integer>();
//    private int index = 0;
//
//    /**
//     * Adds an upgrade to the registry and reads its current value from the given item stack.
//     * Will also write the default value to the stack if the stack does not contain a tag for this upgrade.
//     * */
//    public ItemUpgradeRegistry register(ItemStack stack, IUpgrade upgrade){
//        upgradeRegistry.put(index, upgrade);
//        nameToIndexMap.put(upgrade.getName(), index);
//
//        CompoundNBT upgradeTag = stack.getSubCompound(UPGRADE_TAG, true);
//        if (!upgradeTag.hasKey(upgrade.getName())){
//            upgrade.writeToNBT(upgradeTag);
//        }
//        else {
//            upgrade.readFromNBT(upgradeTag);
//        }
//
//        index++;
//        return this;
//    }
//
//    public IUpgrade getUpgrade(int index){
//        return upgradeRegistry.get(index);
//    }
//
//    public IUpgrade getUpgrade(String name){
//        for (IUpgrade upgrade : upgradeRegistry.values()){
//            if (upgrade.getName().equals(name)){
//                return upgrade;
//            }
//        }
//        return null;
//    }
//
//    public String getNameFromIndex(int index){
//        IUpgrade upgrade = getUpgrade(index);
//        return upgrade == null ? "" : upgrade.getName();
//    }
//
//    public int getIndexFromName(String name){
//        Integer index = nameToIndexMap.get(name);
//        return index == null ? -1 : index;
//    }
//
//    public Collection<IUpgrade> getUpgrades(){
//        return upgradeRegistry.values();
//    }
//
//    public void clear(){
//        index = 0;
//        upgradeRegistry.clear();
//        nameToIndexMap.clear();
//    }
//
//    public int size() {
//        return upgradeRegistry.size();
//    }
}
