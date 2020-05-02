package com.brandon3055.draconicevolution.api.config;

import codechicken.lib.data.MCDataInput;
import codechicken.lib.data.MCDataOutput;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Created by brandon3055 on 2/5/20.
 */
public class DecimalProperty extends AbstractProperty<DecimalProperty> {
    private double value;
    private double min;
    private double max;
    private Function<Double, String> displayFormatter = String::valueOf;
    private Supplier<Double> valueOverride;

    public DecimalProperty(String name, double defaultValue, double min, double max) {
        super(name);
        this.value = defaultValue;
        this.min = min;
        this.max = max;
    }

    public DecimalProperty(String name, ITextComponent displayName, double defaultValue, double min, double max) {
        super(name, displayName);
        this.value = defaultValue;
        this.min = min;
        this.max = max;
    }

    public double getValue() {
        return valueOverride == null ? value : valueOverride.get();
    }

    public void setValue(double value) {
        this.value = value;
    }

    public void setValueOverride(Supplier<Double> valueOverride) {
        this.valueOverride = valueOverride;
    }

    @Override
    public String getDisplayValue() {
        return displayFormatter.apply(getValue());
    }

    public void setValueFormatter(Function<Double, String> displayFormatter) {
        this.displayFormatter = displayFormatter;
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = super.serializeNBT();
        nbt.putDouble("value", value);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        value = Math.max(min, Math.min(max, nbt.getDouble("value")));
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
        value = Math.max(min, Math.min(max, input.readDouble()));
    }
}
