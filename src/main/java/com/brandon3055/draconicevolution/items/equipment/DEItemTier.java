package com.brandon3055.draconicevolution.items.equipment;

import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.brandonscore.lib.TechItemProps;
import com.brandon3055.draconicevolution.init.EquipCfg;
import net.minecraft.item.IItemTier;
import net.minecraft.item.crafting.Ingredient;

import java.util.function.Supplier;

/**
 * Created by brandon3055 on 16/6/20
 */
public class DEItemTier implements IItemTier {
    private TechItemProps itemProps;
    private TechLevel techLevel;
    private Supplier<Float> damageMultiplier;
    private Supplier<Float> speedMultiplier;
    private Supplier<Float> efficiencyMultiplier;

    public DEItemTier(TechItemProps itemProps, Supplier<Float> damageMultiplier, Supplier<Float> speedMultiplier) {
        this.itemProps = itemProps;
        this.techLevel = itemProps.techLevel;
        this.damageMultiplier = damageMultiplier;
        this.speedMultiplier = speedMultiplier;
    }

    public DEItemTier(TechItemProps itemProps, Supplier<Float> damageMultiplier, Supplier<Float> speedMultiplier, Supplier<Float> efficiencyMultiplier) {
        this(itemProps, damageMultiplier, speedMultiplier);
        this.efficiencyMultiplier = efficiencyMultiplier;
    }

    @Override
    public int getMaxUses() {
        return 0; // = unbreakable but can be overridden by item properties
    }


    @Override
    public int getHarvestLevel() {
        return itemProps.miningLevel;
    }

    @Override
    public int getEnchantability() {
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
    public float getEfficiency() {
        switch (techLevel) {
            case DRACONIUM:
                return (float) EquipCfg.draconiumEfficiency * (efficiencyMultiplier == null ? 1 : efficiencyMultiplier.get());
            case WYVERN:
                return (float) EquipCfg.wyvernEfficiency * (efficiencyMultiplier == null ? 1 : efficiencyMultiplier.get());
            case DRACONIC:
                return (float) EquipCfg.draconicEfficiency * (efficiencyMultiplier == null ? 1 : efficiencyMultiplier.get());
            case CHAOTIC:
                return (float) EquipCfg.chaoticEfficiency * (efficiencyMultiplier == null ? 1 : efficiencyMultiplier.get());
        }
        return 1;
    }

    @Override
    public float getAttackDamage() {
        switch (techLevel) {
            case DRACONIUM:
                return (float) EquipCfg.draconiumDamage * damageMultiplier.get();
            case WYVERN:
                return (float) EquipCfg.wyvernDamage * damageMultiplier.get();
            case DRACONIC:
                return (float) EquipCfg.draconicDamage * damageMultiplier.get();
            case CHAOTIC:
                return (float) EquipCfg.chaoticDamage * damageMultiplier.get();
        }
        return 1;
    }

    public float getAttackSpeed() {
        switch (techLevel) {
            case DRACONIUM:
                return (float) (EquipCfg.draconiumSpeed * speedMultiplier.get());
            case WYVERN:
                return (float) (EquipCfg.wyvernSpeed * speedMultiplier.get());
            case DRACONIC:
                return (float) (EquipCfg.draconicSpeed * speedMultiplier.get());
            case CHAOTIC:
                return (float) (EquipCfg.chaoticSpeed * speedMultiplier.get());
        }
        return 1;
    }

    @Override
    public Ingredient getRepairMaterial() {
        return Ingredient.EMPTY;
    }
}
