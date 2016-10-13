package com.brandon3055.draconicevolution.client.render.particle;

import com.brandon3055.brandonscore.client.particle.BCParticle;
import com.brandon3055.brandonscore.client.particle.IBCParticleFactory;
import com.brandon3055.brandonscore.lib.Vec3D;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

/**
 * Created by brandon3055 on 2/5/2016.
 * The particle used to render the beams on the Energy Core
 */
public class ParticleLineIndicator extends BCParticle {

    protected ParticleLineIndicator(World worldIn, Vec3D pos) {
        super(worldIn, pos);
    }

    public ParticleLineIndicator(World worldIn, Vec3D pos, Vec3D speed) {
        super(worldIn, pos, speed);
        this.motionX = speed.x;
        this.motionY = speed.y;
        this.motionZ = speed.z;
        this.particleMaxAge = 60;
        this.particleScale = 1F;
        this.particleTextureIndexY = 0;
    }

    @Override
    public boolean isTransparent() {
        return true;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        particleScale = 1F - ((float)particleAge / (float)particleMaxAge);
    }

    @Override
    public void renderParticle(VertexBuffer vertexbuffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
        float minU = (float)this.particleTextureIndexX / 8.0F;
        float maxU = minU + 0.125F;
        float minV = (float)this.particleTextureIndexY / 8.0F;
        float maxV = minV + 0.125F;
        float scale = 0.1F * this.particleScale;

        float renderX = (float)(this.prevPosX + (this.posX - this.prevPosX) * (double)partialTicks - interpPosX);
        float renderY = (float)(this.prevPosY + (this.posY - this.prevPosY) * (double)partialTicks - interpPosY);
        float renderZ = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * (double)partialTicks - interpPosZ);
        int brightnessForRender = this.getBrightnessForRender(partialTicks);
        int j = brightnessForRender >> 16 & 65535;
        int k = brightnessForRender & 65535;
        vertexbuffer.pos((double)(renderX - rotationX * scale - rotationXY * scale), (double)(renderY - rotationZ * scale), (double)(renderZ - rotationYZ * scale - rotationXZ * scale)).tex((double)maxU, (double)maxV).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
        vertexbuffer.pos((double)(renderX - rotationX * scale + rotationXY * scale), (double)(renderY + rotationZ * scale), (double)(renderZ - rotationYZ * scale + rotationXZ * scale)).tex((double)maxU, (double)minV).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
        vertexbuffer.pos((double)(renderX + rotationX * scale + rotationXY * scale), (double)(renderY + rotationZ * scale), (double)(renderZ + rotationYZ * scale + rotationXZ * scale)).tex((double)minU, (double)minV).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
        vertexbuffer.pos((double)(renderX + rotationX * scale - rotationXY * scale), (double)(renderY - rotationZ * scale), (double)(renderZ + rotationYZ * scale - rotationXZ * scale)).tex((double)minU, (double)maxV).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
    }

    public static class Factory implements IBCParticleFactory {

        @Override
        public Particle getEntityFX(int particleID, World world, Vec3D pos, Vec3D speed, int... args) {
            ParticleLineIndicator particle = new ParticleLineIndicator(world, pos, speed);
            if (args.length >= 3){
                particle.setRBGColorF(args[0] / 255F, args[1] / 255F, args[2] / 255F);
            }

            return particle;
        }
    }
}
