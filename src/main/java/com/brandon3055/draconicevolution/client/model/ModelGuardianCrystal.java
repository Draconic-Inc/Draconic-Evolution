package com.brandon3055.draconicevolution.client.model;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.entity.Entity;

/**
 * ModelChaosCrystal - Mojang-(Original) modified by brandon3055
 * Created using Tabula 5.0.0
 */
public class ModelGuardianCrystal extends Model {
    public RendererModel base;
    public RendererModel glass;
    public RendererModel cube;
    public RendererModel ringBase1;
    public RendererModel RingSegment1;
    public RendererModel RingSegment2;
    public RendererModel RingSegment3;
    public RendererModel RingSegment4;
    public RendererModel ringBase2;
    public RendererModel RingSegment1_1;
    public RendererModel RingSegment2_1;
    public RendererModel RingSegment3_1;
    public RendererModel RingSegment4_1;

    public ModelGuardianCrystal(boolean hasBase) {
        this.textureWidth = 64;
        this.textureHeight = 64;
        this.RingSegment3_1 = new RendererModel(this, 12, 38);
        this.RingSegment3_1.setRotationPoint(0.0F, 0.0F, -10.0F);
        this.RingSegment3_1.addBox(-1.0F, -1.0F, -2.0F, 2, 2, 4, 0.0F);
        this.setRotateAngle(RingSegment3_1, 0.0F, 1.5707963267948966F, 0.0F);
        this.RingSegment3 = new RendererModel(this, 12, 32);
        this.RingSegment3.setRotationPoint(0.0F, 0.0F, -10.0F);
        this.RingSegment3.addBox(-1.0F, -1.0F, -2.0F, 2, 2, 4, 0.0F);
        this.setRotateAngle(RingSegment3, 0.0F, 1.5707963267948966F, 0.0F);
        this.cube = new RendererModel(this, 32, 0);
        this.cube.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.cube.addBox(-4.0F, -4.0F, -4.0F, 8, 8, 8, 0.0F);
        this.RingSegment2_1 = new RendererModel(this, 24, 38);
        this.RingSegment2_1.setRotationPoint(-10.0F, 0.0F, 0.0F);
        this.RingSegment2_1.addBox(-1.0F, -1.0F, -2.0F, 2, 2, 4, 0.0F);
        this.ringBase2 = new RendererModel(this, 0, 0);
        this.ringBase2.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.ringBase2.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, 0.0F);
        this.setRotateAngle(ringBase2, 0.0F, 0.7853981633974483F, 0.0F);
        this.base = new RendererModel(this, 0, 16);
        this.base.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.base.addBox(-6.0F, 0.0F, -6.0F, 12, 4, 12, 0.0F);
        this.ringBase1 = new RendererModel(this, 0, 0);
        this.ringBase1.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.ringBase1.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, 0.0F);
        this.RingSegment1 = new RendererModel(this, 0, 32);
        this.RingSegment1.setRotationPoint(10.0F, 0.0F, 0.0F);
        this.RingSegment1.addBox(-1.0F, -1.0F, -2.0F, 2, 2, 4, 0.0F);
        this.glass = new RendererModel(this, 0, 0);
        this.glass.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.glass.addBox(-4.0F, -4.0F, -4.0F, 8, 8, 8, 0.0F);
        this.RingSegment4_1 = new RendererModel(this, 36, 38);
        this.RingSegment4_1.setRotationPoint(0.0F, 0.0F, 10.0F);
        this.RingSegment4_1.addBox(-1.0F, -1.0F, -2.0F, 2, 2, 4, 0.0F);
        this.setRotateAngle(RingSegment4_1, 0.0F, 1.5707963267948966F, 0.0F);
        this.RingSegment4 = new RendererModel(this, 36, 32);
        this.RingSegment4.setRotationPoint(0.0F, 0.0F, 10.0F);
        this.RingSegment4.addBox(-1.0F, -1.0F, -2.0F, 2, 2, 4, 0.0F);
        this.setRotateAngle(RingSegment4, 0.0F, 1.5707963267948966F, 0.0F);
        this.RingSegment2 = new RendererModel(this, 24, 32);
        this.RingSegment2.setRotationPoint(-10.0F, 0.0F, 0.0F);
        this.RingSegment2.addBox(-1.0F, -1.0F, -2.0F, 2, 2, 4, 0.0F);
        this.RingSegment1_1 = new RendererModel(this, 0, 38);
        this.RingSegment1_1.setRotationPoint(10.0F, 0.0F, 0.0F);
        this.RingSegment1_1.addBox(-1.0F, -1.0F, -2.0F, 2, 2, 4, 0.0F);
        this.ringBase2.addChild(this.RingSegment3_1);
        this.ringBase1.addChild(this.RingSegment3);
        this.ringBase2.addChild(this.RingSegment2_1);
        this.ringBase1.addChild(this.ringBase2);
        this.ringBase1.addChild(this.RingSegment1);
        this.ringBase2.addChild(this.RingSegment4_1);
        this.ringBase1.addChild(this.RingSegment4);
        this.ringBase1.addChild(this.RingSegment2);
        this.ringBase2.addChild(this.RingSegment1_1);
    }

    public void render(Entity entity, float f1, float f2, float f3, float deathAnimation, float health, float scale) {
        GlStateManager.pushMatrix();
        GlStateManager.scalef(2.0F, 2.0F, 2.0F);
        GlStateManager.translatef(0.0F, -0.5F, 0.0F);

        if (this.base != null) {
            this.base.render(scale);
        }

        GlStateManager.pushMatrix();
        float s = 1.1F + (float) Math.sin(f2 / 20) * 0.1F;
        GlStateManager.translatef(0.0F, 0.1F + deathAnimation * 0.9F, 0.0F);
        GlStateManager.rotatef(f2 * 2, 0.0F, 1.0F, 0.1F);
        GlStateManager.scalef(s, s, s);
        this.ringBase1.render(scale);
        GlStateManager.popMatrix();

        GlStateManager.rotatef(f2, 0.0F, 1.0F, 0.0F);
        GlStateManager.translatef(0.0F, 0.7F + deathAnimation * (0.1F + f3), 0.0F);
        GlStateManager.rotatef(60.0F, 0.7071F, 0.0F, 0.7071F);
        this.glass.render(scale);
        float f6 = 0.875F;
        GlStateManager.scalef(f6, f6, f6);
        GlStateManager.rotatef(60.0F, 0.7071F, 0.0F, 0.7071F);
        GlStateManager.rotatef(f2, 0.0F, 1.0F, 0.0F);
        this.glass.render(scale);
        GlStateManager.scalef(f6, f6, f6);
        GlStateManager.rotatef(60.0F, 0.7071F, 0.0F, 0.7071F);
        GlStateManager.rotatef(f2, 0.0F, 1.0F, 0.0F);
        this.cube.render(scale);
        GlStateManager.popMatrix();
    }

    /**
     * This is a helper function from Tabula to set the ROTATION of model parts
     */
    public void setRotateAngle(RendererModel modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
