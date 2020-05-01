package com.brandon3055.draconicevolution.items.tools;

import com.brandon3055.brandonscore.lib.PairKV;
import com.brandon3055.draconicevolution.client.DETextures;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

/**
 * Created by brandon3055 on 5/06/2016.
 */
public class DraconicShovel extends WyvernShovel {
    public DraconicShovel(Properties properties) {
        super(properties);
    }

    //    public DraconicShovel() {
//        super(/*ToolStats.DRA_SHOVEL_ATTACK_DAMAGE, ToolStats.DRA_SHOVEL_ATTACK_SPEED, */SHOVEL_OVERRIDES);
////        this.baseMiningSpeed = (float) ToolStats.DRA_SHOVEL_MINING_SPEED;
////        this.baseAOE = ToolStats.BASE_DRACONIC_MINING_AOE;
////        setEnergyStats(ToolStats.DRACONIC_BASE_CAPACITY, 8000000, 0);
////        this.setHarvestLevel("shovel", 10);
//    }

    @Override
    public double getBaseMinSpeedConfig() {
        return ToolStats.DRA_SHOVEL_MINING_SPEED;
    }

    @Override
    public int getBaseMinAOEConfig() {
        return ToolStats.BASE_DRACONIC_MINING_AOE;
    }

    @Override
    public double getBaseAttackSpeedConfig() {
        return ToolStats.DRA_SHOVEL_ATTACK_SPEED;
    }

    @Override
    public double getBaseAttackDamageConfig() {
        return ToolStats.DRA_SHOVEL_ATTACK_DAMAGE;
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
    public int getMaxUpgradeLevel(ItemStack stack, String upgrade) {
        return 3;
    }

    //region Rendering

    @Override
    public PairKV<TextureAtlasSprite, ResourceLocation> getModels(ItemStack stack) {
        return new PairKV<>(DETextures.DRACONIC_SHOVEL, new ResourceLocation("draconicevolution", "models/item/tools/draconic_shovel.obj"));
    }

    //endregion
}
