package com.brandon3055.draconicevolution.network;

import com.brandon3055.brandonscore.BrandonsCore;
import com.brandon3055.brandonscore.client.particle.BCEffectHandler;
import com.brandon3055.brandonscore.lib.Vec3D;
import com.brandon3055.brandonscore.network.MessageHandlerWrapper;
import com.brandon3055.draconicevolution.client.render.effect.ExplosionFX;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by brandon3055 on 8/06/2016.
 */
public class PacketExplosionFX implements IMessage {

    private BlockPos pos;
    private int radius;
    private boolean update;

    public PacketExplosionFX() {
    }

    public PacketExplosionFX(BlockPos pos, int radius, boolean update) {
        this.pos = pos;
        this.radius = radius;
        this.update = update;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(pos.getX());
        buf.writeInt(pos.getY());
        buf.writeInt(pos.getZ());
        buf.writeShort(radius);
        buf.writeBoolean(update);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        pos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
        radius = buf.readShort();
        update = buf.readBoolean();
    }

    public static class Handler extends MessageHandlerWrapper<PacketExplosionFX, IMessage> {

        @Override
        public IMessage handleMessage(PacketExplosionFX message, MessageContext ctx) {
            spawnFX(message, ctx);
            return null;
        }

        @SideOnly(Side.CLIENT)
        public void spawnFX(PacketExplosionFX message, MessageContext ctx) {
            if (message.update) {
                FMLClientHandler.instance().reloadRenderers();
            }
            else {
                ExplosionFX explosionFX = new ExplosionFX(BrandonsCore.proxy.getClientWorld(), Vec3D.getCenter(message.pos), message.radius);
                BCEffectHandler.spawnGLParticle(ExplosionFX.FX_HANDLER, explosionFX);
            }
        }
    }
}
