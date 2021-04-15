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
    public void renderToBuffer(MatrixStack mStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {}

    public abstract void render(MatrixStack mStack, IRenderTypeBuffer getter, T entity, ItemStack itemstack, int packedLight, int packedOverlay, float red, float green, float blue, float alpha);

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        boolean flag = entity.getFallFlyingTicks() > 4;
        boolean flag1 = entity.isVisuallySwimming();
        this.bipedHead.yRot = netHeadYaw * ((float)Math.PI / 180F);
        if (flag) {
            this.bipedHead.xRot = (-(float)Math.PI / 4F);
        } else if (this.swimAmount > 0.0F) {
            if (flag1) {
                this.bipedHead.xRot = this.rotlerpRad(this.bipedHead.xRot, (-(float)Math.PI / 4F), this.swimAmount);
            } else {
                this.bipedHead.xRot = this.rotlerpRad(this.bipedHead.xRot, headPitch * ((float)Math.PI / 180F), this.swimAmount);
            }
        } else {
            this.bipedHead.xRot = headPitch * ((float)Math.PI / 180F);
        }

        this.bipedBody.yRot = 0.0F;
        this.bipedRightArm.z = 0.0F;
        this.bipedRightArm.x = -5.0F;
        this.bipedLeftArm.z = 0.0F;
        this.bipedLeftArm.x = 5.0F;
        float f = 1.0F;
        if (flag) {
            f = (float)entity.getDeltaMovement().lengthSqr();
            f = f / 0.2F;
            f = f * f * f;
        }

        if (f < 1.0F) {
            f = 1.0F;
        }

        this.bipedRightArm.xRot = MathHelper.cos(limbSwing * 0.6662F + (float)Math.PI) * 2.0F * limbSwingAmount * 0.5F / f;
        this.bipedLeftArm.xRot = MathHelper.cos(limbSwing * 0.6662F) * 2.0F * limbSwingAmount * 0.5F / f;
        this.bipedRightArm.zRot = 0.0F;
        this.bipedLeftArm.zRot = 0.0F;
        this.bipedRightLeg.xRot = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount / f;
        this.bipedLeftLeg.xRot = MathHelper.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount / f;
        this.bipedRightLeg.yRot = 0.0F;
        this.bipedLeftLeg.yRot = 0.0F;
        this.bipedRightLeg.zRot = 0.0F;
        this.bipedLeftLeg.zRot = 0.0F;
        if (this.riding) {
            this.bipedRightArm.xRot += (-(float)Math.PI / 5F);
            this.bipedLeftArm.xRot += (-(float)Math.PI / 5F);
            this.bipedRightLeg.xRot = -1.4137167F;
            this.bipedRightLeg.yRot = ((float)Math.PI / 10F);
            this.bipedRightLeg.zRot = 0.07853982F;
            this.bipedLeftLeg.xRot = -1.4137167F;
            this.bipedLeftLeg.yRot = (-(float)Math.PI / 10F);
            this.bipedLeftLeg.zRot = -0.07853982F;
        }

        this.bipedRightArm.yRot = 0.0F;
        this.bipedRightArm.zRot = 0.0F;
        switch(this.leftArmPose) {
            case EMPTY:
                this.bipedLeftArm.yRot = 0.0F;
                break;
            case BLOCK:
                this.bipedLeftArm.xRot = this.bipedLeftArm.xRot * 0.5F - 0.9424779F;
                this.bipedLeftArm.yRot = ((float)Math.PI / 6F);
                break;
            case ITEM:
                this.bipedLeftArm.xRot = this.bipedLeftArm.xRot * 0.5F - ((float)Math.PI / 10F);
                this.bipedLeftArm.yRot = 0.0F;
        }

        switch(this.rightArmPose) {
            case EMPTY:
                this.bipedRightArm.yRot = 0.0F;
                break;
            case BLOCK:
                this.bipedRightArm.xRot = this.bipedRightArm.xRot * 0.5F - 0.9424779F;
                this.bipedRightArm.yRot = (-(float)Math.PI / 6F);
                break;
            case ITEM:
                this.bipedRightArm.xRot = this.bipedRightArm.xRot * 0.5F - ((float)Math.PI / 10F);
                this.bipedRightArm.yRot = 0.0F;
                break;
            case THROW_SPEAR:
                this.bipedRightArm.xRot = this.bipedRightArm.xRot * 0.5F - (float)Math.PI;
                this.bipedRightArm.yRot = 0.0F;
        }

        if (this.leftArmPose == BipedModel.ArmPose.THROW_SPEAR && this.rightArmPose != BipedModel.ArmPose.BLOCK && this.rightArmPose != BipedModel.ArmPose.THROW_SPEAR && this.rightArmPose != BipedModel.ArmPose.BOW_AND_ARROW) {
            this.bipedLeftArm.xRot = this.bipedLeftArm.xRot * 0.5F - (float)Math.PI;
            this.bipedLeftArm.yRot = 0.0F;
        }

        if (this.attackTime > 0.0F) {
            HandSide handside = this.getAttackArm(entity);
            ModelRenderer modelrenderer = this.getArm(handside);
            float f1 = this.attackTime;
            this.bipedBody.yRot = MathHelper.sin(MathHelper.sqrt(f1) * ((float)Math.PI * 2F)) * 0.2F;
            if (handside == HandSide.LEFT) {
                this.bipedBody.yRot *= -1.0F;
            }

            this.bipedRightArm.z = MathHelper.sin(this.bipedBody.yRot) * 5.0F;
            this.bipedRightArm.x = -MathHelper.cos(this.bipedBody.yRot) * 5.0F;
            this.bipedLeftArm.z = -MathHelper.sin(this.bipedBody.yRot) * 5.0F;
            this.bipedLeftArm.x = MathHelper.cos(this.bipedBody.yRot) * 5.0F;
            this.bipedRightArm.yRot += this.bipedBody.yRot;
            this.bipedLeftArm.yRot += this.bipedBody.yRot;
            this.bipedLeftArm.xRot += this.bipedBody.yRot;
            f1 = 1.0F - this.attackTime;
            f1 = f1 * f1;
            f1 = f1 * f1;
            f1 = 1.0F - f1;
            float f2 = MathHelper.sin(f1 * (float)Math.PI);
            float f3 = MathHelper.sin(this.attackTime * (float)Math.PI) * -(this.bipedHead.xRot - 0.7F) * 0.75F;
            modelrenderer.xRot = (float)((double)modelrenderer.xRot - ((double)f2 * 1.2D + (double)f3));
            modelrenderer.yRot += this.bipedBody.yRot * 2.0F;
            modelrenderer.zRot += MathHelper.sin(this.attackTime * (float)Math.PI) * -0.4F;
        }

        if (this.crouching) {
            this.bipedBody.xRot = 0.5F;
            this.bipedRightArm.xRot += 0.4F;
            this.bipedLeftArm.xRot += 0.4F;
            this.bipedRightLeg.z = 4.0F;
            this.bipedLeftLeg.z = 4.0F;
            this.bipedRightLeg.y = 12.2F;
            this.bipedLeftLeg.y = 12.2F;
            this.bipedHead.y = 4.2F;
            this.bipedBody.y = 3.2F;
            this.bipedLeftArm.y = 5.2F;
            this.bipedRightArm.y = 5.2F;
        } else {
            this.bipedBody.xRot = 0.0F;
            this.bipedRightLeg.z = 0.1F;
            this.bipedLeftLeg.z = 0.1F;
            this.bipedRightLeg.y = 12.0F;
            this.bipedLeftLeg.y = 12.0F;
            this.bipedHead.y = 0.0F;
            this.bipedBody.y = 0.0F;
            this.bipedLeftArm.y = 2.0F;
            this.bipedRightArm.y = 2.0F;
        }

        this.bipedRightArm.zRot += MathHelper.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
        this.bipedLeftArm.zRot -= MathHelper.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
        this.bipedRightArm.xRot += MathHelper.sin(ageInTicks * 0.067F) * 0.05F;
        this.bipedLeftArm.xRot -= MathHelper.sin(ageInTicks * 0.067F) * 0.05F;
        if (this.rightArmPose == BipedModel.ArmPose.BOW_AND_ARROW) {
            this.bipedRightArm.yRot = -0.1F + this.bipedHead.yRot;
            this.bipedLeftArm.yRot = 0.1F + this.bipedHead.yRot + 0.4F;
            this.bipedRightArm.xRot = (-(float)Math.PI / 2F) + this.bipedHead.xRot;
            this.bipedLeftArm.xRot = (-(float)Math.PI / 2F) + this.bipedHead.xRot;
        } else if (this.leftArmPose == BipedModel.ArmPose.BOW_AND_ARROW && this.rightArmPose != BipedModel.ArmPose.THROW_SPEAR && this.rightArmPose != BipedModel.ArmPose.BLOCK) {
            this.bipedRightArm.yRot = -0.1F + this.bipedHead.yRot - 0.4F;
            this.bipedLeftArm.yRot = 0.1F + this.bipedHead.yRot;
            this.bipedRightArm.xRot = (-(float)Math.PI / 2F) + this.bipedHead.xRot;
            this.bipedLeftArm.xRot = (-(float)Math.PI / 2F) + this.bipedHead.xRot;
        }

        float f4 = (float) CrossbowItem.getChargeDuration(entity.getUseItem());
        if (this.rightArmPose == BipedModel.ArmPose.CROSSBOW_CHARGE) {
            this.bipedRightArm.yRot = -0.8F;
            this.bipedRightArm.xRot = -0.97079635F;
            this.bipedLeftArm.xRot = -0.97079635F;
            float f5 = 0;//TODOMathHelper.clamp(this.remainingItemUseTime, 0.0F, f4);
            this.bipedLeftArm.yRot = MathHelper.lerp(f5 / f4, 0.4F, 0.85F);
            this.bipedLeftArm.xRot = MathHelper.lerp(f5 / f4, this.bipedLeftArm.xRot, (-(float)Math.PI / 2F));
        } else if (this.leftArmPose == BipedModel.ArmPose.CROSSBOW_CHARGE) {
            this.bipedLeftArm.yRot = 0.8F;
            this.bipedRightArm.xRot = -0.97079635F;
            this.bipedLeftArm.xRot = -0.97079635F;
            float f6 = 0;//TODOMathHelper.clamp(this.remainingItemUseTime, 0.0F, f4);
            this.bipedRightArm.yRot = MathHelper.lerp(f6 / f4, -0.4F, -0.85F);
            this.bipedRightArm.xRot = MathHelper.lerp(f6 / f4, this.bipedRightArm.xRot, (-(float)Math.PI / 2F));
        }

        if (this.rightArmPose == BipedModel.ArmPose.CROSSBOW_HOLD && this.attackTime <= 0.0F) {
            this.bipedRightArm.yRot = -0.3F + this.bipedHead.yRot;
            this.bipedLeftArm.yRot = 0.6F + this.bipedHead.yRot;
            this.bipedRightArm.xRot = (-(float)Math.PI / 2F) + this.bipedHead.xRot + 0.1F;
            this.bipedLeftArm.xRot = -1.5F + this.bipedHead.xRot;
        } else if (this.leftArmPose == BipedModel.ArmPose.CROSSBOW_HOLD) {
            this.bipedRightArm.yRot = -0.6F + this.bipedHead.yRot;
            this.bipedLeftArm.yRot = 0.3F + this.bipedHead.yRot;
            this.bipedRightArm.xRot = -1.5F + this.bipedHead.xRot;
            this.bipedLeftArm.xRot = (-(float)Math.PI / 2F) + this.bipedHead.xRot + 0.1F;
        }

        if (this.swimAmount > 0.0F) {
            float f7 = limbSwing % 26.0F;
            float f8 = this.attackTime > 0.0F ? 0.0F : this.swimAmount;
            if (f7 < 14.0F) {
                this.bipedLeftArm.xRot = this.rotlerpRad(this.bipedLeftArm.xRot, 0.0F, this.swimAmount);
                this.bipedRightArm.xRot = MathHelper.lerp(f8, this.bipedRightArm.xRot, 0.0F);
                this.bipedLeftArm.yRot = this.rotlerpRad(this.bipedLeftArm.yRot, (float)Math.PI, this.swimAmount);
                this.bipedRightArm.yRot = MathHelper.lerp(f8, this.bipedRightArm.yRot, (float)Math.PI);
                this.bipedLeftArm.zRot = this.rotlerpRad(this.bipedLeftArm.zRot, (float)Math.PI + 1.8707964F * this.getArmAngleSq(f7) / this.getArmAngleSq(14.0F), this.swimAmount);
                this.bipedRightArm.zRot = MathHelper.lerp(f8, this.bipedRightArm.zRot, (float)Math.PI - 1.8707964F * this.getArmAngleSq(f7) / this.getArmAngleSq(14.0F));
            } else if (f7 >= 14.0F && f7 < 22.0F) {
                float f10 = (f7 - 14.0F) / 8.0F;
                this.bipedLeftArm.xRot = this.rotlerpRad(this.bipedLeftArm.xRot, ((float)Math.PI / 2F) * f10, this.swimAmount);
                this.bipedRightArm.xRot = MathHelper.lerp(f8, this.bipedRightArm.xRot, ((float)Math.PI / 2F) * f10);
                this.bipedLeftArm.yRot = this.rotlerpRad(this.bipedLeftArm.yRot, (float)Math.PI, this.swimAmount);
                this.bipedRightArm.yRot = MathHelper.lerp(f8, this.bipedRightArm.yRot, (float)Math.PI);
                this.bipedLeftArm.zRot = this.rotlerpRad(this.bipedLeftArm.zRot, 5.012389F - 1.8707964F * f10, this.swimAmount);
                this.bipedRightArm.zRot = MathHelper.lerp(f8, this.bipedRightArm.zRot, 1.2707963F + 1.8707964F * f10);
            } else if (f7 >= 22.0F && f7 < 26.0F) {
                float f9 = (f7 - 22.0F) / 4.0F;
                this.bipedLeftArm.xRot = this.rotlerpRad(this.bipedLeftArm.xRot, ((float)Math.PI / 2F) - ((float)Math.PI / 2F) * f9, this.swimAmount);
                this.bipedRightArm.xRot = MathHelper.lerp(f8, this.bipedRightArm.xRot, ((float)Math.PI / 2F) - ((float)Math.PI / 2F) * f9);
                this.bipedLeftArm.yRot = this.rotlerpRad(this.bipedLeftArm.yRot, (float)Math.PI, this.swimAmount);
                this.bipedRightArm.yRot = MathHelper.lerp(f8, this.bipedRightArm.yRot, (float)Math.PI);
                this.bipedLeftArm.zRot = this.rotlerpRad(this.bipedLeftArm.zRot, (float)Math.PI, this.swimAmount);
                this.bipedRightArm.zRot = MathHelper.lerp(f8, this.bipedRightArm.zRot, (float)Math.PI);
            }

            float f11 = 0.3F;
            float f12 = 0.33333334F;
            this.bipedLeftLeg.xRot = MathHelper.lerp(this.swimAmount, this.bipedLeftLeg.xRot, 0.3F * MathHelper.cos(limbSwing * 0.33333334F + (float)Math.PI));
            this.bipedRightLeg.xRot = MathHelper.lerp(this.swimAmount, this.bipedRightLeg.xRot, 0.3F * MathHelper.cos(limbSwing * 0.33333334F));
        }

        this.hat.copyFrom(this.bipedHead);

        if (entity instanceof ArmorStandEntity) {
            setRotationAngles((ArmorStandEntity) entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
            return;
        }
    }

    @Override
    protected ModelRenderer getArm(HandSide side) {
        return side == HandSide.LEFT ? this.bipedLeftArm : this.bipedRightArm;
    }


    private void setRotationAngles(ArmorStandEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.bipedHead.xRot = ((float)Math.PI / 180F) * entityIn.getHeadPose().getX();
        this.bipedHead.yRot = ((float)Math.PI / 180F) * entityIn.getHeadPose().getY();
        this.bipedHead.zRot = ((float)Math.PI / 180F) * entityIn.getHeadPose().getZ();
        this.bipedHead.setPos(0.0F, 1.0F, 0.0F);
        this.bipedBody.xRot = ((float)Math.PI / 180F) * entityIn.getBodyPose().getX();
        this.bipedBody.yRot = ((float)Math.PI / 180F) * entityIn.getBodyPose().getY();
        this.bipedBody.zRot = ((float)Math.PI / 180F) * entityIn.getBodyPose().getZ();
        this.bipedLeftArm.xRot = ((float)Math.PI / 180F) * entityIn.getLeftArmPose().getX();
        this.bipedLeftArm.yRot = ((float)Math.PI / 180F) * entityIn.getLeftArmPose().getY();
        this.bipedLeftArm.zRot = ((float)Math.PI / 180F) * entityIn.getLeftArmPose().getZ();
        this.bipedRightArm.xRot = ((float)Math.PI / 180F) * entityIn.getRightArmPose().getX();
        this.bipedRightArm.yRot = ((float)Math.PI / 180F) * entityIn.getRightArmPose().getY();
        this.bipedRightArm.zRot = ((float)Math.PI / 180F) * entityIn.getRightArmPose().getZ();
        this.bipedLeftLeg.xRot = ((float)Math.PI / 180F) * entityIn.getLeftLegPose().getX();
        this.bipedLeftLeg.yRot = ((float)Math.PI / 180F) * entityIn.getLeftLegPose().getY();
        this.bipedLeftLeg.zRot = ((float)Math.PI / 180F) * entityIn.getLeftLegPose().getZ();
        this.bipedLeftLeg.setPos(1.9F, 11.0F, 0.0F);
        this.bipedRightLeg.xRot = ((float)Math.PI / 180F) * entityIn.getRightLegPose().getX();
        this.bipedRightLeg.yRot = ((float)Math.PI / 180F) * entityIn.getRightLegPose().getY();
        this.bipedRightLeg.zRot = ((float)Math.PI / 180F) * entityIn.getRightLegPose().getZ();
        this.bipedRightLeg.setPos(-1.9F, 11.0F, 0.0F);
        this.hat.copyFrom(this.bipedHead);
    }

    private float getArmAngleSq(float limbSwing) {
        return -65.0F * limbSwing + limbSwing * limbSwing;
    }
}
