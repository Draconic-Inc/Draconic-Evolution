package com.brandon3055.draconicevolution.items.tools;

import com.brandon3055.draconicevolution.items.ToolUpgrade;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.item.ItemStack;

import java.util.List;

/**
 * Created by brandon3055 on 2/06/2016.
 */
public class WyvernSword extends ToolBase {

    public WyvernSword(double attackDamage, double attackSpeed) {
        super(attackDamage, attackSpeed);
    }

    public WyvernSword() {
        super(ToolStats.WYV_SWORD_ATTACK_DAMAGE, ToolStats.WYV_SWORD_ATTACK_SPEED);//TODO Attack Damage and speed
        setEnergyStats(ToolStats.WYVERN_BASE_CAPACITY, 512000, 0);
    }

    @Override
    public List<String> getValidUpgrades(ItemStack stack) {
        List<String> list = super.getValidUpgrades(stack);
        list.add(ToolUpgrade.ATTACK_DAMAGE);
        list.add(ToolUpgrade.ATTACK_AOE);
        return list;
    }

    @Override
    public int getMaxUpgradeLevel(ItemStack stack, String upgrade) {
        return 1;
    }

    @Override
    public boolean checkEnchantTypeValid(EnumEnchantmentType type) {
        return type == EnumEnchantmentType.WEAPON || type == EnumEnchantmentType.ALL;
    }

    @Override
    public int getToolTier(ItemStack stack) {
        return 0;
    }
}
