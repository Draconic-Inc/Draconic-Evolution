package com.brandon3055.draconicevolution.items.tools.old;

import com.brandon3055.brandonscore.lib.Pair;
import com.brandon3055.draconicevolution.api.itemupgrade_dep.UpgradeHelper;

import com.brandon3055.draconicevolution.items.ToolUpgrade;
import com.brandon3055.draconicevolution.client.DETextures;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

/**
 * Created by brandon3055 on 5/06/2016.
 */
@Deprecated
public class DraconicSword extends WyvernSword {
    public DraconicSword(Properties properties) {
        super(properties);
    }

    //    public DraconicSword() {
//        super(ToolStats.DRA_SWORD_ATTACK_DAMAGE, ToolStats.DRA_SWORD_ATTACK_SPEED);
//        setEnergyStats(ToolStats.DRACONIC_BASE_CAPACITY, 8000000, 0);
//    }

    @Override
    public double getBaseAttackSpeedConfig() {
        return ToolStats.DRA_SWORD_ATTACK_SPEED;
    }

    @Override
    public double getBaseAttackDamageConfig() {
        return ToolStats.DRA_SWORD_ATTACK_DAMAGE;
    }

    @Override
    public void loadEnergyStats() {
        setEnergyStats(ToolStats.DRACONIC_BASE_CAPACITY, 8000000, 0);
    }

    @Override
    public int getMaxUpgradeLevel(ItemStack stack, String upgrade) {
        return 3;
    }

    @Override
    public int getToolTier(ItemStack stack) {
        return 1;
    }

    //region Attack Stats

    @Override
    public double getMaxWeaponAOE(ItemStack stack) {
        int level = UpgradeHelper.getUpgradeLevel(stack, ToolUpgrade.ATTACK_AOE);
        if (level == 0) return 1;
        else if (level == 1) return 2;
        else if (level == 2) return 4;
        else if (level == 3) return 6;
        else if (level == 4) return 12;
        else return 0;
    }

    //endregion

    @Override
    public int getReaperLevel(ItemStack stack) {
        return 2;
    }

    //region Rendering

//    @Override
//    public void registerRenderer(Feature feature) {
//        super.registerRenderer(feature);
//        ToolOverrideList.putOverride(this, DraconicSword::handleTransforms);
//    }

//    @OnlyIn(Dist.CLIENT)//Avoids synthetic lambda creation booping the classloader on the server.
//    private static IModelState handleTransforms(TransformType transformType, IModelState state) {
//        return transformType == TransformType.FIXED || transformType == TransformType.GROUND ? ToolTransforms.DR_SWORD_STATE : state;
//    }

    @Override
    public Pair<TextureAtlasSprite, ResourceLocation> getModels(ItemStack stack) {
        return new Pair<>(DETextures.DRACONIC_SWORD, new ResourceLocation("draconicevolution", "models/item/tools/draconic_sword.obj"));
    }

    //endregion
}
