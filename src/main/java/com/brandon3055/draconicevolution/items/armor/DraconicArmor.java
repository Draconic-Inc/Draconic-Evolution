package com.brandon3055.draconicevolution.items.armor;

import com.brandon3055.draconicevolution.client.model.ModelDraconicArmor;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by brandon3055 on 6/06/2016.
 */
public class DraconicArmor extends WyvernArmor {

    public DraconicArmor(ArmorMaterial materialIn, int renderIndexIn, EntityEquipmentSlot equipmentSlotIn) {
        super(materialIn, renderIndexIn, equipmentSlotIn);
    }


    //region Upgrade

    @Override
    public int getMaxUpgradeLevel(ItemStack stack) {
        return 3;
    }


//    @Override
//    public int getUpgradeCapacity(ItemStack stack) {
//        return 6;
//    }
//
//    @Override
//    public ItemUpgradeRegistry getValidUpgrades(ItemStack stack, ItemUpgradeRegistry upgradeRegistry) {
//        super.getValidUpgrades(stack, upgradeRegistry);
//
//        //todo modify max tier somehow. Maby just re add everything. Or get them from the registry and modify them
//
//        return upgradeRegistry;
//    }

    //endregion


    //region Rendering

    @SideOnly(Side.CLIENT)
    private ModelBiped model;

    @Override
    public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, EntityEquipmentSlot armorSlot, ModelBiped _default) {
        //return super.getArmorModel(entityLiving, itemStack, armorSlot, _default);

        if (entityLiving != null && !entityLiving.onGround)model = null;

        if (model == null) {
            if (armorType == EntityEquipmentSlot.HEAD) model = new ModelDraconicArmor(1F, true, false, false, false);
            else if (armorType == EntityEquipmentSlot.CHEST) model = new ModelDraconicArmor(1F, false, true, false, false);
            else if (armorType == EntityEquipmentSlot.LEGS) model = new ModelDraconicArmor(1F, false, false, true, false);
            else model = new ModelDraconicArmor(1F, false, false, false, true);
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
