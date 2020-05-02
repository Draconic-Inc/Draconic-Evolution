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
public class BooleanProperty extends AbstractProperty<BooleanProperty> {
    private boolean value;
    private Function<Boolean, String> displayFormatter = String::valueOf;
    private Supplier<Boolean> valueOverride;

    public BooleanProperty(String name, boolean defaultValue) {
        super(name);
        this.value = defaultValue;
    }

    public BooleanProperty(String name, ITextComponent displayName, boolean defaultValue) {
        super(name, displayName);
        this.value = defaultValue;
    }

    public boolean getValue() {
        return valueOverride == null ? value : valueOverride.get();
    }

    public void setValue(boolean value) {
        this.value = value;
    }

    public void setValueOverride(Supplier<Boolean> valueOverride) {
        this.valueOverride = valueOverride;
    }

    @Override
    public String getDisplayValue() {
        return displayFormatter.apply(getValue());
    }

    public void setValueFormatter(Function<Boolean, String> displayFormatter) {
        this.displayFormatter = displayFormatter;
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = super.serializeNBT();
        nbt.putBoolean("value", value);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        value = nbt.getBoolean("value");
        super.deserializeNBT(nbt);
    }

    @Override
    public void serializeMCData(MCDataOutput output) {
        super.serializeMCData(output);
        output.writeBoolean(value);
    }

    @Override
    public void deSerializeMCData(MCDataInput input) {
        super.deSerializeMCData(input);
        value = input.readBoolean();
    }
}
