package com.brandon3055.draconicevolution.api.modules.properties;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import java.util.Map;

/**
 * Created by brandon3055 on 3/5/20.
 */
public class EnergyData implements ModuleData<EnergyData> {
    private final long capacity;

    public EnergyData(long capacity) {
        this.capacity = capacity;
    }

    public long getCapacity() {
        return capacity;
    }

    @Override
    public EnergyData combine(EnergyData other) {
        return new EnergyData(capacity + other.capacity);
    }

    @Override
    public void addInformation(Map<ITextComponent, ITextComponent> map) {
        map.put(new StringTextComponent("EnergyModule"), new StringTextComponent("TODO"));
    }
}
