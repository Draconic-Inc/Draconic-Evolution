package com.brandon3055.draconicevolution.client.render.entity.projectile;

import codechicken.lib.render.buffer.TransformingVertexBuilder;
import codechicken.lib.vec.Matrix4;
import codechicken.lib.vec.Vector3;
import com.brandon3055.brandonscore.api.TimeKeeper;
import com.brandon3055.draconicevolution.entity.projectile.DraconicProjectileEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix3f;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Random;

@OnlyIn(Dist.CLIENT)
public class DraconicArrowRenderer extends EntityRenderer<DraconicProjectileEntity> {
    public static final ResourceLocation RES_ARROW = new ResourceLocation("textures/entity/projectiles/arrow.png");
    public static final ResourceLocation RES_TIPPED_ARROW = new ResourceLocation("textures/entity/projectiles/tipped_arrow.png");

    public DraconicArrowRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn);
    }

    public void render(DraconicProjectileEntity arrowEntity, float entityYaw, float partialTicks, MatrixStack mStack, IRenderTypeBuffer getter, int packedLightIn) {
        mStack.push();
        mStack.rotate(Vector3f.YP.rotationDegrees(MathHelper.lerp(partialTicks, arrowEntity.prevRotationYaw, arrowEntity.rotationYaw) - 90.0F));
        mStack.rotate(Vector3f.ZP.rotationDegrees(MathHelper.lerp(partialTicks, arrowEntity.prevRotationPitch, arrowEntity.rotationPitch)));
        float f9 = (float) arrowEntity.arrowShake - partialTicks;
        if (f9 > 0.0F) {
            float f10 = -MathHelper.sin(f9 * 3.0F) * f9;
            mStack.rotate(Vector3f.ZP.rotationDegrees(f10));
        }

        mStack.rotate(Vector3f.XP.rotationDegrees(45.0F));
        mStack.scale(0.05625F, 0.05625F, 0.05625F);
        mStack.translate(-4.0D, 0.0D, 0.0D);
        IVertexBuilder ivertexbuilder = getter.getBuffer(RenderType.getEntityCutout(this.getEntityTexture(arrowEntity)));
        MatrixStack.Entry matrixstack$entry = mStack.getLast();
        Matrix4f matrix4f = matrixstack$entry.getMatrix();
        Matrix3f matrix3f = matrixstack$entry.getNormal();
        this.drawVertex(matrix4f, matrix3f, ivertexbuilder, -7, -2, -2, 0.0F, 0.15625F, -1, 0, 0, packedLightIn);
        this.drawVertex(matrix4f, matrix3f, ivertexbuilder, -7, -2, 2, 0.15625F, 0.15625F, -1, 0, 0, packedLightIn);
        this.drawVertex(matrix4f, matrix3f, ivertexbuilder, -7, 2, 2, 0.15625F, 0.3125F, -1, 0, 0, packedLightIn);
        this.drawVertex(matrix4f, matrix3f, ivertexbuilder, -7, 2, -2, 0.0F, 0.3125F, -1, 0, 0, packedLightIn);
        this.drawVertex(matrix4f, matrix3f, ivertexbuilder, -7, 2, -2, 0.0F, 0.15625F, 1, 0, 0, packedLightIn);
        this.drawVertex(matrix4f, matrix3f, ivertexbuilder, -7, 2, 2, 0.15625F, 0.15625F, 1, 0, 0, packedLightIn);
        this.drawVertex(matrix4f, matrix3f, ivertexbuilder, -7, -2, 2, 0.15625F, 0.3125F, 1, 0, 0, packedLightIn);
        this.drawVertex(matrix4f, matrix3f, ivertexbuilder, -7, -2, -2, 0.0F, 0.3125F, 1, 0, 0, packedLightIn);

        for (int j = 0; j < 4; ++j) {
            mStack.rotate(Vector3f.XP.rotationDegrees(90.0F));
            this.drawVertex(matrix4f, matrix3f, ivertexbuilder, -8, -2, 0, 0.0F, 0.0F, 0, 1, 0, packedLightIn);
            this.drawVertex(matrix4f, matrix3f, ivertexbuilder, 8, -2, 0, 0.5F, 0.0F, 0, 1, 0, packedLightIn);
            this.drawVertex(matrix4f, matrix3f, ivertexbuilder, 8, 2, 0, 0.5F, 0.15625F, 0, 1, 0, packedLightIn);
            this.drawVertex(matrix4f, matrix3f, ivertexbuilder, -8, 2, 0, 0.0F, 0.15625F, 0, 1, 0, packedLightIn);
        }

        mStack.pop();

//        mStack.rotate(new Quaternion(new Vector3f(0, 0, 1), (TimeKeeper.getClientTick() + partialTicks) * 100F, true));
        super.render(arrowEntity, entityYaw, partialTicks, mStack, getter, packedLightIn);

        Vector3 startPos = new Vector3(0, 0, 0); //Bottom
        Vector3 endPos = new Vector3(0, 1, 0); //Top
        int segCount = 8;
        long randSeed = (TimeKeeper.getClientTick() / 2);
        float scaleMod = 2;
        float deflectMod = 1;
        boolean autoScale = true;
        float segTaper = 0.125F;
        int colour = 0x6300BD;

        rendeArcP2P(mStack, getter, startPos ,endPos ,segCount ,randSeed ,scaleMod ,deflectMod ,autoScale ,segTaper ,colour);
    }

    public void drawVertex(Matrix4f matrix, Matrix3f normals, IVertexBuilder vertexBuilder, int offsetX, int offsetY, int offsetZ, float textureX, float textureY, int p_229039_9_, int p_229039_10_, int p_229039_11_, int packedLightIn) {
        vertexBuilder.pos(matrix, (float) offsetX, (float) offsetY, (float) offsetZ).color(255, 255, 255, 255).tex(textureX, textureY).overlay(OverlayTexture.NO_OVERLAY).lightmap(packedLightIn).normal(normals, (float) p_229039_9_, (float) p_229039_11_, (float) p_229039_10_).endVertex();
    }

    public ResourceLocation getEntityTexture(DraconicProjectileEntity entity) {
        return entity.getColor() > 0 ? RES_TIPPED_ARROW : RES_ARROW;
    }

    public static void renderEnergyBolt(Vector3 startPos, Vector3 endPos, Matrix4 mat, IRenderTypeBuffer getter, float partialTicks) {
        IVertexBuilder builder = new TransformingVertexBuilder(getter.getBuffer(RenderType.getLightning()), mat);
    }


    /**
     * Renders an electrical arc based on vanilla lightning between two points.
     * For best results this effect should be rendered in a vertical orientation.
     * Meaning the end point should ideally be directly above or directly bellow the start point.
     * Keep in mind you are free to apply any operations you want to the supplied matrix stack.
     * Any rotation or translation operations will be relative to the startPos.
     *
     * @param mStack The matrix stack that will be applied when rendering the effect.
     * @param getter an IRenderTypeBuffer.
     * @param startPos The start position for rendering the arc (The bottom in terms of Y).
     * @param endPos The end position for rendering the arc (The top in terms of Y).
     * @param segCount The number of arc segments. (Default 8)
     * @param randSeed The random seed for the arc.
     * @param scaleMod Modifier that applies to the diameter of the arc. (Default 1)
     * @param deflectMod Modifies the segment offsets (Makes it more zigzaggy)(Default 1)
     * @param autoScale If true automatically adjusts the overall scale based on the length of the arc.
     * @param segTaper Allows you to apply a positive or negative taper to each arc segment. (Default 0)
     * @param colour The colour of the arc.
     */
    public static void rendeArcP2P(MatrixStack mStack, IRenderTypeBuffer getter, Vector3 startPos, Vector3 endPos, int segCount, long randSeed, float scaleMod, float deflectMod, boolean autoScale, float segTaper, int colour) {
//        Vector3 startPos = new Vector3(0, 0, 0); //Bottom
//        Vector3 endPos = new Vector3(0, 4, 0); //Top
//        int segCount = 8;
//        long randSeed = 0;//(TimeKeeper.getClientTick() / 2) % 5;
//        float scaleMod = 1;
//        float deflectMod = 1;
//        boolean autoScale = true;
//        float segTaper = 0.125F;
//        int colour = 0x6300BD;

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

        IVertexBuilder builder = getter.getBuffer(RenderType.getLightning());
        Matrix4f matrix4f = mStack.getLast().getMatrix();

        for (int layer = 0; layer < 4; ++layer) {
            float red = ((colour >> 16) & 0xFF) / 255F;
            float green = ((colour >> 8) & 0xFF) / 255F;
            float blue = (colour & 0xFF) / 255F;
            float alpha = 0.3F;
            if (layer == 0) {
                red = green = blue = alpha = 1;
            }

            for (int seg = 0; seg < segCount; seg++) {
                float pos = seg / (float)(segCount);
                float x = segXOffset[seg] - (xOffSum * pos);
                float z = segZOffset[seg] - (zOffSum * pos);

                float nextPos = (seg + 1) / (float)(segCount);
                float nextX = segXOffset[seg+1] - (xOffSum * nextPos);
                float nextZ = segZOffset[seg+1] - (zOffSum * nextPos);

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

    private static void addSegmentQuad(Matrix4f matrix4f, IVertexBuilder builder, float x1, float yOffset, float z1, int segIndex, float x2, float z2, float red, float green, float blue, float alpha, float offsetA, float offsetB, boolean invA, boolean invB, boolean invC, boolean invD, float segHeight) {
        builder.pos(matrix4f, x1 + (invA ? offsetB : -offsetB), yOffset + segIndex * segHeight, z1 + (invB ? offsetB : -offsetB)).color(red, green, blue, alpha).endVertex();
        builder.pos(matrix4f, x2 + (invA ? offsetA : -offsetA), yOffset + (segIndex + 1F) * segHeight, z2 + (invB ? offsetA : -offsetA)).color(red, green, blue, alpha).endVertex();
        builder.pos(matrix4f, x2 + (invC ? offsetA : -offsetA), yOffset + (segIndex + 1F) * segHeight, z2 + (invD ? offsetA : -offsetA)).color(red, green, blue, alpha).endVertex();
        builder.pos(matrix4f, x1 + (invC ? offsetB : -offsetB), yOffset + segIndex * segHeight, z1 + (invD ? offsetB : -offsetB)).color(red, green, blue, alpha).endVertex();
    }

}
