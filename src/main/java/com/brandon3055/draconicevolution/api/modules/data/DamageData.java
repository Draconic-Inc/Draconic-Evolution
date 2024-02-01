package com.brandon3055.draconicevolution.api.modules.data;

import com.brandon3055.draconicevolution.api.modules.lib.ModuleContext;
import net.minecraft.network.chat.Component;

import java.util.Map;

/**
 * Created by brandon3055 on 3/5/20.
 */
public record DamageData(double damagePoints) implements ModuleData<DamageData> {

    @Override
    public DamageData combine(DamageData other) {
        return new DamageData(damagePoints + other.damagePoints);
    }

    @Override
    public void addInformation(Map<Component, Component> map, ModuleContext context, boolean stack) {
        map.put(Component.translatable("module.draconicevolution.damage.name"), Component.translatable("module.draconicevolution.damage.attack", ModuleData.round(damagePoints, 10)));
    }
}
