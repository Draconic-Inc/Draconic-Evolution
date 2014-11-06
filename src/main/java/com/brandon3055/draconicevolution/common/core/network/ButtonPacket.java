package com.brandon3055.draconicevolution.common.core.network;

import com.brandon3055.draconicevolution.common.container.ContainerDissEnchanter;
import com.brandon3055.draconicevolution.common.container.ContainerDraconiumChest;
import com.brandon3055.draconicevolution.common.container.ContainerWeatherController;
import com.brandon3055.draconicevolution.common.tileentities.TileDissEnchanter;
import com.brandon3055.draconicevolution.common.tileentities.TileDraconiumChest;
import com.brandon3055.draconicevolution.common.tileentities.TileWeatherController;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.inventory.Container;

public class ButtonPacket implements IMessage
{
	public static final byte ID_WEATHERCONTROLLER = 0;
	public static final byte ID_DISSENCHANTER = 1;
	public static final byte ID_DRACONIUMCHEST0 = 2;
	public static final byte ID_DRACONIUMCHEST1 = 3;
	public static final byte ID_DRACONIUMCHEST2 = 4;
	public static final byte ID_DRACONIUMCHEST3 = 5;
	byte buttonId = 0;
	boolean state = false;
	
	public ButtonPacket() {}

	public ButtonPacket(byte buttonId, boolean state) {
		this.buttonId = buttonId;
		this.state = state;
	}

	@Override
	public void fromBytes(ByteBuf bytes){
		this.buttonId = bytes.readByte();
		this.state = bytes.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf bytes){
		bytes.writeByte(buttonId);
		bytes.writeBoolean(state);
	}

	public static class Handler implements IMessageHandler<ButtonPacket, IMessage> {

		@Override
		public IMessage onMessage(ButtonPacket message, MessageContext ctx) {
			switch (message.buttonId) {
				case ID_WEATHERCONTROLLER:
				{
					Container container = ctx.getServerHandler().playerEntity.openContainer;
					if (container != null && container instanceof ContainerWeatherController){
						TileWeatherController tileWC = ((ContainerWeatherController) container).getTileWC();
						tileWC.reciveButtonEvent(message.buttonId);
					}
					break;
				}
				case ID_DISSENCHANTER:
				{
					Container container = ctx.getServerHandler().playerEntity.openContainer;
					if (container != null && container instanceof ContainerDissEnchanter){
						TileDissEnchanter tile = ((ContainerDissEnchanter) container).getTile();
						tile.buttonClick(ctx.getServerHandler().playerEntity);
					}
					break;
				}
				case ID_DRACONIUMCHEST0:
				{
					Container container = ctx.getServerHandler().playerEntity.openContainer;
					if (container != null && container instanceof ContainerDraconiumChest){
						TileDraconiumChest tile = ((ContainerDraconiumChest) container).getTile();
						tile.setAutoFeed(0);
					}
					break;
				}
				case ID_DRACONIUMCHEST1:
				{
					Container container = ctx.getServerHandler().playerEntity.openContainer;
					if (container != null && container instanceof ContainerDraconiumChest){
						TileDraconiumChest tile = ((ContainerDraconiumChest) container).getTile();
						tile.setAutoFeed(1);
					}
					break;
				}
				case ID_DRACONIUMCHEST2:
				{
					Container container = ctx.getServerHandler().playerEntity.openContainer;
					if (container != null && container instanceof ContainerDraconiumChest){
						TileDraconiumChest tile = ((ContainerDraconiumChest) container).getTile();
						tile.setAutoFeed(2);
					}
					break;
				}
				case ID_DRACONIUMCHEST3:
				{
					Container container = ctx.getServerHandler().playerEntity.openContainer;
					if (container != null && container instanceof ContainerDraconiumChest){
						TileDraconiumChest tile = ((ContainerDraconiumChest) container).getTile();
						tile.setAutoFeed(3);
					}
					break;
				}

				default:
					break;
			}
			return null;
		}
	}
}