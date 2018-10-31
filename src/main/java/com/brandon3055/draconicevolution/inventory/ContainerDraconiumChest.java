package com.brandon3055.draconicevolution.inventory;

import com.brandon3055.brandonscore.inventory.ContainerBCBase;
import com.brandon3055.brandonscore.inventory.SlotCheckValid;
import com.brandon3055.brandonscore.lib.EnergyHelper;
import com.brandon3055.draconicevolution.DEFeatures;
import com.brandon3055.draconicevolution.blocks.DraconiumChest;
import com.brandon3055.draconicevolution.blocks.tileentity.TileDraconiumChest;
import com.brandon3055.draconicevolution.items.ItemCore;
import invtweaks.api.container.ChestContainer;
import invtweaks.api.container.ContainerSection;
import invtweaks.api.container.ContainerSectionCallback;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by brandon3055 on 4/06/2017.
 */
@ChestContainer(isLargeChest = true, rowSize = 26)
public class ContainerDraconiumChest extends ContainerBCBase<TileDraconiumChest> {

    public InventoryCrafting craftMatrix;
    public IInventory craftResult;
    private List<Slot> mainInventorySlots = new ArrayList<>();


    public ContainerDraconiumChest(EntityPlayer player, TileDraconiumChest tile) {
        super(player, tile);

        tile.openInventory(player);

        //region Main Inventory

        int slotIndex = 0;
        //Slots 0 -> 259 Main Inventory
        for (int y = 0; y < 10; y++) {
            for (int x = 0; x < 26; x++) {
                Slot slot = new SlotCheckValid(tile, slotIndex++, 7 + (x * 18), 7 + (y * 18));
                mainInventorySlots.add(slot);
                addSlotToContainer(slot);
            }
        }

        //Slots 260 -> 264 Furnace Inventory
        for (int x = 0; x < 5; x++) {
            addSlotToContainer(new SlotSmeltable(tile, slotIndex++, 45 + (x * 18), 207));
        }

        //Capacitor Slot 265
        addSlotToContainer(new SlotRFCapacitor(tile, slotIndex++, 8, 245));
        //Core Slot 266
        addSlotToContainer(new SlotCore(tile, slotIndex, 17, 207));

        //endregion

        //region Crafting Inventory

        this.craftMatrix = new InventoryCraftingChest(this, 3, 3, tile);
        this.craftResult = new InventoryCraftingChestResult(tile);

//        LogHelper.dev(inventorySlots.size());
        addSlotToContainer(new SlotCrafting(player, craftMatrix, craftResult, 0, 428, 216));

        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
//                LogHelper.dev(inventorySlots.size());
                addSlotToContainer(new SlotCheckValid(craftMatrix, x + (y * 3), 334 + (x * 18), 198 + (y * 18)));
            }
        }

        //endregion

        addPlayerSlots(161, 189, 2);
    }

    @Override
    public void onCraftMatrixChanged(IInventory inventory) {
        craftResult.setInventorySlotContents(0, CraftingManager.findMatchingResult(craftMatrix, tile.getWorld()));
    }

    @Nullable
    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            //Transferring from Main Container
            if (index < 260) {
                if (!mergeItemStack(itemstack1, 277, inventorySlots.size(), false)) {
                    return ItemStack.EMPTY;
                }
                slot.onSlotChange(itemstack1, itemstack);
            }
            //Transferring from a crafting inventory
            else if (index == 267 || index == 265 || index == 266) {
                //First try placing the stack in the players inventory
                if (!mergeItemStack(itemstack1, 277, inventorySlots.size(), false)) {
                    //If that fails try the chest inventory
                    if (!mergeItemStack(itemstack1, 0, 259, false)) {
                        return ItemStack.EMPTY;
                    }
                }
                slot.onSlotChange(itemstack1, itemstack);
            }
            else if (index >= 260 && index < 277) {
                //First try the players inventory
                if (!mergeItemStack(itemstack1, 0, 259, false)) {
                    //If that fails try the chest inventory
                    if (!mergeItemStack(itemstack1, 277, inventorySlots.size(), false)) {
                        return ItemStack.EMPTY;
                    }
                }
                slot.onSlotChange(itemstack1, itemstack);
            }
            //Transferring from Player Inventory
            else if (!DraconiumChest.isStackValid(itemstack1) || !mergeItemStack(itemstack1, 0, 259, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.getCount() == 0) {
                slot.putStack(ItemStack.EMPTY);
            }
            else {
                slot.onSlotChanged();
            }

            slot.onTake(player, itemstack1);
        }
        return itemstack;
    }

    @Nullable
    @Override
    public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player) {
        ItemStack stack = super.slotClick(slotId, dragType, clickTypeIn, player);

        if (dragType == 1 && clickTypeIn == ClickType.PICKUP && slotId >= 260 && slotId <= 264) {
            tile.validateSmelting();
        }

        return stack;
    }

    @Override
    public void onContainerClosed(EntityPlayer playerIn) {
        super.onContainerClosed(playerIn);
        tile.closeInventory(playerIn);
    }

    @ContainerSectionCallback
    public Map<ContainerSection, List<Slot>> getContainerSelection() {
        Map<ContainerSection, List<Slot>> map = new LinkedHashMap<>();
        map.put(ContainerSection.CHEST, mainInventorySlots);
        return map;
    }

    public class SlotSmeltable extends Slot {
        public SlotSmeltable(IInventory p_i1824_1_, int p_i1824_2_, int p_i1824_3_, int p_i1824_4_) {
            super(p_i1824_1_, p_i1824_2_, p_i1824_3_, p_i1824_4_);
        }

        @Override
        public boolean isItemValid(ItemStack stack) {
            return tile.getSmeltResult(stack) != null;
        }
    }

    public class SlotRFCapacitor extends Slot {
        public SlotRFCapacitor(IInventory inventory, int id, int x, int y) {
            super(inventory, id, x, y);

        }

        @Override
        public boolean isItemValid(ItemStack stack) {
            if (super.isItemValid(stack)) {
                return EnergyHelper.canExtractEnergy(stack);
            }
            return false;
        }

        @Override
        public int getSlotStackLimit() {
            return 1;
        }
    }

    public class SlotCore extends Slot {
        public SlotCore(IInventory inventory, int id, int x, int y) {
            super(inventory, id, x, y);

        }

        @Override
        public boolean isItemValid(ItemStack stack) {
            if (super.isItemValid(stack)) {
                return !stack.isEmpty() && stack.getItem() instanceof ItemCore && stack.getItem() != DEFeatures.draconicCore;
            }
            return false;
        }

        @Override
        public int getSlotStackLimit() {
            return 1;
        }
    }
}
