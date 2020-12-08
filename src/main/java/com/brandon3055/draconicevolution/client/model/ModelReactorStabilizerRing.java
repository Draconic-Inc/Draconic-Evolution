package com.brandon3055.draconicevolution.client.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Quaternion;

import java.util.function.Function;

/**
 * EnergyStabilizer - brandon3055
 * Created using Tabula 5.0.0
 */
public class ModelReactorStabilizerRing extends Model {
    public ModelRenderer ringElement1;
    public ModelRenderer ringElement2;
    public ModelRenderer ringElement3;
    public ModelRenderer ringElement4;
    public ModelRenderer ringElement5;
    public ModelRenderer ringElement6;
    public ModelRenderer ringElement7;
    public ModelRenderer ringElement8;
    public ModelRenderer ringElement9;
    public ModelRenderer ringElement10;
    public ModelRenderer ringElement11;
    public ModelRenderer ringElement12;
    public ModelRenderer hing;
    public ModelRenderer emitter;

    public float embitterRotation;
    public float brightness;

    public ModelReactorStabilizerRing(Function<ResourceLocation, RenderType> renderTypeIn) {
        super(renderTypeIn);
        this.textureWidth = 32;
        this.textureHeight = 16;
        this.ringElement9 = new ModelRenderer(this, 0, 0);
        this.ringElement9.setRotationPoint(6.7F, 0.01F, -1.55F);
        this.ringElement9.addBox(0.0F, 0.0F, 0.0F, 4, 1, 1, 0.0F);
        this.setRotateAngle(ringElement9, 0.0F, 2.0943951023931953F, 0.0F);
        this.ringElement10 = new ModelRenderer(this, 0, 0);
        this.ringElement10.setRotationPoint(1.55F, 0.02F, -6.7F);
        this.ringElement10.addBox(-4.0F, 0.0F, 0.0F, 4, 1, 1, 0.0F);
        this.setRotateAngle(ringElement10, 0.0F, 2.6179938779914944F, 0.0F);
        this.ringElement1 = new ModelRenderer(this, 0, 0);
        this.ringElement1.setRotationPoint(-2.0F, 0.01F, 6.6F);
        this.ringElement1.addBox(0.0F, 0.0F, 0.0F, 4, 1, 1, 0.0F);
        this.ringElement7 = new ModelRenderer(this, 0, 0);
        this.ringElement7.setRotationPoint(-1.55F, 0.02F, -6.7F);
        this.ringElement7.addBox(0.0F, 0.0F, 0.0F, 4, 1, 1, 0.0F);
        this.setRotateAngle(ringElement7, 0.0F, -2.6179938779914944F, 0.0F);
        this.ringElement8 = new ModelRenderer(this, 0, 0);
        this.ringElement8.setRotationPoint(-6.7F, 0.01F, -1.55F);
        this.ringElement8.addBox(-4.0F, 0.0F, 0.0F, 4, 1, 1, 0.0F);
        this.setRotateAngle(ringElement8, 0.0F, -2.0943951023931953F, 0.0F);
        this.emitter = new ModelRenderer(this, 0, 2);
        this.emitter.setRotationPoint(-7.1F, -0.3F, 2.25F);
        this.emitter.addBox(-0.5F, -0.8F, 0.0F, 3, 1, 9, 0.0F);
        this.setRotateAngle(emitter, 0.0F, 3.141592653589793F, 0.0F);
        this.ringElement2 = new ModelRenderer(this, 0, 0);
        this.ringElement2.setRotationPoint(2.0F, 0.01F, -6.6F);
        this.ringElement2.addBox(0.0F, 0.0F, 0.0F, 4, 1, 1, 0.0F);
        this.setRotateAngle(ringElement2, 0.0F, 3.141592653589793F, 0.0F);
        this.ringElement5 = new ModelRenderer(this, 0, 0);
        this.ringElement5.setRotationPoint(-6.7F, 0.01F, 1.55F);
        this.ringElement5.addBox(0.0F, 0.0F, 0.0F, 4, 1, 1, 0.0F);
        this.setRotateAngle(ringElement5, 0.0F, -1.0471975511965976F, 0.0F);
        this.ringElement12 = new ModelRenderer(this, 0, 0);
        this.ringElement12.setRotationPoint(1.55F, 0.02F, 6.7F);
        this.ringElement12.addBox(0.0F, 0.0F, 0.0F, 4, 1, 1, 0.0F);
        this.setRotateAngle(ringElement12, 0.0F, 0.5235987755982988F, 0.0F);
        this.hing = new ModelRenderer(this, 15, 0);
        this.hing.setRotationPoint(-7.45F, -0.5F, -1.75F);
        this.hing.addBox(0.0F, 0.0F, 0.0F, 1, 1, 7, 0.0F);
        this.ringElement6 = new ModelRenderer(this, 0, 0);
        this.ringElement6.setRotationPoint(-1.55F, 0.02F, 6.7F);
        this.ringElement6.addBox(-4.0F, 0.0F, 0.0F, 4, 1, 1, 0.0F);
        this.setRotateAngle(ringElement6, 0.0F, -0.5235987755982988F, 0.0F);
        this.ringElement11 = new ModelRenderer(this, 0, 0);
        this.ringElement11.setRotationPoint(6.7F, 0.01F, 1.55F);
        this.ringElement11.addBox(-4.0F, 0.0F, 0.0F, 4, 1, 1, 0.0F);
        this.setRotateAngle(ringElement11, 0.0F, 1.0471975511965976F, 0.0F);
        this.ringElement4 = new ModelRenderer(this, 0, 0);
        this.ringElement4.setRotationPoint(-6.6F, 0.0F, -2.0F);
        this.ringElement4.addBox(0.0F, 0.0F, 0.0F, 4, 1, 1, 0.0F);
        this.setRotateAngle(ringElement4, 0.0F, -1.5707963267948966F, 0.0F);
        this.ringElement3 = new ModelRenderer(this, 0, 0);
        this.ringElement3.setRotationPoint(6.6F, 0.0F, 2.0F);
        this.ringElement3.addBox(0.0F, 0.0F, 0.0F, 4, 1, 1, 0.0F);
        this.setRotateAngle(ringElement3, 0.0F, 1.5707963267948966F, 0.0F);
    }


    @Override
    public void render(MatrixStack matrix, IVertexBuilder buffer, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        int light = Math.max((int)(brightness * 240), packedLightIn % 240);

        this.ringElement1.render(matrix, buffer, light, packedOverlayIn);
        this.ringElement2.render(matrix, buffer, light, packedOverlayIn);
        this.ringElement3.render(matrix, buffer, light, packedOverlayIn);
        this.ringElement4.render(matrix, buffer, light, packedOverlayIn);
        this.ringElement5.render(matrix, buffer, light, packedOverlayIn);
        this.ringElement6.render(matrix, buffer, light, packedOverlayIn);
        this.ringElement7.render(matrix, buffer, light, packedOverlayIn);
        this.ringElement8.render(matrix, buffer, light, packedOverlayIn);
        this.ringElement9.render(matrix, buffer, light, packedOverlayIn);
        this.ringElement10.render(matrix, buffer,light, packedOverlayIn);
        this.ringElement11.render(matrix, buffer,light, packedOverlayIn);
        this.ringElement12.render(matrix, buffer,light, packedOverlayIn);

        for (int i = 0; i < 4; i++) {
            matrix.push();
            matrix.translate((this.hing.rotationPointX) * 0.0625, (this.hing.rotateAngleY - 0.25) * 0.0625, (this.hing.rotateAngleZ - 0.875) * 0.0625);
            matrix.scale(0.5F, 0.5F, 0.5F);
            matrix.translate(-this.hing.rotationPointX * 0.0625, -this.hing.rotateAngleY * 0.0625, -this.hing.rotateAngleZ * 0.0625);
            this.hing.render(matrix, buffer, 240, packedOverlayIn);
            matrix.pop();

            matrix.push();
            matrix.translate(0.0625 * .5, -0.0625 * 0.125, 0.0625 * 1.125);
            matrix.translate((this.emitter.rotationPointX - .5) * 0.0625, (this.emitter.rotateAngleY - 3.5) * 0.0625, (this.emitter.rotateAngleZ) * 0.0625);
            matrix.rotate(new Quaternion(0, 0, embitterRotation, true));
            matrix.scale(0.5F, 0.5F, 0.5F);
            matrix.translate(-(this.emitter.rotationPointX - .5) * 0.0625, -(this.emitter.rotateAngleY - 3.5) * 0.0625, -(this.emitter.rotateAngleZ) * 0.0625);
            this.emitter.render(matrix, buffer, light, packedOverlayIn);
            matrix.pop();

            matrix.rotate(new Quaternion(0, 90, 0, true));
        }
    }

    /**
     * This is a helper function from Tabula to set the rotation of model parts
     */
    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
