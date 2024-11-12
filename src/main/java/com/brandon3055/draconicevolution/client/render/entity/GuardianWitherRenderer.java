package com.brandon3055.draconicevolution.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.WitherBossModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.WitherArmorLayer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuardianWitherRenderer extends MobRenderer<WitherBoss, WitherBossModel<WitherBoss>> {
   private static final ResourceLocation WITHER_INVULNERABLE_LOCATION = new ResourceLocation("textures/entity/wither/wither_invulnerable.png");
   private static final ResourceLocation WITHER_LOCATION = new ResourceLocation("textures/entity/wither/wither.png");

   public GuardianWitherRenderer(EntityRendererProvider.Context context) {
      super(context, new WitherBossModel<>(context.bakeLayer(ModelLayers.WITHER)), 1.0F);
      this.addLayer(new WitherArmorLayer(this, context.getModelSet()));
   }

   protected int getBlockLightLevel(WitherBoss p_225624_1_, BlockPos p_225624_2_) {
      return 15;
   }

   public ResourceLocation getTextureLocation(WitherBoss p_110775_1_) {
      int i = p_110775_1_.getInvulnerableTicks();
      return i > 0 && (i > 80 || i / 5 % 2 != 1) ? WITHER_INVULNERABLE_LOCATION : WITHER_LOCATION;
   }

   protected void scale(WitherBoss p_225620_1_, PoseStack p_225620_2_, float p_225620_3_) {
      float f = 2.0F;
      int i = p_225620_1_.getInvulnerableTicks();
      if (i > 0) {
         f -= ((float)i - p_225620_3_) / 220.0F * 0.5F;
      }

      p_225620_2_.scale(f, f, f);
   }
}