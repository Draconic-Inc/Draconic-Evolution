package com.brandon3055.draconicevolution.items.tools;

import com.brandon3055.brandonscore.lib.TechItemProps;
import com.brandon3055.draconicevolution.DraconicEvolution;
import net.minecraftforge.common.ToolType;

/**
 * Created by brandon3055 on 21/5/20.
 */
public class ModularPickaxe extends ModularToolBase {
    public ModularPickaxe(TechItemProps props) {
        super(props.addToolType(ToolType.PICKAXE, props.miningLevel));
        DraconicEvolution.proxy.registerToolRenderer(this);
    }
}
