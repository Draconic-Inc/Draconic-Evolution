package com.brandon3055.draconicevolution.api.itemconfig;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Map;

/**
 * Created by brandon3055 on 7/06/2016.
 */
public interface IItemConfigField {

    /**
     * Returns the name of the field. This will be used to reference the field in code.
     */
    String getName();

    /**
     * The unlocalized display name for the field. Will be translated using I18n.
     */
    String getUnlocalizedName();

    /**
     * Warning this is client side! Remember to keep that in mind when implementing and don't forget the @SideOnly
     * Returns the field value formatted for display to the user.
     */
    @SideOnly(Side.CLIENT)
//Because they had to go and make life complicated by moving localization client side!
    String getReadableValue();

    /**
     * Used for the slider control type to display the current value while sliding.
     * Should return a value between min and max based on the input which ranges from 0 to 1.
     */
    String getValueFraction(double percent);

    /**
     * The unlocalized description for the field. Will be translated using I18n
     */
    String getDescription();

    /**
     * Returns the minimum value for this field.
     */
    Object getMin();

    /**
     * Returns the maxim value for this field.
     */
    Object getMax();

    /**
     * Returns the value of this field.
     */
    Object getValue();

    /**
     * Should return a value between 0 and 1 depending in the current value of the field.
     */
    double getFractionalValue();

    /**
     * Called to handle a button press inside the config GUI. Note: This is called server side so you dont need
     * to worry about client -> server synchronization.
     *
     * @param button The button that was pressed.
     * @param data   Data associated with the button if applicable (See the doc for each button in EnumButton for details)
     */
    void handleButton(EnumButton button, int data);

    /**
     * Write this fields value to the given NBTTagCompound with the fields name as the key.
     */
    void writeToNBT(NBTTagCompound compound);

    /**
     * Read this fields value from the given NBTTagCompound with the fields name as the key.
     */
    void readFromNBT(NBTTagCompound compound);

    /**
     * Return the control type for this field.
     * This determines how the user will configure the field. It also effects how you need to handle the button events
     * because different control types will return different buttons to the handleButton method.
     * See EnumControlType for more details on the different control types.
     */
    EnumControlType getType();

    /**
     * Currently only used for control type SELECTIONS. Should return a map of valid selections for the field.
     * Each map entry should be an index which is the index of the selection and a display name.
     * Display name will be used as the display for the selector. Note this will be passed through I18n so it can
     * be an unlocalized name.
     * Note: Even if your not using this feature you should return an empty map rather then null for safety.
     * Also Note: If you want your values to be presented in the order you add them to the map use a LinkedHashMap
     */
    Map<Integer, String> getValues();

    public enum EnumControlType {
        /**
         * Has a single plus and a single minus button. As well as a max and min button
         */
        PLUS1_MINUS1, /**
         * Has 2 plus and 2 minus buttons. As well as a max and min button
         */
        PLUS2_MINUS2, /**
         * Has 3 plus and 3 minus buttons. As well as a max and min button
         */
        PLUS3_MINUS3, /**
         * Has a single plus and a single minus button. As well as a max and min button
         */
        SLIDER, /**
         * Lets you specify a list of predefined values to choose from
         */
        SELECTIONS, /**
         * Should be used for Boolean type fields.
         */
        TOGGLE
    }

    public enum EnumButton {
        /**
         * data field is not relevant for this button.
         */
        PLUS1(0), /**
         * data field is not relevant for this button.
         */
        PLUS2(1), /**
         * data field is not relevant for this button.
         */
        PLUS3(2), /**
         * data field is not relevant for this button.
         */
        MAX(3), /**
         * data field is not relevant for this button.
         */
        MINUS1(4), /**
         * data field is not relevant for this button.
         */
        MINUS2(5), /**
         * data field is not relevant for this button.
         */
        MINUS3(6), /**
         * data field is not relevant for this button.
         */
        MIN(7), /**
         * data field given to the handle button method will be the slider position.
         * Range from 0 to 10000 depending on the slider position.
         */
        SLIDER(8), /**
         * data field given to the handle button method will the index of the value selected.
         */
        SELECTION(9), /**
         * Used for boolean fields. data field is not reinvent for this button.
         */
        TOGGLE(10);

        public final int index;
        private static final EnumButton[] buttons;

        private EnumButton(int index) {
            this.index = index;
        }

        public static EnumButton getButton(int index) {
            if (index < 0 || index >= buttons.length) {
                FMLLog.bigWarning("[DraconicEvolution - API] EnumButton#getButton Attempt to get button for invalid index! [%s]", index);
                return TOGGLE;
            }
            return buttons[index];
        }

        static {
            buttons = new EnumButton[values().length];
            for (EnumButton button : values()) {
                buttons[button.index] = button;
            }
        }
    }
}
