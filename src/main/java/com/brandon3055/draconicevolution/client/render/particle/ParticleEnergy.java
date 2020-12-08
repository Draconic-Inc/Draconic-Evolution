package com.brandon3055.draconicevolution.client.render.particle;

import com.brandon3055.brandonscore.client.particle.BCParticle;
import com.brandon3055.brandonscore.client.particle.IBCParticleFactory;
import com.brandon3055.brandonscore.client.particle.IntParticleType;
import com.brandon3055.brandonscore.lib.Vec3D;
import com.brandon3055.brandonscore.utils.Utils;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class ParticleEnergy extends SpriteTexturedParticle {

    public Vec3D targetPos;
    private final IAnimatedSprite spriteSet;

    public ParticleEnergy(ClientWorld world, double xPos, double yPos, double zPos, Vec3D targetPos, IAnimatedSprite spriteSet) {
        super(world, xPos, yPos, zPos);
        this.targetPos = targetPos;
        this.spriteSet = spriteSet;
        setSprite(spriteSet.get(world.rand));
        canCollide = false;
    }

    @Override
    public IParticleRenderType getRenderType() {
        return IParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    //    public ParticleEnergy(World worldIn, Vec3D pos) {
//        super(worldIn, pos);
//    }
//
//    public ParticleEnergy(World worldIn, Vec3D pos, Vec3D targetPos) {
//        super(worldIn, pos, new Vec3D(0, 0, 0));
//        this.targetPos = targetPos;
////        this.particleMaxAge = 3000;
////        this.particleScale = 1F;
////        this.particleTextureIndexY = 1;
//    }

//    @Override
//    public boolean shouldDisableDepth() {
//        return true;
//    }

    @Override
    public void tick() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        setSprite(spriteSet.get(world.rand));

        Vec3D dir = Vec3D.getDirectionVec(new Vec3D(posX, posY, posZ), targetPos);
        double speed = 0.5D;
        motionX = dir.x * speed;
        motionY = dir.y * speed;
        motionZ = dir.z * speed;
        this.move(this.motionX, this.motionY, this.motionZ);

        if (age++ > maxAge || Utils.getDistanceAtoB(posX, posY, posZ, targetPos.x, targetPos.y, targetPos.z) < 0.5) {
            setExpired();
        }
    }


//    @Override
//    public void onUpdate() {
//        this.prevPosX = this.posX;
//        this.prevPosY = this.posY;
//        this.prevPosZ = this.posZ;
//
//        particleTextureIndexX = rand.nextInt(5);
//
//        Vec3D dir = Vec3D.getDirectionVec(new Vec3D(posX, posY, posZ), targetPos);
//        double speed = 0.5D;
//        motionX = dir.x * speed;
//        motionY = dir.y * speed;
//        motionZ = dir.z * speed;
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

    public static class Factory implements IParticleFactory<IntParticleType.IntParticleData> {
        private final IAnimatedSprite spriteSet;

        public Factory(IAnimatedSprite p_i50823_1_) {
            this.spriteSet = p_i50823_1_;
        }

        @Override
        public Particle makeParticle(IntParticleType.IntParticleData data, ClientWorld world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            ParticleEnergy particleEnergy = new ParticleEnergy(world, x, y, z, new Vec3D(xSpeed, ySpeed, zSpeed), spriteSet);

            if (data.get().length >= 3) {
                particleEnergy.setColor(data.get()[0] / 255F, data.get()[1] / 255F, data.get()[2] / 255F);
            }

            if (data.get().length >= 4) {
                particleEnergy.multiplyParticleScaleBy(data.get()[3] / 100F);
            }

            return particleEnergy;
        }
    }
}
