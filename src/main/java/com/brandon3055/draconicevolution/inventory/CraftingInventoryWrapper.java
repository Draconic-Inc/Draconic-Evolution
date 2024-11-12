package com.brandon3055.draconicevolution.inventory;

import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

/**
 * Created by Brandon on 29/10/2014.
 */
public class CraftingInventoryWrapper extends TransientCraftingContainer {
    private IItemHandlerModifiable wrapped;
    private AbstractContainerMenu container;

    public CraftingInventoryWrapper(AbstractContainerMenu container, int width, int height, IItemHandlerModifiable wrapped) {
        super(container, width, height);
        this.container = container;
        this.wrapped = wrapped;
    }

    @Override
    public int getContainerSize() {
        return wrapped.getSlots();
    }

    @Override
    public boolean isEmpty() {
        for (int i = 0; i < wrapped.getSlots(); i++) {
            if (!wrapped.getStackInSlot(i).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getItem(int index) {
        return index < 0 || index >= this.getContainerSize() ? ItemStack.EMPTY : wrapped.getStackInSlot(index);
    }

    @Override
    public ItemStack removeItemNoUpdate(int index) {
        ItemStack ret = wrapped.getStackInSlot(index);
        wrapped.setStackInSlot(index, ItemStack.EMPTY);
        return ret;
    }

    @Override
    public ItemStack removeItem(int index, int count) {
        if (index < 0 || index >= getContainerSize()) {
            return ItemStack.EMPTY;
        }

        ItemStack itemstack = wrapped.extractItem(index, count, false);

        if (!itemstack.isEmpty()) {
            container.slotsChanged(this);
        }

        return itemstack;
    }

    @Override
    public void setItem(int index, ItemStack stack) {
        wrapped.setStackInSlot(index, stack);
        container.slotsChanged(this);
    }

    @Override
    public void clearContent() {
        for (int i = 0; i < getContainerSize(); i++) {
            wrapped.setStackInSlot(i, ItemStack.EMPTY);
        }
    }

    @Override
    public void fillStackedContents(StackedContents itemHelper) {
        for (int i = 0; i < wrapped.getSlots(); i++) {
            itemHelper.accountSimpleStack(wrapped.getStackInSlot(i));
        }
    }
}
