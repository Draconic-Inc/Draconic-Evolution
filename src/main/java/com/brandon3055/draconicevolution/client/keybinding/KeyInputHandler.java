package com.brandon3055.draconicevolution.client.keybinding;

import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.common.core.network.PlacedItemPacket;
import com.brandon3055.draconicevolution.common.items.tools.ToolHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.util.MovingObjectPosition;

/**
 * Created by Brandon on 14/08/2014.
 */
public class KeyInputHandler {

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onKeyInput(InputEvent.KeyInputEvent event) {
		if(KeyBindings.placeItem.isPressed()) handlePlaceItemKey();
	}

	private void handlePlaceItemKey(){
		EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
		WorldClient world = Minecraft.getMinecraft().theWorld;
		MovingObjectPosition mop = ToolHandler.raytraceFromEntity(world, player, 4.5D);
		if (mop != null) DraconicEvolution.channelHandler.sendToServer(new PlacedItemPacket((byte) mop.sideHit, mop.blockX, mop.blockY, mop.blockZ));
	}
}
