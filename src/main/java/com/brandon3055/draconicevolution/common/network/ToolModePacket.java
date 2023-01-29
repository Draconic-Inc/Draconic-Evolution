package com.brandon3055.draconicevolution.common.network;

import com.brandon3055.draconicevolution.common.items.tools.baseclasses.ToolBase;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

/**
 * Created by Brandon on 27/03/2015.
 */
public class ToolModePacket implements IMessage {

    public boolean shift;
    public boolean ctrl;

    public ToolModePacket() {}

    public ToolModePacket(boolean shift, boolean ctrl) {
        this.shift = shift;
        this.ctrl = ctrl;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(shift);
        buf.writeBoolean(ctrl);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        shift = buf.readBoolean();
        ctrl = buf.readBoolean();
    }

    public static class Handler implements IMessageHandler<ToolModePacket, IMessage> {

        @Override
        public IMessage onMessage(ToolModePacket message, MessageContext ctx) {
            ToolBase.handleModeChange(
                    ctx.getServerHandler().playerEntity.getHeldItem(),
                    ctx.getServerHandler().playerEntity,
                    message.shift,
                    message.ctrl);
            return null;
        }
    }
}
