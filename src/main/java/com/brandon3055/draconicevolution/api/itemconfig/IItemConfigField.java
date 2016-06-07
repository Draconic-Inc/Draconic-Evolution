package com.brandon3055.draconicevolution.api.itemconfig;

import com.brandon3055.brandonscore.lib.PairKV;
import net.minecraft.nbt.NBTTagCompound;

import java.util.Collection;

/**
 * Created by brandon3055 on 7/06/2016.
 */
public interface IItemConfigField {

    String getName();

    /**
     * The unlocalized display name for the field. Will be translated using I18n
     * */
    String getUnlocalizedName();

    String getReadableValue();

    /**
     * The unlocalized description for the field. Will be translated using I18n
     * */
    String getDescription();

    Object getMin();

    Object getMax();

    Object getValue();

    void increment(int multiplier);

    void decrement(int multiplier);

    void writeToNBT(NBTTagCompound compound);

    void readFromNBT(NBTTagCompound compound);

    ControlType getType();

    /**
     * Currently only used for control type SELECTIONS. Should return a list of valid selections for the field.
     * Each selection is a PairKV<Display Name, Value>.
     * Display name will be used as the display for the selector. Note this will be passed through I18n so it can
     * be an unlocalized name.
     * */
    Collection<PairKV<String, Number>> getValues();

    void setValue(PairKV<String, Number> value);

    public enum ControlType {
        /**Plus - Minus*/
        P_M,
        /**Plus Plus - Minus Minus*/
        PP_MM,
        /**Plus Plus Plus - Minus Minus Minus*/
        PPP_MMM,
        /**A simple slider between min and max*/
        SLIDER,
        /**Lets you specify a list of predefined values to choose from*/
        SELECTIONS,
    }
}
