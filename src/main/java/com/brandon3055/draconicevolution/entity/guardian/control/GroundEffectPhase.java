package com.brandon3055.draconicevolution.entity.guardian.control;

import codechicken.lib.vec.Vector3;
import com.brandon3055.brandonscore.network.BCoreNetwork;
import com.brandon3055.brandonscore.utils.MathUtils;
import com.brandon3055.draconicevolution.entity.guardian.DraconicGuardianEntity;
import com.brandon3055.draconicevolution.entity.guardian.GuardianFightManager;
import com.brandon3055.draconicevolution.entity.guardian.GuardianProjectileEntity;
import com.brandon3055.draconicevolution.entity.guardian.GuardianWither;
import com.brandon3055.draconicevolution.init.DEContent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class GroundEffectPhase extends ChargeUpPhase {

    private List<Mob> summoned = new ArrayList<>();
    private int initialWithers = 0;
    private int spawnCount = 0;
    private int nextWither = 0;
    private int withersKilled = 0;

    private int attackStage = 0;
    private int actionDuration = 0;
    private int actionTime = 0;

    public GroundEffectPhase(DraconicGuardianEntity guardian) {
        super(guardian, 5 * 20);
        this.trapPlayers = this.disableFlight = true;
    }

    @Override
    public void serverTick() {
        super.serverTick();
        if (summoned == null) {
            guardian.level().getEntities(DEContent.ENTITY_GUARDIAN_WITHER.get(), guardian.getBoundingBox().inflate(500), e -> true).forEach(Entity::discard);
            summoned = new ArrayList<>();
        }

        if (!isCharged()) {
            return;
        }
        updateWithers();
        if (attackStage == 0 && withersKilled < initialWithers && chargedTime < (3 * 20 * 60)) {
            return;
        }

        if (actionTime >= actionDuration) {
            chargeTime = 0;
            sendPacket(null, 0);
            actionDuration = (6 * 20) + random.nextInt(6 * 20);
            actionTime = 0;
            attackStage++;
            return;
        }
        actionTime++;

        switch (attackStage) {
            case 0:
                attackStage++;
            case 1:
                updateSpinFire();
                break;
            case 2:
                updateSpinFire();
                break;
            case 3:
                updateRandomFire();
                if (actionTime >= actionDuration) {
                    guardian.getPhaseManager().setPhase(PhaseType.START);
                    guardian.level().getEntities(DEContent.ENTITY_GUARDIAN_WITHER.get(), guardian.getBoundingBox().inflate(500), e -> true).forEach(Entity::discard);
                }
                break;
        }
    }

    @Override
    public void initPhase() {
        super.initPhase();
        initialWithers = 8 + random.nextInt(6);
        summoned = null;
        withersKilled = 0;
        spawnCount = 0;
        attackStage = actionDuration = 0;
    }

    private void updateWithers() {
        withersKilled += summoned.stream().filter(e -> !e.isAlive()).count();
        summoned.removeIf(e -> !e.isAlive());

        if (nextWither > 0) {
            nextWither--;
            if (nextWither == 0) {
                summonWither(spawnCount < initialWithers);
            }
        } else if (spawnCount < initialWithers && chargedTime % 5 == 0) {
            nextWither = 3 + random.nextInt(5);
            spawnCount++;
        } else if (spawnCount >= initialWithers && withersKilled >= initialWithers && summoned.size() < 2) {
            nextWither = 80 + random.nextInt(140);
        }

        for (Mob wither : summoned) {
            Player closeTarget = guardian.level().getNearestPlayer(wither.getX(), wither.getY(), wither.getZ(), 200, true);
            if (closeTarget != null) {
                wither.setTarget(closeTarget);
            }
        }
    }

    private void summonWither(boolean canLoot) {
        GuardianWither wither = DEContent.ENTITY_GUARDIAN_WITHER.get().create(guardian.level());
        Vector3 spawnPos = null;
        for (int i = 0; i < 10; i++) {
            spawnPos = Vector3.fromEntity(guardian).add(random.nextInt(80) - 40, random.nextInt(20) - 10, random.nextInt(80) - 40);
            if (MathUtils.distanceSq(spawnPos, Vector3.fromEntity(guardian)) < 40 * 40) {
                break;
            }
        }

        wither.setPos(spawnPos.x, spawnPos.y - 10, spawnPos.z);
        wither.setCustomName(Component.translatable("entity.draconicevolution.guardian_wither"));
        wither.setInvulnerableTicks(3);

        guardian.level().addFreshEntity(wither);
        summoned.add(wither);
        wither.destroyBlocksTick = 10000;
    }

    private void updateSpinFire() {
        Vector3 headPos = Vector3.fromEntity(guardian);
        float rotation = ((guardian.yRotO - 90 - 55) / 360) * (float) Math.PI * 2F;
        headPos.add(Mth.cos(rotation) * 7, 0, Mth.sin(rotation) * 7);
        boolean perimeterHit = attackStage == 1;

        rotation += Math.PI / 2;
        BlockPos origin = guardian.getArenaOrigin();
        int range = perimeterHit ? 30 + random.nextInt(40) : random.nextInt(60);
        double targetX = guardian.getX() + (Mth.cos(rotation) * range);
        double targetY = origin == null ? guardian.getY() - 30 : origin.getY() + 15;
        double targetZ = guardian.getZ() + (Mth.sin(rotation) * range);
        Vector3 aim = new Vector3(targetX, targetY, targetZ).subtract(headPos).normalize();


        GuardianProjectileEntity projectile = new GuardianProjectileEntity(guardian.level(), guardian, aim.x, aim.y, aim.z, null, 25, GuardianFightManager.PROJECTILE_POWER);
        projectile.moveTo(headPos.x, headPos.y, headPos.z, 0.0F, 0.0F);
        guardian.level().addFreshEntity(projectile);
        BCoreNetwork.sendSound(guardian.level(), guardian, SoundEvents.ENDER_DRAGON_SHOOT, SoundSource.HOSTILE, 32.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
        BCoreNetwork.sendParticle(guardian.level(), ParticleTypes.EXPLOSION, headPos, Vector3.ZERO, true);


        guardian.setYRot(guardian.getYRot() + (1F + (((actionTime / (float) actionDuration)) * 10F)));


//        Vector3 headPos = Vector3.fromEntity(guardian);
//        float rotation = ((guardian.yRotO - 90 - 55) / 360) * (float) Math.PI * 2F;
//        headPos.add(MathHelper.cos(rotation) * 7, 0, MathHelper.sin(rotation) * 7);
//        boolean perimeterHit = attackStage == 1;
//        float aimPitch = perimeterHit ? 10 + random.nextInt(30) : 20 + random.nextInt(70);
//        Vector3d aim = calculateViewVector(145 - 90 - aimPitch, guardian.yRot);
//        GuardianProjectileEntity projectile = new GuardianProjectileEntity(guardian.level, guardian, aim.x, aim.y, aim.z, null, 25, GuardianFightManager.PROJECTILE_POWER);
//        projectile.moveTo(headPos.x, headPos.y, headPos.z, 0.0F, 0.0F);
//        guardian.level.addFreshEntity(projectile);
//        BCoreNetwork.sendSound(guardian.level, guardian, SoundEvents.ENDER_DRAGON_SHOOT, SoundCategory.HOSTILE, 32.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
//        BCoreNetwork.sendParticle(guardian.level, ParticleTypes.EXPLOSION, headPos, Vector3.ZERO, true);
//        guardian.yRot += 1F + (((actionTime / (float) actionDuration)) * 10F);
    }

    private void updateRandomFire() {
        Vector3 headPos = Vector3.fromEntity(guardian);
        float rotation = ((guardian.yRotO - 90) / 360) * (float) Math.PI * 2F;
        headPos.add(Mth.cos(rotation) * 7, 0, Mth.sin(rotation) * 7);

        for (int i = 0; i < 2; i++) {
            Vector3 target = Vector3.fromEntity(guardian).add(random.nextInt(160) - 80, -30, random.nextInt(160) - 80);
            BlockPos origin = guardian.getArenaOrigin();
            if (origin != null) {
                target.y = origin.getY() + 15;
            }
            Vector3 aim = target.subtract(headPos).normalize().multiply(3);
            GuardianProjectileEntity projectile = new GuardianProjectileEntity(guardian.level(), guardian, aim.x, aim.y, aim.z, null, 25, GuardianFightManager.PROJECTILE_POWER);
            projectile.moveTo(headPos.x, headPos.y, headPos.z, 0.0F, 0.0F);
            guardian.level().addFreshEntity(projectile);
        }

        BCoreNetwork.sendParticle(guardian.level(), ParticleTypes.EXPLOSION, headPos, Vector3.ZERO, true);
        BCoreNetwork.sendSound(guardian.level(), guardian, SoundEvents.ENDER_DRAGON_SHOOT, SoundSource.HOSTILE, 32.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
        guardian.setYRot(guardian.getYRot() + 1F);
    }

    @Override
    public PhaseType<GroundEffectPhase> getType() {
        return PhaseType.GROUND_EFFECTS;
    }

    protected final Vec3 calculateViewVector(float xRot, float yRot) {
        float f = xRot * ((float) Math.PI / 180F);
        float f1 = -yRot * ((float) Math.PI / 180F);
        float f2 = Mth.cos(f1);
        float f3 = Mth.sin(f1);
        float f4 = Mth.cos(f);
        float f5 = Mth.sin(f);
        return new Vec3(f3 * f4, -f5, f2 * f4);
    }
}
