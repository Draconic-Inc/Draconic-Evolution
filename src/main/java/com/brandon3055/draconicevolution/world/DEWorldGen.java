package com.brandon3055.draconicevolution.world;

import com.brandon3055.draconicevolution.DEConfig;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.init.DEContent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.features.OreFeatures;
import net.minecraft.data.worldgen.placement.OrePlacements;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.BlockColumnConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Random;

/**
 * Created by brandon3055 on 7/12/20
 */
@Mod.EventBusSubscriber(modid = DraconicEvolution.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DEWorldGen {
    public static final RuleTest BASE_STONE_END = new BlockMatchTest(Blocks.END_STONE);


    //Look at OrePlacements
    @SubscribeEvent
    public static void biomeLoading(BiomeLoadingEvent event) {
//        if (event.getCategory() == Biome.BiomeCategory.THEEND) {
//            if (DEConfig.enableOreEnd) {
//                public static final Holder<ConfiguredFeature<OreConfiguration, ?>> ORE_ANCIENT_DEBRIS_SMALL = FeatureUtils.register("ore_ancient_debris_small", Feature.SCATTERED_ORE, new OreConfiguration(NETHER_ORE_REPLACEABLES, Blocks.ANCIENT_DEBRIS.defaultBlockState(), 2, 1.0F));
//
//                Holder<ConfiguredFeature<OreConfiguration, ?>> gen = FeatureUtils.register(DraconicEvolution.MODID + ":ore_end", Feature.ORE, new OreConfiguration(BASE_STONE_END, DEContent.ore_draconium_end.defaultBlockState(), DEConfig.veinSizeEnd));
//
//                Holder<PlacedFeature> gen2 = PlacementUtils.register(DraconicEvolution.MODID + ":ore_end", gen, CountPlacement.of(DEConfig.veinSizeEnd), InSquarePlacement.spread(), HeightRangePlacement.uniform(VerticalAnchor.aboveBottom(8), VerticalAnchor.belowTop(8)));
//
//                event.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, gen2);
//

//                event.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, FeatureUtils.register("", Feature.ORE, new OreConfiguration(BASE_STONE_END, DEContent.ore_draconium_end.defaultBlockState(), DEConfig.veinSizeEnd)));



//                FeatureUtils.register("ore_dirt", Feature.ORE, new OreConfiguration(NATURAL_STONE, Blocks.DIRT.defaultBlockState(), 33));

//                event.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, Feature.ORE.configured(new OreConfiguration(BASE_STONE_END, DEContent.ore_draconium_end.defaultBlockState(), DEConfig.veinSizeEnd))
//                        .decorated(FeatureDecorator.RANGE.configured(new RangeDecoratorConfiguration(0, 0, 80)))
//                        .squared()
//                        .count(DEConfig.veinsPerChunkEnd));
//            }

//            event.getGeneration().addFeature(GenerationStep.Decoration.RAW_GENERATION, new ConfiguredFeature<>(new Feature<NoneFeatureConfiguration>(NoneFeatureConfiguration.CODEC) {
//                @Override
//                public boolean place(WorldGenLevel reader, ChunkGenerator generator, Random rand, BlockPos pos, NoneFeatureConfiguration config) {
//                    return ChaosWorldGenHandler.generateChunk(this, reader, generator, rand, pos);
//                }
//            }, NoneFeatureConfiguration.NONE));
//
//        } else if (event.getCategory() == Biome.BiomeCategory.NETHER && DEConfig.enableOreNether) {
//            event.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, Feature.ORE.configured(new OreConfiguration(OreConfiguration.Predicates.NETHER_ORE_REPLACEABLES, DEContent.ore_draconium_nether.defaultBlockState(), DEConfig.veinSizeNether))
//                    .chance(10)
//                    .decorated(FeatureDecorator.RANGE.configured(new RangeDecoratorConfiguration(4, 4, DEConfig.maxOreHeightNether))));
//        } else if (DEConfig.enableOreOverworld) {
//            event.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, Feature.ORE.configured(new OreConfiguration(OreConfiguration.Predicates.NATURAL_STONE, DEContent.ore_draconium_overworld.defaultBlockState(), DEConfig.veinSizeOverworld))
//                    .chance(10)
//                    .decorated(FeatureDecorator.RANGE.configured(new RangeDecoratorConfiguration(4, 4, DEConfig.maxOreHeightOverworld))));
//        }
    }
}
