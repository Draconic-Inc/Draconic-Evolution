package com.brandon3055.draconicevolution.client.render.particle;

import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.World;

import com.brandon3055.draconicevolution.client.handler.ResourceHandler;

public class ParticleEnergy extends EntityFX {

    public double originalX;
    public double originalY;
    public double originalZ;
    public double targetX;
    public double targetY;
    public double targetZ;
    public int particle;
    public boolean expand = true;

    public ParticleEnergy(World world, double x, double y, double z, double tX, double tY, double tZ, int particle,
            boolean expand) {
        this(world, x, y, z, tX, tY, tZ, particle);
        this.expand = expand;
        this.particleRed = 185F / 255F;
        this.particleGreen = 0.0F;
        this.particleBlue = 197F / 255F;
        this.particleScale = 0F;
    }

    public ParticleEnergy(World world, double x, double y, double z, double tX, double tY, double tZ, int particle) {
        super(world, x, y, z, 0.0D, 0.0D, 0.0D);
        this.motionX = 0;
        this.motionY = 0;
        this.motionZ = 0;
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
        // this.particleScale *= 0.05f + world.rand.nextFloat()*0.005;
        // this.particleScale *= par8;
        this.particleScale = 0.5F;
        this.particleMaxAge = 80;
        this.noClip = true;
    }

    @Override
    public void onUpdate() {
        prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;

        if (!expand && particleScale < 0.7) {
            particleScale += 0.05;
        }

        particleAlpha = (1F - ((float) particleAge / (float) particleMaxAge)) * 0.5F;

        if (particleAge++ >= particleMaxAge) {
            setDead();
        }
        moveEntity(motionX, motionY, motionZ); // also important if you want your particle to move
        float speedMultiplyer = 0.000501F;
        float ySpeedMultiplyer = 0.1006F;
        if (expand) {
            motionX = motionX + (targetX - posX) * speedMultiplyer;
            motionY = motionY + (targetY - posY) * speedMultiplyer;
            motionZ = motionZ + (targetZ - posZ) * speedMultiplyer;
        } else {
            motionX = (targetX - posX) * speedMultiplyer;
            motionY = (targetY - posY) * ySpeedMultiplyer;
            motionZ = (targetZ - posZ) * speedMultiplyer;
        }
        if (expand && Math.abs(targetX - posX) < 0.01
                && Math.abs(targetY - posY) < 0.01
                && Math.abs(targetZ - posZ) < 0.01) {
            motionX = 0;
            motionY = 0;
            motionZ = 0;
            particleRed = 0F;
            particleGreen = 1F;
            particleBlue = 1F;
            particleScale = 2;
            particleAlpha = (1F - ((float) particleAge / (float) particleMaxAge)) * 1F;
        }
    }

    @Override
    // @SideOnly(Side.CLIENT)
    public void renderParticle(Tessellator tesselator, float par2, float par3, float par4, float par5, float par6,
            float par7) { // Note U=X V=Y

        tesselator.draw();
        ResourceHandler.bindParticles();
        tesselator.startDrawingQuads();
        tesselator.setBrightness(200); // make sure you have this!!

        float minU = 0.0F + 0F; // (float)this.particleTextureIndexX / 32.0F;
        float maxU = 0.0F + 0.1245F; // minU + 0.124F;
        float minV = 0F; // (float)this.particleTextureIndexY / 32.0F;
        float maxV = 0.1245F; // minV + 0.124F;
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

        tesselator.setColorRGBA_F(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha);
        // tesselator.setColorRGBA(0, 255, 255, (int) (this.particleAlpha * 255F));

        tesselator.addVertexWithUV(
                (double) (drawX - par3 * drawScale - par6 * drawScale),
                (double) (drawY - par4 * drawScale),
                (double) (drawZ - par5 * drawScale - par7 * drawScale),
                (double) maxU,
                (double) maxV);
        tesselator.addVertexWithUV(
                (double) (drawX - par3 * drawScale + par6 * drawScale),
                (double) (drawY + par4 * drawScale),
                (double) (drawZ - par5 * drawScale + par7 * drawScale),
                (double) maxU,
                (double) minV);
        tesselator.addVertexWithUV(
                (double) (drawX + par3 * drawScale + par6 * drawScale),
                (double) (drawY + par4 * drawScale),
                (double) (drawZ + par5 * drawScale + par7 * drawScale),
                (double) minU,
                (double) minV);
        tesselator.addVertexWithUV(
                (double) (drawX + par3 * drawScale - par6 * drawScale),
                (double) (drawY - par4 * drawScale),
                (double) (drawZ + par5 * drawScale - par7 * drawScale),
                (double) minU,
                (double) maxV);

        tesselator.draw();
        ResourceHandler.bindDefaultParticles();
        tesselator.startDrawingQuads();
    }
}
