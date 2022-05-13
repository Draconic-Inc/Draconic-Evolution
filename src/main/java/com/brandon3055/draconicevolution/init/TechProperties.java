package com.brandon3055.draconicevolution.init;

import com.brandon3055.brandonscore.api.TechLevel;
import net.minecraft.world.item.Item;

/**
 * Created by brandon3055 on 05/05/2022
 */
public class TechProperties extends Item.Properties {

    private TechLevel techLevel;

    public TechProperties(TechLevel techLevel) {
        this.techLevel = techLevel;
    }

    public TechLevel getTechLevel() {
        return techLevel;
    }

    public TechProperties copy() {
        TechProperties copy = new TechProperties(techLevel);
        copy.maxStackSize = this.maxStackSize;
        copy.maxDamage = this.maxDamage;
        copy.craftingRemainingItem = this.craftingRemainingItem;
        copy.category = this.category;
        copy.rarity = this.rarity;
        copy.foodProperties = this.foodProperties;
        copy.isFireResistant = this.isFireResistant;
        return copy;
    }
}
