package com.brandon3055.draconicevolution.items.tools;

import com.brandon3055.brandonscore.lib.Pair;
import com.brandon3055.draconicevolution.client.DETextures;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

/**
 * Created by brandon3055 on 5/06/2016.
 */
@Deprecated
public class DraconicAxe extends WyvernAxe {
//    public DraconicAxe(Set effectiveBlocks) {
//        super(effectiveBlocks);
//    }

    public DraconicAxe(Properties properties) {
        super(properties);
    }


    //    public DraconicAxe() {
//        super(/*ToolStats.DRA_AXE_ATTACK_DAMAGE, ToolStats.DRA_AXE_ATTACK_SPEED, */AXE_OVERRIDES);
////        this.baseMiningSpeed = (float) ToolStats.DRA_AXE_MINING_SPEED;
////        this.baseAOE = ToolStats.BASE_DRACONIC_MINING_AOE;
////        setEnergyStats(ToolStats.DRACONIC_BASE_CAPACITY, 8000000, 0);
//        this.setHarvestLevel("axe", 10);
//    }

    @Override
    public double getBaseMinSpeedConfig() {
        return ToolStats.DRA_AXE_MINING_SPEED;
    }

    @Override
    public double getBaseAttackSpeedConfig() {
        return ToolStats.DRA_AXE_ATTACK_SPEED;
    }

    @Override
    public double getBaseAttackDamageConfig() {
        return ToolStats.DRA_AXE_ATTACK_DAMAGE;
    }

    @Override
    public int getBaseMinAOEConfig() {
        return ToolStats.BASE_DRACONIC_MINING_AOE;
    }

    @Override
    public void loadEnergyStats() {
        setEnergyStats(ToolStats.DRACONIC_BASE_CAPACITY, 8000000, 0);
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

    @Override
    public int getMaxUpgradeLevel(ItemStack stack, String upgrade) {
        return 3;
    }

    //region Rendering

    @Override
    public Pair<TextureAtlasSprite, ResourceLocation> getModels(ItemStack stack) {
        return new Pair<>(DETextures.DRACONIC_AXE, new ResourceLocation("draconicevolution", "models/item/tools/draconic_axe.obj"));
    }

    //endregion
}
