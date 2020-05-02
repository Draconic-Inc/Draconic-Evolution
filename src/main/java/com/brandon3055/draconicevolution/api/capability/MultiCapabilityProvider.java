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

/**
 * Created by brandon3055 on 17/4/20.
 */
public class MultiCapabilityProvider implements ICapabilitySerializable<CompoundNBT> {

    private Map<Capability<?>, INBTSerializable<CompoundNBT>> serializableCapabilityMap = new HashMap<>();
    private Map<String, INBTSerializable<CompoundNBT>> nameCapabilityMap = new HashMap<>();

    public MultiCapabilityProvider() {}

    public <T extends INBTSerializable<CompoundNBT>> void addCapability(String nbtName, Capability<T> capability, T capInstance) {
        serializableCapabilityMap.put(capability, capInstance);
        nameCapabilityMap.put(nbtName, capInstance);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (serializableCapabilityMap.containsKey(cap)) {
            return LazyOptional.of(() -> serializableCapabilityMap.get(cap)).cast();
        }

        return LazyOptional.empty();
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        nameCapabilityMap.forEach((name, serializable) -> nbt.put(name, serializable.serializeNBT()));
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        nameCapabilityMap.forEach((name, serializable) -> serializable.deserializeNBT(nbt.getCompound(name)));
    }
}
