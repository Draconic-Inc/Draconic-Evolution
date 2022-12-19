package com.brandon3055.draconicevolution.client.render.entity;

import com.brandon3055.draconicevolution.DEConfig;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.client.DEShaders;
import com.brandon3055.draconicevolution.entity.GuardianCrystalEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EnderDragonRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuardianCrystalRenderer extends EntityRenderer<GuardianCrystalEntity> {
    private static ResourceLocation ENDER_CRYSTAL_TEXTURES = new ResourceLocation(DraconicEvolution.MODID, "textures/entity/guardian_crystal.png");
    private static RenderType RENDER_TYPE = RenderType.entityCutoutNoCull(ENDER_CRYSTAL_TEXTURES);
    private static final float SIN_45 = (float) Math.sin((Math.PI / 4D));
    private final ModelPart cube;
    private final ModelPart glass;
    private final ModelPart base;

    public GuardianCrystalRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 0.5F;
        ModelPart modelpart = context.bakeLayer(ModelLayers.END_CRYSTAL);
        this.glass = modelpart.getChild("glass");
        this.cube = modelpart.getChild("cube");
        this.base = modelpart.getChild("base");
    }

    @Override
    public void render(GuardianCrystalEntity crystal, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffers, int packedLight) {
        poseStack.pushPose();
        float yBob = getY(crystal, partialTicks);
        float anim = ((float) crystal.time + partialTicks) * 3.0F;
        VertexConsumer vertexconsumer = buffers.getBuffer(RENDER_TYPE);
        poseStack.scale(2.0F, 2.0F, 2.0F);
        poseStack.translate(0.0D, -0.5D, 0.0D);
        int overlayTex = OverlayTexture.NO_OVERLAY;
        if (crystal.showsBottom()) {
            this.base.render(poseStack, vertexconsumer, packedLight, overlayTex);
        }

        poseStack.mulPose(Vector3f.YP.rotationDegrees(anim));
        poseStack.translate(0.0D, 1.5F + yBob / 2.0F, 0.0D);
        poseStack.mulPose(new Quaternion(new Vector3f(SIN_45, 0.0F, SIN_45), 60.0F, true));
        this.glass.render(poseStack, vertexconsumer, packedLight, overlayTex);
        float scale = 0.875F;
        poseStack.scale(scale, scale, scale);
        poseStack.mulPose(new Quaternion(new Vector3f(SIN_45, 0.0F, SIN_45), 60.0F, true));
        poseStack.mulPose(Vector3f.YP.rotationDegrees(anim));
        this.glass.render(poseStack, vertexconsumer, packedLight, overlayTex);
        poseStack.scale(scale, scale, scale);
        poseStack.mulPose(new Quaternion(new Vector3f(SIN_45, 0.0F, SIN_45), 60.0F, true));
        poseStack.mulPose(Vector3f.YP.rotationDegrees(anim));
        this.cube.render(poseStack, vertexconsumer, packedLight, overlayTex);
        poseStack.popPose();

        float shieldPower = crystal.getShieldPower() / (float) Math.max(20, DEConfig.guardianCrystalShield);
        if (shieldPower > 0) {
            DEShaders.shieldBarMode.glUniform1i(0);
            DEShaders.shieldColour.glUniform4f(1F, 0F, 0F, 1.5F * shieldPower);
            DEShaders.shieldActivation.glUniform1f(1F);
            VertexConsumer shaderBuilder = buffers.getBuffer(DraconicGuardianRenderer.SHIELD_TYPE);

            poseStack.pushPose();
            poseStack.scale(2.0F, 2.0F, 2.0F);
            poseStack.translate(0.0D, -0.5D, 0.0D);
            if (crystal.showsBottom()) {
                this.base.render(poseStack, shaderBuilder, packedLight, overlayTex);
            }
            poseStack.mulPose(Vector3f.YP.rotationDegrees(anim));
            poseStack.translate(0.0D, 1.5F + yBob / 2.0F, 0.0D);
            poseStack.mulPose(new Quaternion(new Vector3f(SIN_45, 0.0F, SIN_45), 60.0F, true));
            this.glass.render(poseStack, shaderBuilder, packedLight, overlayTex);
            poseStack.scale(0.875F, 0.875F, 0.875F);
            poseStack.mulPose(new Quaternion(new Vector3f(SIN_45, 0.0F, SIN_45), 60.0F, true));
            poseStack.mulPose(Vector3f.YP.rotationDegrees(anim));
            this.glass.render(poseStack, shaderBuilder, packedLight, overlayTex);
            poseStack.scale(0.875F, 0.875F, 0.875F);
            poseStack.mulPose(new Quaternion(new Vector3f(SIN_45, 0.0F, SIN_45), 60.0F, true));
            poseStack.mulPose(Vector3f.YP.rotationDegrees(anim));
            this.cube.render(poseStack, shaderBuilder, packedLight, overlayTex);
            poseStack.popPose();
        }

        BlockPos blockpos = crystal.getBeamTarget();
        if (blockpos != null) {
            float targetX = (float) blockpos.getX() + 0.5F;
            float targetY = (float) blockpos.getY() + 0.5F;
            float targetZ = (float) blockpos.getZ() + 0.5F;
            float xRel = (float) ((double) targetX - crystal.getX());
            float yRel = (float) ((double) targetY - crystal.getY());
            float zRel = (float) ((double) targetZ - crystal.getZ());
            poseStack.translate(xRel, yRel - 2, zRel);

            float beamPower = crystal.getBeamPower();
            if (beamPower < 1) {
                DraconicGuardianRenderer.renderBeam(-xRel, -yRel + yBob + 2, -zRel, partialTicks, crystal.time, poseStack, buffers, packedLight, beamPower);
            } else {
                DraconicGuardianRenderer.renderBeam(-xRel, -yRel + yBob + 2, -zRel, partialTicks, crystal.time, poseStack, buffers, packedLight);
            }
        }

        super.render(crystal, entityYaw, partialTicks, poseStack, buffers, packedLight);
    }

    public static float getY(GuardianCrystalEntity crystal, float partialTicks) {
        float f = (float) crystal.time + partialTicks;
        float f1 = Mth.sin(f * 0.2F) / 2.0F + 0.5F;
        f1 = (f1 * f1 + f1) * 0.4F;
        return f1 - 1.4F;
    }

    @Override
    public ResourceLocation getTextureLocation(GuardianCrystalEntity entity) {
        return ENDER_CRYSTAL_TEXTURES;
    }

    @Override
    public boolean shouldRender(GuardianCrystalEntity entity, Frustum camera, double camX, double camY, double camZ) {
        return super.shouldRender(entity, camera, camX, camY, camZ) || entity.getBeamTarget() != null;
    }
}
