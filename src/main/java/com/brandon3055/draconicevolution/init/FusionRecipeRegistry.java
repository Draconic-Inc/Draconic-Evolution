//package com.brandon3055.draconicevolution.init;
//
//import com.brandon3055.draconicevolution.api.fusioncrafting.FusionRegistry;
//import com.brandon3055.draconicevolution.api.fusioncrafting.IFusionCraftingInventory;
//import com.brandon3055.draconicevolution.api.fusioncrafting.IFusionRecipeOld;
//import com.brandon3055.draconicevolution.utils.LogHelper;
//import net.minecraft.item.ItemStack;
//import net.minecraft.util.math.BlockPos;
//import net.minecraft.world.World;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * Created by brandon3055 on 23/07/2016.
// */
//public class FusionRecipeRegistry implements FusionRegistry {
//
//    private final List<IFusionRecipeOld> REGISTRY = new ArrayList<IFusionRecipeOld>();
//
//    //region API Interface
//
//    @Override
//    public void add(IFusionRecipeOld recipe) {
//        REGISTRY.add(recipe);
//    }
//
//    @Override
//    public void remove(IFusionRecipeOld recipe) {
//        if (REGISTRY.contains(recipe)) {
//            REGISTRY.remove(recipe);
//        }
//    }
//
//    @Override
//    public List<IFusionRecipeOld> getRecipes() {
//        return new ArrayList<>(REGISTRY);
//    }
//
//    //endregion
//
//    public IFusionRecipeOld findRecipeForCatalyst(ItemStack catalyst) {
//        if (catalyst.isEmpty()) {
//            return null;
//        }
//
//        for (IFusionRecipeOld recipe : REGISTRY) {
//            if (recipe.isRecipeCatalyst(catalyst)) {
//                return recipe;
//            }
//        }
//
//        return null;
//    }
//
//    public IFusionRecipeOld findRecipe(IFusionCraftingInventory inventory, World world, BlockPos corePos) {
//        if (inventory == null || inventory.getStackInCore(0).isEmpty()) {
//            return null;
//        }
//
//        for (IFusionRecipeOld recipe : REGISTRY) {
//            if (REGISTRY.indexOf(recipe) == 74) {
//                LogHelper.dev("Here!");
//            }
//            if (recipe.matches(inventory, world, corePos)) {
//                return recipe;
//            }
//        }
//
//        return null;
//    }
//}
