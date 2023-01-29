package com.brandon3055.draconicevolution.common.network;

import com.brandon3055.brandonscore.BrandonsCore;
import com.brandon3055.draconicevolution.client.handler.ParticleHandler;
import com.brandon3055.draconicevolution.client.render.particle.Particles;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;

public class GenericParticlePacket implements IMessage {

    public static final byte ENERGY_BALL_KILL = 0;
    public static final byte CHAOS_BALL_KILL = 1;
    public static final byte CHAOS_IMPLOSION = 3;
    public static final byte ARROW_SHOCK_WAVE = 4;

    private byte particleId = 0;
    private double posX;
    private double posY;
    private double posZ;
    private int additionalData;

    public GenericParticlePacket() {}

    public GenericParticlePacket(byte particleId, double posX, double posY, double posZ) {
        this.particleId = particleId;
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
    }

    public GenericParticlePacket(byte particleId, double posX, double posY, double posZ, int aditionalData) {
        this.particleId = particleId;
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
        this.additionalData = aditionalData;
    }

    @Override
    public void fromBytes(ByteBuf bytes) {
        this.particleId = bytes.readByte();
        this.posX = (double) bytes.readFloat();
        this.posY = (double) bytes.readFloat();
        this.posZ = (double) bytes.readFloat();

        if (particleId == ARROW_SHOCK_WAVE) {
            this.additionalData = bytes.readInt();
        }
    }

    @Override
    public void toBytes(ByteBuf bytes) {
        bytes.writeByte(particleId);
        bytes.writeFloat((float) posX);
        bytes.writeFloat((float) posY);
        bytes.writeFloat((float) posZ);

        if (particleId == ARROW_SHOCK_WAVE) {
            bytes.writeInt(additionalData);
        }
    }

    public static class Handler implements IMessageHandler<GenericParticlePacket, IMessage> {

        @SideOnly(Side.CLIENT)
        @Override
        public IMessage onMessage(GenericParticlePacket message, MessageContext ctx) {
            switch (message.particleId) {
                case ENERGY_BALL_KILL: {
                    Particles.DragonProjectileParticle particle;
                    for (int i = 0; i < 100; i++) {
                        particle = new Particles.DragonProjectileParticle(
                                BrandonsCore.proxy.getClientWorld(),
                                message.posX,
                                message.posY,
                                message.posZ,
                                0x00FFFF);
                        double m = 0.5D;
                        particle.motionX = (particle.worldObj.rand.nextDouble() - 0.5) * m;
                        particle.motionY = (particle.worldObj.rand.nextDouble() - 0.5) * m;
                        particle.motionZ = (particle.worldObj.rand.nextDouble() - 0.5) * m;
                        ParticleHandler.spawnCustomParticle(particle);
                    }
                    break;
                }
                case CHAOS_BALL_KILL: {
                    Particles.DragonProjectileParticle particle;
                    for (int i = 0; i < 100; i++) {
                        particle = new Particles.DragonProjectileParticle(
                                BrandonsCore.proxy.getClientWorld(),
                                message.posX,
                                message.posY,
                                message.posZ,
                                0x440000);
                        double m = 0.5D;
                        particle.motionX = (particle.worldObj.rand.nextDouble() - 0.5) * m;
                        particle.motionY = (particle.worldObj.rand.nextDouble() - 0.5) * m;
                        particle.motionZ = (particle.worldObj.rand.nextDouble() - 0.5) * m;
                        ParticleHandler.spawnCustomParticle(particle);
                    }
                    break;
                }
                case CHAOS_IMPLOSION: {
                    ParticleHandler.spawnCustomParticle(
                            new Particles.ChaosImplosionParticle(
                                    BrandonsCore.proxy.getClientWorld(),
                                    message.posX,
                                    message.posY,
                                    message.posZ,
                                    200F),
                            512);
                    break;
                }
                case ARROW_SHOCK_WAVE: {
                    ParticleHandler.spawnCustomParticle(
                            new Particles.ArrowShockParticle(
                                    BrandonsCore.proxy.getClientWorld(),
                                    message.posX,
                                    message.posY,
                                    message.posZ,
                                    message.additionalData),
                            256);
                    for (int i = 0; i < 100; i++) {
                        Particles.ArrowParticle particle = new Particles.ArrowParticle(
                                BrandonsCore.proxy.getClientWorld(),
                                message.posX - 0.25 + BrandonsCore.proxy.getClientWorld().rand.nextDouble() * 0.5,
                                message.posY + BrandonsCore.proxy.getClientWorld().rand.nextDouble() * 0.5,
                                message.posZ - 0.25 + BrandonsCore.proxy.getClientWorld().rand.nextDouble() * 0.5,
                                0xff6000,
                                0.2F + BrandonsCore.proxy.getClientWorld().rand.nextFloat() * 10f);

                        double mm = 2;
                        particle.motionX = (BrandonsCore.proxy.getClientWorld().rand.nextDouble() - 0.5) * mm;
                        particle.motionY = (BrandonsCore.proxy.getClientWorld().rand.nextDouble() - 0.5) * mm;
                        particle.motionZ = (BrandonsCore.proxy.getClientWorld().rand.nextDouble() - 0.5) * mm;
                        ParticleHandler.spawnCustomParticle(particle, 64);
                    }
                    break;
                }

                default:
                    break;
            }
            return null;
        }
    }
}
