package com.brandon3055.draconicevolution.api.modules.data;

import com.brandon3055.draconicevolution.api.modules.lib.ModuleContext;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import java.util.Map;

/**
 * Created by brandon3055 on 3/5/20.
 */
public class AOEData implements ModuleData<AOEData> {
    private final int aoe;

    public AOEData(int aoe) {
        this.aoe = aoe;
    }

    public int getAOE() {
        return aoe;
    }

    @Override
    public AOEData combine(AOEData other) {
        return new AOEData(aoe + other.aoe);
    }

    @Override
    public void addInformation(Map<ITextComponent, ITextComponent> map, ModuleContext context) {
        map.put(new StringTextComponent("AOEModule"), new StringTextComponent("TODO"));
    }
}
