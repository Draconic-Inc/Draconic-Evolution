package com.brandon3055.draconicevolution.client.model;

import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.OBJParser;
import codechicken.lib.render.buffer.VBORenderType;
import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.client.model.tool.VBOModelRender;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.HandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

import java.util.Collections;
import java.util.Map;
import java.util.function.Function;

import static net.minecraft.client.renderer.RenderState.*;

/**
 * Created by brandon3055 on 29/6/20
 */
public abstract class VBOBipedModel<T extends LivingEntity> extends BipedModel<T> {

    public VBOModelRender bipedHead;
    public VBOModelRender bipedBody;
    public VBOModelRender bipedRightArm;
    public VBOModelRender bipedLeftArm;
    public VBOModelRender bipedRightLeg;
    public VBOModelRender bipedLeftLeg;

    public VBOBipedModel(float modelSize) {
        super(modelSize);
    }

    protected VBOBipedModel(float modelSize, float yOffset, int textureWidth, int textureHeight) {
        super(modelSize, yOffset, textureWidth, textureHeight);
    }

    public VBOBipedModel(Function<ResourceLocation, RenderType> renderType, float modelSize, float yOffset, int textureWidth, int textureHeight) {
        super(renderType, modelSize, yOffset, textureWidth, textureHeight);
    }

    @Override
    public void render(MatrixStack mStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {}

    public abstract void render(MatrixStack mStack, IRenderTypeBuffer getter, T entity, ItemStack itemstack, int packedLight, int packedOverlay, float red, float green, float blue, float alpha);

    @Override
    public void setRotationAngles(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        boolean flag = entity.getTicksElytraFlying() > 4;
        boolean flag1 = entity.isActualySwimming();
        this.bipedHead.rotateAngleY = netHeadYaw * ((float)Math.PI / 180F);
        if (flag) {
            this.bipedHead.rotateAngleX = (-(float)Math.PI / 4F);
        } else if (this.swimAnimation > 0.0F) {
            if (flag1) {
                this.bipedHead.rotateAngleX = this.rotLerpRad(this.bipedHead.rotateAngleX, (-(float)Math.PI / 4F), this.swimAnimation);
            } else {
                this.bipedHead.rotateAngleX = this.rotLerpRad(this.bipedHead.rotateAngleX, headPitch * ((float)Math.PI / 180F), this.swimAnimation);
            }
        } else {
            this.bipedHead.rotateAngleX = headPitch * ((float)Math.PI / 180F);
        }

        this.bipedBody.rotateAngleY = 0.0F;
        this.bipedRightArm.rotationPointZ = 0.0F;
        this.bipedRightArm.rotationPointX = -5.0F;
        this.bipedLeftArm.rotationPointZ = 0.0F;
        this.bipedLeftArm.rotationPointX = 5.0F;
        float f = 1.0F;
        if (flag) {
            f = (float)entity.getMotion().lengthSquared();
            f = f / 0.2F;
            f = f * f * f;
        }

        if (f < 1.0F) {
            f = 1.0F;
        }

        this.bipedRightArm.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float)Math.PI) * 2.0F * limbSwingAmount * 0.5F / f;
        this.bipedLeftArm.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 2.0F * limbSwingAmount * 0.5F / f;
        this.bipedRightArm.rotateAngleZ = 0.0F;
        this.bipedLeftArm.rotateAngleZ = 0.0F;
        this.bipedRightLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount / f;
        this.bipedLeftLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount / f;
        this.bipedRightLeg.rotateAngleY = 0.0F;
        this.bipedLeftLeg.rotateAngleY = 0.0F;
        this.bipedRightLeg.rotateAngleZ = 0.0F;
        this.bipedLeftLeg.rotateAngleZ = 0.0F;
        if (this.isSitting) {
            this.bipedRightArm.rotateAngleX += (-(float)Math.PI / 5F);
            this.bipedLeftArm.rotateAngleX += (-(float)Math.PI / 5F);
            this.bipedRightLeg.rotateAngleX = -1.4137167F;
            this.bipedRightLeg.rotateAngleY = ((float)Math.PI / 10F);
            this.bipedRightLeg.rotateAngleZ = 0.07853982F;
            this.bipedLeftLeg.rotateAngleX = -1.4137167F;
            this.bipedLeftLeg.rotateAngleY = (-(float)Math.PI / 10F);
            this.bipedLeftLeg.rotateAngleZ = -0.07853982F;
        }

        this.bipedRightArm.rotateAngleY = 0.0F;
        this.bipedRightArm.rotateAngleZ = 0.0F;
        switch(this.leftArmPose) {
            case EMPTY:
                this.bipedLeftArm.rotateAngleY = 0.0F;
                break;
            case BLOCK:
                this.bipedLeftArm.rotateAngleX = this.bipedLeftArm.rotateAngleX * 0.5F - 0.9424779F;
                this.bipedLeftArm.rotateAngleY = ((float)Math.PI / 6F);
                break;
            case ITEM:
                this.bipedLeftArm.rotateAngleX = this.bipedLeftArm.rotateAngleX * 0.5F - ((float)Math.PI / 10F);
                this.bipedLeftArm.rotateAngleY = 0.0F;
        }

        switch(this.rightArmPose) {
            case EMPTY:
                this.bipedRightArm.rotateAngleY = 0.0F;
                break;
            case BLOCK:
                this.bipedRightArm.rotateAngleX = this.bipedRightArm.rotateAngleX * 0.5F - 0.9424779F;
                this.bipedRightArm.rotateAngleY = (-(float)Math.PI / 6F);
                break;
            case ITEM:
                this.bipedRightArm.rotateAngleX = this.bipedRightArm.rotateAngleX * 0.5F - ((float)Math.PI / 10F);
                this.bipedRightArm.rotateAngleY = 0.0F;
                break;
            case THROW_SPEAR:
                this.bipedRightArm.rotateAngleX = this.bipedRightArm.rotateAngleX * 0.5F - (float)Math.PI;
                this.bipedRightArm.rotateAngleY = 0.0F;
        }

        if (this.leftArmPose == BipedModel.ArmPose.THROW_SPEAR && this.rightArmPose != BipedModel.ArmPose.BLOCK && this.rightArmPose != BipedModel.ArmPose.THROW_SPEAR && this.rightArmPose != BipedModel.ArmPose.BOW_AND_ARROW) {
            this.bipedLeftArm.rotateAngleX = this.bipedLeftArm.rotateAngleX * 0.5F - (float)Math.PI;
            this.bipedLeftArm.rotateAngleY = 0.0F;
        }

        if (this.swingProgress > 0.0F) {
            HandSide handside = this.getMainHand(entity);
            ModelRenderer modelrenderer = this.getArmForSide(handside);
            float f1 = this.swingProgress;
            this.bipedBody.rotateAngleY = MathHelper.sin(MathHelper.sqrt(f1) * ((float)Math.PI * 2F)) * 0.2F;
            if (handside == HandSide.LEFT) {
                this.bipedBody.rotateAngleY *= -1.0F;
            }

            this.bipedRightArm.rotationPointZ = MathHelper.sin(this.bipedBody.rotateAngleY) * 5.0F;
            this.bipedRightArm.rotationPointX = -MathHelper.cos(this.bipedBody.rotateAngleY) * 5.0F;
            this.bipedLeftArm.rotationPointZ = -MathHelper.sin(this.bipedBody.rotateAngleY) * 5.0F;
            this.bipedLeftArm.rotationPointX = MathHelper.cos(this.bipedBody.rotateAngleY) * 5.0F;
            this.bipedRightArm.rotateAngleY += this.bipedBody.rotateAngleY;
            this.bipedLeftArm.rotateAngleY += this.bipedBody.rotateAngleY;
            this.bipedLeftArm.rotateAngleX += this.bipedBody.rotateAngleY;
            f1 = 1.0F - this.swingProgress;
            f1 = f1 * f1;
            f1 = f1 * f1;
            f1 = 1.0F - f1;
            float f2 = MathHelper.sin(f1 * (float)Math.PI);
            float f3 = MathHelper.sin(this.swingProgress * (float)Math.PI) * -(this.bipedHead.rotateAngleX - 0.7F) * 0.75F;
            modelrenderer.rotateAngleX = (float)((double)modelrenderer.rotateAngleX - ((double)f2 * 1.2D + (double)f3));
            modelrenderer.rotateAngleY += this.bipedBody.rotateAngleY * 2.0F;
            modelrenderer.rotateAngleZ += MathHelper.sin(this.swingProgress * (float)Math.PI) * -0.4F;
        }

        if (this.isSneak) {
            this.bipedBody.rotateAngleX = 0.5F;
            this.bipedRightArm.rotateAngleX += 0.4F;
            this.bipedLeftArm.rotateAngleX += 0.4F;
            this.bipedRightLeg.rotationPointZ = 4.0F;
            this.bipedLeftLeg.rotationPointZ = 4.0F;
            this.bipedRightLeg.rotationPointY = 12.2F;
            this.bipedLeftLeg.rotationPointY = 12.2F;
            this.bipedHead.rotationPointY = 4.2F;
            this.bipedBody.rotationPointY = 3.2F;
            this.bipedLeftArm.rotationPointY = 5.2F;
            this.bipedRightArm.rotationPointY = 5.2F;
        } else {
            this.bipedBody.rotateAngleX = 0.0F;
            this.bipedRightLeg.rotationPointZ = 0.1F;
            this.bipedLeftLeg.rotationPointZ = 0.1F;
            this.bipedRightLeg.rotationPointY = 12.0F;
            this.bipedLeftLeg.rotationPointY = 12.0F;
            this.bipedHead.rotationPointY = 0.0F;
            this.bipedBody.rotationPointY = 0.0F;
            this.bipedLeftArm.rotationPointY = 2.0F;
            this.bipedRightArm.rotationPointY = 2.0F;
        }

        this.bipedRightArm.rotateAngleZ += MathHelper.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
        this.bipedLeftArm.rotateAngleZ -= MathHelper.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
        this.bipedRightArm.rotateAngleX += MathHelper.sin(ageInTicks * 0.067F) * 0.05F;
        this.bipedLeftArm.rotateAngleX -= MathHelper.sin(ageInTicks * 0.067F) * 0.05F;
        if (this.rightArmPose == BipedModel.ArmPose.BOW_AND_ARROW) {
            this.bipedRightArm.rotateAngleY = -0.1F + this.bipedHead.rotateAngleY;
            this.bipedLeftArm.rotateAngleY = 0.1F + this.bipedHead.rotateAngleY + 0.4F;
            this.bipedRightArm.rotateAngleX = (-(float)Math.PI / 2F) + this.bipedHead.rotateAngleX;
            this.bipedLeftArm.rotateAngleX = (-(float)Math.PI / 2F) + this.bipedHead.rotateAngleX;
        } else if (this.leftArmPose == BipedModel.ArmPose.BOW_AND_ARROW && this.rightArmPose != BipedModel.ArmPose.THROW_SPEAR && this.rightArmPose != BipedModel.ArmPose.BLOCK) {
            this.bipedRightArm.rotateAngleY = -0.1F + this.bipedHead.rotateAngleY - 0.4F;
            this.bipedLeftArm.rotateAngleY = 0.1F + this.bipedHead.rotateAngleY;
            this.bipedRightArm.rotateAngleX = (-(float)Math.PI / 2F) + this.bipedHead.rotateAngleX;
            this.bipedLeftArm.rotateAngleX = (-(float)Math.PI / 2F) + this.bipedHead.rotateAngleX;
        }

        float f4 = (float) CrossbowItem.getChargeTime(entity.getActiveItemStack());
        if (this.rightArmPose == BipedModel.ArmPose.CROSSBOW_CHARGE) {
            this.bipedRightArm.rotateAngleY = -0.8F;
            this.bipedRightArm.rotateAngleX = -0.97079635F;
            this.bipedLeftArm.rotateAngleX = -0.97079635F;
            float f5 = MathHelper.clamp(this.remainingItemUseTime, 0.0F, f4);
            this.bipedLeftArm.rotateAngleY = MathHelper.lerp(f5 / f4, 0.4F, 0.85F);
            this.bipedLeftArm.rotateAngleX = MathHelper.lerp(f5 / f4, this.bipedLeftArm.rotateAngleX, (-(float)Math.PI / 2F));
        } else if (this.leftArmPose == BipedModel.ArmPose.CROSSBOW_CHARGE) {
            this.bipedLeftArm.rotateAngleY = 0.8F;
            this.bipedRightArm.rotateAngleX = -0.97079635F;
            this.bipedLeftArm.rotateAngleX = -0.97079635F;
            float f6 = MathHelper.clamp(this.remainingItemUseTime, 0.0F, f4);
            this.bipedRightArm.rotateAngleY = MathHelper.lerp(f6 / f4, -0.4F, -0.85F);
            this.bipedRightArm.rotateAngleX = MathHelper.lerp(f6 / f4, this.bipedRightArm.rotateAngleX, (-(float)Math.PI / 2F));
        }

        if (this.rightArmPose == BipedModel.ArmPose.CROSSBOW_HOLD && this.swingProgress <= 0.0F) {
            this.bipedRightArm.rotateAngleY = -0.3F + this.bipedHead.rotateAngleY;
            this.bipedLeftArm.rotateAngleY = 0.6F + this.bipedHead.rotateAngleY;
            this.bipedRightArm.rotateAngleX = (-(float)Math.PI / 2F) + this.bipedHead.rotateAngleX + 0.1F;
            this.bipedLeftArm.rotateAngleX = -1.5F + this.bipedHead.rotateAngleX;
        } else if (this.leftArmPose == BipedModel.ArmPose.CROSSBOW_HOLD) {
            this.bipedRightArm.rotateAngleY = -0.6F + this.bipedHead.rotateAngleY;
            this.bipedLeftArm.rotateAngleY = 0.3F + this.bipedHead.rotateAngleY;
            this.bipedRightArm.rotateAngleX = -1.5F + this.bipedHead.rotateAngleX;
            this.bipedLeftArm.rotateAngleX = (-(float)Math.PI / 2F) + this.bipedHead.rotateAngleX + 0.1F;
        }

        if (this.swimAnimation > 0.0F) {
            float f7 = limbSwing % 26.0F;
            float f8 = this.swingProgress > 0.0F ? 0.0F : this.swimAnimation;
            if (f7 < 14.0F) {
                this.bipedLeftArm.rotateAngleX = this.rotLerpRad(this.bipedLeftArm.rotateAngleX, 0.0F, this.swimAnimation);
                this.bipedRightArm.rotateAngleX = MathHelper.lerp(f8, this.bipedRightArm.rotateAngleX, 0.0F);
                this.bipedLeftArm.rotateAngleY = this.rotLerpRad(this.bipedLeftArm.rotateAngleY, (float)Math.PI, this.swimAnimation);
                this.bipedRightArm.rotateAngleY = MathHelper.lerp(f8, this.bipedRightArm.rotateAngleY, (float)Math.PI);
                this.bipedLeftArm.rotateAngleZ = this.rotLerpRad(this.bipedLeftArm.rotateAngleZ, (float)Math.PI + 1.8707964F * this.getArmAngleSq(f7) / this.getArmAngleSq(14.0F), this.swimAnimation);
                this.bipedRightArm.rotateAngleZ = MathHelper.lerp(f8, this.bipedRightArm.rotateAngleZ, (float)Math.PI - 1.8707964F * this.getArmAngleSq(f7) / this.getArmAngleSq(14.0F));
            } else if (f7 >= 14.0F && f7 < 22.0F) {
                float f10 = (f7 - 14.0F) / 8.0F;
                this.bipedLeftArm.rotateAngleX = this.rotLerpRad(this.bipedLeftArm.rotateAngleX, ((float)Math.PI / 2F) * f10, this.swimAnimation);
                this.bipedRightArm.rotateAngleX = MathHelper.lerp(f8, this.bipedRightArm.rotateAngleX, ((float)Math.PI / 2F) * f10);
                this.bipedLeftArm.rotateAngleY = this.rotLerpRad(this.bipedLeftArm.rotateAngleY, (float)Math.PI, this.swimAnimation);
                this.bipedRightArm.rotateAngleY = MathHelper.lerp(f8, this.bipedRightArm.rotateAngleY, (float)Math.PI);
                this.bipedLeftArm.rotateAngleZ = this.rotLerpRad(this.bipedLeftArm.rotateAngleZ, 5.012389F - 1.8707964F * f10, this.swimAnimation);
                this.bipedRightArm.rotateAngleZ = MathHelper.lerp(f8, this.bipedRightArm.rotateAngleZ, 1.2707963F + 1.8707964F * f10);
            } else if (f7 >= 22.0F && f7 < 26.0F) {
                float f9 = (f7 - 22.0F) / 4.0F;
                this.bipedLeftArm.rotateAngleX = this.rotLerpRad(this.bipedLeftArm.rotateAngleX, ((float)Math.PI / 2F) - ((float)Math.PI / 2F) * f9, this.swimAnimation);
                this.bipedRightArm.rotateAngleX = MathHelper.lerp(f8, this.bipedRightArm.rotateAngleX, ((float)Math.PI / 2F) - ((float)Math.PI / 2F) * f9);
                this.bipedLeftArm.rotateAngleY = this.rotLerpRad(this.bipedLeftArm.rotateAngleY, (float)Math.PI, this.swimAnimation);
                this.bipedRightArm.rotateAngleY = MathHelper.lerp(f8, this.bipedRightArm.rotateAngleY, (float)Math.PI);
                this.bipedLeftArm.rotateAngleZ = this.rotLerpRad(this.bipedLeftArm.rotateAngleZ, (float)Math.PI, this.swimAnimation);
                this.bipedRightArm.rotateAngleZ = MathHelper.lerp(f8, this.bipedRightArm.rotateAngleZ, (float)Math.PI);
            }

            float f11 = 0.3F;
            float f12 = 0.33333334F;
            this.bipedLeftLeg.rotateAngleX = MathHelper.lerp(this.swimAnimation, this.bipedLeftLeg.rotateAngleX, 0.3F * MathHelper.cos(limbSwing * 0.33333334F + (float)Math.PI));
            this.bipedRightLeg.rotateAngleX = MathHelper.lerp(this.swimAnimation, this.bipedRightLeg.rotateAngleX, 0.3F * MathHelper.cos(limbSwing * 0.33333334F));
        }

        this.bipedHeadwear.copyModelAngles(this.bipedHead);

        if (entity instanceof ArmorStandEntity) {
            setRotationAngles((ArmorStandEntity) entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
            return;
        }
    }

    @Override
    protected ModelRenderer getArmForSide(HandSide side) {
        return side == HandSide.LEFT ? this.bipedLeftArm : this.bipedRightArm;
    }


    private void setRotationAngles(ArmorStandEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.bipedHead.rotateAngleX = ((float)Math.PI / 180F) * entityIn.getHeadRotation().getX();
        this.bipedHead.rotateAngleY = ((float)Math.PI / 180F) * entityIn.getHeadRotation().getY();
        this.bipedHead.rotateAngleZ = ((float)Math.PI / 180F) * entityIn.getHeadRotation().getZ();
        this.bipedHead.setRotationPoint(0.0F, 1.0F, 0.0F);
        this.bipedBody.rotateAngleX = ((float)Math.PI / 180F) * entityIn.getBodyRotation().getX();
        this.bipedBody.rotateAngleY = ((float)Math.PI / 180F) * entityIn.getBodyRotation().getY();
        this.bipedBody.rotateAngleZ = ((float)Math.PI / 180F) * entityIn.getBodyRotation().getZ();
        this.bipedLeftArm.rotateAngleX = ((float)Math.PI / 180F) * entityIn.getLeftArmRotation().getX();
        this.bipedLeftArm.rotateAngleY = ((float)Math.PI / 180F) * entityIn.getLeftArmRotation().getY();
        this.bipedLeftArm.rotateAngleZ = ((float)Math.PI / 180F) * entityIn.getLeftArmRotation().getZ();
        this.bipedRightArm.rotateAngleX = ((float)Math.PI / 180F) * entityIn.getRightArmRotation().getX();
        this.bipedRightArm.rotateAngleY = ((float)Math.PI / 180F) * entityIn.getRightArmRotation().getY();
        this.bipedRightArm.rotateAngleZ = ((float)Math.PI / 180F) * entityIn.getRightArmRotation().getZ();
        this.bipedLeftLeg.rotateAngleX = ((float)Math.PI / 180F) * entityIn.getLeftLegRotation().getX();
        this.bipedLeftLeg.rotateAngleY = ((float)Math.PI / 180F) * entityIn.getLeftLegRotation().getY();
        this.bipedLeftLeg.rotateAngleZ = ((float)Math.PI / 180F) * entityIn.getLeftLegRotation().getZ();
        this.bipedLeftLeg.setRotationPoint(1.9F, 11.0F, 0.0F);
        this.bipedRightLeg.rotateAngleX = ((float)Math.PI / 180F) * entityIn.getRightLegRotation().getX();
        this.bipedRightLeg.rotateAngleY = ((float)Math.PI / 180F) * entityIn.getRightLegRotation().getY();
        this.bipedRightLeg.rotateAngleZ = ((float)Math.PI / 180F) * entityIn.getRightLegRotation().getZ();
        this.bipedRightLeg.setRotationPoint(-1.9F, 11.0F, 0.0F);
        this.bipedHeadwear.copyModelAngles(this.bipedHead);
    }

    private float getArmAngleSq(float limbSwing) {
        return -65.0F * limbSwing + limbSwing * limbSwing;
    }
}
