package com.brandon3055.draconicevolution.items.tools;

import com.brandon3055.brandonscore.lib.TechItemProps;
import com.brandon3055.draconicevolution.DraconicEvolution;
import net.minecraftforge.common.ToolType;

/**
 * Created by brandon3055 on 21/5/20.
 */
public class ModularShovel extends ModularToolBase {
    public ModularShovel(TechItemProps props) {
        super(props.addToolType(ToolType.SHOVEL, props.miningLevel));
        DraconicEvolution.proxy.registerToolRenderer(this);
    }
}
