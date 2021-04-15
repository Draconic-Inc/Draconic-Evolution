package com.brandon3055.draconicevolution.client.render.particle;

import com.brandon3055.brandonscore.client.particle.BCParticle;
import com.brandon3055.brandonscore.client.particle.IBCParticleFactory;
import com.brandon3055.brandonscore.lib.Vec3D;
import com.brandon3055.brandonscore.utils.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.world.World;

/**
 * Created by brandon3055 on 17/07/2016.
 */
public class ParticlePortal extends BCParticle {

    public Vec3D target;
    public Vec3D start;
    public float baseScale;

    public ParticlePortal(ClientWorld worldIn, Vec3D pos, Vec3D target) {
        super(worldIn, pos, new Vec3D(0, 0, 0));
        this.start = pos;
        this.target = target;
        float speed = 0.12F + (random.nextFloat() * 0.2F);
        this.xd = (target.x - start.x) * speed;
        this.yd = (target.y - start.y) * speed;
        this.zd = (target.z - start.z) * speed;
//        this.particleMaxAge = 120;
//        this.particleRed = this.particleGreen = this.particleBlue = 1.0f;
//        float baseSize = 0.05F + ((float) Minecraft.getInstance().player.getDistance(pos.x, pos.y, pos.z)) * 0.007F;
//        this.baseScale = baseSize + (rand.nextFloat() * (baseSize * 2F));
//        this.particleScale = 0;
//        texturesPerRow = 8F;
//        particleTextureIndexX = 6;
    }

//    @Override
//    public void onUpdate() {
//        double distToTarget = Utils.getDistanceAtoB(new Vec3D(posX, posY, posZ), target);
//
//        if (particleAge >= particleMaxAge || distToTarget < 0.15) {
//            setExpired();
//        }
//
//        double startDist = Utils.getDistanceAtoB(start, target);
//
//        particleScale = ((float) (distToTarget / startDist)) * baseScale;
//
//        particleAge++;
//        prevPosX = posX;
//        prevPosY = posY;
//        prevPosZ = posZ;
//        moveEntityNoClip(motionX, motionY, motionZ);
//    }

    public static class Factory implements IBCParticleFactory {

        @Override
        public Particle getEntityFX(int particleID, World world, Vec3D pos, Vec3D speed, int... args) {
            return new ParticlePortal((ClientWorld)world, pos, speed);
        }
    }
}
