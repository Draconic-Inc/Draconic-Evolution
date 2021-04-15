package com.brandon3055.draconicevolution.world;

import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.init.DEContent;
import com.brandon3055.draconicevolution.utils.LogHelper;
import net.minecraft.block.Blocks;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeGenerationSettings;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.feature.template.BlockMatchRuleTest;
import net.minecraft.world.gen.feature.template.RuleTest;
import net.minecraft.world.gen.feature.template.TagMatchRuleTest;
import net.minecraft.world.gen.placement.Placement;
import net.minecraft.world.gen.placement.TopSolidRangeConfig;
import net.minecraft.world.gen.surfacebuilders.ConfiguredSurfaceBuilders;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.Logger;

import java.util.Random;

import static net.minecraft.world.gen.GenerationStage.Decoration.UNDERGROUND_ORES;
import static net.minecraft.world.gen.feature.OreFeatureConfig.FillerBlockType.NETHER_ORE_REPLACEABLES;
import static net.minecraft.world.gen.feature.OreFeatureConfig.FillerBlockType.NATURAL_STONE;
import static net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus.FORGE;

/**
 * Created by brandon3055 on 7/12/20
 */
@Mod.EventBusSubscriber(modid = DraconicEvolution.MODID, bus = FORGE)
public class DEWorldGen {
    private static Logger LOGGER = DraconicEvolution.LOGGER;

    //    public static final ConfiguredFeature<?, ?> ORE_IRON = register("ore_iron", Feature.ORE.withConfiguration(new OreFeatureConfig(OreFeatureConfig.FillerBlockType.BASE_STONE_OVERWORLD, Features.States.IRON_ORE, 9)).range(64).square().count(20));
    public static final RuleTest BASE_STONE_END = new BlockMatchRuleTest(Blocks.END_STONE);


    @SubscribeEvent
    public static void biomeLoading(BiomeLoadingEvent event) {

        /*if (event.getName().toString().equals("minecraft:small_end_islands")) {
            event.getGeneration().withFeature(UNDERGROUND_ORES, Feature.ORE.withConfiguration(new OreFeatureConfig(BASE_STONE_END, DEContent.ore_draconium_end.getDefaultState(), 32))
                    .withPlacement(Placement.RANGE.configure(new TopSolidRangeConfig(0, 0, 80)))
                    .square()
                    .count(6));
        } else */if (event.getCategory() == Biome.Category.THEEND) {
            event.getGeneration().addFeature(UNDERGROUND_ORES, Feature.ORE.configured(new OreFeatureConfig(BASE_STONE_END, DEContent.ore_draconium_end.defaultBlockState(), 8))
                    .decorated(Placement.RANGE.configured(new TopSolidRangeConfig(0, 0, 80)))
                    .squared()
                    .count(2));

            event.getGeneration().addFeature(GenerationStage.Decoration.RAW_GENERATION, new ConfiguredFeature<>(new Feature<NoFeatureConfig>(NoFeatureConfig.CODEC) {
                @Override
                public boolean place(ISeedReader reader, ChunkGenerator generator, Random rand, BlockPos pos, NoFeatureConfig config) {
                    return ChaosWorldGenHandler.generateChunk(this, reader, generator, rand, pos);
                }
            }, NoFeatureConfig.NONE));


        } else if (event.getCategory() == Biome.Category.NETHER) {
            event.getGeneration().addFeature(UNDERGROUND_ORES, Feature.ORE.configured(new OreFeatureConfig(NETHER_ORE_REPLACEABLES, DEContent.ore_draconium_nether.defaultBlockState(), 16))
                    .chance(10)
                    .decorated(Placement.RANGE.configured(new TopSolidRangeConfig(4, 4, 16))));
        } else {
            event.getGeneration().addFeature(UNDERGROUND_ORES, Feature.ORE.configured(new OreFeatureConfig(NATURAL_STONE, DEContent.ore_draconium_overworld.defaultBlockState(), 8))
                    .chance(10)
                    .decorated(Placement.RANGE.configured(new TopSolidRangeConfig(4, 4, 16))));
        }
    }
}
