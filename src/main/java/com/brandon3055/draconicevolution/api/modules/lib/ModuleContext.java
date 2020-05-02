package com.brandon3055.draconicevolution.api.modules.lib;

import com.brandon3055.draconicevolution.api.capability.ModuleHost;

/**
 * Created by brandon3055 on 18/4/20.
 */
public abstract class ModuleContext {

    private ModuleHost moduleHost;

    public ModuleContext(ModuleHost moduleHost) {
        this.moduleHost = moduleHost;
    }

    public ModuleHost getModuleHost() {
        return moduleHost;
    }

    public abstract Type getType();

    public enum Type {
        ITEM_STACK,
        TILE_ENTITY;
    }
}
