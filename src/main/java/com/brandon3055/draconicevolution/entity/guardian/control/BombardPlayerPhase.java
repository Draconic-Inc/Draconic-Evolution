package com.brandon3055.draconicevolution.entity.guardian.control;

import com.brandon3055.brandonscore.network.BCoreNetwork;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.entity.guardian.DraconicGuardianEntity;
import com.brandon3055.draconicevolution.entity.GuardianProjectileEntity;
import com.brandon3055.draconicevolution.entity.guardian.GuardianFightManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
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
        if (targetPlayer == null || !isValidTarget(targetPlayer)) {
            debug("Aborting bombardment as no target is available. or target is dead.");
            guardian.getPhaseManager().setPhase(PhaseType.START).prevAttackFailed();
        } else if (timeSinceStart > 0 && timeSinceStart >= 20 * 8) {
            guardian.getPhaseManager().setPhase(PhaseType.START);
            debug("Ending bombardment, Timed out");
        } else {
            double distance = targetPlayer.distanceTo(guardian);
            targetLocation = targetPlayer.position();
            //Wait for alignment with player before starting bombardment
            if (!bombarding) {
                double tRelX = targetLocation.x - guardian.getX();
                double tRelZ = targetLocation.z - guardian.getZ();
                double relTargetAngle = MathHelper.clamp(MathHelper.wrapDegrees(180.0D - MathHelper.atan2(tRelX, tRelZ) * (double) (180F / (float) Math.PI) - (double) guardian.yRot), -50.0D, 50.0D);
                bombarding = Math.abs(relTargetAngle) < 1;
                if (bombarding) {
                    debug("Bombs Away!");
                }
            } else {
                timeSinceStart++;
            }

            if (distance < minAttackRange) {
                guardian.getPhaseManager().setPhase(PhaseType.START).prevAttackFailed();
                debug("Ending bombardment, To close");
            } else if (bombarding && distance <= maxAttackRange && timeSinceStart % 2 == 0) {
                Vector3d vector3d2 = guardian.getViewVector(1.0F);
                double headX = guardian.dragonPartHead.getX() - vector3d2.x;
                double headY = guardian.dragonPartHead.getY(0.5D) + 0.5D;
                double headZ = guardian.dragonPartHead.getZ() - vector3d2.z;
                Vector3d targetPos = targetPlayer.position().add(targetPlayer.getDeltaMovement().multiply(5, 5, 5));
                targetPos = targetPos.add(guardian.getRandom().nextGaussian() * 10, guardian.getRandom().nextGaussian() * 10, guardian.getRandom().nextGaussian() * 10);
                double targetRelX = targetPos.x - headX;
                double targetRelY = targetPos.y - headY;
                double targetRelZ = targetPos.z - headZ;
                if (!guardian.isSilent()) {
                    BCoreNetwork.sendSound(guardian.level, guardian, SoundEvents.ENDER_DRAGON_SHOOT, SoundCategory.HOSTILE, 32.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
                }
                GuardianProjectileEntity projectile = new GuardianProjectileEntity(this.guardian.level, this.guardian, targetRelX, targetRelY, targetRelZ, targetPos, 25, GuardianFightManager.PROJECTILE_POWER);
                projectile.moveTo(headX, headY, headZ, 0.0F, 0.0F);
                guardian.level.addFreshEntity(projectile);
            }
        }

        if (tick > 5 * 20) {
            guardian.getPhaseManager().setPhase(PhaseType.START).prevAttackFailed();
            debug("Aborting charge, Master timed out");
        } else if (bombarding && timeSinceStart < 20 && timeSinceStart % 5 == 0) {
            guardian.playSound(SoundEvents.ENDER_DRAGON_GROWL, 20, 0.95F + (guardian.getRandom().nextFloat() * 0.2F));
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

    @Override
    public void targetPlayer(PlayerEntity player) {
        targetPlayer = player;
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
            double distance = targetPlayer.distanceTo(guardian);
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
    public float onAttacked(DamageSource source, float damage, float shield, boolean effective) {
        damageTaken += damage;
        float abortThreshold = shield > damage ? 512 : 128;
        if (damageTaken > abortThreshold && !bombarding && tick > 80) {
            guardian.getPhaseManager().setPhase(PhaseType.START).prevAttackFailed();
            debug("Aborting bombardment, Damage Taken");
        }
        return super.onAttacked(source, damage, shield, effective);
    }
}
