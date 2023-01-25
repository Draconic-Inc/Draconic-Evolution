package com.brandon3055.draconicevolution.client.render.effect;

import codechicken.lib.math.MathHelper;
import codechicken.lib.vec.Vector3;
import com.brandon3055.brandonscore.lib.Vec3D;
import com.brandon3055.brandonscore.utils.BCProfiler;
import com.brandon3055.brandonscore.utils.Utils;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.api.energy.ICrystalLink;
import com.brandon3055.draconicevolution.api.energy.IENetEffectTile;
import com.brandon3055.draconicevolution.client.handler.ClientEventHandler;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Vector3f;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * Created by brandon3055 on 29/11/2016.
 */
public class CrystalFXBeam<T extends BlockEntity & IENetEffectTile> extends CrystalFXBase<T> {

    private final Vec3D linkTarget;
    private final boolean terminateSource;
    private final boolean terminateTarget;
    private long lSeed = 0;
    private boolean bolt = false;
    private float powerLevel = 0;

    public CrystalFXBeam(Level worldIn, T tile, ICrystalLink linkTarget) {
        super((ClientLevel)worldIn, tile);
        this.age = worldIn.random.nextInt(1024);
        this.setPosition(tile.getBeamLinkPos(((BlockEntity) linkTarget).getBlockPos()));
        this.terminateSource = tile.renderBeamTermination();
        this.linkTarget = linkTarget.getBeamLinkPos(tile.getBlockPos());
        this.terminateTarget = linkTarget.renderBeamTermination();
        setBoundingBox(new AABB(x, y, z, this.linkTarget.x, this.linkTarget.y, this.linkTarget.z));
    }


    @Override
    public void tick() {
        super.tick();
        BCProfiler.TICK.start("crystal_beam_fx_update");
        if (ticksTillDeath-- <= 0) {
            remove();
        }

        float[] r = {0.0F, 0.8F, 1.0F};
        float[] g = {0.8F, 0.1F, 0.7F};
        float[] b = {1F, 1F, 0.2F};

        rCol = r[tile.getTier()];
        gCol = g[tile.getTier()];
        bCol = b[tile.getTier()];

        powerLevel = (float) MathHelper.approachExp(powerLevel, fxState, 0.05);
        BCProfiler.TICK.stop();
    }

    @Override
    public void render(VertexConsumer buffer, Camera renderInfo, float partialTicks) {
        if (powerLevel <= 0 && !ClientEventHandler.playerHoldingWrench) {
            return;
        }
        BCProfiler.RENDER.start("crystal_beam_fx");

        float scale = 0.1F * powerLevel;
        if (ClientEventHandler.playerHoldingWrench) {
            scale = 0.1F;
        }

        Vec3 viewVec = renderInfo.getPosition();
        Vector3 source = new Vector3(x - viewVec.x, y - viewVec.y, z - viewVec.z);
        Vector3 target = linkTarget.toVector3().subtract(viewVec.x, viewVec.y, viewVec.z);
        Vector3 dirVec = source.copy().subtract(target).normalize();
        Vector3 planeA = dirVec.copy().perpendicular().normalize();
        Vector3 planeB = dirVec.copy().crossProduct(planeA);
        Vector3 planeC = planeB.copy().rotate(45 * MathHelper.torad, dirVec).normalize();
        Vector3 planeD = planeB.copy().rotate(-45 * MathHelper.torad, dirVec).normalize();
        planeA.multiply(scale);
        planeB.multiply(scale);
        planeC.multiply(scale);
        planeD.multiply(scale);
        float dist = 0.2F * (float) Utils.getDistance(new Vec3D(source), new Vec3D(target));
        float anim = (ClientEventHandler.elapsedTicks + partialTicks) / -15F;
        float red = 1F;
        float green = tile.getTier() == 1 ? 0.3F : 1F;
        if (ClientEventHandler.playerHoldingWrench) {
            red = green = 0;
        }

        Vector3 p1 = source.copy().add(planeA);
        Vector3 p2 = target.copy().add(planeA);
        Vector3 p3 = source.copy().subtract(planeA);
        Vector3 p4 = target.copy().subtract(planeA);
        bufferQuad(buffer, p1, p2, p3, p4, anim, dist, red, green);

        p1 = source.copy().add(planeB);
        p2 = target.copy().add(planeB);
        p3 = source.copy().subtract(planeB);
        p4 = target.copy().subtract(planeB);
        bufferQuad(buffer, p1, p2, p3, p4, anim, dist, red, green);

        p1 = source.copy().add(planeC);
        p2 = target.copy().add(planeC);
        p3 = source.copy().subtract(planeC);
        p4 = target.copy().subtract(planeC);
        bufferQuad(buffer, p1, p2, p3, p4, anim, dist, red, green);

        p1 = source.copy().add(planeD);
        p2 = target.copy().add(planeD);
        p3 = source.copy().subtract(planeD);
        p4 = target.copy().subtract(planeD);
        bufferQuad(buffer, p1, p2, p3, p4, anim, dist, red, green);

        scale *= 2;
        float minU = 0.0F;
        float maxU = 0.53F;
        float minV = 0.0F;
        float maxV = 0.53F;


        if (terminateSource) {
            float viewX = (float) (this.x - viewVec.x());
            float viewY = (float) (this.y - viewVec.y());
            float viewZ = (float) (this.z - viewVec.z());
            Vector3f[] renderVector = getRenderVectors(renderInfo, viewX, viewY, viewZ, scale);
            buffer.vertex(renderVector[0].x(), renderVector[0].y(), renderVector[0].z()).color(red, green, 1F, 1F).uv(maxU, maxV).uv2(240, 240).endVertex();
            buffer.vertex(renderVector[1].x(), renderVector[1].y(), renderVector[1].z()).color(red, green, 1F, 1F).uv(maxU, minV).uv2(240, 240).endVertex();
            buffer.vertex(renderVector[2].x(), renderVector[2].y(), renderVector[2].z()).color(red, green, 1F, 1F).uv(minU, minV).uv2(240, 240).endVertex();
            buffer.vertex(renderVector[3].x(), renderVector[3].y(), renderVector[3].z()).color(red, green, 1F, 1F).uv(minU, maxV).uv2(240, 240).endVertex();
        }

        if (terminateTarget) {
            float viewX = (float) (this.linkTarget.x - viewVec.x());
            float viewY = (float) (this.linkTarget.y - viewVec.y());
            float viewZ = (float) (this.linkTarget.z - viewVec.z());
            Vector3f[] renderVector = getRenderVectors(renderInfo, viewX, viewY, viewZ, scale);
            buffer.vertex(renderVector[0].x(), renderVector[0].y(), renderVector[0].z()).color(red, green, 1F, 1F).uv(maxU, maxV).uv2(240, 240).endVertex();
            buffer.vertex(renderVector[1].x(), renderVector[1].y(), renderVector[1].z()).color(red, green, 1F, 1F).uv(maxU, minV).uv2(240, 240).endVertex();
            buffer.vertex(renderVector[2].x(), renderVector[2].y(), renderVector[2].z()).color(red, green, 1F, 1F).uv(minU, minV).uv2(240, 240).endVertex();
            buffer.vertex(renderVector[3].x(), renderVector[3].y(), renderVector[3].z()).color(red, green, 1F, 1F).uv(minU, maxV).uv2(240, 240).endVertex();
        }

        BCProfiler.RENDER.stop();
    }

    private void bufferQuad(VertexConsumer buffer, Vector3 p1, Vector3 p2, Vector3 p3, Vector3 p4, float anim, float dist, float red, float green) {
        BCProfiler.RENDER.start("buffer_quad");
        buffer.vertex(p1.x, p1.y, p1.z).color(red, green, 1F, 1F).uv(0.5F, anim).uv2(240, 240).endVertex();
        buffer.vertex(p2.x, p2.y, p2.z).color(red, green, 1F, 1F).uv(0.5F, dist + anim).uv2(240, 240).endVertex();
        buffer.vertex(p4.x, p4.y, p4.z).color(red, green, 1F, 1F).uv(1.0F, dist + anim).uv2(240, 240).endVertex();
        buffer.vertex(p3.x, p3.y, p3.z).color(red, green, 1F, 1F).uv(1.0F, anim).uv2(240, 240).endVertex();
        BCProfiler.RENDER.stop();
    }

    @Override
    public ParticleRenderType getRenderType() {
        return tile.getTier() == 0 ? BASIC_HANDLER : tile.getTier() == 1 ? WYVERN_HANDLER : DRACONIC_HANDLER;
    }

    private static final ParticleRenderType BASIC_HANDLER = new FXHandler(new ResourceLocation(DraconicEvolution.MODID, "textures/particle/energy_beam_basic.png"));
    private static final ParticleRenderType WYVERN_HANDLER = new FXHandler(new ResourceLocation(DraconicEvolution.MODID, "textures/particle/energy_beam_wyvern.png"));
    private static final ParticleRenderType DRACONIC_HANDLER = new FXHandler(new ResourceLocation(DraconicEvolution.MODID, "textures/particle/energy_beam_draconic.png"));

    public static class FXHandler implements ParticleRenderType {
        private static final ResourceLocation highlightTexture = new ResourceLocation(DraconicEvolution.MODID, "textures/particle/energy_beam_highlight.png");
        private ResourceLocation texture;
        private float green;

        public FXHandler(ResourceLocation texture) {
            this.texture = texture;
            this.green = texture.getPath().contains("energy_beam_wyvern") ? 0.3F : 1F;
        }

        @Override
        public void begin(BufferBuilder builder, TextureManager textureManager) {
            RenderSystem.setShader(GameRenderer::getPositionColorTexLightmapShader);
            RenderSystem.disableCull();
            RenderSystem.depthMask(false);
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
            if (ClientEventHandler.playerHoldingWrench) {
                RenderSystem.setShaderTexture(0, highlightTexture);
            } else {
                RenderSystem.setShaderTexture(0, texture);
            }
            builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP);
        }

        @Override
        public void end(Tesselator tessellator) {
            tessellator.end();
        }
    }
}