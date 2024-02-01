//package com.brandon3055.draconicevolution.integration.crafttweaker.expands;
//
//import com.blamejared.crafttweaker.api.annotation.ZenRegister;
//import com.blamejared.crafttweaker.api.ingredient.IIngredient;
//import com.blamejared.crafttweaker_annotations.annotations.NativeTypeRegistration;
//import com.brandon3055.draconicevolution.api.crafting.FusionRecipe;
//import org.openzen.zencode.java.ZenCodeType;
//
//@ZenRegister
//@NativeTypeRegistration(value = FusionRecipe.FusionIngredient.class, zenCodeName = "mods.draconicevolution.FusionIngredient")
//public class ExpandFusionIngredient {
//
//    // Static method for making a new FusionIngredient.
//    @ZenCodeType.StaticExpansionMethod
//    public static FusionRecipe.FusionIngredient of(IIngredient ingredient, boolean consume) {
//        return new FusionRecipe.FusionIngredient(ingredient.asVanillaIngredient(), consume);
//    }
//
//    @ZenCodeType.Method
//    @ZenCodeType.Getter("ingredient")
//    public static IIngredient getIngredient(FusionRecipe.FusionIngredient internal) {
//        return IIngredient.fromIngredient(internal.get());
//    }
//
//    @ZenCodeType.Method
//    @ZenCodeType.Getter("consume")
//    public static boolean doesConsume(FusionRecipe.FusionIngredient internal) {
//        return internal.consume();
//    }
//
//}
