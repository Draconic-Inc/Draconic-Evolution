package com.brandon3055.draconicevolution.common.inventory;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;

import cpw.mods.fml.common.registry.GameRegistry;

public class SlotItemValid extends Slot {

    private Item item;
    private boolean fuel = false;

    public SlotItemValid(IInventory inventory, int id, int x, int y, Item validItem) {
        super(inventory, id, x, y);

        this.item = validItem;
    }

    public SlotItemValid(IInventory inventory, int id, int x, int y, boolean fuel) {
        super(inventory, id, x, y);

        this.fuel = fuel;
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        if (fuel) return getItemBurnTime(stack) > 0;
        else return stack.isItemEqual(new ItemStack(item));
    }

    public static int getItemBurnTime(ItemStack stack) {
        if (stack == null) {
            return 0;
        } else {
            Item item = stack.getItem();

            if (item instanceof ItemBlock && Block.getBlockFromItem(item) != Blocks.air) {
                Block block = Block.getBlockFromItem(item);

                if (block == Blocks.wooden_slab) {
                    return 150;
                }

                if (block.getMaterial() == Material.wood) {
                    return 300;
                }

                if (block == Blocks.coal_block) {
                    return 16000;
                }
            }

            if (item instanceof ItemTool && ((ItemTool) item).getToolMaterialName().equals("WOOD")) return 200;
            if (item instanceof ItemSword && ((ItemSword) item).getToolMaterialName().equals("WOOD")) return 200;
            if (item instanceof ItemHoe && ((ItemHoe) item).getToolMaterialName().equals("WOOD")) return 200;
            if (item == Items.stick) return 100;
            if (item == Items.coal) return 1600;
            if (item == Items.lava_bucket) return 20000;
            if (item == Item.getItemFromBlock(Blocks.sapling)) return 100;
            if (item == Items.blaze_rod) return 2400;
            return GameRegistry.getFuelValue(stack);
        }
    }
    /*
     * @Override public IIcon getBackgroundIconIndex() { IIcon icon = Items.coal.getIconIndex(new
     * ItemStack(Items.coal)); if (false) return icon; else return super.getBackgroundIconIndex(); }
     */
}
