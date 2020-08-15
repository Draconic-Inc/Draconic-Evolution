package com.brandon3055.draconicevolution.api.modules.data;

import com.brandon3055.draconicevolution.api.modules.lib.ModuleContext;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import java.util.Map;

/**
 * Created by brandon3055 on 3/5/20.
 */
public class EnergyLinkData implements ModuleData<EnergyLinkData> {

    public EnergyLinkData() {
    }

    //TODO

    @Override
    public EnergyLinkData combine(EnergyLinkData other) {
        return new EnergyLinkData();
    }

    @Override
    public void addInformation(Map<ITextComponent, ITextComponent> map, ModuleContext context, boolean stack) {
        map.put(new StringTextComponent("EnergyLinkModule"), new StringTextComponent("TODO"));
    }
}
