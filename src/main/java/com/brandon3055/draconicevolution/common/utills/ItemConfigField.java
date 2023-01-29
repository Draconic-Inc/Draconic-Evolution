package com.brandon3055.draconicevolution.common.utills;

import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraft.util.StringUtils;

import com.brandon3055.brandonscore.common.utills.DataUtills;
import com.brandon3055.brandonscore.common.utills.InfoHelper;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.common.lib.References;
import com.brandon3055.draconicevolution.common.network.ItemConfigPacket;

/**
 * Created by Brandon on 29/12/2014.
 */
public class ItemConfigField {

    public Object value;
    public int slot;
    public int datatype;
    public String name;
    public int fieldid;
    public Object max;
    public Object min;
    public Object incroment;
    public String modifier;

    public ItemConfigField(int datatype, int slot, String name) {
        this.slot = slot;
        this.datatype = datatype;
        this.name = name;
    }

    public ItemConfigField(int datatype, Object value, int slot, String name) {
        this.value = value;
        this.slot = slot;
        this.datatype = datatype;
        this.name = name;
    }

    public ItemConfigField setMinMaxAndIncromente(Object min, Object max, Object incroment) {
        this.max = max;
        this.min = min;
        this.incroment = incroment;
        return this;
    }

    public String getLocalizedName() {
        return StatCollector.translateToLocal("button.de." + name + ".name");
    }

    // public void writeToItem(ItemStack stack){
    // DataUtills.writeObjectToItem(stack, value, datatype, name);
    // }

    public ItemConfigField readFromItem(ItemStack stack, Object defaultExpected) {
        value = DataUtills.readObjectFromCompound(
                IConfigurableItem.ProfileHelper.getProfileCompound(stack),
                datatype,
                name,
                defaultExpected);
        return this;
    }

    public ItemConfigField setModifier(String modifier) {
        this.modifier = modifier;
        return this;
    }

    public String getFormattedValue() {
        if (datatype == References.INT_ID && !StringUtils.isNullOrEmpty(modifier) && modifier.equals("AOE")) {
            int i = (Integer) value;
            i *= 2;
            return String.valueOf((i + 1) + "x" + (i + 1));
        } else if (datatype == References.BOOLEAN_ID) {
            return (Boolean) value ? StatCollector.translateToLocal("gui.de.on.txt")
                    : StatCollector.translateToLocal("gui.de.off.txt");
        } else
            if (datatype == References.FLOAT_ID && !StringUtils.isNullOrEmpty(modifier) && modifier.equals("PERCENT")) {
                return Math.round((Float) value * 100D) + "%";
            } else if (datatype == References.FLOAT_ID && !StringUtils.isNullOrEmpty(modifier)
                    && modifier.equals("PLUSPERCENT")) {
                        return "+" + Math.round((Float) value * 100D) + "%";
                    } else {
                        return String.valueOf(value);
                    }
    }

    public String getMaxFormattedValue() {
        if (datatype == References.INT_ID && !StringUtils.isNullOrEmpty(modifier) && modifier.equals("AOE")) {
            int i = (Integer) max;
            i *= 2;
            return String.valueOf((i + 1) + "x" + (i + 1));
        } else {
            return String.valueOf(max);
        }
    }

    public String getTooltipInfo() {
        return InfoHelper.ITC() + getLocalizedName() + ": " + InfoHelper.HITC() + getFormattedValue();
    }

    public void sendChanges() {
        DraconicEvolution.network.sendToServer(new ItemConfigPacket(this));
    }

    public int castToInt() {
        switch (datatype) {
            case References.BYTE_ID:
                return (int) (Byte) value;
            case References.SHORT_ID:
                return (int) (Short) value;
            case References.INT_ID:
                return (Integer) value;
            case References.LONG_ID:
                long l = (Long) value;
                return (int) l;
            case References.FLOAT_ID:
                float f = (Float) value;
                return (int) f;
            case References.DOUBLE_ID:
                double d = (Double) value;
                return (int) d;
            case References.BOOLEAN_ID:
                return (Boolean) value ? 1 : 0;
        }
        return 0;
    }

    public double castToDouble() {
        switch (datatype) {
            case References.BYTE_ID:
                return (double) (Byte) value;
            case References.SHORT_ID:
                return (double) (Short) value;
            case References.INT_ID:
                return (double) (Integer) value;
            case References.LONG_ID:
                long l = (Long) value;
                return (double) l;
            case References.FLOAT_ID:
                float f = (Float) value;
                return (double) f;
            case References.DOUBLE_ID:
                return (Double) value;
            case References.BOOLEAN_ID:
                return (Boolean) value ? 1D : 0D;
        }
        return 0D;
    }

    public double castMinToDouble() {
        switch (datatype) {
            case References.BYTE_ID:
                return (double) (Byte) min;
            case References.SHORT_ID:
                return (double) (Short) min;
            case References.INT_ID:
                return (double) (Integer) min;
            case References.LONG_ID:
                long l = (Long) min;
                return (double) l;
            case References.FLOAT_ID:
                float f = (Float) min;
                return (double) f;
            case References.DOUBLE_ID:
                return (Double) min;
            case References.BOOLEAN_ID:
                return 0D;
        }
        return 0D;
    }

    public double castMaxToDouble() {
        switch (datatype) {
            case References.BYTE_ID:
                return (double) (Byte) max;
            case References.SHORT_ID:
                return (double) (Short) max;
            case References.INT_ID:
                return (double) (Integer) max;
            case References.LONG_ID:
                long l = (Long) max;
                return (double) l;
            case References.FLOAT_ID:
                float f = (Float) max;
                return (double) f;
            case References.DOUBLE_ID:
                return (Double) max;
            case References.BOOLEAN_ID:
                return 1D;
        }
        return 0D;
    }
}
