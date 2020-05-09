package com.brandon3055.draconicevolution.api.config;

import codechicken.lib.data.MCDataInput;
import codechicken.lib.data.MCDataOutput;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Created by brandon3055 on 2/5/20.
 */
public class IntegerProperty extends ConfigProperty {
    private int value;
    private IntegerFormatter formatter = IntegerFormatter.RAW;
    private Supplier<Integer> min = () -> 0;
    private Supplier<Integer> max = () -> 0;
    private BiConsumer<ItemStack, IntegerProperty> changeListener = null;

    public IntegerProperty(String name, int defaultValue) {
        super(name);
        this.value = defaultValue;
    }

    public IntegerProperty(String name, ITextComponent displayName, int defaultValue) {
        super(name, displayName);
        this.value = defaultValue;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public IntegerProperty min(int minValue) {
        this.min = () -> minValue;
        return this;
    }

    public IntegerProperty min(Supplier<Integer> minSupplier) {
        this.min = minSupplier;
        return this;
    }

    public IntegerProperty max(int maxValue) {
        this.max = () -> maxValue;
        return this;
    }

    public IntegerProperty max(Supplier<Integer> maxSupplier) {
        this.max = maxSupplier;
        return this;
    }

    public IntegerProperty range(int minValue, int maxValue) {
        this.min = () -> minValue;
        this.max = () -> maxValue;
        return this;
    }

    public IntegerProperty range(Supplier<Integer> minSupplier, Supplier<Integer> maxSupplier) {
        this.min = minSupplier;
        this.max = maxSupplier;
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
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = super.serializeNBT();
        nbt.putInt("value", value);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        value = Math.max(min.get(), Math.min(max.get(), nbt.getInt("value")));
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
}
