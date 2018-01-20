package com.brandon3055.draconicevolution.client.gui.modwiki.moddata.displaycontent;

import com.brandon3055.brandonscore.client.gui.modulargui_old.MGuiElementBase;
import com.brandon3055.brandonscore.client.gui.modulargui_old.lib.EnumAlignment;
import com.brandon3055.brandonscore.client.gui.modulargui_old.lib.IMGuiListener;
import com.brandon3055.brandonscore.client.gui.modulargui_old.modularelements.MGuiButton;
import com.brandon3055.brandonscore.client.gui.modulargui_old.modularelements.MGuiButtonSolid;
import com.brandon3055.brandonscore.client.gui.modulargui_old.modularelements.MGuiListEntry;
import com.brandon3055.draconicevolution.client.gui.modwiki.GuiModWiki;
import com.brandon3055.draconicevolution.client.gui.modwiki.WikiConfig;
import com.brandon3055.draconicevolution.client.gui.modwiki.moddata.guidoctree.TreeBranchRoot;
import com.brandon3055.draconicevolution.utils.LogHelper;
import net.minecraft.client.Minecraft;
import org.w3c.dom.Element;

import java.io.IOException;
import java.util.LinkedList;

/**
 * Created by brandon3055 on 8/09/2016.
 */
public abstract class DisplayComponentBase extends MGuiListEntry implements IMGuiListener {

    private GuiModWiki modWiki;
    protected String componentType;
    public TreeBranchRoot branch;
    public Element element;
    public EnumAlignment alignment = EnumAlignment.CENTER;
    public boolean shadow;
    private int colour = 0xFFFFFF;
    public boolean colourSet = false;
    public int posIndex = 0;
    public DCSplitContainer container = null;
    /**
     * If true will attempt to save the element on exit.
     */
    public boolean requiresSave = false;
    public boolean isBeingEdited = false;
    /**
     * Used with requiresSave to schedule a save.
     */
    public int saveTimer = 0;

    public static final String ATTRIB_ALIGNMENT = "alignment";
    public static final String ATTRIB_SHADOW = "shadow";
    public static final String ATTRIB_COLOUR = "colour";


    public DisplayComponentBase(GuiModWiki modWiki, String componentType, TreeBranchRoot branch) {
        super(modWiki);
        this.modWiki = modWiki;
        this.componentType = componentType;
        this.branch = branch;
    }

    //region List

    @Override
    public int getEntryHeight() {
        return ySize;
    }

    @Override
    public void moveEntry(int newXPos, int newYPos) {
        int moveX = newXPos - xPos;
        int moveY = newYPos - yPos;
        moveBy(moveX, moveY);
    }

    public void setXSize(int xSize) {
        this.xSize = xSize;
    }

    //endregion

    //region Edit

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (WikiConfig.editMode && isMouseOver(mouseX, mouseY) && modWiki.contentWindow.editingComponent != this) {
            modWiki.contentWindow.setEditingComponent(this);
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    public LinkedList<MGuiElementBase> getEditControls() {
        LinkedList<MGuiElementBase> list = new LinkedList<MGuiElementBase>();
        list.add(new MGuiButtonSolid(modularGui, "MOVE_UP", 0, 0, 10, 12, "▲") {
            @Override
            public int getBorderColour(boolean hovering, boolean disabled) {
                return hovering ? 0xFF00FF00 : 0xFFFF0000;
            }
        }.setListener(this).setToolTip(new String[]{"Move Element Up"}));
        list.add(new MGuiButtonSolid(modularGui, "MOVE_DOWN", 0, 0, 10, 12, "▼") {
            @Override
            public int getBorderColour(boolean hovering, boolean disabled) {
                return hovering ? 0xFF00FF00 : 0xFFFF0000;
            }
        }.setListener(this).setToolTip(new String[]{"Move Element Down"}));

        return list;
    }

    @Override
    protected boolean keyTyped(char typedChar, int keyCode) throws IOException {
        return super.keyTyped(typedChar, keyCode);
    }

    @Override
    public void onMGuiEvent(String eventString, MGuiElementBase eventElement) {
        if (eventElement instanceof MGuiButton && ((MGuiButton) eventElement).buttonName.equals("MOVE_UP")) {
            int listIndex = branch.branchContent.indexOf(this);

            if (listIndex <= 0) {
                return;
            }

            DisplayComponentBase compAbove = branch.branchContent.get(listIndex - 1);

            element.setAttribute("index", String.valueOf(compAbove.posIndex));
            compAbove.element.setAttribute("index", String.valueOf(posIndex));

            try {
                branch.save();
                branch.loadBranchContent();
                modWiki.wikiDataTree.setActiveBranch(branch);
                modWiki.contentWindow.setEditingComponent(branch.branchContent.get(listIndex - 1));
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        else if (eventElement instanceof MGuiButton && ((MGuiButton) eventElement).buttonName.equals("MOVE_DOWN")) {
            int listIndex = branch.branchContent.indexOf(this);

            if (listIndex + 1 >= branch.branchContent.size()) {
                return;
            }

            DisplayComponentBase compBellow = branch.branchContent.get(listIndex + 1);

            element.setAttribute("index", String.valueOf(compBellow.posIndex));
            compBellow.element.setAttribute("index", String.valueOf(posIndex));

            try {
                branch.save();
                branch.loadBranchContent();
                modWiki.wikiDataTree.setActiveBranch(branch);
                modWiki.contentWindow.setEditingComponent(branch.branchContent.get(listIndex + 1));
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        else if (eventElement instanceof MGuiButton && ((MGuiButton) eventElement).buttonName.equals("TOGGLE_ALIGN")) {
            alignment = alignment == EnumAlignment.LEFT ? EnumAlignment.CENTER : alignment == EnumAlignment.CENTER ? EnumAlignment.RIGHT : EnumAlignment.LEFT;
            element.setAttribute(ATTRIB_ALIGNMENT, alignment.name());
            save();
        }
    }

    public void save() {
        try {
            int index = branch.branchContent.indexOf(this);
            if (container != null) {
                index = branch.branchContent.indexOf(container);
            }

            branch.save();
            branch.loadBranchContent();
            modWiki.wikiDataTree.setActiveBranch(branch);
            DisplayComponentBase comp = index >= 0 && index < branch.branchContent.size() ? branch.branchContent.get(index) : null;

            if (comp instanceof DCSplitContainer && container != null) {
                String side = element.getAttribute("splitSide");
                modWiki.contentWindow.setEditingComponent(side.equals("LEFT") ? ((DCSplitContainer) comp).componentLeft : side.equals("RIGHT") ? ((DCSplitContainer) comp).componentRight : null);
            }
            else {
                modWiki.contentWindow.setEditingComponent(comp);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    //endregion

    //region render

    @Override
    public void renderBackgroundLayer(Minecraft minecraft, int mouseX, int mouseY, float partialTicks) {
        if (WikiConfig.editMode && WikiConfig.drawEditInfo) {
            drawBorderedRect(xPos, yPos, xSize, ySize, 0.5, 0, modWiki.contentWindow.editingComponent == this ? 0xFF00FF00 : 0xFFFF0000);
            if (isMouseOver(mouseX, mouseY)) {
                int x = list.xPos;
                int y = list.yPos - 10;
                zOffset += 200;
                String s = "Content Type: <" + componentType + "> Click to edit";
                drawColouredRect(x, y, fontRenderer.getStringWidth(s) + 2, 10, 0xFF000000);
                drawString(fontRenderer, s, x + 1, y + 1, 0xFF0000);
                zOffset -= 200;
            }
        }
        super.renderBackgroundLayer(minecraft, mouseX, mouseY, partialTicks);
    }

    //endregion

    //region Load

    public void loadFromXML(Element element) {
        this.element = element;

        try {
            posIndex = Integer.parseInt(element.getAttribute("index"));
        }
        catch (Exception e) {
            LogHelper.error("Failed to load component index in: " + branch.branchID + " Attempting to insert in the correct location based on document order.");
            e.printStackTrace();
            posIndex = branch.branchContent.size();
        }

        if (element.hasAttribute(ATTRIB_ALIGNMENT)) {
            String align = element.getAttribute(ATTRIB_ALIGNMENT);
            alignment = EnumAlignment.valueOf(align.toUpperCase());
        }

        if (element.hasAttribute(ATTRIB_SHADOW)) {
            shadow = Boolean.parseBoolean(element.getAttribute(ATTRIB_SHADOW));
        }

        if (element.hasAttribute(ATTRIB_COLOUR) && element.getAttribute(ATTRIB_COLOUR).length() > 0) {
            try {
                colour = Integer.parseInt(element.getAttribute(ATTRIB_COLOUR), 16);
                colourSet = true;
            }
            catch (Exception e) {
                LogHelper.error("Error reading element colour: " + element + " In:" + branch.branchID);
                e.printStackTrace();
            }
        }
    }

    //endregion

    public abstract void onCreated();

    @Override
    public boolean onUpdate() {
        if (saveTimer > 0) {
            saveTimer--;

            if (saveTimer <= 0) {
                requiresSave = false;
                save();
                return true;
            }
        }

        return super.onUpdate();
    }

    public int getColour() {
        return colourSet ? colour : WikiConfig.TEXT_COLOUR;
    }

    public void setColour(int colour) {
        this.colour = colour;
        colourSet = true;
    }
}
