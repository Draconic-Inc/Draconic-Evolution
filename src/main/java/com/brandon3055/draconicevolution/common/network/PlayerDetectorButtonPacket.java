package com.brandon3055.draconicevolution.common.network;

import com.brandon3055.draconicevolution.common.container.ContainerPlayerDetector;
import com.brandon3055.draconicevolution.common.tileentities.TilePlayerDetectorAdvanced;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class PlayerDetectorButtonPacket implements IMessage {
    private short index = 0;
    private short value = 0;

    public PlayerDetectorButtonPacket() {}

    public PlayerDetectorButtonPacket(byte index, short value) {
        this.index = index;
        this.value = value;
    }

    @Override
    public void toBytes(ByteBuf bytes) {
        bytes.writeByte(index);
        bytes.writeByte(value);
    }

    @Override
    public void fromBytes(ByteBuf bytes) {
        index = bytes.readByte();
        value = bytes.readByte();
    }

    public static class Handler implements IMessageHandler<PlayerDetectorButtonPacket, IMessage> {

        @Override
        public IMessage onMessage(PlayerDetectorButtonPacket message, MessageContext ctx) {
            ContainerPlayerDetector container =
                    (ctx.getServerHandler().playerEntity.openContainer instanceof ContainerPlayerDetector)
                            ? (ContainerPlayerDetector) ctx.getServerHandler().playerEntity.openContainer
                            : null;
            TilePlayerDetectorAdvanced tile =
                    (container != null) ? ((ContainerPlayerDetector) container).getTileDetector() : null;

            if (tile != null) {
                switch (message.index) {
                    case 0:
                        tile.range = message.value;
                        break;
                    case 1:
                        tile.whiteList = message.value == 1;
                        break;
                    case 2:
                        tile.outputInverted = message.value == 1;
                        break;
                }
                ctx.getServerHandler().playerEntity.worldObj.markBlockForUpdate(tile.xCoord, tile.yCoord, tile.zCoord);
            }
            return null;
        }
    }
}
