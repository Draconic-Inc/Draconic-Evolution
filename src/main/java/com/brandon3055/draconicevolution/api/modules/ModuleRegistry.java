package com.brandon3055.draconicevolution.api.modules;

import com.brandon3055.draconicevolution.DraconicEvolution;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.RegistryManager;

/**
 * Created by brandon3055 on 19/4/20.
 *
 * This just provides a way to access the module registry from within the Draconic Evolution API
 */
public class ModuleRegistry {

    private static ForgeRegistry<IModule<?>> MODULE_REGISTRY = null;

    public static ForgeRegistry<IModule<?>> getRegistry() {
        if (MODULE_REGISTRY == null) {
            MODULE_REGISTRY = RegistryManager.ACTIVE.getRegistry(new ResourceLocation("draconicevolution", "modules"));
        }
        return MODULE_REGISTRY;
    }

}
