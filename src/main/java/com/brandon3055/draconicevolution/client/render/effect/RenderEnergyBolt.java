package com.brandon3055.draconicevolution.client.render.effect;

import com.brandon3055.brandonscore.lib.Vec3D;
import com.brandon3055.brandonscore.utils.Utils;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;

import java.util.Random;

/**
 * Created by brandon3055 on 22/06/2016.
 */
public class RenderEnergyBolt {


    @Deprecated
    public static void renderBoltBetween(Vec3D point1, Vec3D point2, double scale, double maxDeflection, int maxSegments, long boltSeed, boolean corona) {
        Tesselator tessellator = Tesselator.getInstance();
        Random random = new Random(boltSeed);

        RenderSystem.disableTexture();
//        RenderSystem.disableLighting();
        RenderSystem.enableBlend();
//        GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, 200, 200);
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);

        double distance = Utils.getDistance(point1, point2);
        Vec3D dirVec = Vec3D.getDirectionVec(point1, point2);
        Vec3D invDir = new Vec3D(1D, 1D, 1D).subtract(dirVec);

        //region Draw Main Bolt

        Vec3D[] vectors = new Vec3D[maxSegments / 2 + random.nextInt(maxSegments / 2)];
        vectors[0] = point1;
        vectors[vectors.length - 1] = point2;

        for (int i = 1; i < vectors.length - 1; i++) {
            double pos = (i / (double) vectors.length) * distance;

            Vec3D point = point1.copy();
            point.add(dirVec.copy().multiply(pos, pos, pos));

            double randX = (-0.5 + random.nextDouble()) * maxDeflection * invDir.x;
            double randY = (-0.5 + random.nextDouble()) * maxDeflection * invDir.y;
            double randZ = (-0.5 + random.nextDouble()) * maxDeflection * invDir.z;

            point.add(randX, randY, randZ);

            vectors[i] = point;
        }

        double rScale = scale * (0.5 + (random.nextDouble() * 0.5));
        for (int i = 1; i < vectors.length; i++) {
            drawBoltSegment(tessellator, vectors[i - 1], vectors[i], (float) rScale);
        }

        //endregion

        //region Draw Corona

        if (corona) {

            Vec3D[][] coronaVecs = new Vec3D[2 + random.nextInt(4)][2 + random.nextInt(3)];

            for (int i = 0; i < coronaVecs.length; i++) {
                coronaVecs[i][0] = point1;
                double d = distance / (2 + (random.nextDouble() * 2));

                for (int v = 1; v < coronaVecs[i].length; v++) {
                    double pos = (v / (double) coronaVecs[i].length) * d;

                    Vec3D point = point1.copy();
                    point.add(dirVec.copy().multiply(pos, pos, pos));

                    double randX = (-0.5 + random.nextDouble()) * maxDeflection * invDir.x * 0.5;
                    double randY = (-0.5 + random.nextDouble()) * maxDeflection * invDir.y * 0.5;
                    double randZ = (-0.5 + random.nextDouble()) * maxDeflection * invDir.z * 0.5;

                    point.add(randX, randY, randZ);

                    coronaVecs[i][v] = point;
                }
            }

            for (int i = 0; i < coronaVecs.length; i++) {
                float f = 0.1F + (random.nextFloat() * 0.5F);
                for (int v = 1; v < coronaVecs[i].length; v++) {
                    drawBoltSegment(tessellator, coronaVecs[i][v - 1], coronaVecs[i][v], (float) scale * f);
                }
            }
        }

        //endregion

        RenderSystem.disableBlend();
//        RenderSystem.enableLighting();
        RenderSystem.enableTexture();
    }

    //WIP
    @Deprecated
    public static void renderCorona(Vec3D source, Vec3D target, double scale, double maxDeflection, int maxSegments, long boltSeed) {
        Tesselator tessellator = Tesselator.getInstance();
        Random random = new Random(boltSeed);

        RenderSystem.disableTexture();
//        RenderSystem.disableLighting();
        RenderSystem.enableBlend();
//        GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, 200, 200);
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);

        double distance = Utils.getDistance(source, target);
        Vec3D dirVec = Vec3D.getDirectionVec(source, target);
        Vec3D invDir = new Vec3D(1D, 1D, 1D).subtract(dirVec);

        //region Draw Corona


        Vec3D[][] coronaVecs = new Vec3D[2 + random.nextInt(maxSegments * 2)][2 + random.nextInt(2)];

        for (int i = 0; i < coronaVecs.length; i++) {
            coronaVecs[i][0] = source;
            Vec3D newDir = invDir.copy();
            newDir.multiply(0.9 + (random.nextDouble() * 0.5), 0.9 + (random.nextDouble() * 0.5), 0.9 + (random.nextDouble() * 0.5));

            for (int v = 1; v < coronaVecs[i].length; v++) {
                double pos = (v / (double) coronaVecs[i].length) * distance;

                Vec3D point = source.copy();
                point.add(dirVec.copy().multiply(pos, pos, pos));

                double randX = (-0.5 + random.nextDouble()) * maxDeflection * newDir.x;
                double randY = (-0.5 + random.nextDouble()) * maxDeflection * newDir.y;
                double randZ = (-0.5 + random.nextDouble()) * maxDeflection * newDir.z;

                point.add(randX, randY, randZ);

                coronaVecs[i][v] = point;
            }
        }

        for (int i = 0; i < coronaVecs.length; i++) {
            float f = 0.1F + (random.nextFloat() * 0.5F);
            for (int v = 1; v < coronaVecs[i].length; v++) {
                drawBoltSegment(tessellator, coronaVecs[i][v - 1], coronaVecs[i][v], (float) scale * f);
            }
        }


        //endregion

        RenderSystem.disableBlend();
//        RenderSystem.enableLighting();
        RenderSystem.enableTexture();
    }


    @Deprecated
    private static void drawBoltSegment(Tesselator tessellator, Vec3D p1, Vec3D p2, float scale) {
//        BufferBuilder buffer = tessellator.getBuilder();
//
//        RenderSystem.pushMatrix();
//        RenderSystem.translated(p1.x, p1.y, p1.z);
//
//        double dist = Utils.getDistanceAtoB(p1, p2);
//        float xd = (float) (p1.x - p2.x);
//        float yd = (float) (p1.y - p2.y);
//        float zd = (float) (p1.z - p2.z);
//        double var7 = (double) Mth.sqrt((double) (xd * xd + zd * zd));
//        float rotYaw = (float) (Math.atan2((double) xd, (double) zd) * 180.0D / 3.141592653589793D);
//        float rotPitch = (float) (Math.atan2((double) yd, var7) * 180.0D / 3.141592653589793D);
//
//        RenderSystem.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
//        RenderSystem.rotatef(180.0F + rotYaw, 0.0F, 0.0F, -1.0F);
//        RenderSystem.rotatef(rotPitch, 1.0F, 0.0F, 0.0F);
//
//        buffer.begin(GL11.GL_TRIANGLE_STRIP, DefaultVertexFormat.POSITION_COLOR);
//        for (int i = 0; i <= 9; i++) {
//            float f = (i + 1F) / 9F;
//            float verX = Mth.sin((float) (i % 3) * (float) Math.PI * 2F / (float) 3) * f * scale;
//            float verZ = Mth.cos((float) (i % 3) * (float) Math.PI * 2F / (float) 3) * f * scale;
//
//            buffer.vertex(verX, dist, verZ).color(0.35F, 0.65F, 0.9F, 0.3F).endVertex();
//            buffer.vertex(verX, 0, verZ).color(0.35F, 0.65F, 0.9F, 0.3F).endVertex();
//        }
//        tessellator.end();
//
//        RenderSystem.popMatrix();
    }









    @Deprecated //Currently just for reference
    public static void renderVanillaLightning(PoseStack mStack, MultiBufferSource getter) {
        //This is basically used to pre calculate the segment random positions. The next position is then re calculated using the same random seed.
        float[] segXOffset = new float[8];
        float[] segZOffset = new float[8];
        float cumulativeX = 0.0F;
        float cumulativeZ = 0.0F;

        long boltRand = 0;//TimeKeeper.getClientTick() / 20;

        Random random = new Random(boltRand);

        for(int segment = 7; segment >= 0; --segment) {
            segXOffset[segment] = cumulativeX;
            segZOffset[segment] = cumulativeZ;
            cumulativeX += (float)(random.nextInt(11) - 5);
            cumulativeZ += (float)(random.nextInt(11) - 5);
        }

        VertexConsumer builder = getter.getBuffer(RenderType.lightning());
        Matrix4f matrix4f = mStack.last().pose();

        //Render each bolt layer
        for(int layer = 0; layer < 4; ++layer) {
            Random random1 = new Random(boltRand);

            //For each branch starting with the main branch
            for(int branch = 0; branch < 3; ++branch) {
                int startSeg = 7; //Top of the bolt
                int endSeg = 0;
                if (branch > 0) {
                    startSeg = 7 - branch;
                }

                if (branch > 0) {
                    endSeg = startSeg - 2;
                }

                float x = segXOffset[startSeg] - cumulativeX;
                float z = segZOffset[startSeg] - cumulativeZ;
                //For each segment in the branch
                for(int seg = startSeg; seg >= endSeg; --seg) {
                    float prevX = x;
                    float prevZ = z;
                    if (branch == 0) { //Main Trunk Offsets
                        x += (float)(random1.nextInt(11) - 5); //These randoms need to match the segXOffset randoms
                        z += (float)(random1.nextInt(11) - 5);
                    } else { //Branch Offsets
                        x += (float)(random1.nextInt(31) - 15);
                        z += (float)(random1.nextInt(31) - 15);
                    }

                    float f6 = 0.5F; //?
                    float red = 0.45F;
                    float green = 0.45F;
                    float blue = 0.5F;
                    //The size of each shell
                    float layerOffsetA = 0.1F + (layer * 0.20F);
                    if (branch == 0) {
                        layerOffsetA *= 1.0F + (seg * 0.1F); //Adds a slight taper to the main branch (Thicker at the top)
                    }

                    float layerOffsetB = 0.1F + (layer * 0.2F);
                    if (branch == 0) {
                        layerOffsetB *= 1.0F + ((seg-1F) * 0.1F); //Adds a slight taper to the main branch  (Thicker at the top)
                    }

                    addVanillaSegmentQuad(matrix4f, builder, x, z, seg, prevX, prevZ, red, green, blue, layerOffsetA, layerOffsetB, false, false, true,  false);    //North Side
                    addVanillaSegmentQuad(matrix4f, builder, x, z, seg, prevX, prevZ, red, green, blue, layerOffsetA, layerOffsetB, true,  false, true,  true);      //East Side
                    addVanillaSegmentQuad(matrix4f, builder, x, z, seg, prevX, prevZ, red, green, blue, layerOffsetA, layerOffsetB, true,  true,  false, true);      //South Side
                    addVanillaSegmentQuad(matrix4f, builder, x, z, seg, prevX, prevZ, red, green, blue, layerOffsetA, layerOffsetB, false, true,  false, false);    //West Side
                }
            }
        }
    }

    private static void addVanillaSegmentQuad(Matrix4f matrix4f, VertexConsumer builder, float x1, float z1, int segIndex, float x2, float z2, float red, float green, float blue, float offsetA, float offsetB, boolean invA, boolean invB, boolean invC, boolean invD) {
        float segHeight = 16F;
        builder.vertex(matrix4f, x1 + (invA ? offsetB : -offsetB), segIndex * segHeight,          z1 + (invB ? offsetB : -offsetB)).color(red, green, blue, 0.3F).endVertex();
        builder.vertex(matrix4f, x2 + (invA ? offsetA : -offsetA), (segIndex + 1F) * segHeight,   z2 + (invB ? offsetA : -offsetA)).color(red, green, blue, 0.3F).endVertex();
        builder.vertex(matrix4f, x2 + (invC ? offsetA : -offsetA), (segIndex + 1F) * segHeight,   z2 + (invD ? offsetA : -offsetA)).color(red, green, blue, 0.3F).endVertex();
        builder.vertex(matrix4f, x1 + (invC ? offsetB : -offsetB), segIndex * segHeight,          z1 + (invD ? offsetB : -offsetB)).color(red, green, blue, 0.3F).endVertex();
    }
}
