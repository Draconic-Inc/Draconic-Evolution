package com.brandon3055.draconicevolution.client.render.effect;

import codechicken.lib.render.state.GlStateTracker;
import com.brandon3055.brandonscore.client.particle.IGLFXHandler;
import com.brandon3055.draconicevolution.blocks.energynet.EnergyCrystal;
import com.brandon3055.draconicevolution.blocks.energynet.tileentity.TileCrystalBase;
import com.brandon3055.draconicevolution.client.DEParticles;
import com.brandon3055.draconicevolution.client.handler.ClientEventHandler;
import com.brandon3055.draconicevolution.helpers.ResourceHelperDE;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

/**
 * Created by brandon3055 on 29/11/2016.
 */
public class CrystalFXRing extends CrystalGLFXBase<TileCrystalBase> {

    private long rSeed = 0;

    public CrystalFXRing(World worldIn, TileCrystalBase tile) {
        super(worldIn, tile);
        this.particleTextureIndexX = 3 + tile.getTier();
        this.particleAge = worldIn.rand.nextInt(1024);
        this.rSeed = tile.getPos().toLong();
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
    public void renderParticle(VertexBuffer vertexbuffer, Entity entity, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
        if (!renderEnabled) {
            return;
        }

        boolean wierless = tile.getType() == EnergyCrystal.CrystalType.WIRELESS;

        rand.setSeed(rSeed);
        float animTime = ClientEventHandler.elapsedTicks + particleAge + partialTicks;

        //region variables

        float renderX = (float) (this.posX - interpPosX);
        float renderY = (float) (this.posY - interpPosY);
        float renderZ = (float) (this.posZ - interpPosZ);
        double mipLevel = Math.max(0, Math.min(1, (entity.getDistanceSq(posX, posY, posZ) - 20) / 600D));

        //endregion

        //region GLRender

        double pCount = 20 + (80 * (1 - mipLevel));//Minecraft.getMinecraft().gameSettings.fancyGraphics ? 35 : 15;TODO?
        for (int i = 0; i < pCount; i++) {
            double rotation = i / pCount * (3.141 * 2D) + animTime / 80D;

            float rFloat3 = rand.nextFloat();
            float rFloat4 = rand.nextFloat();

            //region Shadow

            float scale = 0.01F + (rFloat4 * 0.05F) + ((float) mipLevel * 0.2F);
            float a = 1;//sd + 0.1F;
            float r = particleRed;
            float g = particleGreen;
            float b = particleBlue;

            rotation -= 0.05F;
            //endregion

            //region Sub Circular Calculation

            double subRotationRadius = (0.1 * rFloat3) + 0.02;
            double dir = rand.nextBoolean() ? 1 : -1;
            double sy = Math.cos(dir * rotation * (rFloat3 * 10) * (1 - (rand.nextFloat() * 0.2F))) * subRotationRadius;
            double sx = Math.sin(dir * rotation * (rFloat3 * 10) * (1 - (rand.nextFloat() * 0.2F))) * subRotationRadius;
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
            scale = 0.01F + (rFloat4 * 0.04F) * (float) Math.sin((animTime + i) / 30) + ((float) mipLevel * 0.05F);
            rotation = i / pCount * (3.141 * 2D) + animTime / 200D;
            rotation -= 0.05F;

            renderRadius = 0.4;
            ox = Math.sin(rotation) * renderRadius;
            oz = Math.cos(rotation) * renderRadius;
            drawX = renderX + (float) ox;
            drawY = renderY;
            drawZ = renderZ + (float) oz;

            r = wierless ? 1 : 0;
            g = wierless ? 0 : 1;
            b = wierless ? 0 : 1;

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
    }

    @Override
    public IGLFXHandler getFXHandler() {
        return FX_HANDLER;
    }

    public static final IGLFXHandler FX_HANDLER = new IGLFXHandler() {
        @Override
        public void preDraw(int layer, VertexBuffer vertexbuffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateTracker.pushState();
            GlStateManager.depthMask(false);
            GlStateManager.disableCull();
            GlStateManager.alphaFunc(GL11.GL_GREATER, 0F);
            ResourceHelperDE.bindTexture(DEParticles.DE_SHEET);
            vertexbuffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
        }

        @Override
        public void postDraw(int layer, VertexBuffer vertexbuffer, Tessellator tessellator) {
            tessellator.getBuffer().sortVertexData(0, 0, 0);
            tessellator.draw();
            GlStateTracker.popState();
        }
    };
}