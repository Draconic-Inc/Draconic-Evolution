package com.brandon3055.draconicevolution.api.crafting;

import com.brandon3055.draconicevolution.utils.LogHelper;
import com.google.gson.JsonObject;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

/**
 * Created by brandon3055 on 24/11/20
 */
public class FusionRecipeSerializer extends net.minecraftforge.registries.ForgeRegistryEntry<IRecipeSerializer<?>>  implements IRecipeSerializer<FusionRecipe>  {
    @Override
    public FusionRecipe read(ResourceLocation recipeId, JsonObject json) {
        LogHelper.dev("");
        return new FusionRecipe();
    }

    @Override
    public FusionRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
        LogHelper.dev("");
        return new FusionRecipe();
    }

    @Override
    public void write(PacketBuffer buffer, FusionRecipe recipe) {
        LogHelper.dev("");
    }
}
