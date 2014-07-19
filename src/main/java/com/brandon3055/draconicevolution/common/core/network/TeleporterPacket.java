package com.brandon3055.draconicevolution.common.core.network;

import com.brandon3055.draconicevolution.common.core.handler.ConfigHandler;
import com.brandon3055.draconicevolution.common.core.utills.ItemNBTHelper;
import com.brandon3055.draconicevolution.common.items.ModItems;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.network.NetHandlerPlayServer;

public class TeleporterPacket implements IPacket
{
	byte buttonId = 0;
	int dim = 0;
	int selection = 0;
	double playerX = 0;
	double playerY = 0;
	double playerZ = 0;
	float yaw = 0; 
	float pitch = 0;

	public TeleporterPacket() {
	}

	public TeleporterPacket(byte buttonId, int data) {
		this.buttonId = buttonId;
		this.selection = data;
	}

	public TeleporterPacket(byte buttonId, int data, int dim, double x, double y, double z, float yaw, float pitch) {
		this.buttonId = buttonId;
		this.dim = dim;
		this.selection = data;
		this.playerX = x;
		this.playerY = y;
		this.playerZ = z;
		this.yaw = yaw;
		this.pitch = pitch;
	}
	
	@Override
	public void readBytes(ByteBuf bytes)
	{
		this.buttonId = bytes.readByte();
		this.dim = bytes.readInt();
		this.selection = bytes.readInt();
		this.playerX = bytes.readDouble();
		this.playerY = bytes.readDouble();
		this.playerZ = bytes.readDouble();
		this.yaw = bytes.readFloat();
		this.pitch = bytes.readFloat(); 
	}

	@Override
	public void writeBytes(ByteBuf bytes)
	{
		bytes.writeByte(buttonId);
		bytes.writeInt(dim);
		bytes.writeInt(selection);
		bytes.writeDouble(playerX );
		bytes.writeDouble(playerY);
		bytes.writeDouble(playerZ);
		bytes.writeFloat(yaw);
		bytes.writeFloat(pitch); 
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
			case 0: //Teleporter mode set
				if (player.playerEntity.getCurrentEquippedItem() != null && player.playerEntity.getCurrentEquippedItem().isItemEqual(new ItemStack(ModItems.teleporterMKII)))
					ItemNBTHelper.setShort(player.playerEntity.getCurrentEquippedItem(), "Selection", (short) this.selection);
			break; //Set waypoint
			case 1:
				ItemStack stack = player.playerEntity.getCurrentEquippedItem();
				if (stack != null && stack.isItemEqual(new ItemStack(ModItems.teleporterMKII)))
				{
					ItemNBTHelper.setDouble(stack, "X_" + selection, playerX);
					ItemNBTHelper.setDouble(stack, "Y_" + selection, playerY);
					ItemNBTHelper.setDouble(stack, "Z_" + selection, playerZ);
					ItemNBTHelper.setFloat(stack, "Yaw_" + selection, yaw);
					ItemNBTHelper.setFloat(stack, "Pitch_" + selection, pitch);
					ItemNBTHelper.setIntager(stack, "Dimension_" + selection, dim);
					ItemNBTHelper.setBoolean(stack, "IsSet_" + selection, true);
				}
			break;
			case 6:
				ItemStack stack2 = player.playerEntity.getCurrentEquippedItem();
				if (stack2 != null && stack2.isItemEqual(new ItemStack(ModItems.teleporterMKII)))
				{
					int fuel = ItemNBTHelper.getIntager(stack2, "Fuel", 0);
					if (player.playerEntity.inventory.hasItem(Items.ender_pearl))
					{
						for (int i = 0; i < selection; i++)
						{
							player.playerEntity.inventory.consumeInventoryItem(Items.ender_pearl);
						}
						ItemNBTHelper.setIntager(stack2, "Fuel", fuel + (ConfigHandler.teleporterUsesPerPearl * selection));
					}
					
				}
			break;

		}
	}

}