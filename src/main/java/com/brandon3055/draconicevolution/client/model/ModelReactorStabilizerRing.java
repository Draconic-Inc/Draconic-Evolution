package com.brandon3055.draconicevolution.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.Entity;

import org.lwjgl.opengl.GL11;

/**
 * EnergyStabilizer - brandon3055 Created using Tabula 5.0.0
 */
public class ModelReactorStabilizerRing extends ModelBase {

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
    public ModelRenderer hing1;
    public ModelRenderer hing2;
    public ModelRenderer hing3;
    public ModelRenderer hing4;
    public ModelRenderer emitter1;
    public ModelRenderer emitter2;
    public ModelRenderer emitter3;
    public ModelRenderer emitter4;

    public ModelReactorStabilizerRing() {
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
        this.hing4 = new ModelRenderer(this, 15, 0);
        this.hing4.setRotationPoint(1.75F, -0.5F, 6.95F);
        this.hing4.addBox(0.0F, 0.0F, 0.0F, 1, 1, 7, 0.0F);
        this.setRotateAngle(hing4, 0.0F, -1.5716689914208934F, 0.0F);
        this.ringElement8 = new ModelRenderer(this, 0, 0);
        this.ringElement8.setRotationPoint(-6.7F, 0.01F, -1.55F);
        this.ringElement8.addBox(-4.0F, 0.0F, 0.0F, 4, 1, 1, 0.0F);
        this.setRotateAngle(ringElement8, 0.0F, -2.0943951023931953F, 0.0F);
        this.emitter4 = new ModelRenderer(this, 0, 2);
        this.emitter4.setRotationPoint(2.25F, -0.3F, 7.1F);
        this.emitter4.addBox(-0.5F, -0.8F, 0.0F, 3, 1, 9, 0.0F);
        this.setRotateAngle(emitter4, 0.0F, -1.5707963267948966F, 0.0F);
        this.emitter2 = new ModelRenderer(this, 0, 2);
        this.emitter2.setRotationPoint(-7.1F, -0.3F, 2.25F);
        this.emitter2.addBox(-0.5F, -0.8F, 0.0F, 3, 1, 9, 0.0F);
        this.setRotateAngle(emitter2, 0.0F, 3.141592653589793F, 0.0F);
        this.emitter1 = new ModelRenderer(this, 0, 2);
        this.emitter1.setRotationPoint(7.1F, -0.3F, -2.25F);
        this.emitter1.addBox(-0.5F, -0.8F, 0.0F, 3, 1, 9, 0.0F);
        this.hing2 = new ModelRenderer(this, 15, 0);
        this.hing2.setRotationPoint(6.95F, -0.5F, -1.75F);
        this.hing2.addBox(0.0F, 0.0F, 0.0F, 1, 1, 7, 0.0F);
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
        this.hing1 = new ModelRenderer(this, 15, 0);
        this.hing1.setRotationPoint(-7.45F, -0.5F, -1.75F);
        this.hing1.addBox(0.0F, 0.0F, 0.0F, 1, 1, 7, 0.0F);
        this.ringElement6 = new ModelRenderer(this, 0, 0);
        this.ringElement6.setRotationPoint(-1.55F, 0.02F, 6.7F);
        this.ringElement6.addBox(-4.0F, 0.0F, 0.0F, 4, 1, 1, 0.0F);
        this.setRotateAngle(ringElement6, 0.0F, -0.5235987755982988F, 0.0F);
        this.ringElement11 = new ModelRenderer(this, 0, 0);
        this.ringElement11.setRotationPoint(6.7F, 0.01F, 1.55F);
        this.ringElement11.addBox(-4.0F, 0.0F, 0.0F, 4, 1, 1, 0.0F);
        this.setRotateAngle(ringElement11, 0.0F, 1.0471975511965976F, 0.0F);
        this.emitter3 = new ModelRenderer(this, 0, 2);
        this.emitter3.setRotationPoint(-2.25F, -0.3F, -7.1F);
        this.emitter3.addBox(-0.5F, -0.8F, 0.0F, 3, 1, 9, 0.0F);
        this.setRotateAngle(emitter3, 0.0F, 1.5707963267948966F, 0.0F);
        this.ringElement4 = new ModelRenderer(this, 0, 0);
        this.ringElement4.setRotationPoint(-6.6F, 0.0F, -2.0F);
        this.ringElement4.addBox(0.0F, 0.0F, 0.0F, 4, 1, 1, 0.0F);
        this.setRotateAngle(ringElement4, 0.0F, -1.5707963267948966F, 0.0F);
        this.hing3 = new ModelRenderer(this, 15, 0);
        this.hing3.setRotationPoint(1.75F, -0.5F, -7.45F);
        this.hing3.addBox(0.0F, 0.0F, 0.0F, 1, 1, 7, 0.0F);
        this.setRotateAngle(hing3, 0.0F, -1.5716689914208934F, 0.0F);
        this.ringElement3 = new ModelRenderer(this, 0, 0);
        this.ringElement3.setRotationPoint(6.6F, 0.0F, 2.0F);
        this.ringElement3.addBox(0.0F, 0.0F, 0.0F, 4, 1, 1, 0.0F);
        this.setRotateAngle(ringElement3, 0.0F, 1.5707963267948966F, 0.0F);
    }

    @Override
    public void render(Entity entity, float embitterRotation, float brightness, float f2, float f3, float f4,
            float scale) {

        this.ringElement1.render(scale);
        this.ringElement2.render(scale);
        this.ringElement3.render(scale);
        this.ringElement4.render(scale);
        this.ringElement5.render(scale);
        this.ringElement6.render(scale);
        this.ringElement7.render(scale);
        this.ringElement8.render(scale);
        this.ringElement9.render(scale);
        this.ringElement10.render(scale);
        this.ringElement11.render(scale);
        this.ringElement12.render(scale);

        GL11.glPushMatrix();
        GL11.glTranslated(this.hing1.offsetX, this.hing1.offsetY, this.hing1.offsetZ);
        GL11.glTranslated(
                this.hing1.rotationPointX * scale,
                this.hing1.rotationPointY * scale,
                this.hing1.rotationPointZ * scale);
        GL11.glScaled(0.5D, 0.5D, 0.5D);
        GL11.glTranslated(-this.hing1.offsetX, -this.hing1.offsetY, -this.hing1.offsetZ);
        GL11.glTranslated(
                -this.hing1.rotationPointX * scale,
                -this.hing1.rotationPointY * scale,
                -this.hing1.rotationPointZ * scale);
        this.hing1.render(scale);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glTranslated(this.hing2.offsetX, this.hing2.offsetY, this.hing2.offsetZ);
        GL11.glTranslated(
                this.hing2.rotationPointX * scale,
                this.hing2.rotationPointY * scale,
                this.hing2.rotationPointZ * scale);
        GL11.glScaled(0.5D, 0.5D, 0.5D);
        GL11.glTranslated(-this.hing2.offsetX, -this.hing2.offsetY, -this.hing2.offsetZ);
        GL11.glTranslated(
                -this.hing2.rotationPointX * scale,
                -this.hing2.rotationPointY * scale,
                -this.hing2.rotationPointZ * scale);
        this.hing2.render(scale);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glTranslated(this.hing3.offsetX, this.hing3.offsetY, this.hing3.offsetZ);
        GL11.glTranslated(
                this.hing3.rotationPointX * scale,
                this.hing3.rotationPointY * scale,
                this.hing3.rotationPointZ * scale);
        GL11.glScaled(0.5D, 0.5D, 0.5D);
        GL11.glTranslated(-this.hing3.offsetX, -this.hing3.offsetY, -this.hing3.offsetZ);
        GL11.glTranslated(
                -this.hing3.rotationPointX * scale,
                -this.hing3.rotationPointY * scale,
                -this.hing3.rotationPointZ * scale);
        this.hing3.render(scale);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glTranslated(this.hing4.offsetX, this.hing4.offsetY, this.hing4.offsetZ);
        GL11.glTranslated(
                this.hing4.rotationPointX * scale,
                this.hing4.rotationPointY * scale,
                this.hing4.rotationPointZ * scale);
        GL11.glScaled(0.5D, 0.5D, 0.5D);
        GL11.glTranslated(-this.hing4.offsetX, -this.hing4.offsetY, -this.hing4.offsetZ);
        GL11.glTranslated(
                -this.hing4.rotationPointX * scale,
                -this.hing4.rotationPointY * scale,
                -this.hing4.rotationPointZ * scale);
        this.hing4.render(scale);
        GL11.glPopMatrix();

        // float lastBrightnessX = OpenGlHelper.lastBrightnessX;
        // float lastBrightnessY = OpenGlHelper.lastBrightnessY;
        // OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 200F, 200F);
        float lastBrightnessX = OpenGlHelper.lastBrightnessX;
        float lastBrightnessY = OpenGlHelper.lastBrightnessY;

        float b = brightness * 200F;
        float colour = Math.min(2F, (brightness * 2F) + 0.1F);

        OpenGlHelper.setLightmapTextureCoords(
                OpenGlHelper.lightmapTexUnit,
                Math.min(200F, lastBrightnessX + b),
                Math.min(200F, lastBrightnessY + b));
        GL11.glColor4f(colour, colour, colour, 1F);
        if (brightness > 0F) GL11.glDisable(GL11.GL_LIGHTING);

        GL11.glPushMatrix();
        GL11.glTranslated(this.emitter1.offsetX, this.emitter1.offsetY, this.emitter1.offsetZ);
        GL11.glTranslated(
                this.emitter1.rotationPointX * scale,
                this.emitter1.rotationPointY * scale,
                this.emitter1.rotationPointZ * scale);
        GL11.glRotatef(embitterRotation, 0, 0, 1);
        GL11.glScaled(0.5D, 0.5D, 0.5D);
        GL11.glTranslated(-this.emitter1.offsetX, -this.emitter1.offsetY, -this.emitter1.offsetZ);
        GL11.glTranslated(
                -this.emitter1.rotationPointX * scale,
                -this.emitter1.rotationPointY * scale,
                -this.emitter1.rotationPointZ * scale);
        this.emitter1.render(scale);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glTranslated(this.emitter2.offsetX, this.emitter2.offsetY, this.emitter2.offsetZ);
        GL11.glTranslated(
                this.emitter2.rotationPointX * scale,
                this.emitter2.rotationPointY * scale,
                this.emitter2.rotationPointZ * scale);
        GL11.glRotatef(embitterRotation, 0, 0, -1);
        GL11.glScaled(0.5D, 0.5D, 0.5D);
        GL11.glTranslated(-this.emitter2.offsetX, -this.emitter2.offsetY, -this.emitter2.offsetZ);
        GL11.glTranslated(
                -this.emitter2.rotationPointX * scale,
                -this.emitter2.rotationPointY * scale,
                -this.emitter2.rotationPointZ * scale);
        this.emitter2.render(scale);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glTranslated(this.emitter3.offsetX, this.emitter3.offsetY, this.emitter3.offsetZ);
        GL11.glTranslated(
                this.emitter3.rotationPointX * scale,
                this.emitter3.rotationPointY * scale,
                this.emitter3.rotationPointZ * scale);
        GL11.glRotatef(embitterRotation, 1, 0, 0);
        GL11.glScaled(0.5D, 0.5D, 0.5D);
        GL11.glTranslated(-this.emitter3.offsetX, -this.emitter3.offsetY, -this.emitter3.offsetZ);
        GL11.glTranslated(
                -this.emitter3.rotationPointX * scale,
                -this.emitter3.rotationPointY * scale,
                -this.emitter3.rotationPointZ * scale);
        this.emitter3.render(scale);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glTranslated(this.emitter4.offsetX, this.emitter4.offsetY, this.emitter4.offsetZ);
        GL11.glTranslated(
                this.emitter4.rotationPointX * scale,
                this.emitter4.rotationPointY * scale,
                this.emitter4.rotationPointZ * scale);
        GL11.glRotatef(embitterRotation, -1, 0, 0);
        GL11.glScaled(0.5D, 0.5D, 0.5D);
        GL11.glTranslated(-this.emitter4.offsetX, -this.emitter4.offsetY, -this.emitter4.offsetZ);
        GL11.glTranslated(
                -this.emitter4.rotationPointX * scale,
                -this.emitter4.rotationPointY * scale,
                -this.emitter4.rotationPointZ * scale);
        this.emitter4.render(scale);
        GL11.glPopMatrix();

        if (brightness > 0F) GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glColor4f(1F, 1F, 1F, 1F);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lastBrightnessX, lastBrightnessY);
        GL11.glEnable(GL11.GL_LIGHTING);
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
