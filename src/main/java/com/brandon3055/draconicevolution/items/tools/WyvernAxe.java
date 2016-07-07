package com.brandon3055.draconicevolution.items.tools;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

import java.util.Set;

/**
 * Created by brandon3055 on 2/06/2016.
 */
public class WyvernAxe extends MiningToolBase {

    public WyvernAxe(float attackDamage, float attackSpeed, Set<Block> effectiveBlocks) {
        super(attackDamage, attackSpeed, effectiveBlocks);
    }

    public WyvernAxe() {
        super(ToolStats.WYV_AXE_ATTACK_DAMAGE, ToolStats.WYV_AXE_ATTACK_SPEED, AXE_OVERRIDES);//TODO Attack Damage and speed
        this.baseMiningSpeed = ToolStats.WYV_AXE_MINING_SPEED;
        this.baseAOE = ToolStats.BASE_WYVERN_MINING_AOE;
        setEnergyStats(ToolStats.WYVERN_BASE_CAPACITY, 512000, 0);
        this.setHarvestLevel("axe", 10);
    }

    @Override
    public int getMaxUpgradeLevel(ItemStack stack) {
        return 2;
    }

    @Override
    public int getToolTier(ItemStack stack) {
        return 0;
    }
}
