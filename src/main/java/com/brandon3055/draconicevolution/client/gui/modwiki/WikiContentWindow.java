package com.brandon3055.draconicevolution.client.gui.modwiki;

import codechicken.lib.colour.Colour;
import codechicken.lib.colour.ColourARGB;
import com.brandon3055.brandonscore.client.gui.modulargui.MGuiElementBase;
import com.brandon3055.brandonscore.client.gui.modulargui.lib.EnumAlignment;
import com.brandon3055.brandonscore.client.gui.modulargui.lib.IMGuiListener;
import com.brandon3055.brandonscore.client.gui.modulargui.modularelements.*;
import com.brandon3055.brandonscore.client.utils.GuiHelper;
import com.brandon3055.draconicevolution.client.gui.modwiki.moddata.WikiDocManager;
import com.brandon3055.draconicevolution.client.gui.modwiki.moddata.displaycontent.DisplayComponentBase;
import com.brandon3055.draconicevolution.client.gui.modwiki.moddata.displaycontent.DisplayComponentRegistry;
import com.brandon3055.draconicevolution.client.gui.modwiki.moddata.guidoctree.TreeBranchRoot;
import com.brandon3055.draconicevolution.utils.LogHelper;
import net.minecraft.client.Minecraft;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static com.brandon3055.draconicevolution.client.gui.modwiki.WikiConfig.CONTENT_WINDOW;
import static com.brandon3055.draconicevolution.client.gui.modwiki.WikiConfig.NAV_TEXT;

/**
 * Created by brandon3055 on 31/08/2016.
 */
public class WikiContentWindow extends MGuiList implements IMGuiListener {
    private GuiModWiki guiModWiki;
    public TreeBranchRoot activeBranch;
    public MGuiLabel label;
    private boolean initialized = false;
    public DisplayComponentBase editingComponent = null;
    public LinkedList<MGuiElementBase> editControls = new LinkedList<MGuiElementBase>();
    public MGuiButtonSolid add;
    public MGuiButtonSolid delete;
    public MGuiSelectDialog addSelector;

    public WikiContentWindow(GuiModWiki parentGui) {
        super(parentGui);
        guiModWiki = parentGui;
        topPadding = 12;
        rightPadding = 4;
    }

    //region Init

    @Override
    public void initElement() {
        toRemove.addAll(childElements);

        addChild(label = new MGuiLabel(guiModWiki, xPos, yPos + 1, xSize, 12, activeBranch.branchID.equals("ROOT") ? "Project Intelligence" : activeBranch.branchName) {
            @Override
            public int getTextColour() {
                return NAV_TEXT;
            }

            @Override
            public boolean getDropShadow() {
                Colour colour = new ColourARGB(NAV_TEXT);
                long l = ((colour.r & 0xff) + (colour.g & 0xff) + (colour.b & 0xff)) / 3;
                return l > 80;
            }
        }.setAlignment(EnumAlignment.LEFT));

        addChild(delete = (MGuiButtonSolid) new MGuiButtonSolid(guiModWiki, "DELETE", xPos + xSize - 12, yPos, 12, 12, "X") {
            @Override
            protected int getFillColour(boolean hovering, boolean disabled) {
                return disabled ? 0xFF777777 : hovering ? 0xFFFFFFFF : 0xFFFF0000;
            }

            @Override
            protected int getBorderColour(boolean hovering, boolean disabled) {
                return disabled ? 0xFF555555 : hovering ? 0xFFFF0000 : 0xFFFFFFFF;
            }
        }.setListener(this).setToolTip(new String[]{"Delete Selected Display Component"}));
        delete.disabled = true;

        addChild(add = (MGuiButtonSolid) new MGuiButtonSolid(modularGui, "ADD", xPos + xSize - 38, yPos, 25, 12, "Add") {
            @Override
            protected int getBorderColour(boolean hovering, boolean disabled) {
                return hovering ? 0xFF00FF00 : 0xFFFF0000;
            }
        }.setListener(this).setToolTip(new String[]{"Add New Display Component"}));

        delete.setEnabled(GuiModWiki.editMode && activeBranch.branchData != null);
        add.setEnabled(GuiModWiki.editMode && activeBranch.branchData != null);

        super.initElement();
        initialized = true;
    }

    @Override
    protected void initScrollBar() {
//        scrollBar = new MGuiScrollBar(modularGui, xPos + xSize - 10, yPos + 12, 10, ySize - 14);
        scrollBar = new MGuiScrollBar(modularGui, xPos + xSize - 5, yPos + 11, 6, ySize - 12) {
            @Override
            public int getScrollColour() {
                return mixColours(CONTENT_WINDOW, 0x00505050);
            }
        };
        scrollBar.addChild(new MGuiHoverPopup(modularGui, new String[] {"Pro Tip.", "Hold shift while scrolling to scroll faster!"}, scrollBar));
        scrollBar.borderColour = 0x00000000;
        scrollBar.backColour = 0x00000000;
        addChild(scrollBar);
        scrollBar.setListener(this);
        scrollBar.parentScrollable = this;
    }

    //endregion

    //region Render

    @Override
    public void renderBackgroundLayer(Minecraft minecraft, int mouseX, int mouseY, float partialTicks) {
        int navWindowColour = CONTENT_WINDOW;

        int neg15 = mixColours(navWindowColour, 0x00151515, true);
        int pos20 = mixColours(navWindowColour, 0x00202020);
        int neg30 = mixColours(navWindowColour, 0x00303030, true);

        drawColouredRect(xPos, yPos, xSize, ySize, navWindowColour);        //Main Window
        drawColouredRect(xPos, yPos, xSize, 0.5, pos20);                    //Top Accent
        drawColouredRect(xPos, yPos + 0.5, xSize, 0.6, neg15);              //Top Accent
        drawColouredRect(xPos, yPos + 1, xSize, 11, mixColours(navWindowColour, 0x00080808, true));                    //Top Bar
        drawColouredRect(xPos, yPos + 11.5, xSize, 0.5, neg30);              //Top Bar
        drawColouredRect(xPos, yPos + ySize - 1, xSize, 1, neg30);          //Bottom Accent
        drawColouredRect(xPos, yPos, 0.6, ySize, pos20);                    //Window Left Accent
        drawColouredRect(xPos + 0.6, yPos + 0.5, 0.6, ySize - 1, neg15);    //Window Left Accent 2
        drawColouredRect(xPos + xSize - 0.6, yPos, 0.6, 12, pos20);      //Window Right Accent
        drawColouredRect(xPos + xSize - 1.2, yPos + 0.5, 0.6, 11, neg15);      //Window Right Accent 2

        if (scrollBar.isEnabled() && GuiHelper.isInRect(scrollBar.xPos, scrollBar.yPos, scrollBar.xSize, scrollBar.ySize, mouseX, mouseY)) {
            drawColouredRect(xPos + xSize - 4, yPos + 12, 4, ySize - 12, mixColours(scrollBar.scrollColour, 0xCC000000, true));
        }

        if (activeBranch.branchData == null) {
            drawString(fontRenderer, "[Error: Failed to load page content]", xPos + 3, yPos + 15, 0xFF0000);
        }

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

    //endregion

    //region List

    public void updatePositions() {
        if (!initialized) {
            return;
        }

        label.xPos = xPos;
        label.yPos = yPos + 1;
        label.xSize = xSize;
    }

    //endregion

    //region Editing

    public void setEditingComponent(DisplayComponentBase component) {
        topPadding = 12;

        if (!initialized) {
            return;
        }
        if (editingComponent != null) {
            editingComponent.isBeingEdited = false;
            if (editingComponent.requiresSave) {
                editingComponent.requiresSave = false;
                editingComponent.save();
            }
        }

        this.editingComponent = component;
        toRemove.addAll(editControls);
        editControls.clear();

        delete.disabled = component == null;
        if (editingComponent == null) {
            label.setEnabled(true);
            updateEntriesAndScrollBar();
            return;
        }

        editingComponent.isBeingEdited = true;
        label.setEnabled(false);
        editControls.addAll(editingComponent.getEditControls());

        int offset = 0;
        int yOffset = 0;
        for (MGuiElementBase element : editControls) {
            if (xPos + offset + element.xSize > xPos + xSize - 40) {
                offset = 0;
                yOffset += 12;
                topPadding = 12 + yOffset;
            }
            addChild(element);
            element.xPos = xPos + offset;
            offset += element.xSize + 1;
            element.yPos = yPos + yOffset;
        }

        updateEntriesAndScrollBar();
    }

    @Override
    public void onMGuiEvent(String eventString, MGuiElementBase eventElement) {
        if (eventElement == delete && editingComponent != null) {
//            if (editingComponent instanceof DCSplitContainer) {
//                ((DCSplitContainer) editingComponent).deleteSelectedComponent();
//            }
//            else {
            editingComponent.element.getParentNode().removeChild(editingComponent.element);
//                activeBranch.branchData.removeChild(editingComponent.element);
//            }
            try {
                activeBranch.save();
                WikiDocManager.reload(false, true, true);
                guiModWiki.wikiDataTree.reOpenLast();
                setEditingComponent(null);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        else if (eventElement == add) {
            if (addSelector != null) {
                modularGui.getManager().remove(addSelector);
                addSelector = null;
                return;
            }
            addSelector = new MGuiSelectDialog(modularGui, xPos, yPos, 10, 50);

            List<MGuiElementBase> list = new ArrayList<MGuiElementBase>();
            int width = 10;

            for (String option : DisplayComponentRegistry.REGISTRY.keySet()) {
                int optionWidth = fontRenderer.getStringWidth(option) + 4;
                if (optionWidth + 10 > width) {
                    width = optionWidth + 10;
                }
                list.add(new MGuiLabel(modularGui, 0, 0, optionWidth, 12, option));
            }

            addSelector.xPos = xPos + xSize - (width + 4);
            addSelector.yPos = yPos + 12;
            addSelector.ySize = Math.min((list.size() * 12) + 4, ySize - 12);
            addSelector.initElement();

            addSelector.setOptions(list);
            addSelector.setListener(this);
            modularGui.getManager().add(addSelector, 2);

        }
        else if (eventString.equals("SELECTOR_PICK") && eventElement instanceof MGuiLabel) {
            String type = ((MGuiLabel) eventElement).displayString;
            Element element = activeBranch.branchData.getOwnerDocument().createElement(WikiDocManager.ELEMENT_CONTENT);

            int index = activeBranch.branchContent.size() == 0 ? 0 : activeBranch.branchContent.getLast().posIndex + 1;
            element.setAttribute("index", String.valueOf(index));
            element.setAttribute(WikiDocManager.ATTRIB_TYPE, type);

            Node node = activeBranch.branchData.getFirstChild();
            if (node == null) {
                activeBranch.branchData.appendChild(element);
            }
            else {
                activeBranch.branchData.insertBefore(element, node);
            }

            DisplayComponentBase component = DisplayComponentRegistry.createComponent(guiModWiki, type, element, activeBranch);
            if (component == null) {
                LogHelper.error("Failed to create display component... This should not happen.");
                return;
            }

            modularGui.getManager().remove(addSelector);
            addSelector = null;

            component.onCreated();

            try {
                activeBranch.save();
                WikiDocManager.reload(false, true, true);
                guiModWiki.wikiDataTree.reOpenLast();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //endregion

    //region Misc

    public void setActiveBranch(TreeBranchRoot activeBranch) {
        if (activeBranch != this.activeBranch && scrollBar != null) {
            scrollBar.setScrollPos(0);
        }

        this.activeBranch = activeBranch;
        setEditingComponent(null);
        if (label != null) {
            label.setDisplayString(activeBranch.branchID.equals("ROOT") ? "Project Intelligence" : activeBranch.branchName);
        }

        clear();

        for (DisplayComponentBase entry : activeBranch.branchContent) {
            entry.setXSize(xSize - leftPadding - rightPadding);
            addEntry(entry);
            entry.initElement();
        }
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (super.mouseClicked(mouseX, mouseY, mouseButton)) {
            return true;
        }

        if (editingComponent != null && !editingComponent.isMouseOver(mouseX, mouseY)) {
            setEditingComponent(null);
        }

        if (addSelector != null && !addSelector.isMouseOver(mouseX, mouseY)) {
            modularGui.getManager().remove(addSelector);
            addSelector = null;
        }

        return false;
    }

    //endregion
}
