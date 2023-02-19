package com.brandon3055.draconicevolution.inventory;

import codechicken.lib.math.MathHelper;
import com.brandon3055.brandonscore.inventory.ContainerBCTile;
import com.brandon3055.brandonscore.inventory.ContainerBCore;
import com.brandon3055.draconicevolution.DEConfig;
import com.brandon3055.draconicevolution.init.DEContent;
import com.brandon3055.draconicevolution.blocks.reactor.tileentity.TileReactorCore;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by brandon3055 on 10/02/2017.
 */
public class ContainerReactor extends ContainerBCTile<TileReactorCore> {

    public boolean fuelSlots = false;

    public ContainerReactor(int windowId, PlayerInventory playerInv, PacketBuffer extraData) {
        this(DEContent.container_reactor, windowId, playerInv, getClientTile(extraData));
    }

    public ContainerReactor(@Nullable ContainerType<?> type, int windowId, PlayerInventory player, TileReactorCore tile) {
        super(type, windowId, player, tile);
    }

//    public ContainerReactor(PlayerEntity player, TileReactorCore tile) {
//        super(player, tile);
//        setSlotState();
//    }

    public void setSlotState() {
        fuelSlots = tile.reactorState.get() == TileReactorCore.ReactorState.COLD;

        slots.clear();
        lastSlots.clear();

        if (fuelSlots) {
            addPlayerSlots(44 - 31, 140);

            for (int x = 0; x < 3; x++) {
                addSlot(new SlotReactor(tile, x, 183 + (x * 18), 149));
            }

            for (int x = 0; x < 3; x++) {
                addSlot(new SlotReactor(tile, 3 + x, 183 + (x * 18), 180));
            }

//            for (int y = 0; y < 2; y++) {
//                for (int x = 0; x < 2; x++) {
//                    addSlotToContainer(new SlotReactor(tile, 3 + x + y * 2, 192 + (x * 18), 180 + (y * 18)));
//                }
//            }
        }
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();

        if (tile.reactorState.get() == TileReactorCore.ReactorState.COLD != fuelSlots) {
            setSlotState();
        }
    }

    @Nullable
    @Override
    public ItemStack clicked(int slotId, int dragType, ClickType clickTypeIn, PlayerEntity player) {
        int maxFuel = 10368 + 15;
        int installedFuel = (int) (tile.reactableFuel.get() + tile.convertedFuel.get());
        int free = maxFuel - installedFuel;

        Slot slot = getSlot(slotId);
        if (slot instanceof SlotReactor && clickTypeIn == ClickType.PICKUP) {
            PlayerInventory inventory = player.inventory;
            ItemStack stackInSlot = slot.getItem();
            ItemStack heldStack = inventory.getCarried();

            if (!heldStack.isEmpty()) {
                int value;
                ItemStack copy = heldStack.copy();
                copy.setCount(1);

                if ((value = getFuelValue(copy)) > 0) {
                    int maxInsert = free / value;
                    int insert = Math.min(Math.min(heldStack.getCount(), maxInsert), dragType == 1 ? 1 : 64);
                    tile.reactableFuel.add(insert * value);
                    heldStack.shrink(insert);
                }
                else if ((value = getChaosValue(copy)) > 0) {
                    int maxInsert = free / value;
                    int insert = Math.min(Math.min(heldStack.getCount(), maxInsert), dragType == 1 ? 1 : 64);
                    tile.convertedFuel.add(insert * value);
                    heldStack.shrink(insert);
                }
//                else if ((value = getChaosValue(copy)) > 0) {
//                    int maxInsert = free / value;
//                    int insert = Math.min(Math.min(heldStack.stackSize, maxInsert), dragType == 1 ? 1 : 64);
//                    tile.convertedFuel.value += insert * value;
//                    heldStack.stackSize -= insert;
//                }

                if (heldStack.getCount() <= 0) {
                    inventory.setCarried(ItemStack.EMPTY);
                }
            }
            else if (!stackInSlot.isEmpty()) {
                tile.reactableFuel.subtract(getFuelValue(stackInSlot));
                tile.convertedFuel.subtract(getChaosValue(stackInSlot));
                inventory.setCarried(stackInSlot);
            }

            return ItemStack.EMPTY;
        }
        else if (slotId <= 35) {
            return super.clicked(slotId, dragType, clickTypeIn, player);
        }
        return ItemStack.EMPTY;
    }

    private int getFuelValue(ItemStack stack) {
        if (stack.isEmpty()) {
            return 0;
        }
        else if (stack.getItem() == DEContent.block_draconium_awakened.asItem()) {
            return stack.getCount() * 1296;
        }
        else if (stack.getItem() == DEContent.ingot_draconium_awakened) {
            return stack.getCount() * 144;
        }
        else if (stack.getItem() == DEContent.nugget_draconium_awakened) {
            return stack.getCount() * 16;
        }
        return 0;
    }

    private int getChaosValue(ItemStack stack) {
        if (stack.isEmpty()) {
            return 0;
        }
        else if (stack.getItem() == DEContent.chaos_frag_large) {
            return stack.getCount() * 1296;
        }
        else if (stack.getItem() == DEContent.chaos_frag_medium) {
            return stack.getCount() * 144;
        }
        else if (stack.getItem() == DEContent.chaos_frag_small) {
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
        public void onQuickCraft(ItemStack before, ItemStack after) {
            if (!before.isEmpty() && !after.isEmpty()) {
                if (before.getItem() == after.getItem()) {
                    int i = after.getCount() - before.getCount();

                    if (i > 0) {
                        this.onQuickCraft(before, i);
                    }
                }
            }
        }

        @Override
        public boolean mayPlace(@Nullable ItemStack stack) {
            return false;
        }

        @Override
        public boolean mayPickup(PlayerEntity p_82869_1_) {
            return false;
        }

        @Nullable
        @Override
        public ItemStack getItem() {
            int index = getSlotIndex();
            if (index < 3) {
                int fuel = MathHelper.floor(tile.reactableFuel.get());
                int block = fuel / 1296;
                int ingot = (fuel % 1296) / 144;
                int nugget = ((fuel % 1296) % 144) / 16;

                if (index == 0 && block > 0) {
                    return new ItemStack(DEContent.block_draconium_awakened, block);
                }
                else if (index == 1 && ingot > 0) {
                    return new ItemStack(DEContent.ingot_draconium_awakened, ingot);
                }
                else if (index == 2 && nugget > 0) {
                    return new ItemStack(DEContent.nugget_draconium_awakened, nugget);
                }
            }
            else {
                int chaos = MathHelper.floor(tile.convertedFuel.get());
                int block = chaos / 1296;
                int ingot = (chaos % 1296) / 144;
                int nugget = ((chaos % 1296) % 144) / 16;

                if (index == 3 && block > 0) {
                    return new ItemStack(DEContent.chaos_frag_large, block);
                }
                else if (index == 4 && ingot > 0) {
                    return new ItemStack(DEContent.chaos_frag_medium, ingot);
                }
                else if (index == 5 && nugget > 0) {
                    return new ItemStack(DEContent.chaos_frag_small, nugget);
                }
            }

            return ItemStack.EMPTY;
        }

        @Override
        public void set(@Nonnull ItemStack stack) {
            //this.inventory.setInventorySlotContents(this.slotIndex, stack);
            this.setChanged();
        }

        @Override
        public void setChanged() {
            this.tile.setChanged();
        }

        @Override
        public int getMaxStackSize() {
            return 64;//this.inventory.getInventoryStackLimit();
        }

        @Override
        public ItemStack remove(int amount) {
            return ItemStack.EMPTY;//this.inventory.decrStackSize(this.getSlotIndex, amount);
        }



//        @Override
//        public boolean isHere(IInventory inv, int slotIn) {
//            return false;//inv == this.inventory && slotIn == this.getSlotIndex();
//        }
    }
}
