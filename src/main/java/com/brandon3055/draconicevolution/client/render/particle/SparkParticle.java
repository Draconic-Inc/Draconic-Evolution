package com.brandon3055.draconicevolution.client.render.particle;

import com.brandon3055.brandonscore.client.particle.IntParticleData;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;

import java.util.List;

public class SparkParticle extends TextureSheetParticle {
    private final SpriteSet spriteSet;
    public float sparkSize = 0.5F;
    public float baseSize = 1;


    public SparkParticle(ClientLevel level, double xPos, double yPos, double zPos, double xVel, double yVel, double zVel, SpriteSet spriteSet) {
        super(level, xPos, yPos, zPos);
        this.spriteSet = spriteSet;
        setSprite(spriteSet.get(random));

        this.xd = (-0.5 + random.nextDouble()) * xVel;
        this.yd = (-0.5 + random.nextDouble()) * yVel;
        this.zd = (-0.5 + random.nextDouble()) * zVel;
        this.lifetime = 10 + level.random.nextInt(10);
        hasPhysics = false;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleEnergyCoreFX.PARTICLE_NO_DEPTH_NO_LIGHT;
    }

    @Override
    public void tick() {
        super.tick();

        setSprite(spriteSet.get(random));

        int ttd = lifetime - age;
        if (ttd < 10) {
            quadSize = (ttd / 10F) * baseSize;
        }
        if (ttd <= 1) {
            quadSize = sparkSize;
        }
    }

    public static class Factory implements ParticleProvider<IntParticleData> {
        private final SpriteSet spriteSet;

        public Factory(SpriteSet set) {
            this.spriteSet = set;
        }

        @Override
        public Particle createParticle(IntParticleData data, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            SparkParticle particle = new SparkParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, spriteSet);

            List<Integer> list = data.get();
            int params = list.size();

            //Colour
            if (params >= 3) {
                particle.setColor(list.get(0) / 255F, list.get(1) / 255F, list.get(2) / 255F);
                params -= 3;
            }

            //Scale
            if (params >= 1) {
                particle.baseSize = list.get(3) / 1000F;
                particle.quadSize = particle.baseSize;
                params--;
            }

            //Spark Scale
            if (params >= 1) {
                particle.sparkSize = list.get(4) / 1000F;
                params--;
            }

            //Max Age
            if (params >= 1) {
                particle.lifetime = list.get(5);
                params--;
            }

            //Random age augment
            if (params >= 1) {
                int max = list.get(6);
                particle.lifetime += max > 0 ? level.random.nextInt(max) : 0;
                params--;
            }

            //Gravity
            if (params >= 1) {
                particle.gravity = list.get(7) / 1000F;
                params--;
            }

            //Friction
            if (params >= 1) {
                particle.friction = list.get(8) / 1000F;
                params--;
            }

            //Set velocity directly
            if (params >= 1) {
                particle.setParticleSpeed(xSpeed, ySpeed, zSpeed);
            }

            return particle;
        }
    }
}