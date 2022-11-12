package com.brandon3055.draconicevolution.integration.jei;

import codechicken.lib.render.buffer.TransformingVertexConsumer;
import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.brandonscore.api.render.GuiHelper;
import com.brandon3055.brandonscore.client.utils.GuiHelperOld;
import com.brandon3055.brandonscore.utils.Utils;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.api.crafting.IFusionRecipe;
import com.brandon3055.draconicevolution.init.DEContent;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by brandon3055 on 24/07/2016.
 */
public class FusionRecipeCategory implements IRecipeCategory<IFusionRecipe> {

    private final IDrawable background;
    private final IDrawable icon;
    private final Component localizedName;
    private int xSize = 164;
    private int ySize = 111;

    public FusionRecipeCategory(IGuiHelper guiHelper) {
        background = guiHelper.createDrawable(new ResourceLocation(DraconicEvolution.MODID, "textures/gui/jei_fusion_background.png"), 0, 0, xSize, ySize);
        localizedName = new TranslatableComponent(DEContent.crafting_core.getDescriptionId());
        icon = guiHelper.createDrawableIngredient(new ItemStack(DEContent.crafting_core));
    }

    @Nonnull
    @Override
    public ResourceLocation getUid() {
        return RecipeCategoryUids.FUSION_CRAFTING;
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

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public Class<? extends IFusionRecipe> getRecipeClass() {
        return IFusionRecipe.class;
    }

    @Override
    public void draw(IFusionRecipe recipe, PoseStack matrixStack, double mouseX, double mouseY) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.font != null) {
            TechLevel tier = recipe.getRecipeTier();
            int colour = tier.index == 0 ? 5263615 : (tier.index == 1 ? 8388863 : (tier.index == 2 ? 16737792 : 5263440));
            GuiHelperOld.drawCenteredString(mc.font, matrixStack, I18n.get("gui.draconicevolution.fusion_craft.tier." + recipe.getRecipeTier().name().toLowerCase(Locale.ENGLISH)), this.xSize / 2, 5, colour, false);
            GuiHelperOld.drawCenteredString(mc.font, matrixStack, I18n.get("gui.draconicevolution.fusion_craft.energy_cost"), this.xSize / 2, this.ySize - 20, 4474111, false);
            GuiHelperOld.drawCenteredString(mc.font, matrixStack, Utils.addCommas(recipe.getEnergyCost()) + " OP", this.xSize / 2, this.ySize - 10, 4500223, false);
        }

        MultiBufferSource.BufferSource buffer = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
        TransformingVertexConsumer builder = new TransformingVertexConsumer(buffer.getBuffer(GuiHelper.transColourType), matrixStack);
        GuiHelperOld.drawBorderedRect(builder, (xSize / 2D) - 10, 22, 20, 66, 1, 0x40FFFFFF, 0xFF00FFFF, 0);
        if (recipe.getIngredients().size() > 16) {
            GuiHelperOld.drawBorderedRect(builder, 3, 3, 18, 106, 1, 0x40FFFFFF, 0xFFAA00FF, 0);
            GuiHelperOld.drawBorderedRect(builder, 23, 3, 18, 106, 1, 0x40FFFFFF, 0xFFAA00FF, 0);
            GuiHelperOld.drawBorderedRect(builder, xSize - 21, 3, 18, 106, 1, 0x40FFFFFF, 0xFFAA00FF, 0);
            GuiHelperOld.drawBorderedRect(builder, xSize - 41, 3, 18, 106, 1, 0x40FFFFFF, 0xFFAA00FF, 0);
        } else {
            GuiHelperOld.drawBorderedRect(builder, 12, 3, 20, 106, 1, 0x40FFFFFF, 0xFFAA00FF, 0);
            GuiHelperOld.drawBorderedRect(builder, xSize - 32, 3, 20, 106, 1, 0x40FFFFFF, 0xFFAA00FF, 0);
        }
        buffer.endBatch();
    }

    @Override
    public void setIngredients(IFusionRecipe recipe, IIngredients ingredients) {
        List<Ingredient> recipeIngredients = new ArrayList<>();
        recipeIngredients.add(recipe.getCatalyst());
        recipeIngredients.addAll(recipe.getIngredients());
        ingredients.setInputIngredients(recipeIngredients);
        ingredients.setOutput(VanillaTypes.ITEM, recipe.getResultItem());
    }

    @Override
    public void setRecipe(IRecipeLayout layout, IFusionRecipe recipe, IIngredients ingredients) {
        IGuiItemStackGroup stackGroup = layout.getItemStacks();

        stackGroup.init(0, true, xSize / 2 - 9, ySize / 2 - 9 - 23);
        stackGroup.init(1, false, xSize / 2 - 9, ySize / 2 - 9 + 23);

        List<Ingredient> ingreds = recipe.getIngredients();
        int nColumns = ingreds.size() > 16 ? 4 : 2;
        int xc = xSize / 2 - 9;
        int yc = ySize / 2 - 9;
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

            stackGroup.init(2 + i, true, xPos, yPos);
        }

        stackGroup.set(ingredients);
    }
}
