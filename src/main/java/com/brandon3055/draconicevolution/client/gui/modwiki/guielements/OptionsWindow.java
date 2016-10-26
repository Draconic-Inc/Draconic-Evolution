package com.brandon3055.draconicevolution.client.gui.modwiki.guielements;

import com.brandon3055.brandonscore.client.gui.modulargui.IModularGui;
import com.brandon3055.brandonscore.client.gui.modulargui.MGuiElementBase;
import com.brandon3055.brandonscore.client.gui.modulargui.lib.IMGuiListener;
import com.brandon3055.brandonscore.client.gui.modulargui.modularelements.*;
import com.brandon3055.draconicevolution.client.gui.modwiki.StylePreset;
import com.brandon3055.draconicevolution.client.gui.modwiki.WikiConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by brandon3055 on 18/09/2016.
 */
public class OptionsWindow extends MGuiElementBase implements IMGuiListener{

    public static volatile boolean requiresSave = false;
    public MGuiSelectDialog selector;

    public OptionsWindow(IModularGui modularGui, int xPos, int yPos, int xSize, int ySize) {
        super(modularGui, xPos, yPos, xSize, ySize);
    }

    //region Init

    @Override
    public void initElement() {
        toRemove.addAll(childElements);

        int size = (xSize - 34) / 3;

        addChild(new MGuiButtonSolid(modularGui, "COLOUR_NAV", xPos + 15, yPos + 30, size, 12, I18n.format("modwiki.style.navWindow")){
            @Override
            public int getFillColour(boolean hovering, boolean disabled) {
                return WikiConfig.NAV_WINDOW;
            }

            @Override
            public int getBorderColour(boolean hovering, boolean disabled) {
                return WikiConfig.NAV_WINDOW;
            }
        }.setListener(this));
        addChild(new MGuiButtonSolid(modularGui, "COLOUR_MAIN", xPos + 17 + (size), yPos + 30, size, 12, I18n.format("modwiki.style.mainWindow")){
            @Override
            public int getFillColour(boolean hovering, boolean disabled) {
                return WikiConfig.CONTENT_WINDOW;
            }

            @Override
            public int getBorderColour(boolean hovering, boolean disabled) {
                return WikiConfig.CONTENT_WINDOW;
            }
        }.setListener(this));
        addChild(new MGuiButtonSolid(modularGui, "COLOUR_MENU", xPos + 19 + (size * 2), yPos + 30, size, 12, I18n.format("modwiki.style.menu")){
            @Override
            public int getFillColour(boolean hovering, boolean disabled) {
                return WikiConfig.MENU_BAR;
            }

            @Override
            public int getBorderColour(boolean hovering, boolean disabled) {
                return WikiConfig.MENU_BAR;
            }
        }.setListener(this));


        addChild(new MGuiButtonSolid(modularGui, "COLOUR_NAV_TEXT", xPos + 15, yPos + 44, size, 12, I18n.format("modwiki.style.navText")){
            @Override
            public int getFillColour(boolean hovering, boolean disabled) {
                return mixColours(WikiConfig.NAV_TEXT, 0xFF000000);
            }

            @Override
            public int getBorderColour(boolean hovering, boolean disabled) {
                return mixColours(WikiConfig.NAV_TEXT, 0xFF000000);
            }
        }.setListener(this));
        addChild(new MGuiButtonSolid(modularGui, "COLOUR_MISC_TEXT", xPos + 17 + (size), yPos + 44, size, 12, I18n.format("modwiki.style.text")){
            @Override
            public int getFillColour(boolean hovering, boolean disabled) {
                return mixColours(WikiConfig.TEXT_COLOUR, 0xFF000000);
            }

            @Override
            public int getBorderColour(boolean hovering, boolean disabled) {
                return mixColours(WikiConfig.TEXT_COLOUR, 0xFF000000);
            }
        }.setListener(this));
        addChild(new MGuiButtonSolid(modularGui, "PRESETS", xPos + 19 + (size * 2), yPos + 44, size, 12, I18n.format("modwiki.style.presets")){
            @Override
            public int getFillColour(boolean hovering, boolean disabled) {
                return 0xFFFFFFFF;
            }

            @Override
            public int getBorderColour(boolean hovering, boolean disabled) {
                return hovering ? 0xFF00FFFF : 0xFF000000;
            }
        }.setListener(this));



        super.initElement();
    }

    //endregion

    //region Render

    @Override
    public void renderBackgroundLayer(Minecraft minecraft, int mouseX, int mouseY, float partialTicks) {
        drawBorderedRect(xPos, yPos, xSize, ySize, 1, 0xff707070, 0xff000000);
        drawCenteredString(fontRenderer, I18n.format("generic.options.txt"), xPos + (xSize / 2), yPos + 4, 0xFFFFFF, true);
        drawColouredRect(xPos + 15, yPos + 13, xSize - 30, 0.5, 0xFFFFFFFF);
        drawColouredRect(xPos + 20, yPos + 13.5, xSize - 40, 0.5, 0xFF000000);

        drawCenteredString(fontRenderer, I18n.format("modwiki.label.style"), xPos + (xSize / 2), yPos + 18, 0x00FFFF, true);
        drawColouredRect(xPos + 15, yPos + 60, xSize - 30, 0.5, 0xFF00FFFF);
        drawColouredRect(xPos + 20, yPos + 60.5, xSize - 40, 0.5, 0xFF000000);

        super.renderBackgroundLayer(minecraft, mouseX, mouseY, partialTicks);
    }

    //endregion

    //region Interact

    @Override
    public void onMGuiEvent(String eventString, MGuiElementBase eventElement) {
        if (eventElement instanceof MGuiButton && ((MGuiButton) eventElement).buttonName.equals("COLOUR_NAV")) {
            MGuiColourPicker picker = new MGuiColourPicker(modularGui, xPos, yPos, this);
            picker.xPos = xPos + (xSize / 2) - (picker.xSize / 2);
            picker.yPos = yPos + (ySize / 2) - (picker.ySize / 2);
            picker.setId("SELECT_COLOUR_NAV");
            picker.canDrag = true;
            picker.setColour(WikiConfig.NAV_WINDOW);
            picker.initElement();
            picker.show();
        }
        else if (eventElement instanceof MGuiButton && ((MGuiButton) eventElement).buttonName.equals("COLOUR_MAIN")) {
            MGuiColourPicker picker = new MGuiColourPicker(modularGui, xPos, yPos, this);
            picker.xPos = xPos + (xSize / 2) - (picker.xSize / 2);
            picker.yPos = yPos + (ySize / 2) - (picker.ySize / 2);
            picker.setId("SELECT_COLOUR_MAIN");
            picker.canDrag = true;
            picker.setColour(WikiConfig.CONTENT_WINDOW);
            picker.initElement();
            picker.show();
        }
        else if (eventElement instanceof MGuiButton && ((MGuiButton) eventElement).buttonName.equals("COLOUR_MENU")) {
            MGuiColourPicker picker = new MGuiColourPicker(modularGui, xPos, yPos, this);
            picker.xPos = xPos + (xSize / 2) - (picker.xSize / 2);
            picker.yPos = yPos + (ySize / 2) - (picker.ySize / 2);
            picker.setId("SELECT_COLOUR_MENU");
            picker.canDrag = true;
            picker.setColour(WikiConfig.MENU_BAR);
            picker.initElement();
            picker.show();
        }
        else if (eventElement instanceof MGuiButton && ((MGuiButton) eventElement).buttonName.equals("COLOUR_NAV_TEXT")) {
            MGuiColourPicker picker = new MGuiColourPicker(modularGui, xPos, yPos, this);
            picker.setIncludeAlpha(false);
            picker.xPos = xPos + (xSize / 2) - (picker.xSize / 2);
            picker.yPos = yPos + (ySize / 2) - (picker.ySize / 2);
            picker.setId("SELECT_COLOUR_NAV_TEXT");
            picker.canDrag = true;
            picker.setColour(WikiConfig.NAV_TEXT);
            picker.initElement();
            picker.show();
        }
        else if (eventElement instanceof MGuiButton && ((MGuiButton) eventElement).buttonName.equals("COLOUR_MISC_TEXT")) {
            MGuiColourPicker picker = new MGuiColourPicker(modularGui, xPos, yPos, this);
            picker.setIncludeAlpha(false);
            picker.xPos = xPos + (xSize / 2) - (picker.xSize / 2);
            picker.yPos = yPos + (ySize / 2) - (picker.ySize / 2);
            picker.setId("SELECT_COLOUR_MISC_TEXT");
            picker.canDrag = true;
            picker.setColour(WikiConfig.TEXT_COLOUR);
            picker.initElement();
            picker.show();
        }
        else if (eventElement instanceof MGuiColourPicker && eventString.equals("COLOUR_PICKED")) {
            if (eventElement.id.equals("SELECT_COLOUR_NAV")) {
                WikiConfig.NAV_WINDOW = ((MGuiColourPicker) eventElement).getColourARGB();
            }
            else if (eventElement.id.equals("SELECT_COLOUR_MAIN")) {
                WikiConfig.CONTENT_WINDOW = ((MGuiColourPicker) eventElement).getColourARGB();
            }
            else if (eventElement.id.equals("SELECT_COLOUR_MENU")) {
                WikiConfig.MENU_BAR = ((MGuiColourPicker) eventElement).getColourARGB();
            }
            else if (eventElement.id.equals("SELECT_COLOUR_NAV_TEXT")) {
                WikiConfig.NAV_TEXT = ((MGuiColourPicker) eventElement).getColourARGB();
            }
            else if (eventElement.id.equals("SELECT_COLOUR_MISC_TEXT")) {
                WikiConfig.TEXT_COLOUR = ((MGuiColourPicker) eventElement).getColourARGB();
            }
            WikiConfig.save();
        }
        else if (eventElement instanceof MGuiButton && ((MGuiButton) eventElement).buttonName.equals("PRESETS")) {
            if (selector != null) {
                modularGui.getManager().remove(selector);
                selector = null;
                return;
            }

            selector = new MGuiSelectDialog(modularGui, eventElement.xPos, eventElement.yPos + eventElement.ySize);

            List<MGuiElementBase> list = new LinkedList<>();
            int y = 0;
            for (StylePreset preset : StylePreset.PRESETS) {
                MGuiLabel label = new MGuiLabel(modularGui, 0, 0, fontRenderer.getStringWidth(preset.getName()) + 4, 12, preset.getName());
                label.linkedObject = preset;
                list.add(label);
                y += label.ySize;
            }

            selector.ySize = Math.min(y + 3, ySize - eventElement.yPos - eventElement.ySize);
            selector.initElement();
            selector.setOptions(list);
            selector.setListener(this);
            modularGui.getManager().add(selector, displayLevel + 1);
        }
        else if (eventString.equals("SELECTOR_PICK") && eventElement.linkedObject instanceof StylePreset) {
            ((StylePreset)eventElement.linkedObject).apply();
            if (selector != null) {
                modularGui.getManager().remove(selector);
                selector = null;
            }
        }

    }

    //endregion

    @Override
    public boolean onUpdate() {
        if (requiresSave) {
            requiresSave = false;
            WikiConfig.save();
        }

        return super.onUpdate();
    }
}
