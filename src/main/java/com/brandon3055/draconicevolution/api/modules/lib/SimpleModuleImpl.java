package com.brandon3055.draconicevolution.api.modules.lib;

import com.brandon3055.draconicevolution.api.modules.ModuleType;
import com.brandon3055.draconicevolution.api.modules.lib.BaseModule;
import com.brandon3055.draconicevolution.api.modules.properties.ModuleProperties;
import net.minecraft.item.Item;

/**
 * Created by brandon3055 on 4/16/20.
 */
public class SimpleModuleImpl<T extends ModuleProperties<T>> extends BaseModule<T> {

    private final Item moduleItem;

    public SimpleModuleImpl(ModuleType<T> moduleType, T properties, Item moduleItem) {
        super(moduleType, properties);
        this.moduleItem = moduleItem;
    }

    @Override
    public Item getItem() {
        return moduleItem;
    }
}