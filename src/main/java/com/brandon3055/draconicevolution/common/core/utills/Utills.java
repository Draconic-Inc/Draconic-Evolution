package com.brandon3055.draconicevolution.common.core.utills;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * Created by Brandon on 25/07/2014.
 */
public class Utills {

	public static String formatNumber(double value){
		if (value < 1000D)
			return String.valueOf(value);
		else if (value < 1000000D)
			return String.valueOf(Math.round(value/10D)/100D) + "K";
		else if (value < 1000000000D)
			return String.valueOf(Math.round(value/10000D)/100D) + "M";
		else if (value < 1000000000000D)
			return String.valueOf(Math.round(value/10000000D)/100D) + "B";
		else
			return String.valueOf(Math.round(value/10000000000D)/100D) + "T";
	}

}
