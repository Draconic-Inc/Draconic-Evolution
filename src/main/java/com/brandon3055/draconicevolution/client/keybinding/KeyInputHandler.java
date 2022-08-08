package com.brandon3055.draconicevolution.client.keybinding;

import com.brandon3055.brandonscore.common.utills.ItemNBTHelper;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.common.ModItems;
import com.brandon3055.draconicevolution.common.items.tools.baseclasses.ToolHandler;
import com.brandon3055.draconicevolution.common.network.ButtonPacket;
import com.brandon3055.draconicevolution.common.network.MagnetTogglePacket;
import com.brandon3055.draconicevolution.common.network.PlacedItemPacket;
import com.brandon3055.draconicevolution.common.network.TeleporterPacket;
import com.brandon3055.draconicevolution.common.utills.IConfigurableItem;
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
        if (KeyBindings.placeItem.isPressed()) handlePlaceItemKey();
        else if (KeyBindings.toolConfig.isPressed()) {
            DraconicEvolution.network.sendToServer(new ButtonPacket(ButtonPacket.ID_TOOLCONFIG, false));
        } else if (KeyBindings.toolProfileChange.isPressed()
                && Minecraft.getMinecraft().thePlayer != null
                && Minecraft.getMinecraft().thePlayer.getItemInUse() == null) {
            DraconicEvolution.network.sendToServer(new ButtonPacket(ButtonPacket.ID_TOOL_PROFILE_CHANGE, false));

            ItemStack stack = Minecraft.getMinecraft().thePlayer.getHeldItem();
            if (stack != null
                    && stack.getItem() instanceof IConfigurableItem
                    && ((IConfigurableItem) stack.getItem()).hasProfiles()) {
                int preset = ItemNBTHelper.getInteger(stack, "ConfigProfile", 0);
                if (++preset >= 5) preset = 0;
                ItemNBTHelper.setInteger(stack, "ConfigProfile", preset);
            }
        } else if (KeyBindings.toggleFlight.isPressed()) {
            EntityPlayer player = Minecraft.getMinecraft().thePlayer;
            if (player.capabilities.allowFlying) {
                if (player.capabilities.isFlying) {
                    player.capabilities.isFlying = false;
                    player.sendPlayerAbilities();
                } else {
                    player.capabilities.isFlying = true;
                    if (player.onGround) {
                        player.setPosition(player.posX, player.posY + 0.05D, player.posZ);
                        player.motionY = 0;
                    }
                    player.sendPlayerAbilities();
                }
            }
        } else if (KeyBindings.toggleMagnet.isPressed()) {
            EntityPlayer player = Minecraft.getMinecraft().thePlayer;

            if (player.inventory.hasItem(ModItems.magnet)) {
                DraconicEvolution.network.sendToServer(new MagnetTogglePacket());
            }
        }
    }

    private void handlePlaceItemKey() {
        EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
        WorldClient world = Minecraft.getMinecraft().theWorld;
        MovingObjectPosition mop = ToolHandler.raytraceFromEntity(world, player, 4.5D);
        if (mop != null)
            DraconicEvolution.network.sendToServer(
                    new PlacedItemPacket((byte) mop.sideHit, mop.blockX, mop.blockY, mop.blockZ));
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onMouseInput(InputEvent.MouseInputEvent event) {
        if (KeyBindings.placeItem.isPressed()) handlePlaceItemKey();
        else if (KeyBindings.toolConfig.isPressed()) {
            DraconicEvolution.network.sendToServer(new ButtonPacket(ButtonPacket.ID_TOOLCONFIG, false));
        } else if (KeyBindings.toolProfileChange.isPressed() && Minecraft.getMinecraft().thePlayer != null) {
            DraconicEvolution.network.sendToServer(new ButtonPacket(ButtonPacket.ID_TOOL_PROFILE_CHANGE, false));

            ItemStack stack = Minecraft.getMinecraft().thePlayer.getHeldItem();
            if (stack != null
                    && stack.getItem() instanceof IConfigurableItem
                    && ((IConfigurableItem) stack.getItem()).hasProfiles()) {
                int preset = ItemNBTHelper.getInteger(stack, "ConfigProfile", 0);
                if (++preset >= 5) preset = 0;
                ItemNBTHelper.setInteger(stack, "ConfigProfile", preset);
            }
        }

        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        int change = Mouse.getEventDWheel();
        if (change == 0 || !player.isSneaking()) return;

        if (change > 0) {
            ItemStack item = player.inventory.getStackInSlot(previouseSlot(1, player.inventory.currentItem));
            if (item != null && item.getItem().equals(ModItems.teleporterMKII)) {
                player.inventory.currentItem = previouseSlot(1, player.inventory.currentItem);
                DraconicEvolution.network.sendToServer(new TeleporterPacket(TeleporterPacket.SCROLL, -1, false));
            }
        } else if (change < 0) {
            ItemStack item = player.inventory.getStackInSlot(previouseSlot(-1, player.inventory.currentItem));
            if (item != null && item.getItem().equals(ModItems.teleporterMKII)) {
                player.inventory.currentItem = previouseSlot(-1, player.inventory.currentItem);
                DraconicEvolution.network.sendToServer(new TeleporterPacket(TeleporterPacket.SCROLL, 1, false));
            }
        }
    }

    private int previouseSlot(int i, int c) {
        if (c > 0 && c < 8) return c + i;
        if (c == 0 && i < 0) return 8;
        if (c == 8 && i > 0) return 0;
        if (c == 150 && i < 0) return 152;
        return c + i;
    }
}
