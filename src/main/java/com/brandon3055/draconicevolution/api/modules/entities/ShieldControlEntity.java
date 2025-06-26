package com.brandon3055.draconicevolution.api.modules.entities;

import com.brandon3055.brandonscore.api.BCStreamCodec;
import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.brandonscore.api.power.IOPStorage;
import com.brandon3055.draconicevolution.api.config.BooleanProperty;
import com.brandon3055.draconicevolution.api.config.ConfigProperty;
import com.brandon3055.draconicevolution.api.config.ConfigProperty.BooleanFormatter;
import com.brandon3055.draconicevolution.api.modules.Module;
import com.brandon3055.draconicevolution.api.modules.ModuleHelper;
import com.brandon3055.draconicevolution.api.modules.ModuleTypes;
import com.brandon3055.draconicevolution.api.modules.data.ShieldControlData;
import com.brandon3055.draconicevolution.api.modules.data.ShieldData;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleContext;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleEntity;
import com.brandon3055.draconicevolution.api.modules.lib.StackModuleContext;
import com.brandon3055.draconicevolution.handlers.DESounds;
import com.brandon3055.draconicevolution.init.DEModules;
import com.brandon3055.draconicevolution.init.EquipCfg;
import com.brandon3055.draconicevolution.init.ItemData;
import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.covers1624.quack.collection.FastStream;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.util.thread.EffectiveSide;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by brandon3055 on 7/7/20
 */
public class ShieldControlEntity extends ModuleEntity<ShieldControlData> {
    public static final Set<ResourceKey<DamageType>> UNBLOCKABLE = Sets.newHashSet(DamageTypes.DROWN, DamageTypes.STARVE, DamageTypes.IN_WALL, DamageTypes.GENERIC_KILL);
    public static final HashMap<ResourceKey<DamageType>, Double> ENV_SOURCES = new HashMap<>();

    static {
        ENV_SOURCES.put(DamageTypes.IN_FIRE, 1D);
        ENV_SOURCES.put(DamageTypes.ON_FIRE, 0.5D);
        ENV_SOURCES.put(DamageTypes.LAVA, 4D);
        ENV_SOURCES.put(DamageTypes.HOT_FLOOR, 1D);
        ENV_SOURCES.put(DamageTypes.IN_WALL, 1D);
        ENV_SOURCES.put(DamageTypes.CRAMMING, 0D);
        ENV_SOURCES.put(DamageTypes.CACTUS, 1D);
    }

    //Persistent Fields
    private ShieldSaveData data = new ShieldSaveData(0D, 0D, 0D, 0, 0, 0, (byte) 0, false);

    //Client Sync Fields
    private float shieldAnim;
    private float shieldHitIndicator;
    private final int shieldColour;

    private BooleanProperty shieldEnabled = new BooleanProperty("shield_mod.enabled", true).setFormatter(BooleanFormatter.ENABLED_DISABLED);
    private BooleanProperty alwaysVisible = new BooleanProperty("shield_mod.always_visible", true).setFormatter(BooleanFormatter.YES_NO);

    //No Sync
    private int tick;
    private boolean conflict = false;
    private ShieldData shieldCache;
    private long lastHitTime;
    private double passivePowerCache = 0;

    public static final Codec<ShieldControlEntity> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            DEModules.codec().fieldOf("module").forGetter(ShieldControlEntity::getModule),
            Codec.INT.fieldOf("gridx").forGetter(ModuleEntity::getGridX),
            Codec.INT.fieldOf("gridy").forGetter(ModuleEntity::getGridY),
            ShieldSaveData.CODEC.fieldOf("data").forGetter(e -> e.data),
            Codec.FLOAT.fieldOf("shield_anim").forGetter(e -> e.shieldAnim),
            Codec.FLOAT.fieldOf("shield_hit_indicator").forGetter(e -> e.shieldHitIndicator),
//            Codec.INT.fieldOf("shield_colour").forGetter(e -> e.shieldColour),
            BooleanProperty.CODEC.fieldOf("shield_enabled").forGetter(e -> e.shieldEnabled),
            BooleanProperty.CODEC.fieldOf("always_visible").forGetter(e -> e.alwaysVisible)
    ).apply(builder, ShieldControlEntity::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ShieldControlEntity> STREAM_CODEC = BCStreamCodec.composite(
            DEModules.streamCodec(), ModuleEntity::getModule,
            ByteBufCodecs.INT, ModuleEntity::getGridX,
            ByteBufCodecs.INT, ModuleEntity::getGridY,
            ShieldSaveData.STREAM_CODEC, e -> e.data,
            ByteBufCodecs.FLOAT, e -> e.shieldAnim,
            ByteBufCodecs.FLOAT, e -> e.shieldHitIndicator,
//            ByteBufCodecs.INT, e -> e.shieldColour,
            BooleanProperty.STREAM_CODEC, e -> e.shieldEnabled,
            BooleanProperty.STREAM_CODEC, e -> e.alwaysVisible,
            ShieldControlEntity::new
    );

    public ShieldControlEntity(Module<ShieldControlData> module) {
        super(module);
        this.shieldColour = getDefaultShieldColour(module.getModuleTechLevel());
//        addProperty(shieldEnabled = new BooleanProperty("shield_mod.enabled", true).setFormatter(BooleanFormatter.ENABLED_DISABLED));
//        addProperty(alwaysVisible = new BooleanProperty("shield_mod.always_visible", true).setFormatter(BooleanFormatter.YES_NO));
//        this.savePropertiesToItem = true;
    }

    ShieldControlEntity(Module<?> module, int gridX, int gridY, ShieldSaveData data, float shieldAnim, float shieldHitIndicator/*, int shieldColour*/, BooleanProperty shieldEnabled, BooleanProperty alwaysVisible) {
        super((Module<ShieldControlData>) module, gridX, gridY);
        this.data = data;
        this.shieldAnim = shieldAnim;
        this.shieldHitIndicator = shieldHitIndicator;
//        this.shieldColour = shieldColour;
        this.shieldColour = getDefaultShieldColour(module.getModuleTechLevel());
        this.shieldEnabled = shieldEnabled;
        this.alwaysVisible = alwaysVisible;
    }

    @Override
    public ModuleEntity<?> copy() {
        return new ShieldControlEntity(module, getGridX(), getGridY(), new ShieldSaveData(data), shieldAnim, shieldHitIndicator, shieldEnabled.copy(), alwaysVisible.copy());
    }

    @Override
    public void getEntityProperties(List<ConfigProperty> properties) {
        properties.add(shieldEnabled);
        properties.add(alwaysVisible);
    }

    //region Shield Logic Methods

    @Override
    public void tick(ModuleContext moduleContext) {
        IOPStorage storage = moduleContext.getOpStorage();
        if (!(moduleContext instanceof StackModuleContext context && EffectiveSide.get().isServer() && storage != null)) {
            return;
        }

        if (tick++ % 10 == 0) clearCaches();
        markDirty();


        ShieldData shieldData = getShieldData(context.getEntity());
        data.shieldCapacity = shieldData.shieldCapacity();
        double chargeRate = shieldData.shieldRecharge();
        boolean enabled = shieldEnabled.getValue() && getShieldPoints() > 0;

        if (data.shieldPoints > data.shieldCapacity) {
            data.shieldPoints = data.shieldCapacity;
        }

        //# Rendering #
        //Hit Indicator (Controls the effect where the shield flashes brighter when hit)
        if (shieldHitIndicator > 0) {
            shieldHitIndicator -= 0.1F;
        }

        data.shieldVisible = enabled && (alwaysVisible.getValue() || System.currentTimeMillis() - lastHitTime < 5000);
        if (data.shieldVisible && shieldAnim < 1) {
            shieldAnim = Math.min(shieldAnim + 0.05F, 1F);
        } else if (!data.shieldVisible && shieldAnim > 0) {
            shieldAnim = Math.max(shieldAnim - 0.05F, 0F);
        }

        if (data.envDmgCoolDown > 0) data.envDmgCoolDown--;

        //# Logic #
        if (!context.isEquipped()) {
            return;
        }

        if (conflict) {
            data.shieldCapacity = 0;
            data.shieldPoints = 0;
            return;
        }

        //Passive Draw
        if (enabled && storage.getOPStored() > 0) {
            double passiveDraw = data.shieldPoints * data.shieldPoints * EquipCfg.shieldPassiveModifier;
            if (passiveDraw > 0) {
                passivePowerCache += passiveDraw;
                if (passivePowerCache >= 1) {
                    storage.modifyEnergyStored(-(int) passivePowerCache);
                    passivePowerCache = passivePowerCache % 1;
                }
            }
        } else if (enabled && data.shieldPoints > 0) {
            //Shield drains if you run out of power. It takes 60 seconds to drain from full to zero.
            data.shieldPoints = Math.max(0, data.shieldPoints - (data.shieldCapacity / (60D * 20D)));
        }
        if (data.shieldBoost > 0) {
            data.boostTime--;
            if (data.boostTime == 0) {
                data.shieldBoost = 0;
            }
        }

        //Recharge Logic
        if (!enabled) chargeRate *= 1.25;
        if (data.shieldCoolDown > 0) {
            data.shieldCoolDown = Math.max(0, data.shieldCoolDown - (enabled ? 100 : 125));
        } else if (data.shieldPoints < data.shieldCapacity && data.shieldCapacity > 0 && chargeRate > 0 && storage.getOPStored() > 0) {
            double energyPerPoint = Math.max(chargeRate * EquipCfg.energyShieldChg, EquipCfg.energyShieldChg);
            long extracted = storage.modifyEnergyStored(-(int) Math.max(1, Math.min(chargeRate, data.shieldCapacity - data.shieldPoints) * energyPerPoint));
            data.shieldPoints += extracted / energyPerPoint;
        }
    }

    public double getShieldPoints() {
        return data.shieldPoints + data.shieldBoost;
    }

    public int getShieldCapacity() {
        return data.shieldCapacity;
    }

    public double getMaxShieldBoost() {
        return data.shieldBoost == 0 ? 0 : data.maxBoost;
    }

    public double getShieldBoost() {
        return data.shieldBoost;
    }

    public int getShieldCoolDown() {
        return data.shieldCoolDown;
    }

    public int getMaxShieldCoolDown() {
        return module.getData().coolDownTicks() * 100;
    }

    public void setShieldCoolDown(int shieldCoolDown) {
        data.shieldCoolDown = shieldCoolDown;
    }

    /**
     * Will check if this shield is able to completely absorb this damage event.
     * If so the event will be canceled and shield points will be consumed.
     * Note: partial damage blocking is handled by {@link #tryBlockDamage(LivingIncomingDamageEvent)}
     *
     * @param event The damage event.
     */
    public void tryBlockDamage(LivingIncomingDamageEvent event) {
        DamageSource source = event.getSource();
        if (!shieldEnabled.getValue() || source.is(DamageTypeTags.BYPASSES_INVULNERABILITY) || FastStream.of(UNBLOCKABLE).anyMatch(source::is)) return;

        if (blockEnvironmentalDamage(event, source)) {
            return;
        }

        float damage = applyDamageModifiers(source, event.getAmount());
        if (damage <= getShieldPoints()) {
            LivingEntity entity = event.getEntity();
            event.setCanceled(true);
            subtractShieldPoints(damage);
            onShieldHit(entity, true);
        }
    }

    private boolean blockEnvironmentalDamage(LivingIncomingDamageEvent event, DamageSource source) {
        LivingEntity entity = event.getEntity();
        if (source.is(DamageTypeTags.IS_FIRE) && getShieldPoints() > 10) {
            entity.clearFire();
        }

        double value = FastStream.of(ENV_SOURCES.entrySet()).filter(e -> source.is(e.getKey())).map(Map.Entry::getValue).firstOrDefault(0D);
        if (value != 0) {
            value /= 20;
            if (value <= getShieldPoints()) {
                subtractShieldPoints(value);
                event.setCanceled(true);
                lastHitTime = System.currentTimeMillis();
                shieldHitIndicator = shieldAnim = 1;
                data.shieldCoolDown = getMaxShieldCoolDown();
                if (data.envDmgCoolDown == 0) {
                    float hitPitch = 0.7F + (float) (Math.min(1, getShieldPoints() / ((data.shieldCapacity + getMaxShieldBoost()) * 0.1)) * 0.3);
                    entity.level().playSound(null, entity.blockPosition(), DESounds.SHIELD_STRIKE.get(), SoundSource.PLAYERS, 0.25F, (0.95F + (entity.level().random.nextFloat() * 0.1F)) * hitPitch);
                    data.envDmgCoolDown = 40;
                }
                markDirty();
                return true;
            }
        }
        return false;
    }

    /**
     * Called when the shield in unable to completely block a damage source.
     * This will calculate how much damage the shield can absorb and subtract that
     * amount from the damage event.
     * <p>
     * *Except in cases where something skips LivingAttackEvent and goes strait to LivingDamageEvent... We need to account for that to...
     *
     * @param event The damage event.
     */
    public void tryBlockDamage(LivingDamageEvent.Pre event) {
        DamageSource source = event.getSource();
        if (!shieldEnabled.getValue() || UNBLOCKABLE.contains(source)) return;
        float damage = applyDamageModifiers(source, event.getNewDamage());

        LivingEntity entity = event.getEntity();
        if (damage <= getShieldPoints()) {
            event.setNewDamage(0);
            subtractShieldPoints(damage);
            onShieldHit(entity, true);
        } else if (getShieldPoints() > 0) {
            damage -= getShieldPoints();
            event.setNewDamage(damage);
            onShieldHit(entity, false);
            data.shieldPoints = 0;
            data.shieldBoost = 0;
        }
        markDirty();
    }

    private void onShieldHit(LivingEntity entity, boolean damageBlocked) {
        lastHitTime = System.currentTimeMillis();
        shieldHitIndicator = shieldAnim = 1;
        if (damageBlocked && (data.shieldCapacity + getMaxShieldBoost()) > 0) {
            data.shieldCoolDown = getMaxShieldCoolDown();
            float hitPitch = 0.7F + (float) (Math.min(1, getShieldPoints() / ((data.shieldCapacity + getMaxShieldBoost()) * 0.1)) * 0.3);
            entity.level().playSound(null, entity.blockPosition(), DESounds.SHIELD_STRIKE.get(), SoundSource.PLAYERS, 1F, (0.95F + (entity.level().random.nextFloat() * 0.1F)) * hitPitch);
        }
        markDirty();
    }

    private ShieldData getShieldData(LivingEntity entity) {
        if (shieldCache == null) {
            conflict = false;
            if (entity == null) {
                shieldCache = host.getModuleData(ModuleTypes.SHIELD_BOOST, new ShieldData(0, 0));
            } else {
                shieldCache = ModuleHelper.getCombinedEquippedData(entity, ModuleTypes.SHIELD_BOOST, new ShieldData(0, 0));
                conflict = ModuleHelper.getEquippedModules(entity, ModuleTypes.SHIELD_CONTROLLER).size() > 1;
            }
            markDirty();
        }
        return shieldCache;
    }

    private float applyDamageModifiers(DamageSource source, float damage) {
        if (source.is(DamageTypeTags.BYPASSES_ARMOR)) damage *= 3;
        if (source.is(DamageTypes.MAGIC)) damage *= 2;
        return damage;
    }

    public void boost(float shieldBoost, int boostTime) {
        data.shieldBoost += shieldBoost;
        data.boostTime = Math.max(data.boostTime, boostTime);
        data.maxBoost = data.shieldBoost;
        markDirty();
    }

    public void subtractShieldPoints(double points) {
        if (points > 0) {
            if (data.shieldBoost > 0) {
                double number = Math.min(data.shieldBoost, points);
                data.shieldBoost -= number;
                points -= number;
            }
            data.shieldPoints = Math.max(0, data.shieldPoints - points);
            markDirty();
        }
    }

    //endregion

    //region Render Methods

    public int getShieldColour() {
        return shieldEnabled.getValue() || shieldAnim > 0 ? shieldColour | ((int) ((63 + (192 * shieldHitIndicator)) * Math.min(1, getShieldPoints() / (data.shieldCapacity * 0.1))) << 24) : 0xFFFFFF;
    }

    public boolean isShieldEnabled() {
        return shieldEnabled.getValue();
    }

    public float getShieldState() {
        return shieldAnim;
    }

    private static int getDefaultShieldColour(TechLevel techLevel) {
        return switch (techLevel) {
            case DRACONIUM -> 0x0080cc;
            case WYVERN -> 0x8C00A5;
            case DRACONIC -> 0xff9000;
            case CHAOTIC -> 0xBF0C0C;
        };
    }

    //endregion

    //region Standard Entity Methods

    @Override
    public void clearCaches() {
        shieldCache = null;
    }

    @Override
    public void saveEntityToStack(ItemStack stack, ModuleContext context) {
        stack.set(ItemData.SHIELD_MODULE_CAP, data.shieldCapacity);
        stack.set(ItemData.SHIELD_MODULE_POINTS, data.shieldPoints);
        stack.set(ItemData.SHIELD_MODULE_COOLDWN, data.shieldCoolDown);
        stack.set(ItemData.BOOL_ITEM_PROP_1, shieldEnabled).copy();
        stack.set(ItemData.BOOL_ITEM_PROP_2, alwaysVisible).copy();
    }

    @Override
    public void loadEntityFromStack(ItemStack stack, ModuleContext context) {
        data.shieldCapacity = stack.getOrDefault(ItemData.SHIELD_MODULE_CAP, 0);
        data.shieldPoints = stack.getOrDefault(ItemData.SHIELD_MODULE_POINTS, 0D);
        data.shieldCoolDown = stack.getOrDefault(ItemData.SHIELD_MODULE_COOLDWN, 0);
        shieldEnabled = stack.getOrDefault(ItemData.BOOL_ITEM_PROP_1, shieldEnabled.copy());
        alwaysVisible = stack.getOrDefault(ItemData.BOOL_ITEM_PROP_2, alwaysVisible.copy());
    }

    //endregion

    private static class ShieldSaveData {
        public double shieldPoints;
        public double shieldBoost;
        public double maxBoost;
        public int boostTime;
        public int shieldCapacity;
        public int shieldCoolDown;
        public byte envDmgCoolDown;
        public boolean shieldVisible;

        public static final Codec<ShieldSaveData> CODEC = RecordCodecBuilder.create(builder -> builder.group(
                Codec.DOUBLE.fieldOf("shield_points").forGetter(e -> e.shieldPoints),
                Codec.DOUBLE.fieldOf("shield_boost").forGetter(e -> e.shieldBoost),
                Codec.DOUBLE.fieldOf("max_boost").forGetter(e -> e.maxBoost),
                Codec.INT.fieldOf("boost_time").forGetter(e -> e.boostTime),
                Codec.INT.fieldOf("shield_capacity").forGetter(e -> e.shieldCapacity),
                Codec.INT.fieldOf("shield_cool_down").forGetter(e -> e.shieldCoolDown),
                Codec.BYTE.fieldOf("env_dmg_cool_down").forGetter(e -> e.envDmgCoolDown),
                Codec.BOOL.fieldOf("shield_visible").forGetter(e -> e.shieldVisible)
        ).apply(builder, ShieldSaveData::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, ShieldSaveData> STREAM_CODEC = BCStreamCodec.composite(
                ByteBufCodecs.DOUBLE, e -> e.shieldPoints,
                ByteBufCodecs.DOUBLE, e -> e.shieldBoost,
                ByteBufCodecs.DOUBLE, e -> e.maxBoost,
                ByteBufCodecs.INT, e -> e.boostTime,
                ByteBufCodecs.INT, e -> e.shieldCapacity,
                ByteBufCodecs.INT, e -> e.shieldCoolDown,
                ByteBufCodecs.BYTE, e -> e.envDmgCoolDown,
                ByteBufCodecs.BOOL, e -> e.shieldVisible,
                ShieldSaveData::new
        );

        public ShieldSaveData(double shieldPoints, double shieldBoost, double maxBoost, int boostTime, int shieldCapacity, int shieldCoolDown, byte envDmgCoolDown, boolean shieldVisible) {
            this.shieldPoints = shieldPoints;
            this.shieldBoost = shieldBoost;
            this.maxBoost = maxBoost;
            this.boostTime = boostTime;
            this.shieldCapacity = shieldCapacity;
            this.shieldCoolDown = shieldCoolDown;
            this.envDmgCoolDown = envDmgCoolDown;
            this.shieldVisible = shieldVisible;
        }

        public ShieldSaveData(ShieldSaveData copyFrom) {
            this.shieldPoints = copyFrom.shieldPoints;
            this.shieldBoost = copyFrom.shieldBoost;
            this.maxBoost = copyFrom.maxBoost;
            this.boostTime = copyFrom.boostTime;
            this.shieldCapacity = copyFrom.shieldCapacity;
            this.shieldCoolDown = copyFrom.shieldCoolDown;
            this.envDmgCoolDown = copyFrom.envDmgCoolDown;
            this.shieldVisible = copyFrom.shieldVisible;
        }
    }
}
