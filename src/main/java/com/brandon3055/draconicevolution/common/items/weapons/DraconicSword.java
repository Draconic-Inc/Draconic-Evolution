package com.brandon3055.draconicevolution.common.items.weapons;

import java.util.ArrayList;
import java.util.List;

import com.brandon3055.brandonscore.BrandonsCore;
import com.brandon3055.brandonscore.common.utills.InfoHelper;
import com.brandon3055.brandonscore.common.utills.ItemNBTHelper;
import com.brandon3055.brandonscore.common.utills.Utills;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.client.render.IRenderTweak;
import com.brandon3055.draconicevolution.common.ModItems;
import com.brandon3055.draconicevolution.common.entity.EntityPersistentItem;
import com.brandon3055.draconicevolution.common.handler.BalanceConfigHandler;
import com.brandon3055.draconicevolution.common.items.tools.baseclasses.ToolBase;
import com.brandon3055.draconicevolution.common.items.tools.baseclasses.ToolHandler;
import com.brandon3055.draconicevolution.common.lib.References;
import com.brandon3055.draconicevolution.common.lib.Strings;
import com.brandon3055.draconicevolution.common.network.ToolModePacket;
import com.brandon3055.draconicevolution.common.utills.*;
import com.google.common.collect.Multimap;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;

public class DraconicSword extends ItemSword implements IEnergyContainerWeaponItem, IInventoryTool, IRenderTweak, IUpgradableItem, IHudDisplayItem {
    protected int capacity = BalanceConfigHandler.draconicWeaponsBaseStorage;
    protected int maxReceive = BalanceConfigHandler.draconicWeaponsMaxTransfer;
    protected int maxExtract = BalanceConfigHandler.draconicWeaponsMaxTransfer;

    public DraconicSword() {
        super(ModItems.AWAKENED);
        this.setUnlocalizedName(Strings.draconicSwordName);
        this.setCreativeTab(DraconicEvolution.tabToolsWeapons);
        if (ModItems.isEnabled(this)) GameRegistry.registerItem(this, Strings.draconicSwordName);
    }

    @Override
    public boolean isItemTool(ItemStack p_77616_1_) {
        return true;
    }

    @SuppressWarnings("all")
    @Override
    public void getSubItems(Item item, CreativeTabs tab, List list) {
        list.add(ItemNBTHelper.setInteger(new ItemStack(item, 1, 0), "Energy", 0));
        list.add(ItemNBTHelper.setInteger(new ItemStack(item, 1, 0), "Energy", capacity));
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
    public void registerIcons(final IIconRegister iconRegister) {
        this.itemIcon = iconRegister.registerIcon(References.RESOURCESPREFIX + "draconic_sword");
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
        entity.hurtResistantTime = 0;
        ToolHandler.AOEAttack(player, entity, stack, IConfigurableItem.ProfileHelper.getInteger(stack, References.ATTACK_AOE, 0));
        ToolHandler.damageEntityBasedOnHealth(entity, player, 0.2F);
        return true;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public void addInformation(final ItemStack stack, final EntityPlayer player, final List list, final boolean extraInformation) {
        if (InfoHelper.holdShiftForDetails(list)) {
            List<ItemConfigField> l = getFields(stack, 0);
            for (ItemConfigField f : l) list.add(f.getTooltipInfo());
            list.add(InfoHelper.ITC() + StatCollector.translateToLocal("info.de.sword.txt"));
            InfoHelper.addLore(stack, list);
        }
        ToolBase.holdCTRLForUpgrades(list, stack);
        InfoHelper.addEnergyInfo(stack, list);
        list.add("");
        list.add(EnumChatFormatting.BLUE + "+" + ToolHandler.getBaseAttackDamage(stack) + " " + StatCollector.translateToLocal("info.de.attackDamage.txt"));
        list.add(EnumChatFormatting.BLUE + "+20%" + " " + StatCollector.translateToLocal("info.de.bonusHealthDamage.txt"));
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.rare;
    }

    @Override
    public int receiveEnergy(ItemStack container, int maxReceive, boolean simulate) {

        if (container.stackTagCompound == null) {
            container.stackTagCompound = new NBTTagCompound();
        }
        int energy = container.stackTagCompound.getInteger("Energy");
        int energyReceived = Math.min(getMaxEnergyStored(container) - energy, Math.min(this.maxReceive, maxReceive));

        if (!simulate) {
            energy += energyReceived;
            container.stackTagCompound.setInteger("Energy", energy);
        }
        return energyReceived;
    }

    @Override
    public int extractEnergy(ItemStack container, int maxExtract, boolean simulate) {

        if (container.stackTagCompound == null || !container.stackTagCompound.hasKey("Energy")) {
            return 0;
        }
        int energy = container.stackTagCompound.getInteger("Energy");
        int energyExtracted = Math.min(energy, Math.min(this.maxExtract, maxExtract));

        if (!simulate) {
            energy -= energyExtracted;
            container.stackTagCompound.setInteger("Energy", energy);
        }
        return energyExtracted;
    }

    @Override
    public int getEnergyStored(ItemStack container) {
        if (container.stackTagCompound == null || !container.stackTagCompound.hasKey("Energy")) {
            return 0;
        }
        return container.stackTagCompound.getInteger("Energy");
    }

    @Override
    public int getMaxEnergyStored(ItemStack container) {
        int points = IUpgradableItem.EnumUpgrade.RF_CAPACITY.getUpgradePoints(container);
        return BalanceConfigHandler.draconicWeaponsBaseStorage + points * BalanceConfigHandler.draconicWeaponsStoragePerUpgrade;
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
    public boolean hasCustomEntity(ItemStack stack) {
        return true;
    }

    @Override
    public Entity createEntity(World world, Entity location, ItemStack itemstack) {
        return new EntityPersistentItem(world, location, itemstack);
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
        return enchant.type == EnumEnchantmentType.weapon;
    }

    @Override
    public List<ItemConfigField> getFields(ItemStack stack, int slot) {
        List<ItemConfigField> list = new ArrayList<ItemConfigField>();
        list.add(new ItemConfigField(References.INT_ID, slot, References.ATTACK_AOE).setMinMaxAndIncromente(0, EnumUpgrade.ATTACK_AOE.getUpgradePoints(stack), 1).readFromItem(stack, 1).setModifier("AOE"));
        return list;
    }

    @Override
    public Multimap getAttributeModifiers(ItemStack stack) {
        Multimap map = super.getAttributeModifiers(stack);
        map.clear();
        return map;
    }

    @Override
    public void tweakRender(IItemRenderer.ItemRenderType type) {

        GL11.glTranslated(-0.01, 1.11, -0.15);
        GL11.glRotatef(90, 1, 0, 0);
        GL11.glRotatef(140, 0, -1, 0);
        GL11.glScaled(0.7, 0.7, 0.7);

        if (type == IItemRenderer.ItemRenderType.EQUIPPED_FIRST_PERSON) {
            GL11.glRotatef(180, 0, 0, 1);
            GL11.glTranslated(0, -0.4, 0);
        } else if (type == IItemRenderer.ItemRenderType.INVENTORY) {
            GL11.glScalef(8, 8, 8);
            GL11.glRotatef(180, 0, 1, 0);
            GL11.glTranslated(1.9, 0, 0);
        } else if (type == IItemRenderer.ItemRenderType.ENTITY) {
            GL11.glRotatef(-90.5F, 0, 1, 0);
            GL11.glTranslated(-0.8, 0, 0);
        }
    }

    @Override
    public List<EnumUpgrade> getUpgrades(ItemStack itemstack) {
        return new ArrayList<EnumUpgrade>() {{
            add(EnumUpgrade.RF_CAPACITY);
            add(EnumUpgrade.ATTACK_AOE);
            add(EnumUpgrade.ATTACK_DAMAGE);
        }};
    }

    @Override
    public int getUpgradeCap(ItemStack itemstack) {
        return BalanceConfigHandler.draconicWeaponsMaxUpgrades;
    }

    @Override
    public int getMaxTier(ItemStack itemstack) {
        return 2;
    }

    @Override
    public int getMaxUpgradePoints(int upgradeIndex) {
        if (upgradeIndex == EnumUpgrade.RF_CAPACITY.index) {
            return BalanceConfigHandler.draconicWeaponsMaxCapacityUpgradePoints;
        }
        if (upgradeIndex == EnumUpgrade.ATTACK_AOE.index) {
            return BalanceConfigHandler.draconicWeaponsMaxAttackAOEUpgradePoints;
        }
        if (upgradeIndex == EnumUpgrade.ATTACK_DAMAGE.index) {
            return BalanceConfigHandler.draconicWeaponsMaxAttackDamageUpgradePoints;
        }
        return BalanceConfigHandler.draconicWeaponsMaxUpgradePoints;
    }

    @Override
    public int getMaxUpgradePoints(int upgradeIndex, ItemStack stack) {
        return getMaxUpgradePoints(upgradeIndex);
    }

    @Override
    public int getBaseUpgradePoints(int upgradeIndex) {
        if (upgradeIndex == EnumUpgrade.ATTACK_AOE.index) {
            return BalanceConfigHandler.draconicWeaponsMinAttackAOEUpgradePoints;
        }
        if (upgradeIndex == EnumUpgrade.ARROW_DAMAGE.index) {
            return BalanceConfigHandler.draconicWeaponsMinAttackDamageUpgradePoints;
        }
        return 0;
    }

    @Override
    public List<String> getUpgradeStats(ItemStack stack) {
        List<String> strings = new ArrayList<String>();

        int attackaoe = 0;
        for (ItemConfigField field : getFields(stack, 0))
            if (field.name.equals(References.ATTACK_AOE)) attackaoe = 1 + ((Integer) field.max * 2);

        strings.add(InfoHelper.ITC() + StatCollector.translateToLocal("gui.de.RFCapacity.txt") + ": " + InfoHelper.HITC() + Utills.formatNumber(getMaxEnergyStored(stack)));
        strings.add(InfoHelper.ITC() + StatCollector.translateToLocal("info.de.attackDamage.txt") + ": " + InfoHelper.HITC() + ToolHandler.getBaseAttackDamage(stack));
        strings.add(InfoHelper.ITC() + StatCollector.translateToLocal("gui.de.max.txt") + " " + StatCollector.translateToLocal("gui.de.AttackAOE.txt") + ": " + InfoHelper.HITC() + attackaoe + "x" + attackaoe);


        return strings;
    }

    @Override
    public List<String> getDisplayData(ItemStack stack) {
        List<String> list = new ArrayList<String>();
        for (ItemConfigField field : getFields(stack, 0))
            list.add(field.getTooltipInfo());//list.add(field.getLocalizedName() + ": " + field.getFormattedValue());
        return list;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (!world.isRemote && !BrandonsCore.proxy.isDedicatedServer()) {
            ToolBase.handleModeChange(stack, player, InfoHelper.isShiftKeyDown(), InfoHelper.isCtrlKeyDown());
        } else if (world.isRemote && BrandonsCore.proxy.getMCServer() == null) {
            ToolBase.handleModeChange(stack, player, InfoHelper.isShiftKeyDown(), InfoHelper.isCtrlKeyDown());
            DraconicEvolution.network.sendToServer(new ToolModePacket(InfoHelper.isShiftKeyDown(), InfoHelper.isCtrlKeyDown()));
        }
        return super.onItemRightClick(stack, world, player);
    }

    @Override
    public boolean hasProfiles() {
        return true;
    }

    @Override
    public int getEnergyPerAttack() {
        return BalanceConfigHandler.draconicWeaponsEnergyPerAttack;
    }
}
