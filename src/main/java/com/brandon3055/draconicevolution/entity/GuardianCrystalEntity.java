package com.brandon3055.draconicevolution.entity;

import codechicken.lib.vec.Vector3;
import com.brandon3055.brandonscore.network.BCoreNetwork;
import com.brandon3055.brandonscore.worldentity.WorldEntity;
import com.brandon3055.brandonscore.worldentity.WorldEntityHandler;
import com.brandon3055.draconicevolution.DEConfig;
import com.brandon3055.draconicevolution.entity.guardian.GuardianFightManager;
import com.brandon3055.draconicevolution.handlers.DESounds;
import com.brandon3055.draconicevolution.init.DEContent;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

public class GuardianCrystalEntity extends Entity {
    private static final DataParameter<Optional<BlockPos>> BEAM_TARGET = EntityDataManager.defineId(GuardianCrystalEntity.class, DataSerializers.OPTIONAL_BLOCK_POS);
    private static final DataParameter<Boolean> SHOW_BOTTOM = EntityDataManager.defineId(GuardianCrystalEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Float> SHIELD_POWER = EntityDataManager.defineId(GuardianCrystalEntity.class, DataSerializers.FLOAT);
    private static final DataParameter<Float> BEAM_POWER = EntityDataManager.defineId(GuardianCrystalEntity.class, DataSerializers.FLOAT);
    private static final DataParameter<Integer> UNSTABLE_TIME = EntityDataManager.defineId(GuardianCrystalEntity.class, DataSerializers.INT);
    private UUID managerId;
    public int innerRotation;
    private int beamChargeAnim = 0;

    public GuardianCrystalEntity(EntityType<?> type, World world) {
        super(type, world);
        this.blocksBuilding = true;
        this.innerRotation = this.random.nextInt(100000);
    }

    public GuardianCrystalEntity(World worldIn, double x, double y, double z, UUID managerId) {
        this(DEContent.guardianCrystal, worldIn);
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
    protected boolean isMovementNoisy() {
        return false;
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
        ++this.innerRotation;
        if (this.level instanceof ServerWorld) {
            BlockPos blockpos = this.blockPosition();
            if (getManagerId() != null && this.level.getBlockState(blockpos).isAir()) {
                this.level.setBlockAndUpdate(blockpos, AbstractFireBlock.getState(this.level, blockpos));
            }

            GuardianFightManager manager = getManager();
            if (manager != null) {
                if (getUnstableTime() > 0) {
                    setBeamTarget(null);
                    setUnstableTime(getUnstableTime() - 1);
                    if (getUnstableTime() > 0) {
                        for (int i = 0; i < 3; i++) {
                            BCoreNetwork.sendParticle(level, ParticleTypes.FIREWORK, Vector3.fromEntity(this), new Vector3(random.nextFloat(), 0.5 + (random.nextFloat() / 2), random.nextFloat()).subtract(0.5).multiply(1.5), true);
                        }
                        if (random.nextInt(3) == 0) {
                            setBeamTarget(manager.getArenaOrigin());
                            BCoreNetwork.sendSound(level, this, DESounds.crystalUnstable, SoundCategory.HOSTILE, 6F, 0.9F + (random.nextFloat() * 0.2F), false);
                        }
                    } else {
                        BCoreNetwork.sendSound(level, this, DESounds.crystalRestore, SoundCategory.HOSTILE, 8, 0.5F + this.level.random.nextFloat() * 0.2F, false);
                    }
                } else if (getShieldPower() < DEConfig.guardianCrystalShield) {
                    setShieldPower(Math.min(DEConfig.guardianCrystalShield, getShieldPower() + (DEConfig.guardianCrystalShield / 1200.0f)));
                    playChargeAnimation();
                } else if (beamChargeAnim <= 0 && manager.respawnState == null) {
                    setBeamTarget(null);
                }
                if (beamChargeAnim > 0) {
                    setBeamPower(MathHelper.sin((beamChargeAnim / 20F) * (float) Math.PI));
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
    protected void addAdditionalSaveData(CompoundNBT compound) {
        if (this.getBeamTarget() != null) {
            compound.put("BeamTarget", NBTUtil.writeBlockPos(this.getBeamTarget()));
        }

        compound.putBoolean("ShowBottom", this.shouldShowBottom());
        compound.putUUID("manager_id", managerId);
    }

    @Override
    protected void readAdditionalSaveData(CompoundNBT compound) {
        if (compound.contains("BeamTarget", 10)) {
            this.setBeamTarget(NBTUtil.readBlockPos(compound.getCompound("BeamTarget")));
        }

        if (compound.contains("ShowBottom", 1)) {
            this.setShowBottom(compound.getBoolean("ShowBottom"));
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
        if (this.isInvulnerableTo(source)) {
            return false;
        } else {
            if (!this.removed && !this.level.isClientSide) {
                GuardianFightManager manager = getManager();
                float shield = getShieldPower() / (float) DEConfig.guardianCrystalShield;
                if (shield > 0) {
                    BCoreNetwork.sendSound(level, this, DESounds.shieldStrike, SoundCategory.HOSTILE, 6, 0.5F + shield, false);
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

                this.remove();
                if (!source.isExplosion()) {
                    this.level.explode((Entity) null, this.getX(), this.getY(), this.getZ(), 10.0F, Explosion.Mode.DESTROY);
                }
                this.onCrystalAttacked(source, amount, true);
            }

            return true;
        }
    }

    private void playChargeAnimation() {
        GuardianFightManager manager = getManager();
        if (beamChargeAnim == 0 && manager != null) {
            BCoreNetwork.sendSound(level, this, DESounds.crystalBeam, SoundCategory.HOSTILE, 6, 1, false);
            beamChargeAnim = 20;
            setBeamTarget(manager.getArenaOrigin());
            setBeamPower(0);
        }
    }

    @Override
    public void kill() {
        this.onCrystalAttacked(DamageSource.GENERIC, 0, true);
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
        this.getEntityData().set(BEAM_TARGET, Optional.ofNullable(beamTarget));
    }

    public void setBeamPower(float beamPower) {
        entityData.set(BEAM_POWER, beamPower);
    }

    public float getBeamPower() {
        return entityData.get(BEAM_POWER);
    }

    @Nullable
    public BlockPos getBeamTarget() {
        return this.getEntityData().get(BEAM_TARGET).orElse((BlockPos) null);
    }

    public void setShowBottom(boolean showBottom) {
        this.getEntityData().set(SHOW_BOTTOM, showBottom);
    }

    public boolean shouldShowBottom() {
        return this.getEntityData().get(SHOW_BOTTOM);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean shouldRenderAtSqrDistance(double distance) {
        return super.shouldRenderAtSqrDistance(distance) || this.getBeamTarget() != null;
    }

    @Override
    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public void destabilize() {
        if (getUnstableTime() == 0) {
            BCoreNetwork.sendSound(level, this, DESounds.crystalDestabilize, SoundCategory.HOSTILE, 8, 0.5F + this.level.random.nextFloat() * 0.2F, false);
        }
        setUnstableTime(DEConfig.guardianCrystalUnstableWindow);
    }

    public GuardianFightManager getManager() {
        if (level instanceof ServerWorld && getManagerId() != null) {
            WorldEntity worldEntity = WorldEntityHandler.getWorldEntity(level, managerId);
            if (worldEntity instanceof GuardianFightManager) {
                return (GuardianFightManager) worldEntity;
            }
        }
        return null;
    }
}
