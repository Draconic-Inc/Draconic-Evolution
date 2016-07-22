package com.brandon3055.draconicevolution;

import com.brandon3055.draconicevolution.api.fusioncrafting.FusionRecipeRegistry;
import com.brandon3055.draconicevolution.api.fusioncrafting.SimpleFusionRecipe;
import com.brandon3055.draconicevolution.items.ToolUpgrade;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

/**
 * Created by brandon3055 on 11/06/2016.
 */
public class FusionRecipes {

    public static void registerRecipes() {
        ToolUpgrade.addUpgradeRecipes();

        FusionRecipeRegistry.registerRecipe(new SimpleFusionRecipe(new ItemStack(Items.STONE_AXE), new ItemStack(Items.WOODEN_AXE), 1000, 0, new ItemStack(Items.DIAMOND), new ItemStack(Blocks.OBSIDIAN), new ItemStack(DEFeatures.draconiumDust), new ItemStack(DEFeatures.draconicCore)));
        FusionRecipeRegistry.registerRecipe(new SimpleFusionRecipe(new ItemStack(Items.IRON_AXE), new ItemStack(Items.STONE_AXE), 1000, 0, new ItemStack(Items.DIAMOND), new ItemStack(Blocks.OBSIDIAN), new ItemStack(DEFeatures.draconiumDust), new ItemStack(DEFeatures.draconicCore)));
        FusionRecipeRegistry.registerRecipe(new SimpleFusionRecipe(new ItemStack(Items.GOLDEN_AXE), new ItemStack(Items.IRON_AXE), 1000, 0, new ItemStack(Items.DIAMOND), new ItemStack(Blocks.OBSIDIAN), new ItemStack(DEFeatures.draconiumDust), new ItemStack(DEFeatures.draconicCore)));
        FusionRecipeRegistry.registerRecipe(new SimpleFusionRecipe(new ItemStack(Items.DIAMOND_AXE), new ItemStack(Items.GOLDEN_AXE), 1000, 0, new ItemStack(Items.DIAMOND), new ItemStack(Blocks.OBSIDIAN), new ItemStack(DEFeatures.draconiumDust), new ItemStack(DEFeatures.draconicCore)));
        FusionRecipeRegistry.registerRecipe(new SimpleFusionRecipe(new ItemStack(DEFeatures.wyvernAxe), new ItemStack(Items.DIAMOND_AXE), 1000, 0, new ItemStack(Items.DIAMOND), new ItemStack(Blocks.OBSIDIAN), new ItemStack(DEFeatures.draconiumDust), new ItemStack(DEFeatures.draconicCore)));

        FusionRecipeRegistry.registerRecipe(new SimpleFusionRecipe(new ItemStack(Blocks.BEACON, 12), new ItemStack(Blocks.OBSIDIAN), 1000, 3, "gemDiamond", "gemDiamond", "gemDiamond", "ingotIron", "dustRedstone", new ItemStack(Items.GOLDEN_SWORD), Items.APPLE, Blocks.DIAMOND_ORE));


        FusionRecipeRegistry.registerRecipe(new SimpleFusionRecipe(new ItemStack(Items.STONE_AXE), new ItemStack(Items.WOODEN_AXE), 1000, 0, new ItemStack(DEFeatures.draconicCore)));


        //BTM Demo Recipe
        FusionRecipeRegistry.registerRecipe(new SimpleFusionRecipe(new ItemStack(DEFeatures.draconicBlock, 4), new ItemStack(DEFeatures.draconiumBlock, 4, 1), 1000000, 3, "netherStar", "netherStar", DEFeatures.wyvernCore, DEFeatures.wyvernCore, DEFeatures.dragonHeart, "netherStar", "netherStar", DEFeatures.wyvernCore, DEFeatures.wyvernCore));
   }
}
