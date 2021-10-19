package com.brandon3055.draconicevolution.integration.jei;

import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.api.DraconicAPI;
import com.brandon3055.draconicevolution.api.crafting.IFusionRecipe;
import com.brandon3055.draconicevolution.client.gui.modular.itemconfig.GuiConfigurableItem;
import com.brandon3055.draconicevolution.init.DEContent;
import com.brandon3055.draconicevolution.inventory.ContainerFusionCraftingCore;
import mezz.jei.api.*;
import mezz.jei.api.constants.VanillaRecipeCategoryUid;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.helpers.IStackHelper;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandlerHelper;
import mezz.jei.api.registration.*;
import mezz.jei.api.runtime.IJeiRuntime;
import mezz.jei.plugins.vanilla.cooking.CampfireCategory;
import mezz.jei.util.ErrorUtil;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by brandon3055 on 24/07/2016.
 */
@JeiPlugin
public class DEJEIPlugin implements IModPlugin {

    public static IJeiHelpers jeiHelpers = null;
    public static IJeiRuntime jeiRuntime = null;

    @Nullable
    private FusionRecipeCategory fusionRecipeCategory;

    public DEJEIPlugin() {
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addGuiScreenHandler(GuiConfigurableItem.class, (gui) -> null);
    }

    @Override
    public void registerAdvanced(IAdvancedRegistration registration) {
    }

    @Override
    public void registerIngredients(IModIngredientRegistration registration) {
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IJeiHelpers jeiHelpers = registration.getJeiHelpers();
        IGuiHelper guiHelper = jeiHelpers.getGuiHelper();
        registration.addRecipeCategories(fusionRecipeCategory = new FusionRecipeCategory(guiHelper));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        ErrorUtil.checkNotNull(fusionRecipeCategory, "fusionRecipeCategory");
        jeiHelpers = registration.getJeiHelpers();

        ClientWorld world = Minecraft.getInstance().level;
        registration.addRecipes(world.getRecipeManager().getAllRecipesFor(DraconicAPI.FUSION_RECIPE_TYPE), RecipeCategoryUids.FUSION_CRAFTING);
    }

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        IJeiHelpers jeiHelpers = registration.getJeiHelpers();
        IRecipeTransferHandlerHelper transferHelper = registration.getTransferHelper();
        IStackHelper stackHelper = jeiHelpers.getStackHelper();
        registration.addRecipeTransferHandler(new FusionRecipeTransferHelper(stackHelper, transferHelper), RecipeCategoryUids.FUSION_CRAFTING);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(DEContent.crafting_core), RecipeCategoryUids.FUSION_CRAFTING);

//        if (DEContent.crafting_core.isBlockEnabled()){
//            registration.addRecipeCatalyst(new ItemStack(DEContent.crafting_core), RecipeCategoryUids.FUSION_CRAFTING);
//        }
//        if (DEContent.draconium_chest.isBlockEnabled()) {
//            registration.addRecipeCatalyst(new ItemStack(DEContent.draconium_chest), VanillaRecipeCategoryUid.CRAFTING);
//            registration.addRecipeCatalyst(new ItemStack(DEContent.draconium_chest), VanillaRecipeCategoryUid.FURNACE);
//        }
    }


    //    @Override
//    public void register(IModRegistry registry) {
//        LogHelper.dev("Register JEI");


//        registry.addRecipeClickArea(GuiFusionCraftingCore.class, 81, 45, 18, 22, RecipeCategoryUids.FUSION_CRAFTING);
//        registry.addRecipeClickArea(GuiDraconiumChest.class, 394, 216, 22, 15, VanillaRecipeCategoryUid.CRAFTING);
//        registry.addRecipeClickArea(GuiDraconiumChest.class, 140, 202, 15, 22, VanillaRecipeCategoryUid.SMELTING);

//        hideFeature(DEContent.chaos_crystal);
//        hideFeature(DEContent.placed_item);
//        hideFeature(DEContent.energy_core_structure);
//        hideFeature(DEContent.chaosShardAtmos);

//        IRecipeTransferRegistry recipeTransferRegistry = registry.getRecipeTransferRegistry();
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
//    }

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


    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(DraconicEvolution.MODID, "jei_plugin");
    }
}
