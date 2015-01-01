package com.brandon3055.draconicevolution.common.utills;

import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.common.lib.References;
import com.brandon3055.draconicevolution.common.network.ItemConfigPacket;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

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


	public ItemConfigField(int datatype, int slot, String name){
		this.slot = slot;
		this.datatype = datatype;
		this.name = name;
	}

	public ItemConfigField(int datatype, Object value, int slot, String name){
		this.value = value;
		this.slot = slot;
		this.datatype = datatype;
		this.name = name;
	}

	public ItemConfigField setMinMaxAndIncromente(Object min, Object max, Object incroment){
		this.max = max;
		this.min = min;
		this.incroment = incroment;
		return this;
	}

	public String getLocalizedName() { return StatCollector.translateToLocal("button.de." + name + ".name"); }

//	public void writeToItem(ItemStack stack){
//		DataUtills.writeObjectToItem(stack, value, datatype, name);
//	}

	public ItemConfigField readFromItem(ItemStack stack, Object defaultExpected){
		value = DataUtills.readObjectFromItem(stack, datatype, name, defaultExpected);
		return this;
	}

	public void sendChanges(){
		DraconicEvolution.network.sendToServer(new ItemConfigPacket(this));
	}

	public int castToInt(){
		switch (datatype){
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

	public double castToDouble(){
		switch (datatype){
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


//	public static int getTypeFromItem(ItemStack stack, String name) {
//		if (stack != null && stack.getItem() instanceof IConfigurableItem) return ((IConfigurableItem) stack.getItem()).getDataType(name);
//		return 0;
//	}
}
