package com.brandon3055.draconicevolution.common.network;

import net.minecraft.client.Minecraft;

import com.brandon3055.brandonscore.common.utills.DataUtills;
import com.brandon3055.draconicevolution.common.container.ContainerDataSync;
import com.brandon3055.draconicevolution.common.tileentities.TileObjectSync;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;

/**
 * Created by Brandon on 14/11/2014.
 */
public class TileObjectPacket implements IMessage {

    int x;
    int y;
    int z;
    short index;
    short dataType = -1;
    Object object;
    boolean isContainerPacket;

    /**
     * Used for Tile and Container synchronization
     */
    public TileObjectPacket() {}

    public TileObjectPacket(TileObjectSync tile, byte dataType, int index, Object object) {
        this.isContainerPacket = tile == null;
        if (!isContainerPacket) {
            this.x = tile.xCoord;
            this.y = tile.yCoord;
            this.z = tile.zCoord;
        }
        this.dataType = dataType;
        this.object = object;
        this.index = (short) index;
    }

    @Override
    public void toBytes(ByteBuf bytes) {
        bytes.writeBoolean(isContainerPacket);

        if (!isContainerPacket) {
            bytes.writeInt(x);
            bytes.writeInt(y);
            bytes.writeInt(z);
        }

        bytes.writeByte(dataType);
        bytes.writeShort(index);

        DataUtills.instance.writeObjectToBytes(bytes, dataType, object);
    }

    @Override
    public void fromBytes(ByteBuf bytes) {
        isContainerPacket = bytes.readBoolean();

        if (!isContainerPacket) {
            x = bytes.readInt();
            y = bytes.readInt();
            z = bytes.readInt();
        }

        dataType = bytes.readByte();
        index = bytes.readShort();

        object = DataUtills.instance.readObjectFromBytes(bytes, dataType);
    }

    public static class Handler implements IMessageHandler<TileObjectPacket, IMessage> {

        @Override
        public IMessage onMessage(TileObjectPacket message, MessageContext ctx) {
            if (ctx.side == Side.CLIENT) {
                if (message.isContainerPacket) {
                    ContainerDataSync container = Minecraft
                            .getMinecraft().thePlayer.openContainer instanceof ContainerDataSync
                                    ? (ContainerDataSync) Minecraft.getMinecraft().thePlayer.openContainer
                                    : null;
                    if (container == null) return null;
                    container.receiveSyncData(message.index, (Integer) message.object);
                } else {
                    if (!(Minecraft.getMinecraft().theWorld
                            .getTileEntity(message.x, message.y, message.z) instanceof TileObjectSync))
                        return null;
                    ((TileObjectSync) Minecraft.getMinecraft().theWorld.getTileEntity(message.x, message.y, message.z))
                            .receiveObjectFromServer(message.index, message.object);
                }
            } else {
                if (message.isContainerPacket) {
                    ContainerDataSync container = ctx
                            .getServerHandler().playerEntity.openContainer instanceof ContainerDataSync
                                    ? (ContainerDataSync) ctx.getServerHandler().playerEntity.openContainer
                                    : null;
                    if (container == null) return null;
                    container.receiveSyncData(message.index, (Integer) message.object);
                } else {
                    if (!(ctx.getServerHandler().playerEntity.worldObj
                            .getTileEntity(message.x, message.y, message.z) instanceof TileObjectSync))
                        return null;
                    ((TileObjectSync) ctx.getServerHandler().playerEntity.worldObj
                            .getTileEntity(message.x, message.y, message.z))
                                    .receiveObjectFromClient(message.index, message.object);
                }
            }
            return null;
        }
    }
}
