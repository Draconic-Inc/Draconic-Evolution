package com.brandon3055.draconicevolution.api.capability;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

import static com.brandon3055.draconicevolution.api.capability.DECapabilities.MODULE_HOST_CAPABILITY;
import static com.brandon3055.draconicevolution.api.capability.DECapabilities.PROPERTY_PROVIDER_CAPABILITY;

/**
 * Created by brandon3055 on 17/4/20.
 */
public class ItemCapabilityProvider<T extends INBTSerializable<CompoundNBT>> implements ICapabilitySerializable<CompoundNBT> {

    private T capability;

    public ItemCapabilityProvider(T capability) {
        this.capability = capability;
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if ((cap == MODULE_HOST_CAPABILITY && capability instanceof ModuleHost) || (cap == PROPERTY_PROVIDER_CAPABILITY && capability instanceof PropertyProvider)) {
            return LazyOptional.of(() -> capability).cast();
        }
        return LazyOptional.empty();
    }

    @Override
    public CompoundNBT serializeNBT() {
        return capability.serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        capability.deserializeNBT(nbt);
    }
}
