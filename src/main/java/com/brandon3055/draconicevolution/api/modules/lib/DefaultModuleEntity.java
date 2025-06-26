package com.brandon3055.draconicevolution.api.modules.lib;

import com.brandon3055.draconicevolution.api.modules.Module;
import com.brandon3055.draconicevolution.api.modules.data.ModuleData;
import com.brandon3055.draconicevolution.init.DEModules;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

/**
 * Created by brandon3055 on 13/06/2025
 */
public class DefaultModuleEntity<T extends ModuleData<T>> extends ModuleEntity<T> {

    public static final Codec<DefaultModuleEntity<?>> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            DEModules.codec().fieldOf("module").forGetter(ModuleEntity::getModule),
            Codec.INT.fieldOf("gridx").forGetter(ModuleEntity::getGridX),
            Codec.INT.fieldOf("gridy").forGetter(ModuleEntity::getGridY)
    ).apply(builder, DefaultModuleEntity::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, DefaultModuleEntity<?>> STREAM_CODEC = StreamCodec.composite(
            DEModules.streamCodec(), ModuleEntity::getModule,
            ByteBufCodecs.INT, ModuleEntity::getGridX,
            ByteBufCodecs.INT, ModuleEntity::getGridY,
            DefaultModuleEntity::new
    );

    public DefaultModuleEntity(Module<T> module) {
        super(module);
    }

    DefaultModuleEntity(Module<?> module, int gridX, int gridY) {
        super((Module<T>) module, gridX, gridY);
    }

    @Override
    public ModuleEntity<?> copy() {
        return new DefaultModuleEntity<>(module, getGridX(), getGridY());
    }
}
