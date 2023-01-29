package com.brandon3055.draconicevolution.common.container;

import java.util.ArrayList;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;

import com.brandon3055.draconicevolution.common.inventory.InventoryTool;
import com.brandon3055.draconicevolution.common.utills.IInventoryTool;

/**
 * Created by Brandon on 13/01/2015.
 */
public class ContainerAdvTool extends ContainerDataSync {

    public InventoryTool inventoryTool;
    private EntityPlayer player;
    public int inventoryItemSlot = -1;
    private boolean slotsActive = false;

    public ContainerAdvTool(InventoryPlayer invPlayer, InventoryTool inventory) {
        this.inventoryTool = inventory;
        this.inventoryTool.setContainer(this);
        this.player = invPlayer.player;
        setSlotsActive(false);
    }

    public void setSlotsActive(boolean active) {
        slotsActive = active;
        inventoryItemStacks = new ArrayList();
        inventorySlots = new ArrayList();

        if (slotsActive) {
            for (int x = 0; x < 9; x++) {
                addSlotToContainer(new SlotPlayerInv(player.inventory, x, 8 + 18 * x, 103, inventoryItemSlot));
            }

            for (int y = 0; y < 3; y++) {
                for (int x = 0; x < 9; x++) {
                    addSlotToContainer(
                            new SlotPlayerInv(
                                    player.inventory,
                                    x + y * 9 + 9,
                                    8 + 18 * x,
                                    45 + y * 18,
                                    inventoryItemSlot));
                }
            }

            for (int x = 0; x < inventoryTool.size; x++) {
                addSlotToContainer(new SlotOblitFilter(inventoryTool, x, 8 + 18 * x, 19));
            }

            for (int y = 0; y < 5; y++) {
                addSlotToContainer(new SlotEnchantment(inventoryTool, inventoryTool.size + y, 173, 19 + y * 21));
            }
        } else {
            for (int x = 0; x < 9; x++) {
                addSlotToContainer(
                        new SlotPlayerInv(player.inventory, x, -1000 + 8 + 18 * x, -1000 + 103, inventoryItemSlot));
            }

            for (int y = 0; y < 3; y++) {
                for (int x = 0; x < 9; x++) {
                    addSlotToContainer(
                            new SlotPlayerInv(
                                    player.inventory,
                                    x + y * 9 + 9,
                                    -1000 + 8 + 18 * x,
                                    -1000 + 45 + y * 18,
                                    inventoryItemSlot));
                }
            }
        }

        if (player.worldObj.isRemote) {
            sendObjectToServer(null, 1, active ? 1 : 0);
        }
    }

    public void setInventory(InventoryTool inventory) {
        this.inventoryTool = inventory;
    }

    public void setPlayer(EntityPlayer player) {
        this.player = player;
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return true;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int i) {
        // Slot slot = getSlot(i);
        //
        // if (slot != null && slot.getHasStack())
        // {
        // ItemStack stack = slot.getStack();
        // ItemStack result = stack.copy();
        //
        // if (i >= 36){ //To player
        // if (!mergeItemStack(stack, 0, 36, false)){
        // return null;
        // }
        // }else if (stack.stackSize != 1 || !(stack.getItem() instanceof IEnergyContainerItem) ||
        // !mergeItemStack(stack, 36, 37, false)){
        // return null;
        // }
        //
        // if (stack.stackSize == 0) {
        // slot.putStack(null);
        // }else{
        // slot.onSlotChanged();
        // }
        //
        // slot.onPickupFromSlot(player, stack);
        //
        // return result;
        // }

        return null;
    }

    @Override
    public void receiveSyncData(int index, int value) {
        if (index == 0) {
            updateInventoryStack(value);
        } else if (index == 1) {
            setSlotsActive(value == 1);
        }
    }

    public void updateInventoryStack(int slot) {
        this.inventoryItemSlot = slot;
        if (player.worldObj.isRemote) {
            sendObjectToServer(null, 0, slot);
        }

        if (player.inventory.getStackInSlot(slot) != null
                && player.inventory.getStackInSlot(slot).getItem() instanceof IInventoryTool) {
            this.inventoryTool.setAndReadFromStack(player.inventory.getStackInSlot(slot), slot);
        }
    }

    public class SlotPlayerInv extends Slot {

        public int inventoryItemSlot = -1;

        public SlotPlayerInv(IInventory iInventory, int slot, int x, int y, int inventoryItemSlot) {
            super(iInventory, slot, x, y);
            this.inventoryItemSlot = inventoryItemSlot;
        }

        @Override
        public boolean canTakeStack(EntityPlayer player) {
            return this.inventoryItemSlot != this.slotNumber && super.canTakeStack(player);
        }
    }

    public class SlotOblitFilter extends Slot {

        public SlotOblitFilter(IInventory iInventory, int slot, int x, int y) {
            super(iInventory, slot, x, y);
        }

        @Override
        public boolean isItemValid(ItemStack stack) {
            return stack != null && stack.getItem() instanceof ItemBlock && super.isItemValid(stack);
        }
    }

    public class SlotEnchantment extends Slot {

        InventoryTool inventory;

        public SlotEnchantment(IInventory iInventory, int slot, int x, int y) {
            super(iInventory, slot, x, y);
        }

        @Override
        public boolean isItemValid(ItemStack stack) {
            if (stack == null || stack.getItem() != Items.enchanted_book || !stack.hasTagCompound()) return false;
            NBTTagList list = stack.getTagCompound().getTagList("StoredEnchantments", 10);
            if (list.tagCount() != 1) return false;
            Enchantment enchant = Enchantment.enchantmentsList[list.getCompoundTagAt(0).getInteger("id")];
            if (inventoryTool.inventoryItem == null
                    || !(inventoryTool.inventoryItem.getItem() instanceof IInventoryTool)
                    || (!((IInventoryTool) inventoryTool.inventoryItem.getItem()).isEnchantValid(enchant)
                            && enchant.type != EnumEnchantmentType.all))
                return false;
            if (EnchantmentHelper.getEnchantments(inventoryTool.inventoryItem)
                    .containsKey(list.getCompoundTagAt(0).getInteger("id")))
                return false;
            return super.isItemValid(stack);
        }
    }
}
