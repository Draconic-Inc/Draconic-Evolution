package com.brandon3055.draconicevolution.integration.crafttweaker.handler;

import com.blamejared.crafttweaker.api.ingredient.IIngredient;
import com.blamejared.crafttweaker.api.item.MCItemStack;
import com.blamejared.crafttweaker.api.recipe.handler.IRecipeHandler;
import com.blamejared.crafttweaker.api.recipe.handler.IReplacementRule;
import com.blamejared.crafttweaker.api.recipe.handler.helper.ReplacementHandlerHelper;
import com.blamejared.crafttweaker.api.util.IngredientUtil;
import com.blamejared.crafttweaker.api.util.StringUtil;
import com.brandon3055.draconicevolution.api.crafting.FusionRecipe;
import com.brandon3055.draconicevolution.api.crafting.IFusionRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;

import java.util.BitSet;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.function.Function;
import java.util.stream.Collectors;

@IRecipeHandler.For(FusionRecipe.class)
public class FusionRecipeHandler implements IRecipeHandler<FusionRecipe> {

    @Override
    public String dumpToCommandString(com.blamejared.crafttweaker.api.recipe.manager.base.IRecipeManager manager, FusionRecipe recipe) {
        String outputStr = "<recipetype:draconicevolution:fusion_crafting>.addRecipe(%s, %s, %s, %s, TechLevel.%s, [%s]);";
        StringJoiner ingredientJoiner = new StringJoiner(", ");
        for (IFusionRecipe.IFusionIngredient fusionIngredient : recipe.fusionIngredients()) {
            ingredientJoiner.add("FusionIngredient.of(" + IIngredient.fromIngredient(fusionIngredient.get()).getCommandString() + ", " + fusionIngredient.consume() + ")");
        }
        return String.format(outputStr, StringUtil.quoteAndEscape(recipe.getId()), new MCItemStack(recipe.getResultItem()).getCommandString(), IIngredient.fromIngredient(recipe.getCatalyst()).getCommandString(), recipe.getEnergyCost(), recipe.getRecipeTier().name(), ingredientJoiner);
    }

    @Override
    public Optional<Function<ResourceLocation, FusionRecipe>> replaceIngredients(com.blamejared.crafttweaker.api.recipe.manager.base.IRecipeManager manager, FusionRecipe recipe, List<IReplacementRule> rules) throws ReplacementNotSupportedException {
        // fusionIngredients returns IFusionIngredient even though it's internal list is FusionIngredient, so lets just map it.
        List<FusionRecipe.FusionIngredient> iFusionIngredients = recipe.fusionIngredients().stream().map(iFusionIngredient -> (FusionRecipe.FusionIngredient) iFusionIngredient).collect(Collectors.toList());
        return ReplacementHandlerHelper.replaceIngredientList(iFusionIngredients, FusionRecipe.FusionIngredient.class, recipe, rules, newIngredients -> id -> new FusionRecipe(id, recipe.getResultItem(), recipe.getCatalyst(), recipe.getEnergyCost(), recipe.getRecipeTier(), newIngredients));
    }

    @Override
    public <U extends Recipe<?>> boolean doesConflict(com.blamejared.crafttweaker.api.recipe.manager.base.IRecipeManager manager, FusionRecipe firstRecipe, U secondRecipe) {
        final FusionRecipe second = (FusionRecipe) secondRecipe;

        if (firstRecipe.fusionIngredients().size() != second.fusionIngredients().size()) {
            return false;
        }

        // if the catalyst doesn't conflict, then we don't need to check the rest.
        if (!IngredientUtil.canConflict(firstRecipe.getCatalyst(), second.getCatalyst())) {
            return false;
        }

        List<FusionRecipe.FusionIngredient> firstIngredients = firstRecipe.fusionIngredients().stream().map(iFusionIngredient -> (FusionRecipe.FusionIngredient) iFusionIngredient).collect(Collectors.toList());
        List<FusionRecipe.FusionIngredient> secondIngredients = second.fusionIngredients().stream().map(iFusionIngredient -> (FusionRecipe.FusionIngredient) iFusionIngredient).collect(Collectors.toList());

        return craftVirtually(firstIngredients, secondIngredients);
    }

    // Adapted from CTShapelessRecipeHandler#craftShapelessRecipeVirtually
    private boolean craftVirtually(final List<FusionRecipe.FusionIngredient> first, final List<FusionRecipe.FusionIngredient> second) {
        final BitSet visitData = new BitSet(second.size());

        for (final FusionRecipe.FusionIngredient target : first) {

            for (int i = 0; i < second.size(); ++i) {

                if (visitData.get(i))
                    continue;

                final FusionRecipe.FusionIngredient attempt = second.get(i);

                if (IngredientUtil.canConflict(target.get(), attempt.get())) {

                    visitData.set(i);
                    break;
                }
            }
        }

        // Since all ingredients must have been used, visitData must have been set fully to 1
        return visitData.nextClearBit(0) == second.size();
    }
}
