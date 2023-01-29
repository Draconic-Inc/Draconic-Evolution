package com.brandon3055.draconicevolution.common.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import cofh.api.energy.IEnergyContainerItem;

import com.brandon3055.draconicevolution.common.inventory.SlotChargable;
import com.brandon3055.draconicevolution.common.tileentities.TileEnergyInfuser;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ContainerEnergyInfuser extends ContainerDataSync {

    private TileEnergyInfuser tile;
    private EntityPlayer player;
    private int lastTickEnergyStorage = -1;

    public ContainerEnergyInfuser(InventoryPlayer invPlayer, TileEnergyInfuser tile) {
        this.tile = tile;
        this.player = invPlayer.player;

        for (int x = 0; x < 9; x++) {
            addSlotToContainer(new Slot(invPlayer, x, 8 + 18 * x, 116));
        }

        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                addSlotToContainer(new Slot(invPlayer, x + y * 9 + 9, 8 + 18 * x, 58 + y * 18));
            }
        }

        addSlotToContainer(new SlotChargable(tile, 0, 80, 22));
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

            if (i >= 36) { // To player
                if (!mergeItemStack(stack, 0, 36, false)) {
                    return null;
                }
            } else if (stack.stackSize != 1 || !(stack.getItem() instanceof IEnergyContainerItem)
                    || !mergeItemStack(stack, 36, 37, false)) {
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
        if (tile.energy.getEnergyStored() != lastTickEnergyStorage) {
            sendObjectToClient(null, 0, tile.energy.getEnergyStored());
            lastTickEnergyStorage = tile.energy.getEnergyStored();
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void receiveSyncData(int index, int value) {
        tile.energy.setEnergyStored(value);
    }
}
