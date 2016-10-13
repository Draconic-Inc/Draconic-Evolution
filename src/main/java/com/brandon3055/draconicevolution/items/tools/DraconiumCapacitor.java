package com.brandon3055.draconicevolution.items.tools;

import cofh.api.energy.IEnergyContainerItem;
import com.brandon3055.brandonscore.items.ItemEnergyBase;
import com.brandon3055.brandonscore.utils.InfoHelper;
import com.brandon3055.brandonscore.utils.ItemNBTHelper;
import com.brandon3055.brandonscore.utils.Utils;
import com.brandon3055.draconicevolution.DEFeatures;
import com.brandon3055.draconicevolution.api.IInvCharge;
import com.brandon3055.draconicevolution.api.itemupgrade.*;
import com.brandon3055.draconicevolution.entity.EntityPersistentItem;
import com.brandon3055.draconicevolution.items.ToolUpgrade;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by brandon3055 on 31/05/2016.
 */
public class DraconiumCapacitor extends ItemEnergyBase implements IInvCharge, IUpgradableItem {

    public static final int wyvernTransfer = 8000000;
    public static final int wyvernBaseCap = 64000000;
    public static final int draconicTransfer = 64000000;
    public static final int draconicBaseCap = 256000000;

    public DraconiumCapacitor() {
        this.setHasSubtypes(true);
        this.addName(0, "wyvern").addName(1, "draconic").addName(2, "creative");
        this.setMaxStackSize(1);
    }

    //region Item

    @Override
    public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
        subItems.add(new ItemStack(DEFeatures.draconiumCapacitor, 1, 0));
        subItems.add(ItemNBTHelper.setInteger(new ItemStack(DEFeatures.draconiumCapacitor, 1, 0), "Energy", wyvernBaseCap));

        subItems.add(new ItemStack(DEFeatures.draconiumCapacitor, 1, 1));
        subItems.add(ItemNBTHelper.setInteger(new ItemStack(DEFeatures.draconiumCapacitor, 1, 1), "Energy", draconicBaseCap));

        subItems.add(ItemNBTHelper.setInteger(new ItemStack(DEFeatures.draconiumCapacitor, 1, 2), "Energy", Integer.MAX_VALUE / 2));
    }

    @Override
    public boolean hasCustomEntity(ItemStack stack) {
        return true;
    }

    @Override
    public Entity createEntity(World world, Entity location, ItemStack itemstack) {
        return new EntityPersistentItem(world, location, itemstack);
    }

    //endregion

    //region Energy

    @Override
    public int getCapacity(ItemStack stack) {
        int tier = stack.getItemDamage();

        int upgrade = UpgradeHelper.getUpgradeLevel(stack, ToolUpgrade.RF_CAPACITY);

        switch (tier) {
            case 0:
                return wyvernBaseCap + (upgrade * (wyvernBaseCap / 2));
            case 1:
                return draconicBaseCap + (upgrade * (draconicBaseCap / 2));
            case 2:
                return Integer.MAX_VALUE;
        }

        return 0;
    }

    @Override
    public int getMaxReceive(ItemStack stack) {
        int tier = stack.getItemDamage();

        switch (tier) {
            case 0:
                return wyvernTransfer;
            case 1:
                return draconicTransfer;
            case 2:
                return Integer.MAX_VALUE;
        }

        return 0;
    }

    @Override
    public int getMaxExtract(ItemStack stack) {
        int tier = stack.getItemDamage();

        switch (tier) {
            case 0:
                return wyvernTransfer;
            case 1:
                return draconicTransfer;
            case 2:
                return Integer.MAX_VALUE;
        }

        return 0;
    }

    @Override
    public int receiveEnergy(ItemStack container, int maxReceive, boolean simulate) {
        if (container.getItemDamage() == 2){
            return maxReceive;
        }

        return super.receiveEnergy(container, maxReceive, simulate);
    }

    @Override
    public int extractEnergy(ItemStack container, int maxExtract, boolean simulate) {
        if (container.getItemDamage() == 2){
            return maxExtract;
        }

        return super.extractEnergy(container, maxExtract, simulate);
    }

    //endregion

    //region Activation

    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand) {
        if (player.isSneaking()) {
            int mode = ItemNBTHelper.getShort(stack, "Mode", (short) 0);
            int newMode = mode == 3 ? 0 : mode + 1;
            ItemNBTHelper.setShort(stack, "Mode", (short) newMode);
            if (world.isRemote)
                player.addChatComponentMessage(new TextComponentTranslation(InfoHelper.ITC() + I18n.format("info.de.capacitorMode.txt") + ": " + InfoHelper.HITC() + I18n.format("info.de.capacitorMode" + ItemNBTHelper.getShort(stack, "Mode", (short) 0) + ".txt")));
        }
        return new ActionResult<ItemStack>(EnumActionResult.PASS, stack);
    }

    @Override
    public void onUpdate(ItemStack container, World world, Entity entity, int itemSlot, boolean isSelected) {
        if (!(entity instanceof EntityPlayer)) return;
        EntityPlayer player = (EntityPlayer) entity;

        int mode = ItemNBTHelper.getShort(container, "Mode", (short)0);

        if (mode == 1 || mode == 3){ //Charge Armor
            for (ItemStack stack : player.getArmorInventoryList()){
                int max = Math.min(getEnergyStored(container), getMaxExtract(container));


                if (stack != null && stack.getItem() instanceof IEnergyContainerItem) {
                    IEnergyContainerItem item = (IEnergyContainerItem)stack.getItem();

                    if (item instanceof IInvCharge && !((IInvCharge)item).canCharge(stack, player)){
                        continue;
                    }

                    extractEnergy(container, item.receiveEnergy(stack, max, false), false);
                }
            }
        }

        if (mode == 2 || mode == 3){ //Charge Held Items
            for (ItemStack stack : player.getHeldEquipment()){
                int max = Math.min(getEnergyStored(container), getMaxExtract(container));

                if (stack != null && stack.getItem() instanceof IEnergyContainerItem) {
                    IEnergyContainerItem item = (IEnergyContainerItem)stack.getItem();

                    if (item instanceof IInvCharge && !((IInvCharge)item).canCharge(stack, player)){
                        continue;
                    }

                    extractEnergy(container, item.receiveEnergy(stack, max, false), false);
                }
            }
        }
    }

    @Override
    public boolean canCharge(ItemStack stack, EntityPlayer player) {
        return false;
    }

    //endregion

    //region Display

    @Override
    public boolean hasEffect(ItemStack stack) {
        return ItemNBTHelper.getShort(stack, "Mode", (short)0) > 0;
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
        if (InfoHelper.holdShiftForDetails(tooltip)) {
            tooltip.add(I18n.format("info.de.changwMode.txt"));
            tooltip.add(InfoHelper.ITC() + I18n.format("info.de.capacitorMode.txt") + ": " + InfoHelper.HITC() + I18n.format("info.de.capacitorMode" + ItemNBTHelper.getShort(stack, "Mode", (short) 0) + ".txt"));
            //InfoHelper.addLore(stack, tooltip);
        }

        ToolBase.holdCTRLForUpgrades(tooltip, stack);

        InfoHelper.addEnergyInfo(stack, tooltip);
        if (stack.getItemDamage() == 2){
            tooltip.add(InfoHelper.HITC()+I18n.format("info.creativeCapacitor.txt")+" "+ Utils.formatNumber(Integer.MAX_VALUE/2)+" RF/t");
        }
    }

    //endregion

    //region IUpgradable

    @Override
    public List<String> getValidUpgrades(ItemStack stack) {
        return new ArrayList<String>() {{ add(ToolUpgrade.RF_CAPACITY); }};
    }

    @Override
    public int getMaxUpgradeLevel(ItemStack stack, String upgrade) {
        return stack.getItemDamage() == 0 ? 3 :
                stack.getItemDamage() == 1 ? 6 : 0;
    }

    //endregion
}
