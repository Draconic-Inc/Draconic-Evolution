package com.brandon3055.draconicevolution.client.gui.modwiki;

import com.brandon3055.brandonscore.client.gui.modulargui.MGuiElementBase;
import com.brandon3055.brandonscore.client.gui.modulargui.ModularGuiScreen;
import com.brandon3055.brandonscore.client.gui.modulargui.lib.IMGuiListener;
import com.brandon3055.brandonscore.client.gui.modulargui.modularelements.MGuiButton;
import com.brandon3055.draconicevolution.client.gui.modwiki.moddata.WikiDocManager;
import com.brandon3055.draconicevolution.client.gui.modwiki.moddata.WikiDownloadManager;
import com.brandon3055.draconicevolution.client.gui.modwiki.moddata.guidoctree.GuiDocTree;
import net.minecraft.client.Minecraft;

/**
 * Created by brandon3055 on 30/08/2016.
 */
public class GuiModWiki extends ModularGuiScreen implements IMGuiListener {
    public static GuiModWiki activeInstance = null;
    public WikiMenu wikiMenu;
    public WikiContentList wikiList;
    public WikiContentWindow contentWindow;
    public GuiDocTree wikiDataTree = null;
    public static String activeID = null;
    public static boolean editMode = false;

//    public static ModDataList activeMod = null;
//    public static ModDataEntry activeModData = null;

    /**
     * 0 = Info, 1 = Mod List, 2 = Content List;
     */
    public int guiState = 0;

    public GuiModWiki() {
        GuiModWiki.activeInstance = this;
    }

    //region Initialization

    @Override
    public void initGui() {
        super.initGui();

        wikiDataTree = new GuiDocTree(this);
        buttonList.clear();
        manager.clear();

        manager.add(wikiMenu = new WikiMenu(this));
        manager.add(wikiList = new WikiContentList(this));
        manager.add(contentWindow = new WikiContentWindow(this));
        contentWindow.activeBranch = wikiDataTree.getActiveBranch();
        updateWindowPositions();
        manager.initElements();

        //Something to remember: Branch elements dont get initialized by the normal init call... i think...
        WikiDocManager.reload(true, true, true);

        //wikiDataTree.reOpenLast();
    }

    public void updateWindowPositions() {
        //region Nav Window

        wikiList.xPos = 0;
        wikiList.yPos = wikiMenu.ySize;
        wikiList.ySize = screenHeight() - wikiList.yPos;

        //endregion

        //region Content Window

        contentWindow.xPos = wikiList.xSize;
        contentWindow.xSize = screenWidth() - wikiList.xSize;

        contentWindow.yPos = wikiMenu.ySize;
        contentWindow.ySize = screenHeight() - wikiList.yPos;

        //endregion

        contentWindow.updatePositions();
    }

    @Override
    public void setWorldAndResolution(Minecraft mc, int width, int height) {
        super.setWorldAndResolution(mc, width, height);
        updateWindowPositions();
        wikiList.initElement();
        contentWindow.initElement();
        wikiMenu.initElement();
        if (activeID != null && wikiDataTree.idToBranchMap.containsKey(activeID)) {
            wikiDataTree.setActiveBranch(wikiDataTree.idToBranchMap.get(activeID));
        }
    }

    //endregion

    //region Render

    @Override
    public void renderBackgroundLayer(int mouseX, int mouseY, float partialTicks) {
        super.renderBackgroundLayer(mouseX, mouseY, partialTicks);
    }


    //endregion

    //region User Input

    @Override
    public void onMGuiEvent(String event, MGuiElementBase element) {
        if (element instanceof MGuiButton && ((MGuiButton) element).buttonName.equals("Reload")) {
            WikiDocManager.initFiles();
            WikiDocManager.loadDocsFromDisk();
            wikiDataTree.reloadData();
            wikiList.reloadList();
            wikiDataTree.reOpenLast();
            WikiConfig.load();
            WikiDownloadManager.downloadManifest();
        }
    }

    //endregion

    //region Misc

    @Override
    public int xSize() {
        return width;
    }

    @Override
    public int ySize() {
        return height;
    }

    @Override
    public boolean doesGuiPauseGame() {
        return true;
    }

    //endregion
}
