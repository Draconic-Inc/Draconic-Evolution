package com.brandon3055.draconicevolution.common.utills;

import com.brandon3055.brandonscore.common.utills.ItemNBTHelper;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

/**
 * Created by Brandon on 29/12/2014.
 */
public interface IConfigurableItem {
    List<ItemConfigField> getFields(ItemStack stack, int slot);

    public boolean hasProfiles();

    public static class ProfileHelper {

        public static NBTTagCompound getProfileCompound(ItemStack stack) {
            int profile = ItemNBTHelper.getInteger(stack, "ConfigProfile", 0);
            NBTTagCompound stackCompound = ItemNBTHelper.getCompound(stack);
            if (!stackCompound.hasKey("ConfigProfiles")
                    && stackCompound.getTagList("ConfigProfiles", 10).tagCount() < 5) {
                NBTTagList profileList = new NBTTagList();
                for (int i = 0; i < 5; i++) profileList.appendTag(new NBTTagCompound());
                stackCompound.setTag("ConfigProfiles", profileList);
            }

            return stackCompound.getTagList("ConfigProfiles", 10).getCompoundTagAt(profile);
        }

        public static void setBoolean(ItemStack stack, String tag, boolean b) {
            getProfileCompound(stack).setBoolean(tag, b);
        }

        public static void setInteger(ItemStack stack, String tag, int i) {
            getProfileCompound(stack).setInteger(tag, i);
        }

        public static void setFloat(ItemStack stack, String tag, float f) {
            getProfileCompound(stack).setFloat(tag, f);
        }

        public static void setDouble(ItemStack stack, String tag, double d) {
            getProfileCompound(stack).setDouble(tag, d);
        }

        public static void setString(ItemStack stack, String tag, String s) {
            getProfileCompound(stack).setString(tag, s);
        }

        public static boolean getBoolean(ItemStack stack, String tag, boolean defaultExpected) {
            return getProfileCompound(stack).hasKey(tag)
                    ? getProfileCompound(stack).getBoolean(tag)
                    : defaultExpected;
        }

        public static int getInteger(ItemStack stack, String tag, int defaultExpected) {
            return getProfileCompound(stack).hasKey(tag)
                    ? getProfileCompound(stack).getInteger(tag)
                    : defaultExpected;
        }

        public static float getFloat(ItemStack stack, String tag, float defaultExpected) {
            return getProfileCompound(stack).hasKey(tag)
                    ? getProfileCompound(stack).getFloat(tag)
                    : defaultExpected;
        }

        public static double getDouble(ItemStack stack, String tag, double defaultExpected) {
            return getProfileCompound(stack).hasKey(tag)
                    ? getProfileCompound(stack).getDouble(tag)
                    : defaultExpected;
        }

        public static String getString(ItemStack stack, String tag, String defaultExpected) {
            return getProfileCompound(stack).hasKey(tag)
                    ? getProfileCompound(stack).getString(tag)
                    : defaultExpected;
        }
    }
}
