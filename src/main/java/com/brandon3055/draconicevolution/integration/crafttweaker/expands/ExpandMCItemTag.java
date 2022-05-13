package com.brandon3055.draconicevolution.integration.crafttweaker.expands;

import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import com.blamejared.crafttweaker.api.tag.expand.ExpandItemTag;
import com.blamejared.crafttweaker.api.tag.type.KnownTag;
import com.brandon3055.draconicevolution.api.crafting.FusionRecipe;
import net.minecraft.world.item.Item;
import org.openzen.zencode.java.ZenCodeType;

@ZenRegister
@ZenCodeType.Expansion("crafttweaker.api.tag.MCTag<crafttweaker.api.item.MCItemDefinition>")
public class ExpandMCItemTag {

    // Makes sure that users can provide a tag to a method asking for a FusionIngredient.
    @ZenCodeType.Method
    @ZenCodeType.Caster(implicit = true)
    public static FusionRecipe.FusionIngredient asIIngredient(KnownTag<Item> internal) {
        return ExpandIIngredient.asFusionIngredient(ExpandItemTag.asIIngredient(internal));
    }

}