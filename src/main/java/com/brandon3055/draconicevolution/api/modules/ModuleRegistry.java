package com.brandon3055.draconicevolution.api.modules;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.RegistryManager;

/**
 * Created by brandon3055 on 19/4/20.
 *
 * This just provides a way to access the module registry from within the Draconic Evolution API
 */
public class ModuleRegistry {

    private static ForgeRegistry<Module<?>> MODULE_REGISTRY = null;

    public static ForgeRegistry<Module<?>> getRegistry() {
        if (MODULE_REGISTRY == null) {
            MODULE_REGISTRY = RegistryManager.ACTIVE.getRegistry(new ResourceLocation("draconicevolution", "modules"));
        }
        return MODULE_REGISTRY;
    }

}
