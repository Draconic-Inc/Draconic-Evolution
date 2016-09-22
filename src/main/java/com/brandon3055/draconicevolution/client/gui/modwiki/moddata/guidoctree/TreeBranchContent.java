package com.brandon3055.draconicevolution.client.gui.modwiki.moddata.guidoctree;

import codechicken.lib.colour.Colour;
import codechicken.lib.colour.ColourARGB;
import com.brandon3055.brandonscore.client.gui.modulargui.MGuiElementBase;
import com.brandon3055.brandonscore.client.gui.modulargui.lib.EnumAlignment;
import com.brandon3055.brandonscore.client.gui.modulargui.modularelements.MGuiLabel;
import com.brandon3055.brandonscore.client.gui.modulargui.modularelements.MGuiSlotRender;
import com.brandon3055.brandonscore.client.gui.modulargui.modularelements.MGuiStackIcon;
import com.brandon3055.brandonscore.lib.StackReference;
import com.brandon3055.draconicevolution.client.gui.modwiki.GuiModWiki;
import com.brandon3055.draconicevolution.client.gui.modwiki.guielements.PopupEditContent;
import com.brandon3055.draconicevolution.client.gui.modwiki.guielements.PopupEditMod;
import com.brandon3055.draconicevolution.client.gui.modwiki.moddata.WikiDocManager;
import com.brandon3055.draconicevolution.client.gui.modwiki.swing.SwingHelper;
import com.brandon3055.draconicevolution.client.gui.modwiki.swing.UIAddBranch;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.text.TextFormatting;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static com.brandon3055.draconicevolution.client.gui.modwiki.WikiConfig.NAV_WINDOW;
import static com.brandon3055.draconicevolution.client.gui.modwiki.WikiConfig.NAV_TEXT;
import static com.brandon3055.draconicevolution.client.gui.modwiki.moddata.WikiDocManager.*;

/**
 * Created by brandon3055 on 8/09/2016.
 */
public class TreeBranchContent extends TreeBranchRoot {

    private MGuiLabel label;
    private MGuiElementBase icon;
    private MGuiElementBase iconBackground;

    //TODO add display MGuiList for content area

    public TreeBranchContent(GuiModWiki guiWiki, TreeBranchRoot parent, Element branchData, String contentName) {
        super(guiWiki, parent, contentName);
        this.branchData = branchData;
        xSize = guiWiki.wikiList.getListEntryWidth();
//        ySize = 22;
    }

    public void initBranches() {
        removeChild(label);
        removeChild(icon);
        addChild(label = new MGuiLabel(guiWiki, xPos, yPos, xSize, ySize, branchName) {
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
        }.setAlignment(EnumAlignment.LEFT).setWrap(true));

        StackReference stackReference = getDisplayStack();
        if (stackReference != null) {
            MGuiStackIcon stackIcon = new MGuiStackIcon(modularGui, xPos, yPos, 18, 18, stackReference);
            MGuiSlotRender back = new MGuiSlotRender(modularGui, xPos, yPos, 18, 18);
            addChild(icon = stackIcon);
            addChild(iconBackground = back);

            int line =  modularGui.getMinecraft().fontRendererObj.listFormattedStringToWidth(branchName, label.xSize - 28).size();
            ySize = Math.max(22, (line * modularGui.getMinecraft().fontRendererObj.FONT_HEIGHT) + 7);
            label.ySize = ySize;
            stackIcon.yOffset = (ySize - 18) / 2;
            stackIcon.xOffset = 3;
            back.yOffset = (ySize - 18) / 2;
            back.xOffset = 3;

        }
        else {
            icon = null;
        }

        initElement();
        positionElements(xPos, yPos);
        super.initBranches();
    }

    @Override
    public void positionElements(int newXPos, int newYPos) {
        if (icon != null) {
            //Set Icon Pos
            label.xPos = icon.xPos + icon.xSize;
            label.xSize = (xSize - icon.xSize);
            icon.yPos = yPos;
            if (iconBackground != null) {
                iconBackground.yPos = yPos;
            }
        }
    }

    //region Render In List

    @Override
    public void renderBackgroundLayer(Minecraft minecraft, int mouseX, int mouseY, float partialTicks) {

        boolean selected = isMouseOver(mouseX, mouseY) || guiWiki.wikiDataTree.getActiveBranch() == this;
        int navWindowColour = NAV_WINDOW;

        if (isModBranch) {
            int back = mixColours(navWindowColour, selected ? 0x00404040 : 0x00202020);
            int pos = mixColours(navWindowColour, 0x00505050);
            int neg = mixColours(navWindowColour, 0x00202020, true);

            drawColouredRect(xPos + 1, yPos + 1, xSize - 2, ySize - 2, back);
            drawColouredRect(xPos + 1, yPos + 1, xSize - 2, 0.5, pos);
            drawColouredRect(xPos + 1, yPos + 1, 0.5, ySize - 2, pos);
            drawColouredRect(xPos + 1, yPos + ySize - 1, xSize - 2, 0.5, neg);
            drawColouredRect(xPos + xSize - 1, yPos + 1, 0.5, ySize - 2, neg);
        }
        else {
            int back = mixColours(navWindowColour, selected ? 0x00404040 : 0x00202020);
            int pos = mixColours(navWindowColour, 0x00505050);
            int neg = mixColours(navWindowColour, 0x00202020, true);

            drawColouredRect(xPos + 1, yPos + 1, xSize - 2, ySize - 2, back);
            drawColouredRect(xPos + 1, yPos + 1, xSize - 2, 0.5, pos);
            drawColouredRect(xPos + 1, yPos + 1, 0.5, ySize - 2, pos);
            drawColouredRect(xPos + 1, yPos + ySize - 1, xSize - 2, 0.5, neg);
            drawColouredRect(xPos + xSize - 1, yPos + 1, 0.5, ySize - 2, neg);
        }

        if (selected) {
            drawBorderedRect(xPos + 1, yPos + 1, xSize - 2, ySize - 2, 1, 0, 0xFF000000);
        }

        if (GuiModWiki.editMode && !isModBranch) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(xPos, yPos, 100);
            GlStateManager.scale(0.6, 0.6, 1);
            String s = "W:§f" + sortingWeight;
            drawString(minecraft.fontRendererObj, s, 0, 0, 0xFF0000, false);
            drawColouredRect(0, 0, minecraft.fontRendererObj.getStringWidth(s), 8, 0xAA000000);
            GlStateManager.popMatrix();
        }

        super.renderBackgroundLayer(minecraft, mouseX, mouseY, partialTicks);
    }

    @Override
    public void renderOverlayLayer(Minecraft minecraft, int mouseX, int mouseY, float partialTicks) {
        if (GuiModWiki.editMode && isMouseOver(mouseX, mouseY)) {
            drawHoveringText(Arrays.asList(new String[] {TextFormatting.RED + "[Right click to edit]"}), mouseX, mouseY, minecraft.fontRendererObj, modularGui.screenWidth(), modularGui.screenHeight());
        }
        super.renderOverlayLayer(minecraft, mouseX, mouseY, partialTicks);
    }

    //endregion

    //region Edit

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (GuiModWiki.editMode && isMouseOver(mouseX, mouseY) && mouseButton == 1) {
            if (isModBranch) {
                PopupEditMod editMod = new PopupEditMod(modularGui, list.xPos + 12, list.yPos + 24, list.xSize - 12 - list.rightPadding, list.ySize - 25, list, this);
                list.addChild(editMod);
                list.disableList = true;
                editMod.initElement();
            }
            else {
                PopupEditContent editContent = new PopupEditContent(modularGui, list.xPos + 12, list.yPos + 24, list.xSize - 12 - list.rightPadding, list.ySize - 25, list, this);
                list.addChild(editContent);
                list.disableList = true;
                editContent.initElement();
            }

            return false;
        }
        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    //endregion

    //region XML

    /**
     * Loads this branch and all of its children from XML
     */
    public void loadBranchesXML() {
        //Load Branch ID if not a mod branch (In the case of Mod Branches the branch id is the modid)
        if (!isModBranch) {
            loadNonModAttributes();
        }

        //Load branch content
        loadBranchContent();

        //Load sub branches
        NodeList entryList = branchData.getElementsByTagName(WikiDocManager.ELEMENT_ENTRY);
        for (int entryIndex = 0; entryIndex < entryList.getLength(); entryIndex++) {
            Element entry = (Element) entryList.item(entryIndex);
            if (entry.getParentNode() != branchData) {
                continue;
            }
            String name = entry.getAttribute(ATTRIB_BRANCH_NAME);

            TreeBranchContent contentBranch = new TreeBranchContent(guiWiki, this, entry, name);
            addSubBranch(contentBranch);
            contentBranch.loadBranchesXML();
        }

        Collections.sort(subBranches, BRANCH_SORTER);
    }

    public void loadNonModAttributes() {
        if (branchData.hasAttribute(ATTRIB_BRANCH_ID)) {
            setBranchID(branchData.getAttribute(ATTRIB_BRANCH_ID));
        }
        if (branchData.hasAttribute(ATTRIB_BRANCH_CATEGORY)) {
            String category = branchData.getAttribute(ATTRIB_BRANCH_CATEGORY);
            WikiDocManager.loadedCategories.add(category);
        }
    }

    public StackReference getDisplayStack() {
        if (!isModBranch && branchData.hasAttribute(ATTRIB_ICON_TYPE)) {
            if (branchData.getAttribute(ATTRIB_ICON_TYPE).equals(ICON_TYPE_STACK) && branchData.hasAttribute(ATTRIB_ICON)) {
                String stackString = branchData.getAttribute(ATTRIB_ICON);
                StackReference stack = StackReference.fromString(stackString);

                if (stack == null) {
                    stack = new StackReference("error");
                }

                return stack;
            }
        }
        return null;
    }

    //endregion

    //region User Create

    @Override
    public void createNewSubBranch() {
        UIAddBranch frame = new UIAddBranch(this, WikiDocManager.loadedCategories, branchID, new ArrayList<String>(guiWiki.wikiDataTree.idToBranchMap.keySet()));
        frame.pack();
        frame.setVisible(true);
        SwingHelper.centerOnMinecraftWindow(frame);
    }

    //endregion
}
