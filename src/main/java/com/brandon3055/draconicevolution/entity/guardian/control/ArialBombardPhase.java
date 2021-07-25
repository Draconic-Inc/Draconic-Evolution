package com.brandon3055.draconicevolution.entity.guardian.control;

import codechicken.lib.data.MCDataInput;
import codechicken.lib.vec.Vector3;
import com.brandon3055.brandonscore.network.BCoreNetwork;
import com.brandon3055.draconicevolution.entity.GuardianProjectileEntity;
import com.brandon3055.draconicevolution.entity.guardian.DraconicGuardianEntity;
import com.brandon3055.draconicevolution.entity.guardian.GuardianFightManager;
import com.brandon3055.draconicevolution.network.DraconicNetwork;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;

import java.util.List;
import java.util.stream.Collectors;

public class ArialBombardPhase extends ChargeUpPhase {
    private int volleys = 0;
    private int volleysFired = 0;
    private PlayerEntity attackTarget = null;
    private int volleyRounds = 0;
    private Vector3 targetVec = null;

    public ArialBombardPhase(DraconicGuardianEntity guardian) {
        super(guardian, 5 * 20);
        disableFlight = trapPlayers = true;
    }

    @Override
    public void serverTick() {
        super.serverTick();
        Vector3 guardianPos = Vector3.fromEntity(guardian);
        if ((attackTarget == null || !isValidTarget(attackTarget)) && getChargeProgress() > 0.75) {
            Vector3d focus = Vector3d.atCenterOf(guardian.getArenaOrigin());
            List<PlayerEntity> targetOptions = guardian.level.players()
                    .stream()
                    .filter(e -> e.distanceToSqr(focus) <= 200 * 200)
                    .filter(e -> StartPhase.AGRO_TARGETS.test(guardian, e))
                    .collect(Collectors.toList());
            if (targetOptions.isEmpty()) {
                return;
            } else if (targetOptions.size() == 1) {
                attackTarget = targetOptions.get(0);
            } else {
                for (int i = 0; i < targetOptions.size() * 2; i++) {
                    PlayerEntity target = targetOptions.get(random.nextInt(targetOptions.size()));
                    RayTraceResult result = guardian.level.clip(new RayTraceContext(guardianPos.vec3(), Vector3.fromEntityCenter(target).vec3(), RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, guardian));
                    if (result.getType() == RayTraceResult.Type.MISS) {
                        attackTarget = target;
                        break;
                    }
                }
                if (attackTarget == null) {
                    attackTarget = targetOptions.get(random.nextInt(targetOptions.size()));
                }
            }
            volleyRounds = 15 + random.nextInt(5);
        } else if (attackTarget == null) {
            guardian.yRot += 5F;
            return;
        }

        //Fire Volley
        Vector3 targetPos = Vector3.fromEntity(attackTarget);
        Vector3 dirVec = targetPos.copy();
        dirVec.subtract(guardianPos);
        dirVec.normalize();
        float dirVecXZDist = MathHelper.sqrt(dirVec.x * dirVec.x + dirVec.z * dirVec.z);
        float targetYaw = (float) (MathHelper.atan2(dirVec.x, dirVec.z) * (double) (180F / (float) Math.PI));
        float targetPitch = (float) (MathHelper.atan2(dirVec.y, dirVecXZDist) * (double) (180F / (float) Math.PI));
        guardian.yRot = -targetYaw - 180;
        guardian.xRot = targetPitch + 180;
        Vector3 headPos = guardianPos.copy();
        float rotation = ((guardian.yRot - 90) / 360) * (float) Math.PI * 2F;
        headPos.add(MathHelper.cos(rotation) * 7, 0, MathHelper.sin(rotation) * 7);

        if (chargedTime < 10) return;

        if (targetVec == null) {
            targetVec = targetPos.subtract(headPos).normalize();
        }

        double randMult = 0.1;
        Vector3 randVec = targetVec.copy().add((random.nextDouble() - 0.5) * randMult, (random.nextDouble() - 0.5) * randMult, (random.nextDouble() - 0.5) * randMult);
        GuardianProjectileEntity projectile = new GuardianProjectileEntity(this.guardian.level, this.guardian, randVec.x, randVec.y, randVec.z, null, 25, GuardianFightManager.PROJECTILE_POWER);
        projectile.moveTo(headPos.x, headPos.y, headPos.z, 0.0F, 0.0F);
        guardian.level.addFreshEntity(projectile);
        BCoreNetwork.sendSound(guardian.level, guardian, SoundEvents.ENDER_DRAGON_SHOOT, SoundCategory.HOSTILE, 32.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);

        if (volleyRounds > 0) {
            volleyRounds -= 1;
        } else {
            volleysFired++;
            attackTarget = null;
            chargedTime = 0;
            chargeTime = 0;
            requiredChargeTime = 20 + (int) ((1D - (volleysFired / (double)volleys)) * (20 * 4));
            sendPacket(e -> e.writeVarInt(requiredChargeTime), 0);
            targetVec = null;
        }

        if (volleysFired >= volleys) {
            guardian.getPhaseManager().setPhase(PhaseType.START);
        }
    }

    @Override
    public void resetCharge() {
        super.resetCharge();
        chargedTime = 0;
    }

    @Override
    public void initPhase() {
        super.initPhase();
        volleys = 5 + random.nextInt(5);
        volleysFired = 0;
        volleyRounds = 0;
        targetVec = null;
        attackTarget = null;
        requiredChargeTime = 5 * 20;
    }

    @Override
    public PhaseType<ArialBombardPhase> getType() {
        return PhaseType.ARIAL_BOMBARD;
    }

    @Override
    public void handlePacket(MCDataInput input, int func) {
        if (func == 0) {
            resetCharge();
            requiredChargeTime = input.readVarInt();
        }
    }
}
