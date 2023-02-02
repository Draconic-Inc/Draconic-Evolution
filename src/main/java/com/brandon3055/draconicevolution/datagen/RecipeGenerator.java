package com.brandon3055.draconicevolution.datagen;

import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.init.DEContent;
import com.brandon3055.draconicevolution.init.DEModules;
import com.brandon3055.draconicevolution.init.DETags;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.HashCache;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.util.function.Consumer;

/**
 * Created by brandon3055 on 1/12/20
 */
public class RecipeGenerator extends RecipeProvider {

    public RecipeGenerator(DataGenerator generatorIn) {
        super(generatorIn);
    }

    @Override
    protected void buildCraftingRecipes(Consumer<FinishedRecipe> consumer) {
        components(consumer);

        compressDecompress(consumer);
        machines(consumer);
        energy(consumer);
        tools(consumer);
        equipment(consumer);
        modules(consumer);
        unsorted(consumer);

        FusionRecipeBuilder.fusionRecipe(DEContent.block_draconium_awakened, 4)
                .catalyst(4, DETags.Items.STORAGE_BLOCKS_DRACONIUM)
                .energy(50000000)
                .techLevel(TechLevel.WYVERN)
                .ingredient(DEContent.core_draconium)
                .ingredient(DEContent.core_draconium)
                .ingredient(DEContent.core_draconium)
                .ingredient(DEContent.dragon_heart)
                .ingredient(DEContent.core_draconium)
                .ingredient(DEContent.core_draconium)
                .ingredient(DEContent.core_draconium)
                .build(consumer);
    }

    private static void components(Consumer<FinishedRecipe> consumer) {

        SimpleCookingRecipeBuilder.smelting(Ingredient.of(DETags.Items.DUSTS_DRACONIUM), DEContent.ingot_draconium, 0, 200).unlockedBy("has_draconium_dust", has(DETags.Items.DUSTS_DRACONIUM)).save(consumer, folder("components", DEContent.ingot_draconium));
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(DETags.Items.ORES_DRACONIUM), DEContent.ingot_draconium, 1, 200).unlockedBy("has_draconium_ore", has(DETags.Items.ORES_DRACONIUM)).save(consumer);

        ShapedRecipeBuilder.shaped(DEContent.core_draconium)
                .pattern("ABA")
                .pattern("BCB")
                .pattern("ABA")
                .define('A', DETags.Items.INGOTS_DRACONIUM)
                .define('B', Tags.Items.INGOTS_GOLD)
                .define('C', Tags.Items.GEMS_DIAMOND)
                .unlockedBy("has_draconium", has(DEContent.ingot_draconium))
                .save(consumer, folder("components", DEContent.core_draconium));

        ShapedRecipeBuilder.shaped(DEContent.core_wyvern)
                .pattern("ABA")
                .pattern("BCB")
                .pattern("ABA")
                .define('A', DETags.Items.INGOTS_DRACONIUM)
                .define('B', DEContent.core_draconium)
                .define('C', Tags.Items.NETHER_STARS)
                .unlockedBy("has_core_draconium", has(DEContent.core_draconium))
                .save(consumer, folder("components", DEContent.core_wyvern));

        FusionRecipeBuilder.fusionRecipe(DEContent.core_awakened)
                .catalyst(Tags.Items.NETHER_STARS)
                .energy(1000000)
                .techLevel(TechLevel.WYVERN)
                .ingredient(DEContent.core_wyvern)
                .ingredient(DEContent.core_wyvern)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DEContent.core_wyvern)
                .ingredient(DEContent.core_wyvern)
                .build(consumer, folder("components", DEContent.core_awakened));

        FusionRecipeBuilder.fusionRecipe(DEContent.core_chaotic)
                .catalyst(DEContent.chaos_frag_large)
                .energy(100000000)
                .techLevel(TechLevel.DRACONIC)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DEContent.core_awakened)
                .ingredient(DEContent.core_awakened)
                .ingredient(DEContent.chaos_frag_large)
                .ingredient(DEContent.chaos_frag_large)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DEContent.core_awakened)
                .ingredient(DEContent.core_awakened)
                .ingredient(DEContent.chaos_frag_large)
                .ingredient(DEContent.chaos_frag_large)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .build(consumer, folder("components", DEContent.core_chaotic));

        ShapedRecipeBuilder.shaped(DEContent.energy_core_wyvern)
                .pattern("ABA")
                .pattern("BCB")
                .pattern("ABA")
                .define('A', DETags.Items.INGOTS_DRACONIUM)
                .define('B', Tags.Items.STORAGE_BLOCKS_REDSTONE)
                .define('C', DEContent.core_draconium)
                .unlockedBy("has_core_draconium", has(DEContent.core_draconium))
                .save(consumer, folder("components", DEContent.energy_core_wyvern));

        ShapedRecipeBuilder.shaped(DEContent.energy_core_draconic)
                .pattern("ABA")
                .pattern("BCB")
                .pattern("ABA")
                .define('A', DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .define('B', DEContent.energy_core_wyvern)
                .define('C', DEContent.core_wyvern)
                .unlockedBy("has_core_wyvern", has(DEContent.core_wyvern))
                .save(consumer, folder("components", DEContent.energy_core_draconic));

        ShapedRecipeBuilder.shaped(DEContent.energy_core_chaotic)
                .pattern("ABA")
                .pattern("BCB")
                .pattern("ABA")
                .define('A', DEContent.chaos_frag_medium)
                .define('B', DEContent.energy_core_draconic)
                .define('C', DEContent.core_awakened)
                .unlockedBy("has_core_awakened", has(DEContent.core_awakened))
                .save(consumer, folder("components", DEContent.energy_core_chaotic));
    }

    private static void compressDecompress(Consumer<FinishedRecipe> consumer) {
        compress3x3(DEContent.ingot_draconium, DETags.Items.NUGGETS_DRACONIUM, "nugget_draconium", consumer);
        compress3x3(DEContent.ingot_draconium_awakened, DETags.Items.NUGGETS_DRACONIUM_AWAKENED, "nugget_draconium_awakened", consumer);
        compress3x3(DEContent.block_draconium, DETags.Items.INGOTS_DRACONIUM, "ingot_draconium", consumer);
        compress3x3(DEContent.block_draconium_awakened, DETags.Items.INGOTS_DRACONIUM_AWAKENED, "ingot_draconium_awakened", consumer);

        deCompress(DEContent.nugget_draconium, DETags.Items.INGOTS_DRACONIUM, "ingot_draconium", consumer);
        deCompress(DEContent.nugget_draconium_awakened, DETags.Items.INGOTS_DRACONIUM_AWAKENED, "ingot_draconium_awakened", consumer);
        deCompress(DEContent.ingot_draconium, DETags.Items.STORAGE_BLOCKS_DRACONIUM, "block_draconium", consumer);
        deCompress(DEContent.ingot_draconium_awakened, DETags.Items.STORAGE_BLOCKS_DRACONIUM_AWAKENED, "block_draconium_awakened", consumer);

        deCompress(DEContent.chaos_frag_large, DEContent.chaos_shard, consumer);
        deCompress(DEContent.chaos_frag_medium, DEContent.chaos_frag_large, consumer);
        deCompress(DEContent.chaos_frag_small, DEContent.chaos_frag_medium, consumer);
        compress3x3(DEContent.chaos_shard, DEContent.chaos_frag_large, consumer);
        compress3x3(DEContent.chaos_frag_large, DEContent.chaos_frag_medium, consumer);
        compress3x3(DEContent.chaos_frag_medium, DEContent.chaos_frag_small, consumer);
    }

    private static void machines(Consumer<FinishedRecipe> consumer) {
        ShapedRecipeBuilder.shaped(DEContent.crafting_core)
                .pattern("ABA")
                .pattern("BCB")
                .pattern("ABA")
                .define('A', Tags.Items.STORAGE_BLOCKS_LAPIS)
                .define('B', Tags.Items.GEMS_DIAMOND)
                .define('C', DEContent.core_draconium)
                .unlockedBy("has_core_draconium", has(DEContent.core_draconium))
                .save(consumer);

        ShapedRecipeBuilder.shaped(DEContent.crafting_injector_basic)
                .pattern("ABA")
                .pattern("CDC")
                .pattern("CCC")
                .define('A', Tags.Items.GEMS_DIAMOND)
                .define('B', DEContent.core_draconium)
                .define('C', Tags.Items.STONE)
                .define('D', Tags.Items.STORAGE_BLOCKS_IRON)
                .unlockedBy("has_core_draconium", has(DEContent.core_draconium))
                .save(consumer);

        FusionRecipeBuilder.fusionRecipe(DEContent.crafting_injector_wyvern)
                .catalyst(DEContent.crafting_injector_basic)
                .energy(32000)
                .techLevel(TechLevel.DRACONIUM)
                .ingredient(DEContent.core_wyvern)
                .ingredient(Tags.Items.GEMS_DIAMOND)
                .ingredient(DEContent.core_draconium)
                .ingredient(Tags.Items.GEMS_DIAMOND)
                .ingredient(DEContent.core_draconium)
                .ingredient(Tags.Items.GEMS_DIAMOND)
                .ingredient(DETags.Items.STORAGE_BLOCKS_DRACONIUM)
                .ingredient(Tags.Items.GEMS_DIAMOND)
                .build(consumer);

        FusionRecipeBuilder.fusionRecipe(DEContent.crafting_injector_awakened)
                .catalyst(DEContent.crafting_injector_wyvern)
                .energy(256000)
                .techLevel(TechLevel.WYVERN)
                .ingredient(Tags.Items.GEMS_DIAMOND)
                .ingredient(Tags.Items.GEMS_DIAMOND)
                .ingredient(DEContent.core_wyvern)
                .ingredient(DETags.Items.STORAGE_BLOCKS_DRACONIUM_AWAKENED)
                .ingredient(DEContent.core_wyvern)
                .ingredient(Tags.Items.GEMS_DIAMOND)
                .ingredient(Tags.Items.GEMS_DIAMOND)
                .build(consumer);

        FusionRecipeBuilder.fusionRecipe(DEContent.crafting_injector_chaotic)
                .catalyst(DEContent.crafting_injector_awakened)
                .energy(8000000)
                .techLevel(TechLevel.DRACONIC)
                .ingredient(Tags.Items.GEMS_DIAMOND)
                .ingredient(Tags.Items.GEMS_DIAMOND)
                .ingredient(DEContent.chaos_frag_large)
                .ingredient(DEContent.chaos_frag_large)
                .ingredient(DEContent.chaos_frag_large)
                .ingredient(DEContent.chaos_frag_large)
                .ingredient(Items.DRAGON_EGG)
                .ingredient(Tags.Items.GEMS_DIAMOND)
                .ingredient(Tags.Items.GEMS_DIAMOND)
                .build(consumer);

        ShapedRecipeBuilder.shaped(DEContent.generator)
                .pattern("ABA")
                .pattern("BCB")
                .pattern("ADA")
                .define('A', Tags.Items.INGOTS_NETHER_BRICK)
                .define('B', Tags.Items.INGOTS_IRON)
                .define('C', Items.FURNACE)
                .define('D', DEContent.core_draconium)
                .unlockedBy("has_core_draconium", has(DEContent.core_draconium))
                .save(consumer);

        ShapedRecipeBuilder.shaped(DEContent.grinder)
                .pattern("ABA")
                .pattern("CDC")
                .pattern("AEA")
                .define('A', Tags.Items.INGOTS_IRON)
                .define('B', DETags.Items.INGOTS_DRACONIUM)
                .define('C', Items.DIAMOND_SWORD)
                .define('D', DEContent.energy_core_wyvern)
                .define('E', Tags.Items.HEADS)
                .unlockedBy("has_core_draconium", has(DEContent.core_draconium))
                .save(consumer);

        ShapedRecipeBuilder.shaped(DEContent.energy_transfuser)
                .pattern("ABA")
                .pattern("CDC")
                .pattern("ACA")
                .define('A', DETags.Items.INGOTS_DRACONIUM)
                .define('B', DEContent.energy_core_stabilizer)
                .define('C', DEContent.core_draconium)
                .define('D', Items.ENCHANTING_TABLE)
                .unlockedBy("has_core_draconium", has(DEContent.core_draconium))
                .save(consumer);

        ShapedRecipeBuilder.shaped(DEContent.particle_generator)
                .pattern("ABA")
                .pattern("BCB")
                .pattern("ABA")
                .define('A', Tags.Items.STORAGE_BLOCKS_REDSTONE)
                .define('B', Items.BLAZE_ROD)
                .define('C', DEContent.core_draconium)
                .unlockedBy("has_core_draconium", has(DEContent.core_draconium))
                .save(consumer);

        FusionRecipeBuilder.fusionRecipe(DEContent.draconium_chest)
                .catalyst(Items.CHEST)
                .energy(2000000)
                .techLevel(TechLevel.DRACONIUM)
                .ingredient(Items.FURNACE)
                .ingredient(DEContent.core_draconium)
                .ingredient(Items.FURNACE)
                .ingredient(DEContent.core_draconium)
                .ingredient(Items.FURNACE)
                .ingredient(Items.CRAFTING_TABLE)
                .ingredient(Items.FURNACE)
                .ingredient(DETags.Items.STORAGE_BLOCKS_DRACONIUM)
                .ingredient(Items.FURNACE)
                .ingredient(Items.CRAFTING_TABLE)
                .build(consumer);

        ShapedRecipeBuilder.shaped(DEContent.potentiometer)
                .pattern(" A ")
                .pattern("BCB")
                .pattern("DDD")
                .define('A', ItemTags.PLANKS)
                .define('B', Tags.Items.DUSTS_REDSTONE)
                .define('C', DETags.Items.DUSTS_DRACONIUM)
                .define('D', Items.STONE_SLAB)
                .unlockedBy("has_STONE_SLAB", has(Items.STONE_SLAB))
                .save(consumer);

    }

    private static void energy(Consumer<FinishedRecipe> consumer) {
        ShapedRecipeBuilder.shaped(DEContent.energy_core)
                .pattern("AAA")
                .pattern("BCB")
                .pattern("AAA")
                .define('A', DETags.Items.INGOTS_DRACONIUM)
                .define('B', DEContent.energy_core_wyvern)
                .define('C', DEContent.core_wyvern)
                .unlockedBy("has_core_wyvern", has(DEContent.core_wyvern))
                .save(consumer);

        ShapedRecipeBuilder.shaped(DEContent.energy_core_stabilizer)
                .pattern("A A")
                .pattern(" B ")
                .pattern("A A")
                .define('A', Tags.Items.GEMS_DIAMOND)
                .define('B', DEContent.particle_generator)
                .unlockedBy("has_core_wyvern", has(DEContent.core_wyvern))
                .save(consumer);

        ShapedRecipeBuilder.shaped(DEContent.energy_pylon, 2)
                .pattern("ABA")
                .pattern("CDC")
                .pattern("AEA")
                .define('A', DETags.Items.INGOTS_DRACONIUM)
                .define('B', Items.ENDER_EYE)
                .define('C', Tags.Items.GEMS_EMERALD)
                .define('D', DEContent.core_draconium)
                .define('E', Tags.Items.GEMS_DIAMOND)
                .unlockedBy("has_core_wyvern", has(DEContent.core_wyvern))
                .save(consumer);

        //Reactor
        ShapedRecipeBuilder.shaped(DEContent.reactor_prt_stab_frame)
                .pattern("AAA")
                .pattern("BC ")
                .pattern("AAA")
                .define('A', Tags.Items.INGOTS_IRON)
                .define('B', DEContent.core_wyvern)
                .define('C', DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .unlockedBy("has_core_wyvern", has(DEContent.core_wyvern))
                .save(consumer);

        ShapedRecipeBuilder.shaped(DEContent.reactor_prt_in_rotor)
                .pattern("   ")
                .pattern("AAA")
                .pattern("BCC")
                .define('A', DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .define('B', DEContent.core_draconium)
                .define('C', DETags.Items.INGOTS_DRACONIUM)
                .unlockedBy("has_core_draconium", has(DEContent.core_draconium))
                .save(consumer);

        ShapedRecipeBuilder.shaped(DEContent.reactor_prt_out_rotor)
                .pattern("   ")
                .pattern("AAA")
                .pattern("BCC")
                .define('A', Tags.Items.GEMS_DIAMOND)
                .define('B', DEContent.core_draconium)
                .define('C', DETags.Items.INGOTS_DRACONIUM)
                .unlockedBy("has_core_draconium", has(DEContent.core_draconium))
                .save(consumer);

        ShapedRecipeBuilder.shaped(DEContent.reactor_prt_rotor_full)
                .pattern(" AB")
                .pattern("CDD")
                .pattern(" AB")
                .define('A', DEContent.reactor_prt_in_rotor)
                .define('B', DEContent.reactor_prt_out_rotor)
                .define('C', DEContent.core_wyvern)
                .define('D', DETags.Items.INGOTS_DRACONIUM)
                .unlockedBy("has_core_wyvern", has(DEContent.core_wyvern))
                .save(consumer);

        ShapedRecipeBuilder.shaped(DEContent.reactor_prt_focus_ring)
                .pattern("ABA")
                .pattern("CBC")
                .pattern("ABA")
                .define('A', Tags.Items.INGOTS_GOLD)
                .define('B', Tags.Items.GEMS_DIAMOND)
                .define('C', DEContent.core_wyvern)
                .unlockedBy("has_core_wyvern", has(DEContent.core_wyvern))
                .save(consumer);

        FusionRecipeBuilder.fusionRecipe(DEContent.reactor_stabilizer)
                .catalyst(DEContent.reactor_prt_stab_frame)
                .energy(16000000)
                .techLevel(TechLevel.CHAOTIC)
                .ingredient(DEContent.energy_core_draconic)
                .ingredient(DEContent.reactor_prt_rotor_full)
                .ingredient(DEContent.reactor_prt_focus_ring)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DEContent.core_chaotic)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DEContent.chaos_frag_large)
                .build(consumer);

        FusionRecipeBuilder.fusionRecipe(DEContent.reactor_injector)
                .catalyst(DEContent.core_wyvern)
                .energy(16000000)
                .techLevel(TechLevel.CHAOTIC)
                .ingredient(DETags.Items.INGOTS_DRACONIUM)
                .ingredient(DEContent.reactor_prt_in_rotor)
                .ingredient(DEContent.reactor_prt_in_rotor)
                .ingredient(DEContent.reactor_prt_in_rotor)
                .ingredient(DETags.Items.INGOTS_DRACONIUM)
                .ingredient(DETags.Items.INGOTS_DRACONIUM)
                .ingredient(Tags.Items.INGOTS_IRON)
                .ingredient(DEContent.reactor_prt_in_rotor)
                .ingredient(Tags.Items.INGOTS_IRON)
                .ingredient(DETags.Items.INGOTS_DRACONIUM)
                .build(consumer);


        FusionRecipeBuilder.fusionRecipe(DEContent.reactor_core)
                .catalyst(DEContent.chaos_shard)
                .energy(64000000)
                .techLevel(TechLevel.CHAOTIC)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DETags.Items.INGOTS_DRACONIUM)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DETags.Items.INGOTS_DRACONIUM)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DETags.Items.INGOTS_DRACONIUM)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DEContent.chaos_frag_large)
                .ingredient(DEContent.chaos_frag_large)
                .build(consumer);

        ShapedRecipeBuilder.shaped(DEContent.crystal_binder)
                .pattern(" AB")
                .pattern(" CA")
                .pattern("D  ")
                .define('A', DETags.Items.INGOTS_DRACONIUM)
                .define('B', Tags.Items.GEMS_DIAMOND)
                .define('C', Items.BLAZE_ROD)
                .define('D', DEContent.core_draconium)
                .unlockedBy("has_core_draconium", has(DEContent.core_draconium))
                .save(consumer);

        ShapedRecipeBuilder.shaped(DEContent.crystal_relay_basic, 4)
                .pattern(" A ")
                .pattern("ABA")
                .pattern(" A ")
                .define('A', Tags.Items.GEMS_DIAMOND)
                .define('B', DEContent.energy_core_wyvern)
                .unlockedBy("has_core_draconium", has(DEContent.core_draconium))
                .save(consumer);

        ShapedRecipeBuilder.shaped(DEContent.crystal_relay_wyvern, 4)
                .pattern("ABA")
                .pattern("BCB")
                .pattern("ABA")
                .define('A', DEContent.energy_core_wyvern)
                .define('B', DEContent.crystal_relay_basic)
                .define('C', DEContent.core_draconium)
                .unlockedBy("has_core_draconium", has(DEContent.core_draconium))
                .save(consumer);

        FusionRecipeBuilder.fusionRecipe(DEContent.crystal_relay_draconic, 4)
                .catalyst(4, DEContent.crystal_relay_wyvern)
                .energy(128000)
                .techLevel(TechLevel.DRACONIC)
                .ingredient(DEContent.energy_core_wyvern)
                .ingredient(Tags.Items.GEMS_DIAMOND)
                .ingredient(DEContent.core_wyvern)
                .ingredient(Tags.Items.GEMS_DIAMOND)
                .ingredient(DEContent.energy_core_wyvern)
                .ingredient(DEContent.energy_core_wyvern)
                .ingredient(Tags.Items.GEMS_DIAMOND)
                .ingredient(Tags.Items.GEMS_DIAMOND)
                .ingredient(DEContent.energy_core_wyvern)
                .build(consumer);

        ShapedRecipeBuilder.shaped(DEContent.crystal_wireless_basic)
                .pattern("ABA")
                .pattern("CDC")
                .pattern("ABA")
                .define('A', Items.ENDER_PEARL)
                .define('B', DEContent.particle_generator)
                .define('C', Items.ENDER_EYE)
                .define('D', DEContent.crystal_relay_basic)
                .unlockedBy("has_core_draconium", has(DEContent.core_draconium))
                .save(consumer);

        ShapedRecipeBuilder.shaped(DEContent.crystal_wireless_wyvern)
                .pattern("ABA")
                .pattern("CDC")
                .pattern("ABA")
                .define('A', Items.ENDER_PEARL)
                .define('B', DEContent.particle_generator)
                .define('C', Items.ENDER_EYE)
                .define('D', DEContent.crystal_relay_wyvern)
                .unlockedBy("has_core_draconium", has(DEContent.core_draconium))
                .save(consumer);

        ShapedRecipeBuilder.shaped(DEContent.crystal_wireless_draconic)
                .pattern("ABA")
                .pattern("CDC")
                .pattern("ABA")
                .define('A', Items.ENDER_PEARL)
                .define('B', DEContent.particle_generator)
                .define('C', Items.ENDER_EYE)
                .define('D', DEContent.crystal_relay_draconic)
                .unlockedBy("has_core_draconium", has(DEContent.core_draconium))
                .save(consumer);

        //to-from io
        ShapelessRecipeBuilder.shapeless(DEContent.crystal_io_basic, 2)
                .requires(DEContent.crystal_relay_basic)
                .unlockedBy("has_core_draconium", has(DEContent.core_draconium))
                .save(consumer);

        ShapelessRecipeBuilder.shapeless(DEContent.crystal_io_wyvern, 2)
                .requires(DEContent.crystal_relay_wyvern)
                .unlockedBy("has_core_draconium", has(DEContent.core_draconium))
                .save(consumer);

        ShapelessRecipeBuilder.shapeless(DEContent.crystal_io_draconic, 2)
                .requires(DEContent.crystal_relay_draconic)
                .unlockedBy("has_core_draconium", has(DEContent.core_draconium))
                .save(consumer);

        ShapelessRecipeBuilder.shapeless(DEContent.crystal_relay_basic)
                .requires(DEContent.crystal_io_basic)
                .requires(DEContent.crystal_io_basic)
                .unlockedBy("has_core_draconium", has(DEContent.core_draconium))
                .save(consumer, "draconicevolution:crystal_io_basic_combine");

        ShapelessRecipeBuilder.shapeless(DEContent.crystal_relay_wyvern)
                .requires(DEContent.crystal_io_wyvern)
                .requires(DEContent.crystal_io_wyvern)
                .unlockedBy("has_core_draconium", has(DEContent.core_draconium))
                .save(consumer, "draconicevolution:crystal_io_wyvern_combine");

        ShapelessRecipeBuilder.shapeless(DEContent.crystal_relay_draconic)
                .requires(DEContent.crystal_io_draconic)
                .requires(DEContent.crystal_io_draconic)
                .unlockedBy("has_core_draconium", has(DEContent.core_draconium))
                .save(consumer, "draconicevolution:crystal_io_draconic_combine");
    }

    private static void tools(Consumer<FinishedRecipe> consumer) {
        ShapedRecipeBuilder.shaped(DEContent.dislocator)
                .pattern("ABA")
                .pattern("BCB")
                .pattern("ABA")
                .define('A', Items.BLAZE_POWDER)
                .define('B', DETags.Items.DUSTS_DRACONIUM)
                .define('C', Items.ENDER_EYE)
                .unlockedBy("has_dust_draconium", has(DEContent.dust_draconium))
                .save(consumer);

        FusionRecipeBuilder.fusionRecipe(DEContent.dislocator_advanced)
                .catalyst(DEContent.dislocator)
                .energy(1000000)
                .techLevel(TechLevel.WYVERN)
                .ingredient(Tags.Items.ENDER_PEARLS)
                .ingredient(DETags.Items.INGOTS_DRACONIUM)
                .ingredient(Tags.Items.ENDER_PEARLS)
                .ingredient(DETags.Items.INGOTS_DRACONIUM)
                .ingredient(Tags.Items.ENDER_PEARLS)
                .ingredient(DETags.Items.INGOTS_DRACONIUM)
                .ingredient(DEContent.core_wyvern)
                .ingredient(DETags.Items.INGOTS_DRACONIUM)
                .build(consumer);

        ShapelessRecipeBuilder.shapeless(DEContent.dislocator_p2p_unbound)
                .requires(DEContent.dislocator)
                .requires(DEContent.core_draconium)
                .requires(DEContent.dislocator)
                .requires(Items.GHAST_TEAR)
                .unlockedBy("has_core_draconium", has(DEContent.core_draconium))
                .save(consumer);

        ShapelessRecipeBuilder.shapeless(DEContent.dislocator_player_unbound)
                .requires(DEContent.dislocator)
                .requires(DEContent.core_draconium)
                .requires(Items.GHAST_TEAR)
                .unlockedBy("has_core_draconium", has(DEContent.core_draconium))
                .save(consumer);

        ShapedRecipeBuilder.shaped(DEContent.magnet)
                .pattern("A A")
                .pattern("B B")
                .pattern("CDC")
                .define('A', Tags.Items.DUSTS_REDSTONE)
                .define('B', DETags.Items.INGOTS_DRACONIUM)
                .define('C', Tags.Items.INGOTS_IRON)
                .define('D', DEContent.dislocator)
                .unlockedBy("has_dust_draconium", has(DEContent.dust_draconium))
                .save(consumer);

        ShapedRecipeBuilder.shaped(DEContent.magnet_advanced)
                .pattern("A A")
                .pattern("B B")
                .pattern("CDC")
                .define('A', DETags.Items.INGOTS_DRACONIUM)
                .define('B', Tags.Items.DUSTS_REDSTONE)
                .define('C', DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .define('D', DEContent.magnet)
                .unlockedBy("has_dust_draconium", has(DEContent.dust_draconium))
                .save(consumer);
    }

    private static void equipment(Consumer<FinishedRecipe> consumer) {
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
                .catalyst(DEContent.capacitor_wyvern)
                .energy(32000000)
                .techLevel(TechLevel.DRACONIC)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DEContent.core_awakened)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DEContent.energy_core_draconic)
                .ingredient(DEContent.energy_core_draconic)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DEContent.energy_core_draconic)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .build(consumer, folder("tools", DEContent.capacitor_draconic));

        FusionRecipeBuilder.fusionRecipe(DEContent.capacitor_chaotic)
                .catalyst(DEContent.capacitor_draconic)
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
                .build(consumer, folder("tools", DEContent.capacitor_chaotic));

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
                .ingredient(DEContent.core_chaotic)
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
                .build(consumer, folder("tools", "alt_" + DEContent.staff_chaotic.getRegistryName().getPath()));

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

    private static void modules(Consumer<FinishedRecipe> consumer) {
        ShapedRecipeBuilder.shaped(DEContent.module_core)
                .pattern("IRI")
                .pattern("GDG")
                .pattern("IRI")
                .define('I', Tags.Items.INGOTS_IRON)
                .define('R', Tags.Items.DUSTS_REDSTONE)
                .define('G', Tags.Items.INGOTS_GOLD)
                .define('D', DETags.Items.INGOTS_DRACONIUM)
                .unlockedBy("has_ingot_draconium", has(DETags.Items.INGOTS_DRACONIUM))
                .save(consumer, folder("modules", DEContent.module_core));

        //Energy
        ShapedRecipeBuilder.shaped(DEModules.draconiumEnergy.getItem())
                .pattern("###")
                .pattern("ABA")
                .pattern("###")
                .define('#', Tags.Items.STORAGE_BLOCKS_REDSTONE)
                .define('A', Tags.Items.INGOTS_IRON)
                .define('B', DEContent.module_core)
                .unlockedBy("has_module_core", has(DEContent.module_core))
                .save(consumer, folder("modules", DEModules.draconiumEnergy));

        ShapedRecipeBuilder.shaped(DEModules.wyvernEnergy.getItem())
                .pattern("###")
                .pattern("ABA")
                .pattern("###")
                .define('#', DETags.Items.INGOTS_DRACONIUM)
                .define('A', DEModules.draconiumEnergy.getItem())
                .define('B', DEContent.core_draconium)
                .unlockedBy("has_module_core", has(DEContent.module_core))
                .save(consumer, folder("modules", DEModules.wyvernEnergy));

        ShapedRecipeBuilder.shaped(DEModules.draconicEnergy.getItem())
                .pattern("###")
                .pattern("ABA")
                .pattern("###")
                .define('#', DETags.Items.NUGGETS_DRACONIUM_AWAKENED)
                .define('A', DEModules.wyvernEnergy.getItem())
                .define('B', DEContent.core_wyvern)
                .unlockedBy("has_module_core", has(DEContent.module_core))
                .save(consumer, folder("modules", DEModules.draconicEnergy));

        ShapedRecipeBuilder.shaped(DEModules.chaoticEnergy.getItem())
                .pattern("CCC")
                .pattern("ABA")
                .pattern("CCC")
                //                .define('#', chaos_frag_small)
                .define('A', DEModules.draconicEnergy.getItem())
                .define('B', DEContent.core_awakened)
                .define('C', DEContent.chaos_frag_medium)
                .unlockedBy("has_module_core", has(DEContent.module_core))
                .save(consumer, folder("modules", DEModules.chaoticEnergy));

        //Speed
        ShapedRecipeBuilder.shaped(DEModules.draconiumSpeed.getItem())
                .pattern("#P#")
                .pattern("ABA")
                .pattern("#P#")
                .define('#', Tags.Items.INGOTS_IRON)
                .define('A', Items.CLOCK)
                .define('B', DEContent.module_core)
                .define('P', new NBTIngredient(PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.SWIFTNESS)))
                .unlockedBy("has_module_core", has(DEContent.module_core))
                .save(consumer, folder("modules", DEModules.draconiumSpeed));

        ShapedRecipeBuilder.shaped(DEModules.wyvernSpeed.getItem())
                .pattern("###")
                .pattern("ABA")
                .pattern("###")
                .define('#', DETags.Items.INGOTS_DRACONIUM)
                .define('A', DEModules.draconiumSpeed.getItem())
                .define('B', DEContent.core_draconium)
                .unlockedBy("has_module_core", has(DEContent.module_core))
                .save(consumer, folder("modules", DEModules.wyvernSpeed));

        ShapedRecipeBuilder.shaped(DEModules.draconicSpeed.getItem())
                .pattern("###")
                .pattern("ABA")
                .pattern("###")
                .define('#', DETags.Items.NUGGETS_DRACONIUM_AWAKENED)
                .define('A', DEModules.wyvernSpeed.getItem())
                .define('B', DEContent.core_wyvern)
                .unlockedBy("has_module_core", has(DEContent.module_core))
                .save(consumer, folder("modules", DEModules.draconicSpeed));

        ShapedRecipeBuilder.shaped(DEModules.chaoticSpeed.getItem())
                .pattern("CCC")
                .pattern("ABA")
                .pattern("CCC")
                //                .define('#', chaos_frag_small)
                .define('A', DEModules.draconicSpeed.getItem())
                .define('B', DEContent.core_awakened)
                .define('C', DEContent.chaos_frag_medium)
                .unlockedBy("has_module_core", has(DEContent.module_core))
                .save(consumer, folder("modules", DEModules.chaoticSpeed));

        //Damage
        ShapedRecipeBuilder.shaped(DEModules.draconiumDamage.getItem())
                .pattern("IPG")
                .pattern("ABA")
                .pattern("GPI")
                .define('I', Tags.Items.INGOTS_IRON)
                .define('P', new NBTIngredient(PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.STRENGTH)))
                .define('G', Tags.Items.INGOTS_GOLD)
                .define('A', Tags.Items.DUSTS_GLOWSTONE)
                .define('B', DEContent.module_core)
                .unlockedBy("has_module_core", has(DEContent.module_core))
                .save(consumer, folder("modules", DEModules.draconiumDamage));

        ShapedRecipeBuilder.shaped(DEModules.wyvernDamage.getItem())
                .pattern("IPI")
                .pattern("ABA")
                .pattern("IPI")
                .define('I', DETags.Items.INGOTS_DRACONIUM)
                .define('P', new NBTIngredient(PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.STRONG_STRENGTH)))
                .define('A', DEModules.draconiumDamage.getItem())
                .define('B', DEContent.core_draconium)
                .unlockedBy("has_module_core", has(DEContent.module_core))
                .save(consumer, folder("modules", DEModules.wyvernDamage));

        ShapedRecipeBuilder.shaped(DEModules.draconicDamage.getItem())
                .pattern("IPI")
                .pattern("ABA")
                .pattern("IPI")
                .define('I', DETags.Items.NUGGETS_DRACONIUM_AWAKENED)
                .define('P', Items.DRAGON_BREATH)
                .define('A', DEModules.wyvernDamage.getItem())
                .define('B', DEContent.core_wyvern)
                .unlockedBy("has_module_core", has(DEContent.module_core))
                .save(consumer, folder("modules", DEModules.draconicDamage));

        ShapedRecipeBuilder.shaped(DEModules.chaoticDamage.getItem())
                .pattern("CCC")
                .pattern("ABA")
                .pattern("CCC")
                //                .define('I', chaos_frag_small)
                .define('C', DEContent.chaos_frag_medium)
                .define('A', DEModules.draconicDamage.getItem())
                .define('B', DEContent.core_awakened)
                .unlockedBy("has_module_core", has(DEContent.module_core))
                .save(consumer, folder("modules", DEModules.chaoticDamage));

        //AOE
        ShapedRecipeBuilder.shaped(DEModules.draconiumAOE.getItem())
                .pattern("#I#")
                .pattern("ABA")
                .pattern("#I#")
                .define('#', Items.PISTON)
                .define('I', DETags.Items.INGOTS_DRACONIUM)
                .define('A', DEContent.core_draconium)
                .define('B', DEContent.module_core)
                .unlockedBy("has_module_core", has(DEContent.module_core))
                .save(consumer, folder("modules", DEModules.draconiumAOE));

        ShapedRecipeBuilder.shaped(DEModules.wyvernAOE.getItem())
                .pattern("#I#")
                .pattern("ABA")
                .pattern("#I#")
                .define('#', DETags.Items.INGOTS_DRACONIUM)
                .define('I', Items.NETHERITE_SCRAP)
                .define('A', DEModules.draconiumAOE.getItem())
                .define('B', DEContent.core_wyvern)
                .unlockedBy("has_module_core", has(DEContent.module_core))
                .save(consumer, folder("modules", DEModules.wyvernAOE));

        ShapedRecipeBuilder.shaped(DEModules.draconicAOE.getItem())
                .pattern("#I#")
                .pattern("ABA")
                .pattern("#I#")
                .define('#', Tags.Items.INGOTS_NETHERITE)
                .define('I', DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .define('A', DEModules.wyvernAOE.getItem())
                .define('B', DEContent.core_awakened)
                .unlockedBy("has_module_core", has(DEContent.module_core))
                .save(consumer, folder("modules", DEModules.draconicAOE));

        ShapedRecipeBuilder.shaped(DEModules.chaoticAOE.getItem())
                .pattern("#I#")
                .pattern("ABA")
                .pattern("#I#")
                .define('#', Tags.Items.INGOTS_NETHERITE)
                .define('I', DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .define('A', DEModules.draconicAOE.getItem())
                .define('B', DEContent.core_chaotic)
                .unlockedBy("has_module_core", has(DEContent.module_core))
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
        ShapedRecipeBuilder.shaped(DEModules.wyvernJunkFilter.getItem())
                .pattern("#C#")
                .pattern("ABA")
                .pattern("#D#")
                .define('#', DETags.Items.INGOTS_DRACONIUM)
                .define('A', DEContent.core_draconium)
                .define('B', DEContent.module_core)
                .define('C', Items.LAVA_BUCKET)
                .define('D', Tags.Items.DUSTS_REDSTONE)
                .unlockedBy("has_module_core", has(DEContent.module_core))
                .save(consumer, folder("modules", DEModules.wyvernJunkFilter));

        //Tree Harvest
        ShapedRecipeBuilder.shaped(DEModules.wyvernTreeHarvest.getItem())
                .pattern("#A#")
                .pattern("CMC")
                .pattern("#A#")
                .define('#', DETags.Items.INGOTS_DRACONIUM)
                .define('A', Items.DIAMOND_AXE)
                .define('M', DEContent.module_core)
                .define('C', DEContent.core_draconium)
                .unlockedBy("has_module_core", has(DEContent.module_core))
                .save(consumer, folder("modules", DEModules.wyvernTreeHarvest));

        ShapedRecipeBuilder.shaped(DEModules.draconicTreeHarvest.getItem())
                .pattern("#C#")
                .pattern("IMI")
                .pattern("#W#")
                .define('#', DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .define('C', DEContent.core_draconium)
                .define('I', Tags.Items.INGOTS_NETHERITE)
                .define('M', DEModules.wyvernTreeHarvest.getItem())
                .define('W', DEContent.core_wyvern)
                .unlockedBy("has_module_core", has(DEContent.module_core))
                .save(consumer, folder("modules", DEModules.draconicTreeHarvest));

        ShapedRecipeBuilder.shaped(DEModules.chaoticTreeHarvest.getItem())
                .pattern("#C#")
                .pattern("IMI")
                .pattern("#W#")
                .define('#', Tags.Items.INGOTS_NETHERITE)
                .define('C', DEContent.core_draconium)
                .define('I', DEContent.chaos_frag_large)
                .define('M', DEModules.draconicTreeHarvest.getItem())
                .define('W', DEContent.core_awakened)
                .unlockedBy("has_module_core", has(DEContent.module_core))
                .save(consumer, folder("modules", DEModules.chaoticTreeHarvest));

        //Ender Collection
        ShapedRecipeBuilder.shaped(DEModules.wyvernEnderCollection.getItem())
                .pattern("#C#")
                .pattern("IMI")
                .pattern("#W#")
                .define('#', Items.ENDER_EYE)
                .define('C', DEContent.core_draconium)
                .define('I', DETags.Items.INGOTS_DRACONIUM)
                .define('M', DEContent.module_core)
                .define('W', Tags.Items.CHESTS_ENDER)
                .unlockedBy("has_module_core", has(DEContent.module_core))
                .save(consumer, folder("modules", DEModules.wyvernEnderCollection));

        ShapedRecipeBuilder.shaped(DEModules.draconicEnderCollection.getItem())
                .pattern("#C#")
                .pattern("IMI")
                .pattern("#W#")
                .define('#', DETags.Items.NUGGETS_DRACONIUM_AWAKENED)
                .define('C', DEContent.core_draconium)
                .define('I', Items.COMPARATOR)
                .define('M', DEModules.wyvernEnderCollection.getItem())
                .define('W', Tags.Items.CHESTS)
                .unlockedBy("has_module_core", has(DEContent.module_core))
                .save(consumer, folder("modules", DEModules.draconicEnderCollection));

        //Shield Controller
        ShapedRecipeBuilder.shaped(DEModules.wyvernShieldControl.getItem())
                .pattern("#C#")
                .pattern("ABA")
                .pattern("#D#")
                .define('#', Tags.Items.GEMS_DIAMOND)
                .define('A', DEContent.core_wyvern)
                .define('B', DEContent.module_core)
                .define('C', DEContent.dragon_heart)
                .define('D', DEContent.particle_generator)
                .unlockedBy("has_module_core", has(DEContent.module_core))
                .save(consumer, folder("modules", DEModules.wyvernShieldControl));

        ShapedRecipeBuilder.shaped(DEModules.draconicShieldControl.getItem())
                .pattern("#I#")
                .pattern("ABA")
                .pattern("#I#")
                .define('#', Tags.Items.GEMS_EMERALD)
                .define('A', DEContent.core_awakened)
                .define('B', DEModules.wyvernShieldControl.getItem())
                .define('I', Tags.Items.INGOTS_NETHERITE)
                .unlockedBy("has_module_core", has(DEContent.module_core))
                .save(consumer, folder("modules", DEModules.draconicShieldControl));

        ShapedRecipeBuilder.shaped(DEModules.chaoticShieldControl.getItem())
                .pattern("#I#")
                .pattern("ABA")
                .pattern("#I#")
                .define('#', Tags.Items.NETHER_STARS)
                .define('A', DEContent.core_chaotic)
                .define('B', DEModules.draconicShieldControl.getItem())
                .define('I', Tags.Items.INGOTS_NETHERITE)
                .unlockedBy("has_module_core", has(DEContent.module_core))
                .save(consumer, folder("modules", DEModules.chaoticShieldControl));

        //Shield Capacity
        ShapedRecipeBuilder.shaped(DEModules.wyvernShieldCapacity.getItem())
                .pattern("#I#")
                .pattern("ABA")
                .pattern("#I#")
                .define('#', DETags.Items.INGOTS_DRACONIUM)
                .define('A', Tags.Items.DUSTS_GLOWSTONE)
                .define('B', DEContent.module_core)
                .define('I', Items.NETHERITE_SCRAP)
                .unlockedBy("has_wyvern_shield", has(DEModules.wyvernShieldControl.getItem()))
                .save(consumer, folder("modules", DEModules.wyvernShieldCapacity));

        ShapedRecipeBuilder.shaped(DEModules.draconicShieldCapacity.getItem())
                .pattern("#C#")
                .pattern("ABA")
                .pattern("#D#")
                .define('#', Tags.Items.INGOTS_NETHERITE)
                .define('A', DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .define('B', DEModules.wyvernShieldCapacity.getItem())
                .define('C', DEContent.core_draconium)
                .define('D', DEContent.core_wyvern)
                .unlockedBy("has_draconic_shield", has(DEModules.draconicShieldControl.getItem()))
                .save(consumer, folder("modules", DEModules.draconicShieldCapacity));

        ShapedRecipeBuilder.shaped(DEModules.chaoticShieldCapacity.getItem())
                .pattern("#C#")
                .pattern("ABA")
                .pattern("#D#")
                .define('#', DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .define('A', DEContent.chaos_frag_large)
                .define('B', DEModules.draconicShieldCapacity.getItem())
                .define('C', DEContent.core_wyvern)
                .define('D', DEContent.core_chaotic)
                .unlockedBy("has_chaotic_shield", has(DEModules.chaoticShieldControl.getItem()))
                .save(consumer, folder("modules", DEModules.chaoticShieldCapacity));

        //Shield Capacity XL
        ShapedRecipeBuilder.shaped(DEModules.wyvernLargeShieldCapacity.getItem())
                .pattern("#A#")
                .pattern("A#A")
                .pattern("#A#")
                .define('#', DEModules.wyvernShieldCapacity.getItem())
                .define('A', DEContent.core_draconium)
                .unlockedBy("has_wyvern_shield", has(DEModules.wyvernShieldControl.getItem()))
                .save(consumer, folder("modules", DEModules.wyvernLargeShieldCapacity));

        ShapedRecipeBuilder.shaped(DEModules.draconicLargeShieldCapacity.getItem())
                .pattern("#A#")
                .pattern("A#A")
                .pattern("#A#")
                .define('#', DEModules.draconicShieldCapacity.getItem())
                .define('A', DEContent.core_draconium)
                .unlockedBy("has_draconic_shield", has(DEModules.draconicShieldControl.getItem()))
                .save(consumer, folder("modules", DEModules.draconicLargeShieldCapacity));

        ShapedRecipeBuilder.shaped(DEModules.chaoticLargeShieldCapacity.getItem())
                .pattern("#A#")
                .pattern("A#A")
                .pattern("#A#")
                .define('#', DEModules.chaoticShieldCapacity.getItem())
                .define('A', DEContent.core_draconium)
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
                .define('#', DETags.Items.INGOTS_DRACONIUM)
                .define('A', Tags.Items.DUSTS_REDSTONE)
                .define('B', DEContent.module_core)
                .define('I', Items.NETHERITE_SCRAP)
                .unlockedBy("has_wyvern_shield", has(DEModules.wyvernShieldControl.getItem()))
                .save(consumer, folder("modules", DEModules.wyvernShieldRecovery));

        ShapedRecipeBuilder.shaped(DEModules.draconicShieldRecovery.getItem())
                .pattern("#C#")
                .pattern("ABA")
                .pattern("#D#")
                .define('#', Tags.Items.INGOTS_NETHERITE)
                .define('A', DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .define('B', DEModules.wyvernShieldRecovery.getItem())
                .define('C', DEContent.core_draconium)
                .define('D', DEContent.core_wyvern)
                .unlockedBy("has_draconic_shield", has(DEModules.draconicShieldControl.getItem()))
                .save(consumer, folder("modules", DEModules.draconicShieldRecovery));

        ShapedRecipeBuilder.shaped(DEModules.chaoticShieldRecovery.getItem())
                .pattern("#C#")
                .pattern("ABA")
                .pattern("#D#")
                .define('#', DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .define('A', DEContent.chaos_frag_large)
                .define('B', DEModules.draconicShieldRecovery.getItem())
                .define('C', DEContent.core_wyvern)
                .define('D', DEContent.core_chaotic)
                .unlockedBy("has_chaotic_shield", has(DEModules.chaoticShieldControl.getItem()))
                .save(consumer, folder("modules", DEModules.chaoticShieldRecovery));

        //Flight
        ShapedRecipeBuilder.shaped(DEModules.wyvernFlight.getItem())
                .pattern("#C#")
                .pattern("ABA")
                .pattern("#D#")
                .define('#', DETags.Items.INGOTS_DRACONIUM)
                .define('C', Items.ELYTRA)
                .define('A', DEContent.core_draconium)
                .define('B', DEContent.module_core)
                .define('D', Items.FIREWORK_ROCKET)
                .unlockedBy("has_module_core", has(DEContent.module_core))
                .save(consumer, folder("modules", DEModules.wyvernFlight));

        ShapedRecipeBuilder.shaped(DEModules.draconicFlight.getItem())
                .pattern("#C#")
                .pattern("ABA")
                .pattern("#D#")
                .define('#', DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .define('A', DEContent.core_wyvern)
                .define('B', DEModules.wyvernFlight.getItem())
                .define('C', new NBTIngredient(PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.SLOW_FALLING)))
                .define('D', Items.FIREWORK_ROCKET)
                .unlockedBy("has_module_core", has(DEContent.module_core))
                .save(consumer, folder("modules", DEModules.draconicFlight));

        ShapedRecipeBuilder.shaped(DEModules.chaoticFlight.getItem())
                .pattern("#C#")
                .pattern("ABA")
                .pattern("#C#")
                .define('#', new NBTIngredient(PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.STRONG_SWIFTNESS)))
                .define('A', DEContent.core_awakened)
                .define('B', DEModules.draconicFlight.getItem())
                .define('C', DEContent.chaos_frag_large)
                .unlockedBy("has_module_core", has(DEContent.module_core))
                .save(consumer, folder("modules", DEModules.chaoticFlight));

        //Last Stand
        ShapedRecipeBuilder.shaped(DEModules.wyvernUndying.getItem())
                .pattern("#C#")
                .pattern("ABA")
                .pattern("#D#")
                .define('#', DETags.Items.INGOTS_DRACONIUM)
                .define('A', DEContent.core_draconium)
                .define('B', DEContent.module_core)
                .define('C', Items.TOTEM_OF_UNDYING)
                .define('D', DEModules.wyvernShieldCapacity.getItem())
                .unlockedBy("has_module_core", has(DEContent.module_core))
                .save(consumer, folder("modules", DEModules.wyvernUndying));

        ShapedRecipeBuilder.shaped(DEModules.draconicUndying.getItem())
                .pattern("#C#")
                .pattern("ABA")
                .pattern("#D#")
                .define('#', DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .define('A', DEContent.core_wyvern)
                .define('B', DEModules.wyvernUndying.getItem())
                .define('C', new NBTIngredient(PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.STRONG_HEALING)))
                .define('D', DEModules.draconicShieldCapacity.getItem())
                .unlockedBy("has_module_core", has(DEContent.module_core))
                .save(consumer, folder("modules", DEModules.draconicUndying));

        ShapedRecipeBuilder.shaped(DEModules.chaoticUndying.getItem())
                .pattern("#C#")
                .pattern("ABA")
                .pattern("#D#")
                .define('#', DEContent.chaos_frag_medium)
                .define('A', DEContent.core_awakened)
                .define('B', DEModules.draconicUndying.getItem())
                .define('C', Items.ENCHANTED_GOLDEN_APPLE)
                .define('D', DEModules.chaoticShieldCapacity.getItem())
                .unlockedBy("has_module_core", has(DEContent.module_core))
                .save(consumer, folder("modules", DEModules.chaoticUndying));

        //Auto Feed
        ShapedRecipeBuilder.shaped(DEModules.draconiumAutoFeed.getItem())
                .pattern("#C#")
                .pattern("ABA")
                .pattern("#D#")
                .define('#', Tags.Items.INGOTS_IRON)
                .define('A', Items.COOKIE)
                .define('B', DEContent.module_core)
                .define('C', Items.GOLDEN_APPLE)
                .define('D', DEContent.core_draconium)
                .unlockedBy("has_module_core", has(DEContent.module_core))
                .save(consumer, folder("modules", DEModules.draconiumAutoFeed));

        ShapedRecipeBuilder.shaped(DEModules.wyvernAutoFeed.getItem())
                .pattern("#C#")
                .pattern("ABA")
                .pattern("#C#")
                .define('#', DETags.Items.INGOTS_DRACONIUM)
                .define('A', DEContent.core_draconium)
                .define('B', DEModules.draconiumAutoFeed.getItem())
                .define('C', Items.COOKIE)
                .unlockedBy("has_module_core", has(DEContent.module_core))
                .save(consumer, folder("modules", DEModules.wyvernAutoFeed));

        ShapedRecipeBuilder.shaped(DEModules.draconicAutoFeed.getItem())
                .pattern("#C#")
                .pattern("ABA")
                .pattern("#C#")
                .define('#', DETags.Items.NUGGETS_DRACONIUM_AWAKENED)
                .define('A', DEContent.core_draconium)
                .define('B', DEModules.wyvernAutoFeed.getItem())
                .define('C', Items.COOKIE)
                .unlockedBy("has_module_core", has(DEContent.module_core))
                .save(consumer, folder("modules", DEModules.draconicAutoFeed));

        //Night Vision
        ShapedRecipeBuilder.shaped(DEModules.wyvernNightVision.getItem())
                .pattern("#P#")
                .pattern("ABA")
                .pattern("#P#")
                .define('#', DETags.Items.INGOTS_DRACONIUM)
                .define('A', DEContent.core_draconium)
                .define('B', DEContent.module_core)
                .define('P', new NBTIngredient(PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.NIGHT_VISION)))
                .unlockedBy("has_module_core", has(DEContent.module_core))
                .save(consumer, folder("modules", DEModules.wyvernNightVision));

        //Jump Boost
        ShapedRecipeBuilder.shaped(DEModules.draconiumJump.getItem())
                .pattern("CPD")
                .pattern("ABA")
                .pattern("DPC")
                .define('A', Tags.Items.DUSTS_GLOWSTONE)
                .define('B', DEContent.module_core)
                .define('C', Tags.Items.INGOTS_IRON)
                .define('D', Tags.Items.INGOTS_GOLD)
                .define('P', new NBTIngredient(PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.LEAPING)))
                .unlockedBy("has_module_core", has(DEContent.module_core))
                .save(consumer, folder("modules", DEModules.draconiumJump));

        ShapedRecipeBuilder.shaped(DEModules.wyvernJump.getItem())
                .pattern("#P#")
                .pattern("ABA")
                .pattern("#P#")
                .define('#', DETags.Items.INGOTS_DRACONIUM)
                .define('B', DEContent.core_draconium)
                .define('A', DEModules.draconiumJump.getItem())
                .define('P', new NBTIngredient(PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.STRONG_LEAPING)))
                .unlockedBy("has_module_core", has(DEContent.module_core))
                .save(consumer, folder("modules", DEModules.wyvernJump));

        ShapedRecipeBuilder.shaped(DEModules.draconicJump.getItem())
                .pattern("###")
                .pattern("ABA")
                .pattern("###")
                .define('#', DETags.Items.NUGGETS_DRACONIUM_AWAKENED)
                .define('B', DEContent.core_wyvern)
                .define('A', DEModules.wyvernJump.getItem())
                .unlockedBy("has_module_core", has(DEContent.module_core))
                .save(consumer, folder("modules", DEModules.draconicJump));

        ShapedRecipeBuilder.shaped(DEModules.chaoticJump.getItem())
                .pattern("CCC")
                .pattern("ABA")
                .pattern("CCC")
                //                .define('#', chaos_frag_small)
                .define('B', DEContent.core_awakened)
                .define('A', DEModules.draconicJump.getItem())
                .define('C', DEContent.chaos_frag_medium)
                .unlockedBy("has_module_core", has(DEContent.module_core))
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
                .define('#', DETags.Items.INGOTS_DRACONIUM)
                .define('A', DEContent.core_draconium)
                .define('B', DEContent.module_core)
                .define('C', Items.GOLDEN_BOOTS)
                .define('D', Items.PISTON)
                .unlockedBy("has_module_core", has(DEContent.module_core))
                .save(consumer, folder("modules", DEModules.wyvernHillStep));

        //Arrow Velocity
        ShapedRecipeBuilder.shaped(DEModules.wyvernProjVelocity.getItem())
                .pattern("#P#")
                .pattern("ABA")
                .pattern("#C#")
                .define('#', DETags.Items.INGOTS_DRACONIUM)
                .define('C', ItemTags.ARROWS)
                .define('B', DEContent.module_core)
                .define('A', DEContent.core_draconium)
                .define('P', new NBTIngredient(PotionUtils.setPotion(new ItemStack(Items.SPLASH_POTION), Potions.STRONG_SWIFTNESS)))
                .unlockedBy("has_module_core", has(DEContent.module_core))
                .save(consumer, folder("modules", DEModules.wyvernProjVelocity));

        ShapedRecipeBuilder.shaped(DEModules.draconicProjVelocity.getItem())
                .pattern("###")
                .pattern("ABA")
                .pattern("###")
                .define('#', DETags.Items.NUGGETS_DRACONIUM_AWAKENED)
                .define('B', DEContent.core_wyvern)
                .define('A', DEModules.wyvernProjVelocity.getItem())
                .unlockedBy("has_module_core", has(DEContent.module_core))
                .save(consumer, folder("modules", DEModules.draconicProjVelocity));

        ShapedRecipeBuilder.shaped(DEModules.chaoticProjVelocity.getItem())
                .pattern("CCC")
                .pattern("ABA")
                .pattern("CCC")
                //                .define('#', chaos_frag_small)
                .define('B', DEContent.core_awakened)
                .define('A', DEModules.draconicProjVelocity.getItem())
                .define('C', DEContent.chaos_frag_medium)
                .unlockedBy("has_module_core", has(DEContent.module_core))
                .save(consumer, folder("modules", DEModules.chaoticProjVelocity));

        //Arrow Accuracy
        ShapedRecipeBuilder.shaped(DEModules.wyvernProjAccuracy.getItem())
                .pattern("#P#")
                .pattern("ABA")
                .pattern("#C#")
                .define('#', DETags.Items.INGOTS_DRACONIUM)
                .define('C', ItemTags.ARROWS)
                .define('B', DEContent.module_core)
                .define('A', DEContent.core_draconium)
                .define('P', Items.TARGET)
                .unlockedBy("has_module_core", has(DEContent.module_core))
                .save(consumer, folder("modules", DEModules.wyvernProjAccuracy));

        ShapedRecipeBuilder.shaped(DEModules.draconicProjAccuracy.getItem())
                .pattern("###")
                .pattern("ABA")
                .pattern("###")
                .define('#', DETags.Items.NUGGETS_DRACONIUM_AWAKENED)
                .define('B', DEContent.core_wyvern)
                .define('A', DEModules.wyvernProjAccuracy.getItem())
                .unlockedBy("has_module_core", has(DEContent.module_core))
                .save(consumer, folder("modules", DEModules.draconicProjAccuracy));

        ShapedRecipeBuilder.shaped(DEModules.chaoticProjAccuracy.getItem())
                .pattern("CCC")
                .pattern("ABA")
                .pattern("CCC")
                //                .define('#', chaos_frag_small)
                .define('B', DEContent.core_awakened)
                .define('A', DEModules.draconicProjAccuracy.getItem())
                .define('C', DEContent.chaos_frag_medium)
                .unlockedBy("has_module_core", has(DEContent.module_core))
                .save(consumer, folder("modules", DEModules.chaoticProjAccuracy));

        //Arrow Penetration
        ShapedRecipeBuilder.shaped(DEModules.wyvernProjPenetration.getItem())
                .pattern("#P#")
                .pattern("ABA")
                .pattern("#C#")
                .define('#', DETags.Items.INGOTS_DRACONIUM)
                .define('C', ItemTags.ARROWS)
                .define('B', DEContent.module_core)
                .define('A', DEContent.core_draconium)
                .define('P', Items.SHIELD)
                .unlockedBy("has_module_core", has(DEContent.module_core))
                .save(consumer, folder("modules", DEModules.wyvernProjPenetration));

        ShapedRecipeBuilder.shaped(DEModules.draconicProjPenetration.getItem())
                .pattern("###")
                .pattern("ABA")
                .pattern("###")
                .define('#', DETags.Items.NUGGETS_DRACONIUM_AWAKENED)
                .define('B', DEContent.core_wyvern)
                .define('A', DEModules.wyvernProjPenetration.getItem())
                .unlockedBy("has_module_core", has(DEContent.module_core))
                .save(consumer, folder("modules", DEModules.draconicProjPenetration));

        ShapedRecipeBuilder.shaped(DEModules.chaoticProjPenetration.getItem())
                .pattern("CCC")
                .pattern("ABA")
                .pattern("CCC")
                //                .define('#', chaos_frag_small)
                .define('B', DEContent.core_awakened)
                .define('A', DEModules.draconicProjPenetration.getItem())
                .define('C', DEContent.chaos_frag_medium)
                .unlockedBy("has_module_core", has(DEContent.module_core))
                .save(consumer, folder("modules", DEModules.chaoticProjPenetration));

        //Arrow Damage
        ShapedRecipeBuilder.shaped(DEModules.wyvernProjDamage.getItem())
                .pattern("#P#")
                .pattern("ABA")
                .pattern("#C#")
                .define('#', DETags.Items.INGOTS_DRACONIUM)
                .define('C', ItemTags.ARROWS)
                .define('B', DEContent.module_core)
                .define('A', DEContent.core_draconium)
                .define('P', new NBTIngredient(PotionUtils.setPotion(new ItemStack(Items.SPLASH_POTION), Potions.STRONG_STRENGTH)))
                .unlockedBy("has_module_core", has(DEContent.module_core))
                .save(consumer, folder("modules", DEModules.wyvernProjDamage));

        ShapedRecipeBuilder.shaped(DEModules.draconicProjDamage.getItem())
                .pattern("###")
                .pattern("ABA")
                .pattern("###")
                .define('#', DETags.Items.NUGGETS_DRACONIUM_AWAKENED)
                .define('B', DEContent.core_wyvern)
                .define('A', DEModules.wyvernProjDamage.getItem())
                .unlockedBy("has_module_core", has(DEContent.module_core))
                .save(consumer, folder("modules", DEModules.draconicProjDamage));

        ShapedRecipeBuilder.shaped(DEModules.chaoticProjDamage.getItem())
                .pattern("CCC")
                .pattern("ABA")
                .pattern("CCC")
                //                .define('#', chaos_frag_small)
                .define('B', DEContent.core_awakened)
                .define('A', DEModules.draconicProjDamage.getItem())
                .define('C', DEContent.chaos_frag_medium)
                .unlockedBy("has_module_core", has(DEContent.module_core))
                .save(consumer, folder("modules", DEModules.chaoticProjDamage));

        //Arrow Anti Grav
        ShapedRecipeBuilder.shaped(DEModules.wyvernProjGravComp.getItem())
                .pattern("#P#")
                .pattern("ABA")
                .pattern("#C#")
                .define('#', DETags.Items.INGOTS_DRACONIUM)
                .define('C', ItemTags.ARROWS)
                .define('B', DEContent.module_core)
                .define('A', DEContent.core_draconium)
                .define('P', new NBTIngredient(PotionUtils.setPotion(new ItemStack(Items.SPLASH_POTION), Potions.LONG_SLOW_FALLING)))
                .unlockedBy("has_module_core", has(DEContent.module_core))
                .save(consumer, folder("modules", DEModules.wyvernProjGravComp));

        ShapedRecipeBuilder.shaped(DEModules.draconicProjGravComp.getItem())
                .pattern("###")
                .pattern("ABA")
                .pattern("###")
                .define('#', DETags.Items.NUGGETS_DRACONIUM_AWAKENED)
                .define('B', DEContent.core_wyvern)
                .define('A', DEModules.wyvernProjGravComp.getItem())
                .unlockedBy("has_module_core", has(DEContent.module_core))
                .save(consumer, folder("modules", DEModules.draconicProjGravComp));

        ShapedRecipeBuilder.shaped(DEModules.chaoticProjGravComp.getItem())
                .pattern("CCC")
                .pattern("ABA")
                .pattern("CCC")
                //                .define('#', chaos_frag_small)
                .define('B', DEContent.core_awakened)
                .define('A', DEModules.draconicProjGravComp.getItem())
                .define('C', DEContent.chaos_frag_medium)
                .unlockedBy("has_module_core", has(DEContent.module_core))
                .save(consumer, folder("modules", DEModules.chaoticProjGravComp));

        // Auto Fire
        ShapedRecipeBuilder.shaped(DEModules.wyvernAutoFire.getItem())
                .pattern("#P#")
                .pattern("ABA")
                .pattern("#C#")
                .define('#', DETags.Items.INGOTS_DRACONIUM)
                .define('C', Items.BOW)
                .define('B', DEContent.module_core)
                .define('A', DEContent.core_draconium)
                .define('P', Items.CLOCK)
                .unlockedBy("has_module_core", has(DEContent.module_core))
                .save(consumer, folder("modules", DEModules.wyvernAutoFire));

        // Projectile Anti Immunity
        ShapedRecipeBuilder.shaped(DEModules.draconicProjAntiImmune.getItem())
                .pattern("#P#")
                .pattern("ABA")
                .pattern("#C#")
                .define('#', DETags.Items.NUGGETS_DRACONIUM_AWAKENED)
                .define('C', Tags.Items.ENDER_PEARLS)
                .define('B', DEContent.module_core)
                .define('A', DEContent.core_wyvern)
                .define('P', Items.WITHER_SKELETON_SKULL)
                .unlockedBy("has_module_core", has(DEContent.module_core))
                .save(consumer, folder("modules", DEModules.draconicProjAntiImmune));
    }


    private static void unsorted(Consumer<FinishedRecipe> consumer) {

        ShapedRecipeBuilder.shaped(DEContent.infused_obsidian)
                .pattern("ABA")
                .pattern("BCB")
                .pattern("ABA")
                .define('A', Items.BLAZE_POWDER)
                .define('B', Tags.Items.OBSIDIAN)
                .define('C', DETags.Items.DUSTS_DRACONIUM)
                .unlockedBy("has_dust_draconium", has(DEContent.dust_draconium))
                .save(consumer);


        //        FusionRecipeBuilder.fusionRecipe(ender_energy_manipulator).catalyst(SKELETON_SKULL).energy(12000000).techLevel(WYVERN).ingredient(ENDER_EYE).ingredient(ENDER_EYE).ingredient(ENDER_EYE).ingredient(ENDER_EYE).ingredient(ENDER_EYE).ingredient(ENDER_EYE).ingredient(core_draconium).ingredient(core_wyvern).ingredient(core_draconium).ingredient(ENDER_EYE).build(consumer);


        ShapedRecipeBuilder.shaped(DEContent.dislocator_receptacle)
                .pattern("ABA")
                .pattern(" C ")
                .pattern("A A")
                .define('A', Tags.Items.INGOTS_IRON)
                .define('B', DEContent.core_draconium)
                .define('C', DEContent.infused_obsidian)
                .unlockedBy("has_dust_draconium", has(DEContent.dislocator))
                .save(consumer);


        ShapedRecipeBuilder.shaped(DEContent.dislocator_pedestal)
                .pattern(" A ")
                .pattern(" B ")
                .pattern("CDC")
                .define('A', Items.STONE_PRESSURE_PLATE)
                .define('B', Tags.Items.STONE)
                .define('C', Items.STONE_SLAB)
                .define('D', Items.BLAZE_POWDER)
                .unlockedBy("has_dust_draconium", has(DEContent.dislocator))
                .save(consumer);


        ShapedRecipeBuilder.shaped(DEContent.rain_sensor)
                .pattern(" A ")
                .pattern("BCB")
                .pattern("DDD")
                .define('A', Items.BUCKET)
                .define('B', Tags.Items.DUSTS_REDSTONE)
                .define('C', Items.STONE_PRESSURE_PLATE)
                .define('D', Items.STONE_SLAB)
                .unlockedBy("has_STONE_SLAB", has(Items.STONE_SLAB))
                .save(consumer);

        ShapedRecipeBuilder.shaped(DEContent.disenchanter)
                .pattern("ABA")
                .pattern("CDC")
                .pattern("EEE")
                .define('A', Tags.Items.GEMS_EMERALD)
                .define('B', DEContent.core_draconium)
                .define('C', Items.ENCHANTED_BOOK)
                .define('D', Items.ENCHANTING_TABLE)
                .define('E', Items.BOOKSHELF)
                .unlockedBy("has_core_draconium", has(DEContent.core_draconium))
                .save(consumer);

        ShapedRecipeBuilder.shaped(DEContent.celestial_manipulator)
                .pattern("ABA")
                .pattern("CDC")
                .pattern("EFE")
                .define('A', Tags.Items.STORAGE_BLOCKS_REDSTONE)
                .define('B', Items.CLOCK)
                .define('C', DETags.Items.INGOTS_DRACONIUM)
                .define('D', Items.DRAGON_EGG)
                .define('E', Tags.Items.INGOTS_IRON)
                .define('F', DEContent.core_wyvern)
                .unlockedBy("has_ingot_draconium", has(DETags.Items.INGOTS_DRACONIUM))
                .save(consumer);

        ShapedRecipeBuilder.shaped(DEContent.entity_detector)
                .pattern("ABA")
                .pattern("CDC")
                .pattern("EFE")
                .define('A', Tags.Items.GEMS_LAPIS)
                .define('B', Items.ENDER_EYE)
                .define('C', Tags.Items.DUSTS_REDSTONE)
                .define('D', DETags.Items.INGOTS_DRACONIUM)
                .define('E', Tags.Items.INGOTS_IRON)
                .define('F', DEContent.core_draconium)
                .unlockedBy("has_core_draconium", has(DEContent.core_draconium))
                .save(consumer);

        ShapedRecipeBuilder.shaped(DEContent.entity_detector_advanced)
                .pattern("ABA")
                .pattern("CDC")
                .pattern("EFE")
                .define('A', Tags.Items.STORAGE_BLOCKS_REDSTONE)
                .define('B', Items.SKELETON_SKULL)
                .define('C', Tags.Items.STORAGE_BLOCKS_LAPIS)
                .define('D', Tags.Items.GEMS_DIAMOND)
                .define('E', DETags.Items.INGOTS_DRACONIUM)
                .define('F', DEContent.entity_detector)
                .unlockedBy("has_core_draconium", has(DEContent.core_draconium))
                .save(consumer);

        ShapedRecipeBuilder.shaped(DEContent.fluid_gate)
                .pattern("ABA")
                .pattern("CDC")
                .pattern("AEA")
                .define('A', Tags.Items.INGOTS_IRON)
                .define('B', DEContent.potentiometer)
                .define('C', Items.BUCKET)
                .define('D', DEContent.core_draconium)
                .define('E', Items.COMPARATOR)
                .unlockedBy("has_dust_draconium", has(DEContent.dust_draconium))
                .save(consumer);

        ShapedRecipeBuilder.shaped(DEContent.flux_gate)
                .pattern("ABA")
                .pattern("CDC")
                .pattern("AEA")
                .define('A', Tags.Items.INGOTS_IRON)
                .define('B', DEContent.potentiometer)
                .define('C', Tags.Items.STORAGE_BLOCKS_REDSTONE)
                .define('D', DEContent.core_draconium)
                .define('E', Items.COMPARATOR)
                .unlockedBy("has_dust_draconium", has(DEContent.dust_draconium))
                .save(consumer);

        ShapedRecipeBuilder.shaped(DEContent.dislocation_inhibitor)
                .pattern("AAA")
                .pattern("BCB")
                .pattern("AAA")
                .define('A', Tags.Items.INGOTS_IRON)
                .define('B', Items.IRON_BARS)
                .define('C', DEContent.magnet)
                .unlockedBy("has_magnet", has(DEContent.magnet))
                .save(consumer);


        //        ShapedRecipeBuilder.shaped(info_tablet).pattern("AAA").pattern("ABA").pattern("AAA").define('A', Tags.Items.STONE).define('B', DUSTS_DRACONIUM).build(consumer);


    }

    private static void compress3x3(ItemLike output, ItemLike input, Consumer<FinishedRecipe> consumer) {
        ResourceLocation name = output.asItem().getRegistryName();
        ShapedRecipeBuilder.shaped(output)
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .define('#', input)
                .unlockedBy("has_" + input.asItem().getRegistryName().getPath(), has(input))
                .save(consumer, new ResourceLocation(name.getNamespace(), "compress/" + name.getPath()));
    }

    private static void compress3x3(ItemLike output, TagKey<Item> input, String inputName, Consumer<FinishedRecipe> consumer) {
        ResourceLocation name = output.asItem().getRegistryName();
        ShapedRecipeBuilder.shaped(output)
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .define('#', input)
                .unlockedBy("has_" + inputName, has(input))
                .save(consumer, new ResourceLocation(name.getNamespace(), "compress/" + name.getPath()));
    }

    private static void compress2x2(ItemLike output, ItemLike input, Consumer<FinishedRecipe> consumer) {
        ResourceLocation name = output.asItem().getRegistryName();
        ShapedRecipeBuilder.shaped(output)
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .define('#', input)
                .unlockedBy("has_" + input.asItem().getRegistryName().getPath(), has(input))
                .save(consumer, new ResourceLocation(name.getNamespace(), "compress/" + name.getPath()));
    }

    private static void deCompress(ItemLike output, int count, ItemLike from, Consumer<FinishedRecipe> consumer) {
        ResourceLocation name = output.asItem().getRegistryName();
        ShapelessRecipeBuilder.shapeless(output, count)
                .requires(from)
                .unlockedBy("has_" + from.asItem().getRegistryName().getPath(), has(from))
                .save(consumer, new ResourceLocation(name.getNamespace(), "decompress/" + name.getPath()));
    }

    private static void deCompress(ItemLike output, int count, TagKey<Item> from, String hasName, Consumer<FinishedRecipe> consumer) {
        ResourceLocation name = output.asItem().getRegistryName();
        ShapelessRecipeBuilder.shapeless(output, count)
                .requires(from)
                .unlockedBy("has_" + hasName, has(from))
                .save(consumer, new ResourceLocation(name.getNamespace(), "decompress/" + name.getPath()));
    }

    private static void deCompress(ItemLike output, ItemLike from, Consumer<FinishedRecipe> consumer) {
        deCompress(output, 9, from, consumer);
    }

    private static void deCompress(ItemLike output, TagKey<Item> from, String hasName, Consumer<FinishedRecipe> consumer) {
        deCompress(output, 9, from, hasName, consumer);
    }

    public static String folder(String folder, IForgeRegistryEntry<?> key) {
        return DraconicEvolution.MODID + ":" + folder + "/" + key.getRegistryName().getPath();
    }

    public static String folder(String folder, String name) {
        return DraconicEvolution.MODID + ":" + folder + "/" + name;
    }

    public static InventoryChangeTrigger.TriggerInstance has(TagKey<Item> p_206407_) {
        return inventoryTrigger(ItemPredicate.Builder.item().of(p_206407_).build());
    }

    @Override
    public void run(HashCache cache) {
        super.run(cache);
    }

    public static class NBTIngredient extends net.minecraftforge.common.crafting.NBTIngredient {
        public NBTIngredient(ItemStack stack) {
            super(stack);
        }
    }
}
