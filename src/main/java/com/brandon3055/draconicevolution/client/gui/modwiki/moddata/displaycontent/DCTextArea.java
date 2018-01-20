package com.brandon3055.draconicevolution.client.gui.modwiki.moddata.displaycontent;

import com.brandon3055.brandonscore.client.gui.modulargui_old.MGuiElementBase;
import com.brandon3055.brandonscore.client.gui.modulargui_old.modularelements.MGuiButton;
import com.brandon3055.brandonscore.client.gui.modulargui_old.modularelements.MGuiButtonSolid;
import com.brandon3055.brandonscore.client.gui.modulargui_old.modularelements.MGuiTextField;
import com.brandon3055.draconicevolution.client.gui.modwiki.GuiModWiki;
import com.brandon3055.draconicevolution.client.gui.modwiki.moddata.guidoctree.TreeBranchRoot;
import com.brandon3055.draconicevolution.client.gui.modwiki.swing.SwingHelper;
import com.brandon3055.draconicevolution.client.gui.modwiki.swing.UIEditTextArea;
import net.minecraft.client.Minecraft;
import org.w3c.dom.Element;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import static com.brandon3055.brandonscore.client.gui.modulargui_old.lib.EnumAlignment.CENTER;
import static com.brandon3055.brandonscore.client.gui.modulargui_old.lib.EnumAlignment.LEFT;

/**
 * Created by brandon3055 on 8/09/2016.
 */
public class DCTextArea extends DisplayComponentBase {

    public String rawText = "";
    public UIEditTextArea editTextArea = null;
//    public String displayText = "";
//    public int cursoePos = 0;
//    public int selectStart = -1;
//    public int selectEnd = -1;
//    public boolean draging;
//    public int timer = 0;

    //include links

    public DCTextArea(GuiModWiki modularGui, String componentType, TreeBranchRoot branch) {
        super(modularGui, componentType, branch);
        ySize = 20;
    }

    //region Init

    @Override
    public void initElement() {

        super.initElement();
    }

    //endregion

    //region Render

    @Override
    public void renderBackgroundLayer(Minecraft minecraft, int mouseX, int mouseY, float partialTicks) {
        super.renderBackgroundLayer(minecraft, mouseX, mouseY, partialTicks);
        //region Render Button Back
        if (isBeingEdited) {
            //drawColouredRect(xPos, yPos + ySize, xSize, 12, 0xFF000000);
        }
        //endregion

        List<String> display = parseRawText();

//        int textPos = 0;
        int row = 0;
        for (String text : display) {
            int textWidth = fontRenderer.getStringWidth(text);
            int yTex = yPos + (row * fontRenderer.FONT_HEIGHT);
            int xTex = alignment == LEFT ? xPos + 4 : alignment == CENTER ? xPos + (xSize / 2) - (textWidth / 2) : xPos + xSize - textWidth - 4;

            drawString(fontRenderer, text, xTex, yTex, getColour());
//            int cursor = cursoePos - textPos;
//            if (isBeingEdited && timer / 10 % 2 == 0 && cursoePos != -1 && cursoePos >= textPos && cursoePos <= textPos + text.length()) {
//                boolean onNewLine = text.endsWith("\\n");
//                if (onNewLine && cursor == text.length()) {
//
//                }
//                else if (cursor >= 0 && cursor <= text.length()) {
//                    int pos = cursor == text.length() ? fontRenderer.getStringWidth(text) : fontRenderer.getStringWidth(text.substring(0, cursor));
//                    //drawColouredRect(xTex + pos, yTex, 1, fontRenderer.FONT_HEIGHT, 0xFFFFFFFF);
//                }
//            }

//            textPos += text.length();
            row++;
        }
    }

//    @Override
//    public void renderOverlayLayer(Minecraft minecraft, int mouseX, int mouseY, float partialTicks) {
//        super.renderOverlayLayer(minecraft, mouseX, mouseY, partialTicks);
//    }

    //endregion

    //region Edit Text Area

    //endregion

    //region Interact

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
//        int pos = calculateCursorIndex(mouseX, mouseY);
//        if (pos != -1) {
//            cursoePos = pos;
//        }
//        else {
//            cursoePos = -1;
//        }

        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        return super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
    }

    @Override
    public boolean mouseReleased(int mouseX, int mouseY, int state) {
        return super.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    protected boolean keyTyped(char typedChar, int keyCode) throws IOException {//todo Implement more key codes and text links
//        if (!isBeingEdited) {
//            return super.keyTyped(typedChar, keyCode);
//        }
//        else if (GuiScreen.isKeyComboCtrlA(keyCode)) {
////            this.setCursorPositionEnd();
////            this.setSelectionPos(0);
//            return true;
//        }
//        else if (GuiScreen.isKeyComboCtrlC(keyCode)) {
////            GuiScreen.setClipboardString(this.getSelectedText());
//            return true;
//        }
//        else if (GuiScreen.isKeyComboCtrlV(keyCode)) {
//            if (isBeingEdited && cursoePos != -1) {
//                insertAtSelection(GuiScreen.getClipboardString().replace("\n", "\\n"));
//            }
//
//            return true;
//        }
//        else if (GuiScreen.isKeyComboCtrlX(keyCode)) {
////            GuiScreen.setClipboardString(this.getSelectedText());
////
////            if (this.isEnabled) {
////                this.writeText("");
////            }
//
//            return true;
//        }
//        else {
//            switch (keyCode) {
//                case 28:
//                    insertAtSelection("\\n");
//                    return true;
//                case 14:
//
////                    if (GuiScreen.isCtrlKeyDown()) {
////                        if (this.isEnabled) {
////                            this.deleteWords(-1);
////                        }
////                    }
////                    else if (this.isEnabled) {
////                        this.deleteFromCursor(-1);
////                    }
//                    deleteFromCursor(-1);
//                    return true;
//                case 199:
//
////                    if (GuiScreen.isShiftKeyDown()) {
////                        this.setSelectionPos(0);
////                    }
////                    else {
////                        this.setCursorPositionZero();
////                    }
//
//                    return true;
//                case 200: //^
//                    moveCorsorUpDown(-1);
//                    return true;
//                case 208: //v
//                    moveCorsorUpDown(1);
//                    return true;
//                case 203: //<
//                    moveCursorBy(-1);
////                    if (GuiScreen.isShiftKeyDown()) {
////                        if (GuiScreen.isCtrlKeyDown()) {
////                            this.setSelectionPos(this.getNthWordFromPos(-1, this.getSelectionEnd()));
////                        }
////                        else {
////                            this.setSelectionPos(this.getSelectionEnd() - 1);
////                        }
////                    }
////                    else if (GuiScreen.isCtrlKeyDown()) {
////                        this.setCursorPosition(this.getNthWordFromCursor(-1));
////                    }
////                    else {
////                        this.moveCursorBy(-1);
////                    }
//
//                    return true;
//                case 205: //>
//                    moveCursorBy(1);
////                    if (GuiScreen.isShiftKeyDown()) {
////                        if (GuiScreen.isCtrlKeyDown()) {
////                            this.setSelectionPos(this.getNthWordFromPos(1, this.getSelectionEnd()));
////                        }
////                        else {
////                            this.setSelectionPos(this.getSelectionEnd() + 1);
////                        }
////                    }
////                    else if (GuiScreen.isCtrlKeyDown()) {
////                        this.setCursorPosition(this.getNthWordFromCursor(1));
////                    }
////                    else {
////                        this.moveCursorBy(1);
////                    }
//
//                    return true;
//                case 207:
//
////                    if (GuiScreen.isShiftKeyDown()) {
////                        this.setSelectionPos(this.text.length());
////                    }
////                    else {
////                        this.setCursorPositionEnd();
////                    }
//
//                    return true;
//                case 211:
//
////                    if (GuiScreen.isCtrlKeyDown()) {
////                        if (this.isEnabled) {
////                            this.deleteWords(1);
////                        }
////                    }
////                    else if (this.isEnabled) {
////                        this.deleteFromCursor(1);
////                    }
//                    deleteFromCursor(1);
//                    return true;
//                default:
//
//                    if (ChatAllowedCharacters.isAllowedCharacter(typedChar)) {
//                        insertAtSelection(Character.toString(typedChar));
//                        return true;
//                    }
//                    else {
//                        return super.keyTyped(typedChar, keyCode);
//                    }
//            }
//        }
        return super.keyTyped(typedChar, keyCode);
    }

    //endregion

    //region Helpers

//    public void insertAtSelection(String text) {
//        if (cursoePos < 0) {
//            return;
//        }
//
//        String before = cursoePos >= rawText.length() ? rawText : rawText.substring(0, cursoePos);
//        String after = cursoePos >= rawText.length() ? "" : rawText.substring(cursoePos);
//
//        rawText = before + text + after;
//        moveCursorBy(text.length());
//        ySize = Math.max(parseRawText().size() * fontRenderer.FONT_HEIGHT, 8);
//        list.schedualUpdate();
//        requiresSave = true;
//    }

//    public void deleteFromCursor(int dir) {
//        if (cursoePos < 0) {
//            return;
//        }
//
//        String before = cursoePos >= rawText.length() ? rawText : rawText.substring(0, cursoePos);
//        String after = cursoePos >= rawText.length() ? "" : rawText.substring(cursoePos);
//
//        try {
//
//            if (dir < 0) {
//                if (before.length() + dir < 0) {
//                    before = "";
//                }
//                else {
//                    before = before.substring(0, before.length() + dir);
//                }
//                moveCursorBy(dir);
//            }
//            else if (dir > 0) {
//                if (dir >= after.length()) {
//                    after = "";
//                }
//                else {
//                    after = after.substring(dir);
//                }
//            }
//        }
//        catch (Throwable e) {
//            e.printStackTrace();
//        }
//
//        rawText = before + after;
//        ySize = Math.max(parseRawText().size() * fontRenderer.FONT_HEIGHT, 8);
//        list.schedualUpdate();
//        requiresSave = true;
//    }

//    public void moveCursorBy(int i) {
//        if (cursoePos == -1) {
//            return;
//        }
//
//        cursoePos += i;
//        if (cursoePos < 0) {
//            cursoePos = 0;
//        }
//        if (cursoePos > rawText.length()) {
//            cursoePos = rawText.length();
//        }
//    }

//    public void moveCorsorUpDown(int dir) {
//
//        List<String> display = parseRawText();
//
//        int textPos = 0;
//        int row = 0;
//        for (String text : display) {
//            int textWidth = fontRenderer.getStringWidth(text);
//            int yTex = yPos + (row * fontRenderer.FONT_HEIGHT);
//            int xTex = alignment == LEFT ? xPos + 4 : alignment == CENTER ? xPos + (xSize / 2) - (textWidth / 2) : xPos + xSize - textWidth - 4;
//
//            int cursor = cursoePos - textPos;
//            if (isBeingEdited && cursoePos != -1 && cursoePos >= textPos && cursoePos <= textPos + text.length()) {
//                boolean onNewLine = text.endsWith("\\n");
//                if (onNewLine && cursor == text.length()) {
//
//                }
//                else if (cursor >= 0 && cursor <= text.length()) {
//                    int pos = cursor == text.length() ? fontRenderer.getStringWidth(text) : fontRenderer.getStringWidth(text.substring(0, cursor));
//
//                    if (yTex >= yPos + ySize - fontRenderer.FONT_HEIGHT && dir > 0) {
//                        return;
//                    }
//
//                    int newPos = calculateCursorIndex(xTex + pos, yTex + (dir * fontRenderer.FONT_HEIGHT));
//                    if (newPos != -1) {
//                        cursoePos = newPos;
//                    }
//                    return;
////                    drawColouredRect(xTex + pos, yTex, 1, fontRenderer.FONT_HEIGHT, 0xFFFFFFFF);
//                }
//            }
//
//            textPos += text.length();
//            row++;
//        }
//
//    }

//    public String getDisplayText() {
//        return isBeingEdited ? rawText : displayText;
//    }

    @SuppressWarnings("unchecked")
    public List<String> parseRawText() {
        String text = rawText;

//        if (timer / 10 % 2 == 0) {
//            if (cursoePos >= 0) {
//
//                String before = cursoePos >= text.length() ? text : text.substring(0, cursoePos);
//                String after = cursoePos >= text.length() ? "" : text.substring(cursoePos);
//
//                text = before + TextFormatting.DARK_RED + "|" + TextFormatting.RESET + after;
//            }
//        }

//        if (text.endsWith("\\n") && isBeingEdited) {
//            text += " ";
//        }
//        LinkedList<String> formattedList = new LinkedList<String>();
//        List<String> splitList = Arrays.asList(text.split(Pattern.quote("\\n")));
//
//        int row = 0;
//        for (String subString : splitList) {
//            if (isBeingEdited && splitList.size() - 1 > row) {
//                subString += "\\n";
//            }
//            formattedList.addAll(fontRenderer.listFormattedStringToWidth(subString, xSize - 6));
//            row++;
//        }

        return fontRenderer.listFormattedStringToWidth(text, xSize - 6);
    }

//    public int calculateCursorIndex(int mouseX, int mouseY) {
//        if (!isMouseOver(mouseX, mouseY)) {
//            return -1;
//        }
//
//        int posRow = (mouseY - yPos) / fontRenderer.FONT_HEIGHT;
//
//        List<String> display = parseRawText();
//
//        int textPos = 0;
//        for (String text : display) {
//            int textWidth = fontRenderer.getStringWidth(text);
//            int row = display.indexOf(text);
//            int xTex = alignment == LEFT ? xPos + 4 : alignment == CENTER ? xPos + (xSize / 2) - (textWidth / 2) : xPos + xSize - textWidth - 4;
//
//            if (posRow == row) {
//                int width = mouseX - xTex;
//                if (width < 0) {
//                    return -1;
//                }
//                String s = fontRenderer.trimStringToWidth(text, width);
//
//                return textPos + s.length();
//            }
//            textPos += text.length();
//        }
//
//        return posRow;
//    }

    //endregion

    //region Edit Base

    @Override
    public LinkedList<MGuiElementBase> getEditControls() {
        LinkedList<MGuiElementBase> list = super.getEditControls();
        list.add(new MGuiButtonSolid(modularGui, "TOGGLE_ALIGN", 0, 0, 26, 12, "Align") {
            @Override
            public int getBorderColour(boolean hovering, boolean disabled) {
                return hovering ? 0xFF00FF00 : 0xFFFF0000;
            }
        }.setListener(this).setToolTip(new String[]{"Toggle Text Alignment"}));
        list.add(new MGuiButtonSolid(modularGui, "OPEN_EDITOR", 0, 0, 66, 12, "Show Editor") {
            @Override
            public int getBorderColour(boolean hovering, boolean disabled) {
                return hovering ? 0xFF00FF00 : 0xFFFF0000;
            }
        }.setListener(this).setToolTip(new String[]{"Open the editor or bring it to the front if it is already open in the background"}));
        return list;
    }

    @Override
    public void onMGuiEvent(String eventString, MGuiElementBase eventElement) {
        super.onMGuiEvent(eventString, eventElement);

        if (eventElement instanceof MGuiButton && ((MGuiButton) eventElement).buttonName.equals("OPEN_EDITOR")) {
            if (editTextArea == null) {
                editTextArea = new UIEditTextArea(rawText);
                editTextArea.pack();
            }

            SwingHelper.centerOnMinecraftWindow(editTextArea);
            editTextArea.setVisible(true);
        }
        else if (eventElement.id.equals("COLOUR") && eventString.equals("TEXT_FIELD_CHANGED") && eventElement instanceof MGuiTextField) {
            try {
                setColour(Integer.parseInt(((MGuiTextField) eventElement).getText(), 16));
                element.setAttribute(ATTRIB_COLOUR, Integer.toHexString(getColour()));
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onCreated() {

    }

    @Override
    public int getEntryHeight() {
        return ySize;
    }

    //endregion

    //region XML & Factory

    @Override
    public void save() {
        element.setTextContent(rawText);
        super.save();
        branch.guiWiki.contentWindow.setEditingComponent(null);
    }

    @Override
    public void loadFromXML(Element element) {
        super.loadFromXML(element);
        rawText = element.getTextContent();
    }

    @Override
    public void setXSize(int xSize) {
        super.setXSize(xSize);
        if (this.xSize < 40) {
            this.xSize = 40;
        }
        ySize = Math.max(parseRawText().size() * fontRenderer.FONT_HEIGHT, 8);
    }

    public static class Factory implements IDisplayComponentFactory {
        @Override
        public DisplayComponentBase createNewInstance(GuiModWiki guiWiki, TreeBranchRoot branch) {
            DisplayComponentBase component = new DCTextArea(guiWiki, getID(), branch);
            component.setWorldAndResolution(guiWiki.mc, guiWiki.screenWidth(), guiWiki.screenHeight());
            return component;
        }

        @Override
        public String getID() {
            return "textArea";
        }

    }

    //endregion

    //region Misc

    @Override
    public boolean onUpdate() {
        if (editTextArea != null) {
            if (!isBeingEdited) {
                editTextArea.dispose();
                editTextArea = null;
            }
            else {
//                LogHelper.dev(editTextArea.linkTimer);
                editTextArea.linkTimer = 20;
                if (editTextArea.hasChanged) {
                    editTextArea.hasChanged = false;
                    rawText = editTextArea.text;
                    requiresSave = true;
                    ySize = Math.max(parseRawText().size() * fontRenderer.FONT_HEIGHT, 8);
                    list.schedualUpdate();
                }
                if (editTextArea.isFinished) {
                    save();
                    return true;
                }
            }
        }

        return super.onUpdate();
    }


    //endregion
}
