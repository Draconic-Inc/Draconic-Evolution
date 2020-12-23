package com.brandon3055.draconicevolution.datagen;

import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.init.DEContent;
import com.brandon3055.draconicevolution.init.DETags;
import com.google.gson.*;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.FrameType;
import net.minecraft.block.Blocks;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemTransformVec3f;
import net.minecraft.client.renderer.model.Variant;
import net.minecraft.data.*;
import net.minecraft.resources.IResource;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.generators.*;
import net.minecraftforge.client.model.generators.ModelBuilder.Perspective;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.ConditionalAdvancement;
import net.minecraftforge.common.crafting.ConditionalRecipe;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import org.jline.utils.InputStreamReader;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;

import static com.brandon3055.draconicevolution.DraconicEvolution.MODID;

/**
 * Created by brandon3055 on 26/2/20.
 */
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenEventHandler {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();

        if (event.includeClient()) {
            gen.addProvider(new LangGenerator(gen));
            gen.addProvider(new BlockStateGenerator(gen, event.getExistingFileHelper()));
            gen.addProvider(new ItemModelGenerator(gen, event.getExistingFileHelper()));
        }

        if (event.includeServer()) {
            gen.addProvider(new RecipeGenerator(gen));
            gen.addProvider(new LootTableGenerator(gen));
            BlockTagGenerator blockGenerator = new BlockTagGenerator(gen, DraconicEvolution.MODID, event.getExistingFileHelper());
            gen.addProvider(blockGenerator);
            gen.addProvider(new ItemTagGenerator(gen, blockGenerator, DraconicEvolution.MODID, event.getExistingFileHelper()));
        }
    }

    private static class ItemTagGenerator extends ItemTagsProvider {
        public ItemTagGenerator(DataGenerator dataGenerator, BlockTagsProvider blockTagProvider, String modId, @Nullable ExistingFileHelper existingFileHelper) {
            super(dataGenerator, blockTagProvider, modId, existingFileHelper);
        }

        @Override
        protected void registerTags() {
            getOrCreateBuilder(DETags.Items.DUSTS_DRACONIUM).add(DEContent.dust_draconium);
            getOrCreateBuilder(DETags.Items.DUSTS_DRACONIUM_AWAKENED).add(DEContent.dust_draconium_awakened);
            getOrCreateBuilder(Tags.Items.DUSTS).addTags(DETags.Items.DUSTS_DRACONIUM_AWAKENED, DETags.Items.DUSTS_DRACONIUM);

            getOrCreateBuilder(DETags.Items.NUGGETS_DRACONIUM).add(DEContent.nugget_draconium);
            getOrCreateBuilder(DETags.Items.NUGGETS_DRACONIUM_AWAKENED).add(DEContent.nugget_draconium_awakened);
            getOrCreateBuilder(Tags.Items.NUGGETS).addTags(DETags.Items.NUGGETS_DRACONIUM_AWAKENED, DETags.Items.NUGGETS_DRACONIUM);

            getOrCreateBuilder(DETags.Items.INGOTS_DRACONIUM).add(DEContent.ingot_draconium);
            getOrCreateBuilder(DETags.Items.INGOTS_DRACONIUM_AWAKENED).add(DEContent.ingot_draconium_awakened);
            getOrCreateBuilder(Tags.Items.INGOTS).addTags(DETags.Items.INGOTS_DRACONIUM_AWAKENED, DETags.Items.INGOTS_DRACONIUM);


            getOrCreateBuilder(DETags.Items.STORAGE_BLOCKS_DRACONIUM).add(DEContent.block_draconium.asItem());
            getOrCreateBuilder(DETags.Items.STORAGE_BLOCKS_DRACONIUM_AWAKENED).add(DEContent.block_draconium_awakened.asItem());
            getOrCreateBuilder(Tags.Items.STORAGE_BLOCKS).addTags(DETags.Items.STORAGE_BLOCKS_DRACONIUM, DETags.Items.STORAGE_BLOCKS_DRACONIUM_AWAKENED);

            getOrCreateBuilder(DETags.Items.ORES_DRACONIUM).add(DEContent.ore_draconium_end.asItem(), DEContent.ore_draconium_nether.asItem(), DEContent.ore_draconium_overworld.asItem());
            getOrCreateBuilder(Tags.Items.ORES).addTag(DETags.Items.ORES_DRACONIUM);

        }
    }

    private static class BlockTagGenerator extends BlockTagsProvider {
        public BlockTagGenerator(DataGenerator generatorIn, String modId, @Nullable ExistingFileHelper existingFileHelper) {
            super(generatorIn, modId, existingFileHelper);
        }

        @Override
        protected void registerTags() {
            getOrCreateBuilder(DETags.Blocks.STORAGE_BLOCKS_DRACONIUM).add(DEContent.block_draconium);
            getOrCreateBuilder(DETags.Blocks.STORAGE_BLOCKS_DRACONIUM_AWAKENED).add(DEContent.block_draconium_awakened);
            getOrCreateBuilder(Tags.Blocks.STORAGE_BLOCKS).add(DEContent.block_draconium, DEContent.block_draconium_awakened);

            getOrCreateBuilder(DETags.Blocks.ORES_DRACONIUM).add(DEContent.ore_draconium_end, DEContent.ore_draconium_nether, DEContent.ore_draconium_overworld);
            getOrCreateBuilder(Tags.Blocks.ORES).add(DEContent.ore_draconium_end, DEContent.ore_draconium_nether, DEContent.ore_draconium_overworld);
        }
    }


}
