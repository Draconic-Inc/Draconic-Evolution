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
public class IntegerProperty extends ConfigProperty {
    private int value;
//    private int defaultValue;
    private IntegerFormatter formatter = IntegerFormatter.RAW;
    private int min = Integer.MIN_VALUE;
    private int max = Integer.MAX_VALUE;

    //Not Serialised
    private BiConsumer<ItemStack, IntegerProperty> changeListener = null;

    public static final Codec<IntegerProperty> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            Codec.STRING.fieldOf("name").forGetter(e -> e.name),
            Codec.BOOL.fieldOf("show_on_hud").forGetter(e -> e.showOnHud),
            UUIDUtil.CODEC.optionalFieldOf("unique_name").forGetter(ConfigProperty::getOptionalUniqueName),
            Codec.INT.fieldOf("value").forGetter(e -> e.value),
//            Codec.INT.fieldOf("default_value").forGetter(e -> e.defaultValue),
            Codec.INT.fieldOf("min").forGetter(e -> e.min),
            Codec.INT.fieldOf("max").forGetter(e -> e.max),
            IntegerFormatter.CODEC.fieldOf("formatter").forGetter(e -> e.formatter)
    ).apply(builder, IntegerProperty::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, IntegerProperty> STREAM_CODEC = NeoForgeStreamCodecs.composite(
            ByteBufCodecs.STRING_UTF8, e -> e.name,
            ByteBufCodecs.BOOL, e -> e.showOnHud,
            ByteBufCodecs.optional(UUIDUtil.STREAM_CODEC), ConfigProperty::getOptionalUniqueName,
            ByteBufCodecs.INT, e -> e.value,
            ByteBufCodecs.INT, e -> e.min,
            ByteBufCodecs.INT, e -> e.max,
//            ByteBufCodecs.INT, e -> e.defaultValue,
            IntegerFormatter.STREAM_CODEC, e -> e.formatter,
            IntegerProperty::new
    );

    public IntegerProperty(String name, int defaultValue) {
        super(name);
        this.value = defaultValue;//this.defaultValue = defaultValue;
    }

    public IntegerProperty(String name, Component displayName, int defaultValue) {
        super(name, displayName);
        this.value = defaultValue;//this.defaultValue = defaultValue;
    }

    IntegerProperty(String name, boolean showOnHud, Optional<UUID> uniqueName, int value, int min, int max, IntegerFormatter formatter) {
        super(name, showOnHud, uniqueName);
        this.value = value;
//        this.defaultValue = defaultValue;
        this.min = min;
        this.max = max;
        this.formatter = formatter;
    }

    @Override
    public IntegerProperty copy() {
        return new IntegerProperty(name, showOnHud, Optional.ofNullable(uniqueName), value, min, max, formatter);
    }

//    @Override
//    public Codec<IntegerProperty> codec() {
//        return CODEC;
//    }
//
//    @Override
//    public StreamCodec<RegistryFriendlyByteBuf, IntegerProperty> streamCodec() {
//        return STREAM_CODEC;
//    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = MathHelper.clip(value, getMin(), getMax());
    }

    public IntegerProperty min(int minValue) {
        this.min = minValue;
        validateValue();
        return this;
    }

//    public IntegerProperty min(Supplier<Integer> minSupplier) {
//        this.min = minSupplier;
//        validateValue();
//        return this;
//    }

    public IntegerProperty max(int maxValue) {
        this.max = maxValue;
        validateValue();
        return this;
    }

//    public IntegerProperty max(Supplier<Integer> maxSupplier) {
//        this.max = maxSupplier;
//        validateValue();
//        return this;
//    }

    public IntegerProperty range(int minValue, int maxValue) {
        this.min = minValue;
        this.max = maxValue;
        validateValue();
        return this;
    }

//    public IntegerProperty range(Supplier<Integer> minSupplier, Supplier<Integer> maxSupplier) {
//        this.min = minSupplier;
//        this.max = maxSupplier;
//        validateValue();
//        return this;
//    }

    public int getMin() {
        return min;
    }

    public int getMax() {
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
        return Type.INTEGER;
    }

    public void setChangeListener(Runnable changeListener) {
        this.changeListener = (stack, integerProperty) -> changeListener.run();
    }

    public void setChangeListener(Consumer<ItemStack> changeListener) {
        this.changeListener = (stack, integerProperty) -> changeListener.accept(stack);
    }

    public void setChangeListener(BiConsumer<ItemStack, IntegerProperty> changeListener) {
        this.changeListener = changeListener;
    }

    public IntegerProperty setFormatter(IntegerFormatter formatter) {
        this.formatter = formatter;
        return this;
    }

    public IntegerFormatter getFormatter() {
        return formatter;
    }

//    @Override
//    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
//        CompoundTag nbt = super.serializeNBT(provider);
//        if (this.value != this.defaultValue) {
//            nbt.putInt("value", value);
//        }
//        return nbt;
//    }
//
//    @Override
//    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
//        if (nbt.contains("value")) {
//            value = nbt.getInt("value");
//        }
//        value = Math.max(min.get(), Math.min(max.get(), value));
//        super.deserializeNBT(provider, nbt);
//    }
//
//    @Override
//    public void serializeMCData(MCDataOutput output) {
//        super.serializeMCData(output);
//        output.writeInt(value);
//    }
//
//    @Override
//    public void deSerializeMCData(MCDataInput input) {
//        super.deSerializeMCData(input);
//        value = Math.max(min.get(), Math.min(max.get(), input.readInt()));
//    }

    @Override
    public void loadData(PropertyData data, ItemStack stack) {
        value = Math.max(getMin(), Math.min(getMax(), data.integerValue));
        onValueChanged(stack);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof IntegerProperty that)) return false;
        if (!super.equals(o)) return false;
        return value == that.value && min == that.min && max == that.max && formatter == that.formatter;
    }

    @Override
    public boolean equalsWOValue(Object o) {
        if (!(o instanceof IntegerProperty that)) return false;
        if (!super.equals(o)) return false;
        return min == that.min && max == that.max && formatter == that.formatter;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), value, formatter, min, max);
    }

    @Override
    public String toString() {
        return "IntegerProperty{" +
               "value=" + value +
               '}';
    }
}
