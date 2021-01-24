package com.brandon3055.draconicevolution.client.render.particle;

import com.brandon3055.brandonscore.client.ClientProxy;
import com.brandon3055.brandonscore.client.particle.IntParticleType;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.world.World;

/**
 * Created by brandon3055 on 2/5/2016.
 * The particle used to render the beams on the Energy Core
 */
public class ParticleLineIndicator extends SpriteTexturedParticle {
    public ParticleLineIndicator(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn) {
        super((ClientWorld)worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
        this.motionX = xSpeedIn;
        this.motionY = ySpeedIn;
        this.motionZ = zSpeedIn;
    }

    @Override
    public IParticleRenderType getRenderType() {
        return ClientProxy.PARTICLE_NO_DEPTH_NO_LIGHT;
    }

    public static class Factory implements IParticleFactory<IntParticleType.IntParticleData> {
        private final IAnimatedSprite spriteSet;

        public Factory(IAnimatedSprite p_i50823_1_) {
            this.spriteSet = p_i50823_1_;
        }

        @Override
        public Particle makeParticle(IntParticleType.IntParticleData data, ClientWorld world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            ParticleLineIndicator particle = new ParticleLineIndicator(world, x, y, z, xSpeed, ySpeed, zSpeed);
            particle.selectSpriteRandomly(spriteSet);

            if (data.get().length >= 3) {
                particle.setColor(data.get()[0] / 255F, data.get()[1] / 255F, data.get()[2] / 255F);
            }
            if (data.get().length >= 4) {
                particle.setMaxAge(data.get()[3]);
            }

            return particle;
        }
    }
}
