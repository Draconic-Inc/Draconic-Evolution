package com.brandon3055.draconicevolution.common.network;

import com.brandon3055.draconicevolution.common.container.ContainerDataSync;
import com.brandon3055.draconicevolution.common.tileentities.TileObjectSync;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;

/**
 * Created by Brandon on 14/11/2014.
 */
public class ObjectPacket implements IMessage{
	public static final byte BYTE    = 0;
	public static final byte SHORT   = 1;
	public static final byte INT     = 2;
	public static final byte LONG    = 3;
	public static final byte FLOAT   = 4;
	public static final byte DOUBLE  = 5;
	public static final byte BOOLEAN = 6;
	public static final byte CHAR    = 7;
	public static final byte STRING  = 8;

	int x;
	int y;
	int z;
	short index;
	short dataType = -1;
	Object object;
	boolean isContainerPacket;

	/**Used for Tile and Container synchronization*/
	public ObjectPacket() {}

	public ObjectPacket(TileObjectSync tile, byte dataType, int index, Object object) {
		this.isContainerPacket = tile == null;
		if (!isContainerPacket) {
			this.x = tile.xCoord;
			this.y = tile.yCoord;
			this.z = tile.zCoord;
		}
		this.dataType = dataType;
		this.object = object;
		this.index = (short)index;
	}

	@Override
	public void toBytes(ByteBuf bytes){
		bytes.writeBoolean(isContainerPacket);

		if (!isContainerPacket) {
			bytes.writeInt(x);
			bytes.writeInt(y);
			bytes.writeInt(z);
		}

		bytes.writeByte(dataType);
		bytes.writeShort(index);

		switch (dataType){
			case BYTE:
				bytes.writeByte((Byte)object);
				break;
			case SHORT:
				bytes.writeShort((Short) object);
				break;
			case INT:
				bytes.writeInt((Integer) object);
				break;
			case LONG:
				bytes.writeLong((Long) object);
				break;
			case FLOAT:
				bytes.writeFloat((Float) object);
				break;
			case DOUBLE:
				bytes.writeDouble((Double) object);
				break;
			case CHAR:
				bytes.writeChar((Character) object);
				break;
			case STRING:
				ByteBufUtils.writeUTF8String(bytes, (String)object);
				break;
			case BOOLEAN:
				bytes.writeBoolean((Boolean) object);
				break;
		}
	}

	@Override
	public void fromBytes(ByteBuf bytes){
		isContainerPacket = bytes.readBoolean();

		if (!isContainerPacket) {
			x = bytes.readInt();
			y = bytes.readInt();
			z = bytes.readInt();
		}

		dataType = bytes.readByte();
		index = bytes.readShort();

		switch (dataType){
			case BYTE:
				object = bytes.readByte();
				break;
			case SHORT:
				object = bytes.readShort();
				break;
			case INT:
				object = bytes.readInt();
				break;
			case LONG:
				object = bytes.readLong();
				break;
			case FLOAT:
				object = bytes.readFloat();
				break;
			case DOUBLE:
				object = bytes.readDouble();
				break;
			case CHAR:
				object = bytes.readChar();
				break;
			case STRING:
				object = ByteBufUtils.readUTF8String(bytes);
				break;
			case BOOLEAN:
				object = bytes.readBoolean();
				break;
		}
	}

	public static class Handler implements IMessageHandler<ObjectPacket, IMessage> {

		@Override
		public IMessage onMessage(ObjectPacket message, MessageContext ctx) {
			if (message.isContainerPacket){
				ContainerDataSync container = Minecraft.getMinecraft().thePlayer.openContainer instanceof ContainerDataSync ? (ContainerDataSync)Minecraft.getMinecraft().thePlayer.openContainer : null;
				if (container == null) return null;
				container.receiveSyncData(message.index, message.object);
			}else {
				if (!(Minecraft.getMinecraft().theWorld.getTileEntity(message.x, message.y, message.z) instanceof TileObjectSync)) return null;
				((TileObjectSync) Minecraft.getMinecraft().theWorld.getTileEntity(message.x, message.y, message.z)).receiveObject(message.index, message.object);
			}
			return null;
		}
	}
}
