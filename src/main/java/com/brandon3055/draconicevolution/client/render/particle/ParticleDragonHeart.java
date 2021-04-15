package com.brandon3055.draconicevolution.client.render.particle;

import com.brandon3055.brandonscore.client.particle.BCParticle;
import com.brandon3055.brandonscore.client.particle.IBCParticleFactory;
import com.brandon3055.brandonscore.lib.Vec3D;
import com.brandon3055.brandonscore.utils.Utils;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.world.World;

public class ParticleDragonHeart extends BCParticle {

    public Vec3D targetPos;
    public double startDist = 1;

    public ParticleDragonHeart(ClientWorld worldIn, Vec3D pos) {
        super(worldIn, pos);
    }

    public ParticleDragonHeart(ClientWorld worldIn, Vec3D pos, Vec3D targetPos) {
        super(worldIn, pos, new Vec3D(0, 0, 0));
        this.targetPos = targetPos;
        this.startDist = Utils.getDistanceAtoB(pos, targetPos);
//        this.particleMaxAge = 3000;
//        this.particleScale = 0F;
        this.texturesPerRow = 8F;
    }

//    @Override
//    public boolean shouldDisableDepth() {
//        return true;
//    }
//
//    @Override
//    public void onUpdate() {
//        this.prevPosX = this.posX;
//        this.prevPosY = this.posY;
//        this.prevPosZ = this.posZ;
//
//        //particleTextureIndexX = rand.nextInt(5);
//
//        double dist = Utils.getDistanceAtoB(new Vec3D(posX, posY, posZ), targetPos);
//        double progress = (dist / startDist);
//        particleScale = (float) progress;
//
//        Vec3D dir = Vec3D.getDirectionVec(new Vec3D(posX, posY, posZ), targetPos);
//        double speed = 0.5D * progress;
//        motionX = dir.x * speed;
//        motionY = dir.y * speed;
//        motionZ = dir.z * speed;
//        moveEntityNoClip(motionX, motionY, motionZ);
//
//        if (particleAge++ > particleMaxAge || Utils.getDistanceAtoB(posX, posY, posZ, targetPos.x, targetPos.y, targetPos.z) < 0.01) {
//            setExpired();
//        }
//    }
//
//    @Override
//    //@OnlyIn(Dist.CLIENT)
//    public void renderParticle(BufferBuilder vertexbuffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
//        super.renderParticle(vertexbuffer, entityIn, partialTicks, rotationX, rotationZ, rotationYZ, rotationXY, rotationXZ);
//    }

    public static class Factory implements IBCParticleFactory {

        @Override
        public Particle getEntityFX(int particleID, World world, Vec3D pos, Vec3D speed, int... args) {
            ParticleDragonHeart particleHeart = new ParticleDragonHeart((ClientWorld)world, pos, speed);

            if (args.length >= 3) {
//                particleHeart.setRBGColorF(args[0] / 255F, args[1] / 255F, args[2] / 255F);
            }

            if (args.length >= 4) {
                pos.add(0, 0.5, 0);
                particleHeart.remove();
                Vec3D playerDir = Vec3D.getDirectionVec(pos, speed);
                double dist = Utils.getDistanceAtoB(pos, speed);

                for (int i = 0; i < 100; i++) {
                    double d = dist * (1D / 100D);

                    double randX = world.random.nextDouble() - 0.5D;
                    double randY = world.random.nextDouble() - 0.5D;
                    double randZ = world.random.nextDouble() - 0.5D;
                    //TODO Particles
//                    BCEffectHandler.spawnFX(DEParticles.LINE_INDICATOR, world, pos.add(playerDir.x * d, playerDir.y * d, playerDir.z * d), new Vec3D(randX * 0.05F, randY * 0.05F, randZ * 0.05F), 512D, 255, 0, 0);
                }
            }

            return particleHeart;
        }
    }
}
