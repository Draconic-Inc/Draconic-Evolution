package com.brandon3055.draconicevolution.client.render.particle;

import com.brandon3055.brandonscore.client.particle.BCParticle;
import com.brandon3055.brandonscore.client.particle.IBCParticleFactory;
import com.brandon3055.brandonscore.lib.Vec3D;
import com.brandon3055.brandonscore.utils.BCProfiler;
import com.brandon3055.brandonscore.utils.Utils;
import com.brandon3055.draconicevolution.client.handler.ClientEventHandler;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.util.Direction;
import net.minecraft.world.World;

/**
 * Created by brandon3055 on 2/5/2016.
 * The particle used to render the beams on the Energy Core
 */
public class ParticleEnergyCoreFX extends BCParticle {

    public Vec3D targetPos;
    public boolean toCore = false;
    public int startRotation = 0;
    private Direction.Axis direction;
    public boolean isLargeStabilizer = false;

    public ParticleEnergyCoreFX(World worldIn, Vec3D pos) {
        super(worldIn, pos);
    }

    public ParticleEnergyCoreFX(World worldIn, Vec3D pos, Vec3D targetPos) {
        super(worldIn, pos, new Vec3D(0, 0, 0));
        this.targetPos = targetPos;
//        this.particleMaxAge = 50;
//        this.particleScale = 1F;
//        this.particleTextureIndexY = 1;
        Vec3D dir = Vec3D.getDirectionVec(pos, targetPos);
        this.direction = Direction.getFacingFromVector((float) dir.x, (float) dir.y, (float) dir.z).getAxis();
    }

//    @Override
//    public boolean shouldDisableDepth() {
//        return true;
//    }
//
//    @Override
//    public void onUpdate() {
//        BCProfiler.TICK.start("core_fx_update");
//        this.prevPosX = this.posX;
//        this.prevPosY = this.posY;
//        this.prevPosZ = this.posZ;
//
//        Vec3D tPos = this.targetPos.copy();
//        particleTextureIndexX = rand.nextInt(5);
//
//        if (toCore) {
//            double rotation = ClientEventHandler.elapsedTicks;
//            double offsetX = Math.sin((rotation / 180D * Math.PI) + (startRotation / 100D));
//            double offsetY = Math.cos((rotation / 180D * Math.PI) + (startRotation / 100D));
//
//            double d = isLargeStabilizer ? 1.8 : 0.2;
//            if (direction == Direction.Axis.Z) {
//                tPos.add(offsetX * d, offsetY * d, 0);
//            }
//            else if (direction == Direction.Axis.Y) {
//                tPos.add(offsetX * d, 0, offsetY * d);
//            }
//            else if (direction == Direction.Axis.X) {
//                tPos.add(0, offsetY * d, offsetX * d);
//            }
//        }
//
//        Vec3D dir = Vec3D.getDirectionVec(new Vec3D(posX, posY, posZ), tPos);
//        double speed = toCore ? 0.5D : 0.25D;
//        motionX = dir.x * speed;
//        motionY = dir.y * speed;
//        motionZ = dir.z * speed;
//        moveEntityNoClip(motionX, motionY, motionZ);
//
//        if (particleAge++ > particleMaxAge || Utils.getDistanceAtoB(posX, posY, posZ, tPos.x, tPos.y, tPos.z) < 0.2) {
//            setExpired();
//        }
//        BCProfiler.TICK.stop();
//    }

//    @Override
//    public void renderParticle(BufferBuilder vertexbuffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
//        BCProfiler.RENDER.start("core_fx");
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
//        BCProfiler.RENDER.stop();
//    }

    public static class Factory implements IBCParticleFactory {

        @Override
        public Particle getEntityFX(int particleID, World world, Vec3D pos, Vec3D targetPos, int... args) {
            ParticleEnergyCoreFX particle = new ParticleEnergyCoreFX(world, pos, targetPos);
            particle.toCore = args.length >= 1 && args[0] == 1;
            particle.startRotation = args.length >= 2 ? args[1] : 0;
            particle.isLargeStabilizer = args.length >= 3 && args[2] == 1;
            particle.multipleParticleScaleBy(particle.isLargeStabilizer ? 2 : 1);
            return particle;
        }
    }
}
