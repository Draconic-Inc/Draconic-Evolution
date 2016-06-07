package com.brandon3055.draconicevolution.items.armor;

import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;

/**
 * Created by brandon3055 on 6/06/2016.
 */
public class DraconicArmor extends WyvernArmor {

    public DraconicArmor(ArmorMaterial materialIn, int renderIndexIn, EntityEquipmentSlot equipmentSlotIn) {
        super(materialIn, renderIndexIn, equipmentSlotIn);
    }


    //region Upgrade

    @Override
    public int getUpgradeSlots(ItemStack stack) {
        return 6;
    }

    @Override
    public int getMaxUpgradeTier(ItemStack stack) {
        return 2;
    }

    //endregion
}
