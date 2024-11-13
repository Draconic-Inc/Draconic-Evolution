package com.brandon3055.draconicevolution.datagen;

import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.api.modules.Module;
import com.brandon3055.draconicevolution.api.modules.ModuleRegistry;
import com.brandon3055.draconicevolution.init.DEContent;
import com.brandon3055.draconicevolution.init.DETags;
import com.brandon3055.draconicevolution.integration.equipment.CuriosIntegration;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * Created by brandon3055 on 26/2/20.
 */
@Mod.EventBusSubscriber (bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenEventHandler {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();

        gen.addProvider(event.includeClient(), new LangGenerator(gen.getPackOutput()));
        gen.addProvider(event.includeClient(), new BlockStateGenerator(gen, event.getExistingFileHelper()));
        gen.addProvider(event.includeClient(), new ItemModelGenerator(gen, event.getExistingFileHelper()));
        gen.addProvider(event.includeClient(), new ItemModelGenerator2DModels(gen, event.getExistingFileHelper()));
        gen.addProvider(event.includeClient(), new MultiBlockGenerator(gen));
        gen.addProvider(event.includeClient(), new DynamicTextures(gen, event.getExistingFileHelper()));


        gen.addProvider(event.includeServer(), new RecipeGenerator(gen.getPackOutput()));
        gen.addProvider(event.includeServer(), new LootTableProvider(event.getGenerator().getPackOutput(), Set.of(), List.of(new LootTableProvider.SubProviderEntry(BlockLootProvider::new, LootContextParamSets.BLOCK))));


        BlockTagGenerator blockGenerator = new BlockTagGenerator(gen.getPackOutput(), event.getLookupProvider(), DraconicEvolution.MODID, event.getExistingFileHelper());
        gen.addProvider(event.includeServer(), blockGenerator);
        gen.addProvider(event.includeServer(), new ItemTagGenerator(gen.getPackOutput(), event.getLookupProvider(), blockGenerator.contentsGetter(), DraconicEvolution.MODID, event.getExistingFileHelper()));
        gen.addProvider(event.includeServer(), new DamageTypeGenerator(gen.getPackOutput(), event.getLookupProvider(), DraconicEvolution.MODID, event.getExistingFileHelper()));

        gen.addProvider(event.includeServer(), new CuriosProvider(event.getGenerator().getPackOutput(), event.getExistingFileHelper(), event.getLookupProvider()));

    }

    private static class ItemTagGenerator extends ItemTagsProvider {

        public ItemTagGenerator(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pLookupProvider, CompletableFuture<TagLookup<Block>> pBlockTags, String modId, @Nullable ExistingFileHelper existingFileHelper) {
            super(pOutput, pLookupProvider, pBlockTags, modId, existingFileHelper);
        }

        @Override
        protected void addTags(HolderLookup.Provider pProvider) {
            tag(DETags.Items.DUSTS_DRACONIUM).add(DEContent.DUST_DRACONIUM.get());
            tag(DETags.Items.DUSTS_DRACONIUM_AWAKENED).add(DEContent.DUST_DRACONIUM_AWAKENED.get());
            tag(Tags.Items.DUSTS).addTags(DETags.Items.DUSTS_DRACONIUM_AWAKENED, DETags.Items.DUSTS_DRACONIUM);

            tag(DETags.Items.NUGGETS_DRACONIUM).add(DEContent.NUGGET_DRACONIUM.get());
            tag(DETags.Items.NUGGETS_DRACONIUM_AWAKENED).add(DEContent.NUGGET_DRACONIUM_AWAKENED.get());
            tag(Tags.Items.NUGGETS).addTags(DETags.Items.NUGGETS_DRACONIUM_AWAKENED, DETags.Items.NUGGETS_DRACONIUM);

            tag(DETags.Items.INGOTS_DRACONIUM).add(DEContent.INGOT_DRACONIUM.get());
            tag(DETags.Items.INGOTS_DRACONIUM_AWAKENED).add(DEContent.INGOT_DRACONIUM_AWAKENED.get());
            tag(Tags.Items.INGOTS).addTags(DETags.Items.INGOTS_DRACONIUM_AWAKENED, DETags.Items.INGOTS_DRACONIUM);

            tag(DETags.Items.STORAGE_BLOCKS_DRACONIUM).add(DEContent.ITEM_DRACONIUM_BLOCK.get());
            tag(DETags.Items.STORAGE_BLOCKS_DRACONIUM_AWAKENED).add(DEContent.ITEM_AWAKENED_DRACONIUM_BLOCK.get());
            tag(Tags.Items.STORAGE_BLOCKS).addTags(DETags.Items.STORAGE_BLOCKS_DRACONIUM, DETags.Items.STORAGE_BLOCKS_DRACONIUM_AWAKENED);

            tag(DETags.Items.ORES_DRACONIUM).add(DEContent.ITEM_END_DRACONIUM_ORE.get(), DEContent.ITEM_NETHER_DRACONIUM_ORE.get(), DEContent.ITEM_OVERWORLD_DRACONIUM_ORE.get());
            tag(Tags.Items.ORES).addTag(DETags.Items.ORES_DRACONIUM);

            for (Module<?> module : ModuleRegistry.getRegistry()) {
                tag(DETags.Items.MODULES).add(module.getItem());
            }

            if (ModList.get().isLoaded("curios")) {
                CuriosIntegration.generateTags(this::tag);
            }
        }

    }

//    private static class AdvancementBuilder extends AdvancementProvider {
//        private DataGenerator generator;
//        private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();
//
//        public AdvancementBuilder(DataGenerator generator) {
//            super(generator);
//            this.generator = generator;
//        }
//
//        private void aggAdvancements(Consumer<Advancement> consumer) {
//
////            Advancement advancement = Advancement.Builder.builder()
////                    .withDisplay(Blocks.END_STONE, new TranslationTextComponent("advancements.end.root.title"), new TranslationTextComponent("advancements.end.root.description"), new ResourceLocation("textures/gui/advancements/backgrounds/end.png"), FrameType.TASK, false, false, false)
////                    .withCriterion("entered_end", ChangeDimensionTrigger.Instance.toWorld(World.THE_END))
////                    .register(consumer, "end/root");
//        }
//
//        @Override
//        public void run(HashCache cache) {
//            Path path = this.generator.getOutputFolder();
//            Set<ResourceLocation> set = Sets.newHashSet();
//            Consumer<Advancement> consumer = (advancement) -> {
//                if (!set.add(advancement.getId())) {
//                    throw new IllegalStateException("Duplicate advancement " + advancement.getId());
//                } else {
//                    Path path1 = makePath(advancement.getId());
//
//                    try {
//                        DataProvider.save(GSON, cache, advancement.deconstruct().serializeToJson(), path1);
//                    }
//                    catch (IOException ioexception) {
//                        DraconicEvolution.LOGGER.error("Couldn't save advancement {}", path1, ioexception);
//                    }
//
//                }
//            };
//
//            aggAdvancements(consumer);
//
//
//        }
//
////        private static Path getPath(Path pathIn, Advancement advancementIn) {
////            return pathIn.resolve("data/" + advancementIn.getId().getNamespace() + "/advancements/" + advancementIn.getId().getPath() + ".json");
////        }
//
//        protected Path makePath(ResourceLocation id) {
//            return this.generator.getOutputFolder().resolve("data/" + id.getNamespace() + "/advancements/" + id.getPath() + ".json");
//        }
//    }

}
