package com.brandon3055.draconicevolution.client.model.special;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

import org.lwjgl.opengl.GL11;

import com.brandon3055.draconicevolution.client.handler.ClientEventHandler;

/**
 * DragonWings - brandon3055 Created using Tabula 5.0.0
 */
public class ModelContributorWings extends ModelBase {

    public ModelRenderer rightBaseStem;
    public ModelRenderer leftBaseStem;
    public ModelRenderer rightOuterStem;
    public ModelRenderer rightWingInner;
    public ModelRenderer rightWingOuter;
    public ModelRenderer leftOuterStem;
    public ModelRenderer leftWingInner;
    public ModelRenderer leftWingOuter;

    public ModelContributorWings() {
        this.textureWidth = 32;
        this.textureHeight = 32;
        this.leftWingOuter = new ModelRenderer(this, 0, 18);
        this.leftWingOuter.mirror = true;
        this.leftWingOuter.setRotationPoint(0.5F, 0.0F, 0.0F);
        this.leftWingOuter.addBox(-0.5F, 0.5F, 0.0F, 15, 14, 0, 0.0F);
        this.rightOuterStem = new ModelRenderer(this, 0, 2);
        this.rightOuterStem.setRotationPoint(-12.5F, 0.0F, 0.0F);
        this.rightOuterStem.addBox(-15.0F, -0.5F, -0.5F, 15, 1, 1, 0.0F);
        this.setRotateAngle(rightOuterStem, 0.0F, -0.6108652381980153F, 0.0F);
        this.leftWingInner = new ModelRenderer(this, 0, 4);
        this.leftWingInner.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.leftWingInner.addBox(-0.5F, 0.5F, 0.0F, 13, 14, 0, 0.0F);
        this.rightWingInner = new ModelRenderer(this, 0, 4);
        this.rightWingInner.mirror = true;
        this.rightWingInner.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.rightWingInner.addBox(-12.5F, 0.5F, 0.0F, 13, 14, 0, 0.0F);
        this.rightWingOuter = new ModelRenderer(this, 0, 18);
        this.rightWingOuter.setRotationPoint(-0.5F, 0.0F, 0.0F);
        this.rightWingOuter.addBox(-14.5F, 0.5F, 0.0F, 15, 14, 0, 0.0F);
        this.leftBaseStem = new ModelRenderer(this, 0, 0);
        this.leftBaseStem.setRotationPoint(0.5F, 1.0F, 2.0F);
        this.leftBaseStem.addBox(-0.5F, -0.5F, -0.5F, 13, 1, 1, 0.0F);
        this.setRotateAngle(leftBaseStem, 0.0F, -0.3490658503988659F, 0.0F);
        this.leftOuterStem = new ModelRenderer(this, 0, 2);
        this.leftOuterStem.setRotationPoint(12.5F, 0.0F, 0.0F);
        this.leftOuterStem.addBox(0.0F, -0.5F, -0.5F, 15, 1, 1, 0.0F);
        this.setRotateAngle(leftOuterStem, 0.0F, 0.6108652381980153F, 0.0F);
        this.rightBaseStem = new ModelRenderer(this, 0, 0);
        this.rightBaseStem.setRotationPoint(-0.5F, 1.0F, 2.0F);
        this.rightBaseStem.addBox(-12.5F, -0.5F, -0.5F, 13, 1, 1, 0.0F);
        this.setRotateAngle(rightBaseStem, 0.0F, 0.3490658503988659F, 0.0F);
        this.leftOuterStem.addChild(this.leftWingOuter);
        this.rightBaseStem.addChild(this.rightOuterStem);
        this.leftBaseStem.addChild(this.leftWingInner);
        this.rightBaseStem.addChild(this.rightWingInner);
        this.rightOuterStem.addChild(this.rightWingOuter);
        this.leftBaseStem.addChild(this.leftOuterStem);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float partialTick, float scale) {
        GL11.glPushMatrix();

        float baseRot = 0.45906584F;
        float outerRot = 0.61086524F;

        float animation = (float) Math.sin(((float) ClientEventHandler.elapsedTicks + partialTick) / 20);

        if (entity.isSneaking()) {
            this.leftBaseStem.rotateAngleX = 0.5F;
            this.rightBaseStem.rotateAngleX = 0.5F;
        } else {
            this.leftBaseStem.rotateAngleX = 0F;
            this.rightBaseStem.rotateAngleX = 0F;
        }

        this.leftBaseStem.rotateAngleY = -baseRot + (animation * 0.15F);
        this.leftOuterStem.rotateAngleY = outerRot + (animation * 0.3F);
        this.rightBaseStem.rotateAngleY = baseRot - (animation * 0.15F);
        this.rightOuterStem.rotateAngleY = -outerRot - (animation * 0.3F);

        this.leftBaseStem.render(scale);
        this.rightBaseStem.render(scale);

        GL11.glPopMatrix();
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
