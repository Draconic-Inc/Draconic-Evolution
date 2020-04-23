package com.brandon3055.draconicevolution.api.modules.lib;

import com.brandon3055.draconicevolution.api.modules.IModule;
import com.brandon3055.draconicevolution.api.modules.ModuleType;
import com.brandon3055.draconicevolution.api.modules.properties.ModuleProperties;
import net.minecraftforge.registries.ForgeRegistryEntry;

/**
 * Created by covers1624 on 4/16/20.
 */
public abstract class BaseModule<T extends ModuleProperties<T>> extends ForgeRegistryEntry<IModule<?>> implements IModule<T> {
    private final ModuleType<T> moduleType;
    private T properties;

    public BaseModule(ModuleType<T> moduleType, T properties) {
        this.moduleType = moduleType;
        this.properties = properties;
        properties.loadDefaults(moduleType);
    }

    @Override
    public T getProperties() {
        return properties;
    }

    @Override
    public ModuleType<T> getModuleType() {
        return moduleType;
    }
}



