package com.brandon3055.draconicevolution.datagen;

import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.api.crafting.IngredientStack;
import com.brandon3055.draconicevolution.init.DEContent;
import com.brandon3055.draconicevolution.init.DEModules;
import com.brandon3055.draconicevolution.init.DETags;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.client.gui.recipebook.RecipeOverlayGui;
import net.minecraft.data.*;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.io.IOException;
import java.util.function.Consumer;

import static com.brandon3055.draconicevolution.init.DEContent.*;
import static com.brandon3055.draconicevolution.init.DETags.Items.*;
import static net.minecraft.item.Items.*;
import static net.minecraft.tags.ItemTags.ARROWS;
import static net.minecraft.tags.ItemTags.PLANKS;
import static net.minecraftforge.common.Tags.Items.*;

/**
 * Created by brandon3055 on 1/12/20
 */
public class RecipeGenerator extends RecipeProvider {

    public RecipeGenerator(DataGenerator generatorIn) {
        super(generatorIn);
    }

    @Override
    protected void buildShapelessRecipes(Consumer<IFinishedRecipe> consumer) {
        components(consumer);

        compressDecompress(consumer);
        machines(consumer);
        energy(consumer);
        tools(consumer);
        equipment(consumer);
        modules(consumer);
        unsorted(consumer);

        FusionRecipeBuilder.fusionRecipe(block_draconium_awakened, 4)
                .catalyst(4, STORAGE_BLOCKS_DRACONIUM)
                .energy(50000000)
                .techLevel(TechLevel.WYVERN)
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

        CookingRecipeBuilder.smelting(Ingredient.of(DUSTS_DRACONIUM), ingot_draconium, 0, 200).unlockedBy("has_draconium_dust", has(DUSTS_DRACONIUM)).save(consumer, folder("components", ingot_draconium));
        CookingRecipeBuilder.smelting(Ingredient.of(ORES_DRACONIUM), ingot_draconium, 1, 200).unlockedBy("has_draconium_ore", has(ORES_DRACONIUM)).save(consumer);

        ShapedRecipeBuilder.shaped(core_draconium)
                .pattern("ABA")
                .pattern("BCB")
                .pattern("ABA")
                .define('A', INGOTS_DRACONIUM)
                .define('B', INGOTS_GOLD)
                .define('C', GEMS_DIAMOND)
                .unlockedBy("has_draconium", has(ingot_draconium))
                .save(consumer, folder("components", core_draconium));

        ShapedRecipeBuilder.shaped(core_wyvern)
                .pattern("ABA")
                .pattern("BCB")
                .pattern("ABA")
                .define('A', INGOTS_DRACONIUM)
                .define('B', core_draconium)
                .define('C', NETHER_STARS)
                .unlockedBy("has_core_draconium", has(core_draconium))
                .save(consumer, folder("components", core_wyvern));

        FusionRecipeBuilder.fusionRecipe(core_awakened)
                .catalyst(NETHER_STARS)
                .energy(1000000)
                .techLevel(TechLevel.WYVERN)
                .ingredient(core_wyvern)
                .ingredient(core_wyvern)
                .ingredient(INGOTS_DRACONIUM_AWAKENED)
                .ingredient(INGOTS_DRACONIUM_AWAKENED)
                .ingredient(INGOTS_DRACONIUM_AWAKENED)
                .ingredient(INGOTS_DRACONIUM_AWAKENED)
                .ingredient(core_wyvern)
                .ingredient(core_wyvern)
                .build(consumer, folder("components", core_awakened));

        FusionRecipeBuilder.fusionRecipe(core_chaotic)
                .catalyst(chaos_frag_large)
                .energy(100000000)
                .techLevel(TechLevel.DRACONIC)
                .ingredient(INGOTS_DRACONIUM_AWAKENED)
                .ingredient(INGOTS_DRACONIUM_AWAKENED)
                .ingredient(core_awakened)
                .ingredient(core_awakened)
                .ingredient(chaos_frag_large)
                .ingredient(chaos_frag_large)
                .ingredient(INGOTS_DRACONIUM_AWAKENED)
                .ingredient(core_awakened)
                .ingredient(core_awakened)
                .ingredient(chaos_frag_large)
                .ingredient(chaos_frag_large)
                .ingredient(INGOTS_DRACONIUM_AWAKENED)
                .build(consumer, folder("components", core_chaotic));

        ShapedRecipeBuilder.shaped(energy_core_wyvern)
                .pattern("ABA")
                .pattern("BCB")
                .pattern("ABA")
                .define('A', INGOTS_DRACONIUM)
                .define('B', Tags.Items.STORAGE_BLOCKS_REDSTONE)
                .define('C', core_draconium)
                .unlockedBy("has_core_draconium", has(core_draconium))
                .save(consumer, folder("components", energy_core_wyvern));

        ShapedRecipeBuilder.shaped(energy_core_draconic)
                .pattern("ABA")
                .pattern("BCB")
                .pattern("ABA")
                .define('A', INGOTS_DRACONIUM_AWAKENED)
                .define('B', energy_core_wyvern)
                .define('C', core_wyvern)
                .unlockedBy("has_core_wyvern", has(core_wyvern))
                .save(consumer, folder("components", energy_core_draconic));

        ShapedRecipeBuilder.shaped(energy_core_chaotic)
                .pattern("ABA")
                .pattern("BCB")
                .pattern("ABA")
                .define('A', chaos_frag_medium)
                .define('B', energy_core_draconic)
                .define('C', core_awakened)
                .unlockedBy("has_core_awakened", has(core_awakened))
                .save(consumer, folder("components", energy_core_chaotic));
    }

    private static void compressDecompress(Consumer<IFinishedRecipe> consumer) {
        compress3x3(ingot_draconium, NUGGETS_DRACONIUM, "nugget_draconium", consumer);
        compress3x3(ingot_draconium_awakened, NUGGETS_DRACONIUM_AWAKENED, "nugget_draconium_awakened", consumer);
        compress3x3(block_draconium, INGOTS_DRACONIUM, "ingot_draconium", consumer);
        compress3x3(block_draconium_awakened, INGOTS_DRACONIUM_AWAKENED, "ingot_draconium_awakened", consumer);

        deCompress(nugget_draconium, INGOTS_DRACONIUM, "ingot_draconium", consumer);
        deCompress(nugget_draconium_awakened, INGOTS_DRACONIUM_AWAKENED, "ingot_draconium_awakened", consumer);
        deCompress(ingot_draconium, STORAGE_BLOCKS_DRACONIUM, "block_draconium", consumer);
        deCompress(ingot_draconium_awakened, STORAGE_BLOCKS_DRACONIUM_AWAKENED, "block_draconium_awakened", consumer);

        deCompress(chaos_frag_large, chaos_shard, consumer);
        deCompress(chaos_frag_medium, chaos_frag_large, consumer);
        deCompress(chaos_frag_small, chaos_frag_medium, consumer);
        compress3x3(chaos_shard, chaos_frag_large, consumer);
        compress3x3(chaos_frag_large, chaos_frag_medium, consumer);
        compress3x3(chaos_frag_medium, chaos_frag_small, consumer);
    }

    private static void machines(Consumer<IFinishedRecipe> consumer) {
        ShapedRecipeBuilder.shaped(crafting_core)
                .pattern("ABA")
                .pattern("BCB")
                .pattern("ABA")
                .define('A', STORAGE_BLOCKS_LAPIS)
                .define('B', GEMS_DIAMOND)
                .define('C', core_draconium)
                .unlockedBy("has_core_draconium", has(core_draconium))
                .save(consumer);

        ShapedRecipeBuilder.shaped(crafting_injector_basic)
                .pattern("ABA")
                .pattern("CDC")
                .pattern("CCC")
                .define('A', GEMS_DIAMOND)
                .define('B', core_draconium)
                .define('C', Tags.Items.STONE)
                .define('D', STORAGE_BLOCKS_IRON)
                .unlockedBy("has_core_draconium", has(core_draconium))
                .save(consumer);

        FusionRecipeBuilder.fusionRecipe(crafting_injector_wyvern)
                .catalyst(crafting_injector_basic)
                .energy(32000)
                .techLevel(TechLevel.DRACONIUM)
                .ingredient(core_wyvern)
                .ingredient(GEMS_DIAMOND)
                .ingredient(core_draconium)
                .ingredient(GEMS_DIAMOND)
                .ingredient(core_draconium)
                .ingredient(GEMS_DIAMOND)
                .ingredient(STORAGE_BLOCKS_DRACONIUM)
                .ingredient(GEMS_DIAMOND)
                .build(consumer);

        FusionRecipeBuilder.fusionRecipe(crafting_injector_awakened)
                .catalyst(crafting_injector_wyvern)
                .energy(256000)
                .techLevel(TechLevel.WYVERN)
                .ingredient(GEMS_DIAMOND)
                .ingredient(GEMS_DIAMOND)
                .ingredient(core_wyvern)
                .ingredient(STORAGE_BLOCKS_DRACONIUM_AWAKENED)
                .ingredient(core_wyvern)
                .ingredient(GEMS_DIAMOND)
                .ingredient(GEMS_DIAMOND)
                .build(consumer);

        FusionRecipeBuilder.fusionRecipe(crafting_injector_chaotic)
                .catalyst(crafting_injector_awakened)
                .energy(8000000)
                .techLevel(TechLevel.DRACONIC)
                .ingredient(GEMS_DIAMOND)
                .ingredient(GEMS_DIAMOND)
                .ingredient(chaos_frag_large)
                .ingredient(chaos_frag_large)
                .ingredient(chaos_frag_large)
                .ingredient(chaos_frag_large)
                .ingredient(DRAGON_EGG)
                .ingredient(GEMS_DIAMOND)
                .ingredient(GEMS_DIAMOND)
                .build(consumer);

        ShapedRecipeBuilder.shaped(DEContent.generator)
                .pattern("ABA")
                .pattern("BCB")
                .pattern("ADA")
                .define('A', INGOTS_NETHER_BRICK)
                .define('B', INGOTS_IRON)
                .define('C', FURNACE)
                .define('D', core_draconium)
                .unlockedBy("has_core_draconium", has(core_draconium))
                .save(consumer);

        ShapedRecipeBuilder.shaped(grinder)
                .pattern("ABA")
                .pattern("CDC")
                .pattern("AEA")
                .define('A', INGOTS_IRON)
                .define('B', INGOTS_DRACONIUM)
                .define('C', DIAMOND_SWORD)
                .define('D', core_draconium)
                .define('E', FURNACE)
                .unlockedBy("has_core_draconium", has(core_draconium))
                .save(consumer);

        ShapedRecipeBuilder.shaped(energy_transfuser)
                .pattern("ABA")
                .pattern("CDC")
                .pattern("ACA")
                .define('A', INGOTS_DRACONIUM)
                .define('B', energy_core_stabilizer)
                .define('C', core_draconium)
                .define('D', ENCHANTING_TABLE)
                .unlockedBy("has_core_draconium", has(core_draconium))
                .save(consumer);

        ShapedRecipeBuilder.shaped(particle_generator)
                .pattern("ABA")
                .pattern("BCB")
                .pattern("ABA")
                .define('A', STORAGE_BLOCKS_REDSTONE)
                .define('B', BLAZE_ROD)
                .define('C', core_draconium)
                .unlockedBy("has_core_draconium", has(core_draconium))
                .save(consumer);

        //Disabled until i can finish the re write
//        FusionRecipeBuilder.fusionRecipe(draconium_chest)
//                .catalyst(CHEST)
//                .energy(2000000)
//                .techLevel(TechLevel.DRACONIUM)
//                .ingredient(FURNACE)
//                .ingredient(core_draconium)
//                .ingredient(FURNACE)
//                .ingredient(core_draconium)
//                .ingredient(FURNACE)
//                .ingredient(CRAFTING_TABLE)
//                .ingredient(FURNACE)
//                .ingredient(STORAGE_BLOCKS_DRACONIUM)
//                .ingredient(FURNACE)
//                .ingredient(CRAFTING_TABLE)
//                .build(consumer);

        ShapedRecipeBuilder.shaped(potentiometer)
                .pattern(" A ")
                .pattern("BCB")
                .pattern("DDD")
                .define('A', PLANKS)
                .define('B', DUSTS_REDSTONE)
                .define('C', DUSTS_DRACONIUM)
                .define('D', STONE_SLAB)
                .unlockedBy("has_STONE_SLAB", has(STONE_SLAB))
                .save(consumer);




    }

    private static void energy(Consumer<IFinishedRecipe> consumer) {
        ShapedRecipeBuilder.shaped(energy_core)
                .pattern("AAA")
                .pattern("BCB")
                .pattern("AAA")
                .define('A', INGOTS_DRACONIUM)
                .define('B', energy_core_wyvern)
                .define('C', core_wyvern)
                .unlockedBy("has_core_wyvern", has(core_wyvern))
                .save(consumer);

        ShapedRecipeBuilder.shaped(energy_core_stabilizer)
                .pattern("A A")
                .pattern(" B ")
                .pattern("A A")
                .define('A', GEMS_DIAMOND)
                .define('B', particle_generator)
                .unlockedBy("has_core_wyvern", has(core_wyvern))
                .save(consumer);

        ShapedRecipeBuilder.shaped(energy_pylon, 2)
                .pattern("ABA")
                .pattern("CDC")
                .pattern("AEA")
                .define('A', INGOTS_DRACONIUM)
                .define('B', ENDER_EYE)
                .define('C', GEMS_EMERALD)
                .define('D', core_draconium)
                .define('E', GEMS_DIAMOND)
                .unlockedBy("has_core_wyvern", has(core_wyvern))
                .save(consumer);

        //Reactor
        ShapedRecipeBuilder.shaped(reactor_prt_stab_frame)
                .pattern("AAA")
                .pattern("BC ")
                .pattern("AAA")
                .define('A', INGOTS_IRON)
                .define('B', core_wyvern)
                .define('C', INGOTS_DRACONIUM_AWAKENED)
                .unlockedBy("has_core_wyvern", has(core_wyvern))
                .save(consumer);

        ShapedRecipeBuilder.shaped(reactor_prt_in_rotor)
                .pattern("   ")
                .pattern("AAA")
                .pattern("BCC")
                .define('A', INGOTS_DRACONIUM_AWAKENED)
                .define('B', core_draconium)
                .define('C', INGOTS_DRACONIUM)
                .unlockedBy("has_core_draconium", has(core_draconium))
                .save(consumer);

        ShapedRecipeBuilder.shaped(reactor_prt_out_rotor)
                .pattern("   ")
                .pattern("AAA")
                .pattern("BCC")
                .define('A', GEMS_DIAMOND)
                .define('B', core_draconium)
                .define('C', INGOTS_DRACONIUM)
                .unlockedBy("has_core_draconium", has(core_draconium))
                .save(consumer);

        ShapedRecipeBuilder.shaped(reactor_prt_rotor_full)
                .pattern(" AB")
                .pattern("CDD")
                .pattern(" AB")
                .define('A', reactor_prt_in_rotor)
                .define('B', reactor_prt_out_rotor)
                .define('C', core_wyvern)
                .define('D', INGOTS_DRACONIUM)
                .unlockedBy("has_core_wyvern", has(core_wyvern))
                .save(consumer);

        ShapedRecipeBuilder.shaped(reactor_prt_focus_ring)
                .pattern("ABA")
                .pattern("CBC")
                .pattern("ABA")
                .define('A', INGOTS_GOLD)
                .define('B', GEMS_DIAMOND)
                .define('C', core_wyvern)
                .unlockedBy("has_core_wyvern", has(core_wyvern))
                .save(consumer);

        FusionRecipeBuilder.fusionRecipe(reactor_stabilizer)
                .catalyst(reactor_prt_stab_frame)
                .energy(16000000)
                .techLevel(TechLevel.CHAOTIC)
                .ingredient(energy_core_draconic)
                .ingredient(reactor_prt_rotor_full)
                .ingredient(reactor_prt_focus_ring)
                .ingredient(INGOTS_DRACONIUM_AWAKENED)
                .ingredient(INGOTS_DRACONIUM_AWAKENED)
                .ingredient(core_chaotic)
                .ingredient(INGOTS_DRACONIUM_AWAKENED)
                .ingredient(chaos_frag_large)
                .build(consumer);

        FusionRecipeBuilder.fusionRecipe(reactor_injector)
                .catalyst(core_wyvern)
                .energy(16000000)
                .techLevel(TechLevel.CHAOTIC)
                .ingredient(INGOTS_DRACONIUM)
                .ingredient(reactor_prt_in_rotor)
                .ingredient(reactor_prt_in_rotor)
                .ingredient(reactor_prt_in_rotor)
                .ingredient(INGOTS_DRACONIUM)
                .ingredient(INGOTS_DRACONIUM)
                .ingredient(INGOTS_IRON)
                .ingredient(reactor_prt_in_rotor)
                .ingredient(INGOTS_IRON)
                .ingredient(INGOTS_DRACONIUM)
                .build(consumer);


        FusionRecipeBuilder.fusionRecipe(reactor_core)
                .catalyst(chaos_shard)
                .energy(64000000)
                .techLevel(TechLevel.CHAOTIC)
                .ingredient(INGOTS_DRACONIUM_AWAKENED)
                .ingredient(INGOTS_DRACONIUM)
                .ingredient(INGOTS_DRACONIUM_AWAKENED)
                .ingredient(INGOTS_DRACONIUM)
                .ingredient(INGOTS_DRACONIUM_AWAKENED)
                .ingredient(INGOTS_DRACONIUM)
                .ingredient(INGOTS_DRACONIUM_AWAKENED)
                .ingredient(chaos_frag_large)
                .ingredient(chaos_frag_large)
                .build(consumer);

        ShapedRecipeBuilder.shaped(crystal_binder)
                .pattern(" AB")
                .pattern(" CA")
                .pattern("D  ")
                .define('A', INGOTS_DRACONIUM)
                .define('B', GEMS_DIAMOND)
                .define('C', BLAZE_ROD)
                .define('D', core_draconium)
                .unlockedBy("has_core_draconium", has(core_draconium))
                .save(consumer);

        ShapedRecipeBuilder.shaped(crystal_relay_basic, 4)
                .pattern(" A ")
                .pattern("ABA")
                .pattern(" A ")
                .define('A', GEMS_DIAMOND)
                .define('B', energy_core_wyvern)
                .unlockedBy("has_core_draconium", has(core_draconium))
                .save(consumer);

        ShapedRecipeBuilder.shaped(crystal_relay_wyvern, 4)
                .pattern("ABA")
                .pattern("BCB")
                .pattern("ABA")
                .define('A', energy_core_wyvern)
                .define('B', crystal_relay_basic)
                .define('C', core_draconium)
                .unlockedBy("has_core_draconium", has(core_draconium))
                .save(consumer);

        FusionRecipeBuilder.fusionRecipe(crystal_relay_draconic, 4)
                .catalyst(4, crystal_relay_wyvern)
                .energy(128000)
                .techLevel(TechLevel.DRACONIC)
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

        ShapedRecipeBuilder.shaped(crystal_wireless_basic)
                .pattern("ABA")
                .pattern("CDC")
                .pattern("ABA")
                .define('A', ENDER_PEARL)
                .define('B', particle_generator)
                .define('C', ENDER_EYE)
                .define('D', crystal_relay_basic)
                .unlockedBy("has_core_draconium", has(core_draconium))
                .save(consumer);

        ShapedRecipeBuilder.shaped(crystal_wireless_wyvern)
                .pattern("ABA")
                .pattern("CDC")
                .pattern("ABA")
                .define('A', ENDER_PEARL)
                .define('B', particle_generator)
                .define('C', ENDER_EYE)
                .define('D', crystal_relay_wyvern)
                .unlockedBy("has_core_draconium", has(core_draconium))
                .save(consumer);

        ShapedRecipeBuilder.shaped(crystal_wireless_draconic)
                .pattern("ABA")
                .pattern("CDC")
                .pattern("ABA")
                .define('A', ENDER_PEARL)
                .define('B', particle_generator)
                .define('C', ENDER_EYE)
                .define('D', crystal_relay_draconic)
                .unlockedBy("has_core_draconium", has(core_draconium))
                .save(consumer);

        //to-from io
        ShapelessRecipeBuilder.shapeless(crystal_io_basic, 2)
                .requires(crystal_relay_basic)
                .unlockedBy("has_core_draconium", has(core_draconium))
                .save(consumer);

        ShapelessRecipeBuilder.shapeless(crystal_io_wyvern, 2)
                .requires(crystal_relay_wyvern)
                .unlockedBy("has_core_draconium", has(core_draconium))
                .save(consumer);

        ShapelessRecipeBuilder.shapeless(crystal_io_draconic, 2)
                .requires(crystal_relay_draconic)
                .unlockedBy("has_core_draconium", has(core_draconium))
                .save(consumer);

        ShapelessRecipeBuilder.shapeless(crystal_relay_basic)
                .requires(crystal_io_basic)
                .requires(crystal_io_basic)
                .unlockedBy("has_core_draconium", has(core_draconium))
                .save(consumer, "draconicevolution:crystal_io_basic_combine");

        ShapelessRecipeBuilder.shapeless(crystal_relay_wyvern)
                .requires(crystal_io_wyvern)
                .requires(crystal_io_wyvern)
                .unlockedBy("has_core_draconium", has(core_draconium))
                .save(consumer, "draconicevolution:crystal_io_wyvern_combine");

        ShapelessRecipeBuilder.shapeless(crystal_relay_draconic)
                .requires(crystal_io_draconic)
                .requires(crystal_io_draconic)
                .unlockedBy("has_core_draconium", has(core_draconium))
                .save(consumer, "draconicevolution:crystal_io_draconic_combine");
    }

    private static void tools(Consumer<IFinishedRecipe> consumer) {
        ShapedRecipeBuilder.shaped(dislocator)
                .pattern("ABA")
                .pattern("BCB")
                .pattern("ABA")
                .define('A', BLAZE_POWDER)
                .define('B', DUSTS_DRACONIUM)
                .define('C', ENDER_EYE)
                .unlockedBy("has_dust_draconium", has(dust_draconium))
                .save(consumer);

        FusionRecipeBuilder.fusionRecipe(dislocator_advanced)
                .catalyst(dislocator)
                .energy(1000000)
                .techLevel(TechLevel.WYVERN)
                .ingredient(ENDER_PEARLS)
                .ingredient(INGOTS_DRACONIUM)
                .ingredient(ENDER_PEARLS)
                .ingredient(INGOTS_DRACONIUM)
                .ingredient(ENDER_PEARLS)
                .ingredient(INGOTS_DRACONIUM)
                .ingredient(core_wyvern)
                .ingredient(INGOTS_DRACONIUM)
                .build(consumer);

        ShapelessRecipeBuilder.shapeless(dislocator_p2p)
                .requires(dislocator)
                .requires(core_draconium)
                .requires(dislocator)
                .requires(GHAST_TEAR)
                .unlockedBy("has_core_draconium", has(core_draconium))
                .save(consumer);

        ShapelessRecipeBuilder.shapeless(dislocator_player)
                .requires(dislocator)
                .requires(core_draconium)
                .requires(GHAST_TEAR)
                .unlockedBy("has_core_draconium", has(core_draconium))
                .save(consumer);

        ShapedRecipeBuilder.shaped(magnet)
                .pattern("A A")
                .pattern("B B")
                .pattern("CDC")
                .define('A', DUSTS_REDSTONE)
                .define('B', INGOTS_DRACONIUM)
                .define('C', INGOTS_IRON)
                .define('D', dislocator)
                .unlockedBy("has_dust_draconium", has(dust_draconium))
                .save(consumer);

        ShapedRecipeBuilder.shaped(magnet_advanced)
                .pattern("A A")
                .pattern("B B")
                .pattern("CDC")
                .define('A', INGOTS_DRACONIUM)
                .define('B', DUSTS_REDSTONE)
                .define('C', INGOTS_DRACONIUM_AWAKENED)
                .define('D', magnet)
                .unlockedBy("has_dust_draconium", has(dust_draconium))
                .save(consumer);
    }

    private static void equipment(Consumer<IFinishedRecipe> consumer) {
        //Capacitors
        FusionRecipeBuilder.fusionRecipe(DEContent.capacitor_wyvern)
                .catalyst(DEContent.core_wyvern)
                .energy(8000000)
                .techLevel(TechLevel.WYVERN)
                .ingredient(DETags.Items.INGOTS_DRACONIUM)
                .ingredient(DEContent.energy_core_wyvern)
                .ingredient(DETags.Items.INGOTS_DRACONIUM)
                .ingredient(DEContent.energy_core_wyvern)
                .ingredient(DEContent.energy_core_wyvern)
                .ingredient(DETags.Items.INGOTS_DRACONIUM)
                .ingredient(DEContent.energy_core_wyvern)
                .ingredient(DETags.Items.INGOTS_DRACONIUM)
                .build(consumer, folder("tools", DEContent.capacitor_wyvern));

        FusionRecipeBuilder.fusionRecipe(DEContent.capacitor_draconic)
                .catalyst(DEContent.core_awakened)
                .energy(32000000)
                .techLevel(TechLevel.DRACONIC)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DEContent.capacitor_wyvern)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DEContent.energy_core_draconic)
                .ingredient(DEContent.energy_core_draconic)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DEContent.energy_core_draconic)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .build(consumer, folder("tools", DEContent.capacitor_draconic));

        FusionRecipeBuilder.fusionRecipe(DEContent.capacitor_chaotic)
                .catalyst(capacitor_draconic)
                .energy(128000000)
                .techLevel(TechLevel.CHAOTIC)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DEContent.core_chaotic)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DEContent.energy_core_chaotic)
                .ingredient(DEContent.energy_core_chaotic)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DEContent.energy_core_chaotic)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .build(consumer, folder("tools", DEContent.core_chaotic));

        //Shovel
        FusionRecipeBuilder.fusionRecipe(DEContent.shovel_wyvern)
                .catalyst(Items.DIAMOND_SHOVEL)
                .energy(8000000)
                .techLevel(TechLevel.WYVERN)
                .ingredient(DEContent.core_draconium)
                .ingredient(DETags.Items.INGOTS_DRACONIUM)
                .ingredient(DETags.Items.INGOTS_DRACONIUM)
                .ingredient(DEContent.crystal_relay_basic)
                .ingredient(DEContent.energy_core_wyvern)
                .ingredient(DEContent.crystal_relay_basic)
                .build(consumer, folder("tools", DEContent.shovel_wyvern));

        FusionRecipeBuilder.fusionRecipe(DEContent.shovel_draconic)
                .catalyst(DEContent.shovel_wyvern)
                .energy(32000000)
                .techLevel(TechLevel.DRACONIC)
                .ingredient(Tags.Items.INGOTS_NETHERITE)
                .ingredient(DEContent.core_wyvern)
                .ingredient(Tags.Items.INGOTS_NETHERITE)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(Tags.Items.INGOTS_NETHERITE)
                .ingredient(DEContent.energy_core_draconic)
                .ingredient(Tags.Items.INGOTS_NETHERITE)
                .build(consumer, folder("tools", DEContent.shovel_draconic));

        FusionRecipeBuilder.fusionRecipe(DEContent.shovel_chaotic)
                .catalyst(DEContent.shovel_draconic)
                .energy(128000000)
                .techLevel(TechLevel.CHAOTIC)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(core_chaotic)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DEContent.energy_core_chaotic)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .build(consumer, folder("tools", DEContent.shovel_chaotic));

        //Hoe
        FusionRecipeBuilder.fusionRecipe(DEContent.hoe_wyvern)
                .catalyst(Items.DIAMOND_HOE)
                .energy(8000000)
                .techLevel(TechLevel.WYVERN)
                .ingredient(DEContent.core_draconium)
                .ingredient(DETags.Items.INGOTS_DRACONIUM)
                .ingredient(DETags.Items.INGOTS_DRACONIUM)
                .ingredient(DEContent.crystal_relay_basic)
                .ingredient(DEContent.energy_core_wyvern)
                .ingredient(DEContent.crystal_relay_basic)
                .build(consumer, folder("tools", DEContent.hoe_wyvern));

        FusionRecipeBuilder.fusionRecipe(DEContent.hoe_draconic)
                .catalyst(DEContent.hoe_wyvern)
                .energy(32000000)
                .techLevel(TechLevel.DRACONIC)
                .ingredient(Tags.Items.INGOTS_NETHERITE)
                .ingredient(DEContent.core_wyvern)
                .ingredient(Tags.Items.INGOTS_NETHERITE)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(Tags.Items.INGOTS_NETHERITE)
                .ingredient(DEContent.energy_core_draconic)
                .ingredient(Tags.Items.INGOTS_NETHERITE)
                .build(consumer, folder("tools", DEContent.hoe_draconic));

        FusionRecipeBuilder.fusionRecipe(DEContent.hoe_chaotic)
                .catalyst(DEContent.hoe_draconic)
                .energy(128000000)
                .techLevel(TechLevel.CHAOTIC)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DEContent.core_chaotic)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DEContent.energy_core_chaotic)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .build(consumer, folder("tools", DEContent.hoe_chaotic));

        //Pickaxe
        FusionRecipeBuilder.fusionRecipe(DEContent.pickaxe_wyvern)
                .catalyst(Items.DIAMOND_PICKAXE)
                .energy(8000000)
                .techLevel(TechLevel.WYVERN)
                .ingredient(DEContent.core_draconium)
                .ingredient(DETags.Items.INGOTS_DRACONIUM)
                .ingredient(DETags.Items.INGOTS_DRACONIUM)
                .ingredient(DEContent.crystal_relay_basic)
                .ingredient(DEContent.energy_core_wyvern)
                .ingredient(DEContent.crystal_relay_basic)
                .build(consumer, folder("tools", DEContent.pickaxe_wyvern));

        FusionRecipeBuilder.fusionRecipe(DEContent.pickaxe_draconic)
                .catalyst(DEContent.pickaxe_wyvern)
                .energy(32000000)
                .techLevel(TechLevel.DRACONIC)
                .ingredient(Tags.Items.INGOTS_NETHERITE)
                .ingredient(DEContent.core_wyvern)
                .ingredient(Tags.Items.INGOTS_NETHERITE)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(Tags.Items.INGOTS_NETHERITE)
                .ingredient(DEContent.energy_core_draconic)
                .ingredient(Tags.Items.INGOTS_NETHERITE)
                .build(consumer, folder("tools", DEContent.pickaxe_draconic));

        FusionRecipeBuilder.fusionRecipe(DEContent.pickaxe_chaotic)
                .catalyst(DEContent.pickaxe_draconic)
                .energy(128000000)
                .techLevel(TechLevel.CHAOTIC)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DEContent.core_chaotic)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DEContent.energy_core_chaotic)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .build(consumer, folder("tools", DEContent.pickaxe_chaotic));

        //Axe
        FusionRecipeBuilder.fusionRecipe(DEContent.axe_wyvern)
                .catalyst(Items.DIAMOND_AXE)
                .energy(8000000)
                .techLevel(TechLevel.WYVERN)
                .ingredient(DEContent.core_draconium)
                .ingredient(DETags.Items.INGOTS_DRACONIUM)
                .ingredient(DETags.Items.INGOTS_DRACONIUM)
                .ingredient(DEContent.crystal_relay_basic)
                .ingredient(DEContent.energy_core_wyvern)
                .ingredient(DEContent.crystal_relay_basic)
                .build(consumer, folder("tools", DEContent.axe_wyvern));

        FusionRecipeBuilder.fusionRecipe(DEContent.axe_draconic)
                .catalyst(DEContent.axe_wyvern)
                .energy(32000000)
                .techLevel(TechLevel.DRACONIC)
                .ingredient(Tags.Items.INGOTS_NETHERITE)
                .ingredient(DEContent.core_wyvern)
                .ingredient(Tags.Items.INGOTS_NETHERITE)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(Tags.Items.INGOTS_NETHERITE)
                .ingredient(DEContent.energy_core_draconic)
                .ingredient(Tags.Items.INGOTS_NETHERITE)
                .build(consumer, folder("tools", DEContent.axe_draconic));

        FusionRecipeBuilder.fusionRecipe(DEContent.axe_chaotic)
                .catalyst(DEContent.axe_draconic)
                .energy(128000000)
                .techLevel(TechLevel.CHAOTIC)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DEContent.core_chaotic)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DEContent.energy_core_chaotic)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .build(consumer, folder("tools", DEContent.axe_chaotic));

        //Bow
        FusionRecipeBuilder.fusionRecipe(DEContent.bow_wyvern)
                .catalyst(Items.BOW)
                .energy(8000000)
                .techLevel(TechLevel.WYVERN)
                .ingredient(DEContent.core_draconium)
                .ingredient(DETags.Items.INGOTS_DRACONIUM)
                .ingredient(DETags.Items.INGOTS_DRACONIUM)
                .ingredient(DEContent.crystal_relay_basic)
                .ingredient(DEContent.energy_core_wyvern)
                .ingredient(DEContent.crystal_relay_basic)
                .build(consumer, folder("tools", DEContent.bow_wyvern));

        FusionRecipeBuilder.fusionRecipe(DEContent.bow_draconic)
                .catalyst(DEContent.bow_wyvern)
                .energy(32000000)
                .techLevel(TechLevel.DRACONIC)
                .ingredient(Tags.Items.INGOTS_NETHERITE)
                .ingredient(DEContent.core_wyvern)
                .ingredient(Tags.Items.INGOTS_NETHERITE)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(Tags.Items.INGOTS_NETHERITE)
                .ingredient(DEContent.energy_core_draconic)
                .ingredient(Tags.Items.INGOTS_NETHERITE)
                .build(consumer, folder("tools", DEContent.bow_draconic));

        FusionRecipeBuilder.fusionRecipe(DEContent.bow_chaotic)
                .catalyst(DEContent.bow_draconic)
                .energy(128000000)
                .techLevel(TechLevel.CHAOTIC)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DEContent.core_chaotic)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DEContent.energy_core_chaotic)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .build(consumer, folder("tools", DEContent.bow_chaotic));

        //Sword
        FusionRecipeBuilder.fusionRecipe(DEContent.sword_wyvern)
                .catalyst(Items.DIAMOND_SWORD)
                .energy(8000000)
                .techLevel(TechLevel.WYVERN)
                .ingredient(DEContent.core_draconium)
                .ingredient(DETags.Items.INGOTS_DRACONIUM)
                .ingredient(DETags.Items.INGOTS_DRACONIUM)
                .ingredient(DEContent.crystal_relay_basic)
                .ingredient(DEContent.energy_core_wyvern)
                .ingredient(DEContent.crystal_relay_basic)
                .build(consumer, folder("tools", DEContent.sword_wyvern));

        FusionRecipeBuilder.fusionRecipe(DEContent.sword_draconic)
                .catalyst(DEContent.sword_wyvern)
                .energy(32000000)
                .techLevel(TechLevel.DRACONIC)
                .ingredient(Tags.Items.INGOTS_NETHERITE)
                .ingredient(DEContent.core_wyvern)
                .ingredient(Tags.Items.INGOTS_NETHERITE)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(Tags.Items.INGOTS_NETHERITE)
                .ingredient(DEContent.energy_core_draconic)
                .ingredient(Tags.Items.INGOTS_NETHERITE)
                .build(consumer, folder("tools", DEContent.sword_draconic));

        FusionRecipeBuilder.fusionRecipe(DEContent.sword_chaotic)
                .catalyst(DEContent.sword_draconic)
                .energy(128000000)
                .techLevel(TechLevel.CHAOTIC)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DEContent.core_chaotic)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DEContent.energy_core_chaotic)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .build(consumer, folder("tools", DEContent.sword_chaotic));

        //Staff
        FusionRecipeBuilder.fusionRecipe(DEContent.staff_draconic)
                .catalyst(DEContent.core_awakened)
                .energy(256000000)
                .techLevel(TechLevel.DRACONIC)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DEContent.pickaxe_draconic)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DEContent.sword_draconic)
                .ingredient(DEContent.shovel_draconic)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DEContent.energy_core_draconic)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .build(consumer, folder("tools", DEContent.staff_draconic));

        FusionRecipeBuilder.fusionRecipe(DEContent.staff_chaotic)
                .catalyst(DEContent.core_chaotic)
                .energy(1024000000)
                .techLevel(TechLevel.CHAOTIC)
                .ingredient(DEContent.chaos_frag_medium)
                .ingredient(DEContent.pickaxe_chaotic)
                .ingredient(DEContent.chaos_frag_medium)
                .ingredient(DEContent.chaos_frag_medium)
                .ingredient(DEContent.sword_chaotic)
                .ingredient(DEContent.shovel_chaotic)
                .ingredient(DEContent.chaos_frag_medium)
                .ingredient(DEContent.energy_core_chaotic)
                .ingredient(DEContent.chaos_frag_medium)
                .ingredient(DEContent.chaos_frag_medium)
                .build(consumer, folder("tools", DEContent.staff_chaotic));

        FusionRecipeBuilder.fusionRecipe(DEContent.staff_chaotic)
                .catalyst(DEContent.staff_draconic)
                .energy(1024000000)
                .techLevel(TechLevel.CHAOTIC)
                .ingredient(DEContent.chaos_frag_medium)
                .ingredient(DEContent.core_chaotic)
                .ingredient(DEContent.chaos_frag_medium)
                .ingredient(DEContent.energy_core_chaotic)
                .ingredient(DEContent.core_awakened)
                .ingredient(DEContent.core_awakened)
                .ingredient(DEContent.chaos_frag_large)
                .ingredient(DEContent.energy_core_chaotic)
                .ingredient(DEContent.chaos_frag_large)
                .ingredient(DEContent.energy_core_chaotic)
                .build(consumer, folder("tools", "alt_" + DEContent.staff_chaotic.getItem().getRegistryName().getPath()));

        //Chestpiece
        FusionRecipeBuilder.fusionRecipe(DEContent.chestpiece_wyvern)
                .catalyst(Items.DIAMOND_CHESTPLATE)
                .energy(8000000)
                .techLevel(TechLevel.WYVERN)
                .ingredient(DEContent.core_draconium)
                .ingredient(DETags.Items.INGOTS_DRACONIUM)
                .ingredient(DETags.Items.INGOTS_DRACONIUM)
                .ingredient(DEContent.crystal_relay_basic)
                .ingredient(DEContent.energy_core_wyvern)
                .ingredient(DEContent.crystal_relay_basic)
                .build(consumer, folder("tools", DEContent.chestpiece_wyvern));

        FusionRecipeBuilder.fusionRecipe(DEContent.chestpiece_draconic)
                .catalyst(DEContent.chestpiece_wyvern)
                .energy(32000000)
                .techLevel(TechLevel.DRACONIC)
                .ingredient(Tags.Items.INGOTS_NETHERITE)
                .ingredient(DEContent.core_wyvern)
                .ingredient(Tags.Items.INGOTS_NETHERITE)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(Tags.Items.INGOTS_NETHERITE)
                .ingredient(DEContent.energy_core_draconic)
                .ingredient(Tags.Items.INGOTS_NETHERITE)
                .build(consumer, folder("tools", DEContent.chestpiece_draconic));

        FusionRecipeBuilder.fusionRecipe(DEContent.chestpiece_chaotic)
                .catalyst(DEContent.chestpiece_draconic)
                .energy(128000000)
                .techLevel(TechLevel.CHAOTIC)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DEContent.core_chaotic)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DEContent.energy_core_chaotic)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .build(consumer, folder("tools", DEContent.chestpiece_chaotic));
    }

    private static void modules(Consumer<IFinishedRecipe> consumer) {
        ShapedRecipeBuilder.shaped(module_core)
                .pattern("IRI")
                .pattern("GDG")
                .pattern("IRI")
                .define('I', INGOTS_IRON)
                .define('R', DUSTS_REDSTONE)
                .define('G', INGOTS_GOLD)
                .define('D', INGOTS_DRACONIUM)
                .unlockedBy("has_ingot_draconium", has(INGOTS_DRACONIUM))
                .save(consumer, folder("modules", module_core));

        //Energy
        ShapedRecipeBuilder.shaped(DEModules.draconiumEnergy.getItem())
                .pattern("###")
                .pattern("ABA")
                .pattern("###")
                .define('#', STORAGE_BLOCKS_REDSTONE)
                .define('A', INGOTS_IRON)
                .define('B', module_core)
                .unlockedBy("has_module_core", has(module_core))
                .save(consumer, folder("modules", DEModules.draconiumEnergy));

        ShapedRecipeBuilder.shaped(DEModules.wyvernEnergy.getItem())
                .pattern("###")
                .pattern("ABA")
                .pattern("###")
                .define('#', INGOTS_DRACONIUM)
                .define('A', DEModules.draconiumEnergy.getItem())
                .define('B', core_draconium)
                .unlockedBy("has_module_core", has(module_core))
                .save(consumer, folder("modules", DEModules.wyvernEnergy));

        ShapedRecipeBuilder.shaped(DEModules.draconicEnergy.getItem())
                .pattern("###")
                .pattern("ABA")
                .pattern("###")
                .define('#', NUGGETS_DRACONIUM_AWAKENED)
                .define('A', DEModules.wyvernEnergy.getItem())
                .define('B', core_wyvern)
                .unlockedBy("has_module_core", has(module_core))
                .save(consumer, folder("modules", DEModules.draconicEnergy));

        ShapedRecipeBuilder.shaped(DEModules.chaoticEnergy.getItem())
                .pattern("CCC")
                .pattern("ABA")
                .pattern("CCC")
//                .define('#', chaos_frag_small)
                .define('A', DEModules.draconicEnergy.getItem())
                .define('B', core_awakened)
                .define('C', chaos_frag_medium)
                .unlockedBy("has_module_core", has(module_core))
                .save(consumer, folder("modules", DEModules.chaoticEnergy));

        //Speed
        ShapedRecipeBuilder.shaped(DEModules.draconiumSpeed.getItem())
                .pattern("#P#")
                .pattern("ABA")
                .pattern("#P#")
                .define('#', INGOTS_IRON)
                .define('A', CLOCK)
                .define('B', module_core)
                .define('P', new NBTIngredient(PotionUtils.setPotion(new ItemStack(POTION), Potions.SWIFTNESS)))
                .unlockedBy("has_module_core", has(module_core))
                .save(consumer, folder("modules", DEModules.draconiumSpeed));

        ShapedRecipeBuilder.shaped(DEModules.wyvernSpeed.getItem())
                .pattern("###")
                .pattern("ABA")
                .pattern("###")
                .define('#', INGOTS_DRACONIUM)
                .define('A', DEModules.draconiumSpeed.getItem())
                .define('B', core_draconium)
                .unlockedBy("has_module_core", has(module_core))
                .save(consumer, folder("modules", DEModules.wyvernSpeed));

        ShapedRecipeBuilder.shaped(DEModules.draconicSpeed.getItem())
                .pattern("###")
                .pattern("ABA")
                .pattern("###")
                .define('#', NUGGETS_DRACONIUM_AWAKENED)
                .define('A', DEModules.wyvernSpeed.getItem())
                .define('B', core_wyvern)
                .unlockedBy("has_module_core", has(module_core))
                .save(consumer, folder("modules", DEModules.draconicSpeed));

        ShapedRecipeBuilder.shaped(DEModules.chaoticSpeed.getItem())
                .pattern("CCC")
                .pattern("ABA")
                .pattern("CCC")
//                .define('#', chaos_frag_small)
                .define('A', DEModules.draconicSpeed.getItem())
                .define('B', core_awakened)
                .define('C', chaos_frag_medium)
                .unlockedBy("has_module_core", has(module_core))
                .save(consumer, folder("modules", DEModules.chaoticSpeed));

        //Damage
        ShapedRecipeBuilder.shaped(DEModules.draconiumDamage.getItem())
                .pattern("IPG")
                .pattern("ABA")
                .pattern("GPI")
                .define('I', INGOTS_IRON)
                .define('P', new NBTIngredient(PotionUtils.setPotion(new ItemStack(POTION), Potions.STRENGTH)))
                .define('G', INGOTS_GOLD)
                .define('A', DUSTS_GLOWSTONE)
                .define('B', module_core)
                .unlockedBy("has_module_core", has(module_core))
                .save(consumer, folder("modules", DEModules.draconiumDamage));

        ShapedRecipeBuilder.shaped(DEModules.wyvernDamage.getItem())
                .pattern("IPI")
                .pattern("ABA")
                .pattern("IPI")
                .define('I', INGOTS_DRACONIUM)
                .define('P', new NBTIngredient(PotionUtils.setPotion(new ItemStack(POTION), Potions.STRONG_STRENGTH)))
                .define('A', DEModules.draconiumDamage.getItem())
                .define('B', core_draconium)
                .unlockedBy("has_module_core", has(module_core))
                .save(consumer, folder("modules", DEModules.wyvernDamage));

        ShapedRecipeBuilder.shaped(DEModules.draconicDamage.getItem())
                .pattern("IPI")
                .pattern("ABA")
                .pattern("IPI")
                .define('I', NUGGETS_DRACONIUM_AWAKENED)
                .define('P', DRAGON_BREATH)
                .define('A', DEModules.wyvernDamage.getItem())
                .define('B', core_wyvern)
                .unlockedBy("has_module_core", has(module_core))
                .save(consumer, folder("modules", DEModules.draconicDamage));

        ShapedRecipeBuilder.shaped(DEModules.chaoticDamage.getItem())
                .pattern("CCC")
                .pattern("ABA")
                .pattern("CCC")
//                .define('I', chaos_frag_small)
                .define('C', chaos_frag_medium)
                .define('A', DEModules.draconicDamage.getItem())
                .define('B', core_awakened)
                .unlockedBy("has_module_core", has(module_core))
                .save(consumer, folder("modules", DEModules.chaoticDamage));

        //AOE
        ShapedRecipeBuilder.shaped(DEModules.draconiumAOE.getItem())
                .pattern("#I#")
                .pattern("ABA")
                .pattern("#I#")
                .define('#', PISTON)
                .define('I', INGOTS_DRACONIUM)
                .define('A', core_draconium)
                .define('B', module_core)
                .unlockedBy("has_module_core", has(module_core))
                .save(consumer, folder("modules", DEModules.draconiumAOE));

        ShapedRecipeBuilder.shaped(DEModules.wyvernAOE.getItem())
                .pattern("#I#")
                .pattern("ABA")
                .pattern("#I#")
                .define('#', INGOTS_DRACONIUM)
                .define('I', NETHERITE_SCRAP)
                .define('A', DEModules.draconiumAOE.getItem())
                .define('B', core_wyvern)
                .unlockedBy("has_module_core", has(module_core))
                .save(consumer, folder("modules", DEModules.wyvernAOE));

        ShapedRecipeBuilder.shaped(DEModules.draconicAOE.getItem())
                .pattern("#I#")
                .pattern("ABA")
                .pattern("#I#")
                .define('#', INGOTS_NETHERITE)
                .define('I', INGOTS_DRACONIUM_AWAKENED)
                .define('A', DEModules.wyvernAOE.getItem())
                .define('B', core_awakened)
                .unlockedBy("has_module_core", has(module_core))
                .save(consumer, folder("modules", DEModules.draconicAOE));

        ShapedRecipeBuilder.shaped(DEModules.chaoticAOE.getItem())
                .pattern("#I#")
                .pattern("ABA")
                .pattern("#I#")
                .define('#', INGOTS_NETHERITE)
                .define('I', INGOTS_DRACONIUM_AWAKENED)
                .define('A', DEModules.draconicAOE.getItem())
                .define('B', core_chaotic)
                .unlockedBy("has_module_core", has(module_core))
                .save(consumer, folder("modules", DEModules.chaoticAOE));

//        //Mining Stability
//        ShapedRecipeBuilder.shaped(DEModules.wyvernMiningStability.getItem())
//                .pattern("#C#")
//                .pattern("ABA")
//                .pattern("#D#")
//                .define('#', INGOTS_DRACONIUM)
//                .define('A', core_draconium)
//                .define('B', module_core)
//                .define('C', PHANTOM_MEMBRANE)
//                .define('D', GOLDEN_PICKAXE)
//                .unlockedBy("has_module_core", has(module_core))
//                .save(consumer, folder("modules", DEModules.wyvernMiningStability));
//
//        ShapedRecipeBuilder.shaped(DEModules.wyvernJunkFilter.getItem())
//                .pattern("#C#")
//                .pattern("ABA")
//                .pattern("#D#")
//                .define('#', INGOTS_DRACONIUM)
//                .define('A', core_draconium)
//                .define('B', module_core)
//                .define('C', LAVA_BUCKET)
//                .define('D', DUSTS_REDSTONE)
//                .unlockedBy("has_module_core", has(module_core))
//                .save(consumer, folder("modules", DEModules.wyvernJunkFilter));

        //Shield Controller
        ShapedRecipeBuilder.shaped(DEModules.wyvernShieldControl.getItem())
                .pattern("#C#")
                .pattern("ABA")
                .pattern("#D#")
                .define('#', GEMS_DIAMOND)
                .define('A', core_wyvern)
                .define('B', module_core)
                .define('C', dragon_heart)
                .define('D', particle_generator)
                .unlockedBy("has_module_core", has(module_core))
                .save(consumer, folder("modules", DEModules.wyvernShieldControl));

        ShapedRecipeBuilder.shaped(DEModules.draconicShieldControl.getItem())
                .pattern("#I#")
                .pattern("ABA")
                .pattern("#I#")
                .define('#', GEMS_EMERALD)
                .define('A', core_awakened)
                .define('B', DEModules.wyvernShieldControl.getItem())
                .define('I', INGOTS_NETHERITE)
                .unlockedBy("has_module_core", has(module_core))
                .save(consumer, folder("modules", DEModules.draconicShieldControl));

        ShapedRecipeBuilder.shaped(DEModules.chaoticShieldControl.getItem())
                .pattern("#I#")
                .pattern("ABA")
                .pattern("#I#")
                .define('#', NETHER_STARS)
                .define('A', core_chaotic)
                .define('B', DEModules.draconicShieldControl.getItem())
                .define('I', INGOTS_NETHERITE)
                .unlockedBy("has_module_core", has(module_core))
                .save(consumer, folder("modules", DEModules.chaoticShieldControl));

        //Shield Capacity
        ShapedRecipeBuilder.shaped(DEModules.wyvernShieldCapacity.getItem())
                .pattern("#I#")
                .pattern("ABA")
                .pattern("#I#")
                .define('#', INGOTS_DRACONIUM)
                .define('A', DUSTS_GLOWSTONE)
                .define('B', module_core)
                .define('I', NETHERITE_SCRAP)
                .unlockedBy("has_wyvern_shield", has(DEModules.wyvernShieldControl.getItem()))
                .save(consumer, folder("modules", DEModules.wyvernShieldCapacity));

        ShapedRecipeBuilder.shaped(DEModules.draconicShieldCapacity.getItem())
                .pattern("#C#")
                .pattern("ABA")
                .pattern("#D#")
                .define('#', INGOTS_NETHERITE)
                .define('A', INGOTS_DRACONIUM_AWAKENED)
                .define('B', DEModules.wyvernShieldCapacity.getItem())
                .define('C', core_draconium)
                .define('D', core_wyvern)
                .unlockedBy("has_draconic_shield", has(DEModules.draconicShieldControl.getItem()))
                .save(consumer, folder("modules", DEModules.draconicShieldCapacity));

        ShapedRecipeBuilder.shaped(DEModules.chaoticShieldCapacity.getItem())
                .pattern("#C#")
                .pattern("ABA")
                .pattern("#D#")
                .define('#', INGOTS_DRACONIUM_AWAKENED)
                .define('A', chaos_frag_large)
                .define('B', DEModules.draconicShieldCapacity.getItem())
                .define('C', core_wyvern)
                .define('D', core_chaotic)
                .unlockedBy("has_chaotic_shield", has(DEModules.chaoticShieldControl.getItem()))
                .save(consumer, folder("modules", DEModules.chaoticShieldCapacity));

        //Shield Capacity XL
        ShapedRecipeBuilder.shaped(DEModules.wyvernLargeShieldCapacity.getItem())
                .pattern("#A#")
                .pattern("A#A")
                .pattern("#A#")
                .define('#', DEModules.wyvernShieldCapacity.getItem())
                .define('A', core_draconium)
                .unlockedBy("has_wyvern_shield", has(DEModules.wyvernShieldControl.getItem()))
                .save(consumer, folder("modules", DEModules.wyvernLargeShieldCapacity));

        ShapedRecipeBuilder.shaped(DEModules.draconicLargeShieldCapacity.getItem())
                .pattern("#A#")
                .pattern("A#A")
                .pattern("#A#")
                .define('#', DEModules.draconicShieldCapacity.getItem())
                .define('A', core_draconium)
                .unlockedBy("has_draconic_shield", has(DEModules.draconicShieldControl.getItem()))
                .save(consumer, folder("modules", DEModules.draconicLargeShieldCapacity));

        ShapedRecipeBuilder.shaped(DEModules.chaoticLargeShieldCapacity.getItem())
                .pattern("#A#")
                .pattern("A#A")
                .pattern("#A#")
                .define('#', DEModules.chaoticShieldCapacity.getItem())
                .define('A', core_draconium)
                .unlockedBy("has_chaotic_shield", has(DEModules.chaoticShieldControl.getItem()))
                .save(consumer, folder("modules", DEModules.chaoticLargeShieldCapacity));

        ShapelessRecipeBuilder.shapeless(DEModules.wyvernShieldCapacity.getItem(), 5)
                .requires(DEModules.wyvernLargeShieldCapacity.getItem())
                .unlockedBy("has_wyvern_shield", has(DEModules.wyvernShieldControl.getItem()))
                .save(consumer, DraconicEvolution.MODID + ":modules/uncraft_" + DEModules.wyvernShieldCapacity.getRegistryName().getPath());

        ShapelessRecipeBuilder.shapeless(DEModules.draconicShieldCapacity.getItem(), 5)
                .requires(DEModules.draconicLargeShieldCapacity.getItem())
                .unlockedBy("has_draconic_shield", has(DEModules.draconicShieldControl.getItem()))
                .save(consumer, DraconicEvolution.MODID + ":modules/uncraft_" + DEModules.draconicShieldCapacity.getRegistryName().getPath());

        ShapelessRecipeBuilder.shapeless(DEModules.chaoticShieldCapacity.getItem(), 5)
                .requires(DEModules.chaoticLargeShieldCapacity.getItem())
                .unlockedBy("has_chaotic_shield", has(DEModules.chaoticShieldControl.getItem()))
                .save(consumer, DraconicEvolution.MODID + ":modules/uncraft_" + DEModules.chaoticShieldCapacity.getRegistryName().getPath());

        //Shield Recovery
        ShapedRecipeBuilder.shaped(DEModules.wyvernShieldRecovery.getItem())
                .pattern("#I#")
                .pattern("ABA")
                .pattern("#I#")
                .define('#', INGOTS_DRACONIUM)
                .define('A', DUSTS_REDSTONE)
                .define('B', module_core)
                .define('I', NETHERITE_SCRAP)
                .unlockedBy("has_wyvern_shield", has(DEModules.wyvernShieldControl.getItem()))
                .save(consumer, folder("modules", DEModules.wyvernShieldRecovery));

        ShapedRecipeBuilder.shaped(DEModules.draconicShieldRecovery.getItem())
                .pattern("#C#")
                .pattern("ABA")
                .pattern("#D#")
                .define('#', INGOTS_NETHERITE)
                .define('A', INGOTS_DRACONIUM_AWAKENED)
                .define('B', DEModules.wyvernShieldRecovery.getItem())
                .define('C', core_draconium)
                .define('D', core_wyvern)
                .unlockedBy("has_draconic_shield", has(DEModules.draconicShieldControl.getItem()))
                .save(consumer, folder("modules", DEModules.draconicShieldRecovery));

        ShapedRecipeBuilder.shaped(DEModules.chaoticShieldRecovery.getItem())
                .pattern("#C#")
                .pattern("ABA")
                .pattern("#D#")
                .define('#', INGOTS_DRACONIUM_AWAKENED)
                .define('A', chaos_frag_large)
                .define('B', DEModules.draconicShieldRecovery.getItem())
                .define('C', core_wyvern)
                .define('D', core_chaotic)
                .unlockedBy("has_chaotic_shield", has(DEModules.chaoticShieldControl.getItem()))
                .save(consumer, folder("modules", DEModules.chaoticShieldRecovery));

        //Flight
        ShapedRecipeBuilder.shaped(DEModules.wyvernFlight.getItem())
                .pattern("#C#")
                .pattern("ABA")
                .pattern("#D#")
                .define('#', INGOTS_DRACONIUM)
                .define('C', ELYTRA)
                .define('A', core_draconium)
                .define('B', module_core)
                .define('D', FIREWORK_ROCKET)
                .unlockedBy("has_module_core", has(module_core))
                .save(consumer, folder("modules", DEModules.wyvernFlight));

        ShapedRecipeBuilder.shaped(DEModules.draconicFlight.getItem())
                .pattern("#C#")
                .pattern("ABA")
                .pattern("#D#")
                .define('#', INGOTS_DRACONIUM_AWAKENED)
                .define('A', core_wyvern)
                .define('B', DEModules.wyvernFlight.getItem())
                .define('C', new NBTIngredient(PotionUtils.setPotion(new ItemStack(POTION), Potions.SLOW_FALLING)))
                .define('D', FIREWORK_ROCKET)
                .unlockedBy("has_module_core", has(module_core))
                .save(consumer, folder("modules", DEModules.draconicFlight));

        ShapedRecipeBuilder.shaped(DEModules.chaoticFlight.getItem())
                .pattern("#C#")
                .pattern("ABA")
                .pattern("#C#")
                .define('#', new NBTIngredient(PotionUtils.setPotion(new ItemStack(POTION), Potions.STRONG_SWIFTNESS)))
                .define('A', core_awakened)
                .define('B', DEModules.draconicFlight.getItem())
                .define('C', chaos_frag_large)
                .unlockedBy("has_module_core", has(module_core))
                .save(consumer, folder("modules", DEModules.chaoticFlight));

        //Last Stand
        ShapedRecipeBuilder.shaped(DEModules.wyvernLastStand.getItem())
                .pattern("#C#")
                .pattern("ABA")
                .pattern("#D#")
                .define('#', INGOTS_DRACONIUM)
                .define('A', core_draconium)
                .define('B', module_core)
                .define('C', TOTEM_OF_UNDYING)
                .define('D', DEModules.wyvernShieldCapacity.getItem())
                .unlockedBy("has_module_core", has(module_core))
                .save(consumer, folder("modules", DEModules.wyvernLastStand));

        ShapedRecipeBuilder.shaped(DEModules.draconicLastStand.getItem())
                .pattern("#C#")
                .pattern("ABA")
                .pattern("#D#")
                .define('#', INGOTS_DRACONIUM_AWAKENED)
                .define('A', core_wyvern)
                .define('B', DEModules.wyvernLastStand.getItem())
                .define('C', new NBTIngredient(PotionUtils.setPotion(new ItemStack(POTION), Potions.STRONG_HEALING)))
                .define('D', DEModules.draconicShieldCapacity.getItem())
                .unlockedBy("has_module_core", has(module_core))
                .save(consumer, folder("modules", DEModules.draconicLastStand));

        ShapedRecipeBuilder.shaped(DEModules.chaoticLastStand.getItem())
                .pattern("#C#")
                .pattern("ABA")
                .pattern("#D#")
                .define('#', chaos_frag_medium)
                .define('A', core_awakened)
                .define('B', DEModules.draconicLastStand.getItem())
                .define('C', ENCHANTED_GOLDEN_APPLE)
                .define('D', DEModules.chaoticShieldCapacity.getItem())
                .unlockedBy("has_module_core", has(module_core))
                .save(consumer, folder("modules", DEModules.chaoticLastStand));

        //Auto Feed
        ShapedRecipeBuilder.shaped(DEModules.draconiumAutoFeed.getItem())
                .pattern("#C#")
                .pattern("ABA")
                .pattern("#D#")
                .define('#', INGOTS_IRON)
                .define('A', COOKIE)
                .define('B', module_core)
                .define('C', GOLDEN_APPLE)
                .define('D', core_draconium)
                .unlockedBy("has_module_core", has(module_core))
                .save(consumer, folder("modules", DEModules.draconiumAutoFeed));

        ShapedRecipeBuilder.shaped(DEModules.wyvernAutoFeed.getItem())
                .pattern("#C#")
                .pattern("ABA")
                .pattern("#C#")
                .define('#', INGOTS_DRACONIUM)
                .define('A', core_draconium)
                .define('B', DEModules.draconiumAutoFeed.getItem())
                .define('C', COOKIE)
                .unlockedBy("has_module_core", has(module_core))
                .save(consumer, folder("modules", DEModules.wyvernAutoFeed));

        ShapedRecipeBuilder.shaped(DEModules.draconicAutoFeed.getItem())
                .pattern("#C#")
                .pattern("ABA")
                .pattern("#C#")
                .define('#', NUGGETS_DRACONIUM_AWAKENED)
                .define('A', core_draconium)
                .define('B', DEModules.wyvernAutoFeed.getItem())
                .define('C', COOKIE)
                .unlockedBy("has_module_core", has(module_core))
                .save(consumer, folder("modules", DEModules.draconicAutoFeed));

//        //Night Vision
//        ShapedRecipeBuilder.shaped(DEModules.wyvernNightVision.getItem())
//                .pattern("#P#")
//                .pattern("ABA")
//                .pattern("#P#")
//                .define('#', INGOTS_DRACONIUM)
//                .define('A', core_draconium)
//                .define('B', module_core)
//                .define('P', new NBTIngredient(PotionUtils.setPotion(new ItemStack(POTION), Potions.NIGHT_VISION)))
//                .unlockedBy("has_module_core", has(module_core))
//                .save(consumer, folder("modules", DEModules.wyvernNightVision));

        //Jump Boost
        ShapedRecipeBuilder.shaped(DEModules.draconiumJump.getItem())
                .pattern("CPD")
                .pattern("ABA")
                .pattern("DPC")
                .define('A', DUSTS_GLOWSTONE)
                .define('B', module_core)
                .define('C', INGOTS_IRON)
                .define('D', INGOTS_GOLD)
                .define('P', new NBTIngredient(PotionUtils.setPotion(new ItemStack(POTION), Potions.LEAPING)))
                .unlockedBy("has_module_core", has(module_core))
                .save(consumer, folder("modules", DEModules.draconiumJump));

        ShapedRecipeBuilder.shaped(DEModules.wyvernJump.getItem())
                .pattern("#P#")
                .pattern("ABA")
                .pattern("#P#")
                .define('#', INGOTS_DRACONIUM)
                .define('B', core_draconium)
                .define('A', DEModules.draconiumJump.getItem())
                .define('P', new NBTIngredient(PotionUtils.setPotion(new ItemStack(POTION), Potions.STRONG_LEAPING)))
                .unlockedBy("has_module_core", has(module_core))
                .save(consumer, folder("modules", DEModules.wyvernJump));

        ShapedRecipeBuilder.shaped(DEModules.draconicJump.getItem())
                .pattern("###")
                .pattern("ABA")
                .pattern("###")
                .define('#', NUGGETS_DRACONIUM_AWAKENED)
                .define('B', core_wyvern)
                .define('A', DEModules.wyvernJump.getItem())
                .unlockedBy("has_module_core", has(module_core))
                .save(consumer, folder("modules", DEModules.draconicJump));

        ShapedRecipeBuilder.shaped(DEModules.chaoticJump.getItem())
                .pattern("CCC")
                .pattern("ABA")
                .pattern("CCC")
//                .define('#', chaos_frag_small)
                .define('B', core_awakened)
                .define('A', DEModules.draconicJump.getItem())
                .define('C', chaos_frag_medium)
                .unlockedBy("has_module_core", has(module_core))
                .save(consumer, folder("modules", DEModules.chaoticJump));

//        //Aqua
//        ShapedRecipeBuilder.shaped(DEModules.wyvernAquaAdapt.getItem())
//                .pattern("#C#")
//                .pattern("ABA")
//                .pattern("#D#")
//                .define('#', INGOTS_DRACONIUM)
//                .define('A', core_draconium)
//                .define('B', module_core)
//                .define('C', HEART_OF_THE_SEA)
//                .define('D', IRON_PICKAXE)
//                .unlockedBy("has_module_core", has(module_core))
//                .save(consumer, folder("modules", DEModules.wyvernAquaAdapt));

        //Hill Step
        ShapedRecipeBuilder.shaped(DEModules.wyvernHillStep.getItem())
                .pattern("#C#")
                .pattern("ABA")
                .pattern("D#D")
                .define('#', INGOTS_DRACONIUM)
                .define('A', core_draconium)
                .define('B', module_core)
                .define('C', GOLDEN_BOOTS)
                .define('D', PISTON)
                .unlockedBy("has_module_core", has(module_core))
                .save(consumer, folder("modules", DEModules.wyvernHillStep));

        //Arrow Velocity
        ShapedRecipeBuilder.shaped(DEModules.wyvernProjVelocity.getItem())
                .pattern("#P#")
                .pattern("ABA")
                .pattern("#C#")
                .define('#', INGOTS_DRACONIUM)
                .define('C', ARROWS)
                .define('B', module_core)
                .define('A', core_draconium)
                .define('P', new NBTIngredient(PotionUtils.setPotion(new ItemStack(SPLASH_POTION), Potions.STRONG_SWIFTNESS)))
                .unlockedBy("has_module_core", has(module_core))
                .save(consumer, folder("modules", DEModules.wyvernProjVelocity));

        ShapedRecipeBuilder.shaped(DEModules.draconicProjVelocity.getItem())
                .pattern("###")
                .pattern("ABA")
                .pattern("###")
                .define('#', NUGGETS_DRACONIUM_AWAKENED)
                .define('B', core_wyvern)
                .define('A', DEModules.wyvernProjVelocity.getItem())
                .unlockedBy("has_module_core", has(module_core))
                .save(consumer, folder("modules", DEModules.draconicProjVelocity));

        ShapedRecipeBuilder.shaped(DEModules.chaoticProjVelocity.getItem())
                .pattern("CCC")
                .pattern("ABA")
                .pattern("CCC")
//                .define('#', chaos_frag_small)
                .define('B', core_awakened)
                .define('A', DEModules.draconicProjVelocity.getItem())
                .define('C', chaos_frag_medium)
                .unlockedBy("has_module_core", has(module_core))
                .save(consumer, folder("modules", DEModules.chaoticProjVelocity));

        //Arrow Accuracy
        ShapedRecipeBuilder.shaped(DEModules.wyvernProjAccuracy.getItem())
                .pattern("#P#")
                .pattern("ABA")
                .pattern("#C#")
                .define('#', INGOTS_DRACONIUM)
                .define('C', ARROWS)
                .define('B', module_core)
                .define('A', core_draconium)
                .define('P', TARGET)
                .unlockedBy("has_module_core", has(module_core))
                .save(consumer, folder("modules", DEModules.wyvernProjAccuracy));

        ShapedRecipeBuilder.shaped(DEModules.draconicProjAccuracy.getItem())
                .pattern("###")
                .pattern("ABA")
                .pattern("###")
                .define('#', NUGGETS_DRACONIUM_AWAKENED)
                .define('B', core_wyvern)
                .define('A', DEModules.wyvernProjAccuracy.getItem())
                .unlockedBy("has_module_core", has(module_core))
                .save(consumer, folder("modules", DEModules.draconicProjAccuracy));

        ShapedRecipeBuilder.shaped(DEModules.chaoticProjAccuracy.getItem())
                .pattern("CCC")
                .pattern("ABA")
                .pattern("CCC")
//                .define('#', chaos_frag_small)
                .define('B', core_awakened)
                .define('A', DEModules.draconicProjAccuracy.getItem())
                .define('C', chaos_frag_medium)
                .unlockedBy("has_module_core", has(module_core))
                .save(consumer, folder("modules", DEModules.chaoticProjAccuracy));

        //Arrow Penetration
        ShapedRecipeBuilder.shaped(DEModules.wyvernProjPenetration.getItem())
                .pattern("#P#")
                .pattern("ABA")
                .pattern("#C#")
                .define('#', INGOTS_DRACONIUM)
                .define('C', ARROWS)
                .define('B', module_core)
                .define('A', core_draconium)
                .define('P', SHIELD)
                .unlockedBy("has_module_core", has(module_core))
                .save(consumer, folder("modules", DEModules.wyvernProjPenetration));

        ShapedRecipeBuilder.shaped(DEModules.draconicProjPenetration.getItem())
                .pattern("###")
                .pattern("ABA")
                .pattern("###")
                .define('#', NUGGETS_DRACONIUM_AWAKENED)
                .define('B', core_wyvern)
                .define('A', DEModules.wyvernProjPenetration.getItem())
                .unlockedBy("has_module_core", has(module_core))
                .save(consumer, folder("modules", DEModules.draconicProjPenetration));

        ShapedRecipeBuilder.shaped(DEModules.chaoticProjPenetration.getItem())
                .pattern("CCC")
                .pattern("ABA")
                .pattern("CCC")
//                .define('#', chaos_frag_small)
                .define('B', core_awakened)
                .define('A', DEModules.draconicProjPenetration.getItem())
                .define('C', chaos_frag_medium)
                .unlockedBy("has_module_core", has(module_core))
                .save(consumer, folder("modules", DEModules.chaoticProjPenetration));

        //Arrow Damage
        ShapedRecipeBuilder.shaped(DEModules.wyvernProjDamage.getItem())
                .pattern("#P#")
                .pattern("ABA")
                .pattern("#C#")
                .define('#', INGOTS_DRACONIUM)
                .define('C', ARROWS)
                .define('B', module_core)
                .define('A', core_draconium)
                .define('P', new NBTIngredient(PotionUtils.setPotion(new ItemStack(SPLASH_POTION), Potions.STRONG_STRENGTH)))
                .unlockedBy("has_module_core", has(module_core))
                .save(consumer, folder("modules", DEModules.wyvernProjDamage));

        ShapedRecipeBuilder.shaped(DEModules.draconicProjDamage.getItem())
                .pattern("###")
                .pattern("ABA")
                .pattern("###")
                .define('#', NUGGETS_DRACONIUM_AWAKENED)
                .define('B', core_wyvern)
                .define('A', DEModules.wyvernProjDamage.getItem())
                .unlockedBy("has_module_core", has(module_core))
                .save(consumer, folder("modules", DEModules.draconicProjDamage));

        ShapedRecipeBuilder.shaped(DEModules.chaoticProjDamage.getItem())
                .pattern("CCC")
                .pattern("ABA")
                .pattern("CCC")
//                .define('#', chaos_frag_small)
                .define('B', core_awakened)
                .define('A', DEModules.draconicProjDamage.getItem())
                .define('C', chaos_frag_medium)
                .unlockedBy("has_module_core", has(module_core))
                .save(consumer, folder("modules", DEModules.chaoticProjDamage));

        //Arrow Anti Grav
        ShapedRecipeBuilder.shaped(DEModules.wyvernProjGravComp.getItem())
                .pattern("#P#")
                .pattern("ABA")
                .pattern("#C#")
                .define('#', INGOTS_DRACONIUM)
                .define('C', ARROWS)
                .define('B', module_core)
                .define('A', core_draconium)
                .define('P', new NBTIngredient(PotionUtils.setPotion(new ItemStack(SPLASH_POTION), Potions.LONG_SLOW_FALLING)))
                .unlockedBy("has_module_core", has(module_core))
                .save(consumer, folder("modules", DEModules.wyvernProjGravComp));

        ShapedRecipeBuilder.shaped(DEModules.draconicProjGravComp.getItem())
                .pattern("###")
                .pattern("ABA")
                .pattern("###")
                .define('#', NUGGETS_DRACONIUM_AWAKENED)
                .define('B', core_wyvern)
                .define('A', DEModules.wyvernProjGravComp.getItem())
                .unlockedBy("has_module_core", has(module_core))
                .save(consumer, folder("modules", DEModules.draconicProjGravComp));

        ShapedRecipeBuilder.shaped(DEModules.chaoticProjGravComp.getItem())
                .pattern("CCC")
                .pattern("ABA")
                .pattern("CCC")
//                .define('#', chaos_frag_small)
                .define('B', core_awakened)
                .define('A', DEModules.draconicProjGravComp.getItem())
                .define('C', chaos_frag_medium)
                .unlockedBy("has_module_core", has(module_core))
                .save(consumer, folder("modules", DEModules.chaoticProjGravComp));

        // Auto Fire
        ShapedRecipeBuilder.shaped(DEModules.wyvernAutoFire.getItem())
                .pattern("#P#")
                .pattern("ABA")
                .pattern("#C#")
                .define('#', INGOTS_DRACONIUM)
                .define('C', BOW)
                .define('B', module_core)
                .define('A', core_draconium)
                .define('P', CLOCK)
                .unlockedBy("has_module_core", has(module_core))
                .save(consumer, folder("modules", DEModules.wyvernAutoFire));

        // Projectile Anti Immunity
        ShapedRecipeBuilder.shaped(DEModules.draconicProjAntiImmune.getItem())
                .pattern("#P#")
                .pattern("ABA")
                .pattern("#C#")
                .define('#', NUGGETS_DRACONIUM_AWAKENED)
                .define('C', ENDER_PEARLS)
                .define('B', module_core)
                .define('A', core_wyvern)
                .define('P', WITHER_SKELETON_SKULL)
                .unlockedBy("has_module_core", has(module_core))
                .save(consumer, folder("modules", DEModules.draconicProjAntiImmune));
    }


    private static void unsorted(Consumer<IFinishedRecipe> consumer) {

        ShapedRecipeBuilder.shaped(infused_obsidian)
                .pattern("ABA")
                .pattern("BCB")
                .pattern("ABA")
                .define('A', BLAZE_POWDER)
                .define('B', Tags.Items.OBSIDIAN)
                .define('C', DUSTS_DRACONIUM)
                .unlockedBy("has_dust_draconium", has(dust_draconium))
                .save(consumer);


//        FusionRecipeBuilder.fusionRecipe(ender_energy_manipulator).catalyst(SKELETON_SKULL).energy(12000000).techLevel(WYVERN).ingredient(ENDER_EYE).ingredient(ENDER_EYE).ingredient(ENDER_EYE).ingredient(ENDER_EYE).ingredient(ENDER_EYE).ingredient(ENDER_EYE).ingredient(core_draconium).ingredient(core_wyvern).ingredient(core_draconium).ingredient(ENDER_EYE).build(consumer);


//        ShapedRecipeBuilder.shapedRecipe(dislocator_receptacle).patternLine("ABA").patternLine(" C ").patternLine("A A").key('A', INGOTS_IRON).key('B', core_draconium).key('C', infused_obsidian).build(consumer);
//        ShapedRecipeBuilder.shapedRecipe(dislocator_pedestal).patternLine(" A ").patternLine(" B ").patternLine("CDC").key('A', STONE_PRESSURE_PLATE).key('B', Tags.Items.STONE).key('C', STONE_SLAB).key('D', BLAZE_POWDER).build(consumer);


//        ShapedRecipeBuilder.shapedRecipe(rain_sensor).patternLine(" A ").patternLine("BCB").patternLine("DDD").key('A', BUCKET).key('B', DUSTS_REDSTONE).key('C', STONE_PRESSURE_PLATE).key('D', STONE_SLAB).build(consumer);
//        ShapedRecipeBuilder.shapedRecipe(disenchanter).patternLine("ABA").patternLine("CDC").patternLine("EEE").key('A', GEMS_EMERALD).key('B', core_draconium).key('C', ENCHANTED_BOOK).key('D', ENCHANTING_TABLE).key('E', BOOKSHELF).build(consumer);
//        ShapedRecipeBuilder.shapedRecipe(celestial_manipulator).patternLine("ABA").patternLine("CDC").patternLine("EFE").key('A', STORAGE_BLOCKS_REDSTONE).key('B', CLOCK).key('C', INGOTS_DRACONIUM).key('D', DRAGON_EGG).key('E', INGOTS_IRON).key('F', core_wyvern).build(consumer);
//
//        ShapedRecipeBuilder.shapedRecipe(entity_detector).patternLine("ABA").patternLine("CDC").patternLine("EFE").key('A', GEMS_LAPIS).key('B', ENDER_EYE).key('C', DUSTS_REDSTONE).key('D', INGOTS_DRACONIUM).key('E', INGOTS_IRON).key('F', core_draconium).build(consumer);
//        ShapedRecipeBuilder.shapedRecipe(entity_detector_advanced).patternLine("ABA").patternLine("CDC").patternLine("EFE").key('A', STORAGE_BLOCKS_REDSTONE).key('B', SKELETON_SKULL).key('C', STORAGE_BLOCKS_LAPIS).key('D', GEMS_DIAMOND).key('E', INGOTS_DRACONIUM).key('F', entity_detector).build(consumer);
        ShapedRecipeBuilder.shaped(fluid_gate)
                .pattern("ABA")
                .pattern("CDC")
                .pattern("AEA")
                .define('A', INGOTS_IRON)
                .define('B', potentiometer)
                .define('C', BUCKET)
                .define('D', core_draconium)
                .define('E', COMPARATOR)
                .unlockedBy("has_dust_draconium", has(dust_draconium))
                .save(consumer);

        ShapedRecipeBuilder.shaped(flux_gate)
                .pattern("ABA")
                .pattern("CDC")
                .pattern("AEA")
                .define('A', INGOTS_IRON)
                .define('B', potentiometer)
                .define('C', STORAGE_BLOCKS_REDSTONE)
                .define('D', core_draconium)
                .define('E', COMPARATOR)
                .unlockedBy("has_dust_draconium", has(dust_draconium))
                .save(consumer);

        ShapedRecipeBuilder.shaped(dislocation_inhibitor)
                .pattern("AAA")
                .pattern("BCB")
                .pattern("AAA")
                .define('A', INGOTS_IRON)
                .define('B', IRON_BARS)
                .define('C', magnet)
                .unlockedBy("has_magnet", has(magnet))
                .save(consumer);


//        ShapedRecipeBuilder.shapedRecipe(info_tablet).patternLine("AAA").patternLine("ABA").patternLine("AAA").key('A', Tags.Items.STONE).key('B', DUSTS_DRACONIUM).build(consumer);


    }

    private static void compress3x3(IItemProvider output, IItemProvider input, Consumer<IFinishedRecipe> consumer) {
        ResourceLocation name = output.asItem().getRegistryName();
        ShapedRecipeBuilder.shaped(output)
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .define('#', input)
                .unlockedBy("has_" + input.asItem().getRegistryName().getPath(), has(input))
                .save(consumer, new ResourceLocation(name.getNamespace(), "compress/" + name.getPath()));
    }

    private static void compress3x3(IItemProvider output, ITag<Item> input, String inputName, Consumer<IFinishedRecipe> consumer) {
        ResourceLocation name = output.asItem().getRegistryName();
        ShapedRecipeBuilder.shaped(output)
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .define('#', input)
                .unlockedBy("has_" + inputName, has(input))
                .save(consumer, new ResourceLocation(name.getNamespace(), "compress/" + name.getPath()));
    }

    private static void compress2x2(IItemProvider output, IItemProvider input, Consumer<IFinishedRecipe> consumer) {
        ResourceLocation name = output.asItem().getRegistryName();
        ShapedRecipeBuilder.shaped(output)
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .define('#', input)
                .unlockedBy("has_" + input.asItem().getRegistryName().getPath(), has(input))
                .save(consumer, new ResourceLocation(name.getNamespace(), "compress/" + name.getPath()));
    }

    private static void deCompress(IItemProvider output, int count, IItemProvider from, Consumer<IFinishedRecipe> consumer) {
        ResourceLocation name = output.asItem().getRegistryName();
        ShapelessRecipeBuilder.shapeless(output, count)
                .requires(from)
                .unlockedBy("has_" + from.asItem().getRegistryName().getPath(), has(from))
                .save(consumer, new ResourceLocation(name.getNamespace(), "decompress/" + name.getPath()));
    }

    private static void deCompress(IItemProvider output, int count, ITag<Item> from, String hasName, Consumer<IFinishedRecipe> consumer) {
        ResourceLocation name = output.asItem().getRegistryName();
        ShapelessRecipeBuilder.shapeless(output, count)
                .requires(from)
                .unlockedBy("has_" + hasName, has(from))
                .save(consumer, new ResourceLocation(name.getNamespace(), "decompress/" + name.getPath()));
    }

    private static void deCompress(IItemProvider output, IItemProvider from, Consumer<IFinishedRecipe> consumer) {
        deCompress(output, 9, from, consumer);
    }

    private static void deCompress(IItemProvider output, ITag<Item> from, String hasName, Consumer<IFinishedRecipe> consumer) {
        deCompress(output, 9, from, hasName, consumer);
    }

    public static String folder(String folder, IForgeRegistryEntry<?> key) {
        return DraconicEvolution.MODID + ":" + folder + "/" + key.getRegistryName().getPath();
    }

    public static String folder(String folder, String name) {
        return DraconicEvolution.MODID + ":" + folder + "/" + name;
    }

    @Override
    public void run(DirectoryCache cache) throws IOException {
        super.run(cache);
    }

    public static class NBTIngredient extends net.minecraftforge.common.crafting.NBTIngredient {
        public NBTIngredient(ItemStack stack) {
            super(stack);
        }
    }
}
