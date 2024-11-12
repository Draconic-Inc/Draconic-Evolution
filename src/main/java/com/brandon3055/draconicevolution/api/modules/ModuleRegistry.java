package com.brandon3055.draconicevolution.api.modules;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

/**
 * Created by brandon3055 on 19/4/20.
 *
 * This just provides a way to access the module registry from within the Draconic Evolution API
 */
public class ModuleRegistry {

    private static Registry<Module<?>> REGISTRY = null;

    public static Registry<Module<?>> getRegistry() {
        if (REGISTRY == null) {
            REGISTRY = (Registry<Module<?>>) BuiltInRegistries.REGISTRY.get(new ResourceLocation("draconicevolution", "modules"));
        }
        return REGISTRY;
    }

}
