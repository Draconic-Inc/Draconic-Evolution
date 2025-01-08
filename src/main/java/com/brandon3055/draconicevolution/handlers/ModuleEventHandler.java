package com.brandon3055.draconicevolution.handlers;

import com.brandon3055.draconicevolution.api.capability.DECapabilities;
import com.brandon3055.draconicevolution.api.modules.lib.EntityOverridesItemUse;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleEntity;
import com.brandon3055.draconicevolution.init.ModuleCfg;
import net.covers1624.quack.util.CrashLock;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;

/**
 * Created by brandon3055 on 27/01/2023
 */
public class ModuleEventHandler {

    private static final CrashLock LOCK = new CrashLock("Already Initialized");

    public static void init(IEventBus modBus) {
        LOCK.lock();
        MinecraftForge.EVENT_BUS.addListener(ModuleEventHandler::onPlayerInteract);
        MinecraftForge.EVENT_BUS.addListener(ModuleEventHandler::onLivingUseItem);
        modBus.addListener((FMLLoadCompleteEvent e) -> ModuleCfg.saveStateConfig());
    }

    private static void onPlayerInteract(PlayerInteractEvent event) {
        ItemStack stack = event.getItemStack();
        if (stack.isEmpty()) return;
        stack.getCapability(DECapabilities.MODULE_HOST_CAPABILITY).ifPresent(host -> {
            for (ModuleEntity<?> entity : host.getModuleEntities()) {
                if (entity instanceof EntityOverridesItemUse override) {
                    override.onPlayerInteractEvent(event);
                    return;
                }
            }
        });
    }

    private static void onLivingUseItem(LivingEntityUseItemEvent event) {
        ItemStack stack = event.getItem();
        if (stack.isEmpty()) return;
        stack.getCapability(DECapabilities.MODULE_HOST_CAPABILITY).ifPresent(host -> {
            for (ModuleEntity<?> entity : host.getModuleEntities()) {
                if (entity instanceof EntityOverridesItemUse override) {
                    override.onEntityUseItem(event);
                    return;
                }
            }
        });
    }
}
