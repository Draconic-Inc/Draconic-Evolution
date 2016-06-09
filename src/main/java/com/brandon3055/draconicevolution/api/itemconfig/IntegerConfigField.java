package com.brandon3055.draconicevolution.api.itemconfig;

import net.minecraft.nbt.NBTTagCompound;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by brandon3055 on 1/06/2016.
 * A basic integer config field. Can be extended by any other field that stores its state as an integer.
 */
public class IntegerConfigField implements IItemConfigField {
    protected final String name;
    protected String description;
    private EnumControlType controlType;
    protected int minValue;
    protected int maxValue;
    protected int value;
    private String extension = "";

    /**
     * @param name the name of the field
     * @param defaultValue the default defaultValue of the field
     * @param description a description for this field. This will be passed through I18n so it can be a localization key.
     * @param controlType Valid control types are PLUS_MINUS 1, 2 or 3, SLIDER or SELECTION
     */
    public IntegerConfigField(String name, int defaultValue, int minValue, int maxValue, String description, EnumControlType controlType) {
        this.name = name;
        this.value = defaultValue;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.description = description;
        this.controlType = controlType;
    }

    public IntegerConfigField setExtension(String extension) {
        this.extension = extension;
        return this;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getUnlocalizedName() {
        return "gui.config.field." + getName() + ".entry";
    }

    @Override
    public String getReadableValue() {
        return String.valueOf(value) + extension;
    }

    @Override
    public String getValueFraction(double percent) {
        return String.valueOf((int)(minValue + (percent * (maxValue - minValue)))) + extension;
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
    public double getFractionalValue() {
        return (double)(value - minValue) / (double)(maxValue - minValue);
    }

    @Override
    public void handleButton(EnumButton button, int data) {
        switch (button){
            case MINUS1:
                value--;
                break;
            case MINUS2:
                value -= 10;
                break;
            case MINUS3:
                value -= 100;
                break;
            case PLUS1:
                value++;
                break;
            case PLUS2:
                value += 10;
                break;
            case PLUS3:
                value += 100;
                break;
            case MIN:
                value = minValue;
                break;
            case MAX:
                value = maxValue;
                break;
            case SELECTION:
                value = data;
                break;
            case SLIDER:
                double range = maxValue - minValue;
                double pos = (data / 10000D) * range;
                value = (int)(minValue + pos);
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
        compound.setInteger(name, value);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        value = compound.getInteger(name);
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
        Map<Integer, String> list = new LinkedHashMap<Integer, String>();

        if (controlType == EnumControlType.SELECTIONS){
            for (int i = minValue; i <= maxValue; i++){
                list.put(i, i + extension);
            }
        }

        return list;
    }

}
