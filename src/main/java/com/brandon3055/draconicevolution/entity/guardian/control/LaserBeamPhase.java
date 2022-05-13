package com.brandon3055.draconicevolution.entity.guardian.control;

import codechicken.lib.vec.Vector3;
import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.draconicevolution.api.damage.DraconicIndirectEntityDamage;
import com.brandon3055.draconicevolution.client.sound.GuardianLaserSound;
import com.brandon3055.draconicevolution.entity.guardian.DraconicGuardianEntity;
import com.brandon3055.draconicevolution.entity.guardian.GuardianFightManager;
import com.brandon3055.draconicevolution.network.DraconicNetwork;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;
import java.util.stream.Collectors;

public class LaserBeamPhase extends ChargeUpPhase {

    private boolean soundInitialized = false;
    private Player attackTarget = null;
    private Vector3 beamPos = new Vector3();
    private DamageSource damage;
    private int laserTime = 0;
    private int maxLaserTime;

    public LaserBeamPhase(DraconicGuardianEntity guardian) {
        super(guardian, 3 * 20);
        this.damage = new DraconicIndirectEntityDamage("draconicevolution.guardian_laser", guardian, guardian, TechLevel.CHAOTIC).bypassMagic().bypassArmor().setMagic().setExplosion();
    }

    @Override
    public void serverTick() {
        super.serverTick();
        //Select Target
        if (attackTarget == null || !isValidTarget(attackTarget)) {
            Vec3 focus = Vec3.atCenterOf(guardian.getArenaOrigin());
            List<Player> targetOptions = guardian.level.players()
                    .stream()
                    .filter(e -> e.distanceToSqr(focus) <= 200 * 200)
                    .filter(e -> StartPhase.AGRO_TARGETS.test(guardian, e))
                    .collect(Collectors.toList());

            if (targetOptions.isEmpty()) {
                guardian.getPhaseManager().setPhase(PhaseType.START);
                return;
            } else if (targetOptions.size() == 1) {
                attackTarget = targetOptions.get(0);
            } else {
                attackTarget = targetOptions.get(random.nextInt(targetOptions.size()));
            }
            beamPos = Vector3.fromEntityCenter(attackTarget);
        }

        //Aim at target
        Vector3 guardianPos = Vector3.fromEntity(guardian);
        Vector3 targetPos = Vector3.fromEntity(attackTarget);
        Vector3 dirVec = targetPos.copy();
        dirVec.subtract(guardianPos);
        dirVec.normalize();
//        float dirVecXZDist = MathHelper.sqrt(dirVec.x * dirVec.x + dirVec.z * dirVec.z);
        float targetYaw = (float) (Mth.atan2(dirVec.x, dirVec.z) * (double) (180F / (float) Math.PI));
//        float targetPitch = (float) (MathHelper.atan2(dirVec.y, dirVecXZDist) * (double) (180F / (float) Math.PI));
        guardian.setYRot(-targetYaw - 180);
//        guardian.xRot = targetPitch + 180;
        Vector3 headPos = guardianPos.copy();
        float rotation = ((guardian.getYRot() - 90) / 360) * (float) Math.PI * 2F;
        headPos.add(Mth.cos(rotation) * 7, 0, Mth.sin(rotation) * 7);

        beamPos = Vector3.fromEntityCenter(attackTarget);
        boolean hit = true;
        HitResult result = guardian.level.clip(new ClipContext(headPos.vec3(), beamPos.vec3(), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, guardian));
        if (result.getType() != HitResult.Type.MISS) {
            beamPos = new Vector3(result.getLocation());
            hit = false;
        }

        if (getBeamCharge() > 0.5F) {
            float beamPower = (getBeamCharge() - 0.5F) / 0.5F;
            DraconicNetwork.sendGuardianBeam(guardian.level, headPos, beamPos, beamPower);
            if (!hit & chargedTime % 2 == 0) {
                guardian.level.explode(null, damage, null, beamPos.x, beamPos.y, beamPos.z, 8, false, Explosion.BlockInteraction.DESTROY);
            }else if (hit){
                attackTarget.hurt(damage, beamPower * 20F);
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

        if (laserTime >= maxLaserTime) {
            guardian.getPhaseManager().setPhase(PhaseType.START);
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void clientTick() {
        super.clientTick();
        if (isCharged() && !soundInitialized) {
            soundInitialized = true;
            Minecraft.getInstance().getSoundManager().play(new GuardianLaserSound(guardian.blockPosition(), this));
        }
    }

    @Override
    public void initPhase() {
        super.initPhase();
        soundInitialized = false;
        laserTime = 0;
        maxLaserTime = (4 * 30) + random.nextInt((4 * 30));
    }

    public float getBeamCharge() {
        return Math.min(chargedTime / (6F * 20F), 1F);
    }

    public float getSoundPitch() {
        return 0.5F + (getBeamCharge() * 1.5F);
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
