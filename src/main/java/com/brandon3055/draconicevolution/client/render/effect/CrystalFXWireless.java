package com.brandon3055.draconicevolution.client.render.effect;

import codechicken.lib.math.MathHelper;
import codechicken.lib.vec.Vector3;
import com.brandon3055.draconicevolution.blocks.energynet.tileentity.TileCrystalWirelessIO;
import com.brandon3055.draconicevolution.client.DEMiscSprites;
import com.brandon3055.draconicevolution.client.handler.ClientEventHandler;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Vector3f;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by brandon3055 on 29/11/2016.
 */
public class CrystalFXWireless extends CrystalFXBase<TileCrystalWirelessIO> {

    private final BlockPos linkTarget;
    private final AABB targetBB;
    private float powerLevel = 0;
    private List<PTracker> trackers = new ArrayList<>();

    public CrystalFXWireless(ClientLevel worldIn, TileCrystalWirelessIO tile, BlockPos linkTarget) {
        super(worldIn, tile);
        this.age = worldIn.random.nextInt(1024);
        this.setPosition(tile.getBeamLinkPos(linkTarget));
        this.linkTarget = linkTarget;
        BlockState state = worldIn.getBlockState(linkTarget);
        VoxelShape shape = state.getShape(worldIn, linkTarget);
        if (shape.isEmpty()) shape = Shapes.block();
        targetBB = shape.bounds();
        targetBB.deflate(0.05);
        setBoundingBox(new AABB(x, y, z, this.linkTarget.getX(), this.linkTarget.getY(), this.linkTarget.getZ()));
    }

    @Override
    public void tick() {
        super.tick();
        if (ticksTillDeath-- <= 0) {
            remove();
        }

        rCol = gCol = bCol = alpha = 1;
        powerLevel = (float) MathHelper.approachExp(powerLevel, fxState, 0.05);

        Iterator<PTracker> i = trackers.iterator();
        while (i.hasNext()) {
            PTracker next = i.next();
            if (next.ticksActive >= next.travelTime) {
                i.remove();
            } else {
                next.ticksActive++;
            }
        }

        int ps = Minecraft.getInstance().options.particles.getId();
        if (age % 2 == 0 && powerLevel > random.nextFloat() && (ps == 0 || (ps == 1 && random.nextInt(3) == 0) || (ps == 2 && random.nextInt(10) == 0))) {
            double travel = 50 + random.nextInt(50);
            travel *= (1.4F - powerLevel);
            trackers.add(new PTracker((int) travel, new Vector3(targetBB.minX + (random.nextDouble() * (targetBB.maxX - targetBB.minX)), targetBB.minY + (random.nextDouble() * (targetBB.maxY - targetBB.minY)), targetBB.minZ + (random.nextDouble() * (targetBB.maxZ - targetBB.minZ)))));
        }

        age++;
    }

    @Override
    public void render(VertexConsumer buffer, Camera renderInfo, float partialTicks) {
        int texIndex = (ClientEventHandler.elapsedTicks) % DEMiscSprites.ENERGY_PARTICLE.length;
        TextureAtlasSprite sprite = DEMiscSprites.ENERGY_PARTICLE[texIndex];
        if (sprite == null) return;
        float minU = sprite.getU0();
        float maxU = sprite.getU1();
        float minV = sprite.getV0();
        float maxV = sprite.getV1();

        float scale = 0.08F;
        boolean output = !tile.inputMode.get();

        Vec3 view = renderInfo.getPosition();
        Vector3 source = new Vector3(x - view.x, y - view.y, z - view.z);
        Vector3 target = Vector3.fromBlockPos(linkTarget).subtract(view.x, view.y, view.z);

        for (PTracker tracker : trackers) {
            double progress = ((double) tracker.ticksActive + partialTicks) / (double) tracker.travelTime;
            if (!output) progress = 1 - progress;
            if (progress >= 1 || progress <= 0) {
                continue;
            }
            Vector3 pathVec = target.copy().subtract(source);
            pathVec.add(tracker.tOffset);
            pathVec.multiply(progress);
            pathVec.add(source);

            Vector3f[] renderVector = getRenderVectors(renderInfo, (float) pathVec.x, (float) pathVec.y, (float) pathVec.z, scale);
            buffer.vertex(renderVector[0].x(), renderVector[0].y(), renderVector[0].z()).color(0, 1F, 1F, 1F).uv(maxU, maxV).uv2(240, 240).endVertex();
            buffer.vertex(renderVector[1].x(), renderVector[1].y(), renderVector[1].z()).color(0, 1F, 1F, 1F).uv(maxU, minV).uv2(240, 240).endVertex();
            buffer.vertex(renderVector[2].x(), renderVector[2].y(), renderVector[2].z()).color(0, 1F, 1F, 1F).uv(minU, minV).uv2(240, 240).endVertex();
            buffer.vertex(renderVector[3].x(), renderVector[3].y(), renderVector[3].z()).color(0, 1F, 1F, 1F).uv(minU, maxV).uv2(240, 240).endVertex();
        }
    }

    @Override
    public ParticleRenderType getRenderType() {
        return tile.getTier() == 0 ? BASIC_HANDLER : tile.getTier() == 1 ? WYVERN_HANDLER : DRACONIC_HANDLER;
    }

    private static final ParticleRenderType BASIC_HANDLER = new FXHandler();
    private static final ParticleRenderType WYVERN_HANDLER = new FXHandler();
    private static final ParticleRenderType DRACONIC_HANDLER = new FXHandler();

    public static class FXHandler implements ParticleRenderType {
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
            tessellator.end();
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        }
    }

    public static class PTracker {
        public int ticksActive = 0;
        public final int travelTime;
        public final Vector3 tOffset;

        public PTracker(int travelTime, Vector3 tOffset) {
            this.travelTime = travelTime;
            this.tOffset = tOffset;
        }
    }
}