package com.brandon3055.draconicevolution.items.tools;

import com.brandon3055.brandonscore.lib.Set3;
import net.minecraft.item.ItemStack;

/**
 * Created by brandon3055 on 5/06/2016.
 */
public class DraconicHoe extends WyvernHoe {

    public DraconicHoe() {
        super(ToolStats.DRA_HOE_ATTACK_DAMAGE, ToolStats.DRA_HOE_ATTACK_SPEED);
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
    protected Set3<String, String, String> getTextureLocations() {
        return Set3.of("items/tools/draconic_hoe", "items/tools/obj/draconic_hoe", "models/item/tools/draconic_hoe.obj");
    }

    //endregion
}
