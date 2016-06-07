package com.brandon3055.draconicevolution.api.itemconfig;

import com.brandon3055.brandonscore.lib.PairKV;
import net.minecraft.nbt.NBTTagCompound;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by brandon3055 on 1/06/2016.
 * A basic integer config field. Can be extended by any other field that stores its state as an integer.
 */
public class IntegerConfigField implements IItemConfigField {
    protected final String name;
    protected String description;
    protected int minValue;
    protected int maxValue;
    protected int value;

    public IntegerConfigField(String name, int value, int minValue, int maxValue, String description) {
        this.name = name;
        this.value = value;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.description = description;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getUnlocalizedName() {
        return "field.de." + getName() + ".txt";
    }

    @Override
    public String getReadableValue() {
        return String.valueOf(value);
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public Integer getMin() {
        return minValue;
    }

    @Override
    public Integer getMax() {
        return maxValue;
    }

    @Override
    public Integer getValue() {
        return value;
    }

    @Override
    public void increment(int multiplier) {//TODO Multiplier
        value++;
        if (value > maxValue){
            value = maxValue;
        }
    }

    @Override
    public void decrement(int multiplier) {//TODO Multiplier
        value--;
        if (value < minValue){
            value = minValue;
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        compound.setInteger(name, value);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        value = compound.getInteger(name);
    }

    @Override
    public ControlType getType() {
        return ControlType.PPP_MMM;
    }

    @Override
    public Collection<PairKV<String, Number>> getValues() {
        List<PairKV<String, Number>> list = new ArrayList<PairKV<String, Number>>();
        return list;
    }

    @Override
    public void setValue(PairKV<String, Number> value) {
    }
}
