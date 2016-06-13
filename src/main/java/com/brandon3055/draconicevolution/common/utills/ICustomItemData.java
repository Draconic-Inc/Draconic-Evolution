package com.brandon3055.draconicevolution.common.utills;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Created by Brandon on 13/11/2014.
 */
public interface ICustomItemData {

    public static final String tagName = "TileCompound";

    void writeDataToItem(NBTTagCompound compound, ItemStack stack);

    void readDataFromItem(NBTTagCompound compound, ItemStack stack);
}
