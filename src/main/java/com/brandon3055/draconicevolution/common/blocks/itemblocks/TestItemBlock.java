package com.brandon3055.draconicevolution.common.blocks.itemblocks;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

/**
 * Created by Brandon on 23/06/2014.
 */
public class TestItemBlock extends ItemBlock {

    public TestItemBlock(Block block) {
        super(block);
        setHasSubtypes(true);
        // this.hasSubtypes = true;
    }

    @Override
    public int getMetadata(int par1) {
        return par1;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return super.getUnlocalizedName(stack) + stack.getItemDamage();
    }
}
