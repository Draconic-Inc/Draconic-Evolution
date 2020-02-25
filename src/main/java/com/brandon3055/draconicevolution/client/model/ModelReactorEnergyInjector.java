package com.brandon3055.draconicevolution.client.model;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraft.client.renderer.model.Model;
import org.lwjgl.opengl.GL11;

/**
 * ModelReactorPowerInjector - brandon3055
 * Created using Tabula 5.0.0
 */
public class ModelReactorEnergyInjector extends Model {
    public RendererModel BasePlate;
    public RendererModel shape14;
    public RendererModel coreSupports;
    public RendererModel element1;
    public RendererModel support1;
    public RendererModel element7;
    public RendererModel element6;
    public RendererModel element5;
    public RendererModel element8;
    public RendererModel element2;
    public RendererModel element3;
    public RendererModel element4;
    public RendererModel support2;
    public RendererModel support3;
    public RendererModel support4;
    public RendererModel connector1;
    public RendererModel connector2;
    public RendererModel connector3;
    public RendererModel connector4;
    public RendererModel connector5;
    public RendererModel connector6;
    public RendererModel connector7;
    public RendererModel support8;
    public RendererModel connector8;
    public RendererModel support5;
    public RendererModel support6;
    public RendererModel support7;

    public ModelReactorEnergyInjector() {
        this.textureWidth = 64;
        this.textureHeight = 32;
        this.connector5 = new RendererModel(this, 4, 19);
        this.connector5.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.connector5.addBox(-3.5F, 3.5F, -0.5F, 1, 1, 1, 0.0F);
        this.support4 = new RendererModel(this, 6, 21);
        this.support4.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.support4.addBox(3.5F, 0.0F, -1.5F, 1, 7, 3, 0.0F);
        this.support6 = new RendererModel(this, 2, 26);
        this.support6.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.support6.addBox(4.5F, 3.0F, -0.5F, 1, 4, 1, 0.0F);
        this.element2 = new RendererModel(this, 0, 12);
        this.element2.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.element2.addBox(-1.0F, 2.5F, 2.0F, 2, 3, 1, 0.0F);
        this.setRotateAngle(element2, 0.0F, 1.5707963267948966F, 0.0F);
        this.element4 = new RendererModel(this, 0, 12);
        this.element4.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.element4.addBox(-1.0F, 2.5F, 2.0F, 2, 3, 1, 0.0F);
        this.setRotateAngle(element4, 0.0F, 3.141592653589793F, 0.0F);
        this.element6 = new RendererModel(this, 0, 1);
        this.element6.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.element6.addBox(-1.0F, -3.0F, 1.9F, 2, 5, 1, 0.0F);
        this.setRotateAngle(element6, 0.0F, -1.5707963267948966F, 0.0F);
        this.connector2 = new RendererModel(this, 8, 19);
        this.connector2.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.connector2.addBox(-4.5F, -1.0F, -0.5F, 2, 1, 1, 0.0F);
        this.element5 = new RendererModel(this, 0, 1);
        this.element5.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.element5.addBox(-1.0F, -3.0F, 1.9F, 2, 5, 1, 0.0F);
        this.setRotateAngle(element5, 0.0F, 1.5707963267948966F, 0.0F);
        this.support7 = new RendererModel(this, 2, 26);
        this.support7.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.support7.addBox(-0.5F, 3.0F, -5.5F, 1, 4, 1, 0.0F);
        this.connector8 = new RendererModel(this, 4, 19);
        this.connector8.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.connector8.addBox(-0.5F, 3.5F, 2.5F, 1, 1, 1, 0.0F);
        this.element7 = new RendererModel(this, 0, 1);
        this.element7.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.element7.addBox(-1.0F, -3.0F, 1.9F, 2, 5, 1, 0.0F);
        this.support1 = new RendererModel(this, 42, 23);
        this.support1.setRotationPoint(0.0F, -1.0F, 0.0F);
        this.support1.addBox(-1.5F, 0.0F, 3.5F, 3, 7, 1, 0.0F);
        this.connector7 = new RendererModel(this, 4, 19);
        this.connector7.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.connector7.addBox(-0.5F, 3.5F, -3.5F, 1, 1, 1, 0.0F);
        this.support5 = new RendererModel(this, 2, 26);
        this.support5.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.support5.addBox(-5.5F, 3.0F, -0.5F, 1, 4, 1, 0.0F);
        this.support8 = new RendererModel(this, 2, 26);
        this.support8.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.support8.addBox(-0.5F, 3.0F, 4.5F, 1, 4, 1, 0.0F);
        this.shape14 = new RendererModel(this, 0, 17);
        this.shape14.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.shape14.addBox(-7.0F, 6.0F, -7.0F, 14, 1, 14, 0.0F);
        this.BasePlate = new RendererModel(this, 0, 0);
        this.BasePlate.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.BasePlate.addBox(-8.0F, 7.0F, -8.0F, 16, 1, 16, 0.0F);
        this.element8 = new RendererModel(this, 0, 1);
        this.element8.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.element8.addBox(-1.0F, -3.0F, 1.9F, 2, 5, 1, 0.0F);
        this.setRotateAngle(element8, 0.0F, 3.141592653589793F, 0.0F);
        this.element1 = new RendererModel(this, 0, 12);
        this.element1.setRotationPoint(0.0F, -1.0F, 0.0F);
        this.element1.addBox(-1.0F, 2.5F, 2.0F, 2, 3, 1, 0.0F);
        this.connector4 = new RendererModel(this, 3, 21);
        this.connector4.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.connector4.addBox(-0.5F, -1.0F, -4.5F, 1, 1, 2, 0.0F);
        this.connector6 = new RendererModel(this, 4, 19);
        this.connector6.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.connector6.addBox(2.5F, 3.5F, -0.5F, 1, 1, 1, 0.0F);
        this.support2 = new RendererModel(this, 42, 23);
        this.support2.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.support2.addBox(-1.5F, 0.0F, -4.5F, 3, 7, 1, 0.0F);
        this.support3 = new RendererModel(this, 6, 21);
        this.support3.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.support3.addBox(-4.5F, 0.0F, -1.5F, 1, 7, 3, 0.0F);
        this.connector3 = new RendererModel(this, 3, 21);
        this.connector3.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.connector3.addBox(-0.5F, -1.0F, 2.5F, 1, 1, 2, 0.0F);
        this.element3 = new RendererModel(this, 0, 12);
        this.element3.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.element3.addBox(-1.0F, 2.5F, 2.0F, 2, 3, 1, 0.0F);
        this.setRotateAngle(element3, 0.0F, -1.5707963267948966F, 0.0F);
        this.connector1 = new RendererModel(this, 8, 19);
        this.connector1.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.connector1.addBox(2.5F, -1.0F, -0.5F, 2, 1, 1, 0.0F);
        this.coreSupports = new RendererModel(this, 42, 17);
        this.coreSupports.setRotationPoint(0.0F, -1.0F, 0.0F);
        this.coreSupports.addBox(-2.5F, 6.0F, -2.5F, 5, 1, 5, 0.0F);
        this.support1.addChild(this.connector5);
        this.support1.addChild(this.support4);
        this.support1.addChild(this.support6);
        this.element1.addChild(this.element2);
        this.element1.addChild(this.element4);
        this.element1.addChild(this.element6);
        this.support1.addChild(this.connector2);
        this.element1.addChild(this.element5);
        this.support1.addChild(this.support7);
        this.support1.addChild(this.connector8);
        this.element1.addChild(this.element7);
        this.support1.addChild(this.connector7);
        this.support1.addChild(this.support5);
        this.support1.addChild(this.support8);
        this.BasePlate.addChild(this.shape14);
        this.element1.addChild(this.element8);
        this.support1.addChild(this.connector4);
        this.support1.addChild(this.connector6);
        this.support1.addChild(this.support2);
        this.support1.addChild(this.support3);
        this.support1.addChild(this.connector3);
        this.element1.addChild(this.element3);
        this.support1.addChild(this.connector1);
    }

    public void render(float brightness, float invRender, float f5) {
        GlStateManager.rotated(90, 1, 0, 0);
        this.support1.render(f5);
        this.BasePlate.render(f5);
        //this.coreSupports.render(f5);

        float lastBrightnessX = GLX.lastBrightnessX;
        float lastBrightnessY = GLX.lastBrightnessY;

        float b = brightness * 200F;
        float colour = Math.min(2F, (brightness * 2F) + 0.5F);

        if (brightness > 0F && invRender == 0) {
            GL11.glDisable(GL11.GL_LIGHTING);
        }

        GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, Math.min(200F, lastBrightnessX + b), Math.min(200F, lastBrightnessY + b));
        GL11.glColor4f(colour, colour, colour, 1F);
        this.element1.render(f5);
        GL11.glColor4f(1F, 1F, 1F, 1F);
        GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, lastBrightnessX, lastBrightnessY);

        if (brightness > 0F && invRender == 0) {
            GL11.glEnable(GL11.GL_LIGHTING);
        }
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
