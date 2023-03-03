package com.brandon3055.draconicevolution.entity.projectile;

import com.brandon3055.brandonscore.network.BCoreNetwork;
import com.brandon3055.brandonscore.utils.Utils;
import com.brandon3055.draconicevolution.api.modules.Module;
import com.brandon3055.draconicevolution.api.modules.data.DamageModData;
import com.brandon3055.draconicevolution.api.modules.lib.IDamageModifier;
import com.brandon3055.draconicevolution.init.DEContent;
import com.brandon3055.draconicevolution.items.equipment.damage.DefaultStaffDmgMod;
import com.brandon3055.draconicevolution.lib.Serializers;
import com.google.common.collect.Sets;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;

/**
 * This is effectively a complete re implementation of the vanilla arrow supporting all of the vanilla features.
 * But on top of that it has support for all my custom damage and effects.
 */
@Deprecated //Was going to use this for both the staff and the bow but now i'm going to use something simpler for the bow and i'm not sure about the staff at this point.
public class DraconicProjectileEntity extends AbstractArrow {
    private static final EntityDataAccessor<Integer> COLOR = SynchedEntityData.defineId(DraconicProjectileEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> ARROW_PROJECTILE = SynchedEntityData.defineId(DraconicProjectileEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> NO_DRAG = SynchedEntityData.defineId(DraconicProjectileEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> EFFECT_COLOR = SynchedEntityData.defineId(DraconicProjectileEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Optional<Module<?>>> DAMAGE_MODIFIER = SynchedEntityData.defineId(DraconicProjectileEntity.class, Serializers.OPT_MODULE_SERIALIZER);
    private static final IDamageModifier defaultStaffModifier = new DefaultStaffDmgMod();
    private Potion potion = Potions.EMPTY;
    private final Set<MobEffectInstance> customPotionEffects = Sets.newHashSet();
    private boolean fixedColor;
    private int flightTime = 0;
    private int maxFlightTime = -1;
    private int explosivePower = 0;
    private boolean explodeBlocks = false;
    private float projectileBaseDamage = 0;
    private float secondaryChange = 0;

    //This is a tiny hack that allows the staff's default attack to use a "default" damage modifier.
    private boolean useDefaultStaffModifier = false;
    private boolean pseudoInstantTravel = false;

    public DraconicProjectileEntity(EntityType<? extends DraconicProjectileEntity> type, Level worldIn) {
        super(type, worldIn);
    }

    public DraconicProjectileEntity(Level worldIn, double x, double y, double z) {
        super(DEContent.draconicArrow, x, y, z, worldIn);
    }

    public DraconicProjectileEntity(Level worldIn, LivingEntity shooter) {
        super(DEContent.draconicArrow, shooter, worldIn);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(COLOR, -1);
        this.entityData.define(ARROW_PROJECTILE, true);
        this.entityData.define(NO_DRAG, false);
        this.entityData.define(EFFECT_COLOR, -1);
        this.entityData.define(DAMAGE_MODIFIER, Optional.empty());
    }

    // Custom Projectile Configuration

    /**
     * Controls weather this acts and renders like a regular arrow projectile that would be fired from a bow.
     * The alternative is this acts like an energy projectile such as those fired from the staff.
     * @param isArrowProjectile true if this is an arrow type projectile.
     */
    public void setArrowProjectile(boolean isArrowProjectile) {
        entityData.set(ARROW_PROJECTILE, isArrowProjectile);
    }

    public boolean isArrowProjectile() {
        return entityData.get(ARROW_PROJECTILE);
    }

    public void setDamageModifier(Module<?> damageModule) {
        if (damageModule != null && damageModule.getData() instanceof DamageModData) {
            entityData.set(DAMAGE_MODIFIER, Optional.of(damageModule));
        } else {
            entityData.set(DAMAGE_MODIFIER, Optional.empty());
        }
    }

    @Nullable
    public Module<DamageModData> getDamageModifier() {
        Optional<Module<?>> opt = entityData.get(DAMAGE_MODIFIER);
        if (opt.isPresent() && opt.get().getData() instanceof DamageModData) {
            //noinspection unchecked
            return (Module<DamageModData>) opt.get();
        }
        return null;
    }

    /**
     * Disables air resistance so the projectile will not slow down.
     * Note that if gravity is enabled this will effect how the arrow arcs because
     * normally part of the downward velocity is canceled out by drag.
     * @param hasNoDrag has drag boolean
     */
    public void setNoDrag(boolean hasNoDrag) {
        entityData.set(NO_DRAG, hasNoDrag);
    }

    public boolean hasNoDrag() {
        return entityData.get(NO_DRAG);
    }

    public void setMaxFlightTime(int maxFlightTime) {
        this.maxFlightTime = maxFlightTime;
    }

    /**
     * If this is set the projectile will trigger a simple explosion when id collides with something or when it expires.
     * Is overridden if there is a damage mod applied.
     * @param explosivePower tnt equivalent explosive power.
     */
    public void setExplosivePower(int explosivePower, boolean damageBlocks) {
        this.explosivePower = explosivePower;
        this.explodeBlocks = damageBlocks;
    }

    /**
     * Controled the colour of the base non-arrow projectile (with no damage mod applied)
     * @param colour The effect render colour.
     */
    public void setEffectColour(int colour) {
        entityData.set(EFFECT_COLOR, colour);
    }

    public int getEffectColor() {
        return entityData.get(EFFECT_COLOR);
    }

    public void useDefaultStaffModifier() {
        useDefaultStaffModifier = true;
    }

    /**
     * Automatically set when power is >= 100.
     * Forces projectile to activate when it leaves loaded chunks to avoid
     * visual "misfires" where you fire but nothing happens.
     * */
    public void setPseudoInstantTravel(boolean pseudoInstantTravel) {
        this.pseudoInstantTravel = pseudoInstantTravel;
    }

    /**
     * This is the power value used to calculate damage dealt by non-arrow projectiles
     * The effect this value has varies depending on the damage mod.
     * */
    public void setProjectileBaseDamage(float projectileBaseDamage) {
        this.projectileBaseDamage = projectileBaseDamage;
    }

    // Custom Projectile Logic

    /**
     * Called when the arrow hits something or expires in flight.
     * Detonates the projectiles AOE payload if it has one which expends the
     * damage modifier if it has one.
     *
     * This also triggers the death of non arrow type projectiles.
     *
     * @return true prevents normal arrow damage processing.
     * */
    protected boolean activateDamageEffect(@Nullable HitResult traceResult) {
        if (level.isClientSide) return true;
        Entity owner = getOwner();
        Module<DamageModData> damageMod = getDamageModifier();
        Vec3 pos = position();
        if (traceResult != null) {
            if (traceResult instanceof EntityHitResult) {
                pos = traceResult.getLocation();
            } else {
                Vec3 hitPos = traceResult.getLocation();
                if (hitPos.distanceTo(pos) > 1) {
                    Vec3 dirVec = pos.subtract(hitPos).normalize();
                    pos = hitPos.add(dirVec); //Helps ensure the explosion does not occur inside a block which could limit its power.
                }
            }
        }

        boolean disableDefault = false;
        if (damageMod != null || useDefaultStaffModifier) {
            IDamageModifier modifier;
            if (useDefaultStaffModifier) {
                modifier = defaultStaffModifier;
            } else {
                modifier = damageMod.getData().modifier();
            }
            modifier.doDamageAndEffects(level, pos, traceResult, owner instanceof LivingEntity ? (LivingEntity) owner : null, projectileBaseDamage, secondaryChange, true);
            setDamageModifier(null);
            disableDefault = true;
        } else if (explosivePower > 0) {
            if (owner instanceof LivingEntity) {
                level.explode(owner, DamageSource.explosion((LivingEntity)owner), null, pos.x, pos.y, pos.z, explosivePower, false, explodeBlocks ? Explosion.BlockInteraction.DESTROY : Explosion.BlockInteraction.NONE);
            }else {
                level.explode(this, pos.x, pos.y, pos.z, explosivePower, false, explodeBlocks ? Explosion.BlockInteraction.DESTROY : Explosion.BlockInteraction.NONE);
            }

            explosivePower = 0;
            disableDefault = true;
        }

        if (!isArrowProjectile()) {
            kill();
        }
        return disableDefault;
    }

    @Override
    public void shootFromRotation(Entity p_234612_1_, float playerXRot, float playerYRot, float someZeroOffset, float power, float inaccuracy) {
        if (power >= 100) {
            setPseudoInstantTravel(true);
        }
        super.shootFromRotation(p_234612_1_, playerXRot, playerYRot, someZeroOffset, power, inaccuracy);
    }

    @Override
    protected void onHitEntity(EntityHitResult entityRayTraceResult) {
        if (activateDamageEffect(entityRayTraceResult)) {
            return;
        }
        super.onHitEntity(entityRayTraceResult);
    }

    @Override
    protected void onHitBlock(BlockHitResult blockRayTraceResult) {
        if (activateDamageEffect(blockRayTraceResult)) {
            return;
        }
        super.onHitBlock(blockRayTraceResult);
    }

    // Mostly Vanilla stuff

    public void setPotionEffect(ItemStack stack) {
        if (stack.getItem() == Items.TIPPED_ARROW) {
            this.potion = PotionUtils.getPotion(stack);
            Collection<MobEffectInstance> collection = PotionUtils.getCustomEffects(stack);
            if (!collection.isEmpty()) {
                for (MobEffectInstance effectinstance : collection) {
                    this.customPotionEffects.add(new MobEffectInstance(effectinstance));
                }
            }

            int i = getCustomColor(stack);
            if (i == -1) {
                this.refreshColor();
            } else {
                this.setFixedColor(i);
            }
        } else if (stack.getItem() == Items.ARROW) {
            this.potion = Potions.EMPTY;
            this.customPotionEffects.clear();
            this.entityData.set(COLOR, -1);
        }

    }

    public static int getCustomColor(ItemStack colour) {
        CompoundTag compoundnbt = colour.getTag();
        return compoundnbt != null && compoundnbt.contains("CustomPotionColor", 99) ? compoundnbt.getInt("CustomPotionColor") : -1;
    }

    private void refreshColor() {
        this.fixedColor = false;
        if (this.potion == Potions.EMPTY && this.customPotionEffects.isEmpty()) {
            this.entityData.set(COLOR, -1);
        } else {
            this.entityData.set(COLOR, PotionUtils.getColor(PotionUtils.getAllEffects(this.potion, this.customPotionEffects)));
        }
    }

    public void addEffect(MobEffectInstance effect) {
        this.customPotionEffects.add(effect);
        this.getEntityData().set(COLOR, PotionUtils.getColor(PotionUtils.getAllEffects(this.potion, this.customPotionEffects)));
    }

    @Override
    public void tick() {
        boolean forceActivation = pseudoInstantTravel && !Utils.isAreaLoaded(level, new BlockPos(position().add(getDeltaMovement().multiply(1.1, 1.1, 1.1))), ChunkHolder.FullChunkStatus.ENTITY_TICKING);

        //Drag mitigation. This is a little nasty but it works and there really isn't a better way to do it without mixins.
        if (hasNoDrag()) {
            Vec3 velocity = getDeltaMovement();
            super.tick();
            if (!isNoGravity()) { //This is needed because the drag mitigation disables gravity.
                velocity = velocity.subtract(0, 0.05, 0);
            }
            setDeltaMovement(velocity);
        } else {
            super.tick();
        }

        if (this.level.isClientSide) {
            if (this.inGround) {
                if (this.inGroundTime % 5 == 0) {
                    this.spawnPotionParticles(1);
                }
            } else {
                this.spawnPotionParticles(2);
            }
        } else if (this.inGround && this.inGroundTime != 0 && !this.customPotionEffects.isEmpty() && this.inGroundTime >= 600) {
            this.level.broadcastEntityEvent(this, (byte) 0);
            this.potion = Potions.EMPTY;
            this.customPotionEffects.clear();
            this.entityData.set(COLOR, -1);
        }

        if (!inGround) {
            if (maxFlightTime > 0) {
                flightTime++;
                if (flightTime >= maxFlightTime || forceActivation) {
                    activateDamageEffect(null);
                }
            }
        } else {
            flightTime = 0;
        }
    }

    private void spawnPotionParticles(int particleCount) {
        int i = this.getColor();
        if (i != -1 && particleCount > 0) {
            double d0 = (double) (i >> 16 & 255) / 255.0D;
            double d1 = (double) (i >> 8 & 255) / 255.0D;
            double d2 = (double) (i >> 0 & 255) / 255.0D;

            for (int j = 0; j < particleCount; ++j) {
                this.level.addParticle(ParticleTypes.ENTITY_EFFECT, this.getRandomX(0.5D), this.getRandomY(), this.getRandomZ(0.5D), d0, d1, d2);
            }

        }
    }

    public int getColor() {
        return this.entityData.get(COLOR);
    }

    private void setFixedColor(int colour) {
        this.fixedColor = true;
        this.entityData.set(COLOR, colour);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        if (this.potion != Potions.EMPTY && this.potion != null) {
            compound.putString("Potion", Registry.POTION.getKey(this.potion).toString());
        }

        if (this.fixedColor) {
            compound.putInt("Color", this.getColor());
        }

        if (!this.customPotionEffects.isEmpty()) {
            ListTag listnbt = new ListTag();

            for (MobEffectInstance effectinstance : this.customPotionEffects) {
                listnbt.add(effectinstance.save(new CompoundTag()));
            }

            compound.put("CustomPotionEffects", listnbt);
        }

        if (maxFlightTime != -1) {
            compound.putInt("MaxFlightTime", maxFlightTime);
            compound.putInt("FlightTime", flightTime);
        }
        if (explosivePower > 0) {
            compound.putShort("ExplosivePower", (short) explosivePower);
            compound.putBoolean("ExplodeBlocks", explodeBlocks);
        }
        compound.putBoolean("PseudoInstantTravel", pseudoInstantTravel);
        compound.putFloat("ProjectileBaseDamage", projectileBaseDamage);
        compound.putFloat("SecondaryChange", secondaryChange);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("Potion", 8)) {
            this.potion = PotionUtils.getPotion(compound);
        }

        for (MobEffectInstance effectinstance : PotionUtils.getCustomEffects(compound)) {
            this.addEffect(effectinstance);
        }

        if (compound.contains("Color", 99)) {
            this.setFixedColor(compound.getInt("Color"));
        } else {
            this.refreshColor();
        }

        if (compound.contains("MaxFlightTime")) {
            maxFlightTime = compound.getInt("MaxFlightTime");
            flightTime = compound.getInt("FlightTime");
        }

        if (compound.contains("ExplosivePower")) {
            explosivePower = compound.getShort("ExplosivePower");
            explodeBlocks = compound.getBoolean("ExplodeBlocks");
        }
        pseudoInstantTravel = compound.getBoolean("PseudoInstantTravel");
        projectileBaseDamage = compound.getFloat("ProjectileBaseDamage");
        secondaryChange = compound.getFloat("SecondaryChange");
    }

    @Override
    protected void doPostHurtEffects(LivingEntity living) {
        super.doPostHurtEffects(living);

        for (MobEffectInstance effectinstance : this.potion.getEffects()) {
            living.addEffect(new MobEffectInstance(effectinstance.getEffect(), Math.max(effectinstance.getDuration() / 8, 1), effectinstance.getAmplifier(), effectinstance.isAmbient(), effectinstance.isVisible()));
        }

        if (!this.customPotionEffects.isEmpty()) {
            for (MobEffectInstance effectinstance1 : this.customPotionEffects) {
                living.addEffect(effectinstance1);
            }
        }
    }

    @Override
    protected ItemStack getPickupItem() {
        if (this.customPotionEffects.isEmpty() && this.potion == Potions.EMPTY) {
            return new ItemStack(Items.ARROW);
        } else {
            ItemStack itemstack = new ItemStack(Items.TIPPED_ARROW);
            PotionUtils.setPotion(itemstack, this.potion);
            PotionUtils.setCustomEffects(itemstack, this.customPotionEffects);
            if (this.fixedColor) {
                itemstack.getOrCreateTag().putInt("CustomPotionColor", this.getColor());
            }

            return itemstack;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void handleEntityEvent(byte id) {
        if (id == 0) {
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
            super.handleEntityEvent(id);
        }
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return BCoreNetwork.getEntitySpawnPacket(this);
    }
}
