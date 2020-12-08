//package com.brandon3055.draconicevolution.network;
//
//import com.brandon3055.brandonscore.BrandonsCore;
//import com.brandon3055.brandonscore.lib.PairKV;
//import com.brandon3055.brandonscore.network.MessageHandlerWrapper;
//import com.brandon3055.draconicevolution.DEConfig;
//import com.brandon3055.draconicevolution.client.handler.ClientEventHandler;
//import com.brandon3055.draconicevolution.handlers.DESoundHandler;
//import io.netty.buffer.ByteBuf;
//import net.minecraft.entity.Entity;
//import net.minecraft.entity.player.PlayerEntity;
//import net.minecraft.util.SoundCategory;
//import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
//import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
//
///**
// * Created by brandon3055 on 15/2/2016.
// */
//public class PacketShieldHit implements IMessage {
//    public int playerID;
//    public byte shieldPowerB;
//    public float shieldPowerF;
//
//    public PacketShieldHit() {
//    }
//
//    public PacketShieldHit(PlayerEntity playerHit, float shieldPower) {
//        this.playerID = playerHit.getEntityId();
//        this.shieldPowerB = (byte) (shieldPower * (float) Byte.MAX_VALUE);
//    }
//
//    @Override
//    public void fromBytes(ByteBuf bytes) {
//        playerID = bytes.readInt();
//        shieldPowerB = bytes.readByte();
//        shieldPowerF = (float) shieldPowerB / (float) Byte.MAX_VALUE;
//    }
//
//    @Override
//    public void toBytes(ByteBuf bytes) {
//        bytes.writeInt(playerID);
//        bytes.writeByte(shieldPowerB);
//    }
//
//    public static class Handler extends MessageHandlerWrapper<PacketShieldHit, IMessage> {
//
//        @Override
//        public IMessage handleMessage(PacketShieldHit message, MessageContext ctx) {
//            Entity entity = BrandonsCore.proxy.getClientWorld().getEntityByID(message.playerID);
//            if (entity instanceof PlayerEntity) {
//                ClientEventHandler.playerShieldStatus.put((PlayerEntity) entity, new PairKV<>(message.shieldPowerF, ClientEventHandler.elapsedTicks));
//                if (!DEConfig.disableShieldHitSound){
//                    entity.world.playSound(entity.getPosX() + 0.5D, entity.getPosY() + 0.5D, entity.getPosZ() + 0.5D, DESoundHandler.shieldStrike, SoundCategory.PLAYERS, 0.9F, entity.world.rand.nextFloat() * 0.1F + 0.5F + (0.3F * message.shieldPowerF), false);
//                }
//            }
//            return null;
//        }
//    }
//}
