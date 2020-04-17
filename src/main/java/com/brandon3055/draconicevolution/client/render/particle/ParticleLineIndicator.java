package com.brandon3055.draconicevolution.client.render.particle;

import com.brandon3055.brandonscore.client.particle.BCParticle;
import com.brandon3055.brandonscore.client.particle.IBCParticleFactory;
import com.brandon3055.brandonscore.client.particle.IntParticleType;
import com.brandon3055.brandonscore.lib.Vec3D;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * Created by brandon3055 on 2/5/2016.
 * The particle used to render the beams on the Energy Core
 */
public class ParticleLineIndicator extends SpriteTexturedParticle {
    public ParticleLineIndicator(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn) {
        super(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
    }

    @Override
    public IParticleRenderType getRenderType() {
        return IParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    //    protected ParticleLineIndicator(World worldIn, Vec3D pos) {
//        super(worldIn, pos);
//    }
//
//    public ParticleLineIndicator(World worldIn, Vec3D pos, Vec3D speed) {
//        super(worldIn, pos, speed);
//        this.motionX = speed.x;
//        this.motionY = speed.y;
//        this.motionZ = speed.z;
////        this.particleMaxAge = 60;
////        this.particleScale = 1F;
////        this.particleTextureIndexY = 0;
//    }

//    @Override
//    public boolean shouldDisableDepth() {
//        return true;
//    }

//    @Override
//    public void onUpdate() {
//        super.onUpdate();
//        particleScale = 1F - ((float) particleAge / (float) particleMaxAge);
//    }

//    @Override
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

    public static class Factory implements IParticleFactory<IntParticleType.IntParticleData> {
        private final IAnimatedSprite spriteSet;

        public Factory(IAnimatedSprite p_i50823_1_) {
            this.spriteSet = p_i50823_1_;
        }

        @Nullable
        @Override
        public Particle makeParticle(IntParticleType.IntParticleData data, World world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
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
