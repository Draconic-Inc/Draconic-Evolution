package com.brandon3055.draconicevolution.api.modules.lib;

import codechicken.lib.util.SneakyUtils;
import com.brandon3055.draconicevolution.api.modules.ModuleType;
import com.brandon3055.draconicevolution.api.modules.properties.EnergyModuleProperties;
import com.brandon3055.draconicevolution.api.modules.properties.ModuleProperties;
import com.brandon3055.draconicevolution.init.DEModules;

/**
 * Created by brandon3055 on 18/4/20.
 */
public class BasicModuleType<T extends ModuleProperties<T>> implements ModuleType<T> {
    private int maxInstallable = -1;
    private int defaultWidth = -1;
    private int defaultHeight = -1;
    private String name;

    public BasicModuleType(String name) {
        this.name = name;
    }

    public BasicModuleType(String name, int maxInstallable) {
        this.name = name;
        this.maxInstallable = maxInstallable;
    }

    public <M> M setDefaultSize(int defaultWidth, int defaultHeight) {
        this.defaultWidth = defaultWidth;
        this.defaultHeight = defaultHeight;
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
}
