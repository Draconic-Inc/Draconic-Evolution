package com.brandon3055.draconicevolution.api.capability;

import net.minecraft.world.item.ItemStack;

import java.util.HashSet;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * Primarily used for capability items, provides a reliable way to identify a unique itemStack.
 * Created by brandon3055 on 08/11/2024
 */
public interface IdentityProvider {

    /**
     * This must be a completely unique id that can be used to identify a specific item.
     *
     * @return the unique id for this property provider.
     */
    UUID getIdentity();

    /**
     * In the event there are somehow multiple providers with the same ID (possibly due to creative duplication or some other means of stack duplication)
     * This will be called on all but one of the duplicate providers in order to generate new unique id's.
     * May also be used to generate the initial identity.
     */
    void regenIdentity();

    //Only supports resolving duplicates ModuleHostImpl capabilities (Which also combines PropertyProvider)
    static void resolveDuplicateIdentities(Stream<ItemStack> stacks) {
        HashSet<UUID> uuids = new HashSet<>();
        stacks.map(e -> e.getCapability(DECapabilities.Host.ITEM))
                .filter(Objects::nonNull)
                .filter(provider -> !uuids.add(provider.getIdentity()))
                .forEach(ModuleHost::regenIdentity);
    }


}
