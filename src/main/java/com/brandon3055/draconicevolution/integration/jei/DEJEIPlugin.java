package com.brandon3055.draconicevolution.integration.jei;

import cofh.redstoneflux.api.IEnergyContainerItem;
import com.brandon3055.brandonscore.items.ItemEnergyBase;
import com.brandon3055.brandonscore.registry.ModFeatureParser;
import com.brandon3055.draconicevolution.DEFeatures;
import com.brandon3055.draconicevolution.api.IJEIClearence;
import com.brandon3055.draconicevolution.api.fusioncrafting.IFusionRecipe;
import com.brandon3055.draconicevolution.api.itemupgrade.IUpgradableItem;
import com.brandon3055.draconicevolution.client.gui.GuiDraconiumChest;
import com.brandon3055.draconicevolution.client.gui.GuiFusionCraftingCore;
import com.brandon3055.draconicevolution.inventory.ContainerDraconiumChest;
import com.brandon3055.draconicevolution.items.armor.WyvernArmor;
import com.brandon3055.draconicevolution.lib.RecipeManager;
import com.brandon3055.draconicevolution.utils.LogHelper;
import mezz.jei.api.*;
import mezz.jei.api.gui.IAdvancedGuiHandler;
import mezz.jei.api.ingredients.IModIngredientRegistration;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import mezz.jei.api.recipe.transfer.IRecipeTransferInfo;
import mezz.jei.api.recipe.transfer.IRecipeTransferRegistry;
import net.minecraft.block.Block;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
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

//        registry.getRecipeTransferRegistry().addRecipeTransferHandler(new IRecipeTransferHandler<ContainerFusionCraftingCore>() {
//            @Override
//            public Class<ContainerFusionCraftingCore> getContainerClass() {
//                return ContainerFusionCraftingCore.class;
//            }
//
//            @Nullable
//            @Override
//            public IRecipeTransferError transferRecipe(ContainerFusionCraftingCore container, IRecipeLayout recipeLayout, EntityPlayer player, boolean maxTransfer, boolean doTransfer) {
//                LogHelper.dev("Transfer Recipe");
//                IRecipeTransferError error = new IRecipeTransferError() {
//                    @Override
//                    public Type getType() {
//                        return Type.USER_FACING;
//                    }
//
//                    @Override
//                    public void showError(Minecraft minecraft, int mouseX, int mouseY, IRecipeLayout recipeLayout, int recipeX, int recipeY) {
//
//
//                    }
//                };
//
//                return error;
//            }
//        }, RecipeCategoryUids.FUSION_CRAFTING);

//        registry.getRecipeTransferRegistry().addRecipeTransferHandler(new IRecipeTransferInfo<Container>() {
//            @Override
//            public Class<Container> getContainerClass() {
//                return null;
//            }
//
//            @Override
//            public String getRecipeCategoryUid() {
//                return null;
//            }
//
//            @Override
//            public boolean canHandle(Container container) {
//                return false;
//            }
//
//            @Override
//            public List<Slot> getRecipeSlots(Container container) {
//                return null;
//            }
//
//            @Override
//            public List<Slot> getInventorySlots(Container container) {
//                return null;
//            }
//        });

        registry.handleRecipes(EIOSpawnerRecipesWrapper.class, recipe -> recipe, VanillaRecipeCategoryUid.CRAFTING);
        registry.handleRecipes(IFusionRecipe.class, FusionRecipeWrapper::new, RecipeCategoryUids.FUSION_CRAFTING);

        if (DEFeatures.fusionCraftingCore.isBlockEnabled()){
            registry.addRecipeCatalyst(new ItemStack(DEFeatures.fusionCraftingCore), RecipeCategoryUids.FUSION_CRAFTING);
        }
        if (DEFeatures.draconiumChest.isBlockEnabled()) {
            registry.addRecipeCatalyst(new ItemStack(DEFeatures.draconiumChest), VanillaRecipeCategoryUid.CRAFTING);
            registry.addRecipeCatalyst(new ItemStack(DEFeatures.draconiumChest), VanillaRecipeCategoryUid.SMELTING);
        }

        registry.addRecipeClickArea(GuiFusionCraftingCore.class, 81, 45, 18, 22, RecipeCategoryUids.FUSION_CRAFTING);
        registry.addRecipeClickArea(GuiDraconiumChest.class, 394, 216, 22, 15, VanillaRecipeCategoryUid.CRAFTING);
        registry.addRecipeClickArea(GuiDraconiumChest.class, 140, 202, 15, 22, VanillaRecipeCategoryUid.SMELTING);

        hideFeature(DEFeatures.chaosCrystal);
        hideFeature(DEFeatures.placedItem);
        hideFeature(DEFeatures.invisECoreBlock);
        hideFeature(DEFeatures.chaosShardAtmos);

        IRecipeTransferRegistry recipeTransferRegistry = registry.getRecipeTransferRegistry();
        recipeTransferRegistry.addRecipeTransferHandler(new IRecipeTransferInfo<ContainerDraconiumChest>() {
            @Override
            public Class<ContainerDraconiumChest> getContainerClass() {
                return ContainerDraconiumChest.class;
            }

            @Override
            public String getRecipeCategoryUid() {
                return VanillaRecipeCategoryUid.CRAFTING;
            }

            @Override
            public boolean canHandle(ContainerDraconiumChest container) {
                return true;
            }

            @Override
            public List<Slot> getRecipeSlots(ContainerDraconiumChest container) {
                List<Slot> slots = new ArrayList<>();
                for (Slot slot : container.inventorySlots) {
                    if (slot.slotNumber >= 267 && slot.slotNumber <= 275) {
                        slots.add(slot);
                    }
                }
                return slots;
            }

            @Override
            public List<Slot> getInventorySlots(ContainerDraconiumChest container) {
                List<Slot> slots = new ArrayList<>();
                for (Slot slot : container.inventorySlots) {
                    if ((slot.slotNumber >= 0 && slot.slotNumber < 260) || slot.slotNumber > 275) {
                        slots.add(slot);
                    }
                }
                LogHelper.dev("Inventory Slots: " + slots);
                return slots;
            }
        });

        registry.addRecipes(RecipeManager.FUSION_REGISTRY.getRecipes());
        registry.addAdvancedGuiHandlers(new IAdvancedGuiHandler() {
            @Override
            public Class getGuiContainerClass() {
                return IJEIClearence.class;
            }

            @Nullable
            @Override
            public java.util.List<Rectangle> getGuiExtraAreas(GuiContainer guiContainer) {
                if (guiContainer instanceof IJEIClearence) {
                    return ((IJEIClearence) guiContainer).getGuiExtraAreas();
                }
                return new ArrayList<>();
            }

            @Nullable
            @Override
            public Object getIngredientUnderMouse(GuiContainer guiContainer, int mouseX, int mouseY) {
                return null;
            }
        });

        Item borkedSpawner = Item.REGISTRY.getObject(new ResourceLocation("enderio:itemBrokenSpawner"));
        if (borkedSpawner != null) {
            List<EIOSpawnerRecipesWrapper> wrappers = new ArrayList<>();
            wrappers.add(new EIOSpawnerRecipesWrapper(jeiHelpers.getGuiHelper(), DEFeatures.draconicCore, borkedSpawner));
            wrappers.add(new EIOSpawnerRecipesWrapper(jeiHelpers.getGuiHelper(), DEFeatures.wyvernCore, borkedSpawner));
            wrappers.add(new EIOSpawnerRecipesWrapper(jeiHelpers.getGuiHelper(), DEFeatures.awakenedCore, borkedSpawner));
            wrappers.add(new EIOSpawnerRecipesWrapper(jeiHelpers.getGuiHelper(), DEFeatures.chaoticCore, borkedSpawner));
            registry.addRecipes(wrappers);
        }

        iUpgradables.clear();
        Iterator<ItemStack> i = registry.getIngredientRegistry().getIngredients(ItemStack.class).iterator();
        while (i.hasNext()) {
            ItemStack stack = i.next();
            if (!stack.isEmpty() && stack.getItem() instanceof IUpgradableItem) {
                if ((stack.getItem() instanceof ItemEnergyBase || stack.getItem() instanceof WyvernArmor) && ((IEnergyContainerItem) stack.getItem()).getEnergyStored(stack) == 0) {
                    continue;
                }

                LogHelper.dev("Add Upgradable: " + stack);
                iUpgradables.add(stack);
            }
        }
    }

    private void hideFeature(Object feature) {
        if (ModFeatureParser.isEnabled(feature)) {
            if (feature instanceof Item) {
                jeiHelpers.getIngredientBlacklist().addIngredientToBlacklist(new ItemStack((Item) feature));
            }
            else if (feature instanceof Block) {
                jeiHelpers.getIngredientBlacklist().addIngredientToBlacklist(new ItemStack((Block) feature));
            }
        }
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
