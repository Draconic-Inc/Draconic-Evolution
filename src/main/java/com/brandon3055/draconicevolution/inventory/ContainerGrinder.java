package com.brandon3055.draconicevolution.inventory;

import com.brandon3055.brandonscore.inventory.ContainerBCBase;
import com.brandon3055.brandonscore.inventory.SlotCheckValid;
import com.brandon3055.draconicevolution.blocks.tileentity.TileGrinder;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

/**
 * Created by brandon3055 on 30/3/2016.
 */
public class ContainerGrinder extends ContainerBCBase<TileGrinder> {

    public ContainerGrinder(InventoryPlayer invPlayer, TileGrinder tile) {
        super(invPlayer.player, tile);

        for (int x = 0; x < 9; x++) {
            addSlotToContainer(new Slot(invPlayer, x, 8 + 18 * x, 138));
        }

        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                addSlotToContainer(new Slot(invPlayer, x + y * 9 + 9, 8 + 18 * x, 80 + y * 18));
            }
        }

        addSlotToContainer(new SlotCheckValid.IInv(tile, 0, 97, 36));
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return tile.isUsableByPlayer(player);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int i) {
        Slot slot = getSlot(i);

        if (slot != null && slot.getHasStack()) {
            ItemStack stack = slot.getStack();
            ItemStack result = stack.copy();

            if (i >= 36) {
                if (!mergeItemStack(stack, 0, 36, false)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (!tile.isItemValidForSlot(0, stack) || !mergeItemStack(stack, 36, 36 + tile.getSizeInventory(), false)) {
                return ItemStack.EMPTY;
            }

            if (stack.getCount() == 0) {
                slot.putStack(ItemStack.EMPTY);
            }
            else {
                slot.onSlotChanged();
            }

            slot.onTake(player, stack);

            return result;
        }

        return ItemStack.EMPTY;
    }
}
