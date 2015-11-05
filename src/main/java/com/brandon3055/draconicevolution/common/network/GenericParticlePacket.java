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

public class GenericParticlePacket implements IMessage
{
	public static final byte ENERGY_BALL_KILL = 0;
	public static final byte CHAOS_BALL_KILL = 1;
	public static final byte CHAOS_IMPLOSION = 3;

	byte particleId = 0;
	double posX;
	double posY;
	double posZ;

	public GenericParticlePacket() {}

	public GenericParticlePacket(byte particleId, double posX, double posY, double posZ) {
		this.particleId = particleId;
		this.posX = posX;
		this.posY = posY;
		this.posZ = posZ;
	}

	@Override
	public void fromBytes(ByteBuf bytes){
		this.particleId = bytes.readByte();
		this.posX = (double)bytes.readFloat();
		this.posY = (double)bytes.readFloat();
		this.posZ = (double)bytes.readFloat();
	}

	@Override
	public void toBytes(ByteBuf bytes){
		bytes.writeByte(particleId);
		bytes.writeFloat((float)posX);
		bytes.writeFloat((float)posY);
		bytes.writeFloat((float)posZ);
	}

	public static class Handler implements IMessageHandler<GenericParticlePacket, IMessage> {

		@SideOnly(Side.CLIENT)
		@Override
		public IMessage onMessage(GenericParticlePacket message, MessageContext ctx) {
			switch (message.particleId) {
				case ENERGY_BALL_KILL:
				{
					Particles.DragonProjectileParticle particle;
					for (int i = 0; i < 100; i++){
						particle = new Particles.DragonProjectileParticle(BrandonsCore.proxy.getClientWorld(), message.posX, message.posY, message.posZ, 0x00FFFF);
						double m = 0.5D;
						particle.motionX = (particle.worldObj.rand.nextDouble() - 0.5) * m;
						particle.motionY = (particle.worldObj.rand.nextDouble() - 0.5) * m;
						particle.motionZ = (particle.worldObj.rand.nextDouble() - 0.5) * m;
						ParticleHandler.spawnCustomParticle(particle);
					}
					break;
				}
				case CHAOS_BALL_KILL:
				{
					Particles.DragonProjectileParticle particle;
					for (int i = 0; i < 100; i++){
						particle = new Particles.DragonProjectileParticle(BrandonsCore.proxy.getClientWorld(), message.posX, message.posY, message.posZ, 0x440000);
						double m = 0.5D;
						particle.motionX = (particle.worldObj.rand.nextDouble() - 0.5) * m;
						particle.motionY = (particle.worldObj.rand.nextDouble() - 0.5) * m;
						particle.motionZ = (particle.worldObj.rand.nextDouble() - 0.5) * m;
						ParticleHandler.spawnCustomParticle(particle);
					}
					break;
				}
				case CHAOS_IMPLOSION:
				{
					ParticleHandler.spawnCustomParticle(new Particles.ChaosImplosionParticle(BrandonsCore.proxy.getClientWorld(), message.posX, message.posY, message.posZ, 200F), 512);
					break;
				}


				default:
					break;
			}
			return null;
		}
	}
}