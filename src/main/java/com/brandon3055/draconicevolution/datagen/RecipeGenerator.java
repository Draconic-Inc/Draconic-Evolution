package com.brandon3055.draconicevolution.datagen;

import com.brandon3055.draconicevolution.init.DEContent;
import net.minecraft.data.*;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;

import java.io.IOException;
import java.util.function.Consumer;

import static com.brandon3055.brandonscore.api.TechLevel.*;
import static com.brandon3055.draconicevolution.init.DEContent.*;
import static net.minecraft.item.Items.*;
import static net.minecraftforge.common.Tags.Items.*;

/**
 * Created by brandon3055 on 1/12/20
 */
public class RecipeGenerator extends RecipeProvider {

    public RecipeGenerator(DataGenerator generatorIn) {
        super(generatorIn);
    }

    @Override
    protected void registerRecipes(Consumer<IFinishedRecipe> consumer) {
        components(consumer);

        compressDecompress(consumer);
        machines(consumer);
        energy(consumer);
        tools(consumer);

        unsorted(consumer);

        FusionRecipeBuilder.fusionRecipe(block_draconium_awakened, 4)
                .catalyst(4, block_draconium)
                .energy(50000000)
                .techLevel(WYVERN)
                .ingredient(core_draconium)
                .ingredient(core_draconium)
                .ingredient(core_draconium)
                .ingredient(dragon_heart)
                .ingredient(core_draconium)
                .ingredient(core_draconium)
                .ingredient(core_draconium)
                .build(consumer);
    }

    private static void components(Consumer<IFinishedRecipe> consumer) {
        ShapedRecipeBuilder.shapedRecipe(core_draconium)
                .patternLine("ABA")
                .patternLine("BCB")
                .patternLine("ABA")
                .key('A', ingot_draconium)
                .key('B', INGOTS_GOLD)
                .key('C', GEMS_DIAMOND)
                .addCriterion("has_draconium", hasItem(ingot_draconium))
                .build(consumer);
        ShapedRecipeBuilder.shapedRecipe(core_wyvern)
                .patternLine("ABA")
                .patternLine("BCB")
                .patternLine("ABA")
                .key('A', ingot_draconium)
                .key('B', core_draconium)
                .key('C', NETHER_STARS)
                .addCriterion("has_core_draconium", hasItem(core_draconium))
                .build(consumer);
        FusionRecipeBuilder.fusionRecipe(core_awakened)
                .catalyst(NETHER_STARS)
                .energy(1000000)
                .techLevel(WYVERN)
                .ingredient(core_wyvern)
                .ingredient(core_wyvern)
                .ingredient(ingot_draconium_awakened)
                .ingredient(ingot_draconium_awakened)
                .ingredient(ingot_draconium_awakened)
                .ingredient(ingot_draconium_awakened)
                .ingredient(ingot_draconium_awakened)
                .ingredient(core_wyvern, core_wyvern)
                .build(consumer);
        FusionRecipeBuilder.fusionRecipe(core_chaotic)
                .catalyst(chaos_shard)
                .energy(100000000)
                .techLevel(DRACONIC)
                .ingredient(ingot_draconium_awakened)
                .ingredient(ingot_draconium_awakened)
                .ingredient(core_awakened)
                .ingredient(core_awakened)
                .ingredient(ingot_draconium_awakened)
                .ingredient(core_awakened)
                .ingredient(core_awakened)
                .ingredient(ingot_draconium_awakened)
                .build
                        (consumer);
        ShapedRecipeBuilder.shapedRecipe(energy_core_wyvern)
                .patternLine("ABA")
                .patternLine("BCB")
                .patternLine("ABA")
                .key('A', ingot_draconium)
                .key('B', Tags.Items.STORAGE_BLOCKS_REDSTONE)
                .key('C', core_draconium)
                .addCriterion("has_core_draconium", hasItem(core_draconium))
                .build(consumer);
        ShapedRecipeBuilder.shapedRecipe(energy_core_draconic)
                .patternLine("ABA")
                .patternLine("BCB")
                .patternLine("ABA")
                .key('A', ingot_draconium_awakened)
                .key('B', energy_core_wyvern)
                .key('C', core_wyvern)
                .addCriterion("has_core_wyvern", hasItem(core_wyvern))
                .build(consumer);
    }

    public static void compressDecompress(Consumer<IFinishedRecipe> consumer) {
        compress3x3(ingot_draconium, nugget_draconium, consumer);
        compress3x3(ingot_draconium_awakened, nugget_draconium_awakened, consumer);
        compress3x3(block_draconium, ingot_draconium, consumer);
        compress3x3(block_draconium_awakened, ingot_draconium_awakened, consumer);

        deCompress(nugget_draconium, ingot_draconium, consumer);
        deCompress(nugget_draconium_awakened, ingot_draconium_awakened, consumer);
        deCompress(ingot_draconium, block_draconium, consumer);
        deCompress(ingot_draconium_awakened, block_draconium_awakened, consumer);

        deCompress(chaos_frag_large, chaos_shard, consumer);
        deCompress(chaos_frag_medium, chaos_frag_large, consumer);
        deCompress(chaos_frag_small, chaos_frag_medium, consumer);
        compress3x3(chaos_shard, chaos_frag_large, consumer);
        compress3x3(chaos_frag_large, chaos_frag_medium, consumer);
        compress3x3(chaos_frag_medium, chaos_frag_small, consumer);
    }

    public static void machines(Consumer<IFinishedRecipe> consumer) {
        ShapedRecipeBuilder.shapedRecipe(crafting_core)
                .patternLine("ABA")
                .patternLine("BCB")
                .patternLine("ABA")
                .key('A', STORAGE_BLOCKS_LAPIS)
                .key('B', GEMS_DIAMOND)
                .key('C', core_draconium)
                .addCriterion("has_core_draconium", hasItem(core_draconium))
                .build(consumer);
        ShapedRecipeBuilder.shapedRecipe(crafting_injector_basic)
                .patternLine("ABA")
                .patternLine("CDC")
                .patternLine("CCC")
                .key('A', GEMS_DIAMOND)
                .key('B', core_draconium)
                .key('C', Tags.Items.STONE)
                .key('D', STORAGE_BLOCKS_IRON)
                .addCriterion("has_core_draconium", hasItem(core_draconium))
                .build(consumer);
        FusionRecipeBuilder.fusionRecipe(crafting_injector_wyvern)
                .catalyst(crafting_injector_basic)
                .energy(32000)
                .techLevel(DRACONIUM)
                .ingredient(core_wyvern)
                .ingredient(GEMS_DIAMOND)
                .ingredient(core_draconium)
                .ingredient(GEMS_DIAMOND)
                .ingredient(core_draconium)
                .ingredient(GEMS_DIAMOND)
                .ingredient(block_draconium)
                .ingredient(GEMS_DIAMOND)
                .build(consumer);
        FusionRecipeBuilder.fusionRecipe(crafting_injector_awakened)
                .catalyst(crafting_injector_wyvern)
                .energy(256000)
                .techLevel(WYVERN)
                .ingredient(GEMS_DIAMOND)
                .ingredient(GEMS_DIAMOND)
                .ingredient(core_wyvern)
                .ingredient(block_draconium_awakened)
                .ingredient(core_wyvern)
                .ingredient(GEMS_DIAMOND)
                .ingredient(GEMS_DIAMOND)
                .build(consumer);
        FusionRecipeBuilder.fusionRecipe(crafting_injector_chaotic)
                .catalyst(crafting_injector_awakened)
                .energy(8000000)
                .techLevel(DRACONIC)
                .ingredient(GEMS_DIAMOND)
                .ingredient(GEMS_DIAMOND)
                .ingredient(core_chaotic)
                .ingredient(DRAGON_EGG)
                .ingredient(GEMS_DIAMOND)
                .ingredient(GEMS_DIAMOND)
                .build(consumer);

        ShapedRecipeBuilder.shapedRecipe(DEContent.generator)
                .patternLine("ABA")
                .patternLine("BCB")
                .patternLine("ADA")
                .key('A', INGOTS_NETHER_BRICK)
                .key('B', INGOTS_IRON)
                .key('C', FURNACE)
                .key('D', core_draconium)
                .addCriterion("has_core_draconium", hasItem(core_draconium))
                .build(consumer);
        ShapedRecipeBuilder.shapedRecipe(grinder)
                .patternLine("ABA")
                .patternLine("CDC")
                .patternLine("AEA")
                .key('A', INGOTS_IRON)
                .key('B', ingot_draconium)
                .key('C', DIAMOND_SWORD)
                .key('D', core_draconium)
                .key('E', FURNACE)
                .addCriterion("has_core_draconium", hasItem(core_draconium))
                .build(consumer);
//        ShapedRecipeBuilder.shapedRecipe(energy_infuser)
//                .patternLine("ABA")
//                .patternLine("CDC")
//                .patternLine("ACA")
//                .key('A', ingot_draconium)
//                .key('B', energy_core_stabilizer)
//                .key('C', core_draconium)
//                .key('D', ENCHANTING_TABLE)
//                .addCriterion("has_core_draconium", hasItem(core_draconium))
//                .build(consumer);
        ShapedRecipeBuilder.shapedRecipe(particle_generator)
                .patternLine("ABA")
                .patternLine("BCB")
                .patternLine("ABA")
                .key('A', STORAGE_BLOCKS_REDSTONE)
                .key('B', BLAZE_ROD)
                .key('C', core_draconium)
                .addCriterion("has_core_draconium", hasItem(core_draconium))
                .build(consumer);
        FusionRecipeBuilder.fusionRecipe(draconium_chest)
                .catalyst(CHEST)
                .energy(2000000)
                .techLevel(DRACONIUM)
                .ingredient(FURNACE)
                .ingredient(core_draconium)
                .ingredient(FURNACE)
                .ingredient(core_draconium)
                .ingredient(FURNACE)
                .ingredient(CRAFTING_TABLE)
                .ingredient(FURNACE)
                .ingredient(block_draconium)
                .ingredient(FURNACE)
                .ingredient(CRAFTING_TABLE)
                .build(consumer);
    }

    public static void energy(Consumer<IFinishedRecipe> consumer) {
        ShapedRecipeBuilder.shapedRecipe(energy_core)
                .patternLine("AAA")
                .patternLine("BCB")
                .patternLine("AAA")
                .key('A', ingot_draconium)
                .key('B', energy_core_wyvern)
                .key('C', core_wyvern)
                .addCriterion("has_core_wyvern", hasItem(core_wyvern))
                .build(consumer);
        ShapedRecipeBuilder.shapedRecipe(energy_core_stabilizer)
                .patternLine("A A")
                .patternLine(" B ")
                .patternLine("A A")
                .key('A', GEMS_DIAMOND)
                .key('B', particle_generator)
                .addCriterion("has_core_wyvern", hasItem(core_wyvern))
                .build(consumer);
        ShapedRecipeBuilder.shapedRecipe(energy_pylon, 2)
                .patternLine("ABA")
                .patternLine("CDC")
                .patternLine("AEA")
                .key('A', ingot_draconium)
                .key('B', ENDER_EYE)
                .key('C', GEMS_EMERALD)
                .key('D', core_draconium)
                .key('E', GEMS_DIAMOND)
                .addCriterion("has_core_wyvern", hasItem(core_wyvern))
                .build(consumer);

        FusionRecipeBuilder.fusionRecipe(reactor_core)
                .catalyst(chaos_shard)
                .energy(64000000)
                .techLevel(CHAOTIC)
                .ingredient(ingot_draconium_awakened)
                .ingredient(ingot_draconium)
                .ingredient(ingot_draconium_awakened)
                .ingredient(ingot_draconium)
                .ingredient(ingot_draconium_awakened)
                .ingredient(ingot_draconium)
                .ingredient(ingot_draconium_awakened)
                .build(consumer);

        ShapedRecipeBuilder.shapedRecipe(crystal_binder)
                .patternLine(" AB")
                .patternLine(" CA")
                .patternLine("D  ")
                .key('A', ingot_draconium)
                .key('B', GEMS_DIAMOND)
                .key('C', BLAZE_ROD)
                .key('D', core_draconium)
                .addCriterion("has_core_draconium", hasItem(core_draconium))
                .build(consumer);

        ShapedRecipeBuilder.shapedRecipe(crystal_relay_basic, 4)
                .patternLine(" A ")
                .patternLine("ABA")
                .patternLine(" A ")
                .key('A', GEMS_DIAMOND)
                .key('B', energy_core_wyvern)
                .addCriterion("has_core_draconium", hasItem(core_draconium))
                .build(consumer);
        ShapedRecipeBuilder.shapedRecipe(crystal_relay_wyvern, 4)
                .patternLine("ABA")
                .patternLine("BCB")
                .patternLine("ABA")
                .key('A', energy_core_wyvern)
                .key('B', crystal_relay_basic)
                .key('C', core_draconium)
                .addCriterion("has_core_draconium", hasItem(core_draconium))
                .build(consumer);
        FusionRecipeBuilder.fusionRecipe(crystal_relay_draconic, 4)
                .catalyst(4, crystal_relay_wyvern)
                .energy(128000)
                .techLevel(DRACONIC)
                .ingredient(energy_core_wyvern)
                .ingredient(GEMS_DIAMOND)
                .ingredient(core_wyvern)
                .ingredient(GEMS_DIAMOND)
                .ingredient(energy_core_wyvern)
                .ingredient(energy_core_wyvern)
                .ingredient(GEMS_DIAMOND)
                .ingredient(GEMS_DIAMOND)
                .ingredient(energy_core_wyvern)
                .build(consumer);

        ShapedRecipeBuilder.shapedRecipe(crystal_wireless_basic)
                .patternLine("ABA")
                .patternLine("CDC")
                .patternLine("ABA")
                .key('A', ENDER_PEARL)
                .key('B', particle_generator)
                .key('C', ENDER_EYE)
                .key('D', crystal_relay_basic)
                .addCriterion("has_core_draconium", hasItem(core_draconium))
                .build(consumer);
        ShapedRecipeBuilder.shapedRecipe(crystal_wireless_wyvern)
                .patternLine("ABA")
                .patternLine("CDC")
                .patternLine("ABA")
                .key('A', ENDER_PEARL)
                .key('B', particle_generator)
                .key('C', ENDER_EYE)
                .key('D', crystal_relay_wyvern)
                .addCriterion("has_core_draconium", hasItem(core_draconium))
                .build(consumer);
        ShapedRecipeBuilder.shapedRecipe(crystal_wireless_draconic)
                .patternLine("ABA")
                .patternLine("CDC")
                .patternLine("ABA")
                .key('A', ENDER_PEARL)
                .key('B', particle_generator)
                .key('C', ENDER_EYE)
                .key('D', crystal_relay_draconic)
                .addCriterion("has_core_draconium", hasItem(core_draconium))
                .build(consumer);

        //to-from io
        ShapelessRecipeBuilder.shapelessRecipe(crystal_io_basic, 2)
                .addIngredient(crystal_relay_basic)
                .addCriterion("has_core_draconium", hasItem(core_draconium))
                .build(consumer);
        ShapelessRecipeBuilder.shapelessRecipe(crystal_io_wyvern, 2)
                .addIngredient(crystal_relay_wyvern)
                .addCriterion("has_core_draconium", hasItem(core_draconium))
                .build(consumer);
        ShapelessRecipeBuilder.shapelessRecipe(crystal_io_draconic, 2)
                .addIngredient(crystal_relay_draconic)
                .addCriterion("has_core_draconium", hasItem(core_draconium))
                .build(consumer);
        ShapelessRecipeBuilder.shapelessRecipe(crystal_relay_basic)
                .addIngredient(crystal_io_basic)
                .addIngredient(crystal_io_basic)
                .addCriterion("has_core_draconium", hasItem(core_draconium))
                .build(consumer, "draconicevolution:crystal_io_basic_combine");
        ShapelessRecipeBuilder.shapelessRecipe(crystal_relay_wyvern)
                .addIngredient(crystal_io_wyvern)
                .addIngredient(crystal_io_wyvern)
                .addCriterion("has_core_draconium", hasItem(core_draconium))
                .build(consumer, "draconicevolution:crystal_io_wyvern_combine");
        ShapelessRecipeBuilder.shapelessRecipe(crystal_relay_draconic)
                .addIngredient(crystal_io_draconic)
                .addIngredient(crystal_io_draconic)
                .addCriterion("has_core_draconium", hasItem(core_draconium))
                .build(consumer, "draconicevolution:crystal_io_draconic_combine");
    }

    public static void tools(Consumer<IFinishedRecipe> consumer) {
        ShapedRecipeBuilder.shapedRecipe(pickaxe_wyvern)
                .patternLine(" A ")
                .patternLine("BCB")
                .patternLine(" D ")
                .key('A', core_wyvern)
                .key('B', ingot_draconium)
                .key('C', DIAMOND_PICKAXE)
                .key('D', energy_core_wyvern)
                .addCriterion("has_core_wyvern", hasItem(core_wyvern))
                .build(consumer);
        ShapedRecipeBuilder.shapedRecipe(shovel_wyvern)
                .patternLine(" A ")
                .patternLine("BCB")
                .patternLine(" D ")
                .key('A', core_wyvern)
                .key('B', ingot_draconium)
                .key('C', DIAMOND_SHOVEL)
                .key('D', energy_core_wyvern)
                .addCriterion("has_core_wyvern", hasItem(core_wyvern))
                .build(consumer);
        ShapedRecipeBuilder.shapedRecipe(axe_wyvern)
                .patternLine(" A ")
                .patternLine("BCB")
                .patternLine(" D ")
                .key('A', core_wyvern)
                .key('B', ingot_draconium)
                .key('C', DIAMOND_AXE)
                .key('D', energy_core_wyvern)
                .addCriterion("has_core_wyvern", hasItem(core_wyvern))
                .build(consumer);
        ShapedRecipeBuilder.shapedRecipe(bow_wyvern)
                .patternLine(" A ")
                .patternLine("BCB")
                .patternLine(" D ")
                .key('A', core_wyvern)
                .key('B', ingot_draconium)
                .key('C', BOW)
                .key('D', energy_core_wyvern)
                .addCriterion("has_core_wyvern", hasItem(core_wyvern))
                .build(consumer);
        ShapedRecipeBuilder.shapedRecipe(sword_wyvern)
                .patternLine(" A ")
                .patternLine("BCB")
                .patternLine(" D ")
                .key('A', core_wyvern)
                .key('B', ingot_draconium)
                .key('C', DIAMOND_SWORD)
                .key('D', energy_core_wyvern)
                .addCriterion("has_core_wyvern", hasItem(core_wyvern))
                .build(consumer);

        ShapedRecipeBuilder.shapedRecipe(capacitor_wyvern)
                .patternLine("ABA")
                .patternLine("BCB")
                .patternLine("ABA")
                .key('A', ingot_draconium)
                .key('B', energy_core_wyvern)
                .key('C', core_wyvern)
                .addCriterion("has_core_wyvern", hasItem(core_wyvern))
                .build(consumer);
        ShapedRecipeBuilder.shapedRecipe(capacitor_draconic)
                .patternLine("ABA")
                .patternLine("CDC")
                .patternLine("ACA")
                .key('A', energy_core_draconic)
                .key('B', core_awakened)
                .key('C', ingot_draconium_awakened)
                .key('D', capacitor_wyvern)
                .addCriterion("has_core_awakened", hasItem(core_awakened))
                .build(consumer);

        ShapedRecipeBuilder.shapedRecipe(dislocator)
                .patternLine("ABA")
                .patternLine("BCB")
                .patternLine("ABA")
                .key('A', BLAZE_POWDER)
                .key('B', dust_draconium)
                .key('C', ENDER_EYE)
                .addCriterion("has_dust_draconium", hasItem(dust_draconium))
                .build(consumer);
        FusionRecipeBuilder.fusionRecipe(dislocator_advanced)
                .catalyst(dislocator)
                .energy(1000000)
                .techLevel(WYVERN)
                .ingredient(ENDER_PEARLS)
                .ingredient(ingot_draconium)
                .ingredient(ENDER_PEARLS)
                .ingredient(ingot_draconium)
                .ingredient(ENDER_PEARLS)
                .ingredient(ingot_draconium)
                .ingredient(core_wyvern)
                .ingredient(ingot_draconium)
                .build(consumer);
        ShapelessRecipeBuilder.shapelessRecipe(dislocator_p2p)
                .addIngredient(dislocator)
                .addIngredient(core_draconium)
                .addIngredient(dislocator)
                .addIngredient(GHAST_TEAR)
                .addCriterion("has_core_draconium", hasItem(core_draconium))
                .build(consumer);
        ShapelessRecipeBuilder.shapelessRecipe(dislocator_player)
                .addIngredient(dislocator)
                .addIngredient(core_draconium)
                .addIngredient(GHAST_TEAR)
                .addCriterion("has_core_draconium", hasItem(core_draconium))
                .build(consumer);
    }


    public static void unsorted(Consumer<IFinishedRecipe> consumer) {


//        FusionRecipeBuilder.fusionRecipe(ender_energy_manipulator).catalyst(SKELETON_SKULL).energy(12000000).techLevel(WYVERN).ingredient(ENDER_EYE).ingredient(ENDER_EYE).ingredient(ENDER_EYE).ingredient(ENDER_EYE).ingredient(ENDER_EYE).ingredient(ENDER_EYE).ingredient(core_draconium).ingredient(core_wyvern).ingredient(core_draconium).ingredient(ENDER_EYE).build(consumer);


//        ShapedRecipeBuilder.shapedRecipe(infused_obsidian).patternLine("ABA").patternLine("BCB").patternLine("ABA").key('A', BLAZE_POWDER).key('B', Tags.Items.OBSIDIAN).key('C', dust_draconium).build(consumer);
//        ShapedRecipeBuilder.shapedRecipe(dislocator_receptacle).patternLine("ABA").patternLine(" C ").patternLine("A A").key('A', INGOTS_IRON).key('B', core_draconium).key('C', infused_obsidian).build(consumer);
//        ShapedRecipeBuilder.shapedRecipe(dislocator_pedestal).patternLine(" A ").patternLine(" B ").patternLine("CDC").key('A', STONE_PRESSURE_PLATE).key('B', Tags.Items.STONE).key('C', STONE_SLAB).key('D', BLAZE_POWDER).build(consumer);


//        ShapedRecipeBuilder.shapedRecipe(rain_sensor).patternLine(" A ").patternLine("BCB").patternLine("DDD").key('A', BUCKET).key('B', DUSTS_REDSTONE).key('C', STONE_PRESSURE_PLATE).key('D', STONE_SLAB).build(consumer);
//        ShapedRecipeBuilder.shapedRecipe(disenchanter).patternLine("ABA").patternLine("CDC").patternLine("EEE").key('A', GEMS_EMERALD).key('B', core_draconium).key('C', ENCHANTED_BOOK).key('D', ENCHANTING_TABLE).key('E', BOOKSHELF).build(consumer);
//        ShapedRecipeBuilder.shapedRecipe(celestial_manipulator).patternLine("ABA").patternLine("CDC").patternLine("EFE").key('A', STORAGE_BLOCKS_REDSTONE).key('B', CLOCK).key('C', ingot_draconium).key('D', DRAGON_EGG).key('E', INGOTS_IRON).key('F', core_wyvern).build(consumer);
//        ShapedRecipeBuilder.shapedRecipe(potentiometer).patternLine(" A ").patternLine("BCB").patternLine("DDD").key('A', ItemTags.PLANKS).key('B', DUSTS_REDSTONE).key('C', dust_draconium).key('D', STONE_SLAB).build(consumer);
//        ShapedRecipeBuilder.shapedRecipe(entity_detector).patternLine("ABA").patternLine("CDC").patternLine("EFE").key('A', GEMS_LAPIS).key('B', ENDER_EYE).key('C', DUSTS_REDSTONE).key('D', ingot_draconium).key('E', INGOTS_IRON).key('F', core_draconium).build(consumer);
//        ShapedRecipeBuilder.shapedRecipe(entity_detector_advanced).patternLine("ABA").patternLine("CDC").patternLine("EFE").key('A', STORAGE_BLOCKS_REDSTONE).key('B', SKELETON_SKULL).key('C', STORAGE_BLOCKS_LAPIS).key('D', GEMS_DIAMOND).key('E', ingot_draconium).key('F', entity_detector).build(consumer);
//        ShapedRecipeBuilder.shapedRecipe(flow_gate).patternLine("ABA").patternLine("CDC").patternLine("AEA").key('A', INGOTS_IRON).key('B', potentiometer).key('C', BUCKET).key('D', core_draconium).key('E', COMPARATOR).build(consumer);
//        ShapedRecipeBuilder.shapedRecipe(flux_gate).patternLine("ABA").patternLine("CDC").patternLine("AEA").key('A', INGOTS_IRON).key('B', potentiometer).key('C', STORAGE_BLOCKS_REDSTONE).key('D', core_draconium).key('E', COMPARATOR).build(consumer);
//        ShapedRecipeBuilder.shapedRecipe(dislocation_inhibitor).patternLine("AAA").patternLine("BCB").patternLine("AAA").key('A', INGOTS_IRON).key('B', IRON_BARS).key('C', magnet).build(consumer);


//        ShapedRecipeBuilder.shapedRecipe(info_tablet).patternLine("AAA").patternLine("ABA").patternLine("AAA").key('A', Tags.Items.STONE).key('B', dust_draconium).build(consumer);
//        ShapedRecipeBuilder.shapedRecipe(magnet).patternLine("A A").patternLine("B B").patternLine("CDC").key('A', DUSTS_REDSTONE).key('B', ingot_draconium).key('C', INGOTS_IRON).key('D', dislocator).build(consumer);
//        ShapedRecipeBuilder.shapedRecipe(magnet_advanced).patternLine("A A").patternLine("B B").patternLine("CDC").key('A', ingot_draconium).key('B', DUSTS_REDSTONE).key('C', ingot_draconium_awakened).key('D', magnet).build(consumer);


//                                        addFusion( new ItemStack(reactorComponent), new ItemStack(reactorPart), 16000000, 3, ingot_draconium_awakened, energy_core_draconic, new ItemStack(reactorPart, 1, 3), new ItemStack(reactorPart, 1, 4), ingot_draconium_awakened, ingot_draconium_awakened, core_chaotic, ingot_draconium_awakened);

//                                        addFusion( new ItemStack(reactorComponent, 1, 1), core_wyvern, 16000000, 3, ingot_draconium, new ItemStack(reactorPart, 1, 1), new ItemStack(reactorPart, 1, 1), new ItemStack(reactorPart, 1, 1), ingot_draconium, ingot_draconium, INGOTS_IRON, new ItemStack(reactorPart, 1, 1), INGOTS_IRON, ingot_draconium);


//        addFusionTool(NORMAL, new ItemStack(draconicPick), new ItemStack(wyvernPick), 16000, 2, core_awakened, ingot_draconium_awakened, energy_core_draconic, ingot_draconium_awakened);
//        addFusionTool(HARD, new ItemStack(draconicPick), new ItemStack(wyvernPick), 512000, 2, energy_core_draconic, core_awakened, block_draconium_awakened, block_draconium_awakened);
//        addFusionTool(NORMAL, new ItemStack(draconicShovel), new ItemStack(wyvernShovel), 16000, 2, core_awakened, ingot_draconium_awakened, energy_core_draconic, ingot_draconium_awakened);
//        addFusionTool(HARD, new ItemStack(draconicShovel), new ItemStack(wyvernShovel), 512000, 2, energy_core_draconic, core_awakened, block_draconium_awakened, block_draconium_awakened);
//        addFusionTool(NORMAL, new ItemStack(draconicAxe), new ItemStack(wyvernAxe), 16000, 2, core_awakened, ingot_draconium_awakened, energy_core_draconic, ingot_draconium_awakened);
//        addFusionTool(HARD, new ItemStack(draconicAxe), new ItemStack(wyvernAxe), 512000, 2, energy_core_draconic, core_awakened, block_draconium_awakened, block_draconium_awakened);
//        addFusionTool(NORMAL, new ItemStack(draconicBow), new ItemStack(wyvernBow), 16000, 2, core_awakened, ingot_draconium_awakened, energy_core_draconic, ingot_draconium_awakened);
//        addFusionTool(HARD, new ItemStack(draconicBow), new ItemStack(wyvernBow), 512000, 2, energy_core_draconic, core_awakened, block_draconium_awakened, block_draconium_awakened);
//        addFusionTool(NORMAL, new ItemStack(draconicSword), new ItemStack(wyvernSword), 16000, 2, core_awakened, ingot_draconium_awakened, energy_core_draconic, ingot_draconium_awakened);
//        addFusionTool(HARD, new ItemStack(draconicSword), new ItemStack(wyvernSword), 512000, 2, energy_core_draconic, core_awakened, block_draconium_awakened, block_draconium_awakened);
//        addFusionTool(NORMAL, new ItemStack(draconicHoe), new ItemStack(DIAMOND_HOE), 16000, 2, core_awakened, ingot_draconium_awakened, energy_core_draconic, ingot_draconium_awakened);
//        addFusionTool(HARD, new ItemStack(draconicHoe), new ItemStack(DIAMOND_HOE), 512000, 2, energy_core_draconic, core_awakened, block_draconium_awakened, block_draconium_awakened);
//        addFusionTool(NORMAL, new ItemStack(draconicStaffOfPower), new ItemStack(draconicPick), 16000, 2, ingot_draconium_awakened, ingot_draconium_awakened, ingot_draconium_awakened, ingot_draconium_awakened, ingot_draconium_awakened, core_awakened, draconicShovel, draconicSword);
//        addFusionTool(HARD, new ItemStack(draconicStaffOfPower), new ItemStack(draconicPick), 512000, 2, block_draconium_awakened, block_draconium_awakened, block_draconium_awakened, block_draconium_awakened, block_draconium_awakened, core_awakened, draconicShovel, draconicSword);


//        addFusionTool(NORMAL, new ItemStack(draconicHelm), new ItemStack(wyvernHelm), 320000, 2, ingot_draconium_awakened, core_awakened, ingot_draconium_awakened, energy_core_draconic);
//        addFusionTool(HARD, new ItemStack(draconicHelm), new ItemStack(wyvernHelm), 5000000, 2, block_draconium_awakened, core_awakened, energy_core_draconic);
//        addFusionTool(NORMAL, new ItemStack(draconicChest), new ItemStack(wyvernChest), 320000, 2, ingot_draconium_awakened, core_awakened, ingot_draconium_awakened, energy_core_draconic);
//        addFusionTool(HARD, new ItemStack(draconicChest), new ItemStack(wyvernChest), 5000000, 2, block_draconium_awakened, core_awakened, energy_core_draconic);
//        addFusionTool(NORMAL, new ItemStack(draconicLegs), new ItemStack(wyvernLegs), 320000, 2, ingot_draconium_awakened, core_awakened, ingot_draconium_awakened, energy_core_draconic);
//        addFusionTool(HARD, new ItemStack(draconicLegs), new ItemStack(wyvernLegs), 5000000, 2, block_draconium_awakened, core_awakened, energy_core_draconic);
//        addFusionTool(NORMAL, new ItemStack(draconicBoots), new ItemStack(wyvernBoots), 320000, 2, ingot_draconium_awakened, core_awakened, ingot_draconium_awakened, energy_core_draconic);
//        addFusionTool(HARD, new ItemStack(draconicBoots), new ItemStack(wyvernBoots), 5000000, 2, block_draconium_awakened, core_awakened, energy_core_draconic);


//        //Reactor
//        addShaped(reactorPart, "AAA", "BC ", "AAA", 'A', INGOTS_IRON, 'B', core_wyvern, 'C', ingot_draconium_awakened);
//
//        addShaped(new ItemStack(reactorPart, 1, 1), "   ", "AAA", "BCC", 'A', ingot_draconium_awakened, 'B', core_draconium, 'C', ingot_draconium);
//
//        addShaped(new ItemStack(reactorPart, 1, 2), "   ", "AAA", "BCC", 'A', GEMS_DIAMOND, 'B', core_draconium, 'C', ingot_draconium);
//
//        addShaped(new ItemStack(reactorPart, 1, 3), " AB", "CDD", " AB", 'A', new ItemStack(reactorPart, 1, 1), 'B', new ItemStack(reactorPart, 1, 2), 'C', core_wyvern, 'D', ingot_draconium);
//
//        addShaped(new ItemStack(reactorPart, 1, 4), "ABA", "BCB", "ABA", 'A', "ingotGold", 'B', GEMS_DIAMOND, 'C', core_wyvern);

        //region Upgrade Keys
//                                        addShaped(getKey(ToolUpgrade.RF_CAPACITY), "ABA", "CDC", "ABA", 'A', new ItemStack(DYE, 1, 4), 'B', core_draconium, 'C', ingot_draconium, 'D', energy_core_wyvern);
//                                        addShaped(getKey(ToolUpgrade.DIG_SPEED), "ABA", "CDC", "ABA", 'A', new ItemStack(DYE, 1, 4), 'B', core_draconium, 'C', ingot_draconium, 'D', GOLDEN_PICKAXE);
//                                        addShaped(getKey(ToolUpgrade.DIG_AOE), "ABA", "CDC", "ABA", 'A', new ItemStack(DYE, 1, 4), 'B', core_draconium, 'C', ingot_draconium, 'D', ENDER_PEARLS);
//                                        addShaped(getKey(ToolUpgrade.ATTACK_DAMAGE), "ABA", "CDC", "ABA", 'A', new ItemStack(DYE, 1, 4), 'B', core_draconium, 'C', ingot_draconium, 'D', GOLDEN_SWORD);
//                                        addShaped(getKey(ToolUpgrade.ATTACK_AOE), "ABA", "CDC", "ABA", 'A', new ItemStack(DYE, 1, 4), 'B', core_draconium, 'C', ingot_draconium, 'D', DIAMOND_SWORD);
//                                        addShaped(getKey(ToolUpgrade.ARROW_DAMAGE), "ABC", "DED", "ABA", 'A', new ItemStack(DYE, 1, 4), 'B', core_draconium, 'C', "ingotGold", 'D', ingot_draconium, 'E', ARROW);
//                                        addShaped(getKey(ToolUpgrade.DRAW_SPEED), "ABA", "CDC", "ABE", 'A', new ItemStack(DYE, 1, 4), 'B', core_draconium, 'C', ingot_draconium, 'D', BOW, 'E', "ingotGold");
//                                        addShaped(getKey(ToolUpgrade.ARROW_SPEED), "ABC", "DED", "ABA", 'A', new ItemStack(DYE, 1, 4), 'B', core_draconium, 'C', "feather", 'D', ingot_draconium, 'E', ARROW);
//                                        addShaped(getKey(ToolUpgrade.SHIELD_CAPACITY), "ABA", "CDC", "ABA", 'A', new ItemStack(DYE, 1, 4), 'B', core_draconium, 'C', ingot_draconium, 'D', DIAMOND_CHESTPLATE);
//                                        addShaped(getKey(ToolUpgrade.SHIELD_RECOVERY), "ABA", "CDC", "ABA", 'A', new ItemStack(DYE, 1, 4), 'B', core_draconium, 'C', ingot_draconium, 'D', GOLDEN_CHESTPLATE);
//                                        addShaped(getKey(ToolUpgrade.MOVE_SPEED), "ABA", "CDC", "AEA", 'A', new ItemStack(DYE, 1, 4), 'B', core_draconium, 'C', ingot_draconium, 'D', GOLDEN_BOOTS, 'E', STORAGE_BLOCKS_REDSTONE);
//                                        addShaped(getKey(ToolUpgrade.JUMP_BOOST), "ABA", "CDC", "AEA", 'A', new ItemStack(DYE, 1, 4), 'B', core_draconium, 'C', ingot_draconium, 'D', GOLDEN_BOOTS, 'E', "blockSlime");

    }

    public static void compress3x3(IItemProvider output, IItemProvider input, Consumer<IFinishedRecipe> consumer) {
        ResourceLocation name = output.asItem().getRegistryName();
        ShapedRecipeBuilder.shapedRecipe(output)
                .patternLine("###")
                .patternLine("###")
                .patternLine("###")
                .key('#', input)
                .addCriterion("has_" + input.asItem().getRegistryName().getPath(), hasItem(input))
                .build(consumer, new ResourceLocation(name.getNamespace(), "compress/" + name.getPath()));
    }

    public static void compress2x2(IItemProvider output, IItemProvider input, Consumer<IFinishedRecipe> consumer) {
        ResourceLocation name = output.asItem().getRegistryName();
        ShapedRecipeBuilder.shapedRecipe(output)
                .patternLine("###")
                .patternLine("###")
                .patternLine("###")
                .key('#', input)
                .addCriterion("has_" + input.asItem().getRegistryName().getPath(), hasItem(input))
                .build(consumer, new ResourceLocation(name.getNamespace(), "compress/" + name.getPath()));
    }

    public static void deCompress(IItemProvider output, int count, IItemProvider from, Consumer<IFinishedRecipe> consumer) {
        ResourceLocation name = output.asItem().getRegistryName();
        ShapelessRecipeBuilder.shapelessRecipe(output, count)
                .addIngredient(from)
                .addCriterion("has_" + from.asItem().getRegistryName().getPath(), hasItem(from))
                .build(consumer, new ResourceLocation(name.getNamespace(), "decompress/" + name.getPath()));
    }

    public static void deCompress(IItemProvider output, IItemProvider from, Consumer<IFinishedRecipe> consumer) {
        deCompress(output, 9, from, consumer);
    }

    @Override
    public void act(DirectoryCache cache) throws IOException {
        super.act(cache);
    }
}
