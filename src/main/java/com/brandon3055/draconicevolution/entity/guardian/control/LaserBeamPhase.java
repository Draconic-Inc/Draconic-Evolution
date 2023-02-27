package com.brandon3055.draconicevolution.entity.guardian.control;

import codechicken.lib.vec.Vector3;
import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.draconicevolution.api.damage.DraconicIndirectEntityDamage;
import com.brandon3055.draconicevolution.client.sound.SimpleSoundImpl;
import com.brandon3055.draconicevolution.entity.guardian.DraconicGuardianEntity;
import com.brandon3055.draconicevolution.entity.guardian.GuardianFightManager;
import com.brandon3055.draconicevolution.handlers.DESounds;
import com.brandon3055.draconicevolution.network.DraconicNetwork;
import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;
import java.util.stream.Collectors;

public class LaserBeamPhase extends ChargeUpPhase {
    private static float BUILD_UP_TIME = 6F * 20F;
    private static float SECONDARY_DELAY = 0.5F * 20F;
    private static float SECONDARY_BUILD_UP = 1F * 20F;


    private boolean soundInitialized = false;
    private boolean secondarySoundInit = false;
    private Player attackTarget = null;
    private Vector3 beamPos = new Vector3();
    private DamageSource damage;
    private int laserTime = 0;
    private int maxLaserTime;

    public LaserBeamPhase(DraconicGuardianEntity guardian) {
        super(guardian, 3 * 20);
        this.damage = new DraconicIndirectEntityDamage("draconicevolution.guardian_laser", guardian, guardian, TechLevel.CHAOTIC).bypassMagic().bypassArmor();
    }

    @Override
    public void serverTick() {
        super.serverTick();
        if (laserTime >= maxLaserTime) {
            guardian.getPhaseManager().setPhase(PhaseType.START);
            return;
        }

        if (!selectTarget()) {
            return;
        }

        //Aim at target
        Vector3 guardianPos = Vector3.fromEntity(guardian);
        Vector3 targetPos = Vector3.fromEntity(attackTarget);
        Vector3 dirVec = targetPos.copy();
        dirVec.subtract(guardianPos);
        dirVec.normalize();
        float targetYaw = (float) (Mth.atan2(dirVec.x, dirVec.z) * (double) (180F / (float) Math.PI));
        guardian.setYRot(-targetYaw - 180);
        Vector3 headPos = guardianPos.copy();
        float rotation = ((guardian.getYRot() - 90) / 360) * (float) Math.PI * 2F;
        headPos.add(Mth.cos(rotation) * 7, 0, Mth.sin(rotation) * 7);

        //Check for block Collision
        beamPos = Vector3.fromEntityCenter(attackTarget);
        boolean obstructed = false;
        HitResult result = guardian.level.clip(new ClipContext(headPos.vec3(), beamPos.vec3(), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, guardian));
        if (result.getType() != HitResult.Type.MISS) {
            beamPos = new Vector3(result.getLocation());
            obstructed = true;
        }

        if (getBeamCharge() < 0.5F) {
            return;
        }

        float beamPower = (getBeamCharge() - 0.5F) / 0.5F;
        if (fireSecondary()) {
            beamPower += getSecondaryCharge();
            DraconicNetwork.sendGuardianBeam(guardian.level, headPos, beamPos, beamPower);
            beamPower += getSecondaryCharge() * (Float.MAX_VALUE / 5F);
        } else {
            DraconicNetwork.sendGuardianBeam(guardian.level, headPos, beamPos, beamPower);
            beamPower *= 20;
        }

        if (obstructed & chargedTime % 2 == 0) {
            guardian.level.explode(null, damage, null, beamPos.x, beamPos.y, beamPos.z, 8, false, Explosion.BlockInteraction.DESTROY);
        } else if (!obstructed) {
            float prevHealth = attackTarget.getHealth();
            if (getSecondaryCharge() >= 1) {
                /*
                 * I absolutely hate that I have to do this. But seriously. If some random mid-tier armor with "100% damage absorption" can just magically block
                 * 60,000,000,000,000,000,000,000,000,000,000,000,000 hit points worth of damage like It's nothing. What the hell am I supposed to do?
                 * Guardian beam at full power = death. End of story.
                 * */
                attackTarget.getCombatTracker().recordDamage(damage, prevHealth, beamPower);
                attackTarget.setHealth(prevHealth - beamPower);
                attackTarget.gameEvent(GameEvent.ENTITY_DAMAGED, damage.getEntity());
                if (attackTarget.isDeadOrDying()) {
                    attackTarget.die(damage);
                }
            } else {
                attackTarget.hurt(damage, beamPower);
            }
        }
        if (getBeamCharge() >= 1) {
            if (laserTime == 0) {
                GuardianFightManager manager = guardian.getFightManager();
                if (manager != null) {
                    manager.guardianUpdate(guardian);
                }
            }
            laserTime++;
        }
    }

    private boolean selectTarget() {
        if (attackTarget != null && isValidTarget(attackTarget)) {
            return true;
        }

        Vec3 focus = Vec3.atCenterOf(guardian.getArenaOrigin());
        List<Player> targetOptions = guardian.level.players()
                .stream()
                .filter(e -> e.distanceToSqr(focus) <= 200 * 200)
                .filter(e -> StartPhase.AGRO_TARGETS.test(guardian, e))
                .collect(Collectors.toList());

        if (targetOptions.isEmpty()) {
            guardian.getPhaseManager().setPhase(PhaseType.START);
            return false;
        } else if (targetOptions.size() == 1) {
            attackTarget = targetOptions.get(0);
        } else {
            attackTarget = targetOptions.get(random.nextInt(targetOptions.size()));
        }
        beamPos = Vector3.fromEntityCenter(attackTarget);

        return true;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void clientTick() {
        super.clientTick();
        if (isCharged() && !soundInitialized) {
            soundInitialized = true;
            SimpleSoundImpl.create(DESounds.beam, SoundSource.HOSTILE)
                    .setPitchSupplier(() -> 0.5F + getBeamCharge())
                    .setStoppedSupplier(this::isEnded)
                    .setPos(guardian)
                    .setVolume(100)
                    .loop()
                    .play(Minecraft.getInstance());
        }
        if (fireSecondary() && !secondarySoundInit) {
            secondarySoundInit = true;
            SimpleSoundImpl.create(DESounds.beam, SoundSource.HOSTILE)
                    .setPitchSupplier(() -> 1.5F + (getSecondaryCharge() / 2F))
                    .setStoppedSupplier(this::isEnded)
                    .setPos(guardian)
                    .setVolume(100)
                    .loop()
                    .play(Minecraft.getInstance());
        }
    }

    @Override
    public void initPhase() {
        super.initPhase();
        soundInitialized = secondarySoundInit = false;
        laserTime = 0;
        maxLaserTime = (4 * 30) + random.nextInt((4 * 30));
    }

    public float getBeamCharge() {
        return Math.min(chargedTime / BUILD_UP_TIME, 1F);
    }

    public boolean fireSecondary() {
        return chargedTime - BUILD_UP_TIME - SECONDARY_DELAY > 0;
    }

    public float getSecondaryCharge() {
        return Math.min((chargedTime - BUILD_UP_TIME - SECONDARY_DELAY) / SECONDARY_BUILD_UP, 1F);
    }

    @Override
    public boolean isInvulnerable() {
        return getBeamCharge() < 0.5;
    }

    @Override
    public PhaseType<LaserBeamPhase> getType() {
        return PhaseType.LASER_BEAM;
    }
}
