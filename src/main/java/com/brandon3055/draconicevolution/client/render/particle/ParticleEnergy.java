package com.brandon3055.draconicevolution.client.render.particle;

import com.brandon3055.brandonscore.client.particle.IntParticleType;
import com.brandon3055.brandonscore.lib.Vec3D;
import com.brandon3055.brandonscore.utils.Utils;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;

public class ParticleEnergy extends SpriteTexturedParticle {

    public Vec3D targetPos;
    private final IAnimatedSprite spriteSet;

    public ParticleEnergy(ClientWorld world, double xPos, double yPos, double zPos, Vec3D targetPos, IAnimatedSprite spriteSet) {
        super(world, xPos, yPos, zPos);
        this.targetPos = targetPos;
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
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        setSprite(spriteSet.get(level.random));

        Vec3D dir = Vec3D.getDirectionVec(new Vec3D(x, y, z), targetPos);
        double speed = 0.5D;
        xd = dir.x * speed;
        yd = dir.y * speed;
        zd = dir.z * speed;
        this.move(this.xd, this.yd, this.zd);

        if (age++ > lifetime || Utils.getDistanceAtoB(x, y, z, targetPos.x, targetPos.y, targetPos.z) < 0.5) {
            remove();
        }
    }

    public static class Factory implements IParticleFactory<IntParticleType.IntParticleData> {
        private final IAnimatedSprite spriteSet;

        public Factory(IAnimatedSprite p_i50823_1_) {
            this.spriteSet = p_i50823_1_;
        }

        @Override
        public Particle createParticle(IntParticleType.IntParticleData data, ClientWorld world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            ParticleEnergy particleEnergy = new ParticleEnergy(world, x, y, z, new Vec3D(xSpeed, ySpeed, zSpeed), spriteSet);

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
