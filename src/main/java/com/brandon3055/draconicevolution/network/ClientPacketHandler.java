package com.brandon3055.draconicevolution.network;

import codechicken.lib.packet.ICustomPacketHandler;
import codechicken.lib.packet.PacketCustom;
import com.brandon3055.brandonscore.BrandonsCore;
import com.brandon3055.brandonscore.lib.Vec3D;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.client.DEParticles;
import com.brandon3055.draconicevolution.client.render.effect.ExplosionFX;
import com.brandon3055.draconicevolution.init.DEModules;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.client.particle.FireworkParticle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;

public class ClientPacketHandler implements ICustomPacketHandler.IClientPacketHandler {

    @Override
    public void handlePacket(PacketCustom packet, Minecraft mc, IClientPlayNetHandler handler) {
        switch (packet.getType()) {
//            case 1: //Portal Arrival
//                TileEntity tile = mc.world.getTileEntity(packet.readPos());
//                if (tile instanceof TileDislocatorReceptacle) {
//                    ((TileDislocatorReceptacle) tile).setHidden();
//                }
//                break;
            case DraconicNetwork.C_CRYSTAL_UPDATE:
                CrystalUpdateBatcher.handleBatchedData(packet);
                break;
            case DraconicNetwork.C_EXPLOSION_EFFECT:
                handleExplosionEffect(mc, packet.readPos(), packet.readVarInt(), packet.readBoolean());
                break;
            case DraconicNetwork.C_IMPACT_EFFECT:
                handleImpactEffect(mc, packet.readPos(), packet.readByte());
                break;
            case DraconicNetwork.C_LAST_STAND_ACTIVATION:
                handleLastStandActivation(mc, packet.readVarInt(), packet.readRegistryId());
                break;
            case DraconicNetwork.C_BLINK:
                handleBlinkEffect(mc, packet.readVarInt(), packet.readFloat());
                break;

        }
    }


    public static void handleExplosionEffect(Minecraft mc, BlockPos pos, int radius, boolean reload) {
        if (reload) {
            mc.worldRenderer.loadRenderers();
        } else {
            ExplosionFX explosionFX = new ExplosionFX((ClientWorld) BrandonsCore.proxy.getClientWorld(), Vec3D.getCenter(pos), radius);
            mc.particles.addEffect(explosionFX);
        }
    }

    public static void handleImpactEffect(Minecraft mc, BlockPos pos, int type) {
        if (mc.world == null) return;
        if (type == 0) { //Burst-Explosion Effect
            int size = 4;
            double speed = 1;
            double x = pos.getX() + 0.5;
            double y = pos.getY() + 0.5;
            double z = pos.getZ() + 0.5;
            for(int i = -size; i <= size; ++i) {
                for(int j = -size; j <= size; ++j) {
                    for(int k = -size; k <= size; ++k) {
                        double d3 = (double)j + (mc.world.rand.nextDouble() - mc.world.rand.nextDouble()) * 0.5D;
                        double d4 = (double)i + (mc.world.rand.nextDouble() - mc.world.rand.nextDouble()) * 0.5D;
                        double d5 = (double)k + (mc.world.rand.nextDouble() - mc.world.rand.nextDouble()) * 0.5D;
                        double d6 = (double) MathHelper.sqrt(d3 * d3 + d4 * d4 + d5 * d5) / speed + mc.world.rand.nextGaussian() * 6D;
                        createParticle(mc, x, y, z, d3 / d6, d4 / d6, d5 / d6);
                        if (i != -size && i != size && j != -size && j != size) {
                            k += size * 2 - 1;
                        }
                    }
                }
            }
            mc.particles.addParticle(ParticleTypes.EXPLOSION_EMITTER, x, y, z, 0, 0, 0);
        }
    }

    private static void createParticle(Minecraft mc, double x, double y, double z, double motionX, double motionY, double motionZ) {
        mc.particles.addParticle(DEParticles.guardian_projectile, x, y, z, motionX, motionY, motionZ);

//        FireworkParticle.Spark particle = (FireworkParticle.Spark)mc.particles.addParticle(ParticleTypes.FIREWORK, x, y, z, motionX, motionY, motionZ);
//        particle.canCollide = false;
//        particle.setMaxAge(15 + mc.world.rand.nextInt(5));
//        float ci = 0.5F + (mc.world.rand.nextFloat() * 0.5F);
//        particle.setColor(1F, 0.6F * ci, 0.06F * ci);
    }

    private static void handleLastStandActivation(Minecraft mc, int id, Item item) {
        if (mc.world == null) return;;
        Entity entity = mc.world.getEntityByID(id);
        if (entity != null) {
            mc.particles.emitParticleAtEntity(entity, ParticleTypes.TOTEM_OF_UNDYING, 30);
            if (entity == mc.player) {
                Minecraft.getInstance().gameRenderer.displayItemActivation(new ItemStack(item));
            }
        }
    }

    private static void handleBlinkEffect(Minecraft mc, int id, float distance) {
        Entity entity;
        if (mc.world == null || (entity = mc.world.getEntityByID(id)) == null) return;

        Vector3d vec = entity.getLookVec();
        Vector3d pos = entity.getEyePosition(1);

        for (int i = 0; i < 100; i++) {
            float offset = mc.world.rand.nextFloat() ;
            float speed = (1F - offset) * distance;
            speed *= speed;
            Vector3d spawnPos = pos.add(vec.mul(speed * 10, speed * 10, speed * 10));

            double x = spawnPos.x + (mc.world.rand.nextGaussian() - 0.5) * offset;
            double y = spawnPos.y + (mc.world.rand.nextGaussian() - 0.5) * offset;
            double z = spawnPos.z + (mc.world.rand.nextGaussian() - 0.5) * offset;

            mc.world.addParticle(DEParticles.blink, x, y, z, vec.x * speed, vec.y * speed, vec.z * speed);
        }
    }
}























