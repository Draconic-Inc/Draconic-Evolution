package com.brandon3055.draconicevolution.client.render.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.WitherAuraLayer;
import net.minecraft.client.renderer.entity.model.WitherModel;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuardianWitherRenderer extends MobRenderer<WitherEntity, WitherModel<WitherEntity>> {
   private static final ResourceLocation WITHER_INVULNERABLE_LOCATION = new ResourceLocation("textures/entity/wither/wither_invulnerable.png");
   private static final ResourceLocation WITHER_LOCATION = new ResourceLocation("textures/entity/wither/wither.png");

   public GuardianWitherRenderer(EntityRendererManager p_i46130_1_) {
      super(p_i46130_1_, new WitherModel<>(0.0F), 1.0F);
      this.addLayer(new WitherAuraLayer(this));
   }

   protected int getBlockLightLevel(WitherEntity p_225624_1_, BlockPos p_225624_2_) {
      return 15;
   }

   public ResourceLocation getTextureLocation(WitherEntity p_110775_1_) {
      int i = p_110775_1_.getInvulnerableTicks();
      return i > 0 && (i > 80 || i / 5 % 2 != 1) ? WITHER_INVULNERABLE_LOCATION : WITHER_LOCATION;
   }

   protected void scale(WitherEntity p_225620_1_, MatrixStack p_225620_2_, float p_225620_3_) {
      float f = 2.0F;
      int i = p_225620_1_.getInvulnerableTicks();
      if (i > 0) {
         f -= ((float)i - p_225620_3_) / 220.0F * 0.5F;
      }

      p_225620_2_.scale(f, f, f);
   }
}