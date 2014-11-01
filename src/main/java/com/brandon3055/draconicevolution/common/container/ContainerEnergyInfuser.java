package com.brandon3055.draconicevolution.common.container;

import com.brandon3055.draconicevolution.common.inventory.SlotChargable;
import com.brandon3055.draconicevolution.common.core.utills.EnergyHelper;
import com.brandon3055.draconicevolution.common.tileentities.TileEnergyInfuser;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerEnergyInfuser extends Container {

	private TileEnergyInfuser tile;
	private EntityPlayer player;

	public ContainerEnergyInfuser(InventoryPlayer invPlayer, TileEnergyInfuser tile) {
		this.tile = tile;
		this.player = invPlayer.player;

		for (int x = 0; x < 9; x++) {
			addSlotToContainer(new Slot(invPlayer, x, 8 + 18 * x, 116));
		}

		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 9; x++) {
				addSlotToContainer(new Slot(invPlayer, x + y * 9 + 9, 8 + 18 * x, 58 + y * 18));
			}
		}
		
		addSlotToContainer(new SlotChargable(tile, 0, 80, 22));
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
			}else if (!EnergyHelper.isEnergyContainerItem(stack) || !mergeItemStack(stack, 36, 36 + tile.getSizeInventory(), false)){
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
	public void addCraftingToCrafters(ICrafting par1ICrafting) {
		super.addCraftingToCrafters(par1ICrafting);
	}

	@Override
	public void updateProgressBar(int par1, int par2) {
		super.updateProgressBar(par1, par2);
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
	}
}
