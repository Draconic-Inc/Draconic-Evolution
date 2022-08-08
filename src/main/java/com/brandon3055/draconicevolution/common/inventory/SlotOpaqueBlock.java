package com.brandon3055.draconicevolution.common.inventory;

import net.minecraft.block.Block;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotOpaqueBlock extends Slot {

    public SlotOpaqueBlock(IInventory inventory, int id, int x, int y) {
        super(inventory, id, x, y);
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        if (super.isItemValid(stack)) {
            Block block = Block.getBlockFromItem(stack.getItem());
            return block.isOpaqueCube() && block.renderAsNormalBlock();
        }
        return false;
    }

    @Override
    public int getSlotStackLimit() {
        return 1;
    }
}
