package com.brandon3055.draconicevolution.common.core.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.NetHandlerPlayServer;
import cpw.mods.fml.common.network.ByteBufUtils;
import com.brandon3055.draconicevolution.common.container.ContainerPlayerDetector;
import com.brandon3055.draconicevolution.common.tileentities.TilePlayerDetectorAdvanced;


public class PlayerDetectorStringPacket implements IPacket
{
	private int index = 0;
	private String name = "";
	
	public PlayerDetectorStringPacket() {}

	public PlayerDetectorStringPacket(byte index, String name) {
		this.index = index;
		this.name = name;
	}

	@Override
	public void writeBytes(ByteBuf bytes)
	{
		bytes.writeByte(index);
		ByteBufUtils.writeUTF8String(bytes, name);
	}
	
	@Override
	public void readBytes(ByteBuf bytes)
	{
		index = bytes.readByte();
		name = ByteBufUtils.readUTF8String(bytes);
	}

	@Override
	public void handleClientSide(NetHandlerPlayClient player)
	{
	}

	@Override
	public void handleServerSide(NetHandlerPlayServer player)
	{	
		ContainerPlayerDetector container = (player.playerEntity.openContainer instanceof ContainerPlayerDetector) ? (ContainerPlayerDetector)player.playerEntity.openContainer : null; 
		TilePlayerDetectorAdvanced tile = (container != null) ? ((ContainerPlayerDetector)container).getTileDetector() : null;
			
		if (tile != null)
		{
			tile.names[index] = name;
			player.playerEntity.worldObj.markBlockForUpdate(tile.xCoord, tile.yCoord, tile.zCoord);
		}
	}

}