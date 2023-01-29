package com.brandon3055.draconicevolution.common.blocks.itemblocks;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

/**
 * Created by Brandon on 27/08/2014.
 */
public class CKeyStoneItemBlock extends ItemBlock {

    public CKeyStoneItemBlock(Block block) {
        super(block);
        setHasSubtypes(true);
    }

    @Override
    public void getSubItems(Item item, CreativeTabs p_150895_2_, List list) {
        list.add(new ItemStack(item, 1, 0));
        list.add(new ItemStack(item, 1, 1));
        list.add(new ItemStack(item, 1, 2));
        list.add(new ItemStack(item, 1, 3));
    }

    @Override
    public int getMetadata(int par1) {
        return par1;
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer p_77624_2_, List list, boolean p_77624_4_) {
        int meta = stack.getItemDamage();
        switch (meta) {
            case 0:
                list.add("Permanent Activation (Consume Key)");
                break;
            case 1:
                list.add("Button Activation");
                break;
            case 2:
                list.add("Toggle Activation");
                break;
            case 3:
                list.add("Button Activation (Consume Key)");
                break;
        }
    }
}
