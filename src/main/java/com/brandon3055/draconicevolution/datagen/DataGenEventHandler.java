package com.brandon3055.draconicevolution.datagen;

import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.init.DEContent;
import com.brandon3055.draconicevolution.init.DETags;
import com.brandon3055.draconicevolution.integration.equipment.CuriosIntegration;
import com.google.common.collect.Sets;
import com.google.gson.*;
import net.minecraft.advancements.Advancement;
import net.minecraft.data.*;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Consumer;

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
        protected void addTags() {
            tag(DETags.Items.DUSTS_DRACONIUM).add(DEContent.dust_draconium);
            tag(DETags.Items.DUSTS_DRACONIUM_AWAKENED).add(DEContent.dust_draconium_awakened);
            tag(Tags.Items.DUSTS).addTags(DETags.Items.DUSTS_DRACONIUM_AWAKENED, DETags.Items.DUSTS_DRACONIUM);

            tag(DETags.Items.NUGGETS_DRACONIUM).add(DEContent.nugget_draconium);
            tag(DETags.Items.NUGGETS_DRACONIUM_AWAKENED).add(DEContent.nugget_draconium_awakened);
            tag(Tags.Items.NUGGETS).addTags(DETags.Items.NUGGETS_DRACONIUM_AWAKENED, DETags.Items.NUGGETS_DRACONIUM);

            tag(DETags.Items.INGOTS_DRACONIUM).add(DEContent.ingot_draconium);
            tag(DETags.Items.INGOTS_DRACONIUM_AWAKENED).add(DEContent.ingot_draconium_awakened);
            tag(Tags.Items.INGOTS).addTags(DETags.Items.INGOTS_DRACONIUM_AWAKENED, DETags.Items.INGOTS_DRACONIUM);


            tag(DETags.Items.STORAGE_BLOCKS_DRACONIUM).add(DEContent.block_draconium.asItem());
            tag(DETags.Items.STORAGE_BLOCKS_DRACONIUM_AWAKENED).add(DEContent.block_draconium_awakened.asItem());
            tag(Tags.Items.STORAGE_BLOCKS).addTags(DETags.Items.STORAGE_BLOCKS_DRACONIUM, DETags.Items.STORAGE_BLOCKS_DRACONIUM_AWAKENED);

            tag(DETags.Items.ORES_DRACONIUM).add(DEContent.ore_draconium_end.asItem(), DEContent.ore_draconium_nether.asItem(), DEContent.ore_draconium_overworld.asItem());
            tag(Tags.Items.ORES).addTag(DETags.Items.ORES_DRACONIUM);

            if (ModList.get().isLoaded("curios")) {
                CuriosIntegration.generateTags(this::tag);
            }
        }
    }

    private static class BlockTagGenerator extends BlockTagsProvider {
        public BlockTagGenerator(DataGenerator generatorIn, String modId, @Nullable ExistingFileHelper existingFileHelper) {
            super(generatorIn, modId, existingFileHelper);
        }

        @Override
        protected void addTags() {
            tag(DETags.Blocks.STORAGE_BLOCKS_DRACONIUM).add(DEContent.block_draconium);
            tag(DETags.Blocks.STORAGE_BLOCKS_DRACONIUM_AWAKENED).add(DEContent.block_draconium_awakened);
            tag(Tags.Blocks.STORAGE_BLOCKS).add(DEContent.block_draconium, DEContent.block_draconium_awakened);

            tag(DETags.Blocks.ORES_DRACONIUM).add(DEContent.ore_draconium_end, DEContent.ore_draconium_nether, DEContent.ore_draconium_overworld);
            tag(Tags.Blocks.ORES).add(DEContent.ore_draconium_end, DEContent.ore_draconium_nether, DEContent.ore_draconium_overworld);
        }
    }

    private static class AdvancementBuilder extends AdvancementProvider {
        private DataGenerator generator;
        private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();

        public AdvancementBuilder(DataGenerator generator) {
            super(generator);
            this.generator = generator;
        }

        private void aggAdvancements(Consumer<Advancement> consumer) {

//            Advancement advancement = Advancement.Builder.builder()
//                    .withDisplay(Blocks.END_STONE, new TranslationTextComponent("advancements.end.root.title"), new TranslationTextComponent("advancements.end.root.description"), new ResourceLocation("textures/gui/advancements/backgrounds/end.png"), FrameType.TASK, false, false, false)
//                    .withCriterion("entered_end", ChangeDimensionTrigger.Instance.toWorld(World.THE_END))
//                    .register(consumer, "end/root");
        }

        @Override
        public void run(DirectoryCache cache) throws IOException {
            Path path = this.generator.getOutputFolder();
            Set<ResourceLocation> set = Sets.newHashSet();
            Consumer<Advancement> consumer = (advancement) -> {
                if (!set.add(advancement.getId())) {
                    throw new IllegalStateException("Duplicate advancement " + advancement.getId());
                } else {
                    Path path1 = makePath(advancement.getId());

                    try {
                        IDataProvider.save(GSON, cache, advancement.deconstruct().serializeToJson(), path1);
                    } catch (IOException ioexception) {
                        DraconicEvolution.LOGGER.error("Couldn't save advancement {}", path1, ioexception);
                    }

                }
            };

            aggAdvancements(consumer);


        }

//        private static Path getPath(Path pathIn, Advancement advancementIn) {
//            return pathIn.resolve("data/" + advancementIn.getId().getNamespace() + "/advancements/" + advancementIn.getId().getPath() + ".json");
//        }

        protected Path makePath(ResourceLocation id) {
            return this.generator.getOutputFolder().resolve("data/" + id.getNamespace() + "/advancements/" + id.getPath() + ".json");
        }
    }

}
