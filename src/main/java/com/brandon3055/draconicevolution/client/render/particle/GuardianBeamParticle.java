package com.brandon3055.draconicevolution.client.render.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuardianBeamParticle extends TextureSheetParticle {
   private final SpriteSet sprites;

   private GuardianBeamParticle(ClientLevel world, double x, double y, double z, double power, double my, double mz, SpriteSet animatedSprite) {
      super(world, x, y, z, 0.0D, 0.0D, 0.0D);
      this.sprites = animatedSprite;
      this.xd = 0;//*= (double)0.1F;
      this.yd = 0;//*= (double)0.1F;
      this.zd = 0;//*= (double)0.1F;
      float secondary = Math.max(0, (float) power - 1F);
      this.rCol = 1;
      this.gCol = 0;
      this.bCol = 0;
      if (secondary > 0) {
         rCol = 1 - secondary;
         this.gCol = secondary;
         this.bCol = secondary;
      }
      this.lifetime = 2 + world.random.nextInt(2);
      this.hasPhysics = false;
      this.setSpriteFromAge(animatedSprite);
      scale((float) Math.min(power, 1) * 4);
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

      public Particle createParticle(SimpleParticleType type, ClientLevel world, double x, double y, double z, double power, double p_199234_11_, double p_199234_13_) {
         return new GuardianBeamParticle(world, x, y, z, power, p_199234_11_, p_199234_13_, this.sprites);
      }
   }

}