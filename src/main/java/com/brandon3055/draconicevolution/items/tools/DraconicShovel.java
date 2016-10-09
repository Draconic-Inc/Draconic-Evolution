package com.brandon3055.draconicevolution.items.tools;

import net.minecraft.item.ItemStack;

/**
 * Created by brandon3055 on 5/06/2016.
 */
public class DraconicShovel extends WyvernShovel {
    public DraconicShovel() {
        super(ToolStats.DRA_SHOVEL_ATTACK_DAMAGE, ToolStats.DRA_SHOVEL_ATTACK_SPEED, SHOVEL_OVERRIDES);
        this.baseMiningSpeed = (float)ToolStats.DRA_SHOVEL_MINING_SPEED;
        this.baseAOE = ToolStats.BASE_DRACONIC_MINING_AOE;
        setEnergyStats(ToolStats.DRACONIC_BASE_CAPACITY, 8000000, 0);
        this.setHarvestLevel("shovel", 10);
    }

    @Override
    public int getToolTier(ItemStack stack) {
        return 1;
    }

    @Override
    public int getMaxUpgradeLevel(ItemStack stack, String upgrade) {
        return 3;
    }
}
