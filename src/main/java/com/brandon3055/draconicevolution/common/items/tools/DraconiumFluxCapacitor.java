package com.brandon3055.draconicevolution.common.items.tools;

import java.util.ArrayList;
import java.util.List;

import cofh.api.energy.IEnergyContainerItem;
import com.brandon3055.brandonscore.common.utills.InfoHelper;
import com.brandon3055.brandonscore.common.utills.ItemNBTHelper;
import com.brandon3055.brandonscore.common.utills.Utills;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.common.ModItems;
import com.brandon3055.draconicevolution.common.handler.BalanceConfigHandler;
import com.brandon3055.draconicevolution.common.items.tools.baseclasses.RFItemBase;
import com.brandon3055.draconicevolution.common.items.tools.baseclasses.ToolBase;
import com.brandon3055.draconicevolution.common.lib.Strings;
import com.brandon3055.draconicevolution.common.utills.IUpgradableItem;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

/**
 * Created by Brandon on 24/11/2014.
 */
public class DraconiumFluxCapacitor extends RFItemBase implements IUpgradableItem {
    IIcon[] icons = new IIcon[2];

    public DraconiumFluxCapacitor() {
        this.setUnlocalizedName(Strings.draconiumFluxCapacitorName);
        this.setCreativeTab(DraconicEvolution.tabToolsWeapons);
        this.setHasSubtypes(true);
        this.setMaxStackSize(1);
        ModItems.register(this);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IIconRegister iconRegister) {
        icons[0] = iconRegister.registerIcon(getUnwrappedUnlocalizedName(super.getUnlocalizedName()) + 0);
        icons[1] = iconRegister.registerIcon(getUnwrappedUnlocalizedName(super.getUnlocalizedName()) + 1);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIconFromDamage(int damage) {
        return icons[damage];
    }

    @SideOnly(Side.CLIENT)
    @SuppressWarnings("unchecked")
    @Override
    public void getSubItems(Item item, CreativeTabs tab, List list) {
        list.add(ItemNBTHelper.setInteger(new ItemStack(item, 1, 0), "Energy", 0));
        list.add(ItemNBTHelper.setInteger(new ItemStack(item, 1, 0), "Energy", BalanceConfigHandler.wyvernCapacitorBaseStorage));
        list.add(ItemNBTHelper.setInteger(new ItemStack(item, 1, 1), "Energy", 0));
        list.add(ItemNBTHelper.setInteger(new ItemStack(item, 1, 1), "Energy", BalanceConfigHandler.draconicCapacitorBaseStorage));
    }

    @Override
    public String getUnlocalizedName(ItemStack itemStack) {
        return super.getUnlocalizedName(itemStack) + itemStack.getItemDamage();
    }

    @Override
    public int getCapacity(ItemStack stack) {
        int points = EnumUpgrade.RF_CAPACITY.getUpgradePoints(stack);
        return stack.getItemDamage() == 0 ? BalanceConfigHandler.wyvernCapacitorBaseStorage + points * BalanceConfigHandler.wyvernCapacitorStoragePerUpgrade : stack.getItemDamage() == 1 ? BalanceConfigHandler.draconicCapacitorBaseStorage + points * BalanceConfigHandler.draconicCapacitorStoragePerUpgrade : 0;
    }

    @Override
    public int getMaxExtract(ItemStack stack) {
        return stack.getItemDamage() == 0 ? BalanceConfigHandler.wyvernCapacitorMaxExtract : stack.getItemDamage() == 1 ? BalanceConfigHandler.draconicCapacitorMaxExtract : 0;
    }

    @Override
    public int getMaxReceive(ItemStack stack) {
        return stack.getItemDamage() == 0 ? BalanceConfigHandler.wyvernCapacitorMaxReceive : stack.getItemDamage() == 1 ? BalanceConfigHandler.draconicCapacitorMaxReceive : 0;
    }

    @Override
    public void onUpdate(ItemStack container, World world, Entity entity, int var1, boolean b) {
        if (!(entity instanceof EntityPlayer)) return;
        EntityPlayer player = (EntityPlayer) entity;

        int mode = ItemNBTHelper.getShort(container, "Mode", (short) 0);

        if (mode == 1 || mode == 3) { //Charge Hotbar
            for (int i = 0; i < 9; i++) {
                int max = Math.min(getEnergyStored(container), getMaxExtract(container));
                ItemStack stack = player.inventory.getStackInSlot(i);

                if (stack != null && stack.getItem() instanceof IEnergyContainerItem && stack.getItem() != ModItems.draconiumFluxCapacitor) {
                    IEnergyContainerItem item = (IEnergyContainerItem) stack.getItem();
                    extractEnergy(container, item.receiveEnergy(stack, max, false), false);
                }
            }
        }

        if (mode == 2 || mode == 3) { //Charge Armor and held item
            for (int i = mode == 3 ? 1 : 0; i < 5; i++) {
                int max = Math.min(getEnergyStored(container), getMaxExtract(container));
                ItemStack stack = player.getEquipmentInSlot(i);

                if (stack != null && stack.getItem() instanceof IEnergyContainerItem && stack.getItem() != ModItems.draconiumFluxCapacitor) {
                    IEnergyContainerItem item = (IEnergyContainerItem) stack.getItem();
                    extractEnergy(container, item.receiveEnergy(stack, max, false), false);
                }
            }
        }
    }

    @Override
    public boolean hasEffect(ItemStack stack, int pass) {
        return ItemNBTHelper.getShort(stack, "Mode", (short) 0) > 0;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (player.isSneaking()) {
            int mode = ItemNBTHelper.getShort(stack, "Mode", (short) 0);
            int newMode = mode == 3 ? 0 : mode + 1;
            ItemNBTHelper.setShort(stack, "Mode", (short) newMode);
            if (world.isRemote)
                player.addChatComponentMessage(new ChatComponentTranslation(InfoHelper.ITC() + StatCollector.translateToLocal("info.de.capacitorMode.txt") + ": " + InfoHelper.HITC() + StatCollector.translateToLocal("info.de.capacitorMode" + ItemNBTHelper.getShort(stack, "Mode", (short) 0) + ".txt")));
        }
        return stack;
    }

    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void addInformation(final ItemStack stack, final EntityPlayer player, final List list, final boolean extraInformation) {
        if (InfoHelper.holdShiftForDetails(list)) {

            list.add(StatCollector.translateToLocal("info.de.changwMode.txt"));
            list.add(InfoHelper.ITC() + StatCollector.translateToLocal("info.de.capacitorMode.txt") + ": " + InfoHelper.HITC() + StatCollector.translateToLocal("info.de.capacitorMode" + ItemNBTHelper.getShort(stack, "Mode", (short) 0) + ".txt"));
            //InfoHelper.addLore(stack, list);
        }
        ToolBase.holdCTRLForUpgrades(list, stack);
        InfoHelper.addEnergyInfo(stack, list);
    }

    @Override
    public boolean hasProfiles() {
        return false;
    }

    @Override
    public List<EnumUpgrade> getUpgrades(ItemStack itemstack) {
        return new ArrayList<EnumUpgrade>() {{
            add(EnumUpgrade.RF_CAPACITY);
        }};
    }


    @Override
    public int getUpgradeCap(ItemStack stack) {
        return stack.getItemDamage() == 0 ? BalanceConfigHandler.wyvernCapacitorMaxUpgrades : stack.getItemDamage() == 1 ? BalanceConfigHandler.draconicCapacitorMaxUpgrades : 0;
    }

    @Override
    public int getMaxTier(ItemStack stack) {
        return stack.getItemDamage() == 0 ? 1 : 2;
    }

    @Override
    public int getMaxUpgradePoints(int upgradeIndex) {
        return Math.max(BalanceConfigHandler.wyvernCapacitorMaxUpgradePoints, BalanceConfigHandler.draconicCapacitorMaxUpgradePoints);
    }

    @Override
    public int getMaxUpgradePoints(int upgradeIndex, ItemStack stack) {
        if (stack == null) {
            return getMaxUpgradePoints(upgradeIndex);
        }
        if (upgradeIndex == EnumUpgrade.RF_CAPACITY.index) {
            return stack.getItemDamage() == 0 ? BalanceConfigHandler.wyvernCapacitorMaxCapacityUpgradePoints : stack.getItemDamage() == 1 ? BalanceConfigHandler.draconicCapacitorMaxCapacityUpgradePoints : getMaxUpgradePoints(upgradeIndex);
        }
        return stack.getItemDamage() == 0 ? BalanceConfigHandler.wyvernCapacitorMaxUpgradePoints : stack.getItemDamage() == 1 ? BalanceConfigHandler.draconicCapacitorMaxUpgradePoints : getMaxUpgradePoints(upgradeIndex);
    }

    @Override
    public int getBaseUpgradePoints(int upgradeIndex) {
        return 0;
    }

    @Override
    public List<String> getUpgradeStats(ItemStack stack) {
        List<String> strings = new ArrayList<String>();
        strings.add(InfoHelper.ITC() + StatCollector.translateToLocal("gui.de.RFCapacity.txt") + ": " + InfoHelper.HITC() + Utills.formatNumber(getMaxEnergyStored(stack)));
        return strings;
    }
}
