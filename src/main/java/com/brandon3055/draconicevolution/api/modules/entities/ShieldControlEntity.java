package com.brandon3055.draconicevolution.api.modules.entities;

import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.brandonscore.api.power.IOPStorage;
import com.brandon3055.draconicevolution.api.config.BooleanProperty;
import com.brandon3055.draconicevolution.api.config.ConfigProperty.BooleanFormatter;
import com.brandon3055.draconicevolution.api.modules.Module;
import com.brandon3055.draconicevolution.api.modules.ModuleTypes;
import com.brandon3055.draconicevolution.api.modules.data.ShieldControlData;
import com.brandon3055.draconicevolution.api.modules.data.ShieldData;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleContext;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleEntity;
import com.brandon3055.draconicevolution.api.modules.lib.StackModuleContext;
import com.brandon3055.draconicevolution.handlers.DESounds;
import com.brandon3055.draconicevolution.init.EquipCfg;
import com.brandon3055.draconicevolution.utils.LogHelper;
import com.google.common.collect.Sets;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.fml.util.thread.EffectiveSide;

import java.util.HashMap;
import java.util.Set;

/**
 * Created by brandon3055 on 7/7/20
 */
public class ShieldControlEntity extends ModuleEntity<ShieldControlData> {
    private static final Set<DamageSource> UNBLOCKABLE = Sets.newHashSet(DamageSource.DROWN, DamageSource.STARVE, DamageSource.IN_WALL);
    private static final HashMap<DamageSource, Double> ENV_SOURCES = new HashMap<>();

    static {
        ENV_SOURCES.put(DamageSource.IN_FIRE, 1D);
        ENV_SOURCES.put(DamageSource.ON_FIRE, 0.5D);
        ENV_SOURCES.put(DamageSource.LAVA, 4D);
        ENV_SOURCES.put(DamageSource.HOT_FLOOR, 1D);
        ENV_SOURCES.put(DamageSource.IN_WALL, 1D);
        ENV_SOURCES.put(DamageSource.CRAMMING, 0D);
        ENV_SOURCES.put(DamageSource.CACTUS, 1D);
    }

    private BooleanProperty shieldEnabled;
    private BooleanProperty alwaysVisible;

    private ShieldData shieldCache;
    private long lastHitTime;
    private double passivePowerCache = 0;

    //Persistent Fields
    private double shieldPoints;
    private double shieldBoost; //This is a "boost modifier" separate from the main shield point pool. Its used by things like the last stand module.
    private double maxBoost;
    private int boostTime = 0;
    private int shieldCapacity;
    private int shieldCoolDown;
    private byte envDmgCoolDown = 0;
    private boolean shieldVisible;

    //Client Sync Fields
    private float shieldAnim;
    private float shieldHitIndicator;
    private int shieldColour;

    public ShieldControlEntity(Module<ShieldControlData> module) {
        super(module);
        this.shieldColour = getDefaultShieldColour(module.getModuleTechLevel());
        addProperty(shieldEnabled = new BooleanProperty("shield_mod.enabled", true).setFormatter(BooleanFormatter.ENABLED_DISABLED));
        addProperty(alwaysVisible = new BooleanProperty("shield_mod.always_visible", true).setFormatter(BooleanFormatter.YES_NO));
        this.savePropertiesToItem = true;
    }

    //region Shield Logic Methods

    @Override
    public void tick(ModuleContext moduleContext) {
        IOPStorage storage = moduleContext.getOpStorage();
        if (!(moduleContext instanceof StackModuleContext context && EffectiveSide.get().isServer() && storage != null)) {
            return;
        }

        ShieldData data = getShieldData();
        shieldCapacity = data.shieldCapacity();
        double chargeRate = data.shieldRecharge();
        boolean enabled = shieldEnabled.getValue() && getShieldPoints() > 0;

        if (shieldPoints > shieldCapacity) {
            shieldPoints = shieldCapacity;
        }

        //# Rendering #
        //Hit Indicator (Controls the effect where the shield flashes brighter when hit)
        if (shieldHitIndicator > 0) {
            shieldHitIndicator -= 0.1;
        }

        shieldVisible = enabled && (alwaysVisible.getValue() || System.currentTimeMillis() - lastHitTime < 5000);
        if (shieldVisible && shieldAnim < 1) {
            shieldAnim = Math.min(shieldAnim + 0.05F, 1F);
        } else if (!shieldVisible && shieldAnim > 0) {
            shieldAnim = Math.max(shieldAnim - 0.05F, 0F);
        }

        if (envDmgCoolDown > 0) envDmgCoolDown--;

        //# Logic #
        if (!context.isEquipped()) {
            return;
        }

        //Passive Draw
        if (enabled && storage.getOPStored() > 0) {
            double passiveDraw = shieldPoints * shieldPoints * EquipCfg.shieldPassiveModifier;
            if (passiveDraw > 0) {
                passivePowerCache += passiveDraw;
                if (passivePowerCache >= 1) {
                    storage.modifyEnergyStored(-(int) passivePowerCache);
                    passivePowerCache = passivePowerCache % 1;
                }
            }
        } else if (enabled && shieldPoints > 0) {
            //Shield drains if you run out of power. It takes 60 seconds to drain from full to zero.
            shieldPoints = Math.max(0, shieldPoints - (shieldCapacity / (60D * 20D)));
        }
        if (shieldBoost > 0) {
            boostTime--;
            if (boostTime == 0) {
                shieldBoost = 0;
            }
        }

        //Recharge Logic
        if (!enabled) chargeRate *= 1.25;
        if (shieldCoolDown > 0) {
            shieldCoolDown = Math.max(0, shieldCoolDown - (enabled ? 100 : 125));
        } else if (shieldPoints < shieldCapacity && shieldCapacity > 0 && chargeRate > 0 && storage.getOPStored() > 0) {
            double energyPerPoint = Math.max(chargeRate * EquipCfg.energyShieldChg, EquipCfg.energyShieldChg);
            long extracted = storage.modifyEnergyStored(-(int) Math.max(1, Math.min(chargeRate, shieldCapacity - shieldPoints) * energyPerPoint));
            shieldPoints += extracted / energyPerPoint;
        }
    }

    public double getShieldPoints() {
        return shieldPoints + shieldBoost;
    }

    public int getShieldCapacity() {
        return shieldCapacity;
    }

    public double getMaxShieldBoost() {
        return shieldBoost == 0 ? 0 : maxBoost;
    }

    public double getShieldBoost() {
        return shieldBoost;
    }

    public int getShieldCoolDown() {
        return shieldCoolDown;
    }

    public int getMaxShieldCoolDown() {
        return module.getData().coolDownTicks() * 100;
    }

    public void setShieldCoolDown(int shieldCoolDown) {
        this.shieldCoolDown = shieldCoolDown;
    }

    /**
     * Will check if this shield is able to completely absorb this damage event.
     * If so the event will be canceled and shield points will be consumed.
     * Note: partial damage blocking is handled by {@link #tryBlockDamage(LivingDamageEvent)}
     *
     * @param event The damage event.
     */
    public void tryBlockDamage(LivingAttackEvent event) {
        DamageSource source = event.getSource();
        if (!shieldEnabled.getValue() || UNBLOCKABLE.contains(source)) return;

        if (blockEnvironmentalDamage(event, source)) {
            return;
        }

        float damage = applyDamageModifiers(source, event.getAmount());
        if (damage <= getShieldPoints()) {
            LivingEntity entity = event.getEntityLiving();
            event.setCanceled(true);
            subtractShieldPoints(damage);
            onShieldHit(entity, true);
        }
    }

    private boolean blockEnvironmentalDamage(LivingAttackEvent event, DamageSource source) {
        LivingEntity entity = event.getEntityLiving();
        if (source.isFire() && getShieldPoints() > 10) {
            entity.clearFire();
        }
        if (ENV_SOURCES.containsKey(source)) {
            ENV_SOURCES.put(DamageSource.LAVA, 4D);
            double value = ENV_SOURCES.get(source) / 20;
            if (value <= getShieldPoints()) {
                subtractShieldPoints(value);
                event.setCanceled(true);
                lastHitTime = System.currentTimeMillis();
                shieldHitIndicator = shieldAnim = 1;
                shieldCoolDown = getMaxShieldCoolDown();
                if (envDmgCoolDown == 0) {
                    float hitPitch = 0.7F + (float) (Math.min(1, getShieldPoints() / ((shieldCapacity + getMaxShieldBoost()) * 0.1)) * 0.3);
                    entity.level.playSound(null, entity.blockPosition(), DESounds.shieldStrike, SoundSource.PLAYERS, 0.25F, (0.95F + (entity.level.random.nextFloat() * 0.1F)) * hitPitch);
                    envDmgCoolDown = 40;
                }
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
    public void tryBlockDamage(LivingDamageEvent event) {
        DamageSource source = event.getSource();
        if (!shieldEnabled.getValue() || UNBLOCKABLE.contains(source)) return;
        float damage = applyDamageModifiers(source, event.getAmount());

        LivingEntity entity = event.getEntityLiving();
        if (damage <= getShieldPoints()) {
            event.setCanceled(true);
            subtractShieldPoints(damage);
            onShieldHit(entity, true);
        } else if (getShieldPoints() > 0) {
            damage -= getShieldPoints();
            event.setAmount(damage);
            onShieldHit(entity, false);
            shieldPoints = 0;
            shieldBoost = 0;
        }
    }

    private void onShieldHit(LivingEntity entity, boolean damageBlocked) {
        lastHitTime = System.currentTimeMillis();
        shieldHitIndicator = shieldAnim = 1;
        if (damageBlocked && (shieldCapacity + getMaxShieldBoost()) > 0) {
            shieldCoolDown = getMaxShieldCoolDown();
            float hitPitch = 0.7F + (float) (Math.min(1, getShieldPoints() / ((shieldCapacity + getMaxShieldBoost()) * 0.1)) * 0.3);
            entity.level.playSound(null, entity.blockPosition(), DESounds.shieldStrike, SoundSource.PLAYERS, 1F, (0.95F + (entity.level.random.nextFloat() * 0.1F)) * hitPitch);
        }
    }

    private ShieldData getShieldData() {
        if (shieldCache == null) {
            shieldCache = host.getModuleData(ModuleTypes.SHIELD_BOOST, new ShieldData(0, 0));
        }
        return shieldCache;
    }

    private float applyDamageModifiers(DamageSource source, float damage) {
        if (source.isBypassArmor()) damage *= 3;
        if (source.isMagic()) damage *= 2;
        return damage;
    }

    public void boost(float shieldBoost, int boostTime) {
        this.shieldBoost += shieldBoost;
        this.boostTime = Math.max(this.boostTime, boostTime);
        this.maxBoost = this.shieldBoost;
    }

    public void subtractShieldPoints(double points) {
        if (points > 0) {
            if (shieldBoost > 0) {
                double number = Math.min(shieldBoost, points);
                shieldBoost -= number;
                points -= number;
            }
            shieldPoints = Math.max(0, shieldPoints - points);
        }
    }

    //endregion

    //region Render Methods

    public int getShieldColour() {
        return shieldEnabled.getValue() || shieldAnim > 0 ? shieldColour | ((int) ((63 + (192 * shieldHitIndicator)) * Math.min(1, getShieldPoints() / (shieldCapacity * 0.1))) << 24) : 0xFFFFFF;
    }

    public boolean isShieldEnabled() {
        return shieldEnabled.getValue();
    }

    public float getShieldState() {
        return shieldAnim;
    }

    private static int getDefaultShieldColour(TechLevel techLevel) {
        switch (techLevel) {
            case DRACONIUM:
                return 0x0080cc;
            case WYVERN:
                return 0x8C00A5;
            case DRACONIC:
                return 0xff9000;
            case CHAOTIC:
                return 0xBF0C0C;
        }
        return 0;
    }

    //endregion

    //region Standard Entity Methods

    @Override
    public void clearCaches() {
        shieldCache = null;
    }


    @Override
    public void writeToNBT(CompoundTag compound) {
        super.writeToNBT(compound);
        compound.putDouble("boost", shieldBoost);
        compound.putDouble("max_boost", maxBoost);
        compound.putInt("boost_time", boostTime);
        compound.putByte("env_cdwn", envDmgCoolDown);
        compound.putBoolean("visible", shieldVisible);
        compound.putFloat("anim", shieldAnim);
        compound.putFloat("hit", shieldHitIndicator);
    }

    @Override
    public void readFromNBT(CompoundTag compound) {
        super.readFromNBT(compound);
        shieldBoost = compound.getDouble("boost");
        maxBoost = compound.getDouble("max_boost");
        boostTime = compound.getInt("boost_time");
        envDmgCoolDown = compound.getByte("env_cdwn");
        shieldVisible = compound.getBoolean("visible");
        shieldAnim = compound.getFloat("anim");
        shieldHitIndicator = compound.getFloat("hit");
    }

    @Override
    protected void readExtraData(CompoundTag nbt) {
        shieldCapacity = nbt.getInt("cap");
        shieldPoints = nbt.getDouble("points");
        shieldCoolDown = nbt.getInt("cooldwn");
    }

    @Override
    protected CompoundTag writeExtraData(CompoundTag nbt) {
        nbt.putInt("cap", shieldCapacity);
        nbt.putDouble("points", shieldPoints);
        nbt.putInt("cooldwn", shieldCoolDown);
        return nbt;
    }

    //endregion
}
