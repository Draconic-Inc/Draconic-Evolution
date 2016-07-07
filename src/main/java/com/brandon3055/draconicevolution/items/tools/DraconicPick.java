package com.brandon3055.draconicevolution.items.tools;

import net.minecraft.item.ItemStack;

/**
 * Created by brandon3055 on 5/06/2016.
 */
public class DraconicPick extends WyvernPick {

    public DraconicPick() {
        super(ToolStats.DRA_PICK_ATTACK_DAMAGE, ToolStats.DRA_PICK_ATTACK_SPEED, PICKAXE_OVERRIDES);
        this.baseMiningSpeed = ToolStats.DRA_PICK_MINING_SPEED;
        this.baseAOE = ToolStats.BASE_DRACONIC_MINING_AOE;
        setEnergyStats(ToolStats.DRACONIC_BASE_CAPACITY, 8000000, 0);
        this.setHarvestLevel("pickaxe", 10);
    }

    @Override
    public int getMaxUpgradeLevel(ItemStack stack) {
        return 3;
    }

    @Override
    public int getToolTier(ItemStack stack) {
        return 1;
    }
}
