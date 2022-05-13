package com.brandon3055.draconicevolution.api.modules.data;

import com.brandon3055.draconicevolution.api.modules.lib.ModuleContext;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

import java.util.Map;

/**
 * Created by brandon3055 on 3/5/20.
 */
public class UndyingData implements ModuleData<UndyingData> {

    private final float healthBoost;
    private final float shieldBoost;
    private final int shieldBoostTime;
    private final int chargeTime;
    private final long chargeEnergy;
    private final int invulnerableTime;

    public UndyingData(float healthBoost, float shieldBoost, int shieldBoostTime, int chargeTime, long chargeEnergy, int invulnerableTime) {
        this.healthBoost = healthBoost;
        this.shieldBoost = shieldBoost;
        this.shieldBoostTime = shieldBoostTime;
        this.chargeTime = chargeTime;
        this.chargeEnergy = chargeEnergy;
        this.invulnerableTime = invulnerableTime;
    }

    public float getHealthBoost() {
        return healthBoost;
    }

    public float getShieldBoost() {
        return shieldBoost;
    }

    public int getShieldBoostTime() {
        return shieldBoostTime;
    }

    public int getChargeTime() {
        return chargeTime;
    }

    public long getChargeEnergyRate() {
        return chargeEnergy / chargeTime;
    }

    public long getChargeEnergy() {
        return chargeEnergy;
    }

    public int getInvulnerableTime() {
        return invulnerableTime;
    }

    @Override
    public UndyingData combine(UndyingData other) {
        return other;
    }

    @Override
    public void addInformation(Map<Component, Component> map, ModuleContext context, boolean stack) {
        if (stack){
            map.put(new TranslatableComponent("module.draconicevolution.undying.health.name"), new TranslatableComponent("module.draconicevolution.undying.health.value", (int)healthBoost));
            map.put(new TranslatableComponent("module.draconicevolution.undying.shield.name"), new TranslatableComponent("module.draconicevolution.undying.shield.value", (int)shieldBoost, shieldBoostTime / 20));
            map.put(new TranslatableComponent("module.draconicevolution.undying.charge.name"), new TranslatableComponent("module.draconicevolution.undying.charge.value", chargeTime / 20));
            map.put(new TranslatableComponent("module.draconicevolution.undying.energy.name"), new TranslatableComponent("module.draconicevolution.undying.energy.value", ModuleData.formatNumber(chargeEnergy) , getChargeEnergyRate()));
            map.put(new TranslatableComponent("module.draconicevolution.undying.invuln.name"), new TranslatableComponent("module.draconicevolution.undying.invuln.value", invulnerableTime / 20));
        }
    }
}
