package com.brandon3055.draconicevolution.client.render.effect;

import codechicken.lib.math.MathHelper;
import codechicken.lib.vec.Vector3;
import com.brandon3055.draconicevolution.blocks.energynet.tileentity.TileCrystalWirelessIO;
import com.brandon3055.draconicevolution.client.handler.ClientEventHandler;
import com.brandon3055.draconicevolution.utils.DETextures;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.FireworkRocketItem;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
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

        int ps = Minecraft.getInstance().gameSettings.particles.func_216832_b();
        if (age % 2 == 0 && powerLevel > rand.nextFloat() && (ps == 0 || (ps == 1 && rand.nextInt(3) == 0) || (ps == 2 && rand.nextInt(10) == 0))) {
            double travel = 50 + rand.nextInt(50);
            travel *= (1.4F - powerLevel);
            trackers.add(new PTracker((int) travel, new Vector3(targetBB.minX + (rand.nextDouble() * (targetBB.maxX - targetBB.minX)), targetBB.minY + (rand.nextDouble() * (targetBB.maxY - targetBB.minY)), targetBB.minZ + (rand.nextDouble() * (targetBB.maxZ - targetBB.minZ)))));
        }

//        particleTextureIndexX = ClientEventHandler.elapsedTicks % 5;
        age++;
    }

    @Override
    public void renderParticle(BufferBuilder buffer, ActiveRenderInfo entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
        int texIndex = (ClientEventHandler.elapsedTicks) % DETextures.ENERGY_PARTICLE.length;
        TextureAtlasSprite sprite = DETextures.ENERGY_PARTICLE[texIndex];
        float minU = sprite.getMinU();
        float maxU = sprite.getMaxU();
        float minV = sprite.getMinV();
        float maxV = sprite.getMaxV();


        double scale = 0.08;// * powerLevel;
        boolean output = !tile.inputMode.get();

        Vector3 source = new Vector3(posX - interpPosX, posY - interpPosY, posZ - interpPosZ);
        Vector3 target = Vector3.fromBlockPos(linkTarget).subtract(interpPosX, interpPosY, interpPosZ);

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

            buffer.pos((pathVec.x - rotationX * scale - rotationXY * scale), (pathVec.y - rotationZ * scale), (pathVec.z - rotationYZ * scale - rotationXZ * scale)).tex((double) maxU, (double) maxV)/*.color(particleRed, particleGreen, particleBlue, particleAlpha)*/.endVertex();
            buffer.pos((pathVec.x - rotationX * scale + rotationXY * scale), (pathVec.y + rotationZ * scale), (pathVec.z - rotationYZ * scale + rotationXZ * scale)).tex((double) maxU, (double) minV)/*.color(particleRed, particleGreen, particleBlue, particleAlpha)*/.endVertex();
            buffer.pos((pathVec.x + rotationX * scale + rotationXY * scale), (pathVec.y + rotationZ * scale), (pathVec.z + rotationYZ * scale + rotationXZ * scale)).tex((double) minU, (double) minV)/*.color(particleRed, particleGreen, particleBlue, particleAlpha)*/.endVertex();
            buffer.pos((pathVec.x + rotationX * scale - rotationXY * scale), (pathVec.y - rotationZ * scale), (pathVec.z + rotationYZ * scale - rotationXZ * scale)).tex((double) minU, (double) maxV)/*.color(particleRed, particleGreen, particleBlue, particleAlpha)*/.endVertex();
        }
    }

    private void bufferQuad(BufferBuilder buffer, Vector3 p1, Vector3 p2, Vector3 p3, Vector3 p4, double anim, double dist) {
        buffer.pos(p1.x, p1.y, p1.z).tex(0.5, anim).endVertex();
        buffer.pos(p2.x, p2.y, p2.z).tex(0.5, dist + anim).endVertex();
        buffer.pos(p4.x, p4.y, p4.z).tex(1.0, dist + anim).endVertex();
        buffer.pos(p3.x, p3.y, p3.z).tex(1.0, anim).endVertex();
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
            GlStateManager.color4f(0.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.depthMask(false);
            GlStateManager.texParameter(3553, 10242, 10497.0F);
            GlStateManager.texParameter(3553, 10243, 10497.0F);
            GlStateManager.disableCull();
            GlStateManager.alphaFunc(GL11.GL_GREATER, 0F);
            textureManager.bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
            builder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        }

        @Override
        public void finishRender(Tessellator tessellator) {
            tessellator.draw();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
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