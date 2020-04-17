package com.brandon3055.draconicevolution.modules_temp.impl;

import com.brandon3055.draconicevolution.api.TechLevel;
import com.brandon3055.draconicevolution.init.DEContent;
import com.brandon3055.draconicevolution.modules_temp.BaseModule;
import com.brandon3055.draconicevolution.modules_temp.EnergyModuleProperties;
import com.brandon3055.draconicevolution.modules_temp.ModuleType;
import com.brandon3055.draconicevolution.modules_temp.ModuleTypes;
import net.minecraft.item.Item;

/**
 * Created by brandon3055 on 4/16/20.
 */
public class DraconicEnergyModule extends BaseModule<EnergyModuleProperties> {

    public DraconicEnergyModule(EnergyModuleProperties properties) {
        super(properties);
    }

    @Override
    public ModuleType<EnergyModuleProperties> getModuleType() {
        return ModuleTypes.ENERGY_STORAGE;
    }

    @Override
    public TechLevel getModuleTechLevel() {
        return TechLevel.DRACONIUM;
    }

    @Override
    public Item getItem() {
        return DEContent.test_module_host;
    }

    @Override
    public void applyProperties(EnergyModuleProperties properties) {
        properties.setStorageSize(3000000000L);
    }
}