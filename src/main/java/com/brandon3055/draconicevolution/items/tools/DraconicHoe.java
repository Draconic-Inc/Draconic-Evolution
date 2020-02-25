package com.brandon3055.draconicevolution.items.tools;

import com.brandon3055.brandonscore.lib.PairKV;
import com.brandon3055.draconicevolution.utils.DETextures;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

/**
 * Created by brandon3055 on 5/06/2016.
 */
public class DraconicHoe extends WyvernHoe {
    public DraconicHoe(Properties properties) {
        super(properties);
    }

    //    public DraconicHoe() {
////        super(ToolStats.DRA_HOE_ATTACK_DAMAGE, ToolStats.DRA_HOE_ATTACK_SPEED);
////        setEnergyStats(ToolStats.DRACONIC_BASE_CAPACITY, 8000000, 0);
//    }

    @Override
    public double getBaseAttackSpeedConfig() {
        return ToolStats.DRA_HOE_ATTACK_SPEED;
    }

    @Override
    public double getBaseAttackDamageConfig() {
        return ToolStats.DRA_HOE_ATTACK_DAMAGE;
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
        return new PairKV<>(DETextures.DRACONIC_HOE, new ResourceLocation("draconicevolution", "models/item/tools/draconic_hoe.obj"));
    }

    //endregion
}
