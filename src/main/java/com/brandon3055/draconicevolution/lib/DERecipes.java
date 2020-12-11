//package com.brandon3055.draconicevolution.lib;
//
//import com.brandon3055.draconicevolution.DEConfig;
//import com.brandon3055.draconicevolution.DEFeatures;
//import com.brandon3055.draconicevolution.items.ToolUpgrade;
//import net.minecraft.item.Item;
//import net.minecraft.item.ItemStack;
//import net.minecraft.util.ResourceLocation;
//import net.minecraftforge.fml.common.registry.GameRegistry;
//
//import static com.brandon3055.draconicevolution.DEFeatures.*;
//import static com.brandon3055.draconicevolution.init.RecipeManager.RecipeDifficulty.*;
//import static com.brandon3055.draconicevolution.init.RecipeManager.*;
//import static net.minecraft.init.Blocks.*;
//import static net.minecraft.init.Items.*;
//import static net.minecraft.init.Items.SKULL;
//
///**
// * Created by brandon3055 on 23/07/2016.
// */
//public class DERecipes {
//
//    public static void addRecipes() {
//

//        //endregion
//
//        //region Exotic Items
//
//        //endregion
//
//        /* ------------------ Other ------------------ */
//        if (RecipeManager.isEnabled(DEFeatures.draconiumDust) && RecipeManager.isEnabled(DEFeatures.draconiumIngot)) {
//            GameRegistry.addSmelting(DEFeatures.draconiumDust, new ItemStack(DEFeatures.draconiumIngot), 0);
//        }
//
//        if (RecipeManager.isEnabled(DEFeatures.draconiumOre) && RecipeManager.isEnabled(DEFeatures.draconicIngot)) {
//            GameRegistry.addSmelting(DEFeatures.draconiumOre, new ItemStack(DEFeatures.draconiumIngot), 0);
//        }
//
//        RecipeManager.addRecipe(new RecipeDislocatorClone().setRegistryName(new ResourceLocation("draconicevolution:recipe_dislocator_clone")));
//
//        Item borkedSpawner = Item.REGISTRY.getObject(new ResourceLocation("enderio:item_broken_spawner"));
//        if (borkedSpawner != null) {
//            RecipeManager.addRecipe(new RecipeEIOStabilization(borkedSpawner).setRegistryName(new ResourceLocation("draconicevolution:eio_spawner")));
//        }
//
//        if (DEConfig.clearDataRecipes) {
//            for (int i = 0; i < 9; i++) {
//                addShapeless(ALL, new ItemStack(energyCrystal, 1, i), new ItemStack(energyCrystal, 1, i));
//            }
//            for (int i = 0; i < 4; i++) {
//                addShapeless(ALL, new ItemStack(craftingInjector, 1, i), new ItemStack(craftingInjector, 1, i));
//            }
//
//            addShapeless(ALL, new ItemStack(grinder, 1, 0), new ItemStack(craftingInjector, 1, 0));
//            addShapeless(ALL, new ItemStack(generator, 1, 0), new ItemStack(craftingInjector, 1, 0));
//            addShapeless(ALL, new ItemStack(energyInfuser, 1, 0), new ItemStack(craftingInjector, 1, 0));
//        }
//    }
//
//    public static ItemStack getKey(String name) {
//        return new ItemStack(DEFeatures.toolUpgrade, 1, ToolUpgrade.NAME_TO_ID.get(name));
//    }
//
//    public static void addUpgradeKey(ItemStack input, String name) {
//        addShaped(ALL, new ItemStack(DEFeatures.toolUpgrade, 1, ToolUpgrade.NAME_TO_ID.get(name)), "ABA", "CDC", "ABA", 'A', new ItemStack(DYE, 1, 4), 'B', draconicCore, 'C', "ingotDraconium", 'D', input);
//    }
//}
