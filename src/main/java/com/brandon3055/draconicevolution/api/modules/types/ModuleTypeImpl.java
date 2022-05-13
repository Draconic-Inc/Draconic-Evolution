package com.brandon3055.draconicevolution.api.modules.types;

import com.brandon3055.draconicevolution.api.modules.Module;
import com.brandon3055.draconicevolution.api.modules.ModuleCategory;
import com.brandon3055.draconicevolution.api.modules.ModuleType;
import com.brandon3055.draconicevolution.api.modules.data.ModuleData;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleEntity;
import net.covers1624.quack.util.SneakyUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

/**
 * Created by brandon3055 on 18/4/20.
 */
public class ModuleTypeImpl<T extends ModuleData<T>> implements ModuleType<T> {
    private int maxInstallable = -1;
    private int defaultWidth = -1;
    private int defaultHeight = -1;
    private String name;
    private Function<Module<T>, ModuleEntity> entityFactory;
    private Set<ModuleCategory> categories = new HashSet<>();

    public ModuleTypeImpl(String name, int defaultWidth, int defaultHeight, Function<Module<T>, ModuleEntity> entityFactory, ModuleCategory... categories) {
        this.name = name;
        this.defaultWidth = defaultWidth;
        this.defaultHeight = defaultHeight;
        this.entityFactory = entityFactory;
        this.categories.addAll(Arrays.asList(categories));
    }

    public ModuleTypeImpl(String name, int defaultWidth, int defaultHeight, ModuleCategory... categories) {
        this(name, defaultWidth, defaultHeight, ModuleEntity::new, categories);
    }

    public <M> M setMaxInstallable(int maxInstallable) {
        this.maxInstallable = maxInstallable;
        return SneakyUtils.unsafeCast(this);
    }

    @Override
    public int maxInstallable() {
        return maxInstallable;
    }

    @Override
    public int getDefaultWidth() {
        return defaultWidth;
    }

    @Override
    public int getDefaultHeight() {
        return defaultHeight;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ModuleEntity createEntity(Module<T> module) {
        return entityFactory.apply(module);
    }

    @Override
    public Collection<ModuleCategory> getCategories() {
        return categories;
    }
}
