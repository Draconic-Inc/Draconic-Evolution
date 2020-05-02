package com.brandon3055.draconicevolution.api.itemconfig_dep;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by brandon3055 on 8/06/2016.
 */
@Deprecated
public class AOEConfigField extends IntegerConfigField {

    public AOEConfigField(String name, int value, int minValue, int maxValue, String description) {
        super(name, value, minValue, maxValue, description, EnumControlType.SELECTIONS);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public String getReadableValue() {
        return (1 + (value * 2)) + "x" + (1 + (value * 2));
    }

    @Override
    public Map<Integer, String> getValues() {
        Map<Integer, String> values = new LinkedHashMap<Integer, String>();

        for (int i = minValue; i <= maxValue; i++) {
            String aoe = (1 + (i * 2)) + "x" + (1 + (i * 2));
            values.put(i, aoe);
        }

        return values;
    }

    @Override
    public void handleButton(EnumButton button, int data) {
        if (button == EnumButton.SELECTION) {
            value = data;
            if (value > maxValue) {
                value = maxValue;
            }
            else if (value < minValue) {
                value = minValue;
            }
        }
    }
}
