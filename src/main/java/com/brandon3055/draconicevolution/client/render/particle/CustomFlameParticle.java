package com.brandon3055.draconicevolution.client.render.particle;

import com.brandon3055.brandonscore.client.particle.IntParticleType.IntParticleData;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CustomFlameParticle extends TextureSheetParticle {
//    private float drag =

    private CustomFlameParticle(ClientLevel worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn) {
        super(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
        this.xd = this.xd * (double)0.01F + xSpeedIn;
        this.yd = this.yd * (double)0.01F + ySpeedIn;
        this.zd = this.zd * (double)0.01F + zSpeedIn;
        this.x += (double)((this.random.nextFloat() - this.random.nextFloat()) * 0.05F);
        this.y += (double)((this.random.nextFloat() - this.random.nextFloat()) * 0.05F);
        this.z += (double)((this.random.nextFloat() - this.random.nextFloat()) * 0.05F);
        this.lifetime = (int)(8.0D / (Math.random() * 0.8D + 0.2D)) + 4;
    }

    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    public void move(double x, double y, double z) {
        this.setBoundingBox(this.getBoundingBox().move(x, y, z));
        this.setLocationFromBoundingbox();
    }

    public float getQuadSize(float p_217561_1_) {
        float f = ((float)this.age + p_217561_1_) / (float)this.lifetime;
        return this.quadSize * (1.0F - f * f * 0.5F);
    }

    public int getLightColor(float partialTick) {
        float f = ((float)this.age + partialTick) / (float)this.lifetime;
        f = Mth.clamp(f, 0.0F, 1.0F);
        int i = super.getLightColor(partialTick);
        int j = i & 255;
        int k = i >> 16 & 255;
        j = j + (int)(f * 15.0F * 16.0F);
        if (j > 240) {
            j = 240;
        }

        return j | k << 16;
    }

    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime) {
            this.remove();
        } else {
            this.yd -= 0.04D * (double)this.gravity;
            this.move(this.xd, this.yd, this.zd);
//            this.motionX *= (double)0.96F;
            this.yd *= (double)0.96F;
//            this.motionZ *= (double)0.96F;
            if (this.onGround) {
//                this.motionX *= (double)0.7F;
//                this.motionZ *= (double)0.7F;
            }

        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class Factory implements ParticleProvider<IntParticleData> {
        private final SpriteSet spriteSet;

        public Factory(SpriteSet p_i50823_1_) {
            this.spriteSet = p_i50823_1_;
        }

        @Override
        public Particle createParticle(IntParticleData data, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            CustomFlameParticle flameparticle = new CustomFlameParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed);
            flameparticle.pickSprite(this.spriteSet);
            if (data.get().length >= 1) {
                flameparticle.quadSize *= (data.get()[0] / 255F);
            }
            if (data.get().length >= 2) {
                flameparticle.gravity = -data.get()[1] / 255F;
            }
            return flameparticle;
        }
    }
}