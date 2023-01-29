package com.brandon3055.draconicevolution.client.render.particle;

import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.World;

import com.brandon3055.draconicevolution.client.handler.ResourceHandler;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ParticleDistortion extends EntityFX {

    double originalX;
    double originalZ;

    public ParticleDistortion(World par1World, double par2, double par4, double par6, float par8, float par9,
            float par10, float scale) {
        this(par1World, par2, par4, par6, 1.0F, par8, par9, par10, scale);
    }

    public ParticleDistortion(World world, double par2, double par4, double par6, float par8, float par9, float par10,
            float par11, float scale) {
        super(world, par2, par4, par6, 0.0D, 0.0D, 0.0D);
        this.motionX = par9; // initial motion value X
        this.motionY = par10; // initial motion value Y
        this.motionZ = par11; // initial motion value Z
        this.originalX = par9;
        this.originalZ = par11;
        if (par9 == 0.0F) {
            par9 = 1.0F;
        }
        // float var12 = (float) Math.random() * 0.4F + 0.6F;
        this.particleTextureIndexX = 0; //
        this.particleTextureIndexY = 0;

        this.particleRed = 0.7F; // RGB of your particle
        this.particleGreen = 0.8F;
        this.particleBlue = 1.0F;
        // this.particleScale *= 0.05f + world.rand.nextFloat()*0.005;
        // this.particleScale *= par8;
        this.particleScale = scale;

        this.particleMaxAge = 40 + world.rand.nextInt(40); // how soon the particle dies. You can use randomizer for
                                                           // this
        this.noClip = true; // does your particle collide with blocks?
    }

    @Override
    public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        if (this.particleAge++ >= this.particleMaxAge) {
            this.setDead(); // make sure to have this
        }
        this.moveEntity(this.motionX, this.motionY, this.motionZ); // also important if you want your particle to move
        this.motionX = motionX * (1 - (worldObj.rand.nextFloat() / 10F));
        this.motionY = motionY * (1 - (worldObj.rand.nextFloat() / 10F));
        this.motionZ = motionZ * (1 - (worldObj.rand.nextFloat() / 10F));
        this.particleAlpha = (1F - ((float) this.particleAge / (float) this.particleMaxAge)) * 0.5F;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderParticle(Tessellator tessellator, float par2, float par3, float par4, float par5, float par6,
            float par7) { // Note U=X V=Y

        tessellator.draw();
        ResourceHandler.bindParticles();
        tessellator.startDrawingQuads();
        tessellator.setBrightness(200); // make sure you have this!!

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

        tessellator.setColorRGBA_F(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha);
        tessellator.setColorRGBA(0, 255, 255, (int) (this.particleAlpha * 255F));

        tessellator.addVertexWithUV(
                (double) (drawX - par3 * drawScale - par6 * drawScale),
                (double) (drawY - par4 * drawScale),
                (double) (drawZ - par5 * drawScale - par7 * drawScale),
                (double) maxU,
                (double) maxV);
        tessellator.addVertexWithUV(
                (double) (drawX - par3 * drawScale + par6 * drawScale),
                (double) (drawY + par4 * drawScale),
                (double) (drawZ - par5 * drawScale + par7 * drawScale),
                (double) maxU,
                (double) minV);
        tessellator.addVertexWithUV(
                (double) (drawX + par3 * drawScale + par6 * drawScale),
                (double) (drawY + par4 * drawScale),
                (double) (drawZ + par5 * drawScale + par7 * drawScale),
                (double) minU,
                (double) minV);
        tessellator.addVertexWithUV(
                (double) (drawX + par3 * drawScale - par6 * drawScale),
                (double) (drawY - par4 * drawScale),
                (double) (drawZ + par5 * drawScale - par7 * drawScale),
                (double) minU,
                (double) maxV);

        tessellator.draw();
        ResourceHandler.bindDefaultParticles();
        tessellator.startDrawingQuads();
    }
}
