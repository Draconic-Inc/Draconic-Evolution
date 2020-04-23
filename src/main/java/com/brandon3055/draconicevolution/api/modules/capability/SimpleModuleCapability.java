package com.brandon3055.draconicevolution.api.modules.capability;

import com.brandon3055.draconicevolution.api.modules.IModule;
import com.brandon3055.draconicevolution.api.modules.properties.ModuleProperties;

/**
 * Created by brandon3055 and covers1624 on 4/16/20.
 */
@Deprecated // I dont think i need this. com.brandon3055.draconicevolution.api.modules.lib.ModuleItem has it covered.
public class SimpleModuleCapability<P extends ModuleProperties<P>> implements IModuleProvider<P> {

    private final IModule<P> module;

    public SimpleModuleCapability(IModule<P> module) {
        this.module = module;
    }

    @Override
    public IModule<P> getModule() {
        return module;
    }
}
