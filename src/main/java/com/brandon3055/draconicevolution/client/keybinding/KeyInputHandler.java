package com.brandon3055.draconicevolution.client.keybinding;

import com.brandon3055.brandonscore.handlers.HandHelper;
import com.brandon3055.draconicevolution.client.gui.toolconfig.GuiToolConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by Brandon on 14/08/2014.
 */
public class KeyInputHandler {

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onKeyInput(InputEvent.KeyInputEvent event) {
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		if(KeyBindings.placeItem.isPressed()) {
            handlePlaceItemKey();
        }
		else if(KeyBindings.toolConfig.isPressed()) {
			Minecraft.getMinecraft().displayGuiScreen(new GuiToolConfig(player));
			//DraconicEvolution.network.sendToServer(new PacketSimpleBoolean(PacketSimpleBoolean.ID_TOOL_CONFIG, true));
		}
		else if (KeyBindings.toolProfileChange.isPressed() && player != null && HandHelper.getMainFirst(player) == null){
//			DraconicEvolution.network.sendToServer(new ButtonPacket(ButtonPacket.ID_TOOL_PROFILE_CHANGE, false));

			ItemStack stack = HandHelper.getMainFirst(player);
//			if (stack != null && stack.getItem() instanceof IConfigurableItem && ((IConfigurableItem)stack.getItem()).hasProfiles()){
//				int preset = ItemNBTHelper.getInteger(stack, "ConfigProfile", 0);
//				if (++preset >= 5) preset = 0;
//				ItemNBTHelper.setInteger(stack, "ConfigProfile", preset);
//			}
		}
	}


	private void handlePlaceItemKey(){
//		EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
//		WorldClient world = Minecraft.getMinecraft().theWorld;
//		RayTraceResult mop = ToolHandler.raytraceFromEntity(world, player, 4.5D);
//		if (mop != null) DraconicEvolution.network.sendToServer(new PlacedItemPacket((byte) mop.sideHit, mop.blockX, mop.blockY, mop.blockZ));
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onMouseInput(InputEvent.MouseInputEvent event) {
//		if(KeyBindings.placeItem.isPressed()) handlePlaceItemKey();
//		else if(KeyBindings.toolConfig.isPressed()) {
//			DraconicEvolution.network.sendToServer(new ButtonPacket(ButtonPacket.ID_TOOLCONFIG, false));
//		}
//		else if (KeyBindings.toolProfileChange.isPressed() && Minecraft.getMinecraft().thePlayer != null){
//			DraconicEvolution.network.sendToServer(new ButtonPacket(ButtonPacket.ID_TOOL_PROFILE_CHANGE, false));
//
//			ItemStack stack = Minecraft.getMinecraft().thePlayer.getHeldItem();
//			if (stack != null && stack.getItem() instanceof IConfigurableItem && ((IConfigurableItem)stack.getItem()).hasProfiles()){
//				int preset = ItemNBTHelper.getInteger(stack, "ConfigProfile", 0);
//				if (++preset >= 5) preset = 0;
//				ItemNBTHelper.setInteger(stack, "ConfigProfile", preset);
//			}
//		}
//
//		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
//		int change = Mouse.getEventDWheel();
//		if (change == 0 || !player.isSneaking()) return;
//
//		if (change > 0){
//			ItemStack item = player.inventory.getStackInSlot(previouseSlot(1, player.inventory.currentItem));
//			if (item != null && item.getItem().equals(ModItems.teleporterMKII)){
//				player.inventory.currentItem = previouseSlot(1, player.inventory.currentItem);
//				DraconicEvolution.network.sendToServer(new TeleporterPacket(TeleporterPacket.SCROLL, -1, false));
//			}
//		}else if (change < 0){
//			ItemStack item = player.inventory.getStackInSlot(previouseSlot(-1, player.inventory.currentItem));
//			if (item != null && item.getItem().equals(ModItems.teleporterMKII)){
//				player.inventory.currentItem = previouseSlot(-1, player.inventory.currentItem);
//				DraconicEvolution.network.sendToServer(new TeleporterPacket(TeleporterPacket.SCROLL, 1, false));
//			}
//		}
	}

	private int previouseSlot(int i, int c){
		if (c > 0 && c < 8) return c+i;
		if (c == 0 && i < 0) return 8;
		if (c == 8 && i > 0) return 0;
		return c+i;
	}
}
