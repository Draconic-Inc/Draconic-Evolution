package com.brandon3055.draconicevolution.api.modules.properties;

import com.brandon3055.draconicevolution.api.TechLevel;
import com.brandon3055.draconicevolution.api.capability.ModuleHost;
import net.minecraft.util.text.ITextComponent;

import java.util.List;
import java.util.Map;

/**
 * Created by brandon3055 on 18/4/20.
 * This is a 'blank' implementation of {@link ModuleProperties} Use this for basic modules that dont require any additional properties.
 */
public class BlankModuleProperties extends ModuleProperties<BlankModuleProperties> {

    public BlankModuleProperties(TechLevel techLevel) {
        super(techLevel);
    }

    public BlankModuleProperties(TechLevel techLevel, int width, int height) {
        super(techLevel, width, height);
    }

    @Override
    public void addCombinedStats(List<BlankModuleProperties> propertiesList, Map<ITextComponent, ITextComponent> map, ModuleHost moduleHost) {
    }
}
