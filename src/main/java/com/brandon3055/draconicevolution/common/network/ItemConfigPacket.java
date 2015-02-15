package com.brandon3055.draconicevolution.common.network;

import com.brandon3055.draconicevolution.common.utills.DataUtills;
import com.brandon3055.draconicevolution.common.utills.IConfigurableItem;
import com.brandon3055.draconicevolution.common.utills.ItemConfigField;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class ItemConfigPacket implements IMessage
{
	public byte datatype;
	public int slot;
	public Object value;
	public String name;

	public ItemConfigPacket() {}

	public ItemConfigPacket(ItemConfigField field) {
		this.datatype = (byte)field.datatype;
		this.slot = field.slot;
		this.value = field.value;
		this.name = field.name;
	}

	@Override
	public void fromBytes(ByteBuf bytes){
		this.datatype = bytes.readByte();
		this.slot = bytes.readInt();
		this.name = ByteBufUtils.readUTF8String(bytes);
		this.value = DataUtills.instance.readObjectFromBytes(bytes, datatype);
	}

	@Override
	public void toBytes(ByteBuf bytes){
		bytes.writeByte(datatype);
		bytes.writeInt(slot);
		ByteBufUtils.writeUTF8String(bytes, name);
		DataUtills.instance.writeObjectToBytes(bytes, datatype, value);
	}

	public static class Handler implements IMessageHandler<ItemConfigPacket, IMessage> {

		@Override
		public IMessage onMessage(ItemConfigPacket message, MessageContext ctx) {
			EntityPlayer player = ctx.getServerHandler().playerEntity;
			if (message.slot >= player.inventory.getSizeInventory() || message.slot < 0) return null;
			ItemStack stack = player.inventory.getStackInSlot(message.slot);
			if (stack != null && stack.getItem() instanceof IConfigurableItem) DataUtills.writeObjectToItem(stack, message.value, message.datatype, message.name);
			return null;
		}
	}
}