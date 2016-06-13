package com.brandon3055.draconicevolution.inventory;

import com.brandon3055.brandonscore.inventory.ContainerBCBase;
import com.brandon3055.draconicevolution.blocks.tileentity.TileFusionCraftingCore;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

/**
 * Created by brandon3055 on 30/3/2016.
 */
public class ContainerFusionCraftingCore extends ContainerBCBase<TileFusionCraftingCore> {

	public ContainerFusionCraftingCore(EntityPlayer player, TileFusionCraftingCore tile) {
		super(player, tile);

//		for (int x = 0; x < 9; x++) {
//			addSlotToContainer(new Slot(invPlayer, x, 8 + 18 * x, 138));
//		}
//
//		for (int y = 0; y < 3; y++) {
//			for (int x = 0; x < 9; x++) {
//				addSlotToContainer(new Slot(invPlayer, x + y * 9 + 9, 8 + 18 * x, 80 + y * 18));
//			}
//		}

        addPlayerSlots(10, 116);
		addSlotToContainer(new Slot(tile, 0, 97, 36));
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
			}else if (!tile.isItemValidForSlot(0, stack) || !mergeItemStack(stack, 36, 36 + tile.getSizeInventory(), false)){
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
}
