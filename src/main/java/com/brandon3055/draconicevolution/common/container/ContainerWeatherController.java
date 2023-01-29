package com.brandon3055.draconicevolution.common.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import com.brandon3055.draconicevolution.common.inventory.SlotItemValid;
import com.brandon3055.draconicevolution.common.tileentities.TileWeatherController;

public class ContainerWeatherController extends Container {

    private TileWeatherController tileWC;

    public ContainerWeatherController(InventoryPlayer invPlayer, TileWeatherController tileWC) {
        this.tileWC = tileWC;

        for (int x = 0; x < 9; x++) {
            addSlotToContainer(new Slot(invPlayer, x, 8 + 18 * x, 119));
        }

        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                addSlotToContainer(new Slot(invPlayer, x + y * 9 + 9, 8 + 18 * x, 61 + y * 18));
            }
        }

        addSlotToContainer(new SlotItemValid(tileWC, 0, 47, 15, Items.emerald));
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return tileWC.isUseableByPlayer(player);
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
            } else if (!stack.getItem().equals(Items.emerald)
                    || !mergeItemStack(stack, 36, 36 + tileWC.getSizeInventory(), false)) {
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

    public TileWeatherController getTileWC() {
        return tileWC;
    }
}
