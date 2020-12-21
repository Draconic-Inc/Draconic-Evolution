package com.brandon3055.draconicevolution.init;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;

/**
 * Created by brandon3055 on 21/12/20
 */
public class DETags {

    public static void init(){
        Items.init();
        Blocks.init();
    }

    public static class Items {
        private static void init(){}

        public static final Tags.IOptionalNamedTag<Item> DUSTS_DRACONIUM = tag("dusts/draconium");
        public static final Tags.IOptionalNamedTag<Item> DUSTS_DRACONIUM_AWAKENED = tag("dusts/draconium_awakened");

        public static final Tags.IOptionalNamedTag<Item> NUGGETS_DRACONIUM = tag("nuggets/draconium");
        public static final Tags.IOptionalNamedTag<Item> NUGGETS_DRACONIUM_AWAKENED = tag("nuggets/draconium_awakened");

        public static final Tags.IOptionalNamedTag<Item> INGOTS_DRACONIUM = tag("ingots/draconium");
        public static final Tags.IOptionalNamedTag<Item> INGOTS_DRACONIUM_AWAKENED = tag("ingots/draconium_awakened");

        public static final Tags.IOptionalNamedTag<Item> STORAGE_BLOCKS_DRACONIUM = tag("storage_blocks/draconium");
        public static final Tags.IOptionalNamedTag<Item> STORAGE_BLOCKS_DRACONIUM_AWAKENED = tag("storage_blocks/draconium_awakened");

        public static final Tags.IOptionalNamedTag<Item> ORES_DRACONIUM = tag("ores/draconium");

        private static Tags.IOptionalNamedTag<Item> tag(String name) {
            return ItemTags.createOptional(new ResourceLocation("forge", name));
        }
    }

    public static class Blocks {
        private static void init(){}

        public static final Tags.IOptionalNamedTag<Block> STORAGE_BLOCKS_DRACONIUM = tag("storage_blocks/draconium");
        public static final Tags.IOptionalNamedTag<Block> STORAGE_BLOCKS_DRACONIUM_AWAKENED = tag("storage_blocks/draconium_awakened");

        public static final Tags.IOptionalNamedTag<Block> ORES_DRACONIUM = tag("ores/draconium");

        private static Tags.IOptionalNamedTag<Block> tag(String name) {
            return BlockTags.createOptional(new ResourceLocation("forge", name));
        }
    }
}
