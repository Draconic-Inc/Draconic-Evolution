package com.brandon3055.draconicevolution.entity.guardian.control;

import com.brandon3055.draconicevolution.DEConfig;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.entity.guardian.DraconicGuardianEntity;
import com.brandon3055.draconicevolution.entity.guardian.GuardianFightManager;
import com.brandon3055.draconicevolution.init.DEDamage;
import com.brandon3055.draconicevolution.network.DraconicNetwork;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;

public class ChargingPlayerPhase extends Phase {
    private static final Logger LOGGER = DraconicEvolution.LOGGER;
    private int timeSinceCharge;
    private int tick;
    private float damageTaken;
    private float abortDamageThreshold = 0.05F;
    private double closestApproach;
    private boolean charging = false;
    private Vec3 targetLocation;
    private Player targetPlayer;

    public ChargingPlayerPhase(DraconicGuardianEntity guardisn) {
        super(guardisn);
    }

    public void serverTick() {
        if (targetPlayer == null || isValidTarget(targetPlayer)) {
            debug("Aborting charge player as no target was set or target is dead");
            guardian.getPhaseManager().setPhase(PhaseType.START).prevAttackFailed();
        } else if (timeSinceCharge > 0 && timeSinceCharge >= 20 * 5) {
            guardian.getPhaseManager().setPhase(PhaseType.START).prevAttackFailed().prevAttackFailed();
            debug("Aborting charge, Timed out");
        } else {
            double distance = targetPlayer.distanceTo(guardian);
            if (distance - closestApproach > 16 && timeSinceCharge > 10) {
                guardian.getPhaseManager().setPhase(PhaseType.START).prevAttackFailed().prevAttackFailed();
                debug("Aborting charge, Player got away");
            } else {
                closestApproach = Math.min(distance, closestApproach);
                targetLocation = targetPlayer.position();
                if (!charging) {
                    double tRelX = targetLocation.x - guardian.getX();
                    double tRelZ = targetLocation.z - guardian.getZ();
                    double relTargetAngle = Mth.clamp(Mth.wrapDegrees(180.0D - Mth.atan2(tRelX, tRelZ) * (double) (180F / (float) Math.PI) - (double) guardian.getYRot()), -50.0D, 50.0D);
                    charging = Math.abs(relTargetAngle) < 1;
                    if (charging) {
                        debug("CHARGE!");
                    }
                }
                else {
                    timeSinceCharge++;
                }

                if (distance <= 5) {
                    guardian.getPhaseManager().setPhase(PhaseType.START);
                    debug("Charge Successful");
                    targetPlayer.hurt(DEDamage.guardian(guardian.level(), guardian), GuardianFightManager.CHARGE_DAMAGE);
                    guardian.playSound(SoundEvents.GENERIC_EAT, 20, 0.95F + (guardian.getRandom().nextFloat() * 0.2F));
                    guardian.playSound(SoundEvents.GENERIC_EAT, 20, 0.95F + (guardian.getRandom().nextFloat() * 0.2F));
                    DraconicNetwork.sendImpactEffect(guardian.level(), targetPlayer.blockPosition(), 0);
                }
            }
        }

        if (tick > 10*20) {
            guardian.getPhaseManager().setPhase(PhaseType.START).prevAttackFailed().prevAttackFailed();
            debug("Aborting charge, Master timed out");
        }
        else if (charging && timeSinceCharge < 20 && timeSinceCharge % 5 == 0) {
            guardian.playSound(SoundEvents.ENDER_DRAGON_GROWL, 20, 0.95F + (guardian.getRandom().nextFloat() * 0.2F));
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

    @Override
    public void targetPlayer(Player player) {
        targetPlayer = player;
    }

    public float getMaxRiseOrFall() {
        return 3.0F;
    }

    @Nullable
    public Vec3 getTargetLocation() {
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
    public float onAttacked(DamageSource source, float damage, float shield, boolean effective) {
        damageTaken += damage;
        if (damageTaken > (shield > 0 ? DEConfig.guardianShield * abortDamageThreshold : DEConfig.guardianHealth * abortDamageThreshold)) {
            guardian.getPhaseManager().setPhase(PhaseType.START);
            debug("Aborting charge, Damage Taken");
        }
        return super.onAttacked(source, damage, shield, effective);
    }
}
