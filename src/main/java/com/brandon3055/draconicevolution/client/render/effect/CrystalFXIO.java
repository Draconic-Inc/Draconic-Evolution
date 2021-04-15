package com.brandon3055.draconicevolution.client.render.effect;

import com.brandon3055.draconicevolution.blocks.energynet.tileentity.TileCrystalBase;
import com.brandon3055.draconicevolution.client.DETextures;
import com.brandon3055.draconicevolution.utils.ResourceHelperDE;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import org.lwjgl.opengl.GL11;

/**
 * Created by brandon3055 on 29/11/2016.
 */
public class CrystalFXIO extends CrystalFXBase<TileCrystalBase> {

    private long rSeed = 0;

    public CrystalFXIO(ClientWorld worldIn, TileCrystalBase tile) {
        super(worldIn, tile);
        this.age = worldIn.random.nextInt(1024);
        this.rSeed = tile.getBlockPos().asLong();
    }

    @Override
    public void tick() {
        super.tick();
        if (ticksTillDeath-- <= 0) {
            remove();
        }

        float[] r = {0.0F, 0.8F, 1.0F};
        float[] g = {0.8F, 0.1F, 0.7F};
        float[] b = {1F, 1F, 0.2F};

        rCol = r[tile.getTier()];
        gCol = g[tile.getTier()];
        bCol = b[tile.getTier()];
    }

    @Override
    public void render(IVertexBuilder buffer, ActiveRenderInfo renderInfo, float partialTicks) {
        if (!renderEnabled) {
            return;
        }

        Vector3d viewVec = renderInfo.getPosition();
        float viewX = (float) (this.x - viewVec.x());
        float viewY = (float) (this.y - viewVec.y());
        float viewZ = (float) (this.z - viewVec.z());
        Vector3f[] renderVector = getRenderVectors(renderInfo, viewX, viewY, viewZ, 0.2F);
        buffer.vertex(renderVector[0].x(), renderVector[0].y(), renderVector[0].z()).uv(0.5F, 0.5F).endVertex();
        buffer.vertex(renderVector[1].x(), renderVector[1].y(), renderVector[1].z()).uv(0.5F, 0.0F).endVertex();
        buffer.vertex(renderVector[2].x(), renderVector[2].y(), renderVector[2].z()).uv(0.0F, 0.0F).endVertex();
        buffer.vertex(renderVector[3].x(), renderVector[3].y(), renderVector[3].z()).uv(0.0F, 0.5F).endVertex();
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
        public void begin(BufferBuilder builder, TextureManager textureManager) {
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            ResourceHelperDE.bindTexture(texture);

            RenderSystem.depthMask(false);
            RenderSystem.alphaFunc(516, 0.003921569F);
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
            RenderSystem.glMultiTexCoord2f(0x84c2, 240.0F, 240.0F); //Lightmap

            builder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        }

        @Override
        public void end(Tessellator tessellator) {
            tessellator.end();
        }
    }
}


//region Base Field Implementation
//        rand.setSeed(3490276L);
//                float animTime = ClientEventHandler.elapsedTicks + particleAge + partialTicks;
//                RenderSystem.pushMatrix();
//                RenderSystem.disableCull();
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
//        RenderSystem.popMatrix();
//endregion