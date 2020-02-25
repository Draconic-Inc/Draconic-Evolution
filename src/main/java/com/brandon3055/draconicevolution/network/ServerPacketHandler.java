package com.brandon3055.draconicevolution.network;

import codechicken.lib.packet.ICustomPacketHandler;
import codechicken.lib.packet.PacketCustom;
import com.brandon3055.brandonscore.handlers.HandHelper;
import com.brandon3055.brandonscore.lib.ChatHelper;
import com.brandon3055.draconicevolution.DEContent;
import com.brandon3055.draconicevolution.api.itemconfig.IConfigurableItem;
import com.brandon3055.draconicevolution.api.itemconfig.ToolConfigHelper;
import com.brandon3055.draconicevolution.items.tools.IAOEWeapon;
import com.brandon3055.draconicevolution.items.tools.Magnet;
import com.brandon3055.draconicevolution.items.tools.MiningToolBase;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.IServerPlayNetHandler;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.ArrayList;
import java.util.List;

public class ServerPacketHandler implements ICustomPacketHandler.IServerPacketHandler {


    @Override
    public void handlePacket(PacketCustom packet, ServerPlayerEntity sender, IServerPlayNetHandler handler) {
        switch (packet.getType()) {
            case 1:
                toggleDislocators(sender);
                break;
            case 2:
                changeToolProfile(sender, packet.readBoolean());
                break;
            case 3:
                cycleToolAOE(sender, packet.readBoolean());
                break;
            case 4:
                cycleAttackAOE(sender, packet.readBoolean());
                break;
        }
    }

    private void toggleDislocators(PlayerEntity player) {
        List<ItemStack> dislocators = new ArrayList<>();

        for (ItemStack stack : player.inventory.mainInventory) {
            if (!stack.isEmpty() && stack.getItem() == DEContent.magnet) {
                dislocators.add(stack);
            }
        }

        for (ItemStack stack : player.inventory.offHandInventory) {
            if (!stack.isEmpty() && stack.getItem() == DEContent.magnet) {
                dislocators.add(stack);
            }
        }

//        IBaublesItemHandler handler = BaublesApi.getBaublesHandler(player);
//        if (handler != null) {
//            for (int i = 0; i < handler.getSlots(); i++) {
//                ItemStack stack = handler.getStackInSlot(i);
//                if (!stack.isEmpty() && stack.getItem() == DEFeatures.magnet) {
//                    dislocators.add(stack);
//                }
//            }
//        }

        for (ItemStack dislocator : dislocators) {
            Magnet.toggleEnabled(dislocator);
            boolean enabled = Magnet.isEnabled(dislocator);
            ChatHelper.indexedTrans(player, "chat.item_dislocator_" + (enabled ? "activate" : "deactivate") + ".msg", -30553055);
        }
    }

    private void changeToolProfile(PlayerEntity player, boolean armor) {
        if (armor) {
            int i = 0;
            NonNullList<ItemStack> armorInventory = player.inventory.armorInventory;
            for (int i1 = armorInventory.size() - 1; i1 >= 0; i1--) {
                ItemStack stack = armorInventory.get(i1);
                if (!stack.isEmpty() && stack.getItem() instanceof IConfigurableItem) {
                    ToolConfigHelper.incrementProfile(stack);
                    int newProfile = ToolConfigHelper.getProfile(stack);
                    String name = ToolConfigHelper.getProfileName(stack, newProfile);
                    ChatHelper.indexedTrans(player, new TranslationTextComponent("config.de.armor_profile_" + i + ".msg").getFormattedText() + " " + name, -30553045 + i);
                }
                i++;
            }
        }
        else {
            ItemStack stack = HandHelper.getMainFirst(player);
            if (!stack.isEmpty() && stack.getItem() instanceof IConfigurableItem) {
                ToolConfigHelper.incrementProfile(stack);
            }
        }
    }

    private void cycleToolAOE(PlayerEntity player, boolean depth) {
        ItemStack stack = player.getHeldItemMainhand();

        if (stack.getItem() instanceof MiningToolBase) {
            MiningToolBase tool = (MiningToolBase) stack.getItem();
            int value = depth ? tool.getDigDepth(stack) : tool.getDigAOE(stack);
            int maxValue = depth ? tool.getMaxDigDepth(stack) : tool.getMaxDigAOE(stack);

            value++;
            if (value > maxValue) {
                value = 0;
            }

            if (depth) {
                tool.setMiningDepth(stack, value);
            }
            else {
                tool.setMiningAOE(stack, value);
            }
        }
    }

    private void cycleAttackAOE(PlayerEntity player, boolean reverse) {
        ItemStack stack = player.getHeldItemMainhand();

        if (stack.getItem() instanceof IAOEWeapon) {
            IAOEWeapon weapon = (IAOEWeapon) stack.getItem();
            double value = weapon.getWeaponAOE(stack);
            double maxValue = weapon.getMaxWeaponAOE(stack);

            if (reverse) {
                value -= 0.5;
                if (value < 0) {
                    value = maxValue;
                }
            }
            else {
                value += 0.5;
                if (value > maxValue) {
                    value = 0;
                }
            }

            weapon.setWeaponAOE(stack, value);
        }

    }
}