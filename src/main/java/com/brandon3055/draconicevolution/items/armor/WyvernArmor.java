package com.brandon3055.draconicevolution.items.armor;

import com.brandon3055.brandonscore.config.Feature;
import com.brandon3055.brandonscore.config.ICustomRender;
import com.brandon3055.draconicevolution.api.itemconfig.IConfigurableItem;
import com.brandon3055.draconicevolution.api.itemconfig.ItemConfigFieldRegistry;
import com.brandon3055.draconicevolution.api.itemupgrade.IUpgradableItem;
import com.brandon3055.draconicevolution.client.model.IDualModel;
import com.brandon3055.draconicevolution.client.model.ModelWyvernArmor;
import com.brandon3055.draconicevolution.items.ToolUpgrade;
import com.brandon3055.draconicevolution.items.tools.ToolBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by brandon3055 on 6/06/2016.
 */
public class WyvernArmor extends ItemArmor implements ICustomRender, IDualModel, IConfigurableItem, IUpgradableItem {

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

    //region Render


    @Override
    public boolean hasEffect(ItemStack stack) {
        return false;
    }

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

    @SideOnly(Side.CLIENT)
    private ModelBiped model;

    @Override
    public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, EntityEquipmentSlot armorSlot, ModelBiped _default) {
        //return super.getArmorModel(entityLiving, itemStack, armorSlot, _default);

       if (entityLiving != null && !entityLiving.onGround)model = null;

        if (model == null) {
            if (armorType == EntityEquipmentSlot.HEAD) model = new ModelWyvernArmor(0.5F, true, false, false, false);
            else if (armorType == EntityEquipmentSlot.CHEST) model = new ModelWyvernArmor(1.5F, false, true, false, false);
            else if (armorType == EntityEquipmentSlot.LEGS) model = new ModelWyvernArmor(1.5F, false, false, true, false);
            else model = new ModelWyvernArmor(1F, false, false, false, true);
            this.model.bipedHead.showModel = (armorType == EntityEquipmentSlot.HEAD);
            this.model.bipedHeadwear.showModel = (armorType == EntityEquipmentSlot.HEAD);
            this.model.bipedBody.showModel = ((armorType == EntityEquipmentSlot.CHEST) || (armorType == EntityEquipmentSlot.LEGS));
            this.model.bipedLeftArm.showModel = (armorType == EntityEquipmentSlot.CHEST);
            this.model.bipedRightArm.showModel = (armorType == EntityEquipmentSlot.CHEST);
            this.model.bipedLeftLeg.showModel = (armorType == EntityEquipmentSlot.LEGS || armorType == EntityEquipmentSlot.FEET);
            this.model.bipedRightLeg.showModel = (armorType == EntityEquipmentSlot.LEGS || armorType == EntityEquipmentSlot.FEET);
        }


        if (entityLiving == null) {
            return model;
        }

        this.model.isSneak = entityLiving.isSneaking();
        this.model.isRiding = entityLiving.isRiding();
        this.model.isChild = entityLiving.isChild();
   //     this.model.aimedBow = false;
   //     this.model.heldItemRight = (entityLiving.getHeldItem() != null ? 1 : 0);

//                 this.model.bipedHead.showModel = (armorType == EntityEquipmentSlot.HEAD);
        this.model.bipedHeadwear.showModel = (armorType == EntityEquipmentSlot.HEAD);
        this.model.bipedBody.showModel = ((armorType == EntityEquipmentSlot.CHEST) || (armorType == EntityEquipmentSlot.LEGS));
        this.model.bipedLeftArm.showModel = (armorType == EntityEquipmentSlot.CHEST);
        this.model.bipedRightArm.showModel = (armorType == EntityEquipmentSlot.CHEST);
        this.model.bipedLeftLeg.showModel = (armorType == EntityEquipmentSlot.LEGS || armorType == EntityEquipmentSlot.FEET);
        this.model.bipedRightLeg.showModel = (armorType == EntityEquipmentSlot.LEGS || armorType == EntityEquipmentSlot.FEET);


        return model;
    }

    //endregion
}
