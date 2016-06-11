package com.brandon3055.draconicevolution.api.itemupgrade;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

/**
 * Created by brandon3055 on 31/05/2016.
 *
 */
public interface IUpgrade {

    /**
     * Returns the name of the upgrade.
     * */
    String getName();

    /**
     * Returns the unlocalized name of the upgrade.
     * */
    String getUnlocalizedName();

    /**
     * Returns a sprite for the upgrade.//todo Not sure if i want to keep this
     * */
    ResourceLocation getSprite();

    /**
     * Returns the items required to apply the specified tier.
     * */
    ItemStack[] getRecipeForTier(int tier);

    /**
     * Returns the maximum tier for this upgrade.
     * */
    int getMaxTier();

    //public void onTick();Todo Work out a sain way to implement this

    /**
     * Returns the maximum level allowed for this upgrade.
     * */
    int getMaxLevel();

    /**
     * Returns the upgrades current level.
     * */
    int getLevel();

    /**
     * Return the total number of upgrades applied to the given tier.
     * if tier == -1 return the total upgrades applied for all tiers
     * */
    int getUpgradeCount(int tier);

    /**
     * Called when the upgrade is applied to an item.
     * */
    void onApplied(ItemStack stack);

    /**
     * Called when the upgrade is removed from an item.
     * */
    void onRemoved(ItemStack stack);

    /**
     * Called to write the upgrades values to NBT.
     * */
    void writeToNBT(NBTTagCompound compound);

    /**
     * Called to read the upgrades values from NBT.
     * */
    void readFromNBT(NBTTagCompound compound);
}
