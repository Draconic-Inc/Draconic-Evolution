package com.brandon3055.draconicevolution.init;

import com.brandon3055.draconicevolution.api.capability.ModuleHost;
import com.brandon3055.draconicevolution.api.capability.ModuleProvider;
import com.brandon3055.draconicevolution.api.capability.PropertyProvider;
import net.covers1624.quack.util.CrashLock;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * Created by brandon3055 and covers1624 on 4/16/20.
 */
public class ModCapabilities {

    private static final CrashLock LOCK = new CrashLock("Already Initialized");

    public static void init() {
        LOCK.lock();

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(ModCapabilities::register);
    }

    public static void register(RegisterCapabilitiesEvent event) {
        event.register(ModuleProvider.class);
        event.register(ModuleHost.class);
        event.register(PropertyProvider.class);
    }
}
