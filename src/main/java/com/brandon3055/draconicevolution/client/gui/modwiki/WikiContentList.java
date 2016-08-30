package com.brandon3055.draconicevolution.client.gui.modwiki;

import com.brandon3055.brandonscore.client.gui.modulargui.GuiElementBase;
import com.brandon3055.brandonscore.client.utils.GuiHelper;
import net.minecraft.client.Minecraft;

/**
 * Created by brandon3055 on 31/08/2016.
 */
public class WikiContentList extends GuiElementBase {
    private GuiModWiki guiModWiki;

    public WikiContentList(GuiModWiki parentGui) {
        super(parentGui);
        guiModWiki = parentGui;
    }

    @Override
    public void initElement() {
        super.initElement();
        yPos = guiModWiki.wikiMenu.ySize;
        ySize = guiModWiki.screenHeight() - yPos;
        xSize = 50;
    }

    @Override
    public void renderBackgroundLayer(Minecraft minecraft, int mouseX, int mouseY, float partialTicks) {
        GuiHelper.drawBorderedRect(xPos, yPos, xSize, ySize, 1, 0xFF777777, 0xFF000000);
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
