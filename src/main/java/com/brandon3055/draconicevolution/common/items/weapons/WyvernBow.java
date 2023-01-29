package com.brandon3055.draconicevolution.common.items.weapons;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import com.brandon3055.brandonscore.BrandonsCore;
import com.brandon3055.brandonscore.common.utills.InfoHelper;
import com.brandon3055.brandonscore.common.utills.ItemNBTHelper;
import com.brandon3055.brandonscore.common.utills.Utills;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.common.ModItems;
import com.brandon3055.draconicevolution.common.entity.EntityPersistentItem;
import com.brandon3055.draconicevolution.common.handler.BalanceConfigHandler;
import com.brandon3055.draconicevolution.common.handler.ConfigHandler;
import com.brandon3055.draconicevolution.common.items.tools.baseclasses.ToolBase;
import com.brandon3055.draconicevolution.common.lib.References;
import com.brandon3055.draconicevolution.common.lib.Strings;
import com.brandon3055.draconicevolution.common.utills.IHudDisplayItem;
import com.brandon3055.draconicevolution.common.utills.IInventoryTool;
import com.brandon3055.draconicevolution.common.utills.IUpgradableItem;
import com.brandon3055.draconicevolution.common.utills.ItemConfigField;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class WyvernBow extends ItemBow
        implements IInventoryTool, IUpgradableItem, IEnergyContainerWeaponItem, IHudDisplayItem {

    public static final String[] bowPullIconNameArray = new String[] { "pulling_0", "pulling_1", "pulling_2" };

    protected int capacity = BalanceConfigHandler.wyvernWeaponsBaseStorage;
    protected int maxReceive = BalanceConfigHandler.wyvernWeaponsMaxTransfer;
    protected int maxExtract = BalanceConfigHandler.wyvernWeaponsMaxTransfer;

    @SideOnly(Side.CLIENT)
    private IIcon[] iconArray;

    public WyvernBow() {
        this.maxStackSize = 1;
        this.setMaxDamage(-1);
        this.setCreativeTab(DraconicEvolution.tabToolsWeapons);
        this.setUnlocalizedName(Strings.wyvernBowName);
        if (ModItems.isEnabled(this)) GameRegistry.registerItem(this, Strings.wyvernBowName);
    }

    // region Regular Item Stuff
    @Override
    public boolean isItemTool(ItemStack p_77616_1_) {
        return true;
    }

    @Override
    public String getUnlocalizedName() {

        return String.format(
                "item.%s%s",
                References.MODID.toLowerCase() + ":",
                super.getUnlocalizedName().substring(super.getUnlocalizedName().indexOf(".") + 1));
    }

    @Override
    public String getUnlocalizedName(final ItemStack itemStack) {
        return getUnlocalizedName();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister) {
        this.itemIcon = iconRegister.registerIcon(References.RESOURCESPREFIX + "wyvern_bow" + "_standby");
        this.iconArray = new IIcon[bowPullIconNameArray.length];

        for (int i = 0; i < this.iconArray.length; ++i) {
            this.iconArray[i] = iconRegister
                    .registerIcon(References.RESOURCESPREFIX + "wyvern_bow" + "_" + bowPullIconNameArray[i]);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(ItemStack stack, int renderPass, EntityPlayer player, ItemStack usingItem, int useRemaining) {
        float j = (float) stack.getMaxItemUseDuration() - (float) useRemaining;
        if (usingItem == null) {
            return this.itemIcon;
        }

        BowHandler.BowProperties properties = new BowHandler.BowProperties(stack, player);

        if (j > properties.getDrawTicks()) j = properties.getDrawTicks();

        j /= (float) properties.getDrawTicks();
        int j2 = (int) (j * 2F);

        if (j2 < 0) j2 = 0;
        else if (j2 > 2) j2 = 2;

        return getItemIconForUseDuration(j2);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getItemIconForUseDuration(int par1) {
        return this.iconArray[par1];
    }

    @Override
    public boolean hasCustomEntity(ItemStack stack) {
        return true;
    }

    @Override
    public Entity createEntity(World world, Entity location, ItemStack itemstack) {
        return new EntityPersistentItem(world, location, itemstack);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(final ItemStack stack, final EntityPlayer player, final List list,
            final boolean extraInformation) {
        boolean show = InfoHelper.holdShiftForDetails(list);
        if (show) {
            int preset = ItemNBTHelper.getInteger(stack, "ConfigProfile", 0);
            list.add(
                    EnumChatFormatting.DARK_PURPLE + StatCollector.translateToLocal("info.de.capacitorMode.txt")
                            + ": "
                            + ItemNBTHelper.getString(stack, "ProfileName" + preset, "Profile " + preset));
            List<ItemConfigField> l = getFields(stack, 0);
            for (ItemConfigField f : l) list.add(f.getTooltipInfo());
        }
        ToolBase.holdCTRLForUpgrades(list, stack);
        InfoHelper.addEnergyInfo(stack, list);
        if (show && !ConfigHandler.disableLore) InfoHelper.addLore(stack, list, true);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void getSubItems(Item item, CreativeTabs tab, List list) {
        list.add(ItemNBTHelper.setInteger(new ItemStack(item, 1, 0), "Energy", 0));
        list.add(ItemNBTHelper.setInteger(new ItemStack(item, 1, 0), "Energy", capacity));
    }

    @Override
    public boolean getHasSubtypes() {
        return true;
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return !(getEnergyStored(stack) == getMaxEnergyStored(stack));
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        return 1D - ((double) getEnergyStored(stack) / (double) getMaxEnergyStored(stack));
    }
    // endregion

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        return BowHandler.onBowRightClick(this, stack, world, player);
    }

    @Override
    public void onUsingTick(ItemStack stack, EntityPlayer player, int count) {
        BowHandler.onBowUsingTick(stack, player, count);
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World world, EntityPlayer player, int count) {
        BowHandler.onPlayerStoppedUsingBow(stack, world, player, count);
    }

    // region Interfaces
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
        return enchant.type == EnumEnchantmentType.bow || enchant.effectId == DraconicEvolution.reaperEnchant.effectId;
    }

    @Override
    public List<ItemConfigField> getFields(ItemStack stack, int slot) {
        List<ItemConfigField> list = new ArrayList<ItemConfigField>();

        list.add(
                new ItemConfigField(References.FLOAT_ID, slot, "BowArrowDamage").setMinMaxAndIncromente(
                        (float) getBaseUpgradePoints(EnumUpgrade.ARROW_DAMAGE.index),
                        (float) EnumUpgrade.ARROW_DAMAGE.getUpgradePoints(stack),
                        0.1F).readFromItem(stack, (float) EnumUpgrade.ARROW_DAMAGE.getUpgradePoints(stack)));
        list.add(
                new ItemConfigField(References.FLOAT_ID, slot, "BowArrowSpeedModifier")
                        .setMinMaxAndIncromente(0F, (float) EnumUpgrade.ARROW_SPEED.getUpgradePoints(stack), 0.01F)
                        .readFromItem(stack, 0F).setModifier("PLUSPERCENT"));
        list.add(new ItemConfigField(References.BOOLEAN_ID, slot, "BowAutoFire").readFromItem(stack, false));
        list.add(
                new ItemConfigField(References.FLOAT_ID, slot, "BowExplosionPower").setMinMaxAndIncromente(0F, 4F, 0.1F)
                        .readFromItem(stack, 0F));
        // list.add(new ItemConfigField(References.FLOAT_ID, slot, "BowShockWavePower").setMinMaxAndIncromente(0F, 4F,
        // 0.1F).readFromItem(stack, 0F));
        // list.add(new ItemConfigField(References.BOOLEAN_ID, slot, "BowEnergyBolt").readFromItem(stack, false));
        list.add(
                new ItemConfigField(References.FLOAT_ID, slot, "BowZoomModifier").setMinMaxAndIncromente(0F, 3F, 0.01F)
                        .readFromItem(stack, 0F).setModifier("PLUSPERCENT"));

        return list;
    }

    @Override
    public boolean hasProfiles() {
        return true;
    }

    @Override
    public List<EnumUpgrade> getUpgrades(ItemStack itemstack) {
        return new ArrayList<EnumUpgrade>() {

            {
                add(EnumUpgrade.RF_CAPACITY);
                add(EnumUpgrade.DRAW_SPEED);
                add(EnumUpgrade.ARROW_SPEED);
                add(EnumUpgrade.ARROW_DAMAGE);
            }
        };
    }

    @Override
    public int getUpgradeCap(ItemStack itemstack) {
        return BalanceConfigHandler.wyvernBowMaxUpgrades;
    }

    @Override
    public int getMaxTier(ItemStack itemstack) {
        return 1;
    }

    @Override
    public int getMaxUpgradePoints(int upgradeIndex) {
        if (upgradeIndex == EnumUpgrade.RF_CAPACITY.index) {
            return BalanceConfigHandler.wyvernBowMaxCapacityUpgradePoints;
        }
        if (upgradeIndex == EnumUpgrade.DRAW_SPEED.index) {
            return BalanceConfigHandler.wyvernBowMaxDrawSpeedUpgradePoints;
        }
        if (upgradeIndex == EnumUpgrade.ARROW_SPEED.index) {
            return BalanceConfigHandler.wyvernBowMaxArrowSpeedUpgradePoints;
        }
        if (upgradeIndex == EnumUpgrade.ARROW_DAMAGE.index) {
            return BalanceConfigHandler.wyvernBowMaxArrowDamageUpgradePoints;
        }
        return BalanceConfigHandler.wyvernBowMaxUpgradePoints;
    }

    @Override
    public int getMaxUpgradePoints(int upgradeIndex, ItemStack stack) {
        return getMaxUpgradePoints(upgradeIndex);
    }

    @Override
    public int getBaseUpgradePoints(int upgradeIndex) {
        if (upgradeIndex == EnumUpgrade.DRAW_SPEED.index) {
            return BalanceConfigHandler.wyvernBowMinDrawSpeedUpgradePoints;
        }
        if (upgradeIndex == EnumUpgrade.ARROW_SPEED.index) {
            return BalanceConfigHandler.wyvernBowMinArrowSpeedUpgradePoints;
        }
        if (upgradeIndex == EnumUpgrade.ARROW_DAMAGE.index) {
            return BalanceConfigHandler.wyvernBowMinArrowDamageUpgradePoints;
        }
        return 0;
    }

    @Override
    public List<String> getUpgradeStats(ItemStack stack) {
        BowHandler.BowProperties properties = new BowHandler.BowProperties(stack, null);
        List<String> list = new ArrayList<String>();
        list.add(
                InfoHelper.ITC() + StatCollector.translateToLocal("gui.de.RFCapacity.txt")
                        + ": "
                        + InfoHelper.HITC()
                        + Utills.formatNumber(getMaxEnergyStored(stack)));
        list.add(
                InfoHelper.ITC() + StatCollector.translateToLocal("gui.de.max.txt")
                        + " "
                        + StatCollector.translateToLocal("gui.de.ArrowSpeed.txt")
                        + ": "
                        + InfoHelper.HITC()
                        + "+"
                        + EnumUpgrade.ARROW_SPEED.getUpgradePoints(stack) * 100
                        + "%");
        list.add(
                InfoHelper.ITC() + StatCollector.translateToLocal(
                        "gui.de.ArrowDamage.txt") + ": " + InfoHelper.HITC() + properties.arrowDamage + "");
        list.add(
                InfoHelper.ITC() + StatCollector.translateToLocal("gui.de.DrawSpeed.txt")
                        + ": "
                        + InfoHelper.HITC()
                        + properties.getDrawTicks() / 20D
                        + "s");

        return list;
    }

    @Override
    public int receiveEnergy(ItemStack container, int maxReceive, boolean simulate) {
        int energy = ItemNBTHelper.getInteger(container, "Energy", 0);
        int energyReceived = Math.min(getMaxEnergyStored(container) - energy, Math.min(this.maxReceive, maxReceive));
        if (!simulate) {
            energy += energyReceived;
            ItemNBTHelper.setInteger(container, "Energy", energy);
        }
        return energyReceived;
    }

    @Override
    public int extractEnergy(ItemStack container, int maxExtract, boolean simulate) {
        int energy = ItemNBTHelper.getInteger(container, "Energy", 0);
        int energyExtracted = Math.min(energy, Math.min(this.maxExtract, maxExtract));
        if (!simulate) {
            energy -= energyExtracted;
            ItemNBTHelper.setInteger(container, "Energy", energy);
        }
        return energyExtracted;
    }

    @Override
    public int getEnergyStored(ItemStack container) {
        return ItemNBTHelper.getInteger(container, "Energy", 0);
    }

    @Override
    public int getMaxEnergyStored(ItemStack stack) {
        int points = IUpgradableItem.EnumUpgrade.RF_CAPACITY.getUpgradePoints(stack);
        return BalanceConfigHandler.wyvernWeaponsBaseStorage
                + points * BalanceConfigHandler.wyvernWeaponsStoragePerUpgrade;
    }

    @Override
    public List<String> getDisplayData(ItemStack stack) {
        List<String> list = new ArrayList<String>();

        if (BrandonsCore.proxy.getClientPlayer() != null
                && BrandonsCore.proxy.getClientPlayer().getItemInUse() != null) {
            EntityPlayer player = BrandonsCore.proxy.getClientPlayer();
            BowHandler.BowProperties properties = new BowHandler.BowProperties(stack, player);
            int power = (int) Math
                    .min(((float) player.getItemInUseDuration() / (float) properties.getDrawTicks() * 100F), 100F);
            list.add(
                    InfoHelper.ITC() + StatCollector
                            .translateToLocal("info.de.power.txt") + ": " + InfoHelper.HITC() + power + "%");
        } else {
            int preset = ItemNBTHelper.getInteger(stack, "ConfigProfile", 0);
            list.add(
                    EnumChatFormatting.DARK_PURPLE + StatCollector.translateToLocal("info.de.capacitorMode.txt")
                            + ": "
                            + ItemNBTHelper.getString(stack, "ProfileName" + preset, "Profile " + preset));

            for (ItemConfigField field : getFields(stack, 0)) {
                if ((field.datatype == References.FLOAT_ID && (Float) field.value > 0)
                        || (field.datatype == References.BOOLEAN_ID && (Boolean) field.value))
                    list.add(field.getTooltipInfo());
            }

            list.add(
                    InfoHelper.ITC() + StatCollector.translateToLocal("info.de.charge.txt")
                            + ": "
                            + InfoHelper.HITC()
                            + Utills.formatNumber(getEnergyStored(stack))
                            + " / "
                            + Utills.formatNumber(getMaxEnergyStored(stack)));

            if (BrandonsCore.proxy.getClientPlayer() != null) {
                BowHandler.BowProperties properties = new BowHandler.BowProperties(
                        stack,
                        BrandonsCore.proxy.getClientPlayer());
                list.add(
                        InfoHelper.ITC() + StatCollector.translateToLocal("gui.de.rfPerShot.txt")
                                + ": "
                                + InfoHelper.HITC()
                                + Utills.addCommas(properties.calculateEnergyCost()));
                if (!properties.canFire() && properties.cantFireMessage != null)
                    list.add(EnumChatFormatting.DARK_RED + StatCollector.translateToLocal(properties.cantFireMessage));
            }
        }
        return list;
    }

    @Override
    public int getEnergyPerAttack() {
        return BalanceConfigHandler.wyvernBowEnergyPerShot;
    }
    // endregion
}
