package com.brandon3055.draconicevolution.client.render.effect;

import com.brandon3055.draconicevolution.blocks.energynet.tileentity.TileCrystalBase;
import com.brandon3055.draconicevolution.utils.ResourceHelperDE;
import com.brandon3055.draconicevolution.utils.DETextures;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

/**
 * Created by brandon3055 on 29/11/2016.
 */
public class CrystalFXIO extends CrystalFXBase<TileCrystalBase> {

    private long rSeed = 0;

    public CrystalFXIO(World worldIn, TileCrystalBase tile) {
        super(worldIn, tile);
        this.age = worldIn.rand.nextInt(1024);
        this.rSeed = tile.getPos().toLong();
    }

    @Override
    public void tick() {
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
    public void renderParticle(BufferBuilder buffer, ActiveRenderInfo entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
        if (!renderEnabled) {
            return;
        }

        float renderX = (float) (this.posX - interpPosX);
        float renderY = (float) (this.posY - interpPosY);
        float renderZ = (float) (this.posZ - interpPosZ);

//        baseScale = 0.2F;
        float scale = 0.2F;


        buffer.pos(renderX - rotationX * scale - rotationXY * scale, renderY - rotationZ * scale, renderZ - rotationYZ * scale - rotationXZ * scale).tex(0.5, 0.5).color(particleRed, particleGreen, particleBlue, particleAlpha).lightmap(200, 200).endVertex();
        buffer.pos(renderX - rotationX * scale + rotationXY * scale, renderY + rotationZ * scale, renderZ - rotationYZ * scale + rotationXZ * scale).tex(0.5, 0.0).color(particleRed, particleGreen, particleBlue, particleAlpha).lightmap(200, 200).endVertex();
        buffer.pos(renderX + rotationX * scale + rotationXY * scale, renderY + rotationZ * scale, renderZ + rotationYZ * scale + rotationXZ * scale).tex(0.0, 0.0).color(particleRed, particleGreen, particleBlue, particleAlpha).lightmap(200, 200).endVertex();
        buffer.pos(renderX + rotationX * scale - rotationXY * scale, renderY - rotationZ * scale, renderZ + rotationYZ * scale - rotationXZ * scale).tex(0.0, 0.5).color(particleRed, particleGreen, particleBlue, particleAlpha).lightmap(200, 200).endVertex();

    }

    @Override
    public IParticleRenderType getRenderType() {
        return tile.getTier() == 0 ? BASIC_HANDLER : tile.getTier() == 1 ? WYVERN_HANDLER : DRACONIC_HANDLER;
    }

    private static final IParticleRenderType BASIC_HANDLER = new FXHandler(DETextures.ENERGY_BEAM_BASIC);
    private static final IParticleRenderType WYVERN_HANDLER = new FXHandler(DETextures.ENERGY_BEAM_WYVERN);
    private static final IParticleRenderType DRACONIC_HANDLER = new FXHandler(DETextures.ENERGY_BEAM_DRACONIC);

    public static class FXHandler implements IParticleRenderType {

        private String texture;

        public FXHandler(String texture) {
            this.texture = texture;
        }

        @Override
        public void beginRender(BufferBuilder builder, TextureManager textureManager) {
            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.depthMask(false);
            GlStateManager.disableCull();
            GlStateManager.alphaFunc(GL11.GL_GREATER, 0F);
            ResourceHelperDE.bindTexture(texture);
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            GlStateManager.disableLighting();

            builder.begin(GL11.GL_QUADS, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
        }

        @Override
        public void finishRender(Tessellator tessellator) {
            tessellator.draw();
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
//                double pCount = 100;//Minecraft.getInstance().gameSettings.fancyGraphics ? 35 : 15;
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