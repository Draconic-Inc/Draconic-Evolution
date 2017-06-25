package com.brandon3055.draconicevolution.inventory;

import com.brandon3055.brandonscore.inventory.ContainerBCBase;
import com.brandon3055.brandonscore.inventory.SlotCheckValid;
import com.brandon3055.draconicevolution.blocks.tileentity.TileDissEnchanter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

/**
 * Created by brandon3055 on 30/3/2016.
 */
public class ContainerDissEnchanter extends ContainerBCBase<TileDissEnchanter> {

    public ContainerDissEnchanter(InventoryPlayer invPlayer, TileDissEnchanter tile) {
        super(invPlayer.player, tile);

        addPlayerSlots(8, 60);

        addSlotToContainer(new SlotCheckValid(tile, 0, 27, 23));
        addSlotToContainer(new SlotCheckValid(tile, 1, 76, 23));
        addSlotToContainer(new SlotCheckValid(tile, 2, 134, 23));

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
                    return null;
                }
            }
            else {
                if (stack.getItem() == Items.BOOK) {
                    if (!mergeItemStack(stack, 36, 36 + 2, false)) {
                        return null;
                    }
                }
                else if (!tile.isItemValidForSlot(0, stack) || !mergeItemStack(stack, 36, 36 + tile.getSizeInventory(), false)) {
                    return null;
                }
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

        return null;
    }
}
