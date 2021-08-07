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
import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierManager;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathHeap;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.potion.EffectInstance;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.EndPodiumFeature;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class DraconicGuardianEntity extends MobEntity implements IMob {
    private static final Logger LOGGER = DraconicEvolution.LOGGER;
    public static final DataParameter<Integer> PHASE = EntityDataManager.defineId(DraconicGuardianEntity.class, DataSerializers.INT);
    public static final DataParameter<Integer> CRYSTAL_ID = EntityDataManager.defineId(DraconicGuardianEntity.class, DataSerializers.INT);
    public static final DataParameter<Float> SHIELD_POWER = EntityDataManager.defineId(DraconicGuardianEntity.class, DataSerializers.FLOAT);
    public static final DataParameter<Optional<BlockPos>> ORIGIN = EntityDataManager.defineId(DraconicGuardianEntity.class, DataSerializers.OPTIONAL_BLOCK_POS);
    private static final EntityPredicate PLAYER_INVADER_CONDITION = (new EntityPredicate()).range(64.0D);
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
    public float prevAnimTime;
    public float animTime;
    public boolean slowed;
    public int deathTicks;
    public float yRotA;
    @Nullable
    public GuardianCrystalEntity closestGuardianCrystal;
    @Nullable
    private GuardianFightManager fightManager;
    private final PhaseManager phaseManager;
    private int growlTime = 100;
    private PathPoint[] pathPoints = new PathPoint[24];
    private final PathHeap pathFindQueue = new PathHeap();
    private double speedMult = 1;
    public float dpm = 0;
    private float lastDamage;
    private int hitCoolDown;

    public DraconicGuardianEntity(EntityType<?> type, World world) {
        super(DEContent.draconicGuardian, world);
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
    }

    @Override
    public void knockback(float strength, double ratioX, double ratioZ) {
//        super.applyKnockback(strength, ratioX, ratioZ); //NO!
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
    public AttributeModifierManager getAttributes() {
        return super.getAttributes();
    }

    public static AttributeModifierMap.MutableAttribute registerAttributes() {
        return MobEntity.createMobAttributes().add(Attributes.MAX_HEALTH, DEConfig.guardianHealth);
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
        if (!level.isClientSide && getShieldPower() < DEConfig.guardianShield) {
            GuardianFightManager manager = getFightManager();
            if (manager != null && manager.getNumAliveCrystals() > 0) {
                setShieldPower(Math.min(DEConfig.guardianShield, getShieldPower() + (DEConfig.guardianShield / (20F * 10F))));
            }
        }
        if (hitCoolDown > 0) {
            hitCoolDown--;
        }
    }

    public double[] getMovementOffsets(int index, float partialTicks) {
        if (this.isDeadOrDying()) {
            partialTicks = 0.0F;
        }

        partialTicks = 1.0F - partialTicks;
        int i = this.ringBufferIndex - index & 63;
        int j = this.ringBufferIndex - index - 1 & 63;
        double[] adouble = new double[3];
        double d0 = this.ringBuffer[i][0];
        double d1 = MathHelper.wrapDegrees(this.ringBuffer[j][0] - d0);
        adouble[0] = d0 + d1 * (double) partialTicks;
        d0 = this.ringBuffer[i][1];
        d1 = this.ringBuffer[j][1] - d0;
        adouble[1] = d0 + d1 * (double) partialTicks;
        adouble[2] = MathHelper.lerp(partialTicks, this.ringBuffer[i][2], this.ringBuffer[j][2]);
        return adouble;
    }

    @Override
    public void aiStep() {
        speedMult = codechicken.lib.math.MathHelper.approachLinear(speedMult, phaseManager.getCurrentPhase().getGuardianSpeed(), 0.1);
//        phaseManager.setPhase(PhaseType.START);
        if (this.level.isClientSide) {
            this.setHealth(this.getHealth());
            if (!this.isSilent()) {
                float f = MathHelper.cos(this.animTime * ((float) Math.PI * 2F));
                float f1 = MathHelper.cos(this.prevAnimTime * ((float) Math.PI * 2F));
                if (f1 <= -0.3F && f >= -0.3F) {
                    this.level.playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.ENDER_DRAGON_FLAP, this.getSoundSource(), 5.0F, 0.8F + this.random.nextFloat() * 0.3F, false);
                }

                if (!this.phaseManager.getCurrentPhase().getIsStationary() && --this.growlTime < 0) {
                    this.level.playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.ENDER_DRAGON_GROWL, this.getSoundSource(), 2.5F, 0.8F + this.random.nextFloat() * 0.3F, false);
                    this.growlTime = 200 + this.random.nextInt(200);
                }
            }
        } else {
//            updateShieldState();
        }

        this.prevAnimTime = this.animTime;
        if (this.isDeadOrDying()) {
            float randX = (this.random.nextFloat() - 0.5F) * 8.0F;
            float randY = (this.random.nextFloat() - 0.5F) * 4.0F;
            float randZ = (this.random.nextFloat() - 0.5F) * 8.0F;
            this.level.addParticle(ParticleTypes.EXPLOSION, this.getX() + (double) randX, this.getY() + 2.0D + (double) randY, this.getZ() + (double) randZ, 0.0D, 0.0D, 0.0D);
        } else {
            this.updateDragonEnderCrystal();
            Vector3d vector3d4 = this.getDeltaMovement();
            float f12 = 0.2F / (MathHelper.sqrt(getHorizontalDistanceSqr(vector3d4)) * 10.0F + 1.0F);
            f12 = f12 * (float) Math.pow(2.0D, vector3d4.y);
            if (this.phaseManager.getCurrentPhase().getIsStationary()) {
                this.animTime += 0.1F;
            } else if (this.slowed) {
                this.animTime += f12 * 0.5F;
            } else {
                this.animTime += f12;
            }

            this.yRot = MathHelper.wrapDegrees(this.yRot);
            if (this.isNoAi()) {
                this.animTime = 0.5F;
            } else {
                if (this.ringBufferIndex < 0) {
                    for (int i = 0; i < this.ringBuffer.length; ++i) {
                        this.ringBuffer[i][0] = this.yRot;
                        this.ringBuffer[i][1] = this.getY();
                    }
                }

                if (++this.ringBufferIndex == this.ringBuffer.length) {
                    this.ringBufferIndex = 0;
                }

                this.ringBuffer[this.ringBufferIndex][0] = this.yRot;
                this.ringBuffer[this.ringBufferIndex][1] = this.getY();
                if (this.level.isClientSide) {
                    if (this.lerpSteps > 0) {
                        double d7 = this.getX() + (this.lerpX - this.getX()) / (double) this.lerpSteps;
                        double d0 = this.getY() + (this.lerpY - this.getY()) / (double) this.lerpSteps;
                        double d1 = this.getZ() + (this.lerpZ - this.getZ()) / (double) this.lerpSteps;
                        double d2 = MathHelper.wrapDegrees(this.lerpYRot - (double) this.yRot);
                        this.yRot = (float) ((double) this.yRot + d2 / (double) this.lerpSteps);
                        this.xRot = (float) ((double) this.xRot + (this.lerpXRot - (double) this.xRot) / (double) this.lerpSteps);
                        --this.lerpSteps;
                        this.setPos(d7, d0, d1);
                        this.setRot(this.yRot, this.xRot);
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

                    Vector3d targetLocation = iphase.getTargetLocation();
                    if (targetLocation != null) {
                        double tRelX = targetLocation.x - this.getX();
                        double tRelY = targetLocation.y - this.getY();
                        double tRelZ = targetLocation.z - this.getZ();
                        double distanceSq = tRelX * tRelX + tRelY * tRelY + tRelZ * tRelZ;
                        float maxRiseOrFall = iphase.getMaxRiseOrFall();
                        double distanceXZ = MathHelper.sqrt(tRelX * tRelX + tRelZ * tRelZ);
                        if (distanceXZ > 0.0D) {
                            if (phaseManager.getCurrentPhase().highVerticalAgility()) {
                                tRelY = MathHelper.clamp(tRelY, -maxRiseOrFall, maxRiseOrFall);
                            } else {
                                tRelY = MathHelper.clamp(tRelY / distanceXZ, -maxRiseOrFall, maxRiseOrFall);
                            }
                        }

                        this.setDeltaMovement(this.getDeltaMovement().add(0.0D, tRelY * 0.01D, 0.0D));
                        this.yRot = MathHelper.wrapDegrees(this.yRot);
                        //Angle of target relative to current yaw clamped to +-50
                        double relTargetAngle = MathHelper.clamp(MathHelper.wrapDegrees(180.0D - MathHelper.atan2(tRelX, tRelZ) * (double) (180F / (float) Math.PI) - (double) this.yRot), -50.0D, 50.0D);
                        if (Math.abs(relTargetAngle) < 5) {
                            relTargetAngle *= speedMult * 5; //This is a hack to help line up with the target better when traveling at higher than normal speed.
                        }


                        Vector3d targetVector = targetLocation.subtract(this.getX(), this.getY(), this.getZ()).normalize();
                        Vector3d vector3d2 = new Vector3d(
                                MathHelper.sin(this.yRot * ((float) Math.PI / 180F)),
                                this.getDeltaMovement().y,
                                -MathHelper.cos(this.yRot * ((float) Math.PI / 180F)))
                                .normalize();
                        float f8 = Math.max(((float) vector3d2.dot(targetVector) + 0.5F) / 1.5F, 0.0F);
                        this.yRotA *= 0.8F;
                        this.yRotA = (float) ((double) this.yRotA + relTargetAngle * (double) iphase.getYawFactor());
                        this.yRot += this.yRotA * 0.1F;
                        float f9 = (float) (2.0D / (distanceSq + 1.0D));
                        float f10 = 0.06F;
                        this.moveRelative(f10 * (f8 * f9 + (1.0F - f9)), new Vector3d(0.0D, 0.0D, -1D));
                        if (this.slowed) {
                            this.move(MoverType.SELF, this.getDeltaMovement().scale(0.8F).scale(speedMult));
                        } else {
                            this.move(MoverType.SELF, this.getDeltaMovement().scale(speedMult));
                        }
                        Vector3d vector3d3 = this.getDeltaMovement().normalize();
                        double d6 = 0.8D + 0.15D * (vector3d3.dot(vector3d2) + 1.0D) / 2.0D;
                        this.setDeltaMovement(this.getDeltaMovement().multiply(d6, 0.91F, d6));
                    }
                }

                this.yBodyRot = this.yRot;
                Vector3d[] avector3d = new Vector3d[this.dragonParts.length];

                for (int j = 0; j < this.dragonParts.length; ++j) {
                    avector3d[j] = new Vector3d(this.dragonParts[j].getX(), this.dragonParts[j].getY(), this.dragonParts[j].getZ());
                }

                float f15 = (float) (this.getMovementOffsets(5, 1.0F)[1] - this.getMovementOffsets(10, 1.0F)[1]) * 10.0F * ((float) Math.PI / 180F);
                float f16 = MathHelper.cos(f15);
                float f2 = MathHelper.sin(f15);
                float f17 = this.yRot * ((float) Math.PI / 180F);
                float f3 = MathHelper.sin(f17);
                float f18 = MathHelper.cos(f17);
                this.setPartPosition(this.dragonPartBody, f3 * 0.5F, 0.0D, -f18 * 0.5F);
                this.setPartPosition(this.dragonPartRightWing, f18 * 4.5F, 2.0D, f3 * 4.5F);
                this.setPartPosition(this.dragonPartLeftWing, f18 * -4.5F, 2.0D, f3 * -4.5F);
                if (!this.level.isClientSide && this.hurtTime == 0) {
                    this.collideWithEntities(this.level.getEntities(this, this.dragonPartRightWing.getBoundingBox().inflate(4.0D, 2.0D, 4.0D).move(0.0D, -2.0D, 0.0D), EntityPredicates.NO_CREATIVE_OR_SPECTATOR));
                    this.collideWithEntities(this.level.getEntities(this, this.dragonPartLeftWing.getBoundingBox().inflate(4.0D, 2.0D, 4.0D).move(0.0D, -2.0D, 0.0D), EntityPredicates.NO_CREATIVE_OR_SPECTATOR));
                    this.attackEntitiesInList(this.level.getEntities(this, this.dragonPartHead.getBoundingBox().inflate(1.0D), EntityPredicates.NO_CREATIVE_OR_SPECTATOR));
                    this.attackEntitiesInList(this.level.getEntities(this, this.dragonPartNeck.getBoundingBox().inflate(1.0D), EntityPredicates.NO_CREATIVE_OR_SPECTATOR));
                }

                float f4 = MathHelper.sin(this.yRot * ((float) Math.PI / 180F) - this.yRotA * 0.01F);
                float f19 = MathHelper.cos(this.yRot * ((float) Math.PI / 180F) - this.yRotA * 0.01F);
                float f5 = this.getHeadAndNeckYOffset();
                this.setPartPosition(this.dragonPartHead, f4 * 6.5F * f16, f5 + f2 * 6.5F, -f19 * 6.5F * f16);
                this.setPartPosition(this.dragonPartNeck, f4 * 5.5F * f16, f5 + f2 * 5.5F, -f19 * 5.5F * f16);
                double[] adouble = this.getMovementOffsets(5, 1.0F);

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

                    double[] adouble1 = this.getMovementOffsets(12 + k * 2, 1.0F);
                    float f7 = this.yRot * ((float) Math.PI / 180F) + this.simplifyAngle(adouble1[0] - adouble[0]) * ((float) Math.PI / 180F);
                    float f20 = MathHelper.sin(f7);
                    float f21 = MathHelper.cos(f7);
                    float f22 = 1.5F;
                    float f23 = (float) (k + 1) * 2.0F;
                    this.setPartPosition(enderdragonpartentity, -(f3 * 1.5F + f20 * f23) * f16, adouble1[1] - adouble[1] - (double) ((f23 + 1.5F) * f2) + 1.5D, (f18 * 1.5F + f21 * f23) * f16);
                }

                if (!this.level.isClientSide) {
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
            double[] adouble = this.getMovementOffsets(5, 1.0F);
            double[] adouble1 = this.getMovementOffsets(0, 1.0F);
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

        if (this.random.nextInt(10) == 0 && !level.isClientSide) {
            if (fightManager != null) {
                closestGuardianCrystal = fightManager.getCrystals().stream().min(Comparator.comparingDouble(this::distanceToSqr)).orElse(null);
            } else {
                List<GuardianCrystalEntity> list = this.level.getEntitiesOfClass(GuardianCrystalEntity.class, this.getBoundingBox().inflate(32.0D));
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
                    entity.hurt(new EntityDamageSource(DraconicEvolution.MODID + ".draconic_guardian", this), 15.0F);
                    this.doEnchantDamageEffects(this, entity);
                }
            }
        }

    }

    private void attackEntitiesInList(List<Entity> entities) {
        for (Entity entity : entities) {
            if (entity instanceof LivingEntity) {
                entity.hurt(new EntityDamageSource(DraconicEvolution.MODID + ".draconic_guardian", this), 20.0F);
                this.doEnchantDamageEffects(this, entity);
            }
        }

    }

    private float simplifyAngle(double angle) {
        return (float) MathHelper.wrapDegrees(angle);
    }

    private boolean destroyBlocksInAABB(AxisAlignedBB area) {
        int i = MathHelper.floor(area.minX);
        int j = MathHelper.floor(area.minY);
        int k = MathHelper.floor(area.minZ);
        int l = MathHelper.floor(area.maxX);
        int i1 = MathHelper.floor(area.maxY);
        int j1 = MathHelper.floor(area.maxZ);
        boolean flag = false;
        boolean flag1 = false;

        for (int k1 = i; k1 <= l; ++k1) {
            for (int l1 = j; l1 <= i1; ++l1) {
                for (int i2 = k; i2 <= j1; ++i2) {
                    BlockPos blockpos = new BlockPos(k1, l1, i2);
                    BlockState blockstate = this.level.getBlockState(blockpos);
                    Block block = blockstate.getBlock();
                    if (!blockstate.isAir(this.level, blockpos) && blockstate.getMaterial() != Material.FIRE) {
                        if (net.minecraftforge.common.ForgeHooks.canEntityDestroy(this.level, blockpos, this) && !BlockTags.DRAGON_IMMUNE.contains(block) && block != Blocks.NETHER_BRICKS && block != Blocks.NETHER_BRICK_SLAB) {
                            flag1 = this.level.removeBlock(blockpos, false) || flag1;
                        } else {
                            flag = true;
                        }
                    }
                }
            }
        }

        if (flag1) {
            BlockPos blockpos1 = new BlockPos(i + this.random.nextInt(l - i + 1), j + this.random.nextInt(i1 - j + 1), k + this.random.nextInt(j1 - k + 1));
            this.level.levelEvent(2008, blockpos1, 0);
        }

        return flag;
    }

    public boolean attackEntityPartFrom(DraconicGuardianPartEntity part, DamageSource source, float damage) {
        if (this.phaseManager.getCurrentPhase().getType() == PhaseType.DYING) {
            return false;
        } else {
            float shieldPower = getShieldPower();
            if (shieldPower > 0) {
                BCoreNetwork.sendSound(level, blockPosition(), DESounds.shieldStrike, SoundCategory.HOSTILE, 20, random.nextFloat() * 0.2F + 0.9F, false);
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
            }else {
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
                if (source.getEntity() instanceof PlayerEntity || source.isExplosion()) {
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
        if (source instanceof EntityDamageSource && ((EntityDamageSource) source).isThorns()) {
            this.attackEntityPartFrom(this.dragonPartBody, source, amount);
        }
        return false;
    }

    protected boolean attackDragonFrom(DamageSource source, float amount) {
        return super.hurt(source, amount);
    }

    @Override
    public void kill() {
        this.remove();
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
            this.level.addParticle(ParticleTypes.EXPLOSION_EMITTER, this.getX() + (double) f, this.getY() + 2.0D + (double) f1, this.getZ() + (double) f2, 0.0D, 0.0D, 0.0D);
        }

        boolean flag = this.level.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT);
        int xpAmount = 24000;

        if (!this.level.isClientSide) {
            if (this.deathTicks > 150 && this.deathTicks % 5 == 0 && flag) {
                this.dropExperience(MathHelper.floor((float) xpAmount * 0.08F));
            }

            if (this.deathTicks == 1 && !this.isSilent()) {
                this.level.globalLevelEvent(1028, this.blockPosition(), 0);
            }
        }

        this.move(MoverType.SELF, new Vector3d(0.0D, 0.1F, 0.0D));
        this.yRot += 20.0F;
        this.yBodyRot = this.yRot;
        if (this.deathTicks == 200 && !this.level.isClientSide) {
            if (flag) {
                this.dropExperience(MathHelper.floor((float) xpAmount * 0.2F));
            }

            if (this.fightManager != null) {
                this.fightManager.processDragonDeath(this);
            }

            this.remove();
        }

    }

    private void dropExperience(int xp) {
        while (xp > 0) {
            int i = ExperienceOrbEntity.getExperienceValue(xp);
            xp -= i;
            this.level.addFreshEntity(new ExperienceOrbEntity(this.level, this.getX(), this.getY(), this.getZ(), i));
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
                int pointY = Math.max(arenaOrigin.getY() + GuardianFightManager.CRYSTAL_HEIGHT_FROM_ORIGIN, this.level.getHeightmapPos(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, new BlockPos(pointX, 0, pointZ)).getY());
                pathPoints[i] = new PathPoint(arenaOrigin.getX() + pointX, pointY, arenaOrigin.getZ() + pointZ);
            }
        }

        return this.getNearestPpIdx(this.getX(), this.getY(), this.getZ());
    }

    public int getNearestPpIdx(double x, double y, double z) {
        float f = 10000.0F;
        int i = 0;
        PathPoint pathpoint = new PathPoint(MathHelper.floor(x), MathHelper.floor(y), MathHelper.floor(z));
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
    public Path findPath(int startIdx, int finishIdx, @Nullable PathPoint andThen) {
        for (int i = 0; i < 24; ++i) {
            PathPoint pathpoint = this.pathPoints[i];
            pathpoint.closed = false;
            pathpoint.f = 0.0F;
            pathpoint.g = 0.0F;
            pathpoint.h = 0.0F;
            pathpoint.cameFrom = null;
            pathpoint.heapIdx = -1;
        }

        PathPoint startPoint = this.pathPoints[startIdx];
        PathPoint endPoint = this.pathPoints[finishIdx];

        startPoint.g = 0.0F;
        startPoint.h = startPoint.distanceTo(endPoint);
        startPoint.f = startPoint.h;

        this.pathFindQueue.clear();
        this.pathFindQueue.insert(startPoint);

        PathPoint nextPoint = startPoint;
        int startIndex = 0;
//        if (this.fightManager == null || this.fightManager.getNumAliveCrystals() == 0) {
//            startIndex = 12;
//        }

        while (!this.pathFindQueue.isEmpty()) {
            PathPoint testPoint = this.pathFindQueue.pop();
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
            int testPointIntex = 0;

            for (int l = 0; l < 24; ++l) {
                if (this.pathPoints[l] == testPoint) {
                    testPointIntex = l;
                    break;
                }
            }

            for (int index = startIndex; index < 24; ++index) {
//                if ((this.neighbors[testPointIntex] & 1 << index) > 0) {
                PathPoint pathpoint3 = this.pathPoints[index];
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
//                }
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

    private Path makePath(PathPoint start, PathPoint finish) {
        List<PathPoint> list = Lists.newArrayList();
        PathPoint pathpoint = finish;
        list.add(0, finish);

        while (pathpoint.cameFrom != null) {
            pathpoint = pathpoint.cameFrom;
            list.add(0, pathpoint);
        }

        return new Path(list, new BlockPos(finish.x, finish.y, finish.z), true);
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("dragon_phase", phaseManager.getCurrentPhase().getType().getId());
        if (getArenaOrigin() != null) {
            compound.put("arena_origin", NBTUtil.writeBlockPos(getArenaOrigin()));
        }
        compound.putFloat("shield_power", getShieldPower());
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("dragon_phase")) {
            phaseManager.setPhase(PhaseType.getById(compound.getInt("dragon_phase")));
        }
        if (compound.contains("arena_origin")) {
            setArenaOrigin(NBTUtil.readBlockPos(compound.getCompound("arena_origin")));
        }
        if (level instanceof ServerWorld) {
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
    public SoundCategory getSoundSource() {
        return SoundCategory.HOSTILE;
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

    @OnlyIn(Dist.CLIENT)
    public float getHeadPartYOffset(int p_184667_1_, double[] spineEndOffsets, double[] headPartOffsets) {
        IPhase iphase = this.phaseManager.getCurrentPhase();
        PhaseType<? extends IPhase> phasetype = iphase.getType();
        double d0;
//        if (phasetype != PhaseType.LANDING && phasetype != PhaseType.TAKEOFF) {
            if (iphase.getIsStationary()) {
                d0 = p_184667_1_;
            } else if (p_184667_1_ == 6) {
                d0 = 0.0D;
            } else {
                d0 = headPartOffsets[1] - spineEndOffsets[1];
            }
//        } else {
//            BlockPos blockpos = this.level.getHeightmapPos(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, EndPodiumFeature.END_PODIUM_LOCATION);
//            float f = Math.max(MathHelper.sqrt(blockpos.distSqr(this.position(), true)) / 4.0F, 1.0F);
//            d0 = (float) p_184667_1_ / f;
//        }

        return (float) d0;
    }

    public Vector3d getHeadLookVec(float partialTicks) {
        IPhase iphase = this.phaseManager.getCurrentPhase();
        PhaseType<? extends IPhase> phasetype = iphase.getType();
        Vector3d vector3d;
//        if (phasetype != PhaseType.LANDING && phasetype != PhaseType.TAKEOFF) {
            if (iphase.getIsStationary()) {
                float f4 = this.xRot;
                float f5 = 1.5F;
                this.xRot = -45.0F;
                vector3d = this.getViewVector(partialTicks);
                this.xRot = f4;
            } else {
                vector3d = this.getViewVector(partialTicks);
            }
//        } else {
//            BlockPos blockpos = this.level.getHeightmapPos(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, EndPodiumFeature.END_PODIUM_LOCATION);
//            float f = Math.max(MathHelper.sqrt(blockpos.distSqr(this.position(), true)) / 4.0F, 1.0F);
//            float f1 = 6.0F / f;
//            float f2 = this.xRot;
//            float f3 = 1.5F;
//            this.xRot = -f1 * 1.5F * 5.0F;
//            vector3d = this.getViewVector(partialTicks);
//            this.xRot = f2;
//        }

        return vector3d;
    }

    public void onCrystalAttacked(GuardianCrystalEntity crystal, BlockPos pos, DamageSource dmgSrc, float damage, boolean destroyed) {
        PlayerEntity playerentity;
        if (dmgSrc.getEntity() instanceof PlayerEntity) {
            playerentity = (PlayerEntity) dmgSrc.getEntity();
        } else {
            playerentity = this.level.getNearestPlayer(PLAYER_INVADER_CONDITION, pos.getX(), pos.getY(), pos.getZ());
        }

        if (crystal == this.closestGuardianCrystal && destroyed) {
            this.attackEntityPartFrom(this.dragonPartHead, DamageSource.explosion(playerentity), 10.0F);
        }

        this.phaseManager.getCurrentPhase().onCrystalAttacked(crystal, pos, dmgSrc, playerentity, damage, destroyed);
    }

    @Override
    public void onSyncedDataUpdated(DataParameter<?> key) {
        if (PHASE.equals(key) && this.level.isClientSide) {
            this.phaseManager.setPhase(PhaseType.getById(getEntityData().get(PHASE)));
        } else if (CRYSTAL_ID.equals(key) && level.isClientSide) {
            int id = getEntityData().get(CRYSTAL_ID);
            if (id == -1) {
                closestGuardianCrystal = null;
            } else {
                Entity entity = level.getEntity(id);
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
    public boolean addEffect(EffectInstance effectInstanceIn) {
        if (effectInstanceIn.getEffect().isBeneficial()) {
            //This is mostly for testing purposes
            return super.addEffect(effectInstanceIn);
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
    public IPacket<?> getAddEntityPacket() {
        return super.getAddEntityPacket();
    }
}
