package com.brandon3055.draconicevolution.api.modules.data;

import com.brandon3055.draconicevolution.api.modules.lib.ModuleContext;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;

import java.util.Map;

/**
 * Created by brandon3055 on 3/5/20.
 */
public class EnergyLinkData implements ModuleData<EnergyLinkData> {

    private final int activationEnergy;
    private final int operationEnergy;

    public EnergyLinkData(int activationEnergy, int operationEnergy) {
        this.activationEnergy = activationEnergy;
        this.operationEnergy = operationEnergy;
    }

    public int getActivationEnergy() {
        return activationEnergy;
    }

    public int getOperationEnergy() {
        return operationEnergy;
    }

    @Override
    public EnergyLinkData combine(EnergyLinkData other) {
        return new EnergyLinkData(activationEnergy + other.activationEnergy, operationEnergy + other.operationEnergy);
    }

    @Override
    public void addInformation(Map<Component, Component> map, ModuleContext context, boolean stack) {
        map.put(new TextComponent("EnergyLinkModule"), new TextComponent("TODO"));
    }
}
