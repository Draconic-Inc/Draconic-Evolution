package com.brandon3055.draconicevolution.api.modules.lib;

import com.brandon3055.draconicevolution.api.modules.capability.IModuleHost;
import net.minecraft.world.World;

/**
 * Created by brandon3055 on 18/4/20.
 */
public abstract class ModuleContext {

    private IModuleHost moduleHost;

    public ModuleContext(IModuleHost moduleHost) {
        this.moduleHost = moduleHost;
    }

    public IModuleHost getModuleHost() {
        return moduleHost;
    }

    public abstract Type getType();

    public enum Type {
        ITEM_STACK,
        TILE_ENTITY;
    }
}
