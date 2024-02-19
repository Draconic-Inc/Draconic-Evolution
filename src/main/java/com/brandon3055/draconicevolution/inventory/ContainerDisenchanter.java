package com.brandon3055.draconicevolution.inventory;

import codechicken.lib.data.MCDataInput;
import com.brandon3055.brandonscore.inventory.ContainerBCTile;
import com.brandon3055.brandonscore.inventory.SlotCheckValid;
import com.brandon3055.draconicevolution.blocks.tileentity.TileDisenchanter;
import com.brandon3055.draconicevolution.init.DEContent;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import javax.annotation.Nullable;

/**
 * Created by brandon3055 on 30/3/2016.
 */
public class ContainerDisenchanter extends ContainerBCTile<TileDisenchanter> {

    public ContainerDisenchanter(int windowId, Inventory playerInv, FriendlyByteBuf extraData) {
        this(DEContent.MENU_DISENCHANTER.get(), windowId, playerInv, getClientTile(playerInv, extraData));
    }

    public ContainerDisenchanter(@Nullable MenuType<?> type, int windowId, Inventory player, TileDisenchanter tile) {
        super(type, windowId, player, tile);
//        addPlayerSlots(8, 60);

        addSlot(new SlotCheckValid(tile.itemHandler, 0, 27, 23));
        addSlot(new SlotCheckValid(tile.itemHandler, 1, 76, 23));
        addSlot(new SlotCheckValid(tile.itemHandler, 2, 134, 23));
    }

    @Override
    public boolean stillValid(Player player) {
        return true;//tile.isUsableByPlayer(player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int i) {
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
                if (stack.getItem() == Items.BOOK) {
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
