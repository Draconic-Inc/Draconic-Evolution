package com.brandon3055.draconicevolution.common.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import com.brandon3055.draconicevolution.common.tileentities.TileContainerTemplate;

public class ContainerTemplate extends Container {

    private TileContainerTemplate tile;
    private EntityPlayer player;

    public ContainerTemplate(InventoryPlayer invPlayer, TileContainerTemplate tile) {
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

        for (int x = 0; x < 9; x++) {
            addSlotToContainer(new Slot(tile, x, 8 + 18 * x, 10));
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
}
