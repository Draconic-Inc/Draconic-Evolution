package com.brandon3055.draconicevolution.client.render.entity;

import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.entity.guardian.GuardianProjectileEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

@OnlyIn(Dist.CLIENT)
public class GuardianProjectileRenderer extends EntityRenderer<GuardianProjectileEntity> {
   private static final ResourceLocation DRAGON_FIREBALL_TEXTURE = new ResourceLocation(DraconicEvolution.MODID, "textures/entity/guardian_fireball.png");
   private static final RenderType RENDER_TYPE = RenderType.entityCutoutNoCull(DRAGON_FIREBALL_TEXTURE);

   public GuardianProjectileRenderer(EntityRendererProvider.Context context) {
      super(context);
   }

   @Override
   protected int getBlockLightLevel(GuardianProjectileEntity entityIn, BlockPos partialTicks) {
      return 15;
   }

   @Override
   public void render(GuardianProjectileEntity entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
      matrixStackIn.pushPose();
      matrixStackIn.scale(2.0F, 2.0F, 2.0F);
      matrixStackIn.mulPose(this.entityRenderDispatcher.cameraOrientation());
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
      PoseStack.Pose matrixstack$entry = matrixStackIn.last();
      Matrix4f matrix4f = matrixstack$entry.pose();
      Matrix3f matrix3f = matrixstack$entry.normal();
      VertexConsumer ivertexbuilder = bufferIn.getBuffer(RENDER_TYPE);
      vertex(ivertexbuilder, matrix4f, matrix3f, packedLightIn, 0.0F, 0, 0, 1);
      vertex(ivertexbuilder, matrix4f, matrix3f, packedLightIn, 1.0F, 0, 1, 1);
      vertex(ivertexbuilder, matrix4f, matrix3f, packedLightIn, 1.0F, 1, 1, 0);
      vertex(ivertexbuilder, matrix4f, matrix3f, packedLightIn, 0.0F, 1, 0, 0);
      matrixStackIn.popPose();
      super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
   }

   private static void vertex(VertexConsumer p_229045_0_, Matrix4f p_229045_1_, Matrix3f p_229045_2_, int p_229045_3_, float p_229045_4_, int p_229045_5_, int p_229045_6_, int p_229045_7_) {
      p_229045_0_.vertex(p_229045_1_, p_229045_4_ - 0.5F, (float)p_229045_5_ - 0.25F, 0.0F).color(255, 255, 255, 255).uv((float)p_229045_6_, (float)p_229045_7_).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(p_229045_3_).normal(p_229045_2_, 0.0F, 1.0F, 0.0F).endVertex();
   }

   @Override
   public ResourceLocation getTextureLocation(GuardianProjectileEntity entity) {
      return DRAGON_FIREBALL_TEXTURE;
   }
}
