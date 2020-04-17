package com.brandon3055.draconicevolution.modules_temp;

import net.minecraftforge.registries.ForgeRegistryEntry;

/**
 * Created by covers1624 on 4/16/20.
 */
public abstract class BaseModule<T extends IModuleProperties> extends ForgeRegistryEntry<IModule<?>> implements IModule<T> {
    private T properties;

    public BaseModule(T properties) {
        this.properties = properties;
    }

    @Override
    public T getProperties() {
        return properties;
    }
}



