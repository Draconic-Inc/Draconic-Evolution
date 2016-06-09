package com.brandon3055.draconicevolution.api.itemconfig;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Created by brandon3055 on 1/06/2016.
 */
public class ToolConfigHelper {

    /**
     * Returns the current selected config profile.
     * */
    public static int getProfile(ItemStack stack){
        if (stack.getTagCompound() == null || !stack.getTagCompound().hasKey("ToolProfile")){
            return 0;
        }
        return stack.getTagCompound().getByte("ToolProfile");
    }

    /**
     * Increments the current selected config profile.
     * */
    public static void incrementProfile(ItemStack stack){
        IConfigurableItem item = getTool(stack);
        if (item == null || item.getProfileCount(stack) == 1){
            return;
        }

        int profile = getProfile(stack);
        profile++;

        if (profile >= item.getProfileCount(stack)){
            profile = 0;
        }

        if (stack.getTagCompound() == null){
            stack.setTagCompound(new NBTTagCompound());
        }

        stack.getTagCompound().setByte("ToolProfile", (byte) profile);
    }

    /**
     * Returns the compound to which the fields for the currently selected profile are saved.
     * */
    public static NBTTagCompound getFieldStorage(ItemStack stack){
        String tag = "Profile_"+getProfile(stack);
        return stack.getSubCompound(tag, true);
    }

    /**
     * Returns the value of the specified boolean field.
     * Or false if field is not found.
     * */
    public static boolean getBooleanField(String fieldName, ItemStack stack){
        IConfigurableItem item = getTool(stack);
        if (item == null){
            return false;
        }

        ItemConfigFieldRegistry fieldRegistry = item.getFields(stack, new ItemConfigFieldRegistry());

        if (fieldRegistry.getField(fieldName) == null){
            return false;
        }

        IItemConfigField field = fieldRegistry.getField(fieldName);

        if (field instanceof BooleanConfigField){
            field.readFromNBT(getFieldStorage(stack));
            return (Integer)field.getValue() == 1;
        }

        return false;
    }

    /**
     * Returns the value of the specified integer field.
     * Or 0 if field is not found.
     * */
    public static int getIntegerField(String fieldName, ItemStack stack){
        IConfigurableItem item = getTool(stack);
        if (item == null){
            return 0;
        }

        ItemConfigFieldRegistry fieldRegistry = item.getFields(stack, new ItemConfigFieldRegistry());

        if (fieldRegistry.getField(fieldName) == null){
            return 0;
        }

        IItemConfigField field = fieldRegistry.getField(fieldName);

        if (field instanceof IntegerConfigField){
            field.readFromNBT(getFieldStorage(stack));
            return (Integer) field.getValue();
        }

        return 0;
    }

    /**
     * Returns the value of the specified double field.
     * Or 0 if field is not found.
     * */
    public static double getDoubleField(String fieldName, ItemStack stack){
        IConfigurableItem item = getTool(stack);
        if (item == null){
            return 0;
        }

        ItemConfigFieldRegistry fieldRegistry = item.getFields(stack, new ItemConfigFieldRegistry());

        if (fieldRegistry.getField(fieldName) == null){
            return 0;
        }

        IItemConfigField field = fieldRegistry.getField(fieldName);

        if (field instanceof DoubleConfigField){
            field.readFromNBT(getFieldStorage(stack));
            return (Double) field.getValue();
        }

        return 0;
    }

    private static IConfigurableItem getTool(ItemStack stack) {
        if (stack == null || !(stack.getItem() instanceof IConfigurableItem)){
            return null;
        }

        return (IConfigurableItem) stack.getItem();
    }
}
