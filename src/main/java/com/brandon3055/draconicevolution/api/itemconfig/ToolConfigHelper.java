package com.brandon3055.draconicevolution.api.itemconfig;

import com.brandon3055.brandonscore.utils.ItemNBTHelper;
import com.brandon3055.draconicevolution.api.itemconfig.*;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by brandon3055 on 1/06/2016.
 */
public class ToolConfigHelper {

    /**
     * Returns the current selected config profile.
     * */
    public static int getProfile(ItemStack stack){
        return ItemNBTHelper.getByte(stack, "ToolProfile", (byte)0);
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

        ItemNBTHelper.setByte(stack, "ToolProfile", (byte)profile);
    }

    /**
     * Returns the compound to which the fields for the currently selected profile are saved.
     * */
    public static NBTTagCompound getFieldTag(ItemStack stack){
        String tag = "Profile_"+getProfile(stack);
        NBTTagCompound stackCompound = ItemNBTHelper.getCompound(stack);
        if (!stackCompound.hasKey(tag)){
            stackCompound.setTag(tag, new NBTTagCompound());
        }
        return stackCompound.getCompoundTag(tag);
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

        Map<String, IItemConfigField> fieldMap = item.getFields(stack, new HashMap<String, IItemConfigField>());

        if (!fieldMap.containsKey(fieldName)){
            return false;
        }

        IItemConfigField field = fieldMap.get(fieldName);

        if (field instanceof BooleanConfigField){
            field.readFromNBT(getFieldTag(stack));
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

        Map<String, IItemConfigField> fieldMap = item.getFields(stack, new HashMap<String, IItemConfigField>());

        if (!fieldMap.containsKey(fieldName)){
            return 0;
        }

        IItemConfigField field = fieldMap.get(fieldName);

        if (field instanceof IntegerConfigField){
            field.readFromNBT(getFieldTag(stack));
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

        Map<String, IItemConfigField> fieldMap = item.getFields(stack, new HashMap<String, IItemConfigField>());

        if (!fieldMap.containsKey(fieldName)){
            return 0;
        }

        IItemConfigField field = fieldMap.get(fieldName);

        if (field instanceof DoubleConfigField){
            field.readFromNBT(getFieldTag(stack));
            return (Double) field.getValue();
        }

        return 0;
    }

    private static IConfigurableItem getTool(ItemStack stack){
        if (stack == null || !(stack.getItem() instanceof IConfigurableItem)){
            return null;
        }

        return (IConfigurableItem) stack.getItem();
    }
}
