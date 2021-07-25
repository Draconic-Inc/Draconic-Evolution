package com.brandon3055.draconicevolution.entity.guardian.control;

import codechicken.lib.data.MCDataInput;
import codechicken.lib.vec.Vector3;
import com.brandon3055.brandonscore.lib.TeleportUtils;
import com.brandon3055.draconicevolution.client.DEParticles;
import com.brandon3055.draconicevolution.client.render.particle.GuardianChargeParticle;
import com.brandon3055.draconicevolution.entity.guardian.DraconicGuardianEntity;
import com.brandon3055.draconicevolution.entity.guardian.GuardianFightManager;
import com.brandon3055.draconicevolution.handlers.DESounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class ChargeUpPhase extends Phase {
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

    public void resetCharge() {
        chargeTime = 0;
    }

    @Override
    public void serverTick() {
        if (chargeTime < requiredChargeTime) {
            chargeTime++;
        } else {
            chargedTime++;
        }
        if (trapPlayers && isCharged()) {
            GuardianFightManager manager = guardian.getFightManager();
            if (manager != null) {
                for (PlayerEntity player : manager.getTrackedPlayers()) {
                    if (isValidTarget(player) && player.getY() < manager.getArenaOrigin().getY() - 10) {
                        TeleportUtils.teleportEntity(player, player.level.dimension(), player.getX(), manager.getArenaOrigin().getY() + 15, player.getZ());
                    }
                }
            }
        }

        BlockPos origin = guardian.getArenaOrigin();
        if (origin == null) return;
        if (disableFlight && getChargeProgress() > 0.5) {
            GuardianFightManager manager = guardian.getFightManager();
            if (manager != null) {
                for (PlayerEntity player : manager.getTrackedPlayers()) {
                    if (player.getY() > origin.getY() + 8) {
                        if (player.abilities.flying) {
                            player.abilities.flying = false;
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
        PlayerEntity player = Minecraft.getInstance().player;
        if (player == null || !isValidTarget(player)) return;

        if (effectTimer == 0) {
            speedMod = (float) getChargeProgress();
            effectTime = effectTimer = (int) (20F - (speedMod * 10F));
            guardian.level.playLocalSound(guardian.getX(), guardian.getY(), guardian.getZ(), DESounds.crystalBeam, SoundCategory.HOSTILE, 64, 1F + speedMod, false);
            if (origin != null) {
                for (int i = 0; i < 32; i++) {
                    Minecraft.getInstance().particleEngine.add(new GuardianChargeParticle((ClientWorld) guardian.level, Vector3.fromBlockPosCenter(origin), Vector3.fromEntity(guardian), i / 32D, effectTime, guardian.getPhaseManager()));
                }
            }
        } else {
            effectTimer--;
        }

        if (trapPlayers) {
            for (int i = 0; i < 4; i++) {
                float randDir = random.nextFloat() * (float) Math.PI * 2F;
                int randDist = 95 + random.nextInt(30);
                double x = guardian.getX() + MathHelper.sin(randDir) * randDist;
                double z = guardian.getZ() + MathHelper.cos(randDir) * randDist;
                double y = guardian.getY() - 8 - random.nextInt(32);
                Vector3 motion = new Vector3(guardian.getX(), y, guardian.getZ()).subtract(x, y, z).normalize().multiply((1 + random.nextDouble()) * getChargeProgress());
                guardian.level.addParticle(DEParticles.guardian_cloud, true, x, y, z, motion.x, motion.y, motion.z);
            }

            Vector3 center = new Vector3(guardian.getX(), guardian.getY() - 32, guardian.getZ());
            int threshold = 85;

            if (player != null) {
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

        if (disableFlight && player != null && getChargeProgress() > 0.5) {
            if (player.getY() > origin.getY() + 8) {
                if (player.abilities.flying) {
                    player.abilities.flying = false;
                }
                if (player.isFallFlying()) {
                    Vector3d motion = player.getDeltaMovement();
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
        if (source.getDirectEntity() instanceof AbstractArrowEntity) {
            source.getDirectEntity().setSecondsOnFire(1);
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
}
