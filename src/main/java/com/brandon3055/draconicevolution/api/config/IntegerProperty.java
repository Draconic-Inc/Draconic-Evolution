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
public class IntegerProperty extends ConfigProperty {
    private int value;
    private int defaultValue;
    private IntegerFormatter formatter = IntegerFormatter.RAW;
    private Supplier<Integer> min = () -> Integer.MIN_VALUE;
    private Supplier<Integer> max = () -> Integer.MAX_VALUE;
    private BiConsumer<ItemStack, IntegerProperty> changeListener = null;

    public IntegerProperty(String name, int defaultValue) {
        super(name);
        this.value = this.defaultValue = defaultValue;
    }

    public IntegerProperty(String name, Component displayName, int defaultValue) {
        super(name, displayName);
        this.value = this.defaultValue = defaultValue;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = MathHelper.clip(value, getMin(), getMax());
    }

    public IntegerProperty min(int minValue) {
        this.min = () -> minValue;
        validateValue();
        return this;
    }

    public IntegerProperty min(Supplier<Integer> minSupplier) {
        this.min = minSupplier;
        validateValue();
        return this;
    }

    public IntegerProperty max(int maxValue) {
        this.max = () -> maxValue;
        validateValue();
        return this;
    }

    public IntegerProperty max(Supplier<Integer> maxSupplier) {
        this.max = maxSupplier;
        validateValue();
        return this;
    }

    public IntegerProperty range(int minValue, int maxValue) {
        this.min = () -> minValue;
        this.max = () -> maxValue;
        validateValue();
        return this;
    }

    public IntegerProperty range(Supplier<Integer> minSupplier, Supplier<Integer> maxSupplier) {
        this.min = minSupplier;
        this.max = maxSupplier;
        validateValue();
        return this;
    }

    public int getMin() {
        return min.get();
    }

    public int getMax() {
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

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = super.serializeNBT();
        if (this.value != this.defaultValue) {
            nbt.putInt("value", value);
        }
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        if (nbt.contains("value")) {
            value = nbt.getInt("value");
        }
        value = Math.max(min.get(), Math.min(max.get(), value));
        super.deserializeNBT(nbt);
    }

    @Override
    public void serializeMCData(MCDataOutput output) {
        super.serializeMCData(output);
        output.writeInt(value);
    }

    @Override
    public void deSerializeMCData(MCDataInput input) {
        super.deSerializeMCData(input);
        value = Math.max(min.get(), Math.min(max.get(), input.readInt()));
    }

    @Override
    public void loadData(PropertyData data, ItemStack stack) {
        value = Math.max(min.get(), Math.min(max.get(), data.integerValue));
        onValueChanged(stack);
    }

}
