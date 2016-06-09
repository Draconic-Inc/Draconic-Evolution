package com.brandon3055.draconicevolution.items.armor;

import com.brandon3055.brandonscore.config.Feature;
import com.brandon3055.brandonscore.config.ICustomRender;
import com.brandon3055.draconicevolution.api.itemconfig.IConfigurableItem;
import com.brandon3055.draconicevolution.api.itemconfig.ItemConfigFieldRegistry;
import com.brandon3055.draconicevolution.api.itemupgrade.IUpgradableItem;
import com.brandon3055.draconicevolution.api.itemupgrade.IUpgrade;
import com.brandon3055.draconicevolution.api.itemupgrade.UpgradeRegistry;
import com.brandon3055.draconicevolution.client.model.IDualModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

import java.util.Map;

/**
 * Created by brandon3055 on 6/06/2016.
 */
public class WyvernArmor extends ItemArmor implements ICustomRender, IDualModel, IConfigurableItem, IUpgradableItem{

    public WyvernArmor(ArmorMaterial materialIn, int renderIndexIn, EntityEquipmentSlot equipmentSlotIn) {
        super(materialIn, renderIndexIn, equipmentSlotIn);
    }


    //region Config

    @Override
    public int getProfileCount(ItemStack stack) {
        return 3;
    }

    @Override
    public ItemConfigFieldRegistry getFields(ItemStack stack, ItemConfigFieldRegistry fieldRegistry) {
        return fieldRegistry;
    }


    //endregion

    //region Upgrade


    @Override
    public Map<IUpgrade, Integer> getValidUpgrades(ItemStack stack, Map<IUpgrade, Integer> upgrades) {
        upgrades.put(UpgradeRegistry.RF_CAPACITY, -1);
        upgrades.put(UpgradeRegistry.JUMP_BOOST, -1);
        upgrades.put(UpgradeRegistry.MOVE_SPEED, -1);
        upgrades.put(UpgradeRegistry.SHIELD_CAPACITY, -1);
        upgrades.put(UpgradeRegistry.SHIELD_RECOVERY, -1);
        return upgrades;
    }

    @Override
    public int getUpgradeSlots(ItemStack stack) {
        return 3;
    }

    @Override
    public int getMaxUpgradeTier(ItemStack stack) {
        return 1;
    }

    //endregion

    //region Item Render

    @Override
    public void registerRenderer(Feature feature) {

    }

    @Override
    public boolean registerNormal(Feature feature) {
        return false;
    }

    @Override
    public ModelResourceLocation getModelLocation() {
        return new ModelResourceLocation("");
    }

    //endregion
}
