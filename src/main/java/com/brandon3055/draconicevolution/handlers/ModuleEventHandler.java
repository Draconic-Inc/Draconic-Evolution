package com.brandon3055.draconicevolution.handlers;

import com.brandon3055.draconicevolution.api.capability.DECapabilities;
import com.brandon3055.draconicevolution.api.capability.ModuleHost;
import com.brandon3055.draconicevolution.api.modules.lib.EntityOverridesItemUse;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleEntity;
import net.covers1624.quack.util.CrashLock;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

/**
 * Created by brandon3055 on 27/01/2023
 */
public class ModuleEventHandler {

    private static final CrashLock LOCK = new CrashLock("Already Initialized");

    public static void init() {
        LOCK.lock();
        NeoForge.EVENT_BUS.addListener(ModuleEventHandler::onPlayerInteractItem);
        NeoForge.EVENT_BUS.addListener(ModuleEventHandler::onPlayerInteractBlock);
        NeoForge.EVENT_BUS.addListener(ModuleEventHandler::onLivingUseItem);
    }

    private static void onPlayerInteractItem(PlayerInteractEvent.RightClickItem event) {
        ItemStack stack = event.getItemStack();
        if (stack.isEmpty()) return;
        ModuleHost host = stack.getCapability(DECapabilities.Host.ITEM);
        if (host != null) {
            for (ModuleEntity<?> entity : host.getModuleEntities()) {
                if (entity instanceof EntityOverridesItemUse override) {
                    override.onPlayerInteractEvent(event);
                    return;
                }
            }
        }
    }

    private static void onPlayerInteractBlock(PlayerInteractEvent.RightClickBlock event) {
        ItemStack stack = event.getItemStack();
        if (stack.isEmpty()) return;
        ModuleHost host = stack.getCapability(DECapabilities.Host.ITEM);
        if (host != null) {
            for (ModuleEntity<?> entity : host.getModuleEntities()) {
                if (entity instanceof EntityOverridesItemUse override) {
                    override.onPlayerInteractEvent(event);
                    return;
                }
            }
        }
    }

    private static void onLivingUseItem(LivingEntityUseItemEvent event) {
        ItemStack stack = event.getItem();
        if (stack.isEmpty()) return;
        ModuleHost host = stack.getCapability(DECapabilities.Host.ITEM);
        if (host != null) {
            for (ModuleEntity<?> entity : host.getModuleEntities()) {
                if (entity instanceof EntityOverridesItemUse override) {
                    override.onEntityUseItem(event);
                    return;
                }
            }
        }
    }
}
