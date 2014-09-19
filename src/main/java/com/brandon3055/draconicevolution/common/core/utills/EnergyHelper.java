package com.brandon3055.draconicevolution.common.core.utills;

import cofh.api.energy.IEnergyContainerItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Created by Brandon on 28/06/2014.
 */
public class EnergyHelper {

	public static boolean isEnergyContainerItem(ItemStack stack)
	{
		return (stack != null) && ((stack.getItem() instanceof IEnergyContainerItem));
	}

	public static ItemStack setDefaultEnergyTag(ItemStack container, int energy) {

		if (container.stackTagCompound == null) {
			container.setTagCompound(new NBTTagCompound());
		}
		container.stackTagCompound.setInteger("Energy", energy);

		return container;
	}
}


