package com.brandon3055.draconicevolution.api.modules.data;

import com.brandon3055.draconicevolution.api.modules.lib.ModuleContext;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

import java.util.Map;

/**
 * Created by brandon3055 on 3/5/20.
 */
public record UndyingData(float healthBoost, float shieldBoost, int shieldBoostTime, int chargeTime, long chargeEnergy, int invulnerableTime) implements ModuleData<UndyingData> {

    public long getChargeEnergyRate() {
        return chargeEnergy / chargeTime;
    }

    @Override
    public UndyingData combine(UndyingData other) {
        return other;
    }

    @Override
    public void addInformation(Map<Component, Component> map, ModuleContext context, boolean stack) {
        if (stack) {
            map.put(new TranslatableComponent("module.draconicevolution.undying.health.name"), new TranslatableComponent("module.draconicevolution.undying.health.value", (int) healthBoost));
            map.put(new TranslatableComponent("module.draconicevolution.undying.shield.name"), new TranslatableComponent("module.draconicevolution.undying.shield.value", (int) shieldBoost, shieldBoostTime / 20));
            map.put(new TranslatableComponent("module.draconicevolution.undying.charge.name"), new TranslatableComponent("module.draconicevolution.undying.charge.value", chargeTime / 20));
            map.put(new TranslatableComponent("module.draconicevolution.undying.energy.name"), new TranslatableComponent("module.draconicevolution.undying.energy.value", ModuleData.formatNumber(chargeEnergy), getChargeEnergyRate()));
            map.put(new TranslatableComponent("module.draconicevolution.undying.invuln.name"), new TranslatableComponent("module.draconicevolution.undying.invuln.value", invulnerableTime / 20));
        }
    }
}
