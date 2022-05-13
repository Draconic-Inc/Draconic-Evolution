package com.brandon3055.draconicevolution.api.modules.lib;

import com.brandon3055.brandonscore.api.power.IOInfo;
import com.brandon3055.brandonscore.api.power.IOPStorageModifiable;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;

public class ModularOPStorage implements IOPStorageModifiable, INBTSerializable<CompoundTag> {

    private boolean canExtract = false;
    private long energy = 0;
    private long baseCapacity;
    public long baseTransfer;
    private ModuleHostImpl host;

    public ModularOPStorage(ModuleHostImpl host, long baseCapacity, long baseTransfer) {
        this.baseCapacity = baseCapacity;
        this.baseTransfer = baseTransfer;
        this.host = host;
    }

    public ModularOPStorage(ModuleHostImpl host, long baseCapacity, long baseTransfer, boolean canExtract) {
        this(host, baseCapacity, baseTransfer);
        this.canExtract = canExtract;
    }

    @Override
    public long receiveOP(long maxReceive, boolean simulate) {
        if (!canReceive()) {
            return 0;
        }

        long energyReceived = Math.min(getMaxOPStored() - energy, Math.min(this.maxReceive(), maxReceive));
        if (!simulate) {
            energy += energyReceived;
        }
        return energyReceived;
    }

    @Override
    public long extractOP(long maxExtract, boolean simulate) {
        if (!canExtract()) {
            return 0;
        }

        long energyExtracted = Math.min(energy, Math.min(this.maxExtract(), maxExtract));
        if (!simulate) {
            energy -= energyExtracted;
        }
        return energyExtracted;
    }

    @Override
    public long modifyEnergyStored(long amount) {
        if (amount > getMaxOPStored() - energy) {
            amount = getMaxOPStored() - energy;
        } else if (amount < -energy) {
            amount = -energy;
        }

        energy += amount;
        return Math.abs(amount);
    }

    @Override
    public long getOPStored() {
        return energy;
    }

    @Override
    public long getMaxOPStored() {
        return baseCapacity + host.getEnergyData().getCapacity();
    }

    @Override
    public boolean canExtract() {
        return energy > 0 && (canExtract || host.getEnergyShare().getTransferRate() > 0);
    }

    @Override
    public boolean canReceive() {
        return energy < getMaxOPStored();
    }

    @Override
    public long maxExtract() {
        return baseTransfer + host.getEnergyData().getTransfer();
    }

    @Override
    public long maxReceive() {
        return baseTransfer + host.getEnergyData().getTransfer();
    }

    @Nullable
    @Override
    public IOInfo getIOInfo() {
        return null; //TODO... maybe
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putLong("energy", energy);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        energy = nbt.getLong("energy");
    }
}
