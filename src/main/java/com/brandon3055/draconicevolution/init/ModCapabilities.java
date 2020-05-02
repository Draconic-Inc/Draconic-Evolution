package com.brandon3055.draconicevolution.init;

import com.brandon3055.draconicevolution.api.capability.ModuleHost;
import com.brandon3055.draconicevolution.api.capability.ModuleProvider;
import com.brandon3055.draconicevolution.api.capability.PropertyProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;

/**
 * Created by brandon3055 and covers1624 on 4/16/20.
 */
public class ModCapabilities {


    public static void register() {
        CapabilityManager.INSTANCE.register(ModuleProvider.class, new NullStorage<>(), () -> null);
        CapabilityManager.INSTANCE.register(ModuleHost.class, new SerializableStorage<>(), () -> null);
        CapabilityManager.INSTANCE.register(PropertyProvider.class, new SerializableStorage<>(), () -> null);
    }

    //@formatter:off
    private static class NullStorage<T extends ModuleProvider<?>> implements Capability.IStorage<T> {

        @Override public INBT writeNBT(Capability<T> capability, T instance, Direction side) { return null; }
        @Override public void readNBT(Capability<T> capability, T instance, Direction side, INBT nbt) { }
    }
    //@formatter:on

    private static class SerializableStorage<T extends INBTSerializable<CompoundNBT>> implements Capability.IStorage<T> {
        @Nullable
        @Override
        public INBT writeNBT(Capability<T> capability, T instance, Direction side) {
            return instance.serializeNBT();
        }

        @Override
        public void readNBT(Capability<T> capability, T instance, Direction side, INBT nbt) {
            instance.deserializeNBT((CompoundNBT) nbt);
        }
    }
}
