package com.brandon3055.draconicevolution.common.container;

import com.brandon3055.brandonscore.common.utills.ItemNBTHelper;
import com.brandon3055.draconicevolution.common.inventory.SlotOutput;
import com.brandon3055.draconicevolution.common.tileentities.TileDissEnchanter;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerDissEnchanter extends Container {

    private TileDissEnchanter tile;
    private EntityPlayer player;
    private ItemStack cachIn0;
    private ItemStack cachIn1;
    private ItemStack cachIn2;
    private boolean nullCheck0 = false;
    private boolean nullCheck1 = false;
    private boolean nullCheck2 = false;

    public ContainerDissEnchanter(InventoryPlayer invPlayer, TileDissEnchanter tile) {
        this.tile = tile;
        this.player = invPlayer.player;

        for (int x = 0; x < 9; x++) {
            addSlotToContainer(new Slot(invPlayer, x, 8 + 18 * x, 118));
        }

        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                addSlotToContainer(new Slot(invPlayer, x + y * 9 + 9, 8 + 18 * x, 60 + y * 18));
            }
        }

        addSlotToContainer(new SlotEnchantedItem(tile, 0, 27, 23));
        addSlotToContainer(new SlotBook(tile, 1, 76, 23));
        addSlotToContainer(new SlotOutput(tile, 2, 134, 23));
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
            } else if ((!isStackValidForInventory(stack, 0) || !mergeItemStack(stack, 36, 37, false))
                    && (!isStackValidForInventory(stack, 1) || !mergeItemStack(stack, 37, 38, false))) {
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
        if (slot == 0 && EnchantmentHelper.getEnchantments(stack).size() > 0) return true;
        if (slot == 1 && stack.getItem().equals(Items.book)) return true;
        return false;
    }

    public class SlotBook extends Slot {
        public SlotBook(IInventory inventory, int id, int x, int y) {
            super(inventory, id, x, y);
        }

        @Override
        public boolean isItemValid(ItemStack stack) {
            return stack.getItem().equals(Items.book);
        }

        @Override
        public int getSlotStackLimit() {
            return 64;
        }
    }

    public class SlotEnchantedItem extends Slot {
        public SlotEnchantedItem(IInventory inventory, int id, int x, int y) {
            super(inventory, id, x, y);
        }

        @Override
        public boolean isItemValid(ItemStack stack) {
            return EnchantmentHelper.getEnchantments(stack).size() > 0
                    || ItemNBTHelper.getInteger(stack, "RepairCost", 0) > 0;
        }

        @Override
        public int getSlotStackLimit() {
            return 1;
        }
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        ItemStack stack0 = tile.getStackInSlot(0);
        ItemStack stack1 = tile.getStackInSlot(1);
        ItemStack stack2 = tile.getStackInSlot(2);
        if ((stack0 == null) != nullCheck0) {
            tile.onInventoryChanged();
            nullCheck0 = (stack0 == null);
        }
        if ((stack1 == null) != nullCheck1) {
            tile.onInventoryChanged();
            nullCheck1 = (stack1 == null);
        }
        if ((stack2 == null) != nullCheck2) {
            tile.onInventoryChanged();
            nullCheck2 = (stack2 == null);
        }

        if (stack0 != null && !ItemStack.areItemStacksEqual(stack0, cachIn0)) {
            cachIn0 = stack0.copy();
            tile.onInventoryChanged();
        }
        if (stack1 != null && !ItemStack.areItemStacksEqual(stack1, cachIn1)) {
            cachIn1 = stack1.copy();
            tile.onInventoryChanged();
        }
        if (stack2 != null && !ItemStack.areItemStacksEqual(stack2, cachIn2)) {
            cachIn2 = stack2.copy();
            tile.onInventoryChanged();
        }
    }

    public TileDissEnchanter getTile() {
        return tile;
    }
}
