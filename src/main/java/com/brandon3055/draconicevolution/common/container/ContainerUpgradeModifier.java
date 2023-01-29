package com.brandon3055.draconicevolution.common.container;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.brandon3055.draconicevolution.common.ModItems;
import com.brandon3055.draconicevolution.common.tileentities.TileUpgradeModifier;
import com.brandon3055.draconicevolution.common.utills.IUpgradableItem;
import com.brandon3055.draconicevolution.common.utills.IUpgradableItem.EnumUpgrade;

public class ContainerUpgradeModifier extends ContainerDataSync {

    private static final Item[] CORES_INDEX = { ModItems.draconicCore, ModItems.wyvernCore, ModItems.awakenedCore,
            ModItems.chaoticCore };
    private TileUpgradeModifier tile;
    private EntityPlayer player;
    private boolean slotsActive = true;

    public ContainerUpgradeModifier(InventoryPlayer invPlayer, TileUpgradeModifier tile) {
        this.tile = tile;
        this.player = invPlayer.player;

        for (int x = 0; x < 9; x++) {
            addSlotToContainer(new Slot(invPlayer, x, 8 + 18 * x, 167));
        }

        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                addSlotToContainer(new Slot(invPlayer, x + y * 9 + 9, 8 + 18 * x, 111 + y * 18));
            }
        }

        addSlotToContainer(new SlotUpgradable(tile, 0, 112, 48));
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        for (int i = 0; i < this.crafters.size(); ++i) {
            ICrafting icrafting = (ICrafting) this.crafters.get(i);
            icrafting.sendProgressBarUpdate(this, 0, 0);
        }
        updateSlotState();
    }

    @Override
    public void updateProgressBar(int index, int value) {
        super.updateProgressBar(index, value);
        if (index == 0) updateSlotState();
    }

    private void updateSlotState() {
        if (tile.getStackInSlot(0) != null && tile.getStackInSlot(0).getItem() instanceof IUpgradableItem
                && slotsActive) {
            for (Object o : inventorySlots) {
                if (o instanceof Slot && !(o instanceof SlotUpgradable)) ((Slot) o).xDisplayPosition += 1000;
            }
            slotsActive = false;
        } else if ((tile.getStackInSlot(0) == null || !(tile.getStackInSlot(0).getItem() instanceof IUpgradableItem))
                && !slotsActive) {
                    for (Object o : inventorySlots) {
                        if (o instanceof Slot && !(o instanceof SlotUpgradable)) ((Slot) o).xDisplayPosition -= 1000;
                    }
                    slotsActive = true;
                }
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return tile.isUseableByPlayer(player);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int i) {
        Slot slot = getSlot(i);

        if (slot != null && slot.getHasStack()) {
            ItemStack stack = slot.getStack();
            ItemStack result = stack.copy();

            if (i >= 36) {
                if (!mergeItemStack(stack, 0, 36, false)) {
                    return null;
                }
            } else if (!isStackValidForInventory(stack, 0)
                    || !mergeItemStack(stack, 36, 36 + tile.getSizeInventory(), false)) {
                        return null;
                    }

            if (stack.stackSize == 0) {
                slot.putStack(null);
            } else {
                slot.onSlotChanged();
            }

            slot.onPickupFromSlot(player, stack);

            return result;
        }

        return null;
    }

    private boolean isStackValidForInventory(ItemStack stack, int slot) {
        return true;
    }

    @Override
    public void receiveSyncData(int index, int value) {
        EnumUpgrade upgrade = EnumUpgrade.getUpgradeByIndex(index);
        int coreTier = value / 2;
        boolean addCore = value % 2 == 0;
        ItemStack stack = tile.getStackInSlot(0);
        if (upgrade == null || stack == null
                || !(stack.getItem() instanceof IUpgradableItem)
                || coreTier < 0
                || coreTier > 3
                || (upgrade.getCoresApplied(stack)[coreTier] <= 0 && !addCore))
            return;
        handleCoreTransaction(upgrade, coreTier, addCore, (IUpgradableItem) stack.getItem(), stack);
    }

    private void handleCoreTransaction(EnumUpgrade upgrade, int coreTier, boolean addCoreElseRemove,
            IUpgradableItem upgradableItem, ItemStack stack) {
        int coreSlots = upgradableItem.getUpgradeCap(stack);
        int totalCores = 0;
        int[] coresApplied = upgrade.getCoresApplied(stack);
        for (EnumUpgrade u : upgradableItem.getUpgrades(stack)) totalCores += u.getCoresApplied(stack)[coreTier];

        if (addCoreElseRemove) {
            if (!player.inventory.hasItem(CORES_INDEX[coreTier]) || totalCores >= coreSlots
                    || upgrade.getUpgradePoints(stack) >= upgradableItem.getMaxUpgradePoints(upgrade.index, stack))
                return;
            coresApplied[coreTier]++;
            player.inventory.consumeInventoryItem(CORES_INDEX[coreTier]);
            upgrade.setCoresApplied(stack, coresApplied);
            upgrade.onAppliedToItem(stack);
        } else {
            if (coresApplied[coreTier] <= 0) return;
            coresApplied[coreTier]--;
            upgrade.setCoresApplied(stack, coresApplied);
            upgrade.onRemovedFromItem(stack);
            if (!player.inventory.addItemStackToInventory(new ItemStack(CORES_INDEX[coreTier]))) {
                EntityItem entityItem = new EntityItem(
                        player.worldObj,
                        player.posX,
                        player.posY,
                        player.posZ,
                        new ItemStack(CORES_INDEX[coreTier]));
                if (!player.worldObj.isRemote) player.worldObj.spawnEntityInWorld(entityItem);
            }
        }
    }

    public static class SlotUpgradable extends Slot {

        public SlotUpgradable(IInventory inventory1, int slot, int x, int y) {
            super(inventory1, slot, x, y);
        }

        // Some people think this block makes a nice display stand... And i cant disagree.
        @Override
        public boolean isItemValid(ItemStack stack) {
            return super.isItemValid(stack);
            // return stack != null && stack.getItem() instanceof IUpgradableItem;
        }
    }
}
