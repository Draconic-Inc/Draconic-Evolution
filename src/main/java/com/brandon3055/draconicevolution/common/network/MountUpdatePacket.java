package com.brandon3055.draconicevolution.common.network;

import com.brandon3055.draconicevolution.client.handler.ClientEventHandler;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;

/**
 * Created by Brandon on 1/12/2014.
 */
public class MountUpdatePacket implements IMessage {

    public int entityID;

    public MountUpdatePacket() {}

    public MountUpdatePacket(int id) {
        this.entityID = id;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(entityID);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        entityID = buf.readInt();
    }

    public static class Handler implements IMessageHandler<MountUpdatePacket, IMessage> {

        @Override
        public IMessage onMessage(MountUpdatePacket message, MessageContext ctx) {
            if (ctx.side.equals(Side.SERVER)) {
                if (message.entityID == -1) {
                    ctx.getServerHandler().playerEntity.mountEntity(null);
                    return null;
                } else if (ctx.getServerHandler().playerEntity.ridingEntity != null) {
                    return new MountUpdatePacket(ctx.getServerHandler().playerEntity.ridingEntity.getEntityId());
                }
                return null;
            }

            ClientEventHandler.tryRepositionPlayerOnMount(message.entityID);
            return null;
        }
    }
}
