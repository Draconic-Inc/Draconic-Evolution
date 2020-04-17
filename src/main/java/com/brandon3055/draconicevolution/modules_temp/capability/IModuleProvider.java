package com.brandon3055.draconicevolution.modules_temp.capability;


import com.brandon3055.draconicevolution.modules_temp.IModule;
import com.brandon3055.draconicevolution.modules_temp.IModuleProperties;

/**
 * Created by covers1624 on 4/16/20.
 */
public interface IModuleProvider<P extends IModuleProperties<P>> {

    IModule<P> getModule();
}
