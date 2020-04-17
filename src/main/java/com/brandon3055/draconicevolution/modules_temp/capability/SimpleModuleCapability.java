package com.brandon3055.draconicevolution.modules_temp.capability;

import com.brandon3055.draconicevolution.modules_temp.IModule;
import com.brandon3055.draconicevolution.modules_temp.IModuleProperties;

/**
 * Created by brandon3055 and covers1624 on 4/16/20.
 */
public class SimpleModuleCapability<P extends IModuleProperties<P>> implements IModuleProvider<P> {

    private final IModule<P> module;

    public SimpleModuleCapability(IModule<P> module) {
        this.module = module;
    }

    @Override
    public IModule<P> getModule() {
        return module;
    }
}
