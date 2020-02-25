package com.brandon3055.draconicevolution.client.render.particle;

import com.brandon3055.brandonscore.client.particle.BCParticle;
import com.brandon3055.brandonscore.client.particle.IBCParticleFactory;
import com.brandon3055.brandonscore.lib.Vec3D;
import net.minecraft.client.particle.Particle;
import net.minecraft.world.World;

public class ParticleCustom extends BCParticle {

    public int maxFade = 0;
    public int fadeAge = 0;
    public float spawnAlpha = 0;

    public ParticleCustom(World worldIn, Vec3D pos, Vec3D speed) {
        super(worldIn, pos, speed);

        motionX = speed.x;
        motionY = speed.y;
        motionZ = speed.z;

        texturesPerRow = 4;
    }

//    @Override
//    public void onUpdate() {
//        prevPosX = posX;
//        prevPosY = posY;
//        prevPosZ = posZ;
//
//        if (particleAge++ >= particleMaxAge)
//        {
//            if (fadeAge++ >= maxFade)
//                setExpired();
//            if (maxFade > 0)
//                setAlphaF(spawnAlpha - spawnAlpha * ((float)fadeAge / (float)maxFade));
//        }
//
//        motionY -= particleGravity;
//        move(motionX, motionY, motionZ);
//    }
//
//    @Override
//    public boolean shouldDisableDepth() {
//        return true;
//    }

    public static class Factory implements IBCParticleFactory {

        @Override
        public Particle getEntityFX(int particleID, World world, Vec3D pos, Vec3D speed, int... args) {
            ParticleCustom particle = new ParticleCustom(world, pos, speed);

            if (args.length >= 10) {
                particle.setColour(args[0]/255f, args[1]/255f, args[2]/255f);
                particle.setAlphaF(args[3]/255f);
                particle.spawnAlpha = args[3]/255f;
//                particle.setScale(args[4]/10000F);
                particle.setMaxAge(args[5]);
                particle.setGravity(args[6]/10000D);
                particle.maxFade = args[7];
//                particle.particleTextureIndexX = args[8] % 4;
//                particle.particleTextureIndexY = args[8] / 4;
                particle.canCollide = args[9] == 1;
            }

            return particle;
        }
    }

}
