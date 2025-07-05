package com.brandon3055.draconicevolution.entity.projectile;

import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.brandonscore.network.BCoreNetwork;
import com.brandon3055.draconicevolution.DEConfig;
import com.brandon3055.draconicevolution.init.DEContent;
import com.brandon3055.draconicevolution.init.DEDamage;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import net.covers1624.quack.util.SneakyUtils;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.*;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.event.EventHooks;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

/**
 * Created by brandon3055 on 8/7/21
 */
public class DraconicArrowEntity extends AbstractArrow {
    private static final EntityDataAccessor<Integer> ID_EFFECT_COLOR = SynchedEntityData.defineId(DraconicArrowEntity.class, EntityDataSerializers.INT);
    private static final ItemStack DEFAULT_ARROW_STACK = new ItemStack(Items.ARROW);
//    private Potion potion = Potions.EMPTY;
    private final Set<MobEffectInstance> effects = Sets.newHashSet();
    private boolean fixedColor;

    private static final EntityDataAccessor<Integer> SPECTRAL_TIME = SynchedEntityData.defineId(DraconicArrowEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Byte> TECH_LEVEL = SynchedEntityData.defineId(DraconicArrowEntity.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Byte> PENETRATION = SynchedEntityData.defineId(DraconicArrowEntity.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Float> GRAV_COMPENSATION = SynchedEntityData.defineId(DraconicArrowEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> INIT_VELOCITY = SynchedEntityData.defineId(DraconicArrowEntity.class, EntityDataSerializers.FLOAT); //(Grav comp will deactivate when velocity decreases by say 25%)
    private static final EntityDataAccessor<Boolean> PROJ_ANTI_IMMUNE = SynchedEntityData.defineId(DraconicArrowEntity.class, EntityDataSerializers.BOOLEAN);

    public DraconicArrowEntity(EntityType<? extends DraconicArrowEntity> entityType, Level level) {
        super(entityType, level);
    }

    public DraconicArrowEntity(Level world, double xPos, double yPos, double zPos, ItemStack arrowStack, @Nullable ItemStack weapon) {
        super(DEContent.ENTITY_DRACONIC_ARROW.get(), xPos, yPos, zPos, world, arrowStack, weapon);
    }

    public DraconicArrowEntity(Level world, LivingEntity shooter, ItemStack arrowStack, @Nullable ItemStack weapon) {
        super(DEContent.ENTITY_DRACONIC_ARROW.get(), shooter, world, arrowStack, weapon);
    }

    private PotionContents getPotionContents() {
        return this.getPickupItemStackOrigin().getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
    }

    private void setPotionContents(PotionContents p_331534_) {
        this.getPickupItemStackOrigin().set(DataComponents.POTION_CONTENTS, p_331534_);
        this.updateColor();
    }

    @Override
    protected void setPickupItemStack(ItemStack p_331667_) {
        super.setPickupItemStack(p_331667_);
        this.updateColor();
    }

    @Override
    protected ItemStack getDefaultPickupItem() {
        return new ItemStack(Items.ARROW);
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

        if (this.level().isClientSide) {
            if (this.inGround) {
                if (this.inGroundTime % 5 == 0) {
                    this.makeParticle(1);
                }
            } else {
                this.makeParticle(2);
            }
        } else if (this.inGround && this.inGroundTime != 0 && !this.getPotionContents().equals(PotionContents.EMPTY) && this.inGroundTime >= 600) {
            this.level().broadcastEntityEvent(this, (byte)0);
            this.setPickupItemStack(new ItemStack(Items.ARROW));
        }
    }

    private void superTick() {
        //Projectile Entity Tick
        if (!this.leftOwner) {
            this.leftOwner = this.checkLeftOwner();
        }
        //Entity Tick
        if (!level().isClientSide) {
            this.setSharedFlag(6, this.isCurrentlyGlowing());
        }
        this.baseTick();

        //Abstract Arrow Tick
        boolean flag = this.isNoPhysics();
        Vec3 vector3d = this.getDeltaMovement();
        if (this.xRotO == 0.0F && this.yRotO == 0.0F) {
            double f = vector3d.horizontalDistance();
            this.setYRot((float) (Mth.atan2(vector3d.x, vector3d.z) * (double) (180F / (float) Math.PI)));
            this.setXRot((float) (Mth.atan2(vector3d.y, (double) f) * (double) (180F / (float) Math.PI)));
            this.yRotO = this.getYRot();
            this.xRotO = this.getXRot();
        }

        BlockPos blockpos = this.blockPosition();
        BlockState blockstate = level().getBlockState(blockpos);
        if (!blockstate.isAir() && !flag) {
            VoxelShape voxelshape = blockstate.getCollisionShape(level(), blockpos);
            if (!voxelshape.isEmpty()) {
                Vec3 vector3d1 = this.position();

                for (AABB axisalignedbb : voxelshape.toAabbs()) {
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
            } else if (!level().isClientSide) {
                this.tickDespawn();
            }

            ++this.inGroundTime;
        } else {
            this.inGroundTime = 0;
            Vec3 vector3d2 = this.position();
            Vec3 vector3d3 = vector3d2.add(vector3d);
            HitResult raytraceresult = level().clip(new ClipContext(vector3d2, vector3d3, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
            if (raytraceresult.getType() != HitResult.Type.MISS) {
                vector3d3 = raytraceresult.getLocation();
            }

            while (!this.isRemoved()) {
                EntityHitResult entityraytraceresult = this.findHitEntity(vector3d2, vector3d3);
                if (entityraytraceresult != null) {
                    raytraceresult = entityraytraceresult;
                }

                if (raytraceresult != null && raytraceresult.getType() == HitResult.Type.ENTITY) {
                    Entity entity = ((EntityHitResult) raytraceresult).getEntity();
                    Entity entity1 = this.getOwner();
                    if (entity instanceof Player && entity1 instanceof Player && !((Player) entity1).canHarmPlayer((Player) entity)) {
                        raytraceresult = null;
                        entityraytraceresult = null;
                    }
                }

                if (raytraceresult != null && raytraceresult.getType() != HitResult.Type.MISS && !flag) {
                    if (EventHooks.onProjectileImpact(this, raytraceresult)) {
                        break;
                    }
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
                    level().addParticle(ParticleTypes.CRIT, this.getX() + d3 * (double) i / 4.0D, this.getY() + d4 * (double) i / 4.0D, this.getZ() + d0 * (double) i / 4.0D, -d3, -d4 + 0.2D, -d0);
                }
            }

            double d5 = this.getX() + d3;
            double d1 = this.getY() + d4;
            double d2 = this.getZ() + d0;
            double f1 = vector3d.horizontalDistance();
            if (flag) {
                this.setYRot((float) (Mth.atan2(-d3, -d0) * (double) (180F / (float) Math.PI)));
            } else {
                this.setYRot((float) (Mth.atan2(d3, d0) * (double) (180F / (float) Math.PI)));
            }

            this.setXRot((float) (Mth.atan2(d4, (double) f1) * (double) (180F / (float) Math.PI)));
            this.setXRot(lerpRotation(this.xRotO, this.getXRot()));
            this.setYRot(lerpRotation(this.yRotO, this.getYRot()));
            float f2 = 0.99F;
            float f3 = 0.05F;
            if (this.isInWater()) {
                for (int j = 0; j < 4; ++j) {
                    float f4 = 0.25F;
                    level().addParticle(ParticleTypes.BUBBLE, d5 - d3 * 0.25D, d1 - d4 * 0.25D, d2 - d0 * 0.25D, d3, d4, d0);
                }

                f2 = this.getWaterInertia();
            }

            this.setDeltaMovement(vector3d.scale((double) f2));
            if (!this.isNoGravity() && !flag) {
                Vec3 vector3d4 = this.getDeltaMovement();
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
    protected void onHitEntity(EntityHitResult p_213868_1_) {
        Entity entity = p_213868_1_.getEntity();
        float velLength = (float) this.getDeltaMovement().length();
        double baseDamage = this.getBaseDamage();
        Entity owner = this.getOwner();
        DamageSource damagesource = getDamageSource(entity);
        if (this.getWeaponItem() != null && this.level() instanceof ServerLevel serverlevel) {
            baseDamage = EnchantmentHelper.modifyDamage(serverlevel, this.getWeaponItem(), entity, damagesource, (float)baseDamage);
        }
        int damage = Mth.ceil(Mth.clamp((double)velLength * baseDamage, 0.0D, 2.147483647E9D));

        int penetration = entityData.get(PENETRATION);
        if (this.getPierceLevel() > 0 || penetration > 0) {
            if (this.piercingIgnoreEntityIds == null) {
                this.piercingIgnoreEntityIds = new IntOpenHashSet(5);
            }

            if (this.piercedAndKilledEntities == null) {
                this.piercedAndKilledEntities = Lists.newArrayListWithCapacity(5);
            }

            if (this.piercingIgnoreEntityIds.size() >= this.getPierceLevel() + 1 && this.piercingIgnoreEntityIds.size() >= penetration + 1) {
                this.discard();
                return;
            }

            this.piercingIgnoreEntityIds.add(entity.getId());
        }

        if (this.isCritArrow()) {
            long j = (long) this.random.nextInt(damage / 2 + 2);
            damage = (int) Math.min(j + (long) damage, 2147483647L);
        }

        boolean isEnderman = entity.getType() == EntityType.ENDERMAN;
        int k = entity.getRemainingFireTicks();
        if (this.isOnFire() && !isEnderman) {
            entity.setRemainingFireTicks(5);
        }

        //Break shields with penetration
        if (penetration > 1 && entity instanceof Player) {
            Player player = (Player) entity;
            if (player.isUsingItem() && player.getUseItem().getItem() instanceof ShieldItem) {
                player.getCooldowns().addCooldown(player.getUseItem().getItem(), 100);
                level().broadcastEntityEvent(player, (byte) 30);
                player.stopUsingItem();
            }
        }

        if (entity.hurt(damagesource, (float) damage)) {
            if (isEnderman) {
                return;
            }

            if (entity instanceof LivingEntity livingentity) {
                if (!level().isClientSide && this.getPierceLevel() <= 0 && penetration <= 0) {
                    livingentity.setArrowCount(livingentity.getArrowCount() + 1);
                }

                this.doKnockback(livingentity, damagesource);
                if (this.level() instanceof ServerLevel serverlevel1) {
                    EnchantmentHelper.doPostAttackEffectsWithItemSource(serverlevel1, livingentity, damagesource, this.getWeaponItem());
                }

                this.doPostHurtEffects(livingentity);
                if (livingentity != owner && livingentity instanceof Player && owner instanceof ServerPlayer && !this.isSilent()) {
                    ((ServerPlayer) owner).connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.ARROW_HIT_PLAYER, 0.0F));
                }

                if (!entity.isAlive() && this.piercedAndKilledEntities != null) {
                    this.piercedAndKilledEntities.add(livingentity);
                }

                if (!level().isClientSide && owner instanceof ServerPlayer serverPlayer) {
                    if (this.piercedAndKilledEntities != null && this.shotFromCrossbow()) {
                        CriteriaTriggers.KILLED_BY_CROSSBOW.trigger(serverPlayer, this.piercedAndKilledEntities);
                    } else if (!entity.isAlive() && this.shotFromCrossbow()) {
                        CriteriaTriggers.KILLED_BY_CROSSBOW.trigger(serverPlayer, Arrays.asList(entity));
                    }
                }
            }

            this.playSound(this.getHitGroundSoundEvent(), 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
            if (this.getPierceLevel() <= 0 && penetration <= 0) {
                this.discard();
            }
        } else {
            entity.setRemainingFireTicks(k);
            this.setDeltaMovement(this.getDeltaMovement().scale(0));
            this.setYRot(getYRot() + 180.0F);
            this.yRotO += 180.0F;
            if (!level().isClientSide && this.getDeltaMovement().lengthSqr() < 1.0E-7D) {
                if (this.pickup == AbstractArrow.Pickup.ALLOWED) {
                    this.spawnAtLocation(this.getPickupItem(), 0.1F);
                }

                this.discard();
            }
        }
    }

    private DamageSource getDamageSource(Entity target) {
        Entity owner = this.getOwner();
        TechLevel techLevel = TechLevel.byIndex(entityData.get(TECH_LEVEL));
        boolean bypassImmune = getProjectileImmuneOverride() && DEConfig.projectileAntiImmuneEntities.contains(BuiltInRegistries.ENTITY_TYPE.getKey(target.getType()).toString());
        return DEDamage.draconicArrow(level(), this, owner, techLevel, bypassImmune);
    }

    private int blockPenetration = 0;

    @Override
    protected void onHitBlock(BlockHitResult traceResult) {
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
            BlockState blockstate = level().getBlockState(pos);
            if (!blockstate.isAir()) {
                VoxelShape voxelshape = blockstate.getCollisionShape(level(), pos);
                if (!voxelshape.isEmpty()) {
                    boolean canPass = true;
                    Vec3 vector3d1 = traceResult.getLocation().add(getDeltaMovement().normalize().multiply(canPenetrate, canPenetrate, canPenetrate));

                    for (AABB axisalignedbb : voxelshape.toAabbs()) {
                        if (axisalignedbb.move(pos).contains(vector3d1)) {
                            canPass = false;
                            break;
                        }
                    }
                    if (canPass) {
                        this.playSound(SoundEvents.ANCIENT_DEBRIS_BREAK, 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
                        for (int i = 0; i < 25; i++) {
                            Vec3 critPos = traceResult.getLocation().add(getDeltaMovement().normalize().multiply(2, 2, 2).add((-0.5 + random.nextGaussian()) * 0.2, (-0.5 + random.nextGaussian()) * 0.2, (-0.5 + random.nextGaussian()) * 0.2));
                            Vec3 critVel = getDeltaMovement().normalize().multiply(5, 5, 5);
                            level().addParticle(ParticleTypes.CRIT, critPos.x, critPos.y, critPos.z, critVel.x, critVel.y, critVel.z);
                        }

                        //Fire Shrapnel
                        Vec3 shrapnelTravelEnd = traceResult.getLocation().add(getDeltaMovement().normalize().multiply(remainingPenetration, remainingPenetration, remainingPenetration));
                        EntityHitResult result = findHitEntity(traceResult.getLocation(), shrapnelTravelEnd);
                        if (result != null) {
                            Entity entity = result.getEntity();
                            DamageSource damagesource = getDamageSource(entity);
                            float velocity = (float) this.getDeltaMovement().length();
                            int damage = Mth.ceil(Mth.clamp((double) velocity * this.getBaseDamage(), 0.0D, 2.147483647E9D));
                            entity.hurt(damagesource, (float) damage * 0.75F);
                        }
                    }
                }
            }
        }

        this.lastState = level().getBlockState(traceResult.getBlockPos());
        BlockState blockstate = level().getBlockState(traceResult.getBlockPos());
        blockstate.onProjectileHit(level(), blockstate, traceResult, this);
        Vec3 vector3d = traceResult.getLocation().subtract(this.getX(), this.getY(), this.getZ());
        this.setDeltaMovement(vector3d);
        Vec3 vector3d1 = vector3d.normalize().scale((double) 0.05F);
        this.setPosRaw(this.getX() - vector3d1.x, this.getY() - vector3d1.y, this.getZ() - vector3d1.z);
        this.playSound(this.getHitGroundSoundEvent(), 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
        this.inGround = true;
        this.shakeTime = 7;
        this.setCritArrow(false);
        this.setPierceLevel((byte) 0);
        this.setSoundEvent(SoundEvents.ARROW_HIT);
        this.resetPiercedEntities();
    }

    // # # # # # # # # # # # #

    private void updateColor() {
        PotionContents potioncontents = this.getPotionContents();
        this.entityData.set(ID_EFFECT_COLOR, potioncontents.equals(PotionContents.EMPTY) ? -1 : potioncontents.getColor());
    }

    public void addEffect(MobEffectInstance p_36871_) {
        this.setPotionContents(this.getPotionContents().withEffectAdded(p_36871_));
    }
    

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(ID_EFFECT_COLOR, -1);
        builder.define(SPECTRAL_TIME, 0);
        builder.define(TECH_LEVEL, (byte) TechLevel.DRACONIUM.index);
        builder.define(PENETRATION, (byte) 0);
        builder.define(GRAV_COMPENSATION, 0F);
        builder.define(INIT_VELOCITY, 0F);
        builder.define(PROJ_ANTI_IMMUNE, false);
    }

    private void makeParticle(int p_36877_) {
        int i = this.getColor();
        if (i != -1 && p_36877_ > 0) {
            for (int j = 0; j < p_36877_; j++) {
                this.level()
                        .addParticle(
                                ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, i),
                                this.getRandomX(0.5),
                                this.getRandomY(),
                                this.getRandomZ(0.5),
                                0.0,
                                0.0,
                                0.0
                        );
            }
        }
    }

    public int getColor() {
        return this.entityData.get(ID_EFFECT_COLOR);
    }

//    private static final DataParameter<Integer> SPECTRAL_TIME = EntityDataManager.defineId(DraconicArrowEntity.class, DataSerializers.INT);
//    private static final DataParameter<Byte> TECH_LEVEL = EntityDataManager.defineId(DraconicArrowEntity.class, DataSerializers.BYTE);
//    private static final DataParameter<Byte> PENETRATION = EntityDataManager.defineId(DraconicArrowEntity.class, DataSerializers.BYTE);
//    private static final DataParameter<Float> GRAV_COMPENSATION = EntityDataManager.defineId(DraconicArrowEntity.class, DataSerializers.FLOAT);
//    private static final DataParameter<Float> INIT_VELOCITY = EntityDataManager.defineId(DraconicArrowEntity.class, DataSerializers.FLOAT); //(Grav comp will deactivate when velocity decreases by say 25%)

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);

        if (this.fixedColor) {
            compound.putInt("Color", this.getColor());
        }

        if (getSpectralTime() > 0) {
            compound.putInt("spectral_time", entityData.get(SPECTRAL_TIME));
        }
        compound.putByte("tech_level", entityData.get(TECH_LEVEL));
        compound.putByte("penetration", entityData.get(PENETRATION));
        compound.putFloat("grav_comp", entityData.get(GRAV_COMPENSATION));
        compound.putFloat("init_velocity", entityData.get(INIT_VELOCITY));
        compound.putBoolean("proj_anti_immune", entityData.get(PROJ_ANTI_IMMUNE));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        
        if (compound.contains("spectral_time")) {
            setSpectral(compound.getInt("spectral_time"));
        }
        if (compound.contains("tech_level")) {
            entityData.set(TECH_LEVEL, compound.getByte("tech_level"));
        }
        if (compound.contains("penetration")) {
            entityData.set(PENETRATION, compound.getByte("penetration"));
        }
        if (compound.contains("grav_comp")) {
            entityData.set(GRAV_COMPENSATION, compound.getFloat("grav_comp"));
        }
        if (compound.contains("init_velocity")) {
            entityData.set(INIT_VELOCITY, compound.getFloat("init_velocity"));
        }
        if (compound.contains("proj_anti_immune")) {
            entityData.set(PROJ_ANTI_IMMUNE, compound.getBoolean("proj_anti_immune"));
        }

    }

    @Override
    protected void doPostHurtEffects(LivingEntity hit) {
        super.doPostHurtEffects(hit);
        Entity entity = this.getEffectSource();
        PotionContents potioncontents = this.getPotionContents();
        if (potioncontents.potion().isPresent()) {
            for (MobEffectInstance mobeffectinstance : potioncontents.potion().get().value().getEffects()) {
                hit.addEffect(
                        new MobEffectInstance(
                                mobeffectinstance.getEffect(),
                                Math.max(mobeffectinstance.mapDuration(p_268168_ -> p_268168_ / 8), 1),
                                mobeffectinstance.getAmplifier(),
                                mobeffectinstance.isAmbient(),
                                mobeffectinstance.isVisible()
                        ),
                        entity
                );
            }
        }

        for (MobEffectInstance mobeffectinstance1 : potioncontents.customEffects()) {
            hit.addEffect(mobeffectinstance1, entity);
        }
    }

    @Override
    public void handleEntityEvent(byte p_36869_) {
        if (p_36869_ == 0) {
            int i = this.getColor();
            if (i != -1) {
                float f = (float)(i >> 16 & 0xFF) / 255.0F;
                float f1 = (float)(i >> 8 & 0xFF) / 255.0F;
                float f2 = (float)(i >> 0 & 0xFF) / 255.0F;

                for (int j = 0; j < 20; j++) {
                    this.level()
                            .addParticle(
                                    ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, f, f1, f2),
                                    this.getRandomX(0.5),
                                    this.getRandomY(),
                                    this.getRandomZ(0.5),
                                    0.0,
                                    0.0,
                                    0.0
                            );
                }
            }
        } else {
            super.handleEntityEvent(p_36869_);
        }
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity serverEntity) {
        Entity entity = this.getOwner();
        return SneakyUtils.unsafeCast(BCoreNetwork.getEntitySpawnPacket(this, serverEntity, entity == null ? 0 : entity.getId()));
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return source.is(DamageTypeTags.IS_FIRE) || super.isInvulnerableTo(source);
    }
}