package com.brandon3055.draconicevolution.entity.projectile;

import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.brandonscore.network.BCoreNetwork;
import com.brandon3055.draconicevolution.DEConfig;
import com.brandon3055.draconicevolution.api.damage.DraconicIndirectEntityDamage;
import com.brandon3055.draconicevolution.init.DEContent;
import com.brandon3055.draconicevolution.lib.ProjectileAntiImmunityDamage;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.EndermanEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ShieldItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SChangeGameStatePacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.*;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.*;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

/**
 * Created by brandon3055 on 8/7/21
 */
public class DraconicArrowEntity extends AbstractArrowEntity {
    private static final DataParameter<Integer> ID_EFFECT_COLOR = EntityDataManager.defineId(DraconicArrowEntity.class, DataSerializers.INT);
    private Potion potion = Potions.EMPTY;
    private final Set<EffectInstance> effects = Sets.newHashSet();
    private boolean fixedColor;

    private static final DataParameter<Integer> SPECTRAL_TIME = EntityDataManager.defineId(DraconicArrowEntity.class, DataSerializers.INT);
    private static final DataParameter<Byte> TECH_LEVEL = EntityDataManager.defineId(DraconicArrowEntity.class, DataSerializers.BYTE);
    private static final DataParameter<Byte> PENETRATION = EntityDataManager.defineId(DraconicArrowEntity.class, DataSerializers.BYTE);
    private static final DataParameter<Float> GRAV_COMPENSATION = EntityDataManager.defineId(DraconicArrowEntity.class, DataSerializers.FLOAT);
    private static final DataParameter<Float> INIT_VELOCITY = EntityDataManager.defineId(DraconicArrowEntity.class, DataSerializers.FLOAT); //(Grav comp will deactivate when velocity decreases by say 25%)
    private static final DataParameter<Boolean> PROJ_ANTI_IMMUNE = EntityDataManager.defineId(DraconicArrowEntity.class, DataSerializers.BOOLEAN);

    public DraconicArrowEntity(EntityType<? extends DraconicArrowEntity> entityType, World world) {
        super(entityType, world);
    }

    public DraconicArrowEntity(World world, double xPos, double yPos, double zPos) {
        super(DEContent.draconicArrow, xPos, yPos, zPos, world);
    }

    public DraconicArrowEntity(World world, LivingEntity shooter) {
        super(DEContent.draconicArrow, shooter, world);
    }

    // ## Arrow Setup ##

    public void setSpectral(int spectralTime) {
        entityData.set(SPECTRAL_TIME, spectralTime);
    }

    public int getSpectralTime() {
        return entityData.get(SPECTRAL_TIME);
    }

    public void setTechLevel(TechLevel techLevel) {
        entityData.set(TECH_LEVEL, (byte) techLevel.index);
    }

    public void setPenetration(float penetration) {
        int penCount = 0;
        while (penetration > random.nextFloat()) {
            penCount++;
            penetration -= 0.25;
        }
        entityData.set(PENETRATION, (byte) Math.min(penCount, 127));
    }

    public void setProjectileImmuneOverride(boolean value) {
        entityData.set(PROJ_ANTI_IMMUNE, value);
    }

    public boolean getProjectileImmuneOverride() {
        return entityData.get(PROJ_ANTI_IMMUNE);
    }

    public void setGravComp(float gravComp) {
        entityData.set(GRAV_COMPENSATION, gravComp);
    }

    @Override
    public void shoot(double p_70186_1_, double p_70186_3_, double p_70186_5_, float speed, float p_70186_8_) {
        super.shoot(p_70186_1_, p_70186_3_, p_70186_5_, speed, p_70186_8_);
        entityData.set(INIT_VELOCITY, (float) getDeltaMovement().length());
    }

    @Override
    public void shootFromRotation(Entity p_234612_1_, float p_234612_2_, float p_234612_3_, float p_234612_4_, float speed, float p_234612_6_) {
        super.shootFromRotation(p_234612_1_, p_234612_2_, p_234612_3_, p_234612_4_, speed, p_234612_6_);
        entityData.set(INIT_VELOCITY, (float) getDeltaMovement().length());
    }

    // # # # # # # # # #

    // ## Penetration Impl  ##

    @Override
    public void tick() {
        superTick();

        if (this.level.isClientSide) {
            if (this.inGround) {
                if (this.inGroundTime % 5 == 0) {
                    this.makeParticle(1);
                }
            } else {
                this.makeParticle(2);
            }
        } else if (this.inGround && this.inGroundTime != 0 && !this.effects.isEmpty() && this.inGroundTime >= 600) {
            this.level.broadcastEntityEvent(this, (byte) 0);
            this.potion = Potions.EMPTY;
            this.effects.clear();
            this.entityData.set(ID_EFFECT_COLOR, -1);
        }

    }

    private void superTick() {
        //Projectile Entity Tick
        if (!this.leftOwner) {
            this.leftOwner = this.checkLeftOwner();
        }
        //Entity Tick
        if (!this.level.isClientSide) {
            this.setSharedFlag(6, this.isGlowing());
        }
        this.baseTick();

        //Abstract Arrow Tick
        boolean flag = this.isNoPhysics();
        Vector3d vector3d = this.getDeltaMovement();
        if (this.xRotO == 0.0F && this.yRotO == 0.0F) {
            float f = MathHelper.sqrt(getHorizontalDistanceSqr(vector3d));
            this.yRot = (float) (MathHelper.atan2(vector3d.x, vector3d.z) * (double) (180F / (float) Math.PI));
            this.xRot = (float) (MathHelper.atan2(vector3d.y, (double) f) * (double) (180F / (float) Math.PI));
            this.yRotO = this.yRot;
            this.xRotO = this.xRot;
        }

        BlockPos blockpos = this.blockPosition();
        BlockState blockstate = this.level.getBlockState(blockpos);
        if (!blockstate.isAir(this.level, blockpos) && !flag) {
            VoxelShape voxelshape = blockstate.getCollisionShape(this.level, blockpos);
            if (!voxelshape.isEmpty()) {
                Vector3d vector3d1 = this.position();

                for (AxisAlignedBB axisalignedbb : voxelshape.toAabbs()) {
                    if (axisalignedbb.move(blockpos).contains(vector3d1)) {
                        this.inGround = true;
                        break;
                    }
                }
            }
        }

        if (this.shakeTime > 0) {
            --this.shakeTime;
        }

        if (this.isInWaterOrRain()) {
            this.clearFire();
        }

        if (this.inGround && !flag) {
            if (this.lastState != blockstate && this.shouldFall()) {
                this.startFalling();
            } else if (!this.level.isClientSide) {
                this.tickDespawn();
            }

            ++this.inGroundTime;
        } else {
            this.inGroundTime = 0;
            Vector3d vector3d2 = this.position();
            Vector3d vector3d3 = vector3d2.add(vector3d);
            RayTraceResult raytraceresult = this.level.clip(new RayTraceContext(vector3d2, vector3d3, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, this));
            if (raytraceresult.getType() != RayTraceResult.Type.MISS) {
                vector3d3 = raytraceresult.getLocation();
            }

            while (!this.removed) {
                EntityRayTraceResult entityraytraceresult = this.findHitEntity(vector3d2, vector3d3);
                if (entityraytraceresult != null) {
                    raytraceresult = entityraytraceresult;
                }

                if (raytraceresult != null && raytraceresult.getType() == RayTraceResult.Type.ENTITY) {
                    Entity entity = ((EntityRayTraceResult) raytraceresult).getEntity();
                    Entity entity1 = this.getOwner();
                    if (entity instanceof PlayerEntity && entity1 instanceof PlayerEntity && !((PlayerEntity) entity1).canHarmPlayer((PlayerEntity) entity)) {
                        raytraceresult = null;
                        entityraytraceresult = null;
                    }
                }

                if (raytraceresult != null && raytraceresult.getType() != RayTraceResult.Type.MISS && !flag && !net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(this, raytraceresult)) {
                    this.onHit(raytraceresult);
                    this.hasImpulse = true;
                }

                if (entityraytraceresult == null || (this.getPierceLevel() <= 0 && entityData.get(PENETRATION) <= 0)) {
                    break;
                }

                raytraceresult = null;
            }

            vector3d = this.getDeltaMovement();
            double d3 = vector3d.x;
            double d4 = vector3d.y;
            double d0 = vector3d.z;
            if (this.isCritArrow()) {
                for (int i = 0; i < 4; ++i) {
                    this.level.addParticle(ParticleTypes.CRIT, this.getX() + d3 * (double) i / 4.0D, this.getY() + d4 * (double) i / 4.0D, this.getZ() + d0 * (double) i / 4.0D, -d3, -d4 + 0.2D, -d0);
                }
            }

            double d5 = this.getX() + d3;
            double d1 = this.getY() + d4;
            double d2 = this.getZ() + d0;
            float f1 = MathHelper.sqrt(getHorizontalDistanceSqr(vector3d));
            if (flag) {
                this.yRot = (float) (MathHelper.atan2(-d3, -d0) * (double) (180F / (float) Math.PI));
            } else {
                this.yRot = (float) (MathHelper.atan2(d3, d0) * (double) (180F / (float) Math.PI));
            }

            this.xRot = (float) (MathHelper.atan2(d4, (double) f1) * (double) (180F / (float) Math.PI));
            this.xRot = lerpRotation(this.xRotO, this.xRot);
            this.yRot = lerpRotation(this.yRotO, this.yRot);
            float f2 = 0.99F;
            float f3 = 0.05F;
            if (this.isInWater()) {
                for (int j = 0; j < 4; ++j) {
                    float f4 = 0.25F;
                    this.level.addParticle(ParticleTypes.BUBBLE, d5 - d3 * 0.25D, d1 - d4 * 0.25D, d2 - d0 * 0.25D, d3, d4, d0);
                }

                f2 = this.getWaterInertia();
            }

            this.setDeltaMovement(vector3d.scale((double) f2));
            if (!this.isNoGravity() && !flag) {
                Vector3d vector3d4 = this.getDeltaMovement();
                float antiGrav = entityData.get(GRAV_COMPENSATION);
                if (antiGrav > 0 && !inGround) {
                    float antiGravActivation = Math.min((float) getDeltaMovement().length() / (Math.max(entityData.get(INIT_VELOCITY) * 0.75F, 2F)), 1);
                    this.setDeltaMovement(vector3d4.x, vector3d4.y - ((double) 0.05F * (1 - (antiGravActivation * antiGrav))), vector3d4.z);
                } else {
                    this.setDeltaMovement(vector3d4.x, vector3d4.y - (double) 0.05F, vector3d4.z);
                }
            }

            this.setPos(d5, d1, d2);
            this.checkInsideBlocks();
        }
    }

    @Override
    protected void onHitEntity(EntityRayTraceResult p_213868_1_) {
        Entity entity = p_213868_1_.getEntity();
        float f = (float) this.getDeltaMovement().length();
        int i = MathHelper.ceil(MathHelper.clamp((double) f * this.getBaseDamage(), 0.0D, 2.147483647E9D));
        int penetration = entityData.get(PENETRATION);
        if (this.getPierceLevel() > 0 || penetration > 0) {
            if (this.piercingIgnoreEntityIds == null) {
                this.piercingIgnoreEntityIds = new IntOpenHashSet(5);
            }

            if (this.piercedAndKilledEntities == null) {
                this.piercedAndKilledEntities = Lists.newArrayListWithCapacity(5);
            }

            if (this.piercingIgnoreEntityIds.size() >= this.getPierceLevel() + 1 && this.piercingIgnoreEntityIds.size() >= penetration + 1) {
                this.remove();
                return;
            }

            this.piercingIgnoreEntityIds.add(entity.getId());
        }

        if (this.isCritArrow()) {
            long j = (long) this.random.nextInt(i / 2 + 2);
            i = (int) Math.min(j + (long) i, 2147483647L);
        }

        Entity owner = this.getOwner();
        DamageSource damagesource = getDamageSource(entity);

        boolean isEnderman = entity.getType() == EntityType.ENDERMAN;
        int k = entity.getRemainingFireTicks();
        if (this.isOnFire() && !isEnderman) {
            entity.setSecondsOnFire(5);
        }

        //Break shields with penetration
        if (penetration > 1 && entity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) entity;
            if (player.isUsingItem() && player.getUseItem().getItem() instanceof ShieldItem) {
                player.getCooldowns().addCooldown(player.getUseItem().getItem(), 100);
                level.broadcastEntityEvent(player, (byte) 30);
                player.stopUsingItem();
            }
        }

        if (entity.hurt(damagesource, (float) i)) {
            if (isEnderman) {
                return;
            }

            if (entity instanceof LivingEntity) {
                LivingEntity livingentity = (LivingEntity) entity;
                if (!this.level.isClientSide && this.getPierceLevel() <= 0 && penetration <= 0) {
                    livingentity.setArrowCount(livingentity.getArrowCount() + 1);
                }

                if (this.knockback > 0) {
                    Vector3d vector3d = this.getDeltaMovement().multiply(1.0D, 0.0D, 1.0D).normalize().scale((double) this.knockback * 0.6D);
                    if (vector3d.lengthSqr() > 0.0D) {
                        livingentity.push(vector3d.x, 0.1D, vector3d.z);
                    }
                }

                if (!this.level.isClientSide && owner instanceof LivingEntity) {
                    EnchantmentHelper.doPostHurtEffects(livingentity, owner);
                    EnchantmentHelper.doPostDamageEffects((LivingEntity) owner, livingentity);
                }

                this.doPostHurtEffects(livingentity);
                if (owner != null && livingentity != owner && livingentity instanceof PlayerEntity && owner instanceof ServerPlayerEntity && !this.isSilent()) {
                    ((ServerPlayerEntity) owner).connection.send(new SChangeGameStatePacket(SChangeGameStatePacket.ARROW_HIT_PLAYER, 0.0F));
                }

                if (!entity.isAlive() && this.piercedAndKilledEntities != null) {
                    this.piercedAndKilledEntities.add(livingentity);
                }

                if (!this.level.isClientSide && owner instanceof ServerPlayerEntity) {
                    ServerPlayerEntity serverplayerentity = (ServerPlayerEntity) owner;
                    if (this.piercedAndKilledEntities != null && this.shotFromCrossbow()) {
                        CriteriaTriggers.KILLED_BY_CROSSBOW.trigger(serverplayerentity, this.piercedAndKilledEntities);
                    } else if (!entity.isAlive() && this.shotFromCrossbow()) {
                        CriteriaTriggers.KILLED_BY_CROSSBOW.trigger(serverplayerentity, Arrays.asList(entity));
                    }
                }
            }

            this.playSound(this.getHitGroundSoundEvent(), 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
            if (this.getPierceLevel() <= 0 && penetration <= 0) {
                this.remove();
            }
        } else {
            entity.setRemainingFireTicks(k);
            this.setDeltaMovement(this.getDeltaMovement().scale(0));
            this.yRot += 180.0F;
            this.yRotO += 180.0F;
            if (!this.level.isClientSide && this.getDeltaMovement().lengthSqr() < 1.0E-7D) {
                if (this.pickup == AbstractArrowEntity.PickupStatus.ALLOWED) {
                    this.spawnAtLocation(this.getPickupItem(), 0.1F);
                }

                this.remove();
            }
        }
    }

    private DamageSource getDamageSource(Entity target) {
        Entity owner = this.getOwner();
        EntityDamageSource damagesource;
        TechLevel techLevel = TechLevel.byIndex(entityData.get(TECH_LEVEL));
        if (owner == null) {
            damagesource = DraconicIndirectEntityDamage.arrow(this, this, techLevel);
        } else {
            damagesource = DraconicIndirectEntityDamage.arrow(this, owner, techLevel);
            if (owner instanceof LivingEntity) {
                ((LivingEntity) owner).setLastHurtMob(target);
            }
        }
        if (getProjectileImmuneOverride() && DEConfig.projectileAntiImmuneEntities.contains(target.getType().getRegistryName().toString())) {
            damagesource = new ProjectileAntiImmunityDamage("arrow", this, damagesource.getEntity(), techLevel);
        }
        return damagesource;
    }

    private int blockPenetration = 0;

    @Override
    protected void onHitBlock(BlockRayTraceResult traceResult) {
        byte basePenetration = entityData.get(PENETRATION);
        int remainingPenetration = basePenetration - blockPenetration;
        if (piercingIgnoreEntityIds != null) {
            remainingPenetration -= piercingIgnoreEntityIds.size();
        }
        double canPenetrate = remainingPenetration * 0.25;
        byte techLevel = entityData.get(TECH_LEVEL);
        if (canPenetrate > 0 && basePenetration >= techLevel * 1.5) {
            blockPenetration += remainingPenetration;
            BlockPos pos = traceResult.getBlockPos();
            BlockState blockstate = this.level.getBlockState(pos);
            if (!blockstate.isAir(this.level, pos)) {
                VoxelShape voxelshape = blockstate.getCollisionShape(this.level, pos);
                if (!voxelshape.isEmpty()) {
                    boolean canPass = true;
                    Vector3d vector3d1 = traceResult.getLocation().add(getDeltaMovement().normalize().multiply(canPenetrate, canPenetrate, canPenetrate));

                    for (AxisAlignedBB axisalignedbb : voxelshape.toAabbs()) {
                        if (axisalignedbb.move(pos).contains(vector3d1)) {
                            canPass = false;
                            break;
                        }
                    }
                    if (canPass) {
                        this.playSound(SoundEvents.ANCIENT_DEBRIS_BREAK, 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
                        for (int i = 0; i < 25; i++) {
                            Vector3d critPos = traceResult.getLocation().add(getDeltaMovement().normalize().multiply(2, 2, 2).add((-0.5 + random.nextGaussian()) * 0.2, (-0.5 + random.nextGaussian()) * 0.2, (-0.5 + random.nextGaussian()) * 0.2));
                            Vector3d critVel = getDeltaMovement().normalize().multiply(5, 5, 5);
                            level.addParticle(ParticleTypes.CRIT, critPos.x, critPos.y, critPos.z, critVel.x, critVel.y, critVel.z);
                        }

                        //Fire Shrapnel
                        Vector3d shrapnelTravelEnd = traceResult.getLocation().add(getDeltaMovement().normalize().multiply(remainingPenetration, remainingPenetration, remainingPenetration));
                        EntityRayTraceResult result = findHitEntity(traceResult.getLocation(), shrapnelTravelEnd);
                        if (result != null) {
                            Entity entity = result.getEntity();
                            DamageSource damagesource = getDamageSource(entity);
                            float velocity = (float) this.getDeltaMovement().length();
                            int damage = MathHelper.ceil(MathHelper.clamp((double) velocity * this.getBaseDamage(), 0.0D, 2.147483647E9D));
                            entity.hurt(damagesource, (float) damage * 0.75F);
                        }
                    }
                }
            }
        }

        this.lastState = this.level.getBlockState(traceResult.getBlockPos());
        BlockState blockstate = this.level.getBlockState(traceResult.getBlockPos());
        blockstate.onProjectileHit(this.level, blockstate, traceResult, this);
        Vector3d vector3d = traceResult.getLocation().subtract(this.getX(), this.getY(), this.getZ());
        this.setDeltaMovement(vector3d);
        Vector3d vector3d1 = vector3d.normalize().scale((double) 0.05F);
        this.setPosRaw(this.getX() - vector3d1.x, this.getY() - vector3d1.y, this.getZ() - vector3d1.z);
        this.playSound(this.getHitGroundSoundEvent(), 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
        this.inGround = true;
        this.shakeTime = 7;
        this.setCritArrow(false);
        this.setPierceLevel((byte) 0);
        this.setSoundEvent(SoundEvents.ARROW_HIT);
        this.setShotFromCrossbow(false);
        this.resetPiercedEntities();
    }

    // # # # # # # # # # # # #

    public void setEffectsFromItem(ItemStack p_184555_1_) {
        if (p_184555_1_.getItem() == Items.TIPPED_ARROW) {
            this.potion = PotionUtils.getPotion(p_184555_1_);
            Collection<EffectInstance> collection = PotionUtils.getCustomEffects(p_184555_1_);
            if (!collection.isEmpty()) {
                for (EffectInstance effectinstance : collection) {
                    this.effects.add(new EffectInstance(effectinstance));
                }
            }

            int i = getCustomColor(p_184555_1_);
            if (i == -1) {
                this.updateColor();
            } else {
                this.setFixedColor(i);
            }
        } else if (p_184555_1_.getItem() == Items.ARROW) {
            this.potion = Potions.EMPTY;
            this.effects.clear();
            this.entityData.set(ID_EFFECT_COLOR, -1);
        }

    }

    public static int getCustomColor(ItemStack p_191508_0_) {
        CompoundNBT compoundnbt = p_191508_0_.getTag();
        return compoundnbt != null && compoundnbt.contains("CustomPotionColor", 99) ? compoundnbt.getInt("CustomPotionColor") : -1;
    }

    private void updateColor() {
        this.fixedColor = false;
        if (this.potion == Potions.EMPTY && this.effects.isEmpty()) {
            this.entityData.set(ID_EFFECT_COLOR, -1);
        } else {
            this.entityData.set(ID_EFFECT_COLOR, PotionUtils.getColor(PotionUtils.getAllEffects(this.potion, this.effects)));
        }

    }

    public void addEffect(EffectInstance p_184558_1_) {
        this.effects.add(p_184558_1_);
        this.getEntityData().set(ID_EFFECT_COLOR, PotionUtils.getColor(PotionUtils.getAllEffects(this.potion, this.effects)));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ID_EFFECT_COLOR, -1);
        this.entityData.define(SPECTRAL_TIME, 0);
        this.entityData.define(TECH_LEVEL, (byte) TechLevel.DRACONIUM.index);
        this.entityData.define(PENETRATION, (byte) 0);
        this.entityData.define(GRAV_COMPENSATION, 0F);
        this.entityData.define(INIT_VELOCITY, 0F);
        this.entityData.define(PROJ_ANTI_IMMUNE, false);
    }

    private void makeParticle(int p_184556_1_) {
        int i = this.getColor();
        if (i != -1 && p_184556_1_ > 0) {
            double d0 = (double) (i >> 16 & 255) / 255.0D;
            double d1 = (double) (i >> 8 & 255) / 255.0D;
            double d2 = (double) (i >> 0 & 255) / 255.0D;

            for (int j = 0; j < p_184556_1_; ++j) {
                this.level.addParticle(ParticleTypes.ENTITY_EFFECT, this.getRandomX(0.5D), this.getRandomY(), this.getRandomZ(0.5D), d0, d1, d2);
            }

        }
    }

    public int getColor() {
        return this.entityData.get(ID_EFFECT_COLOR);
    }

    private void setFixedColor(int p_191507_1_) {
        this.fixedColor = true;
        this.entityData.set(ID_EFFECT_COLOR, p_191507_1_);
    }

//    private static final DataParameter<Integer> SPECTRAL_TIME = EntityDataManager.defineId(DraconicArrowEntity.class, DataSerializers.INT);
//    private static final DataParameter<Byte> TECH_LEVEL = EntityDataManager.defineId(DraconicArrowEntity.class, DataSerializers.BYTE);
//    private static final DataParameter<Byte> PENETRATION = EntityDataManager.defineId(DraconicArrowEntity.class, DataSerializers.BYTE);
//    private static final DataParameter<Float> GRAV_COMPENSATION = EntityDataManager.defineId(DraconicArrowEntity.class, DataSerializers.FLOAT);
//    private static final DataParameter<Float> INIT_VELOCITY = EntityDataManager.defineId(DraconicArrowEntity.class, DataSerializers.FLOAT); //(Grav comp will deactivate when velocity decreases by say 25%)

    @Override
    public void addAdditionalSaveData(CompoundNBT compound) {
        super.addAdditionalSaveData(compound);
        if (this.potion != Potions.EMPTY && this.potion != null) {
            compound.putString("Potion", Registry.POTION.getKey(this.potion).toString());
        }

        if (this.fixedColor) {
            compound.putInt("Color", this.getColor());
        }

        if (!this.effects.isEmpty()) {
            ListNBT listnbt = new ListNBT();

            for (EffectInstance effectinstance : this.effects) {
                listnbt.add(effectinstance.save(new CompoundNBT()));
            }

            compound.put("CustomPotionEffects", listnbt);
        }

        if (getSpectralTime() > 0){
            compound.putInt("spectral_time", entityData.get(SPECTRAL_TIME));
        }
        compound.putByte("tech_level", entityData.get(TECH_LEVEL));
        compound.putByte("penetration", entityData.get(PENETRATION));
        compound.putFloat("grav_comp", entityData.get(GRAV_COMPENSATION));
        compound.putFloat("init_velocity", entityData.get(INIT_VELOCITY));
        compound.putBoolean("proj_anti_immune", entityData.get(PROJ_ANTI_IMMUNE));
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("Potion", 8)) {
            this.potion = PotionUtils.getPotion(compound);
        }

        for (EffectInstance effectinstance : PotionUtils.getCustomEffects(compound)) {
            this.addEffect(effectinstance);
        }

        if (compound.contains("Color", 99)) {
            this.setFixedColor(compound.getInt("Color"));
        } else {
            this.updateColor();
        }

        if (compound.contains("spectral_time")) {
            setSpectral(compound.getInt("spectral_time"));
        }
        if (compound.contains("tech_level")){
            entityData.set(TECH_LEVEL, compound.getByte("tech_level"));
        }
        if (compound.contains("penetration")){
            entityData.set(PENETRATION, compound.getByte("penetration"));
        }
        if (compound.contains("grav_comp")){
            entityData.set(GRAV_COMPENSATION, compound.getFloat("grav_comp"));
        }
        if (compound.contains("init_velocity")){
            entityData.set(INIT_VELOCITY, compound.getFloat("init_velocity"));
        }
        if (compound.contains("proj_anti_immune")){
            entityData.set(PROJ_ANTI_IMMUNE, compound.getBoolean("proj_anti_immune"));
        }

    }

    @Override
    protected void doPostHurtEffects(LivingEntity p_184548_1_) {
        super.doPostHurtEffects(p_184548_1_);

        for (EffectInstance effectinstance : this.potion.getEffects()) {
            p_184548_1_.addEffect(new EffectInstance(effectinstance.getEffect(), Math.max(effectinstance.getDuration() / 8, 1), effectinstance.getAmplifier(), effectinstance.isAmbient(), effectinstance.isVisible()));
        }

        if (!this.effects.isEmpty()) {
            for (EffectInstance effectinstance1 : this.effects) {
                p_184548_1_.addEffect(effectinstance1);
            }
        }

        int spectralTime = entityData.get(SPECTRAL_TIME);
        if (spectralTime > 0) {
            EffectInstance effectinstance = new EffectInstance(Effects.GLOWING, spectralTime, 0);
            p_184548_1_.addEffect(effectinstance);
        }
    }

    @Override
    protected ItemStack getPickupItem() {
        if (this.effects.isEmpty() && this.potion == Potions.EMPTY) {
            return new ItemStack(Items.ARROW);
        } else {
            ItemStack itemstack = new ItemStack(Items.TIPPED_ARROW);
            PotionUtils.setPotion(itemstack, this.potion);
            PotionUtils.setCustomEffects(itemstack, this.effects);
            if (this.fixedColor) {
                itemstack.getOrCreateTag().putInt("CustomPotionColor", this.getColor());
            }

            return itemstack;
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void handleEntityEvent(byte p_70103_1_) {
        if (p_70103_1_ == 0) {
            int i = this.getColor();
            if (i != -1) {
                double d0 = (double) (i >> 16 & 255) / 255.0D;
                double d1 = (double) (i >> 8 & 255) / 255.0D;
                double d2 = (double) (i >> 0 & 255) / 255.0D;

                for (int j = 0; j < 20; ++j) {
                    this.level.addParticle(ParticleTypes.ENTITY_EFFECT, this.getRandomX(0.5D), this.getRandomY(), this.getRandomZ(0.5D), d0, d1, d2);
                }
            }
        } else {
            super.handleEntityEvent(p_70103_1_);
        }
    }

    @Override
    public IPacket<?> getAddEntityPacket() {
        return BCoreNetwork.getEntitySpawnPacket(this);
//        return NetworkHooks.getEntitySpawningPacket(this);
    }
}