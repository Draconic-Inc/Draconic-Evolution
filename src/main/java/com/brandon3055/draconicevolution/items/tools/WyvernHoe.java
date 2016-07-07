package com.brandon3055.draconicevolution.items.tools;

import com.brandon3055.draconicevolution.api.itemconfig.AOEConfigField;
import com.brandon3055.draconicevolution.api.itemconfig.ItemConfigFieldRegistry;
import com.brandon3055.draconicevolution.api.itemupgrade.UpgradeHelper;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.item.ItemStack;

import java.util.List;

import static com.brandon3055.draconicevolution.items.ToolUpgrade.DIG_AOE;

/**
 * Created by brandon3055 on 5/06/2016.
 */
public class WyvernHoe extends ToolBase {
    protected int baseAOE;

    public WyvernHoe(float attackDamage, float attackSpeed) {
        super(attackDamage, attackSpeed);
        this.baseAOE = 2;
    }

    public WyvernHoe() {
        super(ToolStats.WYV_HOE_ATTACK_DAMAGE, ToolStats.WYV_HOE_ATTACK_SPEED);//todo attack damage and speed
        setEnergyStats(ToolStats.WYVERN_BASE_CAPACITY, 512000, 0);
        this.baseAOE = 1;
    }

    @Override
    public List<String> getValidUpgrades(ItemStack stack) {
        List<String> list = super.getValidUpgrades(stack);
        list.add(DIG_AOE);
        return list;
    }

    @Override
    public ItemConfigFieldRegistry getFields(ItemStack stack, ItemConfigFieldRegistry registry) {
        int maxAOE = baseAOE + UpgradeHelper.getUpgradeLevel(stack, DIG_AOE);
        registry.register(stack, new AOEConfigField("digAOE", 0, 0, maxAOE, "config.field.digAOE.description"));

        return registry;
    }

    @Override
    public int getMaxUpgradeLevel(ItemStack stack) {
        return 2;
    }

    @Override
    public boolean checkEnchantTypeValid(EnumEnchantmentType type) {
        return type == EnumEnchantmentType.ALL;
    }

    @Override
    public int getToolTier(ItemStack stack) {
        return 0;
    }
}
