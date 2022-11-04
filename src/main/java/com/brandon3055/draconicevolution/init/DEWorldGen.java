package com.brandon3055.draconicevolution.init;

import codechicken.lib.config.ConfigCategory;
import codechicken.lib.math.MathHelper;
import com.brandon3055.draconicevolution.DEConfig;
import com.brandon3055.draconicevolution.world.ChaosWorldGenHandler;
import com.mojang.serialization.Codec;
import net.covers1624.quack.util.CrashLock;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.data.worldgen.features.OreFeatures;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.placement.*;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.*;
import java.util.function.Supplier;

import static com.brandon3055.draconicevolution.DraconicEvolution.MODID;

/**
 * Created by brandon3055 on 04/11/2022
 *
 * /fill ~ 0 ~ ~40 20 ~37 minecraft:air replace #minecraft:base_stone_nether
 * /fill ~ -60 ~ ~33 -32 ~32 minecraft:air replace #minecraft:base_stone_overworld
 */
public class DEWorldGen {
    private static final CrashLock LOCK = new CrashLock("Already Initialized.");

    public static final RuleTest END_STONE = new BlockMatchTest(Blocks.END_STONE);

    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(Registry.FEATURE_REGISTRY, MODID);
    public static final DeferredRegister<ConfiguredFeature<?, ?>> CONFIGURED_FEATURES = DeferredRegister.create(Registry.CONFIGURED_FEATURE_REGISTRY, MODID);
    public static final DeferredRegister<PlacedFeature> PLACED_FEATURES = DeferredRegister.create(Registry.PLACED_FEATURE_REGISTRY, MODID);

    public static RegistryObject<PlacedFeature> DRACONIUM_ORE_PLACED_OVERWORLD;
    public static RegistryObject<PlacedFeature> DRACONIUM_ORE_PLACED_NETHER;
    public static RegistryObject<PlacedFeature> DRACONIUM_ORE_PLACED_END;
    public static RegistryObject<PlacedFeature> ISLAND_FEATURE;

    public static boolean chaosIslandVoidMode;

    public static void init(ConfigCategory configTag) {
        LOCK.lock();

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        FEATURES.register(modEventBus);
        CONFIGURED_FEATURES.register(modEventBus);
        PLACED_FEATURES.register(modEventBus);
        MinecraftForge.EVENT_BUS.addListener(DEWorldGen::onBiomeLoad);

        final Supplier<List<OreConfiguration.TargetBlockState>> ores = () -> List.of(
                OreConfiguration.target(OreFeatures.STONE_ORE_REPLACEABLES, DEContent.ore_draconium_overworld.defaultBlockState()),
                OreConfiguration.target(OreFeatures.DEEPSLATE_ORE_REPLACEABLES, DEContent.ore_draconium_deepslate.defaultBlockState()),
                OreConfiguration.target(OreFeatures.NETHER_ORE_REPLACEABLES, DEContent.ore_draconium_nether.defaultBlockState()),
                OreConfiguration.target(END_STONE, DEContent.ore_draconium_end.defaultBlockState()));

        ConfigCategory worldGenTag = configTag.getCategory("World Gen");

        DRACONIUM_ORE_PLACED_OVERWORLD = createOreFeature("draconium_overworld", ores, worldGenTag, 8, 0.1, -64, -32);
        DRACONIUM_ORE_PLACED_NETHER = createOreFeature("draconium_nether", ores, worldGenTag, 18, 0.05, 0, 16);
        DRACONIUM_ORE_PLACED_END = createOreFeature("draconium_end", ores, worldGenTag, 8, 3, 0, 70);
        ISLAND_FEATURE = createChaosIslandFeature(worldGenTag);
    }

    private static RegistryObject<PlacedFeature> createOreFeature(String baseName, Supplier<List<OreConfiguration.TargetBlockState>> ores, ConfigCategory config, int size, double spawns, int minY, int maxY) {
        ConfigCategory category = config.getCategory(baseName);

        boolean enabled = category.getValue("enableGeneration")
                .setComment("Allows you to disable generation of this ore")
                .setDefaultBoolean(true)
                .getBoolean();
        int cfgSize = category.getValue("maxVeinSize")
                .setComment("Allows you to specify the maximum vein size for this ore")
                .setDefaultInt(size)
                .getInt();
        double cfgSpawns = category.getValue("spawnsPerChunk")
                .setComment("The number of times this ore will attempt to spawn per chunk\nCan be a decimal number if less then one but will be rounded down to the nearest integer value if greater than or equal to one.")
                .setDefaultDouble(spawns)
                .getDouble();
        int cfgMinY = category.getValue("minYHeight")
                .setComment("The minimum Y level this ore will spawn at")
                .setDefaultInt(minY)
                .getInt();
        int cfgMaxY = category.getValue("maxYHeight")
                .setComment("The maximum Y level this ore will spawn at")
                .setDefaultInt(maxY)
                .getInt();

        if (!enabled) return null;

        RegistryObject<ConfiguredFeature<?, ?>> configuredOre = CONFIGURED_FEATURES.register(baseName, () -> new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(ores.get(), cfgSize)));

        PlacementModifier spawnChance = cfgSpawns >= 1 ? CountPlacement.of((int) cfgSpawns) : RarityFilter.onAverageOnceEvery((int) (1 / MathHelper.clip(cfgSpawns, 1 / 128D, 1)));

        return PLACED_FEATURES.register(baseName, () -> new PlacedFeature(configuredOre.getHolder().get(),
                List.of(spawnChance,
                        InSquarePlacement.spread(),
                        BiomeFilter.biome(),
                        HeightRangePlacement.uniform(VerticalAnchor.absolute(cfgMinY), VerticalAnchor.absolute(cfgMaxY))
                )
        ));
    }

    private static RegistryObject<PlacedFeature> createChaosIslandFeature(ConfigCategory config) {
        ConfigCategory category = config.getCategory("Chaos Island");
        boolean enabled = category.getValue("enableGeneration")
                .setComment("Allows you to disable generation of chaos islands")
                .setDefaultBoolean(true)
                .getBoolean();

        chaosIslandVoidMode = category.getValue("chaosIslandVoidMode")
                .setComment("Allows you to disable generation of chaos islands")
                .setDefaultBoolean(false)
                .getBoolean();
        int yOffset = category.getValue("chaosIslandYOffset")
                .setComment("Changes the Y coord that the chaos island generates at.  The crystal generates at Y = 80 + chaosIslandYOffset")
                .setDefaultInt(0)
                .getInt();
        int seperation = category.getValue("chaosIslandSeparation")
                .setComment("Toggles whether the full chaos island should spawn or just the guardian crystals, the chaos crystal, and the guardian.")
                .setDefaultInt(10000)
                .getInt();

        if (!enabled) return null;

        RegistryObject<ChaosIslandFeature> islandFeature = FEATURES.register("chaos_island", () -> new ChaosIslandFeature(NoneFeatureConfiguration.CODEC, yOffset, seperation));
        RegistryObject<ConfiguredFeature<?, ?>> configuredIsland = CONFIGURED_FEATURES.register("chaos_island", () -> new ConfiguredFeature<>(islandFeature.get(), NoneFeatureConfiguration.INSTANCE));
        return PLACED_FEATURES.register("chaos_island", () -> new PlacedFeature(configuredIsland.getHolder().get(), Collections.emptyList()));
    }

    public static void onBiomeLoad(BiomeLoadingEvent event) {
        Biome.BiomeCategory category = event.getCategory();
        List<Holder<PlacedFeature>> oreFeatures = event.getGeneration().getFeatures(GenerationStep.Decoration.UNDERGROUND_ORES);

        if (isLikelyOverworldBiome(category)) {
            if (DRACONIUM_ORE_PLACED_OVERWORLD != null) {
                DRACONIUM_ORE_PLACED_OVERWORLD.getHolder().ifPresent(oreFeatures::add);
            }
        } else if (category == Biome.BiomeCategory.NETHER) {
            if (DRACONIUM_ORE_PLACED_NETHER != null) {
                DRACONIUM_ORE_PLACED_NETHER.getHolder().ifPresent(oreFeatures::add);
            }
        } else if (category == Biome.BiomeCategory.THEEND) {
            if (DRACONIUM_ORE_PLACED_END != null) {
                DRACONIUM_ORE_PLACED_END.getHolder().ifPresent(oreFeatures::add);
            }

            if (ISLAND_FEATURE != null) {
                ISLAND_FEATURE.getHolder().ifPresent(holder -> event.getGeneration().getFeatures(GenerationStep.Decoration.RAW_GENERATION).add(holder));
            }
        }
    }

    public static boolean isLikelyOverworldBiome(Biome.BiomeCategory category) {
        return category != Biome.BiomeCategory.NONE && category != Biome.BiomeCategory.THEEND && category != Biome.BiomeCategory.NETHER;
    }

    public static class ChaosIslandFeature extends Feature<NoneFeatureConfiguration> {

        public final int chaosIslandYOffset;
        public final int chaosIslandSeparation;

        public ChaosIslandFeature(Codec<NoneFeatureConfiguration> codec, int chaosIslandYOffset, int chaosIslandSeparation) {
            super(codec);
            this.chaosIslandYOffset = chaosIslandYOffset;
            this.chaosIslandSeparation = chaosIslandSeparation;
        }

        public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
            return ChaosWorldGenHandler.generateChunk(this, context.level(), context.chunkGenerator(), context.random(), context.origin());
        }
    }
}
