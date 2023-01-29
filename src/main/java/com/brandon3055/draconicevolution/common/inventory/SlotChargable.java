package com.brandon3055.draconicevolution.common.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import cofh.api.energy.IEnergyContainerItem;

import com.brandon3055.draconicevolution.common.tileentities.TileEnergyInfuser;

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

    @Override
    public boolean func_111238_b() {
        return !((TileEnergyInfuser) inventory).running;
    }
}
