package com.brandon3055.draconicevolution.items.tools;

import com.brandon3055.draconicevolution.api.itemupgrade.UpgradeHelper;
import com.brandon3055.draconicevolution.items.ToolUpgrade;
import net.minecraft.item.ItemStack;

/**
 * Created by brandon3055 on 5/06/2016.
 */
public class DraconicSword extends WyvernSword {
    public DraconicSword() {
        super(ToolStats.DRA_SWORD_ATTACK_DAMAGE, ToolStats.DRA_SWORD_ATTACK_SPEED);
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

    protected double getMaxAttackAOE(ItemStack stack) {
        int level = UpgradeHelper.getUpgradeLevel(stack, ToolUpgrade.ATTACK_AOE);
        if (level == 0) return 1;
        else if (level == 1) return 2;
        else if (level == 2) return 4;
        else if (level == 3) return 6;
        else if (level == 4) return 12;
        else return 0;
    }

    //endregion
}
