package com.brandon3055.draconicevolution.api.event;

import com.brandon3055.draconicevolution.api.capability.ModuleHost;
import com.brandon3055.draconicevolution.api.capability.PropertyProvider;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.Event;

/**
 * Created by brandon3055 on 06/11/2022
 */
public class ModularItemInitEvent extends Event {

    private final ItemStack stack;
    private final ModuleHost host;
    private final PropertyProvider provider;

    public ModularItemInitEvent(ItemStack stack, ModuleHost host, PropertyProvider provider) {
        this.stack = stack;
        this.host = host;
        this.provider = provider;
    }

    public ItemStack getStack() {
        return stack;
    }

    public ModuleHost getHost() {
        return host;
    }

    public PropertyProvider getPropertyProvider() {
        return provider;
    }
}
