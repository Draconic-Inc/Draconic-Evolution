package com.brandon3055.draconicevolution.entity.guardian;

import com.brandon3055.brandonscore.network.BCoreNetwork;
import com.brandon3055.brandonscore.worldentity.WorldEntityHandler;
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
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.EndPodiumFeature;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.entity.PartEntity;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.List;

public class DraconicGuardianEntity extends MobEntity implements IMob {
    private static final Logger LOGGER = DraconicEvolution.LOGGER;
    public static final DataParameter<Integer> PHASE = EntityDataManager.createKey(DraconicGuardianEntity.class, DataSerializers.VARINT);
    public static final DataParameter<Integer> CRYSTAL_ID = EntityDataManager.createKey(DraconicGuardianEntity.class, DataSerializers.VARINT);
    public static final DataParameter<Integer> SHIELD_STATE = EntityDataManager.createKey(DraconicGuardianEntity.class, DataSerializers.VARINT);
    private static final EntityPredicate PLAYER_INVADER_CONDITION = (new EntityPredicate()).setDistance(64.0D);
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
    public float field_226525_bB_;
    @Nullable
    public GuardianCrystalEntity closestGuardianCrystal;
    @Nullable
    private GuardianFightManager fightManager;
    private final PhaseManager phaseManager;
    private int growlTime = 100;
    private int sittingDamageReceived;
    private PathPoint[] pathPoints = new PathPoint[24];
    //    private int[] neighbors = new int[24];
    private final PathHeap pathFindQueue = new PathHeap();
    private BlockPos arenaOrigin = null;
    private double speedMult = 1;

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
        this.noClip = true;
        this.ignoreFrustumCheck = true;
        this.phaseManager = new PhaseManager(this);
    }

    public void setFightManager(GuardianFightManager fightManager) {
        this.fightManager = fightManager;
    }

    public void setArenaOrigin(BlockPos arenaOrigin) {
        this.arenaOrigin = arenaOrigin;
    }

    public BlockPos getArenaOrigin() {
        return arenaOrigin;
    }

    public static AttributeModifierMap.MutableAttribute registerAttributes() {
        return MobEntity.func_233666_p_().createMutableAttribute(Attributes.MAX_HEALTH, 200.0D);
    }

    @Override
    protected void registerData() {
        super.registerData();
        this.getDataManager().register(PHASE, PhaseType.HOVER.getId());
        this.getDataManager().register(CRYSTAL_ID, -1);
        this.getDataManager().register(SHIELD_STATE, 500);
    }

    public double[] getMovementOffsets(int index, float partialTicks) {
        if (this.getShouldBeDead()) {
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
    public void livingTick() {
        speedMult = codechicken.lib.math.MathHelper.approachLinear(speedMult, phaseManager.getCurrentPhase().getGuardianSpeed(), 0.1);
//        phaseManager.setPhase(PhaseType.START);
        if (this.world.isRemote) {
            this.setHealth(this.getHealth());
            if (!this.isSilent()) {
                float f = MathHelper.cos(this.animTime * ((float) Math.PI * 2F));
                float f1 = MathHelper.cos(this.prevAnimTime * ((float) Math.PI * 2F));
                if (f1 <= -0.3F && f >= -0.3F) {
                    this.world.playSound(this.getPosX(), this.getPosY(), this.getPosZ(), SoundEvents.ENTITY_ENDER_DRAGON_FLAP, this.getSoundCategory(), 5.0F, 0.8F + this.rand.nextFloat() * 0.3F, false);
                }

                if (!this.phaseManager.getCurrentPhase().getIsStationary() && --this.growlTime < 0) {
                    this.world.playSound(this.getPosX(), this.getPosY(), this.getPosZ(), SoundEvents.ENTITY_ENDER_DRAGON_GROWL, this.getSoundCategory(), 2.5F, 0.8F + this.rand.nextFloat() * 0.3F, false);
                    this.growlTime = 200 + this.rand.nextInt(200);
                }
            }
        } else {
            updateShieldState();
        }

        this.prevAnimTime = this.animTime;
        if (this.getShouldBeDead()) {
            float randX = (this.rand.nextFloat() - 0.5F) * 8.0F;
            float randY = (this.rand.nextFloat() - 0.5F) * 4.0F;
            float randZ = (this.rand.nextFloat() - 0.5F) * 8.0F;
            this.world.addParticle(ParticleTypes.EXPLOSION, this.getPosX() + (double) randX, this.getPosY() + 2.0D + (double) randY, this.getPosZ() + (double) randZ, 0.0D, 0.0D, 0.0D);
        } else {
            this.updateDragonEnderCrystal();
            Vector3d vector3d4 = this.getMotion();
            float f12 = 0.2F / (MathHelper.sqrt(horizontalMag(vector3d4)) * 10.0F + 1.0F);
            f12 = f12 * (float) Math.pow(2.0D, vector3d4.y);
            if (this.phaseManager.getCurrentPhase().getIsStationary()) {
                this.animTime += 0.1F;
            } else if (this.slowed) {
                this.animTime += f12 * 0.5F;
            } else {
                this.animTime += f12;
            }

            this.rotationYaw = MathHelper.wrapDegrees(this.rotationYaw);
            if (this.isAIDisabled()) {
                this.animTime = 0.5F;
            } else {
                if (this.ringBufferIndex < 0) {
                    for (int i = 0; i < this.ringBuffer.length; ++i) {
                        this.ringBuffer[i][0] = this.rotationYaw;
                        this.ringBuffer[i][1] = this.getPosY();
                    }
                }

                if (++this.ringBufferIndex == this.ringBuffer.length) {
                    this.ringBufferIndex = 0;
                }

                this.ringBuffer[this.ringBufferIndex][0] = this.rotationYaw;
                this.ringBuffer[this.ringBufferIndex][1] = this.getPosY();
                if (this.world.isRemote) {
                    if (this.newPosRotationIncrements > 0) {
                        double d7 = this.getPosX() + (this.interpTargetX - this.getPosX()) / (double) this.newPosRotationIncrements;
                        double d0 = this.getPosY() + (this.interpTargetY - this.getPosY()) / (double) this.newPosRotationIncrements;
                        double d1 = this.getPosZ() + (this.interpTargetZ - this.getPosZ()) / (double) this.newPosRotationIncrements;
                        double d2 = MathHelper.wrapDegrees(this.interpTargetYaw - (double) this.rotationYaw);
                        this.rotationYaw = (float) ((double) this.rotationYaw + d2 / (double) this.newPosRotationIncrements);
                        this.rotationPitch = (float) ((double) this.rotationPitch + (this.interpTargetPitch - (double) this.rotationPitch) / (double) this.newPosRotationIncrements);
                        --this.newPosRotationIncrements;
                        this.setPosition(d7, d0, d1);
                        this.setRotation(this.rotationYaw, this.rotationPitch);
                    }

                    this.phaseManager.getCurrentPhase().clientTick();
                } else {
                    IPhase iphase = this.phaseManager.getCurrentPhase();
                    iphase.serverTick();
                    if (this.phaseManager.getCurrentPhase() != iphase) {
                        iphase = this.phaseManager.getCurrentPhase();
                        iphase.serverTick();
                    }

                    Vector3d targetLocation = iphase.getTargetLocation();
                    if (targetLocation != null) {
                        double tRelX = targetLocation.x - this.getPosX();
                        double tRelY = targetLocation.y - this.getPosY();
                        double tRelZ = targetLocation.z - this.getPosZ();
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

                        this.setMotion(this.getMotion().add(0.0D, tRelY * 0.01D, 0.0D));
                        this.rotationYaw = MathHelper.wrapDegrees(this.rotationYaw);
                        //Angle of target relative to current yaw clamped to +-50
                        double relTargetAngle = MathHelper.clamp(MathHelper.wrapDegrees(180.0D - MathHelper.atan2(tRelX, tRelZ) * (double) (180F / (float) Math.PI) - (double) this.rotationYaw), -50.0D, 50.0D);
                        if (Math.abs(relTargetAngle) < 5) {
                            relTargetAngle *= speedMult * 5; //This is a hack to help line up with the target better when traveling at higher than normal speed.
                        }


                        Vector3d targetVector = targetLocation.subtract(this.getPosX(), this.getPosY(), this.getPosZ()).normalize();
                        Vector3d vector3d2 = new Vector3d(
                                MathHelper.sin(this.rotationYaw * ((float) Math.PI / 180F)),
                                this.getMotion().y,
                                -MathHelper.cos(this.rotationYaw * ((float) Math.PI / 180F)))
                                .normalize();
                        float f8 = Math.max(((float) vector3d2.dotProduct(targetVector) + 0.5F) / 1.5F, 0.0F);
                        this.field_226525_bB_ *= 0.8F;
                        this.field_226525_bB_ = (float) ((double) this.field_226525_bB_ + relTargetAngle * (double) iphase.getYawFactor());
                        this.rotationYaw += this.field_226525_bB_ * 0.1F;
                        float f9 = (float) (2.0D / (distanceSq + 1.0D));
                        float f10 = 0.06F;
                        this.moveRelative(f10 * (f8 * f9 + (1.0F - f9)), new Vector3d(0.0D, 0.0D, -1D));
                        if (this.slowed) {
                            this.move(MoverType.SELF, this.getMotion().scale(0.8F).scale(speedMult));
                        } else {
                            this.move(MoverType.SELF, this.getMotion().scale(speedMult));
                        }
                        Vector3d vector3d3 = this.getMotion().normalize();
                        double d6 = 0.8D + 0.15D * (vector3d3.dotProduct(vector3d2) + 1.0D) / 2.0D;
                        this.setMotion(this.getMotion().mul(d6, 0.91F, d6));
                    }
                }

                this.renderYawOffset = this.rotationYaw;
                Vector3d[] avector3d = new Vector3d[this.dragonParts.length];

                for (int j = 0; j < this.dragonParts.length; ++j) {
                    avector3d[j] = new Vector3d(this.dragonParts[j].getPosX(), this.dragonParts[j].getPosY(), this.dragonParts[j].getPosZ());
                }

                float f15 = (float) (this.getMovementOffsets(5, 1.0F)[1] - this.getMovementOffsets(10, 1.0F)[1]) * 10.0F * ((float) Math.PI / 180F);
                float f16 = MathHelper.cos(f15);
                float f2 = MathHelper.sin(f15);
                float f17 = this.rotationYaw * ((float) Math.PI / 180F);
                float f3 = MathHelper.sin(f17);
                float f18 = MathHelper.cos(f17);
                this.setPartPosition(this.dragonPartBody, f3 * 0.5F, 0.0D, -f18 * 0.5F);
                this.setPartPosition(this.dragonPartRightWing, f18 * 4.5F, 2.0D, f3 * 4.5F);
                this.setPartPosition(this.dragonPartLeftWing, f18 * -4.5F, 2.0D, f3 * -4.5F);
                if (!this.world.isRemote && this.hurtTime == 0) {
                    this.collideWithEntities(this.world.getEntitiesInAABBexcluding(this, this.dragonPartRightWing.getBoundingBox().grow(4.0D, 2.0D, 4.0D).offset(0.0D, -2.0D, 0.0D), EntityPredicates.CAN_AI_TARGET));
                    this.collideWithEntities(this.world.getEntitiesInAABBexcluding(this, this.dragonPartLeftWing.getBoundingBox().grow(4.0D, 2.0D, 4.0D).offset(0.0D, -2.0D, 0.0D), EntityPredicates.CAN_AI_TARGET));
                    this.attackEntitiesInList(this.world.getEntitiesInAABBexcluding(this, this.dragonPartHead.getBoundingBox().grow(1.0D), EntityPredicates.CAN_AI_TARGET));
                    this.attackEntitiesInList(this.world.getEntitiesInAABBexcluding(this, this.dragonPartNeck.getBoundingBox().grow(1.0D), EntityPredicates.CAN_AI_TARGET));
                }

                float f4 = MathHelper.sin(this.rotationYaw * ((float) Math.PI / 180F) - this.field_226525_bB_ * 0.01F);
                float f19 = MathHelper.cos(this.rotationYaw * ((float) Math.PI / 180F) - this.field_226525_bB_ * 0.01F);
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
                    float f7 = this.rotationYaw * ((float) Math.PI / 180F) + this.simplifyAngle(adouble1[0] - adouble[0]) * ((float) Math.PI / 180F);
                    float f20 = MathHelper.sin(f7);
                    float f21 = MathHelper.cos(f7);
                    float f22 = 1.5F;
                    float f23 = (float) (k + 1) * 2.0F;
                    this.setPartPosition(enderdragonpartentity, -(f3 * 1.5F + f20 * f23) * f16, adouble1[1] - adouble[1] - (double) ((f23 + 1.5F) * f2) + 1.5D, (f18 * 1.5F + f21 * f23) * f16);
                }

                if (!this.world.isRemote) {
                    this.slowed = this.destroyBlocksInAABB(this.dragonPartHead.getBoundingBox()) | this.destroyBlocksInAABB(this.dragonPartNeck.getBoundingBox()) | this.destroyBlocksInAABB(this.dragonPartBody.getBoundingBox());
                    if (this.fightManager != null) {
                        this.fightManager.guardianUpdate(this);
                    }
                }

                for (int l = 0; l < this.dragonParts.length; ++l) {
                    this.dragonParts[l].prevPosX = avector3d[l].x;
                    this.dragonParts[l].prevPosY = avector3d[l].y;
                    this.dragonParts[l].prevPosZ = avector3d[l].z;
                    this.dragonParts[l].lastTickPosX = avector3d[l].x;
                    this.dragonParts[l].lastTickPosY = avector3d[l].y;
                    this.dragonParts[l].lastTickPosZ = avector3d[l].z;
                }

            }
        }
    }

    private void updateShieldState() {
        int target = 0;
        if (getShouldBeDead()) {
            target = 0;
        } else if (fightManager != null && fightManager.getNumAliveCrystals() > 0) {
            target = 500;
        }
        getDataManager().set(SHIELD_STATE, (int)Math.floor(codechicken.lib.math.MathHelper.approachLinear(getDataManager().get(SHIELD_STATE), target, 10)));
    }

    private void setPartPosition(DraconicGuardianPartEntity part, double offsetX, double offsetY, double offsetZ) {
        part.setPosition(this.getPosX() + offsetX, this.getPosY() + offsetY, this.getPosZ() + offsetZ);
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
            } else if (ticksExisted % 10 == 0 && getHealth() < getMaxHealth()) {
                setHealth(this.getHealth() + 1.0F);
            }
        }

        if (this.rand.nextInt(10) == 0 && !world.isRemote) {
            if (fightManager != null) {
                closestGuardianCrystal = fightManager.getCrystals().stream().min(Comparator.comparingDouble(this::getDistanceSq)).orElse(null);
            } else {
                List<GuardianCrystalEntity> list = this.world.getEntitiesWithinAABB(GuardianCrystalEntity.class, this.getBoundingBox().grow(32.0D));
                GuardianCrystalEntity crystal = null;
                double d0 = Double.MAX_VALUE;
                for (GuardianCrystalEntity endercrystalentity1 : list) {
                    double d1 = endercrystalentity1.getDistanceSq(this);
                    if (d1 < d0) {
                        d0 = d1;
                        crystal = endercrystalentity1;
                    }
                }
                this.closestGuardianCrystal = crystal;
            }
            getDataManager().set(CRYSTAL_ID, closestGuardianCrystal == null ? -1 : closestGuardianCrystal.getEntityId());
        }

    }

    private void collideWithEntities(List<Entity> entities) {
        double d0 = (this.dragonPartBody.getBoundingBox().minX + this.dragonPartBody.getBoundingBox().maxX) / 2.0D;
        double d1 = (this.dragonPartBody.getBoundingBox().minZ + this.dragonPartBody.getBoundingBox().maxZ) / 2.0D;

        for (Entity entity : entities) {
            if (entity instanceof LivingEntity) {
                double d2 = entity.getPosX() - d0;
                double d3 = entity.getPosZ() - d1;
                double d4 = Math.max(d2 * d2 + d3 * d3, 0.1D);
                entity.addVelocity(d2 / d4 * 4.0D, 0.2F, d3 / d4 * 4.0D);
                if (!this.phaseManager.getCurrentPhase().getIsStationary() && ((LivingEntity) entity).getRevengeTimer() < entity.ticksExisted - 2) {
                    entity.attackEntityFrom(new EntityDamageSource(DraconicEvolution.MODID + ".draconic_guardian", this), 15.0F);
                    this.applyEnchantments(this, entity);
                }
            }
        }

    }

    private void attackEntitiesInList(List<Entity> entities) {
        for (Entity entity : entities) {
            if (entity instanceof LivingEntity) {
                entity.attackEntityFrom(new EntityDamageSource(DraconicEvolution.MODID + ".draconic_guardian", this), 20.0F);
                this.applyEnchantments(this, entity);
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
                    BlockState blockstate = this.world.getBlockState(blockpos);
                    Block block = blockstate.getBlock();
                    if (!blockstate.isAir(this.world, blockpos) && blockstate.getMaterial() != Material.FIRE) {
                        if (net.minecraftforge.common.ForgeHooks.canEntityDestroy(this.world, blockpos, this) && !BlockTags.DRAGON_IMMUNE.contains(block) && block != Blocks.NETHER_BRICKS && block != Blocks.NETHER_BRICK_SLAB) {
                            flag1 = this.world.removeBlock(blockpos, false) || flag1;
                        } else {
                            flag = true;
                        }
                    }
                }
            }
        }

        if (flag1) {
            BlockPos blockpos1 = new BlockPos(i + this.rand.nextInt(l - i + 1), j + this.rand.nextInt(i1 - j + 1), k + this.rand.nextInt(j1 - k + 1));
            this.world.playEvent(2008, blockpos1, 0);
        }

        return flag;
    }

    public boolean attackEntityPartFrom(DraconicGuardianPartEntity part, DamageSource source, float damage) {
        if (this.phaseManager.getCurrentPhase().getType() == PhaseType.DYING) {
            return false;
        } else {
            if (fightManager != null && fightManager.getNumAliveCrystals() > 0) {
                getDataManager().set(SHIELD_STATE, 1000);
                BCoreNetwork.sendSound(world, getPosition(), DESounds.shieldStrike, SoundCategory.HOSTILE, 20, rand.nextFloat() * 0.2F + 0.9F, false);
                return false;
            }

            damage = this.phaseManager.getCurrentPhase().onAttacked(source, damage);
            if (part != this.dragonPartHead) {
                damage = damage / 4.0F + Math.min(damage, 1.0F);
            }

            if (damage < 0.01F) {
                return false;
            } else {
                if (source.getTrueSource() instanceof PlayerEntity || source.isExplosion()) {
                    this.attackDragonFrom(source, damage);
                    if (this.getShouldBeDead() && !this.phaseManager.getCurrentPhase().getIsStationary()) {
                        this.setHealth(1.0F);
                        this.phaseManager.setPhase(PhaseType.DYING);
                    }
                }

                return true;
            }
        }
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (source instanceof EntityDamageSource && ((EntityDamageSource) source).getIsThornsDamage()) {
            this.attackEntityPartFrom(this.dragonPartBody, source, amount);
        }
        return false;
    }

    protected boolean attackDragonFrom(DamageSource source, float amount) {
        return super.attackEntityFrom(source, amount);
    }

    @Override
    public void onKillCommand() {
        this.remove();
        if (this.fightManager != null) {
            this.fightManager.guardianUpdate(this);
            this.fightManager.processDragonDeath(this);
        }

    }

    @Override
    protected void onDeathUpdate() {
        if (this.fightManager != null) {
            this.fightManager.guardianUpdate(this);
        }

        ++this.deathTicks;
        if (this.deathTicks >= 180 && this.deathTicks <= 200) {
            float f = (this.rand.nextFloat() - 0.5F) * 8.0F;
            float f1 = (this.rand.nextFloat() - 0.5F) * 4.0F;
            float f2 = (this.rand.nextFloat() - 0.5F) * 8.0F;
            this.world.addParticle(ParticleTypes.EXPLOSION_EMITTER, this.getPosX() + (double) f, this.getPosY() + 2.0D + (double) f1, this.getPosZ() + (double) f2, 0.0D, 0.0D, 0.0D);
        }

        boolean flag = this.world.getGameRules().getBoolean(GameRules.DO_MOB_LOOT);
        int xpAmount = 500;
//      if (this.fightManager != null && !this.fightManager.hasPreviouslyKilledDragon()) {
//         xpAmount = 12000;
        xpAmount = 24000; //Todo make this fight tier dependent
//      }

        if (!this.world.isRemote) {
            if (this.deathTicks > 150 && this.deathTicks % 5 == 0 && flag) {
                this.dropExperience(MathHelper.floor((float) xpAmount * 0.08F));
            }

            if (this.deathTicks == 1 && !this.isSilent()) {
                this.world.playBroadcastSound(1028, this.getPosition(), 0);
            }
        }

        this.move(MoverType.SELF, new Vector3d(0.0D, 0.1F, 0.0D));
        this.rotationYaw += 20.0F;
        this.renderYawOffset = this.rotationYaw;
        if (this.deathTicks == 200 && !this.world.isRemote) {
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
            int i = ExperienceOrbEntity.getXPSplit(xp);
            xp -= i;
            this.world.addEntity(new ExperienceOrbEntity(this.world, this.getPosX(), this.getPosY(), this.getPosZ(), i));
        }

    }

    //Path finding fo the chaos island can be much simpler because the pillar ring is much bigger and the guardian never needs to fly beyond its parimeter.
    public int initPathPoints(boolean regenerate) {
        if (this.pathPoints[0] == null || regenerate) {
            if (arenaOrigin == null) {
                arenaOrigin = getPosition();
            }

            //Circle
            for (int i = 0; i < 24; i++) {
                float loopPos = i / 24F;
                float angle = loopPos * 360;
                int pointX = codechicken.lib.math.MathHelper.floor((GuardianFightManager.CRYSTAL_DIST_FROM_CENTER - 20) * Math.cos(angle * codechicken.lib.math.MathHelper.torad));
                int pointZ = codechicken.lib.math.MathHelper.floor((GuardianFightManager.CRYSTAL_DIST_FROM_CENTER - 20) * Math.sin(angle * codechicken.lib.math.MathHelper.torad));
                int pointY = Math.max(arenaOrigin.getY() + GuardianFightManager.CRYSTAL_HEIGHT_FROM_ORIGIN, this.world.getHeight(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, new BlockPos(pointX, 0, pointZ)).getY());
                pathPoints[i] = new PathPoint(arenaOrigin.getX() + pointX, pointY, arenaOrigin.getZ() + pointZ);
            }
        }

        return this.getNearestPpIdx(this.getPosX(), this.getPosY(), this.getPosZ());
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
                float f1 = this.pathPoints[k].distanceToSquared(pathpoint);
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
            pathpoint.visited = false;
            pathpoint.distanceToTarget = 0.0F;
            pathpoint.totalPathDistance = 0.0F;
            pathpoint.distanceToNext = 0.0F;
            pathpoint.previous = null;
            pathpoint.index = -1;
        }

        PathPoint startPoint = this.pathPoints[startIdx];
        PathPoint endPoint = this.pathPoints[finishIdx];

        startPoint.totalPathDistance = 0.0F;
        startPoint.distanceToNext = startPoint.distanceTo(endPoint);
        startPoint.distanceToTarget = startPoint.distanceToNext;

        this.pathFindQueue.clearPath();
        this.pathFindQueue.addPoint(startPoint);

        PathPoint nextPoint = startPoint;
        int startIndex = 0;
//        if (this.fightManager == null || this.fightManager.getNumAliveCrystals() == 0) {
//            startIndex = 12;
//        }

        while (!this.pathFindQueue.isPathEmpty()) {
            PathPoint testPoint = this.pathFindQueue.dequeue();
            if (testPoint.equals(endPoint)) {
                if (andThen != null) {
                    andThen.previous = endPoint;
                    endPoint = andThen;
                }

                return this.makePath(startPoint, endPoint);
            }

            //If text point is closer then next point becomes test point
            if (testPoint.distanceToSquared(endPoint) < nextPoint.distanceToSquared(endPoint)) {
                nextPoint = testPoint;
            }

            testPoint.visited = true;
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
                if (!pathpoint3.visited) {
                    float f = testPoint.totalPathDistance + testPoint.distanceTo(pathpoint3);
                    if (!pathpoint3.isAssigned() || f < pathpoint3.totalPathDistance) {
                        pathpoint3.previous = testPoint;
                        pathpoint3.totalPathDistance = f;
                        pathpoint3.distanceToNext = pathpoint3.distanceTo(endPoint);
                        if (pathpoint3.isAssigned()) {
                            this.pathFindQueue.changeDistance(pathpoint3, pathpoint3.totalPathDistance + pathpoint3.distanceToNext);
                        } else {
                            pathpoint3.distanceToTarget = pathpoint3.totalPathDistance + pathpoint3.distanceToNext;
                            this.pathFindQueue.addPoint(pathpoint3);
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
                andThen.previous = nextPoint;
                nextPoint = andThen;
            }

            return this.makePath(startPoint, nextPoint);
        }
    }

    private Path makePath(PathPoint start, PathPoint finish) {
        List<PathPoint> list = Lists.newArrayList();
        PathPoint pathpoint = finish;
        list.add(0, finish);

        while (pathpoint.previous != null) {
            pathpoint = pathpoint.previous;
            list.add(0, pathpoint);
        }

        return new Path(list, new BlockPos(finish.x, finish.y, finish.z), true);
    }

    @Override
    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putInt("dragon_phase", phaseManager.getCurrentPhase().getType().getId());
        if (arenaOrigin != null) {
            compound.put("arena_origin", NBTUtil.writeBlockPos(arenaOrigin));
        }
    }

    @Override
    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        if (compound.contains("dragon_phase")) {
            phaseManager.setPhase(PhaseType.getById(compound.getInt("dragon_phase")));
        }
        if (compound.contains("arena_origin")) {
            arenaOrigin = NBTUtil.readBlockPos(compound.getCompound("arena_origin"));
        }
        if (world instanceof ServerWorld) {
            fightManager = WorldEntityHandler.getWorldEntities()
                    .stream()
                    .filter(e -> e instanceof GuardianFightManager)
                    .map(e -> (GuardianFightManager) e)
                    .filter(e -> getUniqueID().equals(e.getGuardianUniqueId()))
                    .findFirst()
                    .orElse(null);

            if (fightManager != null) {
                arenaOrigin = fightManager.getArenaOrigin();
            }
        } else {
            fightManager = null;
        }

    }

    @Override
    public void checkDespawn() {
    }

    public DraconicGuardianPartEntity[] getDragonParts() {
        return this.dragonParts;
    }

    @Override
    public boolean canBeCollidedWith() {
        return false;
    }

    @Override
    public SoundCategory getSoundCategory() {
        return SoundCategory.HOSTILE;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_ENDER_DRAGON_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.ENTITY_ENDER_DRAGON_HURT;
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
        if (phasetype != PhaseType.LANDING && phasetype != PhaseType.TAKEOFF) {
            if (iphase.getIsStationary()) {
                d0 = p_184667_1_;
            } else if (p_184667_1_ == 6) {
                d0 = 0.0D;
            } else {
                d0 = headPartOffsets[1] - spineEndOffsets[1];
            }
        } else {
            BlockPos blockpos = this.world.getHeight(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, EndPodiumFeature.END_PODIUM_LOCATION);
            float f = Math.max(MathHelper.sqrt(blockpos.distanceSq(this.getPositionVec(), true)) / 4.0F, 1.0F);
            d0 = (float) p_184667_1_ / f;
        }

        return (float) d0;
    }

    public Vector3d getHeadLookVec(float partialTicks) {
        IPhase iphase = this.phaseManager.getCurrentPhase();
        PhaseType<? extends IPhase> phasetype = iphase.getType();
        Vector3d vector3d;
        if (phasetype != PhaseType.LANDING && phasetype != PhaseType.TAKEOFF) {
            if (iphase.getIsStationary()) {
                float f4 = this.rotationPitch;
                float f5 = 1.5F;
                this.rotationPitch = -45.0F;
                vector3d = this.getLook(partialTicks);
                this.rotationPitch = f4;
            } else {
                vector3d = this.getLook(partialTicks);
            }
        } else {
            BlockPos blockpos = this.world.getHeight(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, EndPodiumFeature.END_PODIUM_LOCATION);
            float f = Math.max(MathHelper.sqrt(blockpos.distanceSq(this.getPositionVec(), true)) / 4.0F, 1.0F);
            float f1 = 6.0F / f;
            float f2 = this.rotationPitch;
            float f3 = 1.5F;
            this.rotationPitch = -f1 * 1.5F * 5.0F;
            vector3d = this.getLook(partialTicks);
            this.rotationPitch = f2;
        }

        return vector3d;
    }

    public void onCrystalDestroyed(GuardianCrystalEntity crystal, BlockPos pos, DamageSource dmgSrc) {
        PlayerEntity playerentity;
        if (dmgSrc.getTrueSource() instanceof PlayerEntity) {
            playerentity = (PlayerEntity) dmgSrc.getTrueSource();
        } else {
            playerentity = this.world.getClosestPlayer(PLAYER_INVADER_CONDITION, pos.getX(), pos.getY(), pos.getZ());
        }

        if (crystal == this.closestGuardianCrystal) {
            this.attackEntityPartFrom(this.dragonPartHead, DamageSource.causeExplosionDamage(playerentity), 10.0F);
        }

        this.phaseManager.getCurrentPhase().onCrystalDestroyed(crystal, pos, dmgSrc, playerentity);
    }

    @Override
    public void notifyDataManagerChange(DataParameter<?> key) {
        if (PHASE.equals(key) && this.world.isRemote) {
            this.phaseManager.setPhase(PhaseType.getById(getDataManager().get(PHASE)));
        } else if (CRYSTAL_ID.equals(key) && world.isRemote) {
            int id = getDataManager().get(CRYSTAL_ID);
            if (id == -1) {
                closestGuardianCrystal = null;
            } else {
                Entity entity = world.getEntityByID(id);
                closestGuardianCrystal = entity instanceof GuardianCrystalEntity ? (GuardianCrystalEntity) entity : null;
            }
        }

        super.notifyDataManagerChange(key);
    }

    public PhaseManager getPhaseManager() {
        return this.phaseManager;
    }

    @Nullable
    public GuardianFightManager getFightManager() {
        return this.fightManager;
    }

    @Override
    public boolean addPotionEffect(EffectInstance effectInstanceIn) {
        return false;
    }

    @Override
    protected boolean canBeRidden(Entity entityIn) {
        return false;
    }

    @Override
    public boolean isNonBoss() {
        return false;
    }

    @Override
    public boolean isMultipartEntity() {
        return true;
    }

    @Nullable
    @Override
    public PartEntity<?>[] getParts() {
        return dragonParts;
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return super.createSpawnPacket();
    }
}
