package com.brandon3055.draconicevolution.entity.guardian.control;

import codechicken.lib.vec.Vector3;
import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.draconicevolution.api.damage.DraconicIndirectEntityDamage;
import com.brandon3055.draconicevolution.client.sound.GuardianLaserSound;
import com.brandon3055.draconicevolution.entity.guardian.DraconicGuardianEntity;
import com.brandon3055.draconicevolution.network.DraconicNetwork;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Explosion;

import java.util.List;
import java.util.stream.Collectors;

public class LaserBeamPhase extends ChargeUpPhase {

    private boolean soundInitialized = false;
    private PlayerEntity attackTarget = null;
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
            Vector3d focus = Vector3d.atCenterOf(guardian.getArenaOrigin());
            List<PlayerEntity> targetOptions = guardian.level.players()
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
        float targetYaw = (float) (MathHelper.atan2(dirVec.x, dirVec.z) * (double) (180F / (float) Math.PI));
//        float targetPitch = (float) (MathHelper.atan2(dirVec.y, dirVecXZDist) * (double) (180F / (float) Math.PI));
        guardian.yRot = -targetYaw - 180;
//        guardian.xRot = targetPitch + 180;
        Vector3 headPos = guardianPos.copy();
        float rotation = ((guardian.yRot - 90) / 360) * (float) Math.PI * 2F;
        headPos.add(MathHelper.cos(rotation) * 7, 0, MathHelper.sin(rotation) * 7);

        beamPos = Vector3.fromEntityCenter(attackTarget);
        boolean hit = true;
        RayTraceResult result = guardian.level.clip(new RayTraceContext(headPos.vec3(), beamPos.vec3(), RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, guardian));
        if (result.getType() != RayTraceResult.Type.MISS) {
            beamPos = new Vector3(result.location);
            hit = false;
        }

        if (getBeamCharge() > 0.5F) {
            float beamPower = (getBeamCharge() - 0.5F) / 0.5F;
            DraconicNetwork.sendGuardianBeam(guardian.level, headPos, beamPos, beamPower);
            if (!hit & chargedTime % 2 == 0) {
                guardian.level.explode(null, damage, null, beamPos.x, beamPos.y, beamPos.z, 8, false, Explosion.Mode.DESTROY);
            }else if (hit){
                attackTarget.hurt(damage, beamPower * 20F);
            }
            if (getBeamCharge() >= 1) {
                laserTime++;
            }
        }

        if (laserTime >= maxLaserTime) {
            guardian.getPhaseManager().setPhase(PhaseType.START);
        }
    }

    @Override
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
    public PhaseType<LaserBeamPhase> getType() {
        return PhaseType.LASER_BEAM;
    }
}
