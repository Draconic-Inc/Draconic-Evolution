package com.brandon3055.draconicevolution.integration.jei;

import codechicken.lib.gui.modular.lib.GuiRender;
import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.brandonscore.utils.Utils;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.api.crafting.IFusionRecipe;
import com.brandon3055.draconicevolution.init.DEContent;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Locale;

/**
 * Created by brandon3055 on 24/07/2016.
 */
public class FusionRecipeCategory implements IRecipeCategory<RecipeHolder<IFusionRecipe>> {

    private final IDrawable background;
    private final IDrawable icon;
    private final Component localizedName;
    private final int xSize = 164;
    private final int ySize = 111;

    public FusionRecipeCategory(IGuiHelper guiHelper) {
        background = guiHelper.createDrawable(new ResourceLocation(DraconicEvolution.MODID, "textures/gui/jei_fusion_background.png"), 0, 0, xSize, ySize);
        localizedName = Component.translatable(DEContent.CRAFTING_CORE.get().getDescriptionId());
        icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(DEContent.CRAFTING_CORE.get()));
    }

    @Nonnull
    @Override
    public RecipeType<RecipeHolder<IFusionRecipe>> getRecipeType() {
        return DEJEIPlugin.getFusionRecipeType();
    }

    @Nonnull
    @Override
    public Component getTitle() {
        return localizedName;
    }

    @Nonnull
    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Nonnull
    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void draw(RecipeHolder<IFusionRecipe> recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
        GuiRender render = GuiRender.convert(graphics);
        TechLevel tier = recipe.value().getRecipeTier();
        int colour = tier.index == 0 ? 5263615 : (tier.index == 1 ? 8388863 : (tier.index == 2 ? 16737792 : 5263440));
        render.drawCenteredString(I18n.get("gui.draconicevolution.fusion_craft.tier." + recipe.value().getRecipeTier().name().toLowerCase(Locale.ENGLISH)), this.xSize / 2D, 5, colour, false);
        render.drawCenteredString(I18n.get("gui.draconicevolution.fusion_craft.energy_cost"), this.xSize / 2D, this.ySize - 20, 4474111, false);
        render.drawCenteredString(Utils.addCommas(recipe.value().getEnergyCost()) + " OP", this.xSize / 2D, this.ySize - 10, 4500223, false);

        render.borderRect((xSize / 2D) - 10, 22, 20, 66, 1, 0x40FFFFFF, 0xFF00FFFF);
        if (recipe.value().getIngredients().size() > 16) {
            render.borderRect(3, 2, 18, 107, 1, 0x40FFFFFF, 0xFFAA00FF);
            render.borderRect(23, 2, 18, 107, 1, 0x40FFFFFF, 0xFFAA00FF);
            render.borderRect(xSize - 21, 2, 18, 107, 1, 0x40FFFFFF, 0xFFAA00FF);
            render.borderRect(xSize - 41, 2, 18, 107, 1, 0x40FFFFFF, 0xFFAA00FF);
        } else {
            render.borderRect(12, 2, 20, 107, 1, 0x40FFFFFF, 0xFFAA00FF);
            render.borderRect(xSize - 32, 2, 20, 107, 1, 0x40FFFFFF, 0xFFAA00FF);
        }
    }

    @Override
    public void setRecipe(@NotNull IRecipeLayoutBuilder builder, RecipeHolder<IFusionRecipe> recipe, @NotNull IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, xSize / 2 - 8, ySize / 2 - 8 - 23)
                .addIngredients(recipe.value().getCatalyst())
                .setSlotName("catalyst");

        builder.addSlot(RecipeIngredientRole.OUTPUT, xSize / 2 - 8, ySize / 2 - 8 + 23)
                .addItemStack(recipe.value().getResultItem(Minecraft.getInstance().level.registryAccess()))
                .setSlotName("output");

        List<Ingredient> ingreds = recipe.value().getIngredients();
        int nColumns = ingreds.size() > 16 ? 4 : 2;
        int xc = xSize / 2 - 8;
        int yc = ySize / 2;// - 8;
        int rows = (int) Math.ceil(ingreds.size() / (double) nColumns);

        for (int i = 0; i < ingreds.size(); i++) {
            int side = (i % nColumns) >= nColumns / 2 ? 1 : -1;
            int offset = nColumns == 2 ? 0 : i % 2 == 0 ? -1 : 1;
            int row = i / nColumns;

            int xPos = xc + (side * (60 + (offset * 10)));
            int yPos = yc;
            if (rows > 1) {
                yPos = (yc - 42) + ((84 / (rows - 1)) * row);
            }

            builder.addSlot(RecipeIngredientRole.INPUT, xPos, yPos - 8)
                    .addIngredients(ingreds.get(i))
                    .setSlotName("input_" + i);
        }
    }
}
