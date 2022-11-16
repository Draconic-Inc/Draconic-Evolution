package com.brandon3055.draconicevolution.api.modules.lib;

import com.brandon3055.brandonscore.api.power.OPStorage;
import com.brandon3055.draconicevolution.api.capability.ModuleHost;
import com.brandon3055.draconicevolution.api.modules.ModuleTypes;
import com.brandon3055.draconicevolution.api.modules.data.EnergyData;

/**
 * Created by brandon3055 on 16/11/2022
 */
public class ModularOPStorage extends OPStorage {

    private ModuleHost host;

    public ModularOPStorage(ModuleHost host, long baseCapacity) {
        super(baseCapacity);
        this.host = host;
        setReceiveOnly();
    }

    public ModularOPStorage(ModuleHost host, long baseCapacity, long baseTransfer) {
        super(baseCapacity, baseTransfer);
        this.host = host;
        setReceiveOnly();
    }

    public ModularOPStorage(ModuleHost host, long baseCapacity, long baseReceive, long baseExtract) {
        super(baseCapacity, baseReceive, baseExtract);
        this.host = host;
        setReceiveOnly();
    }

    @Override
    public ModularOPStorage setIOMode(boolean allowExtract, boolean allowReceive) {
        return (ModularOPStorage) super.setIOMode(allowExtract, allowReceive);
    }

    @Override
    public long getMaxOPStored() {
        return super.getMaxOPStored() + host.getModuleData(ModuleTypes.ENERGY_STORAGE, EnergyData.EMPTY).getCapacity();
    }

    @Override
    public long maxReceive() {
        return super.maxReceive() + host.getModuleData(ModuleTypes.ENERGY_STORAGE, EnergyData.EMPTY).getTransfer();
    }

    @Override
    public long maxExtract() {
        return super.maxExtract() + host.getModuleData(ModuleTypes.ENERGY_STORAGE, EnergyData.EMPTY).getTransfer();
    }
}