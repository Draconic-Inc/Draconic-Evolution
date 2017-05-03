package com.brandon3055.draconicevolution.client.gui.modwiki;

import com.brandon3055.brandonscore.client.gui.modulargui.MGuiElementBase;
import com.brandon3055.brandonscore.client.gui.modulargui.lib.IMGuiListener;
import com.brandon3055.brandonscore.client.gui.modulargui.modularelements.MGuiButton;
import com.brandon3055.brandonscore.client.gui.modulargui.modularelements.MGuiButtonSolid;
import com.brandon3055.draconicevolution.client.gui.modwiki.guielements.WikiConfigWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;

import static com.brandon3055.draconicevolution.client.gui.modwiki.WikiConfig.MENU_BAR;
import static com.brandon3055.draconicevolution.client.gui.modwiki.WikiConfig.TEXT_COLOUR;

/**
 * Created by brandon3055 on 31/08/2016.
 */
public class WikiMenu extends MGuiElementBase implements IMGuiListener {
    private GuiModWiki guiModWiki;
    private WikiConfigWindow wikiConfigWindow = null;

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


        if (WikiConfig.editMode) {
            addChild(new MGuiButtonSolid(modularGui, "Reload", xPos + (xSize / 2), yPos + 3, 50, 12, "Reload").setColours(0xFF000000, 0xFF333333, 0xFF555555));
            addChild(new MGuiButtonSolid(modularGui, "TOGGLE_EDIT_LINES", xPos + (xSize / 2) + 51, yPos + 3, 12, 12, "E").setColours(MENU_BAR, 0xFFFF0000, 0xFFFF0000).setListener(this).setToolTip(new String[] {"Toggle Edit. Edit lines and info."}));
        }

        String s = I18n.format("generic.options.txt");
        int size = fontRenderer.getStringWidth(s);
        addChild(new MGuiButtonSolid(modularGui, "OPTIONS", xPos + xSize - (size + 7), yPos + 4, size + 4, 12, s){
            @Override
            public int getFillColour(boolean hovering, boolean disabled) {
                return hovering ? mixColours(MENU_BAR, 0x00151515) : MENU_BAR;
            }

            @Override
            public int getBorderColour(boolean hovering, boolean disabled) {
                return hovering ? mixColours(MENU_BAR, 0x00101010, true) : mixColours(MENU_BAR, 0x00202020, true);
            }

            @Override
            public int getTextColour(boolean hovered, boolean disabled) {
                return TEXT_COLOUR;
            }
        }.setListener(this).setToolTip(new String[] {"Open Options Window"}));//.setColours(0xFF888888, 0xFF000000, 0xFF222222)


    }

    //region Render

    @Override
    public void renderBackgroundLayer(Minecraft minecraft, int mouseX, int mouseY, float partialTicks) {
        int base = MENU_BAR;

        drawColouredRect(xPos, yPos, xSize, ySize, base);

        drawColouredRect(xPos, yPos + ySize - 1, xSize, 1, mixColours(base, 0x00151515, true));
        drawColouredRect(xPos + xSize - 1, yPos, 1, ySize, mixColours(base, 0x00151515, true));

        drawColouredRect(xPos, yPos, xSize, 1, mixColours(base, 0x00303030));
        drawColouredRect(xPos, yPos, 1, ySize, mixColours(base, 0x00303030));

        super.renderBackgroundLayer(minecraft, mouseX, mouseY, partialTicks);
    }

    @Override
    public void renderForegroundLayer(Minecraft minecraft, int mouseX, int mouseY, float partialTicks) {
        super.renderForegroundLayer(minecraft, mouseX, mouseY, partialTicks);
    }

//    @Override
//    public void renderOverlayLayer(Minecraft minecraft, int mouseX, int mouseY, float partialTicks) {
//        super.renderOverlayLayer(minecraft, mouseX, mouseY, partialTicks);
//    }

    //endregion

    //region Interact

    @Override
    public void onMGuiEvent(String eventString, MGuiElementBase eventElement) {
        if (eventElement instanceof MGuiButton && ((MGuiButton) eventElement).buttonName.equals("OPTIONS")) {
            if (wikiConfigWindow != null) {
                modularGui.getManager().remove(wikiConfigWindow);
                wikiConfigWindow = null;
            }
            else {
                wikiConfigWindow = new WikiConfigWindow(modularGui, (modularGui.screenWidth() / 2) - 128, (modularGui.screenHeight() / 2) - 100, 256, 200);
                modularGui.getManager().add(wikiConfigWindow, displayLevel + 1);
                wikiConfigWindow.initElement();
            }
        }
        else if (eventElement instanceof MGuiButton && ((MGuiButton) eventElement).buttonName.equals("TOGGLE_EDIT_LINES")) {
            WikiConfig.drawEditInfo = !WikiConfig.drawEditInfo;
        }
    }

    //endregion
}
