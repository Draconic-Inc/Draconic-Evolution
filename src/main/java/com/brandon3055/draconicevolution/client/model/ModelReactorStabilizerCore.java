package com.brandon3055.draconicevolution.client.model;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraft.client.renderer.model.Model;

/**
 * ReactorStabilizerCore - brandon3055
 * Created using Tabula 5.0.0
 */
public class ModelReactorStabilizerCore extends Model {
    public RendererModel hub1;
    public RendererModel hub2;
    public RendererModel rotor1R;
    public RendererModel rotor2R;
    public RendererModel rotor1R_1;
    public RendererModel rotor1R_2;
    public RendererModel rotor1R_3;
    public RendererModel rotor1R_4;
    public RendererModel rotor1L;
    public RendererModel rotor1L_1;
    public RendererModel rotor1L_2;
    public RendererModel rotor1L_3;
    public RendererModel rotor1L_4;
    public RendererModel rotor2R_1;
    public RendererModel rotor2R_2;
    public RendererModel rotor2R_3;
    public RendererModel rotor2R_4;
    public RendererModel rotor2R_5;
    public RendererModel rotor2R_6;
    public RendererModel rotor2L;
    public RendererModel rotor2L_1;
    public RendererModel rotor2L_2;
    public RendererModel rotor2L_3;
    public RendererModel rotor2L_4;
    public RendererModel rotor2L_5;
    public RendererModel rotor2L_6;
    public RendererModel basePlate;
    public RendererModel frame1;
    public RendererModel frame2;
    public RendererModel frame3;
    public RendererModel frame4;
    public RendererModel frame5;
    public RendererModel frame6;
    public RendererModel frame7;
    public RendererModel frame8;
    public RendererModel frame9;
    public RendererModel frame10;
    public RendererModel frame11;
    public RendererModel frame12;
    public RendererModel backSpoke1;
    public RendererModel backSpoke2;
    public RendererModel coreElement2;
    public RendererModel coreElement1;

    public ModelReactorStabilizerCore() {
        this.textureWidth = 64;
        this.textureHeight = 32;
        this.coreElement2 = new RendererModel(this, 32, 20);
        this.coreElement2.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.coreElement2.addBox(-0.5F, -0.5F, -2.0F, 1, 1, 4, 0.0F);
        this.rotor2L_1 = new RendererModel(this, 0, 4);
        this.rotor2L_1.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.rotor2L_1.addBox(4.5F, -0.5F, -7.0F, 1, 1, 13, 0.0F);
        this.setRotateAngle(rotor2L_1, 0.0F, 0.0F, -3.3213615665452094F);
        this.rotor2L_5 = new RendererModel(this, 0, 4);
        this.rotor2L_5.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.rotor2L_5.addBox(4.5F, -0.5F, -7.0F, 1, 1, 13, 0.0F);
        this.setRotateAngle(rotor2L_5, 0.0F, 0.0F, 2.602285914723545F);
        this.rotor1L_3 = new RendererModel(this, 28, 2);
        this.rotor1L_3.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.rotor1L_3.addBox(3.0F, -0.5F, -6.0F, 1, 1, 11, 0.0F);
        this.setRotateAngle(rotor1L_3, 0.0F, 0.0F, 3.637266161156183F);
        this.rotor2L_6 = new RendererModel(this, 0, 4);
        this.rotor2L_6.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.rotor2L_6.addBox(4.5F, -0.5F, -7.0F, 1, 1, 13, 0.0F);
        this.setRotateAngle(rotor2L_6, 0.0F, 0.0F, 3.6808993924560407F);
        this.frame12 = new RendererModel(this, 0, 4);
        this.frame12.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.frame12.addBox(7.0F, -6.0F, 7.0F, 1, 12, 1, 0.0F);
        this.frame10 = new RendererModel(this, 0, 4);
        this.frame10.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.frame10.addBox(-8.0F, -6.0F, -8.0F, 1, 12, 1, 0.0F);
        this.rotor2L_3 = new RendererModel(this, 0, 4);
        this.rotor2L_3.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.rotor2L_3.addBox(4.5F, -0.5F, -7.0F, 1, 1, 13, 0.0F);
        this.setRotateAngle(rotor2L_3, 0.0F, 0.0F, -3.501130479500625F);
        this.rotor2R_6 = new RendererModel(this, 0, 4);
        this.rotor2R_6.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.rotor2R_6.addBox(4.5F, -0.5F, -7.0F, 1, 1, 13, 0.0F);
        this.setRotateAngle(rotor2R_6, 0.0F, 0.0F, 0.5393067388662478F);
        this.coreElement1 = new RendererModel(this, 32, 14);
        this.coreElement1.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.coreElement1.addBox(-1.0F, -1.0F, 2.0F, 2, 2, 4, 0.0F);
        this.rotor1L_4 = new RendererModel(this, 28, 2);
        this.rotor1L_4.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.rotor1L_4.addBox(3.0F, -0.5F, -6.0F, 1, 1, 11, 0.0F);
        this.setRotateAngle(rotor1L_4, 0.0F, 0.0F, 2.6459191460234033F);
        this.basePlate = new RendererModel(this, 0, 18);
        this.basePlate.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.basePlate.addBox(-4.0F, -4.0F, 6.0F, 8, 8, 2, 0.0F);
        this.rotor1L_2 = new RendererModel(this, 28, 2);
        this.rotor1L_2.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.rotor1L_2.addBox(3.0F, -0.5F, -6.0F, 1, 1, 11, 0.0F);
        this.setRotateAngle(rotor1L_2, 0.0F, 0.0F, 2.8937558998065986F);
        this.frame1 = new RendererModel(this, 12, 14);
        this.frame1.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.frame1.addBox(6.0F, 6.0F, -8.0F, 2, 2, 16, 0.0F);
        this.rotor1L_1 = new RendererModel(this, 28, 2);
        this.rotor1L_1.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.rotor1L_1.addBox(3.0F, -0.5F, -6.0F, 1, 1, 11, 0.0F);
        this.setRotateAngle(rotor1L_1, 0.0F, 0.0F, -2.8937558998065986F);
        this.frame5 = new RendererModel(this, 14, 0);
        this.frame5.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.frame5.addBox(-6.0F, -8.0F, -8.0F, 12, 1, 1, 0.0F);
        this.frame11 = new RendererModel(this, 0, 4);
        this.frame11.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.frame11.addBox(7.0F, -6.0F, -8.0F, 1, 12, 1, 0.0F);
        this.rotor1R_3 = new RendererModel(this, 28, 2);
        this.rotor1R_3.setRotationPoint(0.0F, 0.0F, 1.0F);
        this.rotor1R_3.addBox(3.0F, -0.5F, -7.0F, 1, 1, 11, 0.0F);
        this.setRotateAngle(rotor1R_3, 0.0F, 0.0F, -0.49567350756638956F);
        this.rotor2R_3 = new RendererModel(this, 0, 4);
        this.rotor2R_3.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.rotor2R_3.addBox(4.5F, -0.5F, -7.0F, 1, 1, 13, 0.0F);
        this.setRotateAngle(rotor2R_3, 0.0F, 0.0F, 0.3595378259108319F);
        this.frame3 = new RendererModel(this, 12, 14);
        this.frame3.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.frame3.addBox(6.0F, -8.0F, -8.0F, 2, 2, 16, 0.0F);
        this.rotor2R_5 = new RendererModel(this, 0, 4);
        this.rotor2R_5.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.rotor2R_5.addBox(4.5F, -0.5F, -7.0F, 1, 1, 13, 0.0F);
        this.setRotateAngle(rotor2R_5, 0.0F, 0.0F, -0.5393067388662478F);
        this.rotor2R_2 = new RendererModel(this, 0, 4);
        this.rotor2R_2.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.rotor2R_2.addBox(4.5F, -0.5F, -7.0F, 1, 1, 13, 0.0F);
        this.setRotateAngle(rotor2R_2, 0.0F, 0.0F, 0.17976891295541594F);
        this.backSpoke2 = new RendererModel(this, 52, 0);
        this.backSpoke2.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.backSpoke2.addBox(-0.5F, -9.5F, 6.95F, 1, 19, 1, 0.0F);
        this.setRotateAngle(backSpoke2, 0.0F, 0.0F, 0.7853981633974483F);
        this.frame6 = new RendererModel(this, 14, 0);
        this.frame6.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.frame6.addBox(-6.0F, 7.0F, -8.0F, 12, 1, 1, 0.0F);
        this.rotor1R_2 = new RendererModel(this, 28, 2);
        this.rotor1R_2.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.rotor1R_2.addBox(3.0F, -0.5F, -6.0F, 1, 1, 11, 0.0F);
        this.setRotateAngle(rotor1R_2, 0.0F, 0.0F, 0.24783675378319478F);
        this.hub1 = new RendererModel(this, 0, 0);
        this.hub1.setRotationPoint(0.0F, 0.0F, -0.02F);
        this.hub1.addBox(-3.0F, -0.5F, 4.0F, 6, 1, 1, 0.0F);
        this.rotor2R_1 = new RendererModel(this, 0, 4);
        this.rotor2R_1.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.rotor2R_1.addBox(4.5F, -0.5F, -7.0F, 1, 1, 13, 0.0F);
        this.setRotateAngle(rotor2R_1, 0.0F, 0.0F, -0.17976891295541594F);
        this.hub2 = new RendererModel(this, 0, 2);
        this.hub2.setRotationPoint(0.0F, 0.0F, -0.01F);
        this.hub2.addBox(-4.5F, -0.5F, 5.0F, 9, 1, 1, 0.0F);
        this.backSpoke1 = new RendererModel(this, 52, 0);
        this.backSpoke1.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.backSpoke1.addBox(-0.5F, -9.5F, 6.95F, 1, 19, 1, 0.0F);
        this.setRotateAngle(backSpoke1, 0.0F, 0.0F, -0.7853981633974483F);
        this.rotor1R_4 = new RendererModel(this, 28, 2);
        this.rotor1R_4.setRotationPoint(0.0F, 0.0F, 1.0F);
        this.rotor1R_4.addBox(3.0F, -0.5F, -7.0F, 1, 1, 11, 0.0F);
        this.setRotateAngle(rotor1R_4, 0.0F, 0.0F, 0.49567350756638956F);
        this.rotor1R = new RendererModel(this, 28, 2);
        this.rotor1R.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.rotor1R.addBox(3.0F, -0.5F, -6.0F, 1, 1, 11, 0.0F);
        this.rotor2L_4 = new RendererModel(this, 0, 4);
        this.rotor2L_4.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.rotor2L_4.addBox(4.5F, -0.5F, -7.0F, 1, 1, 13, 0.0F);
        this.setRotateAngle(rotor2L_4, 0.0F, 0.0F, -2.7820548276789614F);
        this.frame4 = new RendererModel(this, 12, 14);
        this.frame4.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.frame4.addBox(-8.0F, -8.0F, -8.0F, 2, 2, 16, 0.0F);
        this.rotor2R = new RendererModel(this, 0, 4);
        this.rotor2R.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.rotor2R.addBox(4.5F, -0.5F, -7.0F, 1, 1, 13, 0.0F);
        this.rotor2R_4 = new RendererModel(this, 0, 4);
        this.rotor2R_4.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.rotor2R_4.addBox(4.5F, -0.5F, -7.0F, 1, 1, 13, 0.0F);
        this.setRotateAngle(rotor2R_4, 0.0F, 0.0F, -0.3595378259108319F);
        this.rotor2L_2 = new RendererModel(this, 0, 4);
        this.rotor2L_2.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.rotor2L_2.addBox(4.5F, -0.5F, -7.0F, 1, 1, 13, 0.0F);
        this.setRotateAngle(rotor2L_2, 0.0F, 0.0F, -2.961823740634377F);
        this.frame9 = new RendererModel(this, 0, 4);
        this.frame9.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.frame9.addBox(-8.0F, -6.0F, 7.0F, 1, 12, 1, 0.0F);
        this.rotor2L = new RendererModel(this, 0, 4);
        this.rotor2L.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.rotor2L.addBox(4.5F, -0.5F, -7.0F, 1, 1, 13, 0.0F);
        this.setRotateAngle(rotor2L, 0.0F, 0.0F, -3.141592653589793F);
        this.rotor1L = new RendererModel(this, 28, 2);
        this.rotor1L.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.rotor1L.addBox(3.0F, -0.5F, -6.0F, 1, 1, 11, 0.0F);
        this.setRotateAngle(rotor1L, 0.0F, 0.0F, -3.141592653589793F);
        this.frame8 = new RendererModel(this, 14, 0);
        this.frame8.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.frame8.addBox(-6.0F, 7.0F, 7.0F, 12, 1, 1, 0.0F);
        this.frame2 = new RendererModel(this, 12, 14);
        this.frame2.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.frame2.addBox(-8.0F, 6.0F, -8.0F, 2, 2, 16, 0.0F);
        this.frame7 = new RendererModel(this, 14, 0);
        this.frame7.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.frame7.addBox(-6.0F, -8.0F, 7.0F, 12, 1, 1, 0.0F);
        this.rotor1R_1 = new RendererModel(this, 28, 2);
        this.rotor1R_1.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.rotor1R_1.addBox(3.0F, -0.5F, -6.0F, 1, 1, 11, 0.0F);
        this.setRotateAngle(rotor1R_1, 0.0F, 0.0F, -0.24783675378319478F);
        this.basePlate.addChild(this.coreElement2);
        this.rotor2R.addChild(this.rotor2L_1);
        this.rotor2R.addChild(this.rotor2L_5);
        this.rotor1R.addChild(this.rotor1L_3);
        this.rotor2R.addChild(this.rotor2L_6);
        this.basePlate.addChild(this.frame12);
        this.basePlate.addChild(this.frame10);
        this.rotor2R.addChild(this.rotor2L_3);
        this.rotor2R.addChild(this.rotor2R_6);
        this.basePlate.addChild(this.coreElement1);
        this.rotor1R.addChild(this.rotor1L_4);
        this.rotor1R.addChild(this.rotor1L_2);
        this.basePlate.addChild(this.frame1);
        this.rotor1R.addChild(this.rotor1L_1);
        this.basePlate.addChild(this.frame5);
        this.basePlate.addChild(this.frame11);
        this.rotor1R.addChild(this.rotor1R_3);
        this.rotor2R.addChild(this.rotor2R_3);
        this.basePlate.addChild(this.frame3);
        this.rotor2R.addChild(this.rotor2R_5);
        this.rotor2R.addChild(this.rotor2R_2);
        this.basePlate.addChild(this.backSpoke2);
        this.basePlate.addChild(this.frame6);
        this.rotor1R.addChild(this.rotor1R_2);
        this.rotor2R.addChild(this.rotor2R_1);
        this.basePlate.addChild(this.backSpoke1);
        this.rotor1R.addChild(this.rotor1R_4);
        this.rotor2R.addChild(this.rotor2L_4);
        this.basePlate.addChild(this.frame4);
        this.rotor2R.addChild(this.rotor2R_4);
        this.rotor2R.addChild(this.rotor2L_2);
        this.basePlate.addChild(this.frame9);
        this.rotor2R.addChild(this.rotor2L);
        this.rotor1R.addChild(this.rotor1L);
        this.basePlate.addChild(this.frame8);
        this.basePlate.addChild(this.frame2);
        this.basePlate.addChild(this.frame7);
        this.rotor1R.addChild(this.rotor1R_1);
    }

    public void render(float rotation, float brightness, float invRender, float f5) {
        GlStateManager.pushMatrix();

        this.basePlate.render(f5);
        GlStateManager.rotatef(rotation, 0F, 0F, 1F);
        this.hub1.render(f5);
        GlStateManager.rotatef(rotation * 2F, 0F, 0F, -1F);
        this.hub2.render(f5);

        float lastBrightnessX = GLX.lastBrightnessX;
        float lastBrightnessY = GLX.lastBrightnessY;

        float b = brightness * 200F;
        float colour = Math.min(2F, (brightness * 2F) + 0.1F);
        GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, Math.min(200F, lastBrightnessX + b), Math.min(200F, lastBrightnessY + b));
        if (brightness > 0F && invRender == 0) {
            GlStateManager.disableLighting();
        }

        GlStateManager.color4f(colour, colour, colour, 1F);
        this.rotor2R.render(f5);
        GlStateManager.rotatef(rotation * 2F, 0F, 0F, 1F);
        this.rotor1R.render(f5);
        GlStateManager.rotatef(1F, 1F, 1F, 1F);

        GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, lastBrightnessX, lastBrightnessY);
        if (brightness > 0F && invRender == 0) {
            GlStateManager.enableLighting();
        }

        GlStateManager.popMatrix();
    }

    /**
     * This is a helper function from Tabula to set the rotation of model parts
     */
    public void setRotateAngle(RendererModel modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
