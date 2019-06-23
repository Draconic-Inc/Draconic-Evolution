package com.brandon3055.draconicevolution.items.tools;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import com.brandon3055.brandonscore.items.ItemEnergyBase;
import com.brandon3055.brandonscore.lib.ChatHelper;
import com.brandon3055.brandonscore.lib.EnergyHelper;
import com.brandon3055.brandonscore.utils.InfoHelper;
import com.brandon3055.brandonscore.utils.ItemNBTHelper;
import com.brandon3055.brandonscore.utils.Utils;
import com.brandon3055.draconicevolution.DEConfig;
import com.brandon3055.draconicevolution.DEFeatures;
import com.brandon3055.draconicevolution.api.IInvCharge;
import com.brandon3055.draconicevolution.api.itemupgrade.IUpgradableItem;
import com.brandon3055.draconicevolution.api.itemupgrade.UpgradeHelper;
import com.brandon3055.draconicevolution.entity.EntityPersistentItem;
import com.brandon3055.draconicevolution.integration.BaublesHelper;
import com.brandon3055.draconicevolution.integration.ModHelper;
import com.brandon3055.draconicevolution.items.ToolUpgrade;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by brandon3055 on 31/05/2016.
 */
@Optional.Interface(iface = "baubles.api.IBauble", modid = "baubles")
public class DraconiumCapacitor extends ItemEnergyBase implements IInvCharge, IUpgradableItem, IBauble {

    public static final int wyvernTransfer = 8000000;
    public static final int draconicTransfer = 64000000;

    public DraconiumCapacitor() {
        this.setHasSubtypes(true);
        this.addName(0, "wyvern").addName(1, "draconic").addName(2, "creative");
        this.setMaxStackSize(1);
    }

    //region Item

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems) {
        if (isInCreativeTab(tab)) {
            subItems.add(new ItemStack(DEFeatures.draconiumCapacitor, 1, 0));
            ItemStack wyvernCharged = new ItemStack(DEFeatures.draconiumCapacitor, 1, 0);
            setEnergy(wyvernCharged, getMaxEnergyStored(wyvernCharged));
            subItems.add(wyvernCharged);

            ItemStack uberWyvern = new ItemStack(DEFeatures.draconiumCapacitor, 1, 0);
            for (String upgrade : getValidUpgrades(uberWyvern)) {
                UpgradeHelper.setUpgradeLevel(uberWyvern, upgrade, getMaxUpgradeLevel(uberWyvern, upgrade));
            }
            setEnergy(uberWyvern, getCapacity(uberWyvern));
            subItems.add(uberWyvern);

            subItems.add(new ItemStack(DEFeatures.draconiumCapacitor, 1, 1));
            ItemStack draconicCharged = new ItemStack(DEFeatures.draconiumCapacitor, 1, 1);
            setEnergy(draconicCharged, getMaxEnergyStored(wyvernCharged));
            subItems.add(draconicCharged);

            ItemStack uberDraconic = new ItemStack(DEFeatures.draconiumCapacitor, 1, 1);
            for (String upgrade : getValidUpgrades(uberDraconic)) {
                UpgradeHelper.setUpgradeLevel(uberDraconic, upgrade, getMaxUpgradeLevel(uberDraconic, upgrade));
            }
            setEnergy(uberDraconic, getCapacity(uberDraconic));
            subItems.add(uberDraconic);

            subItems.add(new ItemStack(DEFeatures.draconiumCapacitor, 1, 2));
        }
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
                return DEConfig.wyvernFluxCapBaseCap + (upgrade * (DEConfig.wyvernFluxCapBaseCap / 2));
            case 1:
                return DEConfig.draconicFluxCapBaseCap + (upgrade * (DEConfig.draconicFluxCapBaseCap / 2));
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
        if (container.getItemDamage() == 2) {
            return maxReceive;
        }

        return super.receiveEnergy(container, maxReceive, simulate);
    }

    @Override
    public int extractEnergy(ItemStack container, int maxExtract, boolean simulate) {
        if (container.getItemDamage() == 2) {
            return maxExtract;
        }

        return super.extractEnergy(container, maxExtract, simulate);
    }

    //endregion

    //region Activation

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (player.isSneaking()) {
            int mode = ItemNBTHelper.getShort(stack, "Mode", (short) 0);
            int newMode = mode == 4 ? 0 : mode + 1;
            ItemNBTHelper.setShort(stack, "Mode", (short) newMode);
            if (world.isRemote) {
                ChatHelper.indexedMsg(player, InfoHelper.ITC() + I18n.format("info.de.capacitorMode.txt") + ": " + InfoHelper.HITC() + I18n.format("info.de.capacitorMode" + ItemNBTHelper.getShort(stack, "Mode", (short) 0) + ".txt"));
            }
        }
        return new ActionResult<>(EnumActionResult.PASS, stack);
    }

    @Override
    public void onUpdate(ItemStack container, World world, Entity entity, int itemSlot, boolean isSelected) {
        if (!(entity instanceof EntityPlayer)) {
            return;
        }
        if (ModHelper.isBaublesInstalled) {
            updateEnergy(container, (EntityPlayer) entity, getBaubles((EntityPlayer) entity));
        }
        else {
            updateEnergy(container, (EntityPlayer) entity, new ArrayList<>());
        }
    }

    public void updateEnergy(ItemStack capacitor, EntityPlayer player, List<ItemStack> stacks) {
        int mode = ItemNBTHelper.getShort(capacitor, "Mode", (short) 0);

        if (mode == 0) {
            return;
        }

        if (mode == 4) { //Charge All
            stacks.addAll(player.inventory.armorInventory);
            stacks.addAll(player.inventory.mainInventory);
            stacks.addAll(player.inventory.offHandInventory);
        }
        else {
            if (mode == 1 || mode == 3) { //Charge Armor
                stacks.addAll(player.inventory.armorInventory);
            }
            else {
                stacks.clear(); // Dont charge baubles
            }
            if (mode == 2 || mode == 3) { //Charge Held Items
                stacks.add(player.getHeldItemOffhand());
                stacks.add(player.getHeldItemMainhand());
            }
        }

        for (ItemStack stack : stacks) {
            int max = Math.min(getEnergyStored(capacitor), getMaxExtract(capacitor));

            if (EnergyHelper.canReceiveEnergy(stack)) {
                Item item = stack.getItem();

                if (item instanceof IInvCharge && !((IInvCharge) item).canCharge(stack, player)) {
                    continue;
                }

                extractEnergy(capacitor, EnergyHelper.insertEnergy(stack, max, false), false);
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
        return ItemNBTHelper.getShort(stack, "Mode", (short) 0) > 0;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, World playerIn, List<String> tooltip, ITooltipFlag advanced) {
        if (InfoHelper.holdShiftForDetails(tooltip)) {
            tooltip.add(I18n.format("info.de.changwMode.txt"));
            tooltip.add(InfoHelper.ITC() + I18n.format("info.de.capacitorMode.txt") + ": " + InfoHelper.HITC() + I18n.format("info.de.capacitorMode" + ItemNBTHelper.getShort(stack, "Mode", (short) 0) + ".txt"));
            //InfoHelper.addLore(stack, tooltip);
        }

        ToolBase.holdCTRLForUpgrades(tooltip, stack);

        InfoHelper.addEnergyInfo(stack, tooltip);
        if (stack.getItemDamage() == 2) {
            tooltip.add(InfoHelper.HITC() + I18n.format("info.creativeCapacitor.txt") + " " + Utils.formatNumber(Integer.MAX_VALUE / 2) + " RF/t");
        }
    }

    //endregion

    //region IUpgradable

    @Override
    public List<String> getValidUpgrades(ItemStack stack) {
        return new ArrayList<String>() {{
            add(ToolUpgrade.RF_CAPACITY);
        }};
    }

    @Override
    public int getMaxUpgradeLevel(ItemStack stack, String upgrade) {
        return stack.getItemDamage() == 0 ? 3 : stack.getItemDamage() == 1 ? 4 : 0;
    }

    //endregion

    @Override
    @Optional.Method(modid = "baubles")
    public BaubleType getBaubleType(ItemStack itemstack) {
        return BaubleType.TRINKET;
    }

    @Override
    @Optional.Method(modid = "baubles")
    public void onWornTick(ItemStack itemstack, EntityLivingBase player) {
        if (!(player instanceof EntityPlayer)) return;
        updateEnergy(itemstack, (EntityPlayer) player, getBaubles((EntityPlayer) player));
    }

    private static List<ItemStack> getBaubles(EntityPlayer entity) {
        return BaublesHelper.getBaubles(entity);
    }
}
