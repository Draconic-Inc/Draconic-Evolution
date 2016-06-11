package com.brandon3055.draconicevolution.items.armor;

import com.brandon3055.draconicevolution.api.itemupgrade.ItemUpgradeRegistry;
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
    public int getUpgradeCapacity(ItemStack stack) {
        return 6;
    }

    @Override
    public ItemUpgradeRegistry getValidUpgrades(ItemStack stack, ItemUpgradeRegistry upgradeRegistry) {
        super.getValidUpgrades(stack, upgradeRegistry);

        //todo modify max tier somehow. Maby just re add everything. Or get them from the registry and modify them

        return upgradeRegistry;
    }

    //endregion
}
