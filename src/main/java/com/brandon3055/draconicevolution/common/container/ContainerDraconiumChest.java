package com.brandon3055.draconicevolution.common.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import cofh.api.energy.IEnergyContainerItem;

import com.brandon3055.draconicevolution.common.blocks.DraconiumChest;
import com.brandon3055.draconicevolution.common.inventory.InventoryCraftingChest;
import com.brandon3055.draconicevolution.common.inventory.InventoryCraftingChestResult;
import com.brandon3055.draconicevolution.common.lib.OreDoublingRegistry;
import com.brandon3055.draconicevolution.common.tileentities.TileDraconiumChest;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import invtweaks.api.container.ChestContainer;

@ChestContainer(isLargeChest = false, rowSize = 26)
public class ContainerDraconiumChest extends Container {

    /**
     * The crafting matrix inventory (3x3).
     */
    public InventoryCrafting craftMatrix;

    public IInventory craftResult;
    private TileDraconiumChest tile;
    private EntityPlayer player;
    private World worldObj;

    private int lastProgressTime;
    /**
     * how much energy is stored in the tile
     */
    private int lastEnergyStored;

    private int lastBurnSpeed;
    private int lastTickFeedMode;
    private boolean lastTickOutputLock;

    public ContainerDraconiumChest(InventoryPlayer invPlayer, TileDraconiumChest tile) {
        this.tile = tile;
        this.player = invPlayer.player;
        this.worldObj = tile.getWorldObj();
        tile.openInventory();

        craftMatrix = new InventoryCraftingChest(this, 3, 3, tile);
        craftResult = new InventoryCraftingChestResult(tile);

        addContainerSlots();

        addCraftingSlots();

        addPlayerInventory();

        onCraftMatrixChanged(craftMatrix);
    }

    private void addContainerSlots() {
        for (int chestRow = 0; chestRow < 9; chestRow++) {
            for (int chestCol = 0; chestCol < 26; chestCol++) {
                addSlotToContainer(
                        new SlotDChest(tile, chestCol + (chestRow * 26), 8 + chestCol * 18, 15 + chestRow * 18));
            }
        }
    }

    private void addPlayerInventory() {
        for (int playerInvRow = 0; playerInvRow < 3; playerInvRow++) {
            for (int playerInvCol = 0; playerInvCol < 9; playerInvCol++) {
                addSlotToContainer(
                        new Slot(
                                player.inventory,
                                playerInvCol + (playerInvRow * 9) + 9,
                                161 + 18 * playerInvCol,
                                179 + playerInvRow * 18));
            }
        }

        for (int hotbarSlot = 0; hotbarSlot < 9; hotbarSlot++) {
            addSlotToContainer(new Slot(player.inventory, hotbarSlot, 161 + 18 * hotbarSlot, 235));
        }
    }

    private void addCraftingSlots() {
        int yOffset = 188;
        int xOffset = 334;

        addSlotToContainer(new SlotChargable(tile, 239, 8, 235));

        for (int row = 0; row < 5; row++) {
            addSlotToContainer(new SlotSmeltable(tile, 234 + row, 45 + row * 18, 197));
        }

        addSlotToContainer(new SlotCrafting(player, craftMatrix, craftResult, 0, xOffset + 94, yOffset + 18));

        for (int gridCol = 0; gridCol < 3; ++gridCol) {
            for (int gridRow = 0; gridRow < 3; ++gridRow) {
                addSlotToContainer(
                        new SlotDChest(
                                craftMatrix,
                                gridRow + (gridCol * 3),
                                xOffset + (gridRow * 18),
                                yOffset + (gridCol * 18)));
            }
        }
    }

    @Override
    public void onCraftMatrixChanged(IInventory inventory) {
        craftResult
                .setInventorySlotContents(0, CraftingManager.getInstance().findMatchingRecipe(craftMatrix, worldObj));
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return tile.isUseableByPlayer(player);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int i) {
        ItemStack itemstack = null;
        Slot slot = (Slot) inventorySlots.get(i);
        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            if (i < tile.getSizeInventory() + 16 && i != 240) // Transferring from container
            {
                if (!mergeItemStack(itemstack1, tile.getSizeInventory() + 16, inventorySlots.size(), true)) {
                    return null;
                }
                slot.onSlotChange(itemstack1, itemstack);
            } else if (i == 240) {
                if (!mergeItemStack(itemstack1, tile.getSizeInventory() + 16, inventorySlots.size(), true)) {
                    if (!mergeItemStack(itemstack1, 0, tile.getSizeInventory(), false)) {
                        return null;
                    }
                }
                slot.onSlotChange(itemstack1, itemstack);
            } else if (!DraconiumChest.isStackValid(itemstack1)
                    || !mergeItemStack(itemstack1, 0, tile.getSizeInventory(), false)) // Transferring from player
            {
                return null;
            }
            if (itemstack1.stackSize == 0) {
                slot.putStack(null);
            } else {
                slot.onSlotChanged();
            }
            slot.onPickupFromSlot(player, itemstack1);
        }
        return itemstack;
    }

    @Override
    public void onContainerClosed(EntityPlayer p_75134_1_) {
        super.onContainerClosed(p_75134_1_);
        tile.closeInventory();
        if (!worldObj.isRemote) {
            for (int i = 0; i < 9; ++i) {
                ItemStack itemstack = craftMatrix.getStackInSlotOnClosing(i);

                if (itemstack != null) {
                    player.dropPlayerItemWithRandomChoice(itemstack, false);
                }
            }
        }
    }

    @Override
    public boolean func_94530_a(ItemStack par1ItemStack, Slot par2Slot) {
        return par2Slot.inventory != craftResult && super.func_94530_a(par1ItemStack, par2Slot);
    }

    @Override
    public void addCraftingToCrafters(ICrafting iCrafting) {
        super.addCraftingToCrafters(iCrafting);
        // iCrafting.sendProgressBarUpdate(this, 0, tile.smeltingProgressTime);
        // iCrafting.sendProgressBarUpdate(this, 1, tile.getEnergyStored(ForgeDirection.DOWN)/32);
        // iCrafting.sendProgressBarUpdate(this, 2, tile.smeltingBurnSpeed);
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        for (int i = 0; i < this.crafters.size(); ++i) {
            ICrafting icrafting = (ICrafting) this.crafters.get(i);

            if (lastProgressTime != tile.smeltingProgressTime) {
                icrafting.sendProgressBarUpdate(this, 0, tile.smeltingProgressTime);
            }
            if (lastEnergyStored != tile.getEnergyStored(ForgeDirection.DOWN)) {
                icrafting.sendProgressBarUpdate(this, 1, tile.getEnergyStored(ForgeDirection.DOWN) / 32);
            }
            if (lastBurnSpeed != tile.smeltingBurnSpeed) {
                icrafting.sendProgressBarUpdate(this, 2, tile.smeltingBurnSpeed);
            }
            if (lastTickFeedMode != tile.smeltingAutoFeed) {
                icrafting.sendProgressBarUpdate(this, 3, tile.smeltingAutoFeed);
            }
            if (lastTickOutputLock != tile.lockOutputSlots) {
                icrafting.sendProgressBarUpdate(this, 4, tile.lockOutputSlots ? 1 : 0);
            }
        }

        lastTickOutputLock = tile.lockOutputSlots;
        lastTickFeedMode = tile.smeltingAutoFeed;
        lastBurnSpeed = tile.smeltingBurnSpeed;
        lastProgressTime = tile.smeltingProgressTime;
        lastEnergyStored = tile.getEnergyStored(ForgeDirection.DOWN);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void updateProgressBar(int id, int value) {
        if (id == 0) tile.smeltingProgressTime = value;
        else if (id == 1) tile.energy.setEnergyStored(value * 32);
        else if (id == 2) tile.smeltingBurnSpeed = value;
        else if (id == 3) tile.smeltingAutoFeed = value;
        else if (id == 4) tile.lockOutputSlots = value == 1;
    }

    public TileDraconiumChest getTile() {
        return tile;
    }

    public class SlotSmeltable extends Slot {

        public SlotSmeltable(IInventory p_i1824_1_, int p_i1824_2_, int p_i1824_3_, int p_i1824_4_) {
            super(p_i1824_1_, p_i1824_2_, p_i1824_3_, p_i1824_4_);
        }

        @Override
        public boolean isItemValid(ItemStack stack) {
            return FurnaceRecipes.smelting().getSmeltingResult(stack) != null
                    || OreDoublingRegistry.getOreResult(stack) != null;
        }
    }

    public class SlotChargable extends Slot {

        public SlotChargable(IInventory inventory, int id, int x, int y) {
            super(inventory, id, x, y);
        }

        @Override
        public boolean isItemValid(ItemStack stack) {
            if (super.isItemValid(stack)) {
                return stack != null && stack.getItem() instanceof IEnergyContainerItem;
            }
            return false;
        }

        @Override
        public int getSlotStackLimit() {
            return 1;
        }
    }

    public class SlotDChest extends Slot {

        public SlotDChest(IInventory inventory, int id, int x, int y) {
            super(inventory, id, x, y);
        }

        @Override
        public boolean isItemValid(ItemStack stack) {
            return DraconiumChest.isStackValid(stack);
        }
    }
}
