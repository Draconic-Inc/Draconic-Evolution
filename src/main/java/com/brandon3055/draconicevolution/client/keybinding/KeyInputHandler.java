package com.brandon3055.draconicevolution.client.keybinding;

import com.brandon3055.brandonscore.handlers.HandHelper;
//import com.brandon3055.draconicevolution.client.gui.toolconfig.GuiToolConfig;
//import com.brandon3055.draconicevolution.network.PacketDispatcher;
import com.brandon3055.draconicevolution.client.gui.modular.itemconfig.GuiConfigurableItem;

import com.brandon3055.draconicevolution.inventory.ContainerConfigurableItem;
import com.brandon3055.draconicevolution.items.equipment.IModularArmor;
import com.brandon3055.draconicevolution.network.DraconicNetwork;
import com.brandon3055.draconicevolution.utils.LogHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.network.NetworkHooks;

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
//        if (change == 0 || !player.isShiftKeyDown()) return;
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
        } else if (KeyBindings.toolConfig.isPressed()) {
            DraconicNetwork.sendOpenItemConfig(false);
        }
//        else if (KeyBindings.hudConfig.isPressed()) {
////            Minecraft.getInstance().displayGuiScreen(new GuiHudConfig());
//
//        }
        else if (KeyBindings.toolModules.isPressed()) {
            DraconicNetwork.sendOpenItemConfig(true);
        }
//        else if (KeyBindings.toolProfileChange.isPressed() && HandHelper.getMainFirst(player) != null) {
////            PacketDispatcher.dispatchToolProfileChange(false);
//        }
        else if (KeyBindings.toggleFlight.isPressed()) {
            if (player.abilities.allowFlying) {
                if (player.abilities.isFlying) {
                    player.abilities.isFlying = false;
                    player.sendPlayerAbilities();
                } else {
                    player.abilities.isFlying = true;
                    if (player.onGround) {
                        player.setPosition(player.posX, player.posY + 0.05D, player.posZ);
                        player.setMotion(player.getMotion().x, 0, player.getMotion().z);
                    }
                    player.sendPlayerAbilities();
                }
            }
        }
//        else if (KeyBindings.toggleDislocator.isPressed()) {
////            PacketDispatcher.dispatchToggleDislocators();
//        }
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
    public void priorityKeyInput(InputEvent.KeyInputEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null && event.getAction() == 1) {
            GuiConfigurableItem.checkKeybinding(event.getKey(), event.getScanCode());
        }
        if (mc.gameSettings.keyBindForward.getKey().getKeyCode() == event.getKey() && mc.player.isElytraFlying() && mc.player.getItemStackFromSlot(EquipmentSlotType.CHEST).getItem() instanceof IModularArmor) {
//            Vec3d look = mc.player.getLookVec();
//            Vec3d motion = mc.player.getMotion();
//            mc.player.setMotion(motion.add(
//                    look.x * 0.1D + (look.x * 1.5D - motion.x) * 1.5D,
//                    look.y * 0.1D + (look.y * 1.5D - motion.y) * 1.5D,
//                    look.z * 0.1D + (look.z * 1.5D - motion.z) * 1.5D
//            ));
        }
    }
}
