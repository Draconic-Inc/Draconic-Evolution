package com.brandon3055.draconicevolution.api.modules.entities;

import com.brandon3055.brandonscore.api.BCStreamCodec;
import com.brandon3055.draconicevolution.api.config.BooleanProperty;
import com.brandon3055.draconicevolution.api.config.ConfigProperty;
import com.brandon3055.draconicevolution.api.modules.Module;
import com.brandon3055.draconicevolution.api.modules.data.NoData;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleContext;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleEntity;
import com.brandon3055.draconicevolution.init.DEModules;
import com.brandon3055.draconicevolution.init.ItemData;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class AutoFireEntity extends ModuleEntity<NoData> {

    private BooleanProperty autoFireEnabled = new BooleanProperty("auto_fire_mod.enable", true).setFormatter(ConfigProperty.BooleanFormatter.ENABLED_DISABLED);

    public static final Codec<AutoFireEntity> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            DEModules.codec().fieldOf("module").forGetter(ModuleEntity::getModule),
            Codec.INT.fieldOf("gridx").forGetter(ModuleEntity::getGridX),
            Codec.INT.fieldOf("gridy").forGetter(ModuleEntity::getGridY),
            BooleanProperty.CODEC.fieldOf("auto_fire_enabled").forGetter(e -> e.autoFireEnabled)
    ).apply(builder, AutoFireEntity::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, AutoFireEntity> STREAM_CODEC = BCStreamCodec.composite(
            DEModules.streamCodec(), ModuleEntity::getModule,
            ByteBufCodecs.INT, ModuleEntity::getGridX,
            ByteBufCodecs.INT, ModuleEntity::getGridY,
            BooleanProperty.STREAM_CODEC, e -> e.autoFireEnabled,
            AutoFireEntity::new
    );

    public AutoFireEntity(Module<NoData> module) {
        super(module);
//        addProperty(autoFireEnabled = new BooleanProperty("auto_fire_mod.enable", true).setFormatter(ConfigProperty.BooleanFormatter.ENABLED_DISABLED));
//        this.savePropertiesToItem = true;
    }

    AutoFireEntity(Module<?> module, int gridX, int gridY, BooleanProperty autoFireEnabled) {
        super((Module<NoData>) module, gridX, gridY);
        this.autoFireEnabled = autoFireEnabled;
    }

    @Override
    public ModuleEntity<?> copy() {
        return new AutoFireEntity(module, getGridX(), getGridY(), autoFireEnabled.copy());
    }

    @Override
    public void getEntityProperties(List<ConfigProperty> properties) {
        super.getEntityProperties(properties);
        properties.add(autoFireEnabled);
    }

    public boolean getAutoFireEnabled() {
        return autoFireEnabled.getValue();
    }

    @Override
    public void saveEntityToStack(ItemStack stack, ModuleContext context) {
        stack.set(ItemData.BOOL_ITEM_PROP_1, autoFireEnabled.copy());
    }

    @Override
    public void loadEntityFromStack(ItemStack stack, ModuleContext context) {
        autoFireEnabled = stack.getOrDefault(ItemData.BOOL_ITEM_PROP_1, autoFireEnabled).copy();
    }
}
