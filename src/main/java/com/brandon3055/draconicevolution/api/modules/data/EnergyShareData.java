package com.brandon3055.draconicevolution.api.modules.data;

import com.brandon3055.draconicevolution.api.modules.lib.ModuleContext;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import java.util.Map;

/**
 * Created by brandon3055 on 3/5/20.
 */
public class EnergyShareData implements ModuleData<EnergyShareData> {
    private final long transferRate;

    public EnergyShareData(long transferRate) {
        this.transferRate = transferRate;
    }

    public long getTransferRate() {
        return transferRate;
    }

    @Override
    public EnergyShareData combine(EnergyShareData other) {
        return new EnergyShareData(transferRate + other.transferRate);
    }

    @Override
    public void addInformation(Map<ITextComponent, ITextComponent> map, ModuleContext context, boolean stack) {
        map.put(new StringTextComponent("EnergyModule"), new StringTextComponent("TODO"));
    }
}
