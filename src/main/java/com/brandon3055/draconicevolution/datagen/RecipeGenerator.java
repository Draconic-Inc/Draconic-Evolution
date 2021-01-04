package com.brandon3055.draconicevolution.datagen;

import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.api.crafting.IngredientStack;
import com.brandon3055.draconicevolution.init.DEContent;
import com.brandon3055.draconicevolution.init.DEModules;
import com.brandon3055.draconicevolution.init.DETags;
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
    protected void registerRecipes(Consumer<IFinishedRecipe> consumer) {
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

        CookingRecipeBuilder.smeltingRecipe(Ingredient.fromTag(DUSTS_DRACONIUM), ingot_draconium, 0, 200);
        CookingRecipeBuilder.smeltingRecipe(Ingredient.fromTag(ORES_DRACONIUM), ingot_draconium, 1, 200);

        ShapedRecipeBuilder.shapedRecipe(core_draconium)
                .patternLine("ABA")
                .patternLine("BCB")
                .patternLine("ABA")
                .key('A', INGOTS_DRACONIUM)
                .key('B', INGOTS_GOLD)
                .key('C', GEMS_DIAMOND)
                .addCriterion("has_draconium", hasItem(ingot_draconium))
                .build(consumer, folder("components", core_draconium));

        ShapedRecipeBuilder.shapedRecipe(core_wyvern)
                .patternLine("ABA")
                .patternLine("BCB")
                .patternLine("ABA")
                .key('A', INGOTS_DRACONIUM)
                .key('B', core_draconium)
                .key('C', NETHER_STARS)
                .addCriterion("has_core_draconium", hasItem(core_draconium))
                .build(consumer, folder("components", core_wyvern));

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
                .catalyst(chaos_shard)
                .energy(100000000)
                .techLevel(TechLevel.DRACONIC)
                .ingredient(INGOTS_DRACONIUM_AWAKENED)
                .ingredient(INGOTS_DRACONIUM_AWAKENED)
                .ingredient(core_awakened)
                .ingredient(core_awakened)
                .ingredient(INGOTS_DRACONIUM_AWAKENED)
                .ingredient(core_awakened)
                .ingredient(core_awakened)
                .ingredient(INGOTS_DRACONIUM_AWAKENED)
                .build(consumer, folder("components", core_chaotic));

        ShapedRecipeBuilder.shapedRecipe(energy_core_wyvern)
                .patternLine("ABA")
                .patternLine("BCB")
                .patternLine("ABA")
                .key('A', INGOTS_DRACONIUM)
                .key('B', Tags.Items.STORAGE_BLOCKS_REDSTONE)
                .key('C', core_draconium)
                .addCriterion("has_core_draconium", hasItem(core_draconium))
                .build(consumer, folder("components", energy_core_wyvern));

        ShapedRecipeBuilder.shapedRecipe(energy_core_draconic)
                .patternLine("ABA")
                .patternLine("BCB")
                .patternLine("ABA")
                .key('A', INGOTS_DRACONIUM_AWAKENED)
                .key('B', energy_core_wyvern)
                .key('C', core_wyvern)
                .addCriterion("has_core_wyvern", hasItem(core_wyvern))
                .build(consumer, folder("components", energy_core_draconic));

        ShapedRecipeBuilder.shapedRecipe(energy_core_chaotic)
                .patternLine("ABA")
                .patternLine("BCB")
                .patternLine("ABA")
                .key('A', chaos_frag_medium)
                .key('B', energy_core_draconic)
                .key('C', core_awakened)
                .addCriterion("has_core_awakened", hasItem(core_awakened))
                .build(consumer, folder("components", energy_core_chaotic));
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
                .key('B', INGOTS_DRACONIUM)
                .key('C', DIAMOND_SWORD)
                .key('D', core_draconium)
                .key('E', FURNACE)
                .addCriterion("has_core_draconium", hasItem(core_draconium))
                .build(consumer);

        ShapedRecipeBuilder.shapedRecipe(energy_transfuser)
                .patternLine("ABA")
                .patternLine("CDC")
                .patternLine("ACA")
                .key('A', INGOTS_DRACONIUM)
                .key('B', energy_core_stabilizer)
                .key('C', core_draconium)
                .key('D', ENCHANTING_TABLE)
                .addCriterion("has_core_draconium", hasItem(core_draconium))
                .build(consumer);

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
                .techLevel(TechLevel.DRACONIUM)
                .ingredient(FURNACE)
                .ingredient(core_draconium)
                .ingredient(FURNACE)
                .ingredient(core_draconium)
                .ingredient(FURNACE)
                .ingredient(CRAFTING_TABLE)
                .ingredient(FURNACE)
                .ingredient(STORAGE_BLOCKS_DRACONIUM)
                .ingredient(FURNACE)
                .ingredient(CRAFTING_TABLE)
                .build(consumer);

        ShapedRecipeBuilder.shapedRecipe(potentiometer)
                .patternLine(" A ")
                .patternLine("BCB")
                .patternLine("DDD")
                .key('A', PLANKS)
                .key('B', DUSTS_REDSTONE)
                .key('C', DUSTS_DRACONIUM)
                .key('D', STONE_SLAB)
                .addCriterion("has_STONE_SLAB", hasItem(STONE_SLAB))
                .build(consumer);


    }

    private static void energy(Consumer<IFinishedRecipe> consumer) {
        ShapedRecipeBuilder.shapedRecipe(energy_core)
                .patternLine("AAA")
                .patternLine("BCB")
                .patternLine("AAA")
                .key('A', INGOTS_DRACONIUM)
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
                .key('A', INGOTS_DRACONIUM)
                .key('B', ENDER_EYE)
                .key('C', GEMS_EMERALD)
                .key('D', core_draconium)
                .key('E', GEMS_DIAMOND)
                .addCriterion("has_core_wyvern", hasItem(core_wyvern))
                .build(consumer);

        //Reactor
        ShapedRecipeBuilder.shapedRecipe(reactor_prt_stab_frame)
                .patternLine("AAA")
                .patternLine("BC ")
                .patternLine("AAA")
                .key('A', INGOTS_IRON)
                .key('B', core_wyvern)
                .key('C', INGOTS_DRACONIUM_AWAKENED)
                .addCriterion("has_core_wyvern", hasItem(core_wyvern))
                .build(consumer);

        ShapedRecipeBuilder.shapedRecipe(reactor_prt_in_rotor)
                .patternLine("   ")
                .patternLine("AAA")
                .patternLine("BCC")
                .key('A', INGOTS_DRACONIUM_AWAKENED)
                .key('B', core_draconium)
                .key('C', INGOTS_DRACONIUM)
                .addCriterion("has_core_draconium", hasItem(core_draconium))
                .build(consumer);

        ShapedRecipeBuilder.shapedRecipe(reactor_prt_out_rotor)
                .patternLine("   ")
                .patternLine("AAA")
                .patternLine("BCC")
                .key('A', GEMS_DIAMOND)
                .key('B', core_draconium)
                .key('C', INGOTS_DRACONIUM)
                .addCriterion("has_core_draconium", hasItem(core_draconium))
                .build(consumer);

        ShapedRecipeBuilder.shapedRecipe(reactor_prt_rotor_full)
                .patternLine(" AB")
                .patternLine("CDD")
                .patternLine(" AB")
                .key('A', reactor_prt_in_rotor)
                .key('B', reactor_prt_out_rotor)
                .key('C', core_wyvern)
                .key('D', INGOTS_DRACONIUM)
                .addCriterion("has_core_wyvern", hasItem(core_wyvern))
                .build(consumer);

        ShapedRecipeBuilder.shapedRecipe(reactor_prt_focus_ring)
                .patternLine("ABA")
                .patternLine("CBC")
                .patternLine("ABA")
                .key('A', INGOTS_GOLD)
                .key('B', GEMS_DIAMOND)
                .key('C', core_wyvern)
                .addCriterion("has_core_wyvern", hasItem(core_wyvern))
                .build(consumer);

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
                .build(consumer);

        ShapedRecipeBuilder.shapedRecipe(crystal_binder)
                .patternLine(" AB")
                .patternLine(" CA")
                .patternLine("D  ")
                .key('A', INGOTS_DRACONIUM)
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

    private static void tools(Consumer<IFinishedRecipe> consumer) {
        ShapedRecipeBuilder.shapedRecipe(dislocator)
                .patternLine("ABA")
                .patternLine("BCB")
                .patternLine("ABA")
                .key('A', BLAZE_POWDER)
                .key('B', DUSTS_DRACONIUM)
                .key('C', ENDER_EYE)
                .addCriterion("has_dust_draconium", hasItem(dust_draconium))
                .build(consumer);

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

        ShapedRecipeBuilder.shapedRecipe(magnet)
                .patternLine("A A")
                .patternLine("B B")
                .patternLine("CDC")
                .key('A', DUSTS_REDSTONE)
                .key('B', INGOTS_DRACONIUM)
                .key('C', INGOTS_IRON)
                .key('D', dislocator)
                .addCriterion("has_dust_draconium", hasItem(dust_draconium))
                .build(consumer);

        ShapedRecipeBuilder.shapedRecipe(magnet_advanced)
                .patternLine("A A")
                .patternLine("B B")
                .patternLine("CDC")
                .key('A', INGOTS_DRACONIUM)
                .key('B', DUSTS_REDSTONE)
                .key('C', INGOTS_DRACONIUM_AWAKENED)
                .key('D', magnet)
                .addCriterion("has_dust_draconium", hasItem(dust_draconium))
                .build(consumer);
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
                .catalyst(DEContent.core_chaotic)
                .energy(128000000)
                .techLevel(TechLevel.CHAOTIC)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DEContent.capacitor_draconic)
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
                .ingredient(DEContent.core_awakened)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DEContent.chaos_frag_medium)
                .ingredient(DEContent.chaos_frag_medium)
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
                .ingredient(DEContent.core_awakened)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DEContent.chaos_frag_medium)
                .ingredient(DEContent.chaos_frag_medium)
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
                .ingredient(DEContent.core_awakened)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DEContent.chaos_frag_medium)
                .ingredient(DEContent.chaos_frag_medium)
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
                .ingredient(DEContent.core_awakened)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DEContent.chaos_frag_medium)
                .ingredient(DEContent.chaos_frag_medium)
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
                .ingredient(DEContent.core_awakened)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DEContent.chaos_frag_medium)
                .ingredient(DEContent.chaos_frag_medium)
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
                .ingredient(DEContent.core_awakened)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DEContent.chaos_frag_medium)
                .ingredient(DEContent.chaos_frag_medium)
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
                .ingredient(DEContent.core_awakened)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DEContent.chaos_frag_medium)
                .ingredient(DEContent.chaos_frag_medium)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DEContent.energy_core_chaotic)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .build(consumer, folder("tools", DEContent.chestpiece_chaotic));
    }

    private static void modules(Consumer<IFinishedRecipe> consumer) {
        ShapedRecipeBuilder.shapedRecipe(module_core)
                .patternLine("IRI")
                .patternLine("GDG")
                .patternLine("IRI")
                .key('I', INGOTS_IRON)
                .key('R', DUSTS_REDSTONE)
                .key('G', INGOTS_GOLD)
                .key('D', INGOTS_DRACONIUM)
                .addCriterion("has_ingot_draconium", hasItem(INGOTS_DRACONIUM))
                .build(consumer, folder("modules", module_core));

        //Energy
        ShapedRecipeBuilder.shapedRecipe(DEModules.draconiumEnergy.getItem())
                .patternLine("###")
                .patternLine("ABA")
                .patternLine("###")
                .key('#', STORAGE_BLOCKS_REDSTONE)
                .key('A', INGOTS_IRON)
                .key('B', module_core)
                .addCriterion("has_module_core", hasItem(module_core))
                .build(consumer, folder("modules", DEModules.draconiumEnergy));

        ShapedRecipeBuilder.shapedRecipe(DEModules.wyvernEnergy.getItem())
                .patternLine("###")
                .patternLine("ABA")
                .patternLine("###")
                .key('#', INGOTS_DRACONIUM)
                .key('A', DEModules.draconiumEnergy.getItem())
                .key('B', core_draconium)
                .addCriterion("has_module_core", hasItem(module_core))
                .build(consumer, folder("modules", DEModules.wyvernEnergy));

        ShapedRecipeBuilder.shapedRecipe(DEModules.draconicEnergy.getItem())
                .patternLine("###")
                .patternLine("ABA")
                .patternLine("###")
                .key('#', NUGGETS_DRACONIUM_AWAKENED)
                .key('A', DEModules.wyvernEnergy.getItem())
                .key('B', core_wyvern)
                .addCriterion("has_module_core", hasItem(module_core))
                .build(consumer, folder("modules", DEModules.draconicEnergy));

        ShapedRecipeBuilder.shapedRecipe(DEModules.chaoticEnergy.getItem())
                .patternLine("#C#")
                .patternLine("ABA")
                .patternLine("#C#")
                .key('#', chaos_frag_small)
                .key('A', DEModules.draconicEnergy.getItem())
                .key('B', core_awakened)
                .key('C', chaos_frag_medium)
                .addCriterion("has_module_core", hasItem(module_core))
                .build(consumer, folder("modules", DEModules.chaoticEnergy));

        //Speed
        ShapedRecipeBuilder.shapedRecipe(DEModules.draconiumSpeed.getItem())
                .patternLine("#P#")
                .patternLine("ABA")
                .patternLine("#P#")
                .key('#', INGOTS_IRON)
                .key('A', CLOCK)
                .key('B', module_core)
                .key('P', new NBTIngredient(PotionUtils.addPotionToItemStack(new ItemStack(POTION), Potions.SWIFTNESS)))
                .addCriterion("has_module_core", hasItem(module_core))
                .build(consumer, folder("modules", DEModules.draconiumSpeed));

        ShapedRecipeBuilder.shapedRecipe(DEModules.wyvernSpeed.getItem())
                .patternLine("###")
                .patternLine("ABA")
                .patternLine("###")
                .key('#', INGOTS_DRACONIUM)
                .key('A', DEModules.draconiumSpeed.getItem())
                .key('B', core_draconium)
                .addCriterion("has_module_core", hasItem(module_core))
                .build(consumer, folder("modules", DEModules.wyvernSpeed));

        ShapedRecipeBuilder.shapedRecipe(DEModules.draconicSpeed.getItem())
                .patternLine("###")
                .patternLine("ABA")
                .patternLine("###")
                .key('#', NUGGETS_DRACONIUM_AWAKENED)
                .key('A', DEModules.wyvernSpeed.getItem())
                .key('B', core_wyvern)
                .addCriterion("has_module_core", hasItem(module_core))
                .build(consumer, folder("modules", DEModules.draconicSpeed));

        ShapedRecipeBuilder.shapedRecipe(DEModules.chaoticSpeed.getItem())
                .patternLine("#C#")
                .patternLine("ABA")
                .patternLine("#C#")
                .key('#', chaos_frag_small)
                .key('A', DEModules.draconicSpeed.getItem())
                .key('B', core_awakened)
                .key('C', chaos_frag_medium)
                .addCriterion("has_module_core", hasItem(module_core))
                .build(consumer, folder("modules", DEModules.chaoticSpeed));

        //Damage
        ShapedRecipeBuilder.shapedRecipe(DEModules.draconiumDamage.getItem())
                .patternLine("IPG")
                .patternLine("ABA")
                .patternLine("GPI")
                .key('I', INGOTS_IRON)
                .key('P', new NBTIngredient(PotionUtils.addPotionToItemStack(new ItemStack(POTION), Potions.STRENGTH)))
                .key('G', INGOTS_GOLD)
                .key('A', DUSTS_GLOWSTONE)
                .key('B', module_core)
                .addCriterion("has_module_core", hasItem(module_core))
                .build(consumer, folder("modules", DEModules.draconiumDamage));

        ShapedRecipeBuilder.shapedRecipe(DEModules.wyvernDamage.getItem())
                .patternLine("IPI")
                .patternLine("ABA")
                .patternLine("IPI")
                .key('I', INGOTS_DRACONIUM)
                .key('P', new NBTIngredient(PotionUtils.addPotionToItemStack(new ItemStack(POTION), Potions.STRONG_STRENGTH)))
                .key('A', DEModules.draconiumDamage.getItem())
                .key('B', core_draconium)
                .addCriterion("has_module_core", hasItem(module_core))
                .build(consumer, folder("modules", DEModules.wyvernDamage));

        ShapedRecipeBuilder.shapedRecipe(DEModules.draconicDamage.getItem())
                .patternLine("IPI")
                .patternLine("ABA")
                .patternLine("IPI")
                .key('I', NUGGETS_DRACONIUM_AWAKENED)
                .key('P', DRAGON_BREATH)
                .key('A', DEModules.wyvernDamage.getItem())
                .key('B', core_wyvern)
                .addCriterion("has_module_core", hasItem(module_core))
                .build(consumer, folder("modules", DEModules.draconicDamage));

        ShapedRecipeBuilder.shapedRecipe(DEModules.chaoticDamage.getItem())
                .patternLine("IPI")
                .patternLine("ABA")
                .patternLine("IPI")
                .key('I', chaos_frag_small)
                .key('P', chaos_frag_medium)
                .key('A', DEModules.draconicDamage.getItem())
                .key('B', core_awakened)
                .addCriterion("has_module_core", hasItem(module_core))
                .build(consumer, folder("modules", DEModules.chaoticDamage));

        //AOE
        ShapedRecipeBuilder.shapedRecipe(DEModules.draconiumAOE.getItem())
                .patternLine("#I#")
                .patternLine("ABA")
                .patternLine("#I#")
                .key('#', PISTON)
                .key('I', INGOTS_DRACONIUM)
                .key('A', core_draconium)
                .key('B', module_core)
                .addCriterion("has_module_core", hasItem(module_core))
                .build(consumer, folder("modules", DEModules.draconiumAOE));

        ShapedRecipeBuilder.shapedRecipe(DEModules.wyvernAOE.getItem())
                .patternLine("#I#")
                .patternLine("ABA")
                .patternLine("#I#")
                .key('#', INGOTS_DRACONIUM)
                .key('I', NETHERITE_SCRAP)
                .key('A', DEModules.draconiumAOE.getItem())
                .key('B', core_wyvern)
                .addCriterion("has_module_core", hasItem(module_core))
                .build(consumer, folder("modules", DEModules.wyvernAOE));

        ShapedRecipeBuilder.shapedRecipe(DEModules.draconicAOE.getItem())
                .patternLine("#I#")
                .patternLine("ABA")
                .patternLine("#I#")
                .key('#', INGOTS_NETHERITE)
                .key('I', INGOTS_DRACONIUM_AWAKENED)
                .key('A', DEModules.wyvernAOE.getItem())
                .key('B', core_awakened)
                .addCriterion("has_module_core", hasItem(module_core))
                .build(consumer, folder("modules", DEModules.draconicAOE));

        ShapedRecipeBuilder.shapedRecipe(DEModules.chaoticAOE.getItem())
                .patternLine("#I#")
                .patternLine("ABA")
                .patternLine("#I#")
                .key('#', INGOTS_NETHERITE)
                .key('I', INGOTS_DRACONIUM_AWAKENED)
                .key('A', DEModules.draconicAOE.getItem())
                .key('B', core_chaotic)
                .addCriterion("has_module_core", hasItem(module_core))
                .build(consumer, folder("modules", DEModules.chaoticAOE));

        //Mining Stability
        ShapedRecipeBuilder.shapedRecipe(DEModules.wyvernMiningStability.getItem())
                .patternLine("#C#")
                .patternLine("ABA")
                .patternLine("#D#")
                .key('#', INGOTS_DRACONIUM)
                .key('A', core_draconium)
                .key('B', module_core)
                .key('C', PHANTOM_MEMBRANE)
                .key('D', GOLDEN_PICKAXE)
                .addCriterion("has_module_core", hasItem(module_core))
                .build(consumer, folder("modules", DEModules.wyvernMiningStability));

        ShapedRecipeBuilder.shapedRecipe(DEModules.wyvernJunkFilter.getItem())
                .patternLine("#C#")
                .patternLine("ABA")
                .patternLine("#D#")
                .key('#', INGOTS_DRACONIUM)
                .key('A', core_draconium)
                .key('B', module_core)
                .key('C', LAVA_BUCKET)
                .key('D', DUSTS_REDSTONE)
                .addCriterion("has_module_core", hasItem(module_core))
                .build(consumer, folder("modules", DEModules.wyvernJunkFilter));

        //Shield Controller
        ShapedRecipeBuilder.shapedRecipe(DEModules.wyvernShieldControl.getItem())
                .patternLine("#C#")
                .patternLine("ABA")
                .patternLine("#D#")
                .key('#', GEMS_DIAMOND)
                .key('A', core_wyvern)
                .key('B', module_core)
                .key('C', dragon_heart)
                .key('D', particle_generator)
                .addCriterion("has_module_core", hasItem(module_core))
                .build(consumer, folder("modules", DEModules.wyvernShieldControl));

        ShapedRecipeBuilder.shapedRecipe(DEModules.draconicShieldControl.getItem())
                .patternLine("#I#")
                .patternLine("ABA")
                .patternLine("#I#")
                .key('#', GEMS_EMERALD)
                .key('A', core_awakened)
                .key('B', DEModules.wyvernShieldControl.getItem())
                .key('I', INGOTS_NETHERITE)
                .addCriterion("has_module_core", hasItem(module_core))
                .build(consumer, folder("modules", DEModules.draconicShieldControl));

        ShapedRecipeBuilder.shapedRecipe(DEModules.chaoticShieldControl.getItem())
                .patternLine("#I#")
                .patternLine("ABA")
                .patternLine("#I#")
                .key('#', NETHER_STARS)
                .key('A', core_chaotic)
                .key('B', DEModules.draconicShieldControl.getItem())
                .key('I', INGOTS_NETHERITE)
                .addCriterion("has_module_core", hasItem(module_core))
                .build(consumer, folder("modules", DEModules.chaoticShieldControl));

        //Shield Capacity
        ShapedRecipeBuilder.shapedRecipe(DEModules.wyvernShieldCapacity.getItem())
                .patternLine("#I#")
                .patternLine("ABA")
                .patternLine("#I#")
                .key('#', INGOTS_DRACONIUM)
                .key('A', DUSTS_GLOWSTONE)
                .key('B', module_core)
                .key('I', NETHERITE_SCRAP)
                .addCriterion("has_wyvern_shield", hasItem(DEModules.wyvernShieldControl.getItem()))
                .build(consumer, folder("modules", DEModules.wyvernShieldCapacity));

        ShapedRecipeBuilder.shapedRecipe(DEModules.draconicShieldCapacity.getItem())
                .patternLine("#C#")
                .patternLine("ABA")
                .patternLine("#D#")
                .key('#', INGOTS_NETHERITE)
                .key('A', INGOTS_DRACONIUM_AWAKENED)
                .key('B', DEModules.wyvernShieldCapacity.getItem())
                .key('C', core_draconium)
                .key('D', core_wyvern)
                .addCriterion("has_draconic_shield", hasItem(DEModules.draconicShieldControl.getItem()))
                .build(consumer, folder("modules", DEModules.draconicShieldCapacity));

        ShapedRecipeBuilder.shapedRecipe(DEModules.chaoticShieldCapacity.getItem())
                .patternLine("#C#")
                .patternLine("ABA")
                .patternLine("#D#")
                .key('#', INGOTS_DRACONIUM_AWAKENED)
                .key('A', chaos_frag_medium)
                .key('B', DEModules.draconicShieldCapacity.getItem())
                .key('C', core_wyvern)
                .key('D', core_chaotic)
                .addCriterion("has_chaotic_shield", hasItem(DEModules.chaoticShieldControl.getItem()))
                .build(consumer, folder("modules", DEModules.chaoticShieldCapacity));

        //Shield Capacity XL
        ShapedRecipeBuilder.shapedRecipe(DEModules.wyvernLargeShieldCapacity.getItem())
                .patternLine("#A#")
                .patternLine("A#A")
                .patternLine("#A#")
                .key('#', DEModules.wyvernShieldCapacity.getItem())
                .key('A', core_draconium)
                .addCriterion("has_wyvern_shield", hasItem(DEModules.wyvernShieldControl.getItem()))
                .build(consumer, folder("modules", DEModules.wyvernLargeShieldCapacity));

        ShapedRecipeBuilder.shapedRecipe(DEModules.draconicLargeShieldCapacity.getItem())
                .patternLine("#A#")
                .patternLine("A#A")
                .patternLine("#A#")
                .key('#', DEModules.draconicShieldCapacity.getItem())
                .key('A', core_draconium)
                .addCriterion("has_draconic_shield", hasItem(DEModules.draconicShieldControl.getItem()))
                .build(consumer, folder("modules", DEModules.draconicLargeShieldCapacity));

        ShapedRecipeBuilder.shapedRecipe(DEModules.chaoticLargeShieldCapacity.getItem())
                .patternLine("#A#")
                .patternLine("A#A")
                .patternLine("#A#")
                .key('#', DEModules.chaoticShieldCapacity.getItem())
                .key('A', core_draconium)
                .addCriterion("has_chaotic_shield", hasItem(DEModules.chaoticShieldControl.getItem()))
                .build(consumer, folder("modules", DEModules.chaoticLargeShieldCapacity));

        ShapelessRecipeBuilder.shapelessRecipe(DEModules.wyvernShieldCapacity.getItem(), 5)
                .addIngredient(DEModules.wyvernLargeShieldCapacity.getItem())
                .addCriterion("has_wyvern_shield", hasItem(DEModules.wyvernShieldControl.getItem()))
                .build(consumer, DraconicEvolution.MODID + ":modules/uncraft_" + DEModules.wyvernShieldCapacity.getRegistryName().getPath());

        ShapelessRecipeBuilder.shapelessRecipe(DEModules.draconicShieldCapacity.getItem(), 5)
                .addIngredient(DEModules.draconicLargeShieldCapacity.getItem())
                .addCriterion("has_draconic_shield", hasItem(DEModules.draconicShieldControl.getItem()))
                .build(consumer, DraconicEvolution.MODID + ":modules/uncraft_" + DEModules.draconicShieldCapacity.getRegistryName().getPath());

        ShapelessRecipeBuilder.shapelessRecipe(DEModules.chaoticShieldCapacity.getItem(), 5)
                .addIngredient(DEModules.chaoticLargeShieldCapacity.getItem())
                .addCriterion("has_chaotic_shield", hasItem(DEModules.chaoticShieldControl.getItem()))
                .build(consumer, DraconicEvolution.MODID + ":modules/uncraft_" + DEModules.chaoticShieldCapacity.getRegistryName().getPath());

        //Shield Recovery
        ShapedRecipeBuilder.shapedRecipe(DEModules.wyvernShieldRecovery.getItem())
                .patternLine("#I#")
                .patternLine("ABA")
                .patternLine("#I#")
                .key('#', INGOTS_DRACONIUM)
                .key('A', DUSTS_REDSTONE)
                .key('B', module_core)
                .key('I', NETHERITE_SCRAP)
                .addCriterion("has_wyvern_shield", hasItem(DEModules.wyvernShieldControl.getItem()))
                .build(consumer, folder("modules", DEModules.wyvernShieldRecovery));

        ShapedRecipeBuilder.shapedRecipe(DEModules.draconicShieldRecovery.getItem())
                .patternLine("#C#")
                .patternLine("ABA")
                .patternLine("#D#")
                .key('#', INGOTS_NETHERITE)
                .key('A', INGOTS_DRACONIUM_AWAKENED)
                .key('B', DEModules.wyvernShieldRecovery.getItem())
                .key('C', core_draconium)
                .key('D', core_wyvern)
                .addCriterion("has_draconic_shield", hasItem(DEModules.draconicShieldControl.getItem()))
                .build(consumer, folder("modules", DEModules.draconicShieldRecovery));

        ShapedRecipeBuilder.shapedRecipe(DEModules.chaoticShieldRecovery.getItem())
                .patternLine("#C#")
                .patternLine("ABA")
                .patternLine("#D#")
                .key('#', INGOTS_DRACONIUM_AWAKENED)
                .key('A', chaos_frag_medium)
                .key('B', DEModules.draconicShieldRecovery.getItem())
                .key('C', core_wyvern)
                .key('D', core_chaotic)
                .addCriterion("has_chaotic_shield", hasItem(DEModules.chaoticShieldControl.getItem()))
                .build(consumer, folder("modules", DEModules.chaoticShieldRecovery));

        //Flight
        ShapedRecipeBuilder.shapedRecipe(DEModules.wyvernFlight.getItem())
                .patternLine("#C#")
                .patternLine("ABA")
                .patternLine("#D#")
                .key('#', INGOTS_DRACONIUM)
                .key('C', ELYTRA)
                .key('A', core_draconium)
                .key('B', module_core)
                .key('D', FIREWORK_ROCKET)
                .addCriterion("has_module_core", hasItem(module_core))
                .build(consumer, folder("modules", DEModules.wyvernFlight));

        ShapedRecipeBuilder.shapedRecipe(DEModules.draconicFlight.getItem())
                .patternLine("#C#")
                .patternLine("ABA")
                .patternLine("#D#")
                .key('#', INGOTS_DRACONIUM_AWAKENED)
                .key('A', core_wyvern)
                .key('B', DEModules.wyvernFlight.getItem())
                .key('C', new NBTIngredient(PotionUtils.addPotionToItemStack(new ItemStack(POTION), Potions.SLOW_FALLING)))
                .key('D', FIREWORK_ROCKET)
                .addCriterion("has_module_core", hasItem(module_core))
                .build(consumer, folder("modules", DEModules.draconicFlight));

        ShapedRecipeBuilder.shapedRecipe(DEModules.chaoticFlight.getItem())
                .patternLine("#C#")
                .patternLine("ABA")
                .patternLine("#C#")
                .key('#', new NBTIngredient(PotionUtils.addPotionToItemStack(new ItemStack(POTION), Potions.STRONG_SWIFTNESS)))
                .key('A', core_awakened)
                .key('B', DEModules.draconicFlight.getItem())
                .key('C', chaos_frag_medium)
                .addCriterion("has_module_core", hasItem(module_core))
                .build(consumer, folder("modules", DEModules.chaoticFlight));

        //Last Stand
        ShapedRecipeBuilder.shapedRecipe(DEModules.wyvernLastStand.getItem())
                .patternLine("#C#")
                .patternLine("ABA")
                .patternLine("#D#")
                .key('#', INGOTS_DRACONIUM)
                .key('A', core_draconium)
                .key('B', module_core)
                .key('C', TOTEM_OF_UNDYING)
                .key('D', DEModules.wyvernShieldCapacity.getItem())
                .addCriterion("has_module_core", hasItem(module_core))
                .build(consumer, folder("modules", DEModules.wyvernLastStand));

        ShapedRecipeBuilder.shapedRecipe(DEModules.draconicLastStand.getItem())
                .patternLine("#C#")
                .patternLine("ABA")
                .patternLine("#D#")
                .key('#', INGOTS_DRACONIUM_AWAKENED)
                .key('A', core_wyvern)
                .key('B', DEModules.wyvernLastStand.getItem())
                .key('C', new NBTIngredient(PotionUtils.addPotionToItemStack(new ItemStack(POTION), Potions.STRONG_HEALING)))
                .key('D', DEModules.draconicShieldCapacity.getItem())
                .addCriterion("has_module_core", hasItem(module_core))
                .build(consumer, folder("modules", DEModules.draconicLastStand));

        ShapedRecipeBuilder.shapedRecipe(DEModules.chaoticLastStand.getItem())
                .patternLine("#C#")
                .patternLine("ABA")
                .patternLine("#D#")
                .key('#', chaos_frag_medium)
                .key('A', core_awakened)
                .key('B', DEModules.draconicLastStand.getItem())
                .key('C', ENCHANTED_GOLDEN_APPLE)
                .key('D', DEModules.chaoticShieldCapacity.getItem())
                .addCriterion("has_module_core", hasItem(module_core))
                .build(consumer, folder("modules", DEModules.chaoticLastStand));

        //Auto Feed
        ShapedRecipeBuilder.shapedRecipe(DEModules.draconiumAutoFeed.getItem())
                .patternLine("#C#")
                .patternLine("ABA")
                .patternLine("#D#")
                .key('#', INGOTS_IRON)
                .key('A', COOKIE)
                .key('B', module_core)
                .key('C', GOLDEN_APPLE)
                .key('D', core_draconium)
                .addCriterion("has_module_core", hasItem(module_core))
                .build(consumer, folder("modules", DEModules.draconiumAutoFeed));

        ShapedRecipeBuilder.shapedRecipe(DEModules.wyvernAutoFeed.getItem())
                .patternLine("#C#")
                .patternLine("ABA")
                .patternLine("#C#")
                .key('#', INGOTS_DRACONIUM)
                .key('A', core_draconium)
                .key('B', DEModules.draconiumAutoFeed.getItem())
                .key('C', COOKIE)
                .addCriterion("has_module_core", hasItem(module_core))
                .build(consumer, folder("modules", DEModules.wyvernAutoFeed));

        ShapedRecipeBuilder.shapedRecipe(DEModules.draconicAutoFeed.getItem())
                .patternLine("#C#")
                .patternLine("ABA")
                .patternLine("#C#")
                .key('#', NUGGETS_DRACONIUM_AWAKENED)
                .key('A', core_draconium)
                .key('B', DEModules.wyvernAutoFeed.getItem())
                .key('C', COOKIE)
                .addCriterion("has_module_core", hasItem(module_core))
                .build(consumer, folder("modules", DEModules.draconicAutoFeed));

        //Night Vision
        ShapedRecipeBuilder.shapedRecipe(DEModules.wyvernNightVision.getItem())
                .patternLine("#P#")
                .patternLine("ABA")
                .patternLine("#P#")
                .key('#', INGOTS_DRACONIUM)
                .key('A', core_draconium)
                .key('B', module_core)
                .key('P', new NBTIngredient(PotionUtils.addPotionToItemStack(new ItemStack(POTION), Potions.NIGHT_VISION)))
                .addCriterion("has_module_core", hasItem(module_core))
                .build(consumer, folder("modules", DEModules.wyvernNightVision));

        //Jump Boost
        ShapedRecipeBuilder.shapedRecipe(DEModules.draconiumJump.getItem())
                .patternLine("CPD")
                .patternLine("ABA")
                .patternLine("DPC")
                .key('A', DUSTS_GLOWSTONE)
                .key('B', module_core)
                .key('C', INGOTS_IRON)
                .key('D', INGOTS_GOLD)
                .key('P', new NBTIngredient(PotionUtils.addPotionToItemStack(new ItemStack(POTION), Potions.LEAPING)))
                .addCriterion("has_module_core", hasItem(module_core))
                .build(consumer, folder("modules", DEModules.draconiumJump));

        ShapedRecipeBuilder.shapedRecipe(DEModules.wyvernJump.getItem())
                .patternLine("#P#")
                .patternLine("ABA")
                .patternLine("#P#")
                .key('#', INGOTS_DRACONIUM)
                .key('B', core_draconium)
                .key('A', DEModules.draconiumJump.getItem())
                .key('P', new NBTIngredient(PotionUtils.addPotionToItemStack(new ItemStack(POTION), Potions.STRONG_LEAPING)))
                .addCriterion("has_module_core", hasItem(module_core))
                .build(consumer, folder("modules", DEModules.wyvernJump));

        ShapedRecipeBuilder.shapedRecipe(DEModules.draconicJump.getItem())
                .patternLine("###")
                .patternLine("ABA")
                .patternLine("###")
                .key('#', NUGGETS_DRACONIUM_AWAKENED)
                .key('B', core_wyvern)
                .key('A', DEModules.wyvernJump.getItem())
                .addCriterion("has_module_core", hasItem(module_core))
                .build(consumer, folder("modules", DEModules.draconicJump));

        ShapedRecipeBuilder.shapedRecipe(DEModules.chaoticJump.getItem())
                .patternLine("#C#")
                .patternLine("ABA")
                .patternLine("#C#")
                .key('#', chaos_frag_small)
                .key('B', core_awakened)
                .key('A', DEModules.draconicJump.getItem())
                .key('C', chaos_frag_medium)
                .addCriterion("has_module_core", hasItem(module_core))
                .build(consumer, folder("modules", DEModules.chaoticJump));

        //Aqua
        ShapedRecipeBuilder.shapedRecipe(DEModules.wyvernAquaAdapt.getItem())
                .patternLine("#C#")
                .patternLine("ABA")
                .patternLine("#D#")
                .key('#', INGOTS_DRACONIUM)
                .key('A', core_draconium)
                .key('B', module_core)
                .key('C', HEART_OF_THE_SEA)
                .key('D', IRON_PICKAXE)
                .addCriterion("has_module_core", hasItem(module_core))
                .build(consumer, folder("modules", DEModules.wyvernAquaAdapt));

        //Hill Step
        ShapedRecipeBuilder.shapedRecipe(DEModules.wyvernHillStep.getItem())
                .patternLine("#C#")
                .patternLine("ABA")
                .patternLine("D#D")
                .key('#', INGOTS_DRACONIUM)
                .key('A', core_draconium)
                .key('B', module_core)
                .key('C', GOLDEN_BOOTS)
                .key('D', PISTON)
                .addCriterion("has_module_core", hasItem(module_core))
                .build(consumer, folder("modules", DEModules.wyvernHillStep));
    }

    private static void unsorted(Consumer<IFinishedRecipe> consumer) {

        ShapedRecipeBuilder.shapedRecipe(infused_obsidian)
                .patternLine("ABA")
                .patternLine("BCB")
                .patternLine("ABA")
                .key('A', BLAZE_POWDER)
                .key('B', Tags.Items.OBSIDIAN)
                .key('C', DUSTS_DRACONIUM)
                .addCriterion("has_dust_draconium", hasItem(dust_draconium))
                .build(consumer);


//        FusionRecipeBuilder.fusionRecipe(ender_energy_manipulator).catalyst(SKELETON_SKULL).energy(12000000).techLevel(WYVERN).ingredient(ENDER_EYE).ingredient(ENDER_EYE).ingredient(ENDER_EYE).ingredient(ENDER_EYE).ingredient(ENDER_EYE).ingredient(ENDER_EYE).ingredient(core_draconium).ingredient(core_wyvern).ingredient(core_draconium).ingredient(ENDER_EYE).build(consumer);


//        ShapedRecipeBuilder.shapedRecipe(dislocator_receptacle).patternLine("ABA").patternLine(" C ").patternLine("A A").key('A', INGOTS_IRON).key('B', core_draconium).key('C', infused_obsidian).build(consumer);
//        ShapedRecipeBuilder.shapedRecipe(dislocator_pedestal).patternLine(" A ").patternLine(" B ").patternLine("CDC").key('A', STONE_PRESSURE_PLATE).key('B', Tags.Items.STONE).key('C', STONE_SLAB).key('D', BLAZE_POWDER).build(consumer);


//        ShapedRecipeBuilder.shapedRecipe(rain_sensor).patternLine(" A ").patternLine("BCB").patternLine("DDD").key('A', BUCKET).key('B', DUSTS_REDSTONE).key('C', STONE_PRESSURE_PLATE).key('D', STONE_SLAB).build(consumer);
//        ShapedRecipeBuilder.shapedRecipe(disenchanter).patternLine("ABA").patternLine("CDC").patternLine("EEE").key('A', GEMS_EMERALD).key('B', core_draconium).key('C', ENCHANTED_BOOK).key('D', ENCHANTING_TABLE).key('E', BOOKSHELF).build(consumer);
//        ShapedRecipeBuilder.shapedRecipe(celestial_manipulator).patternLine("ABA").patternLine("CDC").patternLine("EFE").key('A', STORAGE_BLOCKS_REDSTONE).key('B', CLOCK).key('C', INGOTS_DRACONIUM).key('D', DRAGON_EGG).key('E', INGOTS_IRON).key('F', core_wyvern).build(consumer);
//
//        ShapedRecipeBuilder.shapedRecipe(entity_detector).patternLine("ABA").patternLine("CDC").patternLine("EFE").key('A', GEMS_LAPIS).key('B', ENDER_EYE).key('C', DUSTS_REDSTONE).key('D', INGOTS_DRACONIUM).key('E', INGOTS_IRON).key('F', core_draconium).build(consumer);
//        ShapedRecipeBuilder.shapedRecipe(entity_detector_advanced).patternLine("ABA").patternLine("CDC").patternLine("EFE").key('A', STORAGE_BLOCKS_REDSTONE).key('B', SKELETON_SKULL).key('C', STORAGE_BLOCKS_LAPIS).key('D', GEMS_DIAMOND).key('E', INGOTS_DRACONIUM).key('F', entity_detector).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(fluid_gate)
                .patternLine("ABA")
                .patternLine("CDC")
                .patternLine("AEA")
                .key('A', INGOTS_IRON)
                .key('B', potentiometer)
                .key('C', BUCKET)
                .key('D', core_draconium)
                .key('E', COMPARATOR)
                .addCriterion("has_dust_draconium", hasItem(dust_draconium))
                .build(consumer);

        ShapedRecipeBuilder.shapedRecipe(flux_gate)
                .patternLine("ABA")
                .patternLine("CDC")
                .patternLine("AEA")
                .key('A', INGOTS_IRON)
                .key('B', potentiometer)
                .key('C', STORAGE_BLOCKS_REDSTONE)
                .key('D', core_draconium)
                .key('E', COMPARATOR)
                .addCriterion("has_dust_draconium", hasItem(dust_draconium))
                .build(consumer);
//        ShapedRecipeBuilder.shapedRecipe(dislocation_inhibitor).patternLine("AAA").patternLine("BCB").patternLine("AAA").key('A', INGOTS_IRON).key('B', IRON_BARS).key('C', magnet).build(consumer);


//        ShapedRecipeBuilder.shapedRecipe(info_tablet).patternLine("AAA").patternLine("ABA").patternLine("AAA").key('A', Tags.Items.STONE).key('B', DUSTS_DRACONIUM).build(consumer);


    }

    private static void compress3x3(IItemProvider output, IItemProvider input, Consumer<IFinishedRecipe> consumer) {
        ResourceLocation name = output.asItem().getRegistryName();
        ShapedRecipeBuilder.shapedRecipe(output)
                .patternLine("###")
                .patternLine("###")
                .patternLine("###")
                .key('#', input)
                .addCriterion("has_" + input.asItem().getRegistryName().getPath(), hasItem(input))
                .build(consumer, new ResourceLocation(name.getNamespace(), "compress/" + name.getPath()));
    }

    private static void compress3x3(IItemProvider output, ITag<Item> input, String inputName, Consumer<IFinishedRecipe> consumer) {
        ResourceLocation name = output.asItem().getRegistryName();
        ShapedRecipeBuilder.shapedRecipe(output)
                .patternLine("###")
                .patternLine("###")
                .patternLine("###")
                .key('#', input)
                .addCriterion("has_" + inputName, hasItem(input))
                .build(consumer, new ResourceLocation(name.getNamespace(), "compress/" + name.getPath()));
    }

    private static void compress2x2(IItemProvider output, IItemProvider input, Consumer<IFinishedRecipe> consumer) {
        ResourceLocation name = output.asItem().getRegistryName();
        ShapedRecipeBuilder.shapedRecipe(output)
                .patternLine("###")
                .patternLine("###")
                .patternLine("###")
                .key('#', input)
                .addCriterion("has_" + input.asItem().getRegistryName().getPath(), hasItem(input))
                .build(consumer, new ResourceLocation(name.getNamespace(), "compress/" + name.getPath()));
    }

    private static void deCompress(IItemProvider output, int count, IItemProvider from, Consumer<IFinishedRecipe> consumer) {
        ResourceLocation name = output.asItem().getRegistryName();
        ShapelessRecipeBuilder.shapelessRecipe(output, count)
                .addIngredient(from)
                .addCriterion("has_" + from.asItem().getRegistryName().getPath(), hasItem(from))
                .build(consumer, new ResourceLocation(name.getNamespace(), "decompress/" + name.getPath()));
    }

    private static void deCompress(IItemProvider output, int count, ITag<Item> from, String hasName, Consumer<IFinishedRecipe> consumer) {
        ResourceLocation name = output.asItem().getRegistryName();
        ShapelessRecipeBuilder.shapelessRecipe(output, count)
                .addIngredient(from)
                .addCriterion("has_" + hasName, hasItem(from))
                .build(consumer, new ResourceLocation(name.getNamespace(), "decompress/" + name.getPath()));
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
    public void act(DirectoryCache cache) throws IOException {
        super.act(cache);
    }

    public static class NBTIngredient extends net.minecraftforge.common.crafting.NBTIngredient {
        public NBTIngredient(ItemStack stack) {
            super(stack);
        }
    }
}
