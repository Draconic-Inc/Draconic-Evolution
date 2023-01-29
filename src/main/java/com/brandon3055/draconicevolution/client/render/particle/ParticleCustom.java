package com.brandon3055.draconicevolution.client.render.particle;

import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.World;

import com.brandon3055.draconicevolution.client.handler.ResourceHandler;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ParticleCustom extends EntityFX {

    public int red = 0;
    public int green = 0;
    public int blue = 0;
    public int maxAge = 0;
    public int fadeTime = 0;
    public int fadeLength = 0;
    public float gravity = 0F;

    public ParticleCustom(World world, double spawnX, double spawnY, double spawnZ, float spawnMotionX,
            float spawnMotionY, float spawnMotionZ, float scale, boolean canCollide, int index) {
        super(world, spawnX, spawnY, spawnZ, 0.0D, 0.0D, 0.0D);
        this.motionX = spawnMotionX; // initial motion value X
        this.motionY = spawnMotionY; // initial motion value Y
        this.motionZ = spawnMotionZ; // initial motion value Z
        this.particleTextureIndexX = index - 1; //
        this.particleTextureIndexY = 0;

        this.particleScale = scale;

        this.noClip = !canCollide;
    }

    @Override
    public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        if (this.particleAge++ >= this.maxAge) {
            this.particleAlpha = (float) ((float) fadeTime) / ((float) fadeLength);
            if (fadeTime <= 0) this.setDead();
            fadeTime--;
        }

        this.moveEntity(this.motionX, this.motionY, this.motionZ); // also important if you want your particle to move
        // this.motionX = motionX * (1 - (worldObj.rand.nextFloat() / 10F));
        this.motionY = motionY - (gravity / 100F);
        // this.motionZ = motionZ * (1 - (worldObj.rand.nextFloat() / 10F));

    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderParticle(Tessellator tesselator, float par2, float par3, float par4, float par5, float par6,
            float par7) { // Note U=X V=Y

        tesselator.draw();
        ResourceHandler.bindParticles();
        tesselator.startDrawingQuads();
        tesselator.setBrightness(200); // make sure you have this!!

        float minU = (float) this.particleTextureIndexX / 8.0F;
        float maxU = minU + 0.124F;
        float minV = (float) this.particleTextureIndexY / 8.0F;
        float maxV = minV + 0.124F;
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

        tesselator.setColorRGBA(red, green, blue, (int) (this.particleAlpha * 255F));

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
