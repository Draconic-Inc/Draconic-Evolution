package com.brandon3055.draconicevolution.integration.jei;

import com.brandon3055.brandonscore.integration.ModularGuiProperties;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.api.crafting.IFusionRecipe;
import com.brandon3055.draconicevolution.client.gui.EnergyCoreGui;
import com.brandon3055.draconicevolution.client.gui.modular.itemconfig.ConfigurableItemGui;
import com.brandon3055.draconicevolution.init.DEContent;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.*;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Created by brandon3055 on 24/07/2016.
 */
@JeiPlugin
public class DEJEIPlugin implements IModPlugin {

    public static IJeiHelpers jeiHelpers = null;
    public static IJeiRuntime jeiRuntime = null;
    public static RecipeType<IFusionRecipe> FUSION_RECIPE_TYPE = RecipeType.create(DraconicEvolution.MODID, "fusion_crafting", IFusionRecipe.class);
//
//    @Nullable
//    private FusionRecipeCategory fusionRecipeCategory;
//
    public DEJEIPlugin() {
    }
//
    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addGuiScreenHandler(ConfigurableItemGui.Screen.class, gui -> null);
        registration.addGuiScreenHandler(EnergyCoreGui.Screen.class, gui -> gui.hideJEI.get() ? null : ModularGuiProperties.create(gui.getModularGui()));
//        registration.addGuiContainerHandler(GuiDraconiumChest.class, new IGuiContainerHandler<>() {
//            @Override
//            public Collection<IGuiClickableArea> getGuiClickableAreas(GuiDraconiumChest gui, double mouseX, double mouseY) {
//                if (gui.colourDialog.isVisible()) return Collections.emptyList();
//                GuiElement<?> craftIcon = gui.craftIcon;
//                GuiElement<?> smeltArrow = gui.furnaceProgress;
//                IGuiClickableArea craftingArea = IGuiClickableArea.createBasic(craftIcon.xPos() - gui.guiLeft(), craftIcon.yPos() - gui.guiTop(), craftIcon.xSize(), craftIcon.ySize(), RecipeTypes.CRAFTING);
//                IGuiClickableArea smeltingArea = IGuiClickableArea.createBasic(smeltArrow.xPos() - gui.guiLeft(), smeltArrow.yPos() - gui.guiTop(), smeltArrow.xSize(), smeltArrow.ySize(), RecipeTypes.SMELTING);
//                return Lists.newArrayList(craftingArea, smeltingArea);
//            }
//        });
//        registration.addGuiContainerHandler(GuiFusionCraftingCore.class, new IGuiContainerHandler<>() {
//            @Override
//            public Collection<IGuiClickableArea> getGuiClickableAreas(GuiFusionCraftingCore gui, double mouseX, double mouseY) {
//                GuiElement<?> craftIcon = gui.stackIcon;
//                IGuiClickableArea craftingArea = IGuiClickableArea.createBasic(craftIcon.xPos() - gui.guiLeft(), craftIcon.yPos() - gui.guiTop(), craftIcon.xSize(), craftIcon.ySize(), FUSION_RECIPE_TYPE);
//                return Collections.singletonList(craftingArea);
//            }
//        });
//    }
//
//    @Override
//    public void registerAdvanced(IAdvancedRegistration registration) {
//    }
//
//    @Override
//    public void registerIngredients(IModIngredientRegistration registration) {
//    }
//
//    @Override
//    public void registerCategories(IRecipeCategoryRegistration registration) {
//        IJeiHelpers jeiHelpers = registration.getJeiHelpers();
//        IGuiHelper guiHelper = jeiHelpers.getGuiHelper();
//        registration.addRecipeCategories(fusionRecipeCategory = new FusionRecipeCategory(guiHelper));
//    }
//
//    @Override
//    public void registerRecipes(IRecipeRegistration registration) {
//        jeiHelpers = registration.getJeiHelpers();
//
//        ClientLevel world = Minecraft.getInstance().level;
//        if (world == null) return;
//
//        registration.addRecipes(FUSION_RECIPE_TYPE, world.getRecipeManager().getAllRecipesFor(DraconicAPI.FUSION_RECIPE_TYPE));
    }

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
//        IJeiHelpers jeiHelpers = registration.getJeiHelpers();
//        IRecipeTransferHandlerHelper transferHelper = registration.getTransferHelper();
//        IStackHelper stackHelper = jeiHelpers.getStackHelper();
//        registration.addRecipeTransferHandler(new FusionRecipeTransferHelper(stackHelper, transferHelper), FUSION_RECIPE_TYPE);
//
//        //Draconium chest recipe movers
//        registration.addRecipeTransferHandler(new IRecipeTransferInfo<ContainerDraconiumChest, CraftingRecipe>() {
//            @Override
//            public Class<ContainerDraconiumChest> getContainerClass() {
//                return ContainerDraconiumChest.class;
//            }
//
//            @Override
//            public RecipeType<CraftingRecipe> getRecipeType() {
//                return RecipeTypes.CRAFTING;
//            }
//
//            @SuppressWarnings("removal")
//            @Override
//            public ResourceLocation getRecipeCategoryUid() {
//                return VanillaRecipeCategoryUid.CRAFTING;
//            }
//
//            @Override
//            public boolean canHandle(ContainerDraconiumChest container, CraftingRecipe recipe) {
//                return true;
//            }
//
//            @Override
//            public List<Slot> getRecipeSlots(ContainerDraconiumChest container, CraftingRecipe recipe) {
//                return container.craftInputSlots;
//            }
//
//            @Override
//            public List<Slot> getInventorySlots(ContainerDraconiumChest container, CraftingRecipe recipe) {
//                return Stream.of(container.mainSlots, container.playerSlots).flatMap(Collection::stream).collect(Collectors.toList());
//            }
//
//            @SuppressWarnings("removal")
//            @Override
//            public Class<CraftingRecipe> getRecipeClass() {
//                return CraftingRecipe.class;
//            }
//        });
//
//        //TODO Look into adding a custom transfer helper that utilizes all of the furnace slots.
//        registration.addRecipeTransferHandler(new IRecipeTransferInfo<ContainerDraconiumChest, SmeltingRecipe>() {
//            @Override
//            public Class<ContainerDraconiumChest> getContainerClass() {
//                return ContainerDraconiumChest.class;
//            }
//
//            @Override
//            public boolean canHandle(ContainerDraconiumChest container, SmeltingRecipe recipe) {
//                return true;
//            }
//
//            @Override
//            public List<Slot> getRecipeSlots(ContainerDraconiumChest container, SmeltingRecipe recipe) {
//                return container.furnaceInputSlots;
//            }
//
//            @Override
//            public List<Slot> getInventorySlots(ContainerDraconiumChest container, SmeltingRecipe recipe) {
//                return Stream.of(container.mainSlots, container.playerSlots).flatMap(Collection::stream).collect(Collectors.toList());
//            }
//
//            @SuppressWarnings("removal")
//            @Override
//            public Class<SmeltingRecipe> getRecipeClass() {
//                return SmeltingRecipe.class;
//            }
//
//            @Override
//            public RecipeType<SmeltingRecipe> getRecipeType() {
//                return RecipeTypes.SMELTING;
//            }
//
//            @SuppressWarnings("removal")
//            @Override
//            public ResourceLocation getRecipeCategoryUid() {
//                return VanillaRecipeCategoryUid.FURNACE;
//            }
//        });
//
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(DEContent.CRAFTING_CORE.get()), FUSION_RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(DEContent.DRACONIUM_CHEST.get()), RecipeTypes.CRAFTING, RecipeTypes.SMELTING);
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime iJeiRuntime) {
        jeiRuntime = iJeiRuntime;
    }


    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return new ResourceLocation(DraconicEvolution.MODID, "jei_plugin");
    }
}
