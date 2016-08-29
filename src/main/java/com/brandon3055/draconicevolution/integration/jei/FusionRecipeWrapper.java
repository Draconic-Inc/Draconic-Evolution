package com.brandon3055.draconicevolution.integration.jei;

import com.brandon3055.brandonscore.client.gui.effects.GuiEffectRenderer;
import com.brandon3055.brandonscore.client.utils.GuiHelper;
import com.brandon3055.brandonscore.utils.LinkedHashList;
import com.brandon3055.brandonscore.utils.Utils;
import com.brandon3055.draconicevolution.api.fusioncrafting.IFusionRecipe;
import com.brandon3055.draconicevolution.client.gui.GuiFusionCraftingCore;
import com.brandon3055.draconicevolution.client.handler.ClientEventHandler;
import com.brandon3055.draconicevolution.utils.ITickableTimeout;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;

/**
 * Created by brandon3055 on 24/07/2016.
 */
public class FusionRecipeWrapper extends BlankRecipeWrapper implements ITickableTimeout {

    private GuiEffectRenderer effectRenderer = new GuiEffectRenderer();
    public final IFusionRecipe recipe;
    private final List inputs = new LinkedHashList();
    private int timeout = 0;
    private int xSize = 164;
    private int ySize = 111;

    @SuppressWarnings("unchecked")
    public FusionRecipeWrapper(IFusionRecipe recipe) {
        this.recipe = recipe;

        inputs.addAll(recipe.getRecipeIngredients());
        inputs.add(recipe.getRecipeCatalyst());

        ClientEventHandler.tickableList.add(this);

    }

    @Nonnull
    @Override
    public List getInputs() {
        return inputs;
    }

    @Nonnull
    @Override
    public List getOutputs() {
        return Arrays.asList(recipe.getRecipeOutput(null));
    }

    @Override
    public void drawInfo(@Nonnull Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
        timeout = 0;

        if (minecraft.fontRendererObj != null) {
            GuiHelper.drawCenteredString(minecraft.fontRendererObj, I18n.format("gui.jeiFusion.tier." + recipe.getRecipeTier()), xSize / 2, 5, 0xFF6600, false);
            GuiHelper.drawCenteredString(minecraft.fontRendererObj, I18n.format("generic.de.energyCost.txt"), xSize / 2, ySize - 20, 0x4444FF, false);
            GuiHelper.drawCenteredString(minecraft.fontRendererObj, Utils.addCommas(recipe.getEnergyCost() * recipe.getRecipeIngredients().size()) + "RF", xSize / 2, ySize - 10, 0x44AAFF, false);
        }
    }

    @Override
    public void drawAnimations(@Nonnull Minecraft minecraft, int recipeWidth, int recipeHeight) {
        effectRenderer.renderEffects(minecraft.getRenderPartialTicks());
    }

    @Override
    public void tick() {
        timeout++;

        effectRenderer.updateEffects();
        World world = Minecraft.getMinecraft().theWorld;

        if (world != null) {
            List ingredients = recipe.getRecipeIngredients();

            int centerX = xSize / 2;
            int centerY = ySize / 2;

            for (int i = 0; i < ingredients.size(); i++) {
                boolean isLeft = i % 2 == 0;
                boolean isOdd = ingredients.size() % 2 == 1;
                int sideCount = ingredients.size() / 2;

                if (isOdd && !isLeft) {
                    sideCount--;
                }

                int xPos;
                int yPos;


                if (isLeft) {
                    xPos = centerX - 65;
                    int ySize = 80 / Math.max(sideCount - (isOdd ? 0 : 1), 1);

                    int sideIndex = i / 2;

                    if (sideCount <= 1 && (!isOdd || ingredients.size() == 1)) {
                        sideIndex = 1;
                        ySize = 40;
                    }

                    yPos = centerY - 40 + (sideIndex * ySize);
                } else {
                    xPos = centerX + 65;

                    int ySize = 80 / Math.max(sideCount - (isOdd ? 0 : 1), 1);

                    int sideIndex = i / 2;

                    if (isOdd) {
                        sideCount++;
                    }

                    if (sideCount <= 1) {
                        sideIndex = 1;
                        ySize = 40;
                    }

                    yPos = centerY - 40 + (sideIndex * ySize);
                }

                if (world.rand.nextInt(10) == 0) {
                    xPos += -8 + (world.rand.nextDouble() * 16);
                    yPos += -8 + (world.rand.nextDouble() * 16);
                    double ty = centerY + (-20 + (world.rand.nextDouble() * 40));
                    effectRenderer.addEffect(new GuiFusionCraftingCore.EnergyEffect(Minecraft.getMinecraft().theWorld, xPos, yPos, centerX, ty, 0));
                }
            }

            double xPos = centerX - 8 + (world.rand.nextDouble() * 16);
            double yTop = 35 - 8 + (world.rand.nextDouble() * 16);//35
            effectRenderer.addEffect(new GuiFusionCraftingCore.EnergyEffect(Minecraft.getMinecraft().theWorld, xPos, yTop, centerX, 78, 1));//78

            effectRenderer.updateEffects();
        } else {
            effectRenderer.clearEffects();
        }
    }

    @Override
    public int getTimeOut() {
        return timeout;
    }
}
