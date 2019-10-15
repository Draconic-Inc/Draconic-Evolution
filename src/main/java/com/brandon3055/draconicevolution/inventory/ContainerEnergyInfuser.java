package com.brandon3055.draconicevolution.inventory;

import com.brandon3055.brandonscore.inventory.ContainerBCBase;
import com.brandon3055.brandonscore.inventory.SlotCheckValid;
import com.brandon3055.draconicevolution.blocks.tileentity.TileEnergyInfuser;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

/**
 * Created by brandon3055 on 31/05/2016.
 */
public class ContainerEnergyInfuser extends ContainerBCBase<TileEnergyInfuser> {

    public ContainerEnergyInfuser(EntityPlayer player, TileEnergyInfuser tile) {
        super(player, tile);
        this.addPlayerSlots(8, 58);
        addSlotToContainer(new SlotCheckValid(tile.itemHandler, 0, 80, 22));
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
            else if (!mergeItemStack(stack, 36, 36 + tile.itemHandler.getSlots(), false)) {
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
