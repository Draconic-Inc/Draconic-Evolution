package com.brandon3055.draconicevolution.entity.guardian.control;

import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.entity.guardian.DraconicGuardianEntity;
import com.brandon3055.draconicevolution.entity.guardian.GuardianFightManager;
import com.brandon3055.draconicevolution.network.DraconicNetwork;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;

public class ChargingPlayerPhase extends Phase {
    private static final Logger LOGGER = DraconicEvolution.LOGGER;
    private int timeSinceCharge;
    private int tick;
    private float damageTaken;
    private double closestApproach;
    private boolean charging = false;
    private Vector3d targetLocation;
    private PlayerEntity targetPlayer;

    public ChargingPlayerPhase(DraconicGuardianEntity guardisn) {
        super(guardisn);
    }

    public void serverTick() {
        if (targetPlayer == null || !targetPlayer.isAlive()) {
            LOGGER.warn("Aborting charge player as no target was set.");
            guardian.getPhaseManager().setPhase(PhaseType.START);
        } else if (timeSinceCharge > 0 && timeSinceCharge >= 20 * 5) {
            guardian.getPhaseManager().setPhase(PhaseType.START);
            LOGGER.debug("Aborting charge, Timed out");
        } else {
            double distance = targetPlayer.getDistance(guardian);
            if (distance - closestApproach > 16 && timeSinceCharge > 10) {
                guardian.getPhaseManager().setPhase(PhaseType.START);
                LOGGER.debug("Aborting charge, Player got away");
            } else {
                closestApproach = Math.min(distance, closestApproach);
                targetLocation = targetPlayer.getPositionVec();
                if (!charging) {
                    double tRelX = targetLocation.x - guardian.getPosX();
                    double tRelZ = targetLocation.z - guardian.getPosZ();
                    double relTargetAngle = MathHelper.clamp(MathHelper.wrapDegrees(180.0D - MathHelper.atan2(tRelX, tRelZ) * (double) (180F / (float) Math.PI) - (double) guardian.rotationYaw), -50.0D, 50.0D);
                    charging = Math.abs(relTargetAngle) < 1;
                    if (charging) {
                        LOGGER.debug("CHARGE!");
                    }
                }
                else {
                    timeSinceCharge++;
                }

                if (distance <= 5) {
                    guardian.getPhaseManager().setPhase(PhaseType.START);
                    LOGGER.debug("Charge Successful");
                    targetPlayer.attackEntityFrom(new EntityDamageSource(DraconicEvolution.MODID + ".draconic_guardian", guardian).setDamageIsAbsolute().setDamageBypassesArmor(), GuardianFightManager.CHARGE_DAMAGE);
                    guardian.playSound(SoundEvents.ENTITY_GENERIC_EAT, 20, 0.95F + (guardian.getRNG().nextFloat() * 0.2F));
                    guardian.playSound(SoundEvents.ENTITY_GENERIC_EAT, 20, 0.95F + (guardian.getRNG().nextFloat() * 0.2F));
                    DraconicNetwork.sendImpactEffect(guardian.world, targetPlayer.getPosition(), 0);
                }
            }
        }

        if (tick > 10*20) {
            guardian.getPhaseManager().setPhase(PhaseType.START);
            LOGGER.debug("Aborting charge, Master timed out");
        }
        else if (charging && timeSinceCharge < 20 && timeSinceCharge % 5 == 0) {
            guardian.playSound(SoundEvents.ENTITY_ENDER_DRAGON_GROWL, 20, 0.95F + (guardian.getRNG().nextFloat() * 0.2F));
        }

        tick++;
    }

    public void initPhase() {
        targetLocation = null;
        timeSinceCharge = 0;
        closestApproach = 256;
        charging = false;
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

    public PhaseType<ChargingPlayerPhase> getType() {
        return PhaseType.CHARGE_PLAYER;
    }

    @Override
    public double getGuardianSpeed() {
        return charging ? 3 : 0.5;
    }

    @Override
    public float getYawFactor() {
        return super.getYawFactor() * (charging ? 2 : 1);
    }

    @Override
    public boolean highVerticalAgility() {
        return true;
    }

    @Override
    public float onAttacked(DamageSource source, float damage) {
        damageTaken += damage;
        if (damageTaken > 20) {
            guardian.getPhaseManager().setPhase(PhaseType.START);
            LOGGER.info("Aborting charge, Damage Taken");
        }
        return super.onAttacked(source, damage);
    }
}
