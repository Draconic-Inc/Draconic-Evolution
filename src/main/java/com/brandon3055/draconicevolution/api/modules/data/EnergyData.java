package com.brandon3055.draconicevolution.api.modules.data;

import com.brandon3055.draconicevolution.api.modules.lib.ModuleContext;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

import java.util.Map;

/**
 * Created by brandon3055 on 3/5/20.
 */
public record EnergyData(long capacity, long transfer) implements ModuleData<EnergyData> {
    public static final EnergyData EMPTY = new EnergyData(0, 0);

    @Override
    public EnergyData combine(EnergyData other) {
        return new EnergyData(capacity + other.capacity, transfer + other.transfer);
    }

    @Override
    public void addInformation(Map<Component, Component> map, ModuleContext context, boolean stack) {
        long capacity = capacity();
        long transfer = transfer();
        map.put(new TranslatableComponent("module.draconicevolution.energy.capacity"), new TranslatableComponent("module.draconicevolution.energy.capacity.value", ModuleData.formatNumber(capacity)));
        map.put(new TranslatableComponent("module.draconicevolution.energy.transfer"), new TranslatableComponent("module.draconicevolution.energy.transfer.value", ModuleData.formatNumber(transfer)));
    }
}
