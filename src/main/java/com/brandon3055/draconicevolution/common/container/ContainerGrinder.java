package com.brandon3055.draconicevolution.common.container;

import com.brandon3055.draconicevolution.client.interfaces.SlotItemValid;
import com.brandon3055.draconicevolution.common.tileentities.TileGrinder;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerGrinder extends Container {

	private int cachSize = -1;
	private TileGrinder tile;

	public ContainerGrinder(InventoryPlayer invPlayer, TileGrinder tile) {
		this.tile = tile;

		for (int x = 0; x < 9; x++) {
			addSlotToContainer(new Slot(invPlayer, x, 8 + 18 * x, 138));
		}

		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 9; x++) {
				addSlotToContainer(new Slot(invPlayer, x + y * 9 + 9, 8 + 18 * x, 80 + y * 18));
			}
		}
		if (!tile.isExternallyPowered())
			addSlotToContainer(new SlotItemValid(tile, 0, 64, 35, true));
		else
			addSlotToContainer(new SlotItemValid(tile, 0, -10000, -10000, true));

	}

	@Override
	public boolean canInteractWith(EntityPlayer player)
	{
		return tile.isUseableByPlayer(player);
	}

	
	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int i)
	{
		Slot slot = getSlot(i);

		if (slot != null && slot.getHasStack())
		{
			ItemStack stack = slot.getStack();
			ItemStack result = stack.copy();

			if (i >= 36){
				if (!mergeItemStack(stack, 0, 36, false)){
					return null;
				}
			}else if (TileGrinder.getItemBurnTime(stack) == 0 || !mergeItemStack(stack, 36, 36 + tile.getSizeInventory(), false)){
				return null;
			}

			if (stack.stackSize == 0) {
				slot.putStack(null);
			}else{
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
		ItemStack stack = tile.getStackInSlot(0);
		int size = stack == null ? 0 : stack.stackSize;
		if (cachSize != size) {
			cachSize = size;
			tile.tryRefuel();
			tile.getWorldObj().markBlockForUpdate(tile.xCoord, tile.yCoord, tile.zCoord);
		}
	}
}
