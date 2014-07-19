package com.brandon3055.draconicevolution.common.core.utills;

import cofh.api.energy.IEnergyContainerItem;
import net.minecraft.item.ItemStack;

import java.util.List;

/**
 * Created by Brandon on 1/07/2014.
 */
public class ItemInfoHelper {
	public static void energyDisplayInfo(ItemStack stack, List list) {
		IEnergyContainerItem item = (IEnergyContainerItem)stack.getItem();
		int energy = item.getEnergyStored(stack);
		int maxEnergy = item.getMaxEnergyStored(stack);
		String eS = "";
		String eM = "";
		if (energy < 1000)
			eS = String.valueOf(energy);
		else if (energy < 1000000)
			eS = String.valueOf(Math.round((float)energy / 100F)/10F)+"k";
		else
			eS = String.valueOf(Math.round((float)energy / 10000F)/100F)+"m";
		if (maxEnergy < 1000)
			eM = String.valueOf(maxEnergy);
		else if (maxEnergy < 1000000)
			eM = String.valueOf(Math.round((float)maxEnergy / 100F)/10F)+"k";
		else
			eM = String.valueOf(Math.round((float)maxEnergy / 10000F)/100F)+"m";

		list.add(eS + " / " + eM + " RF");
	}
}
