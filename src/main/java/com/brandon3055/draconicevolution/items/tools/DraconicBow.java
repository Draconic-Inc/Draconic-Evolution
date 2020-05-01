package com.brandon3055.draconicevolution.items.tools;

import codechicken.lib.util.ItemNBTUtils;
import com.brandon3055.brandonscore.lib.PairKV;
import com.brandon3055.draconicevolution.api.itemconfig.BooleanConfigField;
import com.brandon3055.draconicevolution.api.itemconfig.DoubleConfigField;
import com.brandon3055.draconicevolution.api.itemconfig.ItemConfigFieldRegistry;
import com.brandon3055.draconicevolution.client.DETextures;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import static com.brandon3055.draconicevolution.api.itemconfig.IItemConfigField.EnumControlType.SLIDER;

/**
 * Created by brandon3055 on 5/06/2016.
 */
public class DraconicBow extends WyvernBow {
    public DraconicBow(Properties properties) {
        super(properties);
    }

    //    public DraconicBow() {
//        setEnergyStats(ToolStats.DRACONIC_BASE_CAPACITY, 8000000, 0);
//    }

    @Override
    public double getBaseAttackSpeedConfig() {
        return 0;
    }

    @Override
    public double getBaseAttackDamageConfig() {
        return 1;
    }

    @Override
    public void loadEnergyStats() {
        setEnergyStats(ToolStats.DRACONIC_BASE_CAPACITY, 8000000, 0);
    }

    @Override
    public ItemConfigFieldRegistry getFields(ItemStack stack, ItemConfigFieldRegistry registry) {
        registry.register(stack, new BooleanConfigField("bowFireArrow", false, "config.field.bowFireArrow.description"));
        registry.register(stack, new DoubleConfigField("bowShockPower", 0, 0, 4, "config.field.bowShockPower.description", SLIDER));
        return super.getFields(stack, registry);
    }

    @Override
    public int getMaxZoomModifier(ItemStack stack) {
        return ToolStats.BOW_DRACONIC_MAX_ZOOM;
    }

    @Override
    public int getToolTier(ItemStack stack) {
        return 1;
    }

    @Override
    public int getMaxUpgradeLevel(ItemStack stack, String upgrade) {
        return 3;
    }


    @Override
    public int getReaperLevel(ItemStack stack) {
        return 2;
    }

    @Override
    public PairKV<TextureAtlasSprite, ResourceLocation> getModels(ItemStack stack) {
        byte pull = ItemNBTUtils.getByte(stack, "render:bow_pull");
        return new PairKV<>(DETextures.DRACONIC_BOW[pull], new ResourceLocation("draconicevolution", String.format("models/item/tools/draconic_bow0%s.obj", pull)));
    }
}
