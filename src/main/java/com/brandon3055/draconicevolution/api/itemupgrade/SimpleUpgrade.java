package com.brandon3055.draconicevolution.api.itemupgrade;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;

/**
 * Created by brandon3055 on 1/06/2016.
 */
public class SimpleUpgrade implements IUpgrade {

    private String name;
    private ResourceLocation sprite = new ResourceLocation("");
    private ItemStack[] recipe = new ItemStack[0];
    private int maxLevel = 32;

    public SimpleUpgrade(String name){
        this.name = name;
        UpgradeRegistry.registerUpgrade(this);
    }

    public SimpleUpgrade setSprite(ResourceLocation sprite){
        this.sprite = sprite;
        return this;
    }

    public SimpleUpgrade setRecipe(ItemStack[] recipe){
        this.recipe = recipe;
        return this;
    }

    public SimpleUpgrade setMaxLevel(int maxLevel){
        this.maxLevel = maxLevel;
        return this;
    }

    public int getLevel(ItemStack stack){
        return UpgradeRegistry.getUpgradeLevel(this, stack);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getLocalizedName() {
        return I18n.translateToLocal("upgrade.de." + getName() + ".txt");
    }

    @Override
    public ResourceLocation getSprite() {
        return sprite;
    }

    @Override
    public ItemStack[] getRecipe() {
        return recipe;
    }

    @Override
    public int getMaxLevel(ItemStack stack) {
        return maxLevel;
    }

    @Override
    public void onApplied(ItemStack stack) {

    }

    @Override
    public void onRemoved(ItemStack stack) {

    }
}
