package com.brandon3055.draconicevolution.client.render.effect;

import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.blocks.energynet.tileentity.TileCrystalBase;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

/**
 * Created by brandon3055 on 29/11/2016.
 */
public class CrystalFXIO extends CrystalFXBase<TileCrystalBase> {

    public CrystalFXIO(ClientLevel worldIn, TileCrystalBase tile) {
        super(worldIn, tile);
        this.age = worldIn.random.nextInt(1024);
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
    public void render(VertexConsumer buffer, Camera renderInfo, float partialTicks) {
        if (!renderEnabled) {
            return;
        }

        Vec3 viewVec = renderInfo.getPosition();
        float viewX = (float) (this.x - viewVec.x());
        float viewY = (float) (this.y - viewVec.y());
        float viewZ = (float) (this.z - viewVec.z());
        Vector3f[] renderVector = getRenderVectors(renderInfo, viewX, viewY, viewZ, 0.2F);
        buffer.vertex(renderVector[0].x(), renderVector[0].y(), renderVector[0].z()).color(1F, 1F, 1F, 1F).uv(0.5F, 0.5F).uv2(240, 240).endVertex();
        buffer.vertex(renderVector[1].x(), renderVector[1].y(), renderVector[1].z()).color(1F, 1F, 1F, 1F).uv(0.5F, 0.0F).uv2(240, 240).endVertex();
        buffer.vertex(renderVector[2].x(), renderVector[2].y(), renderVector[2].z()).color(1F, 1F, 1F, 1F).uv(0.0F, 0.0F).uv2(240, 240).endVertex();
        buffer.vertex(renderVector[3].x(), renderVector[3].y(), renderVector[3].z()).color(1F, 1F, 1F, 1F).uv(0.0F, 0.5F).uv2(240, 240).endVertex();
    }

    @Override
    public ParticleRenderType getRenderType() {
        return tile.getTier() == 0 ? BASIC_HANDLER : tile.getTier() == 1 ? WYVERN_HANDLER : DRACONIC_HANDLER;
    }

    private static final ParticleRenderType BASIC_HANDLER = new FXHandler(new ResourceLocation(DraconicEvolution.MODID, "textures/particle/energy_beam_basic.png"));
    private static final ParticleRenderType WYVERN_HANDLER = new FXHandler(new ResourceLocation(DraconicEvolution.MODID, "textures/particle/energy_beam_wyvern.png"));
    private static final ParticleRenderType DRACONIC_HANDLER = new FXHandler(new ResourceLocation(DraconicEvolution.MODID, "textures/particle/energy_beam_draconic.png"));

    public static class FXHandler implements ParticleRenderType {

        private ResourceLocation texture;

        public FXHandler(ResourceLocation texture) {
            this.texture = texture;
        }

        @Override
        public void begin(BufferBuilder builder, TextureManager textureManager) {
            RenderSystem.depthMask(false);
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
            RenderSystem.setShader(GameRenderer::getPositionColorTexLightmapShader);
            RenderSystem.setShaderTexture(0, texture);
            builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP);
        }

        @Override
        public void end(Tesselator tessellator) {
            tessellator.end();
        }
    }
}