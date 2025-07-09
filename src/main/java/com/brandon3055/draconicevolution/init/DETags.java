package com.brandon3055.draconicevolution.init;

import com.brandon3055.draconicevolution.DraconicEvolution;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

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

        public static final TagKey<Item> DUSTS_DRACONIUM = tag("dusts/draconium");
        public static final TagKey<Item> DUSTS_DRACONIUM_AWAKENED = tag("dusts/draconium_awakened");

        public static final TagKey<Item> NUGGETS_DRACONIUM = tag("nuggets/draconium");
        public static final TagKey<Item> NUGGETS_DRACONIUM_AWAKENED = tag("nuggets/draconium_awakened");

        public static final TagKey<Item> INGOTS_DRACONIUM = tag("ingots/draconium");
        public static final TagKey<Item> INGOTS_DRACONIUM_AWAKENED = tag("ingots/draconium_awakened");

        public static final TagKey<Item> STORAGE_BLOCKS_DRACONIUM = tag("storage_blocks/draconium");
        public static final TagKey<Item> STORAGE_BLOCKS_DRACONIUM_AWAKENED = tag("storage_blocks/draconium_awakened");

        public static final TagKey<Item> ORES_DRACONIUM = tag("ores/draconium");

        public static final TagKey<Item> MODULES = ItemTags.create(ResourceLocation.fromNamespaceAndPath(DraconicEvolution.MODID, "modules"));

        private static TagKey<Item> tag(String name) {
            return ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", name));
        }
    }

    public static class Blocks {
        private static void init(){}

        public static final TagKey<Block> STORAGE_BLOCKS_DRACONIUM = tag("storage_blocks/draconium");
        public static final TagKey<Block> STORAGE_BLOCKS_DRACONIUM_AWAKENED = tag("storage_blocks/draconium_awakened");

        public static final TagKey<Block> ORES_DRACONIUM = tag("ores/draconium");

        public static final TagKey<Block> NEEDS_WYVERN_TOOL = BlockTags.create(ResourceLocation.fromNamespaceAndPath(DraconicEvolution.MODID, "needs_wyvern_tool"));
        public static final TagKey<Block> NEEDS_AWAKENED_TOOL = BlockTags.create(ResourceLocation.fromNamespaceAndPath(DraconicEvolution.MODID, "needs_awakened_tool"));
        public static final TagKey<Block> NEEDS_CHAOTIC_TOOL = BlockTags.create(ResourceLocation.fromNamespaceAndPath(DraconicEvolution.MODID, "needs_chaotic_tool"));

        public static final TagKey<Block> INCORRECT_FOR_WYVERN_TOOL = BlockTags.create(ResourceLocation.fromNamespaceAndPath(DraconicEvolution.MODID, "incorrect_for_wyvern_tool"));
        public static final TagKey<Block> INCORRECT_FOR_AWAKENED_TOOL = BlockTags.create(ResourceLocation.fromNamespaceAndPath(DraconicEvolution.MODID, "incorrect_for_awakened_tool"));
        public static final TagKey<Block> INCORRECT_FOR_CHAOTIC_TOOL = BlockTags.create(ResourceLocation.fromNamespaceAndPath(DraconicEvolution.MODID, "incorrect_for_chaotic_tool"));

        public static final TagKey<Block> MINEABLE_WITH_STAFF = BlockTags.create(ResourceLocation.fromNamespaceAndPath(DraconicEvolution.MODID, "mineable_with_staff"));

        private static TagKey<Block> tag(String name) {
            return BlockTags.create(ResourceLocation.fromNamespaceAndPath("c", name));
        }
    }
}
