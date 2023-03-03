package com.brandon3055.draconicevolution.api.modules.data;

import com.brandon3055.draconicevolution.api.modules.lib.ModuleContext;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

import java.util.Map;

/**
 * Created by brandon3055 on 3/5/20.
 */
public record SpeedData(double speedMultiplier) implements ModuleData<SpeedData> {

    @Override
    public SpeedData combine(SpeedData other) {
        return new SpeedData(speedMultiplier + other.speedMultiplier);
    }

    @Override
    public void addInformation(Map<Component, Component> map, ModuleContext context, boolean stack) {
        map.put(new TranslatableComponent("module.draconicevolution.speed.name"), new TranslatableComponent("module.draconicevolution.speed.value", (int) (speedMultiplier * 100D)));
    }
}
