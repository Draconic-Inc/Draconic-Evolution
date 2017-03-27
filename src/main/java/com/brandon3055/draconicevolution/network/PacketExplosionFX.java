package com.brandon3055.draconicevolution.network;

import com.brandon3055.brandonscore.client.particle.BCEffectHandler;
import com.brandon3055.brandonscore.lib.Vec3D;
import com.brandon3055.brandonscore.network.MessageHandlerWrapper;
import com.brandon3055.draconicevolution.client.render.effect.ExplosionFX;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Created by brandon3055 on 8/06/2016.
 */
public class PacketExplosionFX implements IMessage {

    private BlockPos pos;
    private int radius;

    public PacketExplosionFX(){}

    public PacketExplosionFX(BlockPos pos, int radius) {
        this.pos = pos;
        this.radius = radius;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(pos.getX());
        buf.writeInt(pos.getY());
        buf.writeInt(pos.getZ());
        buf.writeShort(radius);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        pos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
        radius = buf.readShort();
    }

    public static class Handler extends MessageHandlerWrapper<PacketExplosionFX, IMessage> {

        @Override
        public IMessage handleMessage(PacketExplosionFX message, MessageContext ctx) {
            ExplosionFX explosionFX = new ExplosionFX(Minecraft.getMinecraft().theWorld, Vec3D.getCenter(message.pos), message.radius);
            BCEffectHandler.spawnGLParticle(ExplosionFX.FX_HANDLER, explosionFX);
            return null;
        }
    }
}
