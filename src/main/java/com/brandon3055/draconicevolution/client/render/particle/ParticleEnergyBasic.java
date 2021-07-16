package com.brandon3055.draconicevolution.client.render.particle;

import com.brandon3055.brandonscore.client.particle.IntParticleType;
import com.brandon3055.brandonscore.lib.Vec3D;
import com.brandon3055.brandonscore.utils.Utils;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;

public class ParticleEnergyBasic extends SpriteTexturedParticle {

    private final IAnimatedSprite spriteSet;

    public ParticleEnergyBasic(ClientWorld world, double xPos, double yPos, double zPos, IAnimatedSprite spriteSet) {
        super(world, xPos, yPos, zPos);
        this.spriteSet = spriteSet;
        setSprite(spriteSet.get(world.random));
        hasPhysics = false;
    }

    @Override
    public IParticleRenderType getRenderType() {
        return IParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public void tick() {
        super.tick();
    }

    public static class Factory implements IParticleFactory<IntParticleType.IntParticleData> {
        private final IAnimatedSprite spriteSet;

        public Factory(IAnimatedSprite p_i50823_1_) {
            this.spriteSet = p_i50823_1_;
        }

        @Override
        public Particle createParticle(IntParticleType.IntParticleData data, ClientWorld world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            ParticleEnergyBasic particleEnergy = new ParticleEnergyBasic(world, x, y, z, spriteSet);
            particleEnergy.xd = xSpeed;
            particleEnergy.yd = ySpeed;
            particleEnergy.zd = zSpeed;

            if (data.get().length >= 3) {
                particleEnergy.setColor(data.get()[0] / 255F, data.get()[1] / 255F, data.get()[2] / 255F);
            }

            if (data.get().length >= 4) {
                particleEnergy.scale(data.get()[3] / 100F);
            }

            return particleEnergy;
        }
    }
}
