package com.brandon3055.draconicevolution.client.keybinding;

import com.brandon3055.brandonscore.handlers.HandHelper;
//import com.brandon3055.draconicevolution.client.gui.toolconfig.GuiToolConfig;
//import com.brandon3055.draconicevolution.network.PacketDispatcher;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.awt.event.MouseEvent;

/**
 * Created by Brandon on 14/08/2014.
 */
public class KeyInputHandler {

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        PlayerEntity player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }

        onInput(player);
    }


    private void handlePlaceItemKey() {
        RayTraceResult mop = Minecraft.getInstance().objectMouseOver;
        if (mop != null && mop instanceof BlockRayTraceResult) {
            //TODO Packet stuff
//            DraconicEvolution.network.sendToServer(new PacketPlaceItem());
        }
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onMouseInput(InputEvent.MouseInputEvent event) {
        PlayerEntity player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }

        onInput(player);

//        int change = event.getDwheel();
//        if (change == 0 || !player.isSneaking()) return;
//
//        ItemStack item = player.inventory.getStackInSlot(player.inventory.currentItem);
//        if (item.getItem() == DEFeatures.dislocatorAdvanced) {
//            event.setCanceled(true);
//            DraconicEvolution.network.sendToServer(new PacketDislocator(PacketDislocator.SCROLL, change < 0 ? -1 : 1, false));
//        }
    }

    private void onInput(PlayerEntity player) {
        if (KeyBindings.placeItem.isPressed()) {
            handlePlaceItemKey();
        }
        else if (KeyBindings.toolConfig.isPressed()) {
//            Minecraft.getInstance().displayGuiScreen(new GuiToolConfig(player)); TODO PacketDispatcher stuff
        }
        else if (KeyBindings.toolProfileChange.isPressed() && HandHelper.getMainFirst(player) != null) {
//            PacketDispatcher.dispatchToolProfileChange(false);
        }
        else if (KeyBindings.toggleFlight.isPressed()) {
            if (player.abilities.allowFlying) {
                if (player.abilities.isFlying) {
                    player.abilities.isFlying = false;
                    player.sendPlayerAbilities();
                }
                else {
                    player.abilities.isFlying = true;
                    if (player.onGround) {
                        player.setPosition(player.posX, player.posY + 0.05D, player.posZ);
                        player.setMotion(player.getMotion().x, 0, player.getMotion().z);
                    }
                    player.sendPlayerAbilities();
                }
            }
        }
        else if (KeyBindings.toggleDislocator.isPressed()) {
//            PacketDispatcher.dispatchToggleDislocators();
        }
        else if (KeyBindings.armorProfileChange.isPressed()) {
//            PacketDispatcher.dispatchToolProfileChange(true);
        }
        else if (KeyBindings.cycleDigAOE.isPressed()) {
//            PacketDispatcher.dispatchCycleDigAOE(player.isSneaking());
        }
        else if (KeyBindings.cycleAttackAOE.isPressed()) {
//            PacketDispatcher.dispatchCycleAttackAOE(player.isSneaking());
        }
    }

    private int previouseSlot(int i, int c) {
        if (c > 0 && c < 8) return c + i;
        if (c == 0 && i < 0) return 8;
        if (c == 8 && i > 0) return 0;
        return c + i;
    }
}
