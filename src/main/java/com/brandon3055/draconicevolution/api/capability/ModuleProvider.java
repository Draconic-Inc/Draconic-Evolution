package com.brandon3055.draconicevolution.api.capability;


import com.brandon3055.draconicevolution.api.modules.Module;
import com.brandon3055.draconicevolution.api.modules.properties.ModuleData;

/**
 * Created by covers1624 on 4/16/20.
 */
public interface ModuleProvider<P extends ModuleData<P>> {

    Module<P> getModule();
}
