package com.brandon3055.draconicevolution.items.tools;

import com.brandon3055.brandonscore.items.ItemEnergyBase;
import com.brandon3055.brandonscore.lib.TechItemProps;
import com.brandon3055.draconicevolution.DraconicEvolution;

/**
 * Created by brandon3055 on 21/5/20.
 */
public class ModularToolBase extends ItemEnergyBase {
    public ModularToolBase(TechItemProps properties) {
        super(properties);
        DraconicEvolution.proxy.registerToolRenderer(this);
    }
}
