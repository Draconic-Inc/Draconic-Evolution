package com.brandon3055.draconicevolution.client.gui.modwiki.moddata.guidoctree;

import com.brandon3055.draconicevolution.client.gui.modwiki.GuiModWiki;
import net.minecraft.client.Minecraft;
import org.w3c.dom.Element;

/**
 * Created by brandon3055 on 8/09/2016.
 */
@Deprecated
public class TreeBranchMod extends TreeBranchContent {

    public TreeBranchMod(GuiModWiki guiWiki, TreeBranchRoot parent, Element branchData, String modName) {
        super(guiWiki, parent, branchData, modName);
//        xSize = GuiModWiki.wikiList.getListEntryWidth();
//        ySize = 22;
//        addChild(new MGuiLabel(guiWiki, xPos, yPos, xSize, ySize, branchName).setAlignment(EnumAlignment.LEFT).setWrap(true));
//        initElement();
    }

    //region Render

    @Override
    public void renderBackgroundLayer(Minecraft minecraft, int mouseX, int mouseY, float partialTicks) {
//        int navWindowColour = NAV_WINDOW;
//        boolean hovering = isMouseOver(mouseX, mouseY);
//
//        int back = mixColours(navWindowColour, hovering ? 0x00404040 : 0x00303030);
//        int pos = mixColours(navWindowColour, 0x00505050);
//        int neg = mixColours(navWindowColour, 0x00202020, true);
//
//        drawColouredRect(xPos + 1, yPos + 1, xSize - 2, ySize - 2, back);
//        drawColouredRect(xPos + 1, yPos + 1, xSize - 2, 0.5, pos);
//        drawColouredRect(xPos + 1, yPos + 1, 0.5, ySize - 2, pos);
//        drawColouredRect(xPos + 1, yPos + ySize - 1, xSize - 2, 0.5, neg);
//        drawColouredRect(xPos + xSize - 1, yPos + 1, 0.5, ySize - 2, neg);
//
//        super.renderBackgroundLayer(minecraft, mouseX, mouseY, partialTicks);
    }

    //endregion

    //region XML

    public void loadBranchesXML() {
//        NodeList nodeList = mod.getElementsByTagName("contributor");
//
//        for (int i = 0; i < nodeList.getLength(); i++) {
//            Node node = nodeList.item(i);
//            NamedNodeMap map = node.getAttributes();
//            Node name = map.getNamedItem("name");
//
//            if (name == null) {
//                continue;
//            }
//
//            Node ign = map.getNamedItem("ign");
//
//            if (ign == null) {
//                ign = name;
//            }
//
//            Node role = map.getNamedItem("role");
//            String roleS = "";
//
//            if (role != null) {
//                roleS = role.getNodeValue();
//            }
//
//            ContributorEntry contributor = new ContributorEntry(name.getNodeValue(), ign.getNodeValue(), roleS, node.getTextContent());
//            this.contributors.add(contributor);
//        }


        super.loadBranchesXML();
    }

    //endregion

}
