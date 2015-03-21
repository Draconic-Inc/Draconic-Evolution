package com.brandon3055.draconicevolution.common.utills;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.registry.GameData;
import net.minecraft.item.ItemStack;

/**
 * Created by Brandon on 25/07/2014.
 */
public class Utills {

	public static String formatNumber(double value){
		if (value < 1000D)
			return String.valueOf(value);
		else if (value < 1000000D)
			return String.valueOf(Math.round(value)/1000D) + "K";
		else if (value < 1000000000D)
			return String.valueOf(Math.round(value/1000D)/1000D) + "M";
		else if (value < 1000000000000D)
			return String.valueOf(Math.round(value/1000000D)/1000D) + "B";
		else
			return String.valueOf(Math.round(value/1000000000D)/1000D) + "T";
	}

	/**
	 * Calculates the exact distance between two points in 3D space
	 * @param x1 point A x
	 * @param y1 point A y
	 * @param z1 point A z
	 * @param x2 point B x
	 * @param y2 point B y
	 * @param z2 point B z
	 * @return The distance between point A and point B
	 */
	public static double getDistanceAtoB(double x1, double y1, double z1, double x2, double y2, double z2){
		double dx = x1-x2;
		double dy = y1-y2;
		double dz = z1-z2;
		return Math.sqrt((dx*dx + dy*dy + dz*dz ));
	}

	/**
	 * Calculates the exact distance between two points in 2D space
	 * @param x1 point A x
	 * @param z1 point A z
	 * @param x2 point B x
	 * @param z2 point B z
	 * @return The distance between point A and point B
	 */
	public static double getDistanceAtoB(double x1, double z1, double x2, double z2){
		double dx = x1-x2;
		double dz = z1-z2;
		return Math.sqrt((dx*dx + dz*dz ));
	}

	public static final boolean isSinglePlayerServer()
	{
		return FMLCommonHandler.instance().getMinecraftServerInstance() != null;
	}

	public static final boolean isMultiPlayerServer()
	{
		return FMLCommonHandler.instance().getMinecraftServerInstance() == null;
	}

	public static ItemStack getStackFromName(String name, int meta)
	{
		if (name.contains("tile."))
		{
			name = name.replace("draconicevolution", "DraconicEvolution").replace("tile.", "");
			if (GameData.getBlockRegistry().getObject(name) != null) return new ItemStack(GameData.getBlockRegistry().getObject(name), 1, meta);
		}
		if (name.contains("item."))
		{
			name = name.replace("draconicevolution", "DraconicEvolution").replace("item.", "");
			if (GameData.getItemRegistry().getObject(name) != null) return new ItemStack(GameData.getItemRegistry().getObject(name), 1, meta);
		}
		return null;
	}
}
