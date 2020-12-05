package com.brandon3055.draconicevolution.inventory;

import com.brandon3055.brandonscore.inventory.ContainerBCTile;
import com.brandon3055.brandonscore.inventory.ContainerBCore;
import com.brandon3055.draconicevolution.init.DEContent;
import com.brandon3055.draconicevolution.blocks.tileentity.TileCraftingCore;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nullable;

/**
 * Created by brandon3055 on 30/3/2016.
 */
public class ContainerFusionCraftingCore extends ContainerBCTile<TileCraftingCore> {

    public ContainerFusionCraftingCore(int windowId, PlayerInventory playerInv, PacketBuffer extraData) {
        this(DEContent.container_fusion_crafting_core, windowId, playerInv, getClientTile(extraData));
    }

    public ContainerFusionCraftingCore(@Nullable ContainerType<?> type, int windowId, PlayerInventory player, TileCraftingCore tile) {
        super(type, windowId, player, tile);

        addPlayerSlots(10, 119);
        addSlot(new SlotItemHandler(tile.itemHandler, 0, 82, 26));
        addSlot(new OutputSlot(tile.itemHandler, 1, 82, 70));
    }


    @Override
    public boolean canInteractWith(PlayerEntity player) {
        return true;//tile.isUsableByPlayer(player);
    }

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
            else if (!tile.itemHandler.isItemValid(0, stack) || !mergeItemStack(stack, 36, 36 + tile.itemHandler.getSlots(), false)) {
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

    public class OutputSlot extends SlotItemHandler {

        public OutputSlot(IItemHandler inventoryIn, int index, int xPosition, int yPosition) {
            super(inventoryIn, index, xPosition, yPosition);
        }

        @Override
        public boolean isItemValid(@Nullable ItemStack stack) {
            return false;
        }
    }
}
