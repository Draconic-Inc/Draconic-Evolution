package com.brandon3055.draconicevolution.client.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.world.World;

import com.brandon3055.draconicevolution.client.render.particle.ParticleDistortion;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ParticleHandler {

    private static Minecraft mc = Minecraft.getMinecraft();
    private static World theWorld = mc.theWorld;

    public static EntityFX spawnParticle(String particleName, double x, double y, double z, double motionX,
            double motionY, double motionZ, float scale) {
        if (mc != null && mc.renderViewEntity != null && mc.effectRenderer != null) {
            int var14 = mc.gameSettings.particleSetting;
            if (var14 == 1 && theWorld.rand.nextInt(3) == 0) {
                var14 = 2;
            }
            double var15 = mc.renderViewEntity.posX - x;
            double var17 = mc.renderViewEntity.posY - y;
            double var19 = mc.renderViewEntity.posZ - z;
            EntityFX var21 = null;
            double var22 = 16.0D;
            if (var15 * var15 + var17 * var17 + var19 * var19 > var22 * var22) {
                return null;
            } else if (var14 > 1) {
                return null;
            } else {
                if (particleName.equals("distortionParticle")) {
                    var21 = new ParticleDistortion(
                            theWorld,
                            x,
                            y,
                            z,
                            (float) motionX,
                            (float) motionY,
                            (float) motionZ,
                            scale);
                }

                mc.effectRenderer.addEffect(var21);
                return var21;
            }
        }
        return null;
    }

    public static EntityFX spawnCustomParticle(EntityFX particle) {
        return spawnCustomParticle(particle, 64);
    }

    public static EntityFX spawnCustomParticle(EntityFX particle, double vewRange) {
        if (mc != null && mc.renderViewEntity != null && mc.effectRenderer != null) {
            int var14 = mc.gameSettings.particleSetting;
            if (var14 == 1 && theWorld.rand.nextInt(3) == 0) {
                var14 = 2;
            }
            if (!isInRange(particle.posX, particle.posY, particle.posZ, vewRange)) {
                return null;
            } else if (var14 > 1) {
                return null;
            } else {
                mc.effectRenderer.addEffect(particle);
                return particle;
            }
        }
        return null;
    }

    public static boolean isInRange(double x, double y, double z, double vewRange) {
        if (mc == null || mc.renderViewEntity == null || mc.effectRenderer == null) return false;

        double var15 = mc.renderViewEntity.posX - x;
        double var17 = mc.renderViewEntity.posY - y;
        double var19 = mc.renderViewEntity.posZ - z;
        if (var15 * var15 + var17 * var17 + var19 * var19 > vewRange * vewRange) {
            return false;
        }
        return true;
    }
}
