package com.brandon3055.draconicevolution.utils;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collection;

/**
 * This handles merging IItemHandler capabilities together.
 * <p>
 * Created by covers1624 on 6/10/18.
 * De-Scalafied by brandon3055 on 14/12/2020.
 */
@Deprecated //This will be in CCL soon
public class ItemCapMerger {

    public static MergedHandler merge(IItemHandler... handlers) {
        return merge(Arrays.asList(handlers));
    }

    public static MergedHandler merge(Collection<IItemHandler> handlers) {
        if (handlers.stream().allMatch(e -> e instanceof IItemHandlerModifiable)) {
            return new MergedModifiableHandler(handlers);
        } else {
            return new MergedHandler(handlers);
        }
    }

    public static class MergedHandler implements IItemHandler {
        protected IItemHandler[] invMap;
        protected Integer[] slotMap;

        public MergedHandler(Collection<IItemHandler> handlers) {
            int numSlots = handlers.stream().mapToInt(IItemHandler::getSlots).sum();
            invMap = new IItemHandler[numSlots];
            slotMap = new Integer[numSlots];

            int i = 0;
            for (IItemHandler handler : handlers) {
                for (int slot = 0; slot < handler.getSlots(); slot++) {
                    invMap[i] = handler;
                    slotMap[i] = slot;
                    i++;
                }
            }
        }

        @Override
        public int getSlots() {
            return slotMap.length;
        }

        @Nonnull
        @Override
        public ItemStack getStackInSlot(int slot) {
            return invMap[slot].getStackInSlot(slotMap[slot]);
        }

        @Nonnull
        @Override
        public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            return invMap[slot].insertItem(slotMap[slot], stack, simulate);
        }

        @Nonnull
        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            return invMap[slot].extractItem(slotMap[slot], amount, simulate);
        }

        @Override
        public int getSlotLimit(int slot) {
            return invMap[slot].getSlotLimit(slotMap[slot]);
        }

        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            return invMap[slot].isItemValid(slotMap[slot], stack);
        }
    }

    public static class MergedModifiableHandler extends MergedHandler implements IItemHandlerModifiable {

        public MergedModifiableHandler(Collection<IItemHandler> handlers) {
            super(handlers);
        }

        @Override
        public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
            ((IItemHandlerModifiable) invMap[slot]).setStackInSlot(slotMap[slot], stack);
        }
    }
}