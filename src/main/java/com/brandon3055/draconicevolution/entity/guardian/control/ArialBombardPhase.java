package com.brandon3055.draconicevolution.entity.guardian.control;

import codechicken.lib.data.MCDataInput;
import codechicken.lib.vec.Vector3;
import com.brandon3055.brandonscore.network.BCoreNetwork;
import com.brandon3055.draconicevolution.entity.guardian.DraconicGuardianEntity;
import com.brandon3055.draconicevolution.entity.guardian.GuardianFightManager;
import com.brandon3055.draconicevolution.entity.guardian.GuardianProjectileEntity;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.stream.Collectors;

public class ArialBombardPhase extends ChargeUpPhase {
    private int volleys = 0;
    private int volleysFired = 0;
    private Player attackTarget = null;
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
            Vec3 focus = Vec3.atCenterOf(guardian.getArenaOrigin());
            List<Player> targetOptions = guardian.level().players()
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
                    Player target = targetOptions.get(random.nextInt(targetOptions.size()));
                    HitResult result = guardian.level().clip(new ClipContext(guardianPos.vec3(), Vector3.fromEntityCenter(target).vec3(), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, guardian));
                    if (result.getType() == HitResult.Type.MISS) {
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
            guardian.setYRot(guardian.getYRot() + 5);
            return;
        }

        //Fire Volley
        Vector3 targetPos = Vector3.fromEntity(attackTarget);
        Vector3 dirVec = targetPos.copy();
        dirVec.subtract(guardianPos);
        dirVec.normalize();
        double dirVecXZDist = Math.sqrt(dirVec.x * dirVec.x + dirVec.z * dirVec.z);
        float targetYaw = (float) (Mth.atan2(dirVec.x, dirVec.z) * (double) (180F / (float) Math.PI));
        float targetPitch = (float) (Mth.atan2(dirVec.y, dirVecXZDist) * (double) (180F / (float) Math.PI));
        guardian.setYRot(guardian.getYRot() + (-targetYaw - 180));
        guardian.setXRot(guardian.getXRot() + (targetPitch + 180));
        Vector3 headPos = guardianPos.copy();
        float rotation = ((guardian.getYRot() - 90) / 360) * (float) Math.PI * 2F;
        headPos.add(Mth.cos(rotation) * 7, 0, Mth.sin(rotation) * 7);

        if (chargedTime < 10) return;

        if (targetVec == null) {
            targetVec = targetPos.subtract(headPos).normalize();
        }

        double randMult = 0.1;
        Vector3 randVec = targetVec.copy().add((random.nextDouble() - 0.5) * randMult, (random.nextDouble() - 0.5) * randMult, (random.nextDouble() - 0.5) * randMult);
        GuardianProjectileEntity projectile = new GuardianProjectileEntity(this.guardian.level(), this.guardian, randVec.x, randVec.y, randVec.z, null, 25, GuardianFightManager.PROJECTILE_POWER);
        projectile.moveTo(headPos.x, headPos.y, headPos.z, 0.0F, 0.0F);
        guardian.level().addFreshEntity(projectile);
        BCoreNetwork.sendSound(guardian.level(), guardian, SoundEvents.ENDER_DRAGON_SHOOT, SoundSource.HOSTILE, 32.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);

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
