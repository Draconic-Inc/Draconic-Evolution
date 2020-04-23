package com.brandon3055.draconicevolution.api.modules.capability;


import com.brandon3055.draconicevolution.api.modules.IModule;
import com.brandon3055.draconicevolution.api.modules.properties.ModuleProperties;

/**
 * Created by covers1624 on 4/16/20.
 */
public interface IModuleProvider<P extends ModuleProperties<P>> {

    IModule<P> getModule();
}
