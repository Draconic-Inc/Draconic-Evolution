package com.brandon3055.draconicevolution.common.utills;

import com.brandon3055.draconicevolution.common.lib.References;
import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;

/**
 * Created by Brandon on 31/12/2014.
 */
public class DataUtills {

	public static DataUtills instance = new DataUtills();

	public void writeObjectToBytes(ByteBuf bytes, int dataType, Object object){
		switch (dataType){
			case References.BYTE_ID:
				bytes.writeByte((Byte)object);
				break;
			case References.SHORT_ID:
				bytes.writeShort((Short) object);
				break;
			case References.INT_ID:
				bytes.writeInt((Integer) object);
				break;
			case References.LONG_ID:
				bytes.writeLong((Long) object);
				break;
			case References.FLOAT_ID:
				bytes.writeFloat((Float) object);
				break;
			case References.DOUBLE_ID:
				bytes.writeDouble((Double) object);
				break;
			case References.CHAR_ID:
				bytes.writeChar((Character) object);
				break;
			case References.STRING_ID:
				ByteBufUtils.writeUTF8String(bytes, (String) object);
				break;
			case References.BOOLEAN_ID:
				bytes.writeBoolean((Boolean) object);
				break;
			case References.INT_PAIR_ID:
				bytes.writeInt(((IntPair)object).i1);
				bytes.writeInt(((IntPair)object).i2);
				break;
		}
	}

	public Object readObjectFromBytes(ByteBuf bytes, int dataType){
		switch (dataType){
			case References.BYTE_ID:
				return bytes.readByte();
			case References.SHORT_ID:
				return bytes.readShort();
			case References.INT_ID:
				return bytes.readInt();
			case References.LONG_ID:
				return bytes.readLong();
			case References.FLOAT_ID:
				return bytes.readFloat();
			case References.DOUBLE_ID:
				return bytes.readDouble();
			case References.CHAR_ID:
				return bytes.readChar();
			case References.STRING_ID:
				return ByteBufUtils.readUTF8String(bytes);
			case References.BOOLEAN_ID:
				return bytes.readBoolean();
			case References.INT_PAIR_ID:
				IntPair tx = new IntPair(0, 0);
				tx.i1 = bytes.readInt();
				tx.i2 = bytes.readInt();
				return tx;
		}
		return null;
	}

	public static void writeObjectToItem(ItemStack stack, Object value, int datatype, String name){
		switch (datatype){
			case References.BYTE_ID:
				ItemNBTHelper.setByte(stack, name, (Byte) value);
				break;
			case References.SHORT_ID:
				ItemNBTHelper.setShort(stack, name, (Short) value);
				break;
			case References.INT_ID:
				ItemNBTHelper.setInteger(stack, name, (Integer) value);
				break;
			case References.LONG_ID:
				ItemNBTHelper.setLong(stack, name, (Long) value);
				break;
			case References.FLOAT_ID:
				ItemNBTHelper.setFloat(stack, name, (Float) value);
				break;
			case References.DOUBLE_ID:
				ItemNBTHelper.setDouble(stack, name, (Double) value);
				break;
//			case References.CHAR_ID:
//				ItemNBTHelper.setChar(stack, value.name, (Byte)value.value);
//				break;
			case References.STRING_ID:
				ItemNBTHelper.setString(stack, name, (String) value);
				break;
			case References.BOOLEAN_ID:
				ItemNBTHelper.setBoolean(stack, name, (Boolean) value);
				break;
		}
	}

	public static Object readObjectFromItem(ItemStack stack, int dataType, String name, Object defaultExpected){
		switch (dataType){
			case References.BYTE_ID:
				return ItemNBTHelper.getByte(stack, name, (Byte)defaultExpected);
			case References.SHORT_ID:
				return ItemNBTHelper.getShort(stack, name, (Short)defaultExpected);
			case References.INT_ID:
				return ItemNBTHelper.getInteger(stack, name, (Integer)defaultExpected);
			case References.LONG_ID:
				return ItemNBTHelper.getLong(stack, name, (Long)defaultExpected);
			case References.FLOAT_ID:
				return ItemNBTHelper.getFloat(stack, name, (Float)defaultExpected);
			case References.DOUBLE_ID:
				return ItemNBTHelper.getDouble(stack, name, (Double)defaultExpected);
			//case References.CHAR_ID:
			case References.STRING_ID:
				return ItemNBTHelper.getString(stack, name, (String)defaultExpected);
			case References.BOOLEAN_ID:
				return ItemNBTHelper.getBoolean(stack, name, (Boolean)defaultExpected);
		}
		return null;
	}

	public static Object readObjectFromItem(ItemStack stack, int dataType, String name){
		switch (dataType){
			case References.BYTE_ID:
				return ItemNBTHelper.getByte(stack, name, (byte)0);
			case References.SHORT_ID:
				return ItemNBTHelper.getShort(stack, name, (short)0);
			case References.INT_ID:
				return ItemNBTHelper.getInteger(stack, name, 0);
			case References.LONG_ID:
				return ItemNBTHelper.getLong(stack, name, 0L);
			case References.FLOAT_ID:
				return ItemNBTHelper.getFloat(stack, name, 0F);
			case References.DOUBLE_ID:
				return ItemNBTHelper.getDouble(stack, name, 0D);
			//case References.CHAR_ID:
			case References.STRING_ID:
				return ItemNBTHelper.getString(stack, name, "");
			case References.BOOLEAN_ID:
				return ItemNBTHelper.getBoolean(stack, name, false);
		}
		return null;
	}

	public static class IntPair
	{
		public int i1;
		public int i2;

		public IntPair(int i1, int i2)
		{
			this.i1 = i1;
			this.i2 = i2;
		}
	}
}
