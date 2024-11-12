package com.brandon3055.draconicevolution.integration.jei;

import codechicken.lib.gui.modular.elements.GuiElement;
import com.brandon3055.brandonscore.integration.ModularGuiProperties;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.api.DraconicAPI;
import com.brandon3055.draconicevolution.api.crafting.IFusionRecipe;
import com.brandon3055.draconicevolution.client.gui.DraconiumChestGui;
import com.brandon3055.draconicevolution.client.gui.EnergyCoreGui;
import com.brandon3055.draconicevolution.client.gui.FusionCraftingCoreGui;
import com.brandon3055.draconicevolution.client.gui.modular.itemconfig.ConfigurableItemGui;
import com.brandon3055.draconicevolution.init.DEContent;
import com.brandon3055.draconicevolution.inventory.DraconiumChestMenu;
import com.google.common.collect.Lists;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.gui.handlers.IGuiClickableArea;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.helpers.IStackHelper;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandlerHelper;
import mezz.jei.api.recipe.transfer.IRecipeTransferInfo;
import mezz.jei.api.registration.*;
import mezz.jei.api.runtime.IJeiRuntime;
import net.covers1624.quack.collection.FastStream;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by brandon3055 on 24/07/2016.
 */
@JeiPlugin
public class DEJEIPlugin implements IModPlugin {

    public static IJeiHelpers jeiHelpers = null;
    public static IJeiRuntime jeiRuntime = null;
    private static RecipeType<RecipeHolder<IFusionRecipe>> FUSION_RECIPE_TYPE;
//
    @Nullable
    private FusionRecipeCategory fusionRecipeCategory;
//
    public DEJEIPlugin() {
    }
//
    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addGuiScreenHandler(ConfigurableItemGui.Screen.class, gui -> null);
        registration.addGuiScreenHandler(EnergyCoreGui.Screen.class, gui -> EnergyCoreGui.Screen.hideJEI.get() ? null : ModularGuiProperties.create(gui.getModularGui()));

        registration.addGuiContainerHandler(DraconiumChestGui.Screen.class, new IGuiContainerHandler<>() {
            @Override
            public Collection<IGuiClickableArea> getGuiClickableAreas(DraconiumChestGui.Screen screen, double mouseX, double mouseY) {
                DraconiumChestGui gui = (DraconiumChestGui) screen.getModularGui().getProvider();
                if (gui.colourDialog != null && !gui.colourDialog.isRemoved()) {
                    return Collections.emptyList();
                }
                GuiElement<?> craftIcon = gui.craftIcon;
                GuiElement<?> smeltArrow = gui.furnaceProgress;
                IGuiClickableArea craftingArea = IGuiClickableArea.createBasic((int) craftIcon.xMin() - (int) screen.getModularGui().getRoot().xMin(), (int) craftIcon.yMin() - (int) screen.getModularGui().getRoot().yMin(), (int) craftIcon.xSize(), (int) craftIcon.ySize(), RecipeTypes.CRAFTING);
                IGuiClickableArea smeltingArea = IGuiClickableArea.createBasic((int) smeltArrow.xMin() - (int) screen.getModularGui().getRoot().xMin(), (int) smeltArrow.yMin() - (int) screen.getModularGui().getRoot().yMin(), (int) smeltArrow.xSize(), (int) smeltArrow.ySize(), RecipeTypes.SMELTING);
                return Lists.newArrayList(craftingArea, smeltingArea);
            }
        });
        registration.addGuiContainerHandler(FusionCraftingCoreGui.Screen.class, new IGuiContainerHandler<>() {
            @Override
            public Collection<IGuiClickableArea> getGuiClickableAreas(FusionCraftingCoreGui.Screen screen, double mouseX, double mouseY) {
                FusionCraftingCoreGui gui = (FusionCraftingCoreGui) screen.getModularGui().getProvider();
                if (gui.hideRecipes.get()) {
                    return Collections.emptyList();
                }
                GuiElement<?> craftIcon = gui.stackIcon;
                IGuiClickableArea craftingArea = IGuiClickableArea.createBasic((int) craftIcon.xMin() - (int) screen.getModularGui().getRoot().xMin(), (int) craftIcon.yMin() - (int) screen.getModularGui().getRoot().yMin(), (int) craftIcon.xSize(), (int) craftIcon.ySize(), FUSION_RECIPE_TYPE);
                return Collections.singletonList(craftingArea);
            }
        });
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IJeiHelpers jeiHelpers = registration.getJeiHelpers();
        IGuiHelper guiHelper = jeiHelpers.getGuiHelper();
        registration.addRecipeCategories(fusionRecipeCategory = new FusionRecipeCategory(guiHelper));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        jeiHelpers = registration.getJeiHelpers();

        ClientLevel world = Minecraft.getInstance().level;
        if (world == null) return;

        registration.addRecipes(FUSION_RECIPE_TYPE, FastStream.of(world.getRecipeManager().getAllRecipesFor(DraconicAPI.FUSION_RECIPE_TYPE.get())).toList());
    }

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        IJeiHelpers jeiHelpers = registration.getJeiHelpers();
        IRecipeTransferHandlerHelper transferHelper = registration.getTransferHelper();
        IStackHelper stackHelper = jeiHelpers.getStackHelper();
        registration.addRecipeTransferHandler(new FusionRecipeTransferHelper(stackHelper, transferHelper), FUSION_RECIPE_TYPE);

        //Draconium chest recipe movers
        registration.addRecipeTransferHandler(new IRecipeTransferInfo<DraconiumChestMenu, RecipeHolder<CraftingRecipe>>() {
            @Override
            public Class<DraconiumChestMenu> getContainerClass() {
                return DraconiumChestMenu.class;
            }

            @Override
            public RecipeType<RecipeHolder<CraftingRecipe>> getRecipeType() {
                return RecipeTypes.CRAFTING;
            }

            @Override
            public Optional<MenuType<DraconiumChestMenu>> getMenuType() {
                return Optional.of(DEContent.MENU_DRACONIUM_CHEST.get());
            }

            @Override
            public boolean canHandle(DraconiumChestMenu container, RecipeHolder<CraftingRecipe> recipe) {
                return true;
            }

            @Override
            public List<Slot> getRecipeSlots(DraconiumChestMenu container, RecipeHolder<CraftingRecipe> recipe) {
                return container.craftIn.slots().stream().map(e -> (Slot) e).toList();
            }

            @Override
            public List<Slot> getInventorySlots(DraconiumChestMenu container, RecipeHolder<CraftingRecipe> recipe) {
                return Stream.of(container.chestInv.slots(), container.main.slots(), container.hotBar.slots()).flatMap(Collection::stream).collect(Collectors.toList());
            }
        });

        //TODO Look into adding a custom transfer helper that utilizes all of the furnace slots.
        registration.addRecipeTransferHandler(new IRecipeTransferInfo<DraconiumChestMenu, RecipeHolder<SmeltingRecipe>>() {
            @Override
            public Class<DraconiumChestMenu> getContainerClass() {
                return DraconiumChestMenu.class;
            }

            @Override
            public RecipeType<RecipeHolder<SmeltingRecipe>> getRecipeType() {
                return RecipeTypes.SMELTING;
            }

            @Override
            public Optional<MenuType<DraconiumChestMenu>> getMenuType() {
                return Optional.of(DEContent.MENU_DRACONIUM_CHEST.get());
            }

            @Override
            public boolean canHandle(DraconiumChestMenu container, RecipeHolder<SmeltingRecipe> recipe) {
                return true;
            }

            @Override
            public List<Slot> getRecipeSlots(DraconiumChestMenu container, RecipeHolder<SmeltingRecipe> recipe) {
                return container.furnaceInputs.slots().stream().map(e -> (Slot) e).toList();
            }

            @Override
            public List<Slot> getInventorySlots(DraconiumChestMenu container, RecipeHolder<SmeltingRecipe> recipe) {
                return Stream.of(container.chestInv.slots(), container.main.slots(), container.hotBar.slots()).flatMap(Collection::stream).collect(Collectors.toList());
            }
        });
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

    public static RecipeType<RecipeHolder<IFusionRecipe>> getFusionRecipeType() {
        if (FUSION_RECIPE_TYPE == null) FUSION_RECIPE_TYPE = RecipeType.createFromVanilla(DraconicAPI.FUSION_RECIPE_TYPE.get());
        return FUSION_RECIPE_TYPE;
    }
}
