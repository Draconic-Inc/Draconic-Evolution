package com.brandon3055.draconicevolution.client.keybinding;

import com.brandon3055.draconicevolution.client.gui.DislocatorGui;
import com.brandon3055.draconicevolution.client.gui.modular.itemconfig.ConfigurableItemGui;
import com.brandon3055.draconicevolution.items.tools.DislocatorAdvanced;
import com.brandon3055.draconicevolution.network.DraconicNetwork;
import com.brandon3055.draconicevolution.network.InputSync;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.InputEvent;

/**
 * Created by Brandon on 14/08/2014.
 */
public class KeyInputHandler {

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onKeyInput(InputEvent.Key event) {
        Player player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }

        onInput(player);
    }
    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onMouseInput(InputEvent.MouseButton.Pre event) {
        Player player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }

        onInput(player);

//        int change = event.getDwheel();
//        if (change == 0 || !player.isShiftKeyDown()) return;
//
//        ItemStack item = player.inventory.getStackInSlot(player.inventory.currentItem);
//        if (item.getItem() == DEFeatures.dislocatorAdvanced) {
//            event.setCanceled(true);
//            DraconicEvolution.network.sendToServer(new PacketDislocator(PacketDislocator.SCROLL, change < 0 ? -1 : 1, false));
//        }
    }

    private void onInput(Player player) {


//        else if (KeyBindings.hudConfig.isPressed()) {
//            Minecraft.getInstance().displayGuiScreen(new GuiHudConfig());

//        }
        if (KeyBindings.toolModules.isDown()) {
            DraconicNetwork.sendOpenItemConfig(true);
        }
        else if (KeyBindings.toolConfig.isDown()) {
            DraconicNetwork.sendOpenItemConfig(false);
        }
//        else if (KeyBindings.toolProfileChange.isPressed() && HandHelper.getMainFirst(player) != null) {
////            PacketDispatcher.dispatchToolProfileChange(false);
//        }
        else if (KeyBindings.toggleFlight.isDown()) {
            if (player.getAbilities().mayfly) {
                if (player.getAbilities().flying) {
                    player.getAbilities().flying = false;
                    player.onUpdateAbilities();
                } else {
                    player.getAbilities().flying = true;
                    if (player.onGround()) {
                        player.setPos(player.getX(), player.getY() + 0.05D, player.getZ());
                        player.setDeltaMovement(player.getDeltaMovement().x, 0, player.getDeltaMovement().z);
                    }
                    player.onUpdateAbilities();
                }
            }
        } else if (KeyBindings.toggleMagnet.isDown()) {
            DraconicNetwork.sendToggleMagnets();
        } else if (KeyBindings.dislocatorTeleport.isDown()) {
            DraconicNetwork.sendDislocatorMessage(11, output -> {});
        } else if (KeyBindings.dislocatorBlink.isDown()) {
            DraconicNetwork.sendDislocatorMessage(12, output -> {});
        } else if (KeyBindings.dislocatorUp.isDown()) {
            DraconicNetwork.sendDislocatorMessage(13, output -> output.writeBoolean(false));
        } else if (KeyBindings.dislocatorDown.isDown()) {
            DraconicNetwork.sendDislocatorMessage(13, output -> output.writeBoolean(true));
        } else if (KeyBindings.dislocatorGui.isDown()) {
            ItemStack stack = DislocatorAdvanced.findDislocator(player);
            if (!stack.isEmpty()) {
                Minecraft.getInstance().setScreen(new DislocatorGui.Screen(stack.getHoverName(), player));
            }
        } else if (KeyBindings.placeItem.isDown()) {
            DraconicNetwork.sendPlaceItem();
        }

        Options options = Minecraft.getInstance().options;
        boolean oldState = InputSync.getSprintState(player.getUUID());
        boolean newState = options.keyUp.isDown() && options.keySprint.isDown();
        if (oldState != newState) {
            InputSync.setSprintState(player.getUUID(), newState);
            DraconicNetwork.sendSprintState(newState);
        }

//        else if (KeyBindings.armorProfileChange.isPressed()) {
////            PacketDispatcher.dispatchToolProfileChange(true);
//        }
//        else if (KeyBindings.cycleDigAOE.isPressed()) {
////            PacketDispatcher.dispatchCycleDigAOE(player.isShiftKeyDown());
//        }
//        else if (KeyBindings.cycleAttackAOE.isPressed()) {
////            PacketDispatcher.dispatchCycleAttackAOE(player.isShiftKeyDown());
//        }
    }

    private int previouseSlot(int i, int c) {
        if (c > 0 && c < 8) return c + i;
        if (c == 0 && i < 0) return 8;
        if (c == 8 && i > 0) return 0;
        return c + i;
    }


    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void priorityKeyInput(InputEvent.Key event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null && event.getAction() == 1) {
            ConfigurableItemGui.checkKeybinding(event.getKey(), event.getScanCode());
        }
    }
}
