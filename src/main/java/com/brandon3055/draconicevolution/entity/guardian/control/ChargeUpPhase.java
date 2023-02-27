package com.brandon3055.draconicevolution.entity.guardian.control;

import codechicken.lib.data.MCDataInput;
import codechicken.lib.vec.Vector3;
import com.brandon3055.brandonscore.lib.TeleportUtils;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.client.DEParticles;
import com.brandon3055.draconicevolution.client.render.particle.GuardianChargeParticle;
import com.brandon3055.draconicevolution.entity.guardian.DraconicGuardianEntity;
import com.brandon3055.draconicevolution.entity.guardian.GuardianFightManager;
import com.brandon3055.draconicevolution.handlers.DESounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.HashMap;
import java.util.Map;

public abstract class ChargeUpPhase extends Phase {
    protected Map<Player, Vec3> trappedPlayers = new HashMap<>();
    protected int requiredChargeTime;
    protected int chargeTime;
    protected int chargedTime;
    private int effectTimer = 0;
    private int effectTime = 0;
    public boolean disableFlight = false;
    public boolean trapPlayers = false;

    public ChargeUpPhase(DraconicGuardianEntity guardian, int requiredChargeTime) {
        super(guardian);
        this.requiredChargeTime = requiredChargeTime;
    }

    public boolean getIsStationary() {
        return true;
    }

    @Override
    public void initPhase() {
        chargeTime = 0;
        chargedTime = 0;
        effectTimer = 0;
    }

    @Override
    public void removeAreaEffect() {
        trappedPlayers.clear();
    }

    public void resetCharge() {
        chargeTime = 0;
    }

    @Override
    public void serverTick() {
        if (trappedPlayers == null) trappedPlayers = new HashMap<>();
        if (chargeTime < requiredChargeTime) {
            chargeTime++;
        } else {
            chargedTime++;
        }
        if (trapPlayers && isCharged()) {
            GuardianFightManager manager = guardian.getFightManager();
            if (manager != null) {
                for (Player player : manager.getTrackedPlayers()) {
                    Vec3 pos = player.position();
                    Vec3 lastPos = trappedPlayers.getOrDefault(player, player.position());
                    Vec3 center = Vec3.atCenterOf(manager.getArenaOrigin().offset(0, 16, 0));
                    double currentDist = pos.distanceToSqr(center);
                    int threshold = 100*100;
                    int breakAway = 1000*1000; //The player can still teleport away / out of the dimension

                    if (currentDist > threshold && currentDist < breakAway && lastPos.distanceToSqr(center) <= threshold && player.level.dimension().equals(guardian.level.dimension())) {
                        TeleportUtils.teleportEntity(player, player.level.dimension(), lastPos.x, lastPos.y, lastPos.z);
                    }

                    if (isValidTarget(player) && player.getY() < manager.getArenaOrigin().getY() - 10) {
                        TeleportUtils.teleportEntity(player, player.level.dimension(), player.getX(), manager.getArenaOrigin().getY() + 15, player.getZ());
                    }

                    if (player.position().distanceToSqr(center) < threshold) {
                        trappedPlayers.put(player, player.position());
                    }
                }
            }
        }

        BlockPos origin = guardian.getArenaOrigin();
        if (origin == null) return;
        if (disableFlight && getChargeProgress() > 0.5) {
            GuardianFightManager manager = guardian.getFightManager();
            if (manager != null) {
                for (Player player : manager.getTrackedPlayers()) {
                    if (player.getY() > origin.getY() + 8) {
                        if (player.getAbilities().flying) {
                            player.getAbilities().flying = false;
                        }
                    }
                }
            }
        }
    }

    float speedMod;
    int offGroundTime = 0;

    @Override
    @OnlyIn(Dist.CLIENT)
    public void clientTick() {
        if (chargeTime < requiredChargeTime) {
            chargeTime++;
        } else {
            chargedTime++;
        }
        BlockPos origin = guardian.getArenaOrigin();
        if (origin == null) return;
        Player player = Minecraft.getInstance().player;
        if (player == null || !isValidTarget(player)) return;

        if (effectTimer == 0) {
            speedMod = (float) getChargeProgress();
            effectTime = effectTimer = (int) (20F - (speedMod * 10F));
            guardian.level.playLocalSound(guardian.getX(), guardian.getY(), guardian.getZ(), DESounds.crystalBeam, SoundSource.HOSTILE, 64, 1F + speedMod, false);
            if (origin != null) {
                for (int i = 0; i < 32; i++) {
                    Minecraft.getInstance().particleEngine.add(new GuardianChargeParticle((ClientLevel) guardian.level, Vector3.fromBlockPosCenter(origin), Vector3.fromEntity(guardian), i / 32D, effectTime, guardian.getPhaseManager()));
                }
            }
        } else {
            effectTimer--;
        }

        if (trapPlayers) {
            for (int i = 0; i < 4; i++) {
                float randDir = random.nextFloat() * (float) Math.PI * 2F;
                int randDist = 95 + random.nextInt(30);
                double x = guardian.getX() + Mth.sin(randDir) * randDist;
                double z = guardian.getZ() + Mth.cos(randDir) * randDist;
                double y = guardian.getY() - 8 - random.nextInt(32);
                Vector3 motion = new Vector3(guardian.getX(), y, guardian.getZ()).subtract(x, y, z).normalize().multiply((1 + random.nextDouble()) * getChargeProgress());
                guardian.level.addParticle(DEParticles.guardian_cloud, true, x, y, z, motion.x, motion.y, motion.z);
            }

            Vector3 center = new Vector3(guardian.getX(), guardian.getY() - 32, guardian.getZ());
            int threshold = 85;

            if (!player.getAbilities().instabuild) {
                if (player.distanceToSqr(center.vec3()) > threshold * threshold) {
                    double distanceOver = Math.sqrt(player.distanceToSqr(center.vec3())) - threshold;
                    Vector3 forceVec = center.copy().subtract(player.getX(), player.getY(), player.getZ()).normalize().multiply((1 * (distanceOver / 10)) * getChargeProgress());
                    debug(forceVec.toString());
                    player.setDeltaMovement(player.getDeltaMovement().add(forceVec.vec3()));
                }
                if (player.getY() < origin.getY()) {
                    player.setDeltaMovement(player.getDeltaMovement().add(0, ((origin.getY() - player.getY()) / 5F) * getChargeProgress(), 0));
                }
            }
        }

        if (disableFlight && player != null && !player.getAbilities().instabuild && getChargeProgress() > 0.5) {
            if (player.getY() > origin.getY() + 8) {
                if (player.getAbilities().flying) {
                    player.getAbilities().flying = false;
                }
                if (player.isFallFlying()) {
                    Vec3 motion = player.getDeltaMovement();
                    player.setDeltaMovement(motion.x * 0.75, motion.y > 0 ? motion.y * 0.75 : motion.y, motion.z * 0.75);
                }
            }

            if (player.isOnGround() || player.getY() < origin.getY() + 8) {
                offGroundTime = 0;
            } else {
                offGroundTime++;
                if (offGroundTime > 40) {
                    player.setDeltaMovement(player.getDeltaMovement().add(0, Math.min(offGroundTime - 40, 200) / -100D, 0));
                }
            }

            for (int i = 0; i < 2; i++) {
                double x = guardian.getX() - 95 + random.nextInt(95*2);
                double y = guardian.getY() - random.nextInt(35);
                double z = guardian.getZ() - 95 + random.nextInt(95*2);
                if (guardian.distanceToSqr(x, y, z) < 100 * 100) {
                    guardian.level.addParticle(DEParticles.guardian_cloud, true, x, y, z, 0, 0, 0);
                }
            }
        }
    }

    public float animState() {
        return effectTimer > 0 ? effectTimer / (float) effectTime : 0;
    }

    public double getChargeProgress() {
        return chargeTime / (double) requiredChargeTime;
    }

    public boolean isCharged() {
        return chargeTime >= requiredChargeTime;
    }

    public float onAttacked(DamageSource source, float damage, float shield, boolean effective) {
        if (isInvulnerable()) {
            if (source.getDirectEntity() instanceof AbstractArrow) {
                source.getDirectEntity().setSecondsOnFire(1);
            }
            return 0.0F;
        } else {
            return super.onAttacked(source, damage, shield, effective);
        }
    }

    @Override
    public void handlePacket(MCDataInput input, int func) {
        if (func == 0) {
            resetCharge();
        }
    }

    @Override
    public boolean isInvulnerable() {
        return true;
    }
}
