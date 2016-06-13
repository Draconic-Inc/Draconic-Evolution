package com.brandon3055.draconicevolution.common.items.armor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.brandon3055.brandonscore.BrandonsCore;
import com.brandon3055.brandonscore.common.utills.InfoHelper;
import com.brandon3055.brandonscore.common.utills.ItemNBTHelper;
import com.brandon3055.brandonscore.common.utills.Utills;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.client.model.ModelDraconicArmor;
import com.brandon3055.draconicevolution.client.model.ModelDraconicArmorOld;
import com.brandon3055.draconicevolution.common.ModItems;
import com.brandon3055.draconicevolution.common.entity.EntityPersistentItem;
import com.brandon3055.draconicevolution.common.handler.BalanceConfigHandler;
import com.brandon3055.draconicevolution.common.handler.ConfigHandler;
import com.brandon3055.draconicevolution.common.items.tools.baseclasses.ToolBase;
import com.brandon3055.draconicevolution.common.lib.References;
import com.brandon3055.draconicevolution.common.utills.IConfigurableItem;
import com.brandon3055.draconicevolution.common.utills.IInventoryTool;
import com.brandon3055.draconicevolution.common.utills.IUpgradableItem;
import com.brandon3055.draconicevolution.common.utills.ItemConfigField;
import com.brandon3055.draconicevolution.integration.ModHelper;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Optional;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.ReflectionHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.ISpecialArmor;
import thaumcraft.api.IGoggles;
import thaumcraft.api.nodes.IRevealer;

/**
 * Created by Brandon on 3/07/2014.
 */
@Optional.InterfaceList(value = {@Optional.Interface(iface = "thaumcraft.api.IGoggles", modid = "Thaumcraft"), @Optional.Interface(iface = "thaumcraft.api.nodes.IRevealer", modid = "Thaumcraft")})
public class DraconicArmor extends ItemArmor implements ISpecialArmor, IConfigurableItem, IInventoryTool, IGoggles, IRevealer, IUpgradableItem, ICustomArmor {//TODO Wings
    @SideOnly(Side.CLIENT)
    private IIcon helmIcon;
    @SideOnly(Side.CLIENT)
    private IIcon chestIcon;
    @SideOnly(Side.CLIENT)
    private IIcon leggsIcon;
    @SideOnly(Side.CLIENT)
    private IIcon bootsIcon;

    private int maxEnergy = BalanceConfigHandler.draconicArmorBaseStorage;
    private int maxTransfer = BalanceConfigHandler.draconicArmorMaxTransfer;

    public DraconicArmor(ArmorMaterial material, int armorType, String name) {
        super(material, 0, armorType);
        this.setUnlocalizedName(name);
        this.setCreativeTab(DraconicEvolution.tabToolsWeapons);
        if (ModItems.isEnabled(this)) GameRegistry.registerItem(this, name);
    }

    @Override
    public boolean isItemTool(ItemStack p_77616_1_) {
        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void getSubItems(Item item, CreativeTabs p_150895_2_, List list) {
        list.add(ItemNBTHelper.setInteger(new ItemStack(item), "Energy", 0));
        list.add(ItemNBTHelper.setInteger(new ItemStack(item), "Energy", maxEnergy));
    }

    @Override
    public String getUnlocalizedName() {

        return String.format("item.%s%s", References.MODID.toLowerCase() + ":", super.getUnlocalizedName().substring(super.getUnlocalizedName().indexOf(".") + 1));
    }

    @Override
    public String getUnlocalizedName(final ItemStack itemStack) {
        return getUnlocalizedName();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister) {
        helmIcon = iconRegister.registerIcon(References.RESOURCESPREFIX + "draconic_helmet");
        chestIcon = iconRegister.registerIcon(References.RESOURCESPREFIX + "draconic_chestplate");
        leggsIcon = iconRegister.registerIcon(References.RESOURCESPREFIX + "draconic_leggings");
        bootsIcon = iconRegister.registerIcon(References.RESOURCESPREFIX + "draconic_boots");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(ItemStack stack, int renderPass, EntityPlayer player, ItemStack usingItem, int useRemaining) {
        if (stack.getItem() == ModItems.draconicHelm) return helmIcon;
        else if (stack.getItem() == ModItems.draconicChest) return chestIcon;
        else if (stack.getItem() == ModItems.draconicLeggs) return leggsIcon;
        else return bootsIcon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconIndex(ItemStack stack) {
        if (stack.getItem() == ModItems.draconicHelm) return helmIcon;
        else if (stack.getItem() == ModItems.draconicChest) return chestIcon;
        else if (stack.getItem() == ModItems.draconicLeggs) return leggsIcon;
        else return bootsIcon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type) {
        if (!ConfigHandler.useOldArmorModel)
            return References.RESOURCESPREFIX + "textures/models/armor/armorDraconic.png";
        if (stack.getItem() == ModItems.draconicHelm || stack.getItem() == ModItems.draconicChest || stack.getItem() == ModItems.draconicBoots) {
            return References.RESOURCESPREFIX + "textures/models/armor/draconic_layer_1.png";
        } else {
            return References.RESOURCESPREFIX + "textures/models/armor/draconic_layer_2.png";
        }
    }

    @Override
    public EnumRarity getRarity(ItemStack p_77613_1_) {
        return EnumRarity.epic;
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        return 1D - (double) ItemNBTHelper.getInteger(stack, "Energy", 0) / (double) getMaxEnergyStored(stack);
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return getEnergyStored(stack) < getMaxEnergyStored(stack);
    }

    protected float getProtectionShare() {
        switch (armorType) {
            case 0:
                return 0.15F;
            case 1:
                return 0.40F;
            case 2:
                return 0.30F;
            case 3:
                return 0.15F;
        }
        return 0;
    }

    //region ISpecialArmor
    @Override
    public ArmorProperties getProperties(EntityLivingBase player, ItemStack armor, DamageSource source, double damage, int slot) {
        if (source.isUnblockable() || source.isDamageAbsolute() || source.isMagicDamage())
            return new ArmorProperties(0, damageReduceAmount / 100D, 15);
        return new ArmorProperties(0, damageReduceAmount / 24.5D, 1000);
    }

    @Override
    public int getArmorDisplay(EntityPlayer player, ItemStack armor, int slot) {
        return (int) (getProtectionShare() * 20D);
    }

    @Override
    public void damageArmor(EntityLivingBase entity, ItemStack stack, DamageSource source, int damage, int slot) {
    }
    //endregion

    @Override
    public void onArmorTick(World world, EntityPlayer player, ItemStack stack) {
        if (stack == null) return;
        if (stack.getItem() == ModItems.draconicHelm) {
            if (world.isRemote) return;
            if (this.getEnergyStored(stack) >= BalanceConfigHandler.draconicArmorEnergyToRemoveEffects && clearNegativeEffects(player)) {
                this.extractEnergy(stack, BalanceConfigHandler.draconicArmorEnergyToRemoveEffects, false);
            }
            if (player.worldObj.getBlockLightValue((int) Math.floor(player.posX), (int) player.posY + 1, (int) Math.floor(player.posZ)) < 5 && IConfigurableItem.ProfileHelper.getBoolean(stack, "ArmorNVActive", false)) {
                player.addPotionEffect(new PotionEffect(Potion.nightVision.id, 419, 0, true));
            } else if (IConfigurableItem.ProfileHelper.getBoolean(stack, "ArmorNVActive", false) && IConfigurableItem.ProfileHelper.getBoolean(stack, "ArmorNVLock", true))
                player.addPotionEffect(new PotionEffect(Potion.nightVision.id, 419, 0, true));
            else if (player.isPotionActive(Potion.nightVision.id)) player.removePotionEffect(Potion.nightVision.id);

        }
    }

    @SuppressWarnings("unchecked")
    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, EntityPlayer par2EntityPlayer, List list, boolean par4) {
        InfoHelper.addEnergyAndLore(stack, list);
        ToolBase.holdCTRLForUpgrades(list, stack);
    }

    @SuppressWarnings("unchecked")
    public boolean clearNegativeEffects(Entity par3Entity) {
        boolean flag = false;
        if (par3Entity.ticksExisted % 20 == 0) {
            if (par3Entity instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) par3Entity;

                Collection<PotionEffect> potions = player.getActivePotionEffects();

                if (player.isBurning()) {
                    player.extinguish();
                    /*
					* Wyvern Armor doesn't require energy to extinguish player => Draconic Armor shouldn't require.
					* See CustomArmorHandler.applyArmorDamageBlocking
					* */
                    //flag = false;
                }
                for (PotionEffect potion : potions) {
                    int id = potion.getPotionID();
                    if (ReflectionHelper.getPrivateValue(Potion.class, Potion.potionTypes[id], new String[]{"isBadEffect", "field_76418_K", "J"})) {
                        if (potion.getPotionID() == Potion.digSlowdown.id && ModHelper.isHoldingCleaver(player)) break;
                        if ((player.getHeldItem() == null || (player.getHeldItem().getItem() != ModItems.wyvernBow && player.getHeldItem().getItem() != ModItems.draconicBow)) || id != 2) {
                            player.removePotionEffect(id);
                            flag = true;
                        }
                        break;
                    }
                }
            }
        }
        return flag;
    }

    /* IEnergyContainerItem */
    @Override
    public int receiveEnergy(ItemStack container, int maxReceive, boolean simulate) {
        int stored = ItemNBTHelper.getInteger(container, "Energy", 0);
        int receive = Math.min(maxReceive, Math.min(getMaxEnergyStored(container) - stored, maxTransfer));

        if (!simulate) {
            stored += receive;
            ItemNBTHelper.setInteger(container, "Energy", stored);
        }
        return receive;
    }

    @Override
    public int extractEnergy(ItemStack container, int maxExtract, boolean simulate) {

        int stored = ItemNBTHelper.getInteger(container, "Energy", 0);
        int extract = Math.min(maxExtract, Math.min(maxTransfer, stored));

        if (!simulate) {
            stored -= extract;
            ItemNBTHelper.setInteger(container, "Energy", stored);
        }
        return extract;
    }

    @Override
    public int getEnergyStored(ItemStack container) {
        return ItemNBTHelper.getInteger(container, "Energy", 0);
    }

    @Override
    public int getMaxEnergyStored(ItemStack container) {
        int points = IUpgradableItem.EnumUpgrade.RF_CAPACITY.getUpgradePoints(container);
        return BalanceConfigHandler.draconicArmorBaseStorage + points * BalanceConfigHandler.draconicArmorStoragePerUpgrade;
    }

    /* Misc */
    @Override
    public Entity createEntity(World world, Entity location, ItemStack itemstack) {
        return new EntityPersistentItem(world, location, itemstack);
    }

    @Override
    public boolean hasCustomEntity(ItemStack stack) {
        return true;
    }

    @Override
    public List<ItemConfigField> getFields(ItemStack stack, int slot) {
        List<ItemConfigField> list = new ArrayList<ItemConfigField>();
        if (armorType == 0) {
            list.add(new ItemConfigField(References.BOOLEAN_ID, slot, "ArmorNVActive").readFromItem(stack, false));
            list.add(new ItemConfigField(References.BOOLEAN_ID, slot, "ArmorNVLock").readFromItem(stack, true));
            if (Loader.isModLoaded("Thaumcraft"))
                list.add(new ItemConfigField(References.BOOLEAN_ID, slot, "GogglesOfRevealing").readFromItem(stack, true));
        } else if (armorType == 1) {
            list.add(new ItemConfigField(References.FLOAT_ID, slot, "VerticalAcceleration").setMinMaxAndIncromente(0f, 8f, 0.1f).readFromItem(stack, 0F).setModifier("PLUSPERCENT"));
            list.add(new ItemConfigField(References.FLOAT_ID, slot, "ArmorFlightSpeedMult").setMinMaxAndIncromente(0f, 6f, 0.1f).readFromItem(stack, 0F).setModifier("PLUSPERCENT"));
            list.add(new ItemConfigField(References.BOOLEAN_ID, slot, "EffectiveOnSprint").readFromItem(stack, false));
            list.add(new ItemConfigField(References.BOOLEAN_ID, slot, "ArmorFlightLock").readFromItem(stack, false));
            list.add(new ItemConfigField(References.BOOLEAN_ID, slot, "ArmorInertiaCancellation").readFromItem(stack, false));
        } else if (armorType == 2) {
            list.add(new ItemConfigField(References.FLOAT_ID, slot, "ArmorSpeedMult").setMinMaxAndIncromente(0f, 8f, 0.1f).readFromItem(stack, 0F).setModifier("PLUSPERCENT"));
            list.add(new ItemConfigField(References.BOOLEAN_ID, slot, "ArmorSprintOnly").readFromItem(stack, false));
        } else if (armorType == 3) {
            list.add(new ItemConfigField(References.FLOAT_ID, slot, "ArmorJumpMult").setMinMaxAndIncromente(0f, 15f, 0.1f).readFromItem(stack, 0f).setModifier("PLUSPERCENT"));
            list.add(new ItemConfigField(References.BOOLEAN_ID, slot, "ArmorSprintOnly").readFromItem(stack, false));
            list.add(new ItemConfigField(References.BOOLEAN_ID, slot, "ArmorHillStep").readFromItem(stack, true));
        }
        return list;
    }

    @Override
    public String getInventoryName() {
        return StatCollector.translateToLocal("info.de.toolInventoryEnch.txt");
    }

    @Override
    public int getInventorySlots() {
        return 0;
    }

    @Override
    public boolean isEnchantValid(Enchantment enchant) {
        return enchant.type == EnumEnchantmentType.armor || (armorType == 0 && enchant.type == EnumEnchantmentType.armor_head) || (armorType == 1 && enchant.type == EnumEnchantmentType.armor_torso) || (armorType == 2 && enchant.type == EnumEnchantmentType.armor_legs) || (armorType == 3 && enchant.type == EnumEnchantmentType.armor_feet);
    }

    @Optional.Method(modid = "Thaumcraft")
    @Override
    public boolean showIngamePopups(ItemStack itemstack, EntityLivingBase player) {
        return IConfigurableItem.ProfileHelper.getBoolean(itemstack, "GogglesOfRevealing", true);
    }

    @Optional.Method(modid = "Thaumcraft")
    @Override
    public boolean showNodes(ItemStack itemstack, EntityLivingBase player) {
        return IConfigurableItem.ProfileHelper.getBoolean(itemstack, "GogglesOfRevealing", true);
    }

    @SideOnly(Side.CLIENT)
    public ModelBiped model;

    @SideOnly(Side.CLIENT)
    @Override
    public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, int armorSlot) {
        if (ConfigHandler.useOldArmorModel) return super.getArmorModel(entityLiving, itemStack, armorSlot);

        if (!ConfigHandler.useOriginal3DArmorModel) {
            if (model == null) {
                if (armorType == 0) model = new ModelDraconicArmor(1.1F, true, false, false, false);
                else if (armorType == 1) model = new ModelDraconicArmor(1.1F, false, true, false, false);
                else if (armorType == 2) model = new ModelDraconicArmor(1.1F, false, false, true, false);
                else model = new ModelDraconicArmor(1.1F, false, false, false, true);
                this.model.bipedHead.showModel = (armorType == 0);
                this.model.bipedHeadwear.showModel = (armorType == 0);
                this.model.bipedBody.showModel = ((armorType == 1) || (armorType == 2));
                this.model.bipedLeftArm.showModel = (armorType == 1);
                this.model.bipedRightArm.showModel = (armorType == 1);
                this.model.bipedLeftLeg.showModel = (armorType == 2 || armorType == 3);
                this.model.bipedRightLeg.showModel = (armorType == 2 || armorType == 3);
            }
        } else {
            if (model == null) {
                if (armorType == 0) model = new ModelDraconicArmorOld(1.1F, true, false, false, false, true);
                else if (armorType == 1) model = new ModelDraconicArmorOld(1.1F, false, true, false, false, true);
                else if (armorType == 2) model = new ModelDraconicArmorOld(1.1F, false, false, true, false, true);
                else model = new ModelDraconicArmorOld(1.1F, false, false, false, true, true);
                this.model.bipedHead.showModel = (armorType == 0);
                this.model.bipedHeadwear.showModel = (armorType == 0);
                this.model.bipedBody.showModel = ((armorType == 1) || (armorType == 2));
                this.model.bipedLeftArm.showModel = (armorType == 1);
                this.model.bipedRightArm.showModel = (armorType == 1);
                this.model.bipedLeftLeg.showModel = (armorType == 2 || armorType == 3);
                this.model.bipedRightLeg.showModel = (armorType == 2 || armorType == 3);
            }
        }

        if (entityLiving == null) return model;

        this.model.isSneak = entityLiving.isSneaking();
        this.model.isRiding = entityLiving.isRiding();
        this.model.isChild = entityLiving.isChild();
        this.model.aimedBow = false;
        this.model.heldItemRight = (entityLiving.getHeldItem() != null ? 1 : 0);

        if ((entityLiving instanceof EntityPlayer)) {
            if (((EntityPlayer) entityLiving).getItemInUseDuration() > 0) {
                EnumAction enumaction = ((EntityPlayer) entityLiving).getItemInUse().getItemUseAction();
                if (enumaction == EnumAction.block) {
                    this.model.heldItemRight = 3;
                } else if (enumaction == EnumAction.bow) {
                    this.model.aimedBow = true;
                }
            }
        }


        return model;
    }

    @Override
    public List<EnumUpgrade> getUpgrades(ItemStack itemstack) {
        return new ArrayList<EnumUpgrade>() {{
            add(EnumUpgrade.RF_CAPACITY);
            add(EnumUpgrade.SHIELD_CAPACITY);
            add(EnumUpgrade.SHIELD_RECOVERY);
            //	if (armorType == 2) add(EnumUpgrade.MOVE_SPEED);
            //	if (armorType == 3) add(EnumUpgrade.JUMP_BOOST);
        }};
    }

    @Override
    public int getUpgradeCap(ItemStack itemstack) {
        return BalanceConfigHandler.draconicArmorMaxUpgrades;
    }

    @Override
    public int getMaxTier(ItemStack itemstack) {
        return 2;
    }

    @Override
    public List<String> getUpgradeStats(ItemStack stack) {
        List<String> strings = new ArrayList<String>();

        strings.add(InfoHelper.ITC() + StatCollector.translateToLocal("gui.de.RFCapacity.txt") + ": " + InfoHelper.HITC() + Utills.formatNumber(getMaxEnergyStored(stack)));
        strings.add(InfoHelper.ITC() + StatCollector.translateToLocal("gui.de.ShieldCapacity.txt") + ": " + InfoHelper.HITC() + (int) getProtectionPoints(stack));
        strings.add(InfoHelper.ITC() + StatCollector.translateToLocal("gui.de.ShieldRecovery.txt") + ": " + InfoHelper.HITC() + Utills.round(getRecoveryPoints(stack) * 0.2D, 10) + " EPS");

        return strings;
    }

    @Override
    public int getMaxUpgradePoints(int upgradeIndex) {
        if (upgradeIndex == EnumUpgrade.RF_CAPACITY.index) {
            return BalanceConfigHandler.draconicArmorMaxCapacityUpgradePoints;
        }
        return BalanceConfigHandler.draconicArmorMaxUpgradePoints;
    }

    @Override
    public int getMaxUpgradePoints(int upgradeIndex, ItemStack stack) {
        return getMaxUpgradePoints(upgradeIndex);
    }

    @Override
    public int getBaseUpgradePoints(int upgradeIndex) {
        if (upgradeIndex == EnumUpgrade.SHIELD_CAPACITY.index) {
            return (int) (getProtectionShare() * 25) + (armorType == 2 ? 2 : 0);
        }
        if (upgradeIndex == EnumUpgrade.SHIELD_RECOVERY.index) {
            return BalanceConfigHandler.draconicArmorMinShieldRecovery;
        }
        return 0;
    }

    //region//----------------- ICustomArmor Start -----------------//

    @Override
    public float getProtectionPoints(ItemStack stack) {
        return EnumUpgrade.SHIELD_CAPACITY.getUpgradePoints(stack) * 20;
    }

    @Override
    public int getRecoveryPoints(ItemStack stack) {
        return EnumUpgrade.SHIELD_RECOVERY.getUpgradePoints(stack);
    }

    @Override
    public float getSpeedModifier(ItemStack stack, EntityPlayer player) {
        if (IConfigurableItem.ProfileHelper.getBoolean(stack, "ArmorSprintOnly", false)) {
            return player.isSprinting() ? IConfigurableItem.ProfileHelper.getFloat(stack, "ArmorSpeedMult", 0f) : IConfigurableItem.ProfileHelper.getFloat(stack, "ArmorSpeedMult", 0f) / 5F;
        } else return IConfigurableItem.ProfileHelper.getFloat(stack, "ArmorSpeedMult", 0f);
    }

    @Override
    public float getJumpModifier(ItemStack stack, EntityPlayer player) {
        if (IConfigurableItem.ProfileHelper.getBoolean(stack, "ArmorSprintOnly", false)) {
            return player.isSprinting() || BrandonsCore.proxy.isCtrlDown() ? IConfigurableItem.ProfileHelper.getFloat(stack, "ArmorJumpMult", 0f) : IConfigurableItem.ProfileHelper.getFloat(stack, "ArmorJumpMult", 0f) / 5F;
        } else return IConfigurableItem.ProfileHelper.getFloat(stack, "ArmorJumpMult", 0f);
    }

    @Override
    public boolean hasHillStep(ItemStack stack, EntityPlayer player) {
        if (IConfigurableItem.ProfileHelper.getBoolean(stack, "ArmorSprintOnly", false)) {
            return (player.isSprinting() || BrandonsCore.proxy.isCtrlDown()) && IConfigurableItem.ProfileHelper.getBoolean(stack, "ArmorHillStep", true);
        } else return IConfigurableItem.ProfileHelper.getBoolean(stack, "ArmorHillStep", true);
    }

    @Override
    public float getFireResistance(ItemStack stack) {
        return 1F;
    }

    @Override
    public boolean[] hasFlight(ItemStack stack) {
        return new boolean[]{true, IConfigurableItem.ProfileHelper.getBoolean(stack, "ArmorFlightLock", false), IConfigurableItem.ProfileHelper.getBoolean(stack, "ArmorInertiaCancellation", false)};
    }

    @Override
    public float getFlightSpeedModifier(ItemStack stack, EntityPlayer player) {
        if (IConfigurableItem.ProfileHelper.getBoolean(stack, "EffectiveOnSprint", false)) {
            return BrandonsCore.proxy.isCtrlDown() ? IConfigurableItem.ProfileHelper.getFloat(stack, "ArmorFlightSpeedMult", 0f) : 0F;
        } else return IConfigurableItem.ProfileHelper.getFloat(stack, "ArmorFlightSpeedMult", 0f);
    }

    @Override
    public float getFlightVModifier(ItemStack stack, EntityPlayer player) {
        if (IConfigurableItem.ProfileHelper.getBoolean(stack, "EffectiveOnSprint", false)) {
            return BrandonsCore.proxy.isCtrlDown() ? IConfigurableItem.ProfileHelper.getFloat(stack, "VerticalAcceleration", 0F) : 0F;
        } else return IConfigurableItem.ProfileHelper.getFloat(stack, "VerticalAcceleration", 0F);
    }

    @Override
    public int getEnergyPerProtectionPoint() {
        return BalanceConfigHandler.draconicArmorEnergyPerProtectionPoint;
    }

    //endregion

    @Override
    public boolean hasProfiles() {
        return false;
    }
}
