package com.brandon3055.draconicevolution.common.core.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.item.ItemStack;
import net.minecraft.network.NetHandlerPlayServer;
import cpw.mods.fml.common.network.ByteBufUtils;
import com.brandon3055.draconicevolution.common.core.helper.ItemNBTHelper;
import com.brandon3055.draconicevolution.common.items.ModItems;

public class TeleporterStringPacket implements IPacket
{
	int index = 0;
	String name = "Destination";
	
	public TeleporterStringPacket() {}

	public TeleporterStringPacket(int index, String name) {
		this.index = index;
		this.name = name;
	}

	@Override
	public void readBytes(ByteBuf bytes)
	{
		this.index = bytes.readInt();
		
		name = ByteBufUtils.readUTF8String(bytes);
	}

	@Override
	public void writeBytes(ByteBuf bytes)
	{
		bytes.writeInt(index);
	
		ByteBufUtils.writeUTF8String(bytes, name);
	}

	@Override
	public void handleClientSide(NetHandlerPlayClient player)
	{
	
	}

	@Override
	public void handleServerSide(NetHandlerPlayServer player)
	{
		ItemStack stack = player.playerEntity.getCurrentEquippedItem();
		if (stack != null && stack.isItemEqual(new ItemStack(ModItems.teleporterMKII)))
		{
			ItemNBTHelper.setString(stack, "Dest_" + index, name);
		}
	}
}