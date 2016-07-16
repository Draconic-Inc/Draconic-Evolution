package com.brandon3055.draconicevolution.items.tools;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

import java.util.Set;

/**
 * Created by brandon3055 on 2/06/2016.
 */
public class WyvernShovel extends MiningToolBase {

    public WyvernShovel(float attackDamage, float attackSpeed, Set<Block> effectiveBlocks) {
        super(attackDamage, attackSpeed, effectiveBlocks);
    }

    public WyvernShovel() {
        super(ToolStats.WYV_SHOVEL_ATTACK_DAMAGE, ToolStats.WYV_SHOVEL_ATTACK_SPEED, SHOVEL_OVERRIDES);//TODO Attack Damage and speed
        this.baseMiningSpeed = ToolStats.WYV_SHOVEL_MINING_SPEED;
        this.baseAOE = ToolStats.BASE_WYVERN_MINING_AOE;
        setEnergyStats(ToolStats.WYVERN_BASE_CAPACITY, 512000, 0);
        this.setHarvestLevel("shovel", 10);
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
