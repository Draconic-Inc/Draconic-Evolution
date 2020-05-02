package com.brandon3055.draconicevolution.api.itemconfig_dep;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

/**
 * Created by brandon3055 on 1/06/2016.
 */
@Deprecated
public class ToolConfigHelper {

    /**
     * Returns the current selected config profile.
     */
    public static int getProfile(ItemStack stack) {
        if (stack.getTag() == null || !stack.getTag().contains("ToolProfile")) {
            return 0;
        }
        return stack.getTag().getByte("ToolProfile");
    }

    public static String getProfileName(ItemStack stack, int profile) {
        if (stack.getTag() == null || !stack.getTag().contains("ToolProfileName" + profile)) {
            return "Profile " + (profile + 1);
        }

        return stack.getTag().getString("ToolProfileName" + profile);
    }

    public static void setProfileName(ItemStack stack, int profile, String name) {
        if (stack.getTag() == null) {
            stack.setTag(new CompoundNBT());
        }

        stack.getTag().putString("ToolProfileName" + profile, name);
    }


    /**
     * Increments the current selected config profile.
     */
    public static void incrementProfile(ItemStack stack) {
        IConfigurableItem item = getTool(stack);
        if (item == null || item.getProfileCount(stack) == 1) {
            return;
        }

        int profile = getProfile(stack);
        profile++;

        if (profile >= item.getProfileCount(stack)) {
            profile = 0;
        }

        if (stack.getTag() == null) {
            stack.setTag(new CompoundNBT());
        }

        stack.getTag().putByte("ToolProfile", (byte) profile);
    }

    /**
     * Returns the compound to which the fields for the currently selected profile are saved.
     */
    public static CompoundNBT getFieldStorage(ItemStack stack) {
        String tag = "Profile_" + getProfile(stack);
        return stack.getOrCreateChildTag(tag);
    }

    /**
     * Returns the value of the specified boolean field.
     * Or false if field is not found.
     */
    public static boolean getBooleanField(String fieldName, ItemStack stack) {
        IConfigurableItem item = getTool(stack);
        if (item == null) {
            return false;
        }

        ItemConfigFieldRegistry fieldRegistry = item.getFields(stack, new ItemConfigFieldRegistry());

        if (fieldRegistry.getField(fieldName) == null) {
            return false;
        }

        IItemConfigField field = fieldRegistry.getField(fieldName);

        if (field instanceof BooleanConfigField) {
            field.readFromNBT(getFieldStorage(stack));
            return (Integer) field.getValue() == 1;
        }

        return false;
    }

    public static boolean setBooleanField(String fieldName, ItemStack stack, boolean value) {
        IConfigurableItem item = getTool(stack);
        if (item == null) {
            return false;
        }

        ItemConfigFieldRegistry fieldRegistry = item.getFields(stack, new ItemConfigFieldRegistry());

        if (fieldRegistry.getField(fieldName) == null) {
            return false;
        }

        IItemConfigField field = fieldRegistry.getField(fieldName);

        if (field instanceof BooleanConfigField) {
            ((BooleanConfigField) field).value = value ? 1 : 0;
            field.writeToNBT(getFieldStorage(stack));
            return true;
        }

        return false;
    }

    /**
     * Returns the value of the specified integer field.
     * Or 0 if field is not found.
     */
    public static int getIntegerField(String fieldName, ItemStack stack) {
        IConfigurableItem item = getTool(stack);
        if (item == null) {
            return 0;
        }

        ItemConfigFieldRegistry fieldRegistry = item.getFields(stack, new ItemConfigFieldRegistry());

        if (fieldRegistry.getField(fieldName) == null) {
            return 0;
        }

        IItemConfigField field = fieldRegistry.getField(fieldName);

        if (field instanceof IntegerConfigField) {
            field.readFromNBT(getFieldStorage(stack));
            return (Integer) field.getValue();
        }

        return 0;
    }

    public static boolean setIntegerField(String fieldName, ItemStack stack, int value) {
        IConfigurableItem item = getTool(stack);
        if (item == null) {
            return false;
        }

        ItemConfigFieldRegistry fieldRegistry = item.getFields(stack, new ItemConfigFieldRegistry());

        if (fieldRegistry.getField(fieldName) == null) {
            return false;
        }

        IItemConfigField field = fieldRegistry.getField(fieldName);

        if (field instanceof IntegerConfigField) {
            ((IntegerConfigField) field).value = value;
            field.writeToNBT(getFieldStorage(stack));
            return true;
        }

        return false;
    }

    /**
     * Returns the value of the specified double field.
     * Or 0 if field is not found.
     */
    public static double getDoubleField(String fieldName, ItemStack stack) {
        IConfigurableItem item = getTool(stack);
        if (item == null) {
            return 0;
        }

        ItemConfigFieldRegistry fieldRegistry = item.getFields(stack, new ItemConfigFieldRegistry());

        if (fieldRegistry.getField(fieldName) == null) {
            return 0;
        }

        IItemConfigField field = fieldRegistry.getField(fieldName);

        if (field instanceof DoubleConfigField) {
            field.readFromNBT(getFieldStorage(stack));
            return (Double) field.getValue();
        }

        return 0;
    }

    public static boolean setDoubleField(String fieldName, ItemStack stack, double value) {
        IConfigurableItem item = getTool(stack);
        if (item == null) {
            return false;
        }

        ItemConfigFieldRegistry fieldRegistry = item.getFields(stack, new ItemConfigFieldRegistry());

        if (fieldRegistry.getField(fieldName) == null) {
            return false;
        }

        IItemConfigField field = fieldRegistry.getField(fieldName);

        if (field instanceof DoubleConfigField) {
            ((DoubleConfigField) field).value = value;
            field.writeToNBT(getFieldStorage(stack));
            return true;
        }

        return false;
    }

    private static IConfigurableItem getTool(ItemStack stack) {
        if (!(stack.getItem() instanceof IConfigurableItem)) {
            return null;
        }

        return (IConfigurableItem) stack.getItem();
    }
}
