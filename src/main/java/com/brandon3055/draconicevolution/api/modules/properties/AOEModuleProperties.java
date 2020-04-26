package com.brandon3055.draconicevolution.api.modules.properties;

import com.brandon3055.draconicevolution.api.TechLevel;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import java.util.List;
import java.util.Map;

/**
 * Created by covers1624 on 4/16/20.
 */
public class AOEModuleProperties extends ModuleProperties<AOEModuleProperties> {
    private final int aoe;

    public AOEModuleProperties(TechLevel techLevel, int aoe, int width, int height) {
        super(techLevel, width, height);
        this.aoe = aoe;
    }

    public AOEModuleProperties(TechLevel techLevel, int aoe) {
        this(techLevel, aoe, 2, 2);
    }

    public int getAOE() {
        return aoe;
    }

    @Override
    public void addCombinedStats(List<AOEModuleProperties> propertiesList, Map<ITextComponent, ITextComponent> map) {
        map.put(new StringTextComponent("AOEModule"), new StringTextComponent("TODO"));
    }
}
