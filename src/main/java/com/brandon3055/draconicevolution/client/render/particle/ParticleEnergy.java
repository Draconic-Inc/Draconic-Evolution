package com.brandon3055.draconicevolution.client.render.particle;

import com.brandon3055.brandonscore.client.particle.IBCParticleFactory;
import com.brandon3055.brandonscore.lib.Vec3D;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public class ParticleEnergy extends EntityFX {
	public double originalX;
	public double originalY;
	public double originalZ;
	public double targetX;
	public double targetY;
	public double targetZ;
	public int particle;
	public boolean expand = true;

	public ParticleEnergy(World world, double x, double y, double z, double tX, double tY, double tZ, int particle, boolean expand) {
		this(world, x, y, z, tX, tY, tZ, particle);
		this.expand = expand;
		this.particleRed = 185F / 255F;
		this.particleGreen = 0.0F;
		this.particleBlue = 197F / 255F;
		this.particleScale = 0F;
	}

	public ParticleEnergy(World world, double x, double y, double z, double tX, double tY, double tZ, int particle) {
		super(world, x, y, z, 0.0D, 0.0D, 0.0D);
		this.xSpeed = 0;
		this.ySpeed = 0;
		this.zSpeed = 0;
		this.originalX = x;
		this.originalY = y;
		this.originalZ = z;
		this.targetX = tX;
		this.targetY = tY;
		this.targetZ = tZ;
		this.particle = particle;

		this.particleTextureIndexX = 0;
		this.particleTextureIndexY = 0;

		this.particleRed = particle == 1 ? 0F : 1.0F;
		this.particleGreen = particle == 1 ? 1F : 0.2F;
		this.particleBlue = particle == 1 ? 1F : 0.0F;
		//this.particleScale *= 0.05f + world.rand.nextFloat()*0.005;
		//this.particleScale *= par8;
		this.particleScale = 0.5F;
		this.particleMaxAge = 80;
	}

    @Override
    public boolean isTransparent() {
        return true;
    }

    //TODO Great a base particle class!
    @Override
	public void onUpdate() {
//		prevPosX = posX;
//		prevPosY = posY;
//		prevPosZ = posZ;
//
//		if (!expand && particleScale < 0.7){
//			particleScale += 0.05;
//		}
//
//		particleAlpha = (1F - ((float) particleAge / (float) particleMaxAge)) * 0.5F;
//
//		if (particleAge++ >= particleMaxAge) {
//            setExpired();
//        }
//		moveEntity(xSpeed, ySpeed, zSpeed);// also important if you want your particle to move
//		float speedMultiplyer = 0.000501F;
//		float ySpeedMultiplyer = 0.1006F;
//		if (expand) {
//			xSpeed = xSpeed + (targetX - posX) * speedMultiplyer;
//			ySpeed = ySpeed + (targetY - posY) * speedMultiplyer;
//			zSpeed = zSpeed + (targetZ - posZ) * speedMultiplyer;
//		}else
//		{
//			xSpeed = (targetX - posX) * speedMultiplyer;
//			ySpeed = (targetY - posY) * ySpeedMultiplyer;
//			zSpeed = (targetZ - posZ) * speedMultiplyer;
//		}
//		if (expand && Math.abs(targetX - posX) < 0.01 && Math.abs(targetY - posY) < 0.01 && Math.abs(targetZ - posZ) < 0.01){
//			xSpeed = 0;
//			ySpeed = 0;
//			zSpeed = 0;
//			particleRed = 0F;
//			particleGreen = 1F;
//			particleBlue = 1F;
//			particleScale = 2;
//			particleAlpha = (1F - ((float) particleAge / (float) particleMaxAge)) * 1F;
//		}

	}

	@Override
	//@SideOnly(Side.CLIENT)
    public void renderParticle(VertexBuffer worldRendererIn, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
		float minU = 0.0F + 0F;//(float)this.particleTextureIndexX / 32.0F;
		float maxU = 0.0F + 0.1245F;//minU + 0.124F;
		float minV = 0F;//(float)this.particleTextureIndexY / 32.0F;
		float maxV = 0.1245F;//minV + 0.124F;
		float drawScale = 0.1F * this.particleScale;
//        float minU = (float)this.particleTextureIndexX / 16.0F;
//        float maxU = minU + 0.0624375F;
//        float minV = (float)this.particleTextureIndexY / 16.0F;
//        float maxV = minV + 0.0624375F;
//        float drawScale = 0.1F * this.particleScale;

        if (this.particleTexture != null)
        {
            minU = this.particleTexture.getMinU();
            maxU = this.particleTexture.getMaxU();
            minV = this.particleTexture.getMinV();
            maxV = this.particleTexture.getMaxV();
        }

        float f5 = (float)(this.prevPosX + (this.posX - this.prevPosX) * (double)partialTicks - interpPosX);
        float f6 = (float)(this.prevPosY + (this.posY - this.prevPosY) * (double)partialTicks - interpPosY);
        float f7 = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * (double)partialTicks - interpPosZ);
        int i = 1;//this.getBrightnessForRender(partialTicks);
        int j = i >> 16 & 65535;
        int k = i & 65535;
        worldRendererIn.pos((double)(f5 - rotationX * drawScale - rotationXY * drawScale), (double)(f6 - rotationZ * drawScale), (double)(f7 - rotationYZ * drawScale - rotationXZ * drawScale)).tex((double)maxU, (double)maxV).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
        worldRendererIn.pos((double)(f5 - rotationX * drawScale + rotationXY * drawScale), (double)(f6 + rotationZ * drawScale), (double)(f7 - rotationYZ * drawScale + rotationXZ * drawScale)).tex((double)maxU, (double)minV).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
        worldRendererIn.pos((double)(f5 + rotationX * drawScale + rotationXY * drawScale), (double)(f6 + rotationZ * drawScale), (double)(f7 + rotationYZ * drawScale + rotationXZ * drawScale)).tex((double)minU, (double)minV).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
        worldRendererIn.pos((double)(f5 + rotationX * drawScale - rotationXY * drawScale), (double)(f6 - rotationZ * drawScale), (double)(f7 + rotationYZ * drawScale - rotationXZ * drawScale)).tex((double)minU, (double)maxV).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();

	}

    public static class Factory implements IBCParticleFactory {

        @Override
        public EntityFX getEntityFX(int particleID, World world, Vec3D pos, Vec3D speed, int... args) {
            return new ParticleEnergy(world, pos.x, pos.y, pos.z, speed.x, speed.y, speed.z, 0);
        }
    }
}
