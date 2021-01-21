package com.brandon3055.draconicevolution.entity.guardian.control;

import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.entity.guardian.DraconicGuardianEntity;
import com.brandon3055.draconicevolution.entity.GuardianProjectileEntity;
import com.brandon3055.draconicevolution.entity.guardian.GuardianFightManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;

public class BombardPlayerPhase extends Phase {
    private static final Logger LOGGER = DraconicEvolution.LOGGER;
    private int tick;
    private int timeSinceStart;
    private float damageTaken;
    private boolean bombarding = false;
    private Vector3d targetLocation;
    private PlayerEntity targetPlayer;
    private int minAttackRange = 20;
    private int maxAttackRange = 90;

    public BombardPlayerPhase(DraconicGuardianEntity guardisn) {
        super(guardisn);
    }

    public void serverTick() {
        if (targetPlayer == null || !targetPlayer.isAlive()) {
            LOGGER.warn("Aborting bombardment as no target is available.");
            guardian.getPhaseManager().setPhase(PhaseType.START);
        } else if (timeSinceStart > 0 && timeSinceStart >= 20 * 8) {
            guardian.getPhaseManager().setPhase(PhaseType.START);
            LOGGER.info("Ending bombardment, Timed out");
        } else {
            double distance = targetPlayer.getDistance(guardian);
            targetLocation = targetPlayer.getPositionVec();
            //Wait for alignment with player before starting bombardment
            if (!bombarding) {
                double tRelX = targetLocation.x - guardian.getPosX();
                double tRelZ = targetLocation.z - guardian.getPosZ();
                double relTargetAngle = MathHelper.clamp(MathHelper.wrapDegrees(180.0D - MathHelper.atan2(tRelX, tRelZ) * (double) (180F / (float) Math.PI) - (double) guardian.rotationYaw), -50.0D, 50.0D);
                bombarding = Math.abs(relTargetAngle) < 1;
                if (bombarding) {
                    LOGGER.debug("Bombs Away!");
                }
            } else {
                timeSinceStart++;
            }

            if (distance < minAttackRange) {
                guardian.getPhaseManager().setPhase(PhaseType.START);
                LOGGER.info("Ending bombardment, To close");
            } else if (bombarding && distance <= maxAttackRange && timeSinceStart % 2 == 0) {
                Vector3d vector3d2 = guardian.getLook(1.0F);
                double headX = guardian.dragonPartHead.getPosX() - vector3d2.x * 1.0D;
                double headY = guardian.dragonPartHead.getPosYHeight(0.5D) + 0.5D;
                double headZ = guardian.dragonPartHead.getPosZ() - vector3d2.z * 1.0D;
                Vector3d targetPos = targetPlayer.getPositionVec().add(targetPlayer.getMotion().mul(5, 5, 5));
                targetPos = targetPos.add(guardian.getRNG().nextGaussian() * 10, guardian.getRNG().nextGaussian() * 10, guardian.getRNG().nextGaussian() * 10);
                double targetRelX = targetPos.x - headX;
                double targetRelY = targetPos.y - headY;
                double targetRelZ = targetPos.z - headZ;
                if (!guardian.isSilent()) {
                    guardian.world.playEvent(null, 1017, guardian.getPosition(), 0);
                }
                GuardianProjectileEntity projectile = new GuardianProjectileEntity(this.guardian.world, this.guardian, targetRelX, targetRelY, targetRelZ, targetPos, 25, GuardianFightManager.PROJECTILE_POWER);
                projectile.setLocationAndAngles(headX, headY, headZ, 0.0F, 0.0F);
                guardian.world.addEntity(projectile);
            }
        }

        if (tick > 10 * 20) {
            guardian.getPhaseManager().setPhase(PhaseType.START);
            LOGGER.info("Aborting charge, Master timed out");
        } else if (bombarding && timeSinceStart < 20 && timeSinceStart % 5 == 0) {
            guardian.playSound(SoundEvents.ENTITY_ENDER_DRAGON_GROWL, 20, 0.95F + (guardian.getRNG().nextFloat() * 0.2F));
        }

        tick++;
    }

    public void initPhase() {
        targetLocation = null;
        timeSinceStart = 0;
        bombarding = false;
        tick = 0;
        damageTaken = 0;
    }

    public void setTarget(PlayerEntity targetPlayer) {
        this.targetPlayer = targetPlayer;
    }

    public float getMaxRiseOrFall() {
        return 3.0F;
    }

    @Nullable
    public Vector3d getTargetLocation() {
        return targetLocation;
    }

    public PhaseType<BombardPlayerPhase> getType() {
        return PhaseType.BOMBARD_PLAYER;
    }

    @Override
    public double getGuardianSpeed() {
        double speed = 1;
        if (targetPlayer != null) {
            double distance = targetPlayer.getDistance(guardian);
            double sweetSpot = minAttackRange + ((maxAttackRange - minAttackRange) / 2D);
            speed = MathHelper.clamp((distance - sweetSpot) / 10, 0.5, 3);
        }

        return bombarding ? speed : 0.5;
    }

    @Override
    public float getYawFactor() {
        return super.getYawFactor() * (bombarding ? 2 : 1);
    }

    @Override
    public boolean highVerticalAgility() {
        return false;
    }

    @Override
    public float onAttacked(DamageSource source, float damage) {
        damageTaken += damage;
        if (damageTaken > 80) {
            guardian.getPhaseManager().setPhase(PhaseType.START);
            LOGGER.info("Aborting bombardment, Damage Taken");
        }
        return super.onAttacked(source, damage);
    }
}
