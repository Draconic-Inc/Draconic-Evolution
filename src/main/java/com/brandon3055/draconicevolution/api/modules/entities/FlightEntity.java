package com.brandon3055.draconicevolution.api.modules.entities;

import com.brandon3055.brandonscore.api.BCStreamCodec;
import com.brandon3055.draconicevolution.api.config.BooleanProperty;
import com.brandon3055.draconicevolution.api.config.ConfigProperty;
import com.brandon3055.draconicevolution.api.config.ConfigProperty.DecimalFormatter;
import com.brandon3055.draconicevolution.api.config.DecimalProperty;
import com.brandon3055.draconicevolution.api.modules.Module;
import com.brandon3055.draconicevolution.api.modules.data.FlightData;
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
import java.util.Optional;

public class FlightEntity extends ModuleEntity<FlightData> {

    private Optional<BooleanProperty> elytraEnabled = Optional.empty();
    private Optional<BooleanProperty> creativeEnabled = Optional.empty();
    private Optional<DecimalProperty> elytraBoost = Optional.empty();

    public static final Codec<FlightEntity> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            DEModules.codec().fieldOf("module").forGetter(FlightEntity::getModule),
            Codec.INT.fieldOf("gridx").forGetter(ModuleEntity::getGridX),
            Codec.INT.fieldOf("gridy").forGetter(ModuleEntity::getGridY),
            BooleanProperty.CODEC.optionalFieldOf("elytra_enabled").forGetter(e -> e.elytraEnabled),
            BooleanProperty.CODEC.optionalFieldOf("creative_enabled").forGetter(e -> e.creativeEnabled),
            DecimalProperty.CODEC.optionalFieldOf("elytra_boost").forGetter(e -> e.elytraBoost)
    ).apply(builder, FlightEntity::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, FlightEntity> STREAM_CODEC = BCStreamCodec.composite(
            DEModules.streamCodec(), FlightEntity::getModule,
            ByteBufCodecs.INT, ModuleEntity::getGridX,
            ByteBufCodecs.INT, ModuleEntity::getGridY,
            ByteBufCodecs.optional(BooleanProperty.STREAM_CODEC), e -> e.elytraEnabled,
            ByteBufCodecs.optional(BooleanProperty.STREAM_CODEC), e -> e.creativeEnabled,
            ByteBufCodecs.optional(DecimalProperty.STREAM_CODEC), e -> e.elytraBoost,
            FlightEntity::new
    );

    public FlightEntity(Module<FlightData> module) {
        super(module);
        if (module.getData().elytra()) {
            elytraEnabled = Optional.of(new BooleanProperty("flight_mod.elytra", true).setFormatter(ConfigProperty.BooleanFormatter.ENABLED_DISABLED));
            elytraBoost = Optional.of(new DecimalProperty("flight_mod.elytra_boost", module.getData().elytraSpeed()).setFormatter(DecimalFormatter.PERCENT_0).range(0, module.getData().elytraSpeed()));
        }
        if (module.getData().creative()) {
            creativeEnabled = Optional.of(new BooleanProperty("flight_mod.creative", true).setFormatter(ConfigProperty.BooleanFormatter.ENABLED_DISABLED));
        }
//        this.savePropertiesToItem = true;
    }

    FlightEntity(Module<?> module, int gridX, int gridY, Optional<BooleanProperty> elytraEnabled, Optional<BooleanProperty> creativeEnabled, Optional<DecimalProperty> elytraBoost) {
        super((Module<FlightData>) module, gridX, gridY);
        this.elytraEnabled = elytraEnabled;
        this.creativeEnabled = creativeEnabled;
        this.elytraBoost = elytraBoost;
    }

    @Override
    public ModuleEntity<?> copy() {
        return new FlightEntity(module, getGridX(), getGridY(), elytraEnabled.map(BooleanProperty::copy), creativeEnabled.map(BooleanProperty::copy), elytraBoost.map(DecimalProperty::copy));
    }

    @Override
    public void getEntityProperties(List<ConfigProperty> properties) {
        super.getEntityProperties(properties);
        elytraEnabled.ifPresent(properties::add);
        creativeEnabled.ifPresent(properties::add);
        elytraBoost.ifPresent(properties::add);
    }

    @Override
    public void onInstalled(ModuleContext context) {}

    public boolean getElytraEnabled() {
        return elytraEnabled.isPresent() && elytraEnabled.get().getValue();
    }

    public boolean getCreativeEnabled() {
        return creativeEnabled.isPresent() && creativeEnabled.get().getValue();
    }

    public double getElytraBoost() {
        return elytraBoost.map(DecimalProperty::getValue).orElse(0.0);
    }

    @Override
    public void saveEntityToStack(ItemStack stack, ModuleContext context) {
        elytraEnabled.ifPresent(e -> stack.set(ItemData.BOOL_ITEM_PROP_1, e.copy()));
        creativeEnabled.ifPresent(e -> stack.set(ItemData.BOOL_ITEM_PROP_2, e.copy()));
        elytraBoost.ifPresent(e -> stack.set(ItemData.DECIMAL_ITEM_PROP_1, e.copy()));
    }

    @Override
    public void loadEntityFromStack(ItemStack stack, ModuleContext context) {
        elytraEnabled = Optional.ofNullable(stack.get(ItemData.BOOL_ITEM_PROP_1)).map(BooleanProperty::copy);
        creativeEnabled = Optional.ofNullable(stack.get(ItemData.BOOL_ITEM_PROP_2)).map(BooleanProperty::copy);
        elytraBoost = Optional.ofNullable(stack.get(ItemData.DECIMAL_ITEM_PROP_1)).map(DecimalProperty::copy);
    }
}
