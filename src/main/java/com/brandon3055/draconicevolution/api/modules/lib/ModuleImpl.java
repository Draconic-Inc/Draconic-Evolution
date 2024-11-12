package com.brandon3055.draconicevolution.api.modules.lib;

import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.draconicevolution.api.modules.Module;
import com.brandon3055.draconicevolution.api.modules.ModuleRegistry;
import com.brandon3055.draconicevolution.api.modules.ModuleType;
import com.brandon3055.draconicevolution.api.modules.data.ModuleData;
import com.brandon3055.draconicevolution.api.modules.data.ModuleProperties;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.function.Function;

/**
 * Created by brandon3055 on 4/16/20.
 */
public class ModuleImpl<T extends ModuleData<T>> extends BaseModule<T> {

    private Item moduleItem;
    protected int maxInstall = -1;

    public ModuleImpl(ModuleType<T> moduleType, TechLevel techLevel, Function<Module<T>, T> dataGenerator, Item moduleItem) {
        super(moduleType, new ModuleProperties<>(techLevel, dataGenerator));
        this.moduleItem = moduleItem;
    }

    public ModuleImpl(ModuleType<T> moduleType, TechLevel techLevel, Function<Module<T>, T> dataGenerator, int width, int height, Item moduleItem) {
        super(moduleType, new ModuleProperties<>(techLevel, width, height, dataGenerator));
        this.moduleItem = moduleItem;
    }

    public ModuleImpl(ModuleType<T> moduleType, TechLevel techLevel, Function<Module<T>, T> dataGenerator) {
        super(moduleType, new ModuleProperties<>(techLevel, dataGenerator));
    }

    public ModuleImpl(ModuleType<T> moduleType, TechLevel techLevel, Function<Module<T>, T> dataGenerator, int width, int height) {
        super(moduleType, new ModuleProperties<>(techLevel, width, height, dataGenerator));
    }

    public void setModuleItem(Item moduleItem) {
        this.moduleItem = moduleItem;
    }

    @Override
    public Item getItem() {
        if (moduleItem == null) {
            ResourceLocation key = ModuleRegistry.getRegistry().getKey(this);
            moduleItem = BuiltInRegistries.ITEM.get(new ResourceLocation(key.getNamespace(), "item_" + key.getPath()));
            if (moduleItem == Items.AIR) {
                throw new IllegalStateException("Module item was not provided and no matching item was found in the item registry.");
            }
        }
        return moduleItem;
    }

    public ModuleImpl<T> setMaxInstall(int maxInstall) {
        this.maxInstall = maxInstall;
        return this;
    }

    @Override
    public int maxInstallable() {
        return maxInstall == -1 ? super.maxInstallable() : maxInstall;
    }
}