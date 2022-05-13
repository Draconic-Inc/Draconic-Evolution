package com.brandon3055.draconicevolution.init;

import com.brandon3055.draconicevolution.api.capability.ModuleHost;
import com.brandon3055.draconicevolution.api.capability.ModuleProvider;
import com.brandon3055.draconicevolution.api.capability.PropertyProvider;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;

/**
 * Created by brandon3055 and covers1624 on 4/16/20.
 */
public class ModCapabilities {


    public static void register(RegisterCapabilitiesEvent event) {
        event.register(ModuleProvider.class);
        event.register(ModuleHost.class);
        event.register(PropertyProvider.class);
    }

//    //@formatter:off
//    private static class NullStorage<T extends ModuleProvider<?>> implements Capability.IStorage<T> {
//
//        @Override public Tag writeNBT(Capability<T> capability, T instance, Direction side) { return null; }
//        @Override public void readNBT(Capability<T> capability, T instance, Direction side, Tag nbt) { }
//    }
//    //@formatter:on
//
//    private static class SerializableStorage<T extends INBTSerializable<CompoundTag>> implements Capability.IStorage<T> {
//        @Nullable
//        @Override
//        public Tag writeNBT(Capability<T> capability, T instance, Direction side) {
//            return instance.serializeNBT();
//        }
//
//        @Override
//        public void readNBT(Capability<T> capability, T instance, Direction side, Tag nbt) {
//            instance.deserializeNBT((CompoundTag) nbt);
//        }
//    }
}
