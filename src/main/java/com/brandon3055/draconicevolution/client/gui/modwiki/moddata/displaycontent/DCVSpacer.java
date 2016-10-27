package com.brandon3055.draconicevolution.client.gui.modwiki.moddata.displaycontent;

import com.brandon3055.brandonscore.client.gui.modulargui.MGuiElementBase;
import com.brandon3055.brandonscore.client.gui.modulargui.modularelements.MGuiButton;
import com.brandon3055.brandonscore.client.gui.modulargui.modularelements.MGuiButtonSolid;
import com.brandon3055.draconicevolution.client.gui.modwiki.GuiModWiki;
import com.brandon3055.draconicevolution.client.gui.modwiki.WikiConfig;
import com.brandon3055.draconicevolution.client.gui.modwiki.moddata.guidoctree.TreeBranchRoot;
import com.brandon3055.draconicevolution.utils.LogHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import org.w3c.dom.Element;

import java.util.LinkedList;

/**
 * Created by brandon3055 on 8/09/2016.
 */
public class DCVSpacer extends DisplayComponentBase {

    public DCVSpacer(GuiModWiki modularGui, String componentType, TreeBranchRoot branch) {
        super(modularGui, componentType, branch);
        ySize = 8;
    }

    //region Render

    @Override
    public void renderBackgroundLayer(Minecraft minecraft, int mouseX, int mouseY, float partialTicks) {
        super.renderBackgroundLayer(minecraft, mouseX, mouseY, partialTicks);

        if (WikiConfig.editMode && isMouseOver(mouseX, mouseY)) {
            String text = String.format("[Separator: %s Line%s]", ySize / 8D, ySize == 8 ? "" : "s");
            drawCenteredString(fontRenderer, text, xPos + (xSize / 2), yPos + (ySize / 2) - 4, 0x00FF00, true);
        }
    }


    //endregion

    //region Edit

    @Override
    public LinkedList<MGuiElementBase> getEditControls() {
        LinkedList<MGuiElementBase> list = super.getEditControls();
        list.add(new MGuiButtonSolid(modularGui, "SIZE_+", 0, 0, 20, 12, "+"){
            @Override
            public int getBorderColour(boolean hovering, boolean disabled) {
                return hovering ? 0xFF00FF00 : 0xFFFF0000;
            }
        }.setListener(this).setToolTip(new String[]{"Increase Spacer Size (Hold shift for fine adjustment)"}));
        list.add(new MGuiButtonSolid(modularGui, "SIZE_-", 0, 0, 20, 12, "-"){
            @Override
            public int getBorderColour(boolean hovering, boolean disabled) {
                return hovering ? 0xFF00FF00 : 0xFFFF0000;
            }
        }.setListener(this).setToolTip(new String[]{"Decrease Spacer Size (Hold shift for fine adjustment)"}));
        return list;
    }

    @Override
    public void onMGuiEvent(String eventString, MGuiElementBase eventElement) {
        super.onMGuiEvent(eventString, eventElement);
        int modifier = GuiScreen.isShiftKeyDown() ? 1 : 8;

        if (eventElement instanceof MGuiButton && ((MGuiButton) eventElement).buttonName.equals("SIZE_+")) {
            ySize += modifier;
            element.setAttribute("size", String.valueOf(ySize));
            save();
        }
        else if (eventElement instanceof MGuiButton && ((MGuiButton) eventElement).buttonName.equals("SIZE_-")) {
            ySize -= modifier;
            if (ySize < 4) {
                ySize = 4;
            }
            element.setAttribute("size", String.valueOf(ySize));
            save();
        }
    }

    @Override
    public void onCreated() {
        element.setAttribute("size", "8");
    }

    //endregion

    //region XML & Factory

    @Override
    public void loadFromXML(Element element) {
        super.loadFromXML(element);
        if (!element.hasAttribute("size")) {
            LogHelper.error("No size found for space in " + branch.branchID);
            return;
        }
        ySize = Integer.parseInt(element.getAttribute("size"));
    }

    public static class Factory implements IDisplayComponentFactory {
        @Override
        public DisplayComponentBase createNewInstance(GuiModWiki guiWiki, TreeBranchRoot branch) {
            DisplayComponentBase component = new DCVSpacer(guiWiki, getID(), branch);
            component.setWorldAndResolution(guiWiki.mc, guiWiki.screenWidth(), guiWiki.screenHeight());
            return component;
        }

        @Override
        public String getID() {
            return "vSpacer";
        }
    }

    //endregion
}
