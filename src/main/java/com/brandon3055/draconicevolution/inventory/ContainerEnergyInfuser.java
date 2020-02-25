package com.brandon3055.draconicevolution.inventory;

import com.brandon3055.brandonscore.inventory.ContainerBCBase;
import com.brandon3055.brandonscore.inventory.SlotCheckValid;
import com.brandon3055.draconicevolution.DEContent;
import com.brandon3055.draconicevolution.blocks.tileentity.TileEnergyInfuser;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;

import javax.annotation.Nullable;

/**
 * Created by brandon3055 on 31/05/2016.
 */
public class ContainerEnergyInfuser extends ContainerBCBase<TileEnergyInfuser> {

    public ContainerEnergyInfuser(int windowId, PlayerInventory playerInv, PacketBuffer extraData) {
        this(DEContent.container_energy_infuser, windowId, playerInv.player, getClientTile(extraData));
    }

    public ContainerEnergyInfuser(@Nullable ContainerType<?> type, int windowId, PlayerEntity player, TileEnergyInfuser tile) {
        super(type, windowId, player, tile);
        this.addPlayerSlots(8, 58);
        addSlot(new SlotCheckValid(tile.itemHandler, 0, 80, 22));
    }

//    public ContainerEnergyInfuser(PlayerEntity player, TileEnergyInfuser tile) {
//        super(player, tile);
//
//    }

    @Override
    public ItemStack transferStackInSlot(PlayerEntity player, int i) {
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
