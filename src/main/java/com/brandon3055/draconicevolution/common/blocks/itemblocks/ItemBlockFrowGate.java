package com.brandon3055.draconicevolution.common.blocks.itemblocks;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

/**
 * Created by Brandon on 25/6/2015.
 */
public class ItemBlockFrowGate extends ItemBlock {

    public ItemBlockFrowGate(Block block) {
        super(block);
        this.setHasSubtypes(true);
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return super.getUnlocalizedName(stack) + (stack.getItemDamage() / 6 == 0 ? "Flux" : "Fluid");
    }

    @Override
    public int getMetadata(int p_77647_1_) {
        return p_77647_1_;
    }
}
