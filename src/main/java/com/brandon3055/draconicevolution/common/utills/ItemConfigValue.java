package com.brandon3055.draconicevolution.common.utills;

import net.minecraft.item.ItemStack;

/**
 * Created by Brandon on 29/12/2014.
 */
public class ItemConfigValue {

	public Object value;
	public int slot;
	public int datatype;
	public String name;
	public int fieldid;

	public ItemConfigValue(int datatype, int slot, String name){
		this.slot = slot;
		this.datatype = datatype;
		this.name = name;
	}

	public ItemConfigValue(int datatype, Object value, int slot, String name){
		this.value = value;
		this.slot = slot;
		this.datatype = datatype;
		this.name = name;
	}

//	public void writeToItem(ItemStack stack){
//		DataUtills.writeObjectToItem(stack, value, datatype, name);
//	}

	public ItemConfigValue readFromItem(ItemStack stack){
		value = DataUtills.readObjectFromItem(stack, datatype, name);
		return this;
	}

//	public static int getTypeFromItem(ItemStack stack, String name) {
//		if (stack != null && stack.getItem() instanceof IConfigurableItem) return ((IConfigurableItem) stack.getItem()).getDataType(name);
//		return 0;
//	}
}
