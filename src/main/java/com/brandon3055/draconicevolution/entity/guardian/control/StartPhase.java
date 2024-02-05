package com.brandon3055.draconicevolution.entity.guardian.control;

import com.brandon3055.brandonscore.api.TimeKeeper;
import com.brandon3055.draconicevolution.DEConfig;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.entity.GuardianCrystalEntity;
import com.brandon3055.draconicevolution.entity.guardian.DraconicGuardianEntity;
import com.brandon3055.draconicevolution.entity.guardian.GuardianFightManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.List;

/**
 * This is where it all starts. This can be thought of as the "planning" phase. This phase assesses the situation and decides on the next new phase to execute.
 * And all other phases directly or indirectly lead back here when they are done.
 * <p>
 * Reminder. There are a fixed number of {@link PhaseType}'s but those types will create a new instance of their associated phase for each guardian entity.
 * Meaning all dater in a phase is "per-guardian" and not "global" so you dont need to worry about the possibility of simultaneous fights conflicting with each other.
 */
public class StartPhase extends Phase {
    private static final Logger LOGGER = DraconicEvolution.LOGGER;
    public static final TargetingConditions AGRO_TARGETS = TargetingConditions.forCombat().ignoreLineOfSight().range(300).selector(e -> e instanceof Player);
    private Path currentPath;
    private Vec3 targetLocation;
    private boolean clockwise;
//    private int ticksSinceTargetUpdate = 0;
//    private int ticksUntilNextAttack = 0;
//    private boolean immediateAttack = false;

    /**
     * Controls when the guardian will attack next. Every tick this value increases by x where x is the number of nearby players.
     * This value also increases by various amounts every time the guardian or its crystals are attacked.
     * The amount it increases will depend on damage dealt and destroying a crustal will instantly push it past maximum.
     * <p>
     * The way the logic works is whenever the start phase is entered we pick a random "target agro" between {@link #minAgroLevel} and {@link #maxAgroLevel}.
     * Once agroLevel is equal to or greater than this target the next attack will begin.
     */
    private float agroLevel = 0;
    private float minAgroLevel = 20 * 5; //Minimum 5 seconds before agro (Unless attached or multiple players)
    private float maxAgroLevel = 20 * 15; //Maximum 15 seconds before agro
    private float targetAgroLevel = 0;

    /**
     * This is an additional modifier that increases (up to a maximum of {@link #maxAgroModifier}) when the guardian or its crystals are attacked.
     * This then reduces by 1 every tick the guardian is not under attack. This is also a persistent value that does not reset on phase init.
     * As this value increases the {@link #minAgroLevel} and {@link #maxAgroLevel} will decrease by as much as 75% when agroModifier reaches maxAgroModifier
     * Each time the guardian takes damage agroModifier will increase by 10% of maxAgroModifier.
     */
    private int agroModifier = 0;
    private int maxAgroModifier = 2 * 60 * 20; //2 minutes

    private int failedAttacks = 0;

    public StartPhase(DraconicGuardianEntity guardisn) {
        super(guardisn);
    }

    @Override
    public PhaseType<StartPhase> getType() {
        return PhaseType.START;
    }

    @Override
    public void serverTick() {
        agroLevel += getPlayerCount();
        if (TimeKeeper.getServerTick() % 10 == 0) {
            debug("Start Phase, Target Agro: " + (targetAgroLevel / 20F) + ", Agro: " + (agroLevel / 20F));
//            guardian.level.getServer().getPlayerList().broadcastMessage(new StringTextComponent("Start Phase, Target Agro: " + (targetAgroLevel/20F) +", Agro: " + (agroLevel/20F)), ChatType.CHAT, Util.NIL_UUID);
        }

        if (agroLevel >= targetAgroLevel / Math.max(failedAttacks, 1) && startNextAttack()) {
            return;
        }

        double distanceFromTarget = targetLocation == null ? 0.0D : targetLocation.distanceToSqr(guardian.getX(), guardian.getY(), guardian.getZ());
        if (distanceFromTarget < 100.0D || distanceFromTarget > 22500.0D || guardian.horizontalCollision || guardian.verticalCollision) {
            resumePathing();
        }
    }

    @Override
    public void globalServerTick() {
        if (agroModifier > 0) {
            agroModifier--;
        }
    }

    @Override
    public void initPhase() {
        minAgroLevel = 20 * 10;
        maxAgroLevel = 20 * 30;
        if (agroModifier > maxAgroModifier) {
            agroModifier = maxAgroModifier;
        }

        currentPath = null;
        targetLocation = null;
        float agroMod = 1F - (((float) agroModifier / maxAgroModifier) * 0.75F);
        float minAgro = minAgroLevel * agroMod;
        float maxAgro = maxAgroLevel * agroMod;
        targetAgroLevel = minAgro + (random.nextFloat() * (maxAgro - minAgro));
        agroLevel = 0;
        if (guardian.level().getServer() != null) {
            debug("Start Phase, Target Agro: " + (targetAgroLevel / 20F) + ", Agro: " + (agroLevel / 20F));
        }
    }

    @Override
    @Nullable
    public Vec3 getTargetLocation() {
        return targetLocation;
    }

    private boolean startNextAttack() {
//        PlayerEntity testTarget = guardian.level.getNearestPlayer(guardian.getX(), guardian.getY(), guardian.getZ(), 200, true);
//        GuardianFightManager manager = guardian.getFightManager();
//        if (testTarget != null && manager != null && testTarget.getMainHandItem().getItem() == Items.STICK){
//            guardian.getPhaseManager().setPhase(PhaseType.APPROACH_POSITION)
//                    .setTargetLocation(Vector3d.atCenterOf(manager.getArenaOrigin().above(48)))
//                    .setNextPhase(PhaseType.GROUND_EFFECTS);
//            return true;
//        }

        Player closeTarget = guardian.level().getNearestPlayer(guardian.getX(), guardian.getY(), guardian.getZ(), 30, true);

        //Do close range / evasion strat (25% chance)
        if (closeTarget != null && ((guardian.getShieldPower() < DEConfig.guardianShield) || random.nextFloat() < 0.25F)) {
            guardian.getPhaseManager().setPhase(PhaseType.COVER_FIRE);
            return true;
        }

        boolean aggressive = agroModifier > maxAgroModifier * 0.75 || failedAttacks > 3;

        SimpleWeightedRandomList<PhaseType<?>> phases = aggressive ? PhaseType.AGGRESSIVE_WEIGHTED : PhaseType.NORMAL_WEIGHTED;

        Vec3 focus = Vec3.atCenterOf(guardian.getArenaOrigin());
        List<Player> targetOptions = guardian.level().players()
                .stream()
                .filter(e -> e.distanceToSqr(focus) <= 200 * 200)
                .filter(e -> AGRO_TARGETS.test(guardian, e))
                .map(e -> (Player) e)
                .toList();

        if (targetOptions.isEmpty()) {
            return false;
        }
        GuardianFightManager manager = guardian.getFightManager();
        if (manager == null) return false;

        PhaseType phaseType = phases.getRandomValue(random).get();
        IPhase phase = guardian.getPhaseManager().getPhase(phaseType);
        if (phase instanceof ChargeUpPhase) {
            failedAttacks = 0;
//            if (guardian.level.getServer() != null) {
//                guardian.level.getServer().getPlayerList().broadcastMessage(new StringTextComponent("Aggressive Mode: " + aggressive), ChatType.CHAT, Util.NIL_UUID);
//            }
            guardian.getPhaseManager().setPhase(PhaseType.APPROACH_POSITION).setTargetLocation(Vec3.atCenterOf(manager.getArenaOrigin().above(48))).setNextPhase(phaseType);
        } else {
            guardian.getPhaseManager().setPhase(phaseType).targetPlayer(targetOptions.get(random.nextInt(targetOptions.size())));
        }

        return false;
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

    public void immediateAttack(@Nullable Player target) {
        if (target != null) {
            attackPlayer(target);
        } else {
            agroLevel = targetAgroLevel;
        }
    }

    private void attackPlayer(Player player) {
        if (guardian.getRandom().nextFloat() > 0.5F && guardian.distanceToSqr(player) >= 40) {
            guardian.getPhaseManager().setPhase(PhaseType.BOMBARD_PLAYER).targetPlayer(player);
        } else {
            guardian.getPhaseManager().setPhase(PhaseType.CHARGE_PLAYER).targetPlayer(player);
        }
    }

    private void navigateToNextPathNode() {
        if (currentPath != null && !currentPath.isDone()) {
            Vec3i nextPos = currentPath.getNextNodePos();
            currentPath.advance();
            double x = nextPos.getX();
            double z = nextPos.getZ();
            double y = (float) nextPos.getY() + guardian.getRandom().nextFloat() * 20.0F;
            targetLocation = new Vec3(x, y, z);
        }
    }

    @Override
    public float onAttacked(DamageSource source, float damage, float shield, boolean effective) {
        if (!effective) {
            agroLevel += (targetAgroLevel * 0.5F) * (damage / 50F);
        }
        if (shield - damage > 0) {
            agroLevel += (targetAgroLevel * 0.5F) * (damage / (DEConfig.guardianShield / 10F));
        } else {
            agroLevel += (targetAgroLevel * 0.5F) * (damage / (DEConfig.guardianHealth / 10F));
        }
        agroModifier += maxAgroModifier * 0.1F;
        if (guardian.level().getServer() != null) {
            debug("Agro: " + agroLevel + ", Agro Target: " + targetAgroLevel + ", Modifier: " + agroModifier + " " + ((agroModifier / (float) maxAgroModifier) * 100) + "%");
        }
        return damage;
    }

    @Override
    public void onCrystalAttacked(GuardianCrystalEntity crystal, BlockPos pos, DamageSource dmgSrc, @Nullable Player plyr, float damage, boolean destroyed) {
        if (destroyed) {
            if (plyr != null && !plyr.getAbilities().invulnerable) {
                attackPlayer(plyr);
            } else {
                agroLevel = targetAgroLevel;
            }
        } else {
            agroLevel += (targetAgroLevel * 0.25F) * (damage / 10F);
            agroModifier += (maxAgroModifier * 0.05F) * (damage / 10F);
            if (guardian.level().getServer() != null) {
                debug("Agro: " + agroLevel + ", Agro Target: " + targetAgroLevel + ", Modifier: " + agroModifier + " " + ((agroModifier / (float) maxAgroModifier) * 100) + "%");
            }
        }
    }

    private int getPlayerCount() {
        GuardianFightManager manager = guardian.getFightManager();
        if (manager != null) {
            return manager.getTrackedPlayers().size();
        }
        return guardian.level().getNearbyPlayers(AGRO_TARGETS, guardian, guardian.getBoundingBox().inflate(244)).size();
    }

    public StartPhase prevAttackFailed() {
        failedAttacks++;
        return this;
    }
}
