package com.brandon3055.draconicevolution.lib;

import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.draconicevolution.api.modules.Module;
import com.brandon3055.draconicevolution.init.DEModules;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataSerializer;

import java.util.Optional;

/**
 * Created by brandon3055 on 15/4/21
 */
@Deprecated //These need to be registered to ForgeRegistries.DATA_SERIALIZERS
public class Serializers {

    public static final EntityDataSerializer<Optional<Module<?>>> OPT_MODULE_SERIALIZER = new EntityDataSerializer<>() {
        public void write(FriendlyByteBuf buf, Optional<Module<?>> value) {
            buf.writeBoolean(value.isPresent());
            value.ifPresent(module -> buf.writeResourceLocation(DEModules.REGISTRY.getKey(module)));
        }

        public Optional<Module<?>> read(FriendlyByteBuf buf) {
            Module<?> module = DEModules.REGISTRY.get(buf.readResourceLocation());
            return !buf.readBoolean() || module == null ? Optional.empty() : Optional.of(module);
        }

        public Optional<Module<?>> copy(Optional<Module<?>> value) {
            return value;
        }
    };

    public static final EntityDataSerializer<TechLevel> TECH_LEVEL_SERIALIZER = new EntityDataSerializer<TechLevel>() {
        public void write(FriendlyByteBuf packetBuffer, TechLevel techLevel) {
            packetBuffer.writeEnum(techLevel);
        }

        public TechLevel read(FriendlyByteBuf packetBuffer) {
            return packetBuffer.readEnum(TechLevel.class);
        }

        public TechLevel copy(TechLevel techLevel) {
            return techLevel;
        }
    };

    static {
//        DataSerializers.registerSerializer(OPT_MODULE_SERIALIZER);
//        DataSerializers.registerSerializer(TECH_LEVEL_SERIALIZER);
    }

}
