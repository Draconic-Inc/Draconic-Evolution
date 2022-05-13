package com.brandon3055.draconicevolution.api.config;

import codechicken.lib.data.MCDataInput;
import codechicken.lib.data.MCDataOutput;
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
public class BooleanProperty extends ConfigProperty {
    private boolean value;
    private boolean defaultValue;
    private BooleanFormatter formatter = BooleanFormatter.TRUE_FALSE;
    private Supplier<Boolean> valueOverride;
    private BiConsumer<ItemStack, BooleanProperty> changeListener;

    public BooleanProperty(String name, boolean defaultValue) {
        super(name);
        this.value = this.defaultValue = defaultValue;
    }

    public BooleanProperty(String name, Component displayName, boolean defaultValue) {
        super(name, displayName);
        this.value = this.defaultValue = defaultValue;
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
        if (changeListener != null) {
            changeListener.accept(stack, this);
        }
    }

    @Override
    public void validateValue() {}

    @Override
    public Type getType() {
        return Type.BOOLEAN;
    }

    public void setChangeListener(Runnable changeListener) {
        this.changeListener = (stack, t) -> changeListener.run();
    }

    public void setChangeListener(Consumer<ItemStack> changeListener) {
        this.changeListener = (stack, booleanProperty) -> changeListener.accept(stack);
    }

    public void setChangeListener(BiConsumer<ItemStack, BooleanProperty> changeListener) {
        this.changeListener = changeListener;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = super.serializeNBT();
        if (this.value != this.defaultValue) {
            nbt.putBoolean("value", value);
        }
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        if (nbt.contains("value")) {
            value = nbt.getBoolean("value");
        }
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

    @Override
    public void loadData(PropertyData data, ItemStack stack) {
        value = data.booleanValue;
        onValueChanged(stack);
    }
}
