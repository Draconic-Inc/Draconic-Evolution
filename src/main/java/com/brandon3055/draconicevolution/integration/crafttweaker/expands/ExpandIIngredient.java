//package com.brandon3055.draconicevolution.integration.crafttweaker.expands;
//
//import com.blamejared.crafttweaker.api.annotation.ZenRegister;
//import com.blamejared.crafttweaker.api.ingredient.IIngredient;
//import com.brandon3055.draconicevolution.api.crafting.FusionRecipe;
//import org.openzen.zencode.java.ZenCodeType;
//
//@ZenRegister
//@ZenCodeType.Expansion("crafttweaker.api.ingredient.IIngredient")
//public class ExpandIIngredient {
//
//    // Makes sure that users can provide an IIngredient to a method asking for a FusionIngredient.
//    @ZenCodeType.Method
//    @ZenCodeType.Caster(implicit = true)
//    public static FusionRecipe.FusionIngredient asFusionIngredient(IIngredient internal) {
//        return new FusionRecipe.FusionIngredient(internal.asVanillaIngredient(), true);
//    }
//
//}
