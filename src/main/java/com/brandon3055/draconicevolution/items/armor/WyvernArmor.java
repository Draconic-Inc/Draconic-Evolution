package com.brandon3055.draconicevolution.items.armor;

import com.brandon3055.brandonscore.config.Feature;
import com.brandon3055.brandonscore.config.ICustomRender;
import com.brandon3055.draconicevolution.api.itemconfig.IConfigurableItem;
import com.brandon3055.draconicevolution.api.itemconfig.ItemConfigFieldRegistry;
import com.brandon3055.draconicevolution.api.itemupgrade.IUpgradableItem;
import com.brandon3055.draconicevolution.client.model.IDualModel;
import com.brandon3055.draconicevolution.items.ToolUpgrade;
import com.brandon3055.draconicevolution.items.tools.ToolBase;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

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
    public List<String> getValidUpgrades(ItemStack stack) {
        ArrayList<String> list = new ArrayList<String>();
        list.add(ToolUpgrade.RF_CAPACITY);
        list.add(ToolUpgrade.JUMP_BOOST);
        list.add(ToolUpgrade.MOVE_SPEED);
        list.add(ToolUpgrade.SHIELD_CAPACITY);
        list.add(ToolUpgrade.SHIELD_RECOVERY);
        return list;
    }

    @Override
    public int getMaxUpgradeLevel(ItemStack stack) {
        return 2;
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
        ToolBase.holdCTRLForUpgrades(tooltip, stack);
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
