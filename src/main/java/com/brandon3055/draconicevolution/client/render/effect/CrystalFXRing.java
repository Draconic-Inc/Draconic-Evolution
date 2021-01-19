package com.brandon3055.draconicevolution.client.render.effect;

import com.brandon3055.draconicevolution.blocks.energynet.EnergyCrystal;
import com.brandon3055.draconicevolution.blocks.energynet.tileentity.TileCrystalBase;
import com.brandon3055.draconicevolution.client.DETextures;
import com.brandon3055.draconicevolution.client.handler.ClientEventHandler;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import org.lwjgl.opengl.GL11;

/**
 * Created by brandon3055 on 29/11/2016.
 */
public class CrystalFXRing extends CrystalFXBase<TileCrystalBase> {

    private long rSeed = 0;

    public CrystalFXRing(ClientWorld worldIn, TileCrystalBase tile) {
        super(worldIn, tile);
        this.age = worldIn.rand.nextInt(1024);
        this.rSeed = tile.getPos().toLong();
    }

    @Override
    public void tick() {
        super.tick();
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
    public void renderParticle(IVertexBuilder buffer, ActiveRenderInfo renderInfo, float partialTicks) {
        if (!renderEnabled || DETextures.ENERGY_PARTICLE == null || DETextures.ENERGY_PARTICLE[0] == null) {
            return;
        }

        boolean wierless = tile.getCrystalType() == EnergyCrystal.CrystalType.WIRELESS;

        rand.setSeed(rSeed);
        float animTime = ClientEventHandler.elapsedTicks + age + partialTicks;

        //region variables

        Vector3d view = renderInfo.getProjectedView();
        float viewX = (float) (this.posX - view.getX());
        float viewY = (float) (this.posY - view.getY());
        float viewZ = (float) (this.posZ - view.getZ());
        double mipLevel = Math.max(0, Math.min(1, (renderInfo.getBlockPos().distanceSq(posX, posY, posZ, true) - 20) / 600D));

        //endregion

        //region GLRender

        double pCount = 20 + (80 * (1 - mipLevel));//Minecraft.getInstance().gameSettings.fancyGraphics ? 35 : 15;TODO?
        for (int i = 0; i < pCount; i++) {
            double rotation = i / pCount * (3.141 * 2D) + animTime / 80D;

            float rFloat3 = rand.nextFloat();
            float rFloat4 = rand.nextFloat();

            //region Shadow

            float scale = 0.01F + (rFloat4 * 0.05F) + ((float) mipLevel * 0.2F);
            float a = 1;
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
            float drawY = viewY + (float) sy;
            double renderRadius = 0.4 + sx;

            //endregion

            //region Circular Calculation
            double ox = Math.sin(rotation) * renderRadius;
            double oz = Math.cos(rotation) * renderRadius;
            float drawX = viewX + (float) ox;
            float drawZ = viewZ + (float) oz;
            //endregion

            int texIndex = (ClientEventHandler.elapsedTicks) % DETextures.ENERGY_PARTICLE.length;
            TextureAtlasSprite sprite = DETextures.ENERGY_PARTICLE[texIndex];
            float minU = sprite.getMinU();
            float maxU = sprite.getMaxU();
            float minV = sprite.getMinV();
            float maxV = sprite.getMaxV();

            Vector3f[] renderVector = getRenderVectors(renderInfo, drawX, drawY, drawZ, scale);
            buffer.pos(renderVector[0].getX(), renderVector[0].getY(), renderVector[0].getZ()).color(r, g, b, a).tex(maxU, maxV).endVertex();
            buffer.pos(renderVector[1].getX(), renderVector[1].getY(), renderVector[1].getZ()).color(r, g, b, a).tex(maxU, minV).endVertex();
            buffer.pos(renderVector[2].getX(), renderVector[2].getY(), renderVector[2].getZ()).color(r, g, b, a).tex(minU, minV).endVertex();
            buffer.pos(renderVector[3].getX(), renderVector[3].getY(), renderVector[3].getZ()).color(r, g, b, a).tex(minU, maxV).endVertex();

            //region Inner
            scale = 0.01F + (rFloat4 * 0.04F) * (float) Math.sin((animTime + i) / 30) + ((float) mipLevel * 0.05F);
            rotation = i / pCount * (3.141 * 2D) + animTime / 200D;
            rotation -= 0.05F;

            renderRadius = 0.4;
            ox = Math.sin(rotation) * renderRadius;
            oz = Math.cos(rotation) * renderRadius;
            drawX = viewX + (float) ox;
            drawY = viewY;
            drawZ = viewZ + (float) oz;

            r = wierless ? 1 : 0;
            g = wierless ? 0 : 1;
            b = wierless ? 0 : 1;

            minU = DETextures.ORB_PARTICLE.getMinU();
            maxU = DETextures.ORB_PARTICLE.getMaxU();
            minV = DETextures.ORB_PARTICLE.getMinV();
            maxV = DETextures.ORB_PARTICLE.getMaxV();
            renderVector = getRenderVectors(renderInfo, drawX, drawY, drawZ, scale);
            buffer.pos(renderVector[0].getX(), renderVector[0].getY(), renderVector[0].getZ()).color(r, g, b, a).tex(maxU, maxV).endVertex();
            buffer.pos(renderVector[1].getX(), renderVector[1].getY(), renderVector[1].getZ()).color(r, g, b, a).tex(maxU, minV).endVertex();
            buffer.pos(renderVector[2].getX(), renderVector[2].getY(), renderVector[2].getZ()).color(r, g, b, a).tex(minU, minV).endVertex();
            buffer.pos(renderVector[3].getX(), renderVector[3].getY(), renderVector[3].getZ()).color(r, g, b, a).tex(minU, maxV).endVertex();

            //            buffer.pos(drawX - rotationX * scale - rotationXY * scale, drawY - rotationZ * scale, drawZ - rotationYZ * scale - rotationXZ * scale).color(r, g, b, a).tex(maxU, maxV).endVertex();
//            buffer.pos(drawX - rotationX * scale + rotationXY * scale, drawY + rotationZ * scale, drawZ - rotationYZ * scale + rotationXZ * scale).color(r, g, b, a).tex(maxU, minV).endVertex();
//            buffer.pos(drawX + rotationX * scale + rotationXY * scale, drawY + rotationZ * scale, drawZ + rotationYZ * scale + rotationXZ * scale).color(r, g, b, a).tex(minU, minV).endVertex();
//            buffer.pos(drawX + rotationX * scale - rotationXY * scale, drawY - rotationZ * scale, drawZ + rotationYZ * scale - rotationXZ * scale).color(r, g, b, a).tex(minU, maxV).endVertex();

            //endregion
        }

//        endregion

    }

//    @Override
//    public void renderParticle(BufferBuilder buffer, ActiveRenderInfo entity, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {

//
//        boolean wierless = tile.getCrystalType() == EnergyCrystal.CrystalType.WIRELESS;
//
//        rand.setSeed(rSeed);
//        float animTime = ClientEventHandler.elapsedTicks + age + partialTicks;
//
//        //region variables
//
//        float renderX = (float) (this.posX - interpPosX);
//        float renderY = (float) (this.posY - interpPosY);
//        float renderZ = (float) (this.posZ - interpPosZ);
//        double mipLevel = Math.max(0, Math.min(1, (entity.getBlockPos().distanceSq(posX, posY, posZ, true) - 20) / 600D));
//
//        //endregion
//
//        //region GLRender
//
//        double pCount = 20 + (80 * (1 - mipLevel));//Minecraft.getInstance().gameSettings.fancyGraphics ? 35 : 15;TODO?
//        for (int i = 0; i < pCount; i++) {
//            double rotation = i / pCount * (3.141 * 2D) + animTime / 80D;
//
//            float rFloat3 = rand.nextFloat();
//            float rFloat4 = rand.nextFloat();
//
//            //region Shadow
//
//            float scale = 0.01F + (rFloat4 * 0.05F) + ((float) mipLevel * 0.2F);
//            float a = 1;
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
//            int texIndex = (ClientEventHandler.elapsedTicks) % DETextures.ENERGY_PARTICLE.length;
//            TextureAtlasSprite sprite = DETextures.ENERGY_PARTICLE[texIndex];
//            float minU = sprite.getMinU();
//            float maxU = sprite.getMaxU();
//            float minV = sprite.getMinV();
//            float maxV = sprite.getMaxV();
//
//            buffer.pos(drawX - rotationX * scale - rotationXY * scale, drawY - rotationZ * scale, drawZ - rotationYZ * scale - rotationXZ * scale).tex(maxU, maxV).color(r, g, b, a).lightmap(200, 200).endVertex();
//            buffer.pos(drawX - rotationX * scale + rotationXY * scale, drawY + rotationZ * scale, drawZ - rotationYZ * scale + rotationXZ * scale).tex(maxU, minV).color(r, g, b, a).lightmap(200, 200).endVertex();
//            buffer.pos(drawX + rotationX * scale + rotationXY * scale, drawY + rotationZ * scale, drawZ + rotationYZ * scale + rotationXZ * scale).tex(minU, minV).color(r, g, b, a).lightmap(200, 200).endVertex();
//            buffer.pos(drawX + rotationX * scale - rotationXY * scale, drawY - rotationZ * scale, drawZ + rotationYZ * scale - rotationXZ * scale).tex(minU, maxV).color(r, g, b, a).lightmap(200, 200).endVertex();
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
//            r = wierless ? 1 : 0;
//            g = wierless ? 0 : 1;
//            b = wierless ? 0 : 1;
//
//            minU = DETextures.ORB_PARTICLE.getMinU();
//            maxU = DETextures.ORB_PARTICLE.getMaxU();
//            minV = DETextures.ORB_PARTICLE.getMinV();
//            maxV = DETextures.ORB_PARTICLE.getMaxV();
//            buffer.pos(drawX - rotationX * scale - rotationXY * scale, drawY - rotationZ * scale, drawZ - rotationYZ * scale - rotationXZ * scale).tex(maxU, maxV).color(r, g, b, a).lightmap(200, 200).endVertex();
//            buffer.pos(drawX - rotationX * scale + rotationXY * scale, drawY + rotationZ * scale, drawZ - rotationYZ * scale + rotationXZ * scale).tex(maxU, minV).color(r, g, b, a).lightmap(200, 200).endVertex();
//            buffer.pos(drawX + rotationX * scale + rotationXY * scale, drawY + rotationZ * scale, drawZ + rotationYZ * scale + rotationXZ * scale).tex(minU, minV).color(r, g, b, a).lightmap(200, 200).endVertex();
//            buffer.pos(drawX + rotationX * scale - rotationXY * scale, drawY - rotationZ * scale, drawZ + rotationYZ * scale - rotationXZ * scale).tex(minU, maxV).color(r, g, b, a).lightmap(200, 200).endVertex();
//
//            //endregion
//        }
//
//        endregion
//    }

    @Override
    public IParticleRenderType getRenderType() {
        return RENDER_TYPE;
    }

    public static final IParticleRenderType RENDER_TYPE = new IParticleRenderType() {
        @Override
        public void beginRender(BufferBuilder builder, TextureManager textureManager) {
            RenderSystem.depthMask(false);
            RenderSystem.alphaFunc(516, 0.003921569F);
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
            RenderSystem.glMultiTexCoord2f(0x84c2, 240.0F, 240.0F); //Lightmap

            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            textureManager.bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
            builder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR_TEX);
        }

        @Override
        public void finishRender(Tessellator tessellator) {
            tessellator.getBuffer().sortVertexData(0, 0, 0);
            tessellator.draw();
        }
    };
}