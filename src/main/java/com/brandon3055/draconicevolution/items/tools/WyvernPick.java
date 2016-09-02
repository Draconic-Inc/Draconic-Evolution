package com.brandon3055.draconicevolution.items.tools;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

import java.util.Set;

/**
 * Created by brandon3055 on 2/06/2016.
 */
public class WyvernPick extends MiningToolBase {
    
    public WyvernPick(double attackDamage, double attackSpeed, Set<Block> effectiveBlocks) {
        super(attackDamage, attackSpeed, effectiveBlocks);
    }

    public WyvernPick() {
        super(ToolStats.WYV_PICK_ATTACK_DAMAGE, ToolStats.WYV_PICK_ATTACK_SPEED, PICKAXE_OVERRIDES);
        this.baseMiningSpeed = (float)ToolStats.WYV_PICK_MINING_SPEED;
        this.baseAOE = ToolStats.BASE_WYVERN_MINING_AOE;
        setEnergyStats(ToolStats.WYVERN_BASE_CAPACITY, 512000, 0);
        this.setHarvestLevel("pickaxe", 10);
    }

    @Override
    public int getMaxUpgradeLevel(ItemStack stack, String upgrade) {
        return 2;
    }

    @Override
    public int getToolTier(ItemStack stack) {
        return 0;
    }
}
