package com.brandon3055.draconicevolution.api;

import com.brandon3055.draconicevolution.api.crafting.FusionRecipe;
import net.minecraft.block.Block;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ObjectHolder;

/**
 * Created by brandon3055 on 26/11/20
 */
public class DraconicAPI {

    /**
     * Assigned by Forge when the serializer is registered.
     */
    @ObjectHolder("draconicevolution:fusion_crafting")
    public static FusionRecipe.Serializer FUSION_RECIPE_SERIALIZER;

    /**
     * Assigned by Draconic Evolution during mod construction.
     */
    public static IRecipeType<FusionRecipe> FUSION_RECIPE_TYPE;

    @ObjectHolder("draconicevolution:crafting_core")
    public static Block CRAFTING_CORE;

    public static ResourceLocation INGREDIENT_STACK_TYPE = new ResourceLocation("draconicevolution:ingredient_stack");
}
