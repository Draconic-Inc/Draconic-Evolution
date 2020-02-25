package com.brandon3055.draconicevolution.integration.jei;

import com.brandon3055.draconicevolution.DEContent;
import com.brandon3055.draconicevolution.api.fusioncrafting.IFusionRecipe;
import com.brandon3055.draconicevolution.utils.LogHelper;
import mezz.jei.api.*;
import mezz.jei.api.ingredients.IModIngredientRegistration;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import mezz.jei.api.recipe.transfer.IRecipeTransferRegistry;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by brandon3055 on 24/07/2016.
 */
@JEIPlugin
public class DEJEIPlugin implements IModPlugin {

    public static IJeiHelpers jeiHelpers = null;
    public static IJeiRuntime jeiRuntime = null;
    public static List<ItemStack> iUpgradables = new ArrayList<>();

    public DEJEIPlugin() {
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistry subtypeRegistry) {
    }

    @Override
    public void registerIngredients(IModIngredientRegistration registry) {
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry) {
        registry.addRecipeCategories(new FusionRecipeCategory(registry.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void register(IModRegistry registry) {
        LogHelper.dev("Register JEI");
        jeiHelpers = registry.getJeiHelpers();

//        registry.handleRecipes(EIOSpawnerRecipesWrapper.class, recipe -> recipe, VanillaRecipeCategoryUid.CRAFTING);
        registry.handleRecipes(IFusionRecipe.class, FusionRecipeWrapper::new, RecipeCategoryUids.FUSION_CRAFTING);

        if (DEContent.crafting_core.isBlockEnabled()){
            registry.addRecipeCatalyst(new ItemStack(DEContent.crafting_core), RecipeCategoryUids.FUSION_CRAFTING);
        }
        if (DEContent.draconium_chest.isBlockEnabled()) {
            registry.addRecipeCatalyst(new ItemStack(DEContent.draconium_chest), VanillaRecipeCategoryUid.CRAFTING);
            registry.addRecipeCatalyst(new ItemStack(DEContent.draconium_chest), VanillaRecipeCategoryUid.SMELTING);
        }

//        registry.addRecipeClickArea(GuiFusionCraftingCore.class, 81, 45, 18, 22, RecipeCategoryUids.FUSION_CRAFTING);
//        registry.addRecipeClickArea(GuiDraconiumChest.class, 394, 216, 22, 15, VanillaRecipeCategoryUid.CRAFTING);
//        registry.addRecipeClickArea(GuiDraconiumChest.class, 140, 202, 15, 22, VanillaRecipeCategoryUid.SMELTING);

        hideFeature(DEContent.chaos_crystal);
        hideFeature(DEContent.placed_item);
        hideFeature(DEContent.energy_core_structure);
//        hideFeature(DEContent.chaosShardAtmos);

        IRecipeTransferRegistry recipeTransferRegistry = registry.getRecipeTransferRegistry();
//        recipeTransferRegistry.addRecipeTransferHandler(new IRecipeTransferInfo<ContainerDraconiumChest>() {
//            @Override
//            public Class<ContainerDraconiumChest> getContainerClass() {
//                return ContainerDraconiumChest.class;
//            }
//
//            @Override
//            public String getRecipeCategoryUid() {
//                return VanillaRecipeCategoryUid.CRAFTING;
//            }
//
//            @Override
//            public boolean canHandle(ContainerDraconiumChest container) {
//                return true;
//            }
//
//            @Override
//            public List<Slot> getRecipeSlots(ContainerDraconiumChest container) {
//                List<Slot> slots = new ArrayList<>();
//                for (Slot slot : container.inventorySlots) {
//                    if (slot.slotNumber >= 268 && slot.slotNumber <= 276) {
//                        slots.add(slot);
//                    }
//                }
//                return slots;
//            }
//
//            @Override
//            public List<Slot> getInventorySlots(ContainerDraconiumChest container) {
//                List<Slot> slots = new ArrayList<>();
//                for (Slot slot : container.inventorySlots) {
//                    if ((slot.slotNumber >= 0 && slot.slotNumber < 260) || slot.slotNumber > 275) {
//                        slots.add(slot);
//                    }
//                }
//                LogHelper.dev("Inventory Slots: " + slots);
//                return slots;
//            }
//        });
//
//        registry.addRecipes(RecipeManager.FUSION_REGISTRY.getRecipes());
//
//        Item borkedSpawner = Item.REGISTRY.getObject(new ResourceLocation("enderio:item_broken_spawner"));
//        if (borkedSpawner != null) {
//            List<EIOSpawnerRecipesWrapper> wrappers = new ArrayList<>();
//            wrappers.add(new EIOSpawnerRecipesWrapper(jeiHelpers.getGuiHelper(), DEFeatures.draconicCore, borkedSpawner));
//            wrappers.add(new EIOSpawnerRecipesWrapper(jeiHelpers.getGuiHelper(), DEFeatures.wyvernCore, borkedSpawner));
//            wrappers.add(new EIOSpawnerRecipesWrapper(jeiHelpers.getGuiHelper(), DEFeatures.awakenedCore, borkedSpawner));
//            wrappers.add(new EIOSpawnerRecipesWrapper(jeiHelpers.getGuiHelper(), DEFeatures.chaoticCore, borkedSpawner));
//            registry.addRecipes(wrappers);
//        }
//
//        iUpgradables.clear();
//        Iterator<ItemStack> i = registry.getIngredientRegistry().getIngredients(ItemStack.class).iterator();
//        while (i.hasNext()) {
//            ItemStack stack = i.next();
//            if (!stack.isEmpty() && stack.getItem() instanceof IUpgradableItem) {
//                if ((EnergyUtils.isEnergyItem(stack) || stack.getItem() instanceof WyvernArmor) && EnergyUtils.getEnergyStored(stack) == 0) {
//                    continue;
//                }
//
//                LogHelper.dev("Add Upgradable: " + stack);
//                iUpgradables.add(stack);
//            }
//        }
    }

    private void hideFeature(Object feature) {
//        if (ModFeatureParser.isEnabled(feature)) {
//            if (feature instanceof Item) {
//                jeiHelpers.getIngredientBlacklist().addIngredientToBlacklist(new ItemStack((Item) feature));
//            }
//            else if (feature instanceof Block) {
//                jeiHelpers.getIngredientBlacklist().addIngredientToBlacklist(new ItemStack((Block) feature));
//            }
//        }
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime iJeiRuntime) {
        jeiRuntime = iJeiRuntime;
    }

    public static void reloadJEI() {
//        if (jeiHelpers != null) {
//            jeiHelpers.reload();
//        }
    }


}
