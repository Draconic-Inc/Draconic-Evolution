package com.brandon3055.draconicevolution.api.modules.data;

import com.brandon3055.draconicevolution.api.modules.lib.ModuleContext;
import net.minecraft.network.chat.Component;

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
        map.put(Component.translatable("module.draconicevolution.energy.capacity"), Component.translatable("module.draconicevolution.energy.capacity.value", ModuleData.formatNumber(capacity)));
        map.put(Component.translatable("module.draconicevolution.energy.transfer"), Component.translatable("module.draconicevolution.energy.transfer.value", ModuleData.formatNumber(transfer)));
    }
}
