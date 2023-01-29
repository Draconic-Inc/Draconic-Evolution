package com.brandon3055.draconicevolution.client.render.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

import com.brandon3055.draconicevolution.client.handler.ResourceHandler;

/**
 * Created by Brandon on 9/02/2015.
 */
public class ParticleEnergyField extends EntityFX {

    /**
     * Particle Type 0 = Energy Ring, 1 = single particle
     */
    private int type;

    private boolean advanced;
    private boolean renderParticle = true;

    public ParticleEnergyField(World world, double x, double y, double z, int maxAge, int type, boolean advanced) {
        super(world, x, y, z, 0.0D, 0.0D, 0.0D);
        this.particleRed = 1F;
        this.particleGreen = 1F;
        this.particleBlue = 1F;
        this.noClip = true;
        this.motionX = 0.0D;
        this.motionY = 0.0D;
        this.motionZ = 0.0D;
        this.particleMaxAge = maxAge;
        this.type = type;
        this.advanced = advanced;
        this.setSize(1F, 1F);
    }

    public void update(boolean render) {
        for (this.renderParticle = render; this.particleMaxAge - this.particleAge < 4; ++this.particleMaxAge) {}
    }

    @Override
    public void onUpdate() {

        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        if (this.particleAge++ >= this.particleMaxAge) {
            this.setDead();
        }
    }

    @Override
    public void renderParticle(Tessellator tessellator, float partialTick, float rotX, float rotXZ, float rotZ,
            float rotYZ, float rotXY) {
        if (!renderParticle) return;
        tessellator.draw();
        GL11.glPushMatrix();
        // GL11.glPushAttrib(GL11.GL_ATTRIB_STACK_DEPTH);
        GL11.glDepthMask(false);
        ResourceHandler.bindParticles();

        float minU = 0.0F + 0.125F * (advanced ? 4 : 3);
        float maxU = 0.0F + 0.125F * (advanced ? 5 : 4); // minU + 0.124F;
        if (type == 2) {
            minU = 0.0F + 0.125F * 5;
            maxU = 0.0F + 0.125F * 6;
        }
        float minV = 0F; // (float)this.particleTextureIndexY / 32.0F;
        float maxV = 0.123F; // minV + 0.124F;
        float drawScale = 0.2f;

        if (this.particleIcon != null) {
            minU = this.particleIcon.getMinU();
            maxU = this.particleIcon.getMaxU();
            minV = this.particleIcon.getMinV();
            maxV = this.particleIcon.getMaxV();
        }

        float drawX = (float) (this.prevPosX + (this.posX - this.prevPosX) * (double) partialTick - interpPosX);
        float drawY = (float) (this.prevPosY + (this.posY - this.prevPosY) * (double) partialTick - interpPosY);
        float drawZ = (float) (this.prevPosZ + (this.posZ - this.prevPosZ) * (double) partialTick - interpPosZ);

        if (type == 0 || type == 2) {
            tessellator.startDrawingQuads();
            tessellator.setBrightness(200);
            tessellator.setColorRGBA_F(this.particleRed, this.particleGreen, this.particleBlue, 1f);

            double pCount = Minecraft.getMinecraft().gameSettings.fancyGraphics ? 35 : 15;
            for (int i = 0; i < pCount; i++) {
                double rot = i / pCount * (3.141 * 2D) + particleAge / 3D; // 1D;//i / 10D;
                double offset = 0.4;
                double ox = Math.sin(rot) * offset;
                double oz = Math.cos(rot) * offset;

                float drawXX = drawX + (float) ox;
                float drawZZ = drawZ + (float) oz;

                tessellator.addVertexWithUV(
                        (double) (drawXX - rotX * drawScale - rotYZ * drawScale),
                        (double) (drawY - rotXZ * drawScale),
                        (double) (drawZZ - rotZ * drawScale - rotXY * drawScale),
                        (double) maxU,
                        (double) maxV);
                tessellator.addVertexWithUV(
                        (double) (drawXX - rotX * drawScale + rotYZ * drawScale),
                        (double) (drawY + rotXZ * drawScale),
                        (double) (drawZZ - rotZ * drawScale + rotXY * drawScale),
                        (double) maxU,
                        (double) minV);
                tessellator.addVertexWithUV(
                        (double) (drawXX + rotX * drawScale + rotYZ * drawScale),
                        (double) (drawY + rotXZ * drawScale),
                        (double) (drawZZ + rotZ * drawScale + rotXY * drawScale),
                        (double) minU,
                        (double) minV);
                tessellator.addVertexWithUV(
                        (double) (drawXX + rotX * drawScale - rotYZ * drawScale),
                        (double) (drawY - rotXZ * drawScale),
                        (double) (drawZZ + rotZ * drawScale - rotXY * drawScale),
                        (double) minU,
                        (double) maxV);
            }

            tessellator.draw();
        } else if (type == 1) {

            tessellator.startDrawingQuads();
            tessellator.setBrightness(200);
            tessellator.setColorRGBA_F(this.particleRed, this.particleGreen, this.particleBlue, 1f);

            tessellator.addVertexWithUV(
                    (double) (drawX - rotX * drawScale - rotYZ * drawScale),
                    (double) (drawY - rotXZ * drawScale),
                    (double) (drawZ - rotZ * drawScale - rotXY * drawScale),
                    (double) maxU,
                    (double) maxV);
            tessellator.addVertexWithUV(
                    (double) (drawX - rotX * drawScale + rotYZ * drawScale),
                    (double) (drawY + rotXZ * drawScale),
                    (double) (drawZ - rotZ * drawScale + rotXY * drawScale),
                    (double) maxU,
                    (double) minV);
            tessellator.addVertexWithUV(
                    (double) (drawX + rotX * drawScale + rotYZ * drawScale),
                    (double) (drawY + rotXZ * drawScale),
                    (double) (drawZ + rotZ * drawScale + rotXY * drawScale),
                    (double) minU,
                    (double) minV);
            tessellator.addVertexWithUV(
                    (double) (drawX + rotX * drawScale - rotYZ * drawScale),
                    (double) (drawY - rotXZ * drawScale),
                    (double) (drawZ + rotZ * drawScale - rotXY * drawScale),
                    (double) minU,
                    (double) maxV);

            tessellator.draw();
        }

        // GL11.glDepthMask(true);
        // GL11.glPopAttrib();
        GL11.glPopMatrix();

        ResourceHandler.bindDefaultParticles();
        tessellator.startDrawingQuads();
    }
}
