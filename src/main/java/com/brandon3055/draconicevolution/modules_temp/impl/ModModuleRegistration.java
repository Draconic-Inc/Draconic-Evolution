package com.brandon3055.draconicevolution.modules_temp.impl;

import com.brandon3055.draconicevolution.modules_temp.EnergyModuleProperties;
import com.brandon3055.draconicevolution.modules_temp.IModule;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.ObjectHolder;

/**
 * Created by brandon3055 on 4/16/20.
 */
public class ModModuleRegistration {

//    @ObjectHolder("energy_draconic")ToDO
    public static DraconicEnergyModule draconicEnergyModule;


    public void register(RegistryEvent.Register<IModule<?>> event) {
        event.getRegistry().register(new DraconicEnergyModule(new EnergyModuleProperties.Impl(100000L)).setRegistryName("energy_draconic"));
    }

}
