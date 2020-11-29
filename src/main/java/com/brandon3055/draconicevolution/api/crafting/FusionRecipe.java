package com.brandon3055.draconicevolution.api.crafting;

import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.api.DraconicAPI;
import com.brandon3055.draconicevolution.init.DEContent;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

/**
 * Created by brandon3055 on 24/11/20
 */
public class FusionRecipe implements IFusionRecipe {


    @Override
    public boolean matches(IFusionInventory inv, World worldIn) {
        return false;
    }

    @Override
    public ItemStack getCraftingResult(IFusionInventory inv) {
        return null;
    }

    @Override
    public boolean canFit(int width, int height) {
        return false;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return new ItemStack(Items.APPLE);
    }

    @Override
    public ResourceLocation getId() {
        return new ResourceLocation(DraconicEvolution.MODID, "test_recipe");
    }
}
