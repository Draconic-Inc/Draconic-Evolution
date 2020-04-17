package com.brandon3055.draconicevolution.modules_temp.capability;

import com.brandon3055.draconicevolution.modules_temp.ModuleHostImpl;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;
import java.util.concurrent.Callable;

/**
 * Created by brandon3055 and covers1624 on 4/16/20.
 */
public class ModuleCapability {

    @CapabilityInject(IModuleProvider.class)
    public static final Capability<IModuleProvider<?>> MODULE_CAPABILITY = null;

    @CapabilityInject(IModuleHost.class)
    public static final Capability<IModuleHost> MODULE_HOST_CAPABILITY = null;

    public static void register() {
        CapabilityManager.INSTANCE.register(IModuleProvider.class, new NullStorage<>(), () -> null);
        CapabilityManager.INSTANCE.register(IModuleHost.class, new HostStorage(), ModuleHostImpl::new);
    }

    private static class HostStorage implements Capability.IStorage<IModuleHost> {
        @Nullable
        @Override
        public INBT writeNBT(Capability<IModuleHost> capability, IModuleHost instance, Direction side) {
            return instance.serializeNBT();
        }

        @Override
        public void readNBT(Capability<IModuleHost> capability, IModuleHost instance, Direction side, INBT nbt) {
            instance.deserializeNBT((CompoundNBT) nbt);
        }
    }

    //@formatter:off
    private static class NullStorage<T extends IModuleProvider<?>> implements Capability.IStorage<T> {
        @Override public INBT writeNBT(Capability<T> capability, T instance, Direction side) { return null; }
        @Override public void readNBT(Capability<T> capability, T instance, Direction side, INBT nbt) { }
    }
    //@formatter:on
}
