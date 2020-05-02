package com.brandon3055.draconicevolution.api.config;

import codechicken.lib.data.MCDataInput;
import codechicken.lib.data.MCDataOutput;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Created by brandon3055 on 2/5/20.
 */
public class IntegerProperty extends AbstractProperty<IntegerProperty> {
    private int value;
    private int min;
    private int max;
    private Function<Integer, String> displayFormatter = String::valueOf;
    private Supplier<Integer> valueOverride;

    public IntegerProperty(String name, int defaultValue, int min, int max) {
        super(name);
        this.value = defaultValue;
        this.min = min;
        this.max = max;
    }

    public IntegerProperty(String name, ITextComponent displayName, int defaultValue, int min, int max) {
        super(name, displayName);
        this.value = defaultValue;
        this.min = min;
        this.max = max;
    }

    public int getValue() {
        return valueOverride == null ? value : valueOverride.get();
    }

    public void setValue(int value) {
        this.value = value;
    }

    public void setValueOverride(Supplier<Integer> valueOverride) {
        this.valueOverride = valueOverride;
    }

    @Override
    public String getDisplayValue() {
        return displayFormatter.apply(getValue());
    }

    public void setValueFormatter(Function<Integer, String> displayFormatter) {
        this.displayFormatter = displayFormatter;
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = super.serializeNBT();
        nbt.putInt("value", value);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        value = Math.max(min, Math.min(max, nbt.getInt("value")));
        super.deserializeNBT(nbt);
    }

    @Override
    public void serializeMCData(MCDataOutput output) {
        super.serializeMCData(output);
        output.writeVarInt(value);
    }

    @Override
    public void deSerializeMCData(MCDataInput input) {
        super.deSerializeMCData(input);
        value = Math.max(min, Math.min(max, input.readVarInt()));
    }
}
