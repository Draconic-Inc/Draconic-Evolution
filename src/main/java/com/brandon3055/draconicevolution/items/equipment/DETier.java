package com.brandon3055.draconicevolution.items.equipment;

import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.draconicevolution.init.EquipCfg;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;

/**
 * Created by brandon3055 on 16/6/20
 */
public class DETier implements Tier {
    private TechLevel techLevel;

    public DETier(TechLevel techLevel) {
        this.techLevel = techLevel;
    }

    @Override
    public int getUses() {
        return 0;
    }

    @Deprecated
    @Override
    public int getLevel() {
        return techLevel.getHarvestLevel();
    }

    @Override
    public int getEnchantmentValue() {
        return getEnchantability(techLevel);
    }

    public static int getEnchantability(TechLevel techLevel) {
        switch (techLevel) {
            case DRACONIUM:
                return EquipCfg.draconiumEnchantability;
            case WYVERN:
                return EquipCfg.wyvernEnchantability;
            case DRACONIC:
                return EquipCfg.draconicEnchantability;
            case CHAOTIC:
                return EquipCfg.chaoticEnchantability;
        }
        return 22;
    }

    @Override
    public float getSpeed() { //Harvest Speed
        switch (techLevel) {
            case DRACONIUM:
                return (float) EquipCfg.draconiumHarvestSpeed;
            case WYVERN:
                return (float) EquipCfg.wyvernHarvestSpeed;
            case DRACONIC:
                return (float) EquipCfg.draconicHarvestSpeed;
            case CHAOTIC:
                return (float) EquipCfg.chaoticHarvestSpeed;
        }
        return 1;
    }

    @Override
    public float getAttackDamageBonus() {
        switch (techLevel) {
            case DRACONIUM:
                return (float) EquipCfg.draconiumDamage;
            case WYVERN:
                return (float) EquipCfg.wyvernDamage;
            case DRACONIC:
                return (float) EquipCfg.draconicDamage;
            case CHAOTIC:
                return (float) EquipCfg.chaoticDamage;
        }
        return 1;
    }

    public float getAttackSpeed() {
        switch (techLevel) {
            case DRACONIUM:
                return (float) (EquipCfg.draconiumSwingSpeed);
            case WYVERN:
                return (float) (EquipCfg.wyvernSwingSpeed);
            case DRACONIC:
                return (float) (EquipCfg.draconicSwingSpeed);
            case CHAOTIC:
                return (float) (EquipCfg.chaoticSwingSpeed);
        }
        return 1;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return Ingredient.EMPTY;
    }
}
