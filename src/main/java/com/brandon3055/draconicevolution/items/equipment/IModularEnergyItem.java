package com.brandon3055.draconicevolution.items.equipment;

import com.brandon3055.draconicevolution.api.capability.DECapabilities;
import com.brandon3055.draconicevolution.api.capability.ModuleHost;
import com.brandon3055.draconicevolution.api.capability.PropertyProvider;
import com.brandon3055.draconicevolution.api.event.ModularEnergyItemInitEvent;
import com.brandon3055.draconicevolution.api.modules.lib.ModularOPStorage;
import com.brandon3055.draconicevolution.init.CapabilityData;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.NeoForge;

/**
 * Created by brandon3055 on 30/10/2024
 */
public interface IModularEnergyItem extends IModularItem {

    default ModularOPStorage createOPCapForRegistration(ItemStack stack) {
        ModuleHost host = stack.getCapability(DECapabilities.Host.ITEM);
        ModularOPStorage storage = instantiateOPStorage(stack, () -> stack.getCapability(DECapabilities.Host.ITEM));
        NeoForge.EVENT_BUS.post(new ModularEnergyItemInitEvent(stack, host, host instanceof PropertyProvider provider ? provider : null, storage));
        return storage;
    }
}
