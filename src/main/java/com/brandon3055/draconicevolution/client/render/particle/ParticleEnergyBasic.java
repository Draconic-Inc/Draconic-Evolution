package com.brandon3055.draconicevolution.client.render.particle;

import com.brandon3055.brandonscore.client.particle.IntParticleData;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;

import java.util.List;

public class ParticleEnergyBasic extends TextureSheetParticle {

    private final SpriteSet spriteSet;

    public ParticleEnergyBasic(ClientLevel world, double xPos, double yPos, double zPos, SpriteSet spriteSet) {
        super(world, xPos, yPos, zPos);
        this.spriteSet = spriteSet;
        setSprite(spriteSet.get(world.random));
        hasPhysics = false;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public void tick() {
        super.tick();
    }

    public static class Factory implements ParticleProvider<IntParticleData> {
        private final SpriteSet spriteSet;

        public Factory(SpriteSet p_i50823_1_) {
            this.spriteSet = p_i50823_1_;
        }

        @Override
        public Particle createParticle(IntParticleData data, ClientLevel world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            ParticleEnergyBasic particleEnergy = new ParticleEnergyBasic(world, x, y, z, spriteSet);
            particleEnergy.xd = xSpeed;
            particleEnergy.yd = ySpeed;
            particleEnergy.zd = zSpeed;

            List<Integer> list = data.get();
            if (list.size() >= 3) {
                particleEnergy.setColor(list.get(0) / 255F, list.get(1) / 255F, list.get(2) / 255F);
            }

            if (list.size() >= 4) {
                particleEnergy.scale(list.get(3) / 100F);
            }

            return particleEnergy;
        }
    }
}
