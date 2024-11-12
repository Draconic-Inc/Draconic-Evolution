package com.brandon3055.draconicevolution.entity.guardian;

import com.brandon3055.brandonscore.network.BCoreNetwork;
import com.brandon3055.brandonscore.worldentity.WorldEntityHandler;
import com.brandon3055.draconicevolution.DEConfig;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.entity.GuardianCrystalEntity;
import com.brandon3055.draconicevolution.entity.guardian.control.IPhase;
import com.brandon3055.draconicevolution.entity.guardian.control.PhaseManager;
import com.brandon3055.draconicevolution.entity.guardian.control.PhaseType;
import com.brandon3055.draconicevolution.handlers.DESounds;
import com.brandon3055.draconicevolution.init.DEContent;
import com.brandon3055.draconicevolution.init.DEDamage;
import com.google.common.collect.Lists;
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
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.pathfinder.BinaryHeap;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.common.CommonHooks;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class DraconicGuardianEntity extends Mob implements Enemy {
    private static final Logger LOGGER = DraconicEvolution.LOGGER;
    public static final EntityDataAccessor<Integer> PHASE = SynchedEntityData.defineId(DraconicGuardianEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> CRYSTAL_ID = SynchedEntityData.defineId(DraconicGuardianEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Float> SHIELD_POWER = SynchedEntityData.defineId(DraconicGuardianEntity.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Optional<BlockPos>> ORIGIN = SynchedEntityData.defineId(DraconicGuardianEntity.class, EntityDataSerializers.OPTIONAL_BLOCK_POS);
    private static final TargetingConditions PLAYER_INVADER_CONDITION = TargetingConditions.forCombat().range(64.0D);
    public final double[][] ringBuffer = new double[64][3];
    public int ringBufferIndex = -1;
    private final DraconicGuardianPartEntity[] dragonParts;
    public final DraconicGuardianPartEntity dragonPartHead;
    private final DraconicGuardianPartEntity dragonPartNeck;
    private final DraconicGuardianPartEntity dragonPartBody;
    private final DraconicGuardianPartEntity dragonPartTail1;
    private final DraconicGuardianPartEntity dragonPartTail2;
    private final DraconicGuardianPartEntity dragonPartTail3;
    private final DraconicGuardianPartEntity dragonPartRightWing;
    private final DraconicGuardianPartEntity dragonPartLeftWing;
    public float oFlapTime;
    public float flapTime;
    public boolean slowed;
    public int deathTicks;
    public float yRotA;
    @Nullable
    public GuardianCrystalEntity closestGuardianCrystal;
    @Nullable
    private GuardianFightManager fightManager;
    private final PhaseManager phaseManager;
    private int growlTime = 100;
    private Node[] pathPoints = new Node[24];
    private final BinaryHeap pathFindQueue = new BinaryHeap();
    private double speedMult = 1;
    public float dpm = 0;
    private float lastDamage;
    private int hitCoolDown;

    public DraconicGuardianEntity(EntityType<?> type, Level world) {
        super(DEContent.ENTITY_DRACONIC_GUARDIAN.get(), world);
        this.dragonPartHead = new DraconicGuardianPartEntity(this, "head", 1.0F, 1.0F);
        this.dragonPartNeck = new DraconicGuardianPartEntity(this, "neck", 3.0F, 3.0F);
        this.dragonPartBody = new DraconicGuardianPartEntity(this, "body", 5.0F, 3.0F);
        this.dragonPartTail1 = new DraconicGuardianPartEntity(this, "tail", 2.0F, 2.0F);
        this.dragonPartTail2 = new DraconicGuardianPartEntity(this, "tail", 2.0F, 2.0F);
        this.dragonPartTail3 = new DraconicGuardianPartEntity(this, "tail", 2.0F, 2.0F);
        this.dragonPartRightWing = new DraconicGuardianPartEntity(this, "wing", 4.0F, 2.0F);
        this.dragonPartLeftWing = new DraconicGuardianPartEntity(this, "wing", 4.0F, 2.0F);
        this.dragonParts = new DraconicGuardianPartEntity[]{this.dragonPartHead, this.dragonPartNeck, this.dragonPartBody, this.dragonPartTail1, this.dragonPartTail2, this.dragonPartTail3, this.dragonPartRightWing, this.dragonPartLeftWing};
        this.setHealth(this.getMaxHealth());
        this.noPhysics = true;
        this.noCulling = true;
        this.phaseManager = new PhaseManager(this);
        this.setId(ENTITY_COUNTER.getAndAdd(this.dragonParts.length + 1) + 1);
    }

    @Override
    public void setId(int id) {
        super.setId(id);
        for (int i = 0; i < this.dragonParts.length; i++){
            this.dragonParts[i].setId(id + i + 1);
        }
    }

    @Override
    public void knockback(double p_147241_, double p_147242_, double p_147243_) {

    }

    public void setFightManager(GuardianFightManager fightManager) {
        this.fightManager = fightManager;
    }

    public void setArenaOrigin(BlockPos arenaOrigin) {
        entityData.set(ORIGIN, Optional.ofNullable(arenaOrigin));
    }

    public BlockPos getArenaOrigin() {
        return entityData.get(ORIGIN).orElse(null);
    }

    @Override
    public AttributeMap getAttributes() {
        return super.getAttributes();
    }

    public static AttributeSupplier.Builder registerAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, DEConfig.guardianHealth);
    }

    public float getShieldPower() {
        return entityData.get(SHIELD_POWER);
    }

    public void setShieldPower(float shieldPower) {
        entityData.set(SHIELD_POWER, shieldPower);
        GuardianFightManager manager = getFightManager();
        if (manager != null) {
            manager.guardianUpdate(this);
        }
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.getEntityData().define(PHASE, PhaseType.HOVER.getId());
        this.getEntityData().define(CRYSTAL_ID, -1);
        this.getEntityData().define(SHIELD_POWER, (float) 0);
        this.getEntityData().define(ORIGIN, Optional.empty());
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide && getShieldPower() < DEConfig.guardianShield) {
            GuardianFightManager manager = getFightManager();
            if (manager != null && manager.getNumAliveCrystals() > 0) {
                setShieldPower(Math.min(DEConfig.guardianShield, getShieldPower() + (DEConfig.guardianShield / (20F * 10F))));
            }
        }
        if (hitCoolDown > 0) {
            hitCoolDown--;
        }
    }

    public double[] getLatencyPos(int index, float partialTicks) {
        if (this.isDeadOrDying()) {
            partialTicks = 0.0F;
        }

        partialTicks = 1.0F - partialTicks;
        int i = this.ringBufferIndex - index & 63;
        int j = this.ringBufferIndex - index - 1 & 63;
        double[] adouble = new double[3];
        double d0 = this.ringBuffer[i][0];
        double d1 = Mth.wrapDegrees(this.ringBuffer[j][0] - d0);
        adouble[0] = d0 + d1 * (double) partialTicks;
        d0 = this.ringBuffer[i][1];
        d1 = this.ringBuffer[j][1] - d0;
        adouble[1] = d0 + d1 * (double) partialTicks;
        adouble[2] = Mth.lerp(partialTicks, this.ringBuffer[i][2], this.ringBuffer[j][2]);
        return adouble;
    }

    @Override
    public void aiStep() {
        speedMult = codechicken.lib.math.MathHelper.approachLinear(speedMult, phaseManager.getCurrentPhase().getGuardianSpeed(), 0.1);
        if (this.level().isClientSide) {
            this.setHealth(this.getHealth());
            if (!this.isSilent()) {
                float f = Mth.cos(this.flapTime * ((float) Math.PI * 2F));
                float f1 = Mth.cos(this.oFlapTime * ((float) Math.PI * 2F));
                if (f1 <= -0.3F && f >= -0.3F) {
                    this.level().playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.ENDER_DRAGON_FLAP, this.getSoundSource(), 5.0F, 0.8F + this.random.nextFloat() * 0.3F, false);
                }

                if (!this.phaseManager.getCurrentPhase().getIsStationary() && --this.growlTime < 0) {
                    this.level().playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.ENDER_DRAGON_GROWL, this.getSoundSource(), 2.5F, 0.8F + this.random.nextFloat() * 0.3F, false);
                    this.growlTime = 200 + this.random.nextInt(200);
                }
            }
        }

        this.oFlapTime = this.flapTime;
        if (this.isDeadOrDying()) {
            float randX = (this.random.nextFloat() - 0.5F) * 8.0F;
            float randY = (this.random.nextFloat() - 0.5F) * 4.0F;
            float randZ = (this.random.nextFloat() - 0.5F) * 8.0F;
            this.level().addParticle(ParticleTypes.EXPLOSION, this.getX() + (double) randX, this.getY() + 2.0D + (double) randY, this.getZ() + (double) randZ, 0.0D, 0.0D, 0.0D);
        } else {
            this.updateDragonEnderCrystal();
            Vec3 vec3 = this.getDeltaMovement();
            float f = 0.2F / ((float) vec3.horizontalDistance() * 10.0F + 1.0F);
            f = f * (float) Math.pow(2.0D, vec3.y);
            if (this.phaseManager.getCurrentPhase().getIsStationary()) {
                this.flapTime += 0.1F;
            } else if (this.slowed) {
                this.flapTime += f * 0.5F;
            } else {
                this.flapTime += f;
            }

            this.setYRot(Mth.wrapDegrees(this.getYRot()));
            if (this.isNoAi()) {
                this.flapTime = 0.5F;
            } else {
                if (this.ringBufferIndex < 0) {
                    for (int i = 0; i < this.ringBuffer.length; ++i) {
                        this.ringBuffer[i][0] = this.getYRot();
                        this.ringBuffer[i][1] = this.getY();
                    }
                }

                if (++this.ringBufferIndex == this.ringBuffer.length) {
                    this.ringBufferIndex = 0;
                }

                this.ringBuffer[this.ringBufferIndex][0] = this.getYRot();
                this.ringBuffer[this.ringBufferIndex][1] = this.getY();
                if (this.level().isClientSide) {
                    if (this.lerpSteps > 0) {
                        double d7 = this.getX() + (this.lerpX - this.getX()) / (double) this.lerpSteps;
                        double d0 = this.getY() + (this.lerpY - this.getY()) / (double) this.lerpSteps;
                        double d1 = this.getZ() + (this.lerpZ - this.getZ()) / (double) this.lerpSteps;
                        double d2 = Mth.wrapDegrees(this.lerpYRot - (double) this.getYRot());
                        this.setYRot((float) ((double) this.getYRot() + d2 / (double) this.lerpSteps));
                        this.setXRot((float) ((double) this.getXRot() + (this.lerpXRot - (double) this.getXRot()) / (double) this.lerpSteps));
                        --this.lerpSteps;
                        this.setPos(d7, d0, d1);
                        this.setRot(this.getYRot(), this.getXRot());
                    }

                    this.phaseManager.getCurrentPhase().clientTick();
                } else {
                    IPhase iphase = this.phaseManager.getCurrentPhase();
                    iphase.serverTick();
                    if (this.phaseManager.getCurrentPhase() != iphase) {
                        iphase = this.phaseManager.getCurrentPhase();
                        iphase.serverTick();
                    }

                    phaseManager.globalServerTick();

                    Vec3 targetLocation = iphase.getTargetLocation();
                    if (targetLocation != null) {
                        double tRelX = targetLocation.x - this.getX();
                        double tRelY = targetLocation.y - this.getY();
                        double tRelZ = targetLocation.z - this.getZ();
                        double distanceSq = tRelX * tRelX + tRelY * tRelY + tRelZ * tRelZ;
                        float maxRiseOrFall = iphase.getMaxRiseOrFall();
                        double distanceXZ = Math.sqrt(tRelX * tRelX + tRelZ * tRelZ);
                        if (distanceXZ > 0.0D) {
                            if (phaseManager.getCurrentPhase().highVerticalAgility()) {
                                tRelY = Mth.clamp(tRelY, -maxRiseOrFall, maxRiseOrFall);
                            } else {
                                tRelY = Mth.clamp(tRelY / distanceXZ, -maxRiseOrFall, maxRiseOrFall);
                            }
                        }

                        this.setDeltaMovement(this.getDeltaMovement().add(0.0D, tRelY * 0.01D, 0.0D));
                        this.setYRot(Mth.wrapDegrees(this.getYRot()));
                        //Angle of target relative to current yaw clamped to +-50
                        double relTargetAngle = Mth.clamp(Mth.wrapDegrees(180.0D - Mth.atan2(tRelX, tRelZ) * (double) (180F / (float) Math.PI) - (double) this.getYRot()), -50.0D, 50.0D);
                        if (Math.abs(relTargetAngle) < 5) {
                            relTargetAngle *= speedMult * 5; //This is a hack to help line up with the target better when traveling at higher than normal speed.
                        }


                        Vec3 targetVector = targetLocation.subtract(this.getX(), this.getY(), this.getZ()).normalize();
                        Vec3 vector3d2 = new Vec3(
                                Mth.sin(this.getYRot() * ((float) Math.PI / 180F)),
                                this.getDeltaMovement().y,
                                -Mth.cos(this.getYRot() * ((float) Math.PI / 180F)))
                                .normalize();
                        float f8 = Math.max(((float) vector3d2.dot(targetVector) + 0.5F) / 1.5F, 0.0F);
                        this.yRotA *= 0.8F;
                        this.yRotA = (float) ((double) this.yRotA + relTargetAngle * (double) iphase.getYawFactor());
                        this.setYRot(getYRot() + (this.yRotA * 0.1F));
                        float f9 = (float) (2.0D / (distanceSq + 1.0D));
                        float f10 = 0.06F;
                        this.moveRelative(f10 * (f8 * f9 + (1.0F - f9)), new Vec3(0.0D, 0.0D, -1D));
                        if (this.slowed) {
                            this.move(MoverType.SELF, this.getDeltaMovement().scale(0.8F).scale(speedMult));
                        } else {
                            this.move(MoverType.SELF, this.getDeltaMovement().scale(speedMult));
                        }
                        Vec3 vector3d3 = this.getDeltaMovement().normalize();
                        double d6 = 0.8D + 0.15D * (vector3d3.dot(vector3d2) + 1.0D) / 2.0D;
                        this.setDeltaMovement(this.getDeltaMovement().multiply(d6, 0.91F, d6));
                    }
                }

                this.yBodyRot = this.getYRot();
                Vec3[] avector3d = new Vec3[this.dragonParts.length];

                for (int j = 0; j < this.dragonParts.length; ++j) {
                    avector3d[j] = new Vec3(this.dragonParts[j].getX(), this.dragonParts[j].getY(), this.dragonParts[j].getZ());
                }

                float f15 = (float) (this.getLatencyPos(5, 1.0F)[1] - this.getLatencyPos(10, 1.0F)[1]) * 10.0F * ((float) Math.PI / 180F);
                float f16 = Mth.cos(f15);
                float f2 = Mth.sin(f15);
                float f17 = this.getYRot() * ((float) Math.PI / 180F);
                float f3 = Mth.sin(f17);
                float f18 = Mth.cos(f17);
                this.setPartPosition(this.dragonPartBody, f3 * 0.5F, 0.0D, -f18 * 0.5F);
                this.setPartPosition(this.dragonPartRightWing, f18 * 4.5F, 2.0D, f3 * 4.5F);
                this.setPartPosition(this.dragonPartLeftWing, f18 * -4.5F, 2.0D, f3 * -4.5F);
                if (!this.level().isClientSide && this.hurtTime == 0) {
                    this.collideWithEntities(this.level().getEntities(this, this.dragonPartRightWing.getBoundingBox().inflate(4.0D, 2.0D, 4.0D).move(0.0D, -2.0D, 0.0D), EntitySelector.NO_CREATIVE_OR_SPECTATOR));
                    this.collideWithEntities(this.level().getEntities(this, this.dragonPartLeftWing.getBoundingBox().inflate(4.0D, 2.0D, 4.0D).move(0.0D, -2.0D, 0.0D), EntitySelector.NO_CREATIVE_OR_SPECTATOR));
                    this.attackEntitiesInList(this.level().getEntities(this, this.dragonPartHead.getBoundingBox().inflate(1.0D), EntitySelector.NO_CREATIVE_OR_SPECTATOR));
                    this.attackEntitiesInList(this.level().getEntities(this, this.dragonPartNeck.getBoundingBox().inflate(1.0D), EntitySelector.NO_CREATIVE_OR_SPECTATOR));
                }

                float f4 = Mth.sin(this.getYRot() * ((float) Math.PI / 180F) - this.yRotA * 0.01F);
                float f19 = Mth.cos(this.getYRot() * ((float) Math.PI / 180F) - this.yRotA * 0.01F);
                float f5 = this.getHeadAndNeckYOffset();
                this.setPartPosition(this.dragonPartHead, f4 * 6.5F * f16, f5 + f2 * 6.5F, -f19 * 6.5F * f16);
                this.setPartPosition(this.dragonPartNeck, f4 * 5.5F * f16, f5 + f2 * 5.5F, -f19 * 5.5F * f16);
                double[] adouble = this.getLatencyPos(5, 1.0F);

                for (int k = 0; k < 3; ++k) {
                    DraconicGuardianPartEntity enderdragonpartentity = null;
                    if (k == 0) {
                        enderdragonpartentity = this.dragonPartTail1;
                    }

                    if (k == 1) {
                        enderdragonpartentity = this.dragonPartTail2;
                    }

                    if (k == 2) {
                        enderdragonpartentity = this.dragonPartTail3;
                    }

                    double[] adouble1 = this.getLatencyPos(12 + k * 2, 1.0F);
                    float f7 = this.getYRot() * ((float) Math.PI / 180F) + this.simplifyAngle(adouble1[0] - adouble[0]) * ((float) Math.PI / 180F);
                    float f20 = Mth.sin(f7);
                    float f21 = Mth.cos(f7);
                    float f22 = 1.5F;
                    float f23 = (float) (k + 1) * 2.0F;
                    this.setPartPosition(enderdragonpartentity, -(f3 * 1.5F + f20 * f23) * f16, adouble1[1] - adouble[1] - (double) ((f23 + 1.5F) * f2) + 1.5D, (f18 * 1.5F + f21 * f23) * f16);
                }

                if (!this.level().isClientSide) {
                    this.slowed = this.destroyBlocksInAABB(this.dragonPartHead.getBoundingBox()) | this.destroyBlocksInAABB(this.dragonPartNeck.getBoundingBox()) | this.destroyBlocksInAABB(this.dragonPartBody.getBoundingBox());
                    if (this.fightManager != null) {
                        this.fightManager.guardianUpdate(this);
                    }
                }

                for (int l = 0; l < this.dragonParts.length; ++l) {
                    this.dragonParts[l].xo = avector3d[l].x;
                    this.dragonParts[l].yo = avector3d[l].y;
                    this.dragonParts[l].zo = avector3d[l].z;
                    this.dragonParts[l].xOld = avector3d[l].x;
                    this.dragonParts[l].yOld = avector3d[l].y;
                    this.dragonParts[l].zOld = avector3d[l].z;
                }

            }
        }
    }

    private void setPartPosition(DraconicGuardianPartEntity part, double offsetX, double offsetY, double offsetZ) {
        part.setPos(this.getX() + offsetX, this.getY() + offsetY, this.getZ() + offsetZ);
    }

    private float getHeadAndNeckYOffset() {
        if (this.phaseManager.getCurrentPhase().getIsStationary()) {
            return -1.0F;
        } else {
            double[] adouble = this.getLatencyPos(5, 1.0F);
            double[] adouble1 = this.getLatencyPos(0, 1.0F);
            return (float) (adouble[1] - adouble1[1]);
        }
    }

    private void updateDragonEnderCrystal() {
        if (closestGuardianCrystal != null) {
            if (!closestGuardianCrystal.isAlive()) {
                closestGuardianCrystal = null;
            } else if (tickCount % 10 == 0 && getHealth() < getMaxHealth()) {
                setHealth(this.getHealth() + 1.0F);
            }
        }

        if (this.random.nextInt(10) == 0 && !level().isClientSide) {
            if (fightManager != null) {
                closestGuardianCrystal = fightManager.getCrystals().stream().min(Comparator.comparingDouble(this::distanceToSqr)).orElse(null);
            } else {
                List<GuardianCrystalEntity> list = this.level().getEntitiesOfClass(GuardianCrystalEntity.class, this.getBoundingBox().inflate(32.0D));
                GuardianCrystalEntity crystal = null;
                double d0 = Double.MAX_VALUE;
                for (GuardianCrystalEntity endercrystalentity1 : list) {
                    double d1 = endercrystalentity1.distanceToSqr(this);
                    if (d1 < d0) {
                        d0 = d1;
                        crystal = endercrystalentity1;
                    }
                }
                this.closestGuardianCrystal = crystal;
            }
            getEntityData().set(CRYSTAL_ID, closestGuardianCrystal == null ? -1 : closestGuardianCrystal.getId());
        }
    }

    private void collideWithEntities(List<Entity> entities) {
        double d0 = (this.dragonPartBody.getBoundingBox().minX + this.dragonPartBody.getBoundingBox().maxX) / 2.0D;
        double d1 = (this.dragonPartBody.getBoundingBox().minZ + this.dragonPartBody.getBoundingBox().maxZ) / 2.0D;

        for (Entity entity : entities) {
            if (entity instanceof LivingEntity) {
                double d2 = entity.getX() - d0;
                double d3 = entity.getZ() - d1;
                double d4 = Math.max(d2 * d2 + d3 * d3, 0.1D);
                entity.push(d2 / d4 * 4.0D, 0.2F, d3 / d4 * 4.0D);
                if (!this.phaseManager.getCurrentPhase().getIsStationary() && ((LivingEntity) entity).getLastHurtByMobTimestamp() < entity.tickCount - 2) {
                    entity.hurt(DEDamage.guardian(level(), this), 15.0F);
                    this.doEnchantDamageEffects(this, entity);
                }
            }
        }
    }

    private void attackEntitiesInList(List<Entity> entities) {
        for (Entity entity : entities) {
            if (entity instanceof LivingEntity) {
                entity.hurt(DEDamage.guardian(level(), this), 20.0F);
                this.doEnchantDamageEffects(this, entity);
            }
        }
    }

    private float simplifyAngle(double angle) {
        return (float) Mth.wrapDegrees(angle);
    }

    private boolean destroyBlocksInAABB(AABB area) {
        int i = Mth.floor(area.minX);
        int j = Mth.floor(area.minY);
        int k = Mth.floor(area.minZ);
        int l = Mth.floor(area.maxX);
        int i1 = Mth.floor(area.maxY);
        int j1 = Mth.floor(area.maxZ);
        boolean flag = false;
        boolean flag1 = false;

        for (int k1 = i; k1 <= l; ++k1) {
            for (int l1 = j; l1 <= i1; ++l1) {
                for (int i2 = k; i2 <= j1; ++i2) {
                    BlockPos blockpos = new BlockPos(k1, l1, i2);
                    BlockState blockstate = this.level().getBlockState(blockpos);
                    Block block = blockstate.getBlock();
                    if (!blockstate.isAir() && !blockstate.is(BlockTags.FIRE)) {
                        if (CommonHooks.canEntityDestroy(this.level(), blockpos, this) && !blockstate.is(BlockTags.DRAGON_IMMUNE) && block != Blocks.NETHER_BRICKS && block != Blocks.NETHER_BRICK_SLAB) {
                            flag1 = this.level().removeBlock(blockpos, false) || flag1;
                        } else {
                            flag = true;
                        }
                    }
                }
            }
        }

        if (flag1) {
            BlockPos blockpos1 = new BlockPos(i + this.random.nextInt(l - i + 1), j + this.random.nextInt(i1 - j + 1), k + this.random.nextInt(j1 - k + 1));
            this.level().levelEvent(2008, blockpos1, 0);
        }

        return flag;
    }

    public boolean attackEntityPartFrom(DraconicGuardianPartEntity part, DamageSource source, float damage) {
        if (this.phaseManager.getCurrentPhase().getType() == PhaseType.DYING || source.getEntity() == this) {
            return false;
        } else {
            float shieldPower = getShieldPower();
            if (shieldPower > 0) {
                BCoreNetwork.sendSound(level(), blockPosition(), DESounds.SHIELD_STRIKE.get(), SoundSource.HOSTILE, 20, random.nextFloat() * 0.2F + 0.9F, false);
            }

            if (hitCoolDown > 0 && damage < lastDamage * 1.1F) {
                lastDamage = damage;
                return false;
            }
            lastDamage = damage;
            hitCoolDown = 5;

            if (fightManager != null && !fightManager.onGuardianAttacked(this, source, damage)) {
                this.phaseManager.getCurrentPhase().onAttacked(source, damage, shieldPower, false);
                return false;
            }

            damage = this.phaseManager.getCurrentPhase().onAttacked(source, damage, shieldPower, true);
            if (damage > 500) damage = 500;
            shieldPower -= Math.min(shieldPower, damage);
            if (shieldPower > 0) {
                setShieldPower(shieldPower);
                return true;
            } else {
                damage -= getShieldPower();
                setShieldPower(0);
            }

            if (damage > 100) damage = 100;

            if (part != this.dragonPartHead) {
                damage = (damage / 4.0F) + Math.min(damage, 1.0F);
            }

            if (damage < 0.01F) {
                return false;
            } else {
                if (source.getEntity() instanceof Player || source.is(DamageTypes.EXPLOSION)) {
                    this.attackDragonFrom(source, damage);
                    if (this.isDeadOrDying() && !this.phaseManager.getCurrentPhase().getIsStationary()) {
                        this.setHealth(1.0F);
                        this.phaseManager.setPhase(PhaseType.DYING);
                    }
                }

                return true;
            }
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        return level().isClientSide ? false : this.attackEntityPartFrom(this.dragonPartBody, source, amount);
    }

    protected boolean attackDragonFrom(DamageSource source, float amount) {
        return super.hurt(source, amount);
    }

    @Override
    public void kill() {
        this.remove(Entity.RemovalReason.KILLED);
        if (this.fightManager != null) {
            this.fightManager.guardianUpdate(this);
            this.fightManager.processDragonDeath(this);
        }
    }

    @Override
    protected void tickDeath() {
        if (this.fightManager != null) {
            this.fightManager.guardianUpdate(this);
        }

        ++this.deathTicks;
        if (this.deathTicks >= 180 && this.deathTicks <= 200) {
            float f = (this.random.nextFloat() - 0.5F) * 8.0F;
            float f1 = (this.random.nextFloat() - 0.5F) * 4.0F;
            float f2 = (this.random.nextFloat() - 0.5F) * 8.0F;
            this.level().addParticle(ParticleTypes.EXPLOSION_EMITTER, this.getX() + (double) f, this.getY() + 2.0D + (double) f1, this.getZ() + (double) f2, 0.0D, 0.0D, 0.0D);
        }

        boolean flag = this.level().getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT);
        int xpAmount = 24000;

        if (!this.level().isClientSide) {
            if (this.deathTicks > 150 && this.deathTicks % 5 == 0 && flag) {
                this.dropExperience(Mth.floor((float) xpAmount * 0.08F));
            }

            if (this.deathTicks == 1 && !this.isSilent()) {
                this.level().globalLevelEvent(1028, this.blockPosition(), 0);
            }
        }

        this.move(MoverType.SELF, new Vec3(0.0D, 0.1F, 0.0D));
        this.setYRot(this.getYRot() + 20.0F);
        this.yBodyRot = this.getYRot();
        if (this.deathTicks == 200 && !this.level().isClientSide) {
            if (flag) {
                this.dropExperience(Mth.floor((float) xpAmount * 0.2F));
            }

            if (this.fightManager != null) {
                this.fightManager.processDragonDeath(this);
            }

            this.discard();
        }

    }

    private void dropExperience(int xp) {
        while (xp > 0) {
            int i = ExperienceOrb.getExperienceValue(xp);
            xp -= i;
            this.level().addFreshEntity(new ExperienceOrb(this.level(), this.getX(), this.getY(), this.getZ(), i));
        }

    }

    //Path finding fo the chaos island can be much simpler because the pillar ring is much bigger and the guardian never needs to fly beyond its parimeter.
    public int initPathPoints(boolean regenerate) {
        if (this.pathPoints[0] == null || regenerate) {
            if (getArenaOrigin() == null) {
                setArenaOrigin(blockPosition());
            }
            BlockPos arenaOrigin = getArenaOrigin();

            //Circle
            for (int i = 0; i < 24; i++) {
                float loopPos = i / 24F;
                float angle = loopPos * 360;
                int pointX = codechicken.lib.math.MathHelper.floor((GuardianFightManager.CRYSTAL_DIST_FROM_CENTER - 20) * Math.cos(angle * codechicken.lib.math.MathHelper.torad));
                int pointZ = codechicken.lib.math.MathHelper.floor((GuardianFightManager.CRYSTAL_DIST_FROM_CENTER - 20) * Math.sin(angle * codechicken.lib.math.MathHelper.torad));
                int pointY = Math.max(arenaOrigin.getY() + GuardianFightManager.CRYSTAL_HEIGHT_FROM_ORIGIN, this.level().getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, new BlockPos(pointX, 0, pointZ)).getY());
                pathPoints[i] = new Node(arenaOrigin.getX() + pointX, pointY, arenaOrigin.getZ() + pointZ);
            }
        }

        return this.getNearestPpIdx(this.getX(), this.getY(), this.getZ());
    }

    public int getNearestPpIdx(double x, double y, double z) {
        float f = 10000.0F;
        int i = 0;
        Node pathpoint = new Node(Mth.floor(x), Mth.floor(y), Mth.floor(z));
        int j = 0;
        if (this.fightManager == null || this.fightManager.getNumAliveCrystals() == 0) {
            j = 12;
        }

        for (int k = j; k < 24; ++k) {
            if (this.pathPoints[k] != null) {
                float f1 = this.pathPoints[k].distanceToSqr(pathpoint);
                if (f1 < f) {
                    f = f1;
                    i = k;
                }
            }
        }

        return i;
    }

    @Nullable
    public Path findPath(int startIdx, int finishIdx, @Nullable Node andThen) {
        for (int i = 0; i < 24; ++i) {
            Node pathpoint = this.pathPoints[i];
            pathpoint.closed = false;
            pathpoint.f = 0.0F;
            pathpoint.g = 0.0F;
            pathpoint.h = 0.0F;
            pathpoint.cameFrom = null;
            pathpoint.heapIdx = -1;
        }

        Node startPoint = this.pathPoints[startIdx];
        Node endPoint = this.pathPoints[finishIdx];

        startPoint.g = 0.0F;
        startPoint.h = startPoint.distanceTo(endPoint);
        startPoint.f = startPoint.h;

        this.pathFindQueue.clear();
        this.pathFindQueue.insert(startPoint);

        Node nextPoint = startPoint;
        int startIndex = 0;

        while (!this.pathFindQueue.isEmpty()) {
            Node testPoint = this.pathFindQueue.pop();
            if (testPoint.equals(endPoint)) {
                if (andThen != null) {
                    andThen.cameFrom = endPoint;
                    endPoint = andThen;
                }

                return this.makePath(startPoint, endPoint);
            }

            //If text point is closer then next point becomes test point
            if (testPoint.distanceToSqr(endPoint) < nextPoint.distanceToSqr(endPoint)) {
                nextPoint = testPoint;
            }

            testPoint.closed = true;

            for (int index = startIndex; index < 24; ++index) {
                Node pathpoint3 = this.pathPoints[index];
                if (!pathpoint3.closed) {
                    float f = testPoint.g + testPoint.distanceTo(pathpoint3);
                    if (!pathpoint3.inOpenSet() || f < pathpoint3.g) {
                        pathpoint3.cameFrom = testPoint;
                        pathpoint3.g = f;
                        pathpoint3.h = pathpoint3.distanceTo(endPoint);
                        if (pathpoint3.inOpenSet()) {
                            this.pathFindQueue.changeCost(pathpoint3, pathpoint3.g + pathpoint3.h);
                        } else {
                            pathpoint3.f = pathpoint3.g + pathpoint3.h;
                            this.pathFindQueue.insert(pathpoint3);
                        }
                    }
                }
            }
        }

        if (nextPoint == startPoint) {
            return null;
        } else {
            LOGGER.debug("Failed to find path from {} to {}", startIdx, finishIdx);
            if (andThen != null) {
                andThen.cameFrom = nextPoint;
                nextPoint = andThen;
            }

            return this.makePath(startPoint, nextPoint);
        }
    }

    private Path makePath(Node start, Node finish) {
        List<Node> list = Lists.newArrayList();
        Node pathpoint = finish;
        list.add(0, finish);

        while (pathpoint.cameFrom != null) {
            pathpoint = pathpoint.cameFrom;
            list.add(0, pathpoint);
        }

        return new Path(list, new BlockPos(finish.x, finish.y, finish.z), true);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("dragon_phase", phaseManager.getCurrentPhase().getType().getId());
        if (getArenaOrigin() != null) {
            compound.put("arena_origin", NbtUtils.writeBlockPos(getArenaOrigin()));
        }
        compound.putFloat("shield_power", getShieldPower());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("dragon_phase")) {
            phaseManager.setPhase(PhaseType.getById(compound.getInt("dragon_phase")));
        }
        if (compound.contains("arena_origin")) {
            setArenaOrigin(NbtUtils.readBlockPos(compound.getCompound("arena_origin")));
        }
        if (level() instanceof ServerLevel) {
            fightManager = WorldEntityHandler.getWorldEntities()
                    .stream()
                    .filter(e -> e instanceof GuardianFightManager)
                    .map(e -> (GuardianFightManager) e)
                    .filter(e -> getUUID().equals(e.getGuardianUniqueId()))
                    .findFirst()
                    .orElse(null);

            if (fightManager != null) {
                setArenaOrigin(fightManager.getArenaOrigin());
            }
        } else {
            fightManager = null;
        }
        if (compound.contains("shield_power", 5)) {
            setShieldPower(compound.getFloat("shield_power"));
        }
    }

    @Override
    public void checkDespawn() {
    }

    public DraconicGuardianPartEntity[] getDragonParts() {
        return this.dragonParts;
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public SoundSource getSoundSource() {
        return SoundSource.HOSTILE;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENDER_DRAGON_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.ENDER_DRAGON_HURT;
    }

    @Override
    protected float getSoundVolume() {
        return 5.0F;
    }

    @OnlyIn (Dist.CLIENT)
    public float getHeadPartYOffset(int p_184667_1_, double[] spineEndOffsets, double[] headPartOffsets) {
        IPhase iphase = this.phaseManager.getCurrentPhase();
        PhaseType<? extends IPhase> phasetype = iphase.getType();
        double d0;
        if (iphase.getIsStationary()) {
            d0 = p_184667_1_;
        } else if (p_184667_1_ == 6) {
            d0 = 0.0D;
        } else {
            d0 = headPartOffsets[1] - spineEndOffsets[1];
        }

        return (float) d0;
    }

    public void onCrystalAttacked(GuardianCrystalEntity crystal, BlockPos pos, DamageSource dmgSrc, float damage, boolean destroyed) {
        Player playerentity;
        if (dmgSrc.getEntity() instanceof Player) {
            playerentity = (Player) dmgSrc.getEntity();
        } else {
            playerentity = this.level().getNearestPlayer(PLAYER_INVADER_CONDITION, pos.getX(), pos.getY(), pos.getZ());
        }

        if (crystal == this.closestGuardianCrystal && destroyed) {
            this.attackEntityPartFrom(this.dragonPartHead, damageSources().explosion(crystal, playerentity), 20.0F);
        }

        this.phaseManager.getCurrentPhase().onCrystalAttacked(crystal, pos, dmgSrc, playerentity, damage, destroyed);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        if (PHASE.equals(key) && this.level().isClientSide) {
            this.phaseManager.setPhase(PhaseType.getById(getEntityData().get(PHASE)));
        } else if (CRYSTAL_ID.equals(key) && level().isClientSide) {
            int id = getEntityData().get(CRYSTAL_ID);
            if (id == -1) {
                closestGuardianCrystal = null;
            } else {
                Entity entity = level().getEntity(id);
                closestGuardianCrystal = entity instanceof GuardianCrystalEntity ? (GuardianCrystalEntity) entity : null;
            }
        }

        super.onSyncedDataUpdated(key);
    }

    public PhaseManager getPhaseManager() {
        return this.phaseManager;
    }

    @Nullable
    public GuardianFightManager getFightManager() {
        return this.fightManager;
    }

    @Override
    public boolean addEffect(MobEffectInstance effectInstanceIn, @org.jetbrains.annotations.Nullable Entity p_147209_) {
        if (effectInstanceIn.getEffect().isBeneficial()) {
            //This is mostly for testing purposes. I want to be able to heal the guardian
            return super.addEffect(effectInstanceIn, p_147209_);
        }
        return false;
    }

    @Override
    protected boolean canRide(Entity entityIn) {
        return false;
    }

    @Override
    public boolean canChangeDimensions() {
        return false;
    }

    @Nullable
    public DraconicGuardianPartEntity[] getParts() {
        return dragonParts;
    }

    @Override
    public boolean isMultipartEntity() {
        return true;
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return super.getAddEntityPacket();
    }
}
