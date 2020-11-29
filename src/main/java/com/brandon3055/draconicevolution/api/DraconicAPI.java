package com.brandon3055.draconicevolution.api;

import com.brandon3055.draconicevolution.api.crafting.FusionRecipe;
import com.brandon3055.draconicevolution.api.crafting.FusionRecipeSerializer;
import com.brandon3055.draconicevolution.blocks.machines.FusionCraftingCore;
import net.minecraft.block.Block;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraftforge.registries.ObjectHolder;

/**
 * Created by brandon3055 on 26/11/20
 */
public class DraconicAPI {

    /**
     * Assigned by Forge when the serializer is registered.
     */
    @ObjectHolder("draconicevolution:fusion_crafting")
    public static FusionRecipeSerializer FUSION_RECIPE_SERIALIZER;

    /**
     * Assigned by Draconic Evolution during mod construction.
     */
    public static IRecipeType<FusionRecipe> FUSION_RECIPE_TYPE;

    @ObjectHolder("draconicevolution:crafting_core")
    public static Block CRAFTING_CORE;
}
