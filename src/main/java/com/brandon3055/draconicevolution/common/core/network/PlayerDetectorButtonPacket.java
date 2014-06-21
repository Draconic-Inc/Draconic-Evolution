package com.brandon3055.draconicevolution.common.core.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.NetHandlerPlayServer;
import cpw.mods.fml.common.network.ByteBufUtils;
import com.brandon3055.draconicevolution.common.container.ContainerPlayerDetector;
import com.brandon3055.draconicevolution.common.tileentities.TilePlayerDetectorAdvanced;


public class PlayerDetectorButtonPacket implements IPacket
{
	private short index = 0;
	private short value = 0;
	
	public PlayerDetectorButtonPacket() {}

	public PlayerDetectorButtonPacket(byte index, short value) {
		this.index = index;
		this.value = value;
	}

	@Override
	public void writeBytes(ByteBuf bytes)
	{
		bytes.writeByte(index);
		bytes.writeByte(value);
	}
	
	@Override
	public void readBytes(ByteBuf bytes)
	{
		index = bytes.readByte();
		value = bytes.readByte();
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
			switch (index)
			{
				case 0:
					tile.range = value;
					break;
				case 1:
					tile.whiteList = value == 1 ? true : false;
					break;
                case 2:
                    tile.outputInverted = value == 1 ? true : false;
                    break;
			}
			player.playerEntity.worldObj.markBlockForUpdate(tile.xCoord, tile.yCoord, tile.zCoord);
		}
	}

}