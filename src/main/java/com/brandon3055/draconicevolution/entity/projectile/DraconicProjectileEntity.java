package com.brandon3055.draconicevolution.entity.projectile;

import com.brandon3055.brandonscore.utils.DataUtils;
import com.brandon3055.draconicevolution.api.modules.Module;
import com.brandon3055.draconicevolution.api.modules.data.DamageModData;
import com.brandon3055.draconicevolution.init.DEModules;
import com.google.common.collect.Sets;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.datasync.IDataSerializer;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.util.DamageSource;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;

/**
 * This is effectively a complete re implementation of the vanilla arrow supporting all of the vanilla features.
 * But on top of that it has support for all my custom damage and effects.
 */
public class DraconicProjectileEntity extends AbstractArrowEntity {
    private static final IDataSerializer<Optional<Module<?>>> OPTIONAL_SERIALIZER = new IDataSerializer<Optional<Module<?>>>() {
        public void write(PacketBuffer buf, Optional<Module<?>> value) {
            buf.writeBoolean(value.isPresent());
            value.ifPresent(module -> buf.writeResourceLocation(module.getRegistryName()));
        }

        public Optional<Module<?>> read(PacketBuffer buf) {
            Module<?> module = DEModules.MODULE_REGISTRY.getValue(buf.readResourceLocation());
            return !buf.readBoolean() || module == null ? Optional.empty() : Optional.of(module);
        }

        public Optional<Module<?>> copy(Optional<Module<?>> value) {
            return value;
        }
    };
    private static final DataParameter<Integer> COLOR = EntityDataManager.defineId(DraconicProjectileEntity.class, DataSerializers.INT);
    private static final DataParameter<Boolean> ENERGY_PROJECTILE = EntityDataManager.defineId(DraconicProjectileEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Optional<Module<?>>> DAMAGE_MODIFIER = EntityDataManager.defineId(DraconicProjectileEntity.class, OPTIONAL_SERIALIZER);
    private Potion potion = Potions.EMPTY;
    private final Set<EffectInstance> customPotionEffects = Sets.newHashSet();
    private boolean fixedColor;

    public DraconicProjectileEntity(EntityType<? extends DraconicProjectileEntity> type, World worldIn) {
        super(type, worldIn);
    }

    public DraconicProjectileEntity(World worldIn, double x, double y, double z) {
        super(EntityType.ARROW, x, y, z, worldIn);
    }

    public DraconicProjectileEntity(World worldIn, LivingEntity shooter) {
        super(EntityType.ARROW, shooter, worldIn);

    }

    public void setPotionEffect(ItemStack stack) {
        if (stack.getItem() == Items.TIPPED_ARROW) {
            this.potion = PotionUtils.getPotion(stack);
            Collection<EffectInstance> collection = PotionUtils.getCustomEffects(stack);
            if (!collection.isEmpty()) {
                for (EffectInstance effectinstance : collection) {
                    this.customPotionEffects.add(new EffectInstance(effectinstance));
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

    @Override
    public boolean hurt(DamageSource source, float amount) {
        return super.hurt(source, amount);
    }

    public static int getCustomColor(ItemStack colour) {
        CompoundNBT compoundnbt = colour.getTag();
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

    public void addEffect(EffectInstance effect) {
        this.customPotionEffects.add(effect);
        this.getEntityData().set(COLOR, PotionUtils.getColor(PotionUtils.getAllEffects(this.potion, this.customPotionEffects)));
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(COLOR, -1);
        this.entityData.define(ENERGY_PROJECTILE, false);
        this.entityData.define(DAMAGE_MODIFIER, Optional.empty());
    }

    public void tick() {
        setNoGravity(true);
        tickCount = 0;
        super.tick();
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

    public void addAdditionalSaveData(CompoundNBT compound) {
        super.addAdditionalSaveData(compound);
        if (this.potion != Potions.EMPTY && this.potion != null) {
            compound.putString("Potion", Registry.POTION.getKey(this.potion).toString());
        }

        if (this.fixedColor) {
            compound.putInt("Color", this.getColor());
        }

        if (!this.customPotionEffects.isEmpty()) {
            ListNBT listnbt = new ListNBT();

            for (EffectInstance effectinstance : this.customPotionEffects) {
                listnbt.add(effectinstance.save(new CompoundNBT()));
            }

            compound.put("CustomPotionEffects", listnbt);
        }

    }

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
            this.refreshColor();
        }

    }

    protected void doPostHurtEffects(LivingEntity living) {
        super.doPostHurtEffects(living);

        for (EffectInstance effectinstance : this.potion.getEffects()) {
            living.addEffect(new EffectInstance(effectinstance.getEffect(), Math.max(effectinstance.getDuration() / 8, 1), effectinstance.getAmplifier(), effectinstance.isAmbient(), effectinstance.isVisible()));
        }

        if (!this.customPotionEffects.isEmpty()) {
            for (EffectInstance effectinstance1 : this.customPotionEffects) {
                living.addEffect(effectinstance1);
            }
        }
    }

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
    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
