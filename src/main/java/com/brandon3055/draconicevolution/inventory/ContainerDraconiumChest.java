package com.brandon3055.draconicevolution.inventory;

import com.brandon3055.brandonscore.inventory.ContainerBCTile;
import com.brandon3055.brandonscore.inventory.SlotCheckValid;
import com.brandon3055.brandonscore.utils.EnergyUtils;
import com.brandon3055.draconicevolution.blocks.DraconiumChest;
import com.brandon3055.draconicevolution.blocks.tileentity.TileDraconiumChest;
import com.brandon3055.draconicevolution.init.DEContent;
import com.brandon3055.draconicevolution.items.ItemCore;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.CraftingResultSlot;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SSetSlotPacket;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Created by brandon3055 on 4/06/2017.
 */
public class ContainerDraconiumChest extends ContainerBCTile<TileDraconiumChest> {

    public InventoryCraftingChest craftMatrix;
    public InventoryCraftingChestResult craftResult;
    private List<Slot> mainInventorySlots = new ArrayList<>();

    public ContainerDraconiumChest(int windowId, PlayerInventory playerInv, PacketBuffer extraData) {
        this(DEContent.container_draconium_chest, windowId, playerInv, getClientTile(extraData));
    }

    public ContainerDraconiumChest(@Nullable ContainerType<?> type, int windowId, PlayerInventory playerInv, TileDraconiumChest tile) {
        super(type, windowId, playerInv, tile);

        tile.onPlayerOpenContainer(playerInv.player);

        //region Main Inventory

        int slotIndex = 0;
        //Slots 0 -> 259 Main Inventory
        for (int y = 0; y < 10; y++) {
            for (int x = 0; x < 26; x++) {
                Slot slot = new SlotCheckValid(tile.itemHandler, slotIndex++, 17 + (x * 18), 15 + (y * 18));
                mainInventorySlots.add(slot);
                addSlot(slot);
            }
        }

        //Slots 260 -> 264 Furnace Inventory
        for (int x = 0; x < 5; x++) {
            addSlot(new SlotSmeltable(tile.itemHandler, slotIndex++, 51 + (x * 18), 207));
        }

        //Capacitor Slot 265
        addSlot(new SlotRFCapacitor(tile.itemHandler, slotIndex++, 17, 265));
        //Core Slot 266
        addSlot(new SlotCore(tile.itemHandler, slotIndex, 23, 207));

        //endregion

        //region Crafting Inventory

        this.craftMatrix = new InventoryCraftingChest(this, 3, 3, tile);
        this.craftResult = new InventoryCraftingChestResult(tile);

//        LogHelper.dev(inventorySlots.size());
        addSlot(new CraftingResultSlot(playerInv.player, craftMatrix, craftResult, 0, 434, 225));

        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
//                LogHelper.dev(inventorySlots.size());
                addSlot(new SlotCheckValid.IInv(craftMatrix, x + (y * 3), 340 + (x * 18), 207 + (y * 18)));
            }
        }

        //endregion
        // Player inventory
        addPlayerSlots(170, 207, 3);
    }

    @Override
    public void slotsChanged(@Nonnull IInventory inventory) {
        if (!Objects.requireNonNull(tile.getLevel()).isClientSide()) {
            ItemStack stack = ItemStack.EMPTY;
            Optional<ICraftingRecipe> optional = Objects.requireNonNull(tile.getLevel().getServer()).getRecipeManager().getRecipeFor(IRecipeType.CRAFTING, craftMatrix, tile.getLevel());
            if (optional.isPresent()) {
                ICraftingRecipe recipe = optional.get();
                if (craftResult.setRecipeUsed(tile.getLevel(), (ServerPlayerEntity) this.player, recipe)) {
                    stack = recipe.assemble(craftMatrix);
                }
            }
            craftResult.setItem(1, stack);
            super.slotsChanged(inventory);
            ((ServerPlayerEntity)this.player).connection.send(new SSetSlotPacket(containerId, 267, stack));
        }
    }

    @Nullable
    @Override
    public ItemStack quickMoveStack(PlayerEntity playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();

            //Transferring from Main Container
            if (index < 260) {
                if (!moveItemStackTo(itemstack1, 277, slots.size(), false)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(itemstack1, itemstack);
            }
            //Transferring from a crafting inventory
            else if (index == 267 || index == 265 || index == 266) {
                //First try placing the stack in the players inventory
                if (!moveItemStackTo(itemstack1, 277, slots.size(), false)) {
                    //If that fails try the chest inventory
                    if (!moveItemStackTo(itemstack1, 0, 259, false)) {
                        return ItemStack.EMPTY;
                    }
                }
                slot.onQuickCraft(itemstack1, itemstack);
            }
            else if (index >= 260 && index < 277) {
                //First try the players inventory
                if (!moveItemStackTo(itemstack1, 0, 259, false)) {
                    //If that fails try the chest inventory
                    if (!moveItemStackTo(itemstack1, 277, slots.size(), false)) {
                        return ItemStack.EMPTY;
                    }
                }
                slot.onQuickCraft(itemstack1, itemstack);
            }
            //Transferring from Player Inventory
            else if (!DraconiumChest.isStackValid(itemstack1) || !moveItemStackTo(itemstack1, 0, 259, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.getCount() == 0) {
                slot.set(ItemStack.EMPTY);
            }
            else {
                slot.setChanged();
            }

            slot.onTake(player, itemstack1);
        }
        return itemstack;
    }

    @Nullable
    @Override
    public ItemStack clicked(int slotId, int dragType, ClickType clickTypeIn, PlayerEntity player) {
        ItemStack stack = super.clicked(slotId, dragType, clickTypeIn, player);

        if (dragType == 1 && clickTypeIn == ClickType.PICKUP && slotId >= 260 && slotId <= 264) {
            tile.validateSmelting();
        }

        return stack;
    }

    @Override
    public void removed(PlayerEntity playerIn) {
        super.removed(playerIn);
        tile.onPlayerCloseContainer(playerIn);
    }

    public class SlotSmeltable extends SlotItemHandler {
        public SlotSmeltable(IItemHandler p_i1824_1_, int p_i1824_2_, int p_i1824_3_, int p_i1824_4_) {
            super(p_i1824_1_, p_i1824_2_, p_i1824_3_, p_i1824_4_);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return tile.getSmeltResult(stack) != null;
        }
    }

    public class SlotRFCapacitor extends SlotItemHandler {
        public SlotRFCapacitor(IItemHandler inventory, int id, int x, int y) {
            super(inventory, id, x, y);

        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            if (super.mayPlace(stack)) {
                return EnergyUtils.canExtractEnergy(stack);
            }
            return false;
        }

        @Override
        public int getMaxStackSize() {
            return 1;
        }
    }

    public class SlotCore extends SlotItemHandler {
        public SlotCore(IItemHandler inventory, int id, int x, int y) {
            super(inventory, id, x, y);

        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            if (super.mayPlace(stack)) {
                return !stack.isEmpty() && stack.getItem() instanceof ItemCore /*&& stack.getItem() != DEContent.draconicCore*/;
            }
            return false;
        }

        @Override
        public int getMaxStackSize() {
            return 1;
        }
    }
}
