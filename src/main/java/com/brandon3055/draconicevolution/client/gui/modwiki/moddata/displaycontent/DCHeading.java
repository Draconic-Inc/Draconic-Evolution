package com.brandon3055.draconicevolution.client.gui.modwiki.moddata.displaycontent;

import com.brandon3055.brandonscore.client.gui.modulargui.MGuiElementBase;
import com.brandon3055.brandonscore.client.gui.modulargui.lib.EnumAlignment;
import com.brandon3055.brandonscore.client.gui.modulargui.modularelements.*;
import com.brandon3055.brandonscore.utils.Utils;
import com.brandon3055.draconicevolution.client.gui.modwiki.GuiModWiki;
import com.brandon3055.draconicevolution.client.gui.modwiki.moddata.guidoctree.TreeBranchRoot;
import com.google.common.base.Predicate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.StringUtils;
import net.minecraft.util.text.TextFormatting;
import org.w3c.dom.Element;

import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by brandon3055 on 8/09/2016.
 */
public class DCHeading extends DisplayComponentBase {

    public int headingSize = 0;
    public String displayString = "";

    public static final String SIZE_ATTRIB = "size";

    public DCHeading(GuiModWiki modularGui, String componentType, TreeBranchRoot branch) {
        super(modularGui, componentType, branch);
        ySize = 12;
    }

    //region List

    @Override
    public int getEntryHeight() {
        return super.getEntryHeight();
    }

    @Override
    public void setXSize(int xSize) {
        super.setXSize(xSize);

        if (xSize < 10) {
            return;
        }

        float scaleFactor = 1F + (headingSize / 2F);
        int split = fontRenderer.listFormattedStringToWidth(displayString, (int)(xSize / scaleFactor)).size();
        ySize = (int) (fontRenderer.FONT_HEIGHT * scaleFactor * split);
    }

    //endregion

    //region Render

    @Override
    public void renderBackgroundLayer(Minecraft minecraft, int mouseX, int mouseY, float partialTicks) {
        float scaleFactor = 1F + (headingSize / 2F);
        List<String> list = fontRenderer.listFormattedStringToWidth(displayString, (int)(xSize / scaleFactor));

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

            drawString(fontRenderer, string, x, y, getColour(), shadow);

            if (headingSize > 0) {
                GlStateManager.popMatrix();
            }

        }
        super.renderBackgroundLayer(minecraft, mouseX, mouseY, partialTicks);

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
        }.setListener(this).setToolTip(new String[]{"Toggle Text Alignment"}));
        MGuiTextField textField = new MGuiTextField(modularGui, 0, 0, 100, 12, fontRenderer).setListener(this).setMaxStringLength(2048).setText(displayString);
        textField.setId("TEXT");
        textField.addChild(new MGuiHoverPopup(modularGui, new String[] {"Modify Heading Text", TextFormatting.GREEN + "Will Auto-Save 3 seconds after you stop typing."}, textField));
        list.add(textField);

        list.add(new MGuiLabel(modularGui, 0, 0, 37, 12, "Colour:").setAlignment(EnumAlignment.CENTER));
        MGuiTextField colourField = new MGuiTextField(modularGui, 0, 0, 45, 12, fontRenderer).setListener(this).setMaxStringLength(6).setText("FFFFFF");
        colourField.addChild(new MGuiHoverPopup(modularGui, new String[] {"Set the base colour. If left default this will be the text colour for the selected style", "If you change this the only way to to go back is to remove the colour attribute from the entry in the XML file."}, colourField));
        colourField.setId("COLOUR");
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

        list.add(new MGuiButtonSolid(modularGui, "CYCLE_SIZE", 0, 0, 20, 12, "H:"){
            @Override
            protected int getBorderColour(boolean hovering, boolean disabled) {
                return hovering ? 0xFF00FF00 : 0xFFFF0000;
            }
        }.setListener(this).setToolTip(new String[]{"Cycle Trough Heading Sizes", "Hold Shift to reverse"}).setDisplayString("H:" + headingSize));

        list.add(new MGuiButtonSolid(modularGui, "SHADOW", 0, 0, 10, 12, "S"){
            @Override
            protected int getBorderColour(boolean hovering, boolean disabled) {
                return hovering ? 0xFF00FF00 : 0xFFFF0000;
            }
        }.setListener(this).setToolTip(new String[]{"Toggle Shadow"}));

        list.add(new MGuiButtonSolid(modularGui, "OBFUSCATED", 0, 0, 10, 12, TextFormatting.OBFUSCATED + "O"){
            @Override
            protected int getBorderColour(boolean hovering, boolean disabled) {
                return hovering ? 0xFF00FF00 : 0xFFFF0000;
            }
        }.setListener(this).setToolTip(new String[]{"Toggle Obfuscated"}).addToGroup("STYLE"));
        list.add(new MGuiButtonSolid(modularGui, "BOLD", 0, 0, 10, 12, TextFormatting.BOLD + "B"){
            @Override
            protected int getBorderColour(boolean hovering, boolean disabled) {
                return hovering ? 0xFF00FF00 : 0xFFFF0000;
            }
        }.setListener(this).setToolTip(new String[]{"Toggle Bold"}).addToGroup("STYLE"));
        list.add(new MGuiButtonSolid(modularGui, "STRIKETHROUGH", 0, 0, 10, 12, TextFormatting.STRIKETHROUGH + "S"){
            @Override
            protected int getBorderColour(boolean hovering, boolean disabled) {
                return hovering ? 0xFF00FF00 : 0xFFFF0000;
            }
        }.setListener(this).setToolTip(new String[]{"Toggle Strike-through"}).addToGroup("STYLE"));
        list.add(new MGuiButtonSolid(modularGui, "UNDERLINE", 0, 0, 10, 12, TextFormatting.UNDERLINE + "U"){
            @Override
            protected int getBorderColour(boolean hovering, boolean disabled) {
                return hovering ? 0xFF00FF00 : 0xFFFF0000;
            }
        }.setListener(this).setToolTip(new String[]{"Toggle Underline"}).addToGroup("STYLE"));
        list.add(new MGuiButtonSolid(modularGui, "ITALIC", 0, 0, 10, 12, TextFormatting.ITALIC + "I"){
            @Override
            protected int getBorderColour(boolean hovering, boolean disabled) {
                return hovering ? 0xFF00FF00 : 0xFFFF0000;
            }
        }.setListener(this).setToolTip(new String[]{"Toggle Italic"}).addToGroup("STYLE"));

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
//                if (element instanceof MGuiTextField) {
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

            element.setAttribute(SIZE_ATTRIB, String.valueOf(headingSize));
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
        else if (eventElement instanceof MGuiButton && eventElement.isInGroup("STYLE")) {
            TextFormatting format = TextFormatting.valueOf(((MGuiButton)eventElement).buttonName);

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
    }

    @Override
    public void onCreated() {
        element.setAttribute(SIZE_ATTRIB, "0");
        element.setTextContent("Click To Edit");
//        element.setAttribute(ATTRIB_COLOUR, "FFFFFF");
        element.setAttribute(ATTRIB_SHADOW, "true");
        element.setAttribute(ATTRIB_ALIGNMENT, "CENTER");
    }

    //endregion

    //region XML & Factory

    @Override
    public void loadFromXML(Element element) {
        super.loadFromXML(element);
        displayString = element.getTextContent();
        if (element.hasAttribute(SIZE_ATTRIB)) {
            headingSize = Integer.parseInt(element.getAttribute(SIZE_ATTRIB));
        }
    }

    public static class Factory implements IDisplayComponentFactory {
        @Override
        public DisplayComponentBase createNewInstance(GuiModWiki guiWiki, TreeBranchRoot branch) {
            DisplayComponentBase component = new DCHeading(guiWiki, getID(), branch);
            component.setWorldAndResolution(guiWiki.mc, guiWiki.screenWidth(), guiWiki.screenHeight());
            return component;
        }

        @Override
        public String getID() {
            return "heading";
        }
    }

    //endregion
}
