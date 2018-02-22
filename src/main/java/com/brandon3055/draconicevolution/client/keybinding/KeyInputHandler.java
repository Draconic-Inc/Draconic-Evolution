package com.brandon3055.draconicevolution.client.keybinding;

import com.brandon3055.brandonscore.handlers.HandHelper;
import com.brandon3055.draconicevolution.DEFeatures;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.client.gui.toolconfig.GuiToolConfig;
import com.brandon3055.draconicevolution.network.PacketDislocator;
import com.brandon3055.draconicevolution.network.PacketPlaceItem;
import com.brandon3055.draconicevolution.network.PacketSimpleBoolean;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.client.event.MouseEvent;
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
        EntityPlayer player = Minecraft.getMinecraft().player;
        if (player == null) {
            return;
        }

        if (KeyBindings.placeItem.isPressed()) {
            handlePlaceItemKey();
        }
        else if (KeyBindings.toolConfig.isPressed()) {
            Minecraft.getMinecraft().displayGuiScreen(new GuiToolConfig(player));
        }
        else if (KeyBindings.toolProfileChange.isPressed() && HandHelper.getMainFirst(player) != null) {
            DraconicEvolution.network.sendToServer(new PacketSimpleBoolean(PacketSimpleBoolean.ID_TOOL_PROFILE_CHANGE, false));
        }
        else if (KeyBindings.toggleFlight.isPressed()) {
            if (player.capabilities.allowFlying) {
                if (player.capabilities.isFlying) {
                    player.capabilities.isFlying = false;
                    player.sendPlayerAbilities();
                }
                else {
                    player.capabilities.isFlying = true;
                    if (player.onGround) {
                        player.setPosition(player.posX, player.posY + 0.05D, player.posZ);
                        player.motionY = 0;
                    }
                    player.sendPlayerAbilities();
                }
            }
        }
    }


    private void handlePlaceItemKey() {
        RayTraceResult mop = Minecraft.getMinecraft().objectMouseOver;
        if (mop != null && mop.typeOfHit == RayTraceResult.Type.BLOCK) {
            DraconicEvolution.network.sendToServer(new PacketPlaceItem());
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onMouseInput(MouseEvent event) {
        EntityPlayer player = Minecraft.getMinecraft().player;
        if (player == null) {
            return;
        }

        if (KeyBindings.placeItem.isPressed()) {
            handlePlaceItemKey();
        }
        else if (KeyBindings.toolConfig.isPressed()) {
            Minecraft.getMinecraft().displayGuiScreen(new GuiToolConfig(player));
            //DraconicEvolution.network.sendToServer(new PacketSimpleBoolean(PacketSimpleBoolean.ID_TOOL_CONFIG, true));
        }
        else if (KeyBindings.toolProfileChange.isPressed() && player != null && HandHelper.getMainFirst(player) != null) {
            DraconicEvolution.network.sendToServer(new PacketSimpleBoolean(PacketSimpleBoolean.ID_TOOL_PROFILE_CHANGE, false));

            ItemStack stack = HandHelper.getMainFirst(player);
//			if (stack != null && stack.getItem() instanceof IConfigurableItem && ((IConfigurableItem)stack.getItem()).hasProfiles()){
//				int preset = ItemNBTHelper.getInteger(stack, "ConfigProfile", 0);
//				if (++preset >= 5) preset = 0;
//				ItemNBTHelper.setInteger(stack, "ConfigProfile", preset);
//			}
        }

        int change = event.getDwheel();
        if (change == 0 || !player.isSneaking()) return;

        ItemStack item = player.inventory.getStackInSlot(player.inventory.currentItem);
        if (item.getItem() == DEFeatures.dislocatorAdvanced) {
            if (event.isCancelable())
                event.setCanceled(true);
            DraconicEvolution.network.sendToServer(new PacketDislocator(PacketDislocator.SCROLL, change < 0 ? -1 : 1,
                    false));
        }
    }
}
