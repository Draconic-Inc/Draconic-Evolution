package com.brandon3055.draconicevolution.client.render.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BlinkParticle extends TextureSheetParticle {
   private BlinkParticle(ClientLevel world, double x, double y, double z, double motionX, double motionY, double motionZ) {
      super(world, x, y, z, motionX, motionY, motionZ);
      this.xd = motionX;
      this.yd = motionY;
      this.zd = motionZ;
      this.x = x;
      this.y = y;
      this.z = z;
      this.quadSize = 0.1F * (this.random.nextFloat() * 0.2F + 0.5F) * 1.5F;
      float f = this.random.nextFloat() * 0.6F + 0.4F;
      this.rCol = f * 0.9F;
      this.gCol = f * 0.3F;
      this.bCol = f;
      this.lifetime = (int)(Math.random() * 2.0D) + 30;
   }

   public float getQuadSize(float scaleFactor) {
      float f = 1.0F - ((float)this.age + scaleFactor) / ((float)this.lifetime * 1.5F);
      return this.quadSize * f;
   }


   public void tick() {
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      if (this.age++ >= this.lifetime) {
         this.remove();
      } else {
         this.x += this.xd;
         this.y += this.yd;
         this.z += this.zd;
      }
   }

   @Override
   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
   }

   @OnlyIn(Dist.CLIENT)
   public static class Factory implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet spriteSet;

      public Factory(SpriteSet spriteSet) {
         this.spriteSet = spriteSet;
      }

      public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
         BlinkParticle reverseportalparticle = new BlinkParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed);
         reverseportalparticle.pickSprite(this.spriteSet);
         return reverseportalparticle;
      }
   }
}
