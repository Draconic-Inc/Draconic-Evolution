package com.brandon3055.draconicevolution.inventory;

import com.brandon3055.brandonscore.inventory.PlayerSlot;
import com.brandon3055.brandonscore.inventory.SlotCheckValid;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by brandon3055 on 21/12/2017.
 */
public class ContainerJunkFilter extends Container {

    private final ItemStack stack;
    private final PlayerEntity player;
    private final PlayerSlot slot;
    private final IItemHandler itemHandler;

//    public ContainerJunkFilter(int windowId, PlayerInventory playerInv, PacketBuffer extraData) {
//        this(DEContent.container_junk_filter, windowId, playerInv.player, getClientTile(extraData));
//    }

    public ContainerJunkFilter(@Nullable ContainerType<?> type, int id, PlayerEntity player, PlayerSlot slot, IItemHandler itemHandler) {
        super(type, id);
        this.player = player;
        this.slot = slot;
        this.itemHandler = itemHandler;
        this.stack = slot.getStackInSlot(player);
        addPlayerSlots(8, 29 + ((itemHandler.getSlots() / 9) * 18), 4);
        addJunkSlots(8, 21);
    }

//    public ContainerJunkFilter(PlayerEntity player, PlayerSlot slot, IItemHandler itemHandler) {
//        this.player = player;
//        this.slot = slot;
//        this.itemHandler = itemHandler;
//        this.stack = slot.getStackInSlot(player);
//        addPlayerSlots(8, 29 + ((itemHandler.getSlots() / 9) * 18), 4);
//        addJunkSlots(8, 21);
//    }

    @Override
    public void detectAndSendChanges() {
        if (stack != slot.getStackInSlot(player) && !player.world.isRemote) {
            player.closeScreen();
        }
        super.detectAndSendChanges();
    }

    @Override
    public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, PlayerEntity player) {
        if (slotId >= 0 && slotId < inventorySlots.size() && inventorySlots.get(slotId).getStack() == stack) {
            return ItemStack.EMPTY;
        }
        return super.slotClick(slotId, dragType, clickTypeIn, player);
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        return true;
    }

    @Nullable
    @Override
    public ItemStack transferStackInSlot(PlayerEntity playerIn, int i) {
        Slot slot = getSlot(i);

        if (slot != null && slot.getHasStack()) {
            ItemStack stack = slot.getStack();
            ItemStack result = stack.copy();

            //Move To Player Inventory
            if (i >= 36) {
                if (!mergeItemStack(stack, 0, 36, false)) {
                    return ItemStack.EMPTY;
                }
            }
            //Move From Player inventory
            else {
                if (!mergeItemStack(stack, 36, 36 + itemHandler.getSlots(), false)) {
                    return ItemStack.EMPTY;
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

        return ItemStack.EMPTY;
    }

    //The following are some safety checks to handle conditions vanilla normally does not have to deal with.

    @Override
    public void putStackInSlot(int slotID, ItemStack stack) {
        Slot slot = this.getSlot(slotID);
        if (slot != null) {
            slot.putStack(stack);
        }
    }

    @Override
    public Slot getSlot(int slotId) {
        if (slotId < inventorySlots.size() && slotId >= 0) {
            return inventorySlots.get(slotId);
        }
        return null;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void setAll(List<ItemStack> stacks) {
        for (int i = 0; i < stacks.size(); ++i) {
            Slot slot = getSlot(i);
            if (slot != null) {
                slot.putStack(stacks.get(i));
            }
        }
    }

    public void addPlayerSlots(int posX, int posY, int hotbarSpacing) {
        for (int x = 0; x < 9; x++) {
            addSlot(new SlotCheckValid.IInv(player.inventory, x, posX + 18 * x, posY + 54 + hotbarSpacing));
        }

        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                addSlot(new SlotCheckValid.IInv(player.inventory, x + y * 9 + 9, posX + 18 * x, posY + y * 18));
            }
        }
    }

    public void addJunkSlots(int posX, int posY) {
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            int x = i % 9 * 18;
            int y = i / 9 * 18;
            addSlot(new SlotItemHandler(itemHandler, i, posX + x, posY + y));//x + y * 9 + 9
        }
    }
}
