package com.brandon3055.draconicevolution.client.render.particle;

import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BlinkParticle extends SpriteTexturedParticle {
   private BlinkParticle(ClientWorld world, double x, double y, double z, double motionX, double motionY, double motionZ) {
      super(world, x, y, z, motionX, motionY, motionZ);
      this.motionX = motionX;
      this.motionY = motionY;
      this.motionZ = motionZ;
      this.posX = x;
      this.posY = y;
      this.posZ = z;
      this.particleScale = 0.1F * (this.rand.nextFloat() * 0.2F + 0.5F) * 1.5F;
      float f = this.rand.nextFloat() * 0.6F + 0.4F;
      this.particleRed = f * 0.9F;
      this.particleGreen = f * 0.3F;
      this.particleBlue = f;
      this.maxAge = (int)(Math.random() * 2.0D) + 30;
   }

   public float getScale(float scaleFactor) {
      float f = 1.0F - ((float)this.age + scaleFactor) / ((float)this.maxAge * 1.5F);
      return this.particleScale * f;
   }


   public void tick() {
      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      if (this.age++ >= this.maxAge) {
         this.setExpired();
      } else {
         this.posX += this.motionX;
         this.posY += this.motionY;
         this.posZ += this.motionZ;
      }
   }

   @Override
   public IParticleRenderType getRenderType() {
      return IParticleRenderType.PARTICLE_SHEET_OPAQUE;
   }

   @OnlyIn(Dist.CLIENT)
   public static class Factory implements IParticleFactory<BasicParticleType> {
      private final IAnimatedSprite spriteSet;

      public Factory(IAnimatedSprite spriteSet) {
         this.spriteSet = spriteSet;
      }

      public Particle makeParticle(BasicParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
         BlinkParticle reverseportalparticle = new BlinkParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed);
         reverseportalparticle.selectSpriteRandomly(this.spriteSet);
         return reverseportalparticle;
      }
   }
}
