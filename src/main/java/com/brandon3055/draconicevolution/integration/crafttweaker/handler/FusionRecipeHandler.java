package com.brandon3055.draconicevolution.integration.crafttweaker.handler;

import com.blamejared.crafttweaker.api.item.IIngredient;
import com.blamejared.crafttweaker.api.managers.IRecipeManager;
import com.blamejared.crafttweaker.api.recipes.IRecipeHandler;
import com.blamejared.crafttweaker.api.util.StringUtils;
import com.blamejared.crafttweaker.impl.item.MCItemStackMutable;
import com.brandon3055.draconicevolution.api.crafting.FusionRecipe;
import com.brandon3055.draconicevolution.api.crafting.IFusionRecipe;

import java.util.StringJoiner;

@IRecipeHandler.For(FusionRecipe.class)
public class FusionRecipeHandler implements IRecipeHandler<FusionRecipe> {
    
    @Override
    public String dumpToCommandString(IRecipeManager manager, FusionRecipe recipe) {
        String outputStr = "<recipetype:draconicevolution:fusion_crafting>.addRecipe(%s, %s, %s, %s, TechLevel.%s, [%s]);";
        StringJoiner ingredientJoiner = new StringJoiner(", ");
        for(IFusionRecipe.IFusionIngredient fusionIngredient : recipe.fusionIngredients()) {
            ingredientJoiner.add("FusionIngredient.of(" + IIngredient.fromIngredient(fusionIngredient.get()).getCommandString() + ", " + fusionIngredient.consume() + ")");
        }
        return String.format(outputStr, StringUtils.quoteAndEscape(recipe.getId()), new MCItemStackMutable(recipe.getResultItem()).getCommandString(), IIngredient.fromIngredient(recipe.getCatalyst()).getCommandString(), recipe.getEnergyCost(), recipe.getRecipeTier().name(), ingredientJoiner);
    }
    
}
