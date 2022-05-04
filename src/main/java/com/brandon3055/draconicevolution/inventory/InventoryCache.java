package com.brandon3055.draconicevolution.inventory;

import codechicken.lib.util.ArrayUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by brandon3055 on 21/07/2016.
 * A simple item cache in the form of an IInventory. Currently only used by the Debugger
 */
public class InventoryCache implements IInventory {

    //public ItemStackHandler Maby useful for something? But not this
    private ItemStack[] inventoryStacks;

    public InventoryCache(int inventorySize) {
        this.inventoryStacks = new ItemStack[inventorySize];
    }

    @Override
    public int getContainerSize() {
        return inventoryStacks.length;
    }

    @Override
    public boolean isEmpty() {
        return ArrayUtils.isEmpty(inventoryStacks);
    }

    @Nonnull
    @Override
    public ItemStack getItem(int index) {
        return inventoryStacks[index] == null ? ItemStack.EMPTY : inventoryStacks[index];
    }

    @Nullable
    @Override
    public ItemStack removeItem(int index, int count) {
        ItemStack itemstack = getItem(index);

        if (!itemstack.isEmpty()) {
            if (itemstack.getCount() <= count) {
                setItem(index, ItemStack.EMPTY);
            }
            else {
                itemstack = itemstack.split(count);
                if (itemstack.getCount() == 0) {
                    setItem(index, ItemStack.EMPTY);
                }
            }
        }
        return itemstack;
    }

    @Nullable
    @Override
    public ItemStack removeItemNoUpdate(int index) {
        ItemStack item = getItem(index);

        if (!item.isEmpty()) {
            setItem(index, ItemStack.EMPTY);
        }

        return item;
    }

    @Override
    public void setItem(int index, @Nonnull ItemStack stack) {
        if (index < 0 || index >= inventoryStacks.length) {
            return;
        }

        inventoryStacks[index] = stack;

        if (!stack.isEmpty() && stack.getCount() > getMaxStackSize()) {
            stack.setCount(getMaxStackSize());
        }
    }

    @Override
    public int getMaxStackSize() {
        return 64;
    }

    @Override
    public void setChanged() {

    }

    @Override
    public boolean stillValid(PlayerEntity player) {
        return true;
    }

    @Override
    public void startOpen(PlayerEntity player) {

    }

    @Override
    public void stopOpen(PlayerEntity player) {

    }

    @Override
    public boolean canPlaceItem(int index, ItemStack stack) {
        return false;
    }

    @Override
    public void clearContent() {
        for (int i = 0; i < inventoryStacks.length; i++) {
            inventoryStacks[i] = ItemStack.EMPTY;
        }
    }

//    @Override
//    public String getName() {
//        return null;
//    }
//
//    @Override
//    public boolean hasCustomName() {
//        return false;
//    }
//
//    @Override
//    public ITextComponent getDisplayName() {
//        return null;
//    }
}
