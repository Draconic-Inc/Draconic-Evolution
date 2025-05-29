package com.brandon3055.draconicevolution.items.equipment;

import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.draconicevolution.init.DETags;
import com.brandon3055.draconicevolution.init.EquipCfg;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;

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

    @Override
    public TagKey<Block> getIncorrectBlocksForDrops() {
        return switch (techLevel) {
            case WYVERN -> DETags.Blocks.INCORRECT_FOR_WYVERN_TOOL;
            case DRACONIC -> DETags.Blocks.INCORRECT_FOR_AWAKENED_TOOL;
            case CHAOTIC -> DETags.Blocks.INCORRECT_FOR_CHAOTIC_TOOL;
            default -> BlockTags.INCORRECT_FOR_NETHERITE_TOOL;
        };
    }

    @Override
    public int getEnchantmentValue() {
        return getEnchantability(techLevel);
    }

    public static int getEnchantability(TechLevel techLevel) {
        return switch (techLevel) {
            case DRACONIUM -> EquipCfg.draconiumEnchantability;
            case WYVERN -> EquipCfg.wyvernEnchantability;
            case DRACONIC -> EquipCfg.draconicEnchantability;
            case CHAOTIC -> EquipCfg.chaoticEnchantability;
        };
    }

    @Override
    public float getSpeed() { //Harvest Speed
        return switch (techLevel) {
            case DRACONIUM -> (float) EquipCfg.draconiumHarvestSpeed;
            case WYVERN -> (float) EquipCfg.wyvernHarvestSpeed;
            case DRACONIC -> (float) EquipCfg.draconicHarvestSpeed;
            case CHAOTIC -> (float) EquipCfg.chaoticHarvestSpeed;
        };
    }

    @Override
    public float getAttackDamageBonus() {
        return switch (techLevel) {
            case DRACONIUM -> (float) EquipCfg.draconiumDamage;
            case WYVERN -> (float) EquipCfg.wyvernDamage;
            case DRACONIC -> (float) EquipCfg.draconicDamage;
            case CHAOTIC -> (float) EquipCfg.chaoticDamage;
        };
    }

    public float getAttackSpeed() {
        return switch (techLevel) {
            case DRACONIUM -> (float) (EquipCfg.draconiumSwingSpeed);
            case WYVERN -> (float) (EquipCfg.wyvernSwingSpeed);
            case DRACONIC -> (float) (EquipCfg.draconicSwingSpeed);
            case CHAOTIC -> (float) (EquipCfg.chaoticSwingSpeed);
        };
    }

    @Override
    public Ingredient getRepairIngredient() {
        return Ingredient.EMPTY;
    }
}
