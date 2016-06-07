package com.brandon3055.draconicevolution.api.itemconfig;

import com.brandon3055.brandonscore.lib.PairKV;
import com.brandon3055.draconicevolution.utils.LogHelper;
import net.minecraft.nbt.NBTTagCompound;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by brandon3055 on 1/06/2016.
 * A simple boolean config field based on the integer config field
 */
public class BooleanConfigField extends IntegerConfigField {

    public BooleanConfigField(String name, Boolean value, String description) {
        super(name, value ? 1 : 0, 0, 1, description);
    }

    @Override
    public String getReadableValue() {
        return "gui.de." + (value == 1 ? "on" : "off") + ".txt";
    }


    @Override
    public void writeToNBT(NBTTagCompound compound) {
        compound.setBoolean(name, value == 1);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        value = compound.getBoolean(name) ? 1 : 0;
    }

    @Override
    public ControlType getType() {
        return ControlType.SELECTIONS;
    }

    @Override
    public Collection<PairKV<String, Number>> getValues() {
        List<PairKV<String, Number>> list = new ArrayList<PairKV<String, Number>>();
        list.add(new PairKV<String, Number>("gui.de.on.txt", 1));
        list.add(new PairKV<String, Number>("gui.de.off.txt", 0));
        return list;
    }

    @Override
    public void setValue(PairKV<String, Number> value) {
        if (!(value.getValue() instanceof Integer)){
            this.value = 0;
            LogHelper.bigError("[API] BooleanConfigField#setValue value given is not an instance of Integer!");
            return;
        }

        this.value = (Integer) value.getValue();
    }
}
