package com.brandon3055.draconicevolution.client.render.effect;

import codechicken.lib.render.state.GlStateTracker;
import com.brandon3055.brandonscore.client.particle.IGLFXHandler;
import com.brandon3055.draconicevolution.blocks.energynet.tileentity.TileCrystalBase;
import com.brandon3055.draconicevolution.helpers.ResourceHelperDE;
import com.brandon3055.draconicevolution.utils.DETextures;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

/**
 * Created by brandon3055 on 29/11/2016.
 */
public class CrystalFXIO extends CrystalGLFXBase<TileCrystalBase> {

    private long rSeed = 0;

    public CrystalFXIO(World worldIn, TileCrystalBase tile) {
        super(worldIn, tile);
        this.particleTextureIndexX = 3 + tile.getTier();
        this.particleAge = worldIn.rand.nextInt(1024);
        this.rSeed = tile.getPos().toLong();
    }

    @Override
    public int getFXLayer() {
        return 2;
    }

    @Override
    public void onUpdate() {
        if (ticksTillDeath-- <= 0) {
            setExpired();
        }

        float[] r = {0.0F, 0.8F, 1.0F};
        float[] g = {0.8F, 0.1F, 0.7F};
        float[] b = {1F, 1F, 0.2F};

        particleRed = r[tile.getTier()];
        particleGreen = g[tile.getTier()];
        particleBlue = b[tile.getTier()];
    }

    @Override
    public void renderParticle(BufferBuilder vertexbuffer, Entity entity, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
        if (!renderEnabled) {
            return;
        }

        float renderX = (float) (this.posX - interpPosX);
        float renderY = (float) (this.posY - interpPosY);
        float renderZ = (float) (this.posZ - interpPosZ);

        particleScale = 0.2F;


        vertexbuffer.pos((double) (renderX - rotationX * particleScale - rotationXY * particleScale), (double) (renderY - rotationZ * particleScale), (double) (renderZ - rotationYZ * particleScale - rotationXZ * particleScale)).tex(0.5, 0.5).color(particleRed, particleGreen, particleBlue, particleAlpha).endVertex();
        vertexbuffer.pos((double) (renderX - rotationX * particleScale + rotationXY * particleScale), (double) (renderY + rotationZ * particleScale), (double) (renderZ - rotationYZ * particleScale + rotationXZ * particleScale)).tex(0.5, 0.0).color(particleRed, particleGreen, particleBlue, particleAlpha).endVertex();
        vertexbuffer.pos((double) (renderX + rotationX * particleScale + rotationXY * particleScale), (double) (renderY + rotationZ * particleScale), (double) (renderZ + rotationYZ * particleScale + rotationXZ * particleScale)).tex(0.0, 0.0).color(particleRed, particleGreen, particleBlue, particleAlpha).endVertex();
        vertexbuffer.pos((double) (renderX + rotationX * particleScale - rotationXY * particleScale), (double) (renderY - rotationZ * particleScale), (double) (renderZ + rotationYZ * particleScale - rotationXZ * particleScale)).tex(0.0, 0.5).color(particleRed, particleGreen, particleBlue, particleAlpha).endVertex();


//
//        rand.setSeed(rSeed);
//        float animTime = ClientEventHandler.elapsedTicks + particleAge + partialTicks;
//
//        //region variables
//
////        float renderX = (float) (this.prevPosX + (this.posX - this.prevPosX) * (double) partialTicks - interpPosX);
////        float renderY = (float) (this.prevPosY + (this.posY - this.prevPosY) * (double) partialTicks - interpPosY);
////        float renderZ = (float) (this.prevPosZ + (this.posZ - this.prevPosZ) * (double) partialTicks - interpPosZ);
//        float renderX = (float) (this.posX - interpPosX);
//        float renderY = (float) (this.posY - interpPosY);
//        float renderZ = (float) (this.posZ - interpPosZ);
//        double mipLevel = Math.max(0, Math.min(1, (entity.getDistanceSq(posX, posY, posZ) - 20) / 600D));
//
//        //endregion
//
//        //region GLRender
//
//        double pCount = 20 + (80 * (1 - mipLevel));//Minecraft.getMinecraft().gameSettings.fancyGraphics ? 35 : 15;
//        for (int i = 0; i < pCount; i++) {
//            double rotation = i / pCount * (3.141 * 2D) + animTime / 80D;
//
//            float rFloat3 = rand.nextFloat();
//            float rFloat4 = rand.nextFloat();
//
//            //region Shadow
//
//            float scale = 0.01F + (rFloat4 * 0.05F) + ((float) mipLevel * 0.2F);
//            float a = 1;//sd + 0.1F;
//            float r = particleRed;
//            float g = particleGreen;
//            float b = particleBlue;
//
//            rotation -= 0.05F;
//            //endregion
//
//            //region Sub Circular Calculation
//
//            double subRotationRadius = (0.1 * rFloat3) + 0.02;
//            double dir = rand.nextBoolean() ? 1 : -1;
//            double sy = Math.cos(dir * rotation * (rFloat3 * 10) * (1 - (rand.nextFloat() * 0.2F))) * subRotationRadius;
//            double sx = Math.sin(dir * rotation * (rFloat3 * 10) * (1 - (rand.nextFloat() * 0.2F))) * subRotationRadius;
//            float drawY = renderY + (float) sy;
//            double renderRadius = 0.4 + sx;
//
//            //endregion
//
//            //region Circular Calculation
//            double ox = Math.sin(rotation) * renderRadius;
//            double oz = Math.cos(rotation) * renderRadius;
//            float drawX = renderX + (float) ox;
//            float drawZ = renderZ + (float) oz;
//            //endregion
//
//            particleTextureIndexX = (ClientEventHandler.elapsedTicks) % 5;
//            particleTextureIndexY = 1;
//            float minU = (float) this.particleTextureIndexX / texturesPerRow;
//            float maxU = minU + 1F / texturesPerRow;
//            float minV = (float) this.particleTextureIndexY / texturesPerRow;
//            float maxV = minV + 1F / texturesPerRow;
//            vertexbuffer.pos((double) (drawX - rotationX * scale - rotationXY * scale), (double) (drawY - rotationZ * scale), (double) (drawZ - rotationYZ * scale - rotationXZ * scale)).tex((double) maxU, (double) maxV).color(r, g, b, a).endVertex();
//            vertexbuffer.pos((double) (drawX - rotationX * scale + rotationXY * scale), (double) (drawY + rotationZ * scale), (double) (drawZ - rotationYZ * scale + rotationXZ * scale)).tex((double) maxU, (double) minV).color(r, g, b, a).endVertex();
//            vertexbuffer.pos((double) (drawX + rotationX * scale + rotationXY * scale), (double) (drawY + rotationZ * scale), (double) (drawZ + rotationYZ * scale + rotationXZ * scale)).tex((double) minU, (double) minV).color(r, g, b, a).endVertex();
//            vertexbuffer.pos((double) (drawX + rotationX * scale - rotationXY * scale), (double) (drawY - rotationZ * scale), (double) (drawZ + rotationYZ * scale - rotationXZ * scale)).tex((double) minU, (double) maxV).color(r, g, b, a).endVertex();
//
//            //region Inner
//            scale = 0.01F + (rFloat4 * 0.04F) * (float) Math.sin((animTime + i) / 30) + ((float) mipLevel * 0.05F);
//            rotation = i / pCount * (3.141 * 2D) + animTime / 200D;
//            rotation -= 0.05F;
//
//            renderRadius = 0.4;
//            ox = Math.sin(rotation) * renderRadius;
//            oz = Math.cos(rotation) * renderRadius;
//            drawX = renderX + (float) ox;
//            drawY = renderY;
//            drawZ = renderZ + (float) oz;
//
//            r = 0;
//            g = 1;
//            b = 1;
//
//            particleTextureIndexX = 0;
//            particleTextureIndexY = 0;
//            minU = (float) this.particleTextureIndexX / texturesPerRow;
//            maxU = minU + 1F / texturesPerRow;
//            minV = (float) this.particleTextureIndexY / texturesPerRow;
//            maxV = minV + 1F / texturesPerRow;
//            vertexbuffer.pos((double) (drawX - rotationX * scale - rotationXY * scale), (double) (drawY - rotationZ * scale), (double) (drawZ - rotationYZ * scale - rotationXZ * scale)).tex((double) maxU, (double) maxV).color(r, g, b, a).endVertex();
//            vertexbuffer.pos((double) (drawX - rotationX * scale + rotationXY * scale), (double) (drawY + rotationZ * scale), (double) (drawZ - rotationYZ * scale + rotationXZ * scale)).tex((double) maxU, (double) minV).color(r, g, b, a).endVertex();
//            vertexbuffer.pos((double) (drawX + rotationX * scale + rotationXY * scale), (double) (drawY + rotationZ * scale), (double) (drawZ + rotationYZ * scale + rotationXZ * scale)).tex((double) minU, (double) minV).color(r, g, b, a).endVertex();
//            vertexbuffer.pos((double) (drawX + rotationX * scale - rotationXY * scale), (double) (drawY - rotationZ * scale), (double) (drawZ + rotationYZ * scale - rotationXZ * scale)).tex((double) minU, (double) maxV).color(r, g, b, a).endVertex();
//
//            //endregion
//        }
//
//        //endregion
    }

    @Override
    public IGLFXHandler getFXHandler() {
        return tile.getTier() == 0 ? BASIC_HANDLER : tile.getTier() == 1 ? WYVERN_HANDLER : DRACONIC_HANDLER;
    }

    private static final FXHandler BASIC_HANDLER = new FXHandler(DETextures.ENERGY_BEAM_BASIC);
    private static final FXHandler WYVERN_HANDLER = new FXHandler(DETextures.ENERGY_BEAM_WYVERN);
    private static final FXHandler DRACONIC_HANDLER = new FXHandler(DETextures.ENERGY_BEAM_DRACONIC);

    public static class FXHandler implements IGLFXHandler {

        private String texture;

        public FXHandler(String texture) {
            this.texture = texture;
        }

        @Override
        public void preDraw(int layer, BufferBuilder vertexbuffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateTracker.pushState();
            GlStateManager.depthMask(false);
            GlStateManager.disableCull();
            GlStateManager.alphaFunc(GL11.GL_GREATER, 0F);
            ResourceHelperDE.bindTexture(texture);
            vertexbuffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
        }

        @Override
        public void postDraw(int layer, BufferBuilder vertexbuffer, Tessellator tessellator) {
            tessellator.draw();
            GlStateTracker.popState();
        }
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