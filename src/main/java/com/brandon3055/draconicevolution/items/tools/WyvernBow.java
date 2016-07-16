package com.brandon3055.draconicevolution.items.tools;

import com.brandon3055.draconicevolution.items.ToolUpgrade;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.item.ItemStack;

import java.util.List;

/**
 * Created by brandon3055 on 2/06/2016.
 */
public class WyvernBow extends ToolBase {

    public WyvernBow(float attackDamage, float attackSpeed) {
        super(attackDamage, attackSpeed);
    }

    public WyvernBow() {
        super(1, 0);//TODO Attack Damage and speed
        setEnergyStats(ToolStats.WYVERN_BASE_CAPACITY, 512000, 0);
    }

    @Override
    public List<String> getValidUpgrades(ItemStack stack) {
        List<String> list = super.getValidUpgrades(stack);
        list.add(ToolUpgrade.ARROW_DAMAGE);
        list.add(ToolUpgrade.ARROW_SPEED);
        list.add(ToolUpgrade.DRAW_SPEED);
        return list;
    }

    @Override
    public int getMaxUpgradeLevel(ItemStack stack, String upgrade) {
        return 2;
    }

    @Override
    public boolean checkEnchantTypeValid(EnumEnchantmentType type) {
        return type == EnumEnchantmentType.BOW || type == EnumEnchantmentType.ALL;
    }

    @Override
    public int getToolTier(ItemStack stack) {
        return 0;
    }
}
