package com.brandon3055.draconicevolution.entity;

import codechicken.lib.vec.Vector3;
import com.brandon3055.brandonscore.network.BCoreNetwork;
import com.brandon3055.brandonscore.worldentity.WorldEntity;
import com.brandon3055.brandonscore.worldentity.WorldEntityHandler;
import com.brandon3055.draconicevolution.DEConfig;
import com.brandon3055.draconicevolution.entity.guardian.GuardianFightManager;
import com.brandon3055.draconicevolution.handlers.DESounds;
import com.brandon3055.draconicevolution.init.DEContent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.common.CommonHooks;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

public class GuardianCrystalEntity extends Entity {
    private static final EntityDataAccessor<Optional<BlockPos>> BEAM_TARGET = SynchedEntityData.defineId(GuardianCrystalEntity.class, EntityDataSerializers.OPTIONAL_BLOCK_POS);
    private static final EntityDataAccessor<Boolean> SHOW_BOTTOM = SynchedEntityData.defineId(GuardianCrystalEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> SHIELD_POWER = SynchedEntityData.defineId(GuardianCrystalEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> BEAM_POWER = SynchedEntityData.defineId(GuardianCrystalEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> UNSTABLE_TIME = SynchedEntityData.defineId(GuardianCrystalEntity.class, EntityDataSerializers.INT);
    private UUID managerId;
    public int time;
    private int beamChargeAnim = 0;

    public GuardianCrystalEntity(EntityType<?> type, Level world) {
        super(type, world);
        this.blocksBuilding = true;
        this.time = this.random.nextInt(100000);
    }

    public GuardianCrystalEntity(Level worldIn, double x, double y, double z, UUID managerId) {
        this(DEContent.ENTITY_GUARDIAN_CRYSTAL.get(), worldIn);
        this.managerId = managerId;
        this.setPos(x, y, z);
    }

    public UUID getManagerId() {
        return managerId;
    }

    public void setUnstableTime(int unstableTime) {
        entityData.set(UNSTABLE_TIME, unstableTime);
    }

    public int getUnstableTime() {
        return entityData.get(UNSTABLE_TIME);
    }

    public float getShieldPower() {
        return entityData.get(SHIELD_POWER);
    }

    public void setShieldPower(float shieldPower) {
        entityData.set(SHIELD_POWER, shieldPower);
    }

    public void setManagerId(UUID managerId) {
        this.managerId = managerId;
    }

    @Override
    public PushReaction getPistonPushReaction() {
        return PushReaction.IGNORE;
    }

    @Override
    protected void defineSynchedData() {
        this.getEntityData().define(BEAM_TARGET, Optional.empty());
        this.getEntityData().define(SHOW_BOTTOM, true);
        this.getEntityData().define(SHIELD_POWER, Math.max(20, (float) DEConfig.guardianCrystalShield));
        this.getEntityData().define(UNSTABLE_TIME, 0);
        this.getEntityData().define(BEAM_POWER, 1F);
    }

    @Override
    public void tick() {
        ++time;
        if (level() instanceof ServerLevel) {
            BlockPos blockpos = blockPosition();
            if (getManagerId() != null && level().getBlockState(blockpos).isAir()) {
                level().setBlockAndUpdate(blockpos, BaseFireBlock.getState(level(), blockpos));
            }

            GuardianFightManager manager = getManager();
            if (manager != null) {
                if (getUnstableTime() > 0) {
                    setBeamTarget(null);
                    setUnstableTime(getUnstableTime() - 1);
                    if (getUnstableTime() > 0) {
                        for (int i = 0; i < 3; i++) {
                            BCoreNetwork.sendParticle(level(), ParticleTypes.FIREWORK, Vector3.fromEntity(this), new Vector3(random.nextFloat(), 0.5 + (random.nextFloat() / 2), random.nextFloat()).subtract(0.5).multiply(1.5), true);
                        }
                        if (random.nextInt(3) == 0) {
                            setBeamTarget(manager.getArenaOrigin());
                            BCoreNetwork.sendSound(level(), this, DESounds.CRYSTAL_UNSTABLE.get(), SoundSource.HOSTILE, 6F, 0.9F + (random.nextFloat() * 0.2F), false);
                        }
                    } else {
                        BCoreNetwork.sendSound(level(), this, DESounds.CRYSTAL_RESTORE.get(), SoundSource.HOSTILE, 8, 0.5F + level().random.nextFloat() * 0.2F, false);
                    }
                } else if (getShieldPower() < DEConfig.guardianCrystalShield) {
                    setShieldPower(Math.min(DEConfig.guardianCrystalShield, getShieldPower() + (DEConfig.guardianCrystalShield / 1200.0f)));
                    playChargeAnimation();
                } else if (beamChargeAnim <= 0 && manager.respawnState == null) {
                    setBeamTarget(null);
                }
                if (beamChargeAnim > 0) {
                    setBeamPower(Mth.sin((beamChargeAnim / 20F) * (float) Math.PI));
                    beamChargeAnim--;
                    if (beamChargeAnim <= 0) {
                        beamChargeAnim = 0;
                        setBeamTarget(null);
                        setBeamPower(1);
                    }
                }
            }
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        if (getBeamTarget() != null) {
            compound.put("BeamTarget", NbtUtils.writeBlockPos(getBeamTarget()));
        }

        compound.putBoolean("ShowBottom", showsBottom());
        compound.putUUID("manager_id", managerId);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        if (compound.contains("BeamTarget", 10)) {
            setBeamTarget(NbtUtils.readBlockPos(compound.getCompound("BeamTarget")));
        }

        if (compound.contains("ShowBottom", 1)) {
            setShowBottom(compound.getBoolean("ShowBottom"));
        }
        if (compound.contains("manager_id")) {
            managerId = compound.getUUID("manager_id");
        }
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (isInvulnerableTo(source)) {
            return false;
        } else {
            if (!isRemoved() && !level().isClientSide) {
                GuardianFightManager manager = getManager();
                float shield = getShieldPower() / (float) DEConfig.guardianCrystalShield;
                if (shield > 0) {
                    BCoreNetwork.sendSound(level(), this, DESounds.SHIELD_STRIKE.get(), SoundSource.HOSTILE, 6, 0.5F + shield, false);
                }
                if (manager != null && shield > 0) {
                    float modifier = manager.getCrystalDamageModifier(this, source);
                    if (modifier == 0) {
                        playChargeAnimation();
                        return false;
                    }
                    amount *= modifier;
                }
                if (shield > 0) {
                    float newPower = (int) (getShieldPower() - amount);
                    if (newPower > 0) {
                        setShieldPower(newPower);
                        onCrystalAttacked(source, amount, false);
                        return true;
                    }
                }

                discard();
                if (!source.is(DamageTypeTags.IS_EXPLOSION)) {
                    level().explode(null, getX(), getY(), getZ(), 10.0F, Level.ExplosionInteraction.BLOCK);
                }
                onCrystalAttacked(source, amount, true);
            }

            return true;
        }
    }

    private void playChargeAnimation() {
        GuardianFightManager manager = getManager();
        if (beamChargeAnim == 0 && manager != null) {
            BCoreNetwork.sendSound(level(), this, DESounds.CRYSTAL_BEAM.get(), SoundSource.HOSTILE, 6, 1, false);
            beamChargeAnim = 20;
            setBeamTarget(manager.getArenaOrigin());
            setBeamPower(0);
        }
    }

    @Override
    public void kill() {
        onCrystalAttacked(level().damageSources().generic(), 0, true);
        super.kill();
    }

    private void onCrystalAttacked(DamageSource source, float damage, boolean destroyed) {
        GuardianFightManager manager = getManager();
        if (manager != null) {
            manager.onCrystalAttacked(this, source, damage, destroyed);
        }
    }

    public void setBeamTarget(@Nullable BlockPos beamTarget) {
        setBeamPower(1F);
        getEntityData().set(BEAM_TARGET, Optional.ofNullable(beamTarget));
    }

    public void setBeamPower(float beamPower) {
        entityData.set(BEAM_POWER, beamPower);
    }

    public float getBeamPower() {
        return entityData.get(BEAM_POWER);
    }

    @Nullable
    public BlockPos getBeamTarget() {
        return getEntityData().get(BEAM_TARGET).orElse((BlockPos) null);
    }

    public void setShowBottom(boolean showBottom) {
        getEntityData().set(SHOW_BOTTOM, showBottom);
    }

    public boolean showsBottom() {
        return getEntityData().get(SHOW_BOTTOM);
    }

    @Override
    @OnlyIn (Dist.CLIENT)
    public boolean shouldRenderAtSqrDistance(double distance) {
        return super.shouldRenderAtSqrDistance(distance) || getBeamTarget() != null;
    }

    public void destabilize() {
        if (getUnstableTime() == 0) {
            BCoreNetwork.sendSound(level(), this, DESounds.CRYSTAL_DESTABILIZE.get(), SoundSource.HOSTILE, 8, 0.5F + level().random.nextFloat() * 0.2F, false);
        }
        setUnstableTime(DEConfig.guardianCrystalUnstableWindow);
    }

    public GuardianFightManager getManager() {
        if (level() instanceof ServerLevel && getManagerId() != null) {
            WorldEntity worldEntity = WorldEntityHandler.getWorldEntity(level(), managerId);
            if (worldEntity instanceof GuardianFightManager) {
                return (GuardianFightManager) worldEntity;
            }
        }
        return null;
    }
}
