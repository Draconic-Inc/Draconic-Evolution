package com.brandon3055.draconicevolution.inventory;

import codechicken.lib.util.ArrayUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;

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
    public int getSizeInventory() {
        return inventoryStacks.length;
    }

    @Override
    public boolean isEmpty() {
        return ArrayUtils.isEmpty(inventoryStacks);
    }

    @Nonnull
    @Override
    public ItemStack getStackInSlot(int index) {
        return inventoryStacks[index] == null ? ItemStack.EMPTY : inventoryStacks[index];
    }

    @Nullable
    @Override
    public ItemStack decrStackSize(int index, int count) {
        ItemStack itemstack = getStackInSlot(index);

        if (!itemstack.isEmpty()) {
            if (itemstack.getCount() <= count) {
                setInventorySlotContents(index, ItemStack.EMPTY);
            }
            else {
                itemstack = itemstack.splitStack(count);
                if (itemstack.getCount() == 0) {
                    setInventorySlotContents(index, ItemStack.EMPTY);
                }
            }
        }
        return itemstack;
    }

    @Nullable
    @Override
    public ItemStack removeStackFromSlot(int index) {
        ItemStack item = getStackInSlot(index);

        if (!item.isEmpty()) {
            setInventorySlotContents(index, ItemStack.EMPTY);
        }

        return item;
    }

    @Override
    public void setInventorySlotContents(int index, @Nonnull ItemStack stack) {
        if (index < 0 || index >= inventoryStacks.length) {
            return;
        }

        inventoryStacks[index] = stack;

        if (!stack.isEmpty() && stack.getCount() > getInventoryStackLimit()) {
            stack.setCount(getInventoryStackLimit());
        }
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public void markDirty() {

    }

    @Override
    public boolean isUsableByPlayer(EntityPlayer player) {
        return true;
    }

    @Override
    public void openInventory(EntityPlayer player) {

    }

    @Override
    public void closeInventory(EntityPlayer player) {

    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return false;
    }

    @Override
    public int getField(int id) {
        return 0;
    }

    @Override
    public void setField(int id, int value) {

    }

    @Override
    public int getFieldCount() {
        return 0;
    }

    @Override
    public void clear() {
        for (int i = 0; i < inventoryStacks.length; i++) {
            inventoryStacks[i] = ItemStack.EMPTY;
        }
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    @Override
    public ITextComponent getDisplayName() {
        return null;
    }
}
