package com.brandon3055.draconicevolution.api.modules.lib;

import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.draconicevolution.api.modules.ModuleType;
import com.brandon3055.draconicevolution.api.modules.properties.ModuleData;
import com.brandon3055.draconicevolution.api.modules.properties.ModuleProperties;
import net.minecraft.item.Item;

/**
 * Created by brandon3055 on 4/16/20.
 */
public class ModuleImpl<T extends ModuleData<T>> extends BaseModule<T> {

    private final Item moduleItem;

    public ModuleImpl(ModuleType<T> moduleType, TechLevel techLevel, T data, Item moduleItem) {
        super(moduleType, new ModuleProperties<>(techLevel, data));
        this.moduleItem = moduleItem;
    }

    public ModuleImpl(ModuleType<T> moduleType, TechLevel techLevel, T data, int width, int height, Item moduleItem) {
        super(moduleType, new ModuleProperties<>(techLevel, width, height, data));
        this.moduleItem = moduleItem;
    }

    @Override
    public Item getItem() {
        return moduleItem;
    }
}