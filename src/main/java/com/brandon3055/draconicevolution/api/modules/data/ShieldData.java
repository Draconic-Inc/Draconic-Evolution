package com.brandon3055.draconicevolution.api.modules.data;

import com.brandon3055.draconicevolution.api.modules.lib.ModuleContext;
import com.brandon3055.draconicevolution.init.EquipCfg;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

import java.util.Map;

/**
 * Created by brandon3055 on 3/5/20.
 *
 * @param shieldRecharge Shield points per tick
 */
public record ShieldData(int shieldCapacity, double shieldRecharge) implements ModuleData<ShieldData> {

    @Override
    public ShieldData combine(ShieldData other) {
        return new ShieldData(shieldCapacity + other.shieldCapacity, shieldRecharge + other.shieldRecharge);
    }

    @Override
    public void addInformation(Map<Component, Component> map, ModuleContext context, boolean stack) {
        if (shieldCapacity > 0) {
            map.put(new TranslatableComponent("module.draconicevolution.shield_capacity.name"),
                    new TranslatableComponent("module.draconicevolution.shield_capacity.value", shieldCapacity));
        }
        if (shieldRecharge > 0) {
            map.put(new TranslatableComponent("module.draconicevolution.shield_recharge.name"),
                    new TranslatableComponent("module.draconicevolution.shield_recharge.value", ModuleData.round(shieldRecharge * 20, 10), ModuleData.round((shieldCapacity / shieldRecharge) / 20, 10), (int) (Math.max(shieldRecharge * EquipCfg.energyShieldChg, EquipCfg.energyShieldChg))));
        }
        if (shieldCapacity > 0) {
            map.put(new TranslatableComponent("module.draconicevolution.shield_passive.name"),
                    new TranslatableComponent("module.draconicevolution.shield_passive.value", Math.round(shieldCapacity * shieldCapacity * EquipCfg.shieldPassiveModifier * 10) / 10D));
        }
    }
}
