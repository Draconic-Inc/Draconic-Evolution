package com.brandon3055.draconicevolution.api.itemconfig;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by brandon3055 on 1/06/2016.
 * A Double config field for storing floating point values
 */
public class DoubleConfigField implements IItemConfigField {
    protected final String name;
    protected String description;
    private EnumControlType controlType;
    protected double minValue;
    protected double maxValue;
    protected double value;
    private String extension = "";

    /**
     * @param name the name of the field
     * @param defaultValue the default defaultValue of the field
     * @param description a description for this field. This will be passed through I18n so it can be a localization key.
     * @param controlType Valid control types are PLUS_MINUS 1, 2 or 3 and SLIDER
     */
    public DoubleConfigField(String name, double defaultValue, double minValue, double maxValue, String description, EnumControlType controlType) {
        this.name = name;
        this.value = defaultValue;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.description = description;
        this.controlType = controlType;
    }

    public DoubleConfigField setExtension(String extension) {
        this.extension = extension;
        return this;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getUnlocalizedName() {
        return "config.field." + getName() + ".entry";
    }

    @SideOnly(Side.CLIENT)
    @Override
    public String getReadableValue() {
        return String.valueOf(Math.round(value * 100) / 100D) + extension;
    }

    @Override
    public String getValueFraction(double percent) {
        return String.valueOf(Math.round(minValue + (percent * (maxValue - minValue)) * 100) / 100D) + extension;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public Double getMin() {
        return minValue;
    }

    @Override
    public Double getMax() {
        return maxValue;
    }

    @Override
    public Double getValue() {
        return value;
    }

    @Override
    public double getFractionalValue() {
        return (value - minValue) / (maxValue - minValue);
    }

    @Override
    public void handleButton(EnumButton button, int data) {
        switch (button){
            case MINUS1:
                value -= 0.01;
                break;
            case MINUS2:
                value -= 0.1;
                break;
            case MINUS3:
                value -= 1;
                break;
            case PLUS1:
                value += 0.01;
                break;
            case PLUS2:
                value += 0.1;
                break;
            case PLUS3:
                value += 1;
                break;
            case MIN:
                value = minValue;
                break;
            case MAX:
                value = maxValue;
                break;
            case SLIDER:
                double range = maxValue - minValue;
                double pos = (data / 10000D) * range;
                value = minValue + pos;
                break;
        }

        if (value > maxValue) {
            value = maxValue;
        }
        else if (value < minValue){
            value = minValue;
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        compound.setDouble(name, value);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        value = compound.getDouble(name);
        if (value > maxValue){
            value = maxValue;
            writeToNBT(compound);
        }
        else if (value < minValue){
            value = minValue;
            writeToNBT(compound);
        }
    }

    @Override
    public EnumControlType getType() {
        return controlType;
    }

    @Override
    public Map<Integer, String> getValues() {
        return new HashMap<Integer, String>();
    }
}
