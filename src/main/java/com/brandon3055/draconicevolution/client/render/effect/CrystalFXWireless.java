package com.brandon3055.draconicevolution.client.render.effect;

import codechicken.lib.math.MathHelper;
import codechicken.lib.render.state.GlStateManagerHelper;
import codechicken.lib.vec.Vector3;
import com.brandon3055.brandonscore.client.particle.IGLFXHandler;
import com.brandon3055.draconicevolution.blocks.energynet.tileentity.TileCrystalBase;
import com.brandon3055.draconicevolution.client.DEParticles;
import com.brandon3055.draconicevolution.client.handler.ClientEventHandler;
import com.brandon3055.draconicevolution.helpers.ResourceHelperDE;
import com.brandon3055.draconicevolution.utils.DETextures;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by brandon3055 on 29/11/2016.
 */
public class CrystalFXWireless extends CrystalGLFXBase<TileCrystalBase> {

    private final BlockPos linkTarget;
    private final AxisAlignedBB targetBB;
    private float powerLevel = 0;
    private List<PTracker> trackers = new ArrayList<>();

    public CrystalFXWireless(World worldIn, TileCrystalBase tile, BlockPos linkTarget) {
        super(worldIn, tile);
        this.particleTextureIndexX = 3 + tile.getTier();
        this.particleAge = worldIn.rand.nextInt(1024);
        this.setPosition(tile.getBeamLinkPos(linkTarget));
        this.linkTarget = linkTarget;
        this.particleTextureIndexY = 1;
        this.texturesPerRow = 8;
        IBlockState state = worldIn.getBlockState(linkTarget);
        targetBB = state.getBoundingBox(worldIn, linkTarget);
        targetBB.contract(0.05);
    }

    @Override
    public int getFXLayer() {
        return 1;
    }

    @Override
    public void onUpdate() {
        if (ticksTillDeath-- <= 0) {
            setExpired();
        }

//        float[] r = {0.0F, 0.8F, 1.0F};
//        float[] g = {0.8F, 0.1F, 0.7F};
//        float[] b = {1F, 1F, 0.2F};

//        particleRed = r[tile.getTier()];
//        particleGreen = g[tile.getTier()];
//        particleBlue = b[tile.getTier()];
        particleRed = particleGreen = particleBlue = particleAlpha = 1;

        powerLevel = (float) MathHelper.approachExp(powerLevel, fxState, 0.05);

        Iterator<PTracker> i = trackers.iterator();
        while (i.hasNext()) {
            PTracker next = i.next();
            if (next.ticksActive >= next.travelTime) {
                i.remove();
            }
            else {
                next.ticksActive++;
            }
        }

//        if (ClientEventHandler.elapsedTicks % 10 == 0) {
//            LogHelper.dev(targetBB);
        int ps = Minecraft.getMinecraft().gameSettings.particleSetting;
        if (particleAge % 2 == 0 && powerLevel > rand.nextFloat() && (ps == 0 || (ps == 1 && rand.nextInt(3) == 0) || (ps == 2 && rand.nextInt(10) == 0))) {
            double travel = 50 + rand.nextInt(50);
            travel *= (1.4F - powerLevel);

            trackers.add(new PTracker((int) travel, new Vector3(targetBB.minX + (rand.nextDouble() * (targetBB.maxX - targetBB.minX)), targetBB.minY + (rand.nextDouble() * (targetBB.maxY - targetBB.minY)), targetBB.minZ + (rand.nextDouble() * (targetBB.maxZ - targetBB.minZ)))));
        }

        particleTextureIndexX = ClientEventHandler.elapsedTicks % 5;
    }

    @Override
    public void renderParticle(VertexBuffer buffer, Entity entity, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
//        if (powerLevel <= 0) {
//            return;
//        }
        double scale = 0.08;// * powerLevel;

        float minU = (float) this.particleTextureIndexX / texturesPerRow;
        float maxU = minU + 1F / texturesPerRow;
        float minV = (float) this.particleTextureIndexY / texturesPerRow;
        float maxV = minV + 1F / texturesPerRow;

        Vector3 source = new Vector3(posX - interpPosX, posY - interpPosY, posZ - interpPosZ);
        Vector3 target = Vector3.fromBlockPos(linkTarget).subtract(interpPosX, interpPosY, interpPosZ);

        for (PTracker tracker : trackers) {
            double progress = ((double) tracker.ticksActive + partialTicks) / (double) tracker.travelTime;
            if (progress >= 1) {
                continue;
            }
            Vector3 pathVec = target.copy().add(tracker.tOffset).subtract(source);
            pathVec.multiply(progress);

            pathVec.add(source);

            buffer.pos((pathVec.x - rotationX * scale - rotationXY * scale), (pathVec.y - rotationZ * scale), (pathVec.z - rotationYZ * scale - rotationXZ * scale)).tex((double) maxU, (double) maxV)/*.color(particleRed, particleGreen, particleBlue, particleAlpha)*/.endVertex();
            buffer.pos((pathVec.x - rotationX * scale + rotationXY * scale), (pathVec.y + rotationZ * scale), (pathVec.z - rotationYZ * scale + rotationXZ * scale)).tex((double) maxU, (double) minV)/*.color(particleRed, particleGreen, particleBlue, particleAlpha)*/.endVertex();
            buffer.pos((pathVec.x + rotationX * scale + rotationXY * scale), (pathVec.y + rotationZ * scale), (pathVec.z + rotationYZ * scale + rotationXZ * scale)).tex((double) minU, (double) minV)/*.color(particleRed, particleGreen, particleBlue, particleAlpha)*/.endVertex();
            buffer.pos((pathVec.x + rotationX * scale - rotationXY * scale), (pathVec.y - rotationZ * scale), (pathVec.z + rotationYZ * scale - rotationXZ * scale)).tex((double) minU, (double) maxV)/*.color(particleRed, particleGreen, particleBlue, particleAlpha)*/.endVertex();
        }
    }

    private void bufferQuad(VertexBuffer buffer, Vector3 p1, Vector3 p2, Vector3 p3, Vector3 p4, double anim, double dist) {
        buffer.pos(p1.x, p1.y, p1.z).tex(0.5, anim).endVertex();
        buffer.pos(p2.x, p2.y, p2.z).tex(0.5, dist + anim).endVertex();
        buffer.pos(p4.x, p4.y, p4.z).tex(1.0, dist + anim).endVertex();
        buffer.pos(p3.x, p3.y, p3.z).tex(1.0, anim).endVertex();
    }

    @Override
    public IGLFXHandler getFXHandler() {
        return tile.getTier() == 0 ? BASIC_HANDLER : tile.getTier() == 1 ? WYVERN_HANDLER : DRACONIC_HANDLER;
    }

    private static final FXHandler BASIC_HANDLER = new FXHandler(DETextures.ENERGY_BEAM_BASIC);
    private static final FXHandler WYVERN_HANDLER = new FXHandler(DETextures.ENERGY_BEAM_WYVERN);
    private static final FXHandler DRACONIC_HANDLER = new FXHandler(DETextures.ENERGY_BEAM_DRACONIC);

    public static class FXHandler implements IGLFXHandler {

        private String texture;
        private float green;

        public FXHandler(String texture) {
            this.texture = texture;
        }

        @Override
        public void preDraw(int layer, VertexBuffer vertexbuffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
            GlStateManager.color(0.0F, 1.0F, 1.0F, 1.0F);
            GlStateManagerHelper.pushState();
            GlStateManager.depthMask(false);
            GlStateManager.glTexParameterf(3553, 10242, 10497.0F);
            GlStateManager.glTexParameterf(3553, 10243, 10497.0F);
            GlStateManager.disableCull();
            GlStateManager.alphaFunc(GL11.GL_GREATER, 0F);
            ResourceHelperDE.bindTexture(DEParticles.DE_SHEET);
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);


            vertexbuffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        }

        @Override
        public void postDraw(int layer, VertexBuffer vertexbuffer, Tessellator tessellator) {
            tessellator.draw();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            GlStateManagerHelper.popState();
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