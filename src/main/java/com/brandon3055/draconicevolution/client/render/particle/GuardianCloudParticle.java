package com.brandon3055.draconicevolution.client.render.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuardianCloudParticle extends TextureSheetParticle {
   private final SpriteSet sprites;

   private GuardianCloudParticle(ClientLevel p_i232414_1_, double x, double y, double z, double mx, double my, double mz, SpriteSet animatedSprite) {
      super(p_i232414_1_, x, y, z, 0.0D, 0.0D, 0.0D);
      this.sprites = animatedSprite;
      float f = 2.5F;
      this.xd *= (double)0.1F;
      this.yd *= (double)0.1F;
      this.zd *= (double)0.1F;
      this.xd += mx;
      this.yd += my;
      this.zd += mz;
      float f1 = 1.0F - (float)(Math.random() * (double)0.3F);
      if (mx == 0 && my ==  0 && mz == 0) {
         this.rCol = 0.5F * f1;//f1;
         this.gCol = 0;//f1;
         this.bCol = 1 * f1;//f1;
         scale(5);
      } else {
         this.rCol = f1;
         this.gCol = f1;
         this.bCol = f1;
         scale(10);
      }
      this.quadSize *= 1.875F;
      int i = (int)(8.0D / (Math.random() * 0.8D + 0.3D));
      this.lifetime = (int)Math.max((float)i * 2.5F, 1.0F);
      this.hasPhysics = false;
      this.setSpriteFromAge(animatedSprite);
   }

   @Override
   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
   }

   @Override
   public float getQuadSize(float partialTicks) {
      return this.quadSize * Mth.clamp(((float)this.age + partialTicks) / (float)this.lifetime * 32.0F, 0.0F, 1.0F);
   }

   @Override
   public void tick() {
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      if (this.age++ >= this.lifetime) {
         this.remove();
      } else {
         this.setSpriteFromAge(this.sprites);
         this.move(this.xd, this.yd, this.zd);
         this.xd *= (double)0.96F;
         this.yd *= (double)0.96F;
         this.zd *= (double)0.96F;
         Player playerentity = this.level.getNearestPlayer(this.x, this.y, this.z, 2.0D, false);
         if (playerentity != null) {
            double d0 = playerentity.getY();
            if (this.y > d0) {
               this.y += (d0 - this.y) * 0.2D;
               this.yd += (playerentity.getDeltaMovement().y - this.yd) * 0.2D;
               this.setPos(this.x, this.y, this.z);
            }
         }

         if (this.onGround) {
            this.xd *= (double)0.7F;
            this.zd *= (double)0.7F;
         }

      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class Factory implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprites;

      public Factory(SpriteSet p_i50630_1_) {
         this.sprites = p_i50630_1_;
      }

      public Particle createParticle(SimpleParticleType p_199234_1_, ClientLevel p_199234_2_, double p_199234_3_, double p_199234_5_, double p_199234_7_, double p_199234_9_, double p_199234_11_, double p_199234_13_) {
         return new GuardianCloudParticle(p_199234_2_, p_199234_3_, p_199234_5_, p_199234_7_, p_199234_9_, p_199234_11_, p_199234_13_, this.sprites);
      }
   }

}