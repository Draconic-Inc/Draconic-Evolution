package com.brandon3055.draconicevolution.world;

import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.init.DEContent;
import com.brandon3055.draconicevolution.utils.LogHelper;
import net.minecraft.block.Blocks;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.feature.template.BlockMatchRuleTest;
import net.minecraft.world.gen.feature.template.RuleTest;
import net.minecraft.world.gen.feature.template.TagMatchRuleTest;
import net.minecraft.world.gen.placement.Placement;
import net.minecraft.world.gen.placement.TopSolidRangeConfig;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static net.minecraft.world.gen.GenerationStage.Decoration.UNDERGROUND_ORES;
import static net.minecraft.world.gen.feature.OreFeatureConfig.FillerBlockType.BASE_STONE_NETHER;
import static net.minecraft.world.gen.feature.OreFeatureConfig.FillerBlockType.BASE_STONE_OVERWORLD;
import static net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus.FORGE;

/**
 * Created by brandon3055 on 7/12/20
 */
@Mod.EventBusSubscriber(modid = DraconicEvolution.MODID, bus = FORGE)
public class DEWorldGen {


    //    public static final ConfiguredFeature<?, ?> ORE_IRON = register("ore_iron", Feature.ORE.withConfiguration(new OreFeatureConfig(OreFeatureConfig.FillerBlockType.BASE_STONE_OVERWORLD, Features.States.IRON_ORE, 9)).range(64).square().func_242731_b(20));
    public static final RuleTest BASE_STONE_END = new BlockMatchRuleTest(Blocks.END_STONE);


    @SubscribeEvent
    public static void biomeLoading(BiomeLoadingEvent event) {

        /*if (event.getName().toString().equals("minecraft:small_end_islands")) {
            event.getGeneration().withFeature(UNDERGROUND_ORES, Feature.ORE.withConfiguration(new OreFeatureConfig(BASE_STONE_END, DEContent.ore_draconium_end.getDefaultState(), 32))
                    .withPlacement(Placement.RANGE.configure(new TopSolidRangeConfig(0, 0, 80)))
                    .square()
                    .func_242731_b(6));
        } else */if (event.getCategory() == Biome.Category.THEEND) {
            event.getGeneration().withFeature(UNDERGROUND_ORES, Feature.ORE.withConfiguration(new OreFeatureConfig(BASE_STONE_END, DEContent.ore_draconium_end.getDefaultState(), 8))
                    .withPlacement(Placement.RANGE.configure(new TopSolidRangeConfig(0, 0, 80)))
                    .square()
                    .func_242731_b(2));
        } else if (event.getCategory() == Biome.Category.NETHER) {
            event.getGeneration().withFeature(UNDERGROUND_ORES, Feature.ORE.withConfiguration(new OreFeatureConfig(BASE_STONE_NETHER, DEContent.ore_draconium_nether.getDefaultState(), 16))
                    .chance(10)
                    .withPlacement(Placement.RANGE.configure(new TopSolidRangeConfig(4, 4, 16))));
        } else {
            event.getGeneration().withFeature(UNDERGROUND_ORES, Feature.ORE.withConfiguration(new OreFeatureConfig(BASE_STONE_OVERWORLD, DEContent.ore_draconium_overworld.getDefaultState(), 8))
                    .chance(10)
                    .withPlacement(Placement.RANGE.configure(new TopSolidRangeConfig(4, 4, 16))));
        }
    }
}
