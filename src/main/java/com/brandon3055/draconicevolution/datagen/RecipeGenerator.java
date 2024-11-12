package com.brandon3055.draconicevolution.datagen;

import codechicken.lib.datagen.recipe.FurnaceRecipeBuilder;
import codechicken.lib.datagen.recipe.RecipeProvider;
import codechicken.lib.datagen.recipe.ShapedRecipeBuilder;
import codechicken.lib.datagen.recipe.ShapelessRecipeBuilder;
import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.draconicevolution.init.DEContent;
import com.brandon3055.draconicevolution.init.DEModules;
import com.brandon3055.draconicevolution.init.DETags;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
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
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.crafting.NBTIngredient;

import java.util.function.Function;
import java.util.function.Supplier;

import static com.brandon3055.draconicevolution.DraconicEvolution.MODID;

/**
 * Created by brandon3055 on 1/12/20
 */
public class RecipeGenerator extends RecipeProvider {

    public RecipeGenerator(PackOutput pOutput) {
        super(pOutput, MODID);
    }

    @Override
    protected void registerRecipes() {
        components();
        compressDecompress();
        machines();
        energy();
        tools();
        equipment();
        modules();
        unsorted();

        fusionRecipe(DEContent.ITEM_AWAKENED_DRACONIUM_BLOCK, 4)
                .catalyst(4, DETags.Items.STORAGE_BLOCKS_DRACONIUM)
                .energy(50000000)
                .techLevel(TechLevel.WYVERN)
                .ingredient(DEContent.CORE_DRACONIUM)
                .ingredient(DEContent.CORE_DRACONIUM)
                .ingredient(DEContent.CORE_DRACONIUM)
                .ingredient(DEContent.DRAGON_HEART)
                .ingredient(DEContent.CORE_DRACONIUM)
                .ingredient(DEContent.CORE_DRACONIUM)
                .ingredient(DEContent.CORE_DRACONIUM);
    }

    @Override
    public String getName() {
        return "Recipes";
    }


    private void components() {

        smelting(DEContent.INGOT_DRACONIUM, "components", s -> s + "_from_dust")
                .ingredient(Ingredient.of(DETags.Items.DUSTS_DRACONIUM))
                .cookingTime(200)
                .experience(0);

        smelting(DEContent.INGOT_DRACONIUM, "components", s -> s + "_from_ore")
                .ingredient(Ingredient.of(DETags.Items.ORES_DRACONIUM))
                .cookingTime(200)
                .experience(1);

        smelting(DEContent.INGOT_DRACONIUM_AWAKENED, "components", s -> s + "_from_dust")
                .ingredient(Ingredient.of(DETags.Items.DUSTS_DRACONIUM_AWAKENED))
                .cookingTime(200)
                .experience(0);

        shapedRecipe(DEContent.CORE_DRACONIUM, "components")
                .patternLine("ABA")
                .patternLine("BCB")
                .patternLine("ABA")
                .key('A', DETags.Items.INGOTS_DRACONIUM)
                .key('B', Tags.Items.INGOTS_GOLD)
                .key('C', Tags.Items.GEMS_DIAMOND);

        shapedRecipe(DEContent.CORE_WYVERN, "components")
                .patternLine("ABA")
                .patternLine("BCB")
                .patternLine("ABA")
                .key('A', DETags.Items.INGOTS_DRACONIUM)
                .key('B', DEContent.CORE_DRACONIUM)
                .key('C', Tags.Items.NETHER_STARS);

        fusionRecipe(DEContent.CORE_AWAKENED, "components")
                .catalyst(Tags.Items.NETHER_STARS)
                .energy(1000000)
                .techLevel(TechLevel.WYVERN)
                .ingredient(DEContent.CORE_WYVERN)
                .ingredient(DEContent.CORE_WYVERN)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DEContent.CORE_WYVERN)
                .ingredient(DEContent.CORE_WYVERN);

        fusionRecipe(DEContent.CORE_CHAOTIC, "components")
                .catalyst(DEContent.CHAOS_FRAG_LARGE)
                .energy(100000000)
                .techLevel(TechLevel.DRACONIC)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DEContent.CORE_AWAKENED)
                .ingredient(DEContent.CORE_AWAKENED)
                .ingredient(DEContent.CHAOS_FRAG_LARGE)
                .ingredient(DEContent.CHAOS_FRAG_LARGE)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DEContent.CORE_AWAKENED)
                .ingredient(DEContent.CORE_AWAKENED)
                .ingredient(DEContent.CHAOS_FRAG_LARGE)
                .ingredient(DEContent.CHAOS_FRAG_LARGE)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED);

        shapedRecipe(DEContent.ENERGY_CORE_WYVERN, "components")
                .patternLine("ABA")
                .patternLine("BCB")
                .patternLine("ABA")
                .key('A', DETags.Items.INGOTS_DRACONIUM)
                .key('B', Tags.Items.STORAGE_BLOCKS_REDSTONE)
                .key('C', DEContent.CORE_DRACONIUM);

        shapedRecipe(DEContent.ENERGY_CORE_DRACONIC, "components")
                .patternLine("ABA")
                .patternLine("BCB")
                .patternLine("ABA")
                .key('A', DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .key('B', DEContent.ENERGY_CORE_WYVERN)
                .key('C', DEContent.CORE_WYVERN);

        shapedRecipe(DEContent.ENERGY_CORE_CHAOTIC, "components")
                .patternLine("ABA")
                .patternLine("BCB")
                .patternLine("ABA")
                .key('A', DEContent.CHAOS_FRAG_MEDIUM)
                .key('B', DEContent.ENERGY_CORE_DRACONIC)
                .key('C', DEContent.CORE_AWAKENED);
    }

    private void compressDecompress() {
        compress3x3(DEContent.INGOT_DRACONIUM, DETags.Items.NUGGETS_DRACONIUM);
        compress3x3(DEContent.INGOT_DRACONIUM_AWAKENED, DETags.Items.NUGGETS_DRACONIUM_AWAKENED);
        compress3x3(DEContent.DRACONIUM_BLOCK, DETags.Items.INGOTS_DRACONIUM);
        compress3x3(DEContent.AWAKENED_DRACONIUM_BLOCK, DETags.Items.INGOTS_DRACONIUM_AWAKENED);

        deCompress(DEContent.NUGGET_DRACONIUM, DETags.Items.INGOTS_DRACONIUM);
        deCompress(DEContent.NUGGET_DRACONIUM_AWAKENED, DETags.Items.INGOTS_DRACONIUM_AWAKENED);
        deCompress(DEContent.INGOT_DRACONIUM, DETags.Items.STORAGE_BLOCKS_DRACONIUM);
        deCompress(DEContent.INGOT_DRACONIUM_AWAKENED, DETags.Items.STORAGE_BLOCKS_DRACONIUM_AWAKENED);

        deCompress(DEContent.CHAOS_FRAG_LARGE, DEContent.CHAOS_SHARD);
        deCompress(DEContent.CHAOS_FRAG_MEDIUM, DEContent.CHAOS_FRAG_LARGE);
        deCompress(DEContent.CHAOS_FRAG_SMALL, DEContent.CHAOS_FRAG_MEDIUM);
        compress3x3(DEContent.CHAOS_SHARD, DEContent.CHAOS_FRAG_LARGE);
        compress3x3(DEContent.CHAOS_FRAG_LARGE, DEContent.CHAOS_FRAG_MEDIUM);
        compress3x3(DEContent.CHAOS_FRAG_MEDIUM, DEContent.CHAOS_FRAG_SMALL);
    }

    private void machines() {
        shapedRecipe(DEContent.CRAFTING_CORE, "machines")
                .patternLine("ABA")
                .patternLine("BCB")
                .patternLine("ABA")
                .key('A', Tags.Items.STORAGE_BLOCKS_LAPIS)
                .key('B', Tags.Items.GEMS_DIAMOND)
                .key('C', DEContent.CORE_DRACONIUM);

        shapedRecipe(DEContent.BASIC_CRAFTING_INJECTOR, "machines")
                .patternLine("ABA")
                .patternLine("CDC")
                .patternLine("CCC")
                .key('A', Tags.Items.GEMS_DIAMOND)
                .key('B', DEContent.CORE_DRACONIUM)
                .key('C', Tags.Items.STONE)
                .key('D', Tags.Items.STORAGE_BLOCKS_IRON);

        fusionRecipe(DEContent.WYVERN_CRAFTING_INJECTOR, "machines")
                .catalyst(DEContent.BASIC_CRAFTING_INJECTOR)
                .energy(32000)
                .techLevel(TechLevel.DRACONIUM)
                .ingredient(DEContent.CORE_WYVERN)
                .ingredient(Tags.Items.GEMS_DIAMOND)
                .ingredient(DEContent.CORE_DRACONIUM)
                .ingredient(Tags.Items.GEMS_DIAMOND)
                .ingredient(DEContent.CORE_DRACONIUM)
                .ingredient(Tags.Items.GEMS_DIAMOND)
                .ingredient(DETags.Items.STORAGE_BLOCKS_DRACONIUM)
                .ingredient(Tags.Items.GEMS_DIAMOND);

        fusionRecipe(DEContent.AWAKENED_CRAFTING_INJECTOR, "machines")
                .catalyst(DEContent.WYVERN_CRAFTING_INJECTOR)
                .energy(256000)
                .techLevel(TechLevel.WYVERN)
                .ingredient(Tags.Items.GEMS_DIAMOND)
                .ingredient(Tags.Items.GEMS_DIAMOND)
                .ingredient(DEContent.CORE_WYVERN)
                .ingredient(DETags.Items.STORAGE_BLOCKS_DRACONIUM_AWAKENED)
                .ingredient(DEContent.CORE_WYVERN)
                .ingredient(Tags.Items.GEMS_DIAMOND)
                .ingredient(Tags.Items.GEMS_DIAMOND);

        fusionRecipe(DEContent.CHAOTIC_CRAFTING_INJECTOR, "machines")
                .catalyst(DEContent.AWAKENED_CRAFTING_INJECTOR)
                .energy(8000000)
                .techLevel(TechLevel.DRACONIC)
                .ingredient(Tags.Items.GEMS_DIAMOND)
                .ingredient(Tags.Items.GEMS_DIAMOND)
                .ingredient(DEContent.CHAOS_FRAG_LARGE)
                .ingredient(DEContent.CHAOS_FRAG_LARGE)
                .ingredient(DEContent.CHAOS_FRAG_LARGE)
                .ingredient(DEContent.CHAOS_FRAG_LARGE)
                .ingredient(Items.DRAGON_EGG)
                .ingredient(Tags.Items.GEMS_DIAMOND)
                .ingredient(Tags.Items.GEMS_DIAMOND);

        shapedRecipe(DEContent.GENERATOR, "machines")
                .patternLine("ABA")
                .patternLine("BCB")
                .patternLine("ADA")
                .key('A', Tags.Items.INGOTS_NETHER_BRICK)
                .key('B', Tags.Items.INGOTS_IRON)
                .key('C', Items.FURNACE)
                .key('D', DEContent.CORE_DRACONIUM);

        shapedRecipe(DEContent.GRINDER, "machines")
                .patternLine("ABA")
                .patternLine("CDC")
                .patternLine("AEA")
                .key('A', Tags.Items.INGOTS_IRON)
                .key('B', DETags.Items.INGOTS_DRACONIUM)
                .key('C', Items.DIAMOND_SWORD)
                .key('D', DEContent.ENERGY_CORE_WYVERN)
                .key('E', Tags.Items.HEADS);

        shapedRecipe(DEContent.ENERGY_TRANSFUSER, "machines")
                .patternLine("ABA")
                .patternLine("CDC")
                .patternLine("ACA")
                .key('A', DETags.Items.INGOTS_DRACONIUM)
                .key('B', DEContent.ENERGY_CORE_STABILIZER)
                .key('C', DEContent.CORE_DRACONIUM)
                .key('D', Items.ENCHANTING_TABLE);

        shapedRecipe(DEContent.PARTICLE_GENERATOR, "machines")
                .patternLine("ABA")
                .patternLine("BCB")
                .patternLine("ABA")
                .key('A', Tags.Items.STORAGE_BLOCKS_REDSTONE)
                .key('B', Items.BLAZE_ROD)
                .key('C', DEContent.CORE_DRACONIUM);

        fusionRecipe(DEContent.DRACONIUM_CHEST, "machines")
                .catalyst(Items.CHEST)
                .energy(2000000)
                .techLevel(TechLevel.DRACONIUM)
                .ingredient(Items.FURNACE)
                .ingredient(DEContent.CORE_DRACONIUM)
                .ingredient(Items.FURNACE)
                .ingredient(DEContent.CORE_DRACONIUM)
                .ingredient(Items.FURNACE)
                .ingredient(Items.CRAFTING_TABLE)
                .ingredient(Items.FURNACE)
                .ingredient(DETags.Items.STORAGE_BLOCKS_DRACONIUM)
                .ingredient(Items.FURNACE)
                .ingredient(Items.CRAFTING_TABLE);

        shapedRecipe(DEContent.POTENTIOMETER, "machines")
                .patternLine(" A ")
                .patternLine("BCB")
                .patternLine("DDD")
                .key('A', ItemTags.PLANKS)
                .key('B', Tags.Items.DUSTS_REDSTONE)
                .key('C', DETags.Items.DUSTS_DRACONIUM)
                .key('D', Items.STONE_SLAB);

    }

    private void energy() {
        shapedRecipe(DEContent.ENERGY_CORE, "machines")
                .patternLine("AAA")
                .patternLine("BCB")
                .patternLine("AAA")
                .key('A', DETags.Items.INGOTS_DRACONIUM)
                .key('B', DEContent.ENERGY_CORE_WYVERN)
                .key('C', DEContent.CORE_WYVERN);

        shapedRecipe(DEContent.ENERGY_CORE_STABILIZER, "machines")
                .patternLine("A A")
                .patternLine(" B ")
                .patternLine("A A")
                .key('A', Tags.Items.GEMS_DIAMOND)
                .key('B', DEContent.PARTICLE_GENERATOR);

        shapedRecipe(DEContent.ENERGY_PYLON, 2, "machines")
                .patternLine("ABA")
                .patternLine("CDC")
                .patternLine("AEA")
                .key('A', DETags.Items.INGOTS_DRACONIUM)
                .key('B', Items.ENDER_EYE)
                .key('C', Tags.Items.GEMS_EMERALD)
                .key('D', DEContent.CORE_DRACONIUM)
                .key('E', Tags.Items.GEMS_DIAMOND);

        //Reactor
        shapedRecipe(DEContent.REACTOR_PRT_STAB_FRAME, "machines")
                .patternLine("AAA")
                .patternLine("BC ")
                .patternLine("AAA")
                .key('A', Tags.Items.INGOTS_IRON)
                .key('B', DEContent.CORE_WYVERN)
                .key('C', DETags.Items.INGOTS_DRACONIUM_AWAKENED);

        shapedRecipe(DEContent.REACTOR_PRT_IN_ROTOR, "machines")
                .patternLine("   ")
                .patternLine("AAA")
                .patternLine("BCC")
                .key('A', DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .key('B', DEContent.CORE_DRACONIUM)
                .key('C', DETags.Items.INGOTS_DRACONIUM);

        shapedRecipe(DEContent.REACTOR_PRT_OUT_ROTOR, "machines")
                .patternLine("   ")
                .patternLine("AAA")
                .patternLine("BCC")
                .key('A', Tags.Items.GEMS_DIAMOND)
                .key('B', DEContent.CORE_DRACONIUM)
                .key('C', DETags.Items.INGOTS_DRACONIUM);

        shapedRecipe(DEContent.REACTOR_PRT_ROTOR_FULL, "machines")
                .patternLine(" AB")
                .patternLine("CDD")
                .patternLine(" AB")
                .key('A', DEContent.REACTOR_PRT_IN_ROTOR)
                .key('B', DEContent.REACTOR_PRT_OUT_ROTOR)
                .key('C', DEContent.CORE_WYVERN)
                .key('D', DETags.Items.INGOTS_DRACONIUM);

        shapedRecipe(DEContent.REACTOR_PRT_FOCUS_RING, "machines")
                .patternLine("ABA")
                .patternLine("CBC")
                .patternLine("ABA")
                .key('A', Tags.Items.INGOTS_GOLD)
                .key('B', Tags.Items.GEMS_DIAMOND)
                .key('C', DEContent.CORE_WYVERN);

        fusionRecipe(DEContent.REACTOR_STABILIZER, "machines")
                .catalyst(DEContent.REACTOR_PRT_STAB_FRAME)
                .energy(16000000)
                .techLevel(TechLevel.CHAOTIC)
                .ingredient(DEContent.ENERGY_CORE_DRACONIC)
                .ingredient(DEContent.REACTOR_PRT_ROTOR_FULL)
                .ingredient(DEContent.REACTOR_PRT_FOCUS_RING)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DEContent.CORE_CHAOTIC)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DEContent.CHAOS_FRAG_LARGE);

        fusionRecipe(DEContent.REACTOR_INJECTOR, "machines")
                .catalyst(DEContent.CORE_WYVERN)
                .energy(16000000)
                .techLevel(TechLevel.CHAOTIC)
                .ingredient(DETags.Items.INGOTS_DRACONIUM)
                .ingredient(DEContent.REACTOR_PRT_IN_ROTOR)
                .ingredient(DEContent.REACTOR_PRT_IN_ROTOR)
                .ingredient(DEContent.REACTOR_PRT_IN_ROTOR)
                .ingredient(DETags.Items.INGOTS_DRACONIUM)
                .ingredient(DETags.Items.INGOTS_DRACONIUM)
                .ingredient(Tags.Items.INGOTS_IRON)
                .ingredient(DEContent.REACTOR_PRT_IN_ROTOR)
                .ingredient(Tags.Items.INGOTS_IRON)
                .ingredient(DETags.Items.INGOTS_DRACONIUM);

        fusionRecipe(DEContent.REACTOR_CORE, "machines")
                .catalyst(DEContent.CHAOS_SHARD)
                .energy(64000000)
                .techLevel(TechLevel.CHAOTIC)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DETags.Items.INGOTS_DRACONIUM)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DETags.Items.INGOTS_DRACONIUM)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DETags.Items.INGOTS_DRACONIUM)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DEContent.CHAOS_FRAG_LARGE)
                .ingredient(DEContent.CHAOS_FRAG_LARGE);

        shapedRecipe(DEContent.CRYSTAL_BINDER, "tools")
                .patternLine(" AB")
                .patternLine(" CA")
                .patternLine("D  ")
                .key('A', DETags.Items.INGOTS_DRACONIUM)
                .key('B', Tags.Items.GEMS_DIAMOND)
                .key('C', Items.BLAZE_ROD)
                .key('D', DEContent.CORE_DRACONIUM);

        shapedRecipe(DEContent.BASIC_RELAY_CRYSTAL, 4, "machines")
                .patternLine(" A ")
                .patternLine("ABA")
                .patternLine(" A ")
                .key('A', Tags.Items.GEMS_DIAMOND)
                .key('B', DEContent.ENERGY_CORE_WYVERN);

        shapedRecipe(DEContent.WYVERN_RELAY_CRYSTAL, 4, "machines")
                .patternLine("ABA")
                .patternLine("BCB")
                .patternLine("ABA")
                .key('A', DEContent.ENERGY_CORE_WYVERN)
                .key('B', DEContent.BASIC_RELAY_CRYSTAL)
                .key('C', DEContent.CORE_DRACONIUM);

        fusionRecipe(DEContent.DRACONIC_RELAY_CRYSTAL, 4, "machines")
                .catalyst(4, DEContent.WYVERN_RELAY_CRYSTAL)
                .energy(128000)
                .techLevel(TechLevel.DRACONIC)
                .ingredient(DEContent.ENERGY_CORE_WYVERN)
                .ingredient(Tags.Items.GEMS_DIAMOND)
                .ingredient(DEContent.CORE_WYVERN)
                .ingredient(Tags.Items.GEMS_DIAMOND)
                .ingredient(DEContent.ENERGY_CORE_WYVERN)
                .ingredient(DEContent.ENERGY_CORE_WYVERN)
                .ingredient(Tags.Items.GEMS_DIAMOND)
                .ingredient(Tags.Items.GEMS_DIAMOND)
                .ingredient(DEContent.ENERGY_CORE_WYVERN);

        shapedRecipe(DEContent.BASIC_WIRELESS_CRYSTAL, "machines")
                .patternLine("ABA")
                .patternLine("CDC")
                .patternLine("ABA")
                .key('A', Items.ENDER_PEARL)
                .key('B', DEContent.PARTICLE_GENERATOR)
                .key('C', Items.ENDER_EYE)
                .key('D', DEContent.BASIC_RELAY_CRYSTAL);

        shapedRecipe(DEContent.WYVERN_WIRELESS_CRYSTAL, "machines")
                .patternLine("ABA")
                .patternLine("CDC")
                .patternLine("ABA")
                .key('A', Items.ENDER_PEARL)
                .key('B', DEContent.PARTICLE_GENERATOR)
                .key('C', Items.ENDER_EYE)
                .key('D', DEContent.WYVERN_RELAY_CRYSTAL);

        shapedRecipe(DEContent.DRACONIC_WIRELESS_CRYSTAL, "machines")
                .patternLine("ABA")
                .patternLine("CDC")
                .patternLine("ABA")
                .key('A', Items.ENDER_PEARL)
                .key('B', DEContent.PARTICLE_GENERATOR)
                .key('C', Items.ENDER_EYE)
                .key('D', DEContent.DRACONIC_RELAY_CRYSTAL);

        //to-from io
        shapelessRecipe(DEContent.BASIC_IO_CRYSTAL, 2, "machines")
                .addIngredient(DEContent.BASIC_RELAY_CRYSTAL);

        shapelessRecipe(DEContent.WYVERN_IO_CRYSTAL, 2, "machines")
                .addIngredient(DEContent.WYVERN_RELAY_CRYSTAL);

        shapelessRecipe(DEContent.DRACONIC_IO_CRYSTAL, 2, "machines")
                .addIngredient(DEContent.DRACONIC_RELAY_CRYSTAL);

        shapelessRecipe(DEContent.BASIC_RELAY_CRYSTAL, "machines", e -> e + "_combine")
                .addIngredient(DEContent.BASIC_IO_CRYSTAL)
                .addIngredient(DEContent.BASIC_IO_CRYSTAL);

        shapelessRecipe(DEContent.WYVERN_RELAY_CRYSTAL, "machines", e -> e + "_combine")
                .addIngredient(DEContent.WYVERN_IO_CRYSTAL)
                .addIngredient(DEContent.WYVERN_IO_CRYSTAL);

        shapelessRecipe(DEContent.DRACONIC_RELAY_CRYSTAL, "machines", e -> e + "_combine")
                .addIngredient(DEContent.DRACONIC_IO_CRYSTAL)
                .addIngredient(DEContent.DRACONIC_IO_CRYSTAL);
    }

    private void tools() {
        shapedRecipe(DEContent.DISLOCATOR, "tools")
                .patternLine("ABA")
                .patternLine("BCB")
                .patternLine("ABA")
                .key('A', Items.BLAZE_POWDER)
                .key('B', DETags.Items.DUSTS_DRACONIUM)
                .key('C', Items.ENDER_EYE);

        fusionRecipe(DEContent.DISLOCATOR_ADVANCED, "tools")
                .catalyst(DEContent.DISLOCATOR)
                .energy(1000000)
                .techLevel(TechLevel.WYVERN)
                .ingredient(Tags.Items.ENDER_PEARLS)
                .ingredient(DETags.Items.INGOTS_DRACONIUM)
                .ingredient(Tags.Items.ENDER_PEARLS)
                .ingredient(DETags.Items.INGOTS_DRACONIUM)
                .ingredient(Tags.Items.ENDER_PEARLS)
                .ingredient(DETags.Items.INGOTS_DRACONIUM)
                .ingredient(DEContent.CORE_WYVERN)
                .ingredient(DETags.Items.INGOTS_DRACONIUM);

        shapelessRecipe(DEContent.DISLOCATOR_P2P_UNBOUND, "tools")
                .addIngredient(DEContent.DISLOCATOR)
                .addIngredient(DEContent.CORE_DRACONIUM)
                .addIngredient(DEContent.DISLOCATOR)
                .addIngredient(Items.GHAST_TEAR);

        shapelessRecipe(DEContent.DISLOCATOR_PLAYER_UNBOUND, "tools")
                .addIngredient(DEContent.DISLOCATOR)
                .addIngredient(DEContent.CORE_DRACONIUM)
                .addIngredient(Items.GHAST_TEAR);

        shapedRecipe(DEContent.MAGNET, "tools")
                .patternLine("A A")
                .patternLine("B B")
                .patternLine("CDC")
                .key('A', Tags.Items.DUSTS_REDSTONE)
                .key('B', DETags.Items.INGOTS_DRACONIUM)
                .key('C', Tags.Items.INGOTS_IRON)
                .key('D', DEContent.DISLOCATOR);

        shapedRecipe(DEContent.MAGNET_ADVANCED, "tools")
                .patternLine("A A")
                .patternLine("B B")
                .patternLine("CDC")
                .key('A', DETags.Items.INGOTS_DRACONIUM)
                .key('B', Tags.Items.DUSTS_REDSTONE)
                .key('C', DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .key('D', DEContent.MAGNET);
    }

    private void equipment() {
        //Capacitors
        fusionRecipe(DEContent.CAPACITOR_WYVERN, "tools")
                .catalyst(DEContent.CORE_WYVERN)
                .energy(8000000)
                .techLevel(TechLevel.WYVERN)
                .ingredient(DETags.Items.INGOTS_DRACONIUM)
                .ingredient(DEContent.ENERGY_CORE_WYVERN)
                .ingredient(DETags.Items.INGOTS_DRACONIUM)
                .ingredient(DEContent.ENERGY_CORE_WYVERN)
                .ingredient(DEContent.ENERGY_CORE_WYVERN)
                .ingredient(DETags.Items.INGOTS_DRACONIUM)
                .ingredient(DEContent.ENERGY_CORE_WYVERN)
                .ingredient(DETags.Items.INGOTS_DRACONIUM);

        fusionRecipe(DEContent.CAPACITOR_DRACONIC, "tools")
                .catalyst(DEContent.CAPACITOR_WYVERN)
                .energy(32000000)
                .techLevel(TechLevel.DRACONIC)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DEContent.CORE_AWAKENED)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DEContent.ENERGY_CORE_DRACONIC)
                .ingredient(DEContent.ENERGY_CORE_DRACONIC)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DEContent.ENERGY_CORE_DRACONIC)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED);

        fusionRecipe(DEContent.CAPACITOR_CHAOTIC, "tools")
                .catalyst(DEContent.CAPACITOR_DRACONIC)
                .energy(128000000)
                .techLevel(TechLevel.CHAOTIC)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DEContent.CORE_CHAOTIC)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DEContent.ENERGY_CORE_CHAOTIC)
                .ingredient(DEContent.ENERGY_CORE_CHAOTIC)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DEContent.ENERGY_CORE_CHAOTIC)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED);

        //Shovel
        fusionRecipe(DEContent.SHOVEL_WYVERN, "tools")
                .catalyst(Items.DIAMOND_SHOVEL)
                .energy(8000000)
                .techLevel(TechLevel.WYVERN)
                .ingredient(DEContent.CORE_DRACONIUM)
                .ingredient(DETags.Items.INGOTS_DRACONIUM)
                .ingredient(DETags.Items.INGOTS_DRACONIUM)
                .ingredient(DEContent.BASIC_RELAY_CRYSTAL)
                .ingredient(DEContent.ENERGY_CORE_WYVERN)
                .ingredient(DEContent.BASIC_RELAY_CRYSTAL);

        fusionRecipe(DEContent.SHOVEL_DRACONIC, "tools")
                .catalyst(DEContent.SHOVEL_WYVERN)
                .energy(32000000)
                .techLevel(TechLevel.DRACONIC)
                .ingredient(Tags.Items.INGOTS_NETHERITE)
                .ingredient(DEContent.CORE_WYVERN)
                .ingredient(Tags.Items.INGOTS_NETHERITE)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(Tags.Items.INGOTS_NETHERITE)
                .ingredient(DEContent.ENERGY_CORE_DRACONIC)
                .ingredient(Tags.Items.INGOTS_NETHERITE);

        fusionRecipe(DEContent.SHOVEL_CHAOTIC, "tools")
                .catalyst(DEContent.SHOVEL_DRACONIC)
                .energy(128000000)
                .techLevel(TechLevel.CHAOTIC)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DEContent.CORE_CHAOTIC)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DEContent.ENERGY_CORE_CHAOTIC)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED);

        //Hoe
        fusionRecipe(DEContent.HOE_WYVERN, "tools")
                .catalyst(Items.DIAMOND_HOE)
                .energy(8000000)
                .techLevel(TechLevel.WYVERN)
                .ingredient(DEContent.CORE_DRACONIUM)
                .ingredient(DETags.Items.INGOTS_DRACONIUM)
                .ingredient(DETags.Items.INGOTS_DRACONIUM)
                .ingredient(DEContent.BASIC_RELAY_CRYSTAL)
                .ingredient(DEContent.ENERGY_CORE_WYVERN)
                .ingredient(DEContent.BASIC_RELAY_CRYSTAL);

        fusionRecipe(DEContent.HOE_DRACONIC, "tools")
                .catalyst(DEContent.HOE_WYVERN)
                .energy(32000000)
                .techLevel(TechLevel.DRACONIC)
                .ingredient(Tags.Items.INGOTS_NETHERITE)
                .ingredient(DEContent.CORE_WYVERN)
                .ingredient(Tags.Items.INGOTS_NETHERITE)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(Tags.Items.INGOTS_NETHERITE)
                .ingredient(DEContent.ENERGY_CORE_DRACONIC)
                .ingredient(Tags.Items.INGOTS_NETHERITE);

        fusionRecipe(DEContent.HOE_CHAOTIC, "tools")
                .catalyst(DEContent.HOE_DRACONIC)
                .energy(128000000)
                .techLevel(TechLevel.CHAOTIC)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DEContent.CORE_CHAOTIC)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DEContent.ENERGY_CORE_CHAOTIC)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED);

        //Pickaxe
        fusionRecipe(DEContent.PICKAXE_WYVERN, "tools")
                .catalyst(Items.DIAMOND_PICKAXE)
                .energy(8000000)
                .techLevel(TechLevel.WYVERN)
                .ingredient(DEContent.CORE_DRACONIUM)
                .ingredient(DETags.Items.INGOTS_DRACONIUM)
                .ingredient(DETags.Items.INGOTS_DRACONIUM)
                .ingredient(DEContent.BASIC_RELAY_CRYSTAL)
                .ingredient(DEContent.ENERGY_CORE_WYVERN)
                .ingredient(DEContent.BASIC_RELAY_CRYSTAL);

        fusionRecipe(DEContent.PICKAXE_DRACONIC, "tools")
                .catalyst(DEContent.PICKAXE_WYVERN)
                .energy(32000000)
                .techLevel(TechLevel.DRACONIC)
                .ingredient(Tags.Items.INGOTS_NETHERITE)
                .ingredient(DEContent.CORE_WYVERN)
                .ingredient(Tags.Items.INGOTS_NETHERITE)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(Tags.Items.INGOTS_NETHERITE)
                .ingredient(DEContent.ENERGY_CORE_DRACONIC)
                .ingredient(Tags.Items.INGOTS_NETHERITE);

        fusionRecipe(DEContent.PICKAXE_CHAOTIC, "tools")
                .catalyst(DEContent.PICKAXE_DRACONIC)
                .energy(128000000)
                .techLevel(TechLevel.CHAOTIC)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DEContent.CORE_CHAOTIC)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DEContent.ENERGY_CORE_CHAOTIC)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED);

        //Axe
        fusionRecipe(DEContent.AXE_WYVERN, "tools")
                .catalyst(Items.DIAMOND_AXE)
                .energy(8000000)
                .techLevel(TechLevel.WYVERN)
                .ingredient(DEContent.CORE_DRACONIUM)
                .ingredient(DETags.Items.INGOTS_DRACONIUM)
                .ingredient(DETags.Items.INGOTS_DRACONIUM)
                .ingredient(DEContent.BASIC_RELAY_CRYSTAL)
                .ingredient(DEContent.ENERGY_CORE_WYVERN)
                .ingredient(DEContent.BASIC_RELAY_CRYSTAL);

        fusionRecipe(DEContent.AXE_DRACONIC, "tools")
                .catalyst(DEContent.AXE_WYVERN)
                .energy(32000000)
                .techLevel(TechLevel.DRACONIC)
                .ingredient(Tags.Items.INGOTS_NETHERITE)
                .ingredient(DEContent.CORE_WYVERN)
                .ingredient(Tags.Items.INGOTS_NETHERITE)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(Tags.Items.INGOTS_NETHERITE)
                .ingredient(DEContent.ENERGY_CORE_DRACONIC)
                .ingredient(Tags.Items.INGOTS_NETHERITE);

        fusionRecipe(DEContent.AXE_CHAOTIC, "tools")
                .catalyst(DEContent.AXE_DRACONIC)
                .energy(128000000)
                .techLevel(TechLevel.CHAOTIC)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DEContent.CORE_CHAOTIC)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DEContent.ENERGY_CORE_CHAOTIC)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED);

        //Bow
        fusionRecipe(DEContent.BOW_WYVERN, "tools")
                .catalyst(Items.BOW)
                .energy(8000000)
                .techLevel(TechLevel.WYVERN)
                .ingredient(DEContent.CORE_DRACONIUM)
                .ingredient(DETags.Items.INGOTS_DRACONIUM)
                .ingredient(DETags.Items.INGOTS_DRACONIUM)
                .ingredient(DEContent.BASIC_RELAY_CRYSTAL)
                .ingredient(DEContent.ENERGY_CORE_WYVERN)
                .ingredient(DEContent.BASIC_RELAY_CRYSTAL);

        fusionRecipe(DEContent.BOW_DRACONIC, "tools")
                .catalyst(DEContent.BOW_WYVERN)
                .energy(32000000)
                .techLevel(TechLevel.DRACONIC)
                .ingredient(Tags.Items.INGOTS_NETHERITE)
                .ingredient(DEContent.CORE_WYVERN)
                .ingredient(Tags.Items.INGOTS_NETHERITE)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(Tags.Items.INGOTS_NETHERITE)
                .ingredient(DEContent.ENERGY_CORE_DRACONIC)
                .ingredient(Tags.Items.INGOTS_NETHERITE);

        fusionRecipe(DEContent.BOW_CHAOTIC, "tools")
                .catalyst(DEContent.BOW_DRACONIC)
                .energy(128000000)
                .techLevel(TechLevel.CHAOTIC)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DEContent.CORE_CHAOTIC)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DEContent.ENERGY_CORE_CHAOTIC)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED);

        //Sword
        fusionRecipe(DEContent.SWORD_WYVERN, "tools")
                .catalyst(Items.DIAMOND_SWORD)
                .energy(8000000)
                .techLevel(TechLevel.WYVERN)
                .ingredient(DEContent.CORE_DRACONIUM)
                .ingredient(DETags.Items.INGOTS_DRACONIUM)
                .ingredient(DETags.Items.INGOTS_DRACONIUM)
                .ingredient(DEContent.BASIC_RELAY_CRYSTAL)
                .ingredient(DEContent.ENERGY_CORE_WYVERN)
                .ingredient(DEContent.BASIC_RELAY_CRYSTAL);

        fusionRecipe(DEContent.SWORD_DRACONIC, "tools")
                .catalyst(DEContent.SWORD_WYVERN)
                .energy(32000000)
                .techLevel(TechLevel.DRACONIC)
                .ingredient(Tags.Items.INGOTS_NETHERITE)
                .ingredient(DEContent.CORE_WYVERN)
                .ingredient(Tags.Items.INGOTS_NETHERITE)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(Tags.Items.INGOTS_NETHERITE)
                .ingredient(DEContent.ENERGY_CORE_DRACONIC)
                .ingredient(Tags.Items.INGOTS_NETHERITE);

        fusionRecipe(DEContent.SWORD_CHAOTIC, "tools")
                .catalyst(DEContent.SWORD_DRACONIC)
                .energy(128000000)
                .techLevel(TechLevel.CHAOTIC)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DEContent.CORE_CHAOTIC)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DEContent.ENERGY_CORE_CHAOTIC)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED);

        //Staff
        fusionRecipe(DEContent.STAFF_DRACONIC, "tools")
                .catalyst(DEContent.CORE_AWAKENED)
                .energy(256000000)
                .techLevel(TechLevel.DRACONIC)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DEContent.PICKAXE_DRACONIC)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DEContent.SWORD_DRACONIC)
                .ingredient(DEContent.SHOVEL_DRACONIC)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DEContent.ENERGY_CORE_DRACONIC)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED);

        fusionRecipe(DEContent.STAFF_CHAOTIC, "tools")
                .catalyst(DEContent.CORE_CHAOTIC)
                .energy(1024000000)
                .techLevel(TechLevel.CHAOTIC)
                .ingredient(DEContent.CHAOS_FRAG_MEDIUM)
                .ingredient(DEContent.PICKAXE_CHAOTIC)
                .ingredient(DEContent.CHAOS_FRAG_MEDIUM)
                .ingredient(DEContent.CHAOS_FRAG_MEDIUM)
                .ingredient(DEContent.SWORD_CHAOTIC)
                .ingredient(DEContent.SHOVEL_CHAOTIC)
                .ingredient(DEContent.CHAOS_FRAG_MEDIUM)
                .ingredient(DEContent.ENERGY_CORE_CHAOTIC)
                .ingredient(DEContent.CHAOS_FRAG_MEDIUM)
                .ingredient(DEContent.CHAOS_FRAG_MEDIUM);

        fusionRecipe(DEContent.STAFF_CHAOTIC, "tools", e -> e + "_alt")
                .catalyst(DEContent.STAFF_DRACONIC)
                .energy(1024000000)
                .techLevel(TechLevel.CHAOTIC)
                .ingredient(DEContent.CHAOS_FRAG_MEDIUM)
                .ingredient(DEContent.CORE_CHAOTIC)
                .ingredient(DEContent.CHAOS_FRAG_MEDIUM)
                .ingredient(DEContent.ENERGY_CORE_CHAOTIC)
                .ingredient(DEContent.CORE_AWAKENED)
                .ingredient(DEContent.CORE_AWAKENED)
                .ingredient(DEContent.CHAOS_FRAG_LARGE)
                .ingredient(DEContent.ENERGY_CORE_CHAOTIC)
                .ingredient(DEContent.CHAOS_FRAG_LARGE)
                .ingredient(DEContent.ENERGY_CORE_CHAOTIC);

        //Chestpiece
        fusionRecipe(DEContent.CHESTPIECE_WYVERN, "tools")
                .catalyst(Items.DIAMOND_CHESTPLATE)
                .energy(8000000)
                .techLevel(TechLevel.WYVERN)
                .ingredient(DEContent.CORE_DRACONIUM)
                .ingredient(DETags.Items.INGOTS_DRACONIUM)
                .ingredient(DETags.Items.INGOTS_DRACONIUM)
                .ingredient(DEContent.BASIC_RELAY_CRYSTAL)
                .ingredient(DEContent.ENERGY_CORE_WYVERN)
                .ingredient(DEContent.BASIC_RELAY_CRYSTAL);

        fusionRecipe(DEContent.CHESTPIECE_DRACONIC, "tools")
                .catalyst(DEContent.CHESTPIECE_WYVERN)
                .energy(32000000)
                .techLevel(TechLevel.DRACONIC)
                .ingredient(Tags.Items.INGOTS_NETHERITE)
                .ingredient(DEContent.CORE_WYVERN)
                .ingredient(Tags.Items.INGOTS_NETHERITE)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(Tags.Items.INGOTS_NETHERITE)
                .ingredient(DEContent.ENERGY_CORE_DRACONIC)
                .ingredient(Tags.Items.INGOTS_NETHERITE);

        fusionRecipe(DEContent.CHESTPIECE_CHAOTIC, "tools")
                .catalyst(DEContent.CHESTPIECE_DRACONIC)
                .energy(128000000)
                .techLevel(TechLevel.CHAOTIC)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DEContent.CORE_CHAOTIC)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .ingredient(DEContent.ENERGY_CORE_CHAOTIC)
                .ingredient(DETags.Items.INGOTS_DRACONIUM_AWAKENED);
    }

    private void modules() {
        shapedRecipe(DEContent.MODULE_CORE, "modules")
                .patternLine("IRI")
                .patternLine("GDG")
                .patternLine("IRI")
                .key('I', Tags.Items.INGOTS_IRON)
                .key('R', Tags.Items.DUSTS_REDSTONE)
                .key('G', Tags.Items.INGOTS_GOLD)
                .key('D', DETags.Items.INGOTS_DRACONIUM);

        //Energy
        shapedRecipe(DEModules.DRACONIUM_ENERGY.get().getItem(), "modules")
                .patternLine("###")
                .patternLine("ABA")
                .patternLine("###")
                .key('#', Tags.Items.STORAGE_BLOCKS_REDSTONE)
                .key('A', Tags.Items.INGOTS_IRON)
                .key('B', DEContent.MODULE_CORE);

        shapedRecipe(DEModules.WYVERN_ENERGY.get().getItem(), "modules")
                .patternLine("###")
                .patternLine("ABA")
                .patternLine("###")
                .key('#', DETags.Items.INGOTS_DRACONIUM)
                .key('A', DEModules.DRACONIUM_ENERGY.get().getItem())
                .key('B', DEContent.CORE_DRACONIUM);

        shapedRecipe(DEModules.DRACONIC_ENERGY.get().getItem(), "modules")
                .patternLine("###")
                .patternLine("ABA")
                .patternLine("###")
                .key('#', DETags.Items.NUGGETS_DRACONIUM_AWAKENED)
                .key('A', DEModules.WYVERN_ENERGY.get().getItem())
                .key('B', DEContent.CORE_WYVERN);

        shapedRecipe(DEModules.CHAOTIC_ENERGY.get().getItem(), "modules")
                .patternLine("CCC")
                .patternLine("ABA")
                .patternLine("CCC")
                //                .key('#', chaos_frag_small)
                .key('A', DEModules.DRACONIC_ENERGY.get().getItem())
                .key('B', DEContent.CORE_AWAKENED)
                .key('C', DEContent.CHAOS_FRAG_MEDIUM);

        //Energy Link
        shapedRecipe(DEModules.WYVERN_ENERGY_LINK.get().getItem(), "modules")
                .patternLine("#E#")
                .patternLine("WMW")
                .patternLine("#C#")
                .key('#', DETags.Items.INGOTS_DRACONIUM)
                .key('E', Items.END_CRYSTAL)
                .key('M', DEContent.MODULE_CORE)
                .key('W', DEContent.BASIC_WIRELESS_CRYSTAL.get())
                .key('C', DEContent.CORE_WYVERN);

        shapedRecipe(DEModules.DRACONIC_ENERGY_LINK.get().getItem(), "modules")
                .patternLine("#E#")
                .patternLine("WMW")
                .patternLine("#C#")
                .key('#', DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .key('E', Items.END_CRYSTAL)
                .key('M', DEModules.WYVERN_ENERGY_LINK.get().getItem())
                .key('W', DEContent.WYVERN_WIRELESS_CRYSTAL.get())
                .key('C', DEContent.CORE_AWAKENED);

        shapedRecipe(DEModules.CHAOTIC_ENERGY_LINK.get().getItem(), "modules")
                .patternLine("#E#")
                .patternLine("WMW")
                .patternLine("#C#")
                .key('#', Tags.Items.INGOTS_NETHERITE)
                .key('E', Items.END_CRYSTAL)
                .key('M', DEModules.DRACONIC_ENERGY_LINK.get().getItem())
                .key('W', DEContent.DRACONIC_WIRELESS_CRYSTAL.get())
                .key('C', DEContent.CORE_CHAOTIC);

        //Speed
        shapedRecipe(DEModules.DRACONIUM_SPEED.get().getItem(), "modules")
                .patternLine("#P#")
                .patternLine("ABA")
                .patternLine("#P#")
                .key('#', Tags.Items.INGOTS_IRON)
                .key('A', Items.CLOCK)
                .key('B', DEContent.MODULE_CORE)
                .key('P', NBTIngredient.of(true, PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.SWIFTNESS)));

        shapedRecipe(DEModules.WYVERN_SPEED.get().getItem(), "modules")
                .patternLine("###")
                .patternLine("ABA")
                .patternLine("###")
                .key('#', DETags.Items.INGOTS_DRACONIUM)
                .key('A', DEModules.DRACONIUM_SPEED.get().getItem())
                .key('B', DEContent.CORE_DRACONIUM);

        shapedRecipe(DEModules.DRACONIC_SPEED.get().getItem(), "modules")
                .patternLine("###")
                .patternLine("ABA")
                .patternLine("###")
                .key('#', DETags.Items.NUGGETS_DRACONIUM_AWAKENED)
                .key('A', DEModules.WYVERN_SPEED.get().getItem())
                .key('B', DEContent.CORE_WYVERN);

        shapedRecipe(DEModules.CHAOTIC_SPEED.get().getItem(), "modules")
                .patternLine("CCC")
                .patternLine("ABA")
                .patternLine("CCC")
                //                .key('#', chaos_frag_small)
                .key('A', DEModules.DRACONIC_SPEED.get().getItem())
                .key('B', DEContent.CORE_AWAKENED)
                .key('C', DEContent.CHAOS_FRAG_MEDIUM);

        //Damage
        shapedRecipe(DEModules.DRACONIUM_DAMAGE.get().getItem(), "modules")
                .patternLine("IPG")
                .patternLine("ABA")
                .patternLine("GPI")
                .key('I', Tags.Items.INGOTS_IRON)
                .key('P', NBTIngredient.of(true, PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.STRENGTH)))
                .key('G', Tags.Items.INGOTS_GOLD)
                .key('A', Tags.Items.DUSTS_GLOWSTONE)
                .key('B', DEContent.MODULE_CORE);

        shapedRecipe(DEModules.WYVERN_DAMAGE.get().getItem(), "modules")
                .patternLine("IPI")
                .patternLine("ABA")
                .patternLine("IPI")
                .key('I', DETags.Items.INGOTS_DRACONIUM)
                .key('P', NBTIngredient.of(true, PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.STRONG_STRENGTH)))
                .key('A', DEModules.DRACONIUM_DAMAGE.get().getItem())
                .key('B', DEContent.CORE_DRACONIUM);

        shapedRecipe(DEModules.DRACONIC_DAMAGE.get().getItem(), "modules")
                .patternLine("IPI")
                .patternLine("ABA")
                .patternLine("IPI")
                .key('I', DETags.Items.NUGGETS_DRACONIUM_AWAKENED)
                .key('P', Items.DRAGON_BREATH)
                .key('A', DEModules.WYVERN_DAMAGE.get().getItem())
                .key('B', DEContent.CORE_WYVERN);

        shapedRecipe(DEModules.CHAOTIC_DAMAGE.get().getItem(), "modules")
                .patternLine("CCC")
                .patternLine("ABA")
                .patternLine("CCC")
                //                .key('I', chaos_frag_small)
                .key('C', DEContent.CHAOS_FRAG_MEDIUM)
                .key('A', DEModules.DRACONIC_DAMAGE.get().getItem())
                .key('B', DEContent.CORE_AWAKENED);

        //AOE
        shapedRecipe(DEModules.DRACONIUM_AOE.get().getItem(), "modules")
                .patternLine("#I#")
                .patternLine("ABA")
                .patternLine("#I#")
                .key('#', Items.PISTON)
                .key('I', DETags.Items.INGOTS_DRACONIUM)
                .key('A', DEContent.CORE_DRACONIUM)
                .key('B', DEContent.MODULE_CORE);

        shapedRecipe(DEModules.WYVERN_AOE.get().getItem(), "modules")
                .patternLine("#I#")
                .patternLine("ABA")
                .patternLine("#I#")
                .key('#', DETags.Items.INGOTS_DRACONIUM)
                .key('I', Items.NETHERITE_SCRAP)
                .key('A', DEModules.DRACONIUM_AOE.get().getItem())
                .key('B', DEContent.CORE_WYVERN);

        shapedRecipe(DEModules.DRACONIC_AOE.get().getItem(), "modules")
                .patternLine("#I#")
                .patternLine("ABA")
                .patternLine("#I#")
                .key('#', Tags.Items.INGOTS_NETHERITE)
                .key('I', DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .key('A', DEModules.WYVERN_AOE.get().getItem())
                .key('B', DEContent.CORE_AWAKENED);

        shapedRecipe(DEModules.CHAOTIC_AOE.get().getItem(), "modules")
                .patternLine("#I#")
                .patternLine("ABA")
                .patternLine("#I#")
                .key('#', Tags.Items.INGOTS_NETHERITE)
                .key('I', DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .key('A', DEModules.DRACONIC_AOE.get().getItem())
                .key('B', DEContent.CORE_CHAOTIC);

        //Mining Stability
        shapedRecipe(DEModules.WYVERN_MINING_STABILITY.get().getItem(), "modules")
                .patternLine("#C#")
                .patternLine("ABA")
                .patternLine("#D#")
                .key('#', DETags.Items.INGOTS_DRACONIUM)
                .key('A', DEContent.CORE_DRACONIUM)
                .key('B', DEContent.MODULE_CORE)
                .key('C', Items.PHANTOM_MEMBRANE)
                .key('D', Items.GOLDEN_PICKAXE);

        shapedRecipe(DEModules.WYVERN_JUNK_FILTER.get().getItem(), "modules")
                .patternLine("#C#")
                .patternLine("ABA")
                .patternLine("#D#")
                .key('#', DETags.Items.INGOTS_DRACONIUM)
                .key('A', DEContent.CORE_DRACONIUM)
                .key('B', DEContent.MODULE_CORE)
                .key('C', Items.LAVA_BUCKET)
                .key('D', Tags.Items.DUSTS_REDSTONE);

        //Tree Harvest
        shapedRecipe(DEModules.WYVERN_TREE_HARVEST.get().getItem(), "modules")
                .patternLine("#A#")
                .patternLine("CMC")
                .patternLine("#A#")
                .key('#', DETags.Items.INGOTS_DRACONIUM)
                .key('A', Items.DIAMOND_AXE)
                .key('M', DEContent.MODULE_CORE)
                .key('C', DEContent.CORE_DRACONIUM);

        shapedRecipe(DEModules.DRACONIC_TREE_HARVEST.get().getItem(), "modules")
                .patternLine("#C#")
                .patternLine("IMI")
                .patternLine("#W#")
                .key('#', DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .key('C', DEContent.CORE_DRACONIUM)
                .key('I', Tags.Items.INGOTS_NETHERITE)
                .key('M', DEModules.WYVERN_TREE_HARVEST.get().getItem())
                .key('W', DEContent.CORE_WYVERN);

        shapedRecipe(DEModules.CHAOTIC_TREE_HARVEST.get().getItem(), "modules")
                .patternLine("#C#")
                .patternLine("IMI")
                .patternLine("#W#")
                .key('#', Tags.Items.INGOTS_NETHERITE)
                .key('C', DEContent.CORE_DRACONIUM)
                .key('I', DEContent.CHAOS_FRAG_LARGE)
                .key('M', DEModules.DRACONIC_TREE_HARVEST.get().getItem())
                .key('W', DEContent.CORE_AWAKENED);

        //Ender Collection
        shapedRecipe(DEModules.WYVERN_ENDER_COLLECTION.get().getItem(), "modules")
                .patternLine("#C#")
                .patternLine("IMI")
                .patternLine("#W#")
                .key('#', Items.ENDER_EYE)
                .key('C', DEContent.CORE_DRACONIUM)
                .key('I', DETags.Items.INGOTS_DRACONIUM)
                .key('M', DEContent.MODULE_CORE)
                .key('W', Tags.Items.CHESTS_ENDER);

        shapedRecipe(DEModules.DRACONIC_ENDER_COLLECTION.get().getItem(), "modules")
                .patternLine("#C#")
                .patternLine("IMI")
                .patternLine("#W#")
                .key('#', DETags.Items.NUGGETS_DRACONIUM_AWAKENED)
                .key('C', DEContent.CORE_DRACONIUM)
                .key('I', Items.COMPARATOR)
                .key('M', DEModules.WYVERN_ENDER_COLLECTION.get().getItem())
                .key('W', Tags.Items.CHESTS);

        //Shield Controller
        shapedRecipe(DEModules.WYVERN_SHIELD_CONTROL.get().getItem(), "modules")
                .patternLine("#C#")
                .patternLine("ABA")
                .patternLine("#D#")
                .key('#', Tags.Items.GEMS_DIAMOND)
                .key('A', DEContent.CORE_WYVERN)
                .key('B', DEContent.MODULE_CORE)
                .key('C', DEContent.DRAGON_HEART)
                .key('D', DEContent.PARTICLE_GENERATOR);

        shapedRecipe(DEModules.DRACONIC_SHIELD_CONTROL.get().getItem(), "modules")
                .patternLine("#I#")
                .patternLine("ABA")
                .patternLine("#I#")
                .key('#', Tags.Items.GEMS_EMERALD)
                .key('A', DEContent.CORE_AWAKENED)
                .key('B', DEModules.WYVERN_SHIELD_CONTROL.get().getItem())
                .key('I', Tags.Items.INGOTS_NETHERITE);

        shapedRecipe(DEModules.CHAOTIC_SHIELD_CONTROL.get().getItem(), "modules")
                .patternLine("#I#")
                .patternLine("ABA")
                .patternLine("#I#")
                .key('#', Tags.Items.NETHER_STARS)
                .key('A', DEContent.CORE_CHAOTIC)
                .key('B', DEModules.DRACONIC_SHIELD_CONTROL.get().getItem())
                .key('I', Tags.Items.INGOTS_NETHERITE);

        //Shield Capacity
        shapedRecipe(DEModules.WYVERN_SHIELD_CAPACITY.get().getItem(), "modules")
                .patternLine("#I#")
                .patternLine("ABA")
                .patternLine("#I#")
                .key('#', DETags.Items.INGOTS_DRACONIUM)
                .key('A', Tags.Items.DUSTS_GLOWSTONE)
                .key('B', DEContent.MODULE_CORE)
                .key('I', Items.NETHERITE_SCRAP);

        shapedRecipe(DEModules.DRACONIC_SHIELD_CAPACITY.get().getItem(), "modules")
                .patternLine("#C#")
                .patternLine("ABA")
                .patternLine("#D#")
                .key('#', Tags.Items.INGOTS_NETHERITE)
                .key('A', DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .key('B', DEModules.WYVERN_SHIELD_CAPACITY.get().getItem())
                .key('C', DEContent.CORE_DRACONIUM)
                .key('D', DEContent.CORE_WYVERN);

        shapedRecipe(DEModules.CHAOTIC_SHIELD_CAPACITY.get().getItem(), "modules")
                .patternLine("#C#")
                .patternLine("ABA")
                .patternLine("#D#")
                .key('#', DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .key('A', DEContent.CHAOS_FRAG_LARGE)
                .key('B', DEModules.DRACONIC_SHIELD_CAPACITY.get().getItem())
                .key('C', DEContent.CORE_WYVERN)
                .key('D', DEContent.CORE_CHAOTIC);

        //Shield Capacity XL
        shapedRecipe(DEModules.WYVERN_LARGE_SHIELD_CAPACITY.get().getItem(), "modules")
                .patternLine("#A#")
                .patternLine("A#A")
                .patternLine("#A#")
                .key('#', DEModules.WYVERN_SHIELD_CAPACITY.get().getItem())
                .key('A', DEContent.CORE_DRACONIUM);

        shapedRecipe(DEModules.DRACONIC_LARGE_SHIELD_CAPACITY.get().getItem(), "modules")
                .patternLine("#A#")
                .patternLine("A#A")
                .patternLine("#A#")
                .key('#', DEModules.DRACONIC_SHIELD_CAPACITY.get().getItem())
                .key('A', DEContent.CORE_DRACONIUM);

        shapedRecipe(DEModules.CHAOTIC_LARGE_SHIELD_CAPACITY.get().getItem(), "modules")
                .patternLine("#A#")
                .patternLine("A#A")
                .patternLine("#A#")
                .key('#', DEModules.CHAOTIC_SHIELD_CAPACITY.get().getItem())
                .key('A', DEContent.CORE_DRACONIUM);

        shapelessRecipe(DEModules.WYVERN_SHIELD_CAPACITY.get().getItem(), 5, "modules", e -> e + "_uncraft")
                .addIngredient(DEModules.WYVERN_LARGE_SHIELD_CAPACITY.get().getItem());

        shapelessRecipe(DEModules.DRACONIC_SHIELD_CAPACITY.get().getItem(), 5, "modules", e -> e + "_uncraft")
                .addIngredient(DEModules.DRACONIC_LARGE_SHIELD_CAPACITY.get().getItem());

        shapelessRecipe(DEModules.CHAOTIC_SHIELD_CAPACITY.get().getItem(), 5, "modules", e -> e + "_uncraft")
                .addIngredient(DEModules.CHAOTIC_LARGE_SHIELD_CAPACITY.get().getItem());

        //Shield Recovery
        shapedRecipe(DEModules.WYVERN_SHIELD_RECOVERY.get().getItem(), "modules")
                .patternLine("#I#")
                .patternLine("ABA")
                .patternLine("#I#")
                .key('#', DETags.Items.INGOTS_DRACONIUM)
                .key('A', Tags.Items.DUSTS_REDSTONE)
                .key('B', DEContent.MODULE_CORE)
                .key('I', Items.NETHERITE_SCRAP);

        shapedRecipe(DEModules.DRACONIC_SHIELD_RECOVERY.get().getItem(), "modules")
                .patternLine("#C#")
                .patternLine("ABA")
                .patternLine("#D#")
                .key('#', Tags.Items.INGOTS_NETHERITE)
                .key('A', DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .key('B', DEModules.WYVERN_SHIELD_RECOVERY.get().getItem())
                .key('C', DEContent.CORE_DRACONIUM)
                .key('D', DEContent.CORE_WYVERN);

        shapedRecipe(DEModules.CHAOTIC_SHIELD_RECOVERY.get().getItem(), "modules")
                .patternLine("#C#")
                .patternLine("ABA")
                .patternLine("#D#")
                .key('#', DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .key('A', DEContent.CHAOS_FRAG_LARGE)
                .key('B', DEModules.DRACONIC_SHIELD_RECOVERY.get().getItem())
                .key('C', DEContent.CORE_WYVERN)
                .key('D', DEContent.CORE_AWAKENED);

        //Flight
        shapedRecipe(DEModules.WYVERN_FLIGHT.get().getItem(), "modules")
                .patternLine("#C#")
                .patternLine("ABA")
                .patternLine("#D#")
                .key('#', DETags.Items.INGOTS_DRACONIUM)
                .key('C', Items.ELYTRA)
                .key('A', DEContent.CORE_DRACONIUM)
                .key('B', DEContent.MODULE_CORE)
                .key('D', Items.FIREWORK_ROCKET);

        shapedRecipe(DEModules.DRACONIC_FLIGHT.get().getItem(), "modules")
                .patternLine("#C#")
                .patternLine("ABA")
                .patternLine("#D#")
                .key('#', DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .key('A', DEContent.CORE_WYVERN)
                .key('B', DEModules.WYVERN_FLIGHT.get().getItem())
                .key('C', NBTIngredient.of(true, PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.SLOW_FALLING)))
                .key('D', Items.FIREWORK_ROCKET);

        shapedRecipe(DEModules.CHAOTIC_FLIGHT.get().getItem(), "modules")
                .patternLine("#C#")
                .patternLine("ABA")
                .patternLine("#C#")
                .key('#', NBTIngredient.of(true, PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.STRONG_SWIFTNESS)))
                .key('A', DEContent.CORE_AWAKENED)
                .key('B', DEModules.DRACONIC_FLIGHT.get().getItem())
                .key('C', DEContent.CHAOS_FRAG_LARGE);

        //Last Stand
        shapedRecipe(DEModules.WYVERN_UNDYING.get().getItem(), "modules")
                .patternLine("#C#")
                .patternLine("ABA")
                .patternLine("#D#")
                .key('#', DETags.Items.INGOTS_DRACONIUM)
                .key('A', DEContent.CORE_DRACONIUM)
                .key('B', DEContent.MODULE_CORE)
                .key('C', Items.TOTEM_OF_UNDYING)
                .key('D', DEModules.WYVERN_SHIELD_CAPACITY.get().getItem());

        shapedRecipe(DEModules.DRACONIC_UNDYING.get().getItem(), "modules")
                .patternLine("#C#")
                .patternLine("ABA")
                .patternLine("#D#")
                .key('#', DETags.Items.INGOTS_DRACONIUM_AWAKENED)
                .key('A', DEContent.CORE_WYVERN)
                .key('B', DEModules.WYVERN_UNDYING.get().getItem())
                .key('C', NBTIngredient.of(true, PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.STRONG_HEALING)))
                .key('D', DEModules.DRACONIC_SHIELD_CAPACITY.get().getItem());

        shapedRecipe(DEModules.CHAOTIC_UNDYING.get().getItem(), "modules")
                .patternLine("#C#")
                .patternLine("ABA")
                .patternLine("#D#")
                .key('#', DEContent.CHAOS_FRAG_MEDIUM)
                .key('A', DEContent.CORE_AWAKENED)
                .key('B', DEModules.DRACONIC_UNDYING.get().getItem())
                .key('C', Items.ENCHANTED_GOLDEN_APPLE)
                .key('D', DEModules.CHAOTIC_SHIELD_CAPACITY.get().getItem());

        //Auto Feed
        shapedRecipe(DEModules.DRACONIUM_AUTO_FEED.get().getItem(), "modules")
                .patternLine("#C#")
                .patternLine("ABA")
                .patternLine("#D#")
                .key('#', Tags.Items.INGOTS_IRON)
                .key('A', Items.COOKIE)
                .key('B', DEContent.MODULE_CORE)
                .key('C', Items.GOLDEN_APPLE)
                .key('D', DEContent.CORE_DRACONIUM);

        shapedRecipe(DEModules.WYVERN_AUTO_FEED.get().getItem(), "modules")
                .patternLine("#C#")
                .patternLine("ABA")
                .patternLine("#C#")
                .key('#', DETags.Items.INGOTS_DRACONIUM)
                .key('A', DEContent.CORE_DRACONIUM)
                .key('B', DEModules.DRACONIUM_AUTO_FEED.get().getItem())
                .key('C', Items.COOKIE);

        shapedRecipe(DEModules.DRACONIC_AUTO_FEED.get().getItem(), "modules")
                .patternLine("#C#")
                .patternLine("ABA")
                .patternLine("#C#")
                .key('#', DETags.Items.NUGGETS_DRACONIUM_AWAKENED)
                .key('A', DEContent.CORE_DRACONIUM)
                .key('B', DEModules.WYVERN_AUTO_FEED.get().getItem())
                .key('C', Items.COOKIE);

        //Night Vision
        shapedRecipe(DEModules.WYVERN_NIGHT_VISION.get().getItem(), "modules")
                .patternLine("#P#")
                .patternLine("ABA")
                .patternLine("#P#")
                .key('#', DETags.Items.INGOTS_DRACONIUM)
                .key('A', DEContent.CORE_DRACONIUM)
                .key('B', DEContent.MODULE_CORE)
                .key('P', NBTIngredient.of(true, PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.NIGHT_VISION)));

        //Jump Boost
        shapedRecipe(DEModules.DRACONIUM_JUMP.get().getItem(), "modules")
                .patternLine("CPD")
                .patternLine("ABA")
                .patternLine("DPC")
                .key('A', Tags.Items.DUSTS_GLOWSTONE)
                .key('B', DEContent.MODULE_CORE)
                .key('C', Tags.Items.INGOTS_IRON)
                .key('D', Tags.Items.INGOTS_GOLD)
                .key('P', NBTIngredient.of(true, PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.LEAPING)));

        shapedRecipe(DEModules.WYVERN_JUMP.get().getItem(), "modules")
                .patternLine("#P#")
                .patternLine("ABA")
                .patternLine("#P#")
                .key('#', DETags.Items.INGOTS_DRACONIUM)
                .key('B', DEContent.CORE_DRACONIUM)
                .key('A', DEModules.DRACONIUM_JUMP.get().getItem())
                .key('P', NBTIngredient.of(true, PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.STRONG_LEAPING)));

        shapedRecipe(DEModules.DRACONIC_JUMP.get().getItem(), "modules")
                .patternLine("###")
                .patternLine("ABA")
                .patternLine("###")
                .key('#', DETags.Items.NUGGETS_DRACONIUM_AWAKENED)
                .key('B', DEContent.CORE_WYVERN)
                .key('A', DEModules.WYVERN_JUMP.get().getItem());

        shapedRecipe(DEModules.CHAOTIC_JUMP.get().getItem(), "modules")
                .patternLine("CCC")
                .patternLine("ABA")
                .patternLine("CCC")
                //                .key('#', chaos_frag_small)
                .key('B', DEContent.CORE_AWAKENED)
                .key('A', DEModules.DRACONIC_JUMP.get().getItem())
                .key('C', DEContent.CHAOS_FRAG_MEDIUM);

        //Aqua
        shapedRecipe(DEModules.WYVERN_AQUA_ADAPT.get().getItem(), "modules")
                .patternLine("#C#")
                .patternLine("ABA")
                .patternLine("#D#")
                .key('#', DETags.Items.INGOTS_DRACONIUM)
                .key('A', DEContent.CORE_DRACONIUM)
                .key('B', DEContent.MODULE_CORE)
                .key('C', Items.HEART_OF_THE_SEA)
                .key('D', Items.IRON_PICKAXE);

        //Hill Step
        shapedRecipe(DEModules.WYVERN_HILL_STEP.get().getItem(), "modules")
                .patternLine("#C#")
                .patternLine("ABA")
                .patternLine("D#D")
                .key('#', DETags.Items.INGOTS_DRACONIUM)
                .key('A', DEContent.CORE_DRACONIUM)
                .key('B', DEContent.MODULE_CORE)
                .key('C', Items.GOLDEN_BOOTS)
                .key('D', Items.PISTON);

        //Arrow Velocity
        shapedRecipe(DEModules.WYVERN_PROJ_VELOCITY.get().getItem(), "modules")
                .patternLine("#P#")
                .patternLine("ABA")
                .patternLine("#C#")
                .key('#', DETags.Items.INGOTS_DRACONIUM)
                .key('C', ItemTags.ARROWS)
                .key('B', DEContent.MODULE_CORE)
                .key('A', DEContent.CORE_DRACONIUM)
                .key('P', NBTIngredient.of(true, PotionUtils.setPotion(new ItemStack(Items.SPLASH_POTION), Potions.STRONG_SWIFTNESS)));

        shapedRecipe(DEModules.DRACONIC_PROJ_VELOCITY.get().getItem(), "modules")
                .patternLine("###")
                .patternLine("ABA")
                .patternLine("###")
                .key('#', DETags.Items.NUGGETS_DRACONIUM_AWAKENED)
                .key('B', DEContent.CORE_WYVERN)
                .key('A', DEModules.WYVERN_PROJ_VELOCITY.get().getItem());

        shapedRecipe(DEModules.CHAOTIC_PROJ_VELOCITY.get().getItem(), "modules")
                .patternLine("CCC")
                .patternLine("ABA")
                .patternLine("CCC")
                //                .key('#', chaos_frag_small)
                .key('B', DEContent.CORE_AWAKENED)
                .key('A', DEModules.DRACONIC_PROJ_VELOCITY.get().getItem())
                .key('C', DEContent.CHAOS_FRAG_MEDIUM);

        //Arrow Accuracy
        shapedRecipe(DEModules.WYVERN_PROJ_ACCURACY.get().getItem(), "modules")
                .patternLine("#P#")
                .patternLine("ABA")
                .patternLine("#C#")
                .key('#', DETags.Items.INGOTS_DRACONIUM)
                .key('C', ItemTags.ARROWS)
                .key('B', DEContent.MODULE_CORE)
                .key('A', DEContent.CORE_DRACONIUM)
                .key('P', Items.TARGET);

        shapedRecipe(DEModules.DRACONIC_PROJ_ACCURACY.get().getItem(), "modules")
                .patternLine("###")
                .patternLine("ABA")
                .patternLine("###")
                .key('#', DETags.Items.NUGGETS_DRACONIUM_AWAKENED)
                .key('B', DEContent.CORE_WYVERN)
                .key('A', DEModules.WYVERN_PROJ_ACCURACY.get().getItem());

        shapedRecipe(DEModules.CHAOTIC_PROJ_ACCURACY.get().getItem(), "modules")
                .patternLine("CCC")
                .patternLine("ABA")
                .patternLine("CCC")
                //                .key('#', chaos_frag_small)
                .key('B', DEContent.CORE_AWAKENED)
                .key('A', DEModules.DRACONIC_PROJ_ACCURACY.get().getItem())
                .key('C', DEContent.CHAOS_FRAG_MEDIUM);

        //Arrow Penetration
        shapedRecipe(DEModules.WYVERN_PROJ_PENETRATION.get().getItem(), "modules")
                .patternLine("#P#")
                .patternLine("ABA")
                .patternLine("#C#")
                .key('#', DETags.Items.INGOTS_DRACONIUM)
                .key('C', ItemTags.ARROWS)
                .key('B', DEContent.MODULE_CORE)
                .key('A', DEContent.CORE_DRACONIUM)
                .key('P', Items.SHIELD);

        shapedRecipe(DEModules.DRACONIC_PROJ_PENETRATION.get().getItem(), "modules")
                .patternLine("###")
                .patternLine("ABA")
                .patternLine("###")
                .key('#', DETags.Items.NUGGETS_DRACONIUM_AWAKENED)
                .key('B', DEContent.CORE_WYVERN)
                .key('A', DEModules.WYVERN_PROJ_PENETRATION.get().getItem());

        shapedRecipe(DEModules.CHAOTIC_PROJ_PENETRATION.get().getItem(), "modules")
                .patternLine("CCC")
                .patternLine("ABA")
                .patternLine("CCC")
                //                .key('#', chaos_frag_small)
                .key('B', DEContent.CORE_AWAKENED)
                .key('A', DEModules.DRACONIC_PROJ_PENETRATION.get().getItem())
                .key('C', DEContent.CHAOS_FRAG_MEDIUM);

        //Arrow Damage
        shapedRecipe(DEModules.WYVERN_PROJ_DAMAGE.get().getItem(), "modules")
                .patternLine("#P#")
                .patternLine("ABA")
                .patternLine("#C#")
                .key('#', DETags.Items.INGOTS_DRACONIUM)
                .key('C', ItemTags.ARROWS)
                .key('B', DEContent.MODULE_CORE)
                .key('A', DEContent.CORE_DRACONIUM)
                .key('P', NBTIngredient.of(true, PotionUtils.setPotion(new ItemStack(Items.SPLASH_POTION), Potions.STRONG_STRENGTH)));

        shapedRecipe(DEModules.DRACONIC_PROJ_DAMAGE.get().getItem(), "modules")
                .patternLine("###")
                .patternLine("ABA")
                .patternLine("###")
                .key('#', DETags.Items.NUGGETS_DRACONIUM_AWAKENED)
                .key('B', DEContent.CORE_WYVERN)
                .key('A', DEModules.WYVERN_PROJ_DAMAGE.get().getItem());

        shapedRecipe(DEModules.CHAOTIC_PROJ_DAMAGE.get().getItem(), "modules")
                .patternLine("CCC")
                .patternLine("ABA")
                .patternLine("CCC")
                //                .key('#', chaos_frag_small)
                .key('B', DEContent.CORE_AWAKENED)
                .key('A', DEModules.DRACONIC_PROJ_DAMAGE.get().getItem())
                .key('C', DEContent.CHAOS_FRAG_MEDIUM);

        //Arrow Anti Grav
        shapedRecipe(DEModules.WYVERN_PROJ_GRAV_COMP.get().getItem(), "modules")
                .patternLine("#P#")
                .patternLine("ABA")
                .patternLine("#C#")
                .key('#', DETags.Items.INGOTS_DRACONIUM)
                .key('C', ItemTags.ARROWS)
                .key('B', DEContent.MODULE_CORE)
                .key('A', DEContent.CORE_DRACONIUM)
                .key('P', NBTIngredient.of(true, PotionUtils.setPotion(new ItemStack(Items.SPLASH_POTION), Potions.LONG_SLOW_FALLING)));

        shapedRecipe(DEModules.DRACONIC_PROJ_GRAV_COMP.get().getItem(), "modules")
                .patternLine("###")
                .patternLine("ABA")
                .patternLine("###")
                .key('#', DETags.Items.NUGGETS_DRACONIUM_AWAKENED)
                .key('B', DEContent.CORE_WYVERN)
                .key('A', DEModules.WYVERN_PROJ_GRAV_COMP.get().getItem());

        shapedRecipe(DEModules.CHAOTIC_PROJ_GRAV_COMP.get().getItem(), "modules")
                .patternLine("CCC")
                .patternLine("ABA")
                .patternLine("CCC")
                //                .key('#', chaos_frag_small)
                .key('B', DEContent.CORE_AWAKENED)
                .key('A', DEModules.DRACONIC_PROJ_GRAV_COMP.get().getItem())
                .key('C', DEContent.CHAOS_FRAG_MEDIUM);

        // Auto Fire
        shapedRecipe(DEModules.WYVERN_AUTO_FIRE.get().getItem(), "modules")
                .patternLine("#P#")
                .patternLine("ABA")
                .patternLine("#C#")
                .key('#', DETags.Items.INGOTS_DRACONIUM)
                .key('C', Items.BOW)
                .key('B', DEContent.MODULE_CORE)
                .key('A', DEContent.CORE_DRACONIUM)
                .key('P', Items.CLOCK);

        // Projectile Anti Immunity
        shapedRecipe(DEModules.DRACONIC_PROJ_ANTI_IMMUNE.get().getItem(), "modules")
                .patternLine("#P#")
                .patternLine("ABA")
                .patternLine("#C#")
                .key('#', DETags.Items.NUGGETS_DRACONIUM_AWAKENED)
                .key('C', Tags.Items.ENDER_PEARLS)
                .key('B', DEContent.MODULE_CORE)
                .key('A', DEContent.CORE_WYVERN)
                .key('P', Items.WITHER_SKELETON_SKULL);
    }

    private void unsorted() {
        shapedRecipe(DEContent.INFUSED_OBSIDIAN)
                .patternLine("ABA")
                .patternLine("BCB")
                .patternLine("ABA")
                .key('A', Items.BLAZE_POWDER)
                .key('B', Tags.Items.OBSIDIAN)
                .key('C', DETags.Items.DUSTS_DRACONIUM);

        shapedRecipe(DEContent.DISLOCATOR_RECEPTACLE)
                .patternLine("ABA")
                .patternLine(" C ")
                .patternLine("A A")
                .key('A', Tags.Items.INGOTS_IRON)
                .key('B', DEContent.CORE_DRACONIUM)
                .key('C', DEContent.INFUSED_OBSIDIAN);

        shapedRecipe(DEContent.DISLOCATOR_PEDESTAL)
                .patternLine(" A ")
                .patternLine(" B ")
                .patternLine("CDC")
                .key('A', Items.STONE_PRESSURE_PLATE)
                .key('B', Tags.Items.STONE)
                .key('C', Items.STONE_SLAB)
                .key('D', Items.BLAZE_POWDER);

        shapedRecipe(DEContent.RAIN_SENSOR)
                .patternLine(" A ")
                .patternLine("BCB")
                .patternLine("DDD")
                .key('A', Items.BUCKET)
                .key('B', Tags.Items.DUSTS_REDSTONE)
                .key('C', Items.STONE_PRESSURE_PLATE)
                .key('D', Items.STONE_SLAB);

        shapedRecipe(DEContent.DISENCHANTER)
                .patternLine("ABA")
                .patternLine("CDC")
                .patternLine("EEE")
                .key('A', Tags.Items.GEMS_EMERALD)
                .key('B', DEContent.CORE_DRACONIUM)
                .key('C', Items.ENCHANTED_BOOK)
                .key('D', Items.ENCHANTING_TABLE)
                .key('E', Items.BOOKSHELF);

        shapedRecipe(DEContent.CELESTIAL_MANIPULATOR)
                .patternLine("ABA")
                .patternLine("CDC")
                .patternLine("EFE")
                .key('A', Tags.Items.STORAGE_BLOCKS_REDSTONE)
                .key('B', Items.CLOCK)
                .key('C', DETags.Items.INGOTS_DRACONIUM)
                .key('D', Items.DRAGON_EGG)
                .key('E', Tags.Items.INGOTS_IRON)
                .key('F', DEContent.CORE_WYVERN);

        shapedRecipe(DEContent.ENTITY_DETECTOR)
                .patternLine("ABA")
                .patternLine("CDC")
                .patternLine("EFE")
                .key('A', Tags.Items.GEMS_LAPIS)
                .key('B', Items.ENDER_EYE)
                .key('C', Tags.Items.DUSTS_REDSTONE)
                .key('D', DETags.Items.INGOTS_DRACONIUM)
                .key('E', Tags.Items.INGOTS_IRON)
                .key('F', DEContent.CORE_DRACONIUM);

        shapedRecipe(DEContent.ENTITY_DETECTOR_ADVANCED)
                .patternLine("ABA")
                .patternLine("CDC")
                .patternLine("EFE")
                .key('A', Tags.Items.STORAGE_BLOCKS_REDSTONE)
                .key('B', Items.SKELETON_SKULL)
                .key('C', Tags.Items.STORAGE_BLOCKS_LAPIS)
                .key('D', Tags.Items.GEMS_DIAMOND)
                .key('E', DETags.Items.INGOTS_DRACONIUM)
                .key('F', DEContent.ENTITY_DETECTOR);

        shapedRecipe(DEContent.FLUID_GATE)
                .patternLine("ABA")
                .patternLine("CDC")
                .patternLine("AEA")
                .key('A', Tags.Items.INGOTS_IRON)
                .key('B', DEContent.POTENTIOMETER)
                .key('C', Items.BUCKET)
                .key('D', DEContent.CORE_DRACONIUM)
                .key('E', Items.COMPARATOR);

        shapedRecipe(DEContent.FLUX_GATE)
                .patternLine("ABA")
                .patternLine("CDC")
                .patternLine("AEA")
                .key('A', Tags.Items.INGOTS_IRON)
                .key('B', DEContent.POTENTIOMETER)
                .key('C', Tags.Items.STORAGE_BLOCKS_REDSTONE)
                .key('D', DEContent.CORE_DRACONIUM)
                .key('E', Items.COMPARATOR);

        shapedRecipe(DEContent.DISLOCATION_INHIBITOR)
                .patternLine("AAA")
                .patternLine("BCB")
                .patternLine("AAA")
                .key('A', Tags.Items.INGOTS_IRON)
                .key('B', Items.IRON_BARS)
                .key('C', DEContent.MAGNET);
    }

    private void compress3x3(Supplier<? extends ItemLike> output, Supplier<? extends ItemLike> input) {
        shapedRecipe(output, "compress")
                .patternLine("###")
                .patternLine("###")
                .patternLine("###")
                .key('#', input);
    }

    private void compress3x3(Supplier<? extends ItemLike> output, TagKey<Item> input) {
        shapedRecipe(output, "compress")
                .patternLine("###")
                .patternLine("###")
                .patternLine("###")
                .key('#', input);
    }

    private void compress2x2(Supplier<? extends ItemLike> output, Supplier<? extends ItemLike> input) {
        shapedRecipe(output, "compress")
                .patternLine("###")
                .patternLine("###")
                .patternLine("###")
                .key('#', input);
    }

    private void deCompress(Supplier<? extends ItemLike> output, int count, Supplier<? extends ItemLike> from) {
        shapelessRecipe(output, count, "decompress")
                .addIngredient(from);
    }

    private void deCompress(Supplier<? extends ItemLike> output, int count, TagKey<Item> from) {
        shapelessRecipe(output, count, "decompress")
                .addIngredient(from);
    }

    private void deCompress(Supplier<? extends ItemLike> output, Supplier<? extends ItemLike> from) {
        deCompress(output, 9, from);
    }

    private void deCompress(Supplier<? extends ItemLike> output, TagKey<Item> from) {
        deCompress(output, 9, from);
    }

//    public String folder(String folder, IForgeRegistryEntry<?> key) {
//        return DraconicEvolution.MODID + ":" + folder + "/" + key.getRegistryName().getPath();
//    }
//
//    public String folder(String folder, String name) {
//        return DraconicEvolution.MODID + ":" + folder + "/" + name;
//    }
//
//    public InventoryChangeTrigger.TriggerInstance has(TagKey<Item> p_206407_) {
//        return inventoryTrigger(ItemPredicate.Builder.item().of(p_206407_).build());
//    }

    //    @Override
//    public void run(HashCache cache) {
//        super.run(cache);
//    }
//

    protected FusionRecipeBuilder fusionRecipe(Supplier<? extends ItemLike> result, ResourceLocation id) {
        return builder(FusionRecipeBuilder.builder(result.get(), 1, id));
    }

    protected FusionRecipeBuilder fusionRecipe(ItemLike result) {
        return builder(FusionRecipeBuilder.builder(result));
    }

    protected FusionRecipeBuilder fusionRecipe(ItemLike result, int count) {
        return builder(FusionRecipeBuilder.builder(new ItemStack(result, count)));
    }

    protected FusionRecipeBuilder fusionRecipe(Supplier<? extends ItemLike> result) {
        return builder(FusionRecipeBuilder.builder(result.get(), 1));
    }

    protected FusionRecipeBuilder fusionRecipe(Supplier<? extends ItemLike> result, String folder) {
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(result.get().asItem());
        return builder(FusionRecipeBuilder.builder(result.get(), 1, new ResourceLocation(id.getNamespace(), folder + "/" + id.getPath())));
    }

    protected FusionRecipeBuilder fusionRecipe(Supplier<? extends ItemLike> result, String folder, Function<String, String> customPath) {
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(result.get().asItem());
        return builder(FusionRecipeBuilder.builder(result.get(), 1, new ResourceLocation(id.getNamespace(), folder + "/" + customPath.apply(id.getPath()))));
    }

    protected FusionRecipeBuilder fusionRecipe(Supplier<? extends ItemLike> result, int count, String folder) {
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(result.get().asItem());
        return builder(FusionRecipeBuilder.builder(result.get(), count, new ResourceLocation(id.getNamespace(), folder + "/" + id.getPath())));
    }

    protected FusionRecipeBuilder fusionRecipe(Supplier<? extends ItemLike> result, int count) {
        return builder(FusionRecipeBuilder.builder(new ItemStack(result.get(), count)));
    }

    protected FusionRecipeBuilder fusionRecipe(ItemStack result) {
        return builder(FusionRecipeBuilder.builder(result, BuiltInRegistries.ITEM.getKey(result.getItem())));
    }

    protected FusionRecipeBuilder fusionRecipe(ItemStack result, ResourceLocation id) {
        return builder(FusionRecipeBuilder.builder(result, id));
    }

    protected FurnaceRecipeBuilder smelting(Supplier<? extends ItemLike> result, String folder) {
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(result.get().asItem());
        return builder(FurnaceRecipeBuilder.smelting(result.get(), 1, new ResourceLocation(id.getNamespace(), folder + "/" + id.getPath())));
    }

    protected FurnaceRecipeBuilder smelting(Supplier<? extends ItemLike> result, String folder, Function<String, String> customPath) {
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(result.get().asItem());
        return builder(FurnaceRecipeBuilder.smelting(result.get(), 1, new ResourceLocation(id.getNamespace(), folder + "/" + customPath.apply(id.getPath()))));
    }

    protected ShapedRecipeBuilder shapedRecipe(Supplier<? extends ItemLike> result, String folder) {
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(result.get().asItem());
        return builder(ShapedRecipeBuilder.builder(result.get(), 1, new ResourceLocation(id.getNamespace(), folder + "/" + id.getPath())));
    }

    protected ShapedRecipeBuilder shapedRecipe(ItemLike result, String folder) {
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(result.asItem());
        return builder(ShapedRecipeBuilder.builder(result, 1, new ResourceLocation(id.getNamespace(), folder + "/" + id.getPath())));
    }

    protected ShapedRecipeBuilder shapedRecipe(Supplier<? extends ItemLike> result, int count, String folder) {
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(result.get().asItem());
        return builder(ShapedRecipeBuilder.builder(result.get(), count, new ResourceLocation(id.getNamespace(), folder + "/" + id.getPath())));
    }

    protected ShapedRecipeBuilder shapedRecipe(Supplier<? extends ItemLike> result, int count, String folder, Function<String, String> customPath) {
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(result.get().asItem());
        return builder(ShapedRecipeBuilder.builder(result.get(), count, new ResourceLocation(id.getNamespace(), folder + "/" + customPath.apply(id.getPath()))));
    }

    protected ShapelessRecipeBuilder shapelessRecipe(Supplier<? extends ItemLike> result, String folder) {
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(result.get().asItem());
        return builder(ShapelessRecipeBuilder.builder(new ItemStack(result.get(), 1), new ResourceLocation(id.getNamespace(), folder + "/" + id.getPath())));
    }

    protected ShapelessRecipeBuilder shapelessRecipe(Supplier<? extends ItemLike> result, String folder, Function<String, String> customPath) {
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(result.get().asItem());
        return builder(ShapelessRecipeBuilder.builder(new ItemStack(result.get(), 1), new ResourceLocation(id.getNamespace(), folder + "/" + customPath.apply(id.getPath()))));
    }

    protected ShapelessRecipeBuilder shapelessRecipe(Supplier<? extends ItemLike> result, int count, String folder) {
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(result.get().asItem());
        return builder(ShapelessRecipeBuilder.builder(new ItemStack(result.get(), count), new ResourceLocation(id.getNamespace(), folder + "/" + id.getPath())));
    }

    protected ShapelessRecipeBuilder shapelessRecipe(ItemLike result, int count, ResourceLocation id) {
        return builder(ShapelessRecipeBuilder.builder(new ItemStack(result, count), id));
    }

    protected ShapelessRecipeBuilder shapelessRecipe(ItemLike result, int count, String folder, Function<String, String> customPath) {
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(result.asItem());
        return builder(ShapelessRecipeBuilder.builder(new ItemStack(result, count), new ResourceLocation(id.getNamespace(), folder + "/" + customPath.apply(id.getPath()))));
    }
}
