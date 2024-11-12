package com.brandon3055.draconicevolution.api;

import com.brandon3055.draconicevolution.api.crafting.FusionRecipe;
import com.brandon3055.draconicevolution.api.crafting.IFusionRecipe;
import com.brandon3055.draconicevolution.init.DEModules;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredHolder;

/**
 * Created by brandon3055 on 26/11/20
 */
public class DraconicAPI {


    public static DeferredHolder<RecipeSerializer<?>, FusionRecipe.Serializer> FUSION_RECIPE_SERIALIZER;

    /**
     * Assigned by Draconic Evolution during mod construction.
     */
    public static DeferredHolder<RecipeType<?>, RecipeType<IFusionRecipe>> FUSION_RECIPE_TYPE;

//    @ObjectHolder(registryName = "block", value = "draconicevolution:crafting_core")
    public static Block CRAFTING_CORE;

    public static ResourceLocation INGREDIENT_STACK_TYPE = new ResourceLocation("draconicevolution:ingredient_stack");

    /**
     * Any mod that wants to add its own custom modules needs to register itself here so that its module textures will be loaded into the modules atlas.
     * This should be called in your mod constructor.
     */
    public static void addModuleProvider(String modid) {
        DEModules.MODULE_PROVIDING_MODS.add(modid);
    }
}
