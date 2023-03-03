package com.brandon3055.draconicevolution.api.modules.data;

import com.brandon3055.draconicevolution.api.modules.lib.ModuleContext;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;

import java.util.Map;

/**
 * Created by brandon3055 on 3/5/20.
 */
public record EnergyShareData(long transferRate) implements ModuleData<EnergyShareData> {
    public static final EnergyShareData EMPTY = new EnergyShareData(0);

    @Override
    public EnergyShareData combine(EnergyShareData other) {
        return new EnergyShareData(transferRate + other.transferRate);
    }

    @Override
    public void addInformation(Map<Component, Component> map, ModuleContext context, boolean stack) {
        map.put(new TextComponent("EnergyModule"), new TextComponent("TODO"));
    }
}
