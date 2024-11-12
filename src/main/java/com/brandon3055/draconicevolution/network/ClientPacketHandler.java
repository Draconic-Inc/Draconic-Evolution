package com.brandon3055.draconicevolution.network;

import codechicken.lib.data.MCDataInput;
import codechicken.lib.packet.ICustomPacketHandler;
import codechicken.lib.packet.PacketCustom;
import codechicken.lib.vec.Vector3;
import com.brandon3055.brandonscore.BrandonsCore;
import com.brandon3055.brandonscore.utils.MathUtils;
import com.brandon3055.draconicevolution.blocks.tileentity.TilePortal;
import com.brandon3055.draconicevolution.client.CustomBossInfoHandler;
import com.brandon3055.draconicevolution.client.DEParticles;
import com.brandon3055.draconicevolution.client.render.effect.ExplosionFX;
import com.brandon3055.draconicevolution.entity.guardian.DraconicGuardianEntity;
import com.brandon3055.draconicevolution.entity.guardian.control.IPhase;
import com.brandon3055.draconicevolution.entity.guardian.control.PhaseManager;
import com.brandon3055.draconicevolution.init.DEClient;
import com.brandon3055.draconicevolution.items.equipment.damage.DefaultStaffDmgMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.Vec3;

public class ClientPacketHandler implements ICustomPacketHandler.IClientPacketHandler {

    @Override
    public void handlePacket(PacketCustom packet, Minecraft mc) {
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
            case DraconicNetwork.C_UNDYING_ACTIVATION:
                handleUndyingActivation(mc, packet.readVarInt(), packet.readRegistryId());
                break;
            case DraconicNetwork.C_BLINK:
                handleBlinkEffect(mc, packet.readVarInt(), packet.readFloat());
                break;
            case DraconicNetwork.C_STAFF_EFFECT:
                handleStaffEffect(mc, packet);
                break;
            case DraconicNetwork.C_GUARDIAN_BEAM:
                handleGuardianBeam(mc, packet);
                break;
            case DraconicNetwork.C_GUARDIAN_PACKET:
                handleGuardianPacket(mc, packet);
                break;
            case DraconicNetwork.C_BOSS_SHIELD_INFO:
                CustomBossInfoHandler.handlePacket(packet);
                break;
            case DraconicNetwork.C_DISLOCATOR_TELEPORTED:
                handleDislocatorTeleported(mc);
                break;
            case DraconicNetwork.C_CHUNK_RELIGHT:
                handleChunkRelight(mc, packet.readInt(), packet.readInt());
                break;
        }
    }

    public static void handleExplosionEffect(Minecraft mc, BlockPos pos, int radius, boolean reload) {
        if (reload) {
            mc.levelRenderer.allChanged();
        }
        if (radius == 0) return;
        ExplosionFX explosionFX = new ExplosionFX((ClientLevel) BrandonsCore.proxy.getClientWorld(), Vector3.fromBlockPosCenter(pos), radius);
        mc.particleEngine.add(explosionFX);
    }

    public static void handleImpactEffect(Minecraft mc, BlockPos pos, int type) {
        if (mc.level == null) return;
        if (type == 0) { //Burst-Explosion Effect
            int size = 4;
            double speed = 1;
            double x = pos.getX() + 0.5;
            double y = pos.getY() + 0.5;
            double z = pos.getZ() + 0.5;
            for (int i = -size; i <= size; ++i) {
                for (int j = -size; j <= size; ++j) {
                    for (int k = -size; k <= size; ++k) {
                        double d3 = (double) j + (mc.level.random.nextDouble() - mc.level.random.nextDouble()) * 0.5D;
                        double d4 = (double) i + (mc.level.random.nextDouble() - mc.level.random.nextDouble()) * 0.5D;
                        double d5 = (double) k + (mc.level.random.nextDouble() - mc.level.random.nextDouble()) * 0.5D;
                        double d6 = (double) Math.sqrt(d3 * d3 + d4 * d4 + d5 * d5) / speed + mc.level.random.nextGaussian() * 6D;
                        createParticle(mc, x, y, z, d3 / d6, d4 / d6, d5 / d6);
                        if (i != -size && i != size && j != -size && j != size) {
                            k += size * 2 - 1;
                        }
                    }
                }
            }
            mc.particleEngine.createParticle(ParticleTypes.EXPLOSION_EMITTER, x, y, z, 0, 0, 0);
        }
    }

    private static void createParticle(Minecraft mc, double x, double y, double z, double motionX, double motionY, double motionZ) {
        mc.particleEngine.createParticle(DEParticles.GUARDIAN_PROJECTILE.get(), x, y, z, motionX, motionY, motionZ);

//        FireworkParticle.Spark particle = (FireworkParticle.Spark)mc.particles.addParticle(ParticleTypes.FIREWORK, x, y, z, motionX, motionY, motionZ);
//        particle.canCollide = false;
//        particle.setMaxAge(15 + mc.world.rand.nextInt(5));
//        float ci = 0.5F + (mc.world.rand.nextFloat() * 0.5F);
//        particle.setColor(1F, 0.6F * ci, 0.06F * ci);
    }

    private static void handleUndyingActivation(Minecraft mc, int id, Item item) {
        if (mc.level == null) return;
        ;
        Entity entity = mc.level.getEntity(id);
        if (entity != null) {
            mc.particleEngine.createTrackingEmitter(entity, ParticleTypes.TOTEM_OF_UNDYING, 30);
            if (entity == mc.player) {
                DEClient.SHIELD_HUD.get().popTotem();
            }
        }
    }

    private static void handleBlinkEffect(Minecraft mc, int id, float distance) {
        Entity entity;
        if (mc.level == null || (entity = mc.level.getEntity(id)) == null) return;

        Vec3 vec = entity.getLookAngle();
        Vec3 pos = entity.getEyePosition(1);

        for (int i = 0; i < 100; i++) {
            float offset = mc.level.random.nextFloat();
            float speed = (1F - offset) * distance;
            speed *= speed;
            Vec3 spawnPos = pos.add(vec.multiply(speed * 10, speed * 10, speed * 10));

            double x = spawnPos.x + (mc.level.random.nextGaussian() - 0.5) * offset;
            double y = spawnPos.y + (mc.level.random.nextGaussian() - 0.5) * offset;
            double z = spawnPos.z + (mc.level.random.nextGaussian() - 0.5) * offset;

            mc.level.addParticle(DEParticles.BLINK.get(), x, y, z, vec.x * speed, vec.y * speed, vec.z * speed);
        }
    }

    private static void handleStaffEffect(Minecraft mc, MCDataInput data) {
        int type = data.readByte();
        int entityID = data.readVarInt();
        if (mc.level != null) {
            Entity entity = mc.level.getEntity(entityID);
            if (entity instanceof LivingEntity) {
                switch (type) {
                    case 0:
                        DefaultStaffDmgMod.handleEffect((LivingEntity) entity, data);
                        break;
                    case 1:
                        break;
                    case 2:
                        break;
                    case 3:
                        break;
                    case 4:
                        break;
                }
            }
        }
    }

    private static void handleGuardianBeam(Minecraft mc, MCDataInput data) {
        Vector3 source = data.readVector();
        Vector3 target = data.readVector();
        float power = data.readFloat();
        double dist = MathUtils.distance(source, target);
        if (mc.level == null) return;
        for (double d = 0; d < dist; d += 2) {
            Vector3 pos = MathUtils.interpolateVec3(source, target, ((d - 1) + mc.level.random.nextDouble() * 2) / dist);
            mc.level.addParticle(DEParticles.GUARDIAN_BEAM.get(), true, pos.x, pos.y, pos.z, power, 0, 0);
        }
    }

    private static void handleGuardianPacket(Minecraft mc, MCDataInput data) {
        if (mc.level == null) return;
        Entity e = mc.level.getEntity(data.readInt());
        if (!(e instanceof DraconicGuardianEntity)) return;
        ;
        DraconicGuardianEntity guardian = (DraconicGuardianEntity) e;
        int phaseID = data.readByte();
        PhaseManager phaseManager = guardian.getPhaseManager();
        IPhase phase = phaseManager.getCurrentPhase();
        if (phase.getType().getId() != phaseID) return;
        int function = data.readByte();
        phase.handlePacket(data, function);
    }

    private void handleDislocatorTeleported(Minecraft mc) {
        Player player = mc.player;
        if (player == null) return;
        ;
        BlockPos playerPos = player.blockPosition();
        for (BlockPos pos : BlockPos.betweenClosed(playerPos.offset(-1, -1, -1), playerPos.offset(1, 1, 1))) {
            BlockEntity tile = player.level().getBlockEntity(pos);
            if (tile instanceof TilePortal) {
                ((TilePortal) tile).clientArrived(player);
            }
        }
    }

    private void handleChunkRelight(Minecraft mc, int chunkX, int chunkZ) {
        if (mc.level == null) return;
        LevelChunk chunk = mc.level.getChunk(chunkX, chunkZ);
        if (chunk != null) {
//            LevelLightEngine lightManager = mc.level.getLightEngine();
//            lightManager.lightChunk(chunk, false);
        }
    }
}























