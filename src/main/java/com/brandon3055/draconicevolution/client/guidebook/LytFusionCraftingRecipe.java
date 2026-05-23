package com.brandon3055.draconicevolution.client.guidebook;

import com.brandon3055.brandonscore.utils.Utils;
import com.brandon3055.draconicevolution.api.crafting.FusionRecipe;
import guideme.color.SymbolicColor;
import guideme.document.LytRect;
import guideme.document.LytSize;
import guideme.document.block.*;
import guideme.document.flow.LytFlowText;
import guideme.layout.LayoutContext;
import guideme.render.GuiAssets;
import guideme.style.BorderStyle;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;
import java.util.Locale;

public class LytFusionCraftingRecipe extends LytBox {
    private final int inputLength;
    private final LytParagraph craftTierParagraph = new LytParagraph();
    private final LytParagraph craftPowerParagraph = new LytParagraph();
    private final LytVBox textBox;
    private final LytHBox inputBox;
    private final LytHBox outputBox;

    public LytFusionCraftingRecipe(FusionRecipe recipe) {
        inputLength = recipe.getIngredients().size();
        List<Ingredient> inputLeft = recipe.getIngredients().subList(0, (int) Math.ceil(inputLength/2.0));
        List<Ingredient> inputRight = recipe.getIngredients().subList((int) Math.ceil(inputLength/2.0), inputLength);

        append(inputBox = new LytHBox());
        inputBox.setAlignItems(AlignItems.CENTER);
        LytSlotGrid column1;
        inputBox.append(column1 = LytSlotGrid.column(inputLeft, true));
        LytSlotGrid column2;
        inputBox.append(column2 = LytSlotGrid.column(inputRight, true));

        column1.getChildren().forEach(lytNode ->
                ((LytSlot)lytNode).setSlotVisible(false)
        );
        column2.getChildren().forEach(lytNode ->
                ((LytSlot)lytNode).setSlotVisible(false)
        );
        append(outputBox = new LytHBox());
        outputBox.setAlignItems(AlignItems.CENTER);
        LytSlot inputSlot;
        outputBox.append(inputSlot = new LytSlot(recipe.getCatalyst()));
        inputSlot.setSlotVisible(false);
        outputBox.append(new LytGuiSprite(GuiAssets.ARROW, new LytSize(24, 17)));
        LytSlot resultSlot;
        outputBox.append(resultSlot = new LytSlot(recipe.getResultItem()));
        resultSlot.setSlotVisible(false);
        inputSlot.setLargeSlot(true);
        resultSlot.setLargeSlot(true);

        append(textBox = new LytVBox());
        textBox.setAlignItems(AlignItems.END);
        LytFlowText craftTier = new LytFlowText();
        craftTier.setText(
                Component.translatable("gui.draconicevolution.fusion_craft.tier." + recipe.getRecipeTier().name().toLowerCase(Locale.ENGLISH)).getString()
        );
        craftTier.modifyStyle(builder -> {
            builder.color(lightDarkMode ->
                    switch (recipe.getRecipeTier()){
                        case DRACONIUM -> 5263615;
                        case WYVERN -> 8388863;
                        case DRACONIC -> 16737792;
                        case CHAOTIC -> 5263440;
                    }
                )
                .bold(true)
            ;
        });
        craftTierParagraph.append(craftTier);

        LytFlowText energyCost = new LytFlowText();
        energyCost.setText(Utils.addCommas(recipe.getEnergyCost()) + " OP");
        energyCost.modifyStyle(builder -> builder.bold(true).color(lightDarkMode -> 4500223));
        craftPowerParagraph.append(energyCost);

        textBox.append(craftTierParagraph);
        textBox.append(craftPowerParagraph);

        setBackgroundColor(SymbolicColor.BLACK);
        BorderStyle inputBorder = new BorderStyle(SymbolicColor.LIGHT_PURPLE, 1);
        BorderStyle outputBorder = new BorderStyle(SymbolicColor.AQUA, 1);

        inputBox.setBackgroundColor(SymbolicColor.DARK_GRAY);
        inputBox.setBorder(inputBorder);

        outputBox.setBackgroundColor(SymbolicColor.DARK_GRAY);
        outputBox.setBorder(outputBorder);
    }

    @Override
    protected LytRect computeBoxLayout(LayoutContext context, int x, int y, int availableWidth) {
        int height = Math.max(
                ((inputLength * LytSlot.OUTER_SIZE) / 2) + 16
                , 64
        );
        inputBox.layout(context, x + 8
                ,y + (height / 2) -((inputBox.getChildren().getFirst().getChildren().size() * LytSlot.OUTER_SIZE) / 2)
                , availableWidth);

        outputBox.layout(context, x + 58, y + (height/2) - (LytSlot.OUTER_SIZE_LARGE / 2), availableWidth);

        textBox.layout(context, x + 160 - Math.max(
                craftTierParagraph.computeLayout(context, x, y, availableWidth).width(),
                craftPowerParagraph.computeLayout(context, x, y, availableWidth).width()
        ), y + 4, availableWidth);



        return new LytRect(x, y, 160, height);
    }

}