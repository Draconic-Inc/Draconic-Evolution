package com.brandon3055.draconicevolution.integration.crafttweaker.expands;

import com.blamejared.crafttweaker.api.annotations.ZenRegister;
import com.blamejared.crafttweaker.impl.recipes.Replacer;
import com.brandon3055.draconicevolution.api.crafting.FusionRecipe;
import com.brandon3055.draconicevolution.integration.crafttweaker.handler.FusionIngredientReplacementRule;
import org.openzen.zencode.java.ZenCodeType;

@ZenRegister
@ZenCodeType.Expansion("crafttweaker.api.recipe.Replacer")
public class ExpandReplacer {
    
    @ZenCodeType.Method
    public static Replacer replaceFusion(final Replacer internal, final FusionRecipe.FusionIngredient from, final FusionRecipe.FusionIngredient to) {
        return internal.addReplacementRule(FusionIngredientReplacementRule.create(from, to));
    }
}
