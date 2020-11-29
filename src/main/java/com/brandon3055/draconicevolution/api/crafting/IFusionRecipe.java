package com.brandon3055.draconicevolution.api.crafting;

import com.brandon3055.draconicevolution.api.DraconicAPI;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;

/**
 * Created by brandon3055 on 26/11/20
 */
public interface IFusionRecipe extends IRecipe<IFusionInventory> {

    @Override
    default ItemStack getIcon() {
        if (DraconicAPI.CRAFTING_CORE != null){
            return new ItemStack(DraconicAPI.CRAFTING_CORE);
        }
        else {
            return new ItemStack(Blocks.CRAFTING_TABLE);
        }
    }

    @Override
    default IRecipeType<?> getType() {
        return DraconicAPI.FUSION_RECIPE_TYPE;
    }

    @Override
    default IRecipeSerializer<?> getSerializer() {
        return DraconicAPI.FUSION_RECIPE_SERIALIZER;
    }
}
