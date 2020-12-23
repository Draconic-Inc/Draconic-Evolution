package com.brandon3055.draconicevolution.client.render.particle;

import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Random;

@OnlyIn(Dist.CLIENT)
public class GuardianProjectileParticle extends SimpleAnimatedParticle {

    private GuardianProjectileParticle(ClientWorld world, double x, double y, double z, double motionX, double motionY, double motionZ, IAnimatedSprite spriteWithAge) {
        super(world, x, y, z, spriteWithAge, -0.004F);
        this.motionX = motionX;
        this.motionY = motionY;
        this.motionZ = motionZ;
        this.particleScale *= 0.75F;
        this.maxAge = 48 + this.rand.nextInt(12);
        this.selectSpriteWithAge(spriteWithAge);
    }

    public void renderParticle(IVertexBuilder buffer, ActiveRenderInfo renderInfo, float partialTicks) {
        if (this.age < this.maxAge / 3 || (this.age + this.maxAge) / 3 % 2 == 0) {
            super.renderParticle(buffer, renderInfo, partialTicks);
        }
    }

    public void tick() {
        super.tick();
    }

    @OnlyIn(Dist.CLIENT)
    public static class Factory implements IParticleFactory<BasicParticleType> {
        private final IAnimatedSprite spriteSet;
        private static Random rand = new Random();

        public Factory(IAnimatedSprite spriteSet) {
            this.spriteSet = spriteSet;
        }

        public Particle makeParticle(BasicParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            GuardianProjectileParticle particle = new GuardianProjectileParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
            particle.canCollide = false;
            particle.setMaxAge(15 + rand.nextInt(5));
            float ci = 0.5F + (rand.nextFloat() * 0.5F);
            particle.setColor(1F, 0.6F * ci, 0.06F * ci);
            return particle;
        }
    }
}