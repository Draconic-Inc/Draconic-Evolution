package com.brandon3055.draconicevolution.inventory;

import com.brandon3055.brandonscore.utils.ItemNBTHelper;
import com.brandon3055.brandonscore.utils.LogHelperBC;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;

/**
 * Created by brandon3055 on 21/12/2017.
 */
public class InvJunkFilter implements IItemHandlerModifiable {

    private ItemStack stack;
    private NonNullList<ItemStack> stacks;

    public InvJunkFilter(ItemStack stack, int filterSize) {
        this.stack = stack;
        stacks = NonNullList.withSize(filterSize, ItemStack.EMPTY);
    }

    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        return stack.getItem() instanceof BlockItem;
    }

    /**
     * Returns the number of slots available
     *
     * @return The number of slots available
     **/
    @Override
    public int getSlots() {
        return stacks.size();
    }

    /**
     * Returns the ItemStack in a given slot.
     * <p>
     * The result's stack size may be greater than the itemstacks max size.
     * <p>
     * If the result is null, then the slot is empty.
     * If the result is not null but the stack size is zero, then it represents
     * an empty slot that will only accept* a specific itemstack.
     * <p>
     * <p/>
     * IMPORTANT: This ItemStack MUST NOT be modified. This method is not for
     * altering an inventories contents. Any implementers who are able to detect
     * modification through this method should throw an exception.
     * <p/>
     * SERIOUSLY: DO NOT MODIFY THE RETURNED ITEMSTACK
     *
     * @param slot Slot to query
     * @return ItemStack in given slot. May be null.
     **/
    @Nonnull
    @Override
    public ItemStack getStackInSlot(int slot) {
        return ItemStack.EMPTY;
    }

    /**
     * Inserts an ItemStack into the given slot and return the remainder.
     * The ItemStack should not be modified in this function!
     * Note: This behaviour is subtly different from IFluidHandlers.fill()
     *
     * @param slot     Slot to insert into.
     * @param stack    ItemStack to insert.
     * @param simulate If true, the insertion is only simulated
     * @return The remaining ItemStack that was not inserted (if the entire stack is accepted, then return ItemStack.EMPTY).
     * May be the same as the input ItemStack if unchanged, otherwise a new ItemStack.
     **/
    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        return null;
    }

    /**
     * Extracts an ItemStack from the given slot. The returned value must be null
     * if nothing is extracted, otherwise it's stack size must not be greater than amount or the
     * itemstacks getMaxStackSize().
     *
     * @param slot     Slot to extract from.
     * @param amount   Amount to extract (may be greater than the current stacks max limit)
     * @param simulate If true, the extraction is only simulated
     * @return ItemStack extracted from the slot, must be ItemStack.EMPTY, if nothing can be extracted
     **/
    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        return null;
    }

    /**
     * Retrieves the maximum stack size allowed to exist in the given slot.
     *
     * @param slot Slot to query.
     * @return The maximum stack size allowed in the slot.
     */
    @Override
    public int getSlotLimit(int slot) {
        return 0;
    }

    /**
     * Overrides the stack in the given slot. This method is used by the
     * standard Forge helper methods and classes. It is not intended for
     * general use by other mods, and the handler may throw an error if it
     * is called unexpectedly.
     *
     * @param slot  Slot to modify
     * @param stack ItemStack to set slot to (may be null)
     * @throws RuntimeException if the handler is called in a way that the handler
     *                          was not expecting.
     **/
    @Override
    public void setStackInSlot(int slot, @Nonnull ItemStack stack) {

    }

    public void saveItems() {
        if (stack == null) {
            LogHelperBC.bigError("TolkienTweaks: Tried to load Dynamic Item Stack inventory from null item!");
            return;
        }

        CompoundTag compound = ItemNBTHelper.getCompound(stack);
        ListTag list = new ListTag();

        for (ItemStack stack : stacks) {
            if (stack != null && stack.getCount() > 0) {
                CompoundTag tag = new CompoundTag();
                stack.save(tag);
                list.add(tag);
            }
        }

        compound.put("InvItems", list);
    }

    public void loadItems() {
        if (stack == null) {
            LogHelperBC.bigError("TolkienTweaks: Tried to save Dynamic Item Stack inventory to null item!");
            return;
        }

        CompoundTag compound = ItemNBTHelper.getCompound(stack);
        ListTag list = compound.getList("InvItems", 10);
        stacks.clear();

        for (int i = 0; i < list.size(); i++) {
            stacks.add(ItemStack.of(list.getCompound(i)));
        }
    }
}
