package com.brandon3055.draconicevolution.inventory;

import com.brandon3055.brandonscore.inventory.ContainerBCTile;
import com.brandon3055.brandonscore.inventory.ContainerBCore;
import com.brandon3055.brandonscore.inventory.SlotCheckValid;
import com.brandon3055.draconicevolution.init.DEContent;
import com.brandon3055.draconicevolution.blocks.tileentity.TileDissEnchanter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketBuffer;

import javax.annotation.Nullable;

/**
 * Created by brandon3055 on 30/3/2016.
 */
public class ContainerDissEnchanter extends ContainerBCTile<TileDissEnchanter> {

    public ContainerDissEnchanter(int windowId, PlayerInventory playerInv, PacketBuffer extraData) {
        this(DEContent.container_dissenchanter, windowId, playerInv, getClientTile(extraData));
    }

    public ContainerDissEnchanter(@Nullable ContainerType<?> type, int windowId, PlayerInventory player, TileDissEnchanter tile) {
        super(type, windowId, player, tile);
        addPlayerSlots(8, 60);

        addSlot(new SlotCheckValid(tile.itemHandler, 0, 27, 23));
        addSlot(new SlotCheckValid(tile.itemHandler, 1, 76, 23));
        addSlot(new SlotCheckValid(tile.itemHandler, 2, 134, 23));
    }

    @Override
    public boolean stillValid(PlayerEntity player) {
        return true;//tile.isUsableByPlayer(player);
    }

    @Override
    public ItemStack quickMoveStack(PlayerEntity player, int i) {
        Slot slot = getSlot(i);

        if (slot != null && slot.hasItem()) {
            ItemStack stack = slot.getItem();
            ItemStack result = stack.copy();

            if (i >= 36) {
                if (!moveItemStackTo(stack, 0, 36, false)) {
                    return ItemStack.EMPTY;
                }
            }
            else {
                if (stack.getItem() == Items .BOOK) {
                    if (!moveItemStackTo(stack, 36, 36 + 2, false)) {
                        return ItemStack.EMPTY;
                    }
                }
                else if (!tile.isItemValidForSlot(0, stack) || !moveItemStackTo(stack, 36, 36 + tile.itemHandler.getSlots(), false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (stack.getCount() == 0) {
                slot.set(ItemStack.EMPTY);
            }
            else {
                slot.setChanged();
            }

            slot.onTake(player, stack);

            return result;
        }

        return ItemStack.EMPTY;
    }
}
