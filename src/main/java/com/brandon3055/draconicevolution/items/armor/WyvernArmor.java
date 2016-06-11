package com.brandon3055.draconicevolution.items.armor;

import com.brandon3055.brandonscore.config.Feature;
import com.brandon3055.brandonscore.config.ICustomRender;
import com.brandon3055.draconicevolution.api.itemconfig.IConfigurableItem;
import com.brandon3055.draconicevolution.api.itemconfig.ItemConfigFieldRegistry;
import com.brandon3055.draconicevolution.api.itemupgrade.IUpgradableItem;
import com.brandon3055.draconicevolution.api.itemupgrade.ItemUpgradeRegistry;
import com.brandon3055.draconicevolution.api.itemupgrade.SimpleUpgrade;
import com.brandon3055.draconicevolution.api.itemupgrade.UpgradeHelper;
import com.brandon3055.draconicevolution.client.model.IDualModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

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
    public ItemUpgradeRegistry getValidUpgrades(ItemStack stack, ItemUpgradeRegistry upgradeRegistry) {
        upgradeRegistry.register(stack, new SimpleUpgrade(UpgradeHelper.RF_CAPACITY));
        upgradeRegistry.register(stack, new SimpleUpgrade(UpgradeHelper.JUMP_BOOST));
        upgradeRegistry.register(stack, new SimpleUpgrade(UpgradeHelper.MOVE_SPEED));
        upgradeRegistry.register(stack, new SimpleUpgrade(UpgradeHelper.SHIELD_CAPACITY));
        upgradeRegistry.register(stack, new SimpleUpgrade(UpgradeHelper.SHIELD_RECOVERY));
        return upgradeRegistry;
    }

    @Override
    public int getUpgradeCapacity(ItemStack stack) {
        return 3;
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
