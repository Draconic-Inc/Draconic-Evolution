package com.brandon3055.draconicevolution.client.render.particle;

import com.brandon3055.brandonscore.client.particle.BCParticle;
import com.brandon3055.brandonscore.client.particle.IBCParticleFactory;
import com.brandon3055.brandonscore.lib.Vec3D;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.world.level.Level;

public class ParticleSoulExtraction extends BCParticle {

    public Vec3D targetPos;
    public int clusterSize = 0;

    public ParticleSoulExtraction(ClientLevel worldIn, Vec3D pos) {
        super(worldIn, pos);
    }

    public ParticleSoulExtraction(ClientLevel worldIn, Vec3D pos, Vec3D targetPos) {
        super(worldIn, pos, new Vec3D(0, 0, 0));
        this.targetPos = targetPos;
//        this.particleMaxAge = 3000;
//        this.particleScale = 3F;
//        this.particleTextureIndexY = 1;
        this.setColour(0, 0, 0);
        this.xd = (random.nextFloat() - 0.5F) * 0.4F;
        this.yd = (random.nextFloat() - 0.5F) * 0.4F;
        this.zd = (random.nextFloat() - 0.5F) * 0.4F;
    }

//    @Override
//    public boolean shouldDisableDepth() {
//        return true;
//    }

//    @Override
//    public void onUpdate() {
//        if (clusterSize > 0) {
//            for (; clusterSize > 0; clusterSize--) {
//                ParticleSoulExtraction particleSoulExtraction = new ParticleSoulExtraction(world, new Vec3D(posX, posY, posZ), targetPos);
//                particleSoulExtraction.setColour(particleRed, particleGreen, particleBlue);
//                BCEffectHandler.spawnFXDirect(DEParticles.DE_SHEET, particleSoulExtraction);
//            }
//        }
//
//        this.prevPosX = this.posX;
//        this.prevPosY = this.posY;
//        this.prevPosZ = this.posZ;
//
//        particleTextureIndexX = rand.nextInt(5);
//
//        Vec3D pos = new Vec3D(posX, posY, posZ);
//        Vec3D dir = Vec3D.getDirectionVec(pos, targetPos);
//        double distModifier = Math.min(Math.max(Utils.getDistanceAtoB(pos, targetPos), 1), 30);
//        motionX *= 0.95;
//        motionY *= 0.95;
//        motionZ *= 0.95;
//
//        double velocity = Math.abs(motionX) + Math.abs(motionY) + Math.abs(motionZ);
//        double speed = 0.05D * (1 - velocity) * distModifier;
//
//        motionX += dir.x * speed;
//        motionY += dir.y * speed;
//        motionZ += dir.z * speed;
//        moveEntityNoClip(motionX, motionY, motionZ);
//
//        if (particleAge++ > particleMaxAge || Utils.getDistanceAtoB(posX, posY, posZ, targetPos.x, targetPos.y, targetPos.z) < 0.5) {
//            setExpired();
//        }
//    }
//
//    @Override
//    //@OnlyIn(Dist.CLIENT)
//    public void renderParticle(BufferBuilder vertexbuffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
//
//        float minU = (float) this.particleTextureIndexX / 8.0F;
//        float maxU = minU + 0.125F;
//        float minV = (float) this.particleTextureIndexY / 8.0F;
//        float maxV = minV + 0.125F;
//        float scale = 0.1F * this.particleScale;
//
//        float renderX = (float) (this.prevPosX + (this.posX - this.prevPosX) * (double) partialTicks - interpPosX);
//        float renderY = (float) (this.prevPosY + (this.posY - this.prevPosY) * (double) partialTicks - interpPosY);
//        float renderZ = (float) (this.prevPosZ + (this.posZ - this.prevPosZ) * (double) partialTicks - interpPosZ);
//        int brightnessForRender = this.getBrightnessForRender(partialTicks);
//        int j = brightnessForRender >> 16 & 65535;
//        int k = brightnessForRender & 65535;
//        vertexbuffer.pos((double) (renderX - rotationX * scale - rotationXY * scale), (double) (renderY - rotationZ * scale), (double) (renderZ - rotationYZ * scale - rotationXZ * scale)).tex((double) maxU, (double) maxV).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
//        vertexbuffer.pos((double) (renderX - rotationX * scale + rotationXY * scale), (double) (renderY + rotationZ * scale), (double) (renderZ - rotationYZ * scale + rotationXZ * scale)).tex((double) maxU, (double) minV).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
//        vertexbuffer.pos((double) (renderX + rotationX * scale + rotationXY * scale), (double) (renderY + rotationZ * scale), (double) (renderZ + rotationYZ * scale + rotationXZ * scale)).tex((double) minU, (double) minV).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
//        vertexbuffer.pos((double) (renderX + rotationX * scale - rotationXY * scale), (double) (renderY - rotationZ * scale), (double) (renderZ + rotationYZ * scale - rotationXZ * scale)).tex((double) minU, (double) maxV).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
//
//    }

    public static class Factory implements IBCParticleFactory {

        @Override
        public Particle getEntityFX(int particleID, Level world, Vec3D pos, Vec3D speed, int... args) {
            ParticleSoulExtraction particleSoulExtraction = new ParticleSoulExtraction((ClientLevel)world, pos, speed);
            if (args.length > 0) {
                particleSoulExtraction.clusterSize = args[0];
                if (args.length >= 4) {
                    particleSoulExtraction.setColour(args[1] / 255F, args[2] / 255F, args[3] / 255F);
                }
            }
            return particleSoulExtraction;
        }
    }
}
