package com.brandon3055.draconicevolution.modules_temp.capability;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by brandon3055 on 17/4/20.
 */
public class ModuleHostCapabilityProvider implements ICapabilitySerializable<CompoundNBT> {

    private IModuleHost moduleHost;

    public ModuleHostCapabilityProvider(IModuleHost moduleHost) {
        this.moduleHost = moduleHost;
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return LazyOptional.of(() -> moduleHost).cast();
    }

    @Override
    public CompoundNBT serializeNBT() {
        return moduleHost.serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        moduleHost.deserializeNBT(nbt);
    }
}
