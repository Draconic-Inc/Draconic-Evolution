package com.brandon3055.draconicevolution.client.guidebook;

import com.brandon3055.draconicevolution.api.DraconicAPI;
import com.brandon3055.draconicevolution.api.crafting.FusionRecipe;
import com.brandon3055.draconicevolution.api.crafting.IFusionRecipe;
import com.brandon3055.draconicevolution.init.DEContent;
import guideme.compiler.tags.RecipeTypeMappingSupplier;
import guideme.document.block.recipes.LytStandardRecipeBox;
import net.minecraft.world.item.crafting.RecipeHolder;

public class RecipeTypeContributions implements RecipeTypeMappingSupplier {

    @Override
    public void collect(RecipeTypeMappings mappings) {
        mappings.add(DraconicAPI.FUSION_RECIPE_TYPE.value(), RecipeTypeContributions::fusion);
    }

    private static LytStandardRecipeBox<IFusionRecipe> fusion(RecipeHolder<IFusionRecipe> holder) {
        return LytStandardRecipeBox.builder()
                .icon(DEContent.CRAFTING_CORE.get())
                .title(DEContent.CRAFTING_CORE.get().asItem().getDescription().getString())
                .customBody(new LytFusionCraftingRecipe((FusionRecipe) holder.value()))
                .build(holder);
    }
}
