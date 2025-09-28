package com.brandon3055.draconicevolution.api.config;

import com.brandon3055.draconicevolution.client.gui.modular.itemconfig.PropertyData;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Created by brandon3055 on 2/5/20.
 */
public class BooleanProperty extends ConfigProperty {
    private boolean value;
    private boolean defaultValue;
    private BooleanFormatter formatter = BooleanFormatter.TRUE_FALSE;

    //Not Serialised
//    private Supplier<Boolean> valueOverride;
    private BiConsumer<ItemStack, BooleanProperty> changeListener;

    public static final Codec<BooleanProperty> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            Codec.STRING.fieldOf("name").forGetter(e -> e.name),
            Codec.BOOL.fieldOf("show_on_hud").forGetter(e -> e.showOnHud),
            UUIDUtil.CODEC.optionalFieldOf("unique_name").forGetter(ConfigProperty::getOptionalUniqueName),
            Codec.BOOL.fieldOf("value").forGetter(e -> e.value),
            Codec.BOOL.fieldOf("default_value").forGetter(e -> e.defaultValue),
            BooleanFormatter.CODEC.fieldOf("formatter").forGetter(e -> e.formatter)
    ).apply(builder, BooleanProperty::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, BooleanProperty> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, e -> e.name,
            ByteBufCodecs.BOOL, e -> e.showOnHud,
            ByteBufCodecs.optional(UUIDUtil.STREAM_CODEC), ConfigProperty::getOptionalUniqueName,
            ByteBufCodecs.BOOL, e -> e.value,
            ByteBufCodecs.BOOL, e -> e.defaultValue,
            BooleanFormatter.STREAM_CODEC, e -> e.formatter,
            BooleanProperty::new
    );

    public BooleanProperty(String name, boolean defaultValue) {
        super(name);
        this.value = this.defaultValue = defaultValue;
    }

    public BooleanProperty(String name, Component displayName, boolean defaultValue) {
        super(name, displayName);
        this.value = this.defaultValue = defaultValue;
    }

    BooleanProperty(String name, boolean showOnHud, Optional<UUID> uniqueName, boolean value, boolean defaultValue, BooleanFormatter formatter) {
        super(name, showOnHud, uniqueName);
        this.value = value;
        this.defaultValue = defaultValue;
        this.formatter = formatter;
    }

    @Override
    public BooleanProperty copy() {
        return new BooleanProperty(name, showOnHud, Optional.ofNullable(uniqueName), value, defaultValue, formatter);
    }

    //    @Override
//    public Codec<BooleanProperty> codec() {
//        return CODEC;
//    }
//
//    @Override
//    public StreamCodec<RegistryFriendlyByteBuf, BooleanProperty> streamCodec() {
//        return STREAM_CODEC;
//    }

    public boolean getValue() {
//        return valueOverride == null ? value : valueOverride.get();
        return value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }

//    public void setValueOverride(Supplier<Boolean> valueOverride) {
//        this.valueOverride = valueOverride;
//    }

    @Override
    public Component getDisplayValue() {
        return formatter.format(getValue());
    }

    public BooleanProperty setFormatter(BooleanFormatter formatter) {
        this.formatter = formatter;
        return this;
    }

    public BooleanFormatter getFormatter() {
        return formatter;
    }

    @Override
    public void onValueChanged(ItemStack stack) {
        if (provider != null) {
            provider.markDirty();
        }
        if (changeListener != null) {
            changeListener.accept(stack, this);
        }
    }

    @Override
    public void validateValue() {
    }

    @Override
    public Type getType() {
        return Type.BOOLEAN;
    }

    public BooleanProperty setChangeListener(Runnable changeListener) {
        this.changeListener = (stack, t) -> changeListener.run();
        return this;
    }

    public BooleanProperty setChangeListener(Consumer<ItemStack> changeListener) {
        this.changeListener = (stack, booleanProperty) -> changeListener.accept(stack);
        return this;
    }

    public BooleanProperty setChangeListener(BiConsumer<ItemStack, BooleanProperty> changeListener) {
        this.changeListener = changeListener;
        return this;
    }

//    @Override
//    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
//        CompoundTag nbt = super.serializeNBT(provider);
//        if (this.value != this.defaultValue) {
//            nbt.putBoolean("value", value);
//        }
//        return nbt;
//    }
//
//    @Override
//    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
//        if (nbt.contains("value")) {
//            value = nbt.getBoolean("value");
//        }
//        super.deserializeNBT(provider, nbt);
//    }
//
//    @Override
//    public void serializeMCData(MCDataOutput output) {
//        super.serializeMCData(output);
//        output.writeBoolean(value);
//    }
//
//    @Override
//    public void deSerializeMCData(MCDataInput input) {
//        super.deSerializeMCData(input);
//        value = input.readBoolean();
//    }

    @Override
    public void loadData(PropertyData data, ItemStack stack) {
        value = data.booleanValue;
        onValueChanged(stack);
    }

    @Override
    public String toString() {
        return "BooleanProperty{" +
               "value=" + value +
               '}';
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof BooleanProperty that)) return false;
        if (!super.equals(o)) return false;
        return value == that.value && defaultValue == that.defaultValue && formatter == that.formatter;
    }

    @Override
    public boolean equalsWOValue(Object o) {
        if (!(o instanceof BooleanProperty that)) return false;
        if (!super.equals(o)) return false;
        return defaultValue == that.defaultValue && formatter == that.formatter;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), value, defaultValue, formatter);
    }
}
