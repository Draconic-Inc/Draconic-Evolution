package com.brandon3055.draconicevolution.items.tools;

import net.minecraft.item.ItemStack;

/**
 * Created by brandon3055 on 5/06/2016.
 */
public class DraconicAxe extends WyvernAxe {
    public DraconicAxe() {
        super(ToolStats.DRA_AXE_ATTACK_DAMAGE, ToolStats.DRA_AXE_ATTACK_SPEED, AXE_OVERRIDES);
        this.baseMiningSpeed = (float)ToolStats.DRA_AXE_MINING_SPEED;
        this.baseAOE = ToolStats.BASE_DRACONIC_MINING_AOE;
        setEnergyStats(ToolStats.DRACONIC_BASE_CAPACITY, 8000000, 0);
        this.setHarvestLevel("axe", 10);
    }

    @Override
    public int getToolTier(ItemStack stack) {
        return 1;
    }

    @Override
    protected int getHarvestRange() {
        return 2;
    }

    @Override
    protected int getMaxHarvest() {
        return 8192;
    }
}
