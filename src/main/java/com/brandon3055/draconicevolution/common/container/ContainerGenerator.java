package com.brandon3055.draconicevolution.common.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;

import com.brandon3055.draconicevolution.common.inventory.SlotItemValid;
import com.brandon3055.draconicevolution.common.tileentities.TileGenerator;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ContainerGenerator extends ContainerDataSync {

    private TileGenerator tile;
    private int energyCach = -1;
    private int burnCach = -1;
    private int burnRemainingCach = -1;

    public ContainerGenerator(InventoryPlayer invPlayer, TileGenerator tile) {
        this.tile = tile;

        for (int x = 0; x < 9; x++) {
            addSlotToContainer(new Slot(invPlayer, x, 8 + 18 * x, 138));
        }

        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                addSlotToContainer(new Slot(invPlayer, x + y * 9 + 9, 8 + 18 * x, 80 + y * 18));
            }
        }

        addSlotToContainer(new SlotItemValid(tile, 0, 64, 35, true));
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return tile.isUseableByPlayer(player);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int i) {
        Slot slot = getSlot(i);

        if (slot != null && slot.getHasStack()) {
            ItemStack stack = slot.getStack();
            ItemStack result = stack.copy();

            if (i >= 36) {
                if (!mergeItemStack(stack, 0, 36, false)) {
                    return null;
                }
            } else if (TileGenerator.getItemBurnTime(stack) == 0
                    || !mergeItemStack(stack, 36, 36 + tile.getSizeInventory(), false)) {
                        return null;
                    }

            if (stack.stackSize == 0) {
                slot.putStack(null);
            } else {
                slot.onSlotChanged();
            }

            slot.onPickupFromSlot(player, stack);

            return result;
        }

        return null;
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        if (energyCach != tile.getEnergyStored(ForgeDirection.UP))
            energyCach = (Integer) sendObjectToClient(null, 0, tile.getEnergyStored(ForgeDirection.UP));
        if (burnCach != tile.burnTime) burnCach = (Integer) sendObjectToClient(null, 1, tile.burnTime);
        if (burnRemainingCach != tile.burnTimeRemaining)
            burnRemainingCach = (Integer) sendObjectToClient(null, 2, tile.burnTimeRemaining);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void receiveSyncData(int index, int i) {
        if (index == 0) tile.storage.setEnergyStored(i);
        else if (index == 1) tile.burnTime = i;
        else if (index == 2) tile.burnTimeRemaining = i;
    }
}
