package com.brandon3055.draconicevolution.network;

import com.brandon3055.brandonscore.network.MessageHandlerWrapper;
import com.brandon3055.brandonscore.utils.DataUtils;
import com.brandon3055.draconicevolution.client.handler.ClientEventHandler;
import com.brandon3055.draconicevolution.lib.DESoundHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Created by brandon3055 on 15/2/2016.
 */
public class PacketShieldHit implements IMessage {
    public int playerID;
    public byte shieldPowerB;
    public float shieldPowerF;

    public PacketShieldHit() {
    }

    public PacketShieldHit(EntityPlayer playerHit, float shieldPower) {
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

    public static class Handler extends MessageHandlerWrapper<PacketShieldHit, IMessage> {

        @Override
        public IMessage handleMessage(PacketShieldHit message, MessageContext ctx) {
            Entity entity = Minecraft.getMinecraft().theWorld.getEntityByID(message.playerID);
            if (entity instanceof EntityPlayer) {
                ClientEventHandler.playerShieldStatus.put((EntityPlayer) entity, new DataUtils.XZPair<Float, Integer>(message.shieldPowerF, ClientEventHandler.elapsedTicks));
                entity.worldObj.playSound(entity.posX + 0.5D, entity.posY + 0.5D, entity.posZ + 0.5D, DESoundHandler.shieldStrike, SoundCategory.PLAYERS, 0.9F, entity.worldObj.rand.nextFloat() * 0.1F + 0.5F + (0.3F * message.shieldPowerF), false);
            }
            return null;
        }
    }
}
