package com.brandon3055.draconicevolution.integration.crafttweaker.managers;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.action.recipe.ActionAddRecipe;
import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import com.blamejared.crafttweaker.api.ingredient.IIngredient;
import com.blamejared.crafttweaker.api.item.IItemStack;
import com.blamejared.crafttweaker.api.recipe.manager.base.IRecipeManager;
import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.draconicevolution.api.DraconicAPI;
import com.brandon3055.draconicevolution.api.crafting.FusionRecipe;
import com.brandon3055.draconicevolution.api.crafting.IFusionRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeType;
import org.openzen.zencode.java.ZenCodeType;

import java.util.Arrays;

@ZenRegister
@ZenCodeType.Name("mods.draconicevolution.FusionManager")
public class FusionManager implements IRecipeManager {

    @ZenCodeType.Method
    public void addRecipe(String name, IItemStack result, IIngredient catalyst, long totalEnergy, TechLevel techLevel, FusionRecipe.FusionIngredient[] ingredients) {
        name = fixRecipeName(name);
        FusionRecipe fusionRecipe = new FusionRecipe(new ResourceLocation(name), result.getInternal(), catalyst.asVanillaIngredient(), totalEnergy, techLevel, Arrays.asList(ingredients));
        CraftTweakerAPI.apply(new ActionAddRecipe<>(this, fusionRecipe));
    }

    @Override
    public RecipeType<IFusionRecipe> getRecipeType() {
        return DraconicAPI.FUSION_RECIPE_TYPE.get();
    }
}
