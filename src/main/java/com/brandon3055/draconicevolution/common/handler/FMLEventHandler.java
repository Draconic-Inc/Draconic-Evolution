package com.brandon3055.draconicevolution.common.handler;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;

import com.brandon3055.draconicevolution.common.ModItems;
import com.brandon3055.draconicevolution.common.items.armor.CustomArmorHandler;
import com.brandon3055.draconicevolution.common.lib.References;
import com.brandon3055.draconicevolution.common.utills.LogHelper;

import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

public class FMLEventHandler {

    private static boolean mmGiven = false;

    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent eventArgs) {
        if (eventArgs.modID.equalsIgnoreCase(References.MODID)) {
            ConfigHandler.syncConfig();
            LogHelper.info("Config Changed");
        }
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;

        CustomArmorHandler.onPlayerTick(event);
    }

    @SubscribeEvent
    public void serverTickEvent(TickEvent event) {
        ContributorHandler.tick();
    }

    @SubscribeEvent
    public void playerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!event.player.onGround) {
            CustomArmorHandler.ArmorSummery summery = new CustomArmorHandler.ArmorSummery().getSummery(event.player);
            if (summery != null && summery.flight[0]) {
                event.player.capabilities.isFlying = true;
                event.player.sendPlayerAbilities();
            }
        }

        if (!mmGiven && event.player.getCommandSenderName().toLowerCase().equals("dezil_nz")) {
            mmGiven = true;
            event.player.addChatComponentMessage(new ChatComponentText("Hello Dez! Here have a Marshmallow"));
            event.player.worldObj.spawnEntityInWorld(
                    new EntityItem(
                            event.player.worldObj,
                            event.player.posX,
                            event.player.posY,
                            event.player.posZ,
                            new ItemStack(ModItems.dezilsMarshmallow)));
        }

        ContributorHandler.onPlayerLogin(event);
    }
}
