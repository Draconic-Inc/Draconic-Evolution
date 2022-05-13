package com.brandon3055.draconicevolution.integration.crafttweaker.handler;

import com.blamejared.crafttweaker.api.ingredient.IIngredient;
import com.blamejared.crafttweaker.api.recipe.handler.IReplacementRule;
import com.brandon3055.draconicevolution.api.crafting.FusionRecipe;
import net.minecraft.world.item.crafting.Recipe;

import java.util.Objects;
import java.util.Optional;

public final class FusionIngredientReplacementRule implements IReplacementRule {

    private final FusionRecipe.FusionIngredient from;
    private final FusionRecipe.FusionIngredient to;

    private FusionIngredientReplacementRule(final FusionRecipe.FusionIngredient from, final FusionRecipe.FusionIngredient to) {
        this.from = from;
        this.to = to;
    }

    public static IReplacementRule create(final FusionRecipe.FusionIngredient from, final FusionRecipe.FusionIngredient to) {
        return areTheSame(from, to) ? IReplacementRule.EMPTY : new FusionIngredientReplacementRule(from, to);
    }

    private static boolean areTheSame(final FusionRecipe.FusionIngredient a, final FusionRecipe.FusionIngredient b) {
        IIngredient aIngredient = IIngredient.fromIngredient(a.get());
        IIngredient bIngredient = IIngredient.fromIngredient(b.get());
        // early exit
        if (a.consume() != b.consume()) {
            return false;
        }

        return a == b || Objects.equals(a, b) || (aIngredient.contains(bIngredient) && bIngredient.contains(aIngredient));
    }

    @Override
    public <T, U extends Recipe<?>> Optional<T> getReplacement(final T ingredient, final Class<T> type, final U recipe) {
        return IReplacementRule.withType(ingredient, type, recipe, FusionRecipe.FusionIngredient.class, this::getIIngredientReplacement);
    }

    private <U extends Recipe<?>> Optional<FusionRecipe.FusionIngredient> getIIngredientReplacement(final FusionRecipe.FusionIngredient original, final U recipe) {
        return areTheSame(this.from, original) ? Optional.of(this.to) : Optional.empty();
    }


    @Override
    public String describe() {
        return String.format("Replacing FusionIngredient.of(%s, %s) --> FusionIngredient.of(%s, %s)", IIngredient.fromIngredient(this.from.get()).getCommandString(), this.from.consume(), IIngredient.fromIngredient(this.to.get()).getCommandString(), this.to.consume());
    }

}
