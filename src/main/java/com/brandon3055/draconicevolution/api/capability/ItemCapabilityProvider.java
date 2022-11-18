package com.brandon3055.draconicevolution.api.capability;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by brandon3055 on 17/4/20.
 */
@Deprecated //This is dumb and will be removed
public class ItemCapabilityProvider<T extends INBTSerializable<CompoundTag>> implements ICapabilitySerializable<CompoundTag> {

    private T capability;

    public ItemCapabilityProvider(T capability) {
        this.capability = capability;
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if ((cap == DECapabilities.MODULE_HOST_CAPABILITY && capability instanceof ModuleHost) || (cap == DECapabilities.PROPERTY_PROVIDER_CAPABILITY && capability instanceof PropertyProvider)) {
            return LazyOptional.of(() -> capability).cast();
        }
        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        return capability.serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        capability.deserializeNBT(nbt);
    }
}
