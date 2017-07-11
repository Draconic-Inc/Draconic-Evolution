package com.brandon3055.draconicevolution.client.model;

import com.brandon3055.draconicevolution.client.handler.ClientEventHandler;
import com.brandon3055.draconicevolution.client.render.entity.ModelBoxFace;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.Vec3d;

/**
 * DragonWings - brandon3055
 * Created using Tabula 5.0.0
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
    public RenderPlayer renderPlayer = null;

    public ModelContributorWings() {
        this.textureWidth = 32;
        this.textureHeight = 32;
        this.leftWingOuter = new ModelRenderer(this, 0, 18);
        this.leftWingOuter.mirror = true;
        this.leftWingOuter.setRotationPoint(0.5F, 0.0F, 0.0F);
//        this.leftWingOuter.addBox(-0.5F, 0.5F, 0.0F, 15, 14, 0, 0.0F);
        this.leftWingOuter.cubeList.add(new ModelBoxFace(leftWingOuter, leftWingOuter.textureOffsetX, leftWingOuter.textureOffsetY, -0.5F, 0.5F, 0.0F, 15, 14, 0, 5));
        this.rightOuterStem = new ModelRenderer(this, 0, 2);
        this.rightOuterStem.setRotationPoint(-12.5F, 0.0F, 0.0F);
        this.rightOuterStem.addBox(-15.0F, -0.5F, -0.5F, 15, 1, 1, 0.0F);
        this.setRotateAngle(rightOuterStem, 0.0F, -0.6108652381980153F, 0.0F);
        this.leftWingInner = new ModelRenderer(this, 0, 4);
        this.leftWingInner.setRotationPoint(0.0F, 0.0F, 0.0F);
//        this.leftWingInner.addBox(-0.5F, 0.5F, 0.0F, 13, 14, 0, 0.0F);
        this.leftWingInner.cubeList.add(new ModelBoxFace(leftWingInner, leftWingInner.textureOffsetX, leftWingInner.textureOffsetY, -0.5F, 0.5F, 0.0F, 13, 14, 0, 4));
        this.rightWingInner = new ModelRenderer(this, 0, 4);
        this.rightWingInner.mirror = true;
        this.rightWingInner.setRotationPoint(0.0F, 0.0F, 0.0F);
//        this.rightWingInner.addBox(-12.5F, 0.5F, 0.0F, 13, 14, 0, 0.0F);
        this.rightWingInner.cubeList.add(new ModelBoxFace(rightWingInner, rightWingInner.textureOffsetX, rightWingInner.textureOffsetY, -12.5F, 0.5F, 0.0F, 13, 14, 0, 4));
        this.rightWingOuter = new ModelRenderer(this, 0, 18);
        this.rightWingOuter.setRotationPoint(-0.5F, 0.0F, 0.0F);
//        this.rightWingOuter.addBox(-14.5F, 0.5F, 0.0F, 15, 14, 0, 0.0F);
        this.rightWingOuter.cubeList.add(new ModelBoxFace(rightWingOuter, rightWingOuter.textureOffsetX, rightWingOuter.textureOffsetY, -14.5F, 0.5F, 0.0F, 15, 14, 0, 5));
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
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableCull();

        float baseRot = 0.45906584F;
        float outerRot = 0.61086524F;

        float animation = (float) Math.sin(((float) ClientEventHandler.elapsedTicks + Minecraft.getMinecraft().getRenderPartialTicks()) / 40) * 0.5F;

        if (entity instanceof EntityLivingBase && ((EntityLivingBase) entity).isElytraFlying()) {
            float f4 = 1.0F;

            if (entity.motionY < 0.0D) {
                Vec3d vec3d = (new Vec3d(entity.motionX, entity.motionY, entity.motionZ)).normalize();
                f4 = 1.0F - (float) Math.pow(-vec3d.y, 1.5D);
            }

            float f1 = 0;
            f1 = f4 * ((float) Math.PI / 2F) + (1.0F - f4) * f1;
            animation = -3 + (f1 * 2);
        }

        if (entity.isSneaking()) {
            this.leftBaseStem.rotateAngleX = 0.5F;
            this.rightBaseStem.rotateAngleX = 0.5F;
        }
        else {
            this.leftBaseStem.rotateAngleX = 0F;
            this.rightBaseStem.rotateAngleX = 0F;
        }

        this.leftBaseStem.rotateAngleY = -baseRot + (animation * 0.15F);
        this.leftOuterStem.rotateAngleY = outerRot + (animation * 0.3F);
        this.rightBaseStem.rotateAngleY = baseRot - (animation * 0.15F);
        this.rightOuterStem.rotateAngleY = -outerRot - (animation * 0.3F);

        this.leftBaseStem.render(scale);
        this.rightBaseStem.render(scale);
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
