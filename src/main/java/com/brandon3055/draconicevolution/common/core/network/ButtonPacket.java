package com.brandon3055.draconicevolution.common.core.network;

import com.brandon3055.draconicevolution.common.container.ContainerWeatherController;
import com.brandon3055.draconicevolution.common.tileentities.TileWeatherController;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.inventory.Container;
import net.minecraft.network.NetHandlerPlayServer;

public class ButtonPacket implements IPacket
{
	byte buttonId = 0;
	boolean state = false;
	
	public ButtonPacket() {}

	public ButtonPacket(byte buttonId, boolean state) {
		this.buttonId = buttonId;
		this.state = state;
	}

	@Override
	public void readBytes(ByteBuf bytes)
	{
		this.buttonId = bytes.readByte();
		this.state = bytes.readBoolean();
	}

	@Override
	public void writeBytes(ByteBuf bytes)
	{
		bytes.writeByte(buttonId);
		bytes.writeBoolean(state);
	}

	@Override
	public void handleClientSide(NetHandlerPlayClient player)
	{
		System.out.println("a client");
	}

	@Override
	public void handleServerSide(NetHandlerPlayServer player)
	{
		switch (this.buttonId) {
			case 0:
			{
				Container container = player.playerEntity.openContainer;
				if (container != null && container instanceof ContainerWeatherController){
					TileWeatherController tileWC = ((ContainerWeatherController) container).getTileWC();
					tileWC.reciveButtonEvent(buttonId);
				}
				break;
			}
			case 1:
			break;

			default:
			break;
		}
	}

}