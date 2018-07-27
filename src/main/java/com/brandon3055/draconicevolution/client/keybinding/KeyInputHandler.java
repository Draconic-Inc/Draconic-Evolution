package com.brandon3055.draconicevolution.client.keybinding;

import com.brandon3055.brandonscore.handlers.HandHelper;
import com.brandon3055.draconicevolution.DEFeatures;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.client.gui.toolconfig.GuiToolConfig;
import com.brandon3055.draconicevolution.network.PacketDislocator;
import com.brandon3055.draconicevolution.network.PacketPlaceItem;
import com.brandon3055.draconicevolution.network.ccnetwork.PacketDispatcher;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Mouse;

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

        onInput(player);
    }


    private void handlePlaceItemKey() {
        RayTraceResult mop = Minecraft.getMinecraft().objectMouseOver;
        if (mop != null && mop.typeOfHit == RayTraceResult.Type.BLOCK) {
            DraconicEvolution.network.sendToServer(new PacketPlaceItem());
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onMouseInput(InputEvent.MouseInputEvent event) {
        EntityPlayer player = Minecraft.getMinecraft().player;
        if (player == null) {
            return;
        }

        onInput(player);

        int change = Mouse.getEventDWheel();
        if (change == 0 || !player.isSneaking()) return;

        if (change > 0) {
            ItemStack item = player.inventory.getStackInSlot(previouseSlot(1, player.inventory.currentItem));
            if (!item.isEmpty() && item.getItem() == DEFeatures.dislocatorAdvanced) {
                player.inventory.currentItem = previouseSlot(1, player.inventory.currentItem);
                DraconicEvolution.network.sendToServer(new PacketDislocator(PacketDislocator.SCROLL, -1, false));
            }
        }
        else if (change < 0) {
            ItemStack item = player.inventory.getStackInSlot(previouseSlot(-1, player.inventory.currentItem));
            if (!item.isEmpty() && item.getItem() == DEFeatures.dislocatorAdvanced) {
                player.inventory.currentItem = previouseSlot(-1, player.inventory.currentItem);
                DraconicEvolution.network.sendToServer(new PacketDislocator(PacketDislocator.SCROLL, 1, false));
            }
        }
    }

    private void onInput(EntityPlayer player) {
        if (KeyBindings.placeItem.isPressed()) {
            handlePlaceItemKey();
        }
        else if (KeyBindings.toolConfig.isPressed()) {
            Minecraft.getMinecraft().displayGuiScreen(new GuiToolConfig(player));
        }
        else if (KeyBindings.toolProfileChange.isPressed() && HandHelper.getMainFirst(player) != null) {
            PacketDispatcher.dispatchToolProfileChange(false);
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
        else if (KeyBindings.toggleDislocator.isPressed()) {
            PacketDispatcher.dispatchToggleDislocators();
        }
        else if (KeyBindings.armorProfileChange.isPressed()) {
            PacketDispatcher.dispatchToolProfileChange(true);
        }
    }

    private int previouseSlot(int i, int c) {
        if (c > 0 && c < 8) return c + i;
        if (c == 0 && i < 0) return 8;
        if (c == 8 && i > 0) return 0;
        return c + i;
    }
}
