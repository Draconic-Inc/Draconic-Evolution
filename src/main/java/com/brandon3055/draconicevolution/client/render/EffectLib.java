package com.brandon3055.draconicevolution.client.render;

import codechicken.lib.util.ArrayUtils;
import codechicken.lib.vec.Quat;
import codechicken.lib.vec.Rotation;
import codechicken.lib.vec.Vector3;
import com.brandon3055.brandonscore.utils.MathUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Mth;

import java.util.Random;

/**
 * Created by brandon3055 on 12/7/21
 * TODO start improving some of my fancy effects to improve general usability and then move them here.
 */
public class EffectLib {

    private static final Vector3[] vectors = ArrayUtils.fill(new Vector3[8], new Vector3());

    /**
     * Renders an electrical arc based on vanilla lightning between two points.
     * For best results this effect should be rendered in a vertical orientation.
     * Meaning the end point should ideally be directly above or directly bellow the start point.
     * Keep in mind you are free to apply any operations you want to the supplied matrix stack.
     * Any rotation or translation operations will be relative to the startPos.
     *
     * @param mStack     The matrix stack that will be applied when rendering the effect.
     * @param getter     an IRenderTypeBuffer.
     * @param startPos   The start position for rendering the arc (The bottom in terms of Y).
     * @param endPos     The end position for rendering the arc (The top in terms of Y).
     * @param segCount   The number of arc segments. (Default 8)
     * @param randSeed   The random seed for the arc.
     * @param scaleMod   Modifier that applies to the diameter of the arc. (Default 1)
     * @param deflectMod Modifies the segment offsets (Makes it more zigzaggy)(Default 1)
     * @param autoScale  If true automatically adjusts the overall scale based on the length of the arc.
     * @param segTaper   Allows you to apply a positive or negative taper to each arc segment. (Default 0)
     * @param colour     The colour of the arc.
     */
    public static void renderLightningP2P(PoseStack mStack, MultiBufferSource getter, Vector3 startPos, Vector3 endPos, int segCount, long randSeed, float scaleMod, float deflectMod, boolean autoScale, float segTaper, int colour) {
        double height = endPos.y - startPos.y;
        float relScale = autoScale ? (float) height / 128F : 1F; //A scale value calculated by comparing the bolt height to that of vanilla lightning
        float segHeight = (float) height / segCount;
        float[] segXOffset = new float[segCount + 1];
        float[] segZOffset = new float[segCount + 1];
        float xOffSum = 0;
        float zOffSum = 0;

        Random random = new Random(randSeed);
        for (int segment = 0; segment < segCount + 1; segment++) {
            segXOffset[segment] = xOffSum + (float) startPos.x;
            segZOffset[segment] = zOffSum + (float) startPos.z;
            //Figure out what the total offset will be so we can subtract it at the start in order to end up in the correct spot at the end.
            if (segment < segCount) {
                xOffSum += (5 - (random.nextFloat() * 10)) * relScale * deflectMod;
                zOffSum += (5 - (random.nextFloat() * 10)) * relScale * deflectMod;
            }
        }

        xOffSum -= (float) (endPos.x - startPos.x);
        zOffSum -= (float) (endPos.z - startPos.z);

        VertexConsumer builder = getter.getBuffer(RenderType.lightning());
        Matrix4f matrix4f = mStack.last().pose();

        for (int layer = 0; layer < 4; ++layer) {
            float red = ((colour >> 16) & 0xFF) / 255F;
            float green = ((colour >> 8) & 0xFF) / 255F;
            float blue = (colour & 0xFF) / 255F;
            float alpha = 0.3F;
            if (layer == 0) {
                red = green = blue = alpha = 1;
            }

            for (int seg = 0; seg < segCount; seg++) {
                float pos = seg / (float) (segCount);
                float x = segXOffset[seg] - (xOffSum * pos);
                float z = segZOffset[seg] - (zOffSum * pos);

                float nextPos = (seg + 1) / (float) (segCount);
                float nextX = segXOffset[seg + 1] - (xOffSum * nextPos);
                float nextZ = segZOffset[seg + 1] - (zOffSum * nextPos);

                //The size of each shell
                float layerOffsetA = (0.1F + (layer * 0.2F * (1F + segTaper))) * relScale * scaleMod;
                float layerOffsetB = (0.1F + (layer * 0.2F * (1F - segTaper))) * relScale * scaleMod;

                addSegmentQuad(matrix4f, builder, x, (float) startPos.y, z, seg, nextX, nextZ, red, green, blue, alpha, layerOffsetA, layerOffsetB, false, false, true, false, segHeight);    //North Side
                addSegmentQuad(matrix4f, builder, x, (float) startPos.y, z, seg, nextX, nextZ, red, green, blue, alpha, layerOffsetA, layerOffsetB, true, false, true, true, segHeight);      //East Side
                addSegmentQuad(matrix4f, builder, x, (float) startPos.y, z, seg, nextX, nextZ, red, green, blue, alpha, layerOffsetA, layerOffsetB, true, true, false, true, segHeight);      //South Side
                addSegmentQuad(matrix4f, builder, x, (float) startPos.y, z, seg, nextX, nextZ, red, green, blue, alpha, layerOffsetA, layerOffsetB, false, true, false, false, segHeight);    //West Side
            }
        }
    }

    /**
     * This is the same as {@link #renderLightningP2P(PoseStack, MultiBufferSource, Vector3, Vector3, int, long, float, float, boolean, float, int)}
     * Except that it automatically applies the correct transformations in order to render the bolt in an ideal orientation.
     * This means you are free to use this between any two arbitrary points and the bolt will render correctly.
     * But this does come at the cost of increased overhead.
     *
     * @see #renderLightningP2P(PoseStack, MultiBufferSource, Vector3, Vector3, int, long, float, float, boolean, float, int)
     */
    public static void renderLightningP2PRotate(PoseStack mStack, MultiBufferSource getter, Vector3 startPos, Vector3 endPos, int segCount, long randSeed, float scaleMod, float deflectMod, boolean autoScale, float segTaper, int colour) {
        mStack.pushPose();
        double length = MathUtils.distance(startPos, endPos);
        Vector3 virtualEndPos = startPos.copy().add(0, length, 0);
        Vector3 dirVec = endPos.copy();
        dirVec.subtract(startPos);
        dirVec.normalize();
        double dirVecXZDist = Math.sqrt(dirVec.x * dirVec.x + dirVec.z * dirVec.z);
        float yRot = (float) (Mth.atan2(dirVec.x, dirVec.z) * (double) (180F / (float) Math.PI));
        float xRot = (float) (Mth.atan2(dirVec.y, dirVecXZDist) * (double) (180F / (float) Math.PI));
        mStack.translate(startPos.x, startPos.y, startPos.z);
        mStack.mulPose(Vector3f.YP.rotationDegrees(yRot - 90));
        mStack.mulPose(Vector3f.ZP.rotationDegrees(xRot - 90));
        mStack.translate(-startPos.x, -startPos.y, -startPos.z);
        renderLightningP2P(mStack, getter, startPos, virtualEndPos, segCount, randSeed, scaleMod, deflectMod, autoScale, segTaper, colour);
        mStack.popPose();
    }

    private static void addSegmentQuad(Matrix4f matrix4f, VertexConsumer builder, float x1, float yOffset, float z1, int segIndex, float x2, float z2, float red, float green, float blue, float alpha, float offsetA, float offsetB, boolean invA, boolean invB, boolean invC, boolean invD, float segHeight) {
        builder.vertex(matrix4f, x1 + (invA ? offsetB : -offsetB), yOffset + segIndex * segHeight, z1 + (invB ? offsetB : -offsetB)).color(red, green, blue, alpha).endVertex();
        builder.vertex(matrix4f, x2 + (invA ? offsetA : -offsetA), yOffset + (segIndex + 1F) * segHeight, z2 + (invB ? offsetA : -offsetA)).color(red, green, blue, alpha).endVertex();
        builder.vertex(matrix4f, x2 + (invC ? offsetA : -offsetA), yOffset + (segIndex + 1F) * segHeight, z2 + (invD ? offsetA : -offsetA)).color(red, green, blue, alpha).endVertex();
        builder.vertex(matrix4f, x1 + (invC ? offsetB : -offsetB), yOffset + segIndex * segHeight, z1 + (invD ? offsetB : -offsetB)).color(red, green, blue, alpha).endVertex();
    }

    public static void drawParticle(Camera renderInfo, VertexConsumer builder, TextureAtlasSprite sprite, float x, float y, float z, float scale, int light) {
        Rotation rot = new Rotation(new Quat(renderInfo.rotation()));
        vectors[0].set(-1.0F, -1.0F, 0.0F).apply(rot).multiply(scale).add(x, y, z);
        vectors[1].set(-1.0F, 1.0F, 0.0F).apply(rot).multiply(scale).add(x, y, z);
        vectors[2].set(1.0F, 1.0F, 0.0F).apply(rot).multiply(scale).add(x, y, z);
        vectors[3].set(1.0F, -1.0F, 0.0F).apply(rot).multiply(scale).add(x, y, z);

        float uMin = sprite.getU0();
        float uMax = sprite.getU1();
        float vMin = sprite.getV0();
        float vMax = sprite.getV1();
        builder.vertex(vectors[0].x, vectors[0].y, vectors[0].z).color(1F, 1F, 1F, 1F).uv(uMax, vMax).uv2(light).endVertex();
        builder.vertex(vectors[1].x, vectors[1].y, vectors[1].z).color(1F, 1F, 1F, 1F).uv(uMax, vMin).uv2(light).endVertex();
        builder.vertex(vectors[2].x, vectors[2].y, vectors[2].z).color(1F, 1F, 1F, 1F).uv(uMin, vMin).uv2(light).endVertex();
        builder.vertex(vectors[3].x, vectors[3].y, vectors[3].z).color(1F, 1F, 1F, 1F).uv(uMin, vMax).uv2(light).endVertex();
    }

    public static void drawParticle(Rotation rotation, VertexConsumer builder, TextureAtlasSprite sprite, float r, float g, float b, double x, double y, double z, float scale, int light) {
        vectors[0].set(-1.0F, -1.0F, 0.0F).apply(rotation).multiply(scale).add(x, y, z);
        vectors[1].set(-1.0F, 1.0F, 0.0F).apply(rotation).multiply(scale).add(x, y, z);
        vectors[2].set(1.0F, 1.0F, 0.0F).apply(rotation).multiply(scale).add(x, y, z);
        vectors[3].set(1.0F, -1.0F, 0.0F).apply(rotation).multiply(scale).add(x, y, z);

        float uMin = sprite.getU0();
        float uMax = sprite.getU1();
        float vMin = sprite.getV0();
        float vMax = sprite.getV1();
        builder.vertex(vectors[0].x, vectors[0].y, vectors[0].z).color(r, g, b, 1F).uv(uMax, vMax).uv2(light).endVertex();
        builder.vertex(vectors[1].x, vectors[1].y, vectors[1].z).color(r, g, b, 1F).uv(uMax, vMin).uv2(light).endVertex();
        builder.vertex(vectors[2].x, vectors[2].y, vectors[2].z).color(r, g, b, 1F).uv(uMin, vMin).uv2(light).endVertex();
        builder.vertex(vectors[3].x, vectors[3].y, vectors[3].z).color(r, g, b, 1F).uv(uMin, vMax).uv2(light).endVertex();
    }
}
