package com.brandon3055.draconicevolution.client.handler;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

/**
 * Created by Brandon on 28/10/2014.
 */
public class ClientEventHandler {

	public static int elapsedTicks;

	@SubscribeEvent
	public void tickEnd(TickEvent event) {
		elapsedTicks++;

		ToolHudHandler.clientTick();
	}
}
