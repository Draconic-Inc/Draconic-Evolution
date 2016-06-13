package com.brandon3055.draconicevolution;

import com.brandon3055.draconicevolution.api.fusioncrafting.FusionRecipeRegistry;
import com.brandon3055.draconicevolution.api.fusioncrafting.SimpleFusionRecipe;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

/**
 * Created by brandon3055 on 11/06/2016.
 */
public class FusionRecipes {

    public static void registerRecipes() {
        FusionRecipeRegistry.registerRecipe(new SimpleFusionRecipe(new ItemStack(Items.STONE_AXE), new ItemStack(Items.WOODEN_AXE), new ItemStack[] {new ItemStack(Items.DIAMOND), new ItemStack(Blocks.OBSIDIAN), new ItemStack(DEFeatures.draconiumDust), new ItemStack(DEFeatures.draconicCore)}, 1000, 0));
        FusionRecipeRegistry.registerRecipe(new SimpleFusionRecipe(new ItemStack(Items.IRON_AXE), new ItemStack(Items.STONE_AXE), new ItemStack[] {new ItemStack(Items.DIAMOND), new ItemStack(Blocks.OBSIDIAN), new ItemStack(DEFeatures.draconiumDust), new ItemStack(DEFeatures.draconicCore)}, 1000, 1));
        FusionRecipeRegistry.registerRecipe(new SimpleFusionRecipe(new ItemStack(Items.GOLDEN_AXE), new ItemStack(Items.IRON_AXE), new ItemStack[] {new ItemStack(Items.DIAMOND), new ItemStack(Blocks.OBSIDIAN), new ItemStack(DEFeatures.draconiumDust), new ItemStack(DEFeatures.draconicCore)}, 1000, 2));
        FusionRecipeRegistry.registerRecipe(new SimpleFusionRecipe(new ItemStack(Items.DIAMOND_AXE), new ItemStack(Items.GOLDEN_AXE), new ItemStack[] {new ItemStack(Items.DIAMOND), new ItemStack(Blocks.OBSIDIAN), new ItemStack(DEFeatures.draconiumDust), new ItemStack(DEFeatures.draconicCore)}, 1000, 3));
    }
}
