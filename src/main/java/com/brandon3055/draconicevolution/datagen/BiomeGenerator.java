//package com.brandon3055.draconicevolution.datagen;
//
//import com.brandon3055.draconicevolution.DraconicEvolution;
//import net.minecraft.core.HolderLookup;
//import net.minecraft.core.HolderSet;
//import net.minecraft.core.RegistrySetBuilder;
//import net.minecraft.core.registries.Registries;
//import net.minecraft.data.PackOutput;
//import net.minecraft.data.worldgen.features.NetherFeatures;
//import net.minecraft.data.worldgen.placement.VegetationPlacements;
//import net.minecraft.resources.ResourceKey;
//import net.minecraft.resources.ResourceLocation;
//import net.minecraft.tags.BiomeTags;
//import net.minecraft.tags.EntityTypeTags;
//import net.minecraft.world.entity.EntityType;
//import net.minecraft.world.level.biome.Biome;
//import net.minecraft.world.level.biome.MobSpawnSettings;
//import net.minecraft.world.level.levelgen.GenerationStep;
//import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
//import net.minecraft.world.level.levelgen.feature.Feature;
//import net.minecraft.world.level.levelgen.feature.OreFeature;
//import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
//import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
//import net.minecraft.world.level.levelgen.placement.BiomeFilter;
//import net.minecraft.world.level.levelgen.placement.CountOnEveryLayerPlacement;
//import net.minecraft.world.level.levelgen.placement.PlacedFeature;
//import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
//import net.minecraftforge.common.world.ForgeBiomeModifiers;
//import net.minecraftforge.registries.ForgeRegistries;
//
//import java.util.List;
//import java.util.Set;
//import java.util.concurrent.CompletableFuture;
//
///**
// * Created by brandon3055 on 18/05/2024
// */
//public class BiomeGenerator extends DatapackBuiltinEntriesProvider {
//
//    private static final ResourceKey<ConfiguredFeature<?, ?>> DRACONIUM_ORE_FEATURE = ResourceKey.create(Registries.CONFIGURED_FEATURE, new ResourceLocation(DraconicEvolution.MODID, "draconium_ore"));
//    private static final ResourceKey<PlacedFeature> DRACONIUM_ORE = ResourceKey.create(Registries.PLACED_FEATURE, new ResourceLocation(DraconicEvolution.MODID, "draconium_ore"));
//
//    private static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
//            .add(Registries.CONFIGURED_FEATURE, context -> context.register(DRACONIUM_ORE_FEATURE,
//                    new ConfiguredFeature<OreConfiguration, Feature<OreConfiguration>>(
//                        new OreFeature()
//
//                    )
//            ))
//            .add(Registries.PLACED_FEATURE, context -> context.register(LARGE_BASALT_COLUMNS,
//                    new PlacedFeature(
//                            context.lookup(Registries.CONFIGURED_FEATURE).getOrThrow(NetherFeatures.LARGE_BASALT_COLUMNS),
//                            List.of(CountOnEveryLayerPlacement.of(1), BiomeFilter.biome())
//                    )
//            ));
//
//    public BiomeGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
//        super(output, registries, BUILDER, Set.of(DraconicEvolution.MODID));
//    }
//}
