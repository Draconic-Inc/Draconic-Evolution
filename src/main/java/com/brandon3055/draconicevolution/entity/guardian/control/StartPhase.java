package com.brandon3055.draconicevolution.entity.guardian.control;

import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.entity.GuardianCrystalEntity;
import com.brandon3055.draconicevolution.entity.guardian.DraconicGuardianEntity;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.item.EnderCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3i;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;

/**
 * This is where it all starts. This can be thought of as the "planning" phase. This phase assesses the situation and decides on the next new phase to execute.
 * And all other phases directly or indirectly lead back here when they are done.
 */
public class StartPhase extends Phase {
    private static final Logger LOGGER = DraconicEvolution.LOGGER;
    private static final EntityPredicate playerSelector = new EntityPredicate();
    private Path currentPath;
    private Vector3d targetLocation;
    private boolean clockwise;
    private int ticksSinceTargetUpdate = 0;
    private int ticksUntilNextAttack = 0;
    private boolean immediateAttack = false;

    public StartPhase(DraconicGuardianEntity guardisn) {
        super(guardisn);
    }

    @Override
    public PhaseType<StartPhase> getType() {
        return PhaseType.START;
    }

    @Override
    public void serverTick() {
        if (ticksUntilNextAttack > 0) {
            ticksUntilNextAttack--;
        }

        double distanceFromTarget = targetLocation == null ? 0.0D : targetLocation.distanceToSqr(guardian.getX(), guardian.getY(), guardian.getZ());
        if (distanceFromTarget < 100.0D || distanceFromTarget > 22500.0D || guardian.horizontalCollision || guardian.verticalCollision || ticksSinceTargetUpdate++ > (20 * 3)) {
            ticksSinceTargetUpdate = 0;
            findNewTarget();
        }
    }

    @Override
    public void initPhase() {
        currentPath = null;
        targetLocation = null;
        ticksUntilNextAttack = 5 * 20;
        immediateAttack = false;
    }

    @Override
    @Nullable
    public Vector3d getTargetLocation() {
        return targetLocation;
    }

    private void findNewTarget() {
        if (ticksUntilNextAttack > 0) {
            resumePathing();
            return;
        }

        //Check if there is anyone harassing us.
        PlayerEntity closestToGuardian = guardian.level.getNearestPlayer(guardian.getX(), guardian.getY(), guardian.getZ(), 30, true);
        if (closestToGuardian != null && guardian.getRandom().nextFloat() < 0.25) { //25% chance we retaliate
            guardian.getPhaseManager().setPhase(PhaseType.COVER_FIRE);
            LOGGER.info("Cover Fire!!!");
            return;
        }

        //Check if we can / should enter an attack phase.
        BlockPos focus = guardian.getArenaOrigin();
        PlayerEntity player = guardian.level.getNearestPlayer(focus.getX(), focus.getY(), focus.getZ(), 192, true);
        double distanceSq;
        if (player == null || (distanceSq = guardian.distanceToSqr(player)) > 180 * 180) {
            resumePathing();
            return;
        }

        if (immediateAttack || guardian.getRandom().nextFloat() > 0.75) {
            if (guardian.getRandom().nextFloat() < 0.75) {
                if (distanceSq > 45 * 45) {
                    guardian.getPhaseManager().setPhase(PhaseType.BOMBARD_PLAYER).setTarget(player);
                    LOGGER.info("Bombarding player");
                    return;
                }
            } else {
                if (distanceSq > 45 * 45) {
                    guardian.getPhaseManager().setPhase(PhaseType.CHARGE_PLAYER).setTarget(player);
                    LOGGER.info("Charge player");
                    return;
                }
            }
        }


        resumePathing();
    }

    private void resumePathing() {
        if (this.currentPath == null || this.currentPath.isDone()) {
            int nearestIndex = this.guardian.initPathPoints(false);
            int endIndex = nearestIndex;
            if (this.guardian.getRandom().nextInt(8) == 0) {
                this.clockwise = !this.clockwise;
            }

            if (this.clockwise) {
                endIndex += 5 + guardian.getRandom().nextInt(7);
            } else {
                endIndex -= 5 + guardian.getRandom().nextInt(7);
            }

            endIndex = Math.floorMod(endIndex, 24);

            this.currentPath = this.guardian.findPath(nearestIndex, endIndex, null);
            if (this.currentPath != null) {
                this.currentPath.advance();
            }
        }

        this.navigateToNextPathNode();
    }

    public void immediateAttack() {
        ticksUntilNextAttack = 0;
        immediateAttack = true;
    }

    private void attackPlayer(PlayerEntity player) {
        if (guardian.getRandom().nextFloat() > 0.5F && guardian.distanceToSqr(player) >= 40) {
            guardian.getPhaseManager().setPhase(PhaseType.BOMBARD_PLAYER);
            guardian.getPhaseManager().getPhase(PhaseType.BOMBARD_PLAYER).setTarget(player);
        } else {
            guardian.getPhaseManager().setPhase(PhaseType.CHARGE_PLAYER);
            guardian.getPhaseManager().getPhase(PhaseType.CHARGE_PLAYER).setTarget(player);
        }
    }

    private void navigateToNextPathNode() {
        if (currentPath != null && !currentPath.isDone()) {
            Vector3i nextPos = currentPath.getNextNodePos();
            currentPath.advance();
            double x = nextPos.getX();
            double z = nextPos.getZ();
            double y = (float) nextPos.getY() + guardian.getRandom().nextFloat() * 20.0F;
            targetLocation = new Vector3d(x, y, z);
        }
    }

    @Override
    public void onCrystalDestroyed(GuardianCrystalEntity crystal, BlockPos pos, DamageSource dmgSrc, @Nullable PlayerEntity plyr) {
        if (plyr != null && !plyr.abilities.invulnerable) {
            attackPlayer(plyr);
        }
    }
}
