package com.brandon3055.draconicevolution.client.gui.modwiki.moddata.displaycontent;

import com.brandon3055.brandonscore.client.gui.modulargui.MGuiElementBase;
import com.brandon3055.brandonscore.client.gui.modulargui.modularelements.MGuiButtonSolid;
import com.brandon3055.brandonscore.client.gui.modulargui.modularelements.MGuiStackIcon;
import com.brandon3055.brandonscore.lib.StackReference;
import com.brandon3055.draconicevolution.client.gui.modwiki.GuiModWiki;
import com.brandon3055.draconicevolution.client.gui.modwiki.guielements.StackSelector;
import com.brandon3055.draconicevolution.client.gui.modwiki.moddata.guidoctree.TreeBranchRoot;
import com.brandon3055.draconicevolution.integration.jei.IRecipeRenderer;
import com.brandon3055.draconicevolution.integration.jei.JeiHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import org.w3c.dom.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by brandon3055 on 8/09/2016.
 */
public class DCRecipe extends DisplayComponentBase {

    private boolean canRenderRecipes = false;
    private String error = "";
    public String stackString;
    public ItemStack resultStack;
    private StackSelector selector;
    private List<IRecipeRenderer> recipeRenderers = new ArrayList<>();


    public DCRecipe(GuiModWiki modularGui, String componentType, TreeBranchRoot branch) {
        super(modularGui, componentType, branch);
        ySize = 50;
    }

    //region List

    @Override
    public void setXSize(int xSize) {
        super.setXSize(xSize);

        if (!canRenderRecipes) {
            ySize = 12;
        }
        else {
            ySize = 0;
            for (IRecipeRenderer renderer : recipeRenderers) {
                ySize += renderer.getHeight() + fontRenderer.FONT_HEIGHT + 5;
            }
            ySize -= 4;
        }

        if (!JeiHelper.jeiAvailable()) {
            canRenderRecipes = false;
            error = "The mod JEI [Just Enough Items] is required for recipe display.";
            return;
        }

        if (resultStack != null) {
            recipeRenderers = JeiHelper.getRecipeRenderers(resultStack);
            if (recipeRenderers == null) {
                canRenderRecipes = false;
                error = "Something went wrong while loading recipes...";
            }
            else if (recipeRenderers.size() == 0) {
                canRenderRecipes = false;
                error = "Recipe Not Found....";
            }
            else {
                canRenderRecipes = true;
            }
        }
        else {
            canRenderRecipes = false;
            error = "Could not find result item stack. This may be because the mod it belongs to is not installed or maby this page is just broken.";
        }
    }

    //endregion

    //region Render & Interact

    @Override
    public void renderBackgroundLayer(Minecraft minecraft, int mouseX, int mouseY, float partialTicks) {
        super.renderBackgroundLayer(minecraft, mouseX, mouseY, partialTicks);

        if (!canRenderRecipes) {
            drawSplitString(fontRenderer, error, xPos + 4, yPos + 4, xSize - 8, 0xFF0000, false);
            return;
        }

        GlStateManager.pushMatrix();
        GlStateManager.translate(0, 0, getRenderZLevel());

        int yOffset = 0;
        for (IRecipeRenderer renderer : recipeRenderers) {

            int x = xPos;

            switch (alignment) {
                case LEFT:
                    x = xPos + 4;
                    drawString(fontRenderer, renderer.getTitle(), x, yPos + yOffset, getColour());
                    break;
                case CENTER:
                    x = xPos + (xSize / 2) - ((renderer.getWidth() + 2) / 2);
                    drawCenteredString(fontRenderer, renderer.getTitle(), xPos + (xSize / 2), yPos + yOffset, getColour(), false);
                    break;
                case RIGHT:
                    x = xPos + xSize - renderer.getWidth() - 6;
                    drawString(fontRenderer, renderer.getTitle(), xPos + xSize - fontRenderer.getStringWidth(renderer.getTitle()) - 4, yPos + yOffset, getColour());
                    break;
            }

            drawBorderedRect(x, yPos + yOffset + fontRenderer.FONT_HEIGHT, renderer.getWidth() + 2, renderer.getHeight() + 2, 1, 0, 0xFF000000);
            renderer.render(minecraft, x + 1, yPos + yOffset + fontRenderer.FONT_HEIGHT + 1, mouseX, mouseY);
            yOffset += renderer.getHeight() + fontRenderer.FONT_HEIGHT + 4;
        }

        GlStateManager.popMatrix();
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (!canRenderRecipes) {
            drawSplitString(fontRenderer, error, xPos + 4, yPos + 4, xSize - 8, 0xFF0000, false);
            return super.mouseClicked(mouseX, mouseY, mouseButton);
        }

        for (IRecipeRenderer renderer : recipeRenderers) {
            if (renderer.handleClick(mc, mouseX, mouseY, mouseButton)) {
                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    //endregion

    //region Edit

    @Override
    public LinkedList<MGuiElementBase> getEditControls() {
        LinkedList<MGuiElementBase> list = super.getEditControls();

        list.add(new MGuiButtonSolid(modularGui, "TOGGLE_ALIGN", 0, 0, 26, 12, "Align"){
            @Override
            protected int getBorderColour(boolean hovering, boolean disabled) {
                return hovering ? 0xFF00FF00 : 0xFFFF0000;
            }
        }.setListener(this).setToolTip(new String[]{"Toggle Horizontal Alignment"}));

        list.add(new MGuiButtonSolid(modularGui, "SELECT_STACK", 0, 0, 56, 12, "Pick Stack"){
            @Override
            protected int getBorderColour(boolean hovering, boolean disabled) {
                return hovering ? 0xFF00FF00 : 0xFFFF0000;
            }
        }.setListener(this).setToolTip(new String[] {"Select a stack from your inventory"}));

        return list;
    }

    @Override
    public void onMGuiEvent(String eventString, MGuiElementBase eventElement) {
        super.onMGuiEvent(eventString, eventElement);

        if (eventElement instanceof MGuiButtonSolid && ((MGuiButtonSolid) eventElement).buttonName.equals("SELECT_STACK")) {
            EntityPlayer player = Minecraft.getMinecraft().thePlayer;
            selector = new StackSelector(modularGui, list.xPos + list.leftPadding, list.yPos + list.topPadding, list.xSize - list.leftPadding - list.rightPadding, list.ySize - list.topPadding - list.bottomPadding);
            selector.setListener(this);

            List<ItemStack> stacks = new LinkedList<ItemStack>();
            for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
                ItemStack stack = player.inventory.getStackInSlot(i);
                if (stack != null) {
                    stacks.add(stack);
                }
            }
            selector.setStacks(stacks);
            selector.addChild(new MGuiButtonSolid(modularGui, "CANCEL_PICK", selector.xPos + selector.xSize - 42, selector.yPos + selector.ySize - 22, 40, 20, "Cancel").setListener(this).setId("CANCEL_PICK"));
            selector.initElement();
            modularGui.getManager().add(selector, 2);
        }
        else if (eventElement.id.equals("CANCEL_PICK") && selector != null) {
            modularGui.getManager().remove(selector);
        }
        else if (eventString.equals("SELECTOR_PICK")) {
            boolean shouldSave = false;
            if (eventElement instanceof MGuiStackIcon) {
                StackReference reference = new StackReference(((MGuiStackIcon)eventElement).getStack());
//                stackIcon.setStack(reference);
                element.setTextContent(reference.toString());
                shouldSave = true;
            }

            modularGui.getManager().remove(selector);
            if (shouldSave) {
                save();
            }
        }

    }

    @Override
    public void onCreated() {   }

    //endregion

    //region XML & Factory

    @Override
    public void loadFromXML(Element element) {
        super.loadFromXML(element);
        stackString = element.getTextContent();
        StackReference ref = StackReference.fromString(stackString);
        if (ref != null) {
            resultStack = ref.createStack();
        }

        if (!JeiHelper.jeiAvailable()) {
            canRenderRecipes = false;
            error = "The mod JEI [Just Enough Items] is required for recipe display.";
            return;
        }

        if (resultStack != null) {
            recipeRenderers = JeiHelper.getRecipeRenderers(resultStack);
            if (recipeRenderers == null) {
                canRenderRecipes = false;
                error = "Something went wrong while loading recipes...";
            }
            else if (recipeRenderers.size() == 0) {
                canRenderRecipes = false;
                error = "Recipe Not Found....";
            }
            else {
                canRenderRecipes = true;
            }
        }
        else {
            canRenderRecipes = false;
            error = "Could not find result item stack. This may be because the mod it belongs to is not installed or maby this page is just broken.";
        }
    }

    public static class Factory implements IDisplayComponentFactory {
        @Override
        public DisplayComponentBase createNewInstance(GuiModWiki guiWiki, TreeBranchRoot branch) {
            DisplayComponentBase component = new DCRecipe(guiWiki, getID(), branch);
            component.setWorldAndResolution(guiWiki.mc, guiWiki.screenWidth(), guiWiki.screenHeight());
            return component;
        }

        @Override
        public String getID() {
            return "recipe";
        }
    }

    //endregion
}
