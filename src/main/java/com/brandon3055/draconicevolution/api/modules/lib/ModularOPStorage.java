package com.brandon3055.draconicevolution.api.modules.lib;

import com.brandon3055.brandonscore.api.power.OPStorage;
import com.brandon3055.brandonscore.blocks.TileBCore;
import com.brandon3055.draconicevolution.api.capability.DECapabilities;
import com.brandon3055.draconicevolution.api.capability.ModuleHost;
import com.brandon3055.draconicevolution.api.modules.ModuleTypes;
import com.brandon3055.draconicevolution.api.modules.data.EnergyData;

import java.util.function.Supplier;

/**
 * Created by brandon3055 on 16/11/2022
 */
public class ModularOPStorage extends OPStorage {

    private Supplier<ModuleHost> hostSupplier;
    private TileBCore tile;

    public ModularOPStorage(Supplier<ModuleHost> hostSupplier, long baseCapacity) {
        super(baseCapacity);
        this.hostSupplier = hostSupplier;
        setReceiveOnly();
    }

    public ModularOPStorage(Supplier<ModuleHost> hostSupplier, long baseCapacity, long baseTransfer) {
        super(baseCapacity, baseTransfer);
        this.hostSupplier = hostSupplier;
        setReceiveOnly();
    }

    public ModularOPStorage(Supplier<ModuleHost> hostSupplier, long baseCapacity, long baseReceive, long baseExtract) {
        super(baseCapacity, baseReceive, baseExtract);
        this.hostSupplier = hostSupplier;
        setReceiveOnly();
    }

    public ModularOPStorage(TileBCore tile, long baseCapacity) {
        super(baseCapacity);
        this.tile = tile;
    }

    public ModularOPStorage(TileBCore tile, long baseCapacity, long baseTransfer) {
        super(baseCapacity, baseTransfer);
        this.tile = tile;
    }

    public ModularOPStorage(TileBCore tile, long baseCapacity, long baseReceive, long baseExtract) {
        super(baseCapacity, baseReceive, baseExtract);
        this.tile = tile;
    }

    private ModuleHost getHost() {
        if (tile != null) {
            return DECapabilities.Host.fromBlockEntity(tile);
        } else if (hostSupplier != null) {
            return hostSupplier.get();
        }
        return null;
    }

    @Override
    public ModularOPStorage setIOMode(boolean allowExtract, boolean allowReceive) {
        return (ModularOPStorage) super.setIOMode(allowExtract, allowReceive);
    }

    @Override
    public long getMaxOPStored() {
        ModuleHost host = getHost();
        if (host == null) {
            return super.getMaxOPStored();
        }

        return super.getMaxOPStored() + host.getModuleData(ModuleTypes.ENERGY_STORAGE, EnergyData.EMPTY).capacity();
    }

    @Override
    public long maxReceive() {
        ModuleHost host = getHost();
        if (host == null) {
            return super.maxReceive();
        }

        return super.maxReceive() + host.getModuleData(ModuleTypes.ENERGY_STORAGE, EnergyData.EMPTY).transfer();
    }

    @Override
    public long maxExtract() {
        ModuleHost host = getHost();
        if (host == null) {
            return super.maxExtract();
        }

        return super.maxExtract() + host.getModuleData(ModuleTypes.ENERGY_STORAGE, EnergyData.EMPTY).transfer();
    }

    @Override
    public void markDirty() {
        super.markDirty();
        if (tile != null) {
            tile.setChanged();
        }
    }
}