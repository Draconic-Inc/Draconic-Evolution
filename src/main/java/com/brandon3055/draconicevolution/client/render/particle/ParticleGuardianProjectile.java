package com.brandon3055.draconicevolution.client.render.particle;

import com.brandon3055.brandonscore.client.particle.BCParticle;
import com.brandon3055.brandonscore.client.particle.IBCParticleFactory;
import com.brandon3055.brandonscore.lib.Vec3D;
import com.brandon3055.draconicevolution.entity.EntityGuardianProjectile;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public class ParticleGuardianProjectile extends BCParticle {

    public EntityGuardianProjectile entity;

    protected ParticleGuardianProjectile(World worldIn, Vec3D pos) {
        super(worldIn, pos);
    }

    public ParticleGuardianProjectile(World worldIn, Vec3D pos, Vec3D speed) {
        super(worldIn, pos, speed);
        this.particleMaxAge = 50;
        this.particleRed = this.particleGreen = this.particleBlue = 1.0f;
        double m = 0.4D;
        this.motionX = (worldIn.rand.nextDouble() - 0.5) * m;
        this.motionY = (worldIn.rand.nextDouble() - 0.5) * m;
        this.motionZ = (worldIn.rand.nextDouble() - 0.5) * m;
        this.particleTextureIndexX = 7;
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

        particleAlpha = (1F - (float) ((double) particleAge / particleMaxAge));
        particleScale = 1F * (1F - (float) ((double) particleAge / particleMaxAge));

        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        moveEntityNoClip(motionX, motionY, motionZ);
    }

    @Override
    //@SideOnly(Side.CLIENT)
    public void renderParticle(VertexBuffer vertexbuffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {

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

    public static class Factory implements IBCParticleFactory {

        @Override
        public Particle getEntityFX(int particleID, World world, Vec3D pos, Vec3D speed, int... args) {
            ParticleGuardianProjectile particle = new ParticleGuardianProjectile(world, pos, speed);

            if (args.length > 0) {
                Entity projectile = world.getEntityByID(args[0]);
                if (projectile instanceof EntityGuardianProjectile) {
                    particle.entity = (EntityGuardianProjectile) projectile;
                }
            }

            if (args.length > 3) {
                particle.setRBGColorF(args[1] / 255F, args[2] / 255F, args[3] / 255F);
            }

            return particle;
        }
    }
}
