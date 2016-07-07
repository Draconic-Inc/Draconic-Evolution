package com.brandon3055.draconicevolution.items.tools;

import com.brandon3055.draconicevolution.items.ToolUpgrade;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.item.ItemStack;

import java.util.List;

/**
 * Created by brandon3055 on 5/06/2016.
 */
public class DraconicStaffOfPower extends MiningToolBase {
    public DraconicStaffOfPower() {
        super(ToolStats.DRA_STAFF_ATTACK_DAMAGE, ToolStats.DRA_STAFF_ATTACK_SPEED, PICKAXE_OVERRIDES);//TODO Attack Damage and speed
        this.baseMiningSpeed = ToolStats.DRA_STAFF_MINING_SPEED;
        this.baseAOE = ToolStats.BASE_DRACONIC_MINING_AOE + 1;
        setEnergyStats(ToolStats.DRACONIC_BASE_CAPACITY * 3, 16000000, 0);
//        this.setHarvestLevel("all", 10);
        this.setHarvestLevel("pickaxe", 10);
        this.setHarvestLevel("axe", 10);
        this.setHarvestLevel("shovel", 10);
    }

    @Override
    public List<String> getValidUpgrades(ItemStack stack) {
        List<String> list = super.getValidUpgrades(stack);
        list.add(ToolUpgrade.ATTACK_DAMAGE);
        list.add(ToolUpgrade.ATTACK_AOE);
        return list;
    }

    @Override
    public int getMaxUpgradeLevel(ItemStack stack) {
        return 3;
    }

    @Override
    public int getToolTier(ItemStack stack) {
        return 1;
    }

    @Override
    public boolean checkEnchantTypeValid(EnumEnchantmentType type) {
        return super.checkEnchantTypeValid(type) || type == EnumEnchantmentType.WEAPON;
    }
}
