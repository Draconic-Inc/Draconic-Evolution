package com.brandon3055.draconicevolution.api.modules.data;

import com.brandon3055.draconicevolution.api.modules.lib.ModuleContext;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

import java.util.Map;

/**
 * Created by brandon3055 on 3/5/20.
 */
public record AOEData(int aoe) implements ModuleData<AOEData> {

    @Override
    public AOEData combine(AOEData other) {
        return new AOEData(aoe + other.aoe);
    }

    @Override
    public void addInformation(Map<Component, Component> map, ModuleContext context, boolean stack) {
        map.put(new TranslatableComponent("module.draconicevolution.aoe.name"), new TranslatableComponent("module.draconicevolution.aoe.value", 1 + (aoe * 2), 1 + (aoe * 2)));
    }
}
