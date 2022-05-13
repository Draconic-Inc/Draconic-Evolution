package com.brandon3055.draconicevolution.api.modules.data;

import com.brandon3055.draconicevolution.api.modules.lib.ModuleContext;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

import java.util.Map;

/**
 * Created by brandon3055 on 3/5/20.
 */
public class ShieldControlData implements ModuleData<ShieldControlData> {
    private final int coolDownTicks;

    public ShieldControlData(int coolDownTicks) {
        this.coolDownTicks = coolDownTicks;
    }

    public int getCoolDownTicks() {
        return coolDownTicks;
    }

    @Override
    public ShieldControlData combine(ShieldControlData other) {
        return new ShieldControlData(coolDownTicks + other.coolDownTicks);
    }

    @Override
    public void addInformation(Map<Component, Component> map, ModuleContext context, boolean stack) {
        map.put(new TranslatableComponent("module.draconicevolution.shield_control.name"),
                new TranslatableComponent("module.draconicevolution.shield_control.value", ModuleData.round((coolDownTicks / 20D), 10)));
    }
}
