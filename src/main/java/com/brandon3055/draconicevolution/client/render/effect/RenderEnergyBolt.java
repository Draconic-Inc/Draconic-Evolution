package com.brandon3055.draconicevolution.client.render.effect;

import com.brandon3055.brandonscore.lib.Vec3D;
import com.brandon3055.brandonscore.utils.Utils;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

import java.util.Random;

/**
 * Created by brandon3055 on 22/06/2016.
 */
public class RenderEnergyBolt {


    public static void renderBoltBetween(Vec3D point1, Vec3D point2, double scale, double maxDeflection, int maxSegments, long boltSeed, boolean corona) {
        Tessellator tessellator = Tessellator.getInstance();
        Random random = new Random(boltSeed);

        GlStateManager.disableTexture2D();
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 200, 200);
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);

        double distance = Utils.getDistanceAtoB(point1, point2);
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

        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.enableTexture2D();
    }

    //WIP
    public static void renderCorona(Vec3D source, Vec3D target, double scale, double maxDeflection, int maxSegments, long boltSeed) {
        Tessellator tessellator = Tessellator.getInstance();
        Random random = new Random(boltSeed);

        GlStateManager.disableTexture2D();
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 200, 200);
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);

        double distance = Utils.getDistanceAtoB(source, target);
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

        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.enableTexture2D();
    }


    private static void drawBoltSegment(Tessellator tessellator, Vec3D p1, Vec3D p2, float scale) {
        BufferBuilder buffer = tessellator.getBuffer();

        GlStateManager.pushMatrix();
        GlStateManager.translate(p1.x, p1.y, p1.z);

        double dist = Utils.getDistanceAtoB(p1, p2);
        float xd = (float) (p1.x - p2.x);
        float yd = (float) (p1.y - p2.y);
        float zd = (float) (p1.z - p2.z);
        double var7 = (double) MathHelper.sqrt((double) (xd * xd + zd * zd));
        float rotYaw = (float) (Math.atan2((double) xd, (double) zd) * 180.0D / 3.141592653589793D);
        float rotPitch = (float) (Math.atan2((double) yd, var7) * 180.0D / 3.141592653589793D);

        GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(180.0F + rotYaw, 0.0F, 0.0F, -1.0F);
        GlStateManager.rotate(rotPitch, 1.0F, 0.0F, 0.0F);

        buffer.begin(GL11.GL_TRIANGLE_STRIP, DefaultVertexFormats.POSITION_COLOR);
        for (int i = 0; i <= 9; i++) {
            float f = (i + 1F) / 9F;
            float verX = MathHelper.sin((float) (i % 3) * (float) Math.PI * 2F / (float) 3) * f * scale;
            float verZ = MathHelper.cos((float) (i % 3) * (float) Math.PI * 2F / (float) 3) * f * scale;

            buffer.pos(verX, dist, verZ).color(0.35F, 0.65F, 0.9F, 0.3F).endVertex();
            buffer.pos(verX, 0, verZ).color(0.35F, 0.65F, 0.9F, 0.3F).endVertex();
        }
        tessellator.draw();

        GlStateManager.popMatrix();
    }
}
