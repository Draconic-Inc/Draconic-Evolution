package com.brandon3055.draconicevolution.client.gui.modwiki.moddata.displaycontent;

import com.brandon3055.brandonscore.client.gui.modulargui.MGuiElementBase;
import com.brandon3055.brandonscore.client.gui.modulargui.lib.EnumAlignment;
import com.brandon3055.brandonscore.client.gui.modulargui.modularelements.*;
import com.brandon3055.brandonscore.utils.Utils;
import com.brandon3055.draconicevolution.client.gui.modwiki.GuiModWiki;
import com.brandon3055.draconicevolution.client.gui.modwiki.moddata.guidoctree.TreeBranchRoot;
import com.brandon3055.draconicevolution.utils.LogHelper;
import com.google.common.base.Predicate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiConfirmOpenLink;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.StringUtils;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import org.w3c.dom.Element;

import javax.annotation.Nullable;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by brandon3055 on 8/09/2016.
 */
public class DCLink extends DisplayComponentBase {

    public static final String TARGET_BRANCH = "branch";
    public static final String TARGET_WEB = "web";

    public static final String ATTRIB_LINK_TARGET = "targetLocation";
    public static final String ATTRIB_LINK = "link";
    public static final String ATTRIB_SIZE = "size";
    public static final String ATTRIB_HC = "hoverColour";

    public int headingSize = 0;
    public String displayString = "";
    public String linkTarget = "";
    public String link = "";
    public int hoverColour = 0x0000FF;
    private int xMin = 0;
    private int xMax = 1000;

    public DCLink(GuiModWiki modularGui, String componentType, TreeBranchRoot branch) {
        super(modularGui, componentType, branch);
        ySize = 12;
    }

    //region List

    @Override
    public void setXSize(int xSize) {
        super.setXSize(xSize);

        if (xSize < 10) {
            return;
        }

        float scaleFactor = 1F + (headingSize / 2F);
        List<String> list = fontRenderer.listFormattedStringToWidth(displayString, (int) (xSize / scaleFactor));
        int split = list.size();
        ySize = (int) (fontRenderer.FONT_HEIGHT * scaleFactor * split);

        xMin = xSize;
        xMax = 0;

        for (String string : list) {
            float x = 0;
            float scaledWidth = fontRenderer.getStringWidth(string) * scaleFactor;

            switch (alignment) {
                case LEFT:
                    x = (2 * scaleFactor);
                    break;
                case CENTER:
                    x = (xSize / 2F) - (scaledWidth / 2F);
                    break;
                case RIGHT:
                    x = (xSize - scaledWidth) - (2 * scaleFactor);
                    break;
            }

            if (x < xMin) {
                xMin = (int) x;
            }
            if (x + scaledWidth > xMax) {
                xMax = (int) (x + scaledWidth);
            }
        }
    }

    //endregion

    //region Render

    @Override
    public void renderBackgroundLayer(Minecraft minecraft, int mouseX, int mouseY, float partialTicks) {
        float scaleFactor = 1F + (headingSize / 2F);
        List<String> list = fontRenderer.listFormattedStringToWidth(displayString, (int) (xSize / scaleFactor));

        for (String string : list) {

            float x = 0;
            float y = yPos + (fontRenderer.FONT_HEIGHT * scaleFactor * list.indexOf(string));
            float scaledWidth = fontRenderer.getStringWidth(string) * scaleFactor;

            switch (alignment) {
                case LEFT:
                    x = xPos + (2 * scaleFactor);
                    break;
                case CENTER:
                    x = xPos + (xSize / 2F) - (scaledWidth / 2F);
                    break;
                case RIGHT:
                    x = xPos + (xSize - scaledWidth) - (2 * scaleFactor);
                    break;
            }

            if (headingSize > 0) {
                GlStateManager.pushMatrix();
                GlStateManager.translate(x, y, 0);
                GlStateManager.scale(scaleFactor, scaleFactor, 1);
                GlStateManager.translate(-x, -y, 0);
            }

            boolean mouseOver = isMouseOver(mouseX, mouseY) && mouseX >= xPos + xMin && mouseX <= xPos + xMax;

            drawString(fontRenderer, string, x, y, mouseOver ? hoverColour : getColour(), shadow);

            if (headingSize > 0) {
                GlStateManager.popMatrix();
            }

        }
        super.renderBackgroundLayer(minecraft, mouseX, mouseY, partialTicks);
    }

//    @Override
//    public void renderOverlayLayer(Minecraft minecraft, int mouseX, int mouseY, float partialTicks) {
//        super.renderOverlayLayer(minecraft, mouseX, mouseY, partialTicks);
//    }

    //endregion

    //region Interact

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        boolean mouseOver = isMouseOver(mouseX, mouseY) && mouseX >= xPos + xMin && mouseX <= xPos + xMax;

        if (mouseOver) {
            if (linkTarget.equals(TARGET_BRANCH)) {
                if (branch.guiWiki.wikiDataTree.idToBranchMap.containsKey(link)) {
                    branch.guiWiki.wikiDataTree.setActiveBranch(branch.guiWiki.wikiDataTree.idToBranchMap.get(link));
                }
                else {
                    displayString = TextFormatting.DARK_RED + "[ERROR: Broken Link]";
                    headingSize = 0;
                }
            }
            else if (linkTarget.equals(TARGET_WEB)) {
                try {
                    ReflectionHelper.setPrivateValue(GuiScreen.class, branch.guiWiki, new URI(link), "clickedLinkURI", "field_175286_t");
                    this.mc.displayGuiScreen(new GuiConfirmOpenLink(branch.guiWiki, link, 31102009, false));
                }
                catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    //endregion

    //region Edit

    @Override
    public LinkedList<MGuiElementBase> getEditControls() {
        LinkedList<MGuiElementBase> list = super.getEditControls();

        list.add(new MGuiButtonSolid(modularGui, "TOGGLE_ALIGN", 0, 0, 26, 12, "Align") {
            @Override
            public int getBorderColour(boolean hovering, boolean disabled) {
                return hovering ? 0xFF00FF00 : 0xFFFF0000;
            }
        }.setListener(this).setToolTip(new String[]{"Toggle Text Alignment"}));
        MGuiTextField textField = new MGuiTextField(modularGui, 0, 0, 100, 12, fontRenderer).setListener(this).setMaxStringLength(2048).setText(displayString);
        textField.setId("TEXT");
        textField.addChild(new MGuiHoverPopup(modularGui, new String[]{"Modify Link Text", TextFormatting.GREEN + "Will Auto-Save 3 seconds after you stop typing."}, textField));
        list.add(textField);

        list.add(new MGuiLabel(modularGui, 0, 0, 37, 12, "Colour:").setAlignment(EnumAlignment.CENTER));
        MGuiTextField colourField = new MGuiTextField(modularGui, 0, 0, 45, 12, fontRenderer).setListener(this).setMaxStringLength(6);
        colourField.setId("COLOUR");
        colourField.addChild(new MGuiHoverPopup(modularGui, new String[]{"Set the primary link colour.", TextFormatting.GOLD + "This is a HEX value meaning it accepts digits between 0 and F", TextFormatting.GOLD + "Format is Red, Green, Blue [RRGGBB]", TextFormatting.GREEN + "Will Auto-Save 3 seconds after you stop typing."}, colourField));
        colourField.setText(Integer.toHexString(getColour()));
        colourField.setValidator(new Predicate<String>() {
            @Override
            public boolean apply(@Nullable String input) {
                try {
                    Utils.parseHex(input, false);
                }
                catch (Exception e) {
                    return false;
                }
                return true;
            }
        });
        list.add(colourField);

        list.add(new MGuiLabel(modularGui, 0, 0, 70, 12, "Hover Colour:").setAlignment(EnumAlignment.CENTER));
        MGuiTextField hoverColourField = new MGuiTextField(modularGui, 0, 0, 45, 12, fontRenderer).setListener(this).setMaxStringLength(6);
        hoverColourField.setId("COLOUR_HOVER");
        hoverColourField.addChild(new MGuiHoverPopup(modularGui, new String[]{"Set the hover link colour.", TextFormatting.GOLD + "This is a HEX value meaning it accepts digits between 0 and F", TextFormatting.GOLD + "Format is Red, Green, Blue [RRGGBB]", TextFormatting.GREEN + "Will Auto-Save 3 seconds after you stop typing."}, hoverColourField));
        hoverColourField.setText(Integer.toHexString(hoverColour));
        hoverColourField.setValidator(new Predicate<String>() {
            @Override
            public boolean apply(@Nullable String input) {
                try {
                    Utils.parseHex(input, false);
                }
                catch (Exception e) {
                    return false;
                }
                return true;
            }
        });
        list.add(hoverColourField);

        list.add(new MGuiButtonSolid(modularGui, "CYCLE_SIZE", 0, 0, 20, 12, "S:") {
            @Override
            public int getBorderColour(boolean hovering, boolean disabled) {
                return hovering ? 0xFF00FF00 : 0xFFFF0000;
            }
        }.setListener(this).setToolTip(new String[]{"Cycle Trough Link Sizes", "Hold Shift to reverse"}).setDisplayString("S:" + headingSize));

        list.add(new MGuiButtonSolid(modularGui, "SHADOW", 0, 0, 10, 12, "S") {
            @Override
            public int getBorderColour(boolean hovering, boolean disabled) {
                return hovering ? 0xFF00FF00 : 0xFFFF0000;
            }
        }.setListener(this).setToolTip(new String[]{"Toggle Shadow"}));

        list.add(new MGuiButtonSolid(modularGui, "OBFUSCATED", 0, 0, 10, 12, TextFormatting.OBFUSCATED + "O") {
            @Override
            public int getBorderColour(boolean hovering, boolean disabled) {
                return hovering ? 0xFF00FF00 : 0xFFFF0000;
            }
        }.setListener(this).setToolTip(new String[]{"Toggle Obfuscated"}).addToGroup("STYLE"));
        list.add(new MGuiButtonSolid(modularGui, "BOLD", 0, 0, 10, 12, TextFormatting.BOLD + "B") {
            @Override
            public int getBorderColour(boolean hovering, boolean disabled) {
                return hovering ? 0xFF00FF00 : 0xFFFF0000;
            }
        }.setListener(this).setToolTip(new String[]{"Toggle Bold"}).addToGroup("STYLE"));
        list.add(new MGuiButtonSolid(modularGui, "STRIKETHROUGH", 0, 0, 10, 12, TextFormatting.STRIKETHROUGH + "S") {
            @Override
            public int getBorderColour(boolean hovering, boolean disabled) {
                return hovering ? 0xFF00FF00 : 0xFFFF0000;
            }
        }.setListener(this).setToolTip(new String[]{"Toggle Strike-through"}).addToGroup("STYLE"));
        list.add(new MGuiButtonSolid(modularGui, "UNDERLINE", 0, 0, 10, 12, TextFormatting.UNDERLINE + "U") {
            @Override
            public int getBorderColour(boolean hovering, boolean disabled) {
                return hovering ? 0xFF00FF00 : 0xFFFF0000;
            }
        }.setListener(this).setToolTip(new String[]{"Toggle Underline"}).addToGroup("STYLE"));
        list.add(new MGuiButtonSolid(modularGui, "ITALIC", 0, 0, 10, 12, TextFormatting.ITALIC + "I") {
            @Override
            public int getBorderColour(boolean hovering, boolean disabled) {
                return hovering ? 0xFF00FF00 : 0xFFFF0000;
            }
        }.setListener(this).setToolTip(new String[]{"Toggle Italic"}).addToGroup("STYLE"));

        String type = linkTarget.equals(TARGET_BRANCH) ? "Target Branch ID:" : linkTarget.equals(TARGET_WEB) ? "Target URL:" : "Target Type Not Set";
        list.add(new MGuiLabel(modularGui, 0, 0, fontRenderer.getStringWidth(type) + 4, 12, type).setAlignment(EnumAlignment.CENTER));
        MGuiTextField linkField = new MGuiTextField(modularGui, 0, 0, 200, 12, fontRenderer).setListener(this).setMaxStringLength(2048).setText(link);
        linkField.setId("LINK_FIELD");
        hoverColourField.addChild(new MGuiHoverPopup(modularGui, new String[]{"Set the link destination.", TextFormatting.GOLD + "Destination can be a web address if or a branch id depending on the target type.", TextFormatting.GOLD + "To get the id of a branch go to its edit menu and click the \"Copy ID\" button.", TextFormatting.GREEN + "Will Auto-Save 3 seconds after you stop typing."}, linkField));

        list.add(linkField);
        list.add(new MGuiButtonSolid(modularGui, "SWITCH_TARGET", 0, 0, 36, 12, "Target") {
            @Override
            public int getBorderColour(boolean hovering, boolean disabled) {
                return hovering ? 0xFF00FF00 : 0xFFFF0000;
            }
        }.setListener(this).setToolTip(new String[]{"Switch Target Type"}));


        return list;
    }

    @Override
    public void onMGuiEvent(String eventString, MGuiElementBase eventElement) {
        super.onMGuiEvent(eventString, eventElement);

        if (eventElement.id.equals("TEXT") && eventString.equals("TEXT_FIELD_CHANGED") && eventElement instanceof MGuiTextField) {
            if (StringUtils.isNullOrEmpty(((MGuiTextField) eventElement).getText())) {
                return;
            }

            element.setTextContent(((MGuiTextField) eventElement).getText());
            displayString = ((MGuiTextField) eventElement).getText();
            requiresSave = true;
            saveTimer = 60;
//            int pos = ((MGuiTextField) eventElement).getCursorPosition();
//            save();
//
//            for (MGuiElementBase element : branch.guiWiki.contentWindow.editControls) {
//                if (element instanceof MGuiTextField && element.id.equals("TEXT")) {
//                    ((MGuiTextField) element).setFocused(true);
//                    ((MGuiTextField) element).setCursorPosition(pos);
//                    break;
//                }
//            }
        }
        else if (eventString.equals("BUTTON_PRESS") && eventElement instanceof MGuiButtonSolid && ((MGuiButtonSolid) eventElement).buttonName.equals("CYCLE_SIZE")) {
            headingSize += GuiScreen.isShiftKeyDown() ? -1 : 1;

            if (headingSize > 10) {
                headingSize = 0;
            }
            else if (headingSize < 0) {
                headingSize = 0;
            }

            element.setAttribute(ATTRIB_SIZE, String.valueOf(headingSize));
            save();
        }
        else if (eventString.equals("BUTTON_PRESS") && eventElement instanceof MGuiButtonSolid && ((MGuiButtonSolid) eventElement).buttonName.equals("SHADOW")) {
            shadow = !shadow;

            element.setAttribute(ATTRIB_SHADOW, String.valueOf(shadow));
            save();
        }
        else if (eventElement.id.equals("COLOUR") && eventString.equals("TEXT_FIELD_CHANGED") && eventElement instanceof MGuiTextField) {
            try {
                setColour(Utils.parseHex(((MGuiTextField) eventElement).getText()));
                element.setAttribute(ATTRIB_COLOUR, Integer.toHexString(getColour()));

                requiresSave = true;
                saveTimer = 60;
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        else if (eventElement.id.equals("COLOUR_HOVER") && eventString.equals("TEXT_FIELD_CHANGED") && eventElement instanceof MGuiTextField) {
            try {
                hoverColour = Utils.parseHex(((MGuiTextField) eventElement).getText());
                element.setAttribute(ATTRIB_HC, Integer.toHexString(hoverColour));

                requiresSave = true;
                saveTimer = 60;
//                int pos = ((MGuiTextField) eventElement).getCursorPosition();
//                save();
//
//                for (MGuiElementBase element : branch.guiWiki.contentWindow.editControls) {
//                    if (element instanceof MGuiTextField && element.id.equals("COLOUR_HOVER")) {
//                        ((MGuiTextField) element).setFocused(true);
//                        ((MGuiTextField) element).setCursorPosition(pos);
//                        break;
//                    }
//                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        else if (eventElement instanceof MGuiButton && eventElement.isInGroup("STYLE")) {
            TextFormatting format = TextFormatting.valueOf(((MGuiButton) eventElement).buttonName);

            if (displayString.contains(format.toString())) {
                displayString = displayString.replace(format.toString(), "");
            }
            else {
                displayString = format + displayString;
            }

            for (MGuiElementBase element : branch.guiWiki.contentWindow.editControls) {
                if (element instanceof MGuiTextField) {
                    ((MGuiTextField) element).setText(displayString);
                    break;
                }
            }
            element.setTextContent(displayString);

            save();
        }
        else if (eventElement.id.equals("LINK_FIELD") && eventString.equals("TEXT_FIELD_CHANGED") && eventElement instanceof MGuiTextField) {
            try {
                linkTarget = ((MGuiTextField) eventElement).getText();
                element.setAttribute(ATTRIB_LINK, linkTarget);
                requiresSave = true;
                saveTimer = 60;

//                int pos = ((MGuiTextField) eventElement).getCursorPosition();
//                save();
//
//                for (MGuiElementBase element : branch.guiWiki.contentWindow.editControls) {
//                    if (element instanceof MGuiTextField && element.id.equals("LINK_FIELD")) {
//                        ((MGuiTextField) element).setFocused(true);
//                        ((MGuiTextField) element).setCursorPosition(pos);
//                        break;
//                    }
//                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        else if (eventString.equals("BUTTON_PRESS") && eventElement instanceof MGuiButtonSolid && ((MGuiButtonSolid) eventElement).buttonName.equals("SWITCH_TARGET")) {
            if (linkTarget.equals(TARGET_BRANCH)) {
                linkTarget = TARGET_WEB;
            }
            else {
                linkTarget = TARGET_BRANCH;
            }

            element.setAttribute(ATTRIB_LINK_TARGET, linkTarget);
            save();
        }
    }

    @Override
    public void onCreated() {
        element.setAttribute(ATTRIB_LINK_TARGET, TARGET_BRANCH);
        element.setAttribute(ATTRIB_LINK, branch.branchID);
        element.setAttribute(ATTRIB_SIZE, "0");
        element.setTextContent("Click to the left or right of this text to edit.");
//        element.setAttribute(ATTRIB_COLOUR, "FFFFFF");
        element.setAttribute(ATTRIB_HC, "0000FF");
        element.setAttribute(ATTRIB_SHADOW, "true");
        element.setAttribute(ATTRIB_ALIGNMENT, "CENTER");
    }

    //endregion

    //region XML & Factory

    @Override
    public void loadFromXML(Element element) {
        super.loadFromXML(element);
        linkTarget = element.getAttribute(ATTRIB_LINK_TARGET);
        link = element.getAttribute(ATTRIB_LINK);
        displayString = element.getTextContent();
        if (element.hasAttribute(ATTRIB_SIZE)) {
            headingSize = Integer.parseInt(element.getAttribute(ATTRIB_SIZE));
        }
        if (element.hasAttribute(ATTRIB_HC)) {
            try {
                hoverColour = Utils.parseHex(element.getAttribute(ATTRIB_HC), false);
            }
            catch (Exception e) {
                LogHelper.error("Error reading element colour: " + element + " In:" + branch.branchID);
                e.printStackTrace();
            }
        }
    }

    public static class Factory implements IDisplayComponentFactory {
        @Override
        public DisplayComponentBase createNewInstance(GuiModWiki guiWiki, TreeBranchRoot branch) {
            DisplayComponentBase component = new DCLink(guiWiki, getID(), branch);
            component.setWorldAndResolution(guiWiki.mc, guiWiki.screenWidth(), guiWiki.screenHeight());
            return component;
        }

        @Override
        public String getID() {
            return "link";
        }
    }

    //endregion
}
