package com.brandon3055.draconicevolution.api.itemconfig;

import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Created by brandon3055 on 1/06/2016.
 * A simple boolean config field based on the integer config field
 */
public class BooleanConfigField extends IntegerConfigField {

    private String onTxt = "config.field.on.state";
    private String offTxt = "config.field.off.state";

    public BooleanConfigField(String name, Boolean value, String description) {
        super(name, value ? 1 : 0, 0, 1, description, EnumControlType.TOGGLE);
    }

    public BooleanConfigField setOnOffTxt(String onTxt, String offTxt) {
        this.onTxt = onTxt;
        this.offTxt = offTxt;
        return this;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public String getReadableValue() {
        return getValue() == 1 ? I18n.format(onTxt) : I18n.format(offTxt);
    }

    @Override
    public void writeToNBT(CompoundNBT compound) {
        compound.putBoolean(name, value == 1);
    }

    @Override
    public void readFromNBT(CompoundNBT compound) {
        value = compound.getBoolean(name) ? 1 : 0;
    }

    @Override
    public void handleButton(EnumButton button, int data) {
        value = value == 0 ? 1 : 0;
    }
}
