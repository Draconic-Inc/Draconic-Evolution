package com.brandon3055.draconicevolution.client.render.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SimpleAnimatedParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.Random;

@OnlyIn(Dist.CLIENT)
public class GuardianProjectileParticle extends SimpleAnimatedParticle {

    private GuardianProjectileParticle(ClientLevel world, double x, double y, double z, double motionX, double motionY, double motionZ, SpriteSet spriteWithAge) {
        super(world, x, y, z, spriteWithAge, -0.004F);
        this.xd = motionX;
        this.yd = motionY;
        this.zd = motionZ;
        this.quadSize *= 0.75F;
        this.lifetime = 48 + this.random.nextInt(12);
        this.setSpriteFromAge(spriteWithAge);
    }

    public void render(VertexConsumer buffer, Camera camera, float partialTicks) {
        if (this.age < this.lifetime / 3 || (this.age + this.lifetime) / 3 % 2 == 0) {
            super.render(buffer, camera, partialTicks);
        }
    }

    public void tick() {
        super.tick();
    }

    @OnlyIn(Dist.CLIENT)
    public static class Factory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;
        private static Random rand = new Random();

        public Factory(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            GuardianProjectileParticle particle = new GuardianProjectileParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
            particle.hasPhysics = false;
            particle.setLifetime(15 + rand.nextInt(5));
            float ci = 0.5F + (rand.nextFloat() * 0.5F);
            particle.setColor(1F, 0.6F * ci, 0.06F * ci);
            return particle;
        }
    }
}