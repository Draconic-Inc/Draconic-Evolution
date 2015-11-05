package com.brandon3055.draconicevolution.integration;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;

/**
 * Created by brandon3055 on 29/9/2015.
 */
public class ModHelper {

	public static boolean isTConInstalled;
	private static Item cleaver;

	public static void init(){
		isTConInstalled = Loader.isModLoaded("TConstruct");

	}

	public static boolean isHoldingCleaver(EntityPlayer player){
		if (!isTConInstalled) return false;
		else if (cleaver == null) cleaver = GameRegistry.findItem("TConstruct", "cleaver");

		return cleaver != null && player.getHeldItem() != null && player.getHeldItem().getItem().equals(cleaver);
	}


}
