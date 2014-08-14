package com.brandon3055.draconicevolution.common.core.network;

import com.brandon3055.draconicevolution.common.blocks.ModBlocks;
import com.brandon3055.draconicevolution.common.tileentities.TilePlacedItem;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Created by Brandon on 14/08/2014.
 */
public class PlacedItemPacket implements IPacket
{
	byte side = 0;
	int blockX = 0;
	int blockY = 0;
	int blockZ = 0;

	public PlacedItemPacket() {}

	public PlacedItemPacket(byte side, int x, int y, int z) {
		this.side = side;
		this.blockX = x;
		this.blockY = y;
		this.blockZ = z;
	}

	@Override
	public void writeBytes(ByteBuf bytes){
		bytes.writeByte(side);
		bytes.writeInt(blockX);
		bytes.writeInt(blockY);
		bytes.writeInt(blockZ);
	}

	@Override
	public void readBytes(ByteBuf bytes){
		this.side = bytes.readByte();
		this.blockX = bytes.readInt();
		this.blockY = bytes.readInt();
		this.blockZ = bytes.readInt();
	}

	@Override
	public void handleClientSide(NetHandlerPlayClient player){
	}

	@Override
	public void handleServerSide(NetHandlerPlayServer playerData){
		ForgeDirection dir = ForgeDirection.getOrientation(side);
		int x = blockX+dir.offsetX;
		int y = blockY+dir.offsetY;
		int z = blockZ+dir.offsetZ;
		World world = playerData.playerEntity.worldObj;
		EntityPlayer player = playerData.playerEntity;
		if (!world.isAirBlock(x, y, z) || player.getHeldItem() == null) return;
		ItemStack stack = player.getHeldItem();

		world.setBlock(x, y, z, ModBlocks.placedItem, side, 2);
		TilePlacedItem tile = (world.getTileEntity(x, y, z) != null && world.getTileEntity(x, y, z) instanceof TilePlacedItem) ? (TilePlacedItem) world.getTileEntity(x, y, z) : null;

		if (tile == null){
			world.setBlockToAir(x, y, z);
			return;
		}

		tile.setStack(stack.copy());
		player.destroyCurrentEquippedItem();
	}

}
