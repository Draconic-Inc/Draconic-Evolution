package com.brandon3055.draconicevolution.api.modules.lib;

import codechicken.lib.util.SneakyUtils;
import com.brandon3055.draconicevolution.api.modules.ModuleType;
import com.brandon3055.draconicevolution.api.modules.properties.ModuleData;
import com.brandon3055.draconicevolution.api.modules.properties.ModuleProperties;

/**
 * Created by brandon3055 on 18/4/20.
 */
public class BasicModuleType<T extends ModuleData<T>> implements ModuleType<T> {
    private int maxInstallable = -1;
    private int defaultWidth = -1;
    private int defaultHeight = -1;
    private String name;

    public BasicModuleType(String name, int defaultWidth, int defaultHeight) {
        this.name = name;
        this.defaultWidth = defaultWidth;
        this.defaultHeight = defaultHeight;
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
}
