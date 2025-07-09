package com.brandon3055.draconicevolution.init;

import com.brandon3055.brandonscore.api.TechLevel;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;

/**
 * Created by brandon3055 on 05/05/2022
 */
public class TechProperties extends Item.Properties {

    @Deprecated
    private final TechLevel techLevel;

    public TechProperties(TechLevel techLevel) {
        this.techLevel = techLevel;
//        this.component(ItemData.TECH_LEVEL, techLevel);
    }

    @Override
    public Item.Properties durability(int p_41504_) {
        this.component(DataComponents.MAX_STACK_SIZE, 1);
        return this;
    }

    public TechProperties copy() {
        TechProperties copy = new TechProperties(techLevel);
        copy.components.addAll(components.build());
        return copy;
    }

    @Deprecated
    public TechLevel getTechLevel() {
        return techLevel;
    }
}
