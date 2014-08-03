package com.brandon3055.draconicevolution.client.render;

import com.brandon3055.draconicevolution.common.core.handler.ParticleHandler;
import com.brandon3055.draconicevolution.common.core.utills.LogHelper;
import com.brandon3055.draconicevolution.common.lib.References;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

/**
 * Created by Brandon on 27/07/2014.
 */
@SideOnly(Side.CLIENT)
public final class Particles {

	public static class EnergyBeamParticle extends EntityFX {

		private int direction = 0;
		private double masterX = 0;
		private double masterZ = 0;
		private float rotation = 0;
		private boolean mirror = false;
		private double[] trailX = new double[15];
		private double[] trailY = new double[15];
		private double[] trailZ = new double[15];

		public EnergyBeamParticle(World world, double x, double y, double z, double x1, double z1, int direction, boolean mirror) {
			super(world, x, y, z, 0.0D, 0.0D, 0.0D);
			float speed = 0.04F;
			this.motionX = 0;
			this.motionY = 0;
			this.motionZ = 0;
			this.masterX = x1;
			this.masterZ = z1;
			this.direction = direction;
			this.mirror = mirror;

			switch (direction){
				case 0:
					this.motionX = speed;
					break;
				case 1:
					this.motionX = -speed;
					break;
				case 2:
					this.motionZ = speed;
					break;
				case 3:
					this.motionZ = -speed;
					break;
			}

			this.particleTextureIndexX = 0;
			this.particleTextureIndexY = 0;
			this.particleScale = 1F;
			this.noClip = true;
			this.particleMaxAge = 300;
		}

		@Override
		public void onUpdate() {
			if (particleAge > particleMaxAge || this.getDistanceSq(masterX, posY, masterZ) < 0.02) setDead();
			if (this.getDistanceSq(masterX, posY, masterZ) < 0.1) {
				motionX = (masterX - posX) * 0.1F;
				motionY = (Math.floor(posY) + 0.5 - posY) * 0.1F;
				motionZ = (masterZ - posZ) * 0.1F;
				prevPosX = posX;
				prevPosY = posY;
				prevPosZ = posZ;
				moveEntity(motionX, motionY, motionZ);
				return;
			}else {
				trailX[0] = posX;
				trailY[0] = posY;
				trailZ[0] = posZ;
				for (int i = 14; i >= 0; i--){
					if (i > 0) {
						trailX[i] = trailX[i-1];
						trailY[i] = trailY[i-1];
						trailZ[i] = trailZ[i-1];
					}
				}

				float multiplier = 0.27F;
				double masterY = Math.floor(posY) + 0.5;
				if (direction == 0 || direction == 1) {
					posZ = masterZ + Math.sin(rotation) * multiplier;
					posY = masterY + Math.cos(rotation) * multiplier;
				}else{
					posX = masterX + Math.sin(rotation) * multiplier;
					posY = masterY + Math.cos(rotation) * multiplier;
				}
				if (mirror){
					float modifier = 3F;
					if (direction == 0 || direction == 1) {
						posZ = masterZ + Math.sin(rotation + modifier) * multiplier;
						posY = masterY + Math.cos(rotation + modifier) * multiplier;
					}else{
						posX = masterX + Math.sin(rotation + modifier) * multiplier;
						posY = masterY + Math.cos(rotation + modifier) * multiplier;
					}
				}
				setPosition(posX, posY, posZ);
			}
			prevPosX = posX;
			prevPosY = posY;
			prevPosZ = posZ;
			if (direction == 0 || direction == 1)
				moveEntity(motionX, 0, 0);
			else
				moveEntity(0, 0, motionZ);
			particleAge ++;
			if (direction == 0 || direction == 3)
				rotation -= 0.15;
			else
				rotation += 0.15;
		}
		@Override
		@SideOnly(Side.CLIENT)
		public void renderParticle(Tessellator tesselator, float par2, float par3, float par4, float par5, float par6, float par7) {//Note U=X V=Y

			tesselator.draw();
			Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation(References.RESOURCESPREFIX + "textures/particle/particles.png"));
			tesselator.startDrawingQuads();
			tesselator.setBrightness(200);//make sure you have this!!


			float minU = 0.0F + 0F;//(float)this.particleTextureIndexX / 32.0F;
			float maxU = 0.0F + 0.1245F;//minU + 0.124F;
			float minV = 0F;//(float)this.particleTextureIndexY / 32.0F;
			float maxV = 0.1245F;//minV + 0.124F;
			float drawScale = 0.1F * particleScale;

			if (this.particleIcon != null) {
				minU = particleIcon.getMinU();
				maxU = particleIcon.getMaxU();
				minV = particleIcon.getMinV();
				maxV = particleIcon.getMaxV();
			}

			float drawX = (float) (prevPosX + (posX - prevPosX) * (double) par2 - interpPosX);
			float drawY = (float) (prevPosY + (posY - prevPosY) * (double) par2 - interpPosY);
			float drawZ = (float) (prevPosZ + (posZ - prevPosZ) * (double) par2 - interpPosZ);

			tesselator.setColorRGBA_F(particleRed, particleGreen, particleBlue, particleAlpha);

			tesselator.addVertexWithUV((double) (drawX - par3 * drawScale - par6 * drawScale), (double) (drawY - par4 * drawScale), (double) (drawZ - par5 * drawScale - par7 * drawScale), (double) maxU, (double) maxV);
			tesselator.addVertexWithUV((double) (drawX - par3 * drawScale + par6 * drawScale), (double) (drawY + par4 * drawScale), (double) (drawZ - par5 * drawScale + par7 * drawScale), (double) maxU, (double) minV);
			tesselator.addVertexWithUV((double) (drawX + par3 * drawScale + par6 * drawScale), (double) (drawY + par4 * drawScale), (double) (drawZ + par5 * drawScale + par7 * drawScale), (double) minU, (double) minV);
			tesselator.addVertexWithUV((double) (drawX + par3 * drawScale - par6 * drawScale), (double) (drawY - par4 * drawScale), (double) (drawZ + par5 * drawScale - par7 * drawScale), (double) minU, (double) maxV);

			for (int i = 0; i <= 14; i++){
				GL11.glPushMatrix();
				drawX = (float) (trailX[i] + (trailX[i] - trailX[i]) * (double) par2 - interpPosX);
				drawY = (float) (trailY[i] + (trailY[i] - trailY[i]) * (double) par2 - interpPosY);
				drawZ = (float) (trailZ[i] + (trailZ[i] - trailZ[i]) * (double) par2 - interpPosZ);
				float scale = 0.1F * (1F - ((float)i / 14F));
				float scale2 = (1F - ((float)i / 14F));

				if (!mirror) {
					tesselator.setColorRGBA_F(1F, scale2, scale2, scale2);
				}else{
					tesselator.setColorRGBA_F(scale2, 1F, scale2, scale2);
				}

				tesselator.addVertexWithUV((double) (drawX - par3 * scale - par6 * scale), (double) (drawY - par4 * scale), (double) (drawZ - par5 * scale - par7 * scale), (double) maxU, (double) maxV);
				tesselator.addVertexWithUV((double) (drawX - par3 * scale + par6 * scale), (double) (drawY + par4 * scale), (double) (drawZ - par5 * scale + par7 * scale), (double) maxU, (double) minV);
				tesselator.addVertexWithUV((double) (drawX + par3 * scale + par6 * scale), (double) (drawY + par4 * scale), (double) (drawZ + par5 * scale + par7 * scale), (double) minU, (double) minV);
				tesselator.addVertexWithUV((double) (drawX + par3 * scale - par6 * scale), (double) (drawY - par4 * scale), (double) (drawZ + par5 * scale - par7 * scale), (double) minU, (double) maxV);
				GL11.glPopMatrix();
			}

			tesselator.draw();
			Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("textures/particle/particles.png"));
			tesselator.startDrawingQuads();

		}
	}

	public static class EnergyTransferParticle extends EntityFX {

		private double targetX;
		private double targetY;
		private double targetZ;
		private boolean passive;

		public EnergyTransferParticle(World world, double x, double y, double z, double tX, double tY, double tZ, boolean passive) {
			super(world, x, y, z, 0.0D, 0.0D, 0.0D);
			this.passive = passive;
			this.targetX = tX;
			this.targetY = tY;
			this.targetZ = tZ;
			this.motionX = 0;
			this.motionY = 0;
			this.motionZ = 0;
			this.particleTextureIndexX = 0;
			this.particleTextureIndexY = 0;
			this.particleScale = world.rand.nextFloat() + 0.5F;
			this.particleMaxAge = 100;
			this.noClip = true;
			if (!passive){
				this.particleRed = 0F;
				this.particleGreen = 94F/255F;
				this.particleBlue = 250F/255F;
			}
		}

		@Override
		public void onUpdate() {
			if (particleAge > particleMaxAge) setDead();
			if (particleAge > particleMaxAge || this.getDistanceSq(targetX, targetY, targetZ) < 0.05) setDead();
			particleAge ++;
			motionX += (targetX - posX) * 0.001F;
			motionY += (targetY - posY) * 0.001F;
			motionZ += (targetZ - posZ) * 0.001F;
			prevPosX = posX;
			prevPosY = posY;
			prevPosZ = posZ;
			moveEntity(motionX, motionY, motionZ);
			return;
		}
		@Override
		@SideOnly(Side.CLIENT)
		public void renderParticle(Tessellator tesselator, float par2, float par3, float par4, float par5, float par6, float par7) {//Note U=X V=Y

			tesselator.draw();
			Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation(References.RESOURCESPREFIX + "textures/particle/particles.png"));
			tesselator.startDrawingQuads();
			tesselator.setBrightness(200);//make sure you have this!!


			float minU = 0.0F + 0F;//(float)this.particleTextureIndexX / 32.0F;
			float maxU = 0.0F + 0.1245F;//minU + 0.124F;
			float minV = 0F;//(float)this.particleTextureIndexY / 32.0F;
			float maxV = 0.1245F;//minV + 0.124F;
			float drawScale = 0.1F * this.particleScale;

			if (this.particleIcon != null) {
				minU = this.particleIcon.getMinU();
				maxU = this.particleIcon.getMaxU();
				minV = this.particleIcon.getMinV();
				maxV = this.particleIcon.getMaxV();
			}

			float drawX = (float) (this.prevPosX + (this.posX - this.prevPosX) * (double) par2 - interpPosX);
			float drawY = (float) (this.prevPosY + (this.posY - this.prevPosY) * (double) par2 - interpPosY);
			float drawZ = (float) (this.prevPosZ + (this.posZ - this.prevPosZ) * (double) par2 - interpPosZ);

			if (passive)
				tesselator.setColorRGBA_F((float)this.getDistanceSq(targetX, targetY, targetZ)*5F*this.particleRed, (float)this.getDistanceSq(targetX, targetY, targetZ)*5F*this.particleGreen, this.particleBlue, this.particleAlpha);
			else
				tesselator.setColorRGBA_F(this.particleRed, this.particleGreen, this.particleBlue, (float)this.getDistanceSq(targetX, targetY, targetZ)*5F);

			//tesselator.setColorRGBA(0, 255, 255, (int) (this.particleAlpha * 255F));

			tesselator.addVertexWithUV((double) (drawX - par3 * drawScale - par6 * drawScale), (double) (drawY - par4 * drawScale), (double) (drawZ - par5 * drawScale - par7 * drawScale), (double) maxU, (double) maxV);
			tesselator.addVertexWithUV((double) (drawX - par3 * drawScale + par6 * drawScale), (double) (drawY + par4 * drawScale), (double) (drawZ - par5 * drawScale + par7 * drawScale), (double) maxU, (double) minV);
			tesselator.addVertexWithUV((double) (drawX + par3 * drawScale + par6 * drawScale), (double) (drawY + par4 * drawScale), (double) (drawZ + par5 * drawScale + par7 * drawScale), (double) minU, (double) minV);
			tesselator.addVertexWithUV((double) (drawX + par3 * drawScale - par6 * drawScale), (double) (drawY - par4 * drawScale), (double) (drawZ + par5 * drawScale - par7 * drawScale), (double) minU, (double) maxV);

			tesselator.draw();
			Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("textures/particle/particles.png"));
			tesselator.startDrawingQuads();

		}
	}

}
