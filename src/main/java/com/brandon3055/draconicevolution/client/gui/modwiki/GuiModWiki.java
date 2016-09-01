package com.brandon3055.draconicevolution.client.gui.modwiki;

import com.brandon3055.brandonscore.client.gui.modulargui.ModularGuiScreen;
import com.brandon3055.brandonscore.client.gui.modulargui.modularelements.ElementButton;
import com.brandon3055.draconicevolution.utils.LogHelper;

/**
 * Created by brandon3055 on 30/08/2016.
 */
public class GuiModWiki extends ModularGuiScreen {

    public WikiMenu wikiMenu;
    public WikiContentList wikiList;
    public WikiContentWindow contentWindow;

    public GuiModWiki() {

    }

    //region Initialization

    @Override
    public void initGui() {
        super.initGui();
        buttonList.clear();
        manager.clear();

        manager.add(new ElementButton(this, 0, width / 2, height / 2, 100, 15, "Test Button"));
        manager.add(wikiMenu = new WikiMenu(this));
        manager.add(wikiList = new WikiContentList(this));
        manager.add(contentWindow = new WikiContentWindow(this));

        manager.initElements();
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
    public void elementButtonAction(ElementButton button) {

        LogHelper.info(button.buttonId);

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
        return false;
    }

    //endregion
}
