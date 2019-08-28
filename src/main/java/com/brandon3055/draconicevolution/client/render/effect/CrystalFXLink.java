package com.brandon3055.draconicevolution.client.render.effect;

import codechicken.lib.math.MathHelper;
import codechicken.lib.render.state.GlStateTracker;
import codechicken.lib.vec.Vector3;
import com.brandon3055.brandonscore.client.particle.IGLFXHandler;
import com.brandon3055.brandonscore.lib.Vec3D;
import com.brandon3055.draconicevolution.blocks.energynet.tileentity.TileCrystalBase;
import com.brandon3055.draconicevolution.blocks.energynet.tileentity.TileCrystalWirelessIO;
import com.brandon3055.draconicevolution.client.handler.ClientEventHandler;
import com.brandon3055.draconicevolution.helpers.ResourceHelperDE;
import com.brandon3055.draconicevolution.utils.DETextures;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

/**
 * Created by brandon3055 on 29/11/2016.
 */
public class CrystalFXLink extends CrystalGLFXBase<TileCrystalBase> {

    private final Vec3D linkTarget;
    private final boolean terminateSource;
    private final boolean terminateTarget;
    public int timeout = 0;

    public CrystalFXLink(World worldIn, TileCrystalBase tile, Vec3D linkTarget) {
        super(worldIn, tile);
        this.particleTextureIndexX = 3 + tile.getTier();
        this.particleAge = worldIn.rand.nextInt(1024);
        this.setPosition(tile.getBeamLinkPos(linkTarget.getPos()));
        this.terminateSource = true;
        this.linkTarget = linkTarget;
        EnumFacing face = tile instanceof TileCrystalWirelessIO ? ((TileCrystalWirelessIO) tile).getReceiversFaces().get(linkTarget.getPos()) : null;
        if (face != null) {
            linkTarget.add(face.getXOffset() * 0.6, face.getYOffset() * 0.6, face.getZOffset() * 0.6);
        }
        this.terminateTarget = true;
    }

    @Override
    public int getFXLayer() {
        return 1;
    }

    @Override
    public void onUpdate() {
        if (!ClientEventHandler.playerHoldingWrench && timeout <= 0) {
            setExpired();
        }
        else if (timeout > 0) {
            timeout--;
        }
    }

    @Override
    public void renderParticle(BufferBuilder buffer, Entity entity, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
        double scale = 0.1 + (timeout * 0.005);
        Vector3 source = new Vector3(posX - interpPosX, posY - interpPosY, posZ - interpPosZ);
        Vector3 target = linkTarget.toVector3().subtract(interpPosX, interpPosY, interpPosZ);
        Vector3 dirVec = source.copy().subtract(target).normalize();
        Vector3 planeA = dirVec.copy().perpendicular().normalize();
        Vector3 planeB = dirVec.copy().crossProduct(planeA);
        Vector3 planeC = planeB.copy().rotate(45 * MathHelper.torad, dirVec).normalize();
        Vector3 planeD = planeB.copy().rotate(-45 * MathHelper.torad, dirVec).normalize();
        planeA.multiply(scale);
        planeB.multiply(scale);
        planeC.multiply(scale);
        planeD.multiply(scale);
        double dist = 0.2 * source.copy().subtract(target).mag();//Utils.getDistanceAtoB(new Vec3D(source), new Vec3D(target));
        double anim = (ClientEventHandler.elapsedTicks + partialTicks) / -15D;

        Vector3 p1 = source.copy().add(planeA);
        Vector3 p2 = target.copy().add(planeA);
        Vector3 p3 = source.copy().subtract(planeA);
        Vector3 p4 = target.copy().subtract(planeA);
        bufferQuad(buffer, p1, p2, p3, p4, anim, dist);

        p1 = source.copy().add(planeB);
        p2 = target.copy().add(planeB);
        p3 = source.copy().subtract(planeB);
        p4 = target.copy().subtract(planeB);
        bufferQuad(buffer, p1, p2, p3, p4, anim, dist);

        p1 = source.copy().add(planeC);
        p2 = target.copy().add(planeC);
        p3 = source.copy().subtract(planeC);
        p4 = target.copy().subtract(planeC);
        bufferQuad(buffer, p1, p2, p3, p4, anim, dist);

        p1 = source.copy().add(planeD);
        p2 = target.copy().add(planeD);
        p3 = source.copy().subtract(planeD);
        p4 = target.copy().subtract(planeD);
        bufferQuad(buffer, p1, p2, p3, p4, anim, dist);

        scale *= 2;
        double minU = 0.0;
        double maxU = 0.53;
        double minV = 0.0;
        double maxV = 0.53;

        if (terminateSource) {
            buffer.pos((source.x - rotationX * scale - rotationXY * scale), (source.y - rotationZ * scale), (source.z - rotationYZ * scale - rotationXZ * scale)).tex(maxU, maxV).endVertex();
            buffer.pos((source.x - rotationX * scale + rotationXY * scale), (source.y + rotationZ * scale), (source.z - rotationYZ * scale + rotationXZ * scale)).tex(maxU, minV).endVertex();
            buffer.pos((source.x + rotationX * scale + rotationXY * scale), (source.y + rotationZ * scale), (source.z + rotationYZ * scale + rotationXZ * scale)).tex(minU, minV).endVertex();
            buffer.pos((source.x + rotationX * scale - rotationXY * scale), (source.y - rotationZ * scale), (source.z + rotationYZ * scale - rotationXZ * scale)).tex(minU, maxV).endVertex();
        }

        if (terminateTarget) {
            buffer.pos((target.x - rotationX * scale - rotationXY * scale), (target.y - rotationZ * scale), (target.z - rotationYZ * scale - rotationXZ * scale)).tex(maxU, maxV).endVertex();
            buffer.pos((target.x - rotationX * scale + rotationXY * scale), (target.y + rotationZ * scale), (target.z - rotationYZ * scale + rotationXZ * scale)).tex(maxU, minV).endVertex();
            buffer.pos((target.x + rotationX * scale + rotationXY * scale), (target.y + rotationZ * scale), (target.z + rotationYZ * scale + rotationXZ * scale)).tex(minU, minV).endVertex();
            buffer.pos((target.x + rotationX * scale - rotationXY * scale), (target.y - rotationZ * scale), (target.z + rotationYZ * scale - rotationXZ * scale)).tex(minU, maxV).endVertex();
        }
    }

    private void bufferQuad(BufferBuilder buffer, Vector3 p1, Vector3 p2, Vector3 p3, Vector3 p4, double anim, double dist) {
        buffer.pos(p1.x, p1.y, p1.z).tex(0.5, anim).endVertex();
        buffer.pos(p2.x, p2.y, p2.z).tex(0.5, dist + anim).endVertex();
        buffer.pos(p4.x, p4.y, p4.z).tex(1.0, dist + anim).endVertex();
        buffer.pos(p3.x, p3.y, p3.z).tex(1.0, anim).endVertex();
    }

    @Override
    public IGLFXHandler getFXHandler() {
        return HANDLER;
    }

    private static final FXHandler HANDLER = new FXHandler();

    public static class FXHandler implements IGLFXHandler {

        public FXHandler() {
        }

        @Override
        public void preDraw(int layer, BufferBuilder vertexbuffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
            GlStateTracker.pushState();
            GlStateManager.depthMask(false);
            GlStateManager.glTexParameterf(3553, 10242, 10497.0F);
            GlStateManager.glTexParameterf(3553, 10243, 10497.0F);
            GlStateManager.disableCull();
            GlStateManager.alphaFunc(GL11.GL_GREATER, 0F);
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
            ResourceHelperDE.bindTexture(DETextures.ENERGY_BEAM_BASIC);
//            GlStateManager.disableTexture2D();
            GlStateManager.color(1, 0, 0, 1);


            vertexbuffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        }

        @Override
        public void postDraw(int layer, BufferBuilder vertexbuffer, Tessellator tessellator) {
            tessellator.draw();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
//            GlStateManager.enableTexture2D();
            GlStateTracker.popState();
        }
    }
}