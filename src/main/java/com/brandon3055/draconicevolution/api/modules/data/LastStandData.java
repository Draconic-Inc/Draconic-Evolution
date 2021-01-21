package com.brandon3055.draconicevolution.api.modules.data;

import com.brandon3055.brandonscore.utils.Utils;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleContext;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Map;

/**
 * Created by brandon3055 on 3/5/20.
 */
public class LastStandData implements ModuleData<LastStandData> {

    private final float healthBoost;
    private final float shieldBoost;
    private final int shieldBoostTime;
    private final int chargeTime;
    private final long chargeEnergy;
    private final int invulnerableTime;

    public LastStandData(float healthBoost, float shieldBoost, int shieldBoostTime, int chargeTime, long chargeEnergy, int invulnerableTime) {
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
    public LastStandData combine(LastStandData other) {
        return other;
    }

    @Override
    public void addInformation(Map<ITextComponent, ITextComponent> map, ModuleContext context, boolean stack) {
        if (stack){
            map.put(new TranslationTextComponent("module.draconicevolution.last_stand.health.name"), new TranslationTextComponent("module.draconicevolution.last_stand.health.value", (int)healthBoost));
            map.put(new TranslationTextComponent("module.draconicevolution.last_stand.shield.name"), new TranslationTextComponent("module.draconicevolution.last_stand.shield.value", (int)shieldBoost, shieldBoostTime / 20));
            map.put(new TranslationTextComponent("module.draconicevolution.last_stand.charge.name"), new TranslationTextComponent("module.draconicevolution.last_stand.charge.value", chargeTime / 20));
            map.put(new TranslationTextComponent("module.draconicevolution.last_stand.energy.name"), new TranslationTextComponent("module.draconicevolution.last_stand.energy.value", ModuleData.formatNumber(chargeEnergy) , getChargeEnergyRate()));
            map.put(new TranslationTextComponent("module.draconicevolution.last_stand.invuln.name"), new TranslationTextComponent("module.draconicevolution.last_stand.invuln.value", invulnerableTime / 20));
        }
    }
}
