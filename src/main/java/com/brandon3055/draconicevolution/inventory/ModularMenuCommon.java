package com.brandon3055.draconicevolution.inventory;

import com.brandon3055.draconicevolution.api.capability.DECapabilities;
import com.brandon3055.draconicevolution.api.capability.ModuleHost;
import com.brandon3055.draconicevolution.api.capability.PropertyProvider;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * Created by brandon3055 on 12/11/2024
 */
public interface ModularMenuCommon {

    List<Slot> getSlots();
    
    default Stream<ItemStack> getInventoryStacks() {
        return getSlots().stream()
                .map(Slot::getItem)
                .filter(stack -> !stack.isEmpty());
    }
    
    static Stream<PropertyProvider> getProviders(Stream<ItemStack> stacks) {
        return stacks
                .map(e -> e.getCapability(DECapabilities.Properties.ITEM))
                .filter(Objects::nonNull);
    }

    default PropertyProvider findProvider(UUID identity) {
        return getProviders(getInventoryStacks())
                .filter(provider -> provider.getIdentity().equals(identity))
                .findFirst()
                .orElse(null);
    }

    static Stream<ModuleHost> getHosts(Stream<ItemStack> stacks) {
        return stacks
                .map(e -> e.getCapability(DECapabilities.Host.ITEM))
                .filter(Objects::nonNull);
    }

    default ModuleHost findHost(UUID identity) {
        return getHosts(getInventoryStacks())
                .filter(provider -> provider.getIdentity().equals(identity))
                .findFirst()
                .orElse(null);
    }

    default UUID getIdentity(ItemStack stack) {
        PropertyProvider provider = stack.getCapability(DECapabilities.Properties.ITEM);
        if (!stack.isEmpty() && provider != null) {
            return provider.getIdentity();
        }
        return null;
    }
}
