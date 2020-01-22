package com.brandon3055.draconicevolution.items.armor;

import com.brandon3055.brandonscore.BrandonsCore;
import com.brandon3055.brandonscore.api.IFOVModifierItem;
import com.brandon3055.brandonscore.lib.EnergyContainerWrapper;
import com.brandon3055.brandonscore.utils.InfoHelper;
import com.brandon3055.brandonscore.utils.ItemNBTHelper;
import com.brandon3055.draconicevolution.DEConfig;
import com.brandon3055.draconicevolution.api.itemconfig.*;
import com.brandon3055.draconicevolution.api.itemupgrade.IUpgradableItem;
import com.brandon3055.draconicevolution.api.itemupgrade.UpgradeHelper;
import com.brandon3055.draconicevolution.client.model.ModelWyvernArmor;
import com.brandon3055.draconicevolution.entity.EntityPersistentItem;
import com.brandon3055.draconicevolution.items.ToolUpgrade;
import com.brandon3055.draconicevolution.items.tools.ToolBase;
import com.brandon3055.draconicevolution.items.tools.ToolStats;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static com.brandon3055.draconicevolution.api.itemconfig.IItemConfigField.EnumControlType.SLIDER;
import static com.brandon3055.draconicevolution.handlers.CustomArmorHandler.WALK_SPEED_UUID;

/**
 * Created by brandon3055 on 6/06/2016.
 */
public class WyvernArmor extends ItemArmor implements IConfigurableItem, IUpgradableItem, ICustomArmor, IFOVModifierItem {

    private static ArmorMaterial wyvernMaterial = EnumHelper.addArmorMaterial("wyvernArmor", "draconicevolution:wyvern_armor", -1, new int[]{3, 6, 8, 3}, 15, SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND, 2.0F);

    public WyvernArmor(int renderIndexIn, EntityEquipmentSlot equipmentSlotIn) {
        super(wyvernMaterial, renderIndexIn, equipmentSlotIn);
        this.setMaxDamage(-1);
    }

    public WyvernArmor(ArmorMaterial materialIn, int renderIndexIn, EntityEquipmentSlot equipmentSlotIn) {
        super(materialIn, renderIndexIn, equipmentSlotIn);
        this.setMaxDamage(-1);
    }

    //region Armor Logic

    @Override
    public boolean isDamageable() {
        return false;
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems) {
        if (isInCreativeTab(tab)) {
            subItems.add(new ItemStack(this));
            ItemStack stack = new ItemStack(this);
            modifyEnergy(stack, getCapacity(stack));
            subItems.add(stack);

            ItemStack uberStack = new ItemStack(this);

            for (String upgrade : getValidUpgrades(uberStack)) {
                UpgradeHelper.setUpgradeLevel(uberStack, upgrade, getMaxUpgradeLevel(uberStack, upgrade));
            }

            modifyEnergy(uberStack, getCapacity(uberStack));
            subItems.add(uberStack);
        }
    }

    protected float getProtectionShare() {
        switch (armorType) {
            case HEAD:
                return 0.15F;
            case CHEST:
                return 0.40F;
            case LEGS:
                return 0.30F;
            case FEET:
                return 0.15F;
        }
        return 0;
    }

    //endregion

    //region Config

    @Override
    public int getProfileCount(ItemStack stack) {
        return 3;
    }

    @Override
    public ItemConfigFieldRegistry getFields(ItemStack stack, ItemConfigFieldRegistry registry) {
        if (armorType == EntityEquipmentSlot.LEGS) {
            int u = UpgradeHelper.getUpgradeLevel(stack, ToolUpgrade.MOVE_SPEED);
            int i = 100 + (100 * u) + (Math.max(u - 1, 0) * 50);
            registry.register(stack, new IntegerConfigField("armorSpeedModifier", 0, 0, i, "config.field.armorSpeedModifier.description", SLIDER).setPrefix("+").setExtension("%"));
            registry.register(stack, new BooleanConfigField("armorSpeedFOVWarp", false, "config.field.armorSpeedFOVWarp.description"));
        }
        if (armorType == EntityEquipmentSlot.FEET) {
            int u = UpgradeHelper.getUpgradeLevel(stack, ToolUpgrade.JUMP_BOOST);
            int i = 100 + (100 * u) + (Math.max(u - 1, 0) * 50);
            registry.register(stack, new IntegerConfigField("armorJumpModifier", 0, 0, i, "config.field.armorSpeedModifier.description", SLIDER).setPrefix("+").setExtension("%"));
        }
        if (armorType == EntityEquipmentSlot.FEET || armorType == EntityEquipmentSlot.LEGS) {
            registry.register(stack, new BooleanConfigField("sprintBoost", false, "config.field.sprintBoost.description"));
        }

        return registry;
    }

    //endregion

    //region Upgrade

    @Override
    public List<String> getValidUpgrades(ItemStack stack) {
        ArrayList<String> list = new ArrayList<String>();
        list.add(ToolUpgrade.RF_CAPACITY);

        if (armorType == EntityEquipmentSlot.FEET) {
            list.add(ToolUpgrade.JUMP_BOOST);
        }
        if (armorType == EntityEquipmentSlot.LEGS) {
            list.add(ToolUpgrade.MOVE_SPEED);
        }

        list.add(ToolUpgrade.SHIELD_CAPACITY);
        list.add(ToolUpgrade.SHIELD_RECOVERY);
        return list;
    }

    @Override
    public int getMaxUpgradeLevel(ItemStack stack, String upgrade) {
        return 2;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World playerIn, List<String> tooltip, ITooltipFlag advanced) {
        ToolBase.holdCTRLForUpgrades(tooltip, stack);
        InfoHelper.addEnergyInfo(stack, tooltip);
    }

    //endregion

    //region Render

    @Override
    public boolean hasEffect(ItemStack stack) {
        return false;
    }

    @SideOnly(Side.CLIENT)
    public ModelWyvernArmor model;

    @SideOnly(Side.CLIENT)
    @Override
    public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, EntityEquipmentSlot armorSlot, ModelBiped _default) {
        if (DEConfig.disable3DModels) {
            return super.getArmorModel(entityLiving, itemStack, armorSlot, _default);
        }

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

        model.entityModel = _default;

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

    //region ICustomArmor

    @Override
    public float getProtectionPoints(ItemStack stack) {
        int upgradeLevel = UpgradeHelper.getUpgradeLevel(stack, ToolUpgrade.SHIELD_CAPACITY);
        float points = ToolStats.WYVERN_BASE_SHIELD_CAPACITY * getProtectionShare() * (upgradeLevel + 1);
        return points;
    }

    @Override
    public float getRecoveryRate(ItemStack stack) {
        return (float) ToolStats.WYVERN_SHIELD_RECOVERY * (1F + UpgradeHelper.getUpgradeLevel(stack, ToolUpgrade.SHIELD_RECOVERY));//TODO Balance
    }

    @Override
    public float getSpeedModifier(ItemStack stack, EntityPlayer player) {
        float modifier = ToolConfigHelper.getIntegerField("armorSpeedModifier", stack) / 100F;

        if (ToolConfigHelper.getBooleanField("sprintBoost", stack) && !player.isSprinting()) {
            modifier /= 5F;
        }

        return modifier;
    }

    @Override
    public float getJumpModifier(ItemStack stack, EntityPlayer player) {
        float modifier = ToolConfigHelper.getIntegerField("armorJumpModifier", stack) / 100F;

        if (ToolConfigHelper.getBooleanField("sprintBoost", stack) && !BrandonsCore.proxy.isSprintKeyDown()) {
            modifier /= 5F;
        }

        return modifier;
    }

    @Override
    public boolean hasHillStep(ItemStack stack, EntityPlayer player) {
        return false;
    }

    @Override
    public float getFireResistance(ItemStack stack) {
        switch (armorType) {
            case HEAD:
                return 0.15F;
            case CHEST:
                return 0.40F;
            case LEGS:
                return 0.30F;
            case FEET:
                return 0.15F;
        }
        return 0;
    }

    @Override
    public boolean[] hasFlight(ItemStack stack) {
        return new boolean[]{false, false, false};
    }

    @Override
    public float getFlightSpeedModifier(ItemStack stack, EntityPlayer player) {
        return 0;
    }

    @Override
    public float getFlightVModifier(ItemStack stack, EntityPlayer player) {
        return 0;
    }

    @Override
    public int getEnergyPerProtectionPoint() {
        return ToolStats.WYVERN_SHIELD_RECHARGE_COST;
    }

    //endregion

    //region Energy

    protected int getCapacity(ItemStack stack) {
        int level = UpgradeHelper.getUpgradeLevel(stack, ToolUpgrade.RF_CAPACITY);

        if (level == 0) {
            return ToolStats.WYVERN_BASE_CAPACITY;
        }
        else {
            return ToolStats.WYVERN_BASE_CAPACITY * (int) Math.pow(2, level + 1);
        }
    }

    protected int getMaxReceive(ItemStack stack) {
        return ToolStats.WYVERN_MAX_RECIEVE;
    }

    @Override
    public int receiveEnergy(ItemStack container, int maxReceive, boolean simulate) {
        int energy = ItemNBTHelper.getInteger(container, "Energy", 0);
        int energyReceived = Math.min(getCapacity(container) - energy, Math.min(getMaxReceive(container), maxReceive));

        if (!simulate) {
            energy += energyReceived;
            ItemNBTHelper.setInteger(container, "Energy", energy);
        }

        return energyReceived;
    }

    @Override
    public int extractEnergy(ItemStack container, int maxExtract, boolean simulate) {
        return 0;
    }

    @Override
    public int getEnergyStored(ItemStack container) {
        return ItemNBTHelper.getInteger(container, "Energy", 0);
    }

    @Override
    public int getMaxEnergyStored(ItemStack container) {
        return getCapacity(container);
    }

    @Override
    public void modifyEnergy(ItemStack container, int modify) {
        int energy = ItemNBTHelper.getInteger(container, "Energy", 0);
        energy += modify;

        if (energy > getCapacity(container)) {
            energy = getCapacity(container);
        }
        else if (energy < 0) {
            energy = 0;
        }

        ItemNBTHelper.setInteger(container, "Energy", energy);
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return !(getEnergyStored(stack) == getMaxEnergyStored(stack));
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        return 1D - ((double) getEnergyStored(stack) / (double) getMaxEnergyStored(stack));
    }

    @Override
    public ICapabilityProvider initCapabilities(final ItemStack stack, NBTTagCompound nbt) {
        return new EnergyContainerWrapper(stack);
    }

    //endregion

    @Override
    public float getNewFOV(EntityPlayer player, ItemStack stack, float currentFOV, float originalFOV, EntityEquipmentSlot slot) {
        if (DEConfig.disableArmorFOVCorrection) {
            return currentFOV;
        }
        AttributeModifier modifier = player.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getModifier(WALK_SPEED_UUID);
        if (slot == EntityEquipmentSlot.LEGS && modifier != null) {
            boolean fly = player.capabilities.isFlying;
            float speedModifier = (float) modifier.getAmount() / (player.isSprinting() ? (fly ? 1.35F : 1.5F) : (fly ? 1.8181818F : 2F));
            float newFov = (currentFOV - speedModifier); //Completely remove the fov effect added by the speed modifier

            if (!ToolConfigHelper.getBooleanField("armorSpeedFOVWarp", stack)) {
                newFov += speedModifier * 0.25F; //Re apply 25% of what vanilla would normally apply
            }

            //Just in case some other mod has already removed the fov modifier. Would not want to decrease the fov further.
            if (newFov < 1F && player.getActivePotionEffect(MobEffects.SLOWNESS) == null) {
                newFov = 1F;
            }

            return newFov;
        }

        return currentFOV;
    }

    @Override
    public boolean hasCustomEntity(ItemStack stack) {
        return true;
    }

    @Override
    public Entity createEntity(World world, Entity location, ItemStack itemstack) {
        return new EntityPersistentItem(world, location, itemstack);
    }

}
