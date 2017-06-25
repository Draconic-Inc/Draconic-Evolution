package com.brandon3055.draconicevolution.integration.jei;

import com.brandon3055.draconicevolution.DEFeatures;
import com.brandon3055.draconicevolution.blocks.tileentity.TileStabilizedSpawner.SpawnerTier;
import com.brandon3055.draconicevolution.items.ItemCore;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.ICraftingGridHelper;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.IRecipeWrapperFactory;
import mezz.jei.api.recipe.wrapper.ICustomCraftingRecipeWrapper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class EIOSpawnerRecipesWrapper extends BlankRecipeWrapper implements ICustomCraftingRecipeWrapper {

    private static final int craftOutputSlot = 0;
    private static final int craftInputSlot1 = 1;
    private final ItemCore core;
    private final ICraftingGridHelper craftingGridHelper;
    private final Item brokenSpawner;
    private List<ItemStack> inputs = new ArrayList<>();
    private ItemStack output;

    public EIOSpawnerRecipesWrapper(IGuiHelper guiHelper, ItemCore core, Item brokenSpawner) {
        super();
        this.core = core;
        craftingGridHelper = guiHelper.createCraftingGridHelper(craftInputSlot1, craftOutputSlot);
        this.brokenSpawner = brokenSpawner;

        inputs.add(new ItemStack(brokenSpawner));
        inputs.add(new ItemStack(core));
        output = new ItemStack(DEFeatures.stabilizedSpawner);
        DEFeatures.stabilizedSpawner.setStackDataTier(output, SpawnerTier.getTierFromCore(core));
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        ingredients.setInputs(ItemStack.class, inputs);
        ingredients.setOutput(ItemStack.class, output);
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, IIngredients ingredients) {
        IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();

        Object focus = recipeLayout.getFocus().getValue();

        guiItemStacks.init(craftOutputSlot, false, 94, 18);

        for (int y = 0; y < 3; ++y) {
            for (int x = 0; x < 3; ++x) {
                int index = craftInputSlot1 + x + (y * 3);
                guiItemStacks.init(index, true, x * 18, y * 18);
            }
        }

        List<List<ItemStack>> inputs = ingredients.getInputs(ItemStack.class);
        List<List<ItemStack>> outputs = ingredients.getOutputs(ItemStack.class);

//		craftingGridHelper.setInputStacks(guiItemStacks, inputs);
//
//		if (focus instanceof ItemStack && ((ItemStack) focus).getItem() == brokenSpawner && ((ItemStack) focus).hasTagCompound() && ((ItemStack) focus).getTagCompound().hasKey("entityId")) {
//			ItemStack setSpawner = output.copy();
//			DEFeatures.stabilizedSpawner.setStackDataEntity(setSpawner, ((ItemStack) focus).getTagCompound().getString("entityId"));
//			craftingGridHelper.setOutput(guiItemStacks, new ArrayList<ItemStack>(){{add(setSpawner);}});
//		}
//		else {
//			craftingGridHelper.setOutput(guiItemStacks, outputs);
        guiItemStacks.set(ingredients);
//		}
    }

    public static class Factory implements IRecipeWrapperFactory<EIOSpawnerRecipesWrapper> {

        @Override
        public IRecipeWrapper getRecipeWrapper(EIOSpawnerRecipesWrapper recipe) {
            return recipe;
        }
    }
}
