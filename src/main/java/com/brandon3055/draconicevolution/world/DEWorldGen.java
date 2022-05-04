package com.brandon3055.draconicevolution.world;

import com.brandon3055.draconicevolution.DEConfig;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.init.DEContent;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.feature.template.BlockMatchRuleTest;
import net.minecraft.world.gen.feature.template.RuleTest;
import net.minecraft.world.gen.placement.Placement;
import net.minecraft.world.gen.placement.TopSolidRangeConfig;
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


    @SubscribeEvent
    public static void biomeLoading(BiomeLoadingEvent event) {
        if (event.getCategory() == Biome.Category.THEEND) {
            if (DEConfig.enableOreEnd) {
                event.getGeneration().addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Feature.ORE.configured(new OreFeatureConfig(BASE_STONE_END, DEContent.ore_draconium_end.defaultBlockState(), DEConfig.veinSizeEnd))
                        .decorated(Placement.RANGE.configured(new TopSolidRangeConfig(0, 0, 80)))
                        .squared()
                        .count(DEConfig.veinsPerChunkEnd));
            }

            event.getGeneration().addFeature(GenerationStage.Decoration.RAW_GENERATION, new ConfiguredFeature<>(new Feature<NoFeatureConfig>(NoFeatureConfig.CODEC) {
                @Override
                public boolean place(ISeedReader reader, ChunkGenerator generator, Random rand, BlockPos pos, NoFeatureConfig config) {
                    return ChaosWorldGenHandler.generateChunk(this, reader, generator, rand, pos);
                }
            }, NoFeatureConfig.NONE));

        } else if (event.getCategory() == Biome.Category.NETHER && DEConfig.enableOreNether) {
            event.getGeneration().addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Feature.ORE.configured(new OreFeatureConfig(OreFeatureConfig.FillerBlockType.NETHER_ORE_REPLACEABLES, DEContent.ore_draconium_nether.defaultBlockState(), DEConfig.veinSizeNether))
                    .chance(10)
                    .decorated(Placement.RANGE.configured(new TopSolidRangeConfig(4, 4, DEConfig.maxOreHeightNether))));
        } else if (DEConfig.enableOreOverworld) {
            event.getGeneration().addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Feature.ORE.configured(new OreFeatureConfig(OreFeatureConfig.FillerBlockType.NATURAL_STONE, DEContent.ore_draconium_overworld.defaultBlockState(), DEConfig.veinSizeOverworld))
                    .chance(10)
                    .decorated(Placement.RANGE.configured(new TopSolidRangeConfig(4, 4, DEConfig.maxOreHeightOverworld))));
        }
    }
}
