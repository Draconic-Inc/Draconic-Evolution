package com.brandon3055.draconicevolution.client.render.effect;

import com.brandon3055.draconicevolution.blocks.energynet.tileentity.TileCrystalBase;
import com.brandon3055.draconicevolution.client.DEParticles;
import com.brandon3055.draconicevolution.client.handler.ClientEventHandler;
import com.brandon3055.draconicevolution.helpers.ResourceHelperDE;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

/**
 * Created by brandon3055 on 29/11/2016.
 */
public class CrystalFXRing extends CrystalGLFXBase<TileCrystalBase> {

    public CrystalFXRing(World worldIn, TileCrystalBase tile) {
        super(worldIn, tile);
        this.particleTextureIndexX = 3 + tile.getTier();
        this.particleAge = worldIn.rand.nextInt(1024);
    }

    @Override
    public void onUpdate() {
        if (ticksTillDeath-- <= 0) {
            setExpired();
        }

//        particleTextureIndexX = (ClientEventHandler.elapsedTicks) % 7;
//        particleTextureIndexY = 3;

//        particleTextureIndexX = (ClientEventHandler.elapsedTicks) % 5;
//        particleTextureIndexY = 1;

        float[] r = {0.0F, 0.8F, 1.0F};
        float[] g = {0.8F, 0.1F, 0.7F};
        float[] b = {1F,   1F,   0.2F};

        particleRed = r[tile.getTier()];
        particleGreen = g[tile.getTier()];
        particleBlue = b[tile.getTier()];
    }

    @Override
    public void renderParticle(VertexBuffer vertexbuffer, Entity entity, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
        rand.setSeed(3490276L);
        float animTime = ClientEventHandler.elapsedTicks + particleAge + partialTicks;
        GlStateManager.pushMatrix();
        GlStateManager.disableCull();
        ResourceHelperDE.bindTexture(DEParticles.DE_SHEET);

        //region variables

        float renderX = (float) (this.prevPosX + (this.posX - this.prevPosX) * (double) partialTicks - interpPosX);
        float renderY = (float) (this.prevPosY + (this.posY - this.prevPosY) * (double) partialTicks - interpPosY);
        float renderZ = (float) (this.prevPosZ + (this.posZ - this.prevPosZ) * (double) partialTicks - interpPosZ);

        //endregion

        //region GLRender

        double pCount = 100;//Minecraft.getMinecraft().gameSettings.fancyGraphics ? 35 : 15;
        for (int i = 0; i < pCount; i++) {

            double rotation = i / pCount * (3.141 * 2D) + animTime / 80D;

            boolean rBool = rand.nextBoolean();
            float rFloat1 = rand.nextFloat();
            float rFloat2 = rand.nextFloat();
            float rFloat3 = rand.nextFloat();
            float rFloat4 = rand.nextFloat();


            //region Shadow

            float scale = 0.01F + (rFloat4 * 0.05F);
            float a = 1;//sd + 0.1F;
            float r = particleRed;
            float g = particleGreen;
            float b = particleBlue;


            rotation -= 0.05F;
            //endregion

            //region Sub Circular Calculation

            double subRotationRadius = (0.1 * rFloat3) + 0.02;
            double dir = rBool ? 1 : -1;
            double sy = Math.cos(dir * rotation * (rFloat3 * 10) * (1 - (rFloat1 * 0.2F))) * subRotationRadius;
            double sx = Math.sin(dir * rotation * (rFloat3 * 10) * (1 - (rFloat2 * 0.2F))) * subRotationRadius;
            float drawY = renderY + (float) sy;
            double renderRadius = 0.4 + sx;

            //endregion

            //region Circular Calculation
            double ox = Math.sin(rotation) * renderRadius;
            double oz = Math.cos(rotation) * renderRadius;
            float drawX = renderX + (float) ox;
            float drawZ = renderZ + (float) oz;
            //endregion

            particleTextureIndexX = (ClientEventHandler.elapsedTicks) % 5;
            particleTextureIndexY = 1;
            float minU = (float) this.particleTextureIndexX / texturesPerRow;
            float maxU = minU + 1F / texturesPerRow;
            float minV = (float) this.particleTextureIndexY / texturesPerRow;
            float maxV = minV + 1F / texturesPerRow;
            vertexbuffer.pos((double) (drawX - rotationX * scale - rotationXY * scale), (double) (drawY - rotationZ * scale), (double) (drawZ - rotationYZ * scale - rotationXZ * scale)).tex((double) maxU, (double) maxV).color(r, g, b, a).endVertex();
            vertexbuffer.pos((double) (drawX - rotationX * scale + rotationXY * scale), (double) (drawY + rotationZ * scale), (double) (drawZ - rotationYZ * scale + rotationXZ * scale)).tex((double) maxU, (double) minV).color(r, g, b, a).endVertex();
            vertexbuffer.pos((double) (drawX + rotationX * scale + rotationXY * scale), (double) (drawY + rotationZ * scale), (double) (drawZ + rotationYZ * scale + rotationXZ * scale)).tex((double) minU, (double) minV).color(r, g, b, a).endVertex();
            vertexbuffer.pos((double) (drawX + rotationX * scale - rotationXY * scale), (double) (drawY - rotationZ * scale), (double) (drawZ + rotationYZ * scale - rotationXZ * scale)).tex((double) minU, (double) maxV).color(r, g, b, a).endVertex();

            //region Inner
            scale = 0.01F + (rFloat4 * 0.04F) * (float) Math.sin((animTime + i) / 30);
            rotation = i / pCount * (3.141 * 2D) + animTime / 200D;
            rotation -= 0.05F;

            renderRadius = 0.4;
            ox = Math.sin(rotation) * renderRadius;
            oz = Math.cos(rotation) * renderRadius;
            drawX = renderX + (float) ox;
            drawY = renderY;
            drawZ = renderZ + (float) oz;

            r = 0;
            g = 1;
            b = 1;

            particleTextureIndexX = 0;
            particleTextureIndexY = 0;
            minU = (float) this.particleTextureIndexX / texturesPerRow;
            maxU = minU + 1F / texturesPerRow;
            minV = (float) this.particleTextureIndexY / texturesPerRow;
            maxV = minV + 1F / texturesPerRow;
            vertexbuffer.pos((double) (drawX - rotationX * scale - rotationXY * scale), (double) (drawY - rotationZ * scale), (double) (drawZ - rotationYZ * scale - rotationXZ * scale)).tex((double) maxU, (double) maxV).color(r, g, b, a).endVertex();
            vertexbuffer.pos((double) (drawX - rotationX * scale + rotationXY * scale), (double) (drawY + rotationZ * scale), (double) (drawZ - rotationYZ * scale + rotationXZ * scale)).tex((double) maxU, (double) minV).color(r, g, b, a).endVertex();
            vertexbuffer.pos((double) (drawX + rotationX * scale + rotationXY * scale), (double) (drawY + rotationZ * scale), (double) (drawZ + rotationYZ * scale + rotationXZ * scale)).tex((double) minU, (double) minV).color(r, g, b, a).endVertex();
            vertexbuffer.pos((double) (drawX + rotationX * scale - rotationXY * scale), (double) (drawY - rotationZ * scale), (double) (drawZ + rotationYZ * scale - rotationXZ * scale)).tex((double) minU, (double) maxV).color(r, g, b, a).endVertex();

            //endregion
        }


        //endregion

        GlStateManager.popMatrix();
    }
}


//region Base Field Implementation
//        rand.setSeed(3490276L);
//                float animTime = ClientEventHandler.elapsedTicks + particleAge + partialTicks;
//                GlStateManager.pushMatrix();
//                GlStateManager.disableCull();
//                ResourceHelperDE.bindTexture(DEParticles.DE_SHEET);
//
//                //region variables
//
//                float minU = (float) this.particleTextureIndexX / texturesPerRow;
//                float maxU = minU + 1F / texturesPerRow;
//                float minV = (float) this.particleTextureIndexY / texturesPerRow;
//                float maxV = minV + 1F / texturesPerRow;
//                float scale = 0.02F;//0.1F * this.particleScale;
//
//                if (this.particleTexture != null) {
//                minU = this.particleTexture.getMinU();
//                maxU = this.particleTexture.getMaxU();
//                minV = this.particleTexture.getMinV();
//                maxV = this.particleTexture.getMaxV();
//                }
//
//                float renderX = (float) (this.prevPosX + (this.posX - this.prevPosX) * (double) partialTicks - interpPosX);
//                float renderY = (float) (this.prevPosY + (this.posY - this.prevPosY) * (double) partialTicks - interpPosY);
//                float renderZ = (float) (this.prevPosZ + (this.posZ - this.prevPosZ) * (double) partialTicks - interpPosZ);
//
//                //endregio
//
//                //region GLRender
//
//                double pCount = 100;//Minecraft.getMinecraft().gameSettings.fancyGraphics ? 35 : 15;
//                for (int i = 0; i < pCount; i++) {
//
//        double rotation = i / pCount * (3.141 * 2D) + animTime / 80D;
//
//        boolean rBool = rand.nextBoolean();
//        float rFloat1 = rand.nextFloat();
//        float rFloat2 = rand.nextFloat();
//        float rFloat3 = rand.nextFloat();
//
////            int sCount = 1;
////            for (int s = 0; s < sCount; s++) {
//        //region Shadow
//        float sd = 1F;// - s / (float) sCount;
//
//        scale = 0.03F * sd;
//        float a = 1;//sd + 0.1F;
//        float r = sd * particleRed;
//        float g = sd * particleGreen;
//        float b = sd * particleBlue;
//
//
//        rotation -= 0.05F;
//        //endregio
//
//        //region Sub Circular Calculation
//
//        double subRotationRadius = (0.1 * rFloat3) + 0.02;
//        double dir = rBool ? 1 : -1;
//        double sy = Math.cos(dir * rotation * (rFloat3 * 10) * (1 - (rFloat1 * 0.2F))) * subRotationRadius;
//        double sx = Math.sin(dir * rotation * (rFloat3 * 10) * (1 - (rFloat2 * 0.2F))) * subRotationRadius;
//        float drawY = renderY + (float) sy;
//        double renderRadius = 0.4 + sx;
//
//        //endregio
//
//        //region Circular Calculation
//        double ox = Math.sin(rotation) * renderRadius;
//        double oz = Math.cos(rotation) * renderRadius;
//        float drawX = renderX + (float) ox;
//        float drawZ = renderZ + (float) oz;
//        //endregio
//
//        vertexbuffer.pos((double) (drawX - rotationX * scale - rotationXY * scale), (double) (drawY - rotationZ * scale), (double) (drawZ - rotationYZ * scale - rotationXZ * scale)).tex((double) maxU, (double) maxV).color(r, g, b, a).endVertex();
//        vertexbuffer.pos((double) (drawX - rotationX * scale + rotationXY * scale), (double) (drawY + rotationZ * scale), (double) (drawZ - rotationYZ * scale + rotationXZ * scale)).tex((double) maxU, (double) minV).color(r, g, b, a).endVertex();
//        vertexbuffer.pos((double) (drawX + rotationX * scale + rotationXY * scale), (double) (drawY + rotationZ * scale), (double) (drawZ + rotationYZ * scale + rotationXZ * scale)).tex((double) minU, (double) minV).color(r, g, b, a).endVertex();
//        vertexbuffer.pos((double) (drawX + rotationX * scale - rotationXY * scale), (double) (drawY - rotationZ * scale), (double) (drawZ + rotationYZ * scale - rotationXZ * scale)).tex((double) minU, (double) maxV).color(r, g, b, a).endVertex();
//
////            }
//        }
//
//
//        //endregio
//
//        GlStateManager.popMatrix();
//endregion