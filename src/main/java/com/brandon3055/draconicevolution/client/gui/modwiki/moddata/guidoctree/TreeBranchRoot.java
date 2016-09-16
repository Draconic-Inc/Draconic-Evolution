package com.brandon3055.draconicevolution.client.gui.modwiki.moddata.guidoctree;

import com.brandon3055.brandonscore.client.gui.modulargui.MGuiElementBase;
import com.brandon3055.brandonscore.client.gui.modulargui.modularelements.MGuiListEntry;
import com.brandon3055.draconicevolution.client.gui.modwiki.GuiModWiki;
import com.brandon3055.draconicevolution.client.gui.modwiki.WikiContentList;
import com.brandon3055.draconicevolution.client.gui.modwiki.moddata.WikiDocManager;
import com.brandon3055.draconicevolution.client.gui.modwiki.moddata.displaycontent.DisplayComponentBase;
import com.brandon3055.draconicevolution.client.gui.modwiki.moddata.displaycontent.DisplayComponentRegistry;
import com.brandon3055.draconicevolution.client.gui.modwiki.swing.SwingHelper;
import com.brandon3055.draconicevolution.client.gui.modwiki.swing.UIAddModBranch;
import com.brandon3055.draconicevolution.utils.LogHelper;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

import static com.brandon3055.draconicevolution.client.gui.modwiki.moddata.WikiDocManager.ATTRIB_TYPE;

/**
 * Created by brandon3055 on 7/09/2016.
 */
public class TreeBranchRoot extends MGuiListEntry {

    public Element branchData;
    public GuiModWiki guiWiki;
    public LinkedList<TreeBranchRoot> subBranches = new LinkedList<TreeBranchRoot>();
    public LinkedList<DisplayComponentBase> branchContent = new LinkedList<DisplayComponentBase>();
    public TreeBranchRoot parent;
    public String branchName = "[Unknown Branch]";
    public String branchID = "ROOT";
    public boolean isModBranch = false;

    public TreeBranchRoot(GuiModWiki guiWiki, TreeBranchRoot parent, String branchName) {
        super(guiWiki);
        this.parent = parent;
        this.branchName = branchName;
        this.guiWiki = guiWiki;
        ySize = 20;
        xSize = guiWiki.wikiList != null ? guiWiki.wikiList.getListEntryWidth() : 50;
    }

    //region Init

    public void initBranches() {
        for (TreeBranchRoot sub : subBranches) {
            sub.initBranches();
        }
    }

    public void addSubBranch(TreeBranchRoot branch) {
        subBranches.add(branch);
    }

    //endregion

    //region List

    @Override
    public int getEntryHeight() {
        return ySize;
    }

    @Override
    public void moveEntry(int newXPos, int newYPos) {
        xPos = newXPos;
        yPos = newYPos;
        for (MGuiElementBase element : childElements) {
            element.xPos = newXPos;
            element.yPos = newYPos;
        }
        positionElements(newXPos, newYPos);
    }

    public void positionElements(int newXPos, int newYPos) {}

    //endregion

    //region Misc

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (isMouseOver(mouseX, mouseY)) {
            guiWiki.wikiDataTree.setActiveBranch(this);
            modularGui.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            return true;
        }


        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    /**
     * Called when the user clicks the [add] button to create a new branch.
     */
    public void createNewSubBranch() {
        UIAddModBranch frame = new UIAddModBranch(this);
        frame.pack();
        frame.setVisible(true);
        SwingHelper.centerOnMinecraftWindow(frame);
    }

    /**
     * Called by the UI. This actually creates the branch.
     */
    public void createNewSubBranch(String name, String id, String category, TreeBranchRoot branch) {
        guiWiki.wikiList.toAddList.add(new WikiContentList.ToAdd(name, id, category, branch));
    }

    public void setBranchID(String id) {
        branchID = id;
        guiWiki.wikiDataTree.idToBranchMap.put(id, this);
    }

    /**
     * Saves all changed data to disk
     */
    public void save() throws Exception {
        WikiDocManager.saveChanges(branchData.getOwnerDocument());
    }

    //endregion

    //region Content

    public void loadBranchContent() {
        branchContent.clear();
        NodeList nodeList = branchData.getElementsByTagName(WikiDocManager.ELEMENT_CONTENT);
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getParentNode() != branchData) {
                continue;
            }

            if (!(node instanceof Element)) {
                LogHelper.dev("Node Is Not An Element: " + node);
                continue;
            }

            Element content = (Element) node;
            String type = content.getAttribute(ATTRIB_TYPE);

            DisplayComponentBase displayComponent = DisplayComponentRegistry.createComponent(guiWiki, type, content, this);

            if (displayComponent == null) {
                continue;
            }

            branchContent.add(displayComponent);
        }

        Collections.sort(branchContent, INDEX_SORTER);
    }

    public static Comparator<DisplayComponentBase> INDEX_SORTER = new Comparator<DisplayComponentBase>() {
        @Override
        public int compare(DisplayComponentBase o1, DisplayComponentBase o2) {
            return o1.posIndex < o2.posIndex ? -1 : o1.posIndex > o2.posIndex ? 1 : 0;
        }
    };

    //endregion
}
