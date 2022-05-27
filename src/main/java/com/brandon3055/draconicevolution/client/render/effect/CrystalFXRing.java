package com.brandon3055.draconicevolution.client.render.effect;

import com.brandon3055.draconicevolution.blocks.energynet.EnergyCrystal;
import com.brandon3055.draconicevolution.blocks.energynet.tileentity.TileCrystalBase;
import com.brandon3055.draconicevolution.client.DEMiscSprites;
import com.brandon3055.draconicevolution.client.handler.ClientEventHandler;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Vector3f;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.world.phys.Vec3;

/**
 * Created by brandon3055 on 29/11/2016.
 */
public class CrystalFXRing extends CrystalFXBase<TileCrystalBase> {

    private long rSeed = 0;

    public CrystalFXRing(ClientLevel worldIn, TileCrystalBase tile) {
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
    public void render(VertexConsumer buffer, Camera camera, float partialTicks) {
        if (!renderEnabled || DEMiscSprites.ENERGY_PARTICLE == null || DEMiscSprites.ENERGY_PARTICLE[0] == null) {
            return;
        }

        boolean wierless = tile.getCrystalType() == EnergyCrystal.CrystalType.WIRELESS;

        random.setSeed(rSeed);
        float animTime = ClientEventHandler.elapsedTicks + age + partialTicks;

        //region variables

        Vec3 view = camera.getPosition();
        float viewX = (float) (this.x - view.x());
        float viewY = (float) (this.y - view.y());
        float viewZ = (float) (this.z - view.z());
        double mipLevel = Math.max(0, Math.min(1, (camera.getBlockPosition().distToCenterSqr(x, y, z) - 20) / 600D));

        //endregion

        //region GLRender

        double pCount = 20 + (80 * (1 - mipLevel));//Minecraft.getInstance().gameSettings.fancyGraphics ? 35 : 15;TODO?
        for (int i = 0; i < pCount; i++) {
            double rotation = i / pCount * (3.141 * 2D) + animTime / 80D;

            float rFloat3 = random.nextFloat();
            float rFloat4 = random.nextFloat();

            //region Shadow

            float scale = 0.01F + (rFloat4 * 0.05F) + ((float) mipLevel * 0.2F);
            float a = 1;
            float r = rCol;
            float g = gCol;
            float b = bCol;

            rotation -= 0.05F;
            //endregion

            //region Sub Circular Calculation

            double subRotationRadius = (0.1 * rFloat3) + 0.02;
            double dir = random.nextBoolean() ? 1 : -1;
            double sy = Math.cos(dir * rotation * (rFloat3 * 10) * (1 - (random.nextFloat() * 0.2F))) * subRotationRadius;
            double sx = Math.sin(dir * rotation * (rFloat3 * 10) * (1 - (random.nextFloat() * 0.2F))) * subRotationRadius;
            float drawY = viewY + (float) sy;
            double renderRadius = 0.4 + sx;

            //endregion

            //region Circular Calculation
            double ox = Math.sin(rotation) * renderRadius;
            double oz = Math.cos(rotation) * renderRadius;
            float drawX = viewX + (float) ox;
            float drawZ = viewZ + (float) oz;
            //endregion

            int texIndex = (ClientEventHandler.elapsedTicks) % DEMiscSprites.ENERGY_PARTICLE.length;
            TextureAtlasSprite sprite = DEMiscSprites.ENERGY_PARTICLE[texIndex];
            float minU = sprite.getU0();
            float maxU = sprite.getU1();
            float minV = sprite.getV0();
            float maxV = sprite.getV1();

            Vector3f[] renderVector = getRenderVectors(camera, drawX, drawY, drawZ, scale);
            buffer.vertex(renderVector[0].x(), renderVector[0].y(), renderVector[0].z()).color(r, g, b, a).uv(maxU, maxV).uv2(240, 240).endVertex();
            buffer.vertex(renderVector[1].x(), renderVector[1].y(), renderVector[1].z()).color(r, g, b, a).uv(maxU, minV).uv2(240, 240).endVertex();
            buffer.vertex(renderVector[2].x(), renderVector[2].y(), renderVector[2].z()).color(r, g, b, a).uv(minU, minV).uv2(240, 240).endVertex();
            buffer.vertex(renderVector[3].x(), renderVector[3].y(), renderVector[3].z()).color(r, g, b, a).uv(minU, maxV).uv2(240, 240).endVertex();

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

            minU = DEMiscSprites.ORB_PARTICLE.getU0();
            maxU = DEMiscSprites.ORB_PARTICLE.getU1();
            minV = DEMiscSprites.ORB_PARTICLE.getV0();
            maxV = DEMiscSprites.ORB_PARTICLE.getV1();
            renderVector = getRenderVectors(camera, drawX, drawY, drawZ, scale);
            buffer.vertex(renderVector[0].x(), renderVector[0].y(), renderVector[0].z()).color(r, g, b, a).uv(maxU, maxV).uv2(240, 240).endVertex();
            buffer.vertex(renderVector[1].x(), renderVector[1].y(), renderVector[1].z()).color(r, g, b, a).uv(maxU, minV).uv2(240, 240).endVertex();
            buffer.vertex(renderVector[2].x(), renderVector[2].y(), renderVector[2].z()).color(r, g, b, a).uv(minU, minV).uv2(240, 240).endVertex();
            buffer.vertex(renderVector[3].x(), renderVector[3].y(), renderVector[3].z()).color(r, g, b, a).uv(minU, maxV).uv2(240, 240).endVertex();
        }
//        endregion
    }

    @Override
    public ParticleRenderType getRenderType() {
        return RENDER_TYPE;
    }

    public static final ParticleRenderType RENDER_TYPE = new ParticleRenderType() {
        @Override
        public void begin(BufferBuilder builder, TextureManager textureManager) {
            RenderSystem.depthMask(false);
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
            RenderSystem.setShader(GameRenderer::getPositionColorTexLightmapShader);
            RenderSystem.setShaderTexture(0, DEMiscSprites.ATLAS_LOCATION);
            builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP);
        }

        @Override
        public void end(Tesselator tessellator) {
            tessellator.getBuilder().setQuadSortOrigin(0, 0, 0);
            tessellator.end();
        }
    };
}