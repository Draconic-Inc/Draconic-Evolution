package com.brandon3055.draconicevolution.client.render.particle;

import com.brandon3055.brandonscore.client.particle.BCParticle;
import com.brandon3055.brandonscore.lib.Vec3D;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public class ParticleStarSpark extends BCParticle {

    public float sparkSize = 0.5F;

    public ParticleStarSpark(World worldIn, Vec3D pos) {
        super(worldIn, pos);

        double speed = 0.1;
        this.motionX = (-0.5 + rand.nextDouble()) * speed;
        this.motionY = (-0.5 + rand.nextDouble()) * speed;
        this.motionZ = (-0.5 + rand.nextDouble()) * speed;

        this.particleMaxAge = 10 + rand.nextInt(10);
        this.particleTextureIndexY = 1;
    }

    @Override
    public boolean isTransparent() {
        return true;
    }

    @Override
    public void onUpdate() {
        if (particleAge++ > particleMaxAge) {
            setExpired();
        }
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        motionY += particleGravity;

        particleTextureIndexX = rand.nextInt(5);
        int ttd = particleMaxAge - particleAge;
        if (ttd < 10){
            particleScale = (ttd / 10F) * baseScale;
        }
        if (ttd < 1){
            particleScale = sparkSize;
        }

        motionX *= 1 - airResistance;
        motionY *= 1 - airResistance;
        motionZ *= 1 - airResistance;

        moveEntityNoClip(motionX, motionY, motionZ);
    }

    @Override
    public void renderParticle(VertexBuffer vertexbuffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
        if (particleAge == 0) {
            return;
        }
        float minU = (float) this.particleTextureIndexX / 8.0F;
        float maxU = minU + 0.125F;
        float minV = (float) this.particleTextureIndexY / 8.0F;
        float maxV = minV + 0.125F;
        float scale = 0.1F * this.particleScale;

        float renderX = (float) (this.prevPosX + (this.posX - this.prevPosX) * (double) partialTicks - interpPosX);
        float renderY = (float) (this.prevPosY + (this.posY - this.prevPosY) * (double) partialTicks - interpPosY);
        float renderZ = (float) (this.prevPosZ + (this.posZ - this.prevPosZ) * (double) partialTicks - interpPosZ);
        int brightnessForRender = this.getBrightnessForRender(partialTicks);
        int j = brightnessForRender >> 16 & 65535;
        int k = brightnessForRender & 65535;
        vertexbuffer.pos((double) (renderX - rotationX * scale - rotationXY * scale), (double) (renderY - rotationZ * scale), (double) (renderZ - rotationYZ * scale - rotationXZ * scale)).tex((double) maxU, (double) maxV).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
        vertexbuffer.pos((double) (renderX - rotationX * scale + rotationXY * scale), (double) (renderY + rotationZ * scale), (double) (renderZ - rotationYZ * scale + rotationXZ * scale)).tex((double) maxU, (double) minV).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
        vertexbuffer.pos((double) (renderX + rotationX * scale + rotationXY * scale), (double) (renderY + rotationZ * scale), (double) (renderZ + rotationYZ * scale + rotationXZ * scale)).tex((double) minU, (double) minV).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
        vertexbuffer.pos((double) (renderX + rotationX * scale - rotationXY * scale), (double) (renderY - rotationZ * scale), (double) (renderZ + rotationYZ * scale - rotationXZ * scale)).tex((double) minU, (double) maxV).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
    }
}