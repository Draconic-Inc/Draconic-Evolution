package com.brandon3055.draconicevolution.client.render.effect;

import codechicken.lib.math.MathHelper;
import codechicken.lib.render.state.GlStateManagerHelper;
import codechicken.lib.vec.Vector3;
import com.brandon3055.brandonscore.client.particle.IGLFXHandler;
import com.brandon3055.brandonscore.lib.Vec3D;
import com.brandon3055.brandonscore.utils.Utils;
import com.brandon3055.draconicevolution.blocks.energynet.tileentity.TileCrystalBase;
import com.brandon3055.draconicevolution.client.handler.ClientEventHandler;
import com.brandon3055.draconicevolution.helpers.ResourceHelperDE;
import com.brandon3055.draconicevolution.utils.DETextures;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

/**
 * Created by brandon3055 on 29/11/2016.
 */
public class CrystalFXBeam extends CrystalGLFXBase<TileCrystalBase> {

    private final BlockPos linkTarget;

    public CrystalFXBeam(World worldIn, TileCrystalBase tile, BlockPos linkTarget) {
        super(worldIn, tile);
        this.linkTarget = linkTarget;
        this.particleTextureIndexX = 3 + tile.getTier();
        this.particleAge = worldIn.rand.nextInt(1024);
    }

    @Override
    public void onUpdate() {
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
    public void renderParticle(VertexBuffer buffer, Entity entity, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
        double scale = 0.1;
        Vector3 source = new Vector3(posX - interpPosX, posY - interpPosY, posZ - interpPosZ);
        Vector3 target = Vector3.fromBlockPos(linkTarget.add(10, 0, 0)).add(0.5).subtract(interpPosX, interpPosY, interpPosZ);
        Vector3 dirVec = source.copy().subtract(target).normalize();
        Vector3 planeA = dirVec.copy().perpendicular().normalize();
        Vector3 planeB = dirVec.copy().crossProduct(planeA);
        Vector3 planeC = planeB.copy().rotate(45 * MathHelper.torad, dirVec).normalize();
        Vector3 planeD = planeB.copy().rotate(-45 * MathHelper.torad, dirVec).normalize();
        planeA.multiply(scale);
        planeB.multiply(scale);
        planeC.multiply(scale);
        planeD.multiply(scale);
        double dist = 0.5 * Utils.getDistanceAtoB(new Vec3D(source), new Vec3D(target)); //todo cache this on particle creation.. Actually... CACHE ALL THE THINGS!!!
        double anim = (ClientEventHandler.elapsedTicks + partialTicks) / -20D;


//        Vector3 sapa = source.copy().add(planeA);
//        Vector3 tapa = target.copy().add(planeA);
//        Vector3 tspa = target.copy().subtract(planeA);
//        Vector3 sspa = source.copy().subtract(planeA);
//
//        Vector3 p1 = sapa.copy();
//        Vector3 p2 = tapa.copy();
//        Vector3 p3 = tspa.copy();
//        Vector3 p4 = sspa.copy();
//
//
////        p2.x -= 0.4;
////        p3.x += 0.4;
//
//        double segXA = (p2.x - p1.x) / 30D;
//        double segYA = (p2.y - p1.y) / 30D;
//        double segZA = (p2.z - p1.z) / 30D;
//        double segXB = (p3.x - p4.x) / 30D;
//        double segYB = (p3.y - p4.y) / 30D;
//        double segZB = (p3.z - p4.z) / 30D;
//        double an0 = anim;
//        double an1 = dist + anim;
//        double animSeg = (an0 - an1) / 30D;
//
//        for (int i = 0; i < 30; i++) {
//            double d = (i / 30D) * 0.25;
//
//            buffer.pos(p1.x + (i * segXA),       p1.y + (i * segYA),       p1.z + (i * segZA)      ).tex(0.75 + d, an0 + i * animSeg    ).endVertex();        //^>
//            buffer.pos(p1.x + ((i + 1) * segXA), p1.y + ((i + 1) * segYA), p1.z + ((i + 1) * segZA)).tex(0.75 + d, an0 + (i+1) * animSeg).endVertex();     //v>
//            buffer.pos(p4.x + ((i + 1) * segXB), p4.y + ((i + 1) * segYB), p4.z + ((i + 1) * segZB)).tex(0.25 - d, an0 + (i+1) * animSeg).endVertex();     //v<
//            buffer.pos(p4.x + (i * segXB),       p4.y + (i * segYB),       p4.z + (i * segZB)      ).tex(0.25 - d, an0 + i * animSeg    ).endVertex();        //^<
//
//
////            buffer.pos(p1.x, p1.y, p1.z).tex(0.75, 0).endVertex();        //^>
////            buffer.pos(p2.x, p2.y, p2.z).tex(0.75, dist).endVertex();     //v>
////
////
////            buffer.pos(p3.x, p3.y, p3.z).tex(0.25, dist).endVertex();     //v<
////            buffer.pos(p4.x, p4.y, p4.z).tex(0.25, 0).endVertex();        //^<
//        }


//
//        buffer.pos(1.4, 0, 0).tex(0, 0).endVertex();
//        buffer.pos(1, 1, 0).tex(0, 1).endVertex();
//        buffer.pos(2, 1, 0).tex(1, 1).endVertex();
//        buffer.pos(1.5, 0, 0).tex(1, 0).endVertex();

//        buffer.pos(p1.x, p1.y, p1.z).tex(0, 0).endVertex();
//        buffer.pos(p2.x, p2.y, p2.z).tex(0, 1).endVertex();
//        buffer.pos(p3.x, p3.y, p3.z).tex(1, 1).endVertex();
//        buffer.pos(p4.x, p4.y, p4.z).tex(1, 0).endVertex();

        Vector3 p1 = source.copy().add(planeA);
        Vector3 p2 = target.copy().add(planeA);
        Vector3 p3 = target.copy().subtract(planeA);
        Vector3 p4 = source.copy().subtract(planeA);
        buffer.pos(p1.x, p1.y, p1.z).tex(0.25, anim).endVertex();
        buffer.pos(p2.x, p2.y, p2.z).tex(0.25, dist + anim).endVertex();
        buffer.pos(p3.x, p3.y, p3.z).tex(0.75, dist + anim).endVertex();
        buffer.pos(p4.x, p4.y, p4.z).tex(0.75, anim).endVertex();

        p1 = source.copy().add(planeB);
        p2 = target.copy().add(planeB);
        p3 = source.copy().subtract(planeB);
        p4 = target.copy().subtract(planeB);
        buffer.pos(p1.x, p1.y, p1.z).tex(0.25, anim).endVertex();
        buffer.pos(p2.x, p2.y, p2.z).tex(0.25, dist + anim).endVertex();
        buffer.pos(p4.x, p4.y, p4.z).tex(0.75, dist + anim).endVertex();
        buffer.pos(p3.x, p3.y, p3.z).tex(0.75, anim).endVertex();

        p1 = source.copy().add(planeC);
        p2 = target.copy().add(planeC);
        p3 = source.copy().subtract(planeC);
        p4 = target.copy().subtract(planeC);
        buffer.pos(p1.x, p1.y, p1.z).tex(0.25, anim).endVertex();
        buffer.pos(p2.x, p2.y, p2.z).tex(0.25, dist + anim).endVertex();
        buffer.pos(p4.x, p4.y, p4.z).tex(0.75, dist + anim).endVertex();
        buffer.pos(p3.x, p3.y, p3.z).tex(0.75, anim).endVertex();

        p1 = source.copy().add(planeD);
        p2 = target.copy().add(planeD);
        p3 = source.copy().subtract(planeD);
        p4 = target.copy().subtract(planeD);
        buffer.pos(p1.x, p1.y, p1.z).tex(0.25, anim).endVertex();
        buffer.pos(p2.x, p2.y, p2.z).tex(0.25, dist + anim).endVertex();
        buffer.pos(p4.x, p4.y, p4.z).tex(0.75, dist + anim).endVertex();
        buffer.pos(p3.x, p3.y, p3.z).tex(0.75, anim).endVertex();
//        offsetVec = source.copy().add(planeA);
//        vertexbuffer.pos(offsetVec.x, offsetVec.y, offsetVec.z).color(255, 0, 0, 255).endVertex();
//        offsetVec = target.copy().add(planeA);
//        vertexbuffer.pos(offsetVec.x, offsetVec.y, offsetVec.z).color(255, 0, 0, 255).endVertex();
//
//        offsetVec = source.copy().subtract(planeA);
//        vertexbuffer.pos(offsetVec.x, offsetVec.y, offsetVec.z).color(0, 255, 0, 255).endVertex();
//        offsetVec = target.copy().subtract(planeA);
//        vertexbuffer.pos(offsetVec.x, offsetVec.y, offsetVec.z).color(0, 255, 0, 255).endVertex();
//
//        offsetVec = source.copy().add(planeB);
//        vertexbuffer.pos(offsetVec.x, offsetVec.y, offsetVec.z).color(0, 0, 255, 255).endVertex();
//        offsetVec = target.copy().add(planeB);
//        vertexbuffer.pos(offsetVec.x, offsetVec.y, offsetVec.z).color(0, 0, 255, 255).endVertex();
//
//        offsetVec = source.copy().subtract(planeB);
//        vertexbuffer.pos(offsetVec.x, offsetVec.y, offsetVec.z).color(255, 255, 255, 255).endVertex();
//        offsetVec = target.copy().subtract(planeB);
//        vertexbuffer.pos(offsetVec.x, offsetVec.y, offsetVec.z).color(255, 255, 255, 255).endVertex();


    }

    public static final IGLFXHandler FX_HANDLER = new IGLFXHandler() {
        @Override
        public void preDraw(int layer, VertexBuffer vertexbuffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManagerHelper.pushState();
            GlStateManager.depthMask(false);
            GlStateManager.glTexParameterf(3553, 10242, 10497.0F);
            GlStateManager.glTexParameterf(3553, 10243, 10497.0F);
            GlStateManager.disableCull();
            GlStateManager.alphaFunc(GL11.GL_GREATER, 0F);
//            GlStateManager.disableBlend();//
//            GlStateManager.disableAlpha();//
            ResourceHelperDE.bindTexture(DETextures.ENERGY_BEAM_WYVERN);
//            GlStateManager.disableTexture2D();
//            GlStateManager.disableDepth();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);


            vertexbuffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        }

        @Override
        public void postDraw(int layer, VertexBuffer vertexbuffer, Tessellator tessellator) {
            tessellator.draw();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            GlStateManagerHelper.popState();
//            GlStateManager.enableTexture2D();
        }
    };
}