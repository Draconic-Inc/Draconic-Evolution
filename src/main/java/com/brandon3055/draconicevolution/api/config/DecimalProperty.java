package com.brandon3055.draconicevolution.api.config;

import codechicken.lib.data.MCDataInput;
import codechicken.lib.data.MCDataOutput;
import codechicken.lib.math.MathHelper;
import com.brandon3055.draconicevolution.client.gui.modular.itemconfig.PropertyData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Created by brandon3055 on 2/5/20.
 */
public class DecimalProperty extends ConfigProperty {
    private double value;
    private double defaultValue;
    private DecimalFormatter formatter = DecimalFormatter.RAW_2;
    private Supplier<Double> min = () -> Double.NEGATIVE_INFINITY;
    private Supplier<Double> max = () -> Double.POSITIVE_INFINITY;
    private BiConsumer<ItemStack, DecimalProperty> changeListener = null;

    public DecimalProperty(String name, double defaultValue) {
        super(name);
        this.value = this.defaultValue = defaultValue;
    }

    public DecimalProperty(String name, Component displayName, double defaultValue) {
        super(name, displayName);
        this.value = this.defaultValue = defaultValue;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = MathHelper.clip(value, getMin(), getMax());
    }

    public DecimalProperty min(double minValue) {
        this.min = () -> minValue;
        validateValue();
        return this;
    }

    public DecimalProperty min(Supplier<Double> minSupplier) {
        this.min = minSupplier;
        validateValue();
        return this;
    }

    public DecimalProperty max(double maxValue) {
        this.max = () -> maxValue;
        validateValue();
        return this;
    }

    public DecimalProperty max(Supplier<Double> maxSupplier) {
        this.max = maxSupplier;
        validateValue();
        return this;
    }

    public DecimalProperty range(double minValue, double maxValue) {
        this.min = () -> minValue;
        this.max = () -> maxValue;
        validateValue();
        return this;
    }

    public DecimalProperty range(Supplier<Double> minSupplier, Supplier<Double> maxSupplier) {
        this.min = minSupplier;
        this.max = maxSupplier;
        validateValue();
        return this;
    }

    public double getMin() {
        return min.get();
    }

    public double getMax() {
        return max.get();
    }

    @Override
    public String getDisplayValue() {
        return formatter.format(getValue());
    }

    @Override
    public void onValueChanged(ItemStack stack) {
        if (changeListener != null) {
            changeListener.accept(stack, this);
        }
    }

    @Override
    public void validateValue() {
        value = Math.max(min.get(), Math.min(max.get(), value));
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

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = super.serializeNBT();
//        if (this.value != this.defaultValue) {
            nbt.putDouble("value", value);
//        }
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        if (nbt.contains("value")) {
            value = nbt.getDouble("value");
        }
        value = Math.max(min.get(), Math.min(max.get(), value));
        super.deserializeNBT(nbt);
    }

    @Override
    public void serializeMCData(MCDataOutput output) {
        super.serializeMCData(output);
        output.writeDouble(value);
    }

    @Override
    public void deSerializeMCData(MCDataInput input) {
        super.deSerializeMCData(input);
        value = Math.max(min.get(), Math.min(max.get(), input.readDouble()));
    }

    @Override
    public void loadData(PropertyData data, ItemStack stack) {
        value = Math.max(min.get(), Math.min(max.get(), data.decimalValue));
        onValueChanged(stack);
    }
}
