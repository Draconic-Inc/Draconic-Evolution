package com.brandon3055.draconicevolution.items.tools.util;

import com.brandon3055.brandonscore.lib.PairKV;
import com.brandon3055.draconicevolution.api.itemconfig.IntegerConfigField;
import com.brandon3055.draconicevolution.utils.LogHelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by brandon3055 on 8/06/2016.
 */
public class AOEConfigField extends IntegerConfigField {

    public AOEConfigField(String name, int value, int minValue, int maxValue, String description) {
        super(name, value, minValue, maxValue, description);
    }

    @Override
    public ControlType getType() {
        return ControlType.SELECTIONS;
    }

    @Override
    public Collection<PairKV<String, Number>> getValues() {
        List<PairKV<String, Number>> values = new ArrayList<PairKV<String, Number>>();

        for (int i = minValue; i < maxValue; i++){
            String aoe = (1 + (i * 2)) + "x" + (1 + (i * 2));
            values.add(new PairKV<String, Number>(aoe, i));
        }

        return values;
    }

    @Override
    public void setValue(PairKV<String, Number> value) {
        if (!(value.getValue() instanceof Integer)){
            LogHelper.bigError("[API] AOEConfigField#setValue Hay WTF are you doing? Thats supposed to be an integer! FIX IT NOW!");
            return;
        }

        super.setValue(value);
    }
}
