package com.brandon3055.draconicevolution.world;

import com.brandon3055.draconicevolution.DEConfig;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.init.DEContent;
import net.minecraft.block.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.feature.template.BlockMatchRuleTest;
import net.minecraft.world.gen.feature.template.RuleTest;
import net.minecraft.world.gen.placement.Placement;
import net.minecraft.world.gen.placement.TopSolidRangeConfig;
import net.minecraftforge.common.world.BiomeGenerationSettingsBuilder;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Random;

/**
 * Created by brandon3055 on 7/12/20
 */
@Mod.EventBusSubscriber(modid = DraconicEvolution.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DEWorldGen {
    public static final RuleTest BASE_STONE_END = new BlockMatchRuleTest(Blocks.END_STONE);

    public static void init() {
        ORE_DRACONIUM_END = register("ore_draconium_end",
                new ConfiguredFeature<>(Feature.ORE, new OreFeatureConfig(BASE_STONE_END, DEContent.ore_draconium_end.defaultBlockState(), DEConfig.veinSizeEnd))
                        .decorated(Placement.RANGE.configured(new TopSolidRangeConfig(0, 0, 80)))
                        .squared()
                        .count(DEConfig.veinsPerChunkEnd)
        );

        ORE_DRACONIUM_NETHER = register("ore_draconium_nether",
                new ConfiguredFeature<>(Feature.ORE, new OreFeatureConfig(OreFeatureConfig.FillerBlockType.NETHER_ORE_REPLACEABLES, DEContent.ore_draconium_nether.defaultBlockState(), DEConfig.veinSizeNether))
                        .decorated(Placement.RANGE.configured(new TopSolidRangeConfig(4, 4, DEConfig.maxOreHeightNether)))
                        .chance(10) //<- 1 in this number chance
        );

        ORE_DRACONIUM_OVERWORLD = register("ore_draconium_overworld",
                new ConfiguredFeature<>(Feature.ORE, new OreFeatureConfig(OreFeatureConfig.FillerBlockType.NATURAL_STONE, DEContent.ore_draconium_overworld.defaultBlockState(), DEConfig.veinSizeOverworld))
                        .decorated(Placement.RANGE.configured(new TopSolidRangeConfig(4, 4, DEConfig.maxOreHeightOverworld)))
                        .chance(10)
        );

        CHAOS_ISLAND = register("chaos_island",
                new ConfiguredFeature<>(new Feature<NoFeatureConfig>(NoFeatureConfig.CODEC) {
                    @Override
                    public boolean place(ISeedReader reader, ChunkGenerator generator, Random rand, BlockPos pos, NoFeatureConfig config) {
                        return ChaosWorldGenHandler.generateChunk(this, reader, generator, rand, pos);
                    }
                }, NoFeatureConfig.NONE)
        );
    }

    private static <FC extends IFeatureConfig> ConfiguredFeature<FC, ?> register(String key, ConfiguredFeature<FC, ?> configuredFeature) {
        return Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, new ResourceLocation(DraconicEvolution.MODID, key), configuredFeature);
    }

    public static ConfiguredFeature<?, ?> ORE_DRACONIUM_END;
    public static ConfiguredFeature<?, ?> ORE_DRACONIUM_OVERWORLD;
    public static ConfiguredFeature<?, ?> ORE_DRACONIUM_NETHER;
    public static ConfiguredFeature<?, ?> CHAOS_ISLAND;

    @SubscribeEvent
    public static void biomeLoading(BiomeLoadingEvent event) {
        BiomeGenerationSettingsBuilder builder = event.getGeneration();
        Biome.Category category = event.getCategory();

        if (category == Biome.Category.THEEND) {
            if (DEConfig.enableOreEnd) {
                builder.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, ORE_DRACONIUM_END);
            }

            builder.addFeature(GenerationStage.Decoration.RAW_GENERATION, CHAOS_ISLAND);
        } else if (category == Biome.Category.NETHER) {
            if (DEConfig.enableOreNether){
                builder.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, ORE_DRACONIUM_NETHER);
            }
        } else if (DEConfig.enableOreOverworld) {
            builder.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, ORE_DRACONIUM_OVERWORLD);
        }
    }
}
