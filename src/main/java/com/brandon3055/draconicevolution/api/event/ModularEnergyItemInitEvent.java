package com.brandon3055.draconicevolution.api.event;

import com.brandon3055.brandonscore.api.power.IOPStorage;
import com.brandon3055.draconicevolution.api.capability.ModuleHost;
import com.brandon3055.draconicevolution.api.capability.PropertyProvider;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.Event;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.tools.obfuscation.interfaces.IOptionProvider;

/**
 * Created by brandon3055 on 06/11/2022
 */
public class ModularEnergyItemInitEvent extends Event {

    private final ItemStack stack;
    private final ModuleHost host;
    @Nullable
    private final PropertyProvider provider;
    private final IOPStorage opStorage;

    public ModularEnergyItemInitEvent(ItemStack stack, ModuleHost host, @Nullable PropertyProvider provider, IOPStorage opStorage) {
        this.stack = stack;
        this.host = host;
        this.provider = provider;
        this.opStorage = opStorage;
    }

    public ItemStack getStack() {
        return stack;
    }

    public ModuleHost getHost() {
        return host;
    }

    @Nullable
    public PropertyProvider getPropertyProvider() {
        return provider;
    }

    public IOPStorage getOpStorage() {
        return opStorage;
    }
}
