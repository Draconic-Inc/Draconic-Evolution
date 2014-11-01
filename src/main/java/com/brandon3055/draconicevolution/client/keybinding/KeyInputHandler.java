package com.brandon3055.draconicevolution.client.keybinding;

import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.common.core.network.PlacedItemPacket;
import com.brandon3055.draconicevolution.common.core.network.TeleporterPacket;
import com.brandon3055.draconicevolution.common.items.ModItems;
import com.brandon3055.draconicevolution.common.items.tools.ToolHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import org.lwjgl.input.Mouse;

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
		if (mop != null) DraconicEvolution.network.sendToServer(new PlacedItemPacket((byte) mop.sideHit, mop.blockX, mop.blockY, mop.blockZ));
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onMouseInput(InputEvent.MouseInputEvent event) {
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		int change = Mouse.getEventDWheel();
		if (change == 0 || !player.isSneaking()) return;

		if (change > 0){
			ItemStack item = player.inventory.getStackInSlot(previouseSlot(1, player.inventory.currentItem));
			if (item != null && item.getItem().equals(ModItems.teleporterMKII)){
				player.inventory.currentItem = previouseSlot(1, player.inventory.currentItem);
				DraconicEvolution.network.sendToServer(new TeleporterPacket(TeleporterPacket.SCROLL, -1, false));
			}
		}else if (change < 0){
			ItemStack item = player.inventory.getStackInSlot(previouseSlot(-1, player.inventory.currentItem));
			if (item != null && item.getItem().equals(ModItems.teleporterMKII)){
				player.inventory.currentItem = previouseSlot(-1, player.inventory.currentItem);
				DraconicEvolution.network.sendToServer(new TeleporterPacket(TeleporterPacket.SCROLL, 1, false));
			}
		}
	}

	private int previouseSlot(int i, int c){
		if (c > 0 && c < 8) return c+i;
		if (c == 0 && i < 0) return 8;
		if (c == 8 && i > 0) return 0;
		return c+i;
	}
}
