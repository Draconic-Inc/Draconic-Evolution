package com.brandon3055.draconicevolution.api.modules.data;

import com.brandon3055.draconicevolution.api.modules.lib.ModuleContext;
import net.minecraft.network.chat.Component;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * Created by brandon3055 on 3/5/20.
 */
public interface ModuleData<D extends ModuleData<D>> {

    /**
     * All {@link ModuleData} objects are immutable.
     * This method takes another {@link ModuleData} object and returns a new {@link ModuleData}
     * that is the combination of this module and the other module.
     *
     * @param other the other data instance to combine with.
     * @return a new data object that os the combination of this and other
     */
    D combine(D other);

    /**
     * use this to supply human readable information about this data<br>
     * The map is of Property Name to Property Value
     *
     * @param map the map to which this information should be added.
     * @param context The current module context.
     * @param stack True when adding information to item tool tip.
     */
    void addInformation(Map<Component, Component> map, @Nullable ModuleContext context, boolean stack);


    /**
     * This is just a helper method for rounding numbers.
     */
    static double round(double number, double multiplier) {
        return Math.round(number * multiplier) / multiplier;
    }

    static String formatNumber(long value) {
        if (value < 1000000L) return String.valueOf(value);
        else if (value < 1000000000L) return Math.round(value / 1000D) / 1000D + "M";
        else if (value < 1000000000000L) return Math.round(value / 1000000D) / 1000D + "G";
        else if (value < 1000000000000000L) return Math.round(value / 1000000000D) / 1000D + "T";
        else if (value < 1000000000000000000L) return Math.round(value / 1000000000000D) / 1000D + "P";
        else return Math.round(value / 1000000000000000D) / 1000D + "E";
    }
}
