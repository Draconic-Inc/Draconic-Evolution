package com.brandon3055.draconicevolution.inventory;

import codechicken.lib.math.MathHelper;
import com.brandon3055.brandonscore.inventory.ContainerBCBase;
import com.brandon3055.draconicevolution.DEFeatures;
import com.brandon3055.draconicevolution.blocks.reactor.tileentity.TileReactorCore;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by brandon3055 on 10/02/2017.
 */
public class ContainerReactor extends ContainerBCBase<TileReactorCore> {

    public boolean fuelSlots = false;

    public ContainerReactor(EntityPlayer player, TileReactorCore tile) {
        super(player, tile);
        setSlotState();
    }

    public void setSlotState() {
        fuelSlots = tile.reactorState.get() == TileReactorCore.ReactorState.COLD;

        inventorySlots.clear();
        inventoryItemStacks.clear();

        if (fuelSlots) {
            addPlayerSlots(44 - 31, 140);

            for (int x = 0; x < 3; x++) {
                addSlotToContainer(new SlotReactor(tile, x, 183 + (x * 18), 149));
            }

            for (int x = 0; x < 3; x++) {
                addSlotToContainer(new SlotReactor(tile, 3 + x, 183 + (x * 18), 180));
            }

//            for (int y = 0; y < 2; y++) {
//                for (int x = 0; x < 2; x++) {
//                    addSlotToContainer(new SlotReactor(tile, 3 + x + y * 2, 192 + (x * 18), 180 + (y * 18)));
//                }
//            }
        }
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();

        if (tile.reactorState.get() == TileReactorCore.ReactorState.COLD != fuelSlots) {
            setSlotState();
        }
    }

    @Nullable
    @Override
    public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player) {
        int maxFuel = 10368 + 15;
        int installedFuel = (int) (tile.reactableFuel.get() + tile.convertedFuel.get());
        int free = maxFuel - installedFuel;

        Slot slot = getSlot(slotId);
        if (slot instanceof SlotReactor && clickTypeIn == ClickType.PICKUP) {
            InventoryPlayer inventory = player.inventory;
            ItemStack stackInSlot = slot.getStack();
            ItemStack heldStack = inventory.getItemStack();

            if (!heldStack.isEmpty()) {
                int value;
                ItemStack copy = heldStack.copy();
                copy.setCount(1);

                if ((value = getFuelValue(copy)) > 0) {
                    int maxInsert = free / value;
                    int insert = Math.min(Math.min(heldStack.getCount(), maxInsert), dragType == 1 ? 1 : 64);
                    tile.reactableFuel.add((double)insert * value);
                    heldStack.shrink(insert);
                }
//                else if ((value = getChaosValue(copy)) > 0) {
//                    int maxInsert = free / value;
//                    int insert = Math.min(Math.min(heldStack.stackSize, maxInsert), dragType == 1 ? 1 : 64);
//                    tile.convertedFuel.get() += insert * value;
//                    heldStack.stackSize -= insert;
//                }

                if (heldStack.getCount() <= 0) {
                    inventory.setItemStack(ItemStack.EMPTY);
                }
            }
            else if (!stackInSlot.isEmpty()) {
                tile.reactableFuel.subtract((double) getFuelValue(stackInSlot));
                tile.convertedFuel.subtract((double) getChaosValue(stackInSlot));
                inventory.setItemStack(stackInSlot);
            }

            return ItemStack.EMPTY;
        }
        else if (slotId <= 35) {
            return super.slotClick(slotId, dragType, clickTypeIn, player);
        }
        return ItemStack.EMPTY;
    }

    private int getFuelValue(ItemStack stack) {
        if (stack.isEmpty()) {
            return 0;
        }
        else if (stack.getItem() == Item.getItemFromBlock(DEFeatures.draconicBlock)) {
            return stack.getCount() * 1296;
        }
        else if (stack.getItem() == DEFeatures.draconicIngot) {
            return stack.getCount() * 144;
        }
        else if (stack.getItem() == DEFeatures.nugget && stack.getItemDamage() == 1) {
            return stack.getCount() * 16;
        }
        return 0;
    }

    private int getChaosValue(ItemStack stack) {
        if (stack.isEmpty()) {
            return 0;
        }
        else if (stack.getItem() == DEFeatures.chaosShard && stack.getItemDamage() == 1) {
            return stack.getCount() * 1296;
        }
        else if (stack.getItem() == DEFeatures.chaosShard && stack.getItemDamage() == 2) {
            return stack.getCount() * 144;
        }
        else if (stack.getItem() == DEFeatures.chaosShard && stack.getItemDamage() == 3) {
            return stack.getCount() * 16;
        }
        return 0;
    }

    public static class SlotReactor extends Slot {

        private final TileReactorCore tile;

        public SlotReactor(TileReactorCore tile, int index, int xPosition, int yPosition) {
            super(null, index, xPosition, yPosition);
            this.tile = tile;
        }

        @Override
        public void onSlotChange(ItemStack before, ItemStack after) {
            if (!before.isEmpty() && !after.isEmpty()) {
                if (before.getItem() == after.getItem()) {
                    int i = after.getCount() - before.getCount();

                    if (i > 0) {
                        this.onCrafting(before, i);
                    }
                }
            }
        }

        @Override
        public boolean isItemValid(@Nullable ItemStack stack) {
            return false;
        }

        @Nullable
        @Override
        public ItemStack getStack() {
            int index = getSlotIndex();
            if (index < 3) {
                int fuel = MathHelper.floor(tile.reactableFuel.get());
                int block = fuel / 1296;
                int ingot = (fuel % 1296) / 144;
                int nugget = ((fuel % 1296) % 144) / 16;

                if (index == 0 && block > 0) {
                    return new ItemStack(DEFeatures.draconicBlock, block);
                }
                else if (index == 1 && ingot > 0) {
                    return new ItemStack(DEFeatures.draconicIngot, ingot);
                }
                else if (index == 2 && nugget > 0) {
                    return new ItemStack(DEFeatures.nugget, nugget, 1);
                }
            }
            else {
                int chaos = MathHelper.floor(tile.convertedFuel.get());
                int block = chaos / 1296;
                int ingot = (chaos % 1296) / 144;
                int nugget = ((chaos % 1296) % 144) / 16;

                if (index == 3 && block > 0) {
                    return new ItemStack(DEFeatures.chaosShard, block, 1);
                }
                else if (index == 4 && ingot > 0) {
                    return new ItemStack(DEFeatures.chaosShard, ingot, 2);
                }
                else if (index == 5 && nugget > 0) {
                    return new ItemStack(DEFeatures.chaosShard, nugget, 3);
                }
            }

            return ItemStack.EMPTY;
        }

        @Override
        public void putStack(@Nonnull ItemStack stack) {
            //this.inventory.setInventorySlotContents(this.slotIndex, stack);
            this.onSlotChanged();
        }

        @Override
        public void onSlotChanged() {
            this.tile.markDirty();
        }

        @Override
        public int getSlotStackLimit() {
            return 64;//this.inventory.getInventoryStackLimit();
        }

        @Override
        public ItemStack decrStackSize(int amount) {
            return ItemStack.EMPTY;//this.inventory.decrStackSize(this.getSlotIndex, amount);
        }

        @Override
        public boolean isHere(IInventory inv, int slotIn) {
            return false;//inv == this.inventory && slotIn == this.getSlotIndex();
        }
    }
}
