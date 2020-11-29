package com.brandon3055.draconicevolution.client.render.effect;

import codechicken.lib.math.MathHelper;
import codechicken.lib.vec.Vector3;
import com.brandon3055.draconicevolution.blocks.energynet.tileentity.TileCrystalWirelessIO;
import com.brandon3055.draconicevolution.client.DETextures;
import com.brandon3055.draconicevolution.client.handler.ClientEventHandler;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by brandon3055 on 29/11/2016.
 */
public class CrystalFXWireless extends CrystalFXBase<TileCrystalWirelessIO> {

    private final BlockPos linkTarget;
    private final AxisAlignedBB targetBB;
    private float powerLevel = 0;
    private List<PTracker> trackers = new ArrayList<>();

    public CrystalFXWireless(World worldIn, TileCrystalWirelessIO tile, BlockPos linkTarget) {
        super(worldIn, tile);
        this.age = worldIn.rand.nextInt(1024);
        this.setPosition(tile.getBeamLinkPos(linkTarget));
        this.linkTarget = linkTarget;
        BlockState state = worldIn.getBlockState(linkTarget);
        VoxelShape shape = state.getShape(worldIn, linkTarget);
        if (shape.isEmpty()) shape = VoxelShapes.fullCube();
        targetBB = shape.getBoundingBox();
        targetBB.shrink(0.05);
    }

    //    public CrystalFXWireless(World worldIn, TileCrystalWirelessIO tile, BlockPos linkTarget) {
//        super(worldIn, tile);
////        this.particleTextureIndexX = 3 + tile.getTier();
//        this.age = worldIn.rand.nextInt(1024);
//        this.setPosition(tile.getBeamLinkPos(linkTarget));
//        this.linkTarget = linkTarget;
////        this.particleTextureIndexY = 1;
//        this.texturesPerRow = 8;
//        BlockState state = worldIn.getBlockState(linkTarget);
//        targetBB = state.getShape(worldIn, linkTarget).getBoundingBox();
//        targetBB.shrink(0.05);
//    }

//    @Override
//    public int getFXLayer() {
//        return 1;
//    }

    @Override
    public void tick() {
        super.tick();
        if (ticksTillDeath-- <= 0) {
            setExpired();
        }

        particleRed = particleGreen = particleBlue = particleAlpha = 1;
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

        int ps = Minecraft.getInstance().gameSettings.particles.getId();
        if (age % 2 == 0 && powerLevel > rand.nextFloat() && (ps == 0 || (ps == 1 && rand.nextInt(3) == 0) || (ps == 2 && rand.nextInt(10) == 0))) {
            double travel = 50 + rand.nextInt(50);
            travel *= (1.4F - powerLevel);
            trackers.add(new PTracker((int) travel, new Vector3(targetBB.minX + (rand.nextDouble() * (targetBB.maxX - targetBB.minX)), targetBB.minY + (rand.nextDouble() * (targetBB.maxY - targetBB.minY)), targetBB.minZ + (rand.nextDouble() * (targetBB.maxZ - targetBB.minZ)))));
        }

        age++;
    }

    @Override
    public void renderParticle(IVertexBuilder buffer, ActiveRenderInfo renderInfo, float partialTicks) {
        int texIndex = (ClientEventHandler.elapsedTicks) % DETextures.ENERGY_PARTICLE.length;
        TextureAtlasSprite sprite = DETextures.ENERGY_PARTICLE[texIndex];
        if (sprite == null) return;
        float minU = sprite.getMinU();
        float maxU = sprite.getMaxU();
        float minV = sprite.getMinV();
        float maxV = sprite.getMaxV();

        float scale = 0.08F;
        boolean output = !tile.inputMode.get();

        Vec3d view = renderInfo.getProjectedView();
        Vector3 source = new Vector3(posX - view.x, posY - view.y, posZ - view.z);
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
            buffer.pos(renderVector[0].getX(), renderVector[0].getY(), renderVector[0].getZ()).tex(maxU, maxV).endVertex();
            buffer.pos(renderVector[1].getX(), renderVector[1].getY(), renderVector[1].getZ()).tex(maxU, minV).endVertex();
            buffer.pos(renderVector[2].getX(), renderVector[2].getY(), renderVector[2].getZ()).tex(minU, minV).endVertex();
            buffer.pos(renderVector[3].getX(), renderVector[3].getY(), renderVector[3].getZ()).tex(minU, maxV).endVertex();
        }
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
        private float green;

        public FXHandler(String texture) {
            this.texture = texture;
        }

        @Override
        public void beginRender(BufferBuilder builder, TextureManager textureManager) {
            RenderSystem.color4f(0.0F, 1.0F, 1.0F, 1.0F);
            textureManager.bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);

            RenderSystem.depthMask(false);
            RenderSystem.alphaFunc(516, 0.003921569F);
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
            RenderSystem.glMultiTexCoord2f(0x84c2, 240.0F, 240.0F); //Lightmap

            builder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        }

        @Override
        public void finishRender(Tessellator tessellator) {
            tessellator.draw();
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