package com.brandon3055.draconicevolution.client.gui.modwiki;

import com.brandon3055.brandonscore.client.gui.modulargui.MGuiElementBase;
import com.brandon3055.brandonscore.client.gui.modulargui.modularelements.MGuiButtonSolid;
import net.minecraft.client.Minecraft;

/**
 * Created by brandon3055 on 31/08/2016.
 */
public class WikiMenu extends MGuiElementBase {
    private GuiModWiki guiModWiki;

    public WikiMenu(GuiModWiki parentGui) {
        super(parentGui);
        guiModWiki = parentGui;
    }

    @Override
    public void initElement() {
        toRemove.addAll(childElements);
        super.initElement();
        xSize = modularGui.screenWidth();
        ySize = 20;

        addChild(new MGuiButtonSolid(modularGui, "Reload", xPos + (xSize / 2), yPos + 3, 50, 12, "Reload").setColours(0xFF000000, 0xFF333333, 0xFF555555));
    }

    @Override
    public void renderBackgroundLayer(Minecraft minecraft, int mouseX, int mouseY, float partialTicks) {
        //drawBorderedRect(xPos, yPos, xSize, ySize, 1, WikiStyle.BASE_GUI_COLOUR, WikiStyle.BOURDER_COLOUR);

        super.renderBackgroundLayer(minecraft, mouseX, mouseY, partialTicks);
    }

    @Override
    public void renderForegroundLayer(Minecraft minecraft, int mouseX, int mouseY, float partialTicks) {
        super.renderForegroundLayer(minecraft, mouseX, mouseY, partialTicks);
    }

    @Override
    public void renderOverlayLayer(Minecraft minecraft, int mouseX, int mouseY, float partialTicks) {
        super.renderOverlayLayer(minecraft, mouseX, mouseY, partialTicks);
    }
}
