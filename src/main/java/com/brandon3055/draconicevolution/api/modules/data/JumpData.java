package com.brandon3055.draconicevolution.api.modules.data;

import com.brandon3055.draconicevolution.api.modules.lib.ModuleContext;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

import java.util.Map;

/**
 * Created by brandon3055 on 3/5/20.
 */
public record JumpData(double multiplier) implements ModuleData<JumpData> {

    @Override
    public JumpData combine(JumpData other) {
        return new JumpData(multiplier + other.multiplier);
    }

    @Override
    public void addInformation(Map<Component, Component> map, ModuleContext context, boolean stack) {
        map.put(new TranslatableComponent("module.draconicevolution.jump.name"), new TranslatableComponent("module.draconicevolution.jump.value", (int) (multiplier * 100D)));
    }
}
