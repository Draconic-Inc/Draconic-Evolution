package draconicevolution.common.core.helper;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;


public final class ItemNBTHelper {



	// SETTERS ///////////////////////////////////////////////////////////////////
	public static NBTTagCompound getCompound(ItemStack stack){
		if (stack.getTagCompound() == null)
			return new NBTTagCompound();
		else
			return stack.getTagCompound();	
	}
	
	public static void setBoolean(ItemStack stack, String tag, boolean b)
	{
		NBTTagCompound compound = getCompound(stack);
		compound.setBoolean(tag, b);
		stack.setTagCompound(compound);
	}
	
	public static void setShort(ItemStack stack, String tag, short s)
	{
		NBTTagCompound compound = getCompound(stack);
		compound.setShort(tag, s);
		stack.setTagCompound(compound);
	}
	
	public static void setIntager(ItemStack stack, String tag, int i)
	{
		NBTTagCompound compound = getCompound(stack);
		compound.setInteger(tag, i);
		stack.setTagCompound(compound);
	}
	
	public static void setFloat(ItemStack stack, String tag, float f)
	{
		NBTTagCompound compound = getCompound(stack);
		compound.setFloat(tag, f);
		stack.setTagCompound(compound);
	}
	
	public static void setDouble(ItemStack stack, String tag, double d)
	{
		NBTTagCompound compound = getCompound(stack);
		compound.setDouble(tag, d);
		stack.setTagCompound(compound);
	}
	
	public static void setString(ItemStack stack, String tag, String s) {
		NBTTagCompound compound = getCompound(stack);
		compound.setString(tag, s);
		stack.setTagCompound(compound);
	}

	// GETTERS ///////////////////////////////////////////////////////////////////

	public static boolean verifyExistance(ItemStack stack, String tag) {
		NBTTagCompound compound = stack.getTagCompound();
		if (compound == null)
			return false;
		else
			return stack.getTagCompound().hasKey(tag);
	}
	
	public static boolean getBoolean(ItemStack stack, String tag, boolean defaultExpected) {
		return verifyExistance(stack, tag) ? stack.getTagCompound().getBoolean(tag) : defaultExpected;
	}
	
	public static short getShort(ItemStack stack, String tag, short defaultExpected) {
		return verifyExistance(stack, tag) ? stack.getTagCompound().getShort(tag) : defaultExpected;
	}
	
	public static int getIntager(ItemStack stack, String tag, int defaultExpected) {
		return verifyExistance(stack, tag) ? stack.getTagCompound().getInteger(tag) : defaultExpected;
	}
	
	public static float getFloat(ItemStack stack, String tag, float defaultExpected) {
		return verifyExistance(stack, tag) ? stack.getTagCompound().getFloat(tag) : defaultExpected;
	}
	
	public static double getDouble(ItemStack stack, String tag, double defaultExpected) {
		return verifyExistance(stack, tag) ? stack.getTagCompound().getDouble(tag) : defaultExpected;
	}
	
	public static String getString(ItemStack stack, String tag, String defaultExpected) {
		return verifyExistance(stack, tag) ? stack.getTagCompound().getString(tag) : defaultExpected;
	}
}