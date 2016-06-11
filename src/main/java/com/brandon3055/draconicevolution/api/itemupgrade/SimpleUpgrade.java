package com.brandon3055.draconicevolution.api.itemupgrade;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

/**
 * Created by brandon3055 on 1/06/2016.
 */
public class SimpleUpgrade implements IUpgrade {//todo finish implementation

    private String name;
    private ResourceLocation sprite = new ResourceLocation("");
    private ItemStack[] recipe = new ItemStack[0];
    private int maxLevel = 32;

    public SimpleUpgrade(String name){
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getUnlocalizedName() {
        return "upgrade.de." + getName() + ".name";
    }

    public SimpleUpgrade setSprite(ResourceLocation sprite){
        this.sprite = sprite;
        return this;
    }

    @Override
    public ResourceLocation getSprite() {
        return sprite;
    }

    @Override
    public ItemStack[] getRecipeForTier(int tier) {
        return new ItemStack[0];
    }

    @Override
    public int getMaxTier() {
        return 0;
    }

    @Override
    public int getMaxLevel() {
        return 0;
    }

    @Override
    public int getLevel() {
        return 0;
    }

    @Override
    public int getUpgradeCount(int tier) {
        return 0;
    }

    @Override
    public void onApplied(ItemStack stack) {

    }

    @Override
    public void onRemoved(ItemStack stack) {

    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {

    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {

    }


//

//
//    public SimpleUpgrade setRecipe(ItemStack[] recipe){
//        this.recipe = recipe;
//        return this;
//    }
//
//    public SimpleUpgrade setMaxLevel(int maxLevel){
//        this.maxLevel = maxLevel;
//        return this;
//    }
//
//    public int getLevel(ItemStack stack){
//        return UpgradeHelper.getUpgradeLevel(this, stack);
//    }
//
//    @Override
//    public String getName() {
//        return name;
//    }
//
//    @Override
//    public String getUnlocalizedName() {
//        return I18n.translateToLocal("upgrade.de." + getName() + ".txt");
//    }
//
//    @Override
//    public ResourceLocation getSprite() {
//        return sprite;
//    }
//
//    @Override
//    public ItemStack[] getRecipe() {
//        return recipe;
//    }
//
//    @Override
//    public int getMaxLevel(ItemStack stack) {
//        return maxLevel;
//    }
//
//    @Override
//    public void onApplied(ItemStack stack) {
//
//    }
//
//    @Override
//    public void onRemoved(ItemStack stack) {
//
//    }
}
