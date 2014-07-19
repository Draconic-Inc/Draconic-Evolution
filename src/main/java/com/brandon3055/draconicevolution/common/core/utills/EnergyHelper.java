package com.brandon3055.draconicevolution.common.core.utills;

import cofh.api.energy.IEnergyContainerItem;
import net.minecraft.item.ItemStack;

import java.util.List;

/**
 * Created by Brandon on 28/06/2014.
 */
public class EnergyHelper {

	public static boolean isEnergyContainerItem(ItemStack stack)
	{
		return (stack != null) && ((stack.getItem() instanceof IEnergyContainerItem));
	}

}


