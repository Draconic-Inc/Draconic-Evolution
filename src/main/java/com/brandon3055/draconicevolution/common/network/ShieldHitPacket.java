package com.brandon3055.draconicevolution.common.network;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

import com.brandon3055.brandonscore.common.utills.DataUtills;
import com.brandon3055.draconicevolution.client.handler.ClientEventHandler;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

/**
 * Created by brandon3055 on 15/2/2016.
 */
public class ShieldHitPacket implements IMessage {

    public int playerID;
    public byte shieldPowerB;
    public float shieldPowerF;

    public ShieldHitPacket() {}

    public ShieldHitPacket(EntityPlayer playerHit, float shieldPower) {
        this.playerID = playerHit.getEntityId();
        this.shieldPowerB = (byte) (shieldPower * (float) Byte.MAX_VALUE);
    }

    @Override
    public void fromBytes(ByteBuf bytes) {
        playerID = bytes.readInt();
        shieldPowerB = bytes.readByte();
        shieldPowerF = (float) shieldPowerB / (float) Byte.MAX_VALUE;
    }

    @Override
    public void toBytes(ByteBuf bytes) {
        bytes.writeInt(playerID);
        bytes.writeByte(shieldPowerB);
    }

    public static class Handler implements IMessageHandler<ShieldHitPacket, IMessage> {

        @Override
        public IMessage onMessage(ShieldHitPacket message, MessageContext ctx) {
            Entity entity = Minecraft.getMinecraft().theWorld.getEntityByID(message.playerID);
            if (entity instanceof EntityPlayer) ClientEventHandler.playerShieldStatus.put(
                    (EntityPlayer) entity,
                    new DataUtills.XZPair<Float, Integer>(message.shieldPowerF, ClientEventHandler.elapsedTicks));
            return null;
        }
    }
}
