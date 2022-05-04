package com.brandon3055.draconicevolution.client.render.entity;

import codechicken.lib.render.shader.ShaderRenderType;
import codechicken.lib.render.shader.UniformCache;
import com.brandon3055.draconicevolution.DEConfig;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.entity.GuardianCrystalEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuardianCrystalRenderer extends EntityRenderer<GuardianCrystalEntity> {
   private static ResourceLocation ENDER_CRYSTAL_TEXTURES = new ResourceLocation(DraconicEvolution.MODID, "textures/entity/guardian_crystal.png");
   private static RenderType RENDER_TYPE = RenderType.entityCutoutNoCull(ENDER_CRYSTAL_TEXTURES);
   private static final float SIN_45 = (float)Math.sin((Math.PI / 4D));
   private final ModelRenderer cube;
   private final ModelRenderer glass;
   private final ModelRenderer base;

   public GuardianCrystalRenderer(EntityRendererManager renderManagerIn) {
      super(renderManagerIn);
      this.shadowRadius = 0.5F;
      this.glass = new ModelRenderer(64, 32, 0, 0);
      this.glass.addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F);
      this.cube = new ModelRenderer(64, 32, 32, 0);
      this.cube.addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F);
      this.base = new ModelRenderer(64, 32, 0, 16);
      this.base.addBox(-6.0F, 0.0F, -6.0F, 12.0F, 4.0F, 12.0F);
   }

   @Override
   public void render(GuardianCrystalEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer getter, int packedLightIn) {
      matrixStackIn.pushPose();
      float yBob = getY(entityIn, partialTicks);
      float f1 = ((float)entityIn.innerRotation + partialTicks) * 3.0F;
      IVertexBuilder ivertexbuilder = getter.getBuffer(RENDER_TYPE);
      matrixStackIn.pushPose();
      matrixStackIn.scale(2.0F, 2.0F, 2.0F);
      matrixStackIn.translate(0.0D, -0.5D, 0.0D);
      int i = OverlayTexture.NO_OVERLAY;
      if (entityIn.shouldShowBottom()) {
         this.base.render(matrixStackIn, ivertexbuilder, packedLightIn, i);
      }

      matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(f1));
      matrixStackIn.translate(0.0D, (double)(1.5F + yBob / 2.0F), 0.0D);
      matrixStackIn.mulPose(new Quaternion(new Vector3f(SIN_45, 0.0F, SIN_45), 60.0F, true));
      this.glass.render(matrixStackIn, ivertexbuilder, packedLightIn, i);
      matrixStackIn.scale(0.875F, 0.875F, 0.875F);
      matrixStackIn.mulPose(new Quaternion(new Vector3f(SIN_45, 0.0F, SIN_45), 60.0F, true));
      matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(f1));
      this.glass.render(matrixStackIn, ivertexbuilder, packedLightIn, i);
      matrixStackIn.scale(0.875F, 0.875F, 0.875F);
      matrixStackIn.mulPose(new Quaternion(new Vector3f(SIN_45, 0.0F, SIN_45), 60.0F, true));
      matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(f1));
      this.cube.render(matrixStackIn, ivertexbuilder, packedLightIn, i);
      matrixStackIn.popPose();

      float shieldPower = entityIn.getShieldPower() / (float) Math.max(20, DEConfig.guardianCrystalShield);
      if (shieldPower > 0) {
         UniformCache uniforms = DraconicGuardianRenderer.shieldShader.pushCache();
         uniforms.glUniform4f("baseColour", 1F, 0F, 0F, 1.5F * shieldPower);
         IVertexBuilder shaderBuilder = getter.getBuffer(new ShaderRenderType(DraconicGuardianRenderer.shieldType, DraconicGuardianRenderer.shieldShader, uniforms));

         matrixStackIn.pushPose();
         matrixStackIn.scale(2.0F, 2.0F, 2.0F);
         matrixStackIn.translate(0.0D, -0.5D, 0.0D);
         if (entityIn.shouldShowBottom()) {
            this.base.render(matrixStackIn, shaderBuilder, packedLightIn, i);
         }
         matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(f1));
         matrixStackIn.translate(0.0D, 1.5F + yBob / 2.0F, 0.0D);
         matrixStackIn.mulPose(new Quaternion(new Vector3f(SIN_45, 0.0F, SIN_45), 60.0F, true));
         this.glass.render(matrixStackIn, shaderBuilder, packedLightIn, i);
         matrixStackIn.scale(0.875F, 0.875F, 0.875F);
         matrixStackIn.mulPose(new Quaternion(new Vector3f(SIN_45, 0.0F, SIN_45), 60.0F, true));
         matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(f1));
         this.glass.render(matrixStackIn, shaderBuilder, packedLightIn, i);
         matrixStackIn.scale(0.875F, 0.875F, 0.875F);
         matrixStackIn.mulPose(new Quaternion(new Vector3f(SIN_45, 0.0F, SIN_45), 60.0F, true));
         matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(f1));
         this.cube.render(matrixStackIn, shaderBuilder, packedLightIn, i);
         matrixStackIn.popPose();

      }
      matrixStackIn.popPose();

      BlockPos blockpos = entityIn.getBeamTarget();
      if (blockpos != null) {
         float posX = (float)blockpos.getX() + 0.5F;
         float posY = (float)blockpos.getY() + 0.5F;
         float posZ = (float)blockpos.getZ() + 0.5F;
         float relX = (float)((double)posX - entityIn.getX());
         float relY = (float)((double)posY - entityIn.getY());
         float relZ = (float)((double)posZ - entityIn.getZ());
         //Translate to target
         matrixStackIn.translate(relX, relY - 2, relZ);
         //Render from target to self
         float beamPower = entityIn.getBeamPower();
         if (beamPower < 1) {
            DraconicGuardianRenderer.renderBeam(-relX, -relY + yBob + 2, -relZ, partialTicks, entityIn.innerRotation, matrixStackIn, getter, packedLightIn, beamPower);
         }else {
            DraconicGuardianRenderer.renderBeam(-relX, -relY + yBob + 2, -relZ, partialTicks, entityIn.innerRotation, matrixStackIn, getter, packedLightIn);
         }
      }

      super.render(entityIn, entityYaw, partialTicks, matrixStackIn, getter, packedLightIn);
   }

   public static float getY(GuardianCrystalEntity p_229051_0_, float p_229051_1_) {
      float f = (float)p_229051_0_.innerRotation + p_229051_1_;
      float f1 = MathHelper.sin(f * 0.2F) / 2.0F + 0.5F;
      f1 = (f1 * f1 + f1) * 0.4F;
      return f1 - 1.4F;
   }

   @Override
   public ResourceLocation getTextureLocation(GuardianCrystalEntity entity) {
      return ENDER_CRYSTAL_TEXTURES;
   }

   @Override
   public boolean shouldRender(GuardianCrystalEntity livingEntityIn, ClippingHelper camera, double camX, double camY, double camZ) {
      return super.shouldRender(livingEntityIn, camera, camX, camY, camZ) || livingEntityIn.getBeamTarget() != null;
   }
}
