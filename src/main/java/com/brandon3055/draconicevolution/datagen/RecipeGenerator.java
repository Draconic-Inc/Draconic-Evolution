package com.brandon3055.draconicevolution.datagen;

import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.draconicevolution.api.DraconicAPI;
import com.brandon3055.draconicevolution.api.crafting.FusionRecipe;
import com.brandon3055.draconicevolution.init.DEContent;
import com.google.gson.JsonObject;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ForgeRegistries;

import java.nio.file.Path;
import java.util.function.Consumer;

/**
 * Created by brandon3055 on 1/12/20
 */
public class RecipeGenerator extends RecipeProvider {

    public RecipeGenerator(DataGenerator generatorIn) {
        super(generatorIn);
    }

    @Override
    protected void registerRecipes(Consumer<IFinishedRecipe> consumer) {

        FusionRecipeBuilder.fusionRecipe(DEContent.staff_chaotic)
                .catalyst(Items.DIAMOND_PICKAXE)
                .techLevel(TechLevel.CHAOTIC)
                .energy(100000)
                .ingredient(Items.NETHER_STAR)
                .ingredient(Items.APPLE)
                .ingredient(Tags.Items.RODS_WOODEN)
                .ingredient(Tags.Items.FENCES)
                .ingredient(false, Items.DIAMOND, Items.COAL, Items.IRON_INGOT)
                .ingredient(Items.CARROT_ON_A_STICK, Items.GOLD_INGOT)
                .ingredient(Blocks.BEACON)
                .ingredient(false, Blocks.DRAGON_EGG)
                .build(consumer, "test_recipe");


        FusionRecipeBuilder.fusionRecipe(DEContent.staff_draconic)
                .catalyst(Items.DIAMOND_AXE)
                .techLevel(TechLevel.CHAOTIC)
                .energy(100000)
                .ingredient(Items.NETHER_STAR)
                .ingredient(Items.CARROT)
                .ingredient(Items.APPLE)
                .ingredient(Items.ACACIA_BOAT)
                .ingredient(Items.ACACIA_DOOR)
                .ingredient(Items.ACACIA_STAIRS)
                .ingredient(Items.ANDESITE_WALL)
                .ingredient(Items.BLUE_SHULKER_BOX)
                .ingredient(Items.BLUE_BANNER)
                .ingredient(Items.BIRCH_FENCE_GATE)
                .ingredient(Items.APPLE)
                .ingredient(Tags.Items.RODS_WOODEN)
                .ingredient(Tags.Items.FENCES)
                .ingredient(false, Items.DIAMOND, Items.COAL, Items.IRON_INGOT)
                .ingredient(Items.CARROT_ON_A_STICK, Items.GOLD_INGOT)
                .ingredient(Blocks.BEACON)
                .ingredient(false, Blocks.DRAGON_EGG)
                .build(consumer, "test_recipe2");



        FusionRecipeBuilder.fusionRecipe(DEContent.bow_chaotic)
                .catalyst(Items.DIAMOND_AXE)
                .techLevel(TechLevel.CHAOTIC)
                .energy(100000)
                .ingredient(Items.NETHER_STAR)
                .ingredient(Items.CARROT)
                .ingredient(Items.APPLE)
                .ingredient(Items.ACACIA_BOAT)
                .ingredient(Items.ACACIA_DOOR)
                .ingredient(Items.ACACIA_STAIRS)
                .ingredient(Items.ANDESITE_WALL)
                .ingredient(Items.BLUE_SHULKER_BOX)
                .ingredient(Items.BLUE_BANNER)
                .ingredient(Items.BIRCH_FENCE_GATE)
                .ingredient(Items.APPLE)
                .ingredient(Items.ZOMBIE_VILLAGER_SPAWN_EGG)
                .ingredient(Items.ZOMBIE_SPAWN_EGG)
                .ingredient(Items.NETHERITE_BOOTS)
                .ingredient(Items.PILLAGER_SPAWN_EGG)
                .ingredient(Items.ZOMBIE_HEAD)
                .ingredient(Items.PURPUR_BLOCK)
                .ingredient(Items.DARK_OAK_BUTTON)
                .ingredient(Items.BLACK_TERRACOTTA)
                .ingredient(Items.SLIME_BLOCK)
                .ingredient(Items.COD_SPAWN_EGG)
                .ingredient(Items.BLAST_FURNACE)
                .ingredient(Items.BOOKSHELF)
                .ingredient(Items.BLUE_CONCRETE_POWDER)
                .ingredient(Items.BLUE_STAINED_GLASS)
                .ingredient(Items.BLUE_CARPET)
                .ingredient(Tags.Items.RODS_WOODEN)
                .ingredient(Tags.Items.FENCES)
                .ingredient(false, Items.DIAMOND, Items.COAL, Items.IRON_INGOT)
                .ingredient(Items.CARROT_ON_A_STICK, Items.GOLD_INGOT)
                .ingredient(Blocks.BEACON)
                .ingredient(false, Blocks.DRAGON_EGG)
                .build(consumer, "test_recipe3");

//        FusionRecipe recipe = Minecraft.getInstance().world.getRecipeManager().getRecipe(DraconicAPI.FUSION_RECIPE_TYPE, /IFusionInventory/, /World/).get();

    }
}
