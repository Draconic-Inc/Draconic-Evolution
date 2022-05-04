package com.brandon3055.draconicevolution.api.modules.data;

import com.brandon3055.draconicevolution.api.modules.lib.ModuleContext;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

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
    public void addInformation(Map<ITextComponent, ITextComponent> map, ModuleContext context, boolean stack) {
        map.put(new TranslationTextComponent("module.draconicevolution.aoe.name"), new TranslationTextComponent("module.draconicevolution.aoe.value", 1 + (aoe * 2), 1 + (aoe * 2)));
    }
}
