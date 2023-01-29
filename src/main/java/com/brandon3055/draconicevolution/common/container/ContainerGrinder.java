package com.brandon3055.draconicevolution.common.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import com.brandon3055.draconicevolution.common.inventory.SlotItemValid;
import com.brandon3055.draconicevolution.common.tileentities.TileGrinder;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ContainerGrinder extends ContainerDataSync {

    private TileGrinder tile;
    private int energyCach = -1;
    private int energy2Cach = -1;
    private int burnCach = -1;
    private int burnRemainingCach = -1;

    public ContainerGrinder(InventoryPlayer invPlayer, TileGrinder tile) {
        this.tile = tile;

        for (int x = 0; x < 9; x++) {
            addSlotToContainer(new Slot(invPlayer, x, 8 + 18 * x, 138));
        }

        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                addSlotToContainer(new Slot(invPlayer, x + y * 9 + 9, 8 + 18 * x, 80 + y * 18));
            }
        }
        if (!tile.isExternallyPowered()) addSlotToContainer(new SlotItemValid(tile, 0, 64, 35, true));
        else addSlotToContainer(new SlotItemValid(tile, 0, -10000, -10000, true));
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
            } else if (TileGrinder.getItemBurnTime(stack) == 0
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

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        if (energyCach != tile.externalInputBuffer.getEnergyStored())
            energyCach = (Integer) sendObjectToClient(null, 0, tile.externalInputBuffer.getEnergyStored());
        if (energy2Cach != tile.internalGenBuffer.getEnergyStored())
            energy2Cach = (Integer) sendObjectToClient(null, 1, tile.internalGenBuffer.getEnergyStored());
        if (burnCach != tile.burnTime) burnCach = (Integer) sendObjectToClient(null, 2, tile.burnTime);
        if (burnRemainingCach != tile.burnTimeRemaining)
            burnRemainingCach = (Integer) sendObjectToClient(null, 3, tile.burnTimeRemaining);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void receiveSyncData(int index, int i) {
        if (index == 0) tile.externalInputBuffer.setEnergyStored(i);
        else if (index == 1) tile.internalGenBuffer.setEnergyStored(i);
        else if (index == 2) tile.burnTime = i;
        else if (index == 3) tile.burnTimeRemaining = i;
    }
}
