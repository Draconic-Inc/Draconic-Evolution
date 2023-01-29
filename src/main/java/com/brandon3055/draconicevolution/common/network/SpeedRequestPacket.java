package com.brandon3055.draconicevolution.common.network;

import com.brandon3055.brandonscore.BrandonsCore;
import com.brandon3055.draconicevolution.common.handler.ConfigHandler;
import com.brandon3055.draconicevolution.common.handler.MinecraftForgeEventHandler;
import com.brandon3055.draconicevolution.common.utills.LogHelper;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;

/**
 * Created by Brandon on 26/03/2015.
 */
public class SpeedRequestPacket implements IMessage {

    double speed = 0F;

    public SpeedRequestPacket() {}

    public SpeedRequestPacket(double speed) {
        this.speed = speed;
    }

    @Override
    public void toBytes(ByteBuf bytes) {
        bytes.writeDouble(speed);
    }

    @Override
    public void fromBytes(ByteBuf bytes) {
        this.speed = bytes.readDouble();
    }

    public static class Handler implements IMessageHandler<SpeedRequestPacket, IMessage> {

        @Override
        public IMessage onMessage(SpeedRequestPacket message, MessageContext ctx) {
            if (ctx.side == Side.SERVER) {
                if (ConfigHandler.speedLimitDimList.contains(ctx.getServerHandler().playerEntity.dimension)
                        || (BrandonsCore.proxy.isOp(ctx.getServerHandler().playerEntity.getCommandSenderName())
                                && !ConfigHandler.speedLimitops))
                    return new SpeedRequestPacket(20F);
                return new SpeedRequestPacket(ConfigHandler.maxPlayerSpeed);
            } else {
                MinecraftForgeEventHandler.maxSpeed = message.speed;
                MinecraftForgeEventHandler.ticksSinceRequest = 0;
                MinecraftForgeEventHandler.speedNeedsUpdating = false;
                LogHelper.info("Server speed is set to " + message.speed);
            }
            return null;
        }
    }
}
