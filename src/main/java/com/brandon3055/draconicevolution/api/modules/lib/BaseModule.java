package com.brandon3055.draconicevolution.api.modules.lib;

import com.brandon3055.draconicevolution.api.modules.Module;
import com.brandon3055.draconicevolution.api.modules.ModuleType;
import com.brandon3055.draconicevolution.api.modules.data.ModuleData;
import com.brandon3055.draconicevolution.api.modules.data.ModuleProperties;
import net.minecraftforge.registries.ForgeRegistryEntry;

/**
 * Created by covers1624 on 4/16/20.
 */
public abstract class BaseModule<T extends ModuleData<T>> extends ForgeRegistryEntry<Module<?>> implements Module<T> {
    private final ModuleType<T> moduleType;
    private ModuleProperties<T> properties;

    public BaseModule(ModuleType<T> moduleType, ModuleProperties<T> properties) {
        this.moduleType = moduleType;
        this.properties = properties;
        properties.loadDefaults(moduleType);
    }

    @Override
    public ModuleProperties<T> getProperties() {
        return properties;
    }

    @Override
    public ModuleType<T> getType() {
        return moduleType;
    }

    public void reloadData() {
        properties.reloadData(this);
    }
}



