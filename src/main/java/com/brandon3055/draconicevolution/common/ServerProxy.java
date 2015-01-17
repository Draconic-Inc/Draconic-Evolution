package com.brandon3055.draconicevolution.common;

import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.server.MinecraftServer;

/**
 * Created by Brandon on 16/01/2015.
 */
public class ServerProxy extends CommonProxy {
	@Override
	public boolean isDedicatedServer() {
		return true;
	}
}
