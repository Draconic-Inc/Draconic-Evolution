package com.brandon3055.draconicevolution.api.config;

import codechicken.lib.math.MathHelper;
import com.brandon3055.draconicevolution.client.gui.modular.itemconfig.PropertyData;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Created by brandon3055 on 2/5/20.
 */
public class DecimalProperty extends ConfigProperty {
    private double value;
    private DecimalFormatter formatter = DecimalFormatter.RAW_2;
    private double min = Double.NEGATIVE_INFINITY;
    private double max = Double.POSITIVE_INFINITY;

    //Not Serialised
    private BiConsumer<ItemStack, DecimalProperty> changeListener = null;

    public static final Codec<DecimalProperty> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            Codec.STRING.fieldOf("name").forGetter(e -> e.name),
            Codec.BOOL.fieldOf("show_on_hud").forGetter(e -> e.showOnHud),
            UUIDUtil.CODEC.optionalFieldOf("unique_name").forGetter(ConfigProperty::getOptionalUniqueName),
            Codec.DOUBLE.fieldOf("value").forGetter(e -> e.value),
//            Codec.DOUBLE.fieldOf("default_value").forGetter(e -> e.defaultValue),
            Codec.DOUBLE.fieldOf("min").forGetter(e -> e.min),
            Codec.DOUBLE.fieldOf("max").forGetter(e -> e.max),
            DecimalFormatter.CODEC.fieldOf("formatter").forGetter(e -> e.formatter)
    ).apply(builder, DecimalProperty::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, DecimalProperty> STREAM_CODEC = NeoForgeStreamCodecs.composite(
            ByteBufCodecs.STRING_UTF8, e -> e.name,
            ByteBufCodecs.BOOL, e -> e.showOnHud,
            ByteBufCodecs.optional(UUIDUtil.STREAM_CODEC), ConfigProperty::getOptionalUniqueName,
            ByteBufCodecs.DOUBLE, e -> e.value,
//            ByteBufCodecs.DOUBLE, e -> e.defaultValue,
            ByteBufCodecs.DOUBLE, e -> e.min,
            ByteBufCodecs.DOUBLE, e -> e.max,
            DecimalFormatter.STREAM_CODEC, e -> e.formatter,
            DecimalProperty::new
    );

    public DecimalProperty(String name, double defaultValue) {
        super(name);
        this.value = defaultValue;//this.defaultValue = defaultValue;
    }

    public DecimalProperty(String name, Component displayName, double defaultValue) {
        super(name, displayName);
        this.value = defaultValue;//this.defaultValue = defaultValue;
    }

    DecimalProperty(String name, boolean showOnHud, Optional<UUID> uniqueName, double value, double min, double max, DecimalFormatter formatter) {
        super(name, showOnHud, uniqueName);
        this.value = value;
        this.min = min;
        this.max = max;
        this.formatter = formatter;
    }

    @Override
    public DecimalProperty copy() {
        return new DecimalProperty(name, showOnHud, Optional.ofNullable(uniqueName), value, min, max, formatter);
    }

    //    @Override
//    public Codec<DecimalProperty> codec() {
//        return CODEC;
//    }
//
//    @Override
//    public StreamCodec<RegistryFriendlyByteBuf, DecimalProperty> streamCodec() {
//        return STREAM_CODEC;
//    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = MathHelper.clip(value, getMin(), getMax());
    }

    public DecimalProperty min(double minValue) {
        this.min = minValue;
        validateValue();
        return this;
    }

//    public DecimalProperty min(Supplier<Double> minSupplier) {
//        this.min = minSupplier;
//        validateValue();
//        return this;
//    }

    public DecimalProperty max(double maxValue) {
        this.max = maxValue;
        validateValue();
        return this;
    }

//    public DecimalProperty max(Supplier<Double> maxSupplier) {
//        this.max = maxSupplier;
//        validateValue();
//        return this;
//    }

    public DecimalProperty range(double minValue, double maxValue) {
        this.min = minValue;
        this.max = maxValue;
        validateValue();
        return this;
    }

//    public DecimalProperty range(Supplier<Double> minSupplier, Supplier<Double> maxSupplier) {
//        this.min = minSupplier;
//        this.max = maxSupplier;
//        validateValue();
//        return this;
//    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }

    @Override
    public Component getDisplayValue() {
        return Component.literal(formatter.format(getValue()));
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
        value = Math.max(getMin(), Math.min(getMax(), value));
    }

    @Override
    public Type getType() {
        return Type.DECIMAL;
    }

    public void setChangeListener(Runnable changeListener) {
        this.changeListener = (stack, integerProperty) -> changeListener.run();
    }

    public void setChangeListener(Consumer<ItemStack> changeListener) {
        this.changeListener = (stack, integerProperty) -> changeListener.accept(stack);
    }

    public void setChangeListener(BiConsumer<ItemStack, DecimalProperty> changeListener) {
        this.changeListener = changeListener;
    }

    public DecimalProperty setFormatter(DecimalFormatter formatter) {
        this.formatter = formatter;
        return this;
    }

    public DecimalFormatter getFormatter() {
        return formatter;
    }

//    @Override
//    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
//        CompoundTag nbt = super.serializeNBT(provider);
////        if (this.value != this.defaultValue) {
//            nbt.putDouble("value", value);
////        }
//        return nbt;
//    }
//
//    @Override
//    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
//        if (nbt.contains("value")) {
//            value = nbt.getDouble("value");
//        }
//        value = Math.max(min.get(), Math.min(max.get(), value));
//        super.deserializeNBT(provider, nbt);
//    }
//
//    @Override
//    public void serializeMCData(MCDataOutput output) {
//        super.serializeMCData(output);
//        output.writeDouble(value);
//    }
//
//    @Override
//    public void deSerializeMCData(MCDataInput input) {
//        super.deSerializeMCData(input);
//        value = Math.max(min.get(), Math.min(max.get(), input.readDouble()));
//    }

    @Override
    public void loadData(PropertyData data, ItemStack stack) {
        value = Math.max(getMin(), Math.min(getMax(), data.decimalValue));
        onValueChanged(stack);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof DecimalProperty that)) return false;
        if (!super.equals(o)) return false;
        return Double.compare(value, that.value) == 0 && Double.compare(min, that.min) == 0 && Double.compare(max, that.max) == 0 && formatter == that.formatter;
    }

    @Override
    public boolean equalsWOValue(Object o) {
        if (!(o instanceof DecimalProperty that)) return false;
        if (!super.equals(o)) return false;
        return Double.compare(min, that.min) == 0 && Double.compare(max, that.max) == 0 && formatter == that.formatter;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), value, formatter, min, max);
    }

    @Override
    public String toString() {
        return "DecimalProperty{" +
               "value=" + value +
               '}';
    }
}
