package com.brandon3055.draconicevolution.client.render.particle;

import com.brandon3055.brandonscore.client.particle.BCParticle;
import com.brandon3055.brandonscore.lib.Vec3D;
import net.minecraft.client.multiplayer.ClientLevel;

public class ParticleInfuser extends BCParticle {

    public Vec3D targetPos;
    public int type = 0;

    public ParticleInfuser(ClientLevel worldIn, Vec3D pos) {
        super(worldIn, pos);
    }

    public ParticleInfuser(ClientLevel worldIn, Vec3D pos, Vec3D targetPos) {
        super(worldIn, pos, new Vec3D(0, 0, 0));
        this.targetPos = targetPos;
//        this.particleMaxAge = 60;

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
//        if (particleTextureIndexY == 1) {
//            particleTextureIndexX = rand.nextInt(5);
//        }
//
//        Vec3D dir = Vec3D.getDirectionVec(new Vec3D(posX, posY, posZ), targetPos);
//        double speed = 0.01D;
//        motionX = dir.x * speed;
//        motionY = dir.y * speed;
//        motionZ = dir.z * speed;
//        moveEntityNoClip(motionX, motionY, motionZ);
//
//        if (type == 0) {
//            particleScale -= 0.01F;
//            if (Utils.getDistanceAtoB(posX, posY, posZ, targetPos.x, targetPos.y, targetPos.z) < 0.01) {
//                setExpired();
//            }
//        }
//        else {
//            if (particleAge < 1) {
//                particleScale = 2F;
//                particleAlpha = 0.2F;
//                particleTextureIndexY = 0;
//                particleTextureIndexX = 0;
//            }
//            else if (particleAge == 3) {
//                particleTextureIndexY = 1;
//                setScale(0.5F);
//                particleAlpha = 1F;
//            }
//
//            particleScale -= 0.01F;
//            if (Utils.getDistanceAtoB(posX, posY, posZ, targetPos.x, targetPos.y, targetPos.z) < 0.01) {
//                particleScale = 2F;
//                particleAlpha = 0.2F;
//                particleTextureIndexY = 0;
//                particleTextureIndexX = 0;
//            }
//        }
//
//        if (particleAge++ > particleMaxAge) {
//            setExpired();
//        }
//    }
//
//    @Override
//    //@OnlyIn(Dist.CLIENT)
//    public void renderParticle(BufferBuilder vertexbuffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
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
//    }
//
//    public static class Factory implements IBCParticleFactory {
//
//        @Override
//        public Particle getEntityFX(int particleID, World world, Vec3D pos, Vec3D speed, int... args) {
//            ParticleInfuser particleEnergy = new ParticleInfuser(world, pos, speed);
//
//            if (args.length >= 1) {
//                if (args[0] == 0) {
//                    particleEnergy.type = 0;
//                    particleEnergy.setRBGColorF(1, 0.2F, 0);
//                    particleEnergy.setScale(0.5F);
//                }
//                else {
//                    particleEnergy.type = 1;
//                    particleEnergy.setRBGColorF(0, 1, 1);
//                    particleEnergy.setScale(0.5F);
//                }
//
//
//            }
//
//            return particleEnergy;
//        }
//    }
}
