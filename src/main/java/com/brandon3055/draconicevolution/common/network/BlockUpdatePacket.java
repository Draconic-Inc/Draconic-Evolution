package com.brandon3055.draconicevolution.common.network;

import net.minecraft.network.play.server.S23PacketBlockChange;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class BlockUpdatePacket implements IMessage {

    int x, y, z;

    public BlockUpdatePacket() {}

    public BlockUpdatePacket(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public void fromBytes(ByteBuf bytes) {
        this.x = bytes.readInt();
        this.y = bytes.readInt();
        this.z = bytes.readInt();
    }

    @Override
    public void toBytes(ByteBuf bytes) {
        bytes.writeInt(x);
        bytes.writeInt(y);
        bytes.writeInt(z);
    }

    public static class Handler implements IMessageHandler<BlockUpdatePacket, IMessage> {

        @Override
        public IMessage onMessage(BlockUpdatePacket message, MessageContext ctx) {
            ctx.getServerHandler().playerEntity.playerNetServerHandler.sendPacket(
                    new S23PacketBlockChange(
                            message.x,
                            message.y,
                            message.z,
                            ctx.getServerHandler().playerEntity.worldObj));
            return null;
        }
    }
}
