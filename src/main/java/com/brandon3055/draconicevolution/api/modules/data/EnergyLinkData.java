package com.brandon3055.draconicevolution.api.modules.data;

import com.brandon3055.draconicevolution.api.modules.lib.ModuleContext;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Created by brandon3055 on 3/5/20.
 */
public record EnergyLinkData(long activationEnergy, long operationEnergy, long transferLimit, boolean xDimensional) implements ModuleData<EnergyLinkData> {

    @Override
    public EnergyLinkData combine(EnergyLinkData other) {
        return new EnergyLinkData(Math.min(activationEnergy, other.activationEnergy), Math.min(operationEnergy, other.operationEnergy), transferLimit + other.transferLimit, xDimensional | other.xDimensional);
    }

    @Override
    public void addInformation(Map<Component, Component> map, @Nullable ModuleContext context) {
        map.put(Component.translatable("module.draconicevolution.energy_link.activation"), Component.translatable("module.draconicevolution.energy_link.activation.value", ModuleData.formatNumber(activationEnergy())));
        map.put(Component.translatable("module.draconicevolution.energy_link.operation"), Component.translatable("module.draconicevolution.energy_link.operation.value", ModuleData.formatNumber((long) (operationEnergy() * 0.1)), ModuleData.formatNumber(operationEnergy())));
        map.put(Component.translatable("module.draconicevolution.energy_link.transfer"), Component.translatable("module.draconicevolution.energy_link.transfer.value", ModuleData.formatNumber(transferLimit())));
        map.put(Component.translatable("module.draconicevolution.energy_link.dimensional"), Component.translatable("module.draconicevolution.energy_link.dimensional." + (xDimensional() ? "true" : "false")));
    }
}
